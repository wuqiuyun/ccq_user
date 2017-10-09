package ccj.yun28.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.utils.SharedUtil;

import com.lidroid.xutils.BitmapUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 欢迎页
 * 
 * @author meihuali
 * 
 */
public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		View view = View.inflate(this, R.layout.activity_welcome, null);
		setContentView(view);

		ImageView iv_qidong = (ImageView) view.findViewById(R.id.iv_qidong);
		BitmapUtils bitmapUtils = new BitmapUtils(this);
		bitmapUtils.display(iv_qidong, "assets/img/qidong.png");
		//正式
		SharedUtil.saveStringValue(SharedCommon.API_DOMAIN, "app.28yun.com/index.php/webapi_v2/");
		//测式
//		SharedUtil.saveStringValue(SharedCommon.API_DOMAIN, "over.28yun.cn/index.php/webapi_v2/");
		//上线时测式
//		SharedUtil.saveStringValue(SharedCommon.API_DOMAIN, "b.hy.com/index.php/webapi_v2/");
		
		
		SharedUtil.saveStringValue(SharedCommon.SELECTOR_CITY_ID, "");
		SharedUtil.saveStringValue(SharedCommon.ADDRESS, "");
		SharedUtil.saveStringValue(SharedCommon.LATITUDE, "");
		SharedUtil.saveStringValue(SharedCommon.LONGITUDE, "");
		SharedUtil.saveStringValue(SharedCommon.CITY, "");
		
		SharedUtil.saveStringValue(SharedCommon.IMG_DOMAIN, "http://112.74.172.160");
		SharedUtil.saveStringValue(SharedCommon.HP, "http://");
		SharedUtil.saveStringValue(SharedCommon.STORE_QRCODE_IMG_AZ,
				"http://www.28yun.com/wap/qrcode/azyw.png");
		SharedUtil.saveStringValue(SharedCommon.STORE_FX, "http://www.28yun.com/mobile/api/payment/wxpay/ccqsj.png");
		SharedUtil.saveStringValue(
				SharedCommon.STORE_QRCODE_IMG_AZ_URL,
				"http://a.app.qq.com/o/simple.jsp?pkgname=ccj.sz28yun.com");
		SharedUtil.saveStringValue(SharedCommon.STORE_QRCODE_IMG_IOS,
				"http://www.28yun.com/wap/qrcode/iossj.png");
		SharedUtil.saveStringValue(
				SharedCommon.STORE_QRCODE_IMG_IOS_URL,
				"https://itunes.apple.com/us/app/can-can-qiang-shang-jia-ban/id1135937783?l=zh&ls=1&mt=8");
		SharedUtil.saveStringValue(SharedCommon.MALL_QRCODE_IMG_AZ,
				"http://www.28yun.com/wap/qrcode/28yun.png");
		SharedUtil.saveStringValue(SharedCommon.MALL_FX, "http://www.28yun.com/mobile/api/payment/wxpay/ccqyh.png");
		SharedUtil.saveStringValue(SharedCommon.MALL_QRCODE_IMG_AZ_URL,
				"http://www.28yun.com/wap/download.html");
		SharedUtil.saveStringValue(SharedCommon.MALL_QRCODE_IMG_IOS,
				"http://www.28yun.com/wap/qrcode/28yun.png");
		SharedUtil.saveStringValue(
				SharedCommon.MALL_QRCODE_IMG_IOS_URL,
				"http://www.28yun.com/wap/download.html");
		SharedUtil.saveStringValue(SharedCommon.MARKETER_QRCODE_IMG_AZ,
				"http://www.28yun.com/wap/qrcode/azyw.png");
		SharedUtil.saveStringValue(SharedCommon.MARKETER_FX,
				"http://www.28yun.com/mobile/api/payment/wxpay/ccqsy.png");
		SharedUtil.saveStringValue(
				SharedCommon.MARKETER_QRCODE_IMG_AZ_URL,
				"http://www.28yun.com/wap/qrcode/azyw.png");
		SharedUtil.saveStringValue(
				SharedCommon.MARKETER_QRCODE_IMG_IOS,
				"http://www.28yun.com/wap/qrcode/iosyw.png");
		SharedUtil.saveStringValue(
				SharedCommon.MARKETER_QRCODE_IMG_IOS_URL,
				"https://itunes.apple.com/us/app/can-can-qiang-ye-wu-ban/id1134503777?l=zh&ls=1&mt=8");
		SharedUtil.saveStringValue(SharedCommon.MALL_FP, "http://112.74.172.160/data/ccq.png");
		SharedUtil.saveStringValue(SharedCommon.STORE_FP, "http://112.74.172.160/data/ccq.png");
		SharedUtil.saveStringValue(SharedCommon.MARKETER_FP,
				"http://112.74.172.160/data/ccq.png");
		SharedUtil.saveStringValue(SharedCommon.MALL_SERVICE,
				"400-806-9828");
		SharedUtil.saveStringValue(SharedCommon.STORE_SERVICE,
				"400-806-9828");
		SharedUtil.saveStringValue(SharedCommon.MARKETER_SERVICE,
				"400-806-9828");
		SharedUtil.saveStringValue(SharedCommon.MARKETER_SERVICE_ADMIN,
				"400-806-9828");
		SharedUtil.saveStringValue(SharedCommon.CHECK_PHONE,
				"400-806-9828");
		SharedUtil.saveStringValue(SharedCommon.STORE_WITHDRAW_EXPLAIN,
				"http://www.28yun.com");
		SharedUtil.saveStringValue(SharedCommon.STORE_HELP, "http://www.28yun.com");
		SharedUtil.saveStringValue(SharedCommon.MALL_WITHDRAW_EXPLAIN,
				"http://www.28yun.com/mobile/api/payment/wxpay/help/files/withdraw-explain.html");
		SharedUtil.saveStringValue(SharedCommon.MALL_HELP, "http://www.28yun.com/mobile/api/payment/wxpay/help/files/help.html");
		SharedUtil.saveStringValue(SharedCommon.MALL_RED_PACKET,
				"http://www.28yun.com/mobile/api/payment/wxpay/help/files/sign_in_rule.html");
		SharedUtil.saveStringValue(SharedCommon.ACTIVITY_1, "http://www.28yun.com");
		SharedUtil.saveStringValue(SharedCommon.ACTIVITY_2, "http://www.28yun.com");
		SharedUtil.saveStringValue(SharedCommon.ACTIVITY_3, "http://www.28yun.com");
		SharedUtil.saveStringValue(SharedCommon.ACTIVITY_4, "http://www.28yun.com");
		SharedUtil.saveStringValue(SharedCommon.ACTIVITY_5, "http://www.28yun.com/wap/download.html");
		SharedUtil.saveStringValue(SharedCommon.USERAGREEMENT,
				"http://www.28yun.com");
		SharedUtil.saveStringValue(SharedCommon.REG,"http://www.28yun.com/api_wap/tmpl/member/register.html");
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(3000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				Intent intent = new Intent(WelcomeActivity.this,
						GuideActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
