package ccj.yun28.com.mine;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
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
import ccj.yun28.com.bean.wx.ZhuWXPayInfo;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.utils.pay.AlipayUtil;
import ccj.yun28.com.utils.pay.PayResultDialog;
import ccj.yun28.com.utils.pay.receiver.PayResultReceiver;

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
 * 购买VIP收银台
 * 
 * @author meihuali
 * 
 */
public class BuyVIPShouYinTaiActivtity extends BaseActivity implements
		OnClickListener {

	private TextView tv_yuejine;
	private List<Map<String, String>> zhanghuJineList;
	private Dialog mimaDialog;
	private EditText et_password;
	private String pay_code;
	private ZhuWXPayInfo zhuWXPayInfo;
	public static IWXAPI wxApi;
	private PayResultReceiver payResultReceiver;
	// 订单金额
	private String order_amount;
	// 订单号
	private String order_sn;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取账户金额信息成功
	private static final int HANDLER_ZHANGHUJINE_SUCCESS = 3;
	private static final int HANDLER_CHECKPASSWORD_SUCCESS = 4;
	private static final int HANDLER_TJDD_SUCCESS = 5;
	private static final int HANDLER_GETWEIXINCANSHU_SUCCESS = 6;
	private static final int HANDLER_GETZHIFUBAOJIAMI_SUCCESS = 7;
	private static final int HANDLER_WXHUIDIAO_SUCCESS = 8;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(BuyVIPShouYinTaiActivtity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(BuyVIPShouYinTaiActivtity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(BuyVIPShouYinTaiActivtity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取订单详情数据成功
			case HANDLER_ZHANGHUJINE_SUCCESS:
				dissDialog();
				tv_yuejine.setText(zhanghuJineList.get(2).get("value"));
				break;
			// 获取订单详情数据成功
			case HANDLER_CHECKPASSWORD_SUCCESS:
				tjddHttpPost();
				break;
			// 获取订单详情数据成功
			case HANDLER_TJDD_SUCCESS:
				dissDialog();
				if ("11".equals(pay_code)) {
					zfbjiamihttphost();
					initReceiver();
				} else if ("12".equals(pay_code)) {
					getwxcanshuHttpPost();
					initReceiver();
					wxApi = WXAPIFactory.createWXAPI(
							BuyVIPShouYinTaiActivtity.this, Utils.WX_APP_ID);
					wxApi.registerApp(Utils.WX_APP_ID);
				} else {
					dissDialog();
					Toast.makeText(BuyVIPShouYinTaiActivtity.this, "购买成功",
							Toast.LENGTH_SHORT).show();
					setResult(201);
					
					finish();
				}
				break;
			case HANDLER_GETWEIXINCANSHU_SUCCESS:
				dissDialog();
				pay();
				break;
			case HANDLER_WXHUIDIAO_SUCCESS:
				Toast.makeText(BuyVIPShouYinTaiActivtity.this, "支付成功",
						Toast.LENGTH_LONG).show();
				setResult(201);
				finish();
				break;
			case HANDLER_GETZHIFUBAOJIAMI_SUCCESS:
				dissDialog();
//				String info = "partner=\"2088121666678942\"&seller_id=\"appfad@28yun.com\"&out_trade_no=\"20161100007984011404\"&subject=\"重庆小面\"&body=\"重庆小面\"&total_fee=\"1\"&notify_url=\"http://app.28yun.com/index.php/webapi/Pay/AlipayReturn\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&sign=\"XBeZIm0EsC3%2BkIX3VwwaSl7kXXbsESRkdPS0MwnOa9yNEkKcNw5ovRPnTRy%2FIHyORNLzncuWNP9Rr8QfySTrvOsAmrO9QHOQAVKMUW44uv1fMIqf3rPcbE%2FPqdKE5chR9kVhtRlykbMX0WMLiQDQPHRFwASAQIOHAS%2FgFtL7sAQ%3D\"&sign_type=\"RSA\"";
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
		BuyVIPShouYinTaiActivtity.wxApi.sendReq(payReq);// 根据自己支付的activity来替换
		// content.wxApi.sendReq(payReq);// 根据自己支付的activity来替换
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ccqshouyintai);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		TextView tv_jine = (TextView) findViewById(R.id.tv_jine);
		// 支付宝支付
		LinearLayout line_zhifubaozhifu = (LinearLayout) findViewById(R.id.line_zhifubaozhifu);
		// 微信支付
		LinearLayout line_weixinzhifu = (LinearLayout) findViewById(R.id.line_weixinzhifu);
		// 余额支付
		LinearLayout line_yuezhifu = (LinearLayout) findViewById(R.id.line_yuezhifu);
		tv_yuejine = (TextView) findViewById(R.id.tv_yuejine);

		zhanghuJineList = new ArrayList<Map<String, String>>();
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(BuyVIPShouYinTaiActivtity.this);

		if (getIntent() != null) {
			String jine = getIntent().getStringExtra("jine");
			num = getIntent().getStringExtra("num");
			tv_jine.setText(jine);
		}
		showLoading();
		getZhanghuJineHttpPost();

		line_back.setOnClickListener(this);
		line_zhifubaozhifu.setOnClickListener(this);
		line_weixinzhifu.setOnClickListener(this);
		line_yuezhifu.setOnClickListener(this);
	}

	// 获得账户各个金额接口
	private void getZhanghuJineHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(BuyVIPShouYinTaiActivtity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(BuyVIPShouYinTaiActivtity.this));
		httpUtils.send(HttpMethod.POST, JiekouUtils.GETZHANGHUJINE, params,
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
						getZhanghuJineListInfo(arg0.result);
					}
				});
	}

	// 获得账户各个金额数据解析
	protected void getZhanghuJineListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONArray array = object.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = array.getJSONObject(i);
					getZhanghuJineDetailListInfo(json);
				}
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

	private void getZhanghuJineDetailListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String code = json.getString("code");
			String title = json.getString("title");
			String value = json.getString("value");
			Map<String, String> zhanghuJineMap = new HashMap<String, String>();
			zhanghuJineMap.put("code", code);
			zhanghuJineMap.put("title", title);
			zhanghuJineMap.put("value", value);
			zhanghuJineList.add(zhanghuJineMap);
			handler.sendEmptyMessage(HANDLER_ZHANGHUJINE_SUCCESS);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private void initReceiver() {
		payResultReceiver = new PayResultReceiver() {
			@Override
			public void payResult(String resultInfo) {
				if (TextUtils.equals(resultInfo, Utils.IS_ALIPAY)) {
					// 支付宝支付成功
					Toast.makeText(BuyVIPShouYinTaiActivtity.this, "支付成功",
							Toast.LENGTH_LONG).show();
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
			httpUtils.send(HttpMethod.GET, JiekouUtils.WXHUIDIAO+"?order_sn="+order_sn,params,
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_zhifubaozhifu:
			pay_code = "11";
			showLoading();
			tjddHttpPost();
			break;
		case R.id.line_weixinzhifu:
			pay_code = "12";
			showLoading();
			tjddHttpPost();
			break;
		case R.id.line_yuezhifu:
			pay_code = "1";
			showmimaLoading();
			break;
		case R.id.tv_dialog_cancel:
			mimaDialog.dismiss();
			break;
		case R.id.tv_ok:
			mimaDialog.dismiss();
			if (canmima()) {
				showLoading();
				checkPwdHttpPost();
			}
			break;
		default:
			break;
		}
	}

	private boolean canmima() {
		// TODO Auto-generated method stub
		String mima = et_password.getText().toString().trim();
		if ("".equals(mima) || mima == null) {
			Toast.makeText(BuyVIPShouYinTaiActivtity.this, "密码不能为空哦",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (mima.length() < 6) {
			Toast.makeText(BuyVIPShouYinTaiActivtity.this, "密码不能少于六位哦",
					Toast.LENGTH_SHORT).show();
			return false;

		} else if (mima.length() > 20) {
			Toast.makeText(BuyVIPShouYinTaiActivtity.this, "密码不能多于二十位哦",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	// 检查密码
	private void checkPwdHttpPost() {
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(BuyVIPShouYinTaiActivtity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(BuyVIPShouYinTaiActivtity.this));
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

	// 加载中动画
	private Dialog loadingDialog;
	private String goods_name;
	private String num;
	private HttpUtils httpUtils;
	private String[] verstring;
	private Utils utils;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(BuyVIPShouYinTaiActivtity.this)
				.inflate(R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				BuyVIPShouYinTaiActivtity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(BuyVIPShouYinTaiActivtity.this,
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

	// 密码框
	private void showmimaLoading() {
		View view = LayoutInflater.from(BuyVIPShouYinTaiActivtity.this)
				.inflate(R.layout.dialog_tianxiedindan, null);
		et_password = (EditText) view.findViewById(R.id.et_password);
		TextView tv_dialog_cancel = (TextView) view
				.findViewById(R.id.tv_dialog_cancel);
		TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
		tv_dialog_cancel.setOnClickListener(this);
		tv_ok.setOnClickListener(this);
		mimaDialog = new Dialog(BuyVIPShouYinTaiActivtity.this,
				R.style.mDialogStyle);
		mimaDialog.setContentView(view);
		mimaDialog.setCanceledOnTouchOutside(false);
		mimaDialog.show();
	}

	// 提交订单接口
	private void tjddHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(BuyVIPShouYinTaiActivtity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(BuyVIPShouYinTaiActivtity.this));
		params.addBodyParameter("ifcart", "4");
		params.addBodyParameter("cart_id", "101552|" + num);
		params.addBodyParameter("address_id", "1");
		params.addBodyParameter("pay_name", "online");
		params.addBodyParameter("pay_code", pay_code);
		httpUtils.send(HttpMethod.POST, JiekouUtils.TIJIAODINDAN, params,
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
						tjddListInfo(arg0.result);
					}
				});
	}

	// 提交订单数据解析
	protected void tjddListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				String pay_sn = object.getString("pay_sn");
				order_sn = object.getString("order_sn");
				JSONObject pay_info = object.getJSONObject("pay_info");
				goods_name = pay_info.getString("goods_name");
				String order_id = pay_info.getString("order_id");
				order_amount = pay_info.getString("order_amount");
				handler.sendEmptyMessage(HANDLER_TJDD_SUCCESS);
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

	// 获得微信支付参数接口
	protected void getwxcanshuHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		httpUtils.send(HttpMethod.GET, JiekouUtils.GETWEIXINCANSHU
				+ "?order_sn=" + order_sn + "&order_amount=" + order_amount ,params,
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
	
	// 获得支付宝加密参数接口
			protected void zfbjiamihttphost() {
				// TODO Auto-generated method stub
				RequestParams params = new RequestParams();
				params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
				params.addBodyParameter("out_trade_no", "\""+order_sn+"\"");
				params.addBodyParameter("subject", "\""+goods_name+"\"");
				params.addBodyParameter("body", "\""+goods_name+"\"");
				params.addBodyParameter("total_fee", "\""+order_amount+"\"");
				httpUtils.send(HttpMethod.POST, JiekouUtils.GETZHIFUBAO , params,
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

	
	/**
	 * 支付宝支付方法
	 * 
	 * @param orderInfo
	 *            支付字符串，该值从后台获取。
	 */
	private void alipayDemo(String orderInfo) {
		AlipayUtil alipayUtil = new AlipayUtil(new AlipayUtil.OnAlipayResponse() {
			@Override
			public void onResponse(int status, String tips) {
				if (status == AlipayUtil.OnAlipayResponse.SUCCESS) {
					setResult(201);
					finish();
					// 支付成功
					// 如果支付成功后需要做的操作是在本页面的则完成方法一,若在其他页面则完成方法二
					/****************** 方法一 ****************/
					// 然后再在sureListener()里面做支付完成后需要的操作
//					PayResultDialog payResultDialog = new PayResultDialog(BuyVIPShouYinTaiActivtity.this) {
//						@Override
//						public void sureListener() {
//							// 点击确定后的操作
//							Toast.makeText(BuyVIPShouYinTaiActivtity.this, "支付成功", Toast.LENGTH_SHORT).show();
//						}
//					};
//					payResultDialog.setHintInfo("支付成功");
//					payResultDialog.show();
					/****************** 方法二 ****************/
					// Intent intent = new Intent();
					// intent.setAction(Constant.pay_result_receiver_action);
					// intent.putExtra(Constant.pay_result_receiver_info,
					// Constant.IS_ALIPAY);
					// PayActivity.this.sendBroadcast(intent); // 广播发送
				} else if (status == AlipayUtil.OnAlipayResponse.FAIL) {
					// 支付失败
					PayResultDialog payResultDialog = new PayResultDialog(BuyVIPShouYinTaiActivtity.this) {
						@Override
						public void sureListener() {
							// 点击确定后的操作
						}
					};
					payResultDialog.setHintInfo("支付失败");
					payResultDialog.show();
				} else if (status == AlipayUtil.OnAlipayResponse.CONFIRMATION) {
					// 支付结果确认中
					PayResultDialog payResultDialog = new PayResultDialog(BuyVIPShouYinTaiActivtity.this) {
						@Override
						public void sureListener() {
							// 点击确定后的操作
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
