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

public class TiXianTecordDetailActivtiy extends BaseActivity implements
		OnClickListener {

	private TextView tv_daozhangjine;
	private TextView tv_tixianjine;
	private TextView tv_bianhao;
	private TextView tv_shouxufei;
	private TextView tv_tixianzhanghu;
	private TextView tv_shenqingtime;
	private TextView tv_tixianleixing;
	private TextView tv_beizhu;
	private TextView tv_status;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	private static final int HANDLER_TIXIANRECORDDETAIL_SUCCESS = 3;
	// token失效
	private static final int HANDLER_TOKEN_FAILURE = 4;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(TiXianTecordDetailActivtiy.this,
						"当前网络不可用,请检查网络", Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(TiXianTecordDetailActivtiy.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(TiXianTecordDetailActivtiy.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_TIXIANRECORDDETAIL_SUCCESS:
				dissDialog();
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(TiXianTecordDetailActivtiy.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent intent = new Intent(TiXianTecordDetailActivtiy.this,
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
		setContentView(R.layout.activity_tixiantecorddetail);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		tv_daozhangjine = (TextView) findViewById(R.id.tv_daozhangjine);
		tv_tixianjine = (TextView) findViewById(R.id.tv_tixianjine);
		tv_bianhao = (TextView) findViewById(R.id.tv_bianhao);
		tv_shouxufei = (TextView) findViewById(R.id.tv_shouxufei);
		tv_tixianzhanghu = (TextView) findViewById(R.id.tv_tixianzhanghu);
		tv_shenqingtime = (TextView) findViewById(R.id.tv_shenqingtime);
		tv_tixianleixing = (TextView) findViewById(R.id.tv_tixianleixing);
		tv_beizhu = (TextView) findViewById(R.id.tv_beizhu);
		tv_status = (TextView) findViewById(R.id.tv_status);

		if (getIntent() != null) {
			String id = getIntent().getStringExtra("id");
			showLoading();
			tiXianTecordDetailHttpPost(id);
		}

		line_back.setOnClickListener(this);
	}

	private void tiXianTecordDetailHttpPost(String id) {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(TiXianTecordDetailActivtiy.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token",
				new DButil().gettoken(TiXianTecordDetailActivtiy.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(TiXianTecordDetailActivtiy.this));
		params.addBodyParameter("id", id);
		httpUtils.send(HttpMethod.POST, JiekouUtils.TIXIANJILUDETAIL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						tiXianTecordDetailListInfo(arg0.result);
					}
				});
	}

	protected void tiXianTecordDetailListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONObject data = object.getJSONObject("data");
				String id = data.getString("id");
				String w_sn = data.getString("w_sn");
				String time = data.getString("time");
				String type = data.getString("type");
				String amount = data.getString("amount");
				String real_amount = data.getString("real_amount");
				String card_account = data.getString("card_account");
				String fee = data.getString("fee");
				String note = data.getString("note");
				String status = data.getString("status");
				tv_daozhangjine.setText(real_amount + "元");
				tv_tixianjine.setText(amount + "元");
				tv_bianhao.setText(w_sn);
				tv_shouxufei.setText(fee + "元");
				tv_tixianzhanghu.setText(card_account);
				tv_shenqingtime.setText(time);
				tv_beizhu.setText(note);
				tv_status.setText(status);
				tv_tixianleixing.setText(type);

				handler.sendEmptyMessage(HANDLER_TIXIANRECORDDETAIL_SUCCESS);
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
		onBackPressed();
	}

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(TiXianTecordDetailActivtiy.this)
				.inflate(R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				TiXianTecordDetailActivtiy.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(TiXianTecordDetailActivtiy.this,
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
