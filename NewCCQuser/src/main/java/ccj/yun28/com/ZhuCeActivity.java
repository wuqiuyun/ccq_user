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
import android.widget.CheckBox;
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
 * 注册页
 * 
 * @author meihuali
 * 
 */
public class ZhuCeActivity extends BaseActivity implements OnClickListener {

	protected static final int HANDLER_NET_FAILURE = 0;
	private static final int HANDLER_SHIFOUZHUCE_SUCCESS = 1;
	private static final int HANDLER_SHIFOUZHUCE_FAILURE = 2;
	private static final int HANDLER_NN_FAILURE = 3;
	private static final int HANDLER_TOKEN_FAILURE = 4;
	// 手机输入框
	private EditText et_phone;// 输入密码
	private EditText et_mm;
	// 输入密码
	private EditText et_mm_again;
	// 推荐人输入框
	private EditText et_tjr;
	// 是否同意协议
	private CheckBox cb_xy;
	// 用户名
	private String phone;
	public static ZhuCeActivity zhuceintance;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(ZhuCeActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_SHIFOUZHUCE_SUCCESS:
				dissDialog();
				Intent intent = new Intent(ZhuCeActivity.this,
						PhoneYZActivity.class);
				intent.putExtra("phone", phone);
				intent.putExtra("tjr", et_tjr.getText().toString().trim());
				intent.putExtra("password", et_mm.getText().toString().trim());
				startActivity(intent);
				break;
			case HANDLER_SHIFOUZHUCE_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(ZhuCeActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(ZhuCeActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;

			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(ZhuCeActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent lointent = new Intent(ZhuCeActivity.this,
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
		setContentView(R.layout.activity_zhuce);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 手机输入框
		et_phone = (EditText) findViewById(R.id.et_phone);
		// 输入密码
		et_mm = (EditText) findViewById(R.id.et_mm);
		// 再次输入密码
		et_mm_again = (EditText) findViewById(R.id.et_mm_again);
		// 推荐人输入框
		et_tjr = (EditText) findViewById(R.id.et_tjr);
		// 是否同意协议
		cb_xy = (CheckBox) findViewById(R.id.cb_xy);
		// 下一步
		TextView tv_next = (TextView) findViewById(R.id.tv_next);

		zhuceintance = this;
		Intent intent = getIntent();
		if (getIntent() != null) {
			String tjr = getIntent().getStringExtra("tjrgoods_id");
			if (!TextUtils.isEmpty(tjr)) {
				et_tjr.setText(tjr);
				et_tjr.setEnabled(false);
			}
		}
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
				shifouZhuCeHttpPost();
			}
			break;

		default:
			break;
		}
	}

	// 判断用户名是否注册过接口
	private void shifouZhuCeHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(ZhuCeActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("member_name", phone);
		httpUtils.send(HttpMethod.POST, JiekouUtils.CHECK_MEMBERNAME, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						shifouZhuCeListInfo(arg0.result);
					}
				});
	}

	// 解析接口返回数据
	protected void shifouZhuCeListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				handler.sendEmptyMessage(HANDLER_SHIFOUZHUCE_SUCCESS);
			} else if ("700".equals(status)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TOKEN_FAILURE, message));
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_SHIFOUZHUCE_FAILURE, message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 检查输入框填写状态
	private boolean canPost() {
		phone = et_phone.getText().toString().trim();
		String mima = et_mm.getText().toString().trim();
		String mima_again = et_mm_again.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(ZhuCeActivity.this, "手机号码不能为空", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (phone.length() != 11) {
			Toast.makeText(ZhuCeActivity.this, "手机号码格式不正确", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (TextUtils.isEmpty(mima)) {
			Toast.makeText(ZhuCeActivity.this, "登录密码不能为空", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (mima.length() < 6 || mima.length() > 12) {
			Toast.makeText(ZhuCeActivity.this, "登录密码不能少于6位,不能大于12位",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(mima_again)) {
			Toast.makeText(ZhuCeActivity.this, "确认密码不能为空", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (!mima.equals(mima_again)) {
			Toast.makeText(ZhuCeActivity.this, "两次输入密码不一致", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (cb_xy.isChecked() != true) {
			Toast.makeText(ZhuCeActivity.this, "用户协议未同意", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(ZhuCeActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(ZhuCeActivity.this,
				R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(ZhuCeActivity.this, R.style.mDialogStyle);
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
