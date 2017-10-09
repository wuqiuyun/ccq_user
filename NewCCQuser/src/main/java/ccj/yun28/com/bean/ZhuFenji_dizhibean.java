package ccj.yun28.com.bean;

import java.util.List;

/**
 * 附近——分级地址
 * 
 * @author meihuali
 * 
 */
public class ZhuFenji_dizhibean {
	private String code;
	private String message;
	private List<Fenji_dizhibean> data;

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

	public List<Fenji_dizhibean> getData() {
		return data;
	}

	public void setData(List<Fenji_dizhibean> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ZhuFenji_dizhibean [code=" + code + ", message=" + message
				+ ", data=" + data + "]";
	}


}
