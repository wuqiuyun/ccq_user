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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.CcqShopLvAdapter;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.MyListView;
import ccj.yun28.com.view.MyScrollView;
import ccj.yun28.com.view.OnRefreshListener;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 全部餐餐抢店铺
 * 
 * @author meihuali
 * 
 */
public class AllCcqShopActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {
	private String latitude;
	private String longitude;
	private String city_id;
	private MyScrollView scrollView;
	private List<Map<String, String>> ccqshopListdetaiList;
	private CcqShopLvAdapter ccqShopLvAdapter;
	private int nowPage = 1;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取数据失败
	private static final int HANDLER_GETIONFO_FAILURE = 2;
	private static final int HANDLER_CCQSHOPDETAI_SUCCESS = 3;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				line_net_error.setVisibility(View.VISIBLE);
				scrollView.onResfreshFinish();
				dissDialog();
				if (AllCcqShopActivity.this != null) {

					Toast.makeText(AllCcqShopActivity.this, "当前网络不可用,请检查网络",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case HANDLER_NN_FAILURE:
				scrollView.onResfreshFinish();
				line_net_error.setVisibility(View.VISIBLE);
				dissDialog();
				if (AllCcqShopActivity.this != null) {

					Toast.makeText(AllCcqShopActivity.this, "当前网络出错,请检查网络",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case HANDLER_GETIONFO_FAILURE:
				scrollView.onResfreshFinish();
				dissDialog();
				if (AllCcqShopActivity.this != null) {
					if (nowPage == 1) {
						line_net_error.setVisibility(View.VISIBLE);
					}else{
						line_net_error.setVisibility(View.GONE);
					}
					if (msg.obj != null) {
						Toast.makeText(AllCcqShopActivity.this,
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();

					}
				}
				break;
			case HANDLER_CCQSHOPDETAI_SUCCESS:
				line_net_error.setVisibility(View.GONE);
				scrollView.onResfreshFinish();
				dissDialog();
				ccqShopLvAdapter.NotifyList(ccqshopListdetaiList);
				ccqShopLvAdapter.notifyDataSetChanged();
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
		setContentView(R.layout.activity_allccqjuanorshop);
		line_net_error = (LinearLayout) findViewById(R.id.line_net_error);
		TextView tv_notnet_refresh = (TextView) findViewById(R.id.tv_notnet_refresh);
		scrollView = (MyScrollView) findViewById(R.id.scrollView);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		MyListView lv = (MyListView) findViewById(R.id.lv);

		tv_title.setText("餐餐抢店铺");

		ccqshopListdetaiList = new ArrayList<Map<String, String>>();

		ccqShopLvAdapter = new CcqShopLvAdapter(AllCcqShopActivity.this);
		lv.setAdapter(ccqShopLvAdapter);

		Intent intent = getIntent();
		if (getIntent() != null) {
			latitude = getIntent().getStringExtra("starlat");
			longitude = getIntent().getStringExtra("starlng");
			city_id = getIntent().getStringExtra("city_id");
		}

		showLoading();
		getStoreListHttpPost();

		initListView();

		line_back.setOnClickListener(this);
		tv_notnet_refresh.setOnClickListener(this);
		lv.setOnItemClickListener(this);
	}

	// 获取餐餐抢店铺列表数据
	private void getStoreListHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(AllCcqShopActivity.this);
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("lat1", latitude);
		params.addBodyParameter("lng1", longitude);
		params.addBodyParameter("city_id", city_id);
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
			// String city_id = json.getString("city_id");
			// String county_id = json.getString("county_id");
			String distance = "";
			String evaluate = json.getString("evaluate");
			// String latitude = json.getString("latitude");
			// String longitude = json.getString("longitude");
			// String member_id = json.getString("member_id");
			String store_address = json.getString("store_address");
			String store_credit = json.getString("store_credit");
			String store_id = json.getString("store_id");
			String store_name = json.getString("store_name");
			String union_img = json.getString("union_img");
			Map<String, String> ccqshopDetailMap = new HashMap<String, String>();
			// ccqshopDetailMap.put("city_id", city_id);
			// ccqshopDetailMap.put("county_id", county_id);
			ccqshopDetailMap.put("distance", distance);
			ccqshopDetailMap.put("evaluate", evaluate);
			// ccqshopDetailMap.put("latitude", latitude);
			// ccqshopDetailMap.put("longitude", longitude);
			// ccqshopDetailMap.put("member_id", member_id);
			ccqshopDetailMap.put("store_address", store_address);
			ccqshopDetailMap.put("store_credit", store_credit);
			ccqshopDetailMap.put("store_id", store_id);
			ccqshopDetailMap.put("store_name", store_name);
			ccqshopDetailMap.put("union_img", union_img);
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
	private LinearLayout line_net_error;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(AllCcqShopActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				AllCcqShopActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(AllCcqShopActivity.this,
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.tv_notnet_refresh:
			nowPage = 1;
			showLoading();
			getStoreListHttpPost();
			initListView();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
//		Intent intent = new Intent(AllCcqShopActivity.this,
//				CCQStoreDetailActivity.class);
		Intent intent = new Intent(AllCcqShopActivity.this,
				OldCCQStoreDetailActivity.class);
		intent.putExtra("starlat", latitude);
		intent.putExtra("starlng", longitude);
		intent.putExtra("store_id",
				ccqshopListdetaiList.get(position).get("store_id"));
		startActivity(intent);
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
					showLoading();
					nowPage++;
					getStoreListHttpPost();
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(HANDLER_NN_FAILURE);
				}

			}
		});
	}
}