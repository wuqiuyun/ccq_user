package ccj.yun28.com.bean;

import java.util.List;

/**
 * 餐餐抢商品结算
 * 
 * @author meihuali
 * 
 */
public class BuyZkjBean {

	private ADDRESS_INFO address_info;
	private String current_balance;
	private String current_merchants;
	private String current_point;
	private String current_promote;
	private String freight_hash;
	private String goods_amount;
	private String goods_freight;
	private String goods_freight_name;
	private String ifshow_offpay;
	private INV_INFO inv_info;
	private String order_amount;
	private List<STORE_CART_LIST> store_cart_list;
	private MEMBER_INFO member_info;
	public MEMBER_INFO getMember_info() {
		return member_info;
	}

	public void setMember_info(MEMBER_INFO member_info) {
		this.member_info = member_info;
	}

	private STORE_INFO store_info;

	public ADDRESS_INFO getAddress_info() {
		return address_info;
	}

	public void setAddress_info(ADDRESS_INFO address_info) {
		this.address_info = address_info;
	}

	public String getCurrent_balance() {
		return current_balance;
	}

	public void setCurrent_balance(String current_balance) {
		this.current_balance = current_balance;
	}

	public String getCurrent_merchants() {
		return current_merchants;
	}

	public void setCurrent_merchants(String current_merchants) {
		this.current_merchants = current_merchants;
	}

	public String getCurrent_point() {
		return current_point;
	}

	public void setCurrent_point(String current_point) {
		this.current_point = current_point;
	}

	public String getCurrent_promote() {
		return current_promote;
	}

	public void setCurrent_promote(String current_promote) {
		this.current_promote = current_promote;
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

	public List<STORE_CART_LIST> getStore_cart_list() {
		return store_cart_list;
	}

	public void setStore_cart_list(List<STORE_CART_LIST> store_cart_list) {
		this.store_cart_list = store_cart_list;
	}

	public STORE_INFO getStore_info() {
		return store_info;
	}

	public void setStore_info(STORE_INFO store_info) {
		this.store_info = store_info;
	}

	@Override
	public String toString() {
		return "BuyZkjBean [address_info=" + address_info
				+ ", current_balance=" + current_balance
				+ ", current_merchants=" + current_merchants
				+ ", current_point=" + current_point + ", current_promote="
				+ current_promote + ", freight_hash=" + freight_hash
				+ ", goods_amount=" + goods_amount + ", goods_freight="
				+ goods_freight + ", goods_freight_name=" + goods_freight_name
				+ ", ifshow_offpay=" + ifshow_offpay + ", inv_info=" + inv_info
				+ ", order_amount=" + order_amount + ", store_cart_list="
				+ store_cart_list + ", store_info=" + store_info + "]";
	}
}
