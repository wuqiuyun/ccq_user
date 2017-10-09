package ccj.yun28.com.bean;


/**
 * 普通商品订单详情
 * 
 * @author meihuali
 * 
 */
public class ZhuProDinDanDetailBean {

	private String code;
	private String message;
	private ProDinDanDetailBean data;
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
	public ProDinDanDetailBean getData() {
		return data;
	}
	public void setData(ProDinDanDetailBean data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "ZhuProDinDanDetailBean [code=" + code + ", message=" + message
				+ ", data=" + data + "]";
	}

}
