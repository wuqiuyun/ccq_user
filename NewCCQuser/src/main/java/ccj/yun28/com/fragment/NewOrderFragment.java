package ccj.yun28.com.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import ccj.yun28.com.R;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.mine.AllDinDanActivtiy;
import ccj.yun28.com.neworder.MyOrderFrament;
import ccj.yun28.com.neworder.NoEvaluateFragment;
import ccj.yun28.com.neworder.NoPayFragment;
import ccj.yun28.com.neworder.NoUseFragment;
import ccj.yun28.com.neworder.OrderAdapter;
import ccj.yun28.com.utils.Utils;

import com.lidroid.xutils.HttpUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 新版订单页面--黑色版
 * 
 * @author wuqiuyun
 * 
 */
public class NewOrderFragment extends Fragment implements OnClickListener {
	private OrderAdapter orderAdapter = new OrderAdapter(getActivity());
	private View view;
	private Utils utils;
	private String[] verstring;
	private HttpUtils httpUtils;
	private int page = 1;

	// 待付款
	private LinearLayout ll_no_pay;
	private ImageView iv_no_pay;
	// 待使用
	private LinearLayout ll_no_user;
	private ImageView iv_no_use;
	// 待评价
	private LinearLayout ll_no_evaluate;
	private ImageView iv_no_evaluate;
	// 查看全部
	private LinearLayout ll_all_order;
	private ImageView iv_all_order;

	private Fragment fragment;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_new_order, null);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initView();
		setListener();
		initFragment();

	}

	private void initView() {
		ll_no_pay = (LinearLayout) view.findViewById(R.id.ll_no_pay);
		iv_no_pay = (ImageView) view.findViewById(R.id.iv_no_pay);
		ll_no_user = (LinearLayout) view.findViewById(R.id.ll_no_use);
		iv_no_use = (ImageView) view.findViewById(R.id.iv_no_use);
		ll_no_evaluate = (LinearLayout) view.findViewById(R.id.ll_no_evaluate);
		iv_no_evaluate = (ImageView) view.findViewById(R.id.iv_no_evaluate);
		ll_all_order = (LinearLayout) view.findViewById(R.id.ll_all_order);
		iv_all_order = (ImageView) view.findViewById(R.id.iv_all_order);
	}

	private void setListener() {
		ll_no_pay.setOnClickListener(this);
		ll_no_user.setOnClickListener(this);
		ll_no_evaluate.setOnClickListener(this);
		ll_all_order.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_no_pay:// 待付款 TODO
			resetTabView(1);
			fragment = new NoPayFragment();
			showFragment(fragment);
			break;
		case R.id.ll_no_use:// 待使用
			resetTabView(2);
			fragment = new NoUseFragment();
			showFragment(fragment);
			break;
		case R.id.ll_no_evaluate:// 待评价
			resetTabView(3);
			fragment = new NoEvaluateFragment();
			showFragment(fragment);
			break;
		case R.id.ll_all_order:// 查看全部
			/*
			 * resetTabView(4); showFragment(new AllOrderFragment());
			 */

			// Intent intent = new Intent(getActivity(),
			// AllDinDanActivtiy.class);
			Intent intent = new Intent(getActivity(), MyOrderFrament.class);
			 intent.putExtra("fType", 4);
			startActivity(intent);

			break;
		default:
			break;
		}
	}

	// 默认显示的待付款界面
	private void initFragment() {
		resetTabView(1);
		showFragment(new NoPayFragment());
	}

	// fragment切换
	private void showFragment(Fragment fragment) {
		FragmentTransaction manager = getFragmentManager().beginTransaction();
		manager.replace(R.id.order_frame, fragment);
		manager.commitAllowingStateLoss();
	}

	// 重置顶部tab下划线
	private void resetTabView(int type) {
		switch (type) {
		case 1:
			iv_no_pay.setVisibility(View.VISIBLE);
			iv_no_use.setVisibility(View.GONE);
			iv_no_evaluate.setVisibility(View.GONE);
			iv_all_order.setVisibility(View.GONE);
			break;
		case 2:
			iv_no_pay.setVisibility(View.GONE);
			iv_no_use.setVisibility(View.VISIBLE);
			iv_no_evaluate.setVisibility(View.GONE);
			iv_all_order.setVisibility(View.GONE);
			break;
		case 3:
			iv_no_pay.setVisibility(View.GONE);
			iv_no_use.setVisibility(View.GONE);
			iv_no_evaluate.setVisibility(View.VISIBLE);
			iv_all_order.setVisibility(View.GONE);
			break;
		case 4:
			iv_no_pay.setVisibility(View.GONE);
			iv_no_use.setVisibility(View.GONE);
			iv_no_evaluate.setVisibility(View.GONE);
			iv_all_order.setVisibility(View.VISIBLE);
			break;
		}
	}

	// 加载中动画
	private Dialog loadingDialog;
	private DBHelper myDB;

	// 关闭加载dialog
	private void dissDialog() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			if (isLogin()) {
				// loadAllOrder(getActivity());
			} else {
				// tv_nicheng.setText("未登录");TODO
			}
		} catch (Exception e) {
		}

		MobclickAgent.onResume(getActivity());

	}

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
		}

		return true;
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(getActivity());
	}

}
