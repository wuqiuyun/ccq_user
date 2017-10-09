package ccj.yun28.com.bean;

/**
 * 餐餐抢折扣券详情-店铺信息
 * 
 * @author meihuali
 * 
 */
/**
 * @author meihuali
 *
 */
public class StoreInfo {

	private String latitude;
	private String live_store_address;
	private String live_store_tel;
	private String longitude;
	private String m;
	private String store_id;
	private String store_name;
	private String union_img;

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLive_store_address() {
		return live_store_address;
	}

	public void setLive_store_address(String live_store_address) {
		this.live_store_address = live_store_address;
	}

	public String getLive_store_tel() {
		return live_store_tel;
	}

	public void setLive_store_tel(String live_store_tel) {
		this.live_store_tel = live_store_tel;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getStore_id() {
		return store_id;
	}

	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public String getUnion_img() {
		return union_img;
	}

	public void setUnion_img(String union_img) {
		this.union_img = union_img;
	}

	public String getM() {
		return m;
	}

	public void setM(String m) {
		this.m = m;
	}

	@Override
	public String toString() {
		return "StoreInfo [latitude=" + latitude + ", live_store_address="
				+ live_store_address + ", live_store_tel=" + live_store_tel
				+ ", longitude=" + longitude + ", m=" + m + ", store_id="
				+ store_id + ", store_name=" + store_name + ", union_img="
				+ union_img + "]";
	}


}
