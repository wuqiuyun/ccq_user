package ccj.yun28.com.view.star;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import ccj.yun28.com.view.star.StarLinearLayoutLogic.IStarSelectedLis;
import ccj.yun28.com.view.star.drawable.DrawableAttrs;
import ccj.yun28.com.view.star.drawable.DrawableTools;

/**
 * 评分星星控件
 * 
 * @author thduan
 * @date 2016-1-12 下午4:58:42
 */
public class StarLinearLayout extends LinearLayout {
	private Context mContext;
	private StarLayoutParams mParams;
	private List<ImageView> stars;
	private StarLinearLayoutLogic logic;

	public StarLinearLayout(Context context) {
		this(context, null);
	}

	public StarLinearLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StarLinearLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		init();
	}

	private void init() {
		logic = new StarLinearLayoutLogic(getStarSelectedLis());
		stars = new ArrayList<ImageView>();
	}

	public void setStarParams(StarLayoutParams params) {
		mParams = params;
		logic.setParams(params);

		updateStars();
		invalidate();
	}

	private void updateStars() {

		if (mParams == null)
			return;

		if (stars != null) {

			stars.clear();

			removeAllViews();

		}

//		int newStarNum = mParams.isSelectable() ? mParams.getTotalStarNum()
//				: mParams.getSelectedStarNum();
		
		int newStarNum = mParams.isSelectable() ? mParams.getTotalStarNum()
				: mParams.getTotalStarNum();

		for (int i = 0; i < newStarNum; i++) {

			ImageView imageView = createStar(i);

			stars.add(imageView);

			addView(imageView);

		}

	}

	/**
	 * 创建星星imageview
	 * 
	 * @param pos
	 * @return
	 */
	private ImageView createStar(final int pos) {
		ImageView imageView = new ImageView(mContext);

		DrawableTools.setImageDrawableWithStateList(imageView,

		new DrawableAttrs()

		.setNormalDrawable(mParams.getNormalStar())

		.setSelectedDrawable(mParams.getSelectedStar()));

		if (mParams.isSelectable()) { // 可点击

			imageView.setEnabled(true);

			if (pos < mParams.getSelectedStarNum()) { // 已选中的星星

				imageView.setSelected(true);

			} else { // 未被选中的星星

				imageView.setSelected(false);

			}

		} else { // 不可点击，仅展示

			imageView.setEnabled(false);

//			imageView.setSelected(true);
			if (pos < mParams.getSelectedStarNum()) { // 已选中的星星

				imageView.setSelected(true);

			} else { // 未被选中的星星

				imageView.setSelected(false);

			}

		}

		imageView.setPadding(mParams.getStarHorizontalSpace() / 2, 0,
				mParams.getStarHorizontalSpace() / 2, 0);

		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				logic.onStarClick(pos);

			}

		});

		return imageView;

	}

	/**
	 * 星星选择数变化后的UI处理
	 * 
	 * @return
	 */
	private IStarSelectedLis getStarSelectedLis() {
		return new IStarSelectedLis() {

			@Override
			public void onSelected(int curStarNum) {
				for (int i = 0; i < mParams.getTotalStarNum(); i++) {
					if (i < curStarNum) {
						stars.get(i).setSelected(true);
					} else {
						stars.get(i).setSelected(false);
					}
				}
			}
		};
	}

	public StarLinearLayoutLogic getLogic() {
		return logic;
	}

}