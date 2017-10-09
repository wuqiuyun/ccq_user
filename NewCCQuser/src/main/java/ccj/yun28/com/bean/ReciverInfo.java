package ccj.yun28.com.bean;
/**
 * 
 * @author meihuali
 *
 */
public class ReciverInfo {

	private String address;
	private String area;
	private String phone;
	private String street;
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	@Override
	public String toString() {
		return "ReciverInfo [address=" + address + ", area=" + area
				+ ", phone=" + phone + ", street=" + street + "]";
	}
	
}
