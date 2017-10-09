package ccj.yun28.com.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 用户基本信息（数据库存储）
 * 
 * @author meihuali
 * 
 */
public class DBHelper extends SQLiteOpenHelper {

	private final static String name = "ccquser.db";
	private final static int version = 1;

	public DBHelper(Context context) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		DBControl(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		DBControl(db);
	}

	private void DBControl(SQLiteDatabase db) {
		/**
		 * member_id 用户ID member_name用户名（手机号） member_truename昵称 regip注册IP
		 * regdate注册时间 inviteid邀请码 reg_id邀请人ID alipay_acc支付宝账号 wx_id微信openid
		 * exrract_pwd提现密码 status状态码(1为登录,0为未登录) is_vip 是否vip 1.是，0.否 唯一token
		 **/
		db.execSQL("create table if not exists user(member_id integer primary key,"
				+ "member_name text,member_truename text,regip text,"
				+ "regdate text,inviteid text,reg_id text,alipay_acc text,"
				+ "wx_id text,exrract_pwd text,status integer,is_vip text,"
				+ "token text)");
	}
}
