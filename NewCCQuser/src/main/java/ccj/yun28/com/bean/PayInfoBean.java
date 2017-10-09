package ccj.yun28.com.bean;
/**
 * 我- 订单-线上商品结算-支付信息
 * @author meihuali
 *
 */
public class PayInfoBean {
	private String body;
	private String goods_name;
	private String order_amount;
	private String order_sn;
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getGoods_name() {
		return goods_name;
	}
	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}
	public String getOrder_amount() {
		return order_amount;
	}
	public void setOrder_amount(String order_amount) {
		this.order_amount = order_amount;
	}
	public String getOrder_sn() {
		return order_sn;
	}
	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}
	@Override
	public String toString() {
		return "PayInfoBean [body=" + body + ", goods_name=" + goods_name
				+ ", order_amount=" + order_amount + ", order_sn=" + order_sn
				+ "]";
	}
	
}
