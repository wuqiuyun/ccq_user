package ccj.yun28.com.mine;

import java.security.MessageDigest;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.ForgetMMActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.RZPhoneActivity;
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
 * 手机关联-手机认证
 * 
 * @author meihuali
 * 
 */
public class PhoneRenZhengActivity extends BaseActivity implements OnClickListener {

	// 账户名输入框
	private EditText et_zhanghuming;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取我的信息成功
	private static final int HANDLER_CHECKMIMA_SUCCESS = 3;
	

	public static PhoneRenZhengActivity intance;


	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(PhoneRenZhengActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(PhoneRenZhengActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(PhoneRenZhengActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取我的信息成功
			case HANDLER_CHECKMIMA_SUCCESS:
				dissDialog();
				Intent intent = new Intent(PhoneRenZhengActivity.this,
						RZPhoneActivity.class);
				intent.putExtra("title", "手机验证");
				intent.putExtra("type", "phone");
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
		setContentView(R.layout.activity_phonerenzheng);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 账户名输入框
		et_zhanghuming = (EditText) findViewById(R.id.et_zhanghuming);
		// 下一步
		TextView tv_next = (TextView) findViewById(R.id.tv_next);
		TextView tv_forgrtpwd = (TextView) findViewById(R.id.tv_forgrtpwd);
		
		intance = this;
		
		line_back.setOnClickListener(this);
		tv_next.setOnClickListener(this);
		tv_forgrtpwd.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.tv_next:
			if (canPost()) {
				showLoading();
				checkLoginPwdHttpPost();
			}
			break;
		case R.id.tv_forgrtpwd:
			intent = new Intent(PhoneRenZhengActivity.this,
					ForgetMMActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	// 检查登录密码
	// 检查密码是否正确
	private void checkLoginPwdHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(PhoneRenZhengActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(PhoneRenZhengActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(PhoneRenZhengActivity.this));
		params.addBodyParameter("member_passwd", MD5(zhanghuming));
		httpUtils.send(HttpMethod.POST, JiekouUtils.CHECKMIMA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						checkMiMaListInfo(arg0.result);
					}
				});
	}

	protected void checkMiMaListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			String message = object.getString("message");
			if ("200".equals(code)) {
				handler.sendEmptyMessage(HANDLER_CHECKMIMA_SUCCESS);
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

	// 检查输入框填写状态
	private boolean canPost() {
		zhanghuming = et_zhanghuming.getText().toString().trim();
		if (TextUtils.isEmpty(zhanghuming)) {
			Toast.makeText(PhoneRenZhengActivity.this, "账户名不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (zhanghuming.length() < 6) {
			Toast.makeText(PhoneRenZhengActivity.this, "密码不能少于6位",
					Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	// 加载中动画
	private Dialog loadingDialog;
	private String zhanghuming;

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

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(PhoneRenZhengActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				PhoneRenZhengActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(PhoneRenZhengActivity.this,
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
