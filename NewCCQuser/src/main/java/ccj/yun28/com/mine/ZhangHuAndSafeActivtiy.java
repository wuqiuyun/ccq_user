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
import ccj.yun28.com.NewBindPhoneActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 账户与安全
 * 
 * @author meihuali
 * 
 */
public class ZhangHuAndSafeActivtiy extends BaseActivity implements OnClickListener {
	//红点
	private ImageView iv_login_mm_red;
	private LinearLayout line_is_name_sz;
	
	private TextView tv_name;
	private TextView tv_paymima;
	private TextView tv_shimingrenzheng;
	private TextView tv_phonerenzheng;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取我的信息成功
	private static final int HANDLER_INFO_SUCCESS = 3;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(ZhangHuAndSafeActivtiy.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(ZhangHuAndSafeActivtiy.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(ZhangHuAndSafeActivtiy.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				}
				break;
			// 获取我的信息成功
			case HANDLER_INFO_SUCCESS:
				dissDialog();
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
		setContentView(R.layout.activity_zhanghuandsafe);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_name = (LinearLayout) findViewById(R.id.line_name);
		tv_name = (TextView) findViewById(R.id.tv_name);
		line_is_name_sz = (LinearLayout) findViewById(R.id.line_is_name_sz);
		LinearLayout line_loginmima = (LinearLayout) findViewById(R.id.line_loginmima);
		tv_loginmima = (TextView) findViewById(R.id.tv_loginmima);
		iv_login_mm_red = (ImageView) findViewById(R.id.iv_login_mm_red);
		LinearLayout line_paymima = (LinearLayout) findViewById(R.id.line_paymima);
		tv_paymima = (TextView) findViewById(R.id.tv_paymima);
		LinearLayout line_shimingrenzheng = (LinearLayout) findViewById(R.id.line_shimingrenzheng);
		tv_shimingrenzheng = (TextView) findViewById(R.id.tv_shimingrenzheng);
		LinearLayout line_phonerenzheng = (LinearLayout) findViewById(R.id.line_phonerenzheng);
		tv_phonerenzheng = (TextView) findViewById(R.id.tv_phonerenzheng);
		showLoading();
		infoHttpPost();

		line_back.setOnClickListener(this);
		line_name.setOnClickListener(this);
		line_loginmima.setOnClickListener(this);
		line_paymima.setOnClickListener(this);
		line_shimingrenzheng.setOnClickListener(this);
		line_phonerenzheng.setOnClickListener(this);
	}

	private void infoHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(ZhangHuAndSafeActivtiy.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(ZhangHuAndSafeActivtiy.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(ZhangHuAndSafeActivtiy.this));
		httpUtils.send(HttpMethod.POST, JiekouUtils.ZHANGHUYUANQUAN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						infoListInfo(arg0.result);
					}
				});
	}

	protected void infoListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONObject data = object.getJSONObject("data");
				String member_email = data.getString("member_email");
				String member_email_bind = data.getString("member_email_bind");
				String member_mobile = data.getString("member_mobile");
				String member_mobile_bind = data
						.getString("member_mobile_bind");
				String member_name = data.getString("member_name");
				String member_paypwd = data.getString("member_paypwd");
				member_paypwd__status = data.getString("member_paypwd_status");
				String real_name = data.getString("real_name");
				real_verify = data.getString("real_verify");
				set_member_passwd = data.getString("set_member_passwd");
				set_member_name = data.getString("set_member_name");
				if ("0".equals(set_member_passwd)) {
					tv_loginmima.setText("修改");
					iv_login_mm_red.setVisibility(View.GONE);
				}else{
					tv_loginmima.setText("未设置");
					iv_login_mm_red.setVisibility(View.VISIBLE);
				}
				if ("0".equals(set_member_name)) {
					line_is_name_sz.setVisibility(View.INVISIBLE);
				}else{
					line_is_name_sz.setVisibility(View.VISIBLE);
				}
				
				if ("0".equals(set_member_passwd) && "0".equals(set_member_name)) {
					SharedUtil.saveStringValue(SharedCommon.IS_LOGINMIMA_MEMBERNAME, "0");
				}else{
					SharedUtil.saveStringValue(SharedCommon.IS_LOGINMIMA_MEMBERNAME, "1");
				}
				
				tv_name.setText(member_name);
				tv_paymima.setText(member_paypwd);
				tv_shimingrenzheng.setText(real_name);
				tv_phonerenzheng.setText(member_mobile);
				handler.sendEmptyMessage(HANDLER_INFO_SUCCESS);
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
		case R.id.line_name:
			if ("1".equals(set_member_name)) {
				intent = new Intent(ZhangHuAndSafeActivtiy.this,
						NewBindPhoneActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.line_loginmima:
			if ("1".equals(set_member_passwd)) {
				intent = new Intent(ZhangHuAndSafeActivtiy.this,
						SetLoginmimaActivity.class);
				startActivity(intent);
			}else{
				intent = new Intent(ZhangHuAndSafeActivtiy.this,
						LoginMiMaActivity.class);
				startActivity(intent);
			}

			break;
		case R.id.line_paymima:
			if ("1".equals(member_paypwd__status)) {
				intent = new Intent(ZhangHuAndSafeActivtiy.this,
						XiuGaiPayMiMaActivity.class);
				startActivity(intent);
			}else{
				intent = new Intent(ZhangHuAndSafeActivtiy.this,
						ShezhiPayMiMaActivity.class);
				startActivity(intent);
				
			}

			break;
		case R.id.line_shimingrenzheng:
			// 实名认证状态 0未认证1已认证2待认证3认证不通过
			if ("0".equals(real_verify)) {
//				intent = new Intent(ZhangHuAndSafeActivtiy.this,
//						WeiShiMingRenZhengActivity.class);
				intent = new Intent(ZhangHuAndSafeActivtiy.this,
						NewWeiShiMingRenZhengActivity.class);
				intent.putExtra("title", "实名认证");
				startActivity(intent);
			}else {
				intent = new Intent(ZhangHuAndSafeActivtiy.this,
						ShiMingRenZhengActivity.class);
				startActivity(intent);
			}

			break;
		case R.id.line_phonerenzheng:
			intent = new Intent(ZhangHuAndSafeActivtiy.this,
					PhoneRenZhengActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	// 加载中动画
	private Dialog loadingDialog;
	//支付密码状态
	private String member_paypwd__status;
	//实名认证状态
	private String real_verify;
	private String set_member_passwd;
	private TextView tv_loginmima;
	private String set_member_name;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(ZhangHuAndSafeActivtiy.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				ZhangHuAndSafeActivtiy.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(ZhangHuAndSafeActivtiy.this,
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		infoHttpPost();
	}
}
