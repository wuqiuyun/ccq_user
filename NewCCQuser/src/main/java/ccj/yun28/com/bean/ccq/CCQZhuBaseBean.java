package ccj.yun28.com.bean.ccq;


/**
 * 餐餐抢
 * 
 * @author meihuali
 * 
 */
public class CCQZhuBaseBean {
	private String code;
	private CCQBaseInfo data;
	private String message;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public CCQBaseInfo getData() {
		return data;
	}

	public void setData(CCQBaseInfo data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "CCQZhuBaseBean [code=" + code + ", data=" + data + ", message="
				+ message + "]";
	}

}
