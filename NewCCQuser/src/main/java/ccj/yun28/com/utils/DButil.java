package ccj.yun28.com.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ccj.yun28.com.db.DBHelper;

public class DButil {
	/**
	 * 获取uid
	 * 
	 * @param context
	 * @return
	 */
	public String getMember_id(Context context) {
		DBHelper myDB = new DBHelper(context);
		String member_id = "";
		SQLiteDatabase db = myDB.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from user where status=1", null);
		if (cursor.moveToFirst()) {
			member_id = cursor.getString(cursor.getColumnIndex("member_id"));
		}
		cursor.close();
		db.close();
		return member_id;
	}
	/**
	 * 获取用户名
	 * 
	 * @param context
	 * @return
	 */
	public String getMember_name(Context context) {
		DBHelper myDB = new DBHelper(context);
		String member_name = "";
		SQLiteDatabase db = myDB.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from user where status=1", null);
		if (cursor.moveToFirst()) {
			member_name = cursor.getString(cursor.getColumnIndex("member_name"));
		}
		cursor.close();
		db.close();
		return member_name;
	}

	/**
	 * 获取token
	 * 
	 * @param context
	 * @return
	 */
	public String gettoken(Context context) {
		DBHelper myDB = new DBHelper(context);
		String token = "";
		SQLiteDatabase db = myDB.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from user where status=1", null);
		if (cursor.moveToFirst()) {
			token = cursor.getString(cursor.getColumnIndex("token"));
		}
		cursor.close();
		db.close();
		return token;
	}

	/**
	 * 获取is_vip
	 * 
	 * @param context
	 * @return
	 */
	public String getis_vip(Context context) {
		DBHelper myDB = new DBHelper(context);
		String is_vip = "";
		SQLiteDatabase db = myDB.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from user where status=1", null);
		if (cursor.moveToFirst()) {
			is_vip = cursor.getString(cursor.getColumnIndex("is_vip"));
		}
		cursor.close();
		db.close();
		return is_vip;
	}
}
