package ccj.yun28.com;

import org.json.JSONException;
import org.json.JSONObject;

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

import android.app.Activity;
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

/**
 * 关联新的手机号
 * 
 * @author meihuali
 * 
 */
public class YuanPhone_yzm_nextActivity extends BaseActivity implements
		OnClickListener {

	private EditText et_phone;
	private TextView tv_getyzm;
	private TextView tv_daojishi;

	private EditText et_picyzm;
	private LinearLayout line_picyzm;
	private TextView tv_getpicyzm;

	// 短信验证码id
	private String log_id;
	// 图片验证码id
	private String picid = "";
	private boolean isneedpic = false;
	private BitmapUtils bitmapUtils;

	protected static final int HANDLER_NET_FAILURE = 0;
	private static final int HANDLER_GETYZM_SUCCESS = 1;
	private static final int HANDLER_TOKEN_FAILURE = 2;
	private static final int HANDLER_GETINFO_FAILURE = 3;
	private static final int HANDLER_CHECKYZM_SUCCESS = 4;
	private static final int HANDLER_NN_FAILURE = 5;
	// 是否需要图片验证码
	private static final int HANDLER_CHECKNEEDPIC_SUCCESS = 6;
	private static final int HANDLER_GETPICYZM_SUCCESS = 7;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(YuanPhone_yzm_nextActivity.this,
						"当前网络不可用,请检查网络", Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(YuanPhone_yzm_nextActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(YuanPhone_yzm_nextActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// 需要图形验证
			case HANDLER_CHECKNEEDPIC_SUCCESS:
				// 获取图片认证图片
				getpicrenzhengHttpPost();
				break;
			case HANDLER_GETPICYZM_SUCCESS:
				dissDialog();
				break;
			// 获取验证码成功
			case HANDLER_GETYZM_SUCCESS:
				dissDialog();
				CountDown();
				break;
			// 检查验证码成功
			case HANDLER_CHECKYZM_SUCCESS:
				dissDialog();
				Toast.makeText(YuanPhone_yzm_nextActivity.this, "关联成功",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				setResult(401, intent);
				finish();
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(YuanPhone_yzm_nextActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent lointent = new Intent(YuanPhone_yzm_nextActivity.this,
						LoginActivity.class);
				startActivity(lointent);
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
		setContentView(R.layout.activity_yuanphone_yzm_next);

		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);

		line_picyzm = (LinearLayout) findViewById(R.id.line_picyzm);
		et_picyzm = (EditText) findViewById(R.id.et_picyzm);
		tv_getpicyzm = (TextView) findViewById(R.id.tv_getpicyzm);

		et_phone = (EditText) findViewById(R.id.et_phone);
		et_yzm = (EditText) findViewById(R.id.et_yzm);
		tv_getyzm = (TextView) findViewById(R.id.tv_getyzm);
		tv_daojishi = (TextView) findViewById(R.id.tv_daojishi);
		TextView tv_finish = (TextView) findViewById(R.id.tv_finish);

		bitmapUtils = new BitmapUtils(YuanPhone_yzm_nextActivity.this);
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(YuanPhone_yzm_nextActivity.this);

		// 检查是否需要图形验证
		showLoading();
		checkNeedPicHttpPost();

		line_back.setOnClickListener(this);
		tv_getyzm.setOnClickListener(this);
		tv_finish.setOnClickListener(this);

	}

	// 检查是否需要图形验证
	private void checkNeedPicHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("phone", et_phone.getText().toString().trim());
		httpUtils.send(HttpMethod.POST, JiekouUtils.CHECK_NEEDPIC, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						checkNeedPicInfo(arg0.result);
					}
				});
	}

	protected void checkNeedPicInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				isneedpic = true;
				handler.sendEmptyMessage(HANDLER_CHECKNEEDPIC_SUCCESS);
			} else if ("700".equals(code)) {
				isneedpic = false;
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TOKEN_FAILURE, message));
			} else {
				isneedpic = false;
				handler.sendEmptyMessage(HANDLER_GETPICYZM_SUCCESS);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 获取图片认证图片
	protected void getpicrenzhengHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		httpUtils.send(HttpMethod.POST, JiekouUtils.PICRENZHENG_PIC, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						picrenzhengInfo(arg0.result);
					}
				});
	}

	protected void picrenzhengInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONObject data = object.getJSONObject("data");
				picid = data.getString("id");
				String picurl = data.getString("url");

				line_picyzm.setVisibility(View.VISIBLE);
				bitmapUtils.display(tv_getpicyzm, picurl);

				handler.sendEmptyMessage(HANDLER_GETPICYZM_SUCCESS);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.tv_getyzm:
			// 是否有图形验证码
			if (isneedpic) {
				if (et_picyzm.getText().toString().trim().length() != 4) {
					et_picyzm.setFocusable(true);
					Toast.makeText(YuanPhone_yzm_nextActivity.this, "图片验证码错误",
							Toast.LENGTH_SHORT).show();
				} else if (et_phone.getText().toString().trim().length() < 11) {
					et_phone.setFocusable(true);
					Toast.makeText(YuanPhone_yzm_nextActivity.this, "手机号码错误",
							Toast.LENGTH_SHORT).show();

				} else {
					showLoading();
					getYZMHttpPost();
				}
			} else {
				showLoading();
				getYZMHttpPost();
			}
			break;
		case R.id.tv_finish:
			if (canPost()) {
				showLoading();
				bangdingHttpPost();
			}
			break;

		default:
			break;
		}
	}

	// 检查验证码
	private void bangdingHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token",
				new DButil().gettoken(YuanPhone_yzm_nextActivity.this));
		params.addBodyParameter("log_id", log_id);
		params.addBodyParameter("code", et_yzm.getText().toString().trim());
		params.addBodyParameter("member_id",
				new DButil().getMember_id(YuanPhone_yzm_nextActivity.this));
		params.addBodyParameter("member_mobile", et_phone.getText().toString()
				.trim());
		httpUtils.send(HttpMethod.POST, JiekouUtils.BINDPHONE, params,
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

	private boolean canPost() {
		// TODO Auto-generated method stub
		String phonenum = et_phone.getText().toString().trim();
		String yzmnum = et_yzm.getText().toString().trim();
		if (TextUtils.isEmpty(phonenum)) {
			et_phone.setFocusable(true);
			Toast.makeText(YuanPhone_yzm_nextActivity.this, "手机号码不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (phonenum.length() != 11) {
			et_phone.setFocusable(true);
			Toast.makeText(YuanPhone_yzm_nextActivity.this, "手机号码格式不对",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(yzmnum)) {
			tv_getyzm.setFocusable(true);
			Toast.makeText(YuanPhone_yzm_nextActivity.this, "验证码不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (yzmnum.length() > 8) {
			et_yzm.setFocusable(true);
			Toast.makeText(YuanPhone_yzm_nextActivity.this, "验证码错误",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (yzmnum.length() < 4) {
			et_yzm.setFocusable(true);
			Toast.makeText(YuanPhone_yzm_nextActivity.this, "验证码错误",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	// 发送短信验证码接口
	private void getYZMHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("phone", et_phone.getText().toString().trim());
		params.addBodyParameter("member_id",
				new DButil().getMember_id(YuanPhone_yzm_nextActivity.this));
		// 1.注册，短信登陆验证 2.发货通知 3.修改账号密码 4.先消费后买单 5.用户支付成功通知商家 6订单退款
		// 7餐餐抢验证码 8签约密匙 10.关联手机，更换手机 66 图形验证码
		params.addBodyParameter("log_type", "10");
		params.addBodyParameter("captcha", et_picyzm.getText().toString()
				.trim());
		params.addBodyParameter("id", picid);
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

	// 60s时间倒计时(验证码)
	private void CountDown() {
		tv_daojishi.setVisibility(View.VISIBLE);
		tv_getyzm.setVisibility(View.GONE);
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
				tv_getyzm.setVisibility(View.VISIBLE);
			}
		}.start();
	}

	// 加载中动画
	private Dialog loadingDialog;
	private EditText et_yzm;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(YuanPhone_yzm_nextActivity.this)
				.inflate(R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				YuanPhone_yzm_nextActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(YuanPhone_yzm_nextActivity.this,
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
}
