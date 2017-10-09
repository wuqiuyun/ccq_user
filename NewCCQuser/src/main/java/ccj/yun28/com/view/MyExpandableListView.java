package ccj.yun28.com.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * 自定义MyExpandableListView 是为了解决MyExpandableListView和scollerview的兼容问题
 **/
public class MyExpandableListView extends ExpandableListView {

	public MyExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub

		int spec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);

		super.onMeasure(widthMeasureSpec, spec);
	}

	@Override
	public void setSelection(int position) {
		// TODO Auto-generated method stub
		// super.setSelection(position);
		setSelection(0);
	}

}
