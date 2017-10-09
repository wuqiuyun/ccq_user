package ccj.yun28.com.neworder.myorder;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import ccj.yun28.com.R;
import ccj.yun28.com.bean.newbean.OrderDataBean;
import ccj.yun28.com.json.CommonList;
import ccj.yun28.com.neworder.OrderAdapter;
import ccj.yun28.com.neworder.OrderAdapter.OnRefreshListener;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.DialogUtil;
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
 * 待评价
 */
public class MyNoEvaluateFragment extends Fragment   {

	private View view;
	private ListView mLvOrder;
	private MaterialRefreshLayout refreshLayout;
	private OrderAdapter orderAdapter;// 订单

	private int page = 1;
	private Utils utils;
	private String[] verstring;
	private HttpUtils httpUtils;
	private List<OrderDataBean> data;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_my_order, null);
		mLvOrder = (ListView) view.findViewById(R.id.lv_order);
		refreshLayout = (MaterialRefreshLayout) view.findViewById(R.id.refresh_layout);
		refreshLayout.setLoadMore(true);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
		initListView();
		loadNoEvaluate();
	}
	
	private void initListView() {
		refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {

			@Override
			public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
				page =1;
				loadNoEvaluate();
			}

			@Override
			public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
				++page;
				loadNoEvaluate();
			}
		});
	}

	private void initData() {
		utils = new Utils();
		verstring = utils.getVersionInfo(getActivity());
		httpUtils = new HttpUtils();
		if (data == null) {
			data = new ArrayList<OrderDataBean>();
		}

		if (orderAdapter == null) {
			orderAdapter = new OrderAdapter(getActivity());
			mLvOrder.setAdapter(orderAdapter);
		} else {
			orderAdapter.notifyDataSetChanged();
		}
		orderAdapter.setType(4);

		orderAdapter.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				Log.e("log", "删除后刷新数据");
				loadNoEvaluate();
			}

			@Override
			public void onAllOrder() {
				Log.e("log", "查看全部");

			}
		});
	}

	/**
	 * 加载待付款订单
	 * 
	 * params: order_state
	 *            订单状态 0(已取消); 10(默认):待付款; 20:已付款; 30:待收货; 40:已完成;
	 */
	private void loadNoEvaluate() {
		DialogUtil.showDialogLoading(getActivity());
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
		params.addBodyParameter("page", page+"");
		params.addBodyParameter("page_size", "");

		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWORDERLIST, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						DialogUtil.hideDialogLoading();
						Toast.makeText(getActivity(), "获取数据失败",
								Toast.LENGTH_SHORT).show();
					}

					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						DialogUtil.hideDialogLoading();
						CommonList<OrderDataBean> order = CommonList.fromJson(
								arg0.result, OrderDataBean.class);
						Log.e("log", "待评价->order: " + order.toString());
						if ("200".equals(order.getCode())) {
							if (page == 1) {
								data.clear();
							}
							data.addAll(order.getData());
							orderAdapter.NoPayAdapter(data);
						} else {
							Toast.makeText(getActivity(), order.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
						refreshLayout.finishRefresh();
						refreshLayout.finishRefreshLoadMore();
					}
				});
	}

}
