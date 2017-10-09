package ccj.yun28.com.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.RecmmendGoodsActivity;
import ccj.yun28.com.adapter.CcqQuanSYLvAdapter;
import ccj.yun28.com.adapter.CcqStoreSYLvAdapter;
import ccj.yun28.com.bean.newbean.UnReadMassageBean;
import ccj.yun28.com.ccq.CCQStoreDetailActivity;
import ccj.yun28.com.ccq.ChooseCityActivity;
import ccj.yun28.com.ccq.NewBaWangCanActivity;
import ccj.yun28.com.ccq.NewCcqProDetailActivity;
import ccj.yun28.com.ccq.NewChaoJiQuanActivity;
import ccj.yun28.com.ccq.NewHuiyuanGetVipActivity;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.json.Common;
import ccj.yun28.com.listener.OnDoubleDialogListener;
import ccj.yun28.com.lunbotu.ADInfo;
import ccj.yun28.com.lunbotu.ImageCycleView;
import ccj.yun28.com.lunbotu.ImageCycleView.ImageCycleViewListener;
import ccj.yun28.com.mine.NewOpenStoreActivtiy;
import ccj.yun28.com.sy.MessageActivcity;
import ccj.yun28.com.sy.SaoYiSaoActivity;
import ccj.yun28.com.sy.SearchActivity;
import ccj.yun28.com.utils.CheckUpdate;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.DialogUtil;
import ccj.yun28.com.utils.ImageUtil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol;
import com.amap.api.location.AMapLocationListener;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 餐餐抢页--黑色版本
 * 
 * @author meihuali
 * 
 */
public class CCQFragment extends Fragment implements OnClickListener {
	private ListView lv;
	private Intent intent;
	private MaterialRefreshLayout refreshLayout;
	// 没有数据时显示
	private LinearLayout line_net_error;
	// 显示的是店铺还是券
	private String type = "juan";
	private int quannowPage = 1;
	private int storenowPage = 1;
	private CcqQuanSYLvAdapter ccqQuanSYLvAdapter;
	private CcqStoreSYLvAdapter ccqStoreSYLvAdapter;

	private List<Map<String, String>> ccqstoreListdetaiList;
	private List<Map<String, String>> ccqquanListdetaiList;

	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;
	// 轮播图
	private ArrayList<ADInfo> infos = new ArrayList<ADInfo>();
	// 无数据
	private TextView tv_notdata;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取配置文件完成
	private static final int HANDLER_CONFIG_SUCCESS = 2;
	// 定位完成
	protected static final int HANDLER_DINGWEI_FINISH = 3;
	protected static final int HANDLER_DINGWEI_SUCCESS = 4;

	// 券成功
	private static final int HANDLER_CCQQUANLIST_SUCCESS = 5;
	// 商家成功
	private static final int HANDLER_CCQSTOREDETAI_SUCCESS = 6;
	// 获取信息失败
	private static final int HANDLER_GETIONFO_FAILURE = 7;
	// 没有数据
	private static final int HANDLER_NODATA_FAILURE = 8;
	// 当前位置
	private TextView tv_now_place;
	private LinearLayout line_dwshuaxin;

