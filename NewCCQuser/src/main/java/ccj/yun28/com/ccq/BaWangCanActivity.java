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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.CcqShopLvAdapter;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 霸王餐
 * 
 * @author meihuali
 * 
 */
public class BaWangCanActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {

	private List<Map<String, String>> ccqshopListdetaiList;

	private CcqShopLvAdapter ccqShopLvAdapter;
	private boolean isFirst = true;

	// 经 度
	private String longitude = "";
	// 纬 度
	private String latitude = "";
	// 市
	private String shi = "";
	// 定位
	private AMapLocationClient locationClient = null;
	private AMapLocationClientOption locationOption = new AMapLocationClientOption();
	
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取数据失败
	private static final int HANDLER_GETIONFO_FAILURE = 2;
	private static final int HANDLER_CCQSHOPDETAI_SUCCESS = 3;

	protected static final int HANDLER_DINGWEI_SUCCESS = 4;

	protected static final int HANDLER_DINGWEI_FINISH = 5;
	
	// 当前位置
		private TextView tv_now_place;
		private LinearLayout line_dwshuaxin;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				dissDialog();
				if (BaWangCanActivity.this != null) {

					Toast.makeText(BaWangCanActivity.this, "当前网络不可用,请检查网络",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case HANDLER_NN_FAILURE:
				dissDialog();
				if (BaWangCanActivity.this != null) {

					Toast.makeText(BaWangCanActivity.this, "当前网络出错,请检查网络",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case HANDLER_GETIONFO_FAILURE:
				dissDialog();
				if (BaWangCanActivity.this != null) {

					if (msg.obj != null) {
						Toast.makeText(BaWangCanActivity.this,
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();
					}
				}
				break;
			case HANDLER_CCQSHOPDETAI_SUCCESS:
				dissDialog();
				ccqShopLvAdapter.NotifyList(ccqshopListdetaiList);
				ccqShopLvAdapter.notifyDataSetChanged();
				break;
			case HANDLER_DINGWEI_FINISH:
				tv_now_place.setText("定位失败，重新定位或者手动切换城市");
				dissDialog();
				stopLocation();
				if (isFirst) {
					getStoreListHttpPost();
				}
				break;
			case HANDLER_DINGWEI_SUCCESS:
				stopLocation();
				getStoreListHttpPost();
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
		setContentView(R.layout.activity_chaojimingquan);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_weizhi = (LinearLayout) findViewById(R.id.line_weizhi);
		tv_now_place = (TextView) findViewById(R.id.tv_now_place);
		line_dwshuaxin = (LinearLayout) findViewById(R.id.line_dwshuaxin);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		ListView lv = (ListView) findViewById(R.id.lv);
		line_weizhi.setVisibility(View.VISIBLE);
		tv_title.setText("就要霸王餐");

		ccqshopListdetaiList = new ArrayList<Map<String, String>>();

		ccqShopLvAdapter = new CcqShopLvAdapter(BaWangCanActivity.this);
		lv.setAdapter(ccqShopLvAdapter);

		showLoading();
		initLocation();

		line_back.setOnClickListener(this);
		line_dwshuaxin.setOnClickListener(this);
		lv.setOnItemClickListener(this);
	}

	// 获取餐餐抢店铺列表数据
	private void getStoreListHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(BaWangCanActivity.this);
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("lat1", latitude);
		params.addBodyParameter("lng1", longitude);
		params.addBodyParameter("city_id", shi);
		httpUtils.send(HttpMethod.POST, JiekouUtils.BAWANGCAN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						ccqStoreListJsonInfo(arg0.result);
					}
				});
	}

	protected void ccqStoreListJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				ccqshopListdetaiList.clear();
				JSONArray array = object.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = array.getJSONObject(i);
					ccqshopDetailListInfo(json);
				}
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETIONFO_FAILURE, message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private void ccqshopDetailListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String store_name = json.getString("store_name");
			String store_id = json.getString("store_id");
			String union_img = json.getString("union_img");
			String store_credit = json.getString("store_credit");
			String store_address = json.getString("store_address");
			String latitude = json.getString("latitude");
			String longitude = json.getString("longitude");
			String distance_value = json.getString("distance_value");
			String distance = json.getString("distance");
			String evaluate = json.getString("evaluate");
			String sales_num = json.getString("sales_num");
			String geval_scores = json.getString("geval_scores");
			String adv = json.getString("adv");
			Map<String, String> ccqshopDetailMap = new HashMap<String, String>();
			ccqshopDetailMap.put("store_name", store_name);
			ccqshopDetailMap.put("store_id", store_id);
			ccqshopDetailMap.put("union_img", union_img);
			ccqshopDetailMap.put("store_credit", store_credit);
			ccqshopDetailMap.put("store_address", store_address);
			ccqshopDetailMap.put("latitude", latitude);
			ccqshopDetailMap.put("longitude", longitude);
			ccqshopDetailMap.put("distance_value", distance_value);
			ccqshopDetailMap.put("distance", distance);
			ccqshopDetailMap.put("evaluate", evaluate);
			ccqshopDetailMap.put("sales_num", sales_num);
			ccqshopDetailMap.put("geval_scores", geval_scores);
			ccqshopDetailMap.put("adv", adv);
			ccqshopListdetaiList.add(ccqshopDetailMap);
			handler.sendEmptyMessage(HANDLER_CCQSHOPDETAI_SUCCESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 加载中动画
	private Dialog loadingDialog;


	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(BaWangCanActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				BaWangCanActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(BaWangCanActivity.this, R.style.mDialogStyle);
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			
			break;
		case R.id.line_dwshuaxin:
			isFirst = false;
			showLoading();
			startLocation();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		// Intent intent = new Intent(BaWangCanActivity.this,
		// CCQStoreDetailActivity.class);
		Intent intent = new Intent(BaWangCanActivity.this,
				OldCCQStoreDetailActivity.class);
		intent.putExtra("starlat", latitude);
		intent.putExtra("starlng", longitude);
		intent.putExtra("store_id",
				ccqshopListdetaiList.get(position).get("store_id"));
		startActivity(intent);
	}
	
	/**
	 * 初始化定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 * 
	 */
	private void initLocation() {
		// 初始化client
		locationClient = new AMapLocationClient(BaWangCanActivity.this
				.getApplicationContext());
		// 设置定位参数
		locationClient.setLocationOption(getDefaultOption());
		// 设置定位监听
		locationClient.setLocationListener(locationListener);

		// 定位
		startLocation();
	}
	
	/**
	 * 默认的定位参数
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 * 
	 */
	private AMapLocationClientOption getDefaultOption() {
		AMapLocationClientOption mOption = new AMapLocationClientOption();
		mOption.setLocationMode(AMapLocationMode.Hight_Accuracy);// 可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
		mOption.setGpsFirst(false);// 可选，设置是否gps优先，只在高精度模式下有效。默认关闭
		mOption.setHttpTimeOut(30000);// 可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
		mOption.setInterval(2000);// 可选，设置定位间隔。默认为2秒
		mOption.setNeedAddress(true);// 可选，设置是否返回逆地理地址信息。默认是ture
		mOption.setOnceLocation(false);// 可选，设置是否单次定位。默认是false
		mOption.setOnceLocationLatest(false);// 可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
		AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP);// 可选，
																				// 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
		return mOption;
	}

