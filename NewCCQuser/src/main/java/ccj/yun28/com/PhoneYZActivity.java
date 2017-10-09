package ccj.yun28.com;

import java.security.MessageDigest;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
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
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 注册-手机验证页
 * 
 * @author meihuali
 * 
 */
public class PhoneYZActivity extends BaseActivity implements OnClickListener {

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 错误
	private static final int HANDLER_NN_FAILURE = 1;
	// 获取验证码成功
	private static final int HANDLER_GETYZM_SUCCESS = 2;
	private static final int HANDLER_GETINFO_FAILURE = 3;
	private static final int HANDLER_CHECKYZM_SUCCESS = 4;
	private static final int HANDLER_TOKEN_FAILURE = 5;
	private static final int HANDLER_ZHUCE_SUCCESS = 6;
	private static final int HANDLER_ZHUCE_FAILURE = 7;
	// 验证码输入框
	private EditText et_yzm;
	// 验证码倒计时
	private TextView tv_daojishi;
	// 再次发送验证码倒计时
	private TextView tv_again;
	// 验证码id
	private String log_id;
	// 手机号码
	private String phone;
	// 输入的验证码
	private String yzm;

	public static PhoneYZActivity zhucephoneyzintance;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(PhoneYZActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(PhoneYZActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(PhoneYZActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// 获取验证码成功
			case HANDLER_GETYZM_SUCCESS:
				dissDialog();
				CountDown();
				break;
			// 检查验证码成功
			case HANDLER_CHECKYZM_SUCCESS:
				registHttpPost();
				break;
			// 注册失败
			case HANDLER_ZHUCE_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(PhoneYZActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// 注册成功
			case HANDLER_ZHUCE_SUCCESS:
				dissDialog();
				Toast.makeText(PhoneYZActivity.this, "注册成功", Toast.LENGTH_SHORT)
						.show();
				if (!ZhuCeActivity.zhuceintance.isFinishing()) {
					ZhuCeActivity.zhuceintance.finish();
				}
				finish();
				Intent lointent = new Intent(PhoneYZActivity.this,
						LoginActivity.class);
				startActivity(lointent);
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(PhoneYZActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				if (!ZhuCeActivity.zhuceintance.isFinishing()) {
					ZhuCeActivity.zhuceintance.finish();
				}
				finish();
				Intent logintent = new Intent(PhoneYZActivity.this,
						LoginActivity.class);
				startActivity(logintent);
				break;

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phoneyz);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 验证码输入框
		et_yzm = (EditText) findViewById(R.id.et_yzm);
		// 验证码倒计时
		tv_daojishi = (TextView) findViewById(R.id.tv_daojishi);
		// 再次发送验证码倒计时
		tv_again = (TextView) findViewById(R.id.tv_again);
		// 下一步
		TextView tv_two_next = (TextView) findViewById(R.id.tv_two_next);

		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(PhoneYZActivity.this);

		zhucephoneyzintance = this;

		if (getIntent() != null) {
			phone = getIntent().getStringExtra("phone");
			tjr = getIntent().getStringExtra("tjr");
			mima = getIntent().getStringExtra("password");
		}

		showLoading();
		getYZMHttpPost();
		line_back.setOnClickListener(this);
		tv_again.setOnClickListener(this);
		tv_two_next.setOnClickListener(this);
	}

	// 发送短信验证码接口
	private void getYZMHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("phone", phone);
		// 1.注册，短信登陆验证 2.发货通知 3.修改账号密码 4.先消费后买单 5.用户支付成功通知商家 6订单退款
		// 7餐餐抢验证码 8签约密匙 10.关联手机，更换手机 66 图形验证码
		params.addBodyParameter("log_type", "1");
		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWFASONGYZM, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						getYZMListInfo(arg0.result);
					}
				});
	}

	// 发送短信验证码接口返回数据
	protected void getYZMListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				log_id = object.getString("log_id");
				handler.sendEmptyMessage(HANDLER_GETYZM_SUCCESS);
			} else if ("700".equals(status)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TOKEN_FAILURE, message));
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
			if (!ZhuCeActivity.zhuceintance.isFinishing()) {
				ZhuCeActivity.zhuceintance.finish();
			}
			finish();
			Intent logintent = new Intent(PhoneYZActivity.this,
					LoginActivity.class);
			startActivity(logintent);
			break;
		case R.id.tv_again:
			showLoading();
			getYZMHttpPost();
			break;
		case R.id.tv_two_next:
			if (canPost()) {
				showLoading();
				checkYZMHttpPost();
			}
			break;

		default:
			break;
		}
	}

	// 检查验证码
	private void checkYZMHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("member_id",
				new DButil().getMember_id(PhoneYZActivity.this));
		params.addBodyParameter("log_id", log_id);
		params.addBodyParameter("code", yzm);
		httpUtils.send(HttpMethod.POST, JiekouUtils.JIANCHAYZM, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						checkYZMListInfo(arg0.result);
					}
				});
	}

	protected void checkYZMListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				handler.sendEmptyMessage(HANDLER_CHECKYZM_SUCCESS);
			} else if ("700".equals(code)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TOKEN_FAILURE, message));
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

	// 检查输入框填写状态
	private boolean canPost() {
		yzm = et_yzm.getText().toString().trim();
		if (TextUtils.isEmpty(yzm)) {
			Toast.makeText(PhoneYZActivity.this, "验证码不能为空", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (yzm.length() > 8) {
			Toast.makeText(PhoneYZActivity.this, "验证码错误", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (yzm.length() < 4) {
			Toast.makeText(PhoneYZActivity.this, "验证码错误", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}

	// 60s时间倒计时(验证码)
	private void CountDown() {
		tv_daojishi.setVisibility(View.VISIBLE);
		tv_again.setVisibility(View.GONE);
		new CountDownTimer(60 * 1000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub

				tv_daojishi.setText("(" + millisUntilFinished / 1000 + "s)"
						+ "后重新获取");
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				tv_daojishi.setVisibility(View.GONE);
				tv_again.setVisibility(View.VISIBLE);
			}
		}.start();
	}

	// 注册接口
	private void registHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(PhoneYZActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("member_mobile",phone);
		String md5password = MD5(mima);
		params.addBodyParameter("member_passwd", md5password);
		params.addBodyParameter("inviter_id",tjr);
		params.addBodyParameter("log_id", log_id);
		params.addBodyParameter("code", yzm);
		// 1自定义会员注册 2手机会员注册 3微信自定义会员 4微信手机会员
		params.addBodyParameter("reg_type", "2");
		httpUtils.send(HttpMethod.POST, JiekouUtils.REGIST, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						registListInfo(arg0.result);
					}
				});
	}

	// 解析注册接口返回数据
	protected void registListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				handler.sendEmptyMessage(HANDLER_ZHUCE_SUCCESS);
			} else if ("700".equals(status)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TOKEN_FAILURE, message));
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_ZHUCE_FAILURE, message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 加载中动画
	private Dialog loadingDialog;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;
	private String tjr;
	private String mima;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(PhoneYZActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				PhoneYZActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(PhoneYZActivity.this, R.style.mDialogStyle);
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

}
