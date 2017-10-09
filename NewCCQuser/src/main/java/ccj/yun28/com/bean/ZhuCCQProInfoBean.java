package ccj.yun28.com.bean;


/**
 * 餐餐抢店铺商品详情
 * 
 * @author meihuali
 * 
 */
public class ZhuCCQProInfoBean {

	private String code;
	private String message;
	private CCQProInfoBean data;

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

	public CCQProInfoBean getData() {
		return data;
	}

	public void setData(CCQProInfoBean data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ZhuCCQProInfoBean [code=" + code + ", message=" + message
				+ ", data=" + data + "]";
	}

}
