package ccj.yun28.com.mine;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.ProDindanDetailExpandableListViewAdapter;
import ccj.yun28.com.bean.ZhuProDinDanDetailBean;
import ccj.yun28.com.bean.gwc.GroupInfo;
import ccj.yun28.com.bean.gwc.ProductInfo;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.MyExpandableListView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 退款售后详情信息
 * 
 * @author meihuali
 * 
 */
public class TuiKuanDinDanDetailActivity extends BaseActivity implements
		OnClickListener, OnGroupClickListener {

	private Dialog calldialog;
	private ProDindanDetailExpandableListViewAdapter prodindanDetailAdapter;

	// 组元素数据列表
	private List<GroupInfo> groups = new ArrayList<GroupInfo>();
	// 子元素数据列表
	private Map<String, List<ProductInfo>> children = new HashMap<String, List<ProductInfo>>();
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
				Toast.makeText(TuiKuanDinDanDetailActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(TuiKuanDinDanDetailActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					String result = msg.obj.toString().trim();
					Toast.makeText(TuiKuanDinDanDetailActivity.this, result,
							Toast.LENGTH_SHORT).show();
				}
				break;
			// 获取信息成功
			case HANDLER_TUIKUANSHOUHUO_SUCCESS:
				dissDialog();
				virtualData();
				break;
			// 收获成功
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
		setContentView(R.layout.activity_tuikuandindandetail);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		tv_dindan_num = (TextView) findViewById(R.id.tv_dindan_num);
		tv_shouhuoname = (TextView) findViewById(R.id.tv_shouhuoname);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_address = (TextView) findViewById(R.id.tv_address);
		exListView = (MyExpandableListView) findViewById(R.id.exListView);
		tv_zffs = (TextView) findViewById(R.id.tv_zffs);
		tv_jssj = (TextView) findViewById(R.id.tv_jssj);
		tv_fhsj = (TextView) findViewById(R.id.tv_fhsj);
		tv_fptt = (TextView) findViewById(R.id.tv_fptt);
		tv_fpnr = (TextView) findViewById(R.id.tv_fpnr);
		tv_yunfei = (TextView) findViewById(R.id.tv_yunfei);
		tv_spze = (TextView) findViewById(R.id.tv_spze);
		tv_sfk = (TextView) findViewById(R.id.tv_sfk);
		tv_xdsj = (TextView) findViewById(R.id.tv_xdsj);
		tv_kefu = (TextView) findViewById(R.id.tv_kefu);
		tv_delete = (TextView) findViewById(R.id.tv_delete);
		tv_go_pay = (TextView) findViewById(R.id.tv_go_pay);
		tv_querenshouhuo = (TextView) findViewById(R.id.tv_querenshouhuo);
		tv_qupingjia = (TextView) findViewById(R.id.tv_qupingjia);
		tv_tuikuan = (TextView) findViewById(R.id.tv_tuikuan);
		tv_chakaneuliu = (TextView) findViewById(R.id.tv_chakaneuliu);

		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(TuiKuanDinDanDetailActivity.this);
		
		if (getIntent() != null) {
			order_id = getIntent().getStringExtra("order_id");
			showLoading();
			tuikuanshouhouDinDanDetailHttpPost();
		}
		exListView.setOnGroupClickListener(this);

		line_back.setOnClickListener(this);
		tv_kefu.setOnClickListener(this);
		tv_delete.setOnClickListener(this);
		tv_go_pay.setOnClickListener(this);
		tv_querenshouhuo.setOnClickListener(this);
		tv_qupingjia.setOnClickListener(this);
		tv_tuikuan.setOnClickListener(this);
		tv_chakaneuliu.setOnClickListener(this);
	}

	/**
	 * 其键是组元素的Id(通常是一个唯一指定组元素身份的值)
	 */
	private void virtualData() {
		groups.clear();
		children.clear();
		for (int i = 0; i < 1; i++) {

			groups.add(new GroupInfo(i + "", proDinDanDetailBean.getData()
					.getStore().getStore_name()));

			List<ProductInfo> products = new ArrayList<ProductInfo>();
			for (int j = 0; j < proDinDanDetailBean.getData()
					.getOrder_goods_list().size(); j++) {

				products.add(new ProductInfo(
						j + "",
						"商品",
						proDinDanDetailBean.getData().getOrder_goods_list()
								.get(j).getGoods_image(),
						proDinDanDetailBean.getData().getOrder_goods_list()
								.get(j).getGoods_name(),
						Double.parseDouble(proDinDanDetailBean.getData()
								.getOrder_goods_list().get(j).getGoods_price()),
						Integer.parseInt(proDinDanDetailBean.getData()
								.getOrder_goods_list().get(j).getGoods_num()),
						proDinDanDetailBean.getData().getOrder_goods_list()
								.get(j).getOrder_id()));
			}
			children.put(groups.get(i).getId(), products);// 将组元素的一个唯一值，这里取Id，作为子元素List的Key
		}

		prodindanDetailAdapter = new ProDindanDetailExpandableListViewAdapter(
				groups, children, TuiKuanDinDanDetailActivity.this);
		exListView.setAdapter(prodindanDetailAdapter);

		for (int i = 0; i < prodindanDetailAdapter.getGroupCount(); i++) {
			exListView.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
		}
	}

	/**
	 * 点击组禁止收缩功能
	 */

	@Override
	public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return true;
	}

	private void tuikuanshouhouDinDanDetailHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(TuiKuanDinDanDetailActivity.this));
		params.addBodyParameter("order_id", order_id);
		params.addBodyParameter("type", "2");
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
				Gson gson = new Gson();
				proDinDanDetailBean = gson.fromJson(result,
						ZhuProDinDanDetailBean.class);
				tv_dindan_num.setText(proDinDanDetailBean.getData()
						.getOrder_sn());
				tv_shouhuoname.setText(proDinDanDetailBean.getData()
						.getExtend_order_common().getReciver_name());
				tv_phone.setText(proDinDanDetailBean.getData()
						.getExtend_order_common().getReciver_info().getPhone());
				tv_address.setText(proDinDanDetailBean.getData()
						.getExtend_order_common().getReciver_info()
						.getAddress());
				tv_zffs.setText(proDinDanDetailBean.getData().getPayment_code());
				/**
				 * 时间戳转换成具体时间形式
				 */

				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ssE", Locale.getDefault());
				// 当前时间对象
				Date jssjcurDate = new Date(
						(Integer.parseInt(proDinDanDetailBean.getData()
								.getFinnshed_time())) * 1000L);
				Date fhsjcurDate = new Date(
						(Integer.parseInt(proDinDanDetailBean.getData()
								.getShipping_time())) * 1000L);
				Date xdcurDate = new Date((Integer.parseInt(proDinDanDetailBean
						.getData().getAdd_time())) * 1000L);
				String defaultTimeZoneID = TimeZone.getDefault().getID();// America/New_York
				// 在格式化日期前使用一个新的时区
				String newTimeZoneID = "Asia/Shanghai"; // Asia/Shanghai
				format.setTimeZone(TimeZone.getTimeZone(newTimeZoneID));

				if ("0".equals(proDinDanDetailBean.getData().getFinnshed_time())) {
					tv_jssj.setText("未签收");
				} else {
					tv_jssj.setText(format.format(jssjcurDate));

				}
				if ("0".equals(proDinDanDetailBean.getData().getShipping_time())) {
					tv_fhsj.setText("未发货");
				}else{
					tv_fhsj.setText(format.format(fhsjcurDate));
				}
				tv_fptt.setText(proDinDanDetailBean.getData()
						.getExtend_order_common().getInvoice_info()
						.getInv_title());
				tv_fpnr.setText(proDinDanDetailBean.getData()
						.getExtend_order_common().getInvoice_info()
						.getInv_content());
				tv_yunfei.setText(proDinDanDetailBean.getData()
						.getShipping_fee());
				tv_spze.setText(proDinDanDetailBean.getData().getGoods_amount());
				tv_sfk.setText(proDinDanDetailBean.getData().getOrder_amount());
				tv_xdsj.setText(format.format(xdcurDate));
				kefuphone = proDinDanDetailBean.getData().getStore()
						.getLive_store_tel();

				// 0(已取消)10(默认):待付款;20:已付款;30:待收货;40:已完成;
				if ("0".equals(proDinDanDetailBean.getData().getOrder_state())) {
					tv_kefu.setVisibility(View.VISIBLE);
				} else if ("10".equals(proDinDanDetailBean.getData()
						.getOrder_state())) {
					tv_kefu.setVisibility(View.VISIBLE);
					tv_delete.setVisibility(View.VISIBLE);
					tv_go_pay.setVisibility(View.VISIBLE);
				} else if ("20".equals(proDinDanDetailBean.getData()
						.getOrder_state())) {
					tv_kefu.setVisibility(View.VISIBLE);
					tv_querenshouhuo.setVisibility(View.VISIBLE);
					tv_tuikuan.setVisibility(View.VISIBLE);
				} else if ("30".equals(proDinDanDetailBean.getData()
						.getOrder_state())) {
					tv_kefu.setVisibility(View.VISIBLE);
					tv_querenshouhuo.setVisibility(View.VISIBLE);
					tv_chakaneuliu.setVisibility(View.VISIBLE);
				} else if ("40".equals(proDinDanDetailBean.getData()
						.getOrder_state())) {
					if ("0".equals(proDinDanDetailBean.getData()
							.getEvaluation_state())) {
						tv_kefu.setVisibility(View.VISIBLE);
						tv_qupingjia.setVisibility(View.VISIBLE);
					} else {
						tv_kefu.setVisibility(View.VISIBLE);
					}
				}

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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.tv_kefu:
			if (TextUtils.isEmpty(kefuphone)) {
				Toast.makeText(TuiKuanDinDanDetailActivity.this, "店铺电话暂时为空",
						Toast.LENGTH_SHORT).show();
			} else {
				showCallDialog();
			}

			break;
		case R.id.tv_dialog_cancel:
			calldialog.dismiss();
			break;
		case R.id.tv_phone_num:
			Intent callintent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ kefuphone));
			startActivity(callintent);
			break;
		case R.id.tv_delete:
			shanchuHttpPost();
			break;
		case R.id.tv_go_pay:
			intent = new Intent(TuiKuanDinDanDetailActivity.this,
					Mine_Dindan_TianxieDindanActivity.class);
			intent.putExtra("order_id", proDinDanDetailBean.getData()
					.getOrder_id());
			TuiKuanDinDanDetailActivity.this.startActivity(intent);
			break;
		case R.id.tv_querenshouhuo:
			showShouhuoDialog();
			break;
		case R.id.tv_qupingjia:
			intent = new Intent(TuiKuanDinDanDetailActivity.this,
					ProDetailXSQuPingJiaActivity.class);
			intent.putParcelableArrayListExtra("list", proDinDanDetailBean
					.getData().getOrder_goods_list());
			intent.putExtra("order_id", proDinDanDetailBean.getData()
					.getOrder_id());
			startActivityForResult(intent, 100);
			break;
		case R.id.tv_tuikuan:
			intent = new Intent(TuiKuanDinDanDetailActivity.this,
					TuikuanTuihuoActivity.class);
			intent.putExtra("order_id", proDinDanDetailBean.getData()
					.getOrder_id());
			intent.putExtra("num", proDinDanDetailBean.getData().getGoods_num());
			startActivityForResult(intent, 200);
			break;
		case R.id.tv_chakaneuliu:
			intent = new Intent(TuiKuanDinDanDetailActivity.this,
					ChaKanWuLiuActivity.class);
			intent.putExtra("order_id", proDinDanDetailBean.getData()
					.getOrder_id());
			TuiKuanDinDanDetailActivity.this.startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void showShouhuoDialog() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(TuiKuanDinDanDetailActivity.this)
				.inflate(R.layout.dialog_querenshouhuo, null);
		TextView tv_url = (TextView) view.findViewById(R.id.tv_url);
		TextView tv_dialog_cancel = (TextView) view
				.findViewById(R.id.tv_dialog_cancel);
		TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
		tv_url.setText("是否确认收货");

		final Dialog querenshouhuodialog = new Dialog(
				TuiKuanDinDanDetailActivity.this, R.style.mDialogStyle);
		querenshouhuodialog.setContentView(view);
		querenshouhuodialog.setCanceledOnTouchOutside(false);
		querenshouhuodialog.show();

		tv_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				querenshoushuoHttpPost(proDinDanDetailBean.getData()
						.getOrder_id());
			}
		});

		tv_dialog_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				querenshouhuodialog.dismiss();
			}
		});
	}

	// 确认收货接口
	protected void querenshoushuoHttpPost(String order_id) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(TuiKuanDinDanDetailActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(TuiKuanDinDanDetailActivity.this));
		params.addBodyParameter("order_id", order_id);
		params.addBodyParameter("type", "1");
		httpUtils.send(HttpMethod.POST, JiekouUtils.QUERENSHOUHUODINDAN,
				params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						qrshDinDanListInfo(arg0.result);

					}
				});
	}

	protected void qrshDinDanListInfo(String result) {
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

	// 删除订单
	protected void shanchuHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(TuiKuanDinDanDetailActivity.this));
		params.addBodyParameter("order_id", proDinDanDetailBean.getData()
				.getOrder_id());
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
		View view = LayoutInflater.from(TuiKuanDinDanDetailActivity.this)
				.inflate(R.layout.dialog_call, null);
		TextView tv_dialog_cancel = (TextView) view
				.findViewById(R.id.tv_dialog_cancel);
		TextView tv_phone_num = (TextView) view.findViewById(R.id.tv_phone_num);
		tv_phone_num.setText("是否拨打： " + kefuphone);
		tv_dialog_cancel.setOnClickListener(this);
		tv_phone_num.setOnClickListener(this);
		calldialog = new Dialog(TuiKuanDinDanDetailActivity.this,
				R.style.mDialogStyle);
		calldialog.setContentView(view);
		calldialog.setCanceledOnTouchOutside(false);
		calldialog.show();
	}

	// 加载中动画
	private Dialog loadingDialog;
	private String order_id;
	private ZhuProDinDanDetailBean proDinDanDetailBean;

	private MyExpandableListView exListView;
	private TextView tv_dindan_num;
	private TextView tv_shouhuoname;
	private TextView tv_phone;
	private TextView tv_address;
	private TextView tv_zffs;
	private TextView tv_jssj;
	private TextView tv_fhsj;
	private TextView tv_fptt;
	private TextView tv_fpnr;
	private TextView tv_yunfei;
	private TextView tv_spze;
	private TextView tv_sfk;
	private TextView tv_xdsj;
	private TextView tv_kefu;
	private String kefuphone;
	private TextView tv_delete;
	private TextView tv_go_pay;
	private TextView tv_querenshouhuo;
	private TextView tv_qupingjia;
	private TextView tv_tuikuan;
	private TextView tv_chakaneuliu;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(TuiKuanDinDanDetailActivity.this)
				.inflate(R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				TuiKuanDinDanDetailActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(TuiKuanDinDanDetailActivity.this,
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
