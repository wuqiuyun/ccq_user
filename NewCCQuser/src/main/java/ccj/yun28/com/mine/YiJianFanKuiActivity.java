package ccj.yun28.com.mine;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
 * 意见反馈
 * 
 * @author meihuali
 * 
 */
public class YiJianFanKuiActivity extends BaseActivity implements OnClickListener, TextWatcher {

	protected static final int HANDLER_NET_FAILURE = 0;
	private static final int HANDLER_TIJIAOFANKUI_SUCCESS = 1;
	private static final int HANDLER_NN_FAILURE = 2;
	private static final int HANDLER_GETINFO_FAILURE = 3;
	private EditText et_fankui;
	private TextView tv_zishu;
	private EditText et_phone;
	private RadioButton radio_canzuo;
	private RadioButton radio_gongneng;
	private RadioButton radio_yemiantucao;
	private RadioButton radio_yemianxuqiu;
	private RadioButton radio_qita;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(YiJianFanKuiActivity.this, "当前网络不可用,请检查网络", Toast.LENGTH_SHORT)
						.show();
				break;
			case HANDLER_TIJIAOFANKUI_SUCCESS:
				dissDialog();
				finish();
				break;
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(YiJianFanKuiActivity.this, "当前网络出错,请检查网络", Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(YiJianFanKuiActivity.this, msg.obj.toString().trim(), Toast.LENGTH_SHORT)
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
		setContentView(R.layout.activity_yijianfankui);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_tijiao = (LinearLayout) findViewById(R.id.line_tijiao);
		radio_canzuo = (RadioButton) findViewById(R.id.radio_canzuo);
		radio_gongneng = (RadioButton) findViewById(R.id.radio_gongneng);
		radio_yemiantucao = (RadioButton) findViewById(R.id.radio_yemiantucao);
		radio_yemianxuqiu = (RadioButton) findViewById(R.id.radio_yemianxuqiu);
		radio_qita = (RadioButton) findViewById(R.id.radio_qita);
		et_fankui = (EditText) findViewById(R.id.et_fankui);
		tv_zishu = (TextView) findViewById(R.id.tv_zishu);
		et_phone = (EditText) findViewById(R.id.et_phone);

		line_back.setOnClickListener(this);
		line_tijiao.setOnClickListener(this);
		radio_canzuo.setOnClickListener(this);
		radio_gongneng.setOnClickListener(this);
		radio_yemiantucao.setOnClickListener(this);
		radio_yemianxuqiu.setOnClickListener(this);
		radio_qita.setOnClickListener(this);
		et_fankui.addTextChangedListener(this);
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
		case R.id.radio_canzuo:
			radio_canzuo.setChecked(true);
			radio_gongneng.setChecked(false);
			radio_yemiantucao.setChecked(false);
			radio_yemianxuqiu.setChecked(false);
			radio_qita.setChecked(false);
			break;
		case R.id.radio_gongneng:
			radio_canzuo.setChecked(false);
			radio_gongneng.setChecked(true);
			radio_yemiantucao.setChecked(false);
			radio_yemianxuqiu.setChecked(false);
			radio_qita.setChecked(false);
			break;
		case R.id.radio_yemiantucao:
			radio_canzuo.setChecked(false);
			radio_gongneng.setChecked(false);
			radio_yemiantucao.setChecked(true);
			radio_yemianxuqiu.setChecked(false);
			radio_qita.setChecked(false);
			break;
		case R.id.radio_yemianxuqiu:
			radio_canzuo.setChecked(false);
			radio_gongneng.setChecked(false);
			radio_yemiantucao.setChecked(false);
			radio_yemianxuqiu.setChecked(true);
			radio_qita.setChecked(false);
			break;
		case R.id.radio_qita:
			radio_canzuo.setChecked(false);
			radio_gongneng.setChecked(false);
			radio_yemiantucao.setChecked(false);
			radio_yemianxuqiu.setChecked(false);
			radio_qita.setChecked(true);
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
		String[] verstring = utils.getVersionInfo(YiJianFanKuiActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token", new DButil().gettoken(YiJianFanKuiActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(YiJianFanKuiActivity.this));
		params.addBodyParameter("phone", phone);
		params.addBodyParameter("content", fankui);
		params.addBodyParameter("type", "2");
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
		fankui = et_fankui.getText().toString().trim();
		phone = et_phone.getText().toString().trim();
		if (TextUtils.isEmpty(fankui)) {
			Toast.makeText(YiJianFanKuiActivity.this, "反馈内容不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (radio_canzuo.isChecked() == false
				&& radio_gongneng.isChecked() == false
				&& radio_yemiantucao.isChecked() == false
				&& radio_yemianxuqiu.isChecked() == false
				&& radio_qita.isChecked() == false) {
			Toast.makeText(YiJianFanKuiActivity.this, "反馈类型不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	// 加载中动画
	private Dialog loadingDialog;
	private String fankui;
	private String phone = "";

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(YiJianFanKuiActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				YiJianFanKuiActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(YiJianFanKuiActivity.this,
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
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		String fankui = et_fankui.getText().toString().trim();
		if (101 > fankui.length() && fankui.length() > 0) {
			tv_zishu.setText(fankui.length() + "/100");
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
}
