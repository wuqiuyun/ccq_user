package ccj.yun28.com.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.MainActivity1;
import ccj.yun28.com.R;
import ccj.yun28.com.mine.DFKDinDanActivtiy;
import ccj.yun28.com.sy.BuyZKJActivity;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.utils.pay.PayResultDialog;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI wxApi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expay_entry);
		wxApi = WXAPIFactory.createWXAPI(this, Utils.WX_APP_ID);
		wxApi.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		wxApi.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq baseReq) {

	}

	@Override
	public void onResp(BaseResp baseResp) {

		if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			PayResultDialog payResultDialog = new PayResultDialog(
					WXPayEntryActivity.this) {
				@Override
				public void sureListener() {
					WXPayEntryActivity.this.finish();
				}
			};
			switch (baseResp.errCode) {
			case BaseResp.ErrCode.ERR_COMM:// 失败,可对用户做提醒操作
				payResultDialog.setHintInfo("支付失败!");
				payResultDialog.show();
				Toast.makeText(WXPayEntryActivity.this, "支付失败!",
						Toast.LENGTH_SHORT).show();
				finish();
				break;
			case BaseResp.ErrCode.ERR_OK:// 成功

				Intent intent = new Intent();
				intent.setAction(Utils.pay_result_receiver_action);
				intent.putExtra(Utils.pay_result_receiver_info, Utils.IS_WXPAY);
				sendBroadcast(intent);
				payResultDialog.setHintInfo("支付成功!");
				finish();
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:

				// 取消支付,可对用户做提醒操作
				/*
				 * if (!BuyZKJActivity.buyZKJActivity.isFinishing()) {
				 * BuyZKJActivity.buyZKJActivity.finish(); }
				 */

				// 取消支付返回待付款页面
				/*
				 * Intent dzfintent = new Intent(WXPayEntryActivity.this,
				 * DFKDinDanActivtiy.class); 
				 * startActivity(dzfintent);
				 */
				finish();
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				// 支付错误,可对用户做提醒操作
				Toast.makeText(WXPayEntryActivity.this, "支付错误!",
						Toast.LENGTH_SHORT).show();
				finish();
				break;
			default:
				break;
			}
		}
	}

}
