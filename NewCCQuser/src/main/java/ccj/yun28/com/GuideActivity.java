package ccj.yun28.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.systemstatus.SystemStatusManager;
import ccj.yun28.com.utils.SharedUtil;
import cn.jpush.android.api.JPushInterface;

import com.umeng.analytics.MobclickAgent;

/**
 * 广告页或引导页
 * 
 * @author meihuali
 * 
 */
public class GuideActivity extends BaseActivity {

	// 跳过广告
	private TextView tv_go;
	private boolean flag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTranslucentStatus();
		setContentView(R.layout.activity_guide);
		timer.start();
		RelativeLayout rel_guanggao = (RelativeLayout) findViewById(R.id.rel_guanggao);
		tv_go = (TextView) findViewById(R.id.tv_go);

		init();

		// String guanggaotu = SharedUtil.getStringValue(SharedCommon.MALL_FP,
		// "");
		// BitmapUtils bitmapUtils = new BitmapUtils(GuideActivity.this);
		// if (!"".equals(guanggaotu)) {
		// bitmapUtils.display(line_guanggao, guanggaotu);
		// tv_go.setVisibility(View.VISIBLE);
		// }

		SharedUtil.saveStringValue(SharedCommon.SFSETALIAS, "yes");
		SharedUtil.saveStringValue(SharedCommon.CHECKUPDATA, "yes");
		SharedUtil.deleteValue(SharedCommon.DBDH);
		
		tv_go.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				flag = false; 

//				Intent intent = new Intent(GuideActivity.this,
//						MainActivity1.class);
				Intent intent = new Intent(GuideActivity.this,
				MainActivity.class);
				intent.putExtra("type", "");
				startActivity(intent);
				timer.cancel();
				finish();
			}
		});
	}

	/**
	 * 设置状态栏背景状态
	 */
	private void setTranslucentStatus() 
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			Window win = getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			winParams.flags |= bits;
			win.setAttributes(winParams);
		}
		SystemStatusManager tintManager = new SystemStatusManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(0);//状态栏无背景
	}
	
	// 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
	private void init() {
		JPushInterface.init(getApplicationContext());
	}

	// 4s时间倒计时(跳过广告)
	CountDownTimer timer = new CountDownTimer(3000, 1000) {

		@Override
		public void onTick(long millisUntilFinished) {

			tv_go.setText("  " + millisUntilFinished / 1000 + "s 后跳过  ");

		}

		@Override
		public void onFinish() {

			if (flag = true) {
				Intent intent = new Intent(GuideActivity.this,
						MainActivity.class);
				intent.putExtra("type", "");
				startActivity(intent);
				finish();
			}

		}

	};
	
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
