package ccj.yun28.com.bean;

import java.util.List;

/**
 * 订单详情 - 商品信息
 * 
 * @author meihuali
 * 
 */
public class STORE_CART_LIST {

	private String freight;
	private List<GOODS_LIST> goods_list;
	private String store_goods_total;
	private String store_name;

	public String getFreight() {
		return freight;
	}

	public void setFreight(String freight) {
		this.freight = freight;
	}

	public List<GOODS_LIST> getGoods_list() {
		return goods_list;
	}

	public void setGoods_list(List<GOODS_LIST> goods_list) {
		this.goods_list = goods_list;
	}

	public String getStore_goods_total() {
		return store_goods_total;
	}

	public void setStore_goods_total(String store_goods_total) {
		this.store_goods_total = store_goods_total;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	@Override
	public String toString() {
		return "STORE_CART_LIST [freight=" + freight + ", goods_list="
				+ goods_list + ", store_goods_total=" + store_goods_total
				+ ", store_name=" + store_name + "]";
	}

}
