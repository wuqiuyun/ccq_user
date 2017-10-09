package ccj.yun28.com.ccq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * 餐餐抢一元券-收银台
 * 
 * @author meihuali
 * 
 */
public class CcqShouYinTaiActivity extends BaseActivity implements
		OnClickListener {

	private TextView tv_yuejine;
	private List<Map<String, String>> zhanghuJineList;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取账户金额信息成功
	private static final int HANDLER_ZHANGHUJINE_SUCCESS = 3;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(CcqShouYinTaiActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(CcqShouYinTaiActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(CcqShouYinTaiActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取订单详情数据成功
			case HANDLER_ZHANGHUJINE_SUCCESS:
				dissDialog();
				tv_yuejine.setText(zhanghuJineList.get(2).get("value"));//设置余额支付的账户剩余余额
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ccqshouyintai);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		TextView tv_jine = (TextView) findViewById(R.id.tv_jine);
		// 支付宝支付
		LinearLayout line_zhifubaozhifu = (LinearLayout) findViewById(R.id.line_zhifubaozhifu);
		// 微信支付
		LinearLayout line_weixinzhifu = (LinearLayout) findViewById(R.id.line_weixinzhifu);
		// 余额支付
		LinearLayout line_yuezhifu = (LinearLayout) findViewById(R.id.line_yuezhifu);
		tv_yuejine = (TextView) findViewById(R.id.tv_yuejine);

		zhanghuJineList = new ArrayList<Map<String, String>>();

		if (getIntent() != null) {
			String jine = getIntent().getStringExtra("jine");
			tv_jine.setText(jine);
		}
		showLoading();
		getZhanghuJineHttpPost();

		line_back.setOnClickListener(this);
		line_zhifubaozhifu.setOnClickListener(this);
		line_weixinzhifu.setOnClickListener(this);
		line_yuezhifu.setOnClickListener(this);
	}

	// 获得账户各个金额接口
	private void getZhanghuJineHttpPost() {
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(CcqShouYinTaiActivity.this);
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(CcqShouYinTaiActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(CcqShouYinTaiActivity.this));
		httpUtils.send(HttpMethod.POST, JiekouUtils.GETZHANGHUJINE, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Log.e("ee", "失败:" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						getZhanghuJineListInfo(arg0.result);
					}
				});
	}

	// 获得账户各个金额数据解析
	protected void getZhanghuJineListInfo(String result) {
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONArray array = object.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = array.getJSONObject(i);
					getZhanghuJineDetailListInfo(json);
				}
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETINFO_FAILURE, message));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private void getZhanghuJineDetailListInfo(JSONObject json) {
		try {
			String code = json.getString("code");
			String title = json.getString("title");
			String value = json.getString("value");
			Map<String, String> zhanghuJineMap = new HashMap<String, String>();
			zhanghuJineMap.put("code", code);
			zhanghuJineMap.put("title", title);
			zhanghuJineMap.put("value", value);
			zhanghuJineList.add(zhanghuJineMap);
			handler.sendEmptyMessage(HANDLER_ZHANGHUJINE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.line_back:
//			onBackPressed();
			finish();
			break;

		case R.id.line_zhifubaozhifu:
			Intent intent = new Intent();
			intent.putExtra("zffs", "支付宝");
			intent.putExtra("zffs_id", zhanghuJineList.get(0).get("code"));
			setResult(300, intent);
			finish();
			break;
		case R.id.line_weixinzhifu:
			Intent intent1 = new Intent();
			intent1.putExtra("zffs", "微信支付");
			intent1.putExtra("zffs_id", zhanghuJineList.get(1).get("code"));
			setResult(300, intent1);
			finish();
			break;
		case R.id.line_yuezhifu:
			Intent intent2 = new Intent();
			intent2.putExtra("zffs", "余额支付");
			intent2.putExtra("zffs_id", zhanghuJineList.get(2).get("code"));
			setResult(300, intent2);
			finish();
			break;

		default:
			break;
		}
	}

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(CcqShouYinTaiActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				CcqShouYinTaiActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(CcqShouYinTaiActivity.this,
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
