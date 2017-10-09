package ccj.yun28.com.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.util.v;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import ccj.yun28.com.BindPhoneActivtiy;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.Near_nereaAdapter;
import ccj.yun28.com.adapter.CCQCityAdapter;
import ccj.yun28.com.adapter.CCQStringAdapter;
import ccj.yun28.com.adapter.CcqStoreLvAdapter;
import ccj.yun28.com.bean.Fenji_dizhibean;
import ccj.yun28.com.bean.ZhuFenji_dizhibean;
import ccj.yun28.com.bean.ZhuSignBean;
import ccj.yun28.com.ccq.CCQStoreDetailActivity;
import ccj.yun28.com.ccq.CcqTagDetailActivity;
import ccj.yun28.com.ccq.NewBaWangCanActivity;
import ccj.yun28.com.ccq.OldCCQStoreDetailActivity;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.lunbotu.ImageCycleView;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 附近
 * 
 * @author meihuali
 * 
 */
public class NearFragment extends Fragment implements OnItemClickListener,
		OnClickListener {
	private MaterialRefreshLayout refreshLayout;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;
	private List<Map<String, String>> fuJinList;// 附近
	private String[] smartList = new String[] { "距离最近", "人气最旺", "销售最高" };// 智能选择
	private String[] chooseList = new String[] { "全部", "距离小于1千米", "距离小于2千米",
			"距离大于2千米" };// 筛选
	private int nowPage = 1;
	private View v_line;

	private TextView tv_address;
	private ZhuFenji_dizhibean fenji_dizhibean;
	// 区id
	private String narea_q = "";
	// 距离
	private String chooseNum = "";
	// 智能筛选
	private String smartNum = "";
	// 选择街
	private String chooseStreetId = "";
	// 选择区
	private String chooseQuId = "";
	private String sc_id_1 = "";
	// 经 度
	private String longitude = "";
	// 纬 度
	private String latitude = "";
	// 街道
	private String street = "";
	// 区
	private String qu = "";
	// 市
	private String shi = "";
	// 定位
	private AMapLocationClient locationClient = null;
	private AMapLocationClientOption locationOption = new AMapLocationClientOption();

	private LinearLayout ll_empty;
	private TextView tv_refresh;
	private RadioButton radio_qu;
	private RadioButton radio_street;
	private RadioButton radio_smart;
	private RadioButton radio_range;
	private RadioButton radio_qt;
	// 没有网络
	protected static final int HANDLER_NET_FAILURE = 0;
	// 网络错误
	private static final int HANDLER_NN_FAILURE = 1;
	// token无效
	private static final int HANDLER_TOKEN_FAILURE = 2;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 3;
	// 获取信息成功
	private static final int HANDLER_FUJIN_SUCCESS = 4;
	// 没有商家
	private static final int HANDLER_NOMORE_MSG = 5;
	// 定位成功
	protected static final int HANDLER_DINGWEI_SUCCESS = 6;
	// 定位失败，定位完成
	protected static final int HANDLER_DINGWEI_FINISH = 7;
	// 获取分级地址成功
	private static final int HANDLER_NEREA_SUCCESS = 8;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				// line_net_error.setVisibility(View.VISIBLE);
				if (getActivity() != null) {

					Toast.makeText(getActivity(), "当前网络不可用,请检查网络",
							Toast.LENGTH_SHORT).show();
				}
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				// line_net_error.setVisibility(View.VISIBLE);
				if (getActivity() != null) {
					Toast.makeText(getActivity(), "当前网络出错,请检查网络",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				// line_net_error.setVisibility(View.VISIBLE);
				if (getActivity() != null) {
					if (msg.obj != null) {
						Toast.makeText(getActivity(),
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();
					}
				}
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				if (msg.obj != null) {
					Toast.makeText(getActivity(), msg.obj.toString().trim(),
							Toast.LENGTH_SHORT).show();
				}
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "near");
				startActivity(intent);
				break;
			case HANDLER_DINGWEI_SUCCESS:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				radio_qu.setText("选择");
				radio_street.setText("选择");
				clicktype = 0;
				radio_qt.setChecked(true);
				chooseStreetId = "";
				chooseQuId = "";
				nowPage = 1;
				fuJinHttpPost();
				stopLocation();
				break;
			case HANDLER_DINGWEI_FINISH:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				nowPage = 1;
				fuJinHttpPost();
				stopLocation();
				tv_address.setText("定位失败，点击重新定位");
				break;
			// 附近成功
			case HANDLER_FUJIN_SUCCESS:
				dissDialog();

				if (fuJinList.size() > 0) {
					ll_empty.setVisibility(View.GONE);
				} else {
					ll_empty.setVisibility(View.VISIBLE);
				}

				/*
				 * if ("juan".equals(type) && fuJinList.size() > 0) {
				 * ll_empty.setVisibility(View.GONE); } else if
				 * ("shop".equals(type) && fuJinList.size() > 0) {
				 * ll_empty.setVisibility(View.GONE); } else {
				 * ll_empty.setVisibility(View.VISIBLE); }
				 */

				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				ccqStoreLvAdapter.NotifyList(fuJinList);
				ccqStoreLvAdapter.notifyDataSetChanged();
				break;
			// 没有数据
			case HANDLER_NOMORE_MSG:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				ccqStoreLvAdapter.NotifyList(fuJinList);
				ccqStoreLvAdapter.notifyDataSetChanged();
				if (msg.obj != null) {
					Toast.makeText(getActivity(), msg.obj.toString().trim(),
							Toast.LENGTH_SHORT).show();
				}
				break;
			// 获取区/街道成功
			case HANDLER_NEREA_SUCCESS:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				if (popupWindow != null && popupWindow.isShowing()) {
					// 更新数据
					lv_xiala.setAdapter(ccqbaseadapter);
					ccqbaseadapter.NotifyList(fenji_dizhibean.getData());
					ccqbaseadapter.notifyDataSetChanged();
				} else {
					showPopupWindow();
				}

				break;
			default:
				break;
			}
		};
	};
	private boolean isCreateView = false;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_near, null);

		tv_refresh = (TextView) view.findViewById(R.id.tv_refresh);
		ll_empty = (LinearLayout) view.findViewById(R.id.ll_empty);
		tv_address = (TextView) view.findViewById(R.id.tv_address);
		TextView tv_fjsto = (TextView) view.findViewById(R.id.tv_fjsto);
		TextView tv_food = (TextView) view.findViewById(R.id.tv_food);
		TextView tv_hotel = (TextView) view.findViewById(R.id.tv_hotel);
		TextView tv_relax = (TextView) view.findViewById(R.id.tv_relax);
		TextView tv_beauty = (TextView) view.findViewById(R.id.tv_beauty);
		radio_qu = (RadioButton) view.findViewById(R.id.radio_qu);
		radio_street = (RadioButton) view.findViewById(R.id.radio_street);
		radio_smart = (RadioButton) view.findViewById(R.id.radio_smart);
		radio_range = (RadioButton) view.findViewById(R.id.radio_range);
		radio_qt = (RadioButton) view.findViewById(R.id.radio_qt);
		v_line = (View) view.findViewById(R.id.v_line);

		lv = (ListView) view.findViewById(R.id.lv);
		lv.setEmptyView(ll_empty);
		refreshLayout = (MaterialRefreshLayout) view
				.findViewById(R.id.refresh_layout);
		ccqStoreLvAdapter = new CcqStoreLvAdapter(getActivity());
		lv.setAdapter(ccqStoreLvAdapter);
		refreshLayout.setLoadMore(true);

		tv_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setRefresh();// 重新刷新数据
			}
		});

		httpUtils = new HttpUtils();
		Utils utils = new Utils();
		verstring = utils.getVersionInfo(getActivity());
		fuJinList = new ArrayList<Map<String, String>>();

		showLoading();
		initLocation();

		isCreateView = true;

		tv_address.setOnClickListener(this);
		tv_fjsto.setOnClickListener(this);
		tv_food.setOnClickListener(this);
		tv_hotel.setOnClickListener(this);
		tv_relax.setOnClickListener(this);
		tv_beauty.setOnClickListener(this);
		radio_qu.setOnClickListener(this);
		radio_street.setOnClickListener(this);
		radio_smart.setOnClickListener(this);
		radio_range.setOnClickListener(this);
		lv.setOnItemClickListener(this);

		initListView();

		return view;
	}

	public void setIsCreateView() {
		if (isCreateView) {
			showLoading();
			fuJinHttpPost();
		}
	}

	/**
	 * 重新刷新数据
	 */
	private void setRefresh() {
		dissPopupWindow();
		clicktype = 0;
		citytype = 0;
		narea_q = "";
		nowPage = 1;
		chooseQuId = "";
		chooseStreetId = "";
		smartNum = "";
		chooseNum = "";
		radio_qt.setChecked(true);
		radio_qu.setChecked(false);
		radio_street.setChecked(false);
		radio_smart.setChecked(false);
		radio_range.setChecked(false);
		radio_qu.setText("选择");
		radio_street.setText("选择");
		radio_smart.setText("智能选择");
		radio_range.setText("筛选");
		showLoading();
		fuJinHttpPost();
	}

	/**
	 * 获取附近商家请求接口
	 */
	private void fuJinHttpPost() {
		String selector_city = SharedUtil.getStringValue(
				SharedCommon.SELECTOR_CITY_ID, "");

		// Toast.makeText(getActivity(), "重新加载", Toast.LENGTH_SHORT).show();
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("lat1", latitude);
		params.addBodyParameter("lng1", longitude);
		// 只获取当前定位城市商品时传
		params.addBodyParameter("city_id", shi);

		// 需要获取手动定位城市商品时传
		// params.addBodyParameter("city_id",
		// shi.equals(selector_city) || "".equals(shi) ? shi
		// : selector_city);

		params.addBodyParameter("narea_q", chooseQuId);// 限制查询的区/县
		params.addBodyParameter("narea_z", chooseStreetId);// 限制查询的街道/镇
		params.addBodyParameter("sc_id_1", sc_id_1);// 1058: 美食 1003: 酒店 1065:
													// 丽人 1060: 休闲娱乐
		params.addBodyParameter("order", smartNum);// 排序 0 离我最近 1 好评优先 2 销售量
		params.addBodyParameter("distance", chooseNum);// 距离限制 0：全部 1：1千米内
														// 2:1-2千米 3:2+千米
		params.addBodyParameter("page", nowPage + "");

		params.addBodyParameter("goods_images_op", "!m");
		params.addBodyParameter("goods_images_size", "750x350");

		httpUtils.send(HttpMethod.POST, JiekouUtils.CCQSHOPLIST, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						fuJinJsonInfo(arg0.result);

					}
				});

	}

	protected void fuJinJsonInfo(String result) {
		try {

			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				if (nowPage == 1) {
					fuJinList.clear();
				}
				JSONArray array = object.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = array.getJSONObject(i);
					ccqshopDetailListInfo(json);
				}

			} else if ("300".equals(status)) {
				String message = object.getString("message");
				if (nowPage == 1) {
					// lv.setVisibility(View.GONE);
					fuJinList.clear();
				}
				handler.sendMessage(handler.obtainMessage(HANDLER_NOMORE_MSG,
						message));
			} else if ("700".equals(status)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TOKEN_FAILURE, message));
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

	private void ccqshopDetailListInfo(JSONObject json) {
		try {
			String adv = json.getString("adv");
			String city_id = json.getString("city_id");
			String county_id = json.getString("county_id");
			String distance = json.getString("distance");
			String distance_value = json.getString("distance_value");
			String evaluate = json.getString("evaluate");
			String geval_scores = json.getString("geval_scores");
			String latitude = json.getString("latitude");
			String longitude = json.getString("longitude");
			String member_id = json.getString("member_id");
			String sales_num = json.getString("sales_num");
			String store_address = json.getString("store_address");
			String store_credit = json.getString("store_credit");
			String store_id = json.getString("store_id");
			String store_name = json.getString("store_name");
			String union_img = json.getString("union_img");
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
			ccqshopDetailMap.put("distance_value", distance_value);
			ccqshopDetailMap.put("adv", adv);
			ccqshopDetailMap.put("geval_scores", geval_scores);
			fuJinList.add(ccqshopDetailMap);
			handler.sendEmptyMessage(HANDLER_FUJIN_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_fjsto:
			dissPopupWindow();
			clicktype = 0;
			radio_qt.setChecked(true);
			sc_id_1 = "";
			nowPage = 1;
			showLoading();
			fuJinHttpPost();
			break;
		case R.id.tv_food:// 美食
			dissPopupWindow();
			clicktype = 0;
			radio_qt.setChecked(true);
			sc_id_1 = "1058";
			nowPage = 1;
			showLoading();
			fuJinHttpPost();

			break;
		case R.id.tv_hotel:
			dissPopupWindow();
			clicktype = 0;
			radio_qt.setChecked(true);
			sc_id_1 = "1113";
			nowPage = 1;
			showLoading();
			fuJinHttpPost();

			break;
		case R.id.tv_relax:// 休闲
			dissPopupWindow();
			clicktype = 0;
			radio_qt.setChecked(true);
			sc_id_1 = "1060";
			nowPage = 1;
			showLoading();
			fuJinHttpPost();

			break;
		case R.id.tv_beauty:
			dissPopupWindow();
			clicktype = 0;
			radio_qt.setChecked(true);
			sc_id_1 = "1065";
			nowPage = 1;
			showLoading();
			fuJinHttpPost();

			break;
		case R.id.radio_qu:
			if (clicktype == 1) {
				dissPopupWindow();
				clicktype = 0;
				radio_qt.setChecked(true);
			} else {
				clicktype = 1;
				radio_qu.setChecked(true);
				citytype = 2;
				showLoading();
				getNereaHttpPost();

			}
			break;
		case R.id.radio_street:
			if (TextUtils.isEmpty(narea_q)) {
				dissPopupWindow();
				clicktype = 0;
				radio_qt.setChecked(true);
				Toast.makeText(getActivity(), "请先选择区", Toast.LENGTH_SHORT)
						.show();
			} else {
				if (clicktype == 2) {
					dissPopupWindow();
					clicktype = 0;
					radio_qt.setChecked(true);
				} else {
					clicktype = 2;
					radio_street.setChecked(true);
					citytype = 0;
					showLoading();
					getNereaHttpPost();
				}
			}
			break;
		case R.id.radio_smart:

			if (clicktype == 3) {
				dissPopupWindow();
				clicktype = 0;
				radio_smart.setChecked(false);
			} else {
				clicktype = 3;
				radio_smart.setChecked(true);
				if (popupWindow != null && popupWindow.isShowing()) {
					// 更新数据
					lv_xiala.setAdapter(ccqStringadapter);
					ccqStringadapter.NotifyList(smartList);
					ccqStringadapter.notifyDataSetChanged();
				} else {
					showPopupWindow();
				}
			}

			break;
		case R.id.radio_range:
			if (clicktype == 4) {
				dissPopupWindow();
				clicktype = 0;
				radio_range.setChecked(false);
			} else {
				clicktype = 4;
				radio_range.setChecked(true);
				if (popupWindow != null && popupWindow.isShowing()) {
					// 更新数据
					lv_xiala.setAdapter(ccqStringadapter);
					ccqStringadapter.NotifyList(chooseList);
					ccqStringadapter.notifyDataSetChanged();
				} else {
					showPopupWindow();
				}
			}

			break;
		case R.id.tv_address:
			showLoading();
			startLocation();
			break;

		default:
			break;
		}
	}

	// 获取分级地址
	private void getNereaHttpPost() {
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("type", citytype + "");// 0:根据上级获取下级 1:获取省级列表
		params.addBodyParameter("city_id", shi);
		params.addBodyParameter("narea_p", "");// 根据省级id获取下属一级列表
		params.addBodyParameter("narea_s", "");// 根据市级id获取下属一级列表
		params.addBodyParameter("narea_q", narea_q);// 根据区级id获取下属一级列表
		httpUtils.send(HttpMethod.POST, JiekouUtils.FUJIN_NEREA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						nereaJsonInfo(arg0.result);
					}
				});
	}

	protected void nereaJsonInfo(String result) {
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				Gson gson = new Gson();
				fenji_dizhibean = gson.fromJson(result,
						ZhuFenji_dizhibean.class);
				handler.sendEmptyMessage(HANDLER_NEREA_SUCCESS);
			} else if ("700".equals(status)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TOKEN_FAILURE, message));
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

	private void showPopupWindow() {

		View view = View.inflate(getActivity(), R.layout.popupwindow_ccq_qc,
				null);

		lv_xiala = (ListView) view.findViewById(R.id.lv_city);

		ccqStringadapter = new CCQStringAdapter(getActivity());
		ccqbaseadapter = new Near_nereaAdapter(getActivity());
		if (clicktype == 3 || clicktype == 4) {
			lv_xiala.setAdapter(ccqStringadapter);
			if (clicktype == 3) {
				ccqStringadapter.NotifyList(smartList);
			} else {
				ccqStringadapter.NotifyList(chooseList);
			}
		} else {
			lv_xiala.setAdapter(ccqbaseadapter);
			ccqbaseadapter.NotifyList(fenji_dizhibean.getData());
		}
		lv_xiala.setOnItemClickListener(this);

		popupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		popupWindow.setTouchable(true);

		// popupWindow.setOutsideTouchable(false);
		// popupWindow.setFocusable(false);
		// popupWindow.setBackgroundDrawable(new BitmapDrawable());

		ColorDrawable dw = new ColorDrawable(0000000000);// 实例化一个ColorDrawable颜色为半透明
		popupWindow.setBackgroundDrawable(dw);// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener
												// ，设置其他控件变化等操作
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				clicktype = 0;
				radio_qt.setChecked(true);
				radio_qu.setChecked(false);
				radio_street.setChecked(false);
				radio_smart.setChecked(false);
				radio_range.setChecked(false);
				/*
				 * if (StringUtil.isEmpty(chooseQuId)) {
				 * radio_qu.setChecked(false); } if
				 * (StringUtil.isEmpty(chooseStreetId)) {
				 * radio_street.setChecked(false); } if
				 * (StringUtil.isEmpty(smartNum)) {
				 * radio_smart.setChecked(false); } if
				 * (StringUtil.isEmpty(chooseNum)) {
				 * radio_range.setChecked(false); }
				 */

				// Toast.makeText(getActivity(), "空白位置关闭",
				// Toast.LENGTH_SHORT).show();
			}
		});

		popupWindow.showAsDropDown(v_line);
	}

	/**
	 * @description 填充底部商品listview
	 **/
	private void initListView() {
		refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {

			@Override
			public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
				// zheli shua xin
				refreshLayout.finishRefresh();
			}

			@Override
			public void onRefreshLoadMore(
					MaterialRefreshLayout materialRefreshLayout) {
				// super.onRefreshLoadMore(materialRefreshLayout);
				try {
					nowPage++;
					fuJinHttpPost();
				} catch (Exception e) {
					handler.sendEmptyMessage(HANDLER_NN_FAILURE);
				}
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position,
			long arg3) {
		if (parent == lv) {
			Intent intent = new Intent(getActivity(),
					CCQStoreDetailActivity.class);
			intent.putExtra("starlat", latitude);
			intent.putExtra("starlng", longitude);
			intent.putExtra("store_id", fuJinList.get(position).get("store_id"));
			startActivity(intent);
		} else {
			if (clicktype == 1) {// 区
				narea_q = fenji_dizhibean.getData().get(position).getId();
				String chooseQu = fenji_dizhibean.getData().get(position)
						.getName();
				chooseQuId = fenji_dizhibean.getData().get(position).getId();
				radio_qu.setText(chooseQu);
				radio_street.setText("选择");
				chooseStreetId = "";
			} else if (clicktype == 2) {// 街
				String chooseStreet = fenji_dizhibean.getData().get(position)
						.getName();
				chooseStreetId = fenji_dizhibean.getData().get(position)
						.getId();
				radio_street.setText(chooseStreet);
			} else if (clicktype == 3) {// 智能
				String smartText = smartList[position];
				smartNum = position + "";
				radio_smart.setText(smartText);
			} else if (clicktype == 4) {// 筛选
				String chooseText = chooseList[position];
				chooseNum = position + "";
				radio_range.setText(chooseText);
			}
			nowPage = 1;
			showLoading();
			fuJinHttpPost();
		}

		dissPopupWindow();
		dissPopupWindow();
		clicktype = 0;
		radio_qt.setChecked(true);
	}

	// 加载中动画
	private Dialog loadingDialog;
	private PopupWindow popupWindow;
	// 点击选项
	private int clicktype = 0;
	// 市区选择
	private int citytype = 0;
	private CCQStringAdapter ccqStringadapter;
	private ListView lv_xiala;
	private Near_nereaAdapter ccqbaseadapter;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(getActivity(), R.style.mDialogStyle);
		loadingDialog.setContentView(view);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.show();
	}

	// 关闭加载dialog
	private void dissDialog() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		} else if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}

	// 关闭加载popupWindow
	public void dissPopupWindow() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}

	/**
	 * 初始化定位
	 * 
	 */
	private void initLocation() {
		// 初始化client
		locationClient = new AMapLocationClient(getActivity()
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
					// 市
					String fshi = loc.getCity();
					if (!TextUtils.isEmpty(fshi)) {
						shi = fshi.replaceAll("市", "");
					}
					tv_address.setText(loc.getDistrict() + loc.getStreet());
					handler.sendEmptyMessage(HANDLER_DINGWEI_SUCCESS);

					SharedUtil.saveStringValue(SharedCommon.ADDRESS,
							loc.getAddress());
					SharedUtil.saveStringValue(SharedCommon.LATITUDE,
							loc.getLatitude() + "");
					SharedUtil.saveStringValue(SharedCommon.LONGITUDE,
							loc.getLongitude() + "");
					SharedUtil
							.saveStringValue(SharedCommon.CITY, loc.getCity());
				} else {
					// 定位失败
					Toast.makeText(getActivity(), "正在定位中", Toast.LENGTH_SHORT)
							.show();
					handler.sendEmptyMessage(HANDLER_DINGWEI_FINISH);
				}
			} else {
				Toast.makeText(getActivity(), "正在定位中", Toast.LENGTH_SHORT)
						.show();
				handler.sendEmptyMessage(HANDLER_DINGWEI_FINISH);
			}
		}
	};

	private ListView lv;
	private CcqStoreLvAdapter ccqStoreLvAdapter;

	/**
	 * 开始定位
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
	 */
	private void stopLocation() {
		// 停止定位
		locationClient.stopLocation();
	}

	/**
	 * 销毁定位
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

	@Override
	public void onResume() {
		super.onResume();
	}
}
