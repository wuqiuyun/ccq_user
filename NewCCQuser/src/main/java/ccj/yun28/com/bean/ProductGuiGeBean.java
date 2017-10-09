package ccj.yun28.com.bean;

/**
 * 6.商品详情-商品规格
 * 
 * @author meihuali
 * 
 */
public class ProductGuiGeBean {

	private String goods_id;
	private String sign;

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	@Override
	public String toString() {
		return "ProductGuiGeBean [goods_id=" + goods_id + ", sign=" + sign
				+ "]";
	}

}
