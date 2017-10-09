package ccj.yun28.com.bean;

import java.util.List;

/**
 * 餐餐抢店铺详情
 * 
 * @author meihuali
 * 
 */
public class CcqStoreBean {

	private String evaluate_sum;
	private String latitude;
	private String live_store_tel;
	private String longitude;
	private String seller_name;
	private String store_address;
	private String store_credit;
	private String store_name;
	private String union_img;
	private String union_pay;
	private String union_pay_discount;
	private List<StoreProInfoBean> goods_list;

	public String getEvaluate_sum() {
		return evaluate_sum;
	}

	public void setEvaluate_sum(String evaluate_sum) {
		this.evaluate_sum = evaluate_sum;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLive_store_tel() {
		return live_store_tel;
	}

	public void setLive_store_tel(String live_store_tel) {
		this.live_store_tel = live_store_tel;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getSeller_name() {
		return seller_name;
	}

	public void setSeller_name(String seller_name) {
		this.seller_name = seller_name;
	}

	public String getStore_address() {
		return store_address;
	}

	public void setStore_address(String store_address) {
		this.store_address = store_address;
	}

	public String getStore_credit() {
		return store_credit;
	}

	public void setStore_credit(String store_credit) {
		this.store_credit = store_credit;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public String getUnion_img() {
		return union_img;
	}

	public void setUnion_img(String union_img) {
		this.union_img = union_img;
	}

	public String getUnion_pay() {
		return union_pay;
	}

	public void setUnion_pay(String union_pay) {
		this.union_pay = union_pay;
	}

	public String getUnion_pay_discount() {
		return union_pay_discount;
	}

	public void setUnion_pay_discount(String union_pay_discount) {
		this.union_pay_discount = union_pay_discount;
	}

	public List<StoreProInfoBean> getGoods_list() {
		return goods_list;
	}

	public void setGoods_list(List<StoreProInfoBean> goods_list) {
		this.goods_list = goods_list;
	}

	@Override
	public String toString() {
		return "CcqStoreBean [evaluate_sum=" + evaluate_sum + ", latitude="
				+ latitude + ", live_store_tel=" + live_store_tel
				+ ", longitude=" + longitude + ", seller_name=" + seller_name
				+ ", store_address=" + store_address + ", store_credit="
				+ store_credit + ", store_name=" + store_name + ", union_img="
				+ union_img + ", union_pay=" + union_pay
				+ ", union_pay_discount=" + union_pay_discount
				+ ", goods_list=" + goods_list + "]";
	}

}
