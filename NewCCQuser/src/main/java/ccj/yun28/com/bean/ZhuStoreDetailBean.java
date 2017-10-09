package ccj.yun28.com.bean;


/**
 * 餐餐抢店铺详情
 * 
 * @author meihuali
 * 
 */
public class ZhuStoreDetailBean {

	private String code;
	private String message;
	private CcqStoreBean data;

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

	public CcqStoreBean getData() {
		return data;
	}

	public void setData(CcqStoreBean data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ZhuStoreDetailBean [code=" + code + ", message=" + message
				+ ", data=" + data + "]";
	}

}
