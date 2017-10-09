package ccj.yun28.com.sy;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.sy.fragment.PriceFragment;
import ccj.yun28.com.sy.fragment.XiaoLiangFragment;
import ccj.yun28.com.sy.fragment.ZongHeFragment;
import ccj.yun28.com.sy.fragment.ZongHebbFragment;

/**
 * 搜索店铺结果
 * 
 * @author meihuali
 * 
 */
public class SearchShopResultActivity extends BaseActivity implements
		OnClickListener {

	// 输入的文本
	private String text;
	private TextView tv_price;
	private ImageView iv_price;
	private TextView tv_xiaoliang;
	private TextView tv_zonghe;

	private String sort = "1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchshopresult);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 搜索
		EditText et_sousuo = (EditText) findViewById(R.id.et_sousuo);
		// 综合
		LinearLayout line_zonghe = (LinearLayout) findViewById(R.id.line_zonghe);
		tv_zonghe = (TextView) findViewById(R.id.tv_zonghe);
		// 销量
		LinearLayout line_xiaoliang = (LinearLayout) findViewById(R.id.line_xiaoliang);
		tv_xiaoliang = (TextView) findViewById(R.id.tv_xiaoliang);
		// 价格
		LinearLayout line_price = (LinearLayout) findViewById(R.id.line_price);
		tv_price = (TextView) findViewById(R.id.tv_price);
		iv_price = (ImageView) findViewById(R.id.iv_price);

		line_back.setOnClickListener(this);
		line_zonghe.setOnClickListener(this);
		line_xiaoliang.setOnClickListener(this);
		line_price.setOnClickListener(this);

		// text = getIntent().getStringExtra("text");
		// setText(text);
		showFragment(new ZongHebbFragment());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_zonghe:
			tv_zonghe.setTextColor(Color.parseColor("#e4393c"));
			tv_xiaoliang.setTextColor(Color.parseColor("#242424"));
			tv_price.setTextColor(Color.parseColor("#242424"));
			showFragment(new ZongHeFragment());
			break;
		case R.id.line_xiaoliang:
			tv_zonghe.setTextColor(Color.parseColor("#242424"));
			tv_xiaoliang.setTextColor(Color.parseColor("#e4393c"));
			tv_price.setTextColor(Color.parseColor("#242424"));
			showFragment(new XiaoLiangFragment());
			break;
		case R.id.line_price:
			tv_zonghe.setTextColor(Color.parseColor("#242424"));
			tv_xiaoliang.setTextColor(Color.parseColor("#242424"));
			tv_price.setTextColor(Color.parseColor("#e4393c"));
			if ("1".equals(sort)) {
				sort = "0";
				iv_price.setBackgroundResource(R.drawable.jiantx);
			} else {
				sort = "1";
				iv_price.setBackgroundResource(R.drawable.jiant);
			}
			showFragment(new PriceFragment());
			break;

		default:
			break;
		}
	}

	private void showFragment(Fragment fragment) {
		FragmentTransaction manager = getFragmentManager().beginTransaction();
		manager.replace(R.id.fragment_product, fragment);
		manager.commit();
	}

	// 设置存储价格顺序
	public void setSort(String sort) {
		this.sort = sort;
	}

	// 设置价格顺序
	public String getSort() {
		return sort;
	}

}
