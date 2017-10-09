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
 * 先消费后买单订单详情
 * 
 * @author meihuali
 * 
 */
public class XiaofeiMaidanDetailActivity extends BaseActivity implements
		OnClickListener {

	private Dialog calldialog;
	private TextView tv_shjine;
	private ImageView iv_shop_pic;
	private TextView tv_shopname;
	private TextView tv_shop_address;
	private TextView tv_dindan_num;
	private TextView tv_buy_zhanghao;
	private TextView tv_buy_time;
	private TextView tv_num;
	private TextView tv_all_price;
	private String order_id;
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
				Toast.makeText(XiaofeiMaidanDetailActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(XiaofeiMaidanDetailActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					String result = msg.obj.toString().trim();
					Toast.makeText(XiaofeiMaidanDetailActivity.this, result,
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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xiaofeimaidandetail);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		tv_shjine = (TextView) findViewById(R.id.tv_shjine);
		iv_shop_pic = (ImageView) findViewById(R.id.iv_shop_pic);
		tv_shopname = (TextView) findViewById(R.id.tv_shopname);
		tv_shop_address = (TextView) findViewById(R.id.tv_shop_address);
		LinearLayout line_call = (LinearLayout) findViewById(R.id.line_call);
		tv_dindan_num = (TextView) findViewById(R.id.tv_dindan_num);
		tv_buy_zhanghao = (TextView) findViewById(R.id.tv_buy_zhanghao);
		tv_buy_time = (TextView) findViewById(R.id.tv_buy_time);
		tv_num = (TextView) findViewById(R.id.tv_num);
		tv_all_price = (TextView) findViewById(R.id.tv_all_price);
		line_qupingjia = (LinearLayout) findViewById(R.id.line_qupingjia);
		tv_qupingjia = (TextView) findViewById(R.id.tv_qupingjia);
		if (getIntent() != null) {
			order_id = getIntent().getStringExtra("order_id");
			showLoading();
			tuikuanshouhouDinDanDetailHttpPost();
		}
		line_back.setOnClickListener(this);
		line_call.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_call:
			if (TextUtils.isEmpty(live_store_tel)) {
				Toast.makeText(XiaofeiMaidanDetailActivity.this, "店铺电话暂时为空",
						Toast.LENGTH_SHORT).show();
			} else {
				showCallDialog();
			}
			break;

		case R.id.tv_qupingjia:
			Intent intent = new Intent(XiaofeiMaidanDetailActivity.this,
					QuPingJiaActivity.class);
			intent.putExtra("order_id", getorder_id);
			intent.putExtra("goods_id", "0");
			intent.putExtra("pic", store_img);
			startActivityForResult(intent, 100);
			break;

		default:
			break;
		}
	}

	private void tuikuanshouhouDinDanDetailHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(XiaofeiMaidanDetailActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(XiaofeiMaidanDetailActivity.this));
		params.addBodyParameter("order_id", order_id);
		params.addBodyParameter("type", "3");
		httpUtils.send(HttpMethod.POST, JiekouUtils.DINDANDETAIL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						tuikuanshouhouDetailListInfo(arg0.result);

					}
				});
	}

	protected void tuikuanshouhouDetailListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONObject data = object.getJSONObject("data");
				String add_time = data.getString("add_time");
				String buyer_id = data.getString("buyer_id");
				String evaluation_state = data.getString("evaluation_state");
				String finnshed_time = data.getString("finnshed_time");
				String goods_amount = data.getString("goods_amount");
				String goods_num = data.getString("goods_num");
				String member_mobile = data.getString("member_mobile");
				String order_amount = data.getString("order_amount");
				String buyer_name = data.getString("buyer_name");
				getorder_id = data.getString("order_id");
				String order_sn = data.getString("order_sn");
				String order_state = data.getString("order_state");
				String payment_time = data.getString("payment_time");
				String store_id = data.getString("store_id");
				JSONObject store = data.getJSONObject("store");
				live_store_tel = store.getString("live_store_tel");
				String store_address = store.getString("store_address");
				store_img = store.getString("store_img");
				String store_name = store.getString("store_name");

				BitmapUtils bitmapUtils = new BitmapUtils(
						XiaofeiMaidanDetailActivity.this);
				bitmapUtils.display(iv_shop_pic, store_img);
				tv_shjine.setText("实付金额： ¥" + order_amount);
				tv_shopname.setText(store_name);
				tv_shop_address.setText(store_address);
				tv_dindan_num.setText(order_sn);
				tv_buy_zhanghao.setText(buyer_name);
				tv_num.setText(goods_num);
				tv_all_price.setText("¥： " + goods_amount);
				String xsstatus = "";
				if ("0".equals(evaluation_state)) {
					tv_qupingjia.setVisibility(View.VISIBLE);
				} else {
					tv_qupingjia.setVisibility(View.GONE);
					line_qupingjia.setVisibility(View.GONE);
				}
				/**
				 * 时间戳转换成具体时间形式
				 */
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ssE", Locale.getDefault());
				// 当前时间对象
				Date curDate = new Date(
						(Integer.parseInt(add_time)) * 1000L);
				String defaultTimeZoneID = TimeZone.getDefault().getID();// America/New_York
				String newTimeZoneID = "Asia/Shanghai"; // Asia/Shanghai
				format.setTimeZone(TimeZone.getTimeZone(newTimeZoneID));
				tv_buy_time.setText(format.format(curDate));

				handler.sendEmptyMessage(HANDLER_TUIKUANSHOUHUO_SUCCESS);
			} else {
				String message = object.getString("message");
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
		View view = LayoutInflater.from(XiaofeiMaidanDetailActivity.this)
				.inflate(R.layout.dialog_call, null);
		TextView tv_dialog_cancel = (TextView) view
				.findViewById(R.id.tv_dialog_cancel);
		TextView tv_phone_num = (TextView) view.findViewById(R.id.tv_phone_num);
		tv_phone_num.setText("是否拨打： " + live_store_tel);
		tv_dialog_cancel.setOnClickListener(this);
		tv_phone_num.setOnClickListener(this);
		calldialog = new Dialog(XiaofeiMaidanDetailActivity.this,
				R.style.mDialogStyle);
		calldialog.setContentView(view);
		calldialog.setCanceledOnTouchOutside(false);
		calldialog.show();
	}

	// 加载中动画
	private Dialog loadingDialog;
	private String live_store_tel;
	private String getorder_id;
	private String store_img;
	private TextView tv_qupingjia;
	private LinearLayout line_qupingjia;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(XiaofeiMaidanDetailActivity.this)
				.inflate(R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				XiaofeiMaidanDetailActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(XiaofeiMaidanDetailActivity.this,
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
			setResult(101, intent);
			finish();
		}
	}
}
