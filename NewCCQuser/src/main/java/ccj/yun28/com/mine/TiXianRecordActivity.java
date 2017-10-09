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
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.MyListView;
import ccj.yun28.com.view.MyScrollView;
import ccj.yun28.com.view.OnRefreshListener;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 提现记录页面
 * 
 * @author meihuali
 * 
 */
public class TiXianRecordActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {

	private int nowPage = 1;
	private MyScrollView scrollView;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	private static final int HANDLER_TIXIANRECORD_SUCCESS = 3;
	// token失效
	private static final int HANDLER_TOKEN_FAILURE = 4;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(TiXianRecordActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(TiXianRecordActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				scrollView.onResfreshFinish();
				dissDialog();
				Toast.makeText(TiXianRecordActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_TIXIANRECORD_SUCCESS:
				scrollView.onResfreshFinish();
				dissDialog();
				adapter.notifyDataSetChanged();
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(TiXianRecordActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent intent = new Intent(TiXianRecordActivity.this,
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
		setContentView(R.layout.activity_tixianrecord);
		scrollView = (MyScrollView) findViewById(R.id.scrollView);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		MyListView lv = (MyListView) findViewById(R.id.lv);
		tixianRecordList = new ArrayList<Map<String, String>>();
		adapter = new MyAdapter();
		lv.setAdapter(adapter);
		showLoading();
		tixianRecordHttpPost();

		initListView();
		line_back.setOnClickListener(this);
		lv.setOnItemClickListener(this);
	}

	private void tixianRecordHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(TiXianRecordActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v","v3");
		params.addBodyParameter("token",
				new DButil().gettoken(TiXianRecordActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(TiXianRecordActivity.this));
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.TIXIANLIST, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						tixianRecordListInfo(arg0.result);
					}
				});
	}

	protected void tixianRecordListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONArray array = object.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject jsonObject = array.getJSONObject(i);
					tixianRecordListInfo(jsonObject);
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

	private void tixianRecordListInfo(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		try {
			String id = jsonObject.getString("id");
			String time = jsonObject.getString("time");
			String amount = jsonObject.getString("amount");
			String w_sn = jsonObject.getString("w_sn");
			String status = jsonObject.getString("status");
			Map<String, String> tixianRecordMap = new HashMap<String, String>();
			tixianRecordMap.put("id", id);
			tixianRecordMap.put("time", time);
			tixianRecordMap.put("amount", amount);
			tixianRecordMap.put("w_sn", w_sn);
			tixianRecordMap.put("status", status);
			tixianRecordList.add(tixianRecordMap);
			handler.sendEmptyMessage(HANDLER_TIXIANRECORD_SUCCESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return tixianRecordList.size();
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
				convertView = LayoutInflater.from(TiXianRecordActivity.this)
						.inflate(R.layout.item_tixian_record, null);
				holder.tv_tixianjine = (TextView) convertView
						.findViewById(R.id.tv_tixianjine);
				holder.tv_biaohao = (TextView) convertView
						.findViewById(R.id.tv_biaohao);
				holder.tv_time = (TextView) convertView
						.findViewById(R.id.tv_time);
				holder.tv_status = (TextView) convertView
						.findViewById(R.id.tv_status);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tv_status.setText(tixianRecordList.get(position).get("status"));
			holder.tv_tixianjine.setText("提现金额: "
					+ tixianRecordList.get(position).get("amount") + "元");
			holder.tv_biaohao.setText("提现流水编号: "
					+ tixianRecordList.get(position).get("w_sn"));
			holder.tv_time.setText("申请时间: "
					+ tixianRecordList.get(position).get("time"));

			return convertView;
		}
	}

	static class ViewHolder {
		TextView tv_tixianjine;
		TextView tv_biaohao;
		TextView tv_time;
		TextView tv_status;
	}

	// 加载中动画
	private Dialog loadingDialog;
	private List<Map<String, String>> tixianRecordList;
	private MyAdapter adapter;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(TiXianRecordActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				TiXianRecordActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(TiXianRecordActivity.this,
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

	/**
	 * @description 填充底部商品listview
	 **/
	private void initListView() {
		scrollView.getView();
		scrollView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadMoring() {
				// TODO Auto-generated method stub
				try {
					nowPage++;
					tixianRecordHttpPost();
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(HANDLER_NN_FAILURE);
				}

			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(TiXianRecordActivity.this,
				TiXianTecordDetailActivtiy.class);
		intent.putExtra("id", tixianRecordList.get(arg2).get("id"));
		startActivity(intent);
	}
}
