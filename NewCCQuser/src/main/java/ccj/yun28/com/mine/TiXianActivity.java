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
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.WebviewActivity;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 提现页
 * 
 * @author meihuali
 * 
 */
public class TiXianActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {

	private TextView tv_bank_name;
	private Dialog tixiandialog;
	private List<Map<String, String>> zhanghuJineList;
	private MyAdapter adapter;
	private TextView tv_tixian_type;
	private EditText et_tixianjine;
	private String tixian_type;
	private String tixianjine;
	private String card_id;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取账户金额信息成功
	private static final int HANDLER_ZHANGHUJINE_SUCCESS = 3;
	private static final int HANDLER_TIJIAODINDAN_SUCCESS = 4;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(TiXianActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(TiXianActivity.this, "当前网络出错,请检查网络", Toast.LENGTH_SHORT)
						.show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(TiXianActivity.this, msg.obj.toString().trim(),
						Toast.LENGTH_SHORT).show();
				break;
			// 获取订单详情数据成功
			case HANDLER_ZHANGHUJINE_SUCCESS:
				dissDialog();
				adapter.notifyDataSetChanged();
				break;
				// 提交订单详情数据成功
			case HANDLER_TIJIAODINDAN_SUCCESS:
				dissDialog();
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
		setContentView(R.layout.activity_tixian);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_tixian_shuoming = (LinearLayout) findViewById(R.id.line_tixian_shuoming);
		LinearLayout line_tixian_type = (LinearLayout) findViewById(R.id.line_tixian_type);
		tv_tixian_type = (TextView) findViewById(R.id.tv_tixian_type);
		tv_bank_name = (TextView) findViewById(R.id.tv_bank_name);
		LinearLayout line_weibangdingbank = (LinearLayout) findViewById(R.id.line_weibangdingbank);
		LinearLayout line_bangdingbank = (LinearLayout) findViewById(R.id.line_bangdingbank);
		et_tixianjine = (EditText) findViewById(R.id.et_tixianjine);
		TextView tv_querentixian = (TextView) findViewById(R.id.tv_querentixian);
		TextView tv_tixianjilu = (TextView) findViewById(R.id.tv_tixianjilu);
		zhanghuJineList = new ArrayList<Map<String, String>>();
		
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(TiXianActivity.this);

		if (getIntent() != null) {
			String num = getIntent().getStringExtra("num");
			if ("0".equals(num)) {
				line_weibangdingbank.setVisibility(View.VISIBLE);
				line_bangdingbank.setVisibility(View.GONE);
				tv_tixianjilu.setVisibility(View.GONE);
				;
			} else {
				line_weibangdingbank.setVisibility(View.GONE);
				line_bangdingbank.setVisibility(View.VISIBLE);
				tv_tixianjilu.setVisibility(View.VISIBLE);
			}
		}

		line_back.setOnClickListener(this);
		line_tixian_shuoming.setOnClickListener(this);
		line_tixian_type.setOnClickListener(this);
		line_weibangdingbank.setOnClickListener(this);
		line_bangdingbank.setOnClickListener(this);
		tv_querentixian.setOnClickListener(this);
		tv_tixianjilu.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_tixian_shuoming:
			String url = SharedUtil.getStringValue(
					SharedCommon.MALL_WITHDRAW_EXPLAIN, "");
			intent = new Intent(TiXianActivity.this, WebviewActivity.class);
			intent.putExtra("url", url);
			intent.putExtra("title", "提现说明");
			startActivity(intent);
			break;
		case R.id.line_tixian_type:
			showtixiantypeDialog();
			break;
		case R.id.line_weibangdingbank:
			intent = new Intent(TiXianActivity.this, AddBankCardActivity.class);
			startActivity(intent);
			break;
		case R.id.line_bangdingbank:
			intent = new Intent(TiXianActivity.this, BankCardListActivity.class);
			startActivityForResult(intent, 100);
			break;
		case R.id.tv_querentixian:
			if (canPost()) {
				showLoading();
				tixianHttpPost();
			}
			break;
		case R.id.tv_tixianjilu:
			intent = new Intent(TiXianActivity.this, TiXianRecordActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_dialog_cancel:
			tixiandialog.dismiss();
			break;

		default:
			break;
		}
	}

