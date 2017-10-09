package ccj.yun28.com.mine;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.DindanDetailExpandableListViewAdapter;
import ccj.yun28.com.bean.ZhuMineDinDanBuyBean;
import ccj.yun28.com.bean.gwc.GroupInfo;
import ccj.yun28.com.bean.gwc.ProductInfo;
import ccj.yun28.com.bean.wx.ZhuWXPayInfo;
import ccj.yun28.com.ccq.XfmdAcyiviy;
import ccj.yun28.com.gwc.GwcShouYinTaiActivity;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.utils.pay.AlipayUtil;
import ccj.yun28.com.utils.pay.PayResultDialog;
import ccj.yun28.com.utils.pay.receiver.PayResultReceiver;
import ccj.yun28.com.view.MyExpandableListView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 订单-去支付_填写订单页
 * 
 * @author meihuali
 * 
 */
public class Mine_Dindan_TianxieDindanActivity extends BaseActivity implements
		OnGroupClickListener, OnClickListener {

	private String order_id;
	private String zffs;
	private EditText et_password;
	private String fapiao_id;
	// 店铺商品信息
	private MyExpandableListView exListView;
	// 组元素数据列表
	private List<GroupInfo> groups = new ArrayList<GroupInfo>();
	// 子元素数据列表
	private Map<String, List<ProductInfo>> children = new HashMap<String, List<ProductInfo>>();
	private DindanDetailExpandableListViewAdapter dindanDetailAdapter;

	// 支付方式id
	private String zffs_id = "12";
	// 输入密码dialog
	private Dialog tianxiedindanDialog;
	// 收货人姓名
	private TextView tv_shr_name;
	// 收货人地址
	private TextView tv_shr_address;
	// 收货人电话
	private TextView tv_shr_phone;
	// 买家留言
	private TextView tv_mjly;
	// 发票
	private TextView tv_fapiao;
	// 快递名
	private TextView tv_kuaidi_name;
	// 商品总价
	private TextView tv_pro_all_price;
	// 运费
	private TextView tv_yunfei;
	// 省市区地址
	private TextView tv_adress_ssq;
	// 总价
	private TextView tv_all_price;
	// 支付方式
	private TextView tv_zffs;
	private TextView tv_mrzffs;
	// 订单号
	private String order_sn;

	// 微信支付
	private ZhuWXPayInfo zhuWXPayInfo;
	public static IWXAPI wxApi;
	private PayResultReceiver payResultReceiver;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取订单详情信息成功
	private static final int HANDLER_DINDANDETAILINFO_SUCCESS = 3;
	// 去支付返回数据成功
	private static final int HANDLER_QUZHIFU_SUCCESS = 4;
	private static final int HANDLER_QUZHIFU2_SUCCESS = 7;
	private static final int HANDLER_GETWEIXINCANSHU_SUCCESS = 5;
	// 密码正确
	private static final int HANDLER_CHECKPASSWORD_SUCCESS = 6;
	private static final int HANDLER_GETZHIFUBAOJIAMI_SUCCESS = 8;
	private static final int HANDLER_WXHUIDIAO_SUCCESS = 9;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(Mine_Dindan_TianxieDindanActivity.this,
						"当前网络不可用,请检查网络", Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(Mine_Dindan_TianxieDindanActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(Mine_Dindan_TianxieDindanActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取订单详情数据成功
			case HANDLER_DINDANDETAILINFO_SUCCESS:
				dissDialog();
				virtualData();
				break;
			// 去支付返回数据成功
			case HANDLER_QUZHIFU_SUCCESS:
				dissDialog();
				tianxiedindanDialog.dismiss();
				Toast.makeText(Mine_Dindan_TianxieDindanActivity.this, "购买成功",
						Toast.LENGTH_SHORT).show();
					dissDialog();
					XfmdAcyiviy.xfmdintance.finish();
					Intent intent = new Intent(Mine_Dindan_TianxieDindanActivity.this, ZhifuSuccessActivity.class);
					intent.putExtra("order_amount", zhuminedindanbuy.getBuy_list().getOrder_amount());
					intent.putExtra("order_id", order_id);
					intent.putExtra("type", "0");
					startActivity(intent);
					finish();
				break;
			// 去支付返回数据成功
			case HANDLER_QUZHIFU2_SUCCESS:
				dissDialog();
				if ("支付宝".equals(tv_zffs.getText().toString().trim())) {
					zfbjiamihttphost();
					initReceiver();
				} else if ("微信支付".equals(tv_zffs.getText().toString().trim())) {
					// 下面这两句是在微信支付的时候一定要加啊!!!!!!!!!!!!!!!!!
					showLoading();
					getwxcanshuHttpPost();
					initReceiver();
					wxApi = WXAPIFactory.createWXAPI(
							Mine_Dindan_TianxieDindanActivity.this,
							Utils.WX_APP_ID);
					wxApi.registerApp(Utils.WX_APP_ID);
				}
				break;
			// 获取微信详情数据成功
			case HANDLER_GETWEIXINCANSHU_SUCCESS:
				dissDialog();
				pay();
				break;
			// 密码正确
			case HANDLER_CHECKPASSWORD_SUCCESS:
				gopayHttpPost();
				break;
			case HANDLER_WXHUIDIAO_SUCCESS:
				Intent hdintent = new Intent(Mine_Dindan_TianxieDindanActivity.this, ZhifuSuccessActivity.class);
				hdintent.putExtra("order_amount", zhuminedindanbuy.getBuy_list().getOrder_amount());
				hdintent.putExtra("order_id", order_id);
				hdintent.putExtra("type", "0");
				startActivity(hdintent);
				finish();
				break;
			case HANDLER_GETZHIFUBAOJIAMI_SUCCESS:
				dissDialog();
				// String info =
				// "partner=\"2088121666678942\"&seller_id=\"appfad@28yun.com\"&out_trade_no=\"20161100007984011404\"&subject=\"重庆小面\"&body=\"重庆小面\"&total_fee=\"1\"&notify_url=\"http://app.28yun.com/index.php/webapi/Pay/AlipayReturn\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&sign=\"XBeZIm0EsC3%2BkIX3VwwaSl7kXXbsESRkdPS0MwnOa9yNEkKcNw5ovRPnTRy%2FIHyORNLzncuWNP9Rr8QfySTrvOsAmrO9QHOQAVKMUW44uv1fMIqf3rPcbE%2FPqdKE5chR9kVhtRlykbMX0WMLiQDQPHRFwASAQIOHAS%2FgFtL7sAQ%3D\"&sign_type=\"RSA\"";
				if (msg.obj != null) {
					String info = msg.obj.toString().trim();
					alipayDemo(info);
				}
				break;
				default:
				break;
			}
		};
	};

	/**
	 * 支付方法
	 */
	public synchronized void pay() {
		new Thread() {
			@Override
			public void run() {
				sendPayReq();
			}
		}.start();
	}

	private void sendPayReq() {
		PayReq payReq = new PayReq();
		payReq.appId = zhuWXPayInfo.getData().getAppid();
		payReq.partnerId = zhuWXPayInfo.getData().getPartnerid();
		payReq.prepayId = zhuWXPayInfo.getData().getPrepayid();
		payReq.nonceStr = zhuWXPayInfo.getData().getNoncestr();
		payReq.timeStamp = zhuWXPayInfo.getData().getTimestamp();
		payReq.packageValue = zhuWXPayInfo.getData().getPackagestr();
		payReq.sign = zhuWXPayInfo.getData().getSign();
		Mine_Dindan_TianxieDindanActivity.wxApi.sendReq(payReq);// 根据自己支付的activity来替换
		// content.wxApi.sendReq(payReq);// 根据自己支付的activity来替换
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mine_dindan_tianxiandindan);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 收货人姓名
		tv_shr_name = (TextView) findViewById(R.id.tv_shr_name);
		// 收货人电话
		tv_shr_phone = (TextView) findViewById(R.id.tv_shr_phone);
		// 收货人地址
		tv_shr_address = (TextView) findViewById(R.id.tv_shr_address);
		// 店铺商品信息
		exListView = (MyExpandableListView) findViewById(R.id.exListView);
		// 支付方式
		LinearLayout line_zffs = (LinearLayout) findViewById(R.id.line_zffs);
		tv_mrzffs = (TextView) findViewById(R.id.tv_mrzffs);
		tv_zffs = (TextView) findViewById(R.id.tv_zffs);
		// 快递名
		tv_kuaidi_name = (TextView) findViewById(R.id.tv_kuaidi_name);
		// 发票
		tv_fapiao = (TextView) findViewById(R.id.tv_fapiao);
		// 买家留言
		tv_mjly = (TextView) findViewById(R.id.tv_mjly);
		// 商品总价
		tv_pro_all_price = (TextView) findViewById(R.id.tv_pro_all_price);
		// 运费
		tv_yunfei = (TextView) findViewById(R.id.tv_yunfei);
		// 省市区地址
		tv_adress_ssq = (TextView) findViewById(R.id.tv_adress_ssq);
		// 总价
		tv_all_price = (TextView) findViewById(R.id.tv_all_price);
		// 提交订单
		TextView tv_tjdd = (TextView) findViewById(R.id.tv_goto_pay);
		
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(Mine_Dindan_TianxieDindanActivity.this);

		if (getIntent() != null) {
			order_id = getIntent().getStringExtra("order_id");
		}
		showLoading();
		minedinDanDetailInfoHttpPost();

		exListView.setOnGroupClickListener(this);
		line_back.setOnClickListener(this);
		line_zffs.setOnClickListener(this);
		tv_tjdd.setOnClickListener(this);

	}

	// 获取订单详情接口
	private void minedinDanDetailInfoHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(Mine_Dindan_TianxieDindanActivity.this));
		params.addBodyParameter("member_id", new DButil()
				.getMember_id(Mine_Dindan_TianxieDindanActivity.this));
		params.addBodyParameter("order_id", order_id);
		httpUtils.send(HttpMethod.POST, JiekouUtils.QUZHIFUDINDAN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						minedinDanDetailInfoListInfo(arg0.result);
					}
				});
	}

	// 获取订单详情数据解析
	protected void minedinDanDetailInfoListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				Gson gson = new Gson();
				zhuminedindanbuy = gson.fromJson(result,
						ZhuMineDinDanBuyBean.class);
				String sfydz = zhuminedindanbuy.getBuy_list().getAddress_info()
						.getIs_default();
				tv_shr_name.setText(zhuminedindanbuy.getBuy_list()
						.getAddress_info().getTrue_name());
				tv_shr_phone.setText(zhuminedindanbuy.getBuy_list()
						.getAddress_info().getMob_phone());
				tv_shr_address.setText(zhuminedindanbuy.getBuy_list()
						.getAddress_info().getArea_info()
						+ zhuminedindanbuy.getBuy_list().getAddress_info()
								.getAddress());
				String sfyfp = zhuminedindanbuy.getBuy_list().getInv_info()
						.getInv_state();
				if ("1".equals(sfyfp)) {
					tv_fapiao.setText(zhuminedindanbuy.getBuy_list()
							.getInv_info().getContent());
				}
				tv_kuaidi_name.setText(zhuminedindanbuy.getBuy_list()
						.getGoods_freight_name());
				tv_pro_all_price.setText("¥"
						+ zhuminedindanbuy.getBuy_list().getGoods_amount());
				tv_yunfei.setText("¥"
						+ zhuminedindanbuy.getBuy_list().getGoods_freight());
				tv_all_price.setText("¥"
						+ zhuminedindanbuy.getBuy_list().getOrder_amount());
				tv_adress_ssq.setText(zhuminedindanbuy.getBuy_list()
						.getAddress_info().getArea_info());
				fapiao_id = zhuminedindanbuy.getBuy_list().getInv_info()
						.getInv_id();
				order_sn = zhuminedindanbuy.getBuy_list().getTo_pay()
						.getOrder_sn();
				handler.sendEmptyMessage(HANDLER_DINDANDETAILINFO_SUCCESS);
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

	/**
	 * 其键是组元素的Id(通常是一个唯一指定组元素身份的值)
	 */
	private void virtualData() {
		groups.clear();
		children.clear();
		for (int i = 0; i < zhuminedindanbuy.getBuy_list().getStore_cart_list()
				.size(); i++) {

			groups.add(new GroupInfo(i + "", zhuminedindanbuy.getBuy_list()
					.getStore_cart_list().get(i).getStore_name()));

			List<ProductInfo> products = new ArrayList<ProductInfo>();
			for (int j = 0; j < zhuminedindanbuy.getBuy_list()
					.getStore_cart_list().get(i).getGoods_list().size(); j++) {

				products.add(new ProductInfo(j + "", "商品", zhuminedindanbuy
						.getBuy_list().getStore_cart_list().get(i)
						.getGoods_list().get(j).getGoods_image_url(),
						zhuminedindanbuy.getBuy_list().getStore_cart_list()
								.get(i).getGoods_list().get(j).getGoods_name(),
						Double.parseDouble(zhuminedindanbuy.getBuy_list()
								.getStore_cart_list().get(i).getGoods_list()
								.get(j).getGoods_price()),
						Integer.parseInt(zhuminedindanbuy.getBuy_list()
								.getStore_cart_list().get(i).getGoods_list()
								.get(j).getGoods_num()), zhuminedindanbuy
								.getBuy_list().getStore_cart_list().get(i)
								.getGoods_list().get(j).getCart_id()));
			}
			children.put(groups.get(i).getId(), products);// 将组元素的一个唯一值，这里取Id，作为子元素List的Key
		}

		dindanDetailAdapter = new DindanDetailExpandableListViewAdapter(groups,
				children, Mine_Dindan_TianxieDindanActivity.this);
		exListView.setAdapter(dindanDetailAdapter);

		for (int i = 0; i < dindanDetailAdapter.getGroupCount(); i++) {
			exListView.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
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
		case R.id.line_zffs:
			intent = new Intent(Mine_Dindan_TianxieDindanActivity.this,
					GwcShouYinTaiActivity.class);
			intent.putExtra("jine", zhuminedindanbuy.getBuy_list()
					.getOrder_amount().toString().trim());
			startActivityForResult(intent, 100);
			break;
		case R.id.tv_goto_pay:
			if (canTjdd()) {
				if ("支付宝".equals(tv_zffs.getText().toString().trim())|| "微信支付".equals(tv_zffs.getText().toString().trim())) {
					showLoading();
					gopayHttpPost();
				} else {
					showmimaLoading();
				}
			}
			break;
		case R.id.tv_dialog_cancel:
			tianxiedindanDialog.dismiss();
			break;
		case R.id.tv_ok:
			if (canmima()) {
				showLoading();
				checkpasswordHttpPost();
			}
			break;

		default:
			break;
		}
	}

	private boolean canTjdd() {
		// TODO Auto-generated method stub
		String zfff = tv_zffs.getText().toString().trim();
		if ("".equals(zfff) || zfff == null) {
			Toast.makeText(Mine_Dindan_TianxieDindanActivity.this, "支付方式不能为空哦",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private boolean canmima() {
		// TODO Auto-generated method stub
		String mima = et_password.getText().toString().trim();
		if ("".equals(mima) || mima == null) {
			Toast.makeText(Mine_Dindan_TianxieDindanActivity.this, "密码不能为空哦",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (mima.length() < 6) {
			Toast.makeText(Mine_Dindan_TianxieDindanActivity.this, "密码不能少于六位哦",
					Toast.LENGTH_SHORT).show();
			return false;

		} else if (mima.length() > 20) {
			Toast.makeText(Mine_Dindan_TianxieDindanActivity.this,
					"密码不能多于二十位哦", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	// 检查密码接口
	private void checkpasswordHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(Mine_Dindan_TianxieDindanActivity.this));
		params.addBodyParameter("member_id", new DButil()
				.getMember_id(Mine_Dindan_TianxieDindanActivity.this));
		params.addBodyParameter("paypwd", MD5(et_password.getText().toString()
				.trim()));
		httpUtils.send(HttpMethod.POST, JiekouUtils.CHECKZHIFUMIMA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e("ee", "失败:" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						checkpasswordListInfo(arg0.result);
					}
				});
	}

	protected void checkpasswordListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				handler.sendEmptyMessage(HANDLER_CHECKPASSWORD_SUCCESS);
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

	// 去支付接口
	private void gopayHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(Mine_Dindan_TianxieDindanActivity.this));
		params.addBodyParameter("member_id", new DButil()
				.getMember_id(Mine_Dindan_TianxieDindanActivity.this));
		params.addBodyParameter("order_id", order_id);
		params.addBodyParameter("pay_code", zffs_id);
		httpUtils.send(HttpMethod.POST, JiekouUtils.TIJIAOZHIFUDINDAN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e("ee", "失败:" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						gopayListInfo(arg0.result);
					}
				});
	}

	// 去支付数据解析
	protected void gopayListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			String message = object.getString("message");
			if ("200".equals(code)) {
				handler.sendEmptyMessage(HANDLER_QUZHIFU_SUCCESS);
			} else if ("201".equals(code)) {
				handler.sendEmptyMessage(HANDLER_QUZHIFU2_SUCCESS);
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

	// 密码框
	private void showmimaLoading() {
		View view = LayoutInflater.from(Mine_Dindan_TianxieDindanActivity.this)
				.inflate(R.layout.dialog_tianxiedindan, null);
		et_password = (EditText) view.findViewById(R.id.et_password);
		TextView tv_dialog_cancel = (TextView) view
				.findViewById(R.id.tv_dialog_cancel);
		TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
		tv_dialog_cancel.setOnClickListener(this);
		tv_ok.setOnClickListener(this);
		tianxiedindanDialog = new Dialog(
				Mine_Dindan_TianxieDindanActivity.this, R.style.mDialogStyle);
		tianxiedindanDialog.setContentView(view);
		tianxiedindanDialog.setCanceledOnTouchOutside(false);
		tianxiedindanDialog.show();
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

	// 加载中动画
	private Dialog loadingDialog;
	private ZhuMineDinDanBuyBean zhuminedindanbuy;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(Mine_Dindan_TianxieDindanActivity.this)
				.inflate(R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				Mine_Dindan_TianxieDindanActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(Mine_Dindan_TianxieDindanActivity.this,
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
		if (data != null) {
			if (resultCode == 300) {
				zffs = data.getStringExtra("zffs").toString().trim();
				zffs_id = data.getStringExtra("zffs_id");
				tv_zffs.setText(zffs);
				tv_mrzffs.setText("支付方式");
				
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// MD5加密，32位
	public static String MD5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	private void initReceiver() {
		payResultReceiver = new PayResultReceiver() {
			@Override
			public void payResult(String resultInfo) {
				if (TextUtils.equals(resultInfo, Utils.IS_ALIPAY)) {
					// 支付宝支付成功
					Toast.makeText(Mine_Dindan_TianxieDindanActivity.this,
							"支付成功", Toast.LENGTH_LONG).show();
					finish();
					Intent intent = new Intent(Mine_Dindan_TianxieDindanActivity.this, ZhifuSuccessActivity.class);
					intent.putExtra("order_amount", zhuminedindanbuy.getBuy_list().getOrder_amount());
					intent.putExtra("order_id", order_id);
					intent.putExtra("type", "0");
					startActivity(intent);
				} else if (TextUtils.equals(resultInfo, Utils.IS_WXPAY)) {
					// 微信支付成功
					wxhuidiaoHttpPost();
					
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Utils.pay_result_receiver_action);
		registerReceiver(payResultReceiver, intentFilter);
	}

	//微信回调
		protected void wxhuidiaoHttpPost() {
			// TODO Auto-generated method stub
			RequestParams params = new RequestParams();
			params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
			httpUtils.send(HttpMethod.GET, JiekouUtils.WXHUIDIAO+"?order_sn="+order_sn,params, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// TODO Auto-generated method stub
					handler.sendEmptyMessage(HANDLER_NET_FAILURE);
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					// TODO Auto-generated method stub
					wxhuidiaoJsonList(arg0.result);
				}
			});
		}

		
		protected void wxhuidiaoJsonList(String result) {
			// TODO Auto-generated method stub
			try {
				JSONObject object = new JSONObject(result);
				String code = object.getString("code");
				String message = object.getString("message");
				if ("200".equals(code)) {
					handler.sendEmptyMessage(HANDLER_WXHUIDIAO_SUCCESS);
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (payResultReceiver != null)
			unregisterReceiver(payResultReceiver);
	}

	// 获得支付宝加密参数接口
	protected void zfbjiamihttphost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("out_trade_no", "\"" + zhuminedindanbuy.getBuy_list().getTo_pay().getOrder_sn() + "\"");
		params.addBodyParameter("subject", "\""
				+ zhuminedindanbuy.getBuy_list().getTo_pay().getGoods_name() + "\"");
		params.addBodyParameter("body", "\""
				+ zhuminedindanbuy.getBuy_list().getTo_pay().getGoods_name() + "\"");
		params.addBodyParameter("total_fee", "\"" + zhuminedindanbuy.getBuy_list().getOrder_amount() + "\"");
		httpUtils.send(HttpMethod.POST, JiekouUtils.GETZHIFUBAO, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						getzfbjiamiListInfo(arg0.result);
					}
				});
	}

	protected void getzfbjiamiListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			String data = object.getString("data");
			if ("200".equals(code)) {
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETZHIFUBAOJIAMI_SUCCESS, data));
			} else {
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETINFO_FAILURE, data));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 获得微信支付参数接口
	protected void getwxcanshuHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		httpUtils.send(HttpMethod.GET, JiekouUtils.GETWEIXINCANSHU
				+ "?order_sn=" + zhuminedindanbuy.getBuy_list().getTo_pay().getOrder_sn() + "&order_amount=" + zhuminedindanbuy.getBuy_list().getTo_pay().getOrder_amount(),params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						getweixincanshuListInfo(arg0.result);
					}
				});
	}

	// 获得微信支付参数数据解析
	protected void getweixincanshuListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				Gson gson = new Gson();
				zhuWXPayInfo = gson.fromJson(result, ZhuWXPayInfo.class);
				handler.sendEmptyMessage(HANDLER_GETWEIXINCANSHU_SUCCESS);
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

	/**
	 * 支付宝支付方法
	 * 
	 * @param orderInfo
	 *            支付字符串，该值从后台获取。
	 */
	private void alipayDemo(String orderInfo) {
		AlipayUtil alipayUtil = new AlipayUtil(
				new AlipayUtil.OnAlipayResponse() {
					@Override
					public void onResponse(int status, String tips) {
						if (status == AlipayUtil.OnAlipayResponse.SUCCESS) {
							// 支付成功
							// 如果支付成功后需要做的操作是在本页面的则完成方法一,若在其他页面则完成方法二
							/****************** 方法一 ****************/
							// 然后再在sureListener()里面做支付完成后需要的操作
//							PayResultDialog payResultDialog = new PayResultDialog(
//									Mine_Dindan_TianxieDindanActivity.this) {
//								@Override
//								public void sureListener() {
//									// 点击确定后的操作
//									Toast.makeText(
//											Mine_Dindan_TianxieDindanActivity.this,
//											"支付成功", Toast.LENGTH_SHORT).show();
//								}
//							};
//							payResultDialog.setHintInfo("支付成功");
//							payResultDialog.show();
							finish();
							Intent intent = new Intent(Mine_Dindan_TianxieDindanActivity.this, ZhifuSuccessActivity.class);
							intent.putExtra("order_amount", zhuminedindanbuy.getBuy_list().getOrder_amount());
							intent.putExtra("order_id", order_id);
							intent.putExtra("type", "0");
							startActivity(intent);
							/****************** 方法二 ****************/
							// Intent intent = new Intent();
							// intent.setAction(Constant.pay_result_receiver_action);
							// intent.putExtra(Constant.pay_result_receiver_info,
							// Constant.IS_ALIPAY);
							// PayActivity.this.sendBroadcast(intent); // 广播发送
						} else if (status == AlipayUtil.OnAlipayResponse.FAIL) {
							// 支付失败
							PayResultDialog payResultDialog = new PayResultDialog(
									Mine_Dindan_TianxieDindanActivity.this) {
								@Override
								public void sureListener() {
									// 点击确定后的操作
								}
							};
							payResultDialog.setHintInfo("支付失败");
							payResultDialog.show();
						} else if (status == AlipayUtil.OnAlipayResponse.CONFIRMATION) {
							// 支付结果确认中
							PayResultDialog payResultDialog = new PayResultDialog(
									Mine_Dindan_TianxieDindanActivity.this) {
								@Override
								public void sureListener() {
									// 点击确定后的操作
									Intent intent = new Intent(Mine_Dindan_TianxieDindanActivity.this, DFKDinDanActivtiy.class);
									startActivity(intent);
									finish();
								}
							};
							payResultDialog.setHintInfo("支付结果确认中");
							payResultDialog.show();
						}
					}
				});
		alipayUtil.pay(this, orderInfo);
	}
}
