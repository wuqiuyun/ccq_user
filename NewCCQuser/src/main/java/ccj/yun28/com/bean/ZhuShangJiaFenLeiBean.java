package ccj.yun28.com.bean;

import java.util.List;

/**
 * 商家入驻-商家分类
 * 
 * @author meihuali
 * 
 */
public class ZhuShangJiaFenLeiBean {

	private String code;
	private String message;
	private List<ShangJiaFenLeiBean> data;

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

	public List<ShangJiaFenLeiBean> getData() {
		return data;
	}

	public void setData(List<ShangJiaFenLeiBean> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ZhuShangJiaFenLeiBean [code=" + code + ", message=" + message
				+ ", data=" + data + "]";
	}

}
