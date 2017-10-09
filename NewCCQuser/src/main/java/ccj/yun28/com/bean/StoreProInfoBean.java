package ccj.yun28.com.bean;

/**
 * 餐餐抢店铺详情里的商品信息
 * 
 * @author meihuali
 * 
 */
public class StoreProInfoBean {

	private String goods_id;
	private String store_id;
	private String goods_name;
	private String goods_price;
	private String goods_image;
	private String discount;

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	public String getStore_id() {
		return store_id;
	}

	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getGoods_price() {
		return goods_price;
	}

	public void setGoods_price(String goods_price) {
		this.goods_price = goods_price;
	}

	public String getGoods_image() {
		return goods_image;
	}

	public void setGoods_image(String goods_image) {
		this.goods_image = goods_image;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	@Override
	public String toString() {
		return "StoreProInfoBean [goods_id=" + goods_id + ", store_id="
				+ store_id + ", goods_name=" + goods_name + ", goods_price="
				+ goods_price + ", goods_image=" + goods_image + ", discount="
				+ discount + "]";
	}

}
