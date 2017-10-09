package ccj.yun28.com.utils;

import java.security.MessageDigest;

import ccj.yun28.com.db.DBHelper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.DisplayMetrics;

/**
 * 工具类
 * 
 * @author meihuali
 * 
 */
public class Utils {

	public static final int PREFS_MODE = Context.MODE_PRIVATE;

	public static final String PREFS_FILE_GPSANDJPC = "cancanq.prefs";
	/**
	 * 商家在微信平台申请的该应用的id
	 */
	public static final String WX_APP_ID = "wx3b629afd914b81d9";
	public static final String WX_APP_SECRET = "e5d17f8ac7b3bd8cbb66fd88a3dad481";
	/**
	 * 支付结果的Action
	 */
	public static final String pay_result_receiver_action = "com.update";

	/**
	 * intent里键值对的键
	 */
	public static final String pay_result_receiver_info = "update";
	/**
	 * 支付宝支付
	 */
	public static final String IS_ALIPAY = "alipay";
	/**
	 * 微信支付
	 */
	public static final String IS_WXPAY = "wxpay";

	public static final int DBVERSION=3;
	
	//1强制升级 0不强制升级
	public static String forceUpdateString="0";
	
	/** Md5加密 **/
	public static String md5(String str) {
		byte[] hash = null;
		try {
			hash = MessageDigest.getInstance("MD5").digest(
					str.getBytes("utf-8"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		StringBuilder sb = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				sb.append("0");
			sb.append(Integer.toHexString(b & 0xFF));
		}
		return sb.toString();
	}
	
	 /** 
     * 获取当前手机系统版本号 
     * 
     * @return  系统版本号 
     */  
    public static String getSystemVersion() {  
        return android.os.Build.VERSION.RELEASE;  
    }

	/**
	 * 获取当前版本信息
	 */

    public String[] getVersionInfo(Context context) {
		String verString[] = new String[3];
		verString[0] = context.getPackageName();
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(verString[0], 0);
			verString[1] = info.versionCode + "";
			verString[2] = info.versionName + "";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return verString;
	}
    
    /** 获取当前手机屏幕信息 **/
    public int[] getWindowInfo(Activity context) {
		 int info[] = new int[2];
		try {
			DisplayMetrics dm = new DisplayMetrics();
			// 获取屏幕信息
			context.getWindowManager().getDefaultDisplay().getMetrics(dm);

			int screenWidth = dm.widthPixels;

			int screenHeigh = dm.heightPixels;
			info[0] = screenWidth;
			info[1] = screenHeigh;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
	
    
 // 校验登录与否
    public boolean getisLogin(Context context) {
 		try {
 			DBHelper myDB = new DBHelper(context);
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
