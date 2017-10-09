package ccj.yun28.com.mine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.LoginActivity;
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
 * 我的-修改昵称
 * 
 * @author meihuali
 * 
 */
public class NichengActivity extends BaseActivity implements OnClickListener {

	private EditText et_nicheng;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 修改昵称成功
	private static final int HANDLER_EDITNICHENG_SUCCESS = 3;
	private static final int HANDLER_TOKEN_FAILURE = 5;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(NichengActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(NichengActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(NichengActivity.this, msg.obj.toString().trim(),
						Toast.LENGTH_SHORT).show();
				break;
			// 修改昵称成功
			case HANDLER_EDITNICHENG_SUCCESS:
				dissDialog();
				Intent intent = new Intent();
				intent.putExtra("nicheng", et_nicheng.getText().toString()
						.trim());
				setResult(301, intent);
				finish();
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(NichengActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent lointent = new Intent(NichengActivity.this,
						LoginActivity.class);
				lointent.putExtra("type", "qt");
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
		setContentView(R.layout.acticity_nicheng);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_ok = (LinearLayout) findViewById(R.id.line_ok);
		et_nicheng = (EditText) findViewById(R.id.et_nicheng);

		if (getIntent() != null) {
			String nicheng = getIntent().getStringExtra("nicheng");
			et_nicheng.setText(nicheng);
		}
		line_back.setOnClickListener(this);
		line_ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_ok:
			if (canPost()) {
				showLoading();
				editNiChengHttpPost();
			}
			break;

		default:
			break;
		}
	}

	// 修改昵称接口
	private void editNiChengHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(NichengActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token",
				new DButil().gettoken(NichengActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(NichengActivity.this));
		params.addBodyParameter("member_nickname", et_nicheng.getText()
				.toString().trim());
		httpUtils.send(HttpMethod.POST, JiekouUtils.EDITMYINFO, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						editNiChengInfoListInfo(arg0.result);

					}
				});
	}

	protected void editNiChengInfoListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {

				handler.sendEmptyMessage(HANDLER_EDITNICHENG_SUCCESS);
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
		String nicheng = et_nicheng.getText().toString().trim();
		if (TextUtils.isEmpty(nicheng)) {
			Toast.makeText(NichengActivity.this, "昵称不能为空哦", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (nicheng.length() > 20 || nicheng.length() < 4) {
			Toast.makeText(NichengActivity.this, "昵称只能是4-20个字符哦",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (!fuheguize(nicheng)) {
			Toast.makeText(NichengActivity.this, "昵称格式不对哦", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		return true;
	}

	private boolean fuheguize(String nicheng) {
		String regEx = "^[A-Za-z0-9_-]+$";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(nicheng);
		boolean b = matcher.matches();
		return b;
	}

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(NichengActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				NichengActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(NichengActivity.this, R.style.mDialogStyle);
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
