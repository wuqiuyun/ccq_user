package ccj.yun28.com.mine;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.MainActivity;
import ccj.yun28.com.MainActivity1;
import ccj.yun28.com.R;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.utils.DataCleanManager;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.view.WebviewActivity;

/**
 * 新版我的-设置
 * 
 * @author meihuali
 * 
 */
public class NewSettingActivtiy extends BaseActivity implements OnClickListener {

	private DBHelper myDB;
	private String[] infos;
	private Dialog exitlogindialog;
	private ImageView iv_red;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsetting);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 账户与安全
		LinearLayout line_zhanghu_safe = (LinearLayout) findViewById(R.id.line_zhanghu_safe);
		iv_red = (ImageView) findViewById(R.id.iv_red);
		// 账户关联
		LinearLayout line_zhanghu_guanlian = (LinearLayout) findViewById(R.id.line_zhanghu_guanlian);
		// 帮助
		LinearLayout line_help = (LinearLayout) findViewById(R.id.line_help);
		// 清空缓存
		LinearLayout line_clear_huancun = (LinearLayout) findViewById(R.id.line_clear_huancun);
		// 关于
		LinearLayout line_about = (LinearLayout) findViewById(R.id.line_about);
		// 退出当前账户
		TextView tv_exit_user = (TextView) findViewById(R.id.tv_exit_user);

		line_back.setOnClickListener(this);
		line_zhanghu_safe.setOnClickListener(this);
		line_zhanghu_guanlian.setOnClickListener(this);
		line_clear_huancun.setOnClickListener(this);
		line_help.setOnClickListener(this);
		line_about.setOnClickListener(this);
		tv_exit_user.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_zhanghu_safe:
			intent = new Intent(NewSettingActivtiy.this,
					ZhangHuAndSafeActivtiy.class);
			startActivity(intent);
			break;
		case R.id.line_zhanghu_guanlian:
			intent = new Intent(NewSettingActivtiy.this,
					ZhangHuGuanLianActivtiy.class);
			startActivity(intent);
			break;
		case R.id.line_clear_huancun:

			DataCleanManager.cleanApplicationData(NewSettingActivtiy.this);
			try {
				Thread.sleep(1000);
				Toast.makeText(NewSettingActivtiy.this, "清空缓存成功",
						Toast.LENGTH_SHORT).show();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case R.id.line_help:
			intent = new Intent(NewSettingActivtiy.this, WebviewActivity.class);
			intent.putExtra("title", "帮助");
			intent.putExtra("url",
					SharedUtil.getStringValue(SharedCommon.MALL_HELP, ""));
			startActivity(intent);
			break;
		case R.id.line_about:
			intent = new Intent(NewSettingActivtiy.this, AboutWeActivtiy.class);
			startActivity(intent);
			break;
		case R.id.tv_exit_user:
			showExitDialog();
			break;
		case R.id.tv_dialog_cancel:
			exitlogindialog.dismiss();
			break;
		case R.id.tv_ok:
			exitlogindialog.dismiss();
			exitZhangHao();
			break;

		default:
			break;
		}
	}

	// 退出登录dialog
	private void showExitDialog() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(NewSettingActivtiy.this).inflate(
				R.layout.dialog_querenshouhuo, null);
		TextView tv_url = (TextView) view.findViewById(R.id.tv_url);
		TextView tv_dialog_cancel = (TextView) view
				.findViewById(R.id.tv_dialog_cancel);
		TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
		tv_url.setText("是否确认退出登录");
		tv_dialog_cancel.setOnClickListener(this);
		tv_ok.setOnClickListener(this);
		exitlogindialog = new Dialog(NewSettingActivtiy.this,
				R.style.mDialogStyle);
		exitlogindialog.setContentView(view);
		exitlogindialog.setCanceledOnTouchOutside(false);
		exitlogindialog.show();
	}

	// 获取用户信息
	private String[] getUserInfo() {
		String info[] = new String[2];
		SQLiteDatabase db = myDB.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from user where status=1", null);
		if (cursor.moveToFirst()) {
			info[0] = cursor
					.getString(cursor.getColumnIndex("member_truename"));// 昵称
			info[1] = cursor.getString(cursor.getColumnIndex("member_name"));// 用户名
		}
		return info;
	}

	// 退出当前账号
	private void exitZhangHao() {
		SharedUtil.saveStringValue(SharedCommon.DBDH, "me");
		SQLiteDatabase db = myDB.getWritableDatabase();
		db.execSQL("update user set status=? where member_name=?",
				new Object[] { 0, infos[1] });
		db.close();
		Intent intent1 = new Intent(NewSettingActivtiy.this,
				MainActivity.class);
		intent1.putExtra("type", "me");
		startActivity(intent1);
		finish();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		myDB = new DBHelper(this);
		infos = getUserInfo();

		String set_red_dot = SharedUtil.getStringValue(
				SharedCommon.IS_LOGINMIMA_MEMBERNAME, "0");
		if ("0".equals(set_red_dot)) {
			iv_red.setVisibility(View.GONE);
		} else {
			iv_red.setVisibility(View.VISIBLE);
		}
	}

}
