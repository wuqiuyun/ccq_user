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
import ccj.yun28.com.bean.newbean.OrderDataBean;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.json.CommonList;
import ccj.yun28.com.mine.AllDinDanActivtiy;
import ccj.yun28.com.neworder.OrderAdapter.OnRefreshListener;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.DialogUtil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

/**
 * 查看全部订单
 *  @author wuqiuyun
 */

public class AllOrderFragment extends Fragment {
	private View view;

	private ListView mLvOrder;
	private OrderAdapter orderAdapter;// 订单

	private LinearLayout ll_no_login;// 新添加
	private Button btn_login;// 新添加
//	private ScrollView scrollView;// 新添加

	private Utils utils;
	private String[] verstring;
	private HttpUtils httpUtils;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_all_order, null);
		/*
		 * R.layout.fragment_all_order, null);
		 */
		mLvOrder = (ListView) view.findViewById(R.id.lv_order);
		/*
		 * Intent intent = new Intent(getActivity(), AllDinDanActivtiy.class);
		 * startActivity(intent);
		 */

		ll_no_login = (LinearLayout) view.findViewById(R.id.ll_no_login);
		btn_login = (Button) view.findViewById(R.id.btn_login);
//		scrollView = (ScrollView) view.findViewById(R.id.scrollView);

		if (!isLogin()) {
			ll_no_login.setVisibility(View.VISIBLE);
			mLvOrder.setVisibility(View.GONE);
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
			mLvOrder.setVisibility(View.VISIBLE);
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
		// TODO Auto-generated method stub
		super.onResume();
		DialogUtil.showDialogLoading(getActivity());
		loadAllOrder();
	}

	private void initData() {
		utils = new Utils();
		verstring = utils.getVersionInfo(getActivity());
		httpUtils = new HttpUtils();

		if (orderAdapter == null) {
			orderAdapter = new OrderAdapter(getActivity());
			mLvOrder.setAdapter(orderAdapter);
		} else {
			orderAdapter.notifyDataSetChanged();
		}
		orderAdapter.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				Log.e("log", "删除后刷新数据");
				DialogUtil.showDialogLoading(getActivity());
				loadAllOrder();
			}

			@Override
			public void onAllOrder() {

			}
		});

		orderAdapter.setType(4);

	}

	/**
	 * 待评价订单
	 * 
	 * params: order_state
	 *            评论状态 0未评价，1已评价，2已过期未评价
	 */
	private void loadAllOrder() {

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token", new DButil().gettoken(getActivity()));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(getActivity()));
		params.addBodyParameter("order_state", "");
		params.addBodyParameter("evaluation_state", "");
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

						Log.e("log", "查看全部->order: " + order.toString());

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
}
