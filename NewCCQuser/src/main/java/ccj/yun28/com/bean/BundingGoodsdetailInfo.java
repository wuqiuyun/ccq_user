package ccj.yun28.com.bean;

import java.util.List;

/**
 * 餐餐抢折扣券详情-套餐商品信息
 * 
 * @author meihuali
 * 
 */
public class BundingGoodsdetailInfo {
	private String goods_costprice;
	private String goods_name;
	private String quantity;
	public String getGoods_costprice() {
		return goods_costprice;
	}
	public void setGoods_costprice(String goods_costprice) {
		this.goods_costprice = goods_costprice;
	}
	public String getGoods_name() {
		return goods_name;
	}
	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	@Override
	public String toString() {
		return "BundingGoodsdetailInfo [goods_costprice=" + goods_costprice
				+ ", goods_name=" + goods_name + ", quantity=" + quantity + "]";
	}
	
	

}
