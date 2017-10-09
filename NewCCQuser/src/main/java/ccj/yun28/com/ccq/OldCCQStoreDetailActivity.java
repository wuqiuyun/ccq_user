package ccj.yun28.com.ccq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.col.el;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.squareup.picasso.Picasso;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.CcqShopLvAdapter;
import ccj.yun28.com.adapter.StorePingJiaAdapter;
import ccj.yun28.com.adapter.StoreQuanAdapter;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.lunbotu.ADInfo;
import ccj.yun28.com.lunbotu.ImageCycleView;
import ccj.yun28.com.lunbotu.ImageCycleView.ImageCycleViewListener;
import ccj.yun28.com.sy.ProductDetailActivity;
import ccj.yun28.com.sy.fragment.ProductFragment;
import ccj.yun28.com.utils.ImageUtil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.MyListView;
import ccj.yun28.com.view.MyScrollView;
import ccj.yun28.com.view.OnRefreshListener;
import ccj.yun28.com.view.WebviewActivity;

/**
 * 新餐餐抢店铺详情页
 * 
 * @author meihuali
 * 
 */
public class OldCCQStoreDetailActivity extends FragmentActivity implements
		OnClickListener, OnItemClickListener {

	private DBHelper myDB;
	
	// 店铺id
	private String store_id;
	//先消费后买单折扣度
	private LinearLayout line_xfmd;
	private String union_pay_discount;

	// 抢券
	private RadioButton radio_qq;
	// 评价
	private RadioButton radio_pj;
	// 商家
	private RadioButton radio_sj;
	// 店铺名
	private TextView tv_shopname;
	private ImageCycleView ad_view;

	private List<String> huanjingimglist;

	private ImageView iv_pic_first;
	private ImageView iv_pic_second;
	private ImageView iv_pic_third;
	private RadioButton radio_all;
	private RadioButton radio_hao;
	private RadioButton radio_zhong;
	private RadioButton radio_cha;
	private RadioButton radio_huifu;

	private int nowPage = 1;
	private MyScrollView scrollView;

	private List<Map<String, String>> ccqProQuanList;
	private StoreQuanAdapter storeQuanadapter;
	private List<Map<String, String>> storePingJiaList;
	private StorePingJiaAdapter storePingJiaadapter;
	private List<Map<String, String>> storeShangJiaList;
	private CcqShopLvAdapter ccqShopLvAdapter;

	private ArrayList<ADInfo> infos = new ArrayList<ADInfo>();

	private BitmapUtils bitmapUtils;
	// 经 度
	private String sjlongitude = "";
	// 纬 度
	private String sjlatitude = "";
	private MyListView lv;
	private String re_type;
	private RadioGroup tabes_rg;
	private LinearLayout line_shangjia;
	private String clicktype;

	private TextView tv_childpark;
	private TextView tv_wifi;
	private TextView tv_smok;
	private TextView tv_fapiao;
	private TextView tv_fuwu_time;
	private TextView tv_fuwu_phone;
	private ImageView iv_zz_first;
	private ImageView iv_zz_second;
	private List<String> zhiziimglist;
	private TextView tv_address;
	private String store_phone;
	private Dialog calldialog;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	private static final int HANDLER_NN_FAILURE = 1;
	// 主页数据
	private static final int HANDLER_GETINFO_SUCCESS = 2;
	private static final int HANDLER_GETINFO_FAILURE = 3;
	// 轮播图获取成功
	private static final int HANDLER_LBT_SUCCESS = 4;
	// 轮播图获取失败
	private static final int HANDLER_LBT_FAILURE = 5;
	// token失效
	private static final int HANDLER_TOKEN_FAILURE = 6;

	// 抢券
	private static final int HANDLER_CCQPROQUAN_SUCCESS = 7;
	// 评价
	private static final int HANDLER_STOREPINGJIA_SUCCESS = 8;
	// 商家
	private static final int HANDLER_STORESHANGJIA_SUCCESS = 9;
	private static final int HANDLER_NOMORE_MSG = 10;

	private static final int HANDLER_CCQSHOPDETAI_SUCCESS = 11;

	protected static final int HANDLER_DINGWEI_SUCCESS = 12;

	protected static final int HANDLER_DINGWEI_FINISH = 13;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				dissDialog();
				scrollView.onResfreshFinish();
				Toast.makeText(OldCCQStoreDetailActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_NN_FAILURE:
				dissDialog();
				scrollView.onResfreshFinish();

				Toast.makeText(OldCCQStoreDetailActivity.this, "当前网络出错",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_GETINFO_SUCCESS:
				dissDialog();

				bitmapUtils.display(iv_pic_first, huanjingimglist.get(0));
				bitmapUtils.display(iv_pic_second, huanjingimglist.get(1));
				bitmapUtils.display(iv_pic_third, huanjingimglist.get(2));
				break;
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				scrollView.onResfreshFinish();
				lv.setVisibility(View.VISIBLE);
				if ("qq".equals(clicktype)) {
					lv.setAdapter(storeQuanadapter);
					storeQuanadapter.NotifyList(ccqProQuanList);
				} else if ("pj".equals(clicktype)) {
					lv.setAdapter(storePingJiaadapter);
					storePingJiaadapter.NotifyList(storePingJiaList);
				} else if ("sj".equals(clicktype)) {
					lv.setAdapter(ccqShopLvAdapter);
					ccqShopLvAdapter.NotifyList(storeShangJiaList);
				}
				if (msg.obj != null) {
					Toast.makeText(OldCCQStoreDetailActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			case HANDLER_LBT_SUCCESS:
				dissDialog();
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
			case HANDLER_LBT_FAILURE:
				dissDialog();
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
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(OldCCQStoreDetailActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent intent = new Intent(OldCCQStoreDetailActivity.this,
						LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
				break;
			// 获取信息成功
			case HANDLER_CCQPROQUAN_SUCCESS:
				scrollView.onResfreshFinish();
				lv.setVisibility(View.VISIBLE);
				lv.setAdapter(storeQuanadapter);
				storeQuanadapter.NotifyList(ccqProQuanList);
				dissDialog();
				break;
			// 获取信息成功
			case HANDLER_STOREPINGJIA_SUCCESS:
				scrollView.onResfreshFinish();
				lv.setVisibility(View.VISIBLE);
				lv.setAdapter(storePingJiaadapter);
				storePingJiaadapter.NotifyList(storePingJiaList);
				dissDialog();
				break;
			// 没有更多信息
			case HANDLER_NOMORE_MSG:
				dissDialog();
				scrollView.onResfreshFinish();
				lv.setVisibility(View.VISIBLE);
				lv.setAdapter(storePingJiaadapter);
				storePingJiaadapter.NotifyList(storePingJiaList);
				if (msg.obj != null) {
					Toast.makeText(OldCCQStoreDetailActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// 获取信息成功
			case HANDLER_STORESHANGJIA_SUCCESS:
				dissDialog();
				scrollView.onResfreshFinish();
				bitmapUtils.display(iv_zz_first, zhiziimglist.get(0));
				bitmapUtils.display(iv_zz_second, zhiziimglist.get(1));
				break;
			// 获取信息成功
			case HANDLER_CCQSHOPDETAI_SUCCESS:
				scrollView.onResfreshFinish();
				lv.setVisibility(View.VISIBLE);
				lv.setAdapter(ccqShopLvAdapter);
				ccqShopLvAdapter.NotifyList(storeShangJiaList);
				dissDialog();
				break;
			case HANDLER_DINGWEI_FINISH:
				tv_now_place.setText("定位失败，重新定位或者手动切换城市");
				dissDialog();
//				stopLocation();
				if (isFirst) {
					storeFuJinShangJiaHttpPost();
				}
				break;
			case HANDLER_DINGWEI_SUCCESS:
//				stopLocation();
				storeFuJinShangJiaHttpPost();
				break;
			default:
				break;
			}
		};
	};

	private boolean isFirst = true;

	// 市
	private String shi = "";
	// 定位
	private AMapLocationClient locationClient = null;
	private AMapLocationClientOption locationOption = new AMapLocationClientOption();
	// 当前位置
	private TextView tv_now_place;
	private LinearLayout line_dwshuaxin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newstoredetail);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_lunbo = (LinearLayout) findViewById(R.id.line_lunbo);
		//消费买单
		line_xfmd = (LinearLayout) findViewById(R.id.line_xfmd);
		// 店铺名
		tv_shopname = (TextView) findViewById(R.id.tv_shopname);
		scrollView = (MyScrollView) findViewById(R.id.scrollView);
		// 商家轮播图
		ad_view = (ImageCycleView) findViewById(R.id.ad_view);
		// 店铺环境图更多
		TextView tv_store_pic_more = (TextView) findViewById(R.id.tv_store_pic_more);
		// 店铺环境图
		iv_pic_first = (ImageView) findViewById(R.id.iv_pic_first);
		iv_pic_second = (ImageView) findViewById(R.id.iv_pic_second);
		iv_pic_third = (ImageView) findViewById(R.id.iv_pic_third);
		// 抢券
		radio_qq = (RadioButton) findViewById(R.id.radio_qq);
		// 评价
		radio_pj = (RadioButton) findViewById(R.id.radio_pj);
		// 商家
		radio_sj = (RadioButton) findViewById(R.id.radio_sj);
		// 评价
		tabes_rg = (RadioGroup) findViewById(R.id.tabes_rg);
		// 全部
		radio_all = (RadioButton) findViewById(R.id.radio_all);
		// 好评
		radio_hao = (RadioButton) findViewById(R.id.radio_hao);
		// 中评
		radio_zhong = (RadioButton) findViewById(R.id.radio_zhong);
		// 差评
		radio_cha = (RadioButton) findViewById(R.id.radio_cha);
		// 已回复
		radio_huifu = (RadioButton) findViewById(R.id.radio_huifu);
		line_shangjia = (LinearLayout) findViewById(R.id.line_shangjia);
		tv_childpark = (TextView) findViewById(R.id.tv_childpark);
		tv_wifi = (TextView) findViewById(R.id.tv_wifi);
		tv_smok = (TextView) findViewById(R.id.tv_smok);
		tv_fapiao = (TextView) findViewById(R.id.tv_fapiao);
		tv_fuwu_time = (TextView) findViewById(R.id.tv_fuwu_time);
		tv_fuwu_phone = (TextView) findViewById(R.id.tv_fuwu_phone);
		tv_address = (TextView) findViewById(R.id.tv_address);
		iv_zz_first = (ImageView) findViewById(R.id.iv_zz_first);
		iv_zz_second = (ImageView) findViewById(R.id.iv_zz_second);
		ImageView iv_phone = (ImageView) findViewById(R.id.iv_phone);
		lv = (MyListView) findViewById(R.id.lv);
		LinearLayout line_weizhi = (LinearLayout) findViewById(R.id.line_weizhi);
		tv_now_place = (TextView) findViewById(R.id.tv_now_place);
		line_dwshuaxin = (LinearLayout) findViewById(R.id.line_dwshuaxin);

		myDB = new DBHelper(OldCCQStoreDetailActivity.this);

		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int screenWidth = dm.widthPixels;

		int screenHeigh = dm.heightPixels;
		
		LinearLayout.LayoutParams params= new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		//获取当前控件的布局对象
		params.height= screenWidth/15*7 ;//设置当前控件布局的高度
		ad_view.setLayoutParams(params);
		
		if (getIntent() != null) {
			store_id = getIntent().getStringExtra("store_id");
			dqlatitude = getIntent().getStringExtra("starlat");
			dqlongitude = getIntent().getStringExtra("starlng");
		}
		bitmapUtils = new BitmapUtils(OldCCQStoreDetailActivity.this);
		huanjingimglist = new ArrayList<String>();
		ccqProQuanList = new ArrayList<Map<String, String>>();
		storePingJiaList = new ArrayList<Map<String, String>>();
		storeShangJiaList = new ArrayList<Map<String, String>>();
		zhiziimglist = new ArrayList<String>();

		clicktype = "qq";

		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(OldCCQStoreDetailActivity.this);

		storeQuanadapter = new StoreQuanAdapter(OldCCQStoreDetailActivity.this);
		storePingJiaadapter = new StorePingJiaAdapter(
				OldCCQStoreDetailActivity.this);
		ccqShopLvAdapter = new CcqShopLvAdapter(OldCCQStoreDetailActivity.this);

		showLoading();
		sjDetailHttpPost();
		ccqProQuanHttpPost();

		lv.setOnItemClickListener(this);
		line_back.setOnClickListener(this);
		line_xfmd.setOnClickListener(this);
		tv_store_pic_more.setOnClickListener(this);
		radio_qq.setOnClickListener(this);
		radio_pj.setOnClickListener(this);
		radio_sj.setOnClickListener(this);
		radio_all.setOnClickListener(this);
		radio_hao.setOnClickListener(this);
		radio_zhong.setOnClickListener(this);
		radio_cha.setOnClickListener(this);
		radio_huifu.setOnClickListener(this);
		iv_phone.setOnClickListener(this);
		iv_pic_first.setOnClickListener(this);
		iv_pic_second.setOnClickListener(this);
		iv_pic_third.setOnClickListener(this);
		line_dwshuaxin.setOnClickListener(this);

		lv.setFocusable(false);

		initListView();
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
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						sjDetailJsonInfo(arg0.result);
					}
				});

	}

	protected void sjDetailJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				JSONObject data = object.getJSONObject("data");
				String member_id = data.getString("member_id");
				String store_name = data.getString("store_name");
				sjlatitude = data.getString("latitude");
				sjlongitude = data.getString("longitude");
				String union_pay = data.getString("union_pay");//是否开启先消费后买单 1开启 0关闭
				union_pay_discount = data.getString("union_pay_discount");//先消费后买单折扣度
				if ("1".equals(union_pay)) {
					line_xfmd.setVisibility(View.VISIBLE);
				}else{
					line_xfmd.setVisibility(View.GONE);
				}
				
				tv_shopname.setText(store_name);

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
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 轮播图
	private void carouselListInfo(String lbtimg) {
		// TODO Auto-generated method stub
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// fragment切换
	private void showFragment(Fragment fragment) {
		FragmentTransaction manager = getSupportFragmentManager()
				.beginTransaction();
		manager.replace(R.id.DetailLayout, fragment);
		manager.commitAllowingStateLoss();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_xfmd:
			if (isLogin()) {
				intent = new Intent(OldCCQStoreDetailActivity.this,
						XfmdAcyiviy.class);
				intent.putExtra("store_id", store_id);
				intent.putExtra("union_pay_discount", union_pay_discount);
				startActivity(intent);
			} else {
				intent = new Intent(OldCCQStoreDetailActivity.this,
						LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
			}
			
			break;
		case R.id.tv_store_pic_more:
			intent = new Intent(OldCCQStoreDetailActivity.this,
					storeHuanjingPicActivity.class);
			intent.putExtra("store_id", store_id);
			startActivity(intent);
			break;
		case R.id.radio_qq:
			clicktype = "qq";
			lv.setVisibility(View.GONE);
			line_shangjia.setVisibility(View.GONE);
			tabes_rg.setVisibility(View.GONE);
			radio_qq.setChecked(true);
			radio_pj.setChecked(false);
			radio_sj.setChecked(false);
			nowPage = 1;
			showLoading();
			// re_type = "0";
			ccqProQuanHttpPost();
			break;
		case R.id.radio_pj:
			clicktype = "pj";
			lv.setVisibility(View.GONE);
			line_shangjia.setVisibility(View.GONE);
			tabes_rg.setVisibility(View.VISIBLE);
			radio_pj.setChecked(true);
			radio_sj.setChecked(false);
			radio_qq.setChecked(false);
			re_type = "0";
			nowPage = 1;
			showLoading();
			storePingJiaHttpPost();
			break;
		case R.id.radio_sj:
			clicktype = "sj";
			lv.setVisibility(View.GONE);
			line_shangjia.setVisibility(View.VISIBLE);
			tabes_rg.setVisibility(View.GONE);
			radio_sj.setChecked(true);
			radio_pj.setChecked(false);
			radio_qq.setChecked(false);
			nowPage = 1;
//			initLocation();
			showLoading();
			storeShangJiaHttpPost();
			storeFuJinShangJiaHttpPost();
			break;
		case R.id.radio_all:
			nowPage = 1;
			re_type = "0";
			showLoading();
			storePingJiaHttpPost();
			break;
		case R.id.radio_hao:
			nowPage = 1;
			re_type = "1";
			showLoading();
			storePingJiaHttpPost();
			break;
		case R.id.radio_zhong:
			nowPage = 1;
			re_type = "2";
			showLoading();
			storePingJiaHttpPost();

			break;
		case R.id.radio_cha:
			nowPage = 1;
			re_type = "3";
			showLoading();
			storePingJiaHttpPost();

			break;
		case R.id.radio_huifu:
			nowPage = 1;
			re_type = "4";
			showLoading();
			storePingJiaHttpPost();

			break;
		case R.id.iv_phone:
			View view = LayoutInflater.from(OldCCQStoreDetailActivity.this)
					.inflate(R.layout.dialog_red_moban, null);
			TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
			TextView tv_dialog_cancel = (TextView) view
					.findViewById(R.id.tv_dialog_cancel);
			TextView dialog_text = (TextView) view
					.findViewById(R.id.dialog_text);
			dialog_text.setText("是否拨打： " + store_phone);
			tv_dialog_cancel.setOnClickListener(this);
			tv_ok.setOnClickListener(this);
			calldialog = new Dialog(OldCCQStoreDetailActivity.this,
					R.style.mDialogStyle);
			calldialog.setContentView(view);
			calldialog.setCanceledOnTouchOutside(false);
			calldialog.show();
			break;
		case R.id.tv_dialog_cancel:
			calldialog.dismiss();
			break;
		case R.id.tv_ok:
			calldialog.dismiss();
			if (store_phone != null && !"".equals(store_phone)
					&& !"null".equals(store_phone)) {
				Intent callintent = new Intent(Intent.ACTION_CALL,
						Uri.parse("tel:" + store_phone));
				startActivity(callintent);
			} else {
				Toast.makeText(OldCCQStoreDetailActivity.this, "暂无电话",
						Toast.LENGTH_SHORT).show();
			}
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
		case R.id.line_dwshuaxin:
			isFirst = false;
//			showLoading();
//			startLocation();
			break;
		default:
			break;
		}
	}

	private Dialog storePicDialog;
	private ImageView[] mImageViews;
	private ViewPager viewPager;

	private void showStorePicDialog(int pagenum) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(OldCCQStoreDetailActivity.this)
				.inflate(R.layout.activity_bigpicchange, null);

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

		storePicDialog = new Dialog(OldCCQStoreDetailActivity.this,
				R.style.mDialogStyle);
		storePicDialog.setContentView(view);
		storePicDialog.setCanceledOnTouchOutside(true);
		storePicDialog.show();
	}

	/**
	 * 获取券请求接口
	 */
	private void ccqProQuanHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("store_id", store_id);
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWCCQQIANGQUAN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						ccqProQuanJsonInfo(arg0.result);
					}
				});

	}

	protected void ccqProQuanJsonInfo(String result) {
		// TODO Auto-generated method stub
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private void ccqProQuanListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String discount = json.getString("discount");
			String goods_id = json.getString("goods_id");
			String goods_image = json.getString("goods_image");
			String goods_marketprice = json.getString("goods_marketprice");
			String goods_name = json.getString("goods_name");
			String goods_price = json.getString("goods_price");
			String remark = json.getString("remark");
			String save = json.getString("save");
			String store_id = json.getString("store_id");
			Map<String, String> ccqProQuanMap = new HashMap<String, String>();
			ccqProQuanMap.put("discount", discount);
			ccqProQuanMap.put("goods_id", goods_id);
			ccqProQuanMap.put("goods_image", goods_image);
			ccqProQuanMap.put("goods_marketprice", goods_marketprice);
			ccqProQuanMap.put("goods_name", goods_name);
			ccqProQuanMap.put("goods_price", goods_price);
			ccqProQuanMap.put("remark", remark);
			ccqProQuanMap.put("save", save);
			ccqProQuanMap.put("store_id", store_id);
			ccqProQuanList.add(ccqProQuanMap);
			handler.sendEmptyMessage(HANDLER_CCQPROQUAN_SUCCESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	/**
	 * 获取评价请求接口
	 */
	private void storePingJiaHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("store_id", store_id);
		params.addBodyParameter("re_type", re_type);
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWCCQPINGJIA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						storePingJiaJsonInfo(arg0.result);
					}
				});

	}

	protected void storePingJiaJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				if (nowPage == 1) {
					storePingJiaList.clear();
				}
				JSONObject data = object.getJSONObject("data");
				String eva_bad = data.getString("eva_bad");
				String eva_goods = data.getString("eva_goods");
				String eva_posi = data.getString("eva_posi");
				String eva_rep = data.getString("eva_rep");
				String goods_eva = data.getString("goods_eva");

				radio_all.setText("全部\n" + goods_eva);
				radio_hao.setText("好评\n" + eva_goods);
				radio_zhong.setText("中评\n" + eva_posi);
				radio_cha.setText("差评\n" + eva_bad);
				radio_huifu.setText("已回复\n" + eva_rep);

				JSONArray list = data.getJSONArray("list");
				for (int i = 0; i < list.length(); i++) {
					JSONObject json = list.getJSONObject(i);
					storePingJiaListInfo(json);
				}
			} else if ("300".equals(status)) {
				if (nowPage == 1) {
					storePingJiaList.clear();
				}
				String message = object.getString("message");
				JSONObject data = object.getJSONObject("data");
				String eva_bad = data.getString("eva_bad");
				String eva_goods = data.getString("eva_goods");
				String eva_posi = data.getString("eva_posi");
				String eva_rep = data.getString("eva_rep");
				String goods_eva = data.getString("goods_eva");

				radio_all.setText("全部\n" + goods_eva);
				radio_hao.setText("好评\n" + eva_goods);
				radio_zhong.setText("中评\n" + eva_posi);
				radio_cha.setText("差评\n" + eva_bad);
				radio_huifu.setText("已回复\n" + eva_rep);

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
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private void storePingJiaListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String fa_id = json.getString("fa_id");
			String geval_addtime = json.getString("geval_addtime");
			String geval_content = json.getString("geval_content");
			String geval_frommemberid = json.getString("geval_frommemberid");
			String geval_frommembername = json
					.getString("geval_frommembername");
			String geval_scores = json.getString("geval_scores");
			String geval_storeid = json.getString("geval_storeid");
			String goods_image = json.getString("goods_image");
			String store_reply = json.getString("store_reply");
			Map<String, String> ccqProQuanMap = new HashMap<String, String>();
			ccqProQuanMap.put("fa_id", fa_id);
			ccqProQuanMap.put("geval_addtime", geval_addtime);
			ccqProQuanMap.put("geval_content", geval_content);
			ccqProQuanMap.put("geval_frommemberid", geval_frommemberid);
			ccqProQuanMap.put("geval_frommembername", geval_frommembername);
			ccqProQuanMap.put("geval_scores", geval_scores);
			ccqProQuanMap.put("geval_storeid", geval_storeid);
			ccqProQuanMap.put("goods_image", goods_image);
			ccqProQuanMap.put("store_reply", store_reply);
			storePingJiaList.add(ccqProQuanMap);
			handler.sendEmptyMessage(HANDLER_STOREPINGJIA_SUCCESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	/**
	 * 获取商检请求接口
	 */
	private void storeShangJiaHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("store_id", store_id);
		// params.addBodyParameter("store_id", "1203");
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWSHANGJIA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						storeShangJiaJsonInfo(arg0.result);
					}
				});

	}

	protected void storeShangJiaJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				// JSONArray data = object.getJSONArray("data");
				JSONObject data = object.getJSONObject("data");
				String union_pay = data.getString("union_pay");//是否开启先消费后买单 1开启 0关闭
				union_pay_discount = data.getString("union_pay_discount");//先消费后买单折扣度
				if ("1".equals(union_pay)) {
					line_xfmd.setVisibility(View.VISIBLE);
				}else{
					line_xfmd.setVisibility(View.GONE);
				}
				sjlatitude = data.getString("latitude");
				sjlongitude = data.getString("longitude");
				JSONObject shop_info = data.getJSONObject("shop_info");
				String is_child = shop_info.getString("is_child");
				String is_smoke = shop_info.getString("is_smoke");
				String is_ticket = shop_info.getString("is_ticket");
				String is_wifi = shop_info.getString("is_wifi");
				String service_end_time = shop_info
						.getString("service_end_time");
				String service_start_time = shop_info
						.getString("service_start_time");
				String store_address = shop_info.getString("store_address");
				store_phone = shop_info.getString("store_phone");
				tv_address.setText("地址： " + store_address);
				if ("1".equals(is_child)) {
					tv_childpark.setText("  是");
				} else {
					tv_childpark.setText("  否");
				}
				if ("1".equals(is_smoke)) {
					tv_smok.setText("  是");
				} else {
					tv_smok.setText("  否");
				}
				if ("1".equals(is_ticket)) {
					tv_fapiao.setText("  是");
				} else {
					tv_fapiao.setText("  否");
				}
				if ("1".equals(is_wifi)) {
					tv_wifi.setText("  是");
				} else {
					tv_wifi.setText("  否");
				}
				if ("1".equals(is_child)) {
					tv_childpark.setText("  是");
				} else {
					tv_childpark.setText("  否");
				}
				if ("".equals(store_phone) || store_phone == null) {
					tv_fuwu_phone.setText("  暂无电话");
				} else {
					tv_fuwu_phone.setText(store_phone);
				}
				tv_fuwu_time.setText("  " + service_start_time + "-"
						+ service_end_time);

				JSONArray yinyeurl = data.getJSONArray("yinyeurl");

				for (int i = 0; i < yinyeurl.length(); i++) {
					String zhiziimg = yinyeurl.getString(i);
					zhiziimglist.add(zhiziimg);
				}

				handler.sendEmptyMessage(HANDLER_STORESHANGJIA_SUCCESS);
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

	/**
	 * 获取附近商家请求接口
	 */
	private void storeFuJinShangJiaHttpPost() {
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("store_id", store_id);
		// params.addBodyParameter("store_id", "1203");
		params.addBodyParameter("lat1", sjlatitude);
		params.addBodyParameter("lng1", sjlongitude);
		params.addBodyParameter("city_id", "");
		params.addBodyParameter("page", nowPage + "");
		
		
		
		httpUtils.send(HttpMethod.POST, JiekouUtils.FUJIN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						storeFuJinShangJiaJsonInfo(arg0.result);
					}
				});

	}

	protected void storeFuJinShangJiaJsonInfo(String result) {
		try {

			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				if (nowPage == 1) {
					storeShangJiaList.clear();
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
					storeShangJiaList.clear();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private void ccqshopDetailListInfo(JSONObject json) {
		// TODO Auto-generated method stub
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
			storeShangJiaList.add(ccqshopDetailMap);
			handler.sendEmptyMessage(HANDLER_CCQSHOPDETAI_SUCCESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		if ("qq".equals(clicktype)) {
			Intent intent = new Intent(OldCCQStoreDetailActivity.this,
					CcqProDetailActivity.class);
			intent.putExtra("starlat", dqlatitude);
			intent.putExtra("starlng", dqlongitude);
			intent.putExtra("ccqgoods_id",
					ccqProQuanList.get(position).get("goods_id"));
			startActivity(intent);
		} else if ("sj".equals(clicktype)) {
			Intent intent = new Intent(OldCCQStoreDetailActivity.this,
					OldCCQStoreDetailActivity.class);
			intent.putExtra("starlat", dqlatitude);
			intent.putExtra("starlng", dqlongitude);
			intent.putExtra("store_id",
					storeShangJiaList.get(position).get("store_id"));
			startActivity(intent);
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

					if ("qq".equals(clicktype)) {
						nowPage++;
						showLoading();
						ccqProQuanHttpPost();
					} else if ("pj".equals(clicktype)) {
						nowPage++;
						showLoading();
						storePingJiaHttpPost();
					} else if ("sj".equals(clicktype)) {
						nowPage++;
						showLoading();
						storeFuJinShangJiaHttpPost();
					} else {
						scrollView.onResfreshFinish();
					}
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(HANDLER_NN_FAILURE);
				}

			}
		});
	}

	private ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {

		@Override
		public void onImageClick(ADInfo info, int position, View imageView) {

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

	// 加载中动画
	private Dialog loadingDialog;

	private HttpUtils httpUtils;

	private Utils utils;

	private String[] verstring;

	private String dqlatitude;

	private String dqlongitude;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(OldCCQStoreDetailActivity.this)
				.inflate(R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				OldCCQStoreDetailActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(OldCCQStoreDetailActivity.this,
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
//
//	/**
//	 * 初始化定位
//	 * 
//	 * @since 2.8.0
//	 * @author hongming.wang
//	 * 
//	 */
//	private void initLocation() {
//		// 初始化client
//		locationClient = new AMapLocationClient(
//				NewCCQStoreDetailActivity.this.getApplicationContext());
//		// 设置定位参数
//		locationClient.setLocationOption(getDefaultOption());
//		// 设置定位监听
//		locationClient.setLocationListener(locationListener);
//
//		// 定位
//		startLocation();
//	}
//
//	/**
//	 * 默认的定位参数
//	 * 
//	 * @since 2.8.0
//	 * @author hongming.wang
//	 * 
//	 */
//	private AMapLocationClientOption getDefaultOption() {
//		AMapLocationClientOption mOption = new AMapLocationClientOption();
//		mOption.setLocationMode(AMapLocationMode.Hight_Accuracy);// 可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
//		mOption.setGpsFirst(false);// 可选，设置是否gps优先，只在高精度模式下有效。默认关闭
//		mOption.setHttpTimeOut(30000);// 可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
//		mOption.setInterval(2000);// 可选，设置定位间隔。默认为2秒
//		mOption.setNeedAddress(true);// 可选，设置是否返回逆地理地址信息。默认是ture
//		mOption.setOnceLocation(false);// 可选，设置是否单次定位。默认是false
//		mOption.setOnceLocationLatest(false);// 可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
//		AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP);// 可选，
//																				// 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
//		return mOption;
//	}
//
//	/**
//	 * 定位监听
//	 */
//
//	AMapLocationListener locationListener = new AMapLocationListener() {
//		@Override
//		public void onLocationChanged(AMapLocation loc) {
//			if (null != loc) {
//				// 解析定位结果
//				// String result = LocationUtils.getLocationStr(loc);
//				if (loc.getErrorCode() == 0) {
//					// 经 度
//					longitude = loc.getLongitude() + "";
//					// 纬 度
//					latitude = loc.getLatitude() + "";
//					// Toast.makeText(getActivity(), "longitude...."+longitude,
//					// Toast.LENGTH_SHORT)
//					// .show();
//					// 市
//					String allshi = loc.getCity();
//					shi = allshi.replaceAll("市", "");
//					tv_now_place.setText(loc.getCity() + loc.getDistrict());
//					handler.sendEmptyMessage(HANDLER_DINGWEI_SUCCESS);
//				} else {
//					// 定位失败
//					Toast.makeText(NewCCQStoreDetailActivity.this, "定位失败",
//							Toast.LENGTH_SHORT).show();
//					handler.sendEmptyMessage(HANDLER_DINGWEI_FINISH);
//				}
//			} else {
//				// Toast.makeText(getActivity(), "正在定位中", Toast.LENGTH_SHORT)
//				// .show();
//				handler.sendEmptyMessage(HANDLER_DINGWEI_FINISH);
//			}
//		}
//	};
//
//	/**
//	 * 开始定位
//	 * 
//	 * @since 2.8.0
//	 * @author hongming.wang
//	 * 
//	 */
//	private void startLocation() {
//		// 设置定位参数
//		locationClient.setLocationOption(getDefaultOption());
//		// 启动定位
//		locationClient.startLocation();
//	}
//
//	/**
//	 * 停止定位
//	 * 
//	 * @since 2.8.0
//	 * @author hongming.wang
//	 * 
//	 */
//	private void stopLocation() {
//		// 停止定位
//		locationClient.stopLocation();
//	}
//
//	/**
//	 * 销毁定位
//	 * 
//	 * @since 2.8.0
//	 * @author hongming.wang
//	 * 
//	 */
//	private void destroyLocation() {
//		if (null != locationClient) {
//			/**
//			 * 如果AMapLocationClient是在当前Activity实例化的，
//			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
//			 */
//			locationClient.onDestroy();
//			locationClient = null;
//			locationOption = null;
//		}
//	}
	
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

}
