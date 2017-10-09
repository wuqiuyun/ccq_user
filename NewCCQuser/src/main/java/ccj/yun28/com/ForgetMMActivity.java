package ccj.yun28.com;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
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
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.umeng.analytics.MobclickAgent;

/**
 * 忘记密码页
 * 
 * @author meihuali
 * 
 */
public class ForgetMMActivity extends BaseActivity implements OnClickListener {

	// 账户名输入框
	private EditText et_zhanghuming;

	public static ForgetMMActivity forgetintance;

	private String zhanghuming;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 错误
	private static final int HANDLER_NN_FAILURE = 1;
	// 登录成功
	private static final int HANDLER_checkZhanghuming_SUCCESS = 2;
	// 登录失败
	private static final int HANDLER_GETINFO_FAILURE = 3;

	private static final int HANDLER_TOKEN_FAILURE = 4;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(ForgetMMActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(ForgetMMActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 登录成功
			case HANDLER_checkZhanghuming_SUCCESS:
				dissDialog();
				Intent intent = new Intent(ForgetMMActivity.this,
						RZPhoneActivity.class);
				intent.putExtra("title", "身份验证");
				intent.putExtra("type", "wjmm");
				startActivity(intent);
				break;
			// 登录失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(ForgetMMActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(ForgetMMActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent lointent = new Intent(ForgetMMActivity.this,
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
		setContentView(R.layout.activity_forgetmm);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 账户名输入框
		et_zhanghuming = (EditText) findViewById(R.id.et_zhanghuming);
		// 下一步
		TextView tv_next = (TextView) findViewById(R.id.tv_next);

		forgetintance = this;

		line_back.setOnClickListener(this);
		tv_next.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.tv_next:
			if (canPost()) {
				showLoading();
				checkZhanghumingHttpPost();
			}
			break;

		default:
			break;
		}
	}

	// 检查用户名是否可以忘记密码
	private void checkZhanghumingHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(ForgetMMActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		// 用户名
		params.addBodyParameter("member_name", zhanghuming);
		httpUtils.send(HttpMethod.POST,
				JiekouUtils.CHECK_MEMBERNAMEISFORGETPWD, params,
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
						checkZhanghumingInfo(arg0.result);
					}
				});
	}

	protected void checkZhanghumingInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				handler.sendEmptyMessage(HANDLER_checkZhanghuming_SUCCESS);
			} else if ("700".equals(status)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TOKEN_FAILURE, message));
			} else {
				String msg = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETINFO_FAILURE, msg));
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
			Toast.makeText(ForgetMMActivity.this, "账户名不能为空", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		return true;
	}

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(ForgetMMActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				ForgetMMActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(ForgetMMActivity.this, R.style.mDialogStyle);
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
