package ccj.yun28.com.sy.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.SearchProductlvAdapter;
import ccj.yun28.com.bean.ZhuBBsearchBean;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.sy.ProductDetailActivity;
import ccj.yun28.com.sy.SearchProductResultActivity;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.MyScrollView;
import ccj.yun28.com.view.OnRefreshListener;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 宝贝搜索结果价格fragment
 * 
 * @author meihuali
 * 
 */
public class PricebbFragment extends Fragment implements OnItemClickListener {
	// 换一换 页数
	private int nowPage = 1;

	private MyScrollView scrollView;

	private boolean flag = true;
	private List<Map<String, String>> searchList;
	private ListView lv;
	private TextView tv_notdata;
	// 价格顺序
	private String sort;

	private SearchProductlvAdapter searchProductlvAdapter;
	// json数据
	private ZhuBBsearchBean bbsearch;
	protected static final int HANDLER_NET_FAILURE = 0;
	private static final int HANDLER_SEARCHBB_SUCCESS = 1;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 2;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 3;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				scrollView.onResfreshFinish();
				dissDialog();
				Toast.makeText(getActivity(), "当前网络不可用,请检查网络", Toast.LENGTH_SHORT)
						.show();
				break;// 错误
			case HANDLER_NN_FAILURE:
				scrollView.onResfreshFinish();
				dissDialog();
				Toast.makeText(getActivity(), "当前网络出错,请检查网络", Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				scrollView.onResfreshFinish();
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(getActivity(), msg.obj.toString().trim(),
							Toast.LENGTH_SHORT).show();

				}
				break;
			case HANDLER_SEARCHBB_SUCCESS:
				scrollView.onResfreshFinish();
				dissDialog();
				if (searchList.size() < 1) {
					lv.setVisibility(View.GONE);
					tv_notdata.setVisibility(View.VISIBLE);
				} else {
					tv_notdata.setVisibility(View.GONE);
					lv.setVisibility(View.VISIBLE);
					searchProductlvAdapter.NotifyList(searchList, flag);
				}
				break;

			default:
				break;
			}

		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_search, null);
		scrollView = (MyScrollView) view.findViewById(R.id.scrollView);
		lv = (ListView) view.findViewById(R.id.lv);
		tv_notdata = (TextView) view.findViewById(R.id.tv_notdata);
		
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(getActivity());

		searchProductlvAdapter = new SearchProductlvAdapter(getActivity());
		lv.setAdapter(searchProductlvAdapter);

		sort = ((SearchProductResultActivity) getActivity()).getSort();

		searchList = new ArrayList<Map<String, String>>();
		product_type = SharedUtil.getStringValue(SharedCommon.PRODUCTLIST_TYPE,
				"");
		showLoading();
		if ("sy_ss".equals(product_type)) {
			String text = ((SearchProductResultActivity) getActivity())
					.getText();
			searchbbHttppost(text);
		} else if ("cfcy".equals(product_type) || "fzc".equals(product_type)) {
			String item_id = ((SearchProductResultActivity) getActivity())
					.getitem_id();
			searchsydhHttppost(item_id);
		} else if ("syfl".equals(product_type)) {
			String item_id = ((SearchProductResultActivity) getActivity())
					.getitem_id();
			searchsyflHttppost(item_id);
		}

		lv.setOnItemClickListener(this);
		initListView();
		return view;
	}

	// 首页分类下的系列
	private void searchsyflHttppost(String item_id) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("gc_id_2", item_id);
		// 1-销量 2-综合 3-价格
		params.addBodyParameter("key", "3");
		params.addBodyParameter("order", sort);
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.BB_SEARCH, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						searchbbListInfo(arg0.result);
					}
				});
	}

	// 首页导航下的系列
	private void searchsydhHttppost(String item_id) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("gc_id_1", item_id);
		// 1-销量 2-综合 3-价格
		params.addBodyParameter("key", "3");
		params.addBodyParameter("order", sort);
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.BB_SEARCH, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						searchbbListInfo(arg0.result);
					}
				});
	}

	// 搜索接口请求（宝贝）
	protected void searchbbHttppost(String text) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("keyword", text);
		// 1-销量 2-综合 3-价格
		params.addBodyParameter("key", "3");
		params.addBodyParameter("order", sort);
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.BB_SEARCH, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						searchbbListInfo(arg0.result);
					}
				});
	}

	// 解析搜索宝贝返回的数据
	protected void searchbbListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			// if ("200".equals(code)) {
			// Gson gson = new Gson();
			// bbsearch = gson.fromJson(result, ZhuBBsearchBean.class);
			// handler.sendEmptyMessage(HANDLER_SEARCHBB_SUCCESS);
			// }else{
			// String message = object.getString("message");
			// handler.sendMessage(handler.obtainMessage(HANDLER_GETINFO_FAILURE,
			// message));
			// }if ("200".equals(code)) {
			if ("200".equals(code)) {
				if (flag) {
					searchList = new ArrayList<Map<String, String>>();
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

	private void searchccqDetail(JSONObject obj) {
		// TODO Auto-generated method stub
		try {
			String goods_id = obj.getString("goods_id");
			String store_id = obj.getString("store_id");
			String store_name = obj.getString("store_name");
			String goods_name = obj.getString("goods_name");
			String goods_price = obj.getString("goods_price");
			String goods_image = obj.getString("goods_image");
			String goods_favorites_num = obj.getString("goods_favorites_num");
			Map<String, String> searchccq = new HashMap<String, String>();
			searchccq.put("goods_id", goods_id);
			searchccq.put("store_id", store_id);
			searchccq.put("store_name", store_name);
			searchccq.put("goods_name", goods_name);
			searchccq.put("goods_price", goods_price);
			searchccq.put("goods_image", goods_image);
			searchccq.put("goods_favorites_num", goods_favorites_num);
			searchList.add(searchccq);
			handler.sendEmptyMessage(HANDLER_SEARCHBB_SUCCESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 加载中动画
	private Dialog loadingDialog;

	private String product_type;

	private HttpUtils httpUtils;

	private Utils utils;

	private String[] verstring;

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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
		intent.putExtra("goods_id", searchList.get(arg2).get("goods_id"));
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
					nowPage++;
					flag = false;
					if ("sy_ss".equals(product_type)) {
						String text = ((SearchProductResultActivity) getActivity())
								.getText();
						searchbbHttppost(text);
					} else if ("cfcy".equals(product_type)
							|| "fzc".equals(product_type)) {
						String item_id = ((SearchProductResultActivity) getActivity())
								.getitem_id();
						searchsydhHttppost(item_id);
					} else if ("syfl".equals(product_type)) {
						String item_id = ((SearchProductResultActivity) getActivity())
								.getitem_id();
						searchsyflHttppost(item_id);
					}
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(HANDLER_NN_FAILURE);
				}

			}
		});
	}
}
