package ccj.yun28.com.bean.ccq;


/**
 * 餐餐抢城市信息
 * 
 * @author meihuali
 * 
 */
public class QuInfobean {

	private String area_id;
	private String area_name;

	public String getArea_id() {
		return area_id;
	}

	public void setArea_id(String area_id) {
		this.area_id = area_id;
	}

	public String getArea_name() {
		return area_name;
	}

	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}

	@Override
	public String toString() {
		return "QuInfobean [area_id=" + area_id + ", area_name=" + area_name
				+ "]";
	}

}
