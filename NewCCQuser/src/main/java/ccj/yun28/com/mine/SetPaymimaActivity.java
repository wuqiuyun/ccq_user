package ccj.yun28.com.mine;

import java.security.MessageDigest;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
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
import ccj.yun28.com.R;
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
 * 设置支付密码
 * 
 * @author meihuali
 * 
 */
public class SetPaymimaActivity extends BaseActivity implements OnClickListener {
	private EditText et_newmm;
	private EditText et_again_newmm;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取我的信息成功
	private static final int HANDLER_SETLOGINMIMA_SUCCESS = 3;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(SetPaymimaActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(SetPaymimaActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(SetPaymimaActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取我的信息成功
			case HANDLER_SETLOGINMIMA_SUCCESS:
				dissDialog();
				if (!XiuGaiZhiFuMiMaActivity.xiugaiintance.isFinishing()) {
					XiuGaiZhiFuMiMaActivity.xiugaiintance.finish();
				}
				finish();
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
		setContentView(R.layout.activity_setpaymima);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		et_newmm = (EditText) findViewById(R.id.et_newmm);
		et_again_newmm = (EditText) findViewById(R.id.et_again_newmm);
		TextView tv_ok = (TextView) findViewById(R.id.tv_ok);
		
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(SetPaymimaActivity.this);

		if (getIntent() != null) {
			type = getIntent().getStringExtra("type");
		}

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
				if ("3".equals(type)) {
					// 3通过登录密码更新
					member_pwd = getIntent().getStringExtra("member_pwd");
					setZhifuMiMaHttpPost();
				} else if ("2".equals(type)) {
					// 2通过原支付密码更新
					member_old_paypwd = getIntent().getStringExtra(
							"member_old_paypwd");
					setyuanZhifuMiMaHttpPost();
				} else if ("1".equals(type)) {
					// 1 通过手机更新
					log_id = getIntent().getStringExtra("log_id");
					code = getIntent().getStringExtra("code");
					setPhoneZhifuMiMaHttpPost();
				}
			}
			break;

		default:
			break;
		}
	}

	// 通过手机更新支付密码
	private void setPhoneZhifuMiMaHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("member_id",
				new DButil().getMember_id(SetPaymimaActivity.this));
		params.addBodyParameter("member_paypwd", MD5(mima));
		params.addBodyParameter("log_id", log_id);
		params.addBodyParameter("code", code);
		params.addBodyParameter("type", type);
		httpUtils.send(HttpMethod.POST, JiekouUtils.SHEZHIPAYMIMA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						setzhifuMiMaListInfo(arg0.result);
					}
				});
	}

	// 原支付密码更新支付密码
	private void setyuanZhifuMiMaHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("member_id",
				new DButil().getMember_id(SetPaymimaActivity.this));
		params.addBodyParameter("member_paypwd", MD5(mima));
		params.addBodyParameter("member_old_paypwd", MD5(member_old_paypwd));
		params.addBodyParameter("type", type);
		httpUtils.send(HttpMethod.POST, JiekouUtils.SHEZHIPAYMIMA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						setzhifuMiMaListInfo(arg0.result);
					}
				});
	}

	// 登录密码更新支付密码
	private void setZhifuMiMaHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("member_id",
				new DButil().getMember_id(SetPaymimaActivity.this));
		params.addBodyParameter("member_paypwd", MD5(mima));
		params.addBodyParameter("member_pwd", MD5(member_pwd));
		params.addBodyParameter("type", type);
		httpUtils.send(HttpMethod.POST, JiekouUtils.SHEZHIPAYMIMA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						setzhifuMiMaListInfo(arg0.result);
					}
				});
	}

	protected void setzhifuMiMaListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			String message = object.getString("message");
			if ("200".equals(code)) {
				handler.sendEmptyMessage(HANDLER_SETLOGINMIMA_SUCCESS);
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

	private boolean canPost() {
		mima = et_newmm.getText().toString().trim();
		String querenmima = et_again_newmm.getText().toString().trim();
		if (TextUtils.isEmpty(mima)) {
			Toast.makeText(SetPaymimaActivity.this, "新密码不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(querenmima)) {
			Toast.makeText(SetPaymimaActivity.this, "再次输入密码不能为空",
					Toast.LENGTH_SHORT).show();
			return false;

		} else if (mima.length() < 6) {
			Toast.makeText(SetPaymimaActivity.this, "新密码不能少与六位",
					Toast.LENGTH_SHORT).show();
			return false;

		} else if (!mima.equals(querenmima)) {
			Toast.makeText(SetPaymimaActivity.this, "两次密码不一致",
					Toast.LENGTH_SHORT).show();
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
	private String mima;
	private String type;
	private String member_pwd;
	private String member_old_paypwd;
	private String log_id;
	private String code;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(SetPaymimaActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				SetPaymimaActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(SetPaymimaActivity.this,
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
