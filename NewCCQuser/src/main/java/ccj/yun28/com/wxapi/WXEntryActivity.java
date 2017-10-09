package ccj.yun28.com.wxapi;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.utils.Utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI wxApi;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 错误
	private static final int HANDLER_NN_FAILURE = 1;
	// 获取微信个人信息
	protected static final int HANDLER_GETMSG_SUCCESS = 2;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				Toast.makeText(WXEntryActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				Toast.makeText(WXEntryActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_GETMSG_SUCCESS:
				if (!LoginActivity.logionintance.isFinishing()) {
					LoginActivity.logionintance.finish();
				}
				Intent intent = new Intent(WXEntryActivity.this,
						LoginActivity.class);
				String result = (String) msg.obj;
				intent.putExtra("type", "wx");
				intent.putExtra("wxmsg", result);
				startActivity(intent);
				finish();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expay_entry);
		wxApi = WXAPIFactory.createWXAPI(this, Utils.WX_APP_ID);
		wxApi.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		wxApi.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq baseReq) {

	}

	@Override
	public void onResp(BaseResp baseResp) {

		switch (baseResp.errCode) {

		case BaseResp.ErrCode.ERR_COMM:// 失败,可对用户做提醒操作
			Toast.makeText(WXEntryActivity.this, "登录失败!", Toast.LENGTH_SHORT)
					.show();
			finish();
			break;
		case BaseResp.ErrCode.ERR_OK:// 成功

			// 微信登录
			SendAuth.Resp sendResp = (SendAuth.Resp) baseResp;
			if (sendResp != null) {
				String code = sendResp.code;
				getAccess_token(code);
			}

			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			Toast.makeText(WXEntryActivity.this, "取消登录!", Toast.LENGTH_SHORT)
					.show();
			finish();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			Toast.makeText(WXEntryActivity.this, "拒绝授权!", Toast.LENGTH_SHORT)
					.show();
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 获取openid accessToken值用于后期操作
	 * 
	 * @param code
	 *            请求码
	 */
	private void getAccess_token(final String code) {
		HttpUtils httpUtils = new HttpUtils();
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
				+ Utils.WX_APP_ID + "&secret=" + Utils.WX_APP_SECRET + "&code="
				+ code + "&grant_type=authorization_code";
		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Log.e("ee", "网络错误:" + arg1);
				handler.sendEmptyMessage(HANDLER_NET_FAILURE);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				Access_tokenjsoninfo(arg0.result);
			}
		});

	}

	protected void Access_tokenjsoninfo(String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			String openid = jsonObject.getString("openid").toString().trim();
			String access_token = jsonObject.getString("access_token")
					.toString().trim();
			getUserMesg(access_token, openid);
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	/**
	 * 获取微信的个人信息
	 * 
	 * @param access_token
	 * @param openid
	 */
	private void getUserMesg(final String access_token, final String openid) {

		HttpUtils httpUtils = new HttpUtils();
		String path = "https://api.weixin.qq.com/sns/userinfo?access_token="
				+ access_token + "&openid=" + openid;
		// LogUtils.log("getUserMesg：" + path);
		// 网络请求，根据自己的请求方式
		httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.e("ee", "网络错误:" + arg1);
				handler.sendEmptyMessage(HANDLER_NET_FAILURE);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETMSG_SUCCESS, arg0.result));
			}
		});
	}

}
