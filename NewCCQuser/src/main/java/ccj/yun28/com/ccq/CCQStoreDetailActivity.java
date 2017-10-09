package ccj.yun28.com.ccq;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.CcqQuanLvAdapter;
import ccj.yun28.com.ccq.OldCCQStoreDetailActivity.MyPageAdapter;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.lunbotu.ADInfo;
import ccj.yun28.com.lunbotu.ImageCycleView;
import ccj.yun28.com.lunbotu.ImageCycleView.ImageCycleViewListener;
import ccj.yun28.com.utils.ImageUtil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.MyListView;
import ccj.yun28.com.view.MyScrollView;
import ccj.yun28.com.view.OnRefreshListener;
import ccj.yun28.com.view.star.StarLayoutParams;
import ccj.yun28.com.view.star.StarLinearLayout;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;

import com.amap.location.navi.GPSNaviActivity;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.ccqsharesdk.onekeyshare.OnekeyShare;
import com.example.ccqsharesdk.onekeyshare.ShareContentCustomizeCallback;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

//import ccj.yun28.com.DaohangActivity;

/**
 * 餐餐抢店铺详情页-黑色版本
 * 
 * @author meihuali
 * 
 */
public class CCQStoreDetailActivity extends FragmentActivity implements
		OnClickListener, OnItemClickListener {

	private DBHelper myDB;
	private Utils utils;
	private BitmapUtils bitmapUtils;
	private HttpUtils httpUtils;
	private int nowPage = 1;
	private String store_name;
	private String[] verstring;
	private String latitude;
	private String longitude;
	private String sjlatitude;
	private String sjlongitude;
	private String union_pay_discount;
	// 店铺id
	private String store_id;

	private ListView lv;
	private MaterialRefreshLayout refreshLayout;
	// 店铺名
	private TextView tv_name;
	// 店铺图片
	private RelativeLayout relay_lunbo;
	private ImageCycleView ad_view;
	private TextView tv_new_pro;
	// 星星
	private StarLinearLayout starsLayout;
	// 评价星星
	private StarLinearLayout pinjia_starsLayout;
	private TextView tv_pingjia_num;
	// 消费买单
	private TextView tv_xfmd;
	// 已售
	private TextView tv_ccq_sale;
	// 人气
	private TextView tv_ccq_renqi;
	// 店铺电话
	private TextView tv_phone;
	private String live_store_tel;
	// 地址
	private TextView tv_adress;
	// 更多
	private TextView tv_store_pic_more;
	// 环境图
	private ImageView iv_pic_first;
	private ImageView iv_pic_second;
	private ImageView iv_pic_third;
	// 没有券
	private TextView tv_noproduct;
	// 环境图list
	private List<String> huanjingimglist;
	// 商品list
	private List<Map<String, String>> ccqProQuanList;
	private ArrayList<ADInfo> infos = new ArrayList<ADInfo>();
	private CcqQuanLvAdapter ccqQuanLvAdapter;

	// 加载中动画
	private Dialog loadingDialog;
	// 拨打电话dialog
	private Dialog calldialog;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取信息成功
	private static final int HANDLER_GETINFO_SUCCESS = 3;
	private static final int HANDLER_LBT_SUCCESS = 4;
	private static final int HANDLER_CCQPROQUAN_SUCCESS = 5;
	private static final int HANDLER_CCQPROQUAN_NOMORE = 6;
	// token失效
	private static final int HANDLER_TOKEN_FAILURE = 7;
	// 分享成功
	private static final int HANDLER_SHAREINFO_SUCCESS = 8;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				Toast.makeText(CCQStoreDetailActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				Toast.makeText(CCQStoreDetailActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				Toast.makeText(CCQStoreDetailActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取信息成功
			case HANDLER_GETINFO_SUCCESS:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				bitmapUtils.display(iv_pic_first, huanjingimglist.get(0));
				bitmapUtils.display(iv_pic_second, huanjingimglist.get(1));
				bitmapUtils.display(iv_pic_third, huanjingimglist.get(2));
				break;
			// 获取轮播图成功
			case HANDLER_LBT_SUCCESS:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
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
			// 获取券成功
			case HANDLER_CCQPROQUAN_SUCCESS:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				ccqQuanLvAdapter.NotifyList(ccqProQuanList);
				ccqQuanLvAdapter.notifyDataSetChanged();
				break;
			// 没有更多
			case HANDLER_CCQPROQUAN_NOMORE:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				if (nowPage == 1) {
					tv_noproduct.setVisibility(View.VISIBLE);
				} else {
					tv_noproduct.setVisibility(View.GONE);
				}
				ccqQuanLvAdapter.NotifyList(ccqProQuanList);
				ccqQuanLvAdapter.notifyDataSetChanged();
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				if (msg.obj != null) {
					Toast.makeText(CCQStoreDetailActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent intent = new Intent(CCQStoreDetailActivity.this,
						LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
				break;
			// 获取分享信息成功
			case HANDLER_SHAREINFO_SUCCESS:
				dissDialog();
				showShare();
				break;
			default:
				break;
			}
		};
	};
	private String city;
	private String district;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_ccq1);
		View topView = LayoutInflater.from(CCQStoreDetailActivity.this)
				.inflate(R.layout.activity_storedetail, null);
		LinearLayout fragment_ccq_store_top = (LinearLayout) findViewById(R.id.fragment_ccq_store_top);
		fragment_ccq_store_top.setVisibility(View.VISIBLE);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 分享
		LinearLayout line_fenx = (LinearLayout) findViewById(R.id.line_fenx);
		// 店铺名
		tv_name = (TextView) findViewById(R.id.tv_name);
		// 店铺图片
		relay_lunbo = (RelativeLayout) topView.findViewById(R.id.relay_lunbo);
		ad_view = (ImageCycleView) topView.findViewById(R.id.ad_view);
		tv_new_pro = (TextView) topView.findViewById(R.id.tv_new_pro);

		starsLayout = (StarLinearLayout) topView.findViewById(R.id.starsLayout);
		// 已售
		tv_ccq_sale = (TextView) topView.findViewById(R.id.tv_ccq_sale);
		// 人气
		tv_ccq_renqi = (TextView) topView.findViewById(R.id.tv_ccq_renqi);
		// 消费买单
		tv_xfmd = (TextView) topView.findViewById(R.id.tv_xfmd);
		// 电话
		tv_phone = (TextView) topView.findViewById(R.id.tv_phone);
		// 地址
		tv_adress = (TextView) topView.findViewById(R.id.tv_adress);
		ImageView iv_daohang = (ImageView) topView
				.findViewById(R.id.iv_daohang);
		// 整个评价
		LinearLayout line_pinjia = (LinearLayout) topView
				.findViewById(R.id.line_pinjia);
		pinjia_starsLayout = (StarLinearLayout) topView
				.findViewById(R.id.pinjia_starsLayout);
		tv_pingjia_num = (TextView) topView.findViewById(R.id.tv_pingjia_num);

		// 更多
		tv_store_pic_more = (TextView) topView
				.findViewById(R.id.tv_store_pic_more);
		iv_pic_first = (ImageView) topView.findViewById(R.id.iv_pic_first);
		iv_pic_second = (ImageView) topView.findViewById(R.id.iv_pic_second);
		iv_pic_third = (ImageView) topView.findViewById(R.id.iv_pic_third);

		// 商品listview
		tv_noproduct = (TextView) topView.findViewById(R.id.tv_noproduct);

		lv = (ListView) findViewById(R.id.lv);

		ccqQuanLvAdapter = new CcqQuanLvAdapter(CCQStoreDetailActivity.this);
		lv.setAdapter(ccqQuanLvAdapter);
		refreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh_layout);
		refreshLayout.setLoadMore(true);
		lv.addHeaderView(topView);

		myDB = new DBHelper(this);
		bitmapUtils = new BitmapUtils(CCQStoreDetailActivity.this);

		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(CCQStoreDetailActivity.this);

		huanjingimglist = new ArrayList<String>();
		ccqProQuanList = new ArrayList<Map<String, String>>();

		/*if (getIntent() != null) {
			latitude = getIntent().getStringExtra("starlat");
			longitude = getIntent().getStringExtra("starlng");
			store_id = getIntent().getStringExtra("store_id");
		}*/
		
		if (getIntent() != null) {
			latitude = getIntent().getStringExtra("starlat");
			longitude = getIntent().getStringExtra("starlng");
			store_id = getIntent().getStringExtra("store_id");
			city = getIntent().getStringExtra("city");
			district = getIntent().getStringExtra("district");
		}
		
		Map<String, String> mMap = new HashMap<String, String>();
		mMap.put("latitude", latitude);
		mMap.put("longitude", longitude);
		mMap.put("city", city);
		mMap.put("district", district);
		ccqQuanLvAdapter.setExtra(mMap);
		
		showLoading();
		sjDetailHttpPost();
		ccqProQuanHttpPost();

		int[] info = utils.getWindowInfo(CCQStoreDetailActivity.this);
		// 获取当前控件的布局对象
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		// 获取当前控件的布局对象
		params.height = info[0] / 2 * 1;// 设置当前控件布局的高度
		relay_lunbo.setLayoutParams(params);

		initListView();

		line_back.setOnClickListener(this);
		line_fenx.setOnClickListener(this);
		tv_xfmd.setOnClickListener(this);
		tv_phone.setOnClickListener(this);
		iv_daohang.setOnClickListener(this);
		line_pinjia.setOnClickListener(this);
		tv_store_pic_more.setOnClickListener(this);
		iv_pic_first.setOnClickListener(this);
		iv_pic_second.setOnClickListener(this);
		iv_pic_third.setOnClickListener(this);
		lv.setOnItemClickListener(this);
	}

	private void sjDetailHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("store_id", store_id);
		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWCCQDPDETAILSY, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						sjDetailJsonInfo(arg0.result);
					}
				});

	}

	protected void sjDetailJsonInfo(String result) {
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				JSONObject data = object.getJSONObject("data");
				String adv = data.getString("adv");
				String goods_eva = data.getString("goods_eva");
				sjlatitude = data.getString("latitude");
				live_store_tel = data.getString("live_store_tel");
				sjlongitude = data.getString("longitude");
				String member_id = data.getString("member_id");
				String popularity = data.getString("popularity");
				String sold_out = data.getString("sold_out");
				store_address = data.getString("store_address");
				String store_credit = data.getString("store_credit");
				store_name = data.getString("store_name");
				String store_teat = data.getString("store_teat");
				String union_pay = data.getString("union_pay");
				union_pay_discount = data.getString("union_pay_discount");

				if ("1".equals(union_pay)) {
					tv_xfmd.setVisibility(View.VISIBLE);
				} else {
					tv_xfmd.setVisibility(View.GONE);
				}

				tv_name.setText(store_name);
				tv_new_pro.setText(adv);
				tv_ccq_sale.setText(sold_out);
				tv_ccq_renqi.setText(popularity);
				tv_phone.setText(live_store_tel);
				tv_adress.setText(store_address);
				tv_pingjia_num.setText(goods_eva + "条评价");

				starsTest(starsLayout, starsNum(store_teat));
				starsTest(pinjia_starsLayout, starsNum(store_credit));

				JSONArray photo = data.getJSONArray("photo");
				JSONArray slides = data.getJSONArray("slides");
				for (int i = 0; i < slides.length(); i++) {
					String lbtimg = slides.getString(i);
					carouselListInfo(lbtimg);
				}
				for (int i = 0; i < photo.length(); i++) {
					String huanjingimg = photo.getString(i);
					huanjingimglist.add(huanjingimg);
				}
				handler.sendEmptyMessage(HANDLER_GETINFO_SUCCESS);
			} else if ("300".equals(status)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_CCQPROQUAN_NOMORE, message));
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

	private int starsNum(String credit) {
		if ("0.5".equals(credit) || "1".equals(credit)) {
			return 1;
		} else if ("1.5".equals(credit) || "2".equals(credit)) {
			return 2;
		} else if ("2.5".equals(credit) || "3".equals(credit)) {
			return 3;
		} else if ("3.5".equals(credit) || "4".equals(credit)) {
			return 4;
		} else {
			return 5;
		}
	}

	private void starsTest(StarLinearLayout pinjia_starsLayout, int num) {
		StarLayoutParams params = new StarLayoutParams();
		params.setNormalStar(this.getResources().getDrawable(R.drawable.starh))
				.setSelectedStar(
						this.getResources().getDrawable(R.drawable.star))
				.setSelectable(true).setSelectedStarNum(num).setTotalStarNum(5)
				.setStarHorizontalSpace(6);
		pinjia_starsLayout.setStarParams(params);
	}

	// 轮播图
	private void carouselListInfo(String lbtimg) {
		try {
			ADInfo info = new ADInfo();
			info.setAuto_id("");
			info.setUrl("");
			info.setPic(lbtimg);
			info.setSort("");
			info.setAdd_time("");
			infos.add(info);
			handler.sendEmptyMessage(HANDLER_LBT_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {

		@Override
		public void onImageClick(ADInfo info, int position, View imageView) {

		}

		@Override
		public void displayImageWM(String imageURL, ImageView imageView) {
			try {
				ImageUtil.display(imageURL, imageView);
			} catch (Exception e) {
				handler.sendEmptyMessage(HANDLER_NN_FAILURE);
			}
		}

	};

	/**
	 * 商家详情-商品列表
	 */
	private void ccqProQuanHttpPost() {
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("store_id", store_id);
		params.addBodyParameter("page", nowPage + "");
		params.addBodyParameter("goods_images_op","!m");
		params.addBodyParameter("goods_images_size","220x180");

		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWCCQQIANGQUAN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						ccqProQuanJsonInfo(arg0.result);
					}
				});

	}

	protected void ccqProQuanJsonInfo(String result) {
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				if (nowPage == 1) {
					ccqProQuanList.clear();
				}
				JSONArray data = object.getJSONArray("data");
				for (int i = 0; i < data.length(); i++) {
					JSONObject json = data.getJSONObject(i);
					ccqProQuanListInfo(json);
				}
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

	private void ccqProQuanListInfo(JSONObject json) {
		try {
			String discount = json.getString("discount");
			String evaluation_good_star = json
					.getString("evaluation_good_star");
			String goods_click = json.getString("goods_click");
			String goods_id = json.getString("goods_id");
			String goods_image = json.getString("goods_image");
			String goods_marketprice = json.getString("goods_marketprice");
			String goods_name = json.getString("goods_name");
			String goods_price = json.getString("goods_price");
			String goods_salenum = json.getString("goods_salenum");
			String goods_storage = json.getString("goods_storage");
			String remark = json.getString("remark");
			String save = json.getString("save");
			String store_address = json.getString("store_address");
			String store_id = json.getString("store_id");
			String store_name = json.getString("store_name");
			Map<String, String> ccqProQuanMap = new HashMap<String, String>();
			ccqProQuanMap.put("discount", discount);
			ccqProQuanMap.put("evaluation_good_star", evaluation_good_star);
			ccqProQuanMap.put("goods_click", goods_click);
			ccqProQuanMap.put("goods_id", goods_id);
			ccqProQuanMap.put("goods_image", goods_image);
			ccqProQuanMap.put("goods_marketprice", goods_marketprice);
			ccqProQuanMap.put("goods_name", goods_name);
			ccqProQuanMap.put("goods_price", goods_price);
			ccqProQuanMap.put("goods_salenum", goods_salenum);
			ccqProQuanMap.put("goods_storage", goods_storage);
			ccqProQuanMap.put("remark", remark);
			ccqProQuanMap.put("save", save);
			ccqProQuanMap.put("store_address", store_address);
			ccqProQuanMap.put("store_id", store_id);
			ccqProQuanMap.put("store_name", store_name);
			ccqProQuanList.add(ccqProQuanMap);
			handler.sendEmptyMessage(HANDLER_CCQPROQUAN_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_fenx:
			// getShareInfoHttpPost();
			showShare();
			break;
		case R.id.tv_xfmd:
			// if (isLogin()) {
			if (utils.getisLogin(CCQStoreDetailActivity.this)) {
				intent = new Intent(CCQStoreDetailActivity.this,
						XfmdAcyiviy.class);
				intent.putExtra("store_id", store_id);
				intent.putExtra("union_pay_discount", union_pay_discount);
				startActivity(intent);
			} else {
				intent = new Intent(CCQStoreDetailActivity.this,
						LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
			}
			break;
		case R.id.tv_phone:
			if (live_store_tel != null && !"".equals(live_store_tel)
					&& !"null".equals(live_store_tel)) {
				View view = LayoutInflater.from(CCQStoreDetailActivity.this)
						.inflate(R.layout.dialog_red_moban, null);
				TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
				TextView tv_dialog_cancel = (TextView) view
						.findViewById(R.id.tv_dialog_cancel);
				TextView dialog_text = (TextView) view
						.findViewById(R.id.dialog_text);
				dialog_text.setText("是否拨打： " + live_store_tel);
				
				tv_dialog_cancel.setOnClickListener(this);
				tv_ok.setOnClickListener(this);
				
				calldialog = new Dialog(CCQStoreDetailActivity.this,
						R.style.mDialogStyle);
				calldialog.setContentView(view);
				calldialog.setCanceledOnTouchOutside(false);
				calldialog.show();
			} else {
				Toast.makeText(CCQStoreDetailActivity.this, "暂无电话",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.line_pinjia:
			intent = new Intent(CCQStoreDetailActivity.this,
					AllPinjiaActivtiy.class);
			intent.putExtra("type", "sto");
			intent.putExtra("store_id", store_id);
			startActivity(intent);
			break;
		case R.id.tv_store_pic_more:
			intent = new Intent(CCQStoreDetailActivity.this,
					storeHuanjingPicActivity.class);
			intent.putExtra("store_id", store_id);
			startActivity(intent);
			break;
		case R.id.tv_dialog_cancel:
			calldialog.dismiss();
			break;
		case R.id.tv_ok:
			calldialog.dismiss();

			Intent callintent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ live_store_tel));
			startActivity(callintent);

			break;
		case R.id.iv_pic_first:
			showStorePicDialog(0);
			break;
		case R.id.iv_pic_second:
			showStorePicDialog(1);
			break;
		case R.id.iv_pic_third:
			showStorePicDialog(2);
			break;
		case R.id.iv_daohang:

			if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)
					|| TextUtils.isEmpty(sjlatitude)
					|| TextUtils.isEmpty(sjlongitude)) {
				Toast.makeText(CCQStoreDetailActivity.this, "定位失败，请到空旷的地方重新定位",
						Toast.LENGTH_SHORT).show();
			} else {
				intent = new Intent(CCQStoreDetailActivity.this,
						GPSNaviActivity.class);
				intent.putExtra("endlat", sjlatitude);
				intent.putExtra("endlng", sjlongitude);
				intent.putExtra("starlat", latitude);
				intent.putExtra("starlng", longitude);
				startActivity(intent);
			}

			break;

		default:
			break;
		}
	}

	private Dialog storePicDialog;
	private ImageView[] mImageViews;
	private String store_address;

	private String sharetitle;
	private String shareimg;
	private String shareurl;
	private String sharedescription;

	private void showStorePicDialog(int pagenum) {
		View view = LayoutInflater.from(CCQStoreDetailActivity.this).inflate(
				R.layout.activity_bigpicchange, null);

		ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);

		mImageViews = new ImageView[huanjingimglist.size()];
		for (int i = 0; i < mImageViews.length; i++) {
			ImageView imageView = new ImageView(this);
			mImageViews[i] = imageView;
			bitmapUtils.display(imageView, huanjingimglist.get(i));
			imageView.setScaleType(ScaleType.FIT_CENTER);

		}

		MyPageAdapter pageAdapter = new MyPageAdapter();
		// 设置Adapter
		viewPager.setAdapter(pageAdapter);
		viewPager.setCurrentItem(pagenum);
		// // 设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
		// viewPager.setCurrentItem((mImageViews.length) * 100);

		storePicDialog = new Dialog(CCQStoreDetailActivity.this,
				R.style.mDialogStyle);
		storePicDialog.setContentView(view);
		storePicDialog.setCanceledOnTouchOutside(true);
		storePicDialog.show();
	}

	public class MyPageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return huanjingimglist.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			// ((ViewPager)container).removeView(mImageViews[position %
			// mImageViews.length]);
			((ViewPager) container).removeView(mImageViews[position]);

		}

		/**
		 * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
		 */
		@Override
		public Object instantiateItem(View container, int position) {
			try {
				View view = mImageViews[position];
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						storePicDialog.dismiss();
					}
				});
				((ViewPager) container).addView(mImageViews[position], 0);

				Log.e("ee", position + "position");
			} catch (Exception e) {
				// handler something
			}
			return mImageViews[position];
		}

	}

	private void starsTest(String num) {
		int nu = Integer.parseInt(num);
		StarLayoutParams params = new StarLayoutParams();
		params.setNormalStar(getResources().getDrawable(R.drawable.starh))
				.setSelectedStar(getResources().getDrawable(R.drawable.stard))
				.setSelectable(false).setSelectedStarNum(nu).setTotalStarNum(5)
				.setStarHorizontalSpace(20);
		starsLayout.setStarParams(params);
	}

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(CCQStoreDetailActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				CCQStoreDetailActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(CCQStoreDetailActivity.this,
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Intent intent = new Intent(CCQStoreDetailActivity.this,
				CcqProDetailActivity.class);
		intent.putExtra("ccqgoods_id",
				ccqProQuanList.get(position - 1).get("goods_id"));
		intent.putExtra("starlat", latitude);
		intent.putExtra("starlng", longitude);
		startActivity(intent);
	}

	private void showShare() {
		ShareSDK.initSDK(CCQStoreDetailActivity.this, "171a7e7c3c736");
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		//单独设置微博分享内容格式
		/*oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
			@Override
			public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
				if (SinaWeibo.NAME.equals(platform.getName())) {
					paramsToShare.setText("这个店铺不错哟，我经常光顾，你也来看看吧~~"+ SharedUtil.getStringValue(
							SharedCommon.MALL_QRCODE_IMG_AZ_URL, ""));
					paramsToShare.setImageUrl(infos.get(0).getPic());
				}
			}
		});*/

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// 信息分享时电话
		oks.setAddress(store_address);
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("餐餐抢【" + store_name + "】");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(SharedUtil.getStringValue(
				SharedCommon.MALL_QRCODE_IMG_AZ_URL, ""));
		// text是分享文本，所有平台都需要这个字段
		oks.setText("这个店铺不错哟，我经常光顾，你也来看看吧~~");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//		oks.setImagePath(infos.get(0).getUrl());// 确保SDcard下面存在此张图片
//		oks.setImageUrl(infos.get(0).getUrl());
		oks.setImagePath(infos.get(0).getPic());// 确保SDcard下面存在此张图片
		oks.setImageUrl(infos.get(0).getPic());
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(SharedUtil.getStringValue(
				SharedCommon.MALL_QRCODE_IMG_AZ_URL, ""));
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("这个店铺不错哟，我经常光顾，你也来看看吧~~");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(SharedUtil.getStringValue(
				SharedCommon.MALL_QRCODE_IMG_AZ_URL, ""));

		// 启动分享GUI
		oks.show(CCQStoreDetailActivity.this);
	}

	// 获取分享信息
	private void getShareInfoHttpPost() {
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("share_type", "store_share");
		params.addBodyParameter("store_id", store_id);
		httpUtils.send(HttpMethod.POST, JiekouUtils.GETSHAREINFO, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Log.e("ee", "失败:" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						getShareInfo(arg0.result);
						Log.e("log", "分享 arg0.result-->  " + arg0.result);
					}
				});
	}

	protected void getShareInfo(String result) {
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");

			if ("200".equals(code)) {
				JSONObject ob = object.getJSONObject("data");
				sharetitle = ob.getString("title");
				shareimg = ob.getString("img");
				shareurl = ob.getString("url");
				sharedescription = ob.getString("description");
				handler.sendEmptyMessage(HANDLER_SHAREINFO_SUCCESS);
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

	@Override
	public void onDestroy() {
		super.onDestroy();
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
					ccqProQuanHttpPost();
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(HANDLER_NN_FAILURE);
				}
			}
		});
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

}
