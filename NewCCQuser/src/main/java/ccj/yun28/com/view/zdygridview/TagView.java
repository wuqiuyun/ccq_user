package ccj.yun28.com.view.zdygridview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;
import ccj.yun28.com.R;

/**
 ** 每一个标签的view
 * 
 * @author 123456
 * 
 */
public class TagView extends ToggleButton {

	private boolean mCheckEnable = true;

	public TagView(Context paramContext) {
		super(paramContext);
		init();
	}

	public TagView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init();
	}

	public TagView(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, 0);
		init();
	}

	private void init() {
		setTextOn(null);
		setTextOff(null);
		setText("");
		// 背景色
		setBackgroundResource(R.drawable.zylm);
		int c = getResources().getColor(R.color.color_text_black);
		setTextColor(c);
	}

	public void setCheckEnable(boolean paramBoolean) {
		this.mCheckEnable = paramBoolean;
		if (!this.mCheckEnable) {
			super.setChecked(false);
		}
	}

	public void setChecked(boolean paramBoolean) {
		if (this.mCheckEnable) {
			super.setChecked(paramBoolean);
		}
	}
}
