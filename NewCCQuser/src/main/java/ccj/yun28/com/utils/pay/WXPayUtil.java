package ccj.yun28.com.utils.pay;

import android.app.Activity;
import ccj.yun28.com.bean.wx.WXPayInfo;
import ccj.yun28.com.sy.BuyZKJActivity;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * 微信支付工具类 Created by Administrator on 2015/12/10.
 */
public class WXPayUtil implements IWXAPIEventHandler {

	private WXPayInfo payInfo;
	private Activity content;

	public WXPayUtil(WXPayInfo payInfo) {
		this.payInfo = payInfo;
	}

	// public WXPayUtil(Activity content, WXPayInfo data) {
	// this.payInfo = payInfo;
	// this.content = content;
	// }

	private void sendPayReq() {
		PayReq payReq = new PayReq();
		payReq.appId = payInfo.getAppid();
		payReq.partnerId = payInfo.getPartnerid();
		payReq.prepayId = payInfo.getPrepayid();
		payReq.nonceStr = payInfo.getNoncestr();
		payReq.timeStamp = payInfo.getTimestamp();
		payReq.packageValue = payInfo.getPackagestr();
		payReq.sign = payInfo.getSign();
		BuyZKJActivity.wxApi.sendReq(payReq);// 根据自己支付的activity来替换
		// content.wxApi.sendReq(payReq);// 根据自己支付的activity来替换
	}

	/**
	 * 支付方法
	 */
	public synchronized void pay() {
		new Thread() {
			@Override
			public void run() {
				sendPayReq();
			}
		}.start();
	}

	@Override
	public void onReq(BaseReq baseReq) {
	}

	@Override
	public void onResp(BaseResp baseResp) {

	}
}
