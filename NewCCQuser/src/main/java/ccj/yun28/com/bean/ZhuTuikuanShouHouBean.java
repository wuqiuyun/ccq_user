package ccj.yun28.com.bean;

import java.util.List;

/**
 * 退款售后
 * 
 * @author meihuali
 * 
 */
public class ZhuTuikuanShouHouBean {

	private String code;
	private String message;
	private List<TuiKuanShouHouBean> data;
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
	public List<TuiKuanShouHouBean> getData() {
		return data;
	}
	public void setData(List<TuiKuanShouHouBean> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "ZhuTuikuanShouHouBean [code=" + code + ", message=" + message
				+ ", data=" + data + "]";
	}


}
