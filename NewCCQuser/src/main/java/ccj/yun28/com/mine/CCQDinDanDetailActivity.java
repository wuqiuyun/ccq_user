package ccj.yun28.com.mine;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.ccq.CcqProDetailActivity;
import ccj.yun28.com.ccq.OldCCQStoreDetailActivity;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 餐餐抢订单详情信息
 * 
 * @author meihuali
 * 
 */
public class CCQDinDanDetailActivity extends BaseActivity implements
		OnClickListener {

	private Dialog calldialog;
	private ImageView iv_pro_pic;
	private TextView tv_pro_name;
	private TextView tv_pro_num;
	private TextView tv_price;
	private TextView tv_yzm;
	private TextView tv_status;
	private ImageView iv_erweima;
	private ImageView iv_shop_pic;
	private TextView tv_shopname;
	private TextView tv_shop_address;
	private LinearLayout line_call;
	private TextView tv_dindan_num;
	private TextView tv_buy_zhanghao;
	private TextView tv_buy_time;
	private TextView tv_num;
	private TextView tv_all_price;
	private TextView tv_delete;
	private TextView tv_go_pay;
	private TextView tv_chakanjuanma;
	private TextView tv_qupingjia;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 错误
	private static final int HANDLER_NN_FAILURE = 1;
	private static final int HANDLER_GETINFO_FAILURE = 3;
	private static final int HANDLER_TUIKUANSHOUHUO_SUCCESS = 4;
	private static final int HANDLER_SHANCHU_SUCCESS = 5;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(CCQDinDanDetailActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(CCQDinDanDetailActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					String result = msg.obj.toString().trim();
					Toast.makeText(CCQDinDanDetailActivity.this, result,
							Toast.LENGTH_SHORT).show();
				}
				break;
			// 获取信息成功
			case HANDLER_TUIKUANSHOUHUO_SUCCESS:
				dissDialog();
				break;
			// 删除成功
			case HANDLER_SHANCHU_SUCCESS:
				dissDialog();
				Intent intent = new Intent();
				setResult(101, intent);
				finish();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ccqdindandetail);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_pro = (LinearLayout) findViewById(R.id.line_pro);
		line_juanma = (LinearLayout) findViewById(R.id.line_juanma);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		iv_pro_pic = (ImageView) findViewById(R.id.iv_pro_pic);
		tv_pro_name = (TextView) findViewById(R.id.tv_pro_name);
		tv_pro_num = (TextView) findViewById(R.id.tv_pro_num);
		tv_price = (TextView) findViewById(R.id.tv_price);
		tv_yzm = (TextView) findViewById(R.id.tv_yzm);
		tv_status = (TextView) findViewById(R.id.tv_status);
		iv_erweima = (ImageView) findViewById(R.id.iv_erweima);
		iv_shop_pic = (ImageView) findViewById(R.id.iv_shop_pic);
		tv_shopname = (TextView) findViewById(R.id.tv_shopname);
		tv_shop_address = (TextView) findViewById(R.id.tv_shop_address);
		line_call = (LinearLayout) findViewById(R.id.line_call);
		tv_dindan_num = (TextView) findViewById(R.id.tv_dindan_num);
		tv_buy_zhanghao = (TextView) findViewById(R.id.tv_buy_zhanghao);
		tv_buy_time = (TextView) findViewById(R.id.tv_buy_time);
		tv_num = (TextView) findViewById(R.id.tv_num);
		tv_all_price = (TextView) findViewById(R.id.tv_all_price);
		tv_delete = (TextView) findViewById(R.id.tv_delete);
		tv_go_pay = (TextView) findViewById(R.id.tv_go_pay);
		tv_chakanjuanma = (TextView) findViewById(R.id.tv_chakanjuanma);
		tv_qupingjia = (TextView) findViewById(R.id.tv_qupingjia);
		
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(CCQDinDanDetailActivity.this);

		if (getIntent() != null) {
			order_id = getIntent().getStringExtra("order_id");
			showLoading();
			tuikuanshouhouDinDanDetailHttpPost();
		}

		line_back.setOnClickListener(this);
		line_pro.setOnClickListener(this);
		line_call.setOnClickListener(this);
		tv_delete.setOnClickListener(this);
		tv_go_pay.setOnClickListener(this);
		tv_chakanjuanma.setOnClickListener(this);
		tv_qupingjia.setOnClickListener(this);
	}

	private void tuikuanshouhouDinDanDetailHttpPost() {
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(CCQDinDanDetailActivity.this));
		params.addBodyParameter("order_id", order_id);
		params.addBodyParameter("type", "1");
		httpUtils.send(HttpMethod.POST, JiekouUtils.DINDANDETAIL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						tuikuanshouhouDetailListInfo(arg0.result);

					}
				});
	}

	protected void tuikuanshouhouDetailListInfo(String result) {
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONObject data = object.getJSONObject("data");
				String add_time = data.getString("add_time");
				String buyer_id = data.getString("buyer_id");
				String buyer_name = data.getString("buyer_name");
				String check_number = data.getString("check_number");
				String check_number_img = data.getString("check_number_img");
				String evaluation_state = data.getString("evaluation_state");
				String finnshed_time = data.getString("finnshed_time");
				String goods_amount = data.getString("goods_amount");
				goods_id = data.getString("goods_id");
				String goods_name = data.getString("goods_name");
				String goods_num = data.getString("goods_num");
				String goods_price = data.getString("goods_price");
				image = data.getString("image");
				String member_mobile = data.getString("member_mobile");
				String order_amount = data.getString("order_amount");
				getorder_id = data.getString("order_id");
				String order_sn = data.getString("order_sn");
				String order_state = data.getString("order_state");
				String payment_time = data.getString("payment_time");
				 store_id = data.getString("store_id");
				String validity = data.getString("validity");
				JSONObject store = data.getJSONObject("store");
				live_store_tel = store.getString("live_store_tel");
				String store_address = store.getString("store_address");
				String store_img = store.getString("store_img");
				String store_name = store.getString("store_name");

				BitmapUtils bitmapUtils = new BitmapUtils(
						CCQDinDanDetailActivity.this);
				bitmapUtils.display(iv_pro_pic, image);
				bitmapUtils.display(iv_erweima, check_number_img);
				bitmapUtils.display(iv_shop_pic, store_img);
				tv_pro_name.setText(goods_name);
				tv_pro_num.setText("数量： x " + goods_num);
				tv_price.setText("¥： " + goods_price);
				tv_yzm.setText(check_number);
				String xsstatus = "";
				// 0(已取消)10(默认):待付款;20:已付款;30:待收货;40:已完成;
				if ("0".equals(order_state)) {
					xsstatus = "已取消";
					tv_delete.setVisibility(View.GONE);
					tv_go_pay.setVisibility(View.GONE);
					tv_chakanjuanma.setVisibility(View.GONE);
					tv_qupingjia.setVisibility(View.GONE);
					line_juanma.setVisibility(View.GONE);
				} else if ("10".equals(order_state)) {
					xsstatus = "待付款";
					tv_delete.setVisibility(View.VISIBLE);
					tv_go_pay.setVisibility(View.VISIBLE);
					tv_chakanjuanma.setVisibility(View.GONE);
					tv_qupingjia.setVisibility(View.GONE);
					line_juanma.setVisibility(View.GONE);
				} else if ("20".equals(order_state)) {
					xsstatus = "已付款";
					tv_delete.setVisibility(View.GONE);
					tv_go_pay.setVisibility(View.GONE);
					tv_chakanjuanma.setVisibility(View.VISIBLE);
					tv_qupingjia.setVisibility(View.GONE);
				} else if ("40".equals(order_state)) {
					xsstatus = "已完成";
					if ("0".equals(evaluation_state)) {
						xsstatus = "待评价";
						tv_delete.setVisibility(View.GONE);
						tv_go_pay.setVisibility(View.GONE);
						tv_chakanjuanma.setVisibility(View.VISIBLE);
						tv_qupingjia.setVisibility(View.VISIBLE);
					} else {
						tv_delete.setVisibility(View.GONE);
						tv_go_pay.setVisibility(View.GONE);
						tv_chakanjuanma.setVisibility(View.VISIBLE);
						tv_qupingjia.setVisibility(View.GONE);
					}
				}
				tv_status.setText(xsstatus);
				tv_shopname.setText(store_name);
				tv_shop_address.setText(store_address);
				tv_dindan_num.setText(order_sn);
				tv_buy_zhanghao.setText(buyer_name);
				/**
				 * 时间戳转换成具体时间形式  2017-10-10 11:39:25
				 */
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
				
				/**
				 * 时间戳转换成具体时间形式  2017-10-10 11:39:25 周三
				 */
/*				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ssE", Locale.getDefault());
						
*/				// 当前时间对象
				Date curDate = new Date(
						(Integer.parseInt(payment_time)) * 1000L);
				String defaultTimeZoneID = TimeZone.getDefault().getID();// America/New_York
				String newTimeZoneID = "Asia/Shanghai"; // Asia/Shanghai
				format.setTimeZone(TimeZone.getTimeZone(newTimeZoneID));
				tv_buy_time.setText(format.format(curDate));
				tv_num.setText(goods_num);
				tv_all_price.setText(order_amount);

				handler.sendEmptyMessage(HANDLER_TUIKUANSHOUHUO_SUCCESS);
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETINFO_FAILURE, message));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_pro:
			/*intent = new Intent(CCQDinDanDetailActivity.this,
					CcqProDetailActivity.class);
			intent.putExtra("ccqgoods_id", goods_id);
			startActivity(intent);*/
			 intent = new Intent(CCQDinDanDetailActivity.this, OldCCQStoreDetailActivity.class);
			intent.putExtra("store_id", store_id);
			startActivity(intent);
			break;
		case R.id.tv_kefu:
			if (TextUtils.isEmpty(live_store_tel) || live_store_tel == null) {
				Toast.makeText(CCQDinDanDetailActivity.this, "店铺电话暂时为空", Toast.LENGTH_SHORT).show();
			}else{
				showCallDialog();
			}
			break;
		case R.id.line_call:
			if (TextUtils.isEmpty(live_store_tel)|| live_store_tel == null) {
				Toast.makeText(CCQDinDanDetailActivity.this, "店铺电话暂时为空", Toast.LENGTH_SHORT).show();
			}else{
				showCallDialog();
			}
			break;
		case R.id.tv_dialog_cancel:
			calldialog.dismiss();
			break;
		case R.id.tv_phone_num:
			Intent callintent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ live_store_tel));
			startActivity(callintent);
			break;
		case R.id.tv_delete:
			shanchuHttpPost();
			break;
		case R.id.tv_go_pay:
			intent = new Intent(CCQDinDanDetailActivity.this,
					Mine_DinDan_BuyZKJActivity.class);
			intent.putExtra("order_id", getorder_id);
			CCQDinDanDetailActivity.this.startActivity(intent);
			break;
		case R.id.tv_chakanjuanma:
			intent = new Intent(CCQDinDanDetailActivity.this,
					ChaKanJuanMaActivity.class);
			intent.putExtra("order_id", getorder_id);
			CCQDinDanDetailActivity.this.startActivity(intent);
			break;
		case R.id.tv_qupingjia:
			intent = new Intent(CCQDinDanDetailActivity.this,
					QuPingJiaActivity.class);
			intent.putExtra("order_id", getorder_id);
			intent.putExtra("goods_id", goods_id);
			intent.putExtra("pic", image);
			startActivityForResult(intent, 100);
			break;
		default:
			break;
		}
	}

	// 删除订单
	protected void shanchuHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(CCQDinDanDetailActivity.this));
		params.addBodyParameter("order_id", order_id);
		httpUtils.send(HttpMethod.POST, JiekouUtils.DELETEDINDAN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						shanchuListInfo(arg0.result);
					}
				});
	}

	protected void shanchuListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			String message = object.getString("message");
			if ("200".equals(code)) {
				handler.sendMessage(handler.obtainMessage(
						HANDLER_SHANCHU_SUCCESS, message));
			} else {
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETINFO_FAILURE, message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private void showCallDialog() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(CCQDinDanDetailActivity.this).inflate(
				R.layout.dialog_call, null);
		TextView tv_dialog_cancel = (TextView) view
				.findViewById(R.id.tv_dialog_cancel);
		TextView tv_phone_num = (TextView) view.findViewById(R.id.tv_phone_num);
		tv_phone_num.setText("是否拨打： " + live_store_tel);
		tv_dialog_cancel.setOnClickListener(this);
		tv_phone_num.setOnClickListener(this);
		calldialog = new Dialog(CCQDinDanDetailActivity.this,
				R.style.mDialogStyle);
		calldialog.setContentView(view);
		calldialog.setCanceledOnTouchOutside(false);
		calldialog.show();
	}

	// 加载中动画
	private Dialog loadingDialog;
	private String order_id;
	private String goods_id;
	private String live_store_tel;
	private String getorder_id;
	private String store_id = "";
	private String image;
	private LinearLayout line_juanma;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(CCQDinDanDetailActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				CCQDinDanDetailActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(CCQDinDanDetailActivity.this,
				R.style.mDialogStyle);
		loadingDialog.setContentView(view);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.show();
	}

	// 关闭加载dialog
	private void dissDialog() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 101) {
			Intent intent = new Intent();
			setResult(101,intent);
			finish();
		}
	}

}