	private boolean canPost() {
		tixian_type = tv_tixian_type.getText().toString().trim();
		String bank_name = tv_bank_name.getText().toString().trim();
		tixianjine = et_tixianjine.getText().toString().trim();
		int yu = Integer.parseInt(tixianjine) % 100;
		if (TextUtils.isEmpty(tixian_type)) {
			Toast.makeText(TiXianActivity.this, "提现类型不能为空", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if ("请选择银行卡".equals(bank_name)) {
			Toast.makeText(TiXianActivity.this, "请选择银行卡", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (TextUtils.isEmpty(tixianjine)) {
			Toast.makeText(TiXianActivity.this, "提现金额不能为空", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (!"余额".equals(tixian_type)) {
			if (yu != 0) {
				Toast.makeText(TiXianActivity.this, "提现金额应为100或100的倍数",
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}

	// 提交提现信息
	private void tixianHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(TiXianActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(TiXianActivity.this));
		params.addBodyParameter("card_id", card_id);
		params.addBodyParameter("type", type);
		params.addBodyParameter("amount", tixianjine);
		httpUtils.send(HttpMethod.POST, JiekouUtils.TIXIANINFO, params,
				new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(HANDLER_NET_FAILURE);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				tixianJineListInfo(arg0.result);
			}
		});
	}

	protected void tixianJineListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {

				handler.sendEmptyMessage(HANDLER_TIJIAODINDAN_SUCCESS);
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

	// 体现类型dialog
	private void showtixiantypeDialog() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(TiXianActivity.this).inflate(
				R.layout.dialog_tixiantype, null);
		ListView lv = (ListView) view.findViewById(R.id.lv);
		TextView tv_dialog_cancel = (TextView) view
				.findViewById(R.id.tv_dialog_cancel);
		adapter = new MyAdapter();
		lv.setAdapter(adapter);
		showLoading();
		getZhanghuJineHttpPost();
		tv_dialog_cancel.setOnClickListener(this);
		lv.setOnItemClickListener(this);
		tixiandialog = new Dialog(TiXianActivity.this, R.style.mDialogStyle);
		tixiandialog.setContentView(view);
		tixiandialog.setCanceledOnTouchOutside(false);
		tixiandialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (data != null) {
			if (resultCode == 200) {
				String bank_name = data.getStringExtra("name");
				card_id = data.getStringExtra("card_id");
				tv_bank_name.setText(bank_name);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 获得账户各个金额接口
	private void getZhanghuJineHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(TiXianActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(TiXianActivity.this));
		httpUtils.send(HttpMethod.POST, JiekouUtils.TIXIANGEZIJIN, params,
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
				zhanghuJineList.clear();
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
			String code = json.getString("pay_code");
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

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return zhanghuJineList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(TiXianActivity.this).inflate(
						R.layout.item_tixian, null);
				holder.tv_title = (TextView) convertView
						.findViewById(R.id.tv_title);
				holder.tv_jine = (TextView) convertView
						.findViewById(R.id.tv_jine);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv_title.setText(zhanghuJineList.get(position).get("title")
					+ ":  ");
			holder.tv_jine.setText(zhanghuJineList.get(position).get("value"));

			return convertView;
		}
	}

	static class ViewHolder {
		TextView tv_title;
		TextView tv_jine;
	}

	// 加载中动画
	private Dialog loadingDialog;
	private String type;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(TiXianActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(TiXianActivity.this,
				R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(TiXianActivity.this, R.style.mDialogStyle);
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		tixiandialog.dismiss();
		type = zhanghuJineList.get(position).get("code");
		tv_tixian_type.setText(zhanghuJineList.get(position).get("title"));
	}
}
