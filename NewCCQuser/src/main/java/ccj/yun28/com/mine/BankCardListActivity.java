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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.mine.BankCardActivity.ViewHolder;
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
 * 体现跳转银行卡列表
 * 
 * @author meihuali
 * 
 */
public class BankCardListActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {
	private List<Map<String, String>> bankLisList;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取我的钱包信息成功
	private static final int HANDLER_WALLET_SUCCESS = 3;
	// token失效
	private static final int HANDLER_TOKEN_FAILURE = 4;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(BankCardListActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(BankCardListActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:

				dissDialog();
				Toast.makeText(BankCardListActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取我的钱包信息成功
			case HANDLER_WALLET_SUCCESS:
				dissDialog();
				adapter.notifyDataSetChanged();
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(BankCardListActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent intent = new Intent(BankCardListActivity.this,
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
		setContentView(R.layout.activity_bankcardlist);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		ListView lv = (ListView) findViewById(R.id.lv);
		bankLisList = new ArrayList<Map<String, String>>();

		adapter = new MyAdapter();
		lv.setAdapter(adapter);

		if (getIntent() != null) {
			showLoading();
			bankListHttpPost();
		}
		line_back.setOnClickListener(this);
		lv.setOnItemClickListener(this);
	}

	// 银行卡列表接口
		private void bankListHttpPost() {
			// TODO Auto-generated method stub
			HttpUtils httpUtils = new HttpUtils();
			Utils utils = new Utils();
			String[] verstring = utils.getVersionInfo(BankCardListActivity.this);

			RequestParams params = new RequestParams();
			params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
			params.addBodyParameter("api_v", "v3");
			params.addBodyParameter("token",
					new DButil().gettoken(BankCardListActivity.this));
			params.addBodyParameter("member_id",
					new DButil().getMember_id(BankCardListActivity.this));
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
					for (int i = 0; i < list.length(); i++) {
						JSONObject json = list.getJSONObject(i);
						bankListListInfo(json);
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
				handler.sendEmptyMessage(HANDLER_WALLET_SUCCESS);
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
					convertView = LayoutInflater.from(BankCardListActivity.this)
							.inflate(R.layout.item_bankcard, null);
					holder.tv_yinhang_name = (TextView) convertView
							.findViewById(R.id.tv_yinhang_name);
					holder.iv_bank = (ImageView) convertView
							.findViewById(R.id.iv_bank);
					holder.tv_card_num = (TextView) convertView
							.findViewById(R.id.tv_card_num);
					holder.tv_delete = (TextView) convertView
							.findViewById(R.id.tv_delete);
					holder.tv_delete.setVisibility(View.GONE);
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
				BitmapUtils bitmapUtils = new BitmapUtils(BankCardListActivity.this);
				bitmapUtils.display(holder.iv_bank, bankLisList.get(position).get(
						"bank_img"));
				// holder.tv_delete.setText(bankLisList.get(position).get("datetime"));
				return convertView;
			}
		}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		default:
			break;
		}
	}

	// 加载中动画
	private Dialog loadingDialog;
	private MyAdapter adapter;
	private String is_real_verify;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(BankCardListActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				BankCardListActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(BankCardListActivity.this,
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.putExtra("cardnum", bankLisList.get(position)
				.get("card_account"));
		intent.putExtra("card_id", bankLisList.get(position).get("id"));
		setResult(200, intent);
		finish();
	}

}
