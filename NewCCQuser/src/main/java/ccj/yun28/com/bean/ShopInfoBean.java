package ccj.yun28.com.bean;

/**
 * 店铺信息
 * 
 * @author meihuali
 * 
 */
public class ShopInfoBean {
	private String feedbackrate;
	private String store_img;
	private String store_name;
	private String store_tel;

	public String getFeedbackrate() {
		return feedbackrate;
	}

	public void setFeedbackrate(String feedbackrate) {
		this.feedbackrate = feedbackrate;
	}

	public String getStore_img() {
		return store_img;
	}

	public void setStore_img(String store_img) {
		this.store_img = store_img;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public String getStore_tel() {
		return store_tel;
	}

	public void setStore_tel(String store_tel) {
		this.store_tel = store_tel;
	}

	@Override
	public String toString() {
		return "ShopInfoBean [feedbackrate=" + feedbackrate + ", store_img="
				+ store_img + ", store_name=" + store_name + ", store_tel="
				+ store_tel + "]";
	}

}
