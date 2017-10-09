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
import android.view.View.OnFocusChangeListener;
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
import ccj.yun28.com.ZhuCeActivity;
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
 * 新版添加银行卡
 * 
 * @author meihuali
 * 
 */
public class NewAddBankCardActivity extends BaseActivity implements
		OnClickListener, OnFocusChangeListener {

	private EditText et_true_name;
	private EditText et_card_num;
	private TextView tv_khbank;
	private EditText et_sfz_num;
	private EditText et_bank_phone;


	private String bankcode;
	private String bankname;
	private String bank_card_id;
	

	//是否调用获取开户银行接口
	private boolean sfgetkaihu = false;
	//是否提交银行信息接口
	private boolean sftjBankInfo = false;
	
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 银行卡信息提交成功
	private static final int HANDLER_CARDINFO_SUCCESS = 3;
	// token失效
	private static final int HANDLER_TOKEN_FAILURE = 5;
	private static final int HANDLER_GETKAIHUBANK_SUCCESS = 6;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(NewAddBankCardActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(NewAddBankCardActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(NewAddBankCardActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 提交银行卡信息成功
			case HANDLER_CARDINFO_SUCCESS:
				Intent intent = new Intent(NewAddBankCardActivity.this,
						AddBankPicActivity.class);
				intent.putExtra("bank_card_id", bank_card_id);
				startActivityForResult(intent, 100);
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(NewAddBankCardActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent lointent = new Intent(NewAddBankCardActivity.this,
						LoginActivity.class);
				lointent.putExtra("type", "qt");
				startActivity(lointent);
				break;
			case HANDLER_GETKAIHUBANK_SUCCESS:
				if (sftjBankInfo) {
					showLoading();
					tijiaoBankCardHttpPost();
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
		setContentView(R.layout.activity_newaddbankcard);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		et_card_num = (EditText) findViewById(R.id.et_card_num);
		et_true_name = (EditText) findViewById(R.id.et_true_name);
		tv_khbank = (TextView) findViewById(R.id.tv_khbank);
		et_sfz_num = (EditText) findViewById(R.id.et_sfz_num);
		et_bank_phone = (EditText) findViewById(R.id.et_bank_phone);

		TextView tv_tijiao = (TextView) findViewById(R.id.tv_tijiao);

		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(NewAddBankCardActivity.this);
		
		line_back.setOnClickListener(this);
		tv_tijiao.setOnClickListener(this);
		et_card_num.setOnFocusChangeListener(this);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		String card_num = et_card_num.getText().toString().trim();
		if (hasFocus) {
			// 此处为得到焦点时的处理内容
			sfgetkaihu = false;
		} else {
			// 此处为失去焦点时的处理内容
			if (!TextUtils.isEmpty(card_num)) {
				if (card_num.length() > 15 && 20 > card_num.length()) {
					getKaihuBankHttpPost(card_num);
				} else {
					Toast.makeText(NewAddBankCardActivity.this, "银行卡号格式错误",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	// 卡号获得开户银行名称
	private void getKaihuBankHttpPost(String card_num2) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(NewAddBankCardActivity.this));
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("acct_pan", card_num2);
		httpUtils.send(HttpMethod.POST, JiekouUtils.GETKAIHUBANK, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						getKaihuBankJsonInfo(arg0.result);
					}
				});
	}

	protected void getKaihuBankJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONObject data = object.getJSONObject("data");
				bankname = data.getString("name");
				bankcode = data.getString("code");
				tv_khbank.setText(bankname);
				sfgetkaihu = true;
				handler.sendEmptyMessage(HANDLER_GETKAIHUBANK_SUCCESS);

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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.tv_tijiao:
			if (canPost()) {
				if (sfgetkaihu) {
					showLoading();
					tijiaoBankCardHttpPost();
				} else {
					String cardnum = et_card_num.getText().toString().trim();
					getKaihuBankHttpPost(cardnum);
					sftjBankInfo = true;
				}
			}
			// Intent intent = new Intent(NewAddBankCardActivity.this,
			// AddBankPicActivity.class);
			// startActivity(intent);
			break;

		default:
			break;
		}
	}

	// 提交银行卡信息
	private void tijiaoBankCardHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token",
				new DButil().gettoken(NewAddBankCardActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(NewAddBankCardActivity.this));
		params.addBodyParameter("true_name", et_true_name.getText().toString().trim());
		params.addBodyParameter("bank_code", bankcode);
		params.addBodyParameter("bank_name", bankname);
		params.addBodyParameter("bank_mobile", et_bank_phone.getText().toString().trim());
		params.addBodyParameter("number", et_sfz_num.getText().toString().trim());
		params.addBodyParameter("card_account", et_card_num.getText().toString().trim());
		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWADDBANK, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						tijiaoBankCardJsonInfo(arg0.result);
					}
				});
	}

	protected void tijiaoBankCardJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				bank_card_id = object.getString("bank_card_id");
				handler.sendEmptyMessage(HANDLER_CARDINFO_SUCCESS);

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
		String true_name = et_true_name.getText().toString().trim();
		String card_account = et_card_num.getText().toString().trim();
		String sfz_num = et_sfz_num.getText().toString().trim();
		String bank_phone = et_bank_phone.getText().toString().trim();
		if (TextUtils.isEmpty(card_account)) {
			et_card_num.requestFocus();
			Toast.makeText(NewAddBankCardActivity.this, "银行账号不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (card_account.length() < 16 || 19 < card_account.length()) {
			et_card_num.requestFocus();
			Toast.makeText(NewAddBankCardActivity.this, "银行账号格式不正确",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(true_name)) {
			et_true_name.requestFocus();
			Toast.makeText(NewAddBankCardActivity.this, "开户姓名不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(sfz_num)) {
			et_sfz_num.requestFocus();
			Toast.makeText(NewAddBankCardActivity.this, "开户人身份证号码不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (!IDUtils.isCorrectID(sfz_num)) {
			et_sfz_num.requestFocus();
			Toast.makeText(NewAddBankCardActivity.this, "开户人身份证号码格式不正确",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (bank_phone.length() > 12) {
			et_bank_phone.requestFocus();
			Toast.makeText(NewAddBankCardActivity.this, "开户人手机号码格式不正确",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	// 加载中动画
	private Dialog loadingDialog;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(NewAddBankCardActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				NewAddBankCardActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(NewAddBankCardActivity.this,
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
		if (resultCode == 300) {
			Intent intent = new Intent();
			setResult(300, intent);
			finish();
		}
	}

}
