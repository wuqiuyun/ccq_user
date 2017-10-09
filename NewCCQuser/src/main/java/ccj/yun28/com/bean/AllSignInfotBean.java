package ccj.yun28.com.bean;

import java.util.List;

/**
 * 签到信息详情
 * 
 * @author meihuali
 * 
 */
public class AllSignInfotBean {

	private String avator;
	private String banner;
	private String banner_url;
	private String banner_url_az;
	private String banner_url_new_az;
	private String is_do_sign;
	private String is_red_packet;
	private String last_red_packet;
	private String member_name;
	private String num;
	private List<String> other;
	private String ranking;
	private List<QiandaoBean> sign_list;
	private String sign_num;
	private String sum;

	public String getAvator() {
		return avator;
	}

	public void setAvator(String avator) {
		this.avator = avator;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public String getBanner_url() {
		return banner_url;
	}

	public void setBanner_url(String banner_url) {
		this.banner_url = banner_url;
	}

	public String getBanner_url_az() {
		return banner_url_az;
	}

	public void setBanner_url_az(String banner_url_az) {
		this.banner_url_az = banner_url_az;
	}

	public String getBanner_url_new_az() {
		return banner_url_new_az;
	}

	public void setBanner_url_new_az(String banner_url_new_az) {
		this.banner_url_new_az = banner_url_new_az;
	}

	public String getIs_do_sign() {
		return is_do_sign;
	}

	public void setIs_do_sign(String is_do_sign) {
		this.is_do_sign = is_do_sign;
	}

	public String getIs_red_packet() {
		return is_red_packet;
	}

	public void setIs_red_packet(String is_red_packet) {
		this.is_red_packet = is_red_packet;
	}

	public String getLast_red_packet() {
		return last_red_packet;
	}

	public void setLast_red_packet(String last_red_packet) {
		this.last_red_packet = last_red_packet;
	}

	public String getMember_name() {
		return member_name;
	}

	public void setMember_name(String member_name) {
		this.member_name = member_name;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public List<String> getOther() {
		return other;
	}

	public void setOther(List<String> other) {
		this.other = other;
	}

	public String getRanking() {
		return ranking;
	}

	public void setRanking(String ranking) {
		this.ranking = ranking;
	}

	public List<QiandaoBean> getSign_list() {
		return sign_list;
	}

	public void setSign_list(List<QiandaoBean> sign_list) {
		this.sign_list = sign_list;
	}

	public String getSign_num() {
		return sign_num;
	}

	public void setSign_num(String sign_num) {
		this.sign_num = sign_num;
	}

	public String getSum() {
		return sum;
	}

	public void setSum(String sum) {
		this.sum = sum;
	}

	@Override
	public String toString() {
		return "AllSignInfotBean [avator=" + avator + ", banner=" + banner
				+ ", banner_url=" + banner_url + ", banner_url_az="
				+ banner_url_az + ", banner_url_new_az=" + banner_url_new_az
				+ ", is_do_sign=" + is_do_sign + ", is_red_packet="
				+ is_red_packet + ", last_red_packet=" + last_red_packet
				+ ", member_name=" + member_name + ", num=" + num + ", other="
				+ other + ", ranking=" + ranking + ", sign_list=" + sign_list
				+ ", sign_num=" + sign_num + ", sum=" + sum + "]";
	}

}
