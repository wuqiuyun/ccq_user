package ccj.yun28.com.sy;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.MainActivity1;
import ccj.yun28.com.R;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.mine.ProblemActivity;
import ccj.yun28.com.utils.SharedUtil;

/**
 * 消息页面
 * 
 * @author meihuali
 * 
 */
public class MessageActivcity extends BaseActivity implements OnClickListener {

	// 更多图标
	private ImageView iv_more;
	// 弹窗
	private PopupWindow popupWindow;

	private DBHelper myDB;
	private String type;
	private String latitude;
	private String longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);

		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_more = (LinearLayout) findViewById(R.id.line_more);
		iv_more = (ImageView) findViewById(R.id.iv_more);
		TextView tv_wl = (TextView) findViewById(R.id.tv_wl);
		TextView tv_wl_time = (TextView) findViewById(R.id.tv_wl_time);
		LinearLayout line_wl = (LinearLayout) findViewById(R.id.line_wl);
		TextView tv_tz = (TextView) findViewById(R.id.tv_tz);
		TextView tv_tz_time = (TextView) findViewById(R.id.tv_tz_time);
		LinearLayout line_tz = (LinearLayout) findViewById(R.id.line_tz);
		TextView tv_hd = (TextView) findViewById(R.id.tv_hd);
		TextView tv_hd_time = (TextView) findViewById(R.id.tv_hd_time);
		LinearLayout line_hd = (LinearLayout) findViewById(R.id.line_hd);

		if (getIntent() != null) {
			type = getIntent().getStringExtra("type");
			if (TextUtils.isEmpty(type)) {
				type = "宝贝";
			} else if ("餐餐抢".equals(type)) {
				latitude = getIntent().getStringExtra("lat1");
				longitude = getIntent().getStringExtra("lng1");
			}
		}

		myDB = new DBHelper(this);

		line_back.setOnClickListener(this);
		line_more.setOnClickListener(this);
		line_wl.setOnClickListener(this);
		line_tz.setOnClickListener(this);
		line_hd.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_more:
			View view = View.inflate(MessageActivcity.this,
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
			popupWindow.setOutsideTouchable(false);
			popupWindow.setFocusable(true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.showAsDropDown(iv_more);
			break;
		case R.id.line_wl:
			if (isLogin()) {
				intent = new Intent(MessageActivcity.this, WuLiuActivity.class);
				startActivity(intent);
			} else {
				intent = new Intent(MessageActivcity.this, LoginActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.line_tz:
			if (isLogin()) {
				intent = new Intent(MessageActivcity.this,
						TongZhiActivity.class);
				startActivity(intent);
			} else {
				intent = new Intent(MessageActivcity.this, LoginActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.line_hd:
			if (isLogin()) {
				intent = new Intent(MessageActivcity.this,
						HuodongActivity.class);
				startActivity(intent);
			} else {
				intent = new Intent(MessageActivcity.this, LoginActivity.class);
				startActivity(intent);
			}
			break;
		// 首页
		case R.id.tv_home:
			popupWindow.dismiss();
			intent = new Intent(MessageActivcity.this, MainActivity1.class);
			SharedUtil.saveStringValue(SharedCommon.DBDH, "ccq");
			startActivity(intent);
			finish();
			break;
		// 搜索
		case R.id.tv_search:
			popupWindow.dismiss();
			intent = new Intent(MessageActivcity.this, SearchActivity.class);
			intent.putExtra("type", type);
			intent.putExtra("lat1", latitude);
			intent.putExtra("lng1", longitude);
			startActivity(intent);
			finish();
			break;
		// 消息
		case R.id.tv_message:
			popupWindow.dismiss();
			intent = new Intent(MessageActivcity.this, MessageActivcity.class);
			startActivity(intent);
			finish();
			break;
		// 问题反馈
		case R.id.tv_problem:
			popupWindow.dismiss();
			if (isLogin()) {
				intent = new Intent(MessageActivcity.this,
						ProblemActivity.class);
				startActivity(intent);
				finish();
			} else {
				intent = new Intent(MessageActivcity.this, LoginActivity.class);
				startActivity(intent);
			}
			break;

		default:
			break;
		}
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
			// TODO: handle exception
		}

		return true;
	}

}
