package ccj.yun28.com.bean;

/**
 * 餐餐抢折扣券详情-商品套餐信息
 * 
 * @author meihuali
 * 
 */
public class GoodsBundingInfoNew {

	private String is_child;
	private String is_smoke;
	private String is_ticket;
	private String is_wifi;
	private String service_end_time;
	private String service_start_time;
	public String getIs_child() {
		return is_child;
	}
	public void setIs_child(String is_child) {
		this.is_child = is_child;
	}
	public String getIs_smoke() {
		return is_smoke;
	}
	public void setIs_smoke(String is_smoke) {
		this.is_smoke = is_smoke;
	}
	public String getIs_ticket() {
		return is_ticket;
	}
	public void setIs_ticket(String is_ticket) {
		this.is_ticket = is_ticket;
	}
	public String getIs_wifi() {
		return is_wifi;
	}
	public void setIs_wifi(String is_wifi) {
		this.is_wifi = is_wifi;
	}
	public String getService_end_time() {
		return service_end_time;
	}
	public void setService_end_time(String service_end_time) {
		this.service_end_time = service_end_time;
	}
	public String getService_start_time() {
		return service_start_time;
	}
	public void setService_start_time(String service_start_time) {
		this.service_start_time = service_start_time;
	}
	@Override
	public String toString() {
		return "GoodsBundingInfoNew [is_child=" + is_child + ", is_smoke="
				+ is_smoke + ", is_ticket=" + is_ticket + ", is_wifi="
				+ is_wifi + ", service_end_time=" + service_end_time
				+ ", service_start_time=" + service_start_time + "]";
	}

	

}
