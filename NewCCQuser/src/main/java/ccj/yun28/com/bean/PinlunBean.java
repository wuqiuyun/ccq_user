package ccj.yun28.com.bean;

public class PinlunBean {
	private String avator;
	private String feedbackrate;
	private String geval_content;
	private String geval_frommemberid;
	private String geval_frommembername;
	private String geval_scores;

	public String getAvator() {
		return avator;
	}

	public void setAvator(String avator) {
		this.avator = avator;
	}

	public String getFeedbackrate() {
		return feedbackrate;
	}

	public void setFeedbackrate(String feedbackrate) {
		this.feedbackrate = feedbackrate;
	}

	public String getGeval_content() {
		return geval_content;
	}

	public void setGeval_content(String geval_content) {
		this.geval_content = geval_content;
	}

	public String getGeval_frommemberid() {
		return geval_frommemberid;
	}

	public void setGeval_frommemberid(String geval_frommemberid) {
		this.geval_frommemberid = geval_frommemberid;
	}

	public String getGeval_frommembername() {
		return geval_frommembername;
	}

	public void setGeval_frommembername(String geval_frommembername) {
		this.geval_frommembername = geval_frommembername;
	}

	public String getGeval_scores() {
		return geval_scores;
	}

	public void setGeval_scores(String geval_scores) {
		this.geval_scores = geval_scores;
	}

	@Override
	public String toString() {
		return "PinlunBean [avator=" + avator + ", feedbackrate="
				+ feedbackrate + ", geval_content=" + geval_content
				+ ", geval_frommemberid=" + geval_frommemberid
				+ ", geval_frommembername=" + geval_frommembername
				+ ", geval_scores=" + geval_scores + "]";
	}

}
