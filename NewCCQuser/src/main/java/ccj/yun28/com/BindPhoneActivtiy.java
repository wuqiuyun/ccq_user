package ccj.yun28.com;

import org.json.JSONException;
import org.json.JSONObject;

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
import ccj.yun28.com.mine.PhoneRenZhengActivity;
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
 * 关联手机号
 * 
 * @author meihuali
 * 
 */
public class BindPhoneActivtiy extends BaseActivity implements OnClickListener {

	// 验证码输入框
	private EditText et_yzm;
	// 发送验证码
	private TextView tv_yzm;
	private String rzphone;
	private String log_id;
	private String yzm;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取我的信息成功
	private static final int HANDLER_FASONGYZM_SUCCESS = 3;
	private static final int HANDLER_BINDPHONE_SUCCESS = 4;
	private static final int HANDLER_TOKEN_FAILURE = 5;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(BindPhoneActivtiy.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(BindPhoneActivtiy.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(BindPhoneActivtiy.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// 发送验证码成功
			case HANDLER_FASONGYZM_SUCCESS:
				dissDialog();
				CountDown();
				break;
			// 关联手机成功
			case HANDLER_BINDPHONE_SUCCESS:
				dissDialog();
				if (!PhoneRenZhengActivity.intance.isFinishing()) {
					PhoneRenZhengActivity.intance.finish();
				}
				if (!RZPhoneActivity.rzintance.isFinishing()) {
					RZPhoneActivity.rzintance.finish();
				}
				finish();
				break;

			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(BindPhoneActivtiy.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent intent = new Intent(BindPhoneActivtiy.this,
						LoginActivity.class);
				startActivity(intent);
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
		setContentView(R.layout.activity_findpassword);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		// 接收验证码手机号
		TextView tv_phone = (TextView) findViewById(R.id.tv_phone);
		// 验证码输入框
		et_yzm = (EditText) findViewById(R.id.et_yzm);
		// 发送验证码
		tv_yzm = (TextView) findViewById(R.id.tv_yzm);
		// 下一步
		TextView tv_next = (TextView) findViewById(R.id.tv_next);

		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(BindPhoneActivtiy.this);
		
		if (getIntent() != null) {
			tv_title.setText("手机验证");
			rzphone = getIntent().getStringExtra("rzphone");
			tv_phone.setText(rzphone);
		}
		showLoading();
		sendYzmHttpPost();

		line_back.setOnClickListener(this);
		tv_yzm.setOnClickListener(this);
		tv_next.setOnClickListener(this);
	}

	// 发送验证码
	private void sendYzmHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("phone", rzphone);
		params.addBodyParameter("member_id",
				new DButil().getMember_id(BindPhoneActivtiy.this));
		// 1.注册，短信登陆验证 2.发货通知 3.修改账号密码 4.先消费后买单 5.用户支付成功通知商家 6订单退款
		// 7餐餐抢验证码 8签约密匙 10.关联手机，更换手机 66 图形验证码
		params.addBodyParameter("log_type", "10");
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
						fasongYZMListInfo(arg0.result);
					}
				});
	}

	protected void fasongYZMListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				log_id = object.getString("log_id");
				handler.sendEmptyMessage(HANDLER_FASONGYZM_SUCCESS);
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
		case R.id.tv_yzm:
			if ("重新发送".equals(tv_yzm.getText().toString().trim())) {
				
				showLoading();
				sendYzmHttpPost();
			}
			break;
		case R.id.tv_next:
			if (canPost()) {
				showLoading();
				bindPhoneHttpPost();
			}
			break;

		default:
			break;
		}
	}

	// 关联手机号
	private void bindPhoneHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token",
				new DButil().gettoken(BindPhoneActivtiy.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(BindPhoneActivtiy.this));
		params.addBodyParameter("log_id", log_id);
		params.addBodyParameter("code", yzm);
		params.addBodyParameter("member_mobile", rzphone);
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
						infoListInfo(arg0.result);
					}
				});
	}

	protected void infoListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				handler.sendEmptyMessage(HANDLER_BINDPHONE_SUCCESS);
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
			Toast.makeText(BindPhoneActivtiy.this, "验证码不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (yzm.length() < 4) {
			Toast.makeText(BindPhoneActivtiy.this, "验证码不正确", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (yzm.length() > 8) {
			Toast.makeText(BindPhoneActivtiy.this, "验证码不正确", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		return true;
	}

	// 60s时间倒计时(验证码)
	private void CountDown() {
		new CountDownTimer(60 * 1000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub

				tv_yzm.setText("(" + millisUntilFinished / 1000 + "s)"
						+ "后重新发送");
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				tv_yzm.setText("重新发送");
			}
		}.start();
	}

	// 加载中动画
	private Dialog loadingDialog;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(BindPhoneActivtiy.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				BindPhoneActivtiy.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(BindPhoneActivtiy.this, R.style.mDialogStyle);
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