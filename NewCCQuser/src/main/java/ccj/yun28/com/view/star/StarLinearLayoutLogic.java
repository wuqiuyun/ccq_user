package ccj.yun28.com.view.star;

/**
 * 评价星星逻辑控制
 * @author thduan
 * @date 2016-1-12 下午4:37:41
 */
public class StarLinearLayoutLogic {
	/** 当前已选择的星星数 */
	private int curStarNum;
	private StarLayoutParams mParams;
	private IStarSelectedLis starSelectedLi;
	
	public interface IStarSelectedLis {
		void onSelected(int curStarNum);
	}
	
	public StarLinearLayoutLogic(IStarSelectedLis lis) {
		starSelectedLi = lis;
	}
	
	public void setParams(StarLayoutParams params) {
		mParams = params;
		curStarNum = params.getSelectedStarNum();
	}
	
	public float getCurStarNum() {
		return curStarNum;
	}
	
	/**
	 * 返回当前选中的星星数给上层
	 */
	public void onStarClick(int clickPosition) {
		curStarNum = clickPosition + 1;
		if(starSelectedLi != null) {
			starSelectedLi.onSelected(curStarNum);
		}
	}

}
