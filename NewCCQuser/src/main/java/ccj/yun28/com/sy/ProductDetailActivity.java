package ccj.yun28.com.sy;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.MainActivity1;
import ccj.yun28.com.R;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.mine.ProblemActivity;
import ccj.yun28.com.sy.fragment.DetailFragment;
import ccj.yun28.com.sy.fragment.PinjiaFragment;
import ccj.yun28.com.sy.fragment.ProductFragment;
import ccj.yun28.com.utils.SharedUtil;

/**
 * 商品详情页
 * 
 * @author meihuali
 * 
 */
public class ProductDetailActivity extends FragmentActivity implements
		OnClickListener {

	// 商品id
	private String goods_id;
	// 更多图标
	private ImageView iv_more;
	// 弹窗
	private PopupWindow popupWindow;

	public PinjiaFragment pinjiaFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_productdetail);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 购物车
		LinearLayout line_gwc = (LinearLayout) findViewById(R.id.line_gwc);
		LinearLayout line_more = (LinearLayout) findViewById(R.id.line_more);
		iv_more = (ImageView) findViewById(R.id.iv_more);
		// 商品
		RadioButton radio_sp = (RadioButton) findViewById(R.id.radio_sp);
		// 详情
		RadioButton radio_xq = (RadioButton) findViewById(R.id.radio_xq);
		// 评价
		RadioButton radio_pj = (RadioButton) findViewById(R.id.radio_pj);

		myDB = new DBHelper(this);
		
		line_back.setOnClickListener(this);
		line_gwc.setOnClickListener(this);
		line_more.setOnClickListener(this);
		radio_sp.setOnClickListener(this);
		radio_xq.setOnClickListener(this);
		radio_pj.setOnClickListener(this);
		if (getIntent() != null) {
			
			goods_id = getIntent().getStringExtra("goods_id");
			setGoods_id(goods_id);
		}
		
		String pro_pingjia = SharedUtil.getStringValue(
				SharedCommon.PRODANPINJIA, "");
		if ("pro_pinjia".equals(pro_pingjia)) {
			showFragment(new PinjiaFragment());
			SharedUtil.deleteValue(SharedCommon.PRODANPINJIA);
		} else {
//			showFragment(new SYFragment());
			showFragment(new ProductFragment());
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
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_gwc:
			if (isLogin()) {
				intent = new Intent(ProductDetailActivity.this, MainActivity1.class);
				SharedUtil.saveStringValue(SharedCommon.DBDH, "gwc");
				intent.putExtra("type", "gwc");
				startActivity(intent);
				finish();
			}else{
				intent = new Intent(ProductDetailActivity.this, LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
			}
			break;
		case R.id.line_more:
			View view = View.inflate(ProductDetailActivity.this,
					R.layout.popupwindow_message, null);

			TextView tv_home = (TextView) view.findViewById(R.id.tv_home);
			TextView tv_search = (TextView) view.findViewById(R.id.tv_search);
			TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
			TextView tv_problem = (TextView) view.findViewById(R.id.tv_problem);

			tv_home.setOnClickListener(this);
			tv_search.setOnClickListener(this);
			tv_message.setOnClickListener(this);
			tv_problem.setOnClickListener(this);

			popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, true);
			popupWindow.setTouchable(true);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setFocusable(true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.showAsDropDown(iv_more);
			break;
		case R.id.radio_sp:
			showFragment(new ProductFragment());
			break;
		case R.id.radio_xq:
			showFragment(new DetailFragment());
			break;
		case R.id.radio_pj:
			showFragment(new PinjiaFragment());
			break;
		case R.id.tv_home:
			popupWindow.dismiss();
			intent = new Intent(ProductDetailActivity.this, MainActivity1.class);
			SharedUtil.saveStringValue(SharedCommon.DBDH, "ccq");
			intent.putExtra("type", "ccq");
			startActivity(intent);
			finish();
			break;
		case R.id.tv_search:
			popupWindow.dismiss();
			intent = new Intent(ProductDetailActivity.this,
					SearchActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.tv_message:
			popupWindow.dismiss();
			intent = new Intent(ProductDetailActivity.this,
					MessageActivcity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.tv_problem:
			popupWindow.dismiss();
			if (isLogin()) {
				intent = new Intent(ProductDetailActivity.this,
						ProblemActivity.class);
				startActivity(intent);
				finish();
			} else {
				intent = new Intent(ProductDetailActivity.this, LoginActivity.class);
				startActivity(intent);
			}
			break;

		default:
			break;
		}
	}

	// 设置存储商品id
	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	// 设置获取商品id
	public String getGoods_id() {
		return goods_id;
	}

	private DBHelper myDB;

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
			// TODO: handle exception
		}

		return true;
	}

}
