package ccj.yun28.com.bean;

import java.util.List;

/**
 * 餐餐抢商品信息
 * 
 * @author meihuali
 * 
 */
public class CCQProInfoBean {
//	private List<BundingGoodsInfo> bunding_goods_info;
	private String geval_goods_num;
	private List<BundingGoodsInfo> goods_bunding_group;
	private GoodsBundingInfo goods_bunding_info;
	private GoodsBundingInfoNew goods_bunding_info_new;
	private String goods_teat;
	// private List<String> goods_evaluate;//为空
	private List<String> goods_image_mobile;
	private GoodsInfo goods_info;
	// private List<String> spec_list;//为空
	private StoreInfo store_info;
	public List<BundingGoodsInfo> getGoods_bunding_group() {
		return goods_bunding_group;
	}
	public void setGoods_bunding_group(List<BundingGoodsInfo> goods_bunding_group) {
		this.goods_bunding_group = goods_bunding_group;
	}
	public String getGeval_goods_num() {
		return geval_goods_num;
	}
	public void setGeval_goods_num(String geval_goods_num) {
		this.geval_goods_num = geval_goods_num;
	}
	public GoodsBundingInfo getGoods_bunding_info() {
		return goods_bunding_info;
	}
	public void setGoods_bunding_info(GoodsBundingInfo goods_bunding_info) {
		this.goods_bunding_info = goods_bunding_info;
	}
	public GoodsBundingInfoNew getGoods_bunding_info_new() {
		return goods_bunding_info_new;
	}
	public void setGoods_bunding_info_new(GoodsBundingInfoNew goods_bunding_info_new) {
		this.goods_bunding_info_new = goods_bunding_info_new;
	}
	public List<String> getGoods_image_mobile() {
		return goods_image_mobile;
	}
	public void setGoods_image_mobile(List<String> goods_image_mobile) {
		this.goods_image_mobile = goods_image_mobile;
	}
	public GoodsInfo getGoods_info() {
		return goods_info;
	}
	public void setGoods_info(GoodsInfo goods_info) {
		this.goods_info = goods_info;
	}
	public StoreInfo getStore_info() {
		return store_info;
	}
	public void setStore_info(StoreInfo store_info) {
		this.store_info = store_info;
	}
	public String getGoods_teat() {
		return goods_teat;
	}
	public void setGoods_teat(String goods_teat) {
		this.goods_teat = goods_teat;
	}
	@Override
	public String toString() {
		return "CCQProInfoBean [geval_goods_num=" + geval_goods_num
				+ ", goods_bunding_group=" + goods_bunding_group
				+ ", goods_bunding_info=" + goods_bunding_info
				+ ", goods_bunding_info_new=" + goods_bunding_info_new
				+ ", goods_teat=" + goods_teat + ", goods_image_mobile="
				+ goods_image_mobile + ", goods_info=" + goods_info
				+ ", store_info=" + store_info + "]";
	}
	


}
