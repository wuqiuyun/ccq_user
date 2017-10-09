package ccj.yun28.com.mine;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

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

/**
 * 设置-账户关联
 * 
 * @author meihuali
 * 
 */
public class ZhangHuGuanLianActivtiy extends BaseActivity implements
		OnClickListener {

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取信息成功
	private static final int HANDLER_GETINFO_SUCCESS = 3;
	// token失效
	private static final int HANDLER_TOKEN_FAILURE = 4;
	// 解除绑定手机号成功
	private static final int HANDLER_CANCELPHONE_SUCCESS = 5;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(ZhangHuGuanLianActivtiy.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(ZhangHuGuanLianActivtiy.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(ZhangHuGuanLianActivtiy.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 提交成功
			case HANDLER_GETINFO_SUCCESS:
				dissDialog();
				break;
			// 解除绑定手机号成功
			case HANDLER_CANCELPHONE_SUCCESS:
				dissDialog();
				if ("phone".equals(type)) {
					tv_phone.setText("未关联");
				} else if ("wechat".equals(type)) {
					tv_wechat.setText("未关联");
				}
				Toast.makeText(ZhangHuGuanLianActivtiy.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;

			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(ZhangHuGuanLianActivtiy.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent intent = new Intent(ZhangHuGuanLianActivtiy.this,
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
		setContentView(R.layout.activity_zhanghuguanlian);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_wechat = (TextView) findViewById(R.id.tv_wechat);
		LinearLayout line_phone = (LinearLayout) findViewById(R.id.line_phone);
		LinearLayout line_wechat = (LinearLayout) findViewById(R.id.line_wechat);

		showLoading();
		getGuanlianInfoHttpPost();

		line_back.setOnClickListener(this);
		line_phone.setOnClickListener(this);
		line_wechat.setOnClickListener(this);
	}

	private void getGuanlianInfoHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(ZhangHuGuanLianActivtiy.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(ZhangHuGuanLianActivtiy.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(ZhangHuGuanLianActivtiy.this));
		httpUtils.send(HttpMethod.POST, JiekouUtils.SFVIP, params,
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
				// 1是 0否
				JSONObject data = object.getJSONObject("data");
				moblie_verify = data.getString("moblie_verify");
				weixin_verify = data.getString("weixin_verify");
				bind_mobile = data.getString("member_mobile");

				if ("1".equals(moblie_verify)) {
					tv_phone.setText("已绑定");
				}
				if ("1".equals(weixin_verify)) {
					tv_wechat.setText("已绑定");
				}
				handler.sendEmptyMessage(HANDLER_GETINFO_SUCCESS);
			} else if ("700".equals(code)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETINFO_FAILURE, message));
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TOKEN_FAILURE, message));
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
		case R.id.line_phone:
			if ("1".endsWith(moblie_verify)) {
				type = "phone";
				showBindDialog();
			}
			break;
		case R.id.line_wechat:
			if ("1".endsWith(weixin_verify)) {
				type = "wechat";
				showBindDialog();
			}
			break;

		default:
			break;
		}
	}

	private void showBindDialog() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(ZhangHuGuanLianActivtiy.this).inflate(
				R.layout.dialog_bind, null);
		TextView tv_first = (TextView) view.findViewById(R.id.tv_first);
		TextView tv_second = (TextView) view.findViewById(R.id.tv_second);
		TextView tv_no = (TextView) view.findViewById(R.id.tv_no);
		TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);

		if ("phone".equals(type)) {
			tv_first.setText("是否取消与手机号码：" + bind_mobile + "的关联？");
			tv_second.setText("取消后将不能和手机相关关联的操作!");
		} else if ("wechat".equals(type)) {
			tv_first.setText("是否取消与微信的关联？");
			tv_second.setText("取消后将不再能使用微信登录功能！");
		}

		final Dialog bindDialog = new Dialog(ZhangHuGuanLianActivtiy.this,
				R.style.mDialogStyle);
		bindDialog.setContentView(view);
		bindDialog.setCanceledOnTouchOutside(false);
		bindDialog.show();
		tv_no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				bindDialog.dismiss();
			}
		});
		tv_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showLoading();
				bindDialog.dismiss();
				if ("phone".equals(type)) {
					cancelBindPhoneHttpPost();
				} else if ("wechat".equals(type)) {
					cancelBindWechatHttpPost();
				}
			}
		});
	}
	// 解除微信绑定
	protected void cancelBindWechatHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(ZhangHuGuanLianActivtiy.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(ZhangHuGuanLianActivtiy.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(ZhangHuGuanLianActivtiy.this));
		httpUtils.send(HttpMethod.POST, JiekouUtils.CANCELBINDWECHAT, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						cancelBindPhoneListInfo(arg0.result);
					}
				});
	}

	// 解除手机号绑定
	protected void cancelBindPhoneHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(ZhangHuGuanLianActivtiy.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token",
				new DButil().gettoken(ZhangHuGuanLianActivtiy.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(ZhangHuGuanLianActivtiy.this));
		httpUtils.send(HttpMethod.POST, JiekouUtils.CANCELBINDPHONE, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						cancelBindPhoneListInfo(arg0.result);
					}
				});
	}

	protected void cancelBindPhoneListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_CANCELPHONE_SUCCESS, message));
			} else if ("700".equals(code)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETINFO_FAILURE, message));
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TOKEN_FAILURE, message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 加载中动画
	private Dialog loadingDialog;
	private String type;
	private TextView tv_phone;
	private TextView tv_wechat;
	private String moblie_verify;
	private String weixin_verify;
	private String bind_mobile;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(ZhangHuGuanLianActivtiy.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				ZhangHuGuanLianActivtiy.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(ZhangHuGuanLianActivtiy.this,
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
