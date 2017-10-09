package ccj.yun28.com.gwc;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
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
 * 购物车-发票信息
 * 
 * @author meihuali
 * 
 */
public class FaPiaoInfoActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener {

	// 保存发票dialog
	private Dialog savefapiaoDialog;
	private EditText et_fptt;
	private String content = "明细";

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取订单详情信息成功
	private static final int HANDLER_DINDANDETAILINFO_SUCCESS = 3;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(FaPiaoInfoActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(FaPiaoInfoActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(FaPiaoInfoActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
					
				}
				break;
			// 发票添加成功
			case HANDLER_DINDANDETAILINFO_SUCCESS:
				dissDialog();
				Intent intent = new Intent();
				intent.putExtra("fapiao", et_fptt.getText().toString().trim()
						+ content);
				intent.putExtra("fapiao_id", msg.obj.toString().trim());
				setResult(500, intent);
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
		setContentView(R.layout.activity_fapiaoinfo);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);

		RadioGroup tabs_rg = (RadioGroup) findViewById(R.id.tabs_rg);
		et_fptt = (EditText) findViewById(R.id.et_fptt);

		line_back.setOnClickListener(this);
		tabs_rg.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			View view = LayoutInflater.from(FaPiaoInfoActivity.this).inflate(
					R.layout.dialog_savefapiao, null);
			TextView tv_dialog_cancel = (TextView) view
					.findViewById(R.id.tv_dialog_cancel);
			TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
			tv_dialog_cancel.setOnClickListener(this);
			tv_ok.setOnClickListener(this);
			savefapiaoDialog = new Dialog(FaPiaoInfoActivity.this,
					R.style.mDialogStyle);
			savefapiaoDialog.setContentView(view);
			savefapiaoDialog.setCanceledOnTouchOutside(false);
			savefapiaoDialog.show();
			break;
		case R.id.tv_dialog_cancel:
			savefapiaoDialog.dismiss();
			onBackPressed();
			break;
		case R.id.tv_ok:
			if (canPost()) {
				showLoading();
				addfapiaoHttpPost();
			}
			break;

		default:
			break;
		}
	}

	private boolean canPost() {
		// TODO Auto-generated method stub
		String fptitle = et_fptt.getText().toString().trim();
		if ("".equals(fptitle) || fptitle == null) {
			Toast.makeText(FaPiaoInfoActivity.this, "发票抬头不能为空",
					Toast.LENGTH_SHORT).show();
			savefapiaoDialog.dismiss();
			return false;
		}
		return true;
	}

	// 添加发票接口
	private void addfapiaoHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(FaPiaoInfoActivity.this);
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(FaPiaoInfoActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(FaPiaoInfoActivity.this));
		params.addBodyParameter("inv_title", et_fptt.getText().toString()
				.trim());
		params.addBodyParameter("inv_content", content);
		httpUtils.send(HttpMethod.POST, JiekouUtils.ADDFAPIAO, params,
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
						addfapiaoListInfo(arg0.result);
					}
				});
	}

	protected void addfapiaoListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				String invoice = object.getString("invoice");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_DINDANDETAILINFO_SUCCESS, invoice));
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

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		RadioButton radioButton = (RadioButton) findViewById(group
				.getCheckedRadioButtonId());
		content = radioButton.getText().toString();
	}

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(FaPiaoInfoActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				FaPiaoInfoActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(FaPiaoInfoActivity.this,
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
