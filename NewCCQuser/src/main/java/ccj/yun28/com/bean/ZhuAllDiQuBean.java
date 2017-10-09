package ccj.yun28.com.bean;

import java.util.List;

/**
 * 全部地区
 * 
 * @author meihuali
 * 
 */
public class ZhuAllDiQuBean {

	private String code;
	private String message;
	private List<AllShengShiQuBean> data;
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
	public List<AllShengShiQuBean> getData() {
		return data;
	}
	public void setData(List<AllShengShiQuBean> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "ZhuAllDiQuBean [code=" + code + ", message=" + message
				+ ", data=" + data + "]";
	}

}
