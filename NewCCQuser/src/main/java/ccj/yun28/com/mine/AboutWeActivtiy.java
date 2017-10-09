package ccj.yun28.com.mine;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.utils.SharedUtil;

import com.lidroid.xutils.BitmapUtils;

/**
 * 我的 - 关于我们
 * 
 * @author meihuali
 * 
 */
public class AboutWeActivtiy extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		TextView tv_banbenhao = (TextView) findViewById(R.id.tv_banbenhao);
		ImageView iv_erweima = (ImageView) findViewById(R.id.iv_erweima);

		BitmapUtils bitmapUtils = new BitmapUtils(AboutWeActivtiy.this);
		bitmapUtils.display(iv_erweima,
				SharedUtil.getStringValue(SharedCommon.MALL_QRCODE_IMG_AZ, ""));
		tv_banbenhao.setText("当前版本号："+getVersion());
		
		line_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
	}

	public String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
