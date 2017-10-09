package ccj.yun28.com.mine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import ccj.yun28.com.R;
import ccj.yun28.com.mine.fragment.AllDinDanFragment;
import ccj.yun28.com.mine.fragment.DfkDinDanFragment;
import ccj.yun28.com.mine.fragment.DpjDinDanFragment;
import ccj.yun28.com.mine.fragment.DshDinDanFragment;
import ccj.yun28.com.mine.fragment.DsyDinDanFragment;

/**
 * 全部订单页
 * 
 * @author meihuali
 * 
 */
public class AllDinDanActivtiy extends FragmentActivity implements
		OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alldindan);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 全部订单
		RadioButton radio_all = (RadioButton) findViewById(R.id.radio_all);
		// 待付款
		RadioButton radio_dfk = (RadioButton) findViewById(R.id.radio_dfk);
		// 待收货
		RadioButton radio_dsh = (RadioButton) findViewById(R.id.radio_dsh);
		// 待使用
		RadioButton radio_dsy = (RadioButton) findViewById(R.id.radio_dsy);
		// 待评价
		RadioButton radio_dpj = (RadioButton) findViewById(R.id.radio_dpj);

		line_back.setOnClickListener(this);
		radio_all.setOnClickListener(this);
		radio_dfk.setOnClickListener(this);
		radio_dsh.setOnClickListener(this);
		radio_dsy.setOnClickListener(this);
		radio_dpj.setOnClickListener(this);

		if (getIntent() != null) {
			String type = getIntent().getStringExtra("type");
			if ("dsy".equals(type)) {
				radio_dsy.setChecked(true);
				showFragment(new DsyDinDanFragment());
			}else {
				showFragment(new AllDinDanFragment());
			}
		}else {
			showFragment(new AllDinDanFragment());
		}
	}

	// fragment切换
	private void showFragment(Fragment fragment) {
		FragmentTransaction manager = getSupportFragmentManager()
				.beginTransaction();
		manager.replace(R.id.DetailLayout, fragment);
		// manager.commit();
		manager.commitAllowingStateLoss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.radio_all:
			showFragment(new AllDinDanFragment());
			break;
		case R.id.radio_dfk:
			showFragment(new DfkDinDanFragment());
			break;
		case R.id.radio_dsh:
			showFragment(new DshDinDanFragment());
			break;
		case R.id.radio_dsy:
			showFragment(new DsyDinDanFragment());
			break;
		case R.id.radio_dpj:
			showFragment(new DpjDinDanFragment());
			break;

		default:
			break;
		}
	}
}
