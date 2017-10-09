package ccj.yun28.com.view.zdygridview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import ccj.yun28.com.R;
import ccj.yun28.com.view.OnRefreshListener;

public class MyTagListView extends ListView {

	private OnRefreshListener listener;
	private View view;
	private View footer_view;
	private int lastY;
	private boolean isLoadMoving = false;
	private boolean isScrollBottom = false;

	public MyTagListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		lastY = getScrollY();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_UP:
			if (view != null && listener != null) {
				handler.sendMessageDelayed(handler.obtainMessage(), 5);
			}

			break;
		}
		return super.onTouchEvent(ev);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int scrollY = getScrollY();
			if (lastY != scrollY) {
				lastY = scrollY;
				handler.sendMessageDelayed(handler.obtainMessage(), 5);
			} else {
				isScrollBottom = view.getMeasuredHeight() <= getScrollY()
						+ getHeight();
				if (isScrollBottom && !isLoadMoving) {
					footer_view.setVisibility(View.VISIBLE);
					isLoadMoving = true;
					listener.onLoadMoring();
				}
			}
		}
	};

	public void getView() {
		this.view = getChildAt(0);
		footer_view = LayoutInflater.from(getContext()).inflate(
				R.layout.scroll_load, null);

		ImageView imageView = (ImageView) footer_view
				.findViewById(R.id.image_load);
		Animation animation = AnimationUtils.loadAnimation(getContext(),
				R.anim.progress_rotate);
		imageView.startAnimation(animation);

		measureView(footer_view);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				footer_view.getMeasuredHeight());
		footer_view.setVisibility(View.GONE);
		((ViewGroup) view).addView(footer_view, params);
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	// �������뼰����
	private float xDistance, yDistance, xLast, yLast;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();

			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;

			if (xDistance > yDistance) {
				return false;
			}
		}

		return super.onInterceptTouchEvent(ev);
	}

}
