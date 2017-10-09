package ccj.yun28.com.sy;

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
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.MainActivity1;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.WuliiulvAdapter;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.mine.ProblemActivity;
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
 * 物流助手
 * 
 * @author meihuali
 * 
 */
public class WuLiuActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {

	// 更多图标
	private ImageView iv_more;
	// 弹窗
	private PopupWindow popupWindow;
	// 加载中动画
	private Dialog dialog;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;

	private static final int HANDLER_WULIU_SUCCESS = 1;
	private static final int HANDLER_GETINFO_FAILURE = 2;
	private static final int HANDLER_NN_FAILURE = 3;
	private static final int HANDLER_TOKEN_FAILURE = 4;

	private List<Map<String, String>> wuliuList;
	private WuliiulvAdapter wladapter;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				Toast.makeText(WuLiuActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_WULIU_SUCCESS:
				dissDialog();
				wladapter.NotifyList(wuliuList);
				wladapter.notifyDataSetChanged();
				break;
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(WuLiuActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {

					Toast.makeText(WuLiuActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(WuLiuActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent intent = new Intent(WuLiuActivity.this,
						LoginActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}

		};
	};
	private TextView tv_not_data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wuliu);
		tv_not_data = (TextView) findViewById(R.id.tv_not_data);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_more = (LinearLayout) findViewById(R.id.line_more);
		iv_more = (ImageView) findViewById(R.id.iv_more);
		ListView lv = (ListView) findViewById(R.id.lv);

		wladapter = new WuliiulvAdapter(WuLiuActivity.this);
		lv.setAdapter(wladapter);

		wuliuList = new ArrayList<Map<String, String>>();

		showLoading();
		wlHttpPost();

		line_back.setOnClickListener(this);
		line_more.setOnClickListener(this);
		lv.setOnItemClickListener(this);
	}

	// 获取物流信息接口
	private void wlHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(WuLiuActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(WuLiuActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(WuLiuActivity.this));
		httpUtils.send(HttpMethod.POST, JiekouUtils.XIAOXI, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						wlListInfo(arg0.result);
					}
				});
	}

	// 解析物流接口返回的数据
	protected void wlListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONObject job = object.getJSONObject("list");
				// //系统消息
				// JSONArray message_type1 = job.getJSONArray("message_type1");
				// 物流消息
				JSONArray message_type2 = job.getJSONArray("message_type2");
				if (message_type2.length() < 1) {
					tv_not_data.setVisibility(View.VISIBLE);
				}
				for (int i = 0; i < message_type2.length(); i++) {
					JSONObject json = message_type2.getJSONObject(i);
					String goods_images = json.getString("goods_images");
					String is_read = json.getString("is_read");
					String kuaidi = json.getString("kuaidi");
					String message_body = json.getString("message_body");
					String message_id = json.getString("message_id");
					String message_time = json.getString("message_time");
					String message_title = json.getString("message_title");
					String message_type = json.getString("message_type");
					String order_state = json.getString("order_state");
					String read_member_id = json.getString("read_member_id");
					String to_member_id = json.getString("to_member_id");
					Map<String, String> wuliumap = new HashMap<String, String>();
					wuliumap.put("goods_images", goods_images);
					wuliumap.put("is_read", is_read);
					wuliumap.put("kuaidi", kuaidi);
					wuliumap.put("message_body", message_body);
					wuliumap.put("message_id", message_id);
					wuliumap.put("message_time", message_time);
					wuliumap.put("message_title", message_title);
					wuliumap.put("message_type", message_type);
					wuliumap.put("order_state", order_state);
					wuliumap.put("read_member_id", read_member_id);
					wuliumap.put("to_member_id", to_member_id);
					wuliuList.add(wuliumap);
				}
				handler.sendEmptyMessage(HANDLER_WULIU_SUCCESS);
				// // 活动消息
				// JSONArray message_type3 = job.getJSONArray("message_type3");
			} else if ("700".equals(code)) {
				String message = object.getString("message");
				handler.sendEmptyMessage(HANDLER_TOKEN_FAILURE);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_more:
			View view = View.inflate(WuLiuActivity.this,
					R.layout.popupwindow_message, null);

			TextView tv_home = (TextView) view.findViewById(R.id.tv_home);
			TextView tv_search = (TextView) view.findViewById(R.id.tv_search);
			TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
			TextView tv_problem = (TextView) view.findViewById(R.id.tv_problem);

			tv_home.setOnClickListener(this);
			tv_search.setOnClickListener(this);
			tv_message.setOnClickListener(this);
			tv_problem.setOnClickListener(this);

			popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, true);
			popupWindow.setTouchable(true);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setFocusable(true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.showAsDropDown(iv_more);
			break;
		case R.id.tv_home:
			popupWindow.dismiss();
			intent = new Intent(WuLiuActivity.this, MainActivity1.class);
			SharedUtil.saveStringValue(SharedCommon.DBDH, "ccq");
			intent.putExtra("type", "ccq");
			startActivity(intent);
			finish();
			break;
		case R.id.tv_search:
			popupWindow.dismiss();
			intent = new Intent(WuLiuActivity.this, SearchActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.tv_message:
			popupWindow.dismiss();
			intent = new Intent(WuLiuActivity.this, MessageActivcity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.tv_problem:
			popupWindow.dismiss();
			intent = new Intent(WuLiuActivity.this, ProblemActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(WuLiuActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(WuLiuActivity.this,
				R.anim.loading_anim);
		image.startAnimation(animation);

		dialog = new Dialog(WuLiuActivity.this, R.style.mDialogStyle);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

	}

	// 关闭加载dialog
	private void dissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(WuLiuActivity.this,
				MessageEveryDetailActivtiy.class);
		intent.putExtra("message_title", wuliuList.get(position).get("kuaidi"));
		intent.putExtra("message_body",
				wuliuList.get(position).get("message_body"));
		intent.putExtra("message_time",
				wuliuList.get(position).get("message_time"));
		intent.putExtra("message_id", wuliuList.get(position).get("message_id"));
		startActivity(intent);
	}

}
