package ccj.yun28.com.bean;

import java.util.List;

/**
 * 商品评价列表
 * 
 * @author meihuali
 * 
 */
public class ZhuProductpinjiaBean {

	private String code;
	private String message;
	private List<ProductpinjiaBean> data;

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

	public List<ProductpinjiaBean> getData() {
		return data;
	}

	public void setData(List<ProductpinjiaBean> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ZhuProductpinjiaBean [code=" + code + ", message=" + message
				+ ", data=" + data + "]";
	}

}
