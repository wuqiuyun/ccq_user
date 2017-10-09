package ccj.yun28.com.bean;

import java.util.List;

/**
 * 全部订单 - 全部
 * 
 * @author meihuali
 * 
 */
public class ZhuAllDinDanBean {

	private String code;
	private String message;
	private List<AllDinDanBean> data;
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
	public List<AllDinDanBean> getData() {
		return data;
	}
	public void setData(List<AllDinDanBean> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "ZhuAllDinDanBean [code=" + code + ", message=" + message
				+ ", data=" + data + "]";
	}


}
