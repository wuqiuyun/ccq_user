package ccj.yun28.com.mine;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.IDUtils;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 新版未实名认证
 * 
 * @author meihuali
 * 
 */
public class NewWeiShiMingRenZhengActivity extends BaseActivity implements
		OnClickListener {

	private String rzid;
	private EditText et_zhengjian_num;
	private EditText et_true_name;
	
	public static NewWeiShiMingRenZhengActivity newWeiShiMingRenZheng;
	
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 提交成功
	private static final int HANDLER_TIJIAO_SUCCESS = 3;
	// token失效
	private static final int HANDLER_TOKEN_FAILURE = 4;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(NewWeiShiMingRenZhengActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(NewWeiShiMingRenZhengActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(NewWeiShiMingRenZhengActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 提交成功
			case HANDLER_TIJIAO_SUCCESS:
				dissDialog();
				Intent smpicintent = new Intent(NewWeiShiMingRenZhengActivity.this,
						NewWeiShiMingRenZhengNextActivity.class);
				smpicintent.putExtra("rzid", rzid);
				startActivityForResult(smpicintent, 100); 
				
				break;

				// token失效
				case HANDLER_TOKEN_FAILURE:
					dissDialog();
					if (msg.obj != null) {
						Toast.makeText(NewWeiShiMingRenZhengActivity.this,
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();
					}
					Intent intent = new Intent(NewWeiShiMingRenZhengActivity.this,
							LoginActivity.class);
					intent.putExtra("type", "qt");
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
		setContentView(R.layout.activity_newweishimingrenzheng);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		et_zhengjian_num = (EditText) findViewById(R.id.et_zhengjian_num);
		et_true_name = (EditText) findViewById(R.id.et_true_name);
		TextView tv_tijiao = (TextView) findViewById(R.id.tv_tijiao);
		
		newWeiShiMingRenZheng = this;
		
		if (getIntent() != null) {
			String title = getIntent().getStringExtra("title");
			tv_title.setText(title);
		}
		
		line_back.setOnClickListener(this);
		tv_tijiao.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.tv_tijiao:
			if (canPost()) {
				showLoading();
				tijiaoHttpPost();
			}
			break;
		default:
			break;
		}
	}

	

	// 提交实名认证接口
	private void tijiaoHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(NewWeiShiMingRenZhengActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token",
				new DButil().gettoken(NewWeiShiMingRenZhengActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(NewWeiShiMingRenZhengActivity.this));
		params.addBodyParameter("true_name", et_true_name.getText().toString().trim());
		params.addBodyParameter("number", et_zhengjian_num.getText().toString().trim());
		httpUtils.send(HttpMethod.POST, JiekouUtils.TIJIAOSHIMINGRENZHENG,
				params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e("ee", "失败:" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						tijiaoListInfo(arg0.result);
					}
				});
	}

	protected void tijiaoListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				rzid = object.getString("id");
				handler.sendEmptyMessage(HANDLER_TIJIAO_SUCCESS);
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
		String zhengjian_num = et_zhengjian_num.getText().toString().trim();
		String true_name = et_true_name.getText().toString().trim();
		if (TextUtils.isEmpty(zhengjian_num)) {
			Toast.makeText(NewWeiShiMingRenZhengActivity.this, "身份证号码不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (!IDUtils.isCorrectID(zhengjian_num)) {
			Toast.makeText(NewWeiShiMingRenZhengActivity.this, "身份证号码格式不正确",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(true_name)) {
			Toast.makeText(NewWeiShiMingRenZhengActivity.this, "真实姓名不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(NewWeiShiMingRenZhengActivity.this)
				.inflate(R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				NewWeiShiMingRenZhengActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(NewWeiShiMingRenZhengActivity.this,
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
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Intent reintent = new Intent();
		setResult(601, reintent);
		finish();
	}
}
