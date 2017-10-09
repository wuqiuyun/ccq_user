package ccj.yun28.com.ccq;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.StorePingJiaAdapter;
import ccj.yun28.com.ccq.OldCCQStoreDetailActivity.MyPageAdapter;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.MyScrollView;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

/**
 * 餐餐抢店铺详情-店铺环境图
 * 
 * @author meihuali
 * 
 */
public class storeHuanjingPicActivity extends BaseActivity implements
		OnClickListener {

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 错误
	private static final int HANDLER_NN_FAILURE = 1;
	// 获取内容失败
	protected static final int HANDLER_GETINFO_FAILURE = 2;

	private static final int HANDLER_STOREHUANJINGPIC_FAILURE = 3;
	// token失效
	private static final int HANDLER_TOKEN_FAILURE = 4;

	private int nowPage = 1;
	private MyScrollView scrollView;
	private List<Map<String, String>> storePingJiaList;
	private StorePingJiaAdapter storePingJiaadapter;

	private List<String> zhengjianimglist;
	private List<String> waibuimglist;
	private List<String> neibuimglist;
	private BitmapUtils bitmapUtils;

	// 商家外部
	private ImageView iv_sj_wb_first;
	private ImageView iv_sj_wb_second;
	private ImageView iv_sj_wb_third;
	private ImageView iv_sj_wb_four;
	private ImageView iv_sj_wb_five;
	private ImageView iv_sj_wb_six;
	// 商家内部
	private ImageView iv_sj_nb_first;
	private ImageView iv_sj_nb_second;
	private ImageView iv_sj_nb_third;
	// 证件资料
	private ImageView iv_zjzl_first;
	private ImageView iv_zjzl_second;
	private ImageView iv_zjzl_third;
	private ImageView iv_zjzl_four;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				if (storeHuanjingPicActivity.this != null) {
					Toast.makeText(storeHuanjingPicActivity.this,
							"当前网络不可用,请检查网络", Toast.LENGTH_SHORT).show();
				}
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				if (storeHuanjingPicActivity.this != null) {
					Toast.makeText(storeHuanjingPicActivity.this,
							"当前网络出错,请检查网络", Toast.LENGTH_SHORT).show();
				}
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (storeHuanjingPicActivity.this != null) {
					if (msg.obj != null) {
						Toast.makeText(storeHuanjingPicActivity.this,
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();
					}
				}
				break;
			// 获取信息成功
			case HANDLER_STOREHUANJINGPIC_FAILURE:
				dissDialog();
				
				bitmapUtils.display(iv_sj_wb_first, waibuimglist.get(0));
				bitmapUtils.display(iv_sj_wb_second, waibuimglist.get(1));
				bitmapUtils.display(iv_sj_wb_third, waibuimglist.get(2));
				bitmapUtils.display(iv_sj_wb_four, waibuimglist.get(3));
				bitmapUtils.display(iv_sj_wb_five, waibuimglist.get(4));
				bitmapUtils.display(iv_sj_wb_six, waibuimglist.get(5));
				
				bitmapUtils.display(iv_sj_nb_first, neibuimglist.get(0));
				bitmapUtils.display(iv_sj_nb_second, neibuimglist.get(1));
				bitmapUtils.display(iv_sj_nb_third, neibuimglist.get(2));
				
				bitmapUtils.display(iv_zjzl_first, zhengjianimglist.get(0));
				bitmapUtils.display(iv_zjzl_second, zhengjianimglist.get(1));
				bitmapUtils.display(iv_zjzl_third, zhengjianimglist.get(2));
				bitmapUtils.display(iv_zjzl_four, zhengjianimglist.get(3));
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (storeHuanjingPicActivity.this != null) {
					if (msg.obj != null) {
						Toast.makeText(storeHuanjingPicActivity.this,
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();
					}
				}
				Intent intent = new Intent(storeHuanjingPicActivity.this,
						LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_storehuanjingpic);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 商家外部环境图
		iv_sj_wb_first = (ImageView) findViewById(R.id.iv_sj_wb_first);
		iv_sj_wb_second = (ImageView) findViewById(R.id.iv_sj_wb_second);
		iv_sj_wb_third = (ImageView) findViewById(R.id.iv_sj_wb_third);
		iv_sj_wb_four = (ImageView) findViewById(R.id.iv_sj_wb_four);
		iv_sj_wb_five = (ImageView) findViewById(R.id.iv_sj_wb_five);
		iv_sj_wb_six = (ImageView) findViewById(R.id.iv_sj_wb_six);
		// 商家内部环境图
		iv_sj_nb_first = (ImageView) findViewById(R.id.iv_sj_nb_first);
		iv_sj_nb_second = (ImageView) findViewById(R.id.iv_sj_nb_second);
		iv_sj_nb_third = (ImageView) findViewById(R.id.iv_sj_nb_third);
		// 资质/荣誉
		iv_zjzl_first = (ImageView) findViewById(R.id.iv_zjzl_first);
		iv_zjzl_second = (ImageView) findViewById(R.id.iv_zjzl_second);
		iv_zjzl_third = (ImageView) findViewById(R.id.iv_zjzl_third);
		iv_zjzl_four = (ImageView) findViewById(R.id.iv_zjzl_four);

		bitmapUtils = new BitmapUtils(storeHuanjingPicActivity.this);
		
		store_id = getIntent().getStringExtra("store_id");

		zhengjianimglist = new ArrayList<String>();
		waibuimglist = new ArrayList<String>();
		neibuimglist = new ArrayList<String>();


		showLoading();
		storeHuanjingPicHttpPost();

		line_back.setOnClickListener(this);
		iv_sj_wb_first.setOnClickListener(this);
		iv_sj_wb_second.setOnClickListener(this);
		iv_sj_wb_third.setOnClickListener(this);
		iv_sj_wb_four.setOnClickListener(this);
		iv_sj_wb_five.setOnClickListener(this);
		iv_sj_wb_six.setOnClickListener(this);
		iv_sj_nb_first.setOnClickListener(this);
		iv_sj_nb_second.setOnClickListener(this);
		iv_sj_nb_third.setOnClickListener(this);
		iv_zjzl_first.setOnClickListener(this);
		iv_zjzl_second.setOnClickListener(this);
		iv_zjzl_third.setOnClickListener(this);
		iv_zjzl_four.setOnClickListener(this);
	}
	/**
	 * 获取店铺环境图请求接口
	 */
	private void storeHuanjingPicHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(storeHuanjingPicActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("store_id", store_id);
//		params.addBodyParameter("store_id", "1203");
		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWCCQDPDHUANJINGALLPIC,
				params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						storeHuanjingPicJsonInfo(arg0.result);
					}
				});

	}

	protected void storeHuanjingPicJsonInfo(String result) {
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				JSONObject data = object.getJSONObject("data");
				// 证件图 4张
				JSONArray document = data.getJSONArray("document");
				// 店铺外部图 6张
				JSONArray external = data.getJSONArray("external");
				// 店铺内部图 3张
				JSONArray inside = data.getJSONArray("inside");
				for (int i = 0; i < document.length(); i++) {
					String zhengjianimg = document.getString(i);
					zhengjianimglist.add(zhengjianimg);
				}
				for (int i = 0; i < external.length(); i++) {
					String waibuimg = external.getString(i);
					waibuimglist.add(waibuimg);
				}
				for (int i = 0; i < inside.length(); i++) {
					String neibuimg = inside.getString(i);
					neibuimglist.add(neibuimg);
				}
				handler.sendEmptyMessage(HANDLER_STOREHUANJINGPIC_FAILURE);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.iv_sj_wb_first:
			showStorePicDialog(waibuimglist,0);
			break;
		case R.id.iv_sj_wb_second:
			showStorePicDialog(waibuimglist,1);
			break;
		case R.id.iv_sj_wb_third:
			showStorePicDialog(waibuimglist,2);
			break;
		case R.id.iv_sj_wb_four:
			showStorePicDialog(waibuimglist,3);
			break;
		case R.id.iv_sj_wb_five:
			showStorePicDialog(waibuimglist,4);
			break;
		case R.id.iv_sj_wb_six:
			showStorePicDialog(waibuimglist,5);
			break;
		case R.id.iv_sj_nb_first:
			showStorePicDialog(neibuimglist,0);
			break;
		case R.id.iv_sj_nb_second:
			showStorePicDialog(neibuimglist,1);
			break;
		case R.id.iv_sj_nb_third:
			showStorePicDialog(neibuimglist,2);
			break;
		case R.id.iv_zjzl_first:
			showStorePicDialog(zhengjianimglist,0);
			break;
		case R.id.iv_zjzl_second:
			showStorePicDialog(zhengjianimglist,1);
			break;
		case R.id.iv_zjzl_third:
			showStorePicDialog(zhengjianimglist,2);
			break;
		case R.id.iv_zjzl_four:
			showStorePicDialog(zhengjianimglist,3);
			break;
		case R.id.iv_dia_store_pic:
			storePicDialog.dismiss();
			break;

		default:
			break;
		}
	}

	private Dialog storePicDialog;
	private List<String> pagelist;
	private ImageView[] mImageViews;
	private ViewPager viewPager;
	
	private void showStorePicDialog(List<String> list, int pagenum) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(storeHuanjingPicActivity.this)
				.inflate(R.layout.activity_bigpicchange, null);

		ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
		
		pagelist = new ArrayList<String>();
		pagelist = list;

		mImageViews = new ImageView[list.size()];
		for (int i = 0; i < mImageViews.length; i++) {
			ImageView imageView = new ImageView(this);
			mImageViews[i] = imageView;
			bitmapUtils.display(imageView, list.get(i));
			imageView.setScaleType(ScaleType.FIT_CENTER);
		}

		MyPageAdapter pageAdapter = new MyPageAdapter();
		// 设置Adapter
		viewPager.setAdapter(pageAdapter);
		viewPager.setCurrentItem(pagenum);
		// // 设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
		// viewPager.setCurrentItem((mImageViews.length) * 100);

		storePicDialog = new Dialog(storeHuanjingPicActivity.this,
				R.style.mDialogStyle);
		storePicDialog.setContentView(view);
		storePicDialog.setCanceledOnTouchOutside(true);
		storePicDialog.show();
	}

	// 加载中动画
	private Dialog loadingDialog;
	private String store_id;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(storeHuanjingPicActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				storeHuanjingPicActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(storeHuanjingPicActivity.this,
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
			return pagelist.size();
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
}

