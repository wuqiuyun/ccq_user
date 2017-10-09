package ccj.yun28.com;

import java.io.Serializable;

public class ResultInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4986117560684360069L;
	public String  status;
	public String  msg;
	public String  action;
	public String  message;

	public static final String STATUS_SUCCEED = "请求成功";

	public static final String STATUS_ERROR = "请求失败";
	
	public static final String STATUS_TIMEOUT ="请求超时";

}
