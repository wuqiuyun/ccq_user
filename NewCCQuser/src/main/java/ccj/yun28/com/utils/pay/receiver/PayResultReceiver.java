package ccj.yun28.com.utils.pay.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import ccj.yun28.com.utils.Utils;

/**
 * 支付结果的广播接收者类 Created by Administrator on 2015/12/10.
 */
public abstract class PayResultReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String resultInfo = intent.getExtras().getString(
				Utils.pay_result_receiver_info);
		if (!TextUtils.isEmpty(resultInfo))
			payResult(resultInfo);
	}

	public abstract void payResult(String resultInfo);
}
