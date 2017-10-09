package ccj.yun28.com.mine;

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
 * 我要开店
 * @author meihuali
 *
 */
public class OpenStoreActivtiy extends BaseActivity implements OnClickListener{
	private EditText et_liuyan;
	private String liuyan;

	protected static final int HANDLER_NET_FAILURE = 0;
	private static final int HANDLER_TIJIAOFANKUI_SUCCESS = 1;
	private static final int HANDLER_NN_FAILURE = 2;
	private static final int HANDLER_GETINFO_FAILURE = 3;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(OpenStoreActivtiy.this, "当前网络不可用,请检查网络", Toast.LENGTH_SHORT)
						.show();
				break;
			case HANDLER_TIJIAOFANKUI_SUCCESS:
				dissDialog();
				finish();
				break;
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(OpenStoreActivtiy.this, "当前网络出错,请检查网络", Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(OpenStoreActivtiy.this, msg.obj.toString().trim(), Toast.LENGTH_SHORT)
					.show();
				}
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
		setContentView(R.layout.activity_openstore);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_tijiao = (LinearLayout) findViewById(R.id.line_tijiao);
		et_liuyan = (EditText) findViewById(R.id.et_liuyan);

		line_back.setOnClickListener(this);
		line_tijiao.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_tijiao:
			if (canPost()) {
				showLoading();
				tijiaoHttpPost();
			}
			break;

		default:
			break;
		}
	}

	//提交反馈信息接口
	private void tijiaoHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(OpenStoreActivtiy.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token", new DButil().gettoken(OpenStoreActivtiy.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(OpenStoreActivtiy.this));
		params.addBodyParameter("phone", "");
		params.addBodyParameter("content", liuyan);
		params.addBodyParameter("type", "6");
		httpUtils.send(HttpMethod.POST, JiekouUtils.FANKUIXINXI, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						tijiaoJsonInfo(arg0.result);
					}
				});
	}

	protected void tijiaoJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				handler.sendEmptyMessage(HANDLER_TIJIAOFANKUI_SUCCESS);
			}else{
				
				String message = object.getString("message");
				handler.sendEmptyMessage(HANDLER_GETINFO_FAILURE);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private boolean canPost() {
		liuyan = et_liuyan.getText().toString().trim();
		if (TextUtils.isEmpty(liuyan)) {
			Toast.makeText(OpenStoreActivtiy.this, "留言内容不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	// 加载中动画
	private Dialog loadingDialog;
	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(OpenStoreActivtiy.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				OpenStoreActivtiy.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(OpenStoreActivtiy.this,
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
