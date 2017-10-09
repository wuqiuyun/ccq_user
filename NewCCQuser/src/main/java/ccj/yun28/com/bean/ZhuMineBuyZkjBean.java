package ccj.yun28.com.bean;


/**
 * 我的-订单-餐餐抢商品结算
 * 
 * @author meihuali
 * 
 */
public class ZhuMineBuyZkjBean {

	private String code;
	private String message;
	private MineBuyZkjBean buy_list;
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
	public MineBuyZkjBean getBuy_list() {
		return buy_list;
	}
	public void setBuy_list(MineBuyZkjBean buy_list) {
		this.buy_list = buy_list;
	}
	@Override
	public String toString() {
		return "ZhuMineBuyZkjBean [code=" + code + ", message=" + message
				+ ", buy_list=" + buy_list + "]";
	}

}
