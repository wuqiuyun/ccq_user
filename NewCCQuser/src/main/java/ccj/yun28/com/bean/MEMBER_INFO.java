package ccj.yun28.com.bean;
/**
 * 订单 - 会员信息
 * 
 * @author meihuali
 * 
 */
public class MEMBER_INFO {
	private String member_mobile;
	private String member_mobile_bind;
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
	@Override
	public String toString() {
		return "MEMBER_INFO [member_mobile=" + member_mobile
				+ ", member_mobile_bind=" + member_mobile_bind + "]";
	}
}
