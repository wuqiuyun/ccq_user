package ccj.yun28.com.sy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.MyGridView;
import ccj.yun28.com.view.MyListView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 搜索页
 * 
 * @author meihuali
 * 
 */
public class SearchActivity extends BaseActivity implements OnClickListener,
		OnEditorActionListener, OnItemClickListener {

	private MyListView lv_search_record;// 历史listview
	private TextView clear_history;// 清除历史记录
	private List<String> listValues;
	private ArrayAdapter<String> adapter;
	// 整个历史记录
	private LinearLayout line_history_record;

	private PopupWindow popupWindow;
	// 搜索类型(宝贝)
	private String type = "bb";
	// 热搜list
	private List<String> hotList;
	// 热搜适配器
	private GridAdapter gridAdapter;

	//热门搜索类型，1：店铺；2：商品 默认商品
	private String hottype = "1";
	// 加载中动画
	private Dialog loadingDialog;

	public static final String SEARCH_HISTORY = "search_history";
	private EditText et_search;
	private TextView tv_choose;

	protected static final int HANDLER_NET_FAILURE = 0;
	protected static final int HANDLER_HOT_SEARCH_SUCCESS = 1;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				Toast.makeText(SearchActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_HOT_SEARCH_SUCCESS:
				dissDialog();
				gridAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}

		};
	};
	private String lat1;
	private String lng1;
	private MyGridView gv;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		// 选择店铺，宝贝，联盟商家
		tv_choose = (TextView) findViewById(R.id.tv_choose);
		et_search = (EditText) findViewById(R.id.et_search);
		TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		line_history_record = (LinearLayout) findViewById(R.id.line_history_record);

		gv = (MyGridView) findViewById(R.id.gv);

		lv_search_record = (MyListView) findViewById(R.id.lv_search_record);

		View history = LayoutInflater.from(this).inflate(
				R.layout.list_footer_view, null);
		clear_history = (TextView) history.findViewById(R.id.clear_history);
		lv_search_record.addFooterView(history);
		
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(SearchActivity.this);

		hotList = new ArrayList<String>();
		listValues = new ArrayList<String>();

		initSearchHistory();
		adapter = new ArrayAdapter<String>(this,
				R.layout.item_discover_search_history, R.id.history, listValues);
		if (listValues.size() > 0) {
			adapter.notifyDataSetChanged();
			lv_search_record.setAdapter(adapter);
			line_history_record.setVisibility(View.VISIBLE);
			clear_history.setVisibility(View.VISIBLE);
		} else {
			line_history_record.setVisibility(View.GONE);
			clear_history.setVisibility(View.GONE);
		}

		if (getIntent() != null && getIntent().getStringExtra("type") != null) {
			if ("餐餐抢".equals(getIntent().getStringExtra("type"))) {
				tv_choose.setText("商家 ");
				type = "ccq";
				lat1 = getIntent().getStringExtra("lat1");
				lng1 = getIntent().getStringExtra("lng1");
				showLoading();
//				hotccqHttpPost();
				hottype = "1";
				hotHttpPost();
			}else if ("宝贝".equals(getIntent().getStringExtra("type"))) {
				tv_choose.setText("抢券 ");
				type = "bb";
				showLoading();
				hottype = "2";
				hotHttpPost();
			}
		}

		gridAdapter = new GridAdapter();
		gv.setAdapter(gridAdapter);

		lv_search_record.setOnItemClickListener(this);
		gv.setOnItemClickListener(this);
		tv_choose.setOnClickListener(this);
		tv_cancel.setOnClickListener(this);
		clear_history.setOnClickListener(this);
		et_search.setOnEditorActionListener(this);
	}

