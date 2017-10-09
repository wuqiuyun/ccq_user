package ccj.yun28.com.utils.pay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alipay.sdk.app.PayTask;

/**
 * 支付宝支付工具 Created by Administrator on 2015/12/9.
 */
public class AlipayUtil {

	private static final int RQF_PAY = 1;

	private OnAlipayResponse onAlipayResponse;

	public AlipayUtil(OnAlipayResponse onAlipayResponse) {
		this.onAlipayResponse = onAlipayResponse;
	}

	/**
	 * 功能:支付 使用方法：
	 * 
	 * @param activity
	 * @param orderInfo
	 *            后台对订单进行拼接编码等一系列操作后的订单信息
	 */
	public void pay(final Activity activity, final String orderInfo) {
		new Thread() {
			@Override
			public void run() {
				PayTask alipay = new PayTask(activity);
				String result = alipay.pay(orderInfo);
				System.out.println("alipay result=" + result);
				Log.e("error", result);
				Message msg = new Message();
				msg.what = RQF_PAY;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			AlipayResult alipayResult = new AlipayResult((String) msg.obj);

			switch (msg.what) {
			case RQF_PAY:
				if ("9000".equals(alipayResult.getResultStatus())) {
					onAlipayResponse.onResponse(OnAlipayResponse.SUCCESS,
							OnAlipayResponse.SUCCESS_TIPS);
				} else if ("8000".equals(alipayResult.getResultStatus())) {
					onAlipayResponse.onResponse(OnAlipayResponse.CONFIRMATION,
							OnAlipayResponse.CONFIR_TIPS);
				} else {
					onAlipayResponse.onResponse(OnAlipayResponse.FAIL,
							alipayResult.getTips());
				}
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 支付宝支付结果返回
	 * 
	 * @author frj
	 */
	public interface OnAlipayResponse {
		/**
		 * 表示支付成功
		 */
		public static final int SUCCESS = 1;
		/**
		 * 表示支付失败
		 */
		public static final int FAIL = 2;
		/**
		 * 表示支付结果确认中
		 */
		public static final int CONFIRMATION = 3;

		/**
		 * 支付成功提示信息
		 */
		public static final String SUCCESS_TIPS = "支付成功！";
		/**
		 * 支付失败提示信息
		 */
		public static final String FAIL_TIPS = "支付失败";
		/**
		 * 支付结果确认中提示信息
		 */
		public static final String CONFIR_TIPS = "支付结果确认中";

		/**
		 * 返回结果，当status为SUCCESS的值时 表示成功，此时tips值为支付成功；如果status值为FAIL值
		 * 表示失败，此时tips值为支付宝返回码对应的失败的值的信息; 如果status值为CONFIRMATION的值时
		 * 表示支付结果确认中,此时tips值为支付结果确认中
		 * 
		 * @param status
		 * @param tips
		 */
		void onResponse(int status, String tips);
	}

}
