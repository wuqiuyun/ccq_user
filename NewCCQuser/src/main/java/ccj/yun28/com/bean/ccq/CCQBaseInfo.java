package ccj.yun28.com.bean.ccq;

import java.util.List;


/**
 * 餐餐抢全部分类，城市
 * 
 * @author meihuali
 * 
 */
public class CCQBaseInfo {
	private List<QuInfobean> area;
	private List<AllFenleibean> classify;

	public List<QuInfobean> getArea() {
		return area;
	}

	public void setArea(List<QuInfobean> area) {
		this.area = area;
	}

	public List<AllFenleibean> getClassify() {
		return classify;
	}

	public void setClassify(List<AllFenleibean> classify) {
		this.classify = classify;
	}

	@Override
	public String toString() {
		return "CCQBaseInfo [area=" + area + ", classify=" + classify + "]";
	}

}
