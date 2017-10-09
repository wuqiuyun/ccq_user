package ccj.yun28.com.bean;

import java.util.List;

public class FenleiBean {

	private String deep;
	private String gc_id;
	private String gc_name;
	private String gc_parent_id;
	private String gc_show;
	private String gc_sort;
	private String type_id;
	private List<ErjiFenleiBean> class2;

	public String getDeep() {
		return deep;
	}

	public void setDeep(String deep) {
		this.deep = deep;
	}

	public String getGc_id() {
		return gc_id;
	}

	public void setGc_id(String gc_id) {
		this.gc_id = gc_id;
	}

	public String getGc_name() {
		return gc_name;
	}

	public void setGc_name(String gc_name) {
		this.gc_name = gc_name;
	}

	public String getGc_parent_id() {
		return gc_parent_id;
	}

	public void setGc_parent_id(String gc_parent_id) {
		this.gc_parent_id = gc_parent_id;
	}

	public String getGc_show() {
		return gc_show;
	}

	public void setGc_show(String gc_show) {
		this.gc_show = gc_show;
	}

	public String getGc_sort() {
		return gc_sort;
	}

	public void setGc_sort(String gc_sort) {
		this.gc_sort = gc_sort;
	}

	public String getType_id() {
		return type_id;
	}

	public void setType_id(String type_id) {
		this.type_id = type_id;
	}

	public List<ErjiFenleiBean> getClass2() {
		return class2;
	}

	public void setClass2(List<ErjiFenleiBean> class2) {
		this.class2 = class2;
	}

	@Override
	public String toString() {
		return "FenleiBean [deep=" + deep + ", gc_id=" + gc_id + ", gc_name="
				+ gc_name + ", gc_parent_id=" + gc_parent_id + ", gc_show="
				+ gc_show + ", gc_sort=" + gc_sort + ", type_id=" + type_id
				+ ", class2=" + class2 + "]";
	}

}
