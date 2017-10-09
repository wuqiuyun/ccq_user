package ccj.yun28.com.sy;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.sy.fragment.PricebbFragment;
import ccj.yun28.com.sy.fragment.XiaoLiangbbFragment;
import ccj.yun28.com.sy.fragment.ZongHebbFragment;
import ccj.yun28.com.utils.SharedUtil;

/**
 * 搜索商品结果
 * 
 * @author meihuali
 * 
 */
public class SearchProductResultActivity extends BaseActivity implements
		OnClickListener, OnEditorActionListener {

	// 输入的文本
	private String text;
	private TextView tv_price;
	private ImageView iv_price;
	private TextView tv_xiaoliang;
	private TextView tv_zonghe;
	private EditText et_sousuo;

	private String sort = "1";
	// 首页导航系列id
	private String item_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchproductresult);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 搜索
		et_sousuo = (EditText) findViewById(R.id.et_sousuo);
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
		String product_type = SharedUtil.getStringValue(
				SharedCommon.PRODUCTLIST_TYPE, "");
		if ("sy_ss".equals(product_type)) {
			text = getIntent().getStringExtra("text");
			et_sousuo.setText(text);
			setText(text);
		} else if ("cfcy".equals(product_type) || "fzc".equals(product_type)) {
			item_id = getIntent().getStringExtra("gc_id_1");
			setitem_id(item_id);
		} else if ("syfl".equals(product_type)) {
			item_id = getIntent().getStringExtra("gc_id_2");
			setitem_id(item_id);
		}
		et_sousuo.setOnEditorActionListener(this);
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
			showFragment(new ZongHebbFragment());
			break;
		case R.id.line_xiaoliang:
			tv_zonghe.setTextColor(Color.parseColor("#242424"));
			tv_xiaoliang.setTextColor(Color.parseColor("#e4393c"));
			tv_price.setTextColor(Color.parseColor("#242424"));
			showFragment(new XiaoLiangbbFragment());
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
			showFragment(new PricebbFragment());
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

	// 设置存储商品id
	public void setText(String text) {
		this.text = text;
	}

	// 设置获取商品id
	public String getText() {
		return text;
	}

	// 设置存储系列id
	public void setitem_id(String item_id) {
		this.item_id = item_id;
	}

	// 设置获取系列id
	public String getitem_id() {
		return item_id;
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
			String text = et_sousuo.getText().toString().trim();
			if (TextUtils.isEmpty(text)) {
				Toast.makeText(SearchProductResultActivity.this, "搜索文本不能为空",
						Toast.LENGTH_SHORT).show();
			} else {
				setText(text);
				showFragment(new ZongHebbFragment());
				tv_zonghe.setTextColor(Color.parseColor("#e4393c"));
				tv_xiaoliang.setTextColor(Color.parseColor("#242424"));
				tv_price.setTextColor(Color.parseColor("#242424"));
			}

			return true;
		}

		return false;
	}

}
