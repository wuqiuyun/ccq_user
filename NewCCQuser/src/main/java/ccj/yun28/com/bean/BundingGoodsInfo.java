package ccj.yun28.com.bean;

import java.util.List;

/**
 * 餐餐抢折扣券详情-套餐商品信息
 * 
 * @author meihuali
 * 
 */
public class BundingGoodsInfo {
	private String name;
	private List<BundingGoodsdetailInfo> list;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<BundingGoodsdetailInfo> getList() {
		return list;
	}
	public void setList(List<BundingGoodsdetailInfo> list) {
		this.list = list;
	}
	@Override
	public String toString() {
		return "BundingGoodsInfo [name=" + name + ", list=" + list + "]";
	}
	
	

}
