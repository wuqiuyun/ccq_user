package ccj.yun28.com.bean;

import java.util.List;

/**
 * 我- 订单-线上商品结算
 * @author meihuali
 *
 */
public class MineXSBuyZkjBean {
	private ADDRESS_INFO address_info;
	private String freight_hash;
	private String goods_amount;
	private String goods_freight;
	private String goods_freight_name;
	private String ifshow_offpay;
	private INV_INFO inv_info;
	private String order_amount;
	private String order_message;
	private List<STORE_CART_LIST> store_cart_list;
	private PayInfoBean to_pay;
	public ADDRESS_INFO getAddress_info() {
		return address_info;
	}
	public void setAddress_info(ADDRESS_INFO address_info) {
		this.address_info = address_info;
	}
	public String getFreight_hash() {
		return freight_hash;
	}
	public void setFreight_hash(String freight_hash) {
		this.freight_hash = freight_hash;
	}
	public String getGoods_amount() {
		return goods_amount;
	}
	public void setGoods_amount(String goods_amount) {
		this.goods_amount = goods_amount;
	}
	public String getGoods_freight() {
		return goods_freight;
	}
	public void setGoods_freight(String goods_freight) {
		this.goods_freight = goods_freight;
	}
	public String getGoods_freight_name() {
		return goods_freight_name;
	}
	public void setGoods_freight_name(String goods_freight_name) {
		this.goods_freight_name = goods_freight_name;
	}
	public String getIfshow_offpay() {
		return ifshow_offpay;
	}
	public void setIfshow_offpay(String ifshow_offpay) {
		this.ifshow_offpay = ifshow_offpay;
	}
	public INV_INFO getInv_info() {
		return inv_info;
	}
	public void setInv_info(INV_INFO inv_info) {
		this.inv_info = inv_info;
	}
	public String getOrder_amount() {
		return order_amount;
	}
	public void setOrder_amount(String order_amount) {
		this.order_amount = order_amount;
	}
	public String getOrder_message() {
		return order_message;
	}
	public void setOrder_message(String order_message) {
		this.order_message = order_message;
	}
	public List<STORE_CART_LIST> getStore_cart_list() {
		return store_cart_list;
	}
	public void setStore_cart_list(List<STORE_CART_LIST> store_cart_list) {
		this.store_cart_list = store_cart_list;
	}
	public PayInfoBean getTo_pay() {
		return to_pay;
	}
	public void setTo_pay(PayInfoBean to_pay) {
		this.to_pay = to_pay;
	}
	@Override
	public String toString() {
		return "MineXSBuyZkjBean [address_info=" + address_info
				+ ", freight_hash=" + freight_hash + ", goods_amount="
				+ goods_amount + ", goods_freight=" + goods_freight
				+ ", goods_freight_name=" + goods_freight_name
				+ ", ifshow_offpay=" + ifshow_offpay + ", inv_info=" + inv_info
				+ ", order_amount=" + order_amount + ", order_message="
				+ order_message + ", store_cart_list=" + store_cart_list
				+ ", to_pay=" + to_pay + "]";
	}

}
