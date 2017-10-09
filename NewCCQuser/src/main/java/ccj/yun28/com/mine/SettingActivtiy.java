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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.MainActivity1;
import ccj.yun28.com.R;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.utils.DataCleanManager;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.view.WebviewActivity;

/**
 * 我的-设置
 * 
 * @author meihuali
 * 
 */
public class SettingActivtiy extends BaseActivity implements OnClickListener {

	private DBHelper myDB;
	private String[] infos;
	private Dialog exitlogindialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 个人资料
		LinearLayout line_myinfo = (LinearLayout) findViewById(R.id.line_myinfo);
		// 账户与安全
		LinearLayout line_zhanghu_safe = (LinearLayout) findViewById(R.id.line_zhanghu_safe);
		// 清空缓存
		LinearLayout line_clear_huancun = (LinearLayout) findViewById(R.id.line_clear_huancun);
		// 帮助
		LinearLayout line_help = (LinearLayout) findViewById(R.id.line_help);
		// 给我评分
		LinearLayout line_pingfen = (LinearLayout) findViewById(R.id.line_pingfen);
		// 关于
		LinearLayout line_about = (LinearLayout) findViewById(R.id.line_about);
		// 退出当前账户
		TextView tv_exit_user = (TextView) findViewById(R.id.tv_exit_user);

		myDB = new DBHelper(this);
		infos = getUserInfo();

		line_back.setOnClickListener(this);
		line_myinfo.setOnClickListener(this);
		line_zhanghu_safe.setOnClickListener(this);
		line_clear_huancun.setOnClickListener(this);
		line_help.setOnClickListener(this);
		line_pingfen.setOnClickListener(this);
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
		case R.id.line_myinfo:
			intent = new Intent(SettingActivtiy.this, SettingInfoActivtiy.class);
			startActivity(intent);
			break;
		case R.id.line_zhanghu_safe:
			intent = new Intent(SettingActivtiy.this, ZhangHuAndSafeActivtiy.class);
			startActivity(intent);
			break;
		case R.id.line_clear_huancun:
			
            DataCleanManager.cleanApplicationData(SettingActivtiy.this);
            try {
                Thread.sleep(1000);
                Toast.makeText(SettingActivtiy.this, "清空缓存成功", Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			break;
		case R.id.line_help:
			intent = new Intent(SettingActivtiy.this, WebviewActivity.class);
			intent.putExtra("title", "帮助");
			intent.putExtra("url", SharedUtil.getStringValue(SharedCommon.MALL_HELP, ""));
			startActivity(intent);
			break;
		case R.id.line_pingfen:
			break;
		case R.id.line_about:
			intent = new Intent(SettingActivtiy.this, AboutWeActivtiy.class);
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

	//退出登录dialog
	private void showExitDialog() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(SettingActivtiy.this).inflate(
				R.layout.dialog_querenshouhuo, null);
		TextView tv_url = (TextView) view.findViewById(R.id.tv_url);
		TextView tv_dialog_cancel = (TextView) view
				.findViewById(R.id.tv_dialog_cancel);
		TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
		tv_url.setText("是否确认退出登录");
		tv_dialog_cancel.setOnClickListener(this);
		tv_ok.setOnClickListener(this);
		exitlogindialog = new Dialog(SettingActivtiy.this,
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
		Intent intent1 = new Intent(SettingActivtiy.this, MainActivity1.class);
		intent1.putExtra("type", "me");
		startActivity(intent1);
		finish();
	}
	
}
