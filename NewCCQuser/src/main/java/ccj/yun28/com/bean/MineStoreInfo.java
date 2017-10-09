package ccj.yun28.com.bean;
/**
 * 我- 订单-线上商品结算
 * @author meihuali
 *
 */
public class MineStoreInfo {

	private String member_mobile;
	private String member_mobile_bind;
	private String store_img;
	private String store_name;
	public String getMember_mobile() {
		return member_mobile;
	}
	public void setMember_mobile(String member_mobile) {
		this.member_mobile = member_mobile;
	}
	public String getMember_mobile_bind() {
		return member_mobile_bind;
	}
	public void setMember_mobile_bind(String member_mobile_bind) {
		this.member_mobile_bind = member_mobile_bind;
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
		return "MineStoreInfo [member_mobile=" + member_mobile
				+ ", member_mobile_bind=" + member_mobile_bind + ", store_img="
				+ store_img + ", store_name=" + store_name + "]";
	}
	
}
