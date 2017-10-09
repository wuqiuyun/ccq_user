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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.HotSellingHorizontalListViewAdapter;
import ccj.yun28.com.adapter.LikeGridAdapter;
import ccj.yun28.com.adapter.NewProductHorizontalListViewAdapter;
import ccj.yun28.com.adapter.PptjMyGridAdapter;
import ccj.yun28.com.ccq.CCQStoreDetailActivity;
import ccj.yun28.com.ccq.OldCCQStoreDetailActivity;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.lunbotu.ADInfo;
import ccj.yun28.com.lunbotu.ImageCycleView;
import ccj.yun28.com.lunbotu.ImageCycleView.ImageCycleViewListener;
import ccj.yun28.com.sy.FenleiActivity;
import ccj.yun28.com.sy.MessageActivcity;
import ccj.yun28.com.sy.ProductDetailActivity;
import ccj.yun28.com.sy.SaoYiSaoActivity;
import ccj.yun28.com.sy.SearchActivity;
import ccj.yun28.com.sy.SearchProductResultActivity;
import ccj.yun28.com.sy.YqzcActivity;
import ccj.yun28.com.utils.ImageUtil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.HorizontalListView;
import ccj.yun28.com.view.MyGridView;
import ccj.yun28.com.view.MyScrollView;
import ccj.yun28.com.view.OnRefreshListener;
import ccj.yun28.com.view.WebviewActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol;
import com.amap.api.location.AMapLocationListener;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.umeng.analytics.MobclickAgent;

/**
 * 首页
 * 
 * @author meihuali
 * 
 */
