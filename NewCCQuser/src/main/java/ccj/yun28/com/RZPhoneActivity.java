package ccj.yun28.com;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 忘记密码-认证手机号
 * 
 * @author meihuali
 * 
 */
public class RZPhoneActivity extends BaseActivity implements OnClickListener {
	// 认证手机号输入框
	private EditText et_rzphone;
	private String type = "";
	private String rzphone;

	public static RZPhoneActivity rzintance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rzphone);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 认证手机号输入框
		et_rzphone = (EditText) findViewById(R.id.et_rzphone);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		// 下一步
		TextView tv_next = (TextView) findViewById(R.id.tv_next);

		rzintance = this;

		if (getIntent() != null) {
			String title = getIntent().getStringExtra("title");
			type = getIntent().getStringExtra("type");
			tv_title.setText(title);
		}

		line_back.setOnClickListener(this);
		tv_next.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.tv_next:
			if (canPost()) {
				if ("phone".equals(type)) {
					Intent intent = new Intent(RZPhoneActivity.this,
							BindPhoneActivtiy.class);
					intent.putExtra("rzphone", rzphone);
					startActivity(intent);
				} else if ("wjmm".equals(type)) {
					Intent intent = new Intent(RZPhoneActivity.this,
							FindPasswordActivtiy.class);
					intent.putExtra("rzphone", rzphone);
					startActivity(intent);
				}
			}
			break;

		default:
			break;
		}
	}

	// 检查输入框填写状态
	private boolean canPost() {
		rzphone = et_rzphone.getText().toString().trim();
		if (TextUtils.isEmpty(rzphone)) {
			Toast.makeText(RZPhoneActivity.this, "认证手机号不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (rzphone.length() < 11) {
			Toast.makeText(RZPhoneActivity.this, "认证手机号不能少于11位",
					Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

}
