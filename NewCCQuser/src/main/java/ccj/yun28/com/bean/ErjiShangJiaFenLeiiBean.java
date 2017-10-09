package ccj.yun28.com.bean;

/**
 * /** 商家分类 -- 二级
 * 
 * @author meihuali
 * 
 */
public class ErjiShangJiaFenLeiiBean {
	private String ccq;
	private String gc_id;
	private String gc_img;
	private String gc_name;
	private String gc_parent_id;
	private String gc_show;

	public String getCcq() {
		return ccq;
	}

	public void setCcq(String ccq) {
		this.ccq = ccq;
	}

	public String getGc_id() {
		return gc_id;
	}

	public void setGc_id(String gc_id) {
		this.gc_id = gc_id;
	}

	public String getGc_img() {
		return gc_img;
	}

	public void setGc_img(String gc_img) {
		this.gc_img = gc_img;
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

	@Override
	public String toString() {
		return "ErjiShangJiaFenLeiiBean [ccq=" + ccq + ", gc_id=" + gc_id
				+ ", gc_img=" + gc_img + ", gc_name=" + gc_name
				+ ", gc_parent_id=" + gc_parent_id + ", gc_show=" + gc_show
				+ "]";
	}

}