public class SYFragment extends Fragment implements OnClickListener,
		OnItemClickListener {
	
	private int nowPage = 1;
	// 换一换 页数
	private int page = 1;
	// 轮播图
	private ImageCycleView ad_view;

	private MyScrollView scrollView;

	// 五个分类
	private TextView tv_yqzc;
	private TextView tv_vipzq;
	private TextView tv_cfcy;
	private TextView tv_fzc;
	private TextView tv_fl;
	// 超值商品
	private ImageView iv_cz1;
	private ImageView iv_cz2;
	private ImageView iv_cz_big;
	// 好店推荐
	private TextView tv_hd_name1;
	private ImageView iv_hd1;
	private TextView tv_hd_name2;
	private ImageView iv_hd2;
	private TextView tv_hd_name3;
	private ImageView iv_hd3;
	private TextView tv_hd_name4;
	private ImageView iv_hd4;
	private ImageView iv_hd_big;
	// 人气商品
	private ImageView iv_rq1;
	private ImageView iv_rq2;
	private TextView tv_rq_name3;
	private ImageView iv_rq3;
	private TextView tv_rq_name4;
	private ImageView iv_rq4;
	private TextView tv_rq_name5;
	private ImageView iv_rq5;
	private TextView tv_rq_name6;
	private ImageView iv_rq6;

	// 新品推荐
	private HorizontalListView new_horizontallistview;
	private NewProductHorizontalListViewAdapter nphlvadapter;
	// 热销推荐
	private TextView tv_refresh;
	private HorizontalListView rx_horizontallistview;
	private HotSellingHorizontalListViewAdapter hshlvadapter;
	// 品牌专区
	private MyGridView gv_pp;
	private PptjMyGridAdapter mygridadapter;
	// 猜我喜欢
	private MyGridView gv_like;
	private LikeGridAdapter likegridadapter;

	// 横条 goods_id
	private String hd_goods_id;
	private String cz_goods_id;

	private BitmapUtils bitmapUtils;

	private DBHelper myDB;

	private List<Map<String, String>> brandList;
	private List<Map<String, String>> hot_sellingList;
	private List<Map<String, String>> new_goodsList;
	private List<Map<String, String>> worth_buyingList;
	private List<Map<String, String>> popularityList;
	private List<Map<String, String>> storeList;
	private List<Map<String, String>> guesslikeList;
	private List<Map<String, String>> sy_daohangList;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	protected static final int HANDLER_NET_FAILURE1 = 100;
	// 轮播图获取成功
	private static final int HANDLER_LBT_SUCCESS = 1;
	// 轮播图获取失败
	private static final int HANDLER_LBT_FAILURE = 2;
	// 错误
	private static final int HANDLER_NN_FAILURE = 3;
	private static final int HANDLER_BRAND_SUCCESS = 4;
	private static final int HANDLER_HOT_SELLING_SUCCESS = 5;
	private static final int HANDLER_NEW_GOODS_SUCCESS = 6;
	private static final int HANDLER_WORTH_BUYING_SUCCESS = 7;
	private static final int HANDLER_POPULARITY_SUCCESS = 8;
	private static final int HANDLER_STORE_SUCCESS = 9;
	private static final int HANDLER_GUESSLIKE_SUCCESS = 10;

	private static final int HANDLER_SY_DAOHANG_SUCCESS = 11;
	// 配置文件完成
	private static final int HANDLER_CONFIG_SUCCESS = 12;
	// 首页数据完成
	private static final int HANDLER_SYINFO_SUCCESS = 13;
	// 首页数据失败
	private static final int HANDLER_SYINFO_FAILURE = 14;
	private static final int HANDLER_GETINFO_FAILURE = 15;
	// 定位完成
	private static final int HANDLER_DINGWEI_FINISH = 16;
	// 没有更多数据
	private static final int HANDLER_NO_MORE = 17;

	private ArrayList<ADInfo> infos = new ArrayList<ADInfo>();

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				scrollView.onResfreshFinish();
				dissDialog();
				line_net_error.setVisibility(View.VISIBLE);
				if (getActivity() != null) {

					Toast.makeText(getActivity(), "当前网络不可用,请检查网络", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			case HANDLER_NET_FAILURE1:
				scrollView.onResfreshFinish();
				dissDialog();
				line_net_error.setVisibility(View.VISIBLE);
				if (getActivity() != null) {
					if (msg.obj != null) {
						Toast.makeText(getActivity(),
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();
					}

				}
				break;
			// 首页数据完成
			case HANDLER_SYINFO_SUCCESS:
				dissDialog();
				line_net_error.setVisibility(View.GONE);
				break;
				// 没有更多数据
			case HANDLER_NO_MORE:
				scrollView.onResfreshFinish();
				dissDialog();
				if (getActivity() != null) {
					if (msg.obj != null) {
						Toast.makeText(getActivity(),
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();
					}

				}
				break;
			case HANDLER_SYINFO_FAILURE:
				dissDialog();
				line_net_error.setVisibility(View.VISIBLE);
				if (getActivity() != null) {

					if (msg.obj != null) {
						Toast.makeText(getActivity(),
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();
					}
				}
				break;
			// 轮播图获取成功
			case HANDLER_LBT_SUCCESS:
				line_net_error.setVisibility(View.GONE);
				try {
					// 下载轮播图成功
					if (infos.size() > 0) {
						ad_view.setVisibility(View.VISIBLE);
						ad_view.setImageResources(infos, mAdCycleViewListener);
					} else {
						ad_view.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(HANDLER_NN_FAILURE);
				}

				break;
			// 异常
			case HANDLER_NN_FAILURE:
				scrollView.onResfreshFinish();
				dissDialog();
				line_net_error.setVisibility(View.VISIBLE);
				if (getActivity() != null) {

					Toast.makeText(getActivity(), "当前网络出错,请检查网络", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// 品牌专区
			case HANDLER_BRAND_SUCCESS:
				line_net_error.setVisibility(View.GONE);
				tv_ppzq_name1.setText(brandList.get(0).get("data"));
				tv_ppzq_name2.setText(brandList.get(1).get("data"));
				tv_ppzq_name3.setText(brandList.get(2).get("data"));
				tv_ppzq_name4.setText(brandList.get(3).get("data"));
				tv_ppzq_name5.setText(brandList.get(4).get("data"));
				tv_ppzq_name6.setText(brandList.get(5).get("data"));
				tv_ppzq_name7.setText(brandList.get(6).get("data"));
				tv_ppzq_name8.setText(brandList.get(7).get("data"));

				bitmapUtils.display(iv_ppzq1, brandList.get(0).get("image"));
				bitmapUtils.display(iv_ppzq2, brandList.get(1).get("image"));
				bitmapUtils.display(iv_ppzq3, brandList.get(2).get("image"));
				bitmapUtils.display(iv_ppzq4, brandList.get(3).get("image"));
				bitmapUtils.display(iv_ppzq5, brandList.get(4).get("image"));
				bitmapUtils.display(iv_ppzq6, brandList.get(5).get("image"));
				bitmapUtils.display(iv_ppzq7, brandList.get(6).get("image"));
				bitmapUtils.display(iv_ppzq8, brandList.get(7).get("image"));
				// mygridadapter.NotifyList(brandList);
				break;
			// 热销商品
			case HANDLER_HOT_SELLING_SUCCESS:
				line_net_error.setVisibility(View.GONE);
				hshlvadapter.NotifyList(hot_sellingList);
				break;
			// 新品推荐
			case HANDLER_NEW_GOODS_SUCCESS:
				line_net_error.setVisibility(View.GONE);
				nphlvadapter.NotifyList(new_goodsList);
				break;
			// 超值商品
			case HANDLER_WORTH_BUYING_SUCCESS:
				line_net_error.setVisibility(View.GONE);
				bitmapUtils.display(iv_cz1, worth_buyingList.get(0)
						.get("image"));
				bitmapUtils.display(iv_cz2, worth_buyingList.get(1)
						.get("image"));
				break;
			// 人气商品
			case HANDLER_POPULARITY_SUCCESS:
				line_net_error.setVisibility(View.GONE);
				bitmapUtils.display(iv_rq1, popularityList.get(0).get("image"));
				bitmapUtils.display(iv_rq2, popularityList.get(1).get("image"));
				bitmapUtils.display(iv_rq3, popularityList.get(2).get("image"));
				bitmapUtils.display(iv_rq4, popularityList.get(3).get("image"));
				bitmapUtils.display(iv_rq5, popularityList.get(4).get("image"));
				bitmapUtils.display(iv_rq6, popularityList.get(5).get("image"));
				break;
			// 好店推荐
			case HANDLER_STORE_SUCCESS:
				line_net_error.setVisibility(View.GONE);
				tv_hd_name1.setText(storeList.get(0).get("data"));
				bitmapUtils.display(iv_hd1, storeList.get(0).get("image"));
				tv_hd_name2.setText(storeList.get(1).get("data"));
				bitmapUtils.display(iv_hd2, storeList.get(1).get("image"));
				tv_hd_name3.setText(storeList.get(2).get("data"));
				bitmapUtils.display(iv_hd3, storeList.get(2).get("image"));
				tv_hd_name4.setText(storeList.get(3).get("data"));
				bitmapUtils.display(iv_hd4, storeList.get(3).get("image"));
				break;
			// 猜你喜欢
			case HANDLER_GUESSLIKE_SUCCESS:
				line_net_error.setVisibility(View.GONE);
				scrollView.onResfreshFinish();
				likegridadapter.NotifyList(guesslikeList);
				likegridadapter.notifyDataSetChanged();
				break;
			case HANDLER_DINGWEI_FINISH:
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
		View view = inflater.inflate(R.layout.fragment_sy, null);
		LinearLayout line_sys = (LinearLayout) view.findViewById(R.id.line_sys);
		LinearLayout line_search = (LinearLayout) view
				.findViewById(R.id.line_search);
		LinearLayout line_message = (LinearLayout) view
				.findViewById(R.id.line_message);
		line_net_error = (LinearLayout) view
				.findViewById(R.id.line_net_error);
		TextView tv_notnet_refresh = (TextView) view.findViewById(R.id.tv_notnet_refresh);
		
		scrollView = (MyScrollView) view.findViewById(R.id.scrollView);

		// 轮播图
		ad_view = (ImageCycleView) view.findViewById(R.id.ad_view);
		// 五个分类
		tv_yqzc = (TextView) view.findViewById(R.id.tv_yqzc);
		tv_vipzq = (TextView) view.findViewById(R.id.tv_vipzq);
		tv_cfcy = (TextView) view.findViewById(R.id.tv_cfcy);
		tv_fzc = (TextView) view.findViewById(R.id.tv_fzc);
		tv_fl = (TextView) view.findViewById(R.id.tv_fl);
		// 新品推荐
		new_horizontallistview = (HorizontalListView) view
				.findViewById(R.id.new_horizontallistview);
		// 换一批
		tv_refresh = (TextView) view.findViewById(R.id.tv_refresh);
		// 热销推荐
		rx_horizontallistview = (HorizontalListView) view
				.findViewById(R.id.rx_horizontallistview);
		// 超值商品
		iv_cz1 = (ImageView) view.findViewById(R.id.iv_cz1);
		iv_cz2 = (ImageView) view.findViewById(R.id.iv_cz2);
		iv_cz_big = (ImageView) view.findViewById(R.id.iv_cz_big);
		// 好店推荐
		tv_hd_name1 = (TextView) view.findViewById(R.id.tv_hd_name1);
		iv_hd1 = (ImageView) view.findViewById(R.id.iv_hd1);
		tv_hd_name2 = (TextView) view.findViewById(R.id.tv_hd_name2);
		iv_hd2 = (ImageView) view.findViewById(R.id.iv_hd2);
		tv_hd_name3 = (TextView) view.findViewById(R.id.tv_hd_name3);
		iv_hd3 = (ImageView) view.findViewById(R.id.iv_hd3);
		tv_hd_name4 = (TextView) view.findViewById(R.id.tv_hd_name4);
		iv_hd4 = (ImageView) view.findViewById(R.id.iv_hd4);
		iv_hd_big = (ImageView) view.findViewById(R.id.iv_hd_big);
		// 品牌推荐
		line_ppzq1 = (LinearLayout) view.findViewById(R.id.line_ppzq1);
		tv_ppzq_name1 = (TextView) view.findViewById(R.id.tv_ppzq_name1);
		iv_ppzq1 = (ImageView) view.findViewById(R.id.iv_ppzq1);
		line_ppzq2 = (LinearLayout) view.findViewById(R.id.line_ppzq2);
		tv_ppzq_name2 = (TextView) view.findViewById(R.id.tv_ppzq_name2);
		iv_ppzq2 = (ImageView) view.findViewById(R.id.iv_ppzq2);
		line_ppzq3 = (LinearLayout) view.findViewById(R.id.line_ppzq3);
		tv_ppzq_name3 = (TextView) view.findViewById(R.id.tv_ppzq_name3);
		iv_ppzq3 = (ImageView) view.findViewById(R.id.iv_ppzq3);
		line_ppzq4 = (LinearLayout) view.findViewById(R.id.line_ppzq4);
		tv_ppzq_name4 = (TextView) view.findViewById(R.id.tv_ppzq_name4);
		iv_ppzq4 = (ImageView) view.findViewById(R.id.iv_ppzq4);
		line_ppzq5 = (LinearLayout) view.findViewById(R.id.line_ppzq5);
		tv_ppzq_name5 = (TextView) view.findViewById(R.id.tv_ppzq_name5);
		iv_ppzq5 = (ImageView) view.findViewById(R.id.iv_ppzq5);
		line_ppzq6 = (LinearLayout) view.findViewById(R.id.line_ppzq6);
		tv_ppzq_name6 = (TextView) view.findViewById(R.id.tv_ppzq_name6);
		iv_ppzq6 = (ImageView) view.findViewById(R.id.iv_ppzq6);
		line_ppzq7 = (LinearLayout) view.findViewById(R.id.line_ppzq7);
		tv_ppzq_name7 = (TextView) view.findViewById(R.id.tv_ppzq_name7);
		iv_ppzq7 = (ImageView) view.findViewById(R.id.iv_ppzq7);
		line_ppzq8 = (LinearLayout) view.findViewById(R.id.line_ppzq8);
		tv_ppzq_name8 = (TextView) view.findViewById(R.id.tv_ppzq_name8);
		iv_ppzq8 = (ImageView) view.findViewById(R.id.iv_ppzq8);
		// gv_pp = (MyGridView) view.findViewById(R.id.gv_pp);
		// 人气商品
		iv_rq1 = (ImageView) view.findViewById(R.id.iv_rq1);
		iv_rq2 = (ImageView) view.findViewById(R.id.iv_rq2);
		iv_rq3 = (ImageView) view.findViewById(R.id.iv_rq3);
		iv_rq4 = (ImageView) view.findViewById(R.id.iv_rq4);
		iv_rq5 = (ImageView) view.findViewById(R.id.iv_rq5);
		iv_rq6 = (ImageView) view.findViewById(R.id.iv_rq6);
		// 猜我喜欢
		gv_like = (MyGridView) view.findViewById(R.id.gv_like);

		bitmapUtils = new BitmapUtils(getActivity());
		

		nphlvadapter = new NewProductHorizontalListViewAdapter(getActivity());
		nphlvadapter.notifyDataSetChanged();
		new_horizontallistview.setAdapter(nphlvadapter);

		hshlvadapter = new HotSellingHorizontalListViewAdapter(getActivity());
		hshlvadapter.notifyDataSetChanged();
		rx_horizontallistview.setAdapter(hshlvadapter);

		brandList = new ArrayList<Map<String, String>>();
		hot_sellingList = new ArrayList<Map<String, String>>();
		new_goodsList = new ArrayList<Map<String, String>>();
		worth_buyingList = new ArrayList<Map<String, String>>();
		popularityList = new ArrayList<Map<String, String>>();
		storeList = new ArrayList<Map<String, String>>();
		guesslikeList = new ArrayList<Map<String, String>>();
		sy_daohangList = new ArrayList<Map<String, String>>();

		myDB = new DBHelper(getActivity());
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(getActivity());

		// mygridadapter = new PptjMyGridAdapter(getActivity());
		// gv_pp.setAdapter(mygridadapter);
		likegridadapter = new LikeGridAdapter(getActivity());
		gv_like.setAdapter(likegridadapter);

		showLoading();
		// 首页数据
		syHttpPost();
		// 猜你喜欢数据
		guesslikeHttpPost();
		
		line_search.setOnClickListener(this);
		line_message.setOnClickListener(this);
		tv_vipzq.setOnClickListener(this);
		tv_fl.setOnClickListener(this);
		tv_notnet_refresh.setOnClickListener(this);

		// 超值商品
		iv_cz1.setOnClickListener(this);
		iv_cz2.setOnClickListener(this);
		iv_cz_big.setOnClickListener(this);
		// 好店推荐
		iv_hd1.setOnClickListener(this);
		iv_hd2.setOnClickListener(this);
		iv_hd3.setOnClickListener(this);
		iv_hd4.setOnClickListener(this);
		iv_hd_big.setOnClickListener(this);
		// 人气商品
		iv_rq1.setOnClickListener(this);
		iv_rq2.setOnClickListener(this);
		iv_rq3.setOnClickListener(this);
		iv_rq4.setOnClickListener(this);
		iv_rq5.setOnClickListener(this);
		iv_rq6.setOnClickListener(this);

		// 扫一扫
		line_sys.setOnClickListener(this);
		// 换一批
		tv_refresh.setOnClickListener(this);
		// 邀请注册
		tv_yqzc.setOnClickListener(this);
		// 厨房餐饮
		tv_cfcy.setOnClickListener(this);
		// 服装城
		tv_fzc.setOnClickListener(this);
		line_ppzq1.setOnClickListener(this);
		line_ppzq2.setOnClickListener(this);
		line_ppzq3.setOnClickListener(this);
		line_ppzq4.setOnClickListener(this);
		line_ppzq5.setOnClickListener(this);
		line_ppzq6.setOnClickListener(this);
		line_ppzq7.setOnClickListener(this);
		line_ppzq8.setOnClickListener(this);
		new_horizontallistview.setOnItemClickListener(this);
		rx_horizontallistview.setOnItemClickListener(this);
		// gv_pp.setOnItemClickListener(this);
		gv_like.setOnItemClickListener(this);

		initListView();
		// 初始化定位
		initLocation();
		gv_like.setFocusable(false);

		return view;
	}

	// 猜你喜欢数据
	private void guesslikeHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("type", "2");
		params.addBodyParameter("member_id", "");
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.GUESSLIKE, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e("ee", "失败:" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						guesslikeJsonInfo(arg0.result);
					}
				});
	}

	// 解析猜你喜欢返回的数据
	protected void guesslikeJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				JSONArray data = object.getJSONArray("data");
				for (int i = 0; i < data.length(); i++) {
					JSONObject json = data.getJSONObject(i);
					guesslikeListInfo(json);
				}
			}else if ("300".equals(status)){
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(HANDLER_NO_MORE,
						message));
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(HANDLER_NET_FAILURE1,
						message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private void guesslikeListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String goods_id = json.getString("goods_id");
			String goods_name = json.getString("goods_name");
			String goods_price = json.getString("goods_price");
			String goods_image = json.getString("goods_image");
			String union_goods = json.getString("union_goods");
			String store_id = json.getString("store_id");
			Map<String, String> guesslikeMap = new HashMap<String, String>();
			guesslikeMap.put("goods_id", goods_id);
			guesslikeMap.put("goods_name", goods_name);
			guesslikeMap.put("goods_price", goods_price);
			guesslikeMap.put("goods_image", goods_image);
			guesslikeMap.put("union_goods", union_goods);
			guesslikeMap.put("store_id", store_id);
			guesslikeList.add(guesslikeMap);
			handler.sendEmptyMessage(HANDLER_GUESSLIKE_SUCCESS);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 首页网络请求
	private void syHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		httpUtils.send(HttpMethod.POST, JiekouUtils.SHOUYE,params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e("ee", "失败:" + arg1);
						handler.sendMessage(handler.obtainMessage(
								HANDLER_NET_FAILURE1, "sy:" + arg1));
						// handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						syJsonInfo(arg0.result);
					}
				});
	}

	// 解析首页返回数据
	protected void syJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				JSONObject job = object.getJSONObject("data");
				// 横条广告1
				JSONArray banner1 = job.getJSONArray("banner1");
				// 横条广告2
				JSONArray banner2 = job.getJSONArray("banner2");
				// 品牌专区
				JSONArray brand = job.getJSONArray("brand");
				// 轮播
				JSONArray carousel = job.getJSONArray("carousel");
				// 热销推荐
				JSONArray hot_selling = job.getJSONArray("hot_selling");
				// 导航
				JSONArray navigate = job.getJSONArray("navigate");
				// 新品推荐
				JSONArray new_goods = job.getJSONArray("new_goods");
				// 人气商品
				JSONArray popularity = job.getJSONArray("popularity");
				// 好店推荐
				JSONArray store = job.getJSONArray("store");
				// 超值商品
				JSONArray worth_buying = job.getJSONArray("worth_buying");
				for (int i = 0; i < banner1.length(); i++) {
					JSONObject json = banner1.getJSONObject(i);
					banner1ListInfo(json);
				}
				for (int i = 0; i < banner2.length(); i++) {
					JSONObject json = banner2.getJSONObject(i);
					banner2ListInfo(json);
				}
				for (int i = 0; i < brand.length(); i++) {
					JSONObject json = brand.getJSONObject(i);
					brandListInfo(json);
				}
				infos.clear();
				for (int i = 0; i < carousel.length(); i++) {
					JSONObject json = carousel.getJSONObject(i);
					carouselListInfo(json);
				}

				hot_sellingList.clear();
				for (int i = 0; i < hot_selling.length(); i++) {
					JSONObject json = hot_selling.getJSONObject(i);
					hot_sellingListInfo(json);
				}
				for (int i = 0; i < navigate.length(); i++) {
					JSONObject json = navigate.getJSONObject(i);
					navigateListInfo(json);
				}
				for (int i = 0; i < new_goods.length(); i++) {
					JSONObject json = new_goods.getJSONObject(i);
					new_goodsListInfo(json);
				}
				for (int i = 0; i < popularity.length(); i++) {
					JSONObject json = popularity.getJSONObject(i);
					popularityListInfo(json);
				}
				for (int i = 0; i < store.length(); i++) {
					JSONObject json = store.getJSONObject(i);
					storeListInfo(json);
				}
				for (int i = 0; i < worth_buying.length(); i++) {
					JSONObject json = worth_buying.getJSONObject(i);
					worth_buyingListInfo(json);
				}
				handler.sendEmptyMessage(HANDLER_SYINFO_SUCCESS);
			} else {
				String msg = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_SYINFO_FAILURE, msg));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 超值商品
	private void worth_buyingListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String goods_id = json.getString("goods_id");
			String goods_name = json.getString("goods_name");
			String goods_price = json.getString("goods_price");
			String image = json.getString("image");
			String item_name = json.getString("item_name");
			String item_sort = json.getString("item_sort");
			String union_goods = json.getString("union_goods");
			Map<String, String> worth_buyingMap = new HashMap<String, String>();
			worth_buyingMap.put("goods_id", goods_id);
			worth_buyingMap.put("goods_name", goods_name);
			worth_buyingMap.put("goods_price", goods_price);
			worth_buyingMap.put("image", image);
			worth_buyingMap.put("item_name", item_name);
			worth_buyingMap.put("item_sort", item_sort);
			worth_buyingMap.put("union_goods", union_goods);
			worth_buyingList.add(worth_buyingMap);
			handler.sendEmptyMessage(HANDLER_WORTH_BUYING_SUCCESS);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 好店推荐
	private void storeListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String data = json.getString("data");
			String image = json.getString("image");
			String item_name = json.getString("item_name");
			String item_sort = json.getString("item_sort");
			String store_id = json.getString("store_id");
			Map<String, String> storeMap = new HashMap<String, String>();
			storeMap.put("data", data);
			storeMap.put("image", image);
			storeMap.put("item_name", item_name);
			storeMap.put("item_sort", item_sort);
			storeMap.put("store_id", store_id);
			storeList.add(storeMap);
			handler.sendEmptyMessage(HANDLER_STORE_SUCCESS);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 人气商品
	private void popularityListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String goods_id = json.getString("goods_id");
			String image = json.getString("image");
			String item_name = json.getString("item_name");
			String item_sort = json.getString("item_sort");
			Map<String, String> popularityMap = new HashMap<String, String>();
			popularityMap.put("goods_id", goods_id);
			popularityMap.put("image", image);
			popularityMap.put("item_name", item_name);
			popularityMap.put("item_sort", item_sort);
			popularityList.add(popularityMap);
			handler.sendEmptyMessage(HANDLER_POPULARITY_SUCCESS);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 新品推荐
	private void new_goodsListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String goods_id = json.getString("goods_id");
			String goods_name = json.getString("goods_name");
			String goods_price = json.getString("goods_price");
			String image = json.getString("image");
			String item_name = json.getString("item_name");
			String item_sort = json.getString("item_sort");
			String union_goods = json.getString("union_goods");
			Map<String, String> new_goodsMap = new HashMap<String, String>();
			new_goodsMap.put("goods_id", goods_id);
			new_goodsMap.put("goods_name", goods_name);
			new_goodsMap.put("goods_price", goods_price);
			new_goodsMap.put("image", image);
			new_goodsMap.put("item_name", item_name);
			new_goodsMap.put("item_sort", item_sort);
			new_goodsMap.put("union_goods", union_goods);
			new_goodsList.add(new_goodsMap);
			handler.sendEmptyMessage(HANDLER_NEW_GOODS_SUCCESS);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 首页导航
	private void navigateListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String data = json.getString("data");
			String goods_id = json.getString("goods_id");
			String item_name = json.getString("item_name");
			String item_sort = json.getString("item_sort");
			String url = json.getString("url");
			Map<String, String> sy_daohangMap = new HashMap<String, String>();
			sy_daohangMap.put("data", data);
			sy_daohangMap.put("goods_id", goods_id);
			sy_daohangMap.put("item_name", item_name);
			sy_daohangMap.put("item_sort", item_sort);
			sy_daohangMap.put("url", url);
			sy_daohangList.add(sy_daohangMap);
			handler.sendEmptyMessage(HANDLER_SY_DAOHANG_SUCCESS);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 热销推荐
	private void hot_sellingListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String goods_id = json.getString("goods_id");
			String goods_name = json.getString("goods_name");
			String goods_price = json.getString("goods_price");
			String image = json.getString("image");
			String item_name = json.getString("item_name");
			String item_sort = json.getString("item_sort");
			String union_goods = json.getString("union_goods");
			Map<String, String> hot_sellingMap = new HashMap<String, String>();
			hot_sellingMap.put("goods_id", goods_id);
			hot_sellingMap.put("goods_name", goods_name);
			hot_sellingMap.put("goods_price", goods_price);
			hot_sellingMap.put("image", image);
			hot_sellingMap.put("item_name", item_name);
			hot_sellingMap.put("item_sort", item_sort);
			hot_sellingMap.put("union_goods", union_goods);
			hot_sellingList.add(hot_sellingMap);
			handler.sendEmptyMessage(HANDLER_HOT_SELLING_SUCCESS);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 轮播图
	private void carouselListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			
			ADInfo info = new ADInfo();
			info.setAuto_id(json.getString("goods_id"));
			info.setUrl(json.getString("data"));
			info.setPic(json.getString("image"));
			info.setSort(json.getString("item_name"));
			info.setAdd_time(json.getString("item_sort"));
			infos.add(info);
			handler.sendEmptyMessage(HANDLER_LBT_SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 品牌专区
	private void brandListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String data = json.getString("data");
			String image = json.getString("image");
			String item_name = json.getString("item_name");
			String item_sort = json.getString("item_sort");
			Map<String, String> brandMap = new HashMap<String, String>();
			brandMap.put("data", data);
			brandMap.put("image", image);
			brandMap.put("item_name", item_name);
			brandMap.put("item_sort", item_sort);
			brandList.add(brandMap);
			handler.sendEmptyMessage(HANDLER_BRAND_SUCCESS);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 超值横条图
	private void banner2ListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			hd_goods_id = json.getString("goods_id");
			String image = json.getString("image");
			String item_name = json.getString("item_name");
			String item_sort = json.getString("item_sort");
			BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
			bitmapUtils.display(iv_hd_big, image);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 超值横条图
	private void banner1ListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			cz_goods_id = json.getString("goods_id");
			String image = json.getString("image");
			String item_name = json.getString("item_name");
			String item_sort = json.getString("item_sort");
			BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
			bitmapUtils.display(iv_cz_big, image);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {

		@Override
		public void onImageClick(ADInfo info, int position, View imageView) {
			try {
				String imgUrl = info.getUrl();
				if (imgUrl != null && imgUrl.contains("http://")) {
					Intent intent = new Intent(getActivity(),
							WebviewActivity.class);
					intent.putExtra("url", imgUrl);
					intent.putExtra("title", "VIP会员专区");
					startActivity(intent);
				} else {
					Intent intent = new Intent(getActivity(),
							ProductDetailActivity.class);
					intent.putExtra("goods_id", info.getAuto_id());
					startActivity(intent);
				}
			} catch (Exception e) {
				// TODO: handle exception
				handler.sendEmptyMessage(HANDLER_NN_FAILURE);
			}

		}

		@Override
		public void displayImageWM(String imageURL, ImageView imageView) {
			// TODO Auto-generated method stub
			try {
				ImageUtil.display(imageURL, imageView);
			} catch (Exception e) {
				// TODO: handle exception
				handler.sendEmptyMessage(HANDLER_NN_FAILURE);
			}
		}

	};

	// private void toImgUrl(String imgUrl) {
	// try {
	// Intent intent = new Intent();
	// intent.setAction("android.intent.action.VIEW");
	// Uri content_url = Uri.parse(imgUrl);
	// intent.setData(content_url);
	// startActivity(intent);
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// }

	// 点击事件
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.line_sys:
			intent = new Intent(getActivity(), SaoYiSaoActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.line_search:
			intent = new Intent(getActivity(), SearchActivity.class);
			intent.putExtra("type", "宝贝");
			intent.putExtra("lat1", latitude);
			intent.putExtra("lng1", longitude);
			startActivity(intent);
			break;
		case R.id.line_message:
			intent = new Intent(getActivity(), MessageActivcity.class);
			intent.putExtra("type", "宝贝");
			startActivity(intent);
			break;
		case R.id.tv_notnet_refresh:
			showLoading();
			nowPage = 1;
			// 首页数据
			syHttpPost();
			// 猜你喜欢数据
			guesslikeHttpPost();
			initListView();
			break;
		// 邀请注册
		case R.id.tv_yqzc:
			if (isLogin()) {
				intent = new Intent(getActivity(), YqzcActivity.class);
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("dbdh", "sy");
				startActivity(intent);
			}
			break;
		// VIP专区
		case R.id.tv_vipzq:
			intent = new Intent(getActivity(), WebviewActivity.class);
			String url = sy_daohangList.get(1).get("url");
			intent.putExtra("url", url);
			intent.putExtra("title", "VIP会员专区");
			startActivity(intent);
			break;
		// 厨房餐饮
		case R.id.tv_cfcy:
			intent = new Intent(getActivity(),
					SearchProductResultActivity.class);
			SharedUtil.saveStringValue(SharedCommon.PRODUCTLIST_TYPE, "cfcy");
			intent.putExtra("gc_id_1", sy_daohangList.get(2).get("item_sort"));
			startActivity(intent);
			break;
		// 服装城
		case R.id.tv_fzc:
			intent = new Intent(getActivity(),
					SearchProductResultActivity.class);
			SharedUtil.saveStringValue(SharedCommon.PRODUCTLIST_TYPE, "fzc");
			intent.putExtra("gc_id_1", sy_daohangList.get(3).get("item_sort"));
			startActivity(intent);
			break;
		// 分类
		case R.id.tv_fl:
			intent = new Intent(getActivity(), FenleiActivity.class);
			startActivity(intent);
			break;
		// 换一批
		case R.id.tv_refresh:
			refreshHttpPost();
			break;
		// 超值
		case R.id.iv_cz1:
			intent = new Intent(getActivity(), ProductDetailActivity.class);
			intent.putExtra("goods_id", worth_buyingList.get(0).get("goods_id"));
			startActivity(intent);
			break;
		case R.id.iv_cz2:
			intent = new Intent(getActivity(), ProductDetailActivity.class);
			intent.putExtra("goods_id", worth_buyingList.get(1).get("goods_id"));
			startActivity(intent);
			break;
		case R.id.iv_cz_big:
			intent = new Intent(getActivity(), ProductDetailActivity.class);
			intent.putExtra("goods_id", cz_goods_id);
			startActivity(intent);
			break;
		// 好店
		case R.id.iv_hd1:
//			intent = new Intent(getActivity(), CCQStoreDetailActivity.class);
			intent = new Intent(getActivity(), OldCCQStoreDetailActivity.class);
			intent.putExtra("store_id", storeList.get(0).get("store_id"));
			intent.putExtra("starlat", latitude);
			intent.putExtra("starlng", longitude);
			startActivity(intent);
			break;
		case R.id.iv_hd2:
//			intent = new Intent(getActivity(), CCQStoreDetailActivity.class);
			intent = new Intent(getActivity(), OldCCQStoreDetailActivity.class);
			intent.putExtra("store_id", storeList.get(1).get("store_id"));
			startActivity(intent);
			break;
		case R.id.iv_hd3:
//			intent = new Intent(getActivity(), CCQStoreDetailActivity.class);
			intent = new Intent(getActivity(), OldCCQStoreDetailActivity.class);
			intent.putExtra("store_id", storeList.get(2).get("store_id"));
			startActivity(intent);
			break;
		case R.id.iv_hd4:
//			intent = new Intent(getActivity(), CCQStoreDetailActivity.class);
			intent = new Intent(getActivity(), OldCCQStoreDetailActivity.class);
			intent.putExtra("store_id", storeList.get(3).get("store_id"));
			startActivity(intent);
			break;
		case R.id.iv_hd_big:
			intent = new Intent(getActivity(), ProductDetailActivity.class);
			intent.putExtra("goods_id", hd_goods_id);
			startActivity(intent);
			break;
		// 人气
		case R.id.iv_rq1:
			intent = new Intent(getActivity(), ProductDetailActivity.class);
			intent.putExtra("goods_id", popularityList.get(0).get("goods_id"));
			startActivity(intent);
			break;
		case R.id.iv_rq2:
			intent = new Intent(getActivity(), ProductDetailActivity.class);
			intent.putExtra("goods_id", popularityList.get(1).get("goods_id"));
			startActivity(intent);
			break;
		case R.id.iv_rq3:
			intent = new Intent(getActivity(), ProductDetailActivity.class);
			intent.putExtra("goods_id", popularityList.get(2).get("goods_id"));
			startActivity(intent);
			break;
		case R.id.iv_rq4:
			intent = new Intent(getActivity(), ProductDetailActivity.class);
			intent.putExtra("goods_id", popularityList.get(3).get("goods_id"));
			startActivity(intent);
			break;
		case R.id.iv_rq5:
			intent = new Intent(getActivity(), ProductDetailActivity.class);
			intent.putExtra("goods_id", popularityList.get(4).get("goods_id"));
			startActivity(intent);
			break;
		case R.id.iv_rq6:
			intent = new Intent(getActivity(), ProductDetailActivity.class);
			intent.putExtra("goods_id", popularityList.get(5).get("goods_id"));
			startActivity(intent);
			break;
		case R.id.line_ppzq1:
			intent = new Intent(getActivity(),
					SearchProductResultActivity.class);
			SharedUtil.saveStringValue(SharedCommon.PRODUCTLIST_TYPE, "sy_ss");
			String text1 = brandList.get(0).get("data");
			intent.putExtra("text", text1);
			startActivity(intent);
			break;
		case R.id.line_ppzq2:
			intent = new Intent(getActivity(),
					SearchProductResultActivity.class);
			SharedUtil.saveStringValue(SharedCommon.PRODUCTLIST_TYPE, "sy_ss");
			String text2 = brandList.get(1).get("data");
			intent.putExtra("text", text2);
			startActivity(intent);
			break;
		case R.id.line_ppzq3:
			intent = new Intent(getActivity(),
					SearchProductResultActivity.class);
			SharedUtil.saveStringValue(SharedCommon.PRODUCTLIST_TYPE, "sy_ss");
			String text3 = brandList.get(2).get("data");
			intent.putExtra("text", text3);
			startActivity(intent);
			break;
		case R.id.line_ppzq4:
			intent = new Intent(getActivity(),
					SearchProductResultActivity.class);
			SharedUtil.saveStringValue(SharedCommon.PRODUCTLIST_TYPE, "sy_ss");
			String text4 = brandList.get(3).get("data");
			intent.putExtra("text", text4);
			startActivity(intent);
			break;
		case R.id.line_ppzq5:
			intent = new Intent(getActivity(),
					SearchProductResultActivity.class);
			SharedUtil.saveStringValue(SharedCommon.PRODUCTLIST_TYPE, "sy_ss");
			String text5 = brandList.get(4).get("data");
			intent.putExtra("text", text5);
			startActivity(intent);
			break;
		case R.id.line_ppzq6:
			intent = new Intent(getActivity(),
					SearchProductResultActivity.class);
			SharedUtil.saveStringValue(SharedCommon.PRODUCTLIST_TYPE, "sy_ss");
			String text6 = brandList.get(5).get("data");
			intent.putExtra("text", text6);
			startActivity(intent);
			break;
		case R.id.line_ppzq7:
			intent = new Intent(getActivity(),
					SearchProductResultActivity.class);
			SharedUtil.saveStringValue(SharedCommon.PRODUCTLIST_TYPE, "sy_ss");
			String text7 = brandList.get(6).get("data");
			intent.putExtra("text", text7);
			startActivity(intent);
			break;
		case R.id.line_ppzq8:
			intent = new Intent(getActivity(),
					SearchProductResultActivity.class);
			SharedUtil.saveStringValue(SharedCommon.PRODUCTLIST_TYPE, "sy_ss");
			String text8 = brandList.get(7).get("data");
			intent.putExtra("text", text8);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	// 热销推荐换一批
	private void refreshHttpPost() {
		// TODO Auto-generated method stub

		if (page != 3) {
			page++;
		} else {
			page = 1;
		}
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("page", page + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.REFRESH, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						refreshListinfo(arg0.result);
					}
				});
	}

	protected void refreshListinfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			hot_sellingList.clear();
			if ("200".equals(status)) {
				JSONArray data = object.getJSONArray("data");
				for (int i = 0; i < data.length(); i++) {
					JSONObject json = data.getJSONObject(i);
					hot_sellingListInfo(json);
				}
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

	/**
	 * @description 填充底部商品listview
	 **/
	private void initListView() {
		scrollView.getView();
		scrollView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadMoring() {
				// TODO Auto-generated method stub
				try {
					nowPage++;
					guesslikeHttpPost();
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(HANDLER_NN_FAILURE);
				}

			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent;
		if (parent == new_horizontallistview) {
			intent = new Intent(getActivity(), ProductDetailActivity.class);
			intent.putExtra("goods_id",
					new_goodsList.get(position).get("goods_id"));
			startActivity(intent);
		} else if (parent == rx_horizontallistview) {
			intent = new Intent(getActivity(), ProductDetailActivity.class);
			intent.putExtra("goods_id",
					hot_sellingList.get(position).get("goods_id"));
			startActivity(intent);
		} else if (parent == gv_pp) {
			intent = new Intent(getActivity(),
					SearchProductResultActivity.class);
			SharedUtil.saveStringValue(SharedCommon.PRODUCTLIST_TYPE, "sy_ss");
			String text = brandList.get(position).get("data");
			intent.putExtra("text", text);
			startActivity(intent);
		} else if (parent == gv_like) {
			intent = new Intent(getActivity(), ProductDetailActivity.class);
			intent.putExtra("goods_id",
					guesslikeList.get(position).get("goods_id"));
			startActivity(intent);
		}
	}

	// 加载中动画
	private Dialog loadingDialog;

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

		return true;
	}

	// 经 度
	private String longitude = "";
	// 纬 度
	private String latitude = "";
	// 市
	private String shi = "深圳";
	// 定位
	private AMapLocationClient locationClient = null;
	private AMapLocationClientOption locationOption = new AMapLocationClientOption();

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
					// 市
					String allshi = loc.getCity();
					if (!TextUtils.isEmpty(allshi)) {
						shi = allshi.replaceAll("市", "");
						String street = loc.getStreet();
					}
				} else {
					// 定位失败
					// Toast.makeText(getActivity(), loc.getErrorInfo(),
					// Toast.LENGTH_SHORT).show();
				}
			} else {
				// Toast.makeText(getActivity(), "定位失败，loc is null",
				// Toast.LENGTH_SHORT).show();
			}
			handler.sendEmptyMessage(HANDLER_DINGWEI_FINISH);
		}
	};
	private LinearLayout line_ppzq1;
	private TextView tv_ppzq_name1;
	private ImageView iv_ppzq1;
	private LinearLayout line_ppzq2;
	private TextView tv_ppzq_name2;
	private ImageView iv_ppzq2;
	private LinearLayout line_ppzq3;
	private TextView tv_ppzq_name3;
	private ImageView iv_ppzq3;
	private LinearLayout line_ppzq4;
	private TextView tv_ppzq_name4;
	private ImageView iv_ppzq4;
	private LinearLayout line_ppzq5;
	private TextView tv_ppzq_name5;
	private ImageView iv_ppzq5;
	private LinearLayout line_ppzq6;
	private TextView tv_ppzq_name6;
	private ImageView iv_ppzq6;
	private LinearLayout line_ppzq7;
	private TextView tv_ppzq_name7;
	private ImageView iv_ppzq7;
	private LinearLayout line_ppzq8;
	private TextView tv_ppzq_name8;
	private ImageView iv_ppzq8;
	private LinearLayout line_net_error;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

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

}