	private boolean choosecityxz = false;
	// 换一换 页数
	private int nowPage = 1;
	// 经 度
	private String longitude = "";
	// 纬 度
	private String latitude = "";
	// 获取区县信息
	private String district = "";
	// 城市
	private String city = "";
	// 市
	private String dwshi = "";
	private String shi = "";
	// 定位
	private AMapLocationClient locationClient = null;
	private AMapLocationClientOption locationOption = new AMapLocationClientOption();

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				line_net_error.setVisibility(View.VISIBLE);
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
				line_net_error.setVisibility(View.VISIBLE);
				if (getActivity() != null) {
					Toast.makeText(getActivity(), "当前网络出错,请检查网络",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case HANDLER_GETIONFO_FAILURE:
				line_net_error.setVisibility(View.VISIBLE);
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				if (getActivity() != null) {
					if (msg.obj != null) {
						Toast.makeText(getActivity(),
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();
					}
				}
				break;
			// 配置文件完成
			case HANDLER_CONFIG_SUCCESS:
				// 初始化定位
				initLocation();
				break;
			case HANDLER_DINGWEI_SUCCESS:
				ccqListHttpPost();
				stopLocation();
				break;
			case HANDLER_DINGWEI_FINISH:
				ccqListHttpPost();
				stopLocation();
				tv_now_place.setText("定位失败，重新定位或者手动切换城市");
				break;
			// 券成功
			case HANDLER_CCQQUANLIST_SUCCESS:
				line_net_error.setVisibility(View.GONE);
				dissDialog();
				Log.e("log", "券成功:" + ccqquanListdetaiList.size());

				/*
				 * if (!"shop".equals(type)&&ccqquanListdetaiList.size() > 0) {
				 * ll_empty.setVisibility(View.GONE); }else{
				 * ll_empty.setVisibility(View.VISIBLE); }
				 */

				if ("juan".equals(type) && ccqquanListdetaiList.size() > 0) {
					ll_empty.setVisibility(View.GONE);
				} else if ("shop".equals(type)
						&& ccqstoreListdetaiList.size() > 0) {
					ll_empty.setVisibility(View.GONE);
				} else {
					ll_empty.setVisibility(View.VISIBLE);
				}

				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				ccqQuanSYLvAdapter.NotifyList(ccqquanListdetaiList, longitude,
						latitude);
				ccqQuanSYLvAdapter.notifyDataSetChanged();
				break;
			// 商家成功
			case HANDLER_CCQSTOREDETAI_SUCCESS:
				dissDialog();
				Log.e("log", "商家成功:" + ccqstoreListdetaiList.size());

				/*
				 * if (!"juan".equals(type)&& ccqstoreListdetaiList.size() > 0)
				 * { ll_empty.setVisibility(View.GONE); }else{
				 * ll_empty.setVisibility(View.VISIBLE); }
				 */

				if ("juan".equals(type) && ccqquanListdetaiList.size() > 0) {
					ll_empty.setVisibility(View.GONE);
				} else if ("shop".equals(type)
						&& ccqstoreListdetaiList.size() > 0) {
					ll_empty.setVisibility(View.GONE);
				} else {
					ll_empty.setVisibility(View.VISIBLE);
				}

				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				line_net_error.setVisibility(View.GONE);
				ccqStoreSYLvAdapter.NotifyList(ccqstoreListdetaiList,
						longitude, latitude);
				ccqStoreSYLvAdapter.notifyDataSetChanged();
				break;
			// 没有数据
			case HANDLER_NODATA_FAILURE:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				line_net_error.setVisibility(View.GONE);
				if ("juan".equals(type)) {
					if (quannowPage == 1) {
						Log.e("log", "没有数据11:" + ccqquanListdetaiList.size());
						if (ccqquanListdetaiList.size() > 0) {
							ll_empty.setVisibility(View.GONE);
						} else {
							ll_empty.setVisibility(View.VISIBLE);
						}
						ccqQuanSYLvAdapter.NotifyList(ccqquanListdetaiList,
								longitude, latitude);
						ccqQuanSYLvAdapter.notifyDataSetChanged();
						ccqStoreSYLvAdapter.NotifyList(ccqstoreListdetaiList,
								longitude, latitude);
						ccqStoreSYLvAdapter.notifyDataSetChanged();
					}
				} else {
					if (storenowPage == 1) {
						Log.e("log", "没有数据22:" + ccqstoreListdetaiList.size());
						if (ccqstoreListdetaiList.size() > 0) {
							ll_empty.setVisibility(View.GONE);
						} else {
							ll_empty.setVisibility(View.VISIBLE);
						}
						ccqQuanSYLvAdapter.NotifyList(ccqquanListdetaiList,
								longitude, latitude);
						ccqQuanSYLvAdapter.notifyDataSetChanged();
						ccqStoreSYLvAdapter.NotifyList(ccqstoreListdetaiList,
								longitude, latitude);
						ccqStoreSYLvAdapter.notifyDataSetChanged();
					}
				}
				if (getActivity() != null) {
					if (msg.obj != null) {
						Toast.makeText(getActivity(),
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();
					}
				}
				break;
			default:
				break;
			}
		};
	};
	private CheckUpdate cu;

	private ImageView iv_tishi;// 未读消息提示
	private LinearLayout ll_empty;// 没有数据提示
	private Button btn_join;
	private Button btn_look_other;

	private DBHelper myDB;
	private ScrollView scrollView;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_ccq_home, null);
		// View topView =
		// LayoutInflater.from(getActivity()).inflate(R.layout.view_fragment_ccq_list_top,
		// null);
		scrollView = (ScrollView) view.findViewById(R.id.scrollView);
		ll_empty = (LinearLayout) view.findViewById(R.id.ll_empty);
		btn_join = (Button) view.findViewById(R.id.btn_join);
		btn_look_other = (Button) view.findViewById(R.id.btn_look_other);

