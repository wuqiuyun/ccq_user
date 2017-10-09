package ccj.yun28.com.utils;

import android.content.SharedPreferences;
import ccj.yun28.com.common.CanCanQApplication;

/**
 * sharedprefre工具类
 * 
 * @author Administrator
 * 
 */
public class SharedUtil {

	public static String getStringValue(String key, String defValue) {
		SharedPreferences mPrefs = CanCanQApplication.mContext
				.getSharedPreferences(Utils.PREFS_FILE_GPSANDJPC,
						Utils.PREFS_MODE);

		return mPrefs.getString(key, (String) defValue);

	}

	public static void saveStringValue(String key, String value) {
		SharedPreferences mPrefs = CanCanQApplication.mContext
				.getSharedPreferences(Utils.PREFS_FILE_GPSANDJPC,
						Utils.PREFS_MODE);
		SharedPreferences.Editor editor = mPrefs.edit();

		editor.putString(key, (String) value);

		editor.commit();
	}

	public static int getIntValue(String key, int defValue) {
		SharedPreferences mPrefs = CanCanQApplication.mContext
				.getSharedPreferences(Utils.PREFS_FILE_GPSANDJPC,
						Utils.PREFS_MODE);

		return mPrefs.getInt(key, defValue);

	}

	public static void saveIntValue(String key, int value) {
		SharedPreferences mPrefs = CanCanQApplication.mContext
				.getSharedPreferences(Utils.PREFS_FILE_GPSANDJPC,
						Utils.PREFS_MODE);
		SharedPreferences.Editor editor = mPrefs.edit();

		editor.putInt(key, value);

		editor.commit();
	}

	public static long getLongValue(String key, long defValue) {
		SharedPreferences mPrefs = CanCanQApplication.mContext
				.getSharedPreferences(Utils.PREFS_FILE_GPSANDJPC,
						Utils.PREFS_MODE);

		return mPrefs.getLong(key, defValue);

	}

	public static void saveLongValue(String key, long value) {
		SharedPreferences mPrefs = CanCanQApplication.mContext
				.getSharedPreferences(Utils.PREFS_FILE_GPSANDJPC,
						Utils.PREFS_MODE);
		SharedPreferences.Editor editor = mPrefs.edit();

		editor.putLong(key, value);

		editor.commit();
	}

	public static Boolean getBooleanValue(String key, Boolean defValue) {
		SharedPreferences mPrefs = CanCanQApplication.mContext
				.getSharedPreferences(Utils.PREFS_FILE_GPSANDJPC,
						Utils.PREFS_MODE);

		return mPrefs.getBoolean(key, defValue);

	}

	public static void saveBooleanValue(String key, Boolean value) {
		SharedPreferences mPrefs = CanCanQApplication.mContext
				.getSharedPreferences(Utils.PREFS_FILE_GPSANDJPC,
						Utils.PREFS_MODE);
		SharedPreferences.Editor editor = mPrefs.edit();

		editor.putBoolean(key, value);

		editor.commit();
	}

	public static Float getFloatValue(String key, Float defValue) {
		SharedPreferences mPrefs = CanCanQApplication.mContext
				.getSharedPreferences(Utils.PREFS_FILE_GPSANDJPC,
						Utils.PREFS_MODE);

		return mPrefs.getFloat(key, defValue);

	}

	public static void saveFloatValue(String key, Float value) {
		SharedPreferences mPrefs = CanCanQApplication.mContext
				.getSharedPreferences(Utils.PREFS_FILE_GPSANDJPC,
						Utils.PREFS_MODE);
		SharedPreferences.Editor editor = mPrefs.edit();

		editor.putFloat(key, value);

		editor.commit();
	}

	public static void deleteValue(String key) {
		SharedPreferences mPrefs = CanCanQApplication.mContext
				.getSharedPreferences(Utils.PREFS_FILE_GPSANDJPC,
						Utils.PREFS_MODE);
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.remove(key);
		editor.commit();
	}

}