package ccj.yun28.com.mine;

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
 * 退款退货
 * @author meihuali
 *
 */
public class TuikuanTuihuoActivity extends BaseActivity implements OnClickListener{

	private EditText et_phone;
	private EditText et_miaoshu;
	private RadioButton radio_huanhuo;
	private RadioButton radio_tuihuo;
	private RadioButton radio_tuikuan;
	private String type;
	private String order_id;
	private String num;
	// 网络异常
		protected static final int HANDLER_NET_FAILURE = 0;
		// 错误
		private static final int HANDLER_NN_FAILURE = 1;
		private static final int HANDLER_TUIKUANTUIHUO_SUCCESS = 2;
		private static final int HANDLER_GETINFO_FAILURE = 3;
		private Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				// 网络异常
				case HANDLER_NET_FAILURE:
					dissDialog();
					Toast.makeText(TuikuanTuihuoActivity.this, "当前网络不可用,请检查网络", Toast.LENGTH_SHORT)
							.show();
					break;
				// 错误
				case HANDLER_NN_FAILURE:
					dissDialog();
					Toast.makeText(TuikuanTuihuoActivity.this, "当前网络出错,请检查网络", Toast.LENGTH_SHORT).show();
					break;
				case HANDLER_GETINFO_FAILURE:
					dissDialog();
					Toast.makeText(TuikuanTuihuoActivity.this, msg.obj.toString().trim(),
							Toast.LENGTH_SHORT).show();
					break;
				// 获取信息成功
				case HANDLER_TUIKUANTUIHUO_SUCCESS:
					dissDialog();
					Intent intent = new Intent();
					setResult(101, intent);
					finish();
					break;
				}
			};
		};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tuikuantuihuo);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		radio_huanhuo = (RadioButton) findViewById(R.id.radio_huanhuo);
		radio_tuihuo = (RadioButton) findViewById(R.id.radio_tuihuo);
		radio_tuikuan = (RadioButton) findViewById(R.id.radio_tuikuan);
		et_phone = (EditText) findViewById(R.id.et_phone);
		TextView tv_shuliang = (TextView) findViewById(R.id.tv_shuliang);
		et_miaoshu = (EditText) findViewById(R.id.et_miaoshu);
		TextView tv_tijiao = (TextView) findViewById(R.id.tv_tijiao);
		
		if (getIntent() != null) {
			order_id = getIntent().getStringExtra("order_id");
			num = getIntent().getStringExtra("num");
			tv_shuliang.setText(num);
		}
		
		line_back.setOnClickListener(this);
		tv_tijiao.setOnClickListener(this);
		radio_huanhuo.setOnClickListener(this);
		radio_tuihuo.setOnClickListener(this);
		radio_tuikuan.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.tv_tijiao:
			if (canPost()) {
				showLoading();
				tuikuantuihuoHttpPost();
			}
			break;
		case R.id.radio_huanhuo:
			type = "1";
			break;
		case R.id.radio_tuihuo:
			type = "2";
			break;
		case R.id.radio_tuikuan:
			type = "3";
			break;

		default:
			break;
		}
	}

	//提交退款退货接口
	private void tuikuantuihuoHttpPost() {
		// TODO Auto-generated method stub
			HttpUtils httpUtils = new HttpUtils();
			Utils utils = new Utils();
			String[] verstring = utils.getVersionInfo(TuikuanTuihuoActivity.this);

			RequestParams params = new RequestParams();
			params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
			params.addBodyParameter("token", new DButil().gettoken(TuikuanTuihuoActivity.this));
			params.addBodyParameter("member_id",
					new DButil().getMember_id(TuikuanTuihuoActivity.this));
			params.addBodyParameter("order_id", order_id);
			params.addBodyParameter("type", type);
			params.addBodyParameter("phone", phone);
			params.addBodyParameter("num", num);
			params.addBodyParameter("remark", miaoshu);
			httpUtils.send(HttpMethod.POST, JiekouUtils.TUIKUANTUIHUO, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
							handler.sendEmptyMessage(HANDLER_NET_FAILURE);
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							// TODO Auto-generated method stub
							tuikuantuihuoListInfo(arg0.result);

						}
					});
		}

		// 解析全部订单接口数据
		protected void tuikuantuihuoListInfo(String result) {
			// TODO Auto-generated method stub
			try {
				JSONObject object = new JSONObject(result);
				String status = object.getString("code");
				if ("200".equals(status)) {
					handler.sendEmptyMessage(HANDLER_TUIKUANTUIHUO_SUCCESS);
				} else {
					String message = object.getString("message");
					handler.sendMessage(handler.obtainMessage(HANDLER_GETINFO_FAILURE, message));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				handler.sendEmptyMessage(HANDLER_NN_FAILURE);
			}
		}

	private boolean canPost() {
		phone = et_phone.getText().toString().trim();
		miaoshu = et_miaoshu.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(TuikuanTuihuoActivity.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}else if(TextUtils.isEmpty(miaoshu)){
			Toast.makeText(TuikuanTuihuoActivity.this, "问题描述不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}else if(phone.length() != 11){
			Toast.makeText(TuikuanTuihuoActivity.this, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
			return false;
		}else if(radio_huanhuo.isChecked() == false && radio_tuihuo.isChecked() == false && radio_tuikuan.isChecked()==false){
			Toast.makeText(TuikuanTuihuoActivity.this, "请选择类型", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	// 加载中动画
		private Dialog loadingDialog;
	private String phone;
	private String miaoshu;

		// 加载动画
		private void showLoading() {
			View view = LayoutInflater.from(TuikuanTuihuoActivity.this).inflate(
					R.layout.loading, null);
			ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
			Animation animation = AnimationUtils.loadAnimation(TuikuanTuihuoActivity.this,
					R.anim.loading_anim);
			image.startAnimation(animation);
			loadingDialog = new Dialog(TuikuanTuihuoActivity.this, R.style.mDialogStyle);
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
