package ccj.yun28.com.bean;

import java.util.List;

public class ZhuFenleiBean {

	private String code;
	private String message;
	private List<FenleiBean> data;

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

	public List<FenleiBean> getData() {
		return data;
	}

	public void setData(List<FenleiBean> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ZhuFenleiBean [code=" + code + ", message=" + message
				+ ", data=" + data + "]";
	}

}
