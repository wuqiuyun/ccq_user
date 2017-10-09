package ccj.yun28.com.view.star.drawable;

import android.graphics.drawable.Drawable;

/**
 * 图片pressed、normal等集合样式属性
 * @author thduan
 * 2015-12-29 14:33:41
 */
public class DrawableAttrs extends BaseAttrs{
	private Drawable normalDrawable;
	private Drawable pressedDrawable;
	private Drawable disableDrawable;
	private Drawable checkedDrawable;
	private Drawable selectedDrawable;
	
	public Drawable getNormalDrawable() {
		return normalDrawable;
	}
	
	public DrawableAttrs setNormalDrawable(int normalResId) {
		this.normalDrawable = mContext.getResources().getDrawable(normalResId);
		return this;
	}
	
	public DrawableAttrs setNormalDrawable(Drawable normalDrawable) {
		this.normalDrawable = normalDrawable;
		return this;
	}
	public Drawable getPressedDrawable() {
		return pressedDrawable;
	}
	
	public DrawableAttrs setPressedDrawable(int pressedResId) {
		this.pressedDrawable = mContext.getResources().getDrawable(pressedResId);
		return this;
	}
	public DrawableAttrs setPressedDrawable(Drawable pressedDrawable) {
		this.pressedDrawable = pressedDrawable;
		return this;
	}
	public Drawable getDisableDrawable() {
		return disableDrawable;
	}
	public DrawableAttrs setDisableDrawable(Drawable disableDrawable) {
		this.disableDrawable = disableDrawable;
		return this;
	}
	public DrawableAttrs setDisableDrawable(int disableResId) {
		this.disableDrawable = mContext.getResources().getDrawable(disableResId);;
		return this;
	}
	
	public Drawable getCheckedDrawable() {
		return checkedDrawable;
	}
	
	public DrawableAttrs setCheckedDrawable(int checkedResId) {
		this.checkedDrawable = mContext.getResources().getDrawable(checkedResId);
		return this;
	}
	
	public DrawableAttrs setCheckedDrawable(Drawable checkedDrawable) {
		this.checkedDrawable = checkedDrawable;
		return this;
	}

	public Drawable getSelectedDrawable() {
		return selectedDrawable;
	}

	public DrawableAttrs setSelectedDrawable(Drawable selectedDrawable) {
		this.selectedDrawable = selectedDrawable;
		return this;
	}
	
	public DrawableAttrs setSelectedDrawable(int selectedResId) {
		this.selectedDrawable = mContext.getResources().getDrawable(selectedResId);
		return this;
	}

}
