package ccj.yun28.com.gwc;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
 * 购物车-支付收银台
 * 
 * @author meihuali
 * 
 */
public class GwcShouYinTaiActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	private MyAdapter adapter;
	private List<Map<String, String>> zhanghuJineList;
	private LinearLayout line_one;
	private ListView lv;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取账户金额信息成功
	private static final int HANDLER_ZHANGHUJINE_SUCCESS = 3;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				line_one.setVisibility(View.VISIBLE);
				lv.setVisibility(View.GONE);
				dissDialog();
				Toast.makeText(GwcShouYinTaiActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				line_one.setVisibility(View.VISIBLE);
				lv.setVisibility(View.GONE);
				dissDialog();
				Toast.makeText(GwcShouYinTaiActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				line_one.setVisibility(View.VISIBLE);
				lv.setVisibility(View.GONE);
				dissDialog();
				Toast.makeText(GwcShouYinTaiActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取订单详情数据成功
			case HANDLER_ZHANGHUJINE_SUCCESS:
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
		setContentView(R.layout.activity_gwcshouyintai);
		line_one = (LinearLayout) findViewById(R.id.line_one);
		LinearLayout line_more = (LinearLayout) findViewById(R.id.line_more);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 金额
		TextView tv_jine = (TextView) findViewById(R.id.tv_jine);
		LinearLayout line_zfbzf = (LinearLayout) findViewById(R.id.line_zfbzf);
		LinearLayout line_wxzf = (LinearLayout) findViewById(R.id.line_wxzf);

		lv = (ListView) findViewById(R.id.lv);
		zhanghuJineList = new ArrayList<Map<String, String>>();
		adapter = new MyAdapter();
		lv.setAdapter(adapter);
		
		showLoading();
		getZhanghuJineHttpPost();

		if (getIntent() != null) {
			String jine = getIntent().getStringExtra("jine").toString().trim();
			tv_jine.setText("¥ " + jine);
		}
		line_back.setOnClickListener(this);
		line_more.setOnClickListener(this);
		line_zfbzf.setOnClickListener(this);
		line_wxzf.setOnClickListener(this);
		lv.setOnItemClickListener(this);
	}

	// 获得账户各个金额接口
	private void getZhanghuJineHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(GwcShouYinTaiActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(GwcShouYinTaiActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(GwcShouYinTaiActivity.this));
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
				convertView = LayoutInflater.from(GwcShouYinTaiActivity.this)
						.inflate(R.layout.item_zhanghujine, null);
				holder.tv_title = (TextView) convertView
						.findViewById(R.id.tv_title);
				holder.tv_jine = (TextView) convertView
						.findViewById(R.id.tv_jine);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv_title.setText(zhanghuJineList.get(position).get("title"));
			holder.tv_jine.setText(zhanghuJineList.get(position).get("value"));

			return convertView;
		}
	}

	static class ViewHolder {
		TextView tv_title;
		TextView tv_jine;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_more:
			line_one.setVisibility(View.GONE);
			lv.setVisibility(View.VISIBLE);
			adapter.notifyDataSetChanged();
			break;
		case R.id.line_zfbzf:
			Intent intent = new Intent();
			intent.putExtra("zffs", "支付宝");
			intent.putExtra("zffs_id", zhanghuJineList.get(0).get("code"));
			setResult(300, intent);
			finish();
			break;
		case R.id.line_wxzf:
			Intent intent1 = new Intent();
			intent1.putExtra("zffs", "微信支付");
			intent1.putExtra("zffs_id", zhanghuJineList.get(1).get("code"));
			setResult(300, intent1);
			finish();
			break;
		default:
			break;
		}
	}

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(GwcShouYinTaiActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				GwcShouYinTaiActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(GwcShouYinTaiActivity.this,
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		Intent intent2 = new Intent();
		intent2.putExtra("zffs_id", zhanghuJineList.get(position).get("code"));
		intent2.putExtra("zffs", zhanghuJineList.get(position).get("title"));
		setResult(300, intent2);
		finish();
	}

}
