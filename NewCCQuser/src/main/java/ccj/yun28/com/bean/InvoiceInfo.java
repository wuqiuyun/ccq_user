package ccj.yun28.com.bean;
/**
 * 普通商品订单详情-发票信息
 * @author meihuali
 *
 */
public class InvoiceInfo {

	private String inv_content;
	private String inv_title;
	public String getInv_content() {
		return inv_content;
	}
	public void setInv_content(String inv_content) {
		this.inv_content = inv_content;
	}
	public String getInv_title() {
		return inv_title;
	}
	public void setInv_title(String inv_title) {
		this.inv_title = inv_title;
	}
	@Override
	public String toString() {
		return "InvoiceInfo [inv_content=" + inv_content + ", inv_title="
				+ inv_title + "]";
	}
	
}
