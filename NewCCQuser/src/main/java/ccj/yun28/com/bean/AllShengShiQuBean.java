package ccj.yun28.com.bean;

import java.util.List;

/**
 * 所有地址 - 省
 * @author meihuali
 *
 */
public class AllShengShiQuBean {
	private String area_id;
	private String area_name;
	private String area_parent_id;
	private List<AllShiQuBean> class2;
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
	public String getArea_parent_id() {
		return area_parent_id;
	}
	public void setArea_parent_id(String area_parent_id) {
		this.area_parent_id = area_parent_id;
	}
	public List<AllShiQuBean> getClass2() {
		return class2;
	}
	public void setClass2(List<AllShiQuBean> class2) {
		this.class2 = class2;
	}
	@Override
	public String toString() {
		return "AllShengShiQuBean [area_id=" + area_id + ", area_name="
				+ area_name + ", area_parent_id=" + area_parent_id
				+ ", class2=" + class2 + "]";
	}
	
}
