package ccj.yun28.com.utils.pay;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.text.TextUtils;

public class AlipayResult {

	private static final Map<String, String> sResultStatus;

	private String mResult;

	String resultStatus = null; // 状态码
	String memo = null;
	String result = null;
	boolean isSignOk = false;

	public AlipayResult(String result) {
		this.mResult = result;
	}

	static {
		sResultStatus = new HashMap<String, String>();
		sResultStatus.put("9000", "操作成功！");
		sResultStatus.put("4000", "系统异常！");
		sResultStatus.put("4001", "数据格式不正确！");
		sResultStatus.put("4003", "该用户关联的支付宝账户被冻结或不允许支付！");
		sResultStatus.put("4004", "该用户已解除关联！");
		sResultStatus.put("4005", "关联失败或没有关联！");
		sResultStatus.put("4006", "订单支付失败！");
		sResultStatus.put("4010", "重新关联账户！");
		sResultStatus.put("6000", "支付服务正在进行升级操作！");
		sResultStatus.put("6001", "用户中途取消支付操作！");
		sResultStatus.put("6002", "网络连接出错！");
		sResultStatus.put("7001", "网页支付失败！");
	}

	public String getResult() {
		String src = mResult.replace("{", "");
		src = src.replace("}", "");
		return getContent(src, "memo=", ";result");
	}

	/**
	 * 返回返回码对应提示信息
	 * 
	 * @return
	 */
	public String getTips() {
		if (TextUtils.isEmpty(resultStatus)) {
			this.resultStatus = getResultStatus();
		}
		return sResultStatus.get(resultStatus);
	}

	/**
	 * 功能:获得订单ID
	 * 
	 * @return
	 */
	public String getOutTradeNo() {
		String src = mResult.replace("{", "");
		src = src.replace("}", "");

		return getContent(src, "out_trade_no=", "\"&subject");
	}

	public String getResultStatus() {
		String src = mResult.replace("{", "");
		src = src.replace("}", "");
		return getContent(src, "resultStatus=", ";memo");
	}

	/**
	 * 将字符串制成JSONObject对象
	 * 
	 * @param src
	 * @param split
	 * @return
	 */
	public JSONObject string2JSON(String src, String split) {
		JSONObject json = new JSONObject();

		try {
			String[] arr = src.split(split);
			for (int i = 0; i < arr.length; i++) {
				String[] arrKey = arr[i].split("=");
				json.put(arrKey[0], arr[i].substring(arrKey[0].length() + 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return json;
	}

	/**
	 * 根据字符串或者两个节点之间的内容
	 * 
	 * @param src
	 *            字符串
	 * @param startTag
	 *            开始节点
	 * @param endTag
	 *            结束节点
	 * @return
	 */
	private String getContent(String src, String startTag, String endTag) {
		String content = src;
		int start = src.indexOf(startTag);
		start += startTag.length();

		try {
			if (endTag != null) {
				int end = src.indexOf(endTag);
				content = src.substring(start, end);
			} else {
				content = src.substring(start);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return content;
	}
}
