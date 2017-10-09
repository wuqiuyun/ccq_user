package ccj.yun28.com.lunbotu;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import ccj.yun28.com.R;

/**
 * 广告图片自动轮播控件</br>
 * 
 * <pre>
 *   集合ViewPager和指示器的一个轮播控件，主要用于�?般常见的广告图片轮播，具有自动轮播和手动轮播功能 
 *   使用：只�?在xml文件中使用{@code <com.minking.imagecycleview.ImageCycleView/>} �?
 *   然后在页面中调用  {@link #setImageResources(ArrayList, ImageCycleViewListener) }即可!
 *   
 *   另外提供{@link #startImageCycle() } \ {@link #pushImageCycle() }两种方法，用于在Activity不可见之时节省资源；
 *   因为自动轮播�?要进行控制，有利于内存管�?
 * </pre>
 * 
 */
public class ImageCycleView extends LinearLayout {

	/**
	 * 上下�?
	 */
	private Context mContext;

	/**
	 * 图片轮播视图
	 */
	private CycleViewPager mBannerPager = null;

	/**
	 * 滚动图片视图适配�?
	 */
	private ImageCycleAdapter mAdvAdapter;

	/**
	 * 图片轮播指示器控�?
	 */
	private ViewGroup mGroup;

	/**
	 * 图片轮播指示�?-个图
	 */
	private ImageView mImageView = null;

	/**
	 * 滚动图片指示�?-视图列表
	 */
	private ImageView[] mImageViews = null;

	/**
	 * 图片滚动当前图片下标
	 */
	private int mImageIndex = 1;

	/**
	 * 手机密度
	 */
	private float mScale;

	/**
	 * @param context
	 */
	public ImageCycleView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ImageCycleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mScale = context.getResources().getDisplayMetrics().density;
		LayoutInflater.from(context)
				.inflate(R.layout.view_banner_content, this);
		mBannerPager = (CycleViewPager) findViewById(R.id.pager_banner);
		mBannerPager.setOnPageChangeListener(new GuidePageChangeListener());

		// 设置ViewPage切换动画
		mBannerPager.setPageTransformer(true, new FadePageTransformer());
		// 设置ViewPage切换时间
		ViewPagerScroller pagerScroller = new ViewPagerScroller(context);
		pagerScroller.setScrollDuration(400);// 图片切换速度，时间越长，速度越慢 TODO
		pagerScroller.initViewPagerScroll(mBannerPager);

		mBannerPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					// 开始图片滚动
					startImageTimerTask();
					break;
				default:
					// 停止图片滚动
					stopImageTimerTask();
					break;
				}
				return false;
			}
		});
		// 滚动图片右下指示器视�?
		mGroup = (ViewGroup) findViewById(R.id.viewGroup);
	}

	/**
	 * view理所当然就是滑动中的那个view，position这里是float类型，不是平时理解的int位置，而是当前滑动状态的一个表示，
	 * 比如当滑动到正全屏时，position是0，而向左滑动，使得右边刚好有一部被进入屏幕时，position是1，
	 * 如果前一页和下一页基本各在屏幕占一半时，前一页的position是-0.5，后一页的posiotn是0.5
	 * 
	 */
	public class FadePageTransformer implements PageTransformer {

		public void transformPage(View view, float position) {
			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0);
			} else if (position == 0) { // 正全屏
				view.setAlpha(1);
			} else if (position < 0) { // [-1,0]
				// Use the default slide transition when moving to the left page
				view.setAlpha(-position);
			} else if (position <= 1) { // (0,1]
				// Fade the page out.
				view.setAlpha(1 - position);
			} else { // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}
	}

	/**
	 * 装填图片数据
	 * 
	 * @param imageUrlList
	 * @param imageCycleViewListener
	 */
	public void setImageResources(ArrayList<ADInfo> infoList,
			ImageCycleViewListener imageCycleViewListener) {
		// 清除�?有子视图
		mGroup.removeAllViews();
		// 图片广告数量
		final int imageCount = infoList.size();
		mImageViews = new ImageView[imageCount];
		for (int i = 0; i < imageCount; i++) {
			mImageView = new ImageView(mContext);
			int imageParams = (int) (mScale * 20 + 0.5f);// XP与DP转换，�?�应不同分辨�?
			int imagePadding = (int) (mScale * 5 + 0.5f);
			LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layout.setMargins(3, 0, 3, 0);
			mImageView.setLayoutParams(layout);
			// mImageView.setPadding(imagePadding, imagePadding, imagePadding,
			// imagePadding);
			mImageViews[i] = mImageView;
			if (i == 0) {
				mImageViews[i].setBackgroundResource(R.drawable.yyw);
			} else {
				mImageViews[i].setBackgroundResource(R.drawable.yy);
			}
			mGroup.addView(mImageViews[i]);
		}
		mAdvAdapter = new ImageCycleAdapter(mContext, infoList,
				imageCycleViewListener);
		mBannerPager.setAdapter(mAdvAdapter);
		startImageTimerTask();
	}

	/**
	 * 开始轮播(手动控制自动轮播与否，便于资源控�?)
	 */
	public void startImageCycle() {
		startImageTimerTask();
	}

	/**
	 * 暂停轮播—�?�用于节省资�?
	 */
	public void pushImageCycle() {
		stopImageTimerTask();
	}

	/**
	 * 开始图片滚动任�?
	 */
	private void startImageTimerTask() {
		stopImageTimerTask();
		// 图片3每秒滚动一次 TODO
		mHandler.postDelayed(mImageTimerTask, 5000);
	}

	/**
	 * 停止图片滚动任务
	 */
	private void stopImageTimerTask() {
		mHandler.removeCallbacks(mImageTimerTask);
	}

	private Handler mHandler = new Handler();

	/**
	 * 图片自动轮播Task
	 */
	private Runnable mImageTimerTask = new Runnable() {

		@Override
		public void run() {
			if (mImageViews != null) {
				// 下标等于图片列表长度说明已滚动到�?后一张图�?,重置下标
				if ((++mImageIndex) == mImageViews.length + 1) {
					mImageIndex = 1;
				}
				mBannerPager.setCurrentItem(mImageIndex);
			}
		}
	};

	/**
	 * 轮播图片状�?�监听器
	 * 
	 * @author minking
	 */
	private final class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE)
				startImageTimerTask(); // �?始下次计�?
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int index) {

			if (index == 0 || index == mImageViews.length + 1) {
				return;
			}
			// 设置图片滚动指示器背�?
			mImageIndex = index;
			index -= 1;
			mImageViews[index].setBackgroundResource(R.drawable.yyw);
			for (int i = 0; i < mImageViews.length; i++) {
				if (index != i) {
					mImageViews[i].setBackgroundResource(R.drawable.yy);
				}
			}

		}

	}

	private class ImageCycleAdapter extends PagerAdapter {

		/**
		 * 图片视图缓存列表
		 */
		private ArrayList<ImageView> mImageViewCacheList;

		/**
		 * 图片资源列表
		 */
		private ArrayList<ADInfo> mAdList = new ArrayList<ADInfo>();

		/**
		 * 广告图片点击监听�?
		 */
		private ImageCycleViewListener mImageCycleViewListener;

		private Context mContext;

		public ImageCycleAdapter(Context context, ArrayList<ADInfo> adList,
				ImageCycleViewListener imageCycleViewListener) {
			mContext = context;
			mAdList = adList;
			mImageCycleViewListener = imageCycleViewListener;
			mImageViewCacheList = new ArrayList<ImageView>();
		}

		@Override
		public int getCount() {
			return mAdList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			String imageUrl = mAdList.get(position).getPic();
			ImageView imageView = null;
			if (mImageViewCacheList.isEmpty()) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);

			} else {
				imageView = mImageViewCacheList.remove(0);
			}
			// 设置图片点击监听
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mImageCycleViewListener.onImageClick(mAdList.get(position),
							position, v);
				}
			});
			imageView.setTag(imageUrl);
			container.addView(imageView);
			mImageCycleViewListener.displayImageWM(imageUrl, imageView);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			ImageView view = (ImageView) object;
			container.removeView(view);
			mImageViewCacheList.add(view);
		}

	}

	/**
	 * 轮播控件的监听事�?
	 * 
	 * @author minking
	 */
	public static interface ImageCycleViewListener {

		/**
		 * 加载图片资源
		 * 
		 * @param imageURL
		 * @param imageView
		 */
		public void displayImageWM(String imageURL, ImageView imageView);

		/**
		 * 单击图片事件
		 * 
		 * @param position
		 * @param imageView
		 */
		public void onImageClick(ADInfo info, int postion, View imageView);

	}

}
