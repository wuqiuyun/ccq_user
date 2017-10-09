package ccj.yun28.com.bean;


/**
 * 签到信息
 * 
 * @author meihuali
 * 
 */
public class ZhuSignBean {

	private String code;
	private String message;
	private AllSignInfotBean data;

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

	public AllSignInfotBean getData() {
		return data;
	}

	public void setData(AllSignInfotBean data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ZhuSignBean [code=" + code + ", message=" + message + ", data="
				+ data + "]";
	}

}
