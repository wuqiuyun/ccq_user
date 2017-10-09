package ccj.yun28.com.mine;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import ccj.yun28.com.R;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 云币购买VIP收银台
 * 
 * @author meihuali
 * 
 */
public class YunBiShouYingTaiActivity extends BaseActivity implements
		OnClickListener {

	//云币
	private String yunbicode;
	//招商
	private String zhaoshangcode;
	//推广
	private String tuiguangcode;
	//vip
	private String vipcode;

	private TextView tv_yunbijine;
	private TextView tv_zhaoshangjine;
	private TextView tv_tuiguangjine;
	private TextView tv_vipjine;
	private Dialog mimaDialog;
	private EditText et_password;
	private String pay_code;

	private List<Map<String, String>> zhanghuJineList;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取账户金额信息成功
	private static final int HANDLER_ZHANGHUJINE_SUCCESS = 3;
	//检查密码成功
	private static final int HANDLER_CHECKPASSWORD_SUCCESS = 4;
	//提交订单成功
	private static final int HANDLER_TJDD_SUCCESS = 5;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(YunBiShouYingTaiActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(YunBiShouYingTaiActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(YunBiShouYingTaiActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				}
				break;
			// 获取订单详情数据成功
			case HANDLER_ZHANGHUJINE_SUCCESS:
				dissDialog();
				for (int i = 0; i < zhanghuJineList.size(); i++) {
					if ("云币支付".equals(zhanghuJineList.get(i).get("title"))) {
						yunbicode = zhanghuJineList.get(i).get("code");
						tv_yunbijine.setText(zhanghuJineList.get(i).get("value"));
					}else if ("招商佣金支付".equals(zhanghuJineList.get(i).get("title"))) {
						zhaoshangcode = zhanghuJineList.get(i).get("code");
						tv_zhaoshangjine.setText(zhanghuJineList.get(i).get("value"));
					}else if ("推广佣金支付".equals(zhanghuJineList.get(i).get("title"))) {
						tuiguangcode = zhanghuJineList.get(i).get("code");
						tv_tuiguangjine.setText(zhanghuJineList.get(i).get("value"));
					}else if ("vip佣金支付".equals(zhanghuJineList.get(i).get("title"))) {
						vipcode = zhanghuJineList.get(i).get("code");
						tv_vipjine.setText(zhanghuJineList.get(i).get("value"));
					}
				}
				break;
			case HANDLER_CHECKPASSWORD_SUCCESS:
				tjddHttpPost();
				break;
			case HANDLER_TJDD_SUCCESS:
				dissDialog();
//				Intent intent = new Intent(YunBiShouYingTaiActivity.this, VIPhuiyuanActivity.class);
//				startActivity(intent);
				setResult(200);
				finish();
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
		setContentView(R.layout.activity_yunbiduihuanshouyintai);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		TextView tv_jine = (TextView) findViewById(R.id.tv_jine);
		// 云币支付
		LinearLayout line_yunbizhifu = (LinearLayout) findViewById(R.id.line_yunbizhifu);
		tv_yunbijine = (TextView) findViewById(R.id.tv_yunbijine);
		// 招商佣金支付
		LinearLayout line_zhaoshangzhifu = (LinearLayout) findViewById(R.id.line_zhaoshangzhifu);
		tv_zhaoshangjine = (TextView) findViewById(R.id.tv_zhaoshangjine);
		// 推广佣金支付
		LinearLayout line_tuiguangzhifu = (LinearLayout) findViewById(R.id.line_tuiguangzhifu);
		tv_tuiguangjine = (TextView) findViewById(R.id.tv_tuiguangjine);
		// VIP佣金支付
		LinearLayout line_vipzhifu = (LinearLayout) findViewById(R.id.line_vipzhifu);
		tv_vipjine = (TextView) findViewById(R.id.tv_vipjine);

		zhanghuJineList = new ArrayList<Map<String, String>>();
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(YunBiShouYingTaiActivity.this);

		if (getIntent() != null) {
			String jine = getIntent().getStringExtra("jine");
			num = getIntent().getStringExtra("num");
			tv_jine.setText(jine);
		}
		showLoading();
		getZhanghuJineHttpPost();

		line_back.setOnClickListener(this);
		line_yunbizhifu.setOnClickListener(this);
		line_zhaoshangzhifu.setOnClickListener(this);
		line_tuiguangzhifu.setOnClickListener(this);
		line_vipzhifu.setOnClickListener(this);
	}

	// 获得账户各个金额接口
	private void getZhanghuJineHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(YunBiShouYingTaiActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(YunBiShouYingTaiActivity.this));
		httpUtils.send(HttpMethod.POST, JiekouUtils.GETZHANGHUJINE, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e("ee", "失败:" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						getZhanghuJineListInfo(arg0.result);
					}
				});
	}

	// 获得账户各个金额数据解析
	protected void getZhanghuJineListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONArray array = object.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = array.getJSONObject(i);
					getZhanghuJineDetailListInfo(json);
				}
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

	private void getZhanghuJineDetailListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String code = json.getString("code");
			String title = json.getString("title");
			String value = json.getString("value");
			Map<String, String> zhanghuJineMap = new HashMap<String, String>();
			zhanghuJineMap.put("code", code);
			zhanghuJineMap.put("title", title);
			zhanghuJineMap.put("value", value);
			zhanghuJineList.add(zhanghuJineMap);
			handler.sendEmptyMessage(HANDLER_ZHANGHUJINE_SUCCESS);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 提交订单接口
		private void tjddHttpPost() {
			// TODO Auto-generated method stub
			RequestParams params = new RequestParams();
			params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
			params.addBodyParameter("token",
					new DButil().gettoken(YunBiShouYingTaiActivity.this));
			params.addBodyParameter("member_id",
					new DButil().getMember_id(YunBiShouYingTaiActivity.this));
			params.addBodyParameter("ifcart", "4");
			params.addBodyParameter("cart_id", "101552|" + num);
			params.addBodyParameter("address_id", "1");
			params.addBodyParameter("pay_name", "online");
			params.addBodyParameter("pay_code", pay_code);
			httpUtils.send(HttpMethod.POST, JiekouUtils.TIJIAODINDAN, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
							Log.e("ee", "失败:" + arg1);
							handler.sendEmptyMessage(HANDLER_NET_FAILURE);
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							// TODO Auto-generated method stub
							tjddListInfo(arg0.result);
						}
					});
		}

		// 提交订单数据解析
		protected void tjddListInfo(String result) {
			// TODO Auto-generated method stub
			try {
				JSONObject object = new JSONObject(result);
				String code = object.getString("code");
				if ("200".equals(code)) {
					String pay_sn = object.getString("pay_sn");
					String order_sn = object.getString("order_sn");
					JSONObject pay_info = object.getJSONObject("pay_info");
					String goods_name = pay_info.getString("goods_name");
					String order_id = pay_info.getString("order_id");
					String order_amount = pay_info.getString("order_amount");
					handler.sendEmptyMessage(HANDLER_TJDD_SUCCESS);
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
	private String num;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(YunBiShouYingTaiActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				YunBiShouYingTaiActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(YunBiShouYingTaiActivity.this,
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_yunbizhifu:
			pay_code = yunbicode;
			showmimaLoading();
			break;
		case R.id.line_zhaoshangzhifu:
			pay_code = zhaoshangcode;
			showmimaLoading();
			break;
		case R.id.line_tuiguangzhifu:
			pay_code = tuiguangcode;
			showmimaLoading();
			break;
		case R.id.line_vipzhifu:
			pay_code = vipcode;
			showmimaLoading();
			break;
		case R.id.tv_dialog_cancel:
			mimaDialog.dismiss();
			break;
		case R.id.tv_ok:
			mimaDialog.dismiss();
			if (canmima()) {
				showLoading();
				checkPwdHttpPost();
			}
			break;
		default:
			break;
		}
	}
	
	private boolean canmima() {
		// TODO Auto-generated method stub
		String mima = et_password.getText().toString().trim();
		if ("".equals(mima) || mima == null) {
			Toast.makeText(YunBiShouYingTaiActivity.this, "密码不能为空哦",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (mima.length() < 6) {
			Toast.makeText(YunBiShouYingTaiActivity.this, "密码不能少于六位哦",
					Toast.LENGTH_SHORT).show();
			return false;

		} else if (mima.length() > 20) {
			Toast.makeText(YunBiShouYingTaiActivity.this, "密码不能多于二十位哦",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	//检查密码
	private void checkPwdHttpPost() {
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(YunBiShouYingTaiActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(YunBiShouYingTaiActivity.this));
		params.addBodyParameter("paypwd", MD5(et_password.getText().toString()
				.trim()));
		httpUtils.send(HttpMethod.POST, JiekouUtils.CHECKZHIFUMIMA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e("ee", "失败:" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						checkpasswordListInfo(arg0.result);
					}
				});
	}

	protected void checkpasswordListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				handler.sendEmptyMessage(HANDLER_CHECKPASSWORD_SUCCESS);
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


	
	// 密码框
		private void showmimaLoading() {
			View view = LayoutInflater.from(YunBiShouYingTaiActivity.this).inflate(
					R.layout.dialog_tianxiedindan, null);
			et_password = (EditText) view.findViewById(R.id.et_password);
			TextView tv_dialog_cancel = (TextView) view
					.findViewById(R.id.tv_dialog_cancel);
			TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
			tv_dialog_cancel.setOnClickListener(this);
			tv_ok.setOnClickListener(this);
			mimaDialog = new Dialog(YunBiShouYingTaiActivity.this,
					R.style.mDialogStyle);
			mimaDialog.setContentView(view);
			mimaDialog.setCanceledOnTouchOutside(false);
			mimaDialog.show();
		}

}
