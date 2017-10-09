package ccj.yun28.com.mine;

import java.security.MessageDigest;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.MainActivity1;
import ccj.yun28.com.R;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.db.DBHelper;
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
 * 我的 - 账户安全 - 设置登陆密码
 * 
 * @author meihuali
 * 
 */
public class SetLoginmimaActivity extends BaseActivity implements
		OnClickListener {
	private DBHelper myDB;
	private String[] infos;
	private EditText et_newmm;
	private EditText et_again_newmm;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取我的信息成功
	private static final int HANDLER_SETLOGINMIMA_SUCCESS = 3;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(SetLoginmimaActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(SetLoginmimaActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(SetLoginmimaActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取我的信息成功
			case HANDLER_SETLOGINMIMA_SUCCESS:
				dissDialog();
				showLoginDialog();
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
		setContentView(R.layout.activity_setloginmima);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		et_newmm = (EditText) findViewById(R.id.et_newmm);
		et_again_newmm = (EditText) findViewById(R.id.et_again_newmm);
		TextView tv_ok = (TextView) findViewById(R.id.tv_ok);

		myDB = new DBHelper(this);
		infos = getUserInfo();

		line_back.setOnClickListener(this);
		tv_ok.setOnClickListener(this);
	}

	protected void showLoginDialog() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(SetLoginmimaActivity.this).inflate(
				R.layout.dialog_querenshouhuo, null);
		TextView tv_url = (TextView) view.findViewById(R.id.tv_url);
		TextView tv_dialog_cancel = (TextView) view
				.findViewById(R.id.tv_dialog_cancel);
		TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
		tv_url.setText("修改登录密码成功");
		tv_ok.setText("重新登录");
		final Dialog logindialog = new Dialog(SetLoginmimaActivity.this,
				R.style.mDialogStyle);
		logindialog.setContentView(view);
		logindialog.setCanceledOnTouchOutside(false);
		logindialog.show();

		tv_dialog_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				logindialog.dismiss();
				SharedUtil.saveStringValue(SharedCommon.DBDH, "me");
				SQLiteDatabase db = myDB.getWritableDatabase();
				db.execSQL("update user set status=? where member_name=?",
						new Object[] { 0, infos[1] });
				db.close();
				Intent intent = new Intent(SetLoginmimaActivity.this,
						MainActivity1.class);
				intent.putExtra("type", "me");
				startActivity(intent);
				finish();
			}
		});
		tv_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				logindialog.dismiss();
				SharedUtil.saveStringValue(SharedCommon.DBDH, "me");
				SQLiteDatabase db = myDB.getWritableDatabase();
				db.execSQL("update user set status=? where member_name=?",
						new Object[] { 0, infos[1] });
				db.close();
				Intent intent = new Intent(SetLoginmimaActivity.this,
						LoginActivity.class);
				intent.putExtra("type", "againme");
				startActivity(intent);
				finish();
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.tv_ok:
			if (canPost()) {
				showLoading();
				setLoginMiMaHttpPost();
			}
			break;

		default:
			break;
		}
	}

	// 设置登录密码
	private void setLoginMiMaHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(SetLoginmimaActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(SetLoginmimaActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(SetLoginmimaActivity.this));
		params.addBodyParameter("member_pwd", MD5(mima));
		params.addBodyParameter("type", "2");
		httpUtils.send(HttpMethod.POST, JiekouUtils.UPDATAMIMA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						setLoginMiMaListInfo(arg0.result);
					}
				});
	}

	protected void setLoginMiMaListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			String message = object.getString("message");
			if ("200".equals(code)) {
				handler.sendEmptyMessage(HANDLER_SETLOGINMIMA_SUCCESS);
			} else {
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETINFO_FAILURE, message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private boolean canPost() {
		mima = et_newmm.getText().toString().trim();
		String querenmima = et_again_newmm.getText().toString().trim();
		if (TextUtils.isEmpty(mima)) {
			Toast.makeText(SetLoginmimaActivity.this, "新密码不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(querenmima)) {
			Toast.makeText(SetLoginmimaActivity.this, "再次输入密码不能为空",
					Toast.LENGTH_SHORT).show();
			return false;

		} else if (mima.length() < 6) {
			Toast.makeText(SetLoginmimaActivity.this, "新密码不能少与6位",
					Toast.LENGTH_SHORT).show();
			return false;

		} else if (mima.length() > 20) {
			Toast.makeText(SetLoginmimaActivity.this, "新密码不能多与20位",
					Toast.LENGTH_SHORT).show();
			return false;

		} else if (!mima.equals(querenmima)) {
			Toast.makeText(SetLoginmimaActivity.this, "两次密码不一致",
					Toast.LENGTH_SHORT).show();
			return false;

		}
		return true;
	}

	// MD5加密，32位
	public static String MD5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	// 获取用户信息
	private String[] getUserInfo() {
		String info[] = new String[2];
		SQLiteDatabase db = myDB.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from user where status=1", null);
		if (cursor.moveToFirst()) {
			info[0] = cursor
					.getString(cursor.getColumnIndex("member_truename"));// 昵称
			info[1] = cursor.getString(cursor.getColumnIndex("member_name"));// 用户名
		}
		return info;
	}

	// 加载中动画
	private Dialog loadingDialog;
	private String mima;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(SetLoginmimaActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				SetLoginmimaActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(SetLoginmimaActivity.this,
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
