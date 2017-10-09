package ccj.yun28.com.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

public class FileUtils {
	/** 缓存文件目录 */
	private File mCacheDir;
	static File dir = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	public static final String cacheDir = dir + "/jd/imgs/";

	public FileUtils(Context context) {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			mCacheDir = new File(cacheDir);
		else
			mCacheDir = context.getCacheDir();// 如何获取系统内置的缓存存储路径
		if (!mCacheDir.exists())
			mCacheDir.mkdirs();
	}

	public String getCacheDir() {
		return mCacheDir.getAbsolutePath();
	}

	public static final String JD_PATH = dir + "/jd/";
	public static String SavePath = JD_PATH + "temphtml.html";

	// 保存字符串到文件中
	public static int saveAsAddThingFileWriter(String content) {
		File file = new File(JD_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		FileWriter fwriter = null;
		try {
			fwriter = new FileWriter(SavePath);
			// String tophtmlString="<html><body style=\"bgcolor:\"green\"\">";
			// String endhtmlString="</body></html>";
			// content=tophtmlString+content+endhtmlString;
			// content=String.format(content);
			content = content.replaceAll("&lt;", "<");
			content = content.replaceAll("&gt;", ">");
			content = content.replaceAll("&nbsp;", "");
			content = content.replaceAll("&quot;", "\"");

			fwriter.write(content);
		} catch (IOException ex) {
			ex.printStackTrace();
			return 0;
		}
		return 0;
	}

	public static boolean fileIsExists(String savePath2) {
		try {
			// TODO Auto-generated method stub
			File f = new File(savePath2);
			if (!f.exists()) {
				return false;
			}

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
}