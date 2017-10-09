package ccj.yun28.com.view.star.drawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

/**
 * 图片处理为圆形、圆角矩形等样式属性
 * @author thduan
 * 2015-12-30 15:03:15
 */
public class BitmapAttrs extends BaseAttrs{
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 2;
    private static final int DEF_BORDER_WIDTH = 0;
    private static final int DEF_TARGET_WIDTH = -1;
    
    /** 原始图片 */
	private Bitmap oriBitmap;
	
	/** 边框宽度 */
	private int borderWidth = DEF_BORDER_WIDTH;
	
	/** 边框颜色 */
	private int borderColor;
	
	/** 目标图片类型：圆形、矩形 */
	private EShape shapeType = EShape.RECT;
	
	/** 矩形圆角参数数组：必须有8个元素，分别表示左上、右上、右下、左下角的圆角弧度 */
	private float[] radiis;
	
	/** 预期生成的bitmap的宽度，
	 *  强烈建议设置 targetWidth、targetHeight这两个属性，
	 *  否则生成图片后由于显示到View时会因为拉伸方式导致图片展示不是预期效果，
	 *  如果事先设置了这两个属性，那么会进行伸缩
	 **/
	private int targetWidth = DEF_TARGET_WIDTH;
	
	/** 预期生成的bitmap的高度 */
	private int targetHeight = DEF_TARGET_WIDTH;
	
	enum EShape{
		CIRCLE,  //圆形
		RECT   //矩形：直角矩形、圆角矩形、个别角圆角矩形皆可以
	}
	
	/**
	 * 不允许外部调用，外部必须在初始化的时候传图片
	 */
	private BitmapAttrs() {}
	
	public BitmapAttrs(Bitmap bitmap) {
		this.oriBitmap = bitmap;
	}
	
	public BitmapAttrs(int resId) {
		this.oriBitmap = getBitmapFromDrawable(mContext.getResources().getDrawable(resId));
	}
	
	public BitmapAttrs(Drawable drawable) {
		this.oriBitmap = getBitmapFromDrawable(drawable);
	}

	public Bitmap getOriBitmap() {
		return oriBitmap;
	}

	public EShape getShapeType() {
		return shapeType;
	}
	
	public BitmapAttrs shapeCircle() {
		this.shapeType = EShape.CIRCLE;
		return this;
	}
	
	public BitmapAttrs shapeRect() {
		this.shapeType = EShape.RECT;
		return this;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public BitmapAttrs setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
		return this;
	}

	public int getBorderColor() {
		return borderColor;
	}

	public BitmapAttrs setBorderColor(int borderColorResID) {
		this.borderColor = mContext.getResources().getColor(borderColorResID);
		return this;
	}
	
	public BitmapAttrs setBorderColor(String borderColor) {
		this.borderColor = Color.parseColor(borderColor);
		return this;
	}

	public float[] getRadiis() {
		if(radiis == null) {
			return new float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
		}else {
			return radiis;
		}
	}

	public BitmapAttrs setRadiis(float[] radiis) {
		this.radiis = radiis;
		return this;
	}
	
	public BitmapAttrs setCornerRadius(float cornerRadius) {
		this.radiis = new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius};
		return this;
	}

	public int getTargetWidth() {
		if(targetWidth == DEF_TARGET_WIDTH) {
			if(oriBitmap != null) {
				return oriBitmap.getWidth();
			}else {
				return DEF_TARGET_WIDTH;
			}
		}else {
			return targetWidth;
		}
	}

	public BitmapAttrs setTargetWidth(int targetWidth) {
		this.targetWidth = targetWidth;
		return this;
	}

	public int getTargetHeight() {
		if(targetHeight == DEF_TARGET_WIDTH) {
			if(oriBitmap != null) {
				return oriBitmap.getHeight();
			}else {
				return DEF_TARGET_WIDTH;
			}
		}else {
			return targetHeight;
		}
	}

	public BitmapAttrs setTargetHeight(int targetHeight) {
		this.targetHeight = targetHeight;
		return this;
	}
	
    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
}
