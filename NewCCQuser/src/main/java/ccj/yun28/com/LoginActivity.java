package ccj.yun28.com;

import java.security.MessageDigest;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.wxapi.WXPayEntryActivity;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

/**
 * 登录页
 * 
 * @author meihuali
 * 
 */
public class LoginActivity extends BaseActivity implements OnClickListener,
		TextWatcher {

	private String db = "";
	// 用户名输入框
	private EditText et_user;
	// 密码输入框
	private EditText et_password;
	// 密码是否显示图标
	private ImageView iv_mima;
	// 密码是否显示标签
	private boolean flag = false;
	// 账户名
	private String zhanghu;
	// 密码
	private String password;
	// 获取的token值
	private String token;
	// 安卓唯一识别码
	private String android_id;
	// 用户id
	private String member_id;

	private String type = "";
	private TextView tv_login;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;
	private TextView tv_daojishi;
	private TextView tv_again;
	private EditText et_yzm;
	private EditText et_phone;
	private LinearLayout line_mima_login;
	private LinearLayout line_phone_login;
	private LinearLayout line_forget;
	private LinearLayout line_tishi;
	private RadioButton radio_mima;
	private RadioButton radio_phone;
	private ImageView iv_wx;

	private String loginType = "mima";

	// 操作数据库
	private DBHelper myDB;

	public static LoginActivity logionintance;

	// token获取是否成功
	private boolean tag = false;

	private IWXAPI api;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 错误
	private static final int HANDLER_NN_FAILURE = 1;
	// 登录成功
	private static final int HANDLER_LOGIN_SUCCESS = 2;
	// 登录失败
	private static final int HANDLER_LOGIN_FAILURE = 3;
	// 获取token成功
	private static final int HANDLER_TOKEN_SUCCESS = 4;
	// 获取验证码成功
	private static final int HANDLER_GETYZM_SUCCESS = 5;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(LoginActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(LoginActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取token成功
			case HANDLER_TOKEN_SUCCESS:
				dissDialog();
				tag = true;
				break;
			// 登录成功
			case HANDLER_LOGIN_SUCCESS:
				SharedUtil.saveStringValue(SharedCommon.DETAILNAME, zhanghu);
				dissDialog();
				Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT)
						.show();
				if (!"".equals(type)) {
					if ("qt".equals(type)) {
						finish();
					} else {
						Intent intent = new Intent(LoginActivity.this,
								MainActivity.class);
						intent.putExtra("type", type);
						SharedUtil.saveStringValue(SharedCommon.DBDH, type);
						startActivity(intent);
						finish();
					}
				}
				finish();
				break;
			// 获取验证码成功
			case HANDLER_GETYZM_SUCCESS:
				dissDialog();
				CountDown();
				Toast.makeText(LoginActivity.this, "验证码已发送", Toast.LENGTH_SHORT)
						.show();
				break;
			// 登录失败
			case HANDLER_LOGIN_FAILURE:
				dissDialog();
				String result = (String) msg.obj;
				Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT)
						.show();
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 注册
		LinearLayout line_zhuce = (LinearLayout) findViewById(R.id.line_zhuce);
		line_mima_login = (LinearLayout) findViewById(R.id.line_mima_login);
		// 用户名输入框
		et_user = (EditText) findViewById(R.id.et_user);
		// 密码输入框
		et_password = (EditText) findViewById(R.id.et_password);
		// 密码是否显示图标
		iv_mima = (ImageView) findViewById(R.id.iv_mima);
		// 登录
		tv_login = (TextView) findViewById(R.id.tv_login);
		// 忘记密码
		line_forget = (LinearLayout) findViewById(R.id.line_forget);
		// 发送验证码提示
		line_tishi = (LinearLayout) findViewById(R.id.line_tishi);
		radio_mima = (RadioButton) findViewById(R.id.radio_mima);
		radio_phone = (RadioButton) findViewById(R.id.radio_phone);

		line_phone_login = (LinearLayout) findViewById(R.id.line_phone_login);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_yzm = (EditText) findViewById(R.id.et_yzm);
		tv_daojishi = (TextView) findViewById(R.id.tv_daojishi);
		tv_again = (TextView) findViewById(R.id.tv_again);

		// 微信登录
		iv_wx = (ImageView) findViewById(R.id.iv_wx);

		String detailzhanghu = SharedUtil.getStringValue(
				SharedCommon.DETAILNAME, "");
		if (!TextUtils.isEmpty(detailzhanghu)) {
			et_user.setText(detailzhanghu);
		}

		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(LoginActivity.this);

		logionintance = this;

		if (getIntent() != null) {
			type = getIntent().getStringExtra("type");
			if ("wx".equals(type)) {

				showLoading();
				type = SharedUtil
						.getStringValue(SharedCommon.WXLOGINLSBC, "qt");
				String wxusermsg = getIntent().getStringExtra("wxmsg");
				wxLoginHttpPost(wxusermsg);
			}else{
				// 获取token前缀
				tokenHttpPost();
				if ("againme".equals(type)) {
					againme = true;
					type = "me";
				}
			}
		}

		myDB = new DBHelper(this);

		// 安卓唯一识别码
		android_id = Secure.getString(LoginActivity.this.getContentResolver(),
				Secure.ANDROID_ID);

		registToWX();

		line_back.setOnClickListener(this);
		line_zhuce.setOnClickListener(this);
		iv_mima.setOnClickListener(this);
		tv_login.setOnClickListener(this);
		line_forget.setOnClickListener(this);
		et_user.addTextChangedListener(this);
		et_password.addTextChangedListener(this);
		et_phone.addTextChangedListener(this);
		et_yzm.addTextChangedListener(this);
		radio_mima.setOnClickListener(this);
		radio_phone.setOnClickListener(this);
		tv_again.setOnClickListener(this);
		iv_wx.setOnClickListener(this);
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		if ("mima".equals(loginType)) {
			if (TextUtils.isEmpty(et_user.getText().toString().trim())
					|| TextUtils.isEmpty(et_password.getText().toString()
							.trim())) {
				tv_login.setBackgroundResource(R.drawable.hx);
			}
		} else {
			if (TextUtils.isEmpty(et_phone.getText().toString().trim())
					|| TextUtils.isEmpty(et_yzm.getText().toString().trim())) {
				tv_login.setBackgroundResource(R.drawable.hx);
			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	// 变化中
	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		if ("mima".equals(loginType)) {
			if (!TextUtils.isEmpty(et_user.getText().toString().trim())) {
				if (!TextUtils.isEmpty(et_password.getText().toString().trim())) {
					tv_login.setBackgroundResource(R.drawable.hhx);
				} else {
					tv_login.setBackgroundResource(R.drawable.hx);
				}
			} else {
				tv_login.setBackgroundResource(R.drawable.hx);
			}
		} else {
			if (!TextUtils.isEmpty(et_phone.getText().toString().trim())) {
				if (!TextUtils.isEmpty(et_yzm.getText().toString().trim())) {
					tv_login.setBackgroundResource(R.drawable.hhx);
				} else {
					tv_login.setBackgroundResource(R.drawable.hx);
				}
			} else {
				tv_login.setBackgroundResource(R.drawable.hx);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			// 返回
			if (againme) {
				intent = new Intent(LoginActivity.this, MainActivity.class);
				intent.putExtra("type", type);
				SharedUtil.saveStringValue(SharedCommon.DBDH, type);
				startActivity(intent);
			}else{
				onBackPressed();
			}
			break;
		case R.id.line_zhuce:
			// 注册
			intent = new Intent(LoginActivity.this, ZhuCeActivity.class);
			startActivity(intent);
			break;
		case R.id.radio_mima:
			if (!TextUtils.isEmpty(et_user.getText().toString().trim())) {
				if (!TextUtils.isEmpty(et_password.getText().toString().trim())) {
					tv_login.setBackgroundResource(R.drawable.hhx);
				} else {
					tv_login.setBackgroundResource(R.drawable.hx);
				}
			} else {
				tv_login.setBackgroundResource(R.drawable.hx);
			}
			loginType = "mima";
			line_mima_login.setVisibility(View.VISIBLE);
			line_phone_login.setVisibility(View.GONE);
			line_tishi.setVisibility(View.GONE);
			line_forget.setVisibility(View.VISIBLE);
			break;
		case R.id.radio_phone:
			if (!TextUtils.isEmpty(et_phone.getText().toString().trim())) {
				if (!TextUtils.isEmpty(et_yzm.getText().toString().trim())) {
					tv_login.setBackgroundResource(R.drawable.hhx);
				} else {
					tv_login.setBackgroundResource(R.drawable.hx);
				}
			} else {
				tv_login.setBackgroundResource(R.drawable.hx);
			}
			loginType = "phone";
			line_phone_login.setVisibility(View.VISIBLE);
			line_tishi.setVisibility(View.VISIBLE);
			line_mima_login.setVisibility(View.GONE);
			line_forget.setVisibility(View.GONE);
			break;
		case R.id.iv_mima:
			if (flag) {
				// 隐藏密码
				flag = false;
				et_password
						.setTransformationMethod(PasswordTransformationMethod
								.getInstance());
				// 光标显示在最右
				Editable ea = et_password.getText();
				et_password.setSelection(ea.length());

				iv_mima.setBackgroundResource(R.drawable.visible);
			} else {
				// 显示密码
				flag = true;
				et_password
						.setTransformationMethod(HideReturnsTransformationMethod
								.getInstance());
				// 光标显示在最右
				Editable ea = et_password.getText();
				et_password.setSelection(ea.length());

				iv_mima.setBackgroundResource(R.drawable.visiblel);
			}

			break;
		case R.id.tv_login:
			// 登录
			if (tag) {
				if ("mima".equals(loginType)) {
					if (canPost()) {
						showLoading();
						loginHttpPost();
					}

				} else {
					if (canPhonePost()) {
						showLoading();
						phoneLoginHttpPost();
					}
				}
			} else {
				Toast.makeText(LoginActivity.this, "获取前置token失败",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.line_forget:
			// 忘记密码
			intent = new Intent(LoginActivity.this, ForgetMMActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_again:
			if (et_phone.getText().toString().trim() != null
					&& !TextUtils.isEmpty(et_phone.getText().toString().trim()) 
					&& et_phone.getText().toString().trim().length() == 11) {
				showLoading();
				getYZMHttpPost();
			} else {
				Toast.makeText(LoginActivity.this, "手机号码格式错误",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.iv_wx:
			// 微信登录
			SharedUtil.saveStringValue(SharedCommon.WXLOGINLSBC, type);
			wxLogin();
			break;
		default:
			break;
		}
	}

	// 微信登录
	private void wxLoginHttpPost(String wxusermsg) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("info", wxusermsg);
		httpUtils.send(HttpMethod.POST, JiekouUtils.WEIXINLOGIN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						loginJsonInfo(arg0.result);
					}
				});
	}

	// 发送短信验证码接口
	private void getYZMHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("phone", et_phone.getText().toString().trim());
		// 30： 手机验证码登录
		params.addBodyParameter("log_type", "30");
		httpUtils.send(HttpMethod.POST, JiekouUtils.LOGINFASONGYZM, params,
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
				log_id = object.getString("code_id");
				handler.sendEmptyMessage(HANDLER_GETYZM_SUCCESS);
			} else if ("300".equals(status)) {
				String message = object.getString("message");
				tv_again.setText(message);
				handler.sendMessage(handler.obtainMessage(
						HANDLER_LOGIN_FAILURE, message));
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_LOGIN_FAILURE, message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 60s时间倒计时(验证码)
	private void CountDown() {
		tv_daojishi.setVisibility(View.VISIBLE);
		tv_again.setVisibility(View.GONE);
		new CountDownTimer(60 * 1000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub

				tv_daojishi.setText("发送短信中...(" + millisUntilFinished / 1000
						+ "s)");
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				tv_daojishi.setVisibility(View.GONE);
				tv_again.setVisibility(View.VISIBLE);
			}
		}.start();
	}

	// 接口获取token前缀
	private void tokenHttpPost() {

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		httpUtils.send(HttpMethod.POST, JiekouUtils.TOKEN_QZ, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Log.e("ee", "网络错误:" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						tokenjsoninfo(arg0.result);
					}
				});
	}

	// 解析token接口返回数据
	protected void tokenjsoninfo(String result) {
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				token = object.getString("token");
				handler.sendEmptyMessage(HANDLER_TOKEN_SUCCESS);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 登录网络请求
	private void loginHttpPost() {
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		// 用户名
		params.addBodyParameter("member_name", zhanghu);
		// 密码
		String md5password = MD5(password);
		params.addBodyParameter("member_passwd", md5password);
		// token字符串
		String tokenone = token + "be56e0" + android_id + zhanghu + password;
		// 加密token
		String jmtoken = MD5(tokenone);
		params.addBodyParameter("token", jmtoken);
		// 设备唯一码
		params.addBodyParameter("m_id", android_id);
		// 来源类型 1 安卓 2 ios 3 微信
		params.addBodyParameter("ctype", "1");
		// 登录类型 1 商城会员 member 2 卖家 store 3=业务员（运营商） marketer 4=代理商
		params.addBodyParameter("gtype", "1");

		httpUtils.send(HttpMethod.POST, JiekouUtils.LOGIN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Log.e("ee", "失败:" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						loginJsonInfo(arg0.result);
					}
				});
	}

	// 手机验证登录
	private void phoneLoginHttpPost() {
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("member_mobile", et_phone.getText().toString()
				.trim());
		params.addBodyParameter("code", et_yzm.getText().toString().trim());
		params.addBodyParameter("code_id", log_id);
		// token字符串
		String tokenone = token + "be56e0" + android_id
				+ et_phone.getText().toString().trim()
				+ MD5(et_phone.getText().toString().trim());
		// 加密token
		String jmtoken = MD5(tokenone);
		params.addBodyParameter("token", jmtoken);
		params.addBodyParameter("m_id", android_id);
		params.addBodyParameter("ctype", "1");
		params.addBodyParameter("gtype", "1");
		httpUtils.send(HttpMethod.POST, JiekouUtils.PHONELOGIN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Log.e("ee", "失败:" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						loginJsonInfo(arg0.result);
					}
				});
	}

	// 解析登录返回数据
	protected void loginJsonInfo(String result) {
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				JSONObject job = object.getJSONObject("data");
				InsertceshiData(job);
				handler.sendEmptyMessage(HANDLER_LOGIN_SUCCESS);
			} else {
				String msg = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_LOGIN_FAILURE, msg));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 检查输入框填写状态
	private boolean canPost() {
		zhanghu = et_user.getText().toString().trim();
		password = et_password.getText().toString().trim();
		if (TextUtils.isEmpty(zhanghu)) {
			Toast.makeText(LoginActivity.this, "账号不能为空", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		if (TextUtils.isEmpty(password)) {
			Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		if (password.length() < 6) {
			Toast.makeText(LoginActivity.this, "密码不能少于6位", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}

	// 检查输入框填写状态
	private boolean canPhonePost() {
		String phone = et_phone.getText().toString().trim();
		String yzm = et_yzm.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(LoginActivity.this, "手机号码不能为空", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		if (phone.length() != 11) {
			Toast.makeText(LoginActivity.this, "手机号码格式不正确", Toast.LENGTH_SHORT)
			.show();
			return false;
		}
		if (TextUtils.isEmpty(yzm)) {
			Toast.makeText(LoginActivity.this, "验证码不能为空", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		if (yzm.length() < 4) {
			Toast.makeText(LoginActivity.this, "验证码格式不正确", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
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

	// 插入数据
	private void InsertceshiData(JSONObject js) {
		try {
			SQLiteDatabase db = myDB.getWritableDatabase();
			member_id = js.getString("member_id");

			String member_name = js.getString("member_name");
			String member_truename = js.getString("member_truename");
			String is_vip = js.getString("is_vip");
			String token = js.getString("token");

			String regip = "";
			String regdate = "";
			String inviteid = "";
			String reg_id = "";
			String alipay_acc = "";
			String wx_id = "";
			String exrract_pwd = "";
			int status = 1;

			boolean isFirst = true;
			Cursor cursor = db.rawQuery(
					"select * from user where member_id = ?",
					new String[] { member_id });
			if (cursor.moveToFirst())
				isFirst = false;
			if (isFirst) {
				db.execSQL(
						"insert into user(member_id,member_name,member_truename,regip,regdate,inviteid,reg_id,alipay_acc,wx_id,exrract_pwd,status,is_vip,token) values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
						new Object[] { member_id, member_name, member_truename,
								regip, regdate, inviteid, reg_id, alipay_acc,
								wx_id, exrract_pwd, status, is_vip, token });
			} else {
				db.execSQL(
						"update user set member_name=?,member_truename=?,regip=?,regdate=?,inviteid=?,reg_id=?,alipay_acc=?,wx_id=?,exrract_pwd=?,status=?,is_vip=?,token=? where member_id=?",
						new Object[] { member_name, member_truename, regip,
								regdate, inviteid, reg_id, alipay_acc, wx_id,
								exrract_pwd, status, is_vip, token, member_id });
			}

			db.close();
			handler.sendEmptyMessage(HANDLER_LOGIN_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}

	}

	private void registToWX() {
		api = WXAPIFactory.createWXAPI(this, Utils.WX_APP_ID, false);
		// 将该app注册到微信
		api.registerApp(Utils.WX_APP_ID);
	}

	public void wxLogin() {
		if (!api.isWXAppInstalled()) {
			Toast.makeText(LoginActivity.this, "您还未安装微信客户端", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		final SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "wechat_sdk_demo_test";
		api.sendReq(req);
	}

	// 加载中动画
	private Dialog loadingDialog;
	private String log_id;
	private boolean againme;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(LoginActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(LoginActivity.this,
				R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(LoginActivity.this, R.style.mDialogStyle);
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
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
