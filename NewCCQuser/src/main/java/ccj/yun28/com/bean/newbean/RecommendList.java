package ccj.yun28.com.bean.newbean;

/**
 * 推荐商品列表
 *
 * @author wuqiuyun
 */
public class RecommendList {

	private String goods_id;
	private String store_id;
	private String goods_name;
	private String goods_addtime;
	private String goods_click;
	private String evaluation_good_star;
	private String goods_marketprice;
	private String goods_price;
	private String goods_image;
	private String goods_storage;
	private String goods_salenum;
	private String remark;
	private String discount;
	private String store_name;
	private String store_address;
	private String longitude;
	private String latitude;
	private String distance_value;
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
	public String getGoods_addtime() {
		return goods_addtime;
	}
	public void setGoods_addtime(String goods_addtime) {
		this.goods_addtime = goods_addtime;
	}
	public String getGoods_click() {
		return goods_click;
	}
	public void setGoods_click(String goods_click) {
		this.goods_click = goods_click;
	}
	public String getEvaluation_good_star() {
		return evaluation_good_star;
	}
	public void setEvaluation_good_star(String evaluation_good_star) {
		this.evaluation_good_star = evaluation_good_star;
	}
	public String getGoods_marketprice() {
		return goods_marketprice;
	}
	public void setGoods_marketprice(String goods_marketprice) {
		this.goods_marketprice = goods_marketprice;
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
	public String getGoods_storage() {
		return goods_storage;
	}
	public void setGoods_storage(String goods_storage) {
		this.goods_storage = goods_storage;
	}
	public String getGoods_salenum() {
		return goods_salenum;
	}
	public void setGoods_salenum(String goods_salenum) {
		this.goods_salenum = goods_salenum;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getStore_name() {
		return store_name;
	}
	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}
	public String getStore_address() {
		return store_address;
	}
	public void setStore_address(String store_address) {
		this.store_address = store_address;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getDistance_value() {
		return distance_value;
	}
	public void setDistance_value(String distance_value) {
		this.distance_value = distance_value;
	}
	@Override
	public String toString() {
		return "RecommendList [goods_id=" + goods_id + ", store_id=" + store_id
				+ ", goods_name=" + goods_name + ", goods_addtime="
				+ goods_addtime + ", goods_click=" + goods_click
				+ ", evaluation_good_star=" + evaluation_good_star
				+ ", goods_marketprice=" + goods_marketprice + ", goods_price="
				+ goods_price + ", goods_image=" + goods_image
				+ ", goods_storage=" + goods_storage + ", goods_salenum="
				+ goods_salenum + ", remark=" + remark + ", discount="
				+ discount + ", store_name=" + store_name + ", store_address="
				+ store_address + ", longitude=" + longitude + ", latitude="
				+ latitude + ", distance_value=" + distance_value + "]";
	}
}