//	private void hotccqHttpPost() {
//		// TODO Auto-generated method stub
//		RequestParams params = new RequestParams();
//		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
//		httpUtils.send(HttpMethod.POST, JiekouUtils.HOT_CCQSEARCH,params,
//				new RequestCallBack<String>() {
//
//					@Override
//					public void onFailure(HttpException arg0, String arg1) {
//						// TODO Auto-generated method stub
//						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
//					}
//
//					@Override
//					public void onSuccess(ResponseInfo<String> arg0) {
//						// TODO Auto-generated method stub
//						hotListinfo(arg0.result);
//					}
//				});
//	}

	// 热门搜索接口请求
	private void hotHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("type", hottype);//1：店铺；2：商品 默认商品
		httpUtils.send(HttpMethod.POST, JiekouUtils.HOT_SEARCH,params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						hotListinfo(arg0.result);
					}
				});
	}

	// 解析热门接口返回数据
	protected void hotListinfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				hotList.clear();
				JSONArray array = object.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = array.getJSONObject(i);
					String title = json.getString("title");
					hotList.add(title);
				}
				handler.sendEmptyMessage(HANDLER_HOT_SEARCH_SUCCESS);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_cancel:
			onBackPressed();
			break;
		case R.id.tv_choose:
			View view = View.inflate(SearchActivity.this,
					R.layout.popupwindow_searchchoose, null);
			TextView tv_shop = (TextView) view.findViewById(R.id.tv_shop);
			TextView tv_product = (TextView) view.findViewById(R.id.tv_product);
			TextView tv_lmsj = (TextView) view.findViewById(R.id.tv_lmsj);

			tv_shop.setOnClickListener(this);
			tv_product.setOnClickListener(this);
			tv_lmsj.setOnClickListener(this);

			popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, true);
			popupWindow.setTouchable(true);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setFocusable(true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.showAsDropDown(tv_choose);
			break;
		case R.id.tv_shop:
			type = "dp";
			popupWindow.dismiss();
			tv_choose.setText("店铺   ");
			break;
		case R.id.tv_product:
			type = "bb";
			popupWindow.dismiss();
			tv_choose.setText("抢券   ");
			hottype = "2";
			hotHttpPost();
			break;
		case R.id.tv_lmsj:
			type = "ccq";
			popupWindow.dismiss();
			tv_choose.setText("商家    ");
			hottype = "1";
			hotHttpPost();
			break;
		case R.id.clear_history:
			SharedPreferences sp = getSharedPreferences(SEARCH_HISTORY, 0);
			sp.edit().putString(SEARCH_HISTORY, "").commit();
			initSearchHistory();
			adapter.notifyDataSetChanged();
			line_history_record.setVisibility(View.GONE);
			clear_history.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	/**
	 * 读取历史搜索记录
	 */
	public void initSearchHistory() {
		listValues.clear();
		String longhistory = "";
		SharedPreferences sp = getSharedPreferences(
				SearchActivity.SEARCH_HISTORY, 0);
		longhistory = sp.getString(SearchActivity.SEARCH_HISTORY, "");
		String[] hisArrays = longhistory.split(",");
		// listValues = new ArrayList<String>();
		if (hisArrays.length == 0) {
			return;
		}
		for (int i = 0; i < hisArrays.length; i++) {
			if (!TextUtils.isEmpty(hisArrays[i])) {
				listValues.add(hisArrays[i]);
			}
		}
	}

	/**
	 * 保存搜索记录
	 */
	private void saveSearchHistory() {
		String text = et_search.getText().toString().trim();
		if (text.length() < 1) {
			return;
		}
		SharedPreferences sp = getSharedPreferences(SEARCH_HISTORY, 0);
		String longhistory = sp.getString(SEARCH_HISTORY, "");
		String[] tmpHistory = longhistory.split(",");
		ArrayList<String> historyList = new ArrayList<String>(
				Arrays.asList(tmpHistory));
		if (historyList.size() > 0) {
			int i;
			for (i = 0; i < historyList.size(); i++) {
				if (text.equals(historyList.get(i))) {
					historyList.remove(i);
					break;
				}
			}
			historyList.add(0, text);
		}
		if (historyList.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < historyList.size(); i++) {
				if (!TextUtils.isEmpty((historyList.get(i)))) {
					sb.append(historyList.get(i) + ",");
				}
			}
			sp.edit().putString(SEARCH_HISTORY, sb.toString()).commit();
		} else {
			if (!"".equals(text) && text != null) {
				sp.edit().putString(SEARCH_HISTORY, text + ",").commit();

			}
		}
	}

	private class GridAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return hotList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return hotList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = LayoutInflater.from(SearchActivity.this).inflate(
						R.layout.item_hot_search, null);
			}
			TextView textView = (TextView) convertView
					.findViewById(R.id.label_item);
			textView.setText(hotList.get(position));
			return convertView;
		}

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		initSearchHistory();
		adapter = new ArrayAdapter<String>(this,
				R.layout.item_discover_search_history, R.id.history, listValues);
		if (listValues.size() > 0) {
			adapter.notifyDataSetChanged();
			lv_search_record.setAdapter(adapter);
			line_history_record.setVisibility(View.VISIBLE);
			clear_history.setVisibility(View.VISIBLE);
		} else {
			line_history_record.setVisibility(View.GONE);
			clear_history.setVisibility(View.GONE);
		}
	}

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(SearchActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(SearchActivity.this,
				R.anim.loading_anim);
		image.startAnimation(animation);

		loadingDialog = new Dialog(SearchActivity.this, R.style.mDialogStyle);
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
			String text = et_search.getText().toString().trim();
			if (TextUtils.isEmpty(text)) {
				Toast.makeText(SearchActivity.this, "搜索文本不能为空",
						Toast.LENGTH_SHORT).show();
			} else {
				saveSearchHistory();
				if ("bb".equals(type)) {
					Intent intent = new Intent(SearchActivity.this,
							SearchCCQProResultActivity.class);
					SharedUtil.saveStringValue(SharedCommon.PRODUCTLIST_TYPE,
							"sy_ss");
					intent.putExtra("text", text);
					intent.putExtra("lat1", lat1);
					intent.putExtra("lng1", lng1);
					startActivity(intent);
				}
				if ("dp".equals(type)) {
					Intent intent = new Intent(SearchActivity.this,
							SearchShopResultActivity.class);
					intent.putExtra("text", text);
					startActivity(intent);
				}
				if ("ccq".equals(type)) {
					Intent intent = new Intent(SearchActivity.this,
							SearchCCQResultActivity.class);
					intent.putExtra("text", text);
					intent.putExtra("lat1", lat1);
					intent.putExtra("lng1", lng1);
					startActivity(intent);
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		if (parent == gv) {
			if ("ccq".equals(type)) {
				Intent intent = new Intent(SearchActivity.this,
						SearchCCQResultActivity.class);
				intent.putExtra("text", hotList.get(position));
				intent.putExtra("lat1", lat1);
				intent.putExtra("lng1", lng1);
				startActivity(intent);
			}else {
				SharedUtil.saveStringValue(SharedCommon.PRODUCTLIST_TYPE,
						"sy_ss");
//				Intent intent = new Intent(SearchActivity.this,
//						SearchProductResultActivity.class);
				Intent intent = new Intent(SearchActivity.this,
						SearchCCQProResultActivity.class);
				intent.putExtra("text", hotList.get(position));
				intent.putExtra("lat1", lat1);
				intent.putExtra("lng1", lng1);
				startActivity(intent);
			}
		} else if (parent == lv_search_record) {
			if ("ccq".equals(type)) {
				Intent intent = new Intent(SearchActivity.this,
						SearchCCQResultActivity.class);
				intent.putExtra("text", listValues.get(position));
				intent.putExtra("lat1", lat1);
				intent.putExtra("lng1", lng1);
				startActivity(intent);
			}else {
				SharedUtil.saveStringValue(SharedCommon.PRODUCTLIST_TYPE,
						"sy_ss");
				Intent intent = new Intent(SearchActivity.this,
						SearchCCQProResultActivity.class);
				intent.putExtra("text", listValues.get(position));
				intent.putExtra("lat1", lat1);
				intent.putExtra("lng1", lng1);
				startActivity(intent);
			}
			
		}
		
	}

}
