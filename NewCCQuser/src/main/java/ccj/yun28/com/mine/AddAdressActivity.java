package ccj.yun28.com.mine;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
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
 * 我的-新增收获地址
 * 
 * @author meihuali
 * 
 */
public class AddAdressActivity extends BaseActivity implements OnClickListener {

	//名字
	private EditText et_name;
	private String name;
	//电话
	private EditText et_phone;
	private String phone;
	//省市区
	private TextView tv_ssq;
	private String address;
	//详细地址
	private EditText et_detailadress;
	private String xiangxi;
	//是否默认
	private CheckBox cb_mr;
	private String is_default = "1";

	//区id
	private String area_id;
	//市id
	private String city_id;

	private String type;
	private String address_id;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取信息成功
	private static final int HANDLER_ADDADDRESS_SUCCESS = 3;
	private static final int HANDLER_EDITGETADDRESS_SUCCESS = 4;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(AddAdressActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(AddAdressActivity.this, "当前网络出错,请检查网络", Toast.LENGTH_SHORT)
						.show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(AddAdressActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取地址信息成功
			case HANDLER_ADDADDRESS_SUCCESS:
				dissDialog();
				Intent intent = new Intent();
				setResult(101, intent);
				finish();
				break;
				// 获取地址信息成功
			case HANDLER_EDITGETADDRESS_SUCCESS:
				dissDialog();
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
		setContentView(R.layout.activity_addadress);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_save = (LinearLayout) findViewById(R.id.line_save);
		et_name = (EditText) findViewById(R.id.et_name);
		et_phone = (EditText) findViewById(R.id.et_phone);
		LinearLayout line_adress = (LinearLayout) findViewById(R.id.line_adress);
		tv_ssq = (TextView) findViewById(R.id.tv_ssq);
		et_detailadress = (EditText) findViewById(R.id.et_detailadress);
		cb_mr = (CheckBox) findViewById(R.id.cb_mr);
		
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(AddAdressActivity.this);

		if (getIntent() != null) {
			type = getIntent().getStringExtra("type");
		}
		if ("edit".equals(type)) {
			address_id = getIntent().getStringExtra("address_id");
			getOneAddressHttpPost(address_id);
		}
		
		line_back.setOnClickListener(this);
		line_save.setOnClickListener(this);
		line_adress.setOnClickListener(this);
	}

	//获取单个地址详情
	private void getOneAddressHttpPost(String address_id) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("address_id", address_id);
		httpUtils.send(HttpMethod.POST, JiekouUtils.GETONEADDRESS, params,
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
						getOneAddressListInfo(arg0.result);
					}
				});
	}

	protected void getOneAddressListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			String message = object.getString("message");
			if ("200".equals(code)) {
				JSONObject jsonObject = object.getJSONObject("data");
				String address1 = jsonObject.getString("address");
				String address_id1 = jsonObject.getString("address_id");
				String area_id1 = jsonObject.getString("area_id");
				String area_info1 = jsonObject.getString("area_info");
				String city_id1 = jsonObject.getString("city_id");
				String dlyp_id1 = jsonObject.getString("dlyp_id");
				String is_default1 = jsonObject.getString("is_default");
				String member_id1 = jsonObject.getString("member_id");
				String mob_phone1 = jsonObject.getString("mob_phone");
				String tel_phone1 = jsonObject.getString("tel_phone");
				String true_name1 = jsonObject.getString("true_name");
				
				area_id = area_id1;
				city_id = city_id1;
				address = area_info1;
				et_name.setText(true_name1);
				et_phone.setText(mob_phone1);
				tv_ssq.setText(area_info1);
				et_detailadress.setText(address1);
				if ("1".equals(is_default1)) {
					cb_mr.setChecked(true);
				}else {
					cb_mr.setChecked(false);
				}
				
				handler.sendEmptyMessage(HANDLER_EDITGETADDRESS_SUCCESS);
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
	

	private void editAddressHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("address_id", address_id);
		params.addBodyParameter("member_id",
				new DButil().getMember_id(AddAdressActivity.this));
		params.addBodyParameter("true_name", name);
		params.addBodyParameter("area_id", area_id);
		params.addBodyParameter("city_id", city_id);
		params.addBodyParameter("area_info", address);
		params.addBodyParameter("address", xiangxi);
		params.addBodyParameter("tel_phone", "");
		params.addBodyParameter("mob_phone", phone);
		boolean sfmr = cb_mr.isChecked();
		if (sfmr) {
			is_default = "1";
		}else{
			is_default = "0";
		}
		params.addBodyParameter("is_default", is_default);
		httpUtils.send(HttpMethod.POST, JiekouUtils.EDITADDRESS, params,
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
						editAddressListInfo(arg0.result);
					}
				});
	}

	protected void editAddressListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			String message = object.getString("message");
			if ("200".equals(code)) {
				handler.sendEmptyMessage(HANDLER_ADDADDRESS_SUCCESS);
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

	// 新增收货地址接口
	private void addAddressHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("member_id",
				new DButil().getMember_id(AddAdressActivity.this));
		params.addBodyParameter("true_name", name);
		params.addBodyParameter("area_id", area_id);
		params.addBodyParameter("city_id", city_id);
		params.addBodyParameter("area_info", address);
		params.addBodyParameter("address", xiangxi);
		params.addBodyParameter("tel_phone", "");
		params.addBodyParameter("mob_phone", phone);
		boolean sfmr = cb_mr.isChecked();
		if (sfmr) {
			is_default = "1";
		}else{
			is_default = "0";
		}
		params.addBodyParameter("is_default", is_default);
		httpUtils.send(HttpMethod.POST, JiekouUtils.ADDADDRESS, params,
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
						addAddressListInfo(arg0.result);
					}
				});
	}

	protected void addAddressListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			String message = object.getString("message");
			if ("200".equals(code)) {
				handler.sendEmptyMessage(HANDLER_ADDADDRESS_SUCCESS);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_save:
			if (cansave()) {
				if ("edit".equals(type)) {
					showLoading();
					editAddressHttpPost();
				}else{
					showLoading();
					addAddressHttpPost();
				}
			}
			break;
		case R.id.line_adress:
			Intent intent = new Intent(AddAdressActivity.this, ChooseShengShiQuActicity.class);
			startActivityForResult(intent, 100);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (data != null) {
			if (resultCode == 200) {
				area_id = data.getStringExtra("area_id");
				city_id = data.getStringExtra("city_id");
				address = data.getStringExtra("area_info");
				tv_ssq.setText(address);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
		
	}

	//是否填写完成
	private boolean cansave() {
		name = et_name.getText().toString().trim();
		phone = et_phone.getText().toString().trim();
		address = tv_ssq.getText().toString().trim();
		xiangxi = et_detailadress.getText().toString().trim();
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(AddAdressActivity.this, "收货人姓名不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}else if (TextUtils.isEmpty(phone)) {
			Toast.makeText(AddAdressActivity.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}else if (phone.length() != 11) {
			Toast.makeText(AddAdressActivity.this, "手机格式不正确", Toast.LENGTH_SHORT).show();
			return false;
		}else if (TextUtils.isEmpty(address)) {
			Toast.makeText(AddAdressActivity.this, "所在地区不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}else if (TextUtils.isEmpty(xiangxi)) {
			Toast.makeText(AddAdressActivity.this, "详细地址不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	// 加载中动画
	private Dialog loadingDialog;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(AddAdressActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				AddAdressActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(AddAdressActivity.this, R.style.mDialogStyle);
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
