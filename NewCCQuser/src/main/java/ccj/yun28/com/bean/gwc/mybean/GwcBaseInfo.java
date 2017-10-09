package ccj.yun28.com.bean.gwc.mybean;

import java.util.List;

/**
 * 购物车
 * 
 * @author meihuali
 * 
 */
public class GwcBaseInfo {
	protected String code;
	protected String message;
	protected String sum;
	private List<GwcdpInfoBean> cart_list;

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

	public String getSum() {
		return sum;
	}

	public void setSum(String sum) {
		this.sum = sum;
	}

	public List<GwcdpInfoBean> getCart_list() {
		return cart_list;
	}

	public void setCart_list(List<GwcdpInfoBean> cart_list) {
		this.cart_list = cart_list;
	}

	@Override
	public String toString() {
		return "GwcBaseInfo [code=" + code + ", message=" + message + ", sum="
				+ sum + ", cart_list=" + cart_list + "]";
	}

}
