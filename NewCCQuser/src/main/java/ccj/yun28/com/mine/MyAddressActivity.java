package ccj.yun28.com.mine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.MyAdressAdapter;
import ccj.yun28.com.adapter.MyAdressAdapter.EditAddressDateListener;
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
 * 我的——收货地址
 * 
 * @author meihuali
 * 
 */
public class MyAddressActivity extends BaseActivity implements OnClickListener, OnItemClickListener, EditAddressDateListener {

	private List<Map<String, String>> addressList;
	private MyAdressAdapter adressAdapter;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取信息成功
	private static final int HANDLER_ADDRESS_SUCCESS = 3;

	private static final int HANDLER_DELETEADDRESS_SUCCESS = 4;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(MyAddressActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(MyAddressActivity.this, "当前网络出错,请检查网络", Toast.LENGTH_SHORT)
						.show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(MyAddressActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取地址信息成功
			case HANDLER_ADDRESS_SUCCESS:
				dissDialog();
				adressAdapter.NotifyList(addressList);
				adressAdapter.notifyDataSetChanged();
				break;
			// 删除地址信息成功
			case HANDLER_DELETEADDRESS_SUCCESS:
				addressHttpPost();
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
		setContentView(R.layout.activity_myaddress);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 收货地址
		ListView lv = (ListView) findViewById(R.id.lv);
		// 新增收货地址
		TextView tv_add_new_adress = (TextView) findViewById(R.id.tv_add_new_adress);

		addressList = new ArrayList<Map<String, String>>();

		adressAdapter = new MyAdressAdapter(MyAddressActivity.this);
		lv.setAdapter(adressAdapter);

		adressAdapter.setEditAddressDateListener(this);
		
		if (getIntent() != null) {
			type  = getIntent().getStringExtra("type");
		}
		showLoading();
		addressHttpPost();

		line_back.setOnClickListener(this);
		tv_add_new_adress.setOnClickListener(this);
		lv.setOnItemClickListener(this);
	}

	private void addressHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(MyAddressActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(MyAddressActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(MyAddressActivity.this));
		httpUtils.send(HttpMethod.POST, JiekouUtils.MYADDRESS, params,
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
						addressListInfo(arg0.result);
					}
				});
	}

	protected void addressListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				addressList.clear();
				JSONArray data = object.getJSONArray("data");
				for (int i = 0; i < data.length(); i++) {
					JSONObject json = data.getJSONObject(i);
					addressDetailListInfo(json);
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

	private void addressDetailListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String address = json.getString("address");
			String address_id = json.getString("address_id");
			String area_id = json.getString("area_id");
			String area_info = json.getString("area_info");
			String city_id = json.getString("city_id");
			String dlyp_id = json.getString("dlyp_id");
			String is_default = json.getString("is_default");
			String member_id = json.getString("member_id");
			String mob_phone = json.getString("mob_phone");
			String tel_phone = json.getString("tel_phone");
			String true_name = json.getString("true_name");
			Map<String, String> addressMap = new HashMap<String, String>();
			addressMap.put("address", address);
			addressMap.put("address_id", address_id);
			addressMap.put("area_id", area_id);
			addressMap.put("area_info", area_info);
			addressMap.put("city_id", city_id);
			addressMap.put("dlyp_id", dlyp_id);
			addressMap.put("is_default", is_default);
			addressMap.put("member_id", member_id);
			addressMap.put("mob_phone", mob_phone);
			addressMap.put("tel_phone", tel_phone);
			addressMap.put("true_name", true_name);
			addressList.add(addressMap);
			handler.sendEmptyMessage(HANDLER_ADDRESS_SUCCESS);
		} catch (Exception e) {
			// TODO: handle exception
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
		case R.id.tv_add_new_adress:
			Intent intent = new Intent(MyAddressActivity.this,
					AddAdressActivity.class);
			intent.putExtra("type", "add");
			startActivityForResult(intent, 300);
			break;

		default:
			break;
		}
	}

	// 加载中动画
	private Dialog loadingDialog;
	private String type = "";

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(MyAddressActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				MyAddressActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(MyAddressActivity.this, R.style.mDialogStyle);
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(type)) {
			String address_id = addressList.get(position).get("address_id");
			String area_info = addressList.get(position).get("area_info");
			String true_name = addressList.get(position).get("true_name");
			String mob_phone = addressList.get(position).get("mob_phone");
			String address = addressList.get(position).get("address");
			
			Intent intent = new Intent();
			intent.putExtra("address_id", address_id);
			intent.putExtra("area_info", area_info);
			intent.putExtra("true_name", true_name);
			intent.putExtra("mob_phone", mob_phone);
			intent.putExtra("address", address);
			setResult(101, intent);
			finish();
		}
	}

	@Override
	public void editAddressDate(String type, String address_id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(MyAddressActivity.this, AddAdressActivity.class);
		intent.putExtra("type", type);
		intent.putExtra("address_id", address_id);
		startActivityForResult(intent, 200);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 101) {
			showLoading();
			addressHttpPost();
		}
	}
}
