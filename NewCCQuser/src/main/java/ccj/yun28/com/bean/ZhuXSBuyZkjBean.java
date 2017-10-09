package ccj.yun28.com.bean;


/**
 * 线上商品结算
 * 
 * @author meihuali
 * 
 */
public class ZhuXSBuyZkjBean {

	private String code;
	private String message;
	private XSBuyZkjBean buy_list;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public XSBuyZkjBean getBuy_list() {
		return buy_list;
	}

	public void setBuy_list(XSBuyZkjBean buy_list) {
		this.buy_list = buy_list;
	}

	@Override
	public String toString() {
		return "ZhuXSBuyZkjBean [code=" + code + ", message=" + message
				+ ", buy_list=" + buy_list + "]";
	}

}
