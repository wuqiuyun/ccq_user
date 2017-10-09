package ccj.yun28.com.mine;

import java.security.MessageDigest;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.ZhuMineBuyZkjBean;
import ccj.yun28.com.bean.wx.ZhuWXPayInfo;
import ccj.yun28.com.ccq.CcqShouYinTaiActivity;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.utils.pay.AlipayUtil;
import ccj.yun28.com.utils.pay.PayResultDialog;
import ccj.yun28.com.utils.pay.receiver.PayResultReceiver;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
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
 * 我的- 订单- 去支付-餐餐抢填写订单页
 * 
 * @author meihuali
 * 
 */
public class Mine_DinDan_BuyZKJActivity extends BaseActivity implements
		OnClickListener {

	// 订单号
	private TextView tv_dindan_num;
	// 店铺名
	private TextView tv_shop_name;
	// 商品图
	private ImageView iv_pic;
	// 商品名
	private TextView tv_pro_name;
	// 价格
	private TextView tv_price;
	// 实付款
	private TextView tv_zf_price;
	// 支付方式
	private TextView tv_zffs;
	private TextView tv_zffsmr;
	// 支付方式id
	private String zffs_id = "12";
	// 商品id
	private String order_id;
	// 什么支付方式
	private String zffs;
	// 发票id
	private String sfyfapiao;
	// 输入密码dialog
	private Dialog tianxiedindanDialog;
	private EditText et_password;
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
	// 获取提交订单信息数据成功
	private static final int HANDLER_QUZHIFU_SUCCESS = 4;
	private static final int HANDLER_QUZHIFU2_SUCCESS = 7;
	// 获取微信信息正确
	private static final int HANDLER_GETWEIXINCANSHU_SUCCESS = 5;
	// 支付密码正确
	private static final int HANDLER_CHECKPASSWORD_SUCCESS = 6;
	private static final int HANDLER_GETZHIFUBAOJIAMI_SUCCESS = 8;
	private static final int HANDLER_WXHUIDIAO_SUCCESS = 9;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(Mine_DinDan_BuyZKJActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(Mine_DinDan_BuyZKJActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(Mine_DinDan_BuyZKJActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取订单详情数据成功
			case HANDLER_DINDANDETAILINFO_SUCCESS:
				dissDialog();
				break;
			// 获取提交订单信息数据成功
			case HANDLER_QUZHIFU_SUCCESS:
				dissDialog();
				tianxiedindanDialog.dismiss();
				Toast.makeText(Mine_DinDan_BuyZKJActivity.this, "购买成功",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(Mine_DinDan_BuyZKJActivity.this, ZhifuSuccessActivity.class);
				intent.putExtra("order_amount", zhuMineBuyZkj.getBuy_list().getTo_pay().getOrder_amount());
				intent.putExtra("order_id", order_id);
				intent.putExtra("type", "3");
				startActivity(intent);
				finish();
				// 获取提交订单信息数据成功
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
							Mine_DinDan_BuyZKJActivity.this, Utils.WX_APP_ID);
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
				Intent hdintent = new Intent(Mine_DinDan_BuyZKJActivity.this, ZhifuSuccessActivity.class);
				hdintent.putExtra("order_amount", zhuMineBuyZkj.getBuy_list().getTo_pay().getOrder_amount());
				hdintent.putExtra("order_id", order_id);
				hdintent.putExtra("type", "3");
				startActivity(hdintent);
				finish();
				break;
			case HANDLER_GETZHIFUBAOJIAMI_SUCCESS:
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
		Mine_DinDan_BuyZKJActivity.wxApi.sendReq(payReq);// 根据自己支付的activity来替换
		// content.wxApi.sendReq(payReq);// 根据自己支付的activity来替换
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buyzkja);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 订单号
		tv_dindan_num = (TextView) findViewById(R.id.tv_dindan_num);
		// 店铺名
		tv_shop_name = (TextView) findViewById(R.id.tv_shop_name);
		// 商品图
		iv_pic = (ImageView) findViewById(R.id.iv_pic);
		// 商品名
		tv_pro_name = (TextView) findViewById(R.id.tv_pro_name);
		// 价格
		tv_price = (TextView) findViewById(R.id.tv_price);
		// 支付方式
		LinearLayout line_zffs = (LinearLayout) findViewById(R.id.line_zffs);
		tv_zffsmr = (TextView) findViewById(R.id.tv_zffsmr);
		tv_zffs = (TextView) findViewById(R.id.tv_zffs);
		// 实付款
		tv_zf_price = (TextView) findViewById(R.id.tv_zf_price);
		// 提交订单
		LinearLayout line_tjdd = (LinearLayout) findViewById(R.id.line_tjdd);
		
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(Mine_DinDan_BuyZKJActivity.this);

		if (getIntent() != null) {
			order_id = getIntent().getStringExtra("order_id");
		}
		showLoading();
		minedinDanDetailInfoHttpPost(order_id);

		line_back.setOnClickListener(this);
		line_zffs.setOnClickListener(this);
		line_tjdd.setOnClickListener(this);
	}

	// 获得支付宝加密参数接口
	protected void zfbjiamihttphost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("out_trade_no", "\"" + zhuMineBuyZkj.getBuy_list().getTo_pay().getOrder_sn() + "\"");
		params.addBodyParameter("subject", "\""
				+ zhuMineBuyZkj.getBuy_list().getTo_pay().getGoods_name() + "\"");
		params.addBodyParameter("body", "\""
				+ zhuMineBuyZkj.getBuy_list().getTo_pay().getGoods_name() + "\"");
		params.addBodyParameter("total_fee", "\"" + zhuMineBuyZkj.getBuy_list().getTo_pay().getOrder_amount() + "\"");
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
				+ "?order_sn=" + zhuMineBuyZkj.getBuy_list().getTo_pay().getOrder_sn() + "&order_amount=" + zhuMineBuyZkj.getBuy_list().getTo_pay().getOrder_amount(),params,
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

	// 获取订单详情接口
	private void minedinDanDetailInfoHttpPost(String order_id) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(Mine_DinDan_BuyZKJActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(Mine_DinDan_BuyZKJActivity.this));
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
						minedinDanInfoListInfo(arg0.result);
					}
				});
	}

	// 获取订单详情数据解析
	protected void minedinDanInfoListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				Gson gson = new Gson();
				zhuMineBuyZkj = gson.fromJson(result, ZhuMineBuyZkjBean.class);
				BitmapUtils bitmapUtils = new BitmapUtils(
						Mine_DinDan_BuyZKJActivity.this);
				bitmapUtils.display(iv_pic, zhuMineBuyZkj.getBuy_list()
						.getStore_cart_list().get(0).getGoods_list().get(0)
						.getGoods_image_url());
				tv_shop_name.setText(zhuMineBuyZkj.getBuy_list()
						.getStore_info().getStore_name());
				tv_pro_name.setText(zhuMineBuyZkj.getBuy_list()
						.getStore_cart_list().get(0).getGoods_list().get(0)
						.getGoods_name());
				tv_price.setText("¥："
						+ zhuMineBuyZkj.getBuy_list().getStore_cart_list()
								.get(0).getGoods_list().get(0).getGoods_price());
				tv_zf_price
						.setText("¥："
								+ zhuMineBuyZkj.getBuy_list()
										.getStore_cart_list().get(0)
										.getGoods_list().get(0)
										.getGoods_price());
				tv_dindan_num.setText(zhuMineBuyZkj.getBuy_list()
						.getStore_info().getMember_mobile());
				
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_zffs:
			Intent intent = new Intent(Mine_DinDan_BuyZKJActivity.this,
					CcqShouYinTaiActivity.class);
			intent.putExtra("jine", zhuMineBuyZkj.getBuy_list()
					.getStore_cart_list().get(0).getGoods_list().get(0)
					.getGoods_price());
			startActivityForResult(intent, 100);
			break;
		case R.id.line_tjdd:
			if (!TextUtils.isEmpty(tv_zffs.getText().toString().trim())) {
				if ("支付宝".equals(tv_zffs.getText().toString().trim()) || "微信支付".equals(tv_zffs.getText().toString().trim())) {
					showLoading();
					gopayHttpPost();
				} else {
					showmimaLoading();
				}
			} else {
				Toast.makeText(Mine_DinDan_BuyZKJActivity.this, "请选择支付方式",
						Toast.LENGTH_SHORT).show();
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

		default:
			break;
		}
	}

	private boolean canmima() {
		// TODO Auto-generated method stub
		String mima = et_password.getText().toString().trim();
		if ("".equals(mima) || mima == null) {
			Toast.makeText(Mine_DinDan_BuyZKJActivity.this, "密码不能为空哦",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (mima.length() < 6) {
			Toast.makeText(Mine_DinDan_BuyZKJActivity.this, "密码不能少于六位哦",
					Toast.LENGTH_SHORT).show();
			return false;

		} else if (mima.length() > 20) {
			Toast.makeText(Mine_DinDan_BuyZKJActivity.this, "密码不能多于二十位哦",
					Toast.LENGTH_SHORT).show();
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
				new DButil().gettoken(Mine_DinDan_BuyZKJActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(Mine_DinDan_BuyZKJActivity.this));
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
				new DButil().gettoken(Mine_DinDan_BuyZKJActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(Mine_DinDan_BuyZKJActivity.this));
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == 300) {
			zffs = data.getStringExtra("zffs").toString().trim();
			zffs_id = data.getStringExtra("zffs_id");
			tv_zffs.setText(zffs);
			tv_zffsmr.setText("支付方式");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 密码框
	private void showmimaLoading() {
		View view = LayoutInflater.from(Mine_DinDan_BuyZKJActivity.this)
				.inflate(R.layout.dialog_tianxiedindan, null);
		et_password = (EditText) view.findViewById(R.id.et_password);
		TextView tv_dialog_cancel = (TextView) view
				.findViewById(R.id.tv_dialog_cancel);
		TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
		tv_dialog_cancel.setOnClickListener(this);
		tv_ok.setOnClickListener(this);
		tianxiedindanDialog = new Dialog(Mine_DinDan_BuyZKJActivity.this,
				R.style.mDialogStyle);
		tianxiedindanDialog.setContentView(view);
		tianxiedindanDialog.setCanceledOnTouchOutside(false);
		tianxiedindanDialog.show();
	}

	// 加载中动画
	private Dialog loadingDialog;
	private ZhuMineBuyZkjBean zhuMineBuyZkj;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(Mine_DinDan_BuyZKJActivity.this)
				.inflate(R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				Mine_DinDan_BuyZKJActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(Mine_DinDan_BuyZKJActivity.this,
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

	private void initReceiver() {
		payResultReceiver = new PayResultReceiver() {
			@Override
			public void payResult(String resultInfo) {
				if (TextUtils.equals(resultInfo, Utils.IS_ALIPAY)) {
					// 支付宝支付成功
					Toast.makeText(Mine_DinDan_BuyZKJActivity.this,
							"支付成功", Toast.LENGTH_LONG).show();
					Intent intent = new Intent(Mine_DinDan_BuyZKJActivity.this, ZhifuSuccessActivity.class);
					intent.putExtra("order_amount", zhuMineBuyZkj.getBuy_list().getTo_pay().getOrder_amount());
					intent.putExtra("order_id", order_id);
					intent.putExtra("type", "3");
					startActivity(intent);
					finish();
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
			httpUtils.send(HttpMethod.GET, JiekouUtils.WXHUIDIAO+"?order_sn="+zhuMineBuyZkj.getBuy_list().getTo_pay().getOrder_sn(),params,
					new RequestCallBack<String>() {

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
//							// 支付成功
//							// 如果支付成功后需要做的操作是在本页面的则完成方法一,若在其他页面则完成方法二
//							/****************** 方法一 ****************/
//							// 然后再在sureListener()里面做支付完成后需要的操作
//							PayResultDialog payResultDialog = new PayResultDialog(
//									Mine_DinDan_BuyZKJActivity.this) {
//								@Override
//								public void sureListener() {
//									// 点击确定后的操作
//									Toast.makeText(
//											Mine_DinDan_BuyZKJActivity.this,
//											"支付成功", Toast.LENGTH_SHORT).show();
//								}
//							};
//							payResultDialog.setHintInfo("支付成功");
//							payResultDialog.show();
							Toast.makeText(Mine_DinDan_BuyZKJActivity.this,
									"支付成功", Toast.LENGTH_LONG).show();
							Intent intent = new Intent(Mine_DinDan_BuyZKJActivity.this, ZhifuSuccessActivity.class);
							intent.putExtra("order_amount", zhuMineBuyZkj.getBuy_list().getTo_pay().getOrder_amount());
							intent.putExtra("order_id", order_id);
							intent.putExtra("type", "3");
							startActivity(intent);
							finish();
							/****************** 方法二 ****************/
							// Intent intent = new Intent();
							// intent.setAction(Constant.pay_result_receiver_action);
							// intent.putExtra(Constant.pay_result_receiver_info,
							// Constant.IS_ALIPAY);
							// PayActivity.this.sendBroadcast(intent); // 广播发送
						} else if (status == AlipayUtil.OnAlipayResponse.FAIL) {
							// 支付失败
							PayResultDialog payResultDialog = new PayResultDialog(
									Mine_DinDan_BuyZKJActivity.this) {
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
									Mine_DinDan_BuyZKJActivity.this) {
								@Override
								public void sureListener() {
									// 点击确定后的操作
									Intent intent = new Intent(Mine_DinDan_BuyZKJActivity.this, DFKDinDanActivtiy.class);
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
