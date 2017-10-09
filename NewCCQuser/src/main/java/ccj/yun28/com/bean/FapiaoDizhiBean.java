package ccj.yun28.com.bean;
/**
 * 发票地址
 * @author meihuali
 *
 */
public class FapiaoDizhiBean {

	private InvoiceInfo invoice_info;
	private String order_message;
	private ReciverInfo reciver_info;
	private String reciver_name;
	private String shipping_express_id;
	private String shipping_time;
	public InvoiceInfo getInvoice_info() {
		return invoice_info;
	}
	public void setInvoice_info(InvoiceInfo invoice_info) {
		this.invoice_info = invoice_info;
	}
	public String getOrder_message() {
		return order_message;
	}
	public void setOrder_message(String order_message) {
		this.order_message = order_message;
	}
	public ReciverInfo getReciver_info() {
		return reciver_info;
	}
	public void setReciver_info(ReciverInfo reciver_info) {
		this.reciver_info = reciver_info;
	}
	public String getReciver_name() {
		return reciver_name;
	}
	public void setReciver_name(String reciver_name) {
		this.reciver_name = reciver_name;
	}
	public String getShipping_express_id() {
		return shipping_express_id;
	}
	public void setShipping_express_id(String shipping_express_id) {
		this.shipping_express_id = shipping_express_id;
	}
	public String getShipping_time() {
		return shipping_time;
	}
	public void setShipping_time(String shipping_time) {
		this.shipping_time = shipping_time;
	}
	@Override
	public String toString() {
		return "FapiaoDizhiBean [invoice_info=" + invoice_info
				+ ", order_message=" + order_message + ", reciver_info="
				+ reciver_info + ", reciver_name=" + reciver_name
				+ ", shipping_express_id=" + shipping_express_id
				+ ", shipping_time=" + shipping_time + "]";
	}
	
}
