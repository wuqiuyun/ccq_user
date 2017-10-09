package ccj.yun28.com.view.star.drawable;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.graphics.Shader;

/**
 * 图片处理：圆形、圆角等
 * @author thduan
 * 2015-12-30 15:55:27
 */
public class BitmapEditor {
    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();
    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();
    private BitmapShader mBitmapShader;
    /** 输出的图片 */
    private Bitmap mOutputBm;
    private Canvas mCanvas;
    /** 图片处理属性 */
    private BitmapAttrs mAttrs;
	
	public BitmapEditor(BitmapAttrs attrs) {
		mAttrs = attrs;
		mOutputBm = Bitmap.createBitmap(mAttrs.getTargetWidth(), mAttrs.getTargetHeight(), Config.ARGB_8888);
		mCanvas = new Canvas(mOutputBm);
		setup();
	}
	
	public Bitmap getResult() {
		drawBitmap(mCanvas);
		return mOutputBm;
	}
	
	private void drawBitmap(Canvas canvas) {
		if(mAttrs.getBorderWidth() > 0) {
			canvas.drawPath(getPath(false), mBorderPaint);
		}
		canvas.drawPath(getPath(true), mBitmapPaint);
	}
	
	/**
	 * 构造path对象
	 * @param isDrawablePath
	 * @return
	 */
	private Path getPath(boolean isDrawablePath) {
		Path path = new Path();
		switch (mAttrs.getShapeType()) {
		case CIRCLE:
			if(isDrawablePath) {
				circleDrawablePath(path);
			}else {
				circleBorderPath(path);
			}
			break;
		case RECT:
		default:
			if(isDrawablePath) {
				rectDrawablePath(path);
			}else {
				rectBorderPath(path);
			}
			break;
		}
		return path;
	}
	
	/**
	 * 圆形图形
	 * @param path
	 * @return
	 */
	private Path circleDrawablePath(Path path) {
		float radius = Math.min(mDrawableRect.width()/2, mDrawableRect.height()/2);
		path.addCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), radius, Direction.CW);
		return path;
	}
	
	/**
	 * 圆形边框
	 * @param path
	 * @return
	 */
	private Path circleBorderPath(Path path) {
		float radius = Math.min(mBorderRect.width()/2, mBorderRect.height()/2);
		path.addCircle(mBorderRect.centerX(), mBorderRect.centerY(), radius, Direction.CW);
		return path;
	}
	
	/**
	 * 矩形图形
	 * @param path
	 * @return
	 */
	private Path rectDrawablePath(Path path) {
		path.addRoundRect(mDrawableRect, mAttrs.getRadiis(), Direction.CW);
		return path;
	}
	
	/**
	 * 矩形边框
	 * @param path
	 * @return
	 */
	private Path rectBorderPath(Path path) {
		path.addRoundRect(mBorderRect, mAttrs.getRadiis(), Direction.CW);
		return path;
	}
	
	/**
	 * 设置paint、rect区域、缩放大小
	 */
    private void setup() {
        if (mAttrs.getOriBitmap() == null) {
            return;
        }

        mBitmapShader = new BitmapShader(mAttrs.getOriBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);
        
        mBorderPaint.setStyle(Paint.Style.FILL);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mAttrs.getBorderColor());

        mBorderRect.set(0, 0, mAttrs.getTargetWidth(), mAttrs.getTargetHeight());
        mDrawableRect.set(mAttrs.getBorderWidth(), mAttrs.getBorderWidth(), mBorderRect.width() - mAttrs.getBorderWidth(), mBorderRect.height() - mAttrs.getBorderWidth());

        updateShaderMatrix();
    }

    private void updateShaderMatrix() {
        int bitmapWidth = mAttrs.getOriBitmap().getWidth();
        int bitmapHeight = mAttrs.getOriBitmap().getHeight();;
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (bitmapWidth * mDrawableRect.height() > mDrawableRect.width() * bitmapHeight) {
            scale = mDrawableRect.height() / (float) bitmapHeight;
            dx = (mDrawableRect.width() - bitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) bitmapWidth;
            dy = (mDrawableRect.height() - bitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mAttrs.getBorderWidth(), (int) (dy + 0.5f) + mAttrs.getBorderWidth());

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

}
