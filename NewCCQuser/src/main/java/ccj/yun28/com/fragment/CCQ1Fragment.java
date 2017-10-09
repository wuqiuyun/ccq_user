package ccj.yun28.com.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.MainActivity1;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.CcqJuanLvAdapter;
import ccj.yun28.com.adapter.CcqShopLvAdapter;
import ccj.yun28.com.ccq.BaWangCanActivity;
import ccj.yun28.com.ccq.CCQStoreDetailActivity;
import ccj.yun28.com.ccq.CcqProDetailActivity;
import ccj.yun28.com.ccq.CcqTagDetailActivity;
import ccj.yun28.com.ccq.ChaoZhiQuanActivity;
import ccj.yun28.com.ccq.ChooseCityActivity;
import ccj.yun28.com.ccq.OldCCQStoreDetailActivity;
import ccj.yun28.com.ccq.NewHuiyuanGetVipActivity;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.sy.MessageActivcity;
import ccj.yun28.com.sy.SaoYiSaoActivity;
import ccj.yun28.com.sy.SearchActivity;
import ccj.yun28.com.sy.YqzcActivity;
import ccj.yun28.com.utils.CheckUpdate;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.MyListView;
import ccj.yun28.com.view.MyScrollView;
import ccj.yun28.com.view.OnRefreshListener;
import ccj.yun28.com.view.WebviewActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol;
import com.amap.api.location.AMapLocationListener;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.umeng.analytics.MobclickAgent;

/**
 * 修改后 餐餐抢页为首页
 * 
 * @author meihuali
 * 
 */