	/**
	 * 定位监听
	 */

	AMapLocationListener locationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation loc) {
			if (null != loc) {
				// 解析定位结果
				// String result = LocationUtils.getLocationStr(loc);
				if (loc.getErrorCode() == 0) {
					// 经 度
					longitude = loc.getLongitude() + "";
					// 纬 度
					latitude = loc.getLatitude() + "";
					// Toast.makeText(getActivity(), "longitude...."+longitude,
					// Toast.LENGTH_SHORT)
					// .show();
					// 市
					String allshi = loc.getCity();
					shi = allshi.replaceAll("市", "");
					tv_now_place.setText(loc.getCity() + loc.getDistrict());
					handler.sendEmptyMessage(HANDLER_DINGWEI_SUCCESS);
				} else {
					// 定位失败
					Toast.makeText(BaWangCanActivity.this, "定位失败", Toast.LENGTH_SHORT)
							.show();
					handler.sendEmptyMessage(HANDLER_DINGWEI_FINISH);
				}
			} else {
				// Toast.makeText(getActivity(), "正在定位中", Toast.LENGTH_SHORT)
				// .show();
				handler.sendEmptyMessage(HANDLER_DINGWEI_FINISH);
			}
		}
	};

	/**
	 * 开始定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 * 
	 */
	private void startLocation() {
		// 设置定位参数
		locationClient.setLocationOption(getDefaultOption());
		// 启动定位
		locationClient.startLocation();
	}

	/**
	 * 停止定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 * 
	 */
	private void stopLocation() {
		// 停止定位
		locationClient.stopLocation();
	}

	/**
	 * 销毁定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 * 
	 */
	private void destroyLocation() {
		if (null != locationClient) {
			/**
			 * 如果AMapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
			 */
			locationClient.onDestroy();
			locationClient = null;
			locationOption = null;
		}
	}


}
