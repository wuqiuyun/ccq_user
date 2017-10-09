package ccj.yun28.com.bean;

import java.util.List;

/**
 * 2,商品详情
 * 
 * @author meihuali
 * 
 */
public class AllProductBean {

	private String geval_goods_num;
	private List<PinJIaInfoBean> goods_evaluate;
	private List<String> goods_image_mobile;
	private GoodsInfoBean goods_info;
	private List<ProductGuiGeBean> spec_list;
	private StoreInfoBean store_info;

	public String getGeval_goods_num() {
		return geval_goods_num;
	}

	public void setGeval_goods_num(String geval_goods_num) {
		this.geval_goods_num = geval_goods_num;
	}

	public List<PinJIaInfoBean> getGoods_evaluate() {
		return goods_evaluate;
	}

	public void setGoods_evaluate(List<PinJIaInfoBean> goods_evaluate) {
		this.goods_evaluate = goods_evaluate;
	}

	public List<String> getGoods_image_mobile() {
		return goods_image_mobile;
	}

	public void setGoods_image_mobile(List<String> goods_image_mobile) {
		this.goods_image_mobile = goods_image_mobile;
	}

	public GoodsInfoBean getGoods_info() {
		return goods_info;
	}

	public void setGoods_info(GoodsInfoBean goods_info) {
		this.goods_info = goods_info;
	}

	public List<ProductGuiGeBean> getSpec_list() {
		return spec_list;
	}

	public void setSpec_list(List<ProductGuiGeBean> spec_list) {
		this.spec_list = spec_list;
	}

	public StoreInfoBean getStore_info() {
		return store_info;
	}

	public void setStore_info(StoreInfoBean store_info) {
		this.store_info = store_info;
	}

	@Override
	public String toString() {
		return "AllProductBean [geval_goods_num=" + geval_goods_num
				+ ", goods_evaluate=" + goods_evaluate
				+ ", goods_image_mobile=" + goods_image_mobile
				+ ", goods_info=" + goods_info + ", spec_list=" + spec_list
				+ ", store_info=" + store_info + "]";
	}

}
