package ccj.yun28.com.bean;

import java.util.List;

/**
 * 搜索-宝贝
 * 
 * @author meihuali
 * 
 */
public class ZhuBBsearchBean {
	private String code;
	private String message;
	private List<BBsearchBean> data;

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

	public List<BBsearchBean> getData() {
		return data;
	}

	public void setData(List<BBsearchBean> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ZhuBBsearchBean [code=" + code + ", message=" + message
				+ ", data=" + data + "]";
	}

}
