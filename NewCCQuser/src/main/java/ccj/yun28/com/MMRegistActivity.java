package ccj.yun28.com;

import java.security.MessageDigest;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 密码重置
 * 
 * @author meihuali
 * 
 */
public class MMRegistActivity extends BaseActivity implements OnClickListener {

	// 密码输入框
	private EditText et_newmm;
	// 确认密码输入框
	private EditText et_again_newmm;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取我的信息成功
	private static final int HANDLER_SETLOGINMIMA_SUCCESS = 3;
	private static final int HANDLER_TOKEN_FAILURE = 4;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(MMRegistActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(MMRegistActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {

					Toast.makeText(MMRegistActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// 重置密码成功
			case HANDLER_SETLOGINMIMA_SUCCESS:
				dissDialog();
				showLoginDialog();
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(MMRegistActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent intent = new Intent(MMRegistActivity.this,
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
		setContentView(R.layout.activity_mmregist);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 密码输入框
		et_newmm = (EditText) findViewById(R.id.et_newmm);
		// 确认密码输入框
		et_again_newmm = (EditText) findViewById(R.id.et_again_newmm);
		// 完成
		TextView tv_ok = (TextView) findViewById(R.id.tv_ok);

		if (getIntent() != null) {
			log_id = getIntent().getStringExtra("log_id");
			code = getIntent().getStringExtra("code");
		}
		line_back.setOnClickListener(this);
		tv_ok.setOnClickListener(this);
	}

	// 设置登录密码
	private void setLoginMiMaHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(MMRegistActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(MMRegistActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(MMRegistActivity.this));
		String md5password = MD5(mima);
		params.addBodyParameter("member_pwd", md5password);
		params.addBodyParameter("code", code);
		params.addBodyParameter("log_id", log_id);
		params.addBodyParameter("type", "1");
		httpUtils.send(HttpMethod.POST, JiekouUtils.UPDATAMIMA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						setLoginMiMaListInfo(arg0.result);
					}
				});
	}

	protected void setLoginMiMaListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				handler.sendEmptyMessage(HANDLER_SETLOGINMIMA_SUCCESS);
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
		case R.id.tv_ok:
			if (canPost()) {
				showLoading();
				setLoginMiMaHttpPost();
			}
			break;

		default:
			break;
		}
	}

	protected void showLoginDialog() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(MMRegistActivity.this).inflate(
				R.layout.dialog_querenshouhuo, null);
		TextView tv_url = (TextView) view.findViewById(R.id.tv_url);
		TextView tv_dialog_cancel = (TextView) view
				.findViewById(R.id.tv_dialog_cancel);
		TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
		tv_url.setText("修改登录密码成功");
		tv_ok.setText("重新登录");
		final Dialog logindialog = new Dialog(MMRegistActivity.this,
				R.style.mDialogStyle);
		logindialog.setContentView(view);
		logindialog.setCanceledOnTouchOutside(false);
		logindialog.show();

		tv_dialog_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				logindialog.dismiss();
				SharedUtil.saveStringValue(SharedCommon.DBDH, "me");
				if (!LoginActivity.logionintance.isFinishing()) {
					LoginActivity.logionintance.finish();
				}
				if (!ForgetMMActivity.forgetintance.isFinishing()) {
					ForgetMMActivity.forgetintance.finish();
				}
				if (!RZPhoneActivity.rzintance.isFinishing()) {
					RZPhoneActivity.rzintance.finish();
				}
				if (!FindPasswordActivtiy.findpwdintance.isFinishing()) {
					FindPasswordActivtiy.findpwdintance.finish();
				}
				finish();
			}
		});
		tv_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				logindialog.dismiss();
				if (!ForgetMMActivity.forgetintance.isFinishing()) {
					ForgetMMActivity.forgetintance.finish();
				}
				if (!RZPhoneActivity.rzintance.isFinishing()) {
					RZPhoneActivity.rzintance.finish();
				}
				if (!FindPasswordActivtiy.findpwdintance.isFinishing()) {
					FindPasswordActivtiy.findpwdintance.finish();
				}
				finish();
			}
		});
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

	// 检查输入框填写状态
	private boolean canPost() {
		mima = et_newmm.getText().toString().trim();
		String mima_again = et_again_newmm.getText().toString().trim();
		if (TextUtils.isEmpty(mima)) {
			Toast.makeText(MMRegistActivity.this, "密码不能为空", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (mima.length() < 6) {
			Toast.makeText(MMRegistActivity.this, "密码不能少于6位",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (mima.length() > 20) {
			Toast.makeText(MMRegistActivity.this, "密码不能大于20位",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(mima_again)) {
			Toast.makeText(MMRegistActivity.this, "确认密码不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (!mima.equals(mima_again)) {
			Toast.makeText(MMRegistActivity.this, "两次密码输入不一样",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	// 加载中动画
	private Dialog loadingDialog;
	private String mima;
	private String log_id;
	private String code;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(MMRegistActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				MMRegistActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(MMRegistActivity.this, R.style.mDialogStyle);
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
