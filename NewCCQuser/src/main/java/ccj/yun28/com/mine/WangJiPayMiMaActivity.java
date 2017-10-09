package ccj.yun28.com.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;

/**
 * 忘记支付密码
 * @author meihuali
 *
 */
public class WangJiPayMiMaActivity extends BaseActivity implements OnClickListener{

		// 认证手机号输入框
		private EditText et_rzphone;
		private String rzphone;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_wangjipaymima);
			// 返回
			LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
			// 认证手机号输入框
			et_rzphone = (EditText) findViewById(R.id.et_rzphone);
			// 下一步
			TextView tv_next = (TextView) findViewById(R.id.tv_next);

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
					Intent intent = new Intent(WangJiPayMiMaActivity.this,
							FindPayPasswordActivtiy.class);
					intent.putExtra("rzphone", rzphone);
					startActivity(intent);
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
				Toast.makeText(WangJiPayMiMaActivity.this, "认证手机号不能为空",
						Toast.LENGTH_SHORT).show();
				return false;
			}if (rzphone.length() != 11) {
				Toast.makeText(WangJiPayMiMaActivity.this, "认证手机号应该为11位",
						Toast.LENGTH_SHORT).show();
				return false;
			}

			return true;
		}

	}
