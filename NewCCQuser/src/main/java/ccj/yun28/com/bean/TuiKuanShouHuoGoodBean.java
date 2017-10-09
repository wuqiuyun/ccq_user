package ccj.yun28.com.bean;
/**
 * 退款售后商品详情
 * @author meihuali
 *
 */
public class TuiKuanShouHuoGoodBean {

	private String goods_id;
	private String goods_image;
	private String goods_name;
	private String goods_num;
	private String goods_price;
	private String goods_spec;
	private String rec_id;
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
	public String getGoods_num() {
		return goods_num;
	}
	public void setGoods_num(String goods_num) {
		this.goods_num = goods_num;
	}
	public String getGoods_price() {
		return goods_price;
	}
	public void setGoods_price(String goods_price) {
		this.goods_price = goods_price;
	}
	public String getGoods_spec() {
		return goods_spec;
	}
	public void setGoods_spec(String goods_spec) {
		this.goods_spec = goods_spec;
	}
	public String getRec_id() {
		return rec_id;
	}
	public void setRec_id(String rec_id) {
		this.rec_id = rec_id;
	}
	@Override
	public String toString() {
		return "TuiKuanShouHuoGoodBean [goods_id=" + goods_id
				+ ", goods_image=" + goods_image + ", goods_name=" + goods_name
				+ ", goods_num=" + goods_num + ", goods_price=" + goods_price
				+ ", goods_spec=" + goods_spec + ", rec_id=" + rec_id + "]";
	}
	
	
}
