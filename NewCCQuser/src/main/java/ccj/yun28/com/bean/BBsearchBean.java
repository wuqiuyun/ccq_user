package ccj.yun28.com.bean;

/**
 * 搜索-宝贝
 * 
 * @author meihuali
 * 
 */
public class BBsearchBean {
	private String goods_favorites_num;
	private String goods_id;
	private String goods_image;
	private String goods_name;
	private String goods_price;
	private String store_id;
	private String store_name;

	public String getGoods_favorites_num() {
		return goods_favorites_num;
	}

	public void setGoods_favorites_num(String goods_favorites_num) {
		this.goods_favorites_num = goods_favorites_num;
	}

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	public String getGoods_image() {
		return goods_image;
	}

	public void setGoods_image(String goods_image) {
		this.goods_image = goods_image;
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

	public String getStore_id() {
		return store_id;
	}

	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	@Override
	public String toString() {
		return "BBsearchBean [goods_favorites_num=" + goods_favorites_num
				+ ", goods_id=" + goods_id + ", goods_image=" + goods_image
				+ ", goods_name=" + goods_name + ", goods_price=" + goods_price
				+ ", store_id=" + store_id + ", store_name=" + store_name + "]";
	}

}
