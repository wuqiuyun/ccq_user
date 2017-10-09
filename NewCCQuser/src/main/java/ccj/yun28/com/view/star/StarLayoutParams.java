package ccj.yun28.com.view.star;

import android.graphics.drawable.Drawable;

/**
 * 星星构造参数
 * @author thduan
 * @date 2016-1-12 下午3:21:07
 */
public class StarLayoutParams{

	/** 默认选择的星星数为3 */
	public static final int DEF_SELECTED_STAR_NUM = 3;
	/** 星星总数 */
	public static final int DEF_TOTAL_STAR_NUM = 5;
	/** 星星之间的默认间距 */
	public static final int DEF_STAR_HORIZONTAL_SPACE = 10;
	/** 未被选中的星星图片 */
	private Drawable normalStar;
	/** 被选中的星星图片 */
	private Drawable selectedStar;
	/** 星星控件是否可点击选择 */
	private boolean isSelectable;
	private int selectedStarNum = DEF_SELECTED_STAR_NUM;
	private int totalStarNum = DEF_TOTAL_STAR_NUM;
	private int starHorizontalSpace = DEF_STAR_HORIZONTAL_SPACE;
	
	public Drawable getNormalStar() {
		return normalStar;
	}
	public StarLayoutParams setNormalStar(Drawable normalStar) {
		this.normalStar = normalStar;
		return this;
	}
	public Drawable getSelectedStar() {
		return selectedStar;
	}
	public StarLayoutParams setSelectedStar(Drawable selectedStar) {
		this.selectedStar = selectedStar;
		return this;
	}
	public boolean isSelectable() {
		return isSelectable;
	}
	public StarLayoutParams setSelectable(boolean isSelectable) {
		this.isSelectable = isSelectable;
		return this;
	}
	public int getSelectedStarNum() {
		return selectedStarNum;
	}
	public StarLayoutParams setSelectedStarNum(int selectedStarCnt) {
		this.selectedStarNum = selectedStarCnt;
		return this;
	}
	public int getTotalStarNum() {
		return totalStarNum;
	}
	public StarLayoutParams setTotalStarNum(int totalStarCnt) {
		this.totalStarNum = totalStarCnt;
		return this;
	}
	public int getStarHorizontalSpace() {
		return starHorizontalSpace;
	}
	
	public StarLayoutParams setStarHorizontalSpace(int starHorizontalSpace) {
		this.starHorizontalSpace = starHorizontalSpace;
		return this;
	}
	
}
