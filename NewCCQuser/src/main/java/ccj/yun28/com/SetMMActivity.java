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
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 注册-设置密码页(废弃)
 * 
 * @author meihuali
 * 
 */
public class SetMMActivity extends BaseActivity implements OnClickListener {

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 错误
	private static final int HANDLER_NN_FAILURE = 1;
	// 获取验证码成功
	private static final int HANDLER_ZHUCE_SUCCESS = 2;
	private static final int HANDLER_ZHUCE_FAILURE = 3;
	private static final int HANDLER_TOKEN_FAILURE = 4;

	// 输入密码
	private EditText et_mm;
	// 输入密码
	private EditText et_mm_again;
	private String mima;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(SetMMActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(SetMMActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 注册失败
			case HANDLER_ZHUCE_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(SetMMActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// 注册成功
			case HANDLER_ZHUCE_SUCCESS:
				dissDialog();
				Toast.makeText(SetMMActivity.this, "注册成功", Toast.LENGTH_SHORT)
						.show();
				if (!ZhuCeActivity.zhuceintance.isFinishing()) {
					ZhuCeActivity.zhuceintance.finish();
				}
				if (!PhoneYZActivity.zhucephoneyzintance.isFinishing()) {
					PhoneYZActivity.zhucephoneyzintance.finish();
				}
				if (!ZhuCeActivity.zhuceintance.isFinishing()) {
					ZhuCeActivity.zhuceintance.finish();
				}
				finish();
				break;

			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(SetMMActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent intent = new Intent(SetMMActivity.this,
						LoginActivity.class);
				startActivity(intent);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setmm);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 输入密码
		et_mm = (EditText) findViewById(R.id.et_mm);
		// 再次输入密码
		et_mm_again = (EditText) findViewById(R.id.et_mm_again);
		// 完成
		TextView tv_ok = (TextView) findViewById(R.id.tv_ok);

		line_back.setOnClickListener(this);
		tv_ok.setOnClickListener(this);
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
				registHttpPost();
			}
			break;

		default:
			break;
		}
	}

	// 注册接口
	private void registHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(SetMMActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("member_mobile",
				getIntent().getStringExtra("phone"));
		String md5password = MD5(mima);
		params.addBodyParameter("member_passwd", md5password);
		params.addBodyParameter("inviter_id",
				getIntent().getStringExtra("inviter_id"));
		params.addBodyParameter("log_id", getIntent().getStringExtra("log_id"));
		params.addBodyParameter("code", getIntent().getStringExtra("code"));
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

	// 检查输入框填写状态
	private boolean canPost() {
		mima = et_mm.getText().toString().trim();
		String mima_again = et_mm_again.getText().toString().trim();
		if (TextUtils.isEmpty(mima)) {
			Toast.makeText(SetMMActivity.this, "密码不能为空", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (mima.length() < 6 || mima.length() > 20) {
			Toast.makeText(SetMMActivity.this, "密码不能少于6位,不能大于20位",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(mima_again)) {
			Toast.makeText(SetMMActivity.this, "确认密码不能为空", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (!mima.equals(mima_again)) {
			Toast.makeText(SetMMActivity.this, "两次输入密码不一致", Toast.LENGTH_SHORT)
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

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(SetMMActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(SetMMActivity.this,
				R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(SetMMActivity.this, R.style.mDialogStyle);
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
