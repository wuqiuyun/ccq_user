package ccj.yun28.com.view.star.drawable;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

/**
 * 形状pressed、normal等集合样式属性
 * @author thduan
 * 2015-12-29 14:33:32
 */
public class ShapeAttrs extends BaseAttrs{
	private static final int DEFAULT_STROKE_WIDTH = 0;
	private int normalArgb = INVAILD_VALUE;
	private int pressedArgb = INVAILD_VALUE;
	private int checkedArgb = INVAILD_VALUE;
	private int disableArgb = INVAILD_VALUE;
	/** 边框宽度 */
	private int strokeWidth = DEFAULT_STROKE_WIDTH;
	/** 边框颜色 */
	private int strokeNormalArgb = INVAILD_VALUE;
	private int strokePressedArgb = INVAILD_VALUE;
	private int strokeCheckedArgb = INVAILD_VALUE;
	private int strokeDisableArgb = INVAILD_VALUE;
	/** 上层传时注意：api < 12的和 api >= 12的，左下角与右下角相反; 数组必须8个元素  */
	private float[] cornerRadii;
	/** 默认为矩形 */
	private int shapeType = GradientDrawable.RECTANGLE;
	
	public int getNormalArgb() {
		return normalArgb;
	}
	
	public ShapeAttrs setNormalArgb(int normalArgbResId) {
		this.normalArgb = mContext.getResources().getColor(normalArgbResId);
		return this;
	}
	
	public ShapeAttrs setNormalArgb(String normalArgbColor) {
		this.normalArgb = Color.parseColor(normalArgbColor);
		return this;
	}

	public int getPressedArgb() {
		return pressedArgb;
	}
	
	public ShapeAttrs setPressedArgb(String pressedArgbColor) {
		this.pressedArgb = Color.parseColor(pressedArgbColor);
		return this;
	}
	public ShapeAttrs setPressedArgb(int pressedArgbResId) {
		this.pressedArgb = mContext.getResources().getColor(pressedArgbResId);
		return this;
	}
	public int getDisableArgb() {
		return disableArgb;
	}
	public ShapeAttrs setDisableArgb(int disableArgbResId) {
		this.disableArgb = mContext.getResources().getColor(disableArgbResId);
		return this;
	}
	public ShapeAttrs setDisableArgb(String disableArgbColor) {
		this.disableArgb = Color.parseColor(disableArgbColor);
		return this;
	}
	public int getStrokeWidth() {
		return strokeWidth;
	}
	public ShapeAttrs setStrokeWidth(int strokeWidth) {
		this.strokeWidth = strokeWidth;
		return this;
	}
	public int getStrokeNormalArgb() {
		return strokeNormalArgb;
	}
	public ShapeAttrs setStrokeNormalArgb(int strokeNormalArgbResId) {
		this.strokeNormalArgb = mContext.getResources().getColor(strokeNormalArgbResId);
		return this;
	}
	public ShapeAttrs setStrokeNormalArgb(String strokeNormalArgbColor) {
		this.strokeNormalArgb = Color.parseColor(strokeNormalArgbColor);
		return this;
	}
	
	public int getStrokePressedArgb() {
		return strokePressedArgb;
	}
	public ShapeAttrs setStrokePressedArgb(int strokePressedArgbResId) {
		this.strokePressedArgb = mContext.getResources().getColor(strokePressedArgbResId);
		return this;
	}
	public ShapeAttrs setStrokePressedArgb(String strokePressedArgbColor) {
		this.strokePressedArgb = Color.parseColor(strokePressedArgbColor);
		return this;
	}
	public int getStrokeDisableArgb() {
		return strokeDisableArgb;
	}
	public ShapeAttrs setStrokeDisableArgb(int strokeDisableArgbResId) {
		this.strokeDisableArgb = mContext.getResources().getColor(strokeDisableArgbResId);
		return this;
	}
	public ShapeAttrs setStrokeDisableArgb(String strokeDisableArgbColor) {
		this.strokeDisableArgb = Color.parseColor(strokeDisableArgbColor);
		return this;
	}
	public float[] getCornerRadii() {
		return cornerRadii;
	}
	public ShapeAttrs setCornerRadii(float[] cornerRadii) {
		this.cornerRadii = cornerRadii;
		return this;
	}
	
	public int getCheckedArgb() {
		return checkedArgb;
	}

	public ShapeAttrs setCheckedArgb(int checkedArgbResId) {
		this.checkedArgb = mContext.getResources().getColor(checkedArgbResId);
		return this;
	}
	
	public ShapeAttrs setCheckedArgb(String checkedArgbColor) {
		this.checkedArgb = Color.parseColor(checkedArgbColor);
		return this;
	}

	public int getStrokeCheckedArgb() {
		return strokeCheckedArgb;
	}

	public ShapeAttrs setStrokeCheckedArgb(int strokeCheckedArgbResId) {
		this.strokeCheckedArgb = mContext.getResources().getColor(strokeCheckedArgbResId);
		return this;
	}
	
	public ShapeAttrs setStrokeCheckedArgb(String strokeCheckedArgbColor) {
		this.strokeCheckedArgb = Color.parseColor(strokeCheckedArgbColor);
		return this;
	}

	public int getShapeType() {
		return shapeType;
	}
	
	public ShapeAttrs shapeOval() {
		this.shapeType = GradientDrawable.OVAL;
		return this;
	}
	
	public ShapeAttrs shapeRect() {
		this.shapeType = GradientDrawable.RECTANGLE;
		return this;
	}
	
	public ShapeAttrs setCornerRadius(float cornerRadius) {
		cornerRadii = new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius,};
		return this;
	}

	/**
	 * 是否有 disable状态
	 * @return
	 */
	public boolean hasDisableState() {
		return (disableArgb != INVAILD_VALUE || 
				strokeDisableArgb != INVAILD_VALUE);
	}
	
	public boolean hasPressedState() {
		return (pressedArgb != INVAILD_VALUE || 
				strokePressedArgb != INVAILD_VALUE);
	}
	
	public boolean hasCheckedState() {
		return (checkedArgb != INVAILD_VALUE || 
				strokeCheckedArgb != INVAILD_VALUE);
	}
	
	/**
	 * normal状态通常是必须有的
	 * @return
	 */
	public boolean hasNormalState() {
		return (normalArgb != INVAILD_VALUE ||
				strokeNormalArgb != INVAILD_VALUE);
	}
	
}
