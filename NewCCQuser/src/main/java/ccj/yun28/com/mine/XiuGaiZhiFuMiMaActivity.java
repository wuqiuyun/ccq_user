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
 * 修改支付密码
 * @author meihuali
 *
 */
public class XiuGaiZhiFuMiMaActivity extends BaseActivity implements OnClickListener{
	private EditText et_mima;
	private String mima;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取我的信息成功
	private static final int HANDLER_CHECKMIMA_SUCCESS = 3;
	

	public static XiuGaiZhiFuMiMaActivity xiugaiintance;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(XiuGaiZhiFuMiMaActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(XiuGaiZhiFuMiMaActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(XiuGaiZhiFuMiMaActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取我的信息成功
			case HANDLER_CHECKMIMA_SUCCESS:
				dissDialog();
				Intent intent = new Intent(XiuGaiZhiFuMiMaActivity.this,
						SetPaymimaActivity.class);
				intent.putExtra("type", "2");
				intent.putExtra("member_old_paypwd", mima);
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
		setContentView(R.layout.activity_xiugaizhifumima);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		et_mima = (EditText) findViewById(R.id.et_mima);
		TextView tv_next = (TextView) findViewById(R.id.tv_next);
//		TextView tv_forgetmima = (TextView) findViewById(R.id.tv_forgetmima);

		xiugaiintance = this;
		
		line_back.setOnClickListener(this);
		tv_next.setOnClickListener(this);
//		tv_forgetmima.setOnClickListener(this);
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
				checkpayMiMaHttpPost();
			}
			break;
		case R.id.tv_forgetmima:
			Intent intent = new Intent(XiuGaiZhiFuMiMaActivity.this,
					ForgetMMActivity.class);
			startActivity(intent);

			break;

		default:
			break;
		}
	}

	// 检查密码是否正确
	private void checkpayMiMaHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(XiuGaiZhiFuMiMaActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(XiuGaiZhiFuMiMaActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(XiuGaiZhiFuMiMaActivity.this));
		params.addBodyParameter("member_paypwd", MD5(mima));
		httpUtils.send(HttpMethod.POST, JiekouUtils.CHECKPAYMIMA, params,
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

	private boolean canPost() {
		mima = et_mima.getText().toString().trim();
		if (TextUtils.isEmpty(mima)) {
			Toast.makeText(XiuGaiZhiFuMiMaActivity.this, "原密码不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (mima.length() < 6) {
			Toast.makeText(XiuGaiZhiFuMiMaActivity.this, "原密码不能少于6位",
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

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(XiuGaiZhiFuMiMaActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				XiuGaiZhiFuMiMaActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(XiuGaiZhiFuMiMaActivity.this,
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
