package ccj.yun28.com.bean;

import java.util.List;

/**
 * 所有地址 - 市
 * @author meihuali
 *
 */
public class AllShiQuBean {
	private String area_id;
	private String area_name;
	private String area_parent_id;
	private List<AllQuBean> class3;
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
	public List<AllQuBean> getClass3() {
		return class3;
	}
	public void setClass3(List<AllQuBean> class3) {
		this.class3 = class3;
	}
	@Override
	public String toString() {
		return "AllShiQuBean [area_id=" + area_id + ", area_name=" + area_name
				+ ", area_parent_id=" + area_parent_id + ", class3=" + class3
				+ "]";
	}
	
}
