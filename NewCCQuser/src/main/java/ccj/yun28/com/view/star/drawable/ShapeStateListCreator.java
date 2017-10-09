package ccj.yun28.com.view.star.drawable;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * 形状状态
 * @author thduan
 * 2015-12-29 15:12:19
 */
public class ShapeStateListCreator {
	private ShapeAttrs mAttrs;
	
	public ShapeStateListCreator(ShapeAttrs attrs) {
		mAttrs = attrs;
	}
	
	public StateListDrawable createStateListDrawable() {
		DrawableAttrs drawableAttrs = new DrawableAttrs();
		drawableAttrs.setNormalDrawable(normalDrawable());
		drawableAttrs.setPressedDrawable(pressedDrawable());
		drawableAttrs.setCheckedDrawable(checkedDrawable());
		drawableAttrs.setDisableDrawable(disableDrawable());
		return new DrawableStateListCreator(drawableAttrs).createStateListDrawable();
	}
	
	private GradientDrawable disableDrawable() {
		if(mAttrs.hasDisableState()) {
			return newGradientDrawable(mAttrs.getDisableArgb(), 
					mAttrs.getStrokeDisableArgb());
		}else {
			return null;
		}
	}
	
	private GradientDrawable pressedDrawable() {
		if(mAttrs.hasPressedState()) {
			return newGradientDrawable(mAttrs.getPressedArgb(), 
					mAttrs.getStrokePressedArgb());
		}else {
			return null;
		}
	}
	
	private GradientDrawable checkedDrawable() {
		if(mAttrs.hasCheckedState()) {
			return newGradientDrawable(mAttrs.getCheckedArgb(), 
					mAttrs.getStrokeCheckedArgb());
		}else {
			return null;
		}
	}
	
	private GradientDrawable normalDrawable() {
		if(mAttrs.hasNormalState()) {
			return newGradientDrawable(mAttrs.getNormalArgb(), 
					mAttrs.getStrokeNormalArgb());
		}else {
			return null;
		}
	}
	
	private GradientDrawable newGradientDrawable(int color, int strokeColor) {
		GradientDrawable drawable = new GradientDrawable();
		drawable.setShape(mAttrs.getShapeType());
		drawable.setColor(color);
		drawable.setStroke(mAttrs.getStrokeWidth(), strokeColor);
		if(mAttrs.getCornerRadii() != null && mAttrs.getCornerRadii().length == 8) {
			drawable.setCornerRadii(mAttrs.getCornerRadii());
		}
		return drawable;
	}
}

