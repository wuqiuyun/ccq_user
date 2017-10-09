package ccj.yun28.com.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import ccj.yun28.com.R;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;

public class ImageUtil {
	public static void display(String imageUrl, ImageView imageview) {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		FileUtils fileUtils = new FileUtils(imageview.getContext());
		BitmapDisplayConfig displayConfig = new BitmapDisplayConfig();
		displayConfig.setShowOriginal(false); // 显示原始图片,不压缩, 尽量不要使用, 图片太大时容易OOM。
		BitmapUtils bitmapUtils = new BitmapUtils(imageview.getContext(),
				fileUtils.getCacheDir(), cacheSize);
		bitmapUtils.configDefaultConnectTimeout(5000);
		bitmapUtils.configDefaultReadTimeout(5000);
		bitmapUtils.configThreadPoolSize(5);
		bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);// 设置图片压缩类型

		// 默认图片 图片地址
		bitmapUtils.configDefaultLoadingImage(R.drawable.banner)
				.configDefaultLoadFailedImage(R.drawable.banner)
				.display(imageview, imageUrl);
	}

	/**
	 * 相片按相框的比例动态缩放
	 * 
	 * @param context
	 * @param 要缩放的图片
	 * @param width
	 *            模板宽度
	 * @param height
	 *            模板高度
	 * @return
	 */
	public static Bitmap upImageSize(Context context, Bitmap bmp, int width,
			int height) {
		if (bmp == null) {
			return null;
		}
		// 计算比例
		float scaleX = (float) width / bmp.getWidth();// 宽的比例
		float scaleY = (float) height / bmp.getHeight();// 高的比例
		// 新的宽高
		int newW = 0;
		int newH = 0;
		if (scaleX > scaleY) {
			newW = (int) (bmp.getWidth() * scaleX);
			newH = (int) (bmp.getHeight() * scaleX);
		} else if (scaleX <= scaleY) {
			newW = (int) (bmp.getWidth() * scaleY);
			newH = (int) (bmp.getHeight() * scaleY);
		}
		return Bitmap.createScaledBitmap(bmp, newW, newH, true);
	}
}