		btn_join.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {// 申请入驻
				if (isLogin()) {
					intent = new Intent(getActivity(),
							NewOpenStoreActivtiy.class);
					startActivity(intent);
				} else {
					intent = new Intent(getActivity(), LoginActivity.class);
					intent.putExtra("type", "qt");
					startActivity(intent);

				}
			}
		});

		btn_look_other.setOnClickListener(new OnClickListener() {// 查看其他地方美食

					@Override
					public void onClick(View v) {
						intent = new Intent(getActivity(),
								ChooseCityActivity.class);
						intent.putExtra("dwcity", shi.trim());
						intent.putExtra("latitude", latitude);
						intent.putExtra("", longitude);
						startActivityForResult(intent, 200);
					}
				});
		line_net_error = (LinearLayout) view.findViewById(R.id.line_net_error);
		TextView tv_notnet_refresh = (TextView) view
				.findViewById(R.id.tv_notnet_refresh);
		tv_notdata = (TextView) view.findViewById(R.id.tv_notdata);
		LinearLayout line_city = (LinearLayout) view
				.findViewById(R.id.line_city);
		tv_city = (TextView) view.findViewById(R.id.tv_city);
		LinearLayout fragment_ccq_top = (LinearLayout) view
				.findViewById(R.id.fragment_ccq_top);
		fragment_ccq_top.setVisibility(View.VISIBLE);
		LinearLayout line_search = (LinearLayout) view
				.findViewById(R.id.line_search);
		LinearLayout line_scans = (LinearLayout) view
				.findViewById(R.id.line_scans);
		RelativeLayout line_message = (RelativeLayout) view
				.findViewById(R.id.line_message);
		iv_tishi = (ImageView) view.findViewById(R.id.iv_tishi);
		RelativeLayout relay_lunbo = (RelativeLayout) view
				.findViewById(R.id.relay_lunbo);
		ad_view = (ImageCycleView) view.findViewById(R.id.ad_view);
		ImageView iv_vip = (ImageView) view.findViewById(R.id.iv_vip);
		ImageView iv_superquan = (ImageView) view
				.findViewById(R.id.iv_superquan);
		ImageView iv_bawang = (ImageView) view.findViewById(R.id.iv_bawang);
		// 美食
		RadioButton radio_quan = (RadioButton) view
				.findViewById(R.id.radio_quan);
		// 商家
		RadioButton radio_store = (RadioButton) view
				.findViewById(R.id.radio_store);

		tv_now_place = (TextView) view.findViewById(R.id.tv_now_place);
		line_dwshuaxin = (LinearLayout) view.findViewById(R.id.line_dwshuaxin);

		lv = (ListView) view.findViewById(R.id.lv);
		// lv.setEmptyView(ll_empty);

		ccqstoreListdetaiList = new ArrayList<Map<String, String>>();
		ccqquanListdetaiList = new ArrayList<Map<String, String>>();

		ccqQuanSYLvAdapter = new CcqQuanSYLvAdapter(getActivity());
		ccqStoreSYLvAdapter = new CcqStoreSYLvAdapter(getActivity());
		lv.setAdapter(ccqQuanSYLvAdapter);
		refreshLayout = (MaterialRefreshLayout) view
				.findViewById(R.id.refresh_layout);
		refreshLayout.setLoadMore(true);
		// lv.addHeaderView(topView);

		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(getActivity());
		getResolution();
		showLoading();
		// 配置文件
		httpPost();

		String checkupdata = SharedUtil.getStringValue(
				SharedCommon.CHECKUPDATA, "");
		if ("yes".equals(checkupdata)) {
			cu = new CheckUpdate(getActivity());
			cu.StartCheck();
		}

		int[] info = utils.getWindowInfo(getActivity());

		// 获取当前控件的布局对象
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		// 获取当前控件的布局对象
		params.height = info[0] / 15 * 8;// 设置当前控件布局的高度
		relay_lunbo.setLayoutParams(params);

		line_city.setOnClickListener(this);
		line_search.setOnClickListener(this);
		line_scans.setOnClickListener(this);
		line_message.setOnClickListener(this);
		iv_vip.setOnClickListener(this);
		iv_superquan.setOnClickListener(this);
		iv_bawang.setOnClickListener(this);
		radio_quan.setOnClickListener(this);
		radio_store.setOnClickListener(this);
		line_dwshuaxin.setOnClickListener(this);
		tv_notnet_refresh.setOnClickListener(this);

		lv.setFocusable(false);

		initListView();

		return view;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		registerBoradcastReceiver();
	}

	public static final String SCROLLVIEW_TOP = "ccj.yun28.com.fragment.scrollTo";

	private BroadcastReceiver bReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(SCROLLVIEW_TOP)) {
				setScrollViewTop();
			}
		};
	};

	private void registerBoradcastReceiver() {
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(SCROLLVIEW_TOP);
		getActivity().registerReceiver(bReceiver, mIntentFilter);
	}

	/**
	 * ScrollView滑动到顶部
	 */
	public void setScrollViewTop() {
		// 直接置顶，瞬间回到顶部，没有滚动过程，其中Y值可以设置为大于0的值，使Scrollview停在指定位置;
		// scrollView.scrollTo(0,0);
		// 类似于手动拖回顶部,有滚动过程;
		// scrollView.fullScroll(View.FOCUS_UP);
		// 类似于手动拖回顶部,有滚动过程，其中Y值可以设置为大于0的值，使Scrollview停在指定位置。
		scrollView.smoothScrollTo(0, 0);
	}

	// 校验登录与否
	private boolean isLogin() {
		try {
			if (myDB != null) {
				SQLiteDatabase db = myDB.getReadableDatabase();
				Cursor cursor = db.rawQuery(
						"select * from user where status = 1", null);
				if (cursor == null || cursor.getCount() == 0) {
					return false;
				}
			}
		} catch (Exception e) {
		}

		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		unReadMassage();// 未读消息
	}

	private String resolution = "2";// 手机屏幕分辨率

	// 获取屏幕分辨率 判断需要什么样的图
	private String getResolution() {
		WindowManager windowManager = (getActivity()).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int screenWidth = screenWidth = display.getWidth();
		int screenHeight = screenHeight = display.getHeight();
		if (480 > screenWidth) {
			resolution = "1";
		} else if (480 <= screenWidth && screenWidth < 720) {
			resolution = "2";
		} else if (720 <= screenWidth && screenWidth < 1080) {
			resolution = "3";
		} else if (1080 >= screenWidth) {
			resolution = "4";
		}
		return resolution;
	}

	// 获取配置文件
	private void httpPost() {
		RequestParams params = new RequestParams();
		// params.addBodyParameter("sh", "test1");
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		httpUtils.send(HttpMethod.POST, JiekouUtils.PEIZHI, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Log.e("ee", "PEIZHI" + JiekouUtils.PEIZHI);
						Log.e("ee", "网络错误:" + arg1);
						// handler.sendMessage(handler.obtainMessage(
						// HANDLER_NET_FAILURE1, arg1));
						handler.sendEmptyMessage(HANDLER_CONFIG_SUCCESS);

					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						peizhiJsonInfo(arg0.result);
					}
				});

	}

	// 获取配置文件接口返回
	protected void peizhiJsonInfo(String result) {
		try {
			JSONObject jo = new JSONObject(result);
			String status = jo.getString("code");
			if ("200".equals(status)) {
				JSONObject object = jo.getJSONObject("data");
				// api域名 例如 api.28yun.net
				String api_domain = object.getString("API_DOMAIN");
				// 图片服务器域名 例如 http://58.67.219.121
				String img_domain = object.getString("IMG_DOMAIN");
				// http:// 和 https:// 协议可以做切换
				String hp = object.getString("HP");
				// 商家后台App二维码地址安卓版
				String store_qrcode_img_az = object
						.getString("STORE_QRCODE_IMG_AZ");
				// 商家后台分享图片地址
				// http://www.28yun.com/mobile/api/payment/wxpay/ccqsj.png
				String store_fx = object.getString("STORE_FX");
				// 商家后台App二维码地址安卓版url
				String store_qrcode_img_az_url = object
						.getString("STORE_QRCODE_IMG_AZ_URL");
				// 商家后台App二维码地址IOS版
				String store_qrcode_img_ios = object
						.getString("STORE_QRCODE_IMG_IOS");
				// 商家后台App二维码地址IOS版url
				String store_qrcode_img_ios_url = object
						.getString("STORE_QRCODE_IMG_IOS_URL");
				// 商城App二维码地址安卓版
				String mall_qrcode_img_az = object
						.getString("MALL_QRCODE_IMG_AZ");
				// 商城App分享图片地址
				// http://www.28yun.com/mobile/api/payment/wxpay/ccqyh.png
				String mall_fx = object.getString("MALL_FX");
				// 商城App二维码地址安卓版url
				String mall_qrcode_img_az_url = object
						.getString("MALL_QRCODE_IMG_AZ_URL");
				// 商城App二维码地址IOS版
				String mall_qrcode_img_ios = object
						.getString("MALL_QRCODE_IMG_IOS");
				// 商城App二维码地址IOS版url
				String mall_qrcode_img_ios_url = object
						.getString("MALL_QRCODE_IMG_IOS_URL");
				// 事业部App二维码地址安卓版
				String marketer_qrcode_img_az = object
						.getString("MARKETER_QRCODE_IMG_AZ");
				// 事业部App分享图片地址
				// http://www.28yun.com/mobile/api/payment/wxpay/ccqsy.png
				String marketer_fx = object.getString("MARKETER_FX");
				// 事业部App二维码地址安卓版url
				String marketer_qrcode_img_az_url = object
						.getString("MARKETER_QRCODE_IMG_AZ_URL");
				// 事业部App二维码地址IOS版
				String marketer_qrcode_img_ios = object
						.getString("MARKETER_QRCODE_IMG_IOS");
				// 事业部App二维码地址IOS版url
				String marketer_qrcode_img_ios_url = object
						.getString("MARKETER_QRCODE_IMG_IOS_URL");
				// 商城App首屏广告图
				String mall_fp = object.getString("MALL_FP");
				// 商家后台App首屏广告图
				String store_fp = object.getString("STORE_FP");
				// 事业部App首屏广告图
				String marketer_fp = object.getString("MARKETER_FP");
				// 商城App客服电话
				String mall_service = object.getString("MALL_SERVICE");
				// 商家后台App客服电话
				String store_service = object.getString("STORE_SERVICE");
				// 事业部App客服电话
				String marketer_service = object.getString("MARKETER_SERVICE");
				// 事业部App管理员电话
				String marketer_service_admin = object
						.getString("MARKETER_SERVICE_ADMIN");
				// 商家后台审核人员电话
				String check_phone = object.getString("CHECK_PHONE");
				// 商家后台提现说明
				String store_withdraw_explain = object
						.getString("STORE_WITHDRAW_EXPLAIN");
				// 商家帮助中心
				String store_help = object.getString("STORE_HELP");
				// 商城提现说明
				String mall_withdraw_explain = object
						.getString("MALL_WITHDRAW_EXPLAIN");
				// 商城帮助中心
				String mall_help = object.getString("MALL_HELP");
				// 红包规则
				String mall_red_packet = object.getString("MALL_RED_PACKET");
				// 活动页1
				String activity_1 = object.getString("ACTIVITY_1");
				// 活动页2
				String activity_2 = object.getString("ACTIVITY_2");
				// 活动页3
				String activity_3 = object.getString("ACTIVITY_3");
				// 活动页4
				String activity_4 = object.getString("ACTIVITY_4");
				// 安卓 给我评分
				String activity_5 = object.getString("ACTIVITY_5");
				// 用户协议
				String useragreement = object.getString("USERAGREEMENT");
				// 用户协议
				String reg = object.getString("REG");

				SharedUtil
						.saveStringValue(SharedCommon.API_DOMAIN1, api_domain);
				SharedUtil.saveStringValue(SharedCommon.IMG_DOMAIN, img_domain);
				SharedUtil.saveStringValue(SharedCommon.HP, hp);
				SharedUtil.saveStringValue(SharedCommon.STORE_QRCODE_IMG_AZ,
						store_qrcode_img_az);
				SharedUtil.saveStringValue(SharedCommon.STORE_FX, store_fx);
				SharedUtil.saveStringValue(
						SharedCommon.STORE_QRCODE_IMG_AZ_URL,
						store_qrcode_img_az_url);
				SharedUtil.saveStringValue(SharedCommon.STORE_QRCODE_IMG_IOS,
						store_qrcode_img_ios);
				SharedUtil.saveStringValue(
						SharedCommon.STORE_QRCODE_IMG_IOS_URL,
						store_qrcode_img_ios_url);
				SharedUtil.saveStringValue(SharedCommon.MALL_QRCODE_IMG_AZ,
						mall_qrcode_img_az);
				SharedUtil.saveStringValue(SharedCommon.MALL_FX, mall_fx);
				SharedUtil.saveStringValue(SharedCommon.MALL_QRCODE_IMG_AZ_URL,
						mall_qrcode_img_az_url);
				SharedUtil.saveStringValue(SharedCommon.MALL_QRCODE_IMG_IOS,
						mall_qrcode_img_ios);
				SharedUtil.saveStringValue(
						SharedCommon.MALL_QRCODE_IMG_IOS_URL,
						mall_qrcode_img_ios_url);
				SharedUtil.saveStringValue(SharedCommon.MARKETER_QRCODE_IMG_AZ,
						marketer_qrcode_img_az);
				SharedUtil.saveStringValue(SharedCommon.MARKETER_FX,
						marketer_fx);
				SharedUtil.saveStringValue(
						SharedCommon.MARKETER_QRCODE_IMG_AZ_URL,
						marketer_qrcode_img_az_url);
				SharedUtil.saveStringValue(
						SharedCommon.MARKETER_QRCODE_IMG_IOS,
						marketer_qrcode_img_ios);
				SharedUtil.saveStringValue(
						SharedCommon.MARKETER_QRCODE_IMG_IOS_URL,
						marketer_qrcode_img_ios_url);
				SharedUtil.saveStringValue(SharedCommon.MALL_FP, mall_fp);
				SharedUtil.saveStringValue(SharedCommon.STORE_FP, store_fp);
				SharedUtil.saveStringValue(SharedCommon.MARKETER_FP,
						marketer_fp);
				SharedUtil.saveStringValue(SharedCommon.MALL_SERVICE,
						mall_service);
				SharedUtil.saveStringValue(SharedCommon.STORE_SERVICE,
						store_service);
				SharedUtil.saveStringValue(SharedCommon.MARKETER_SERVICE,
						marketer_service);
				SharedUtil.saveStringValue(SharedCommon.MARKETER_SERVICE_ADMIN,
						marketer_service_admin);
				SharedUtil.saveStringValue(SharedCommon.CHECK_PHONE,
						check_phone);
				SharedUtil.saveStringValue(SharedCommon.STORE_WITHDRAW_EXPLAIN,
						store_withdraw_explain);
				SharedUtil.saveStringValue(SharedCommon.STORE_HELP, store_help);
				SharedUtil.saveStringValue(SharedCommon.MALL_WITHDRAW_EXPLAIN,
						mall_withdraw_explain);
				SharedUtil.saveStringValue(SharedCommon.MALL_HELP, mall_help);
				SharedUtil.saveStringValue(SharedCommon.MALL_RED_PACKET,
						mall_red_packet);
				SharedUtil.saveStringValue(SharedCommon.ACTIVITY_1, activity_1);
				SharedUtil.saveStringValue(SharedCommon.ACTIVITY_2, activity_2);
				SharedUtil.saveStringValue(SharedCommon.ACTIVITY_3, activity_3);
				SharedUtil.saveStringValue(SharedCommon.ACTIVITY_4, activity_4);
				SharedUtil.saveStringValue(SharedCommon.ACTIVITY_5, activity_5);
				SharedUtil.saveStringValue(SharedCommon.USERAGREEMENT,
						useragreement);
				SharedUtil.saveStringValue(SharedCommon.REG, reg);
				handler.sendEmptyMessage(HANDLER_CONFIG_SUCCESS);
			} else {
				String message = jo.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETIONFO_FAILURE, message));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 餐餐抢列表
	protected void ccqListHttpPost() {
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("lat1", latitude);
		params.addBodyParameter("lng1", longitude);
		params.addBodyParameter("city_id", tv_city.getText().toString().trim());
		// 配iocn类型 安卓mdpi图:1 安卓hdpi图:2 安卓xhdpi图:3 安卓xxhdpi图:4
		params.addBodyParameter("type", resolution);

		// TODO
		params.addBodyParameter("goods_images_op", "!m");
		params.addBodyParameter("goods_images_size", "750x350");

		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWCCQSY, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Log.e("ee", "PEIZHIlist" + JiekouUtils.NEWCCQSY);
						Log.e("ee", "网络错误:list" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						ccqJsonInfo(arg0.result);
					}
				});
	}

	// 餐餐抢列表数据解析
	protected void ccqJsonInfo(String result) {
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				JSONObject job = object.getJSONObject("data");
				// 轮播图
				JSONArray carousel = job.getJSONArray("carousel");
				// 店铺条目
				JSONArray unionstore_list = job.getJSONArray("unionstore_list");
				JSONArray goods_list = job.getJSONArray("goods_list");
				if (carousel.length() > 0) {
					infos.clear();
					for (int i = 0; i < carousel.length(); i++) {
						/*
						 * JSONObject json = carousel.getJSONObject(i); String
						 * data = json.getString("data"); String id =
						 * json.getString("id"); String image =
						 * json.getString("image"); String item_name =
						 * json.getString("item_name"); String item_sort =
						 * json.getString("item_sort"); String item_type =
						 * json.getString("item_type"); ADInfo info = new
						 * ADInfo(); info.setPic(image); infos.add(info);
						 */

						JSONObject json = carousel.getJSONObject(i);
						String data = json.getString("data");
						String id = json.getString("id");
						String image = json.getString("image");
						String item_name = json.getString("item_name");
						String item_sort = json.getString("item_sort");
						String item_type = json.getString("item_type");
						String goods_id = json.getString("goods_id");
						String store_id = json.getString("store_id");

						ADInfo info = new ADInfo();
						info.setPic(image);
						info.setData(data);
						info.setItem_type(item_type);
						info.setGoods_id(goods_id);
						info.setStore_id(store_id);
						infos.add(info);
					}
				}

				ad_view.setImageResources(infos, mAdCycleViewListener);

				ccqstoreListdetaiList.clear();
				for (int i = 0; i < unionstore_list.length(); i++) {
					JSONObject json = unionstore_list.getJSONObject(i);
					ccqshopDetailListInfo(json);
				}
				/*
				 * if (ccqstoreListdetaiList.size() > 0) {
				 * ll_empty.setVisibility(View.GONE); }else{
				 * ll_empty.setVisibility(View.VISIBLE); }
				 */
				ccqquanListdetaiList.clear();
				for (int i = 0; i < goods_list.length(); i++) {
					JSONObject json = goods_list.getJSONObject(i);
					quan_listListInfo(json);
				}
				/*
				 * if (ccqquanListdetaiList.size() > 0) {
				 * ll_empty.setVisibility(View.GONE); }else{
				 * ll_empty.setVisibility(View.VISIBLE); }
				 */

			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETIONFO_FAILURE, message));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {

		@Override
		public void onImageClick(ADInfo info, int position, View imageView) {

			// if (utils.getisLogin(getActivity())) {
			// Intent intent = new Intent(getActivity(),
			// NewHuiyuanGetVipActivity.class);
			// startActivity(intent);
			// } else {
			// Intent intent = new Intent(getActivity(), LoginActivity.class);
			// intent.putExtra("type", "ccq");
			// startActivity(intent);
			// }
			if (utils.getisLogin(getActivity())) {
				// item_type:操作类型 0:只展示图片无需操作 1:跳转商品页面 2:跳转商家页面 3:跳转webview
				// 11:跳转领取一个月免费vip
				Intent intent = null;
				if (info.getItem_type().equals("1")) {
					intent = new Intent(getActivity(),
							NewCcqProDetailActivity.class);// TODO
					intent.putExtra("starlat", latitude);
					intent.putExtra("starlng", longitude);
					intent.putExtra("ccqgoods_id", info.getData());
					intent.putExtra("store_id", info.getStore_id());
					intent.putExtra("city", city);
					intent.putExtra("district", district);
					startActivity(intent);
				} else if (info.getItem_type().equals("2")) {
					intent = new Intent(getActivity(),
							CCQStoreDetailActivity.class);
					intent.putExtra("starlat", latitude);
					intent.putExtra("starlng", longitude);
					intent.putExtra("store_id", info.getData());
					startActivity(intent);
				} else if (info.getItem_type().equals("11")) {
					intent = new Intent(getActivity(),
							NewHuiyuanGetVipActivity.class);
					startActivity(intent);
				}
			} else {
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "ccq");
				startActivity(intent);
			}

		}

		@Override
		public void displayImageWM(String imageURL, ImageView imageView) {
			try {
				ImageUtil.display(imageURL, imageView);
			} catch (Exception e) {
			}

		}
	};

	@Override
	public void onClick(View v) {
		Intent intent;

		switch (v.getId()) {
		case R.id.line_city:
			intent = new Intent(getActivity(), ChooseCityActivity.class);
			intent.putExtra("dwcity", shi.trim());
			intent.putExtra("latitude", latitude);
			intent.putExtra("longitude", longitude);
			startActivityForResult(intent, 200);
			break;
		case R.id.line_search:
			intent = new Intent(getActivity(), SearchActivity.class);
			intent.putExtra("type", "餐餐抢");
			intent.putExtra("lat1", latitude);
			intent.putExtra("lng1", longitude);
			intent.putExtra("city_id", shi);
			startActivity(intent);
			break;
		case R.id.line_scans:
			intent = new Intent(getActivity(), SaoYiSaoActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.line_message:
			intent = new Intent(getActivity(), MessageActivcity.class);
			intent.putExtra("type", "餐餐抢");
			intent.putExtra("lat1", latitude);
			intent.putExtra("lng1", longitude);
			startActivity(intent);
			break;
		case R.id.tv_notnet_refresh:
			showLoading();
			// 初始化定位
			initLocation();
			// 配置文件
			httpPost();
			break;
		case R.id.iv_vip: // 跳转到热门推荐
			intent = new Intent(getActivity(), RecmmendGoodsActivity.class);
			intent.putExtra("starlat", latitude);
			intent.putExtra("starlng", longitude);
			intent.putExtra("city", city);
			intent.putExtra("district",district);
			
			startActivity(intent);
			/*
			 * if (utils.getisLogin(getActivity())) { intent = new
			 * Intent(getActivity(), RecmmendGoodsActivity.class);
			 * startActivity(intent); } else { intent = new
			 * Intent(getActivity(), LoginActivity.class);
			 * intent.putExtra("type", "ccq"); startActivity(intent); }
			 */
			break;
		case R.id.iv_superquan:
			// intent = new Intent(getActivity(), ChaoZhiQuanActivity.class);
			intent = new Intent(getActivity(), NewChaoJiQuanActivity.class);
			/*
			 * intent.putExtra("dwcity", shi.trim());
			 * intent.putExtra("latitude", latitude);
			 * intent.putExtra("longitude", longitude);
			 */
			intent.putExtra("city_id", shi.trim().replaceAll("市", ""));
			intent.putExtra("starlat", latitude);
			intent.putExtra("starlng", longitude);
			startActivityForResult(intent, 200);
			break;
		case R.id.iv_bawang:
			// intent = new Intent(getActivity(), BaWangCanActivity.class);
			intent = new Intent(getActivity(), NewBaWangCanActivity.class);
			intent.putExtra("dwcity", shi.trim());
			intent.putExtra("latitude", latitude);
			intent.putExtra("longitude", longitude);
			startActivityForResult(intent, 200);
			break;

		case R.id.line_shuaxin:
			showLoading();
			startLocation();
			break;
		case R.id.radio_quan:
			type = "juan";
			if (ccqquanListdetaiList.size() > 0) {
				ll_empty.setVisibility(View.GONE);
			} else {
				ll_empty.setVisibility(View.VISIBLE);
			}
			lv.setAdapter(ccqQuanSYLvAdapter);
			break;
		case R.id.radio_store:
			type = "shop";
			if (ccqstoreListdetaiList.size() > 0) {
				ll_empty.setVisibility(View.GONE);
			} else {
				ll_empty.setVisibility(View.VISIBLE);
			}
			lv.setAdapter(ccqStoreSYLvAdapter);
			break;
		case R.id.line_dwshuaxin:
			quannowPage = 1;
			storenowPage = 1;
			showLoading();
			startLocation();
			break;
		default:
			break;
		}
	}

	private void getQuanListHttpPost() {
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("lat1", latitude);
		params.addBodyParameter("lng1", longitude);
		params.addBodyParameter("city_id", tv_city.getText().toString().trim());
		params.addBodyParameter("page", quannowPage + "");

		params.addBodyParameter("goods_images_op", "!c");
		params.addBodyParameter("goods_images_size", "750x350");

		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWSY_CANCANQIANGJUAN,
				params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						ccqQuanListJsonInfo(arg0.result);
					}
				});
	}

	protected void ccqQuanListJsonInfo(String result) {
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				// 商品条目
				if (quannowPage == 1) {
					ccqquanListdetaiList.clear();
				}
				JSONArray array = object.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = array.getJSONObject(i);
					quan_listListInfo(json);
				}
				/*
				 * if (ccqquanListdetaiList.size() > 0) {
				 * ll_empty.setVisibility(View.GONE); }else{
				 * ll_empty.setVisibility(View.VISIBLE); }
				 */

			} else if ("300".equals(status)) {
				String message = object.getString("message");
				if (quannowPage == 1) {
					ccqquanListdetaiList.clear();
				}
				handler.sendMessage(handler.obtainMessage(
						HANDLER_NODATA_FAILURE, message));
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETIONFO_FAILURE, message));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 餐餐抢券列表详细
	private void quan_listListInfo(JSONObject json) {
		try {
			String discount = json.getString("discount");
			String distance_value = json.getString("distance_value");
			String goods_addtime = json.getString("goods_addtime");
			String goods_click = json.getString("goods_click");
			String goods_id = json.getString("goods_id");
			String goods_image = json.getString("goods_image");
			String goods_image2 = json.getString("goods_image2");
			String goods_marketprice = json.getString("goods_marketprice");
			String goods_name = json.getString("goods_name");
			String goods_price = json.getString("goods_price");
			String goods_salenum = json.getString("goods_salenum");
			String goods_storage = json.getString("goods_storage");
			String latitude = json.getString("latitude");
			String longitude = json.getString("longitude");
			String remark = json.getString("remark");
			String store_address = json.getString("store_address");
			String store_id = json.getString("store_id");
			String store_name = json.getString("store_name");
			String evaluation_good_star = json
					.getString("evaluation_good_star");
			Map<String, String> ccqgoodsDetailMap = new HashMap<String, String>();
			ccqgoodsDetailMap.put("discount", discount);
			ccqgoodsDetailMap.put("distance_value", distance_value);
			ccqgoodsDetailMap.put("goods_addtime", goods_addtime);
			ccqgoodsDetailMap.put("goods_click", goods_click);
			ccqgoodsDetailMap.put("goods_id", goods_id);
			ccqgoodsDetailMap.put("goods_image", goods_image);
			ccqgoodsDetailMap.put("goods_image2", goods_image2);
			ccqgoodsDetailMap.put("goods_marketprice", goods_marketprice);
			ccqgoodsDetailMap.put("goods_name", goods_name);
			ccqgoodsDetailMap.put("goods_price", goods_price);
			ccqgoodsDetailMap.put("goods_salenum", goods_salenum);
			ccqgoodsDetailMap.put("goods_storage", goods_storage);
			ccqgoodsDetailMap.put("latitude", latitude);
			ccqgoodsDetailMap.put("longitude", longitude);
			ccqgoodsDetailMap.put("remark", remark);
			ccqgoodsDetailMap.put("store_address", store_address);
			ccqgoodsDetailMap.put("store_id", store_id);
			ccqgoodsDetailMap.put("store_name", store_name);
			ccqgoodsDetailMap.put("evaluation_good_star", evaluation_good_star);
			ccqquanListdetaiList.add(ccqgoodsDetailMap);
			handler.sendEmptyMessage(HANDLER_CCQQUANLIST_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 获取餐餐抢店铺列表数据
	private void getStoreListHttpPost() {
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("lat1", latitude);
		params.addBodyParameter("lng1", longitude);
		params.addBodyParameter("city_id", tv_city.getText().toString().trim());
		params.addBodyParameter("page", storenowPage + "");

		params.addBodyParameter("goods_images_op", "!c");
		params.addBodyParameter("goods_images_size", "750x500");

		// params.addBodyParameter("page_size", "8");
		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWSY_CANCANQIANGSHOP,
				params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						ccqStoreListJsonInfo(arg0.result);
					}
				});
	}

	protected void ccqStoreListJsonInfo(String result) {
		try {

			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				if (storenowPage == 1) {
					ccqstoreListdetaiList.clear();
				}
				JSONArray array = object.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = array.getJSONObject(i);
					ccqshopDetailListInfo(json);
				}
				/*
				 * if (ccqstoreListdetaiList.size() > 0) {
				 * ll_empty.setVisibility(View.GONE); }else{
				 * ll_empty.setVisibility(View.VISIBLE); }
				 */
			} else if ("300".equals(status)) {
				String message = object.getString("message");
				if (storenowPage == 1) {
					// lv.setVisibility(View.GONE);
					ccqstoreListdetaiList.clear();
				} else {
					// lv.setVisibility(View.VISIBLE);
				}
				handler.sendMessage(handler.obtainMessage(
						HANDLER_NODATA_FAILURE, message));
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETIONFO_FAILURE, message));
				lv.setVisibility(View.GONE);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private void ccqshopDetailListInfo(JSONObject json) {
		try {
			String city_id = json.getString("city_id");
			String county_id = json.getString("county_id");
			String distance = json.getString("distance");
			String distance_value = json.getString("distance_value");
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
			String live_store_tel = json.getString("live_store_tel");
			String store_traffic = json.getString("store_traffic");
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
			ccqshopDetailMap.put("live_store_tel", live_store_tel);
			ccqshopDetailMap.put("store_traffic", store_traffic);
			ccqstoreListdetaiList.add(ccqshopDetailMap);
			handler.sendEmptyMessage(HANDLER_CCQSTOREDETAI_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private Dialog qiehuanCityDialog;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 300) {
			final String getcity = data.getStringExtra("city");
			final String reslatitude = data.getStringExtra("latitude");
			final String reslongitude = data.getStringExtra("longitude");
			
			choosecityxz = true;
			quannowPage = 1;
			storenowPage = 1;
			latitude = reslatitude;
			longitude = reslongitude;

			if (!TextUtils.isEmpty(dwshi)) {

				if (getcity.equals(dwshi)) {
					if (qiehuanCityDialog != null
							&& qiehuanCityDialog.isShowing()) {
						qiehuanCityDialog.dismiss();
					}
					tv_city.setText(getcity);
					shi = getcity;
					showLoading();
					getQuanListHttpPost();
					getStoreListHttpPost();
				} else {
					View view = LayoutInflater.from(getActivity()).inflate(
							R.layout.dialog_bankcard_delete, null);
					TextView tv_text = (TextView) view
							.findViewById(R.id.tv_text);
					TextView tv_dialog_cancel = (TextView) view
							.findViewById(R.id.tv_dialog_cancel);
					TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
					tv_text.setText("系统定位您在" + dwshi + "\n\n 是否切换到" + getcity);
					tv_dialog_cancel.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							if (qiehuanCityDialog != null
									&& qiehuanCityDialog.isShowing()) {
								qiehuanCityDialog.dismiss();
							}
							tv_city.setText(dwshi);
							shi = dwshi;
							showLoading();
							getQuanListHttpPost();
							getStoreListHttpPost();
						}
					});

					tv_ok.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							if (qiehuanCityDialog != null
									&& qiehuanCityDialog.isShowing()) {
								qiehuanCityDialog.dismiss();
							}
							tv_city.setText(getcity);
							shi = getcity;
							showLoading();
							getQuanListHttpPost();
							getStoreListHttpPost();
						}
					});
					qiehuanCityDialog = new Dialog(getActivity(),
							R.style.mDialogStyle);
					qiehuanCityDialog.setContentView(view);
					qiehuanCityDialog.setCanceledOnTouchOutside(false);
					qiehuanCityDialog.show();
				}
			} else {
				tv_city.setText(getcity);
				shi = getcity;
				showLoading();
				getQuanListHttpPost();
				getStoreListHttpPost();
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
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
					String allshi = loc.getCity();

					// 获取区县信息
					district = loc.getDistrict();
					city = loc.getCity();
					
					SharedUtil.saveStringValue(SharedCommon.ADDRESS, loc.getAddress());
					SharedUtil.saveStringValue(SharedCommon.LATITUDE, loc.getLatitude()+"");
					SharedUtil.saveStringValue(SharedCommon.LONGITUDE, loc.getLongitude()+"");
					SharedUtil.saveStringValue(SharedCommon.CITY, loc.getCity());
					

					Log.e("log", "longitude:" + longitude + "  latitude:"
							+ latitude + "  district:" + district + "  city:"
							+ city);

				if (ccqQuanSYLvAdapter == null) {
						ccqQuanSYLvAdapter = new CcqQuanSYLvAdapter(
								getActivity());
					}

					/*List<Map<String, String>> cityInfo = new ArrayList<Map<String, String>>();
					Map<String, String> map = new HashMap<String, String>();
					map.put("city", city);
					map.put("district", district);
					cityInfo.clear();
					cityInfo.add(map);
					ccqQuanSYLvAdapter.setCityInfo(cityInfo);*/
					
					Map<String, String> map = new HashMap<String, String>();
					map.put("city", city);
					map.put("district", district);
					map.put("latitude", latitude);
					map.put("longitude", longitude);
					ccqQuanSYLvAdapter.setCityInfo(map);

					
					
					if (ccqStoreSYLvAdapter == null) {
						ccqStoreSYLvAdapter = new CcqStoreSYLvAdapter(
								getActivity());
					}

					Map<String, String> mMap = new HashMap<String, String>();
					mMap.put("city", city);
					mMap.put("district", district);
					mMap.put("latitude", latitude);
					mMap.put("longitude", longitude);
					ccqStoreSYLvAdapter.setExtra(mMap);
					

					if (!choosecityxz) {
						if (!TextUtils.isEmpty(allshi)) {
							dwshi = allshi.replaceAll("市", "");
							shi = allshi.replaceAll("市", "");
							tv_city.setText(shi.trim());
						}
					}
					tv_now_place.setText(loc.getDistrict() + loc.getStreet());
					handler.sendEmptyMessage(HANDLER_DINGWEI_SUCCESS);
				} else {
					// 定位失败
					// Toast.makeText(getActivity(), "正在定位中",
					// Toast.LENGTH_SHORT)
					// .show();

					// 自动定位失败时弹出dialog
					DialogUtil.showDoubleTextSelection(getActivity(),
							"自动定位失败，重新定位或手动切换城市！", "手动定位", "再试一次",
							new OnDoubleDialogListener() {

								@Override
								public void onClick(View v, boolean bl) {
									if (bl) {
										// 再试一次
										initLocation();
									} else {
										// 手动定位
										Intent intent = new Intent(
												getActivity(),
												ChooseCityActivity.class);
										intent.putExtra("dwcity", shi.trim());
										intent.putExtra("latitude", latitude);
										intent.putExtra("longitude", longitude);
										startActivityForResult(intent, 200);
									}
								}
							});

					handler.sendEmptyMessage(HANDLER_DINGWEI_FINISH);
				}
			} else {
				Toast.makeText(getActivity(), "正在定位中", Toast.LENGTH_SHORT)
						.show();
				handler.sendEmptyMessage(HANDLER_DINGWEI_FINISH);
			}
		}
	};

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

	// 加载中动画
	private Dialog loadingDialog;
	private TextView tv_city;
	private ImageCycleView ad_view;

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
		}
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
					if ("juan".equals(type)) {
						quannowPage++;
						getQuanListHttpPost();
					} else {
						storenowPage++;
						getStoreListHttpPost();
					}
				} catch (Exception e) {
					handler.sendEmptyMessage(HANDLER_NN_FAILURE);
				}
			}
		});
	}

	/**
	 * 未读消息
	 */
	private void unReadMassage() {
		Log.e("log", "unReadMassage（）-> 执行  ");
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);

		params.addBodyParameter("token", new DButil().gettoken(getActivity()));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(getActivity()));

		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWSY_UNREAD_MESSAGE,
				params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Common<UnReadMassageBean> massage = Common.fromJson(
								arg0.result, UnReadMassageBean.class);
						String code = massage.getCode();
						massage.getMessage();

						if ("200".equals(code)) {
							iv_tishi.setVisibility(View.VISIBLE);
							massage.getData().getUnread_type1();
							Log.e("log", "显示小红点" + massage.getMessage());
						} else {
							iv_tishi.setVisibility(View.GONE);
							Log.e("log", "隐藏小红点" + massage.getMessage());
						}
					}
				});

	}

}
