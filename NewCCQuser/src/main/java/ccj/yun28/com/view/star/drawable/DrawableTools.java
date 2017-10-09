package ccj.yun28.com.view.star.drawable;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.widget.ImageView;

/**
 * 1.图形pressed、normal等样式封装
 * 2.图片圆形、圆角处理封装
 * @author thduan
 * 2015-12-28 13:50:50
 */
public class DrawableTools {
	
	/**
	 * Imageview设置 图片pressed、normal等样式
	 * @param view
	 * @param attrs
	 */
	public static void setImageDrawableWithStateList(ImageView view, DrawableAttrs attrs) {
		view.setImageDrawable(getStateListDrawable(attrs));
	}
	
	/**
	 * ImageView设置 形状pressed、normal等样式
	 * @param view
	 * @param attrs
	 */
	public static void setImageDrawableWithStateList(ImageView view, ShapeAttrs attrs) {
		view.setImageDrawable(getStateListDrawable(attrs));
	}
	
	/**
	 * View设置背景图片pressed、normal等样式
	 * @param view
	 * @param attrs
	 */
	public static void setViewBgWithStateListDrawable(View view, DrawableAttrs attrs) {
		view.setBackgroundDrawable(getStateListDrawable(attrs));
	}
	
	/**
	 * View设置背景形状pressed、normal等样式
	 * @param view
	 * @param attrs
	 */
	public static void setViewBgWithStateListDrawable(View view, ShapeAttrs attrs) {
		view.setBackgroundDrawable(getStateListDrawable(attrs));
	}
	
	/**
	 * 生成图片pressed、normal等样式集合
	 * @param attrs
	 * @return
	 */
	public static StateListDrawable getStateListDrawable(DrawableAttrs attrs) {
		return new DrawableStateListCreator(attrs).createStateListDrawable();
	}
	
	/**
	 * 生成形成pressed、normal等样式集合
	 * @param attrs
	 * @return
	 */
	public static StateListDrawable getStateListDrawable(ShapeAttrs attrs) {
		return new ShapeStateListCreator(attrs).createStateListDrawable();
	}
	
	/**
	 * Image图片设置为处理后的bitmap
	 * @param view
	 * @param attrs
	 */
	public static void setImageDrawableWithEditedBm(ImageView view, BitmapAttrs attrs) {
		view.setImageBitmap(getEditedBitmap(attrs));
	}
	
	/**
	 * View背景设置为 处理后的bitmap
	 * @param view
	 * @param attrs
	 */
	public static void setViewBgWithEditedBm(View view, BitmapAttrs attrs) {
		view.setBackgroundDrawable(new BitmapDrawable(getEditedBitmap(attrs)));
	}
	
	/**
	 * 图片处理为圆形、圆角等
	 * @param attrs
	 * @return
	 */
	public static Bitmap getEditedBitmap(BitmapAttrs attrs) {
		return new BitmapEditor(attrs).getResult();
	}
	
}
