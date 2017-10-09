package ccj.yun28.com.bean.wx;

/**
 * 后台返回微信支付信息的jsonbean
 */
public class WXPayInfo {
	/** 商家在微信开放平台申请的应用id */
	private String appid;
	private String partnerid;
	private String prepayid;
	/** 随机串防重发 */
	private String noncestr;
	private String timestamp;
	/** 商家根据文档填写的数据和签名 */
	private String packagestr;
	/** 商家根据微信开放平台对数据做的签名 */
	private String sign;

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getPartnerid() {
		return partnerid;
	}

	public void setPartnerid(String partnerid) {
		this.partnerid = partnerid;
	}

	public String getPrepayid() {
		return prepayid;
	}

	public void setPrepayid(String prepayid) {
		this.prepayid = prepayid;
	}

	public String getNoncestr() {
		return noncestr;
	}

	public void setNoncestr(String noncestr) {
		this.noncestr = noncestr;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getPackagestr() {
		return packagestr;
	}

	public void setPackagestr(String packagestr) {
		this.packagestr = packagestr;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	@Override
	public String toString() {
		return "WXPayInfo [appid=" + appid + ", partnerid=" + partnerid
				+ ", prepayid=" + prepayid + ", noncestr=" + noncestr
				+ ", timestamp=" + timestamp + ", packagestr=" + packagestr
				+ ", sign=" + sign + "]";
	}

}
