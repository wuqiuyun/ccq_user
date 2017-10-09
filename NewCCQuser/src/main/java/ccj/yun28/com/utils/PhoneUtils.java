package ccj.yun28.com.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 获取手机本身自带的属性的方法，如当前所连的wifi的网关
 * 
 * @author Administrator
 * 
 */
public class PhoneUtils {
	/**
	 * 获取当前所连wifip
	 * 
	 * @return
	 */
	public static String getWayIps(Context context) {
		WifiManager wm = (WifiManager) context.getSystemService("wifi");
		DhcpInfo di = wm.getDhcpInfo();
		long getewayIpL = di.gateway;
		String getwayIpS = long2ip(getewayIpL);// 网关地址
		return getwayIpS;
	}
	/**
	 * 获取当前所连wif的ip
	 * 
	 * @return
	 */
	public static String getWifiIp(Context context) {
		WifiManager wm = (WifiManager) context.getSystemService("wifi");
		DhcpInfo di = wm.getDhcpInfo();
		long getewayIpL = di.ipAddress;
		String getwayIpS = long2ip(getewayIpL);// 网关地址
		return getwayIpS;
	}
	/**
	 * 获取当前所连wif的mac地址
	 * 
	 * @return
	 */
	public static String getWifiWG(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE); 
		WifiInfo info = wifi.getConnectionInfo(); 
		return info.getMacAddress();
	}
	/**
	 * 获取当前所连wif网关(内)
	 * 
	 * @return
	 */
	private static String long2ip(long ip) {
		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf((int) (ip & 0xff)));
		sb.append('.');
		sb.append(String.valueOf((int) ((ip >> 8) & 0xff)));
		sb.append('.');
		sb.append(String.valueOf((int) ((ip >> 16) & 0xff)));
		sb.append('.');
		sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
		return sb.toString();
	}
	/**
	 * 外部存储是否可写(也可读)，true代表可写，false代表不可写.
	 * 
	 * @return
	 */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}
	/**
	 * 隐藏键盘
	 * 
	 * @param activity
	 */
	public static void hideKeyboard(Activity activity) {
		InputMethodManager im = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		View v = activity.getCurrentFocus();
		if (v != null) {
			im.hideSoftInputFromWindow(v.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	// / -1无网络，1为 wifi 其他返回值为手机网络，其中如果是3g为2 ，其他为3
		public static int GetNetType(Context context) {
			ConnectivityManager connectMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connectMgr.getActiveNetworkInfo();
			if (info == null)
				return -1;
			if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				return 1;
			} else if (info != null
					&& info.getType() == ConnectivityManager.TYPE_MOBILE) {
				if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_UMTS
						|| info.getSubtype() == TelephonyManager.NETWORK_TYPE_HSDPA // 联通3g
						|| info.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_0
						|| info.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_A// 电信3g
						|| info.getSubtype() == TelephonyManager.NETWORK_TYPE_HSUPA
						|| info.getSubtype() == TelephonyManager.NETWORK_TYPE_HSPA)
					return 2;
				// NETWORK_TYPE_CDMA 网络类型为CDMA
				// * NETWORK_TYPE_EDGE 网络类型为EDGE
				// * NETWORK_TYPE_EVDO_0 网络类型为EVDO0
				// * NETWORK_TYPE_EVDO_A 网络类型为EVDOA
				// * NETWORK_TYPE_GPRS 网络类型为GPRS
				// * NETWORK_TYPE_HSDPA 网络类型为HSDPA
				// * NETWORK_TYPE_HSPA 网络类型为HSPA
				// * NETWORK_TYPE_HSUPA 网络类型为HSUPA
				// * NETWORK_TYPE_UMTS 网络类型为UMTS
			}

			return 3;// 2g或者其他无法识别
		}
		/*
	     * @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
	     * @return
	     */ 
	    public static final boolean ping() { 
	     
	        String result = null; 
	        try { 
	                String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网 
	                Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);// ping网址3次 
	                // 读取ping的内容，可以不加 
	                InputStream input = p.getInputStream(); 
	                BufferedReader in = new BufferedReader(new InputStreamReader(input)); 
	                StringBuffer stringBuffer = new StringBuffer(); 
	                String content = ""; 
	                while ((content = in.readLine()) != null) { 
	                        stringBuffer.append(content); 
	                } 
	                // ping的状态 
	                int status = p.waitFor(); 
	                if (status == 0) { 
	                        result = "success"; 
	                        return true; 
	                } else { 
	                        result = "failed"; 
	                } 
	        } catch (IOException e) { 
	                result = "IOException"; 
	        } catch (InterruptedException e) { 
	                result = "InterruptedException"; 
	        } finally { 
	        } 
	        return false;
	    }
	    /**
	  		 * 判断是否用户是否安装了云店APP
	  		 */
	  		public static boolean isAppInstalled(Context context, String packageName) {
	  			final PackageManager packageManager = context.getPackageManager();
	  			List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
	  			List<String> pName = new ArrayList<String>();
	  			if (pinfo != null) {
	  				for (int i = 0; i < pinfo.size(); i++) {
	  					String pn = pinfo.get(i).packageName;
	  					pName.add(pn);
	  				}
	  			}
	  			return pName.contains(packageName);
	  		}     
}
