package ccj.yun28.com.bean;


/**
 * 餐餐抢商品结算
 * 
 * @author meihuali
 * 
 */
public class ZhuBuyZkjBean {

	private String code;
	private String message;
	private BuyZkjBean buy_list;

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

	public BuyZkjBean getBuy_list() {
		return buy_list;
	}

	public void setBuy_list(BuyZkjBean buy_list) {
		this.buy_list = buy_list;
	}

	@Override
	public String toString() {
		return "ZhuBuyZkjBean [code=" + code + ", message=" + message
				+ ", buy_list=" + buy_list + "]";
	}

}
