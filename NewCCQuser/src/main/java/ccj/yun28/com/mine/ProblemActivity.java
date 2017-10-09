package ccj.yun28.com.mine;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;

/**
 * 问题反馈
 * 
 * @author meihuali
 * 
 */
public class ProblemActivity extends BaseActivity implements OnClickListener {

	private Dialog calldialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_problem);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_callkefu = (LinearLayout) findViewById(R.id.line_callkefu);
		LinearLayout line_yijianfankui = (LinearLayout) findViewById(R.id.line_yijianfankui);

		line_back.setOnClickListener(this);
		line_callkefu.setOnClickListener(this);
		line_yijianfankui.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_callkefu:
			View view = LayoutInflater.from(ProblemActivity.this).inflate(
					R.layout.dialog_call, null);
			TextView tv_dialog_cancel = (TextView) view
					.findViewById(R.id.tv_dialog_cancel);
			TextView tv_phone_num = (TextView) view
					.findViewById(R.id.tv_phone_num);
			tv_phone_num.setText("是否拨打： 400-806-9828");
			tv_dialog_cancel.setOnClickListener(this);
			tv_phone_num.setOnClickListener(this);
			calldialog = new Dialog(ProblemActivity.this, R.style.mDialogStyle);
			calldialog.setContentView(view);
			calldialog.setCanceledOnTouchOutside(false);
			calldialog.show();
			break;
		case R.id.line_yijianfankui:
			Intent intent = new Intent(ProblemActivity.this, YiJianFanKuiActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_dialog_cancel:
			calldialog.dismiss();
			break;
		case R.id.tv_phone_num:
			Intent callintent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ "4008069828"));
			startActivity(callintent);
			break;
		default:
			break;
		}
	}
}
