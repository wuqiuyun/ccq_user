package ccj.yun28.com.bean;

/**
 * 订单 - 店铺信息
 * 
 * @author meihuali
 * 
 */
public class STORE_INFO {

	private String store_img;
	private String store_name;
	private String store_address;
	private String live_store_tel;


	public String getStore_address() {
		return store_address;
	}

	public void setStore_address(String store_address) {
		this.store_address = store_address;
	}

	public String getLive_store_tel() {
		return live_store_tel;
	}

	public void setLive_store_tel(String live_store_tel) {
		this.live_store_tel = live_store_tel;
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

	@Override
	public String toString() {
		return "STORE_INFO [store_img=" + store_img + ", store_name="
				+ store_name + ", store_address=" + store_address
				+ ", live_store_tel=" + live_store_tel + "]";
	}


}
