package ccj.yun28.com.view.star.drawable;

import android.graphics.drawable.StateListDrawable;

/**
 * 图片不同状态展示
 * @author thduan
 * 2015-12-29 14:24:19
 */
public class DrawableStateListCreator {
	private DrawableAttrs mAttrs;
	
	public DrawableStateListCreator(DrawableAttrs attrs) {
		mAttrs = attrs;
	}
	
	public  StateListDrawable createStateListDrawable() {
		StateListDrawable state = new StateListDrawable();
		state.addState(new int[]{-android.R.attr.state_enabled}, mAttrs.getDisableDrawable());
		state.addState(new int[]{android.R.attr.state_checked}, mAttrs.getCheckedDrawable());
		state.addState(new int[]{android.R.attr.state_selected}, mAttrs.getSelectedDrawable());
		state.addState(new int[]{android.R.attr.state_pressed}, mAttrs.getPressedDrawable());
		state.addState(new int[]{}, mAttrs.getNormalDrawable());
		return state;
	}

}
