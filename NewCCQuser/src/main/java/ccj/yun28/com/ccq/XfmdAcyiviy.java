package ccj.yun28.com.ccq;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;

/**
 * 餐餐抢-消费买单页
 * 
 * @author meihuali
 * 
 */
public class XfmdAcyiviy extends BaseActivity implements OnClickListener,
		TextWatcher {

	// 消费总额
	private EditText et_xfze;
	// 消费折扣
	private String union_pay_discount;
	// 实付款
	private TextView tv_sfk;
	private String store_id;
	
	public static XfmdAcyiviy xfmdintance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xfmd);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 标题-店铺名
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		// 全店折扣
		TextView tv_qdzk = (TextView) findViewById(R.id.tv_qdzk);
		// 消费总额
		et_xfze = (EditText) findViewById(R.id.et_xfze);
		// 实付款
		tv_sfk = (TextView) findViewById(R.id.tv_sfk);
		// 确认买单
		TextView tv_qrmd = (TextView) findViewById(R.id.tv_qrmd);
		
		xfmdintance = this;
		
		if (getIntent() != null) {
			
			union_pay_discount = getIntent().getStringExtra("union_pay_discount");
			
			store_id = getIntent().getStringExtra("store_id");
			
			tv_qdzk.setText(union_pay_discount + " 折");
		}

		et_xfze.addTextChangedListener(this);

		line_back.setOnClickListener(this);
		tv_qrmd.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.tv_qrmd:
			String shijijine = tv_sfk.getText().toString().trim();
			if (!TextUtils.isEmpty(shijijine)) {
				Intent intent = new Intent(XfmdAcyiviy.this,
						XfmdShouYinTaiActivity.class);
				intent.putExtra("jine", shijijine);
				intent.putExtra("yuanjia", et_xfze.getText().toString().trim());
				intent.putExtra("store_id", store_id);
				startActivity(intent);

			} else {
				Toast.makeText(XfmdAcyiviy.this, "请输入消费金额", Toast.LENGTH_SHORT)
						.show();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {// 原有的文本s中，从start开始的before(0为增加，1为删除)个字符将会被一个新的长度为count的文本替换
		// TODO Auto-generated method stub
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
		String string = s.toString();
		if (!TextUtils.isEmpty(string)) {
			int i = string.indexOf(".");
			int length = string.length();
			if (i != -1 && (length - i) > 3) {// 只允许有两位小数
				string = string.substring(0, i + 3);
				et_xfze.setText(string);
			}
			if (!string.equals("") || string != null){
				et_xfze.setSelection(string.length());//将光标移至文字末尾
			}
			double jine = Double.parseDouble(string);
			double zhekou = Double.parseDouble(union_pay_discount);
			
			double shijiyine = jine * (zhekou/10);
//			String shiji = new BigDecimal(shijiyine) + "";
			
			tv_sfk.setText(new DecimalFormat("0.00").format(shijiyine));
		}else{
			tv_sfk.setText("");
		}
	}

}