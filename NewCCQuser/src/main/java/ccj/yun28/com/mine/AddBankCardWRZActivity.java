package ccj.yun28.com.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
/**
 * 添加银行卡   未认证
 * @author meihuali
 *
 */
public class AddBankCardWRZActivity extends BaseActivity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addbankcardwrz);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		TextView tv_go_shiming = (TextView) findViewById(R.id.tv_go_shiming);
		line_back.setOnClickListener(this);
		tv_go_shiming.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.tv_go_shiming:
			Intent intent = new Intent(AddBankCardWRZActivity.this, NewWeiShiMingRenZhengActivity.class);
			intent.putExtra("title", "实名认证");
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}
