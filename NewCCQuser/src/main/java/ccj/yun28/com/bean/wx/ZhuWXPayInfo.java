package ccj.yun28.com.bean.wx;


/**
 * 后台返回微信支付信息的jsonbean
 */
public class ZhuWXPayInfo {
	private String code;
	private String message;
	private WXPayInfo data;

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

	public WXPayInfo getData() {
		return data;
	}

	public void setData(WXPayInfo data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ZhuWXPayInfo [code=" + code + ", message=" + message
				+ ", data=" + data + "]";
	}

}
