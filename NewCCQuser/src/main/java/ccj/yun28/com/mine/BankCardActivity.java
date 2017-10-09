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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;


/**
 * 银行卡列表详情页
 * 
 * @author meihuali
 * 
 */
public class BankCardActivity extends BaseActivity implements OnClickListener {

	private List<Map<String, String>> bankLisList;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();
	private String delid;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取银行卡信息成功
	private static final int HANDLER_BANKLIST_SUCCESS = 3;
	// 删除银行卡成功
	private static final int HANDLER_DELETECARD_SUCCESS = 4;
	// token失效
	private static final int HANDLER_TOKEN_FAILURE = 5;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(BankCardActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(BankCardActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:

				dissDialog();
				Toast.makeText(BankCardActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取我的钱包信息成功
			case HANDLER_BANKLIST_SUCCESS:
				dissDialog();
				adapter.notifyDataSetChanged();
				break;
			// 删除银行卡成功
			case HANDLER_DELETECARD_SUCCESS:
				bankListHttpPost();
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(BankCardActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent intent = new Intent(BankCardActivity.this,
						LoginActivity.class);
				intent.putExtra("type", "qt");
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
		setContentView(R.layout.activity_bankcard);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_addbank = (LinearLayout) findViewById(R.id.line_addbank);
		TextView tv_weibangdingbank = (TextView) findViewById(R.id.tv_weibangdingbank);
		lv = (ListView) findViewById(R.id.lv);
		bankLisList = new ArrayList<Map<String, String>>();
		
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(BankCardActivity.this);

		adapter = new MyAdapter();
		lv.setAdapter(adapter);

		if (getIntent() != null) {
			String num = getIntent().getStringExtra("num");
			if ("0".equals(num)) {
				tv_weibangdingbank.setVisibility(View.VISIBLE);
				lv.setVisibility(View.GONE);
			} else {
				tv_weibangdingbank.setVisibility(View.GONE);
				lv.setVisibility(View.VISIBLE);
				showLoading();
				bankListHttpPost();
			}
		}
		line_back.setOnClickListener(this);
		line_addbank.setOnClickListener(this);
	}

	// 银行卡列表接口
	private void bankListHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token",
				new DButil().gettoken(BankCardActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(BankCardActivity.this));
		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWBANKLIST, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						bankListJsonInfo(arg0.result);
					}
				});
	}

	protected void bankListJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				bankLisList.clear();
				JSONObject data = object.getJSONObject("data");
				is_real_verify = data.getString("is_real_verify");
				JSONArray list = data.getJSONArray("list");
				if (list.length() > 0) {
					for (int i = 0; i < list.length(); i++) {
						JSONObject json = list.getJSONObject(i);
						bankListListInfo(json);
					}
				}else{
					handler.sendEmptyMessage(HANDLER_BANKLIST_SUCCESS);
				}
			} else if ("700".equals(code)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TOKEN_FAILURE, message));
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

	private void bankListListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String bank_name = json.getString("bank_name");
			String bank_img = json.getString("bank_img");
			String card_account = json.getString("card_account");
			String card_id = json.getString("id");
			Map<String, String> bankLisMap = new HashMap<String, String>();
			bankLisMap.put("bank_name", bank_name);
			bankLisMap.put("bank_img", bank_img);
			bankLisMap.put("card_account", card_account);
			bankLisMap.put("card_id", card_id);
			bankLisList.add(bankLisMap);
			handler.sendEmptyMessage(HANDLER_BANKLIST_SUCCESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return bankLisList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (temp.get(position) == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(BankCardActivity.this)
						.inflate(R.layout.item_bankcard, null);
				holder.tv_yinhang_name = (TextView) convertView
						.findViewById(R.id.tv_yinhang_name);
				holder.iv_bank = (ImageView) convertView
						.findViewById(R.id.iv_bank);
				holder.tv_card_num = (TextView) convertView
						.findViewById(R.id.tv_card_num);
				holder.tv_delete = (TextView) convertView
						.findViewById(R.id.tv_delete);
				temp.put(position, convertView);
				convertView.setTag(holder);
			} else {
				convertView = temp.get(position);
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv_yinhang_name.setText(bankLisList.get(position).get(
					"bank_name"));
			holder.tv_card_num.setText(bankLisList.get(position).get(
					"card_account"));
			BitmapUtils bitmapUtils = new BitmapUtils(BankCardActivity.this);
			bitmapUtils.display(holder.iv_bank, bankLisList.get(position).get(
					"bank_img"));
			// holder.tv_delete.setText(bankLisList.get(position).get("datetime"));
			holder.tv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					delid = bankLisList.get(position).get("card_id");
					showDeleteCardDialog();

				}
			});
			return convertView;
		}
	}

	// 删除银行卡dialog
	private Dialog delDialog;

	private void showDeleteCardDialog() {
		View view = LayoutInflater.from(BankCardActivity.this).inflate(
				R.layout.dialog_red_moban, null);
		TextView dialog_text = (TextView) view.findViewById(R.id.dialog_text);
		tv_ok = (TextView) view.findViewById(R.id.tv_ok);
		tv_dialog_cancel = (TextView) view.findViewById(R.id.tv_dialog_cancel);
		dialog_text.setText("亲，是否删除？\n\n银行卡删除只能重新添加了哦！");
		tv_ok.setOnClickListener(this);
		tv_dialog_cancel.setOnClickListener(this);
		delDialog = new Dialog(BankCardActivity.this, R.style.mDialogStyle);
		delDialog.setContentView(view);
		delDialog.setCanceledOnTouchOutside(false);
		delDialog.show();
	}

	// 银行卡列表接口
	private void deleteBankCardHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token",
				new DButil().gettoken(BankCardActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(BankCardActivity.this));
		params.addBodyParameter("card_id", delid);
		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWDELETEBANK, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						deleteBankCardJsonInfo(arg0.result);
					}
				});
	}

	protected void deleteBankCardJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				handler.sendEmptyMessage(HANDLER_DELETECARD_SUCCESS);
			} else if ("700".equals(code)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TOKEN_FAILURE, message));
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

	static class ViewHolder {
		TextView tv_yinhang_name;
		ImageView iv_bank;
		TextView tv_card_num;
		TextView tv_delete;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			Intent resintent = new Intent();
			setResult(200, resintent);
			finish();
			break;
		case R.id.line_addbank:
			// 是否实名认证 实名认证状态 0 未认证   1已认证   2待认证3认证不通过
			if ("1".equals(is_real_verify)) {
				// 1已认证   2待认证3认证不通过
				Intent intent = new Intent(BankCardActivity.this,
						NewAddBankCardActivity.class);
				startActivityForResult(intent, 100);
			} else if ("0".equals(is_real_verify)) {
				Intent wrzintent = new Intent(BankCardActivity.this,
						AddBankCardWRZActivity.class);
				startActivity(wrzintent);
			} else {
				Intent wrzintent = new Intent(BankCardActivity.this,
						ShiMingRenZhengActivity.class);
				startActivity(wrzintent);
			}
			break;
		case R.id.tv_ok:

			delDialog.dismiss();
			showLoading();
			deleteBankCardHttpPost();
			break;
		case R.id.tv_dialog_cancel:
			delDialog.dismiss();
			break;

		default:
			break;
		}
	}

	// 加载中动画
	private Dialog loadingDialog;
	private MyAdapter adapter;
	private String is_real_verify;
	private TextView tv_ok;
	private TextView tv_dialog_cancel;
	private ListView lv;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(BankCardActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				BankCardActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(BankCardActivity.this, R.style.mDialogStyle);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 300) {
			showLoading();
			bankListHttpPost();
		}
	}
}
