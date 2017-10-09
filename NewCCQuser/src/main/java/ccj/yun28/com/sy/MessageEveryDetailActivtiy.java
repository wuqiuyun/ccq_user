package ccj.yun28.com.sy;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.MainActivity1;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.WuliiulvAdapter;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.mine.ProblemActivity;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 每条消息的详情
 * 
 * @author meihuali
 * 
 */
public class MessageEveryDetailActivtiy extends BaseActivity implements
		OnClickListener {

	private ImageView iv_more;
	// 弹窗
	private PopupWindow popupWindow;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;

	private static final int HANDLER_GETINFO_FAILURE = 2;
	private static final int HANDLER_NN_FAILURE = 3;
	private static final int HANDLER_TOKEN_FAILURE = 4;

	private List<Map<String, String>> wuliuList;
	private WuliiulvAdapter wladapter;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				Toast.makeText(MessageEveryDetailActivtiy.this,
						"当前网络不可用,请检查网络", Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(MessageEveryDetailActivtiy.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {

					Toast.makeText(MessageEveryDetailActivtiy.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(MessageEveryDetailActivtiy.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent intent = new Intent(MessageEveryDetailActivtiy.this,
						LoginActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messageeverydetail);

		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_more = (LinearLayout) findViewById(R.id.line_more);
		iv_more = (ImageView) findViewById(R.id.iv_more);

		TextView tv_message_type = (TextView) findViewById(R.id.tv_message_type);
		TextView tv_message_detail = (TextView) findViewById(R.id.tv_message_detail);
		TextView tv_message_time = (TextView) findViewById(R.id.tv_message_time);

		String message_title = getIntent().getStringExtra("message_title")
				.toString().trim();
		String message_body = getIntent().getStringExtra("message_body")
				.toString().trim();
		String message_time = getIntent().getStringExtra("message_time")
				.toString().trim();
		String message_id = getIntent().getStringExtra("message_id").toString()
				.trim();

		tv_message_type.setText(message_title);
		tv_message_detail.setText(message_body);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssE",
				Locale.getDefault());
		// 当前时间对象
		Date curDate = new Date((Integer.parseInt(message_time)) * 1000L);
		String defaultTimeZoneID = TimeZone.getDefault().getID();// America/New_York
		// System.out.println("默认时区(美国/纽约)：" + defaultTimeZoneID);
		// System.out.println("默认时区(美国/纽约)：" + format.format(curDate));

		// 在格式化日期前使用一个新的时区
		String newTimeZoneID = "Asia/Shanghai"; // Asia/Shanghai
		format.setTimeZone(TimeZone.getTimeZone(newTimeZoneID));
		// System.out.println("新的时区(中国/上海)：" + newTimeZoneID);
		// System.out.println("新的时区(中国/上海)：" + format.format(curDate));
		tv_message_time.setText(format.format(curDate));
		showLoading();
		messageReadHttpPost(message_id);

		line_back.setOnClickListener(this);
		line_more.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_more:
			View view = View.inflate(MessageEveryDetailActivtiy.this,
					R.layout.popupwindow_message, null);

			TextView tv_home = (TextView) view.findViewById(R.id.tv_home);
			TextView tv_search = (TextView) view.findViewById(R.id.tv_search);
			TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
			TextView tv_problem = (TextView) view.findViewById(R.id.tv_problem);

			tv_home.setOnClickListener(this);
			tv_search.setOnClickListener(this);
			tv_message.setOnClickListener(this);
			tv_problem.setOnClickListener(this);

			popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, true);
			popupWindow.setTouchable(true);
			popupWindow.setOutsideTouchable(false);
			popupWindow.setFocusable(true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.showAsDropDown(iv_more);
			break;
		case R.id.tv_home:
			popupWindow.dismiss();
			intent = new Intent(MessageEveryDetailActivtiy.this,
					MainActivity1.class);
			SharedUtil.saveStringValue(SharedCommon.DBDH, "ccq");
			intent.putExtra("type", "ccq");
			startActivity(intent);
			finish();
			break;
		case R.id.tv_search:
			popupWindow.dismiss();
			intent = new Intent(MessageEveryDetailActivtiy.this,
					SearchActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.tv_message:
			popupWindow.dismiss();
			intent = new Intent(MessageEveryDetailActivtiy.this,
					MessageActivcity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.tv_problem:
			popupWindow.dismiss();
			intent = new Intent(MessageEveryDetailActivtiy.this,
					ProblemActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}

	// 消息已读
	private void messageReadHttpPost(String message_id) {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(MessageEveryDetailActivtiy.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(MessageEveryDetailActivtiy.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(MessageEveryDetailActivtiy.this));
		params.addBodyParameter("message_id", message_id);
		httpUtils.send(HttpMethod.POST, JiekouUtils.XIAOXIYIDU, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						messageReadInfo(arg0.result);
					}
				});
	}

	protected void messageReadInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				dissDialog();
			} else if ("700".equals(code)) {
				String message = object.getString("message");
				handler.sendEmptyMessage(HANDLER_TOKEN_FAILURE);
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETINFO_FAILURE, message));

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}

	}

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(MessageEveryDetailActivtiy.this)
				.inflate(R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				MessageEveryDetailActivtiy.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(MessageEveryDetailActivtiy.this,
				R.style.mDialogStyle);
		loadingDialog.setContentView(view);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.show();
	}

	// 关闭加载dialog
	private void dissDialog() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
	}
}
