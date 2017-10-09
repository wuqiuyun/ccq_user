package ccj.yun28.com.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.R;
import ccj.yun28.com.common.SharedCommon;

import cn.jpush.android.service.DownloadService;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * @description 更新类
 **/
public class CheckUpdate {

	private static final int NOTIFICATION_ID = 0x112;

	private String versionsText = "";
	private Context context;
	private File f;

	private RemoteViews view;
	private NotificationManager manager;
	private Notification notification;
	public ProgressDialog pd;
	private Map<Integer, Integer> downMap = new HashMap<Integer, Integer>();

	private final int DOWN_APK_PROGRESS = 1;
	private final int DOWN_APK_SUCCESS = 2;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_APK_PROGRESS:
				try {
					Map<String, Integer> map = (Map<String, Integer>) msg.obj;
					view.setProgressBar(R.id.down_progress, 100,
							map.get("bit"), true);
					view.setTextViewText(R.id.progress_text, map.get("bit")
							+ "%");
					view.setTextViewText(R.id.ram_change, getMB(map.get("cur"))
							+ "/" + getMB(map.get("max")) + "MB");
					manager.notify(0, notification);
					// Map<String, Integer> map = (Map<String, Integer>)
					// msg.obj;
					// pd.setProgress(map.get("cur"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case DOWN_APK_SUCCESS:
				manager.cancel(0);
				// if (pd != null) {
				// pd.dismiss();
				// }
				InstallApk();
				break;
			}
		}
	};

	public CheckUpdate(Context context) {
		this.context = context;
	}

	public void StartCheck() {
		String version[] = getVersionInfo();
		HttpUtils hu = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addHeader("ccq", new Utils().getSystemVersion() + ","
				+ version[0] + "," + version[2]);
		params.addBodyParameter("app_type", "1");
		params.addBodyParameter("user_versions", version[2]);
		hu.send(HttpMethod.POST, JiekouUtils.UPDATAAPK, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						JsonUpdate(arg0.result);
					}
				});
	}

	/** 解析服务器数据 **/
	private void JsonUpdate(String result) {

		try {
			JSONObject jsonObject = new JSONObject(result);
			int code = Integer.parseInt(jsonObject.getString("code"));
			if (code == 200) {
				String message = jsonObject.getString("message");
				JSONObject data = jsonObject.getJSONObject("data");
				String verString = data.getString("versions");
				String down_url = data.getString("url");
				JSONArray versions_info = data.getJSONArray("versions_info");
				for (int i = 0; i < versions_info.length(); i++) {
					versionsText = (String) versionsText + versions_info.get(i);
				}
				Utils.forceUpdateString = data.getString("force");

				showUpdateDialog(verString, message, down_url);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/** 弹出更新窗口 **/
	private void showUpdateDialog(final String vername, String message,
			final String down_url) {

		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_update, null);
		TextView verText = (TextView) view.findViewById(R.id.ver_name);
		TextView updateText = (TextView) view.findViewById(R.id.update_msg);
		TextView downText = (TextView) view.findViewById(R.id.dialog_btn_down);
		// TextView cancleText = (TextView)
		// view.findViewById(R.id.dialog_btn_no);

		if (!TextUtils.isEmpty(versionsText)) {
			updateText.setText(versionsText);
		} else {
			updateText.setText(message);
		}

		verText.setText(vername);

		final Dialog dialog = new Dialog(context, R.style.dialogstyle);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnKeyListener(keylistener);
		dialog.setCancelable(false);
		if (!dialog.isShowing() && dialog != null) {
			dialog.show();
		}

		downText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedUtil.saveStringValue(SharedCommon.DOWNAPKURL, down_url);
				SharedUtil.saveStringValue(SharedCommon.NETAPKNAME, vername);
				SharedUtil.deleteValue(SharedCommon.CHECKUPDATA);
				dialog.dismiss();
				// pd = new
				// ProgressDialog(context,ProgressDialog.THEME_HOLO_LIGHT);
				// pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				// pd.setCanceledOnTouchOutside(false); pd.setMessage("正在下载更新");
				// // pd.setProgressNumberFormat("%.2fM/%.2fM");
				// // pd.setProgressNumberFormat("%1d kb/%2d kb");
				// pd.show(); new
				// DownThread(down_url).start();

				Toast.makeText(context, "后台更新中", Toast.LENGTH_SHORT).show();
				showNotification(down_url);
			}
		});
		// cancleText.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// // dialog.dismiss();
		// if (Utils.forceUpdateString.equals("1")) {
		// SharedUtil.deleteValue(SharedCommon.CHECKUPDATA);
		// dialog.dismiss();
		// // Toast.makeText(context, "一定要更新哦.......", 1).show();
		// }
		//
		// }
		// });
	}

	/** 显示下载进度条 **/
	private void showNotification(String down_url) {

		downMap.put(0, 0);
		view = new RemoteViews(context.getPackageName(), R.layout.down_notifiy);
		manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		String app_name = context.getString(R.string.app_name);
		view.setTextViewText(R.id.app_name, app_name);
		view.setImageViewResource(R.id.image, R.drawable.logo);
		notification = new Notification();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.contentView = view;
		notification.icon = android.R.drawable.stat_sys_download;
		notification.tickerText = "正在下载...";
		manager.notify(0, notification);
		new DownThread(down_url).start();

	}

	/** 下载 **/
	private class DownThread extends Thread {
		String down_url;

		public DownThread(String down_url) {
			this.down_url = down_url;
		}

		public void run() {
			ApkDownload(down_url);
		}
	}

	/** 下载 **/
	@SuppressLint("NewApi")
	private void ApkDownload(String down_url) {

		String filePath = Environment.getExternalStorageDirectory() + "/Ccq";
		String fileType = down_url.substring(down_url.lastIndexOf("."));
		String fileName = Utils.md5(down_url) + fileType;

		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		f = new File(filePath, fileName);
		if (f.exists()) {
			f.delete();
		}

		try {
			URL url = new URL(down_url);
			Log.d("HTTP下载", down_url);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setConnectTimeout(5 * 1000);
			connection.setReadTimeout(8 * 1000);
			connection.setDoInput(true);
			int maxsize = connection.getContentLength();
			int cursize = 0;
			int bit = 0;
			FileOutputStream fos = new FileOutputStream(f);
			InputStream is = connection.getInputStream();
			byte[] bt = new byte[4096];
			int len = -1;
			while ((len = is.read(bt)) != -1) {
				fos.write(bt, 0, len);
				fos.flush();
				cursize += len;
				bit = cursize * 100 / maxsize;
				if (bit - downMap.get(0) >= 1) {
					downMap.put(0, bit);
					Map<String, Integer> map = new HashMap<String, Integer>();
					map.put("bit", bit);
					map.put("cur", cursize);
					map.put("max", maxsize);
					// pd.setProgressNumberFormat(String.format("%.2fM/%.2fM",
					// cursize, maxsize));
					// pd.setMax(maxsize);
					handler.sendMessage(handler.obtainMessage(
							DOWN_APK_PROGRESS, map));
				}

			}
			handler.sendEmptyMessage(DOWN_APK_SUCCESS);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	// /** 下载 **/
	// @SuppressLint("NewApi")
	// private void ApkDownload(String down_url) {
	//
	// String filePath = Environment.getExternalStorageDirectory() + "/Ccq";
	// String fileType = down_url.substring(down_url.lastIndexOf("."));
	// String fileName = Utils.md5(down_url) + fileType;
	//
	// File file = new File(filePath);
	// if (!file.exists()) {
	// file.mkdirs();
	// }
	// f = new File(filePath, fileName);
	// if (f.exists()) {
	// f.delete();
	// }
	//
	// HttpClient client = new DefaultHttpClient();
	// HttpGet get = new HttpGet(down_url);
	// HttpResponse response;
	// try {
	// response = client.execute(get);
	// HttpEntity entity = response.getEntity();
	// long length = entity.getContentLength();
	//
	// pd.setMax((int)length);//设置进度条的最大值
	//
	// InputStream is = entity.getContent();
	// FileOutputStream fileOutputStream = null;
	// if (is != null) {
	// fileOutputStream = new FileOutputStream(f);
	// byte[] buf = new byte[1024];
	// int ch = -1;
	// int count = 0;
	// while ((ch = is.read(buf)) != -1) {
	// fileOutputStream.write(buf, 0, ch);
	// count += ch;
	// if (length > 0) {
	// pd.setProgress(count);
	// }
	// }
	// }
	// fileOutputStream.flush();
	// if (fileOutputStream != null) {
	// fileOutputStream.close();
	// }
	// InstallApk(); //告诉HANDER已经下载完成了，可以安装了
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	/** 内存转化为MB并保留两位小数 **/
	private String getMB(int num) {
		double count = (double) num / 1048576;
		DecimalFormat df = new DecimalFormat(".00");
		String str = df.format(count);
		return str;
	}

	/** 安装 **/
	private void InstallApk() {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(f),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/** 获取当前版本信息 **/
	private String[] getVersionInfo() {
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

	/**
	 * 此功能可以使得dialog无法响应返回键
	 */
	OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				return true;
			} else {
				return false;
			}
		}
	};


}
