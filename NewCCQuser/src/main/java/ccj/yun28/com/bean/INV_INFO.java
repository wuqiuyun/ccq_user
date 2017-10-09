package ccj.yun28.com.bean;

/**
 * 订单- 其他信息（eg：发票信息）
 * 
 * @author meihuali
 * 
 */
public class INV_INFO {
	private String content;
	private String inv_code;
	private String inv_company;
	private String inv_content;
	private String inv_goto_addr;
	private String inv_id;
	private String inv_rec_mobphone;
	private String inv_rec_name;
	private String inv_rec_province;
	private String inv_reg_addr;
	private String inv_reg_baccount;
	private String inv_reg_bname;
	private String inv_reg_phone;
	private String inv_state;
	private String inv_title;
	private String member_id;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getInv_code() {
		return inv_code;
	}

	public void setInv_code(String inv_code) {
		this.inv_code = inv_code;
	}

	public String getInv_company() {
		return inv_company;
	}

	public void setInv_company(String inv_company) {
		this.inv_company = inv_company;
	}

	public String getInv_content() {
		return inv_content;
	}

	public void setInv_content(String inv_content) {
		this.inv_content = inv_content;
	}

	public String getInv_goto_addr() {
		return inv_goto_addr;
	}

	public void setInv_goto_addr(String inv_goto_addr) {
		this.inv_goto_addr = inv_goto_addr;
	}

	public String getInv_id() {
		return inv_id;
	}

	public void setInv_id(String inv_id) {
		this.inv_id = inv_id;
	}

	public String getInv_rec_mobphone() {
		return inv_rec_mobphone;
	}

	public void setInv_rec_mobphone(String inv_rec_mobphone) {
		this.inv_rec_mobphone = inv_rec_mobphone;
	}

	public String getInv_rec_name() {
		return inv_rec_name;
	}

	public void setInv_rec_name(String inv_rec_name) {
		this.inv_rec_name = inv_rec_name;
	}

	public String getInv_rec_province() {
		return inv_rec_province;
	}

	public void setInv_rec_province(String inv_rec_province) {
		this.inv_rec_province = inv_rec_province;
	}

	public String getInv_reg_addr() {
		return inv_reg_addr;
	}

	public void setInv_reg_addr(String inv_reg_addr) {
		this.inv_reg_addr = inv_reg_addr;
	}

	public String getInv_reg_baccount() {
		return inv_reg_baccount;
	}

	public void setInv_reg_baccount(String inv_reg_baccount) {
		this.inv_reg_baccount = inv_reg_baccount;
	}

	public String getInv_reg_bname() {
		return inv_reg_bname;
	}

	public void setInv_reg_bname(String inv_reg_bname) {
		this.inv_reg_bname = inv_reg_bname;
	}

	public String getInv_reg_phone() {
		return inv_reg_phone;
	}

	public void setInv_reg_phone(String inv_reg_phone) {
		this.inv_reg_phone = inv_reg_phone;
	}

	public String getInv_state() {
		return inv_state;
	}

	public void setInv_state(String inv_state) {
		this.inv_state = inv_state;
	}

	public String getInv_title() {
		return inv_title;
	}

	public void setInv_title(String inv_title) {
		this.inv_title = inv_title;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	@Override
	public String toString() {
		return "INV_INFO [content=" + content + ", inv_code=" + inv_code
				+ ", inv_company=" + inv_company + ", inv_content="
				+ inv_content + ", inv_goto_addr=" + inv_goto_addr
				+ ", inv_id=" + inv_id + ", inv_rec_mobphone="
				+ inv_rec_mobphone + ", inv_rec_name=" + inv_rec_name
				+ ", inv_rec_province=" + inv_rec_province + ", inv_reg_addr="
				+ inv_reg_addr + ", inv_reg_baccount=" + inv_reg_baccount
				+ ", inv_reg_bname=" + inv_reg_bname + ", inv_reg_phone="
				+ inv_reg_phone + ", inv_state=" + inv_state + ", inv_title="
				+ inv_title + ", member_id=" + member_id + "]";
	}

}