public class CCQ1Fragment extends Fragment implements OnClickListener,
		OnItemClickListener {

	// 没有数据时显示
	private LinearLayout line_net_error;
	// 列表下的更多
	// private TextView tv_di_more;
	// 显示的是店铺还是券
	private String type = "juan";

	private BitmapUtils bitmapUtils;
	private MyScrollView scrollView;
	private TextView tv_city;
	private TextView tv_more;
	private ImageView iv_more;
	private TextView tv_huiyuan;
	private ImageView iv_huiyuan;
	private ImageView iv_vip;
	private TextView tv_vip;
	private ImageView iv_tuijian;
	private TextView tv_tuijian;
	private TextView tv_play;
	private ImageView iv_play;
	private ImageView iv_hotel;
	private TextView tv_hotel;
	private ImageView iv_beauty;
	private TextView tv_beauty;
	private ImageView iv_food;
	private TextView tv_food;
	// 专题
	private ImageView iv_zuanti_one;
	private ImageView iv_zuanti_two;
	private ImageView iv_zuanti_three;

	private List<Map<String, String>> ccqshopListdetaiList;
	private List<Map<String, String>> ccqsytopListdetaiList;
	private List<Map<String, String>> ccqgoodsListdetaiList;
	private List<Map<String, String>> ccqtwozuantiListdetaiList;

	private CheckUpdate cu;
	private String choosecitytype;
	private boolean choosecityxz = false;
	private MaterialRefreshLayout refreshLayout;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	// 是否更换区域
	private boolean changearea = false;

	// 无数据
	private TextView tv_notdata;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取数据失败
	private static final int HANDLER_GETIONFO_FAILURE = 2;
	// 定位完成
	protected static final int HANDLER_DINGWEI_FINISH = 3;
	protected static final int HANDLER_DINGWEI_SUCCESS = 11;
	// 两个专题获取成功
	private static final int HANDLER_CCQTWOZUANTI_SUCCESS = 4;
	// 餐餐抢券获取成功
	private static final int HANDLER_CCQGOODSLIST_SUCCESS = 5;
	// 餐餐抢首页顶部获取成功
	private static final int HANDLER_CCQSYTOP_SUCCESS = 6;
	// 餐餐抢店铺获取成功
	private static final int HANDLER_CCQSHOPLIST_SUCCESS = 7;
	// 更换区域后获取店铺数据
	private static final int HANDLER_CCQSHOPDETAI_SUCCESS = 8;
	protected static final int HANDLER_CONFIG_SUCCESS = 9;
	private static final int HANDLER_NODATA_FAILURE = 10;
	// 当前位置
	private TextView tv_now_place;
	private LinearLayout line_dwshuaxin;
	private ListView lv;

	private CcqJuanLvAdapter ccqJuanLvAdapter;
	private CcqShopLvAdapter ccqShopLvAdapter;

	private DBHelper myDB;
	private boolean isDw = false;
	// 经 度
	private String longitude = "";
	// 纬 度
	private String latitude = "";
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
			case HANDLER_DINGWEI_FINISH:
				stopLocation();
				ccqListHttpPost();
				tv_now_place.setText("定位失败，重新定位或者手动切换城市");
				break;
			case HANDLER_DINGWEI_SUCCESS:
				ccqListHttpPost();
				stopLocation();
				break;
			case HANDLER_CCQTWOZUANTI_SUCCESS:
				line_net_error.setVisibility(View.GONE);
				dissDialog();
				bitmapUtils.display(iv_zuanti_one, ccqtwozuantiListdetaiList
						.get(0).get("icon"));
				bitmapUtils.display(iv_zuanti_two, ccqtwozuantiListdetaiList
						.get(1).get("icon"));
				bitmapUtils.display(iv_zuanti_three, ccqtwozuantiListdetaiList
						.get(2).get("icon"));
				break;
			case HANDLER_CCQGOODSLIST_SUCCESS:
				line_net_error.setVisibility(View.GONE);
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				ccqJuanLvAdapter.NotifyList(ccqgoodsListdetaiList);
				ccqJuanLvAdapter.notifyDataSetChanged();
				break;
			case HANDLER_CCQSYTOP_SUCCESS:
				dissDialog();
				line_net_error.setVisibility(View.GONE);
				tv_food.setText(ccqsytopListdetaiList.get(0).get("title"));
				tv_hotel.setText(ccqsytopListdetaiList.get(1).get("title"));
				tv_play.setText(ccqsytopListdetaiList.get(2).get("title"));
				tv_beauty.setText(ccqsytopListdetaiList.get(3).get("title"));
				tv_tuijian.setText(ccqsytopListdetaiList.get(4).get("title"));
				tv_vip.setText(ccqsytopListdetaiList.get(5).get("title"));
				tv_huiyuan.setText(ccqsytopListdetaiList.get(6).get("title"));
				tv_more.setText(ccqsytopListdetaiList.get(7).get("title"));

				bitmapUtils.display(iv_food,
						ccqsytopListdetaiList.get(0).get("icon"));
				bitmapUtils.display(iv_hotel,
						ccqsytopListdetaiList.get(1).get("icon"));
				bitmapUtils.display(iv_play,
						ccqsytopListdetaiList.get(2).get("icon"));
				bitmapUtils.display(iv_beauty, ccqsytopListdetaiList.get(3)
						.get("icon"));
				bitmapUtils.display(iv_tuijian, ccqsytopListdetaiList.get(4)
						.get("icon"));
				bitmapUtils.display(iv_vip,
						ccqsytopListdetaiList.get(5).get("icon"));
				bitmapUtils.display(iv_huiyuan, ccqsytopListdetaiList.get(6)
						.get("icon"));
				bitmapUtils.display(iv_more,
						ccqsytopListdetaiList.get(7).get("icon"));
				break;
			case HANDLER_CCQSHOPLIST_SUCCESS:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				line_net_error.setVisibility(View.GONE);
				ccqShopLvAdapter.NotifyList(ccqshopListdetaiList);
				ccqShopLvAdapter.notifyDataSetChanged();
				break;
			case HANDLER_CCQSHOPDETAI_SUCCESS:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				line_net_error.setVisibility(View.GONE);
				ccqShopLvAdapter.NotifyList(ccqshopListdetaiList);
				ccqShopLvAdapter.notifyDataSetChanged();
				break;
			// 没有数据
			case HANDLER_NODATA_FAILURE:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				line_net_error.setVisibility(View.GONE);
				if ("juan".equals(type)) {
					if (juannowPage == 1) {
						ccqJuanLvAdapter.NotifyList(ccqgoodsListdetaiList);
						ccqJuanLvAdapter.notifyDataSetChanged();
						ccqShopLvAdapter.NotifyList(ccqshopListdetaiList);
						ccqShopLvAdapter.notifyDataSetChanged();
					}
				} else {
					if (shopnowPage == 1) {
						ccqJuanLvAdapter.NotifyList(ccqgoodsListdetaiList);
						ccqJuanLvAdapter.notifyDataSetChanged();
						ccqShopLvAdapter.NotifyList(ccqshopListdetaiList);
						ccqShopLvAdapter.notifyDataSetChanged();
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
			// 配置文件完成
			case HANDLER_CONFIG_SUCCESS:
				// ccqListHttpPost();
				initLocation();
				break;

			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_ccq1, null);

		View topView = LayoutInflater.from(getActivity()).inflate(
				R.layout.view_fragment_ccq1_list_top, null);
		line_net_error = (LinearLayout) topView
				.findViewById(R.id.line_net_error);
		TextView tv_notnet_refresh = (TextView) topView
				.findViewById(R.id.tv_notnet_refresh);
		scrollView = (MyScrollView) topView.findViewById(R.id.scrollView);
		tv_notdata = (TextView) topView.findViewById(R.id.tv_notdata);
		// 选择城市
		LinearLayout line_city = (LinearLayout) topView
				.findViewById(R.id.line_city);
		tv_city = (TextView) topView.findViewById(R.id.tv_city);
		// 搜索
		LinearLayout line_search = (LinearLayout) topView
				.findViewById(R.id.line_search);
		// 扫一扫
		LinearLayout line_scans = (LinearLayout) topView
				.findViewById(R.id.line_scans);
		// 消息
		LinearLayout line_message = (LinearLayout) topView
				.findViewById(R.id.line_message);
		// 美食
		LinearLayout line_food = (LinearLayout) topView
				.findViewById(R.id.line_food);

		tv_food = (TextView) topView.findViewById(R.id.tv_food);
		iv_food = (ImageView) topView.findViewById(R.id.iv_food);

		// 丽人
		LinearLayout line_beauty = (LinearLayout) topView
				.findViewById(R.id.line_beauty);
		tv_beauty = (TextView) topView.findViewById(R.id.tv_beauty);
		iv_beauty = (ImageView) topView.findViewById(R.id.iv_beauty);

		// 酒店
		LinearLayout line_hotel = (LinearLayout) topView
				.findViewById(R.id.line_hotel);
		tv_hotel = (TextView) topView.findViewById(R.id.tv_hotel);
		iv_hotel = (ImageView) topView.findViewById(R.id.iv_hotel);

		// 休闲娱乐
		LinearLayout line_play = (LinearLayout) topView
				.findViewById(R.id.line_play);
		tv_play = (TextView) topView.findViewById(R.id.tv_play);
		iv_play = (ImageView) topView.findViewById(R.id.iv_play);

		// 推荐注册
		LinearLayout line_tuijian = (LinearLayout) topView
				.findViewById(R.id.line_tuijian);
		tv_tuijian = (TextView) topView.findViewById(R.id.tv_tuijian);
		iv_tuijian = (ImageView) topView.findViewById(R.id.iv_tuijian);

		// VIP
		LinearLayout line_vip = (LinearLayout) topView
				.findViewById(R.id.line_vip);
		tv_vip = (TextView) topView.findViewById(R.id.tv_vip);
		iv_vip = (ImageView) topView.findViewById(R.id.iv_vip);

		// 会员
		LinearLayout line_huiyuan = (LinearLayout) topView
				.findViewById(R.id.line_huiyuan);
		tv_huiyuan = (TextView) topView.findViewById(R.id.tv_huiyuan);
		iv_huiyuan = (ImageView) topView.findViewById(R.id.iv_huiyuan);

		// 更多
		LinearLayout line_more = (LinearLayout) topView
				.findViewById(R.id.line_more);
		tv_more = (TextView) topView.findViewById(R.id.tv_more);
		iv_more = (ImageView) topView.findViewById(R.id.iv_more);

		iv_zuanti_one = (ImageView) topView.findViewById(R.id.iv_zuanti_one);
		iv_zuanti_two = (ImageView) topView.findViewById(R.id.iv_zuanti_two);
		iv_zuanti_three = (ImageView) topView
				.findViewById(R.id.iv_zuanti_three);

		tv_now_place = (TextView) topView.findViewById(R.id.tv_now_place);
		line_dwshuaxin = (LinearLayout) topView
				.findViewById(R.id.line_dwshuaxin);

		// 热门餐餐抢
		RadioButton radio_ccq = (RadioButton) topView
				.findViewById(R.id.radio_ccq);
		// 附近的商家
		RadioButton radio_store = (RadioButton) topView
				.findViewById(R.id.radio_store);
		lv = (ListView) view.findViewById(R.id.lv);

		myDB = new DBHelper(getActivity());

		ccqtwozuantiListdetaiList = new ArrayList<Map<String, String>>();
		ccqgoodsListdetaiList = new ArrayList<Map<String, String>>();
		ccqsytopListdetaiList = new ArrayList<Map<String, String>>();
		ccqshopListdetaiList = new ArrayList<Map<String, String>>();

		ccqJuanLvAdapter = new CcqJuanLvAdapter(getActivity());
		ccqShopLvAdapter = new CcqShopLvAdapter(getActivity());
		lv.setAdapter(ccqJuanLvAdapter);
		refreshLayout = (MaterialRefreshLayout) view
				.findViewById(R.id.refresh_layout);
		refreshLayout.setLoadMore(true);
		lv.addHeaderView(topView);

		shi = tv_city.getText().toString().trim();

		httpUtils = new HttpUtils();
		Utils utils = new Utils();
		verstring = utils.getVersionInfo(getActivity());

		getResolution();

		showLoading();
		// 配置文件
		httpPost();

		// 初始化定位
		// initLocation();

		// 餐餐抢店铺列表
		// ccqshopHttpPost();

		String checkupdata = SharedUtil.getStringValue(
				SharedCommon.CHECKUPDATA, "");
		if ("yes".equals(checkupdata)) {
			cu = new CheckUpdate(getActivity());
			cu.StartCheck();
		}

		bitmapUtils = new BitmapUtils(getActivity());
		// juan_lv.setOnItemClickListener(this);
		lv.setOnItemClickListener(this);
		line_city.setOnClickListener(this);
		line_search.setOnClickListener(this);
		line_scans.setOnClickListener(this);
		line_message.setOnClickListener(this);
		tv_notnet_refresh.setOnClickListener(this);
		line_food.setOnClickListener(this);
		line_beauty.setOnClickListener(this);
		line_hotel.setOnClickListener(this);
		line_play.setOnClickListener(this);
		line_tuijian.setOnClickListener(this);
		line_vip.setOnClickListener(this);
		line_huiyuan.setOnClickListener(this);
		line_more.setOnClickListener(this);
		iv_zuanti_one.setOnClickListener(this);
		iv_zuanti_two.setOnClickListener(this);
		iv_zuanti_three.setOnClickListener(this);
		radio_ccq.setOnClickListener(this);
		radio_store.setOnClickListener(this);
		line_dwshuaxin.setOnClickListener(this);
		// juan_lv.setFocusable(false);
		lv.setFocusable(false);

		initListView();

		return view;
	}

	private String resolution = "2";// 手机屏幕分辨率

	// 获取屏幕分辨率 判断需要什么样的图
	private String getResolution() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		// params.addBodyParameter("sh", "test1");
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		httpUtils.send(HttpMethod.POST, JiekouUtils.PEIZHI, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e("ee", "PEIZHI" + JiekouUtils.PEIZHI);
						Log.e("ee", "网络错误:" + arg1);
						// handler.sendMessage(handler.obtainMessage(
						// HANDLER_NET_FAILURE1, arg1));
						handler.sendEmptyMessage(HANDLER_CONFIG_SUCCESS);

					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						peizhiJsonInfo(arg0.result);
					}
				});

	}

	// 获取配置文件接口返回
	protected void peizhiJsonInfo(String result) {
		// TODO Auto-generated method stub
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 餐餐抢列表
	protected void ccqListHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("lat1", latitude);
		params.addBodyParameter("lng1", longitude);
		params.addBodyParameter("city_id", tv_city.getText().toString().trim());
		// 配iocn类型 安卓mdpi图:1 安卓hdpi图:2 安卓xhdpi图:3 安卓xxhdpi图:4
		params.addBodyParameter("type", resolution);
		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWCCQSY, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e("ee", "PEIZHIlist" + JiekouUtils.NEWCCQSY);
						Log.e("ee", "网络错误:list" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						ccqJsonInfo(arg0.result);
					}
				});
	}

	// 餐餐抢列表数据解析
	protected void ccqJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				JSONObject job = object.getJSONObject("data");
				// 两个专题
				JSONArray b2 = job.getJSONArray("b2");
				// 商品条目
				JSONArray goods_list = job.getJSONArray("goods_list");
				// 顶部分类
				JSONArray header = job.getJSONArray("header");
				// 店铺条目
				JSONArray unionstore_list = job.getJSONArray("unionstore_list");

				for (int i = 0; i < b2.length(); i++) {
					JSONObject json = b2.getJSONObject(i);
					b2ListInfo(json);
				}
				ccqgoodsListdetaiList.clear();
				for (int i = 0; i < goods_list.length(); i++) {
					JSONObject json = goods_list.getJSONObject(i);
					goods_listListInfo(json);
				}
				ccqsytopListdetaiList.clear();
				for (int i = 0; i < header.length(); i++) {
					JSONObject json = header.getJSONObject(i);
					headerListInfo(json);
				}
				ccqshopListdetaiList.clear();
				for (int i = 0; i < unionstore_list.length(); i++) {
					JSONObject json = unionstore_list.getJSONObject(i);
					unionstore_listListInfo(json);
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

	// 两个专题
	private void b2ListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String icon = json.getString("icon");
			String title = json.getString("title");
			Map<String, String> ccqtwozuantiDetailMap = new HashMap<String, String>();
			ccqtwozuantiDetailMap.put("icon", icon);
			ccqtwozuantiDetailMap.put("title", title);
			ccqtwozuantiListdetaiList.add(ccqtwozuantiDetailMap);
			handler.sendEmptyMessage(HANDLER_CCQTWOZUANTI_SUCCESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 餐餐抢券列表详细
	private void goods_listListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String discount = json.getString("discount");
			String goods_id = json.getString("goods_id");
			String goods_image = json.getString("goods_image");
			String goods_marketprice = json.getString("goods_marketprice");
			String goods_name = json.getString("goods_name");
			String goods_price = json.getString("goods_price");
			String store_address = json.getString("store_address");
			String store_id = json.getString("store_id");
			String store_name = json.getString("store_name");
			Map<String, String> ccqgoodsDetailMap = new HashMap<String, String>();
			ccqgoodsDetailMap.put("discount", discount);
			ccqgoodsDetailMap.put("goods_id", goods_id);
			ccqgoodsDetailMap.put("goods_image", goods_image);
			ccqgoodsDetailMap.put("goods_marketprice", goods_marketprice);
			ccqgoodsDetailMap.put("goods_name", goods_name);
			ccqgoodsDetailMap.put("goods_price", goods_price);
			ccqgoodsDetailMap.put("store_address", store_address);
			ccqgoodsDetailMap.put("store_id", store_id);
			ccqgoodsDetailMap.put("store_name", store_name);
			ccqgoodsListdetaiList.add(ccqgoodsDetailMap);
			handler.sendEmptyMessage(HANDLER_CCQGOODSLIST_SUCCESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 餐餐抢首页顶部分类
	private void headerListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String icon = json.getString("icon");
			String sc_id_1 = json.getString("sc_id_1");
			String title = json.getString("title");
			String url = json.getString("url");
			Map<String, String> ccqsytopDetailMap = new HashMap<String, String>();
			ccqsytopDetailMap.put("icon", icon);
			ccqsytopDetailMap.put("sc_id_1", sc_id_1);
			ccqsytopDetailMap.put("title", title);
			ccqsytopDetailMap.put("url", url);
			ccqsytopListdetaiList.add(ccqsytopDetailMap);
			handler.sendEmptyMessage(HANDLER_CCQSYTOP_SUCCESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 餐餐抢店铺列表详细
	private void unionstore_listListInfo(JSONObject json) {
		// TODO Auto-generated method stub
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
			Map<String, String> ccqshopDetailMap = new HashMap<String, String>();
			ccqshopDetailMap.put("city_id", city_id);
			ccqshopDetailMap.put("county_id", county_id);
			ccqshopDetailMap.put("distance", distance);
			ccqshopDetailMap.put("distance_value", distance_value);
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
			ccqshopListdetaiList.add(ccqshopDetailMap);
			handler.sendEmptyMessage(HANDLER_CCQSHOPLIST_SUCCESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
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
		locationClient = new AMapLocationClient(getActivity()
				.getApplicationContext());
		// 设置定位参数
		locationClient.setLocationOption(getDefaultOption());
		// 设置定位监听
		locationClient.setLocationListener(locationListener);

		// 定位
		startLocation();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
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
			intent.putExtra("city_id", tv_city.getText().toString().trim());
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
		case R.id.line_food:
			intent = new Intent(getActivity(), CcqTagDetailActivity.class);
			intent.putExtra("city_id", tv_city.getText().toString().trim());
			intent.putExtra("sc_id_1",
					ccqsytopListdetaiList.get(0).get("sc_id_1"));
			intent.putExtra("title", ccqsytopListdetaiList.get(0).get("title"));
			startActivity(intent);

			break;
		case R.id.line_hotel:
			intent = new Intent(getActivity(), CcqTagDetailActivity.class);
			intent.putExtra("city_id", tv_city.getText().toString().trim());
			intent.putExtra("sc_id_1",
					ccqsytopListdetaiList.get(1).get("sc_id_1"));
			intent.putExtra("title", ccqsytopListdetaiList.get(1).get("title"));
			startActivity(intent);

			break;
		case R.id.line_play:
			intent = new Intent(getActivity(), CcqTagDetailActivity.class);
			intent.putExtra("city_id", tv_city.getText().toString().trim());
			intent.putExtra("sc_id_1",
					ccqsytopListdetaiList.get(2).get("sc_id_1"));
			intent.putExtra("title", ccqsytopListdetaiList.get(2).get("title"));
			startActivity(intent);

			break;
		case R.id.line_beauty:
			intent = new Intent(getActivity(), CcqTagDetailActivity.class);
			intent.putExtra("city_id", tv_city.getText().toString().trim());
			intent.putExtra("sc_id_1",
					ccqsytopListdetaiList.get(3).get("sc_id_1"));
			intent.putExtra("title", ccqsytopListdetaiList.get(3).get("title"));
			startActivity(intent);

			break;
		case R.id.line_tuijian:
			if (isLogin()) {
				intent = new Intent(getActivity(), YqzcActivity.class);
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("dbdh", "ccq");
				startActivity(intent);
			}

			break;
		case R.id.line_vip:
			intent = new Intent(getActivity(), WebviewActivity.class);
			String url = ccqsytopListdetaiList.get(5).get("url");
			intent.putExtra("url", url);
			intent.putExtra("title", "VIP会员专区");
			startActivity(intent);

			break;
		case R.id.line_huiyuan:
			((MainActivity1) getActivity()).changeFragment();

			break;
		case R.id.line_more:

			break;
		case R.id.iv_zuanti_one:
			if (isLogin()) {
				intent = new Intent(getActivity(),
						NewHuiyuanGetVipActivity.class);
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("dbdh", "ccq");
				startActivity(intent);
			}
			break;
		case R.id.iv_zuanti_two:
			intent = new Intent(getActivity(), ChaoZhiQuanActivity.class);
			intent.putExtra("starlat", latitude);
			intent.putExtra("starlng", longitude);
			intent.putExtra("city_id", tv_city.getText().toString().trim());
			startActivity(intent);
			break;
		case R.id.iv_zuanti_three:
			intent = new Intent(getActivity(), BaWangCanActivity.class);
			intent.putExtra("city_id", tv_city.getText().toString().trim());
			intent.putExtra("starlat", latitude);
			intent.putExtra("starlng", longitude);
			startActivity(intent);
			break;
		case R.id.radio_ccq:
			type = "juan";
			lv.setAdapter(ccqJuanLvAdapter);
			break;
		case R.id.radio_store:
			type = "shop";
			lv.setAdapter(ccqShopLvAdapter);
			break;
		case R.id.line_dwshuaxin:
			juannowPage = 1;
			shopnowPage = 1;
			showLoading();
			startLocation();
			break;

		default:
			break;
		}
	}

	private int juannowPage = 1;
	private int shopnowPage = 1;

	private void getJuanListHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("lat1", latitude);
		params.addBodyParameter("lng1", longitude);
		params.addBodyParameter("city_id", tv_city.getText().toString().trim());
		params.addBodyParameter("page", juannowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWSY_CANCANQIANGJUAN,
				params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						ccqJuanListJsonInfo(arg0.result);
					}
				});
	}

	protected void ccqJuanListJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				// 商品条目
				if (juannowPage == 1) {
					ccqgoodsListdetaiList.clear();
				}
				JSONArray array = object.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = array.getJSONObject(i);
					juan_listListInfo(json);
				}

			} else if ("300".equals(status)) {
				String message = object.getString("message");
				if (juannowPage == 1) {
					ccqgoodsListdetaiList.clear();
					// juan_lv.setVisibility(View.GONE);
				} else {
					// juan_lv.setVisibility(View.VISIBLE);
				}
				handler.sendMessage(handler.obtainMessage(
						HANDLER_NODATA_FAILURE, message));
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

	// 餐餐抢券列表详细
	private void juan_listListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String discount = json.getString("discount");
			String goods_id = json.getString("goods_id");
			String goods_image = json.getString("goods_image");
			String goods_marketprice = json.getString("goods_marketprice");
			String goods_name = json.getString("goods_name");
			String goods_price = json.getString("goods_price");
			String store_address = json.getString("store_address");
			String store_id = json.getString("store_id");
			String store_name = json.getString("store_name");
			Map<String, String> ccqgoodsDetailMap = new HashMap<String, String>();
			ccqgoodsDetailMap.put("discount", discount);
			ccqgoodsDetailMap.put("goods_id", goods_id);
			ccqgoodsDetailMap.put("goods_image", goods_image);
			ccqgoodsDetailMap.put("goods_marketprice", goods_marketprice);
			ccqgoodsDetailMap.put("goods_name", goods_name);
			ccqgoodsDetailMap.put("goods_price", goods_price);
			ccqgoodsDetailMap.put("store_address", store_address);
			ccqgoodsDetailMap.put("store_id", store_id);
			ccqgoodsDetailMap.put("store_name", store_name);
			ccqgoodsListdetaiList.add(ccqgoodsDetailMap);
			handler.sendEmptyMessage(HANDLER_CCQGOODSLIST_SUCCESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 获取餐餐抢店铺列表数据
	private void getStoreListHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("lat1", latitude);
		params.addBodyParameter("lng1", longitude);
		params.addBodyParameter("city_id", tv_city.getText().toString().trim());
		params.addBodyParameter("page", shopnowPage + "");
		// params.addBodyParameter("page_size", "8");
		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWSY_CANCANQIANGSHOP,
				params, new RequestCallBack<String>() {

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
				if (shopnowPage == 1) {
					ccqshopListdetaiList.clear();
				}
				JSONArray array = object.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = array.getJSONObject(i);
					ccqshopDetailListInfo(json);
				}

			} else if ("300".equals(status)) {
				String message = object.getString("message");
				if (shopnowPage == 1) {
					// lv.setVisibility(View.GONE);
					ccqshopListdetaiList.clear();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private void ccqshopDetailListInfo(JSONObject json) {
		// TODO Auto-generated method stub
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
			ccqshopListdetaiList.add(ccqshopDetailMap);
			handler.sendEmptyMessage(HANDLER_CCQSHOPDETAI_SUCCESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
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
					// Toast.makeText(getActivity(), "longitude...."+longitude,
					// Toast.LENGTH_SHORT)
					// .show();
					// 市

					String allshi = loc.getCity();
					if (!choosecityxz) {
						if (!TextUtils.isEmpty(allshi)) {
							dwshi = allshi.replaceAll("市", "");
							shi = allshi.replaceAll("市", "");
							String street = loc.getStreet();
							tv_city.setText(shi.trim());
						}
					}
					tv_now_place.setText(loc.getCity() + loc.getDistrict());
					handler.sendEmptyMessage(HANDLER_DINGWEI_SUCCESS);
				} else {
					// 定位失败
					Toast.makeText(getActivity(), "定位失败", Toast.LENGTH_SHORT)
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
	private Dialog qiehuanCityDialog;

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

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		if ("shop".equals(type)) {
			int index = position - 1;
			Intent intent = new Intent(getActivity(),
					OldCCQStoreDetailActivity.class);
			intent.putExtra("starlat", latitude);
			intent.putExtra("starlng", longitude);
			intent.putExtra("store_id",
					ccqshopListdetaiList.get(index).get("store_id"));
			startActivity(intent);
		} else if ("juan".equals(type)) {
			int juanindex = position - 1;
			Intent intent = new Intent(getActivity(),
					CcqProDetailActivity.class);
			intent.putExtra("starlat", latitude);
			intent.putExtra("starlng", longitude);
			intent.putExtra("ccqgoods_id", ccqgoodsListdetaiList.get(juanindex)
					.get("goods_id"));
			startActivity(intent);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == 300) {

			final String getcity = data.getStringExtra("city");
			final String reslatitude = data.getStringExtra("latitude");
			final String reslongitude = data.getStringExtra("longitude");
			choosecityxz = true;
			juannowPage = 1;
			shopnowPage = 1;
			latitude = reslatitude;
			longitude = reslongitude;

			if (!TextUtils.isEmpty(dwshi)) {

				if (getcity.equals(dwshi)) {
					qiehuanCityDialog.dismiss();
					tv_city.setText(getcity);
					shi = getcity;
					showLoading();
					getJuanListHttpPost();
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
							// TODO Auto-generated method stub
							qiehuanCityDialog.dismiss();
							tv_city.setText(dwshi);
							latitude = reslatitude;
							longitude = reslongitude;
							shi = dwshi;
							showLoading();
							getJuanListHttpPost();
							getStoreListHttpPost();
						}
					});

					tv_ok.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							qiehuanCityDialog.dismiss();
							tv_city.setText(getcity);
							shi = getcity;
							showLoading();
							getJuanListHttpPost();
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
				latitude = reslatitude;
				longitude = reslongitude;
				shi = getcity;
				showLoading();
				getJuanListHttpPost();
				getStoreListHttpPost();
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
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
			// TODO: handle exception
		}
		;

		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(getActivity());
	}

	/**
	 * @description 填充底部商品listview
	 **/
	private void initListView() {
		refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {

			@Override
			public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
				// TODO Auto-generated method stub
				// zheli shua xin
				refreshLayout.finishRefresh();
			}

			@Override
			public void onRefreshLoadMore(
					MaterialRefreshLayout materialRefreshLayout) {
				// TODO Auto-generated method stub
				// super.onRefreshLoadMore(materialRefreshLayout);
				try {
					if ("juan".equals(type)) {
						juannowPage++;
						getJuanListHttpPost();
					} else {
						shopnowPage++;
						getStoreListHttpPost();
					}
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(HANDLER_NN_FAILURE);
				}
			}
		});
	}

}
