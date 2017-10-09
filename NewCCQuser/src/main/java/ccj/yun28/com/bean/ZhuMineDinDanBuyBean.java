package ccj.yun28.com.bean;


/**
 * 我-订单-线上商品结算
 * 
 * @author meihuali
 * 
 */
public class ZhuMineDinDanBuyBean {

	private String code;
	private String message;
	private MineXSBuyZkjBean buy_list;
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
	public MineXSBuyZkjBean getBuy_list() {
		return buy_list;
	}
	public void setBuy_list(MineXSBuyZkjBean buy_list) {
		this.buy_list = buy_list;
	}
	@Override
	public String toString() {
		return "zhuminedindanbuyBean [code=" + code + ", message=" + message
				+ ", buy_list=" + buy_list + "]";
	}

}
