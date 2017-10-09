package ccj.yun28.com.neworder;

import java.util.List;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.RecmmendGoodsActivity;
import ccj.yun28.com.bean.newbean.OrderDataBean;
import ccj.yun28.com.bean.newbean.RecommendList;
import ccj.yun28.com.ccq.NewCcqProDetailActivity;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.json.CommonList;
import ccj.yun28.com.neworder.OrderAdapter.OnRefreshListener;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.DialogUtil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.HorizontalListView;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 待评价
 */

public class NoEvaluateFragment extends Fragment implements OnClickListener {

	private View view;
	private ListView mLvOrder;
	private HorizontalListView mLvRecommend;
	private OrderAdapter orderAdapter;// 订单
	private RecommendAdapter recommendAdapter;// 向你推荐其他热抢美食

	private LinearLayout ll_no_login;
	private Button btn_login;// 登录
	private ScrollView scrollView;
	private TextView tv_more_recommend;

	private List<OrderDataBean> data;
	private List<RecommendList> recommendData;
	private Utils utils;
	private String[] verstring;
	private HttpUtils httpUtils;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_recommend_food, null);
		/*
		 * View topView =
		 * LayoutInflater.from(getActivity()).inflate(R.layout.fragment_order,
		 * null);
		 */

		mLvOrder = (ListView) view.findViewById(R.id.lv_order);
		mLvRecommend = (HorizontalListView) view
				.findViewById(R.id.lv_recommend);

		ll_no_login = (LinearLayout) view.findViewById(R.id.ll_no_login);
		btn_login = (Button) view.findViewById(R.id.btn_login);
		scrollView = (ScrollView) view.findViewById(R.id.scrollView);
		tv_more_recommend = (TextView) view
				.findViewById(R.id.tv_more_recommend);
		tv_more_recommend.setOnClickListener(this);

		// mLvRecommend.addHeaderView(topView);

		if (!isLogin()) {
			ll_no_login.setVisibility(View.VISIBLE);
			scrollView.setVisibility(View.GONE);
			btn_login.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(),
							LoginActivity.class);
					intent.putExtra("type", "order");
					startActivity(intent);
				}
			});
		} else {
			ll_no_login.setVisibility(View.GONE);
			scrollView.setVisibility(View.VISIBLE);
		}
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initData();

	}

	private DBHelper myDB;

	// 校验登录与否
	private boolean isLogin() {
		if (myDB == null) {
			myDB = new DBHelper(getActivity());
		}

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

	@Override
	public void onResume() {
		super.onResume();
		DialogUtil.showDialogLoading(getActivity());
		loadNoEvaluate();
		loadRecommend();
	}

	private void initData() {
		utils = new Utils();
		verstring = utils.getVersionInfo(getActivity());
		httpUtils = new HttpUtils();

		if (orderAdapter == null) {
			orderAdapter = new OrderAdapter(getActivity());
			mLvOrder.setAdapter(orderAdapter);
			orderAdapter.setFragmentType(3);

		} else {
			orderAdapter.notifyDataSetChanged();
		}
		orderAdapter.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				Log.e("log", "删除后刷新数据");
				DialogUtil.showDialogLoading(getActivity());
				loadNoEvaluate();
			}

			@Override
			public void onAllOrder() {

			}
		});

		if (recommendAdapter == null) {
			recommendAdapter = new RecommendAdapter(getActivity());
			mLvRecommend.setAdapter(recommendAdapter);
		} else {
			recommendAdapter.notifyDataSetChanged();
		}

		// TODO
		mLvRecommend.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						NewCcqProDetailActivity.class);
				
				 String starlat = SharedUtil.getStringValue(SharedCommon.LATITUDE, "");
				 String starlng = SharedUtil.getStringValue(SharedCommon.LONGITUDE, "");
				
				intent.putExtra("starlat", starlat);// 经度
				intent.putExtra("starlng", starlng);// 纬度
				
				intent.putExtra("ccqgoods_id", recommendData.get(position)
						.getGoods_id());// goods_id
				intent.putExtra("store_id", recommendData.get(position)
						.getStore_id());
				intent.putExtra("city", "");// 所在市
				intent.putExtra("district", "");// 所在区
				startActivity(intent);
			}
		});

	}

	/**
	 * 待评价订单
	 * 
	 * @param context
	 * @param i
	 *            评论状态 0未评价，1已评价，2已过期未评价
	 */
	private void loadNoEvaluate() {

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token", new DButil().gettoken(getActivity()));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(getActivity()));
		params.addBodyParameter("order_state", "40");
		params.addBodyParameter("evaluation_state", "0");
		params.addBodyParameter("goods_images_op", "!m");
		params.addBodyParameter("goods_images_size", "220x180");
		params.addBodyParameter("page", "1");
		params.addBodyParameter("page_size", "");

		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWORDERLIST, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						DialogUtil.hideDialogLoading();
						Toast.makeText(getActivity(), "当前网络不可用,请检查网络",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						DialogUtil.hideDialogLoading();
						CommonList<OrderDataBean> order = CommonList.fromJson(
								arg0.result, OrderDataBean.class);

						Log.e("log", "待付款->order: " + order.toString());

						if ("200".equals(order.getCode())) {
							List<OrderDataBean> data = order.getData();

							orderAdapter.NoPayAdapter(data);

						} else {
							Toast.makeText(getActivity(), order.getMessage(),
									Toast.LENGTH_SHORT).show();
						}

					}
				});

	}

	// 推荐美食 修改参数 接口地址
	private void loadRecommend() {
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		/*
		 * params.addBodyParameter("api_v", "v3");
		 * params.addBodyParameter("token", new
		 * DButil().gettoken(getActivity()));
		 * params.addBodyParameter("member_id", new
		 * DButil().getMember_id(getActivity()));
		 * params.addBodyParameter("order_state", "20");// TODO 10
		 * //params.addBodyParameter("evaluation_state", "");
		 * params.addBodyParameter("goods_images_op", "");
		 * params.addBodyParameter("goods_images_size", "");
		 */
		params.addBodyParameter("page", "1");
		params.addBodyParameter("goods_images_op", "!m");
		params.addBodyParameter("goods_images_size", "220x180");
		// params.addBodyParameter("page_size", "");

		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWRECOMMENDLIST, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						DialogUtil.hideDialogLoading();
						Toast.makeText(getActivity(), "当前网络不可用,请检查网络",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						DialogUtil.hideDialogLoading();
						CommonList<RecommendList> order = CommonList.fromJson(
								arg0.result, RecommendList.class);

						Log.e("log", "推荐商品列表: " + order.toString());

						if ("200".equals(order.getCode())) {
							recommendData = order.getData();

							recommendAdapter.NoPayAdapter(recommendData);

						} else {
							Toast.makeText(getActivity(), order.getMessage(),
									Toast.LENGTH_SHORT).show();
						}

					}
				});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_more_recommend:
			// Toast.makeText(getActivity(), "更多推荐", Toast.LENGTH_SHORT).show();

			Intent intent = new Intent(getActivity(),
					RecmmendGoodsActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

}
