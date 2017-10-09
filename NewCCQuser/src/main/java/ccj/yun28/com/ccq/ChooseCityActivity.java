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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.ChooseCityMyGridAdapter;
import ccj.yun28.com.common.SharedCommon;
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
 * ccq-选择城市页
 * 
 * @author meihuali
 * 
 */
public class ChooseCityActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取信息成功
	private static final int HANDLER_CCQKAITONGCITY_SUCCESS = 3;

	private List<Map<String, String>> yiKaiTongCityList;
	private ChooseCityMyGridAdapter chooseCityMyGridAdapter;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(ChooseCityActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(ChooseCityActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(ChooseCityActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取信息成功
			case HANDLER_CCQKAITONGCITY_SUCCESS:
				dissDialog();
				chooseCityMyGridAdapter.NotifyList(yiKaiTongCityList);
				chooseCityMyGridAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choosecity);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 定位城市
		TextView tv_dweicity = (TextView) findViewById(R.id.tv_dweicity);
		// String city = getIntent().getStringExtra("dwcity");
		// tv_dweicity.setText(city);
		// 开通城市gridview
		GridView gv_opencity = (GridView) findViewById(R.id.gv_opencity);

		yiKaiTongCityList = new ArrayList<Map<String, String>>();
		chooseCityMyGridAdapter = new ChooseCityMyGridAdapter(
				ChooseCityActivity.this);
		gv_opencity.setAdapter(chooseCityMyGridAdapter);

		yiKaiTongCityHttpPost();

		line_back.setOnClickListener(this);
		gv_opencity.setOnItemClickListener(this);

	}

	// 已开通城市接口
	private void yiKaiTongCityHttpPost() {
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(ChooseCityActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		httpUtils.send(HttpMethod.POST, JiekouUtils.CCQYIKAITONGCITY, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						yiKaiTongCityListInfo(arg0.result);
					}
				});
	}

	// 已开通城市数据解析
	protected void yiKaiTongCityListInfo(String result) {
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				JSONArray array = object.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = array.getJSONObject(i);
					yiKaiTongCityDetailListInfo(json);
				}
			} else {
				String data = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETINFO_FAILURE, data));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private void yiKaiTongCityDetailListInfo(JSONObject json) {
		try {
			String gc_id = json.getString("gc_id");
			String title = json.getString("title");
			Map<String, String> yiKaiTongCityMap = new HashMap<String, String>();
			yiKaiTongCityMap.put("gc_id", gc_id);
			yiKaiTongCityMap.put("title", title);
			yiKaiTongCityList.add(yiKaiTongCityMap);
			handler.sendEmptyMessage(HANDLER_CCQKAITONGCITY_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(ChooseCityActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				ChooseCityActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(ChooseCityActivity.this,
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent();
		intent.putExtra("city", yiKaiTongCityList.get(arg2).get("title"));
		intent.putExtra("latitude", getIntent().getStringExtra("latitude"));
		intent.putExtra("longitude", getIntent().getStringExtra("longitude"));
		// 保存city_id
		SharedUtil.saveStringValue(SharedCommon.SELECTOR_CITY_ID, yiKaiTongCityList.get(arg2).get("gc_id"));
		Log.e("log", "");
		SharedUtil.saveStringValue(SharedCommon.CITY, yiKaiTongCityList.get(arg2).get("title"));
		setResult(300, intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		onBackPressed();
	}

}
