package ccj.yun28.com.ccq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.CCQCityAdapter;
import ccj.yun28.com.adapter.CCQfenleioneAdapter;
import ccj.yun28.com.adapter.CCQfenleitwoAdapter;
import ccj.yun28.com.adapter.CcqShopLvAdapter;
import ccj.yun28.com.bean.ZhuBBsearchBean;
import ccj.yun28.com.bean.ccq.CCQZhuBaseBean;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.MyListView;
import ccj.yun28.com.view.MyScrollView;
import ccj.yun28.com.view.OnRefreshListener;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol;
import com.amap.api.location.AMapLocationListener;
import com.amap.location.demo.CheckPermissionsActivity;
import com.amap.location.demo.LocationUtils;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 餐餐抢5个tag详情页
 * 
 * @author meihuali
 * 
 */
public class CcqTagDetailActivity extends CheckPermissionsActivity implements
		OnClickListener, OnItemClickListener, OnEditorActionListener {
	private boolean qbflflag = true;
	private boolean qcflag = true;
	private boolean lwzjflag = true;
	// 请求接口类型
	private String typehttppoString = "dp";
	
	// 无数据
	private TextView tv_notdata;
	// 全部分类弹窗
	private PopupWindow qbfl_popupWindow;
	// 全城弹窗
	private PopupWindow qc_popupWindow;
	// 离我最近弹窗
	private PopupWindow lwzj_popupWindow;
	// 全部分类
	private LinearLayout line_qbfl;
	private TextView tv_qbfl;
	private ImageView iv_qbfl;
	// 全城
	private LinearLayout line_qc;
	private TextView tv_qc;
	private ImageView iv_qc;
	// 离我最近
	private LinearLayout line_lwzj;
	private TextView tv_lwzj;
	private ImageView iv_lwzj;
	// 线条
	private View v_line;
	// 当前位置
	private TextView tv_now_place;
	// 店铺listview
	private ListView lv;
	private MaterialRefreshLayout refreshLayout;
	// 下拉区
	private ListView lv_city;
	// 全部分类
	private int zu;
	private ListView lv_one;
	private ListView lv_two;

	private CcqShopLvAdapter CcqShopLvAdapter;
	private List<Map<String, String>> ccqshopdetaiList;

	private CCQZhuBaseBean ccqfenlei;
	private CCQfenleioneAdapter ccqfenleioneadapter;
	private CCQfenleitwoAdapter ccqfenleitwoadapter;

	// 换一换 页数
	private int nowPage = 1;
	// 经 度
	private String longitude = "";
	// 纬 度
	private String latitude = "";
	// 市
	private String shi = "";
	// 定位
	private AMapLocationClient locationClient = null;
	private AMapLocationClientOption locationOption = new AMapLocationClientOption();

	// 店铺完成
	private boolean shopflag = false;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 分类成功
	private static final int HANDLER_CCQFENLEI_SUCCESS = 2;
	// 定位完成
	protected static final int HANDLER_DINGWEI_FINISH = 3;
	// 店铺列表成功
	private static final int HANDLER_CCQSHOPDETAI_SUCCESS = 4;
	// 店铺列表失败
	private static final int HANDLER_CCQSHOPDETAI_FAILURE = 5;
	private EditText et_search;
	private String lwzj = "离我最近";
	private String city = "全城";
	private String leixing = "全部分类";

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				Toast.makeText(CcqTagDetailActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(CcqTagDetailActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_CCQFENLEI_SUCCESS:
				if (shopflag) {
					dissDialog();
				}

				break;
			// 餐餐抢店铺信息成功
			case HANDLER_CCQSHOPDETAI_SUCCESS:
				//scrollView.onResfreshFinish();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				dissDialog();
				shopflag = true;
				tv_qc.setText(city);
				tv_lwzj.setText(lwzj);
				tv_qbfl.setText(leixing);
				tv_lwzj.setTextColor(Color.parseColor("#242424"));
				tv_qbfl.setTextColor(Color.parseColor("#242424"));
				tv_qc.setTextColor(Color.parseColor("#242424"));
				iv_lwzj.setBackgroundResource(R.drawable.xlgruy);
				iv_qbfl.setBackgroundResource(R.drawable.xlgruy);
				iv_qc.setBackgroundResource(R.drawable.xlgruy);
				lv.setVisibility(View.VISIBLE);
				CcqShopLvAdapter.NotifyList(ccqshopdetaiList);
				CcqShopLvAdapter.notifyDataSetChanged();
				tv_notdata.setVisibility(View.GONE);
				break;
			// 餐餐抢店铺信息失败
			case HANDLER_CCQSHOPDETAI_FAILURE:
				///scrollView.onResfreshFinish();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				dissDialog();
				tv_qc.setText(city);
				tv_lwzj.setText(lwzj);
				tv_qbfl.setText(leixing);
				tv_lwzj.setTextColor(Color.parseColor("#242424"));
				tv_qbfl.setTextColor(Color.parseColor("#242424"));
				tv_qc.setTextColor(Color.parseColor("#242424"));
				iv_lwzj.setBackgroundResource(R.drawable.xlgruy);
				iv_qbfl.setBackgroundResource(R.drawable.xlgruy);
				iv_qc.setBackgroundResource(R.drawable.xlgruy);
				if (sfyclv) {
//					lv.setVisibility(View.GONE);
					ccqshopdetaiList.clear();
					CcqShopLvAdapter.NotifyList(ccqshopdetaiList);
					CcqShopLvAdapter.notifyDataSetChanged();
					tv_notdata.setVisibility(View.VISIBLE);
				}
				Toast.makeText(CcqTagDetailActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_DINGWEI_FINISH:
				stopLocation();
				// 餐餐抢分类
				ccqFlHttpPost();
				// 餐餐抢店铺列表
				ccqshopHttpPost();
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
		setContentView(R.layout.activity_ccqtagdetail);

		//scrollView = (MyScrollView) findViewById(R.id.scrollView);
		
		View topView = LayoutInflater.from(this).inflate(R.layout.view_ccqtag_detail_list_top, null);
		
		LinearLayout line_back = (LinearLayout)topView. findViewById(R.id.line_back);
		tv_notdata = (TextView) topView.findViewById(R.id.tv_notdata);
		// 搜索
		LinearLayout line_search = (LinearLayout) topView.findViewById(R.id.line_search);
		et_search = (EditText) topView.findViewById(R.id.et_search);
		// 全部分类
		line_qbfl = (LinearLayout) topView.findViewById(R.id.line_qbfl);
		tv_qbfl = (TextView) topView.findViewById(R.id.tv_qbfl);
		iv_qbfl = (ImageView) topView.findViewById(R.id.iv_qbfl);
		// 全城
		line_qc = (LinearLayout) topView.findViewById(R.id.line_qc);
		tv_qc = (TextView) topView.findViewById(R.id.tv_qc);
		iv_qc = (ImageView) topView.findViewById(R.id.iv_qc);
		// 离我最近
		line_lwzj = (LinearLayout) topView.findViewById(R.id.line_lwzj);
		tv_lwzj = (TextView) topView.findViewById(R.id.tv_lwzj);
		iv_lwzj = (ImageView)topView. findViewById(R.id.iv_lwzj);
		// 当前位置
		tv_now_place = (TextView) topView.findViewById(R.id.tv_now_place);
		// 线条
		v_line = topView.findViewById(R.id.v_line);
		// 刷新
		LinearLayout line_shuaxin = (LinearLayout)topView. findViewById(R.id.line_shuaxin);
		// 店铺listview
		lv = (ListView) findViewById(R.id.lv);
		
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(CcqTagDetailActivity.this);
		
		refreshLayout = (MaterialRefreshLayout)findViewById(R.id.refresh_layout);
		refreshLayout.setLoadMore(true);
		ccqshopdetaiList = new ArrayList<Map<String, String>>();
		CcqShopLvAdapter = new CcqShopLvAdapter(CcqTagDetailActivity.this);
		lv.setAdapter(CcqShopLvAdapter);
		lv.addHeaderView(topView);

		showLoading();
		// 初始化定位
		initLocation();
		// 餐餐抢店铺列表
		// ccqshopHttpPost();
		if (getIntent() != null) {
			shi = getIntent().getStringExtra("city_id");
		}

		initListView();

		lv.setOnItemClickListener(this);
		line_back.setOnClickListener(this);
		line_qbfl.setOnClickListener(this);
		line_qc.setOnClickListener(this);
		line_lwzj.setOnClickListener(this);
		line_shuaxin.setOnClickListener(this);
		et_search.setOnEditorActionListener(this);

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
		locationClient = new AMapLocationClient(this.getApplicationContext());
		// 设置定位参数
		locationClient.setLocationOption(getDefaultOption());
		// 设置定位监听
		locationClient.setLocationListener(locationListener);

		// 定位
		startLocation();
	}

	// 餐餐抢分类接口请求
	private void ccqFlHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("city", shi);
		httpUtils.send(HttpMethod.POST, JiekouUtils.CCQFENLEI, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						ccqflJsonInfo(arg0.result);
					}
				});
	}

	// 解析餐餐抢分类接口返回的数据
	protected void ccqflJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				Gson gson = new Gson();
				ccqfenlei = gson.fromJson(result, CCQZhuBaseBean.class);
				handler.sendEmptyMessage(HANDLER_CCQFENLEI_SUCCESS);
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_CCQSHOPDETAI_FAILURE, message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 餐餐抢店铺列表
	protected void ccqshopHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("lat1", latitude);
		params.addBodyParameter("lng1", longitude);
		params.addBodyParameter("sc_id_1", getIntent()
				.getStringExtra("sc_id_1"));
		params.addBodyParameter("city_id", shi);
		params.addBodyParameter("page", nowPage + "");

		httpUtils.send(HttpMethod.POST, JiekouUtils.CCQSHOPLIST, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						ccqshopListInfo(arg0.result);
					}
				});
	}

	// 餐餐抢店铺列表数据解析
	protected void ccqshopListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				JSONArray array = object.getJSONArray("data");
				if (sfyclv) {
					ccqshopdetaiList.clear();
				}
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = array.getJSONObject(i);
					ccqshopDetailListInfo(json);
				}
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_CCQSHOPDETAI_FAILURE, message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 餐餐抢店铺详细
	private void ccqshopDetailListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String city_id = json.getString("city_id");
			String county_id = json.getString("county_id");
			String distance = json.getString("distance");
			String evaluate = json.getString("evaluate");
			String latitude = json.getString("latitude");
			String longitude = json.getString("longitude");
			String member_id = json.getString("member_id");
			String store_address = json.getString("store_address");
			String store_credit = json.getString("store_credit");
			String store_id = json.getString("store_id");
			String store_name = json.getString("store_name");
			String union_img = json.getString("union_img");
			String sales_num = json.getString("sales_num");
			String adv = json.getString("adv");
			String geval_scores = json.getString("geval_scores");
			Map<String, String> ccqshopDetailMap = new HashMap<String, String>();
			ccqshopDetailMap.put("city_id", city_id);
			ccqshopDetailMap.put("county_id", county_id);
			ccqshopDetailMap.put("distance", distance);
			ccqshopDetailMap.put("evaluate", evaluate);
			ccqshopDetailMap.put("latitude", latitude);
			ccqshopDetailMap.put("longitude", longitude);
			ccqshopDetailMap.put("member_id", member_id);
			ccqshopDetailMap.put("store_address", store_address);
			ccqshopDetailMap.put("store_credit", store_credit);
			ccqshopDetailMap.put("store_id", store_id);
			ccqshopDetailMap.put("store_name", store_name);
			ccqshopDetailMap.put("union_img", union_img);
			ccqshopDetailMap.put("sales_num", sales_num);
			ccqshopDetailMap.put("adv", adv);
			ccqshopDetailMap.put("geval_scores", geval_scores);
			ccqshopdetaiList.add(ccqshopDetailMap);
			handler.sendEmptyMessage(HANDLER_CCQSHOPDETAI_SUCCESS);
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
			erjiDialog();
			onBackPressed();
			break;
		case R.id.line_qbfl:
			if (qbflflag) {
				erjiDialog();
				showqbflDialog();
				qbflflag = false;
				qcflag = true;
				lwzjflag = true;
			} else {
				qbflflag = true;
				qcflag = true;
				lwzjflag = true;
				erjiDialog();
			}
			break;
		case R.id.line_qc:
			if (qcflag) {
				erjiDialog();
				showqcDialog();
				qcflag = false;
				qbflflag = true;
				lwzjflag = true;
			} else {
				qcflag = true;
				qbflflag = true;
				lwzjflag = true;
				erjiDialog();
			}
			break;
		case R.id.line_lwzj:
			if (lwzjflag) {
				erjiDialog();
				showlwzjDialog();
				lwzjflag = false;
				qbflflag = true;
				qcflag = true;
			} else {
				lwzjflag = true;
				qbflflag = true;
				qcflag = true;
				erjiDialog();
			}
			break;
		case R.id.tv_lwzj:
			erjiDialog();
			sfyclv = true;
			lwzj_popupWindow.dismiss();
			typehttppoString = "lwzj";
			nowPage = 1;
			showLoading();
			ccqshoplwzjHttpPost();
			break;
		case R.id.tv_hpyx:
			erjiDialog();
			sfyclv = true;
			lwzj_popupWindow.dismiss();
			typehttppoString = "hpyx";
			nowPage = 1;
			showLoading();
			ccqshophpyxHttpPost();
			break;
		case R.id.line_shuaxin:
			sfshuaxin  = true;
			tv_qc.setText("全城");
			tv_qbfl.setText("全部分类");
			showLoading();
			startLocation();
			break;

		default:
			break;
		}
	}

	private void showlwzjDialog() {
		// TODO Auto-generated method stub
		tv_lwzj.setTextColor(Color.parseColor("#e4393c"));
		tv_qbfl.setTextColor(Color.parseColor("#242424"));
		tv_qc.setTextColor(Color.parseColor("#242424"));
		iv_lwzj.setBackgroundResource(R.drawable.xlred);
		iv_qbfl.setBackgroundResource(R.drawable.xlgruy);
		iv_qc.setBackgroundResource(R.drawable.xlgruy);

		View lwziview = View.inflate(CcqTagDetailActivity.this,
				R.layout.popupwindow_ccq_lwzj, null);
		TextView tv_lwzj = (TextView) lwziview.findViewById(R.id.tv_lwzj);
		TextView tv_hpyx = (TextView) lwziview.findViewById(R.id.tv_hpyx);

		tv_lwzj.setOnClickListener(this);
		tv_hpyx.setOnClickListener(this);

		lwzj_popupWindow = new PopupWindow(lwziview, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		lwzj_popupWindow.setTouchable(true);
		lwzj_popupWindow.setOutsideTouchable(false);
		lwzj_popupWindow.setFocusable(false);
		lwzj_popupWindow.setBackgroundDrawable(new BitmapDrawable());
		lwzj_popupWindow.showAsDropDown(v_line);
	}

	private void showqcDialog() {
		// TODO Auto-generated method stub
		tv_qc.setTextColor(Color.parseColor("#e4393c"));
		tv_qbfl.setTextColor(Color.parseColor("#242424"));
		tv_lwzj.setTextColor(Color.parseColor("#242424"));
		iv_qc.setBackgroundResource(R.drawable.xlred);
		iv_qbfl.setBackgroundResource(R.drawable.xlgruy);
		iv_lwzj.setBackgroundResource(R.drawable.xlgruy);

		View qcview = View.inflate(CcqTagDetailActivity.this,
				R.layout.popupwindow_ccq_qc, null);

		lv_city = (ListView) qcview.findViewById(R.id.lv_city);

		CCQCityAdapter ccqcityadapter = new CCQCityAdapter(
				CcqTagDetailActivity.this);
		lv_city.setAdapter(ccqcityadapter);
		ccqcityadapter.NotifyList(ccqfenlei.getData().getArea());
		lv_city.setOnItemClickListener(this);

		qc_popupWindow = new PopupWindow(qcview, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		qc_popupWindow.setTouchable(true);
		qc_popupWindow.setOutsideTouchable(false);
		qc_popupWindow.setFocusable(false);
		qc_popupWindow.setBackgroundDrawable(new BitmapDrawable());
		qc_popupWindow.showAsDropDown(v_line);
	}

	private void showqbflDialog() {
		// TODO Auto-generated method stub
		tv_qbfl.setTextColor(Color.parseColor("#e4393c"));
		tv_lwzj.setTextColor(Color.parseColor("#242424"));
		tv_qc.setTextColor(Color.parseColor("#242424"));
		iv_qbfl.setBackgroundResource(R.drawable.xlred);
		iv_lwzj.setBackgroundResource(R.drawable.xlgruy);
		iv_qc.setBackgroundResource(R.drawable.xlgruy);

		View view = View.inflate(CcqTagDetailActivity.this,
				R.layout.popupwindow_ccq_qbfl, null);

		lv_one = (ListView) view.findViewById(R.id.lv_one);
		lv_two = (ListView) view.findViewById(R.id.lv_two);

		ccqfenleioneadapter = new CCQfenleioneAdapter(CcqTagDetailActivity.this);
		lv_one.setAdapter(ccqfenleioneadapter);
		ccqfenleitwoadapter = new CCQfenleitwoAdapter(CcqTagDetailActivity.this);
		lv_two.setAdapter(ccqfenleitwoadapter);
		ccqfenleioneadapter.NotifyList(ccqfenlei.getData().getClassify());
		ccqfenleitwoadapter.NotifyList(ccqfenlei.getData().getClassify().get(0)
				.getClass2());
		lv_one.setOnItemClickListener(this);
		lv_two.setOnItemClickListener(this);
		qbfl_popupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		qbfl_popupWindow.setTouchable(true);
		qbfl_popupWindow.setOutsideTouchable(false);
		qbfl_popupWindow.setFocusable(false);
		qbfl_popupWindow.setBackgroundDrawable(new BitmapDrawable());
		qbfl_popupWindow.showAsDropDown(v_line);
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
				 String result = LocationUtils.getLocationStr(loc);
				if (loc.getErrorCode() == 0) {
					// 经 度
					longitude = loc.getLongitude() + "";
					// 纬 度
					latitude = loc.getLatitude() + "";
					// 市
					String allshi = loc.getCity();
					if (sfshuaxin == true) {
						shi = allshi.replaceAll("市", "");
					}
					String street = loc.getStreet();
					tv_now_place.setText(allshi + street);
				} else {
					shi = getIntent().getStringExtra("city_id");
					// 定位失败
					Toast.makeText(CcqTagDetailActivity.this, "定位失败",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				shi = getIntent().getStringExtra("city_id");
				Toast.makeText(CcqTagDetailActivity.this, "定位失败，loc is null",
						Toast.LENGTH_SHORT).show();
			}
			handler.sendEmptyMessage(HANDLER_DINGWEI_FINISH);
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

	// 加载中动画
	private Dialog loadingDialog;
	private ZhuBBsearchBean bbsearch;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(CcqTagDetailActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				CcqTagDetailActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(CcqTagDetailActivity.this,
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
	public void onItemClick(AdapterView<?> parent, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		int index =  position - 1;
		if (parent == lv) {
			erjiDialog();
//			Intent intent = new Intent(CcqTagDetailActivity.this,
//					CCQStoreDetailActivity.class);
			Intent intent = new Intent(CcqTagDetailActivity.this,
					OldCCQStoreDetailActivity.class);
			intent.putExtra("store_id",
					ccqshopdetaiList.get(index).get("store_id"));
			startActivity(intent);
		} else if (parent == lv_one) {
			if (ccqfenlei.getData().getClassify().get(position).getClass2() == null) {
				Toast.makeText(CcqTagDetailActivity.this, "暂无数据",
						Toast.LENGTH_SHORT).show();
			} else {
				zu = position;
				ccqfenleitwoadapter.NotifyList(ccqfenlei.getData()
						.getClassify().get(position).getClass2());
				ccqfenleitwoadapter.notifyDataSetChanged();
			}
		} else if (parent == lv_two) {
			qbfl_popupWindow.dismiss();
			showLoading();
			sc_id_2 = ccqfenlei.getData().getClassify().get(zu).getClass2()
					.get(position).getGc_id();
			leixing = ccqfenlei.getData().getClassify().get(zu).getClass2()
					.get(position).getGc_name();
			nowPage = 1;
			sfyclv = true;
			typehttppoString = "fl";
			ccqshopflHttpPost(sc_id_2);
		} else if (parent == lv_city) {
			qc_popupWindow.dismiss();
			showLoading();
			area_id = ccqfenlei.getData().getArea().get(position).getArea_id();
			city = ccqfenlei.getData().getArea().get(position).getArea_name();
			nowPage = 1;
			sfyclv = true;
			typehttppoString = "qy";
			ccqshopqyHttpPost(area_id);
		}
	}

	// 关闭加载dialog
	private void erjiDialog() {
		tv_lwzj.setTextColor(Color.parseColor("#242424"));
		tv_qbfl.setTextColor(Color.parseColor("#242424"));
		tv_qc.setTextColor(Color.parseColor("#242424"));
		iv_lwzj.setBackgroundResource(R.drawable.xlgruy);
		iv_qbfl.setBackgroundResource(R.drawable.xlgruy);
		iv_qc.setBackgroundResource(R.drawable.xlgruy);
		if (qbfl_popupWindow != null && qbfl_popupWindow.isShowing()) {
			qbfl_popupWindow.dismiss();
		}
		if (qc_popupWindow != null && qc_popupWindow.isShowing()) {
			qc_popupWindow.dismiss();
		}
		if (lwzj_popupWindow != null && lwzj_popupWindow.isShowing()) {
			lwzj_popupWindow.dismiss();
		}
	}

	// 餐餐抢点击离我最近店铺列表
	protected void ccqshoplwzjHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("lat1", latitude);
		params.addBodyParameter("lng1", longitude);
		params.addBodyParameter("order", "0");
		params.addBodyParameter("city_id", shi.trim());
		params.addBodyParameter("page", nowPage + "");
		lwzj = "离我最近";
		httpUtils.send(HttpMethod.POST, JiekouUtils.CCQSHOPLIST, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						ccqshopListInfo(arg0.result);
					}
				});
	}

	// 餐餐抢点击好评优先店铺列表
	protected void ccqshophpyxHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("lat1", latitude);
		params.addBodyParameter("lng1", longitude);
		params.addBodyParameter("city_id", shi.trim());
		params.addBodyParameter("order", "1");
		params.addBodyParameter("page", nowPage + "");
		lwzj = "好评优先";
		httpUtils.send(HttpMethod.POST, JiekouUtils.CCQSHOPLIST, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						ccqshopListInfo(arg0.result);
					}
				});
	}

	// 餐餐抢点击区域店铺列表
	protected void ccqshopqyHttpPost(String area_id) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("lat1", latitude);
		params.addBodyParameter("lng1", longitude);
		params.addBodyParameter("city_id", shi.trim());
		params.addBodyParameter("area_id", area_id);
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.CCQSHOPLIST, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						ccqshopListInfo(arg0.result);
					}
				});
	}

	// 餐餐抢点击分类店铺列表
	protected void ccqshopflHttpPost(String sc_id_2) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("lat1", latitude);
		params.addBodyParameter("lng1", longitude);
		params.addBodyParameter("city_id", shi.trim());
		params.addBodyParameter("sc_id_2", sc_id_2);
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.CCQSHOPLIST, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						ccqshopListInfo(arg0.result);
					}
				});
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent e) {
		// TODO Auto-generated method stub

		/* 判断是否是“下一步”键 */
		if (actionId == EditorInfo.IME_ACTION_DONE
				|| actionId == EditorInfo.IME_ACTION_GO
				|| actionId == EditorInfo.IME_ACTION_SEARCH) {
			/* 隐藏软键盘 */
			InputMethodManager imm = (InputMethodManager) v.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
				imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
			}
			searchtext = et_search.getText().toString().trim();
			if (TextUtils.isEmpty(searchtext)) {
				Toast.makeText(CcqTagDetailActivity.this, "搜索文本不能为空",
						Toast.LENGTH_SHORT).show();
			} else {
				typehttppoString = "search";
				nowPage = 1;
				showLoading();
				sfyclv = true;
				searchccqHttppost(searchtext);
			}

			return true;
		}

		return false;
	}

	// 搜索接口请求（宝贝）
	protected void searchccqHttppost(String text) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("lat1", latitude);
		params.addBodyParameter("lng1", longitude);
		params.addBodyParameter("keyword", text);
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.CCQSHOPLIST, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						ccqshopListInfo(arg0.result);
					}
				});
	}

	private boolean sfyclv;
	private String sc_id_2;
	private String area_id;
	private String searchtext;
	private boolean sfshuaxin = false;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	/**
	 * @description 填充底部商品listview
	 **/
	private void initListView() {
		
		refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
			
			@Override
			public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
				// TODO Auto-generated method stub
				//这里刷新 
				refreshLayout.finishRefresh();
			}
			
			@Override
			public void onRefreshLoadMore(
					MaterialRefreshLayout materialRefreshLayout) {
				// TODO Auto-generated method stub
				//super.onRefreshLoadMore(materialRefreshLayout);
				try {
					nowPage++;
					sfyclv = false;
					if ("dp".equals(typehttppoString)) {
						ccqshopHttpPost();
					} else if ("lwzj".equals(typehttppoString)) {
						ccqshoplwzjHttpPost();
					} else if ("hpyx".equals(typehttppoString)) {
						ccqshophpyxHttpPost();
					} else if ("fl".equals(typehttppoString)) {
						ccqshopflHttpPost(sc_id_2);
					} else if ("qy".equals(typehttppoString)) {
						ccqshopqyHttpPost(area_id);
					} else if ("search".equals(typehttppoString)) {
						searchccqHttppost(searchtext);
					}
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(HANDLER_NN_FAILURE);
				}
			}
			
		
			
		});
		
//		scrollView.getView();
//		scrollView.setOnRefreshListener(new OnRefreshListener() {
//
//			@Override
//			public void onRefresh() {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onLoadMoring() {
//				// TODO Auto-generated method stub
//				try {
//					nowPage++;
//					sfyclv = false;
//					if ("dp".equals(typehttppoString)) {
//						ccqshopHttpPost();
//					} else if ("lwzj".equals(typehttppoString)) {
//						ccqshoplwzjHttpPost();
//					} else if ("hpyx".equals(typehttppoString)) {
//						ccqshophpyxHttpPost();
//					} else if ("fl".equals(typehttppoString)) {
//						ccqshopflHttpPost(sc_id_2);
//					} else if ("qy".equals(typehttppoString)) {
//						ccqshopqyHttpPost(area_id);
//					} 
////					else if ("search".equals(typehttppoString)) {
////						searchccqHttppost(searchtext);
////					}
//				} catch (Exception e) {
//					// TODO: handle exception
//					handler.sendEmptyMessage(HANDLER_NN_FAILURE);
//				}
//
//			}
//		});
	}
}
