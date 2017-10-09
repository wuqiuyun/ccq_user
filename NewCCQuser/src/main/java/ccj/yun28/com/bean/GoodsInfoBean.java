package ccj.yun28.com.bean;

import java.util.List;

/**
 * 5.商品详情-商品信息
 * 
 * @author meihuali
 * 
 */
public class GoodsInfoBean {

	private String color_id;
	private String evaluation_count;
	private String evaluation_good_star;
	private String gc_id;
	private String gc_id_1;
	private String gc_id_2;
	private String gc_id_3;
	private String goods_click;
	private String goods_collect;
	private String goods_commonid;
	private String goods_freight;
	private String goods_id;
	private String goods_marketprice;
	private String goods_name;
	private String goods_price;
	private String goods_promotion_price;
	private String goods_promotion_type;
	private String goods_salenum;
	private List<ProductDQGuizeBean> goods_spec;
	private String goods_storage;
	private List<ProductGuizeNameBean> spec_name;
	private List<List<ProductGuiZeBean>> spec_value;
	private String store_id;
	private String store_name;
	private String transport_id;
	private String vip;

	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	public String getColor_id() {
		return color_id;
	}

	public void setColor_id(String color_id) {
		this.color_id = color_id;
	}

	public String getEvaluation_count() {
		return evaluation_count;
	}

	public void setEvaluation_count(String evaluation_count) {
		this.evaluation_count = evaluation_count;
	}

	public String getEvaluation_good_star() {
		return evaluation_good_star;
	}

	public void setEvaluation_good_star(String evaluation_good_star) {
		this.evaluation_good_star = evaluation_good_star;
	}

	public String getGc_id() {
		return gc_id;
	}

	public void setGc_id(String gc_id) {
		this.gc_id = gc_id;
	}

	public String getGc_id_1() {
		return gc_id_1;
	}

	public void setGc_id_1(String gc_id_1) {
		this.gc_id_1 = gc_id_1;
	}

	public String getGc_id_2() {
		return gc_id_2;
	}

	public void setGc_id_2(String gc_id_2) {
		this.gc_id_2 = gc_id_2;
	}

	public String getGc_id_3() {
		return gc_id_3;
	}

	public void setGc_id_3(String gc_id_3) {
		this.gc_id_3 = gc_id_3;
	}

	public String getGoods_click() {
		return goods_click;
	}

	public void setGoods_click(String goods_click) {
		this.goods_click = goods_click;
	}

	public String getGoods_collect() {
		return goods_collect;
	}

	public void setGoods_collect(String goods_collect) {
		this.goods_collect = goods_collect;
	}

	public String getGoods_commonid() {
		return goods_commonid;
	}

	public void setGoods_commonid(String goods_commonid) {
		this.goods_commonid = goods_commonid;
	}

	public String getGoods_freight() {
		return goods_freight;
	}

	public void setGoods_freight(String goods_freight) {
		this.goods_freight = goods_freight;
	}

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	public String getGoods_marketprice() {
		return goods_marketprice;
	}

	public void setGoods_marketprice(String goods_marketprice) {
		this.goods_marketprice = goods_marketprice;
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

	public String getGoods_promotion_price() {
		return goods_promotion_price;
	}

	public void setGoods_promotion_price(String goods_promotion_price) {
		this.goods_promotion_price = goods_promotion_price;
	}

	public String getGoods_promotion_type() {
		return goods_promotion_type;
	}

	public void setGoods_promotion_type(String goods_promotion_type) {
		this.goods_promotion_type = goods_promotion_type;
	}

	public String getGoods_salenum() {
		return goods_salenum;
	}

	public void setGoods_salenum(String goods_salenum) {
		this.goods_salenum = goods_salenum;
	}

	public List<ProductDQGuizeBean> getGoods_spec() {
		return goods_spec;
	}

	public void setGoods_spec(List<ProductDQGuizeBean> goods_spec) {
		this.goods_spec = goods_spec;
	}

	public String getGoods_storage() {
		return goods_storage;
	}

	public void setGoods_storage(String goods_storage) {
		this.goods_storage = goods_storage;
	}

	public List<ProductGuizeNameBean> getSpec_name() {
		return spec_name;
	}

	public void setSpec_name(List<ProductGuizeNameBean> spec_name) {
		this.spec_name = spec_name;
	}

	public List<List<ProductGuiZeBean>> getSpec_value() {
		return spec_value;
	}

	public void setSpec_value(List<List<ProductGuiZeBean>> spec_value) {
		this.spec_value = spec_value;
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

	public String getTransport_id() {
		return transport_id;
	}

	public void setTransport_id(String transport_id) {
		this.transport_id = transport_id;
	}

	@Override
	public String toString() {
		return "GoodsInfoBean [color_id=" + color_id + ", evaluation_count="
				+ evaluation_count + ", evaluation_good_star="
				+ evaluation_good_star + ", gc_id=" + gc_id + ", gc_id_1="
				+ gc_id_1 + ", gc_id_2=" + gc_id_2 + ", gc_id_3=" + gc_id_3
				+ ", goods_click=" + goods_click + ", goods_collect="
				+ goods_collect + ", goods_commonid=" + goods_commonid
				+ ", goods_freight=" + goods_freight + ", goods_id=" + goods_id
				+ ", goods_marketprice=" + goods_marketprice + ", goods_name="
				+ goods_name + ", goods_price=" + goods_price
				+ ", goods_promotion_price=" + goods_promotion_price
				+ ", goods_promotion_type=" + goods_promotion_type
				+ ", goods_salenum=" + goods_salenum + ", goods_spec="
				+ goods_spec + ", goods_storage=" + goods_storage
				+ ", spec_name=" + spec_name + ", spec_value=" + spec_value
				+ ", store_id=" + store_id + ", store_name=" + store_name
				+ ", transport_id=" + transport_id + "]";
	}

}
