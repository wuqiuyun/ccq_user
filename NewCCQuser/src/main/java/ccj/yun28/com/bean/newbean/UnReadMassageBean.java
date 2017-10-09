package ccj.yun28.com.bean.newbean;

/**
 * 未读消息
 * 
 * @author wuqiuyun
 * 
 */
public class UnReadMassageBean {
	private String unread_type1;// 通知消息
	private String unread_type2;// 物流消息
	private String unread_type3;// 活动消息

	public String getUnread_type1() {
		return unread_type1;
	}

	public void setUnread_type1(String unread_type1) {
		this.unread_type1 = unread_type1;
	}

	public String getUnread_type2() {
		return unread_type2;
	}

	public void setUnread_type2(String unread_type2) {
		this.unread_type2 = unread_type2;
	}

	public String getUnread_type3() {
		return unread_type3;
	}

	public void setUnread_type3(String unread_type3) {
		this.unread_type3 = unread_type3;
	}

	@Override
	public String toString() {
		return "UnReadMassageBean [unread_type1=" + unread_type1
				+ ", unread_type2=" + unread_type2 + ", unread_type3="
				+ unread_type3 + "]";
	}

}
