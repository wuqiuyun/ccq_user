package ccj.yun28.com.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
/**
 * 修改支付密码
 * @author meihuali
 *
 */
public class XiuGaiPayMiMaActivity extends BaseActivity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xiugaipaymima);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_forgetmima = (LinearLayout) findViewById(R.id.line_forgetmima);
		LinearLayout line_updatemima = (LinearLayout) findViewById(R.id.line_updatemima);
		line_back.setOnClickListener(this);
		line_forgetmima.setOnClickListener(this);
		line_updatemima.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_forgetmima:
			intent = new Intent(XiuGaiPayMiMaActivity.this, WangJiPayMiMaActivity.class);
			startActivity(intent);
			break;
		case R.id.line_updatemima:
			intent = new Intent(XiuGaiPayMiMaActivity.this, XiuGaiZhiFuMiMaActivity.class);
			startActivity(intent);
			
			break;

		default:
			break;
		}
	}
	
}
