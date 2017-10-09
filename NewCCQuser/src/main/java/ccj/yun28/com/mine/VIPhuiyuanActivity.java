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
import android.widget.RelativeLayout;
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
 * 我的-VIP会员
 * 
 * @author meihuali
 * 
 */
public class VIPhuiyuanActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {

	private List<Map<String, String>> infoList;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取我的信息成功
	private static final int HANDLER_INFO_SUCCESS = 3;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(VIPhuiyuanActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(VIPhuiyuanActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(VIPhuiyuanActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取我的信息成功
			case HANDLER_INFO_SUCCESS:
				dissDialog();
				adapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viphuiyuan);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		tv_weikaitong = (TextView) findViewById(R.id.tv_weikaitong);
		rela_yikaitong = (RelativeLayout) findViewById(R.id.rela_yikaitong);
		tv_vip_num = (TextView) findViewById(R.id.tv_vip_num);
		tv_vip_time = (TextView) findViewById(R.id.tv_vip_time);
		ListView lv = (ListView) findViewById(R.id.lv);
		LinearLayout line_duihuanvip = (LinearLayout) findViewById(R.id.line_duihuanvip);
		infoList = new ArrayList<Map<String, String>>();
		adapter = new MyAdapter();
		lv.setAdapter(adapter);

		showLoading();
		vipHuiYuanHttpPost();

		line_back.setOnClickListener(this);
		line_duihuanvip.setOnClickListener(this);
		lv.setOnItemClickListener(this);
	}

	// 获取会员信息
	private void vipHuiYuanHttpPost() {
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(VIPhuiyuanActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(VIPhuiyuanActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(VIPhuiyuanActivity.this));
		httpUtils.send(HttpMethod.POST, JiekouUtils.VIPINFO, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						infoListInfo(arg0.result);
					}
				});
	}

	protected void infoListInfo(String result) {
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				infoList.clear();
				JSONObject data = object.getJSONObject("data");
				String button = data.getString("button");
				String is_vip = data.getString("is_vip");
				JSONArray list = data.getJSONArray("list");
				JSONArray list2 = data.getJSONArray("list2");
				String member_name = data.getString("member_name");
				String vip_endtime = data.getString("vip_endtime");
				String vip_starttime = data.getString("vip_starttime");
				String vip_time = data.getString("vip_time");

				if ("0".equals(is_vip)) {
					tv_weikaitong.setVisibility(View.VISIBLE);
					rela_yikaitong.setVisibility(View.GONE);
					tv_weikaitong.setText("为账户" + member_name + "开通VIP服务");
				} else if ("1".equals(is_vip)) {
					tv_weikaitong.setVisibility(View.GONE);
					rela_yikaitong.setVisibility(View.VISIBLE);
					tv_vip_num.setText(member_name);
					tv_vip_time.setText(vip_time);
				}

				for (int i = 0; i < list.length(); i++) {
					JSONObject json = list.getJSONObject(i);
					infolistListInfo(json);
				}

				handler.sendEmptyMessage(HANDLER_INFO_SUCCESS);
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

	private void infolistListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String button = json.getString("button");
			String gift = json.getString("gift");
			String mon = json.getString("mon");
			String num = json.getString("num");
			String price = json.getString("price");
			String price_name = json.getString("price_name");
			Map<String, String> infoMap = new HashMap<String, String>();
			infoMap.put("button", button);
			infoMap.put("gift", gift);
			infoMap.put("mon", mon);
			infoMap.put("num", num);
			infoMap.put("price", price);
			infoMap.put("price_name", price_name);
			infoList.add(infoMap);
			handler.sendEmptyMessage(HANDLER_INFO_SUCCESS);
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
		case R.id.line_duihuanvip:
			Intent intent = new Intent(VIPhuiyuanActivity.this,
					YunBiDuiHuanVIPActivity.class);
			startActivityForResult(intent, 200);
			break;

		default:
			break;
		}
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infoList.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(VIPhuiyuanActivity.this)
						.inflate(R.layout.item_viu_huiyuan, null);
				holder.tv_time = (TextView) convertView
						.findViewById(R.id.tv_time);
				holder.tv_price = (TextView) convertView
						.findViewById(R.id.tv_price);
				holder.tv_youhui = (TextView) convertView
						.findViewById(R.id.tv_youhui);
				holder.tv_anniu = (TextView) convertView
						.findViewById(R.id.tv_anniu);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tv_time.setText(infoList.get(position).get("mon"));
			holder.tv_price.setText(infoList.get(position).get("price_name"));
			holder.tv_youhui.setText(infoList.get(position).get("gift"));
			holder.tv_anniu.setText(infoList.get(position).get("button"));

			return convertView;
		}
	}

	static class ViewHolder {
		TextView tv_time;
		TextView tv_price;
		TextView tv_youhui;
		TextView tv_anniu;
	}

	// 加载中动画
	private Dialog loadingDialog;
	private TextView tv_weikaitong;
	private RelativeLayout rela_yikaitong;
	private TextView tv_vip_num;
	private TextView tv_vip_time;
	private MyAdapter adapter;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(VIPhuiyuanActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				VIPhuiyuanActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(VIPhuiyuanActivity.this,
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(VIPhuiyuanActivity.this,
				BuyVIPShouYinTaiActivtity.class);
		intent.putExtra("jine", infoList.get(arg2).get("price_name"));
		intent.putExtra("num", infoList.get(arg2).get("num"));
		startActivityForResult(intent, 200);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 201) {
			showLoading();
			vipHuiYuanHttpPost();
		}
	}
}
