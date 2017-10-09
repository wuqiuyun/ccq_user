package ccj.yun28.com.bean;


/**
 * 1.商品详情
 * 
 * @author meihuali
 * 
 */
public class ZhuProductBean {

	private String code;
	private String message;
	private AllProductBean data;

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

	public AllProductBean getData() {
		return data;
	}

	public void setData(AllProductBean data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ZhuProductBean [code=" + code + ", message=" + message
				+ ", data=" + data + "]";
	}

}
