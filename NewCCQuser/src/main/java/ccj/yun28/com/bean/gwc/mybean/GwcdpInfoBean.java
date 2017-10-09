package ccj.yun28.com.bean.gwc.mybean;

import java.util.List;

/**
 * 购物车
 * 
 * @author meihuali
 * 
 */
public class GwcdpInfoBean extends GwcBaseInfo {

	protected String name;
	protected String id;
	private List<GwcspInfoBean> shop;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<GwcspInfoBean> getShop() {
		return shop;
	}

	public void setShop(List<GwcspInfoBean> shop) {
		this.shop = shop;
	}

	@Override
	public String toString() {
		return "GwcdpInfoBean [name=" + name + ", id=" + id + ", shop=" + shop
				+ "]";
	}

}
