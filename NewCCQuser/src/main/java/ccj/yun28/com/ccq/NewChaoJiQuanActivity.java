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
import android.os.Message;
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
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.RecmmendGoodsActivity;
import ccj.yun28.com.adapter.CcqJuanLvAdapter;
import ccj.yun28.com.adapter.CcqQuanLvAdapter;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 餐餐抢首页--新超值名券 0830
 * 
 * @author meihuali
 * 
 */
public class NewChaoJiQuanActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {
	private LinearLayout ll_empty;
	private TextView tv_back;

	// private CcqJuanLvAdapter ccqJuanLvAdapter;
	private String latitude;
	private String longitude;
	private String city_id;
	private CcqQuanLvAdapter ccqQuanLvAdapter;
	private List<Map<String, String>> ccqgoodsListdetaiList;
	private MaterialRefreshLayout refreshLayout;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取数据失败
	private static final int HANDLER_GETIONFO_FAILURE = 2;
	private static final int HANDLER_CCQGOODSLIST_SUCCESS = 3;
	private int page = 1;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				dissDialog();
				String data = (String) msg.obj;
				if (NewChaoJiQuanActivity.this != null) {

					Toast.makeText(NewChaoJiQuanActivity.this, data,
							Toast.LENGTH_SHORT).show();
					Log.e("log", "data-->" + data);
					// Toast.makeText(NewChaoJiQuanActivity.this,
					// "当前网络不可用,请检查网络",
					// Toast.LENGTH_SHORT).show();
				}
				break;
			case HANDLER_NN_FAILURE:
				dissDialog();
				if (NewChaoJiQuanActivity.this != null) {

					Toast.makeText(NewChaoJiQuanActivity.this, "系统异常，请稍后再试",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case HANDLER_GETIONFO_FAILURE:
				dissDialog();
				if (NewChaoJiQuanActivity.this != null) {

					if (msg.obj != null) {
						Toast.makeText(NewChaoJiQuanActivity.this,
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();
					}
				}
				break;
			case HANDLER_CCQGOODSLIST_SUCCESS:
				dissDialog();
				ccqQuanLvAdapter.NotifyList(ccqgoodsListdetaiList);
				ccqQuanLvAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chaojimingquan);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		ListView lv = (ListView) findViewById(R.id.lv);
		ll_empty = (LinearLayout) findViewById(R.id.ll_empty);
		tv_back = (TextView) findViewById(R.id.tv_goto_recommend);
		refreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh_layout);
		refreshLayout.setLoadMore(true);

		lv.setEmptyView(ll_empty);
		tv_title.setText("超级名券");

		ccqgoodsListdetaiList = new ArrayList<Map<String, String>>();

		// ccqJuanLvAdapter = new CcqJuanLvAdapter(NewChaoZhiQuanActivity.this);
		ccqQuanLvAdapter = new CcqQuanLvAdapter(NewChaoJiQuanActivity.this);
		lv.setAdapter(ccqQuanLvAdapter);

		Intent intent = getIntent();
		if (getIntent() != null) {
			latitude = getIntent().getStringExtra("starlat");
			longitude = getIntent().getStringExtra("starlng");
			city_id = getIntent().getStringExtra("city_id");
		}

		showLoading();
		initListView();
		getJuanListHttpPost();

		line_back.setOnClickListener(this);
		tv_back.setOnClickListener(this);
		lv.setOnItemClickListener(this);

	}

	private void initListView() {
		refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {

			@Override
			public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
				page = 1;
				getJuanListHttpPost();
			}

			@Override
			public void onRefreshLoadMore(
					MaterialRefreshLayout materialRefreshLayout) {
				++page;
				getJuanListHttpPost();
			}
		});
	}

	private void getJuanListHttpPost() {
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(NewChaoJiQuanActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("lat1", latitude);
		params.addBodyParameter("lng1", longitude);
		params.addBodyParameter("city_id", city_id);
		params.addBodyParameter("goods_images_op", "!m");
		params.addBodyParameter("goods_images_size", "220x180");
		params.addBodyParameter("page", page + "");

		httpUtils.send(HttpMethod.POST, JiekouUtils.CHAOZHIJUAN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Message msg = new Message();
						msg.obj = arg1;
						msg.what = HANDLER_NET_FAILURE;
						handler.sendMessage(msg);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						ccqJuanListJsonInfo(arg0.result);
						refreshLayout.finishRefresh();
						refreshLayout.finishRefreshLoadMore();
					}
				});
	}

	protected void ccqJuanListJsonInfo(String result) {
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				if (page == 1) {
					ccqgoodsListdetaiList.clear();
				}

				// 商品条目
				JSONArray array = object.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = array.getJSONObject(i);
					goods_listListInfo(json);
				}

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
	private void goods_listListInfo(JSONObject json) {
		try {
			String discount = json.getString("discount");
			String goods_id = json.getString("goods_id");
			String goods_image = json.getString("goods_image");
			String goods_marketprice = json.getString("goods_marketprice");
			String goods_name = json.getString("goods_name");
			String goods_price = json.getString("goods_price");
			String goods_storage = json.getString("goods_storage");
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
			ccqgoodsDetailMap.put("goods_storage", goods_storage);
			ccqgoodsDetailMap.put("store_address", store_address);
			ccqgoodsDetailMap.put("store_id", store_id);
			ccqgoodsDetailMap.put("store_name", store_name);
			ccqgoodsListdetaiList.add(ccqgoodsDetailMap);
			handler.sendEmptyMessage(HANDLER_CCQGOODSLIST_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 加载中动画
	private Dialog loadingDialog;
	private Intent intent;
	private Utils utils;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(NewChaoJiQuanActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				NewChaoJiQuanActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(NewChaoJiQuanActivity.this,
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
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;

		case R.id.tv_goto_recommend:
			intent = new Intent(NewChaoJiQuanActivity.this,
					RecmmendGoodsActivity.class);
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Intent intent = new Intent(NewChaoJiQuanActivity.this,
				CcqProDetailActivity.class);
		intent.putExtra("starlat", latitude);
		intent.putExtra("starlng", longitude);
		intent.putExtra("ccqgoods_id",
				ccqgoodsListdetaiList.get(position).get("goods_id"));
		startActivity(intent);
	}

}
