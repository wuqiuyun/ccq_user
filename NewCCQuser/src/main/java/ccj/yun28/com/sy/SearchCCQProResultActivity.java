package ccj.yun28.com.sy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.CcqJuanLvAdapter;
import ccj.yun28.com.adapter.SearchccqlvAdapter;
import ccj.yun28.com.bean.ZhuBBsearchBean;
import ccj.yun28.com.ccq.CCQStoreDetailActivity;
import ccj.yun28.com.ccq.CcqProDetailActivity;
import ccj.yun28.com.ccq.OldCCQStoreDetailActivity;
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
 * 餐餐抢券结果
 * 
 * @author meihuali
 * 
 */
public class SearchCCQProResultActivity extends BaseActivity implements
		OnClickListener, OnEditorActionListener, OnItemClickListener {

	private boolean flag = true;
	// 输入的文本
	private String text;
	private TextView tv_price;
	private ImageView iv_price;
	private TextView tv_xiaoliang;
	private TextView tv_zonghe;

	private String sort = "1";
	private String lat1;
	private String lng1;
	private MyListView lv;
	private TextView tv_notdata;
	// 换一换 页数
	private int nowPage = 1;

	private MyScrollView scrollView;

	// json数据
	private ZhuBBsearchBean bbsearch;
	protected static final int HANDLER_NET_FAILURE = 0;
	protected static final int HANDLER_SEARCHBB_SUCCESS = 1;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 2;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 3;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(SearchCCQProResultActivity.this,
						"当前网络不可用,请检查网络", Toast.LENGTH_SHORT).show();
				break;// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(SearchCCQProResultActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				scrollView.onResfreshFinish();
				dissDialog();
				if (msg.obj != null) {
					if (flag) {
						lv.setVisibility(View.GONE);
						tv_notdata.setVisibility(View.VISIBLE);
					}
					Toast.makeText(SearchCCQProResultActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();

				}
				break;
			case HANDLER_SEARCHBB_SUCCESS:
				scrollView.onResfreshFinish();
				dissDialog();
				if (ccqgoodsListdetaiList.size() < 1) {
					lv.setVisibility(View.GONE);
					tv_notdata.setVisibility(View.VISIBLE);
				} else {
					tv_notdata.setVisibility(View.GONE);
					lv.setVisibility(View.VISIBLE);
					ccqJuanLvAdapter.NotifyList(ccqgoodsListdetaiList);
					ccqJuanLvAdapter.notifyDataSetChanged();
				}
				break;

			default:
				break;
			}

		};
	};
	private TextView tv_cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchshopresult);
		// 返回
		// LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		tv_cancel = (TextView) findViewById(R.id.tv_back);
		et_sousuo = (EditText) findViewById(R.id.et_sousuo);
		scrollView = (MyScrollView) findViewById(R.id.scrollView);
		lv = (MyListView) findViewById(R.id.lv);
		tv_notdata = (TextView) findViewById(R.id.tv_notdata);

		ccqgoodsListdetaiList = new ArrayList<Map<String, String>>();
		ccqJuanLvAdapter = new CcqJuanLvAdapter(SearchCCQProResultActivity.this);
		lv.setAdapter(ccqJuanLvAdapter);

		if (getIntent() != null) {
			text = getIntent().getStringExtra("text");
			lat1 = getIntent().getStringExtra("lat1");
			lng1 = getIntent().getStringExtra("lng1");
		}
		showLoading();
		searchccqHttppost(text);

		initListView();
		tv_cancel.setOnClickListener(this);
		et_sousuo.setOnEditorActionListener(this);
		lv.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_back:
			onBackPressed();
			break;
		}
	}

	// 搜索接口请求（宝贝）
	protected void searchccqHttppost(String text) {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils
				.getVersionInfo(SearchCCQProResultActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("lat1", lat1);
		params.addBodyParameter("lng1", lng1);
		params.addBodyParameter("goods_name", text);
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.CCQPROLIST, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						searchccqListInfo(arg0.result);
					}
				});
	}

	// 解析搜索宝贝返回的数据
	protected void searchccqListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				if (flag) {
					ccqgoodsListdetaiList = new ArrayList<Map<String, String>>();
				}
				JSONArray data = object.getJSONArray("data");
				for (int i = 0; i < data.length(); i++) {
					JSONObject obj = data.getJSONObject(i);
					searchccqDetail(obj);
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

	private void searchccqDetail(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String goods_id = json.getString("goods_id");
			String store_id = json.getString("store_id");
			String goods_name = json.getString("goods_name");
			String goods_addtime = json.getString("goods_addtime");
			String goods_click = json.getString("goods_click");
			String goods_marketprice = json.getString("goods_marketprice");
			String goods_price = json.getString("goods_price");
			String goods_image = json.getString("goods_image");
			String goods_storage = json.getString("goods_storage");
			String goods_salenum = json.getString("goods_salenum");
			String remark = json.getString("remark");
			String discount = json.getString("discount");
			String store_name = json.getString("store_name");
			String longitude = json.getString("longitude");
			String latitude = json.getString("latitude");
			String distance_value = json.getString("distance_value");
			String store_address = json.getString("store_address");
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
			ccqgoodsDetailMap.put("goods_addtime", goods_addtime);
			ccqgoodsDetailMap.put("goods_click", goods_click);
			ccqgoodsDetailMap.put("goods_storage", goods_storage);
			ccqgoodsDetailMap.put("goods_salenum", goods_salenum);
			ccqgoodsDetailMap.put("remark", remark);
			ccqgoodsDetailMap.put("distance_value", distance_value);
			ccqgoodsDetailMap.put("longitude", longitude);
			ccqgoodsDetailMap.put("latitude", latitude);
			ccqgoodsListdetaiList.add(ccqgoodsDetailMap);
			handler.sendEmptyMessage(HANDLER_SEARCHBB_SUCCESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			String text = et_sousuo.getText().toString().trim();
			if (TextUtils.isEmpty(text)) {
				Toast.makeText(SearchCCQProResultActivity.this, "搜索文本不能为空",
						Toast.LENGTH_SHORT).show();
			} else {
				nowPage = 1;
				showLoading();
				flag = true;
				searchccqHttppost(text);
			}

			return true;
		}

		return false;
	}

	// 加载中动画
	private Dialog loadingDialog;
	private EditText et_sousuo;
	private List<Map<String, String>> ccqgoodsListdetaiList;
	private CcqJuanLvAdapter ccqJuanLvAdapter;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(SearchCCQProResultActivity.this)
				.inflate(R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				SearchCCQProResultActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(SearchCCQProResultActivity.this,
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
					flag = false;
					searchccqHttppost(text);
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(HANDLER_NN_FAILURE);
				}

			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		// Intent intent = new Intent(SearchCCQResultActivity.this,
		// CCQStoreDetailActivity.class);

		Intent intent = new Intent(SearchCCQProResultActivity.this,
				CcqProDetailActivity.class);
		intent.putExtra("starlat", lat1);
		intent.putExtra("starlng", lng1);
		intent.putExtra("ccqgoods_id",
				ccqgoodsListdetaiList.get(arg2).get("goods_id"));
		startActivity(intent);

	}

}
