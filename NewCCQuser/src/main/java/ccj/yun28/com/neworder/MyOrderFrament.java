package ccj.yun28.com.neworder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import ccj.yun28.com.R;
import ccj.yun28.com.mine.fragment.AllDinDanFragment;
import ccj.yun28.com.mine.fragment.DsyDinDanFragment;
import ccj.yun28.com.neworder.myorder.MyAllOrderFragment;
import ccj.yun28.com.neworder.myorder.MyNoEvaluateFragment;
import ccj.yun28.com.neworder.myorder.MyNoPayFragment;
import ccj.yun28.com.neworder.myorder.MyNoUseFragment;
import ccj.yun28.com.utils.Utils;

import com.amap.api.col.ft;
import com.lidroid.xutils.HttpUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 查看全部订单
 */
public class MyOrderFrament extends FragmentActivity implements OnClickListener {

	private Fragment fragment;
	private LinearLayout line_back;
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
	//
	private int fType;// TODO

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_newmy_order);
		initView();
		setListener();
		getIntentData();


		initFragment();
	}

	private void getIntentData() {
		Intent intent = getIntent();
		fType = intent.getIntExtra("fType", 1);

	}

	private void initView() {
		line_back = (LinearLayout) findViewById(R.id.line_back);
		ll_no_pay = (LinearLayout) findViewById(R.id.ll_no_pay);
		iv_no_pay = (ImageView) findViewById(R.id.iv_no_pay);
		ll_no_user = (LinearLayout) findViewById(R.id.ll_no_use);
		iv_no_use = (ImageView) findViewById(R.id.iv_no_use);
		ll_no_evaluate = (LinearLayout) findViewById(R.id.ll_no_evaluate);
		iv_no_evaluate = (ImageView) findViewById(R.id.iv_no_evaluate);
		ll_all_order = (LinearLayout) findViewById(R.id.ll_all_order);
		iv_all_order = (ImageView) findViewById(R.id.iv_all_order);
	}

	private void setListener() {
		line_back.setOnClickListener(this);
		ll_no_pay.setOnClickListener(this);
		ll_no_user.setOnClickListener(this);
		ll_no_evaluate.setOnClickListener(this);
		ll_all_order.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.line_back:
			finish();
			break;
		case R.id.ll_no_pay:// 待付款
			resetTabView(1);
			fragment = new MyNoPayFragment();
			showFragment(fragment);
			break;
		case R.id.ll_no_use:// 待使用
			resetTabView(2);
			fragment = new MyNoUseFragment();
			showFragment(fragment);
			break;
		case R.id.ll_no_evaluate:// 待评价
			resetTabView(3);
			fragment = new MyNoEvaluateFragment();
			showFragment(fragment);
			break;
		case R.id.ll_all_order:// 查看全部
			resetTabView(4);
			fragment = new MyAllOrderFragment();
			showFragment(fragment);
			break;
		default:
			break;
		}
	}

	// 默认显示的待付款界面
	private void initFragment() {
		resetTabView(fType); // TODO
		showFragment(new MyNoPayFragment());
	}

	// fragment切换
	private void showFragment(Fragment fragment) {
		FragmentTransaction manager = getSupportFragmentManager()
				.beginTransaction();
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

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
