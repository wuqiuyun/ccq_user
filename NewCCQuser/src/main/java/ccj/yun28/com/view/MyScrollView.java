package ccj.yun28.com.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ScrollView;
import ccj.yun28.com.R;

/**
 * 下拉加载数据
 * 
 * @author Administrator
 * 
 */
public class MyScrollView extends ScrollView {

	private OnRefreshListener listener;
	private View view;
	private View footer_view;
	private int lastY;
	private boolean isLoadMoving = false;
	private boolean isScrollBottom = false;
	private ScrollViewListener scrollViewListener = null;

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
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

	public void setOnRefreshListener(OnRefreshListener listener) {
		this.listener = listener;
	}

	public void onResfreshFinish() {
		if (isLoadMoving) {
			isLoadMoving = false;
			isScrollBottom = false;
			footer_view.setVisibility(View.GONE);
		}
	}

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

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		// x为当前滑动条的横坐标，y表示当前滑动条的纵坐标，oldx为前一次滑动的横坐标，oldy表示前一次滑动的纵坐标
		super.onScrollChanged(x, y, oldx, oldy);
		if (scrollViewListener != null) {
			// 在这里将方法暴露出去
			scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
		}
	}

	// 是否要其弹性滑动
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
			int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

		// 弹性滑动关键则是maxOverScrollX， 以及maxOverScrollY，
		// 一般默认值都是0，需要弹性时，更改其值即可
		// 即就是，为零则不会发生弹性，不为零（>0,负数未测试）则会滑动到其值的位置
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
				scrollRangeX, scrollRangeY, 0, 0, isTouchEvent);
	}

	// 接口
	public interface ScrollViewListener {

		void onScrollChanged(View scrollView, int x, int y, int oldx, int oldy);

	}

	public void setScrollViewListener(ScrollViewListener listener) {
		scrollViewListener = listener;
	}
}
