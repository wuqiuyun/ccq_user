package ccj.yun28.com;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.View;

public class BaseActivity extends Activity {
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (newConfig.fontScale != 1)// 非默认值
			getResources();
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public Resources getResources() {
		Resources res = super.getResources();
		if (res.getConfiguration().fontScale != 1) {
			// 非默认值
			Configuration newConfig = new Configuration();
			newConfig.setToDefaults();
			// 设置默认
			res.updateConfiguration(newConfig, res.getDisplayMetrics());
		}
		return res;
	}
}
