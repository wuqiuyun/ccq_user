package ccj.yun28.com.mine;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
 * 新版我的钱包
 * 
 * @author meihuali
 * 
 */
public class NewMyWalletActivity extends BaseActivity implements OnClickListener {

	private TextView tv_yue;
	private TextView tv_yunbi;
	private TextView tv_zhaoshang;
	private TextView tv_tuiguang;
	private TextView tv_vip;
	private TextView tv_bankcard;
	private TextView tv_tixian;
	private String card;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取我的钱包信息成功
	private static final int HANDLER_WALLET_SUCCESS = 3;
	// token失效
	private static final int HANDLER_TOKEN_FAILURE = 4;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(NewMyWalletActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(NewMyWalletActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(NewMyWalletActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取我的钱包信息成功
			case HANDLER_WALLET_SUCCESS:
				dissDialog();
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(NewMyWalletActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent intent = new Intent(NewMyWalletActivity.this,
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
		setContentView(R.layout.activity_newmywallety);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_yue = (LinearLayout) findViewById(R.id.line_yue);
		tv_yue = (TextView) findViewById(R.id.tv_yue);
		LinearLayout line_yunbi = (LinearLayout) findViewById(R.id.line_yunbi);
		tv_yunbi = (TextView) findViewById(R.id.tv_yunbi);

		LinearLayout line_zhaoshang = (LinearLayout) findViewById(R.id.line_zhaoshang);
		tv_zhaoshang = (TextView) findViewById(R.id.tv_zhaoshang);
		LinearLayout line_tuiguang = (LinearLayout) findViewById(R.id.line_tuiguang);
		tv_tuiguang = (TextView) findViewById(R.id.tv_tuiguang);
		LinearLayout line_vip = (LinearLayout) findViewById(R.id.line_vip);
		tv_vip = (TextView) findViewById(R.id.tv_vip);
		LinearLayout line_bankcard = (LinearLayout) findViewById(R.id.line_bankcard);
		tv_bankcard = (TextView) findViewById(R.id.tv_bankcard);
		LinearLayout line_tixian = (LinearLayout) findViewById(R.id.line_tixian);
		tv_tixian = (TextView) findViewById(R.id.tv_tixian);

		showLoading();
		walletHttpPost();

		line_back.setOnClickListener(this);
		line_yunbi.setOnClickListener(this);
		line_yue.setOnClickListener(this);
		line_zhaoshang.setOnClickListener(this);
		line_tuiguang.setOnClickListener(this);
		line_vip.setOnClickListener(this);
		line_bankcard.setOnClickListener(this);
		line_tixian.setOnClickListener(this);
	}

	private void walletHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(NewMyWalletActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token",
				new DButil().gettoken(NewMyWalletActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(NewMyWalletActivity.this));
		httpUtils.send(HttpMethod.POST, JiekouUtils.MYWALLET, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						walletListInfo(arg0.result);
					}
				});
	}

	protected void walletListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONObject data = object.getJSONObject("data");
				String current_balance = data.getString("current_balance");
				String current_point = data.getString("current_point");
				String current_merchants = data.getString("current_merchants");
				String current_promote = data.getString("current_promote");
				String current_vip = data.getString("current_vip");
				String bankcard = data.getString("bankcard");
				String all_real_amount = data.getString("all_real_amount");
				tv_yue.setText(current_balance);
				tv_yunbi.setText(current_point);
				tv_zhaoshang.setText(current_merchants);
				tv_tuiguang.setText(current_promote);
				tv_vip.setText(current_vip);
				tv_bankcard.setText(bankcard);
				tv_tixian.setText(all_real_amount);
				handler.sendEmptyMessage(HANDLER_WALLET_SUCCESS);
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
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_yunbi:
			intent = new Intent(NewMyWalletActivity.this,
					NewYunBiORYuEDetailActivity.class);
			intent.putExtra("title", "云币");
			intent.putExtra("leixing", "yunbi");
			startActivity(intent);
			break;
		case R.id.line_yue:
			intent = new Intent(NewMyWalletActivity.this,
					NewYunBiORYuEDetailActivity.class);
			intent.putExtra("title", "余额");
			intent.putExtra("leixing", "yue");
			startActivity(intent);
			break;
		case R.id.line_zhaoshang:
			intent = new Intent(NewMyWalletActivity.this,
					NewYunBiORYuEDetailActivity.class);
			intent.putExtra("title", "招商佣金");
			intent.putExtra("leixing", "zs");
			startActivity(intent);
			break;
		case R.id.line_tuiguang:
			intent = new Intent(NewMyWalletActivity.this,
					NewYunBiORYuEDetailActivity.class);
			intent.putExtra("title", "推广佣金");
			intent.putExtra("leixing", "tg");
			startActivity(intent);
			break;
		case R.id.line_vip:
			intent = new Intent(NewMyWalletActivity.this,
					NewYunBiORYuEDetailActivity.class);
			intent.putExtra("title", "VIP佣金");
			intent.putExtra("leixing", "vip");
			startActivity(intent);
			break;
		case R.id.line_bankcard:
			intent = new Intent(NewMyWalletActivity.this,
					BankCardActivity.class);
			intent.putExtra("num", card);
			startActivityForResult(intent, 100);
			break;
		case R.id.line_tixian:
			intent = new Intent(NewMyWalletActivity.this, TiXianRecordActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(NewMyWalletActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				NewMyWalletActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(NewMyWalletActivity.this,
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
		if (resultCode == 200) {
			showLoading();
			walletHttpPost();
		}
	}
}
