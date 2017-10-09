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
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.LookWuLiuAdapter;
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
 * 查看物流
 * 
 * @author meihuali
 * 
 */
public class ChaKanWuLiuActivity extends BaseActivity implements OnClickListener {
	private List<Map<String, String>> lookWuLiuList;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 错误
	private static final int HANDLER_NN_FAILURE = 1;
	// 待收货
	private static final int HANDLER_LOOKWULIU_SUCCESS = 2;
	private static final int HANDLER_ALLDINDAN_FAILURE = 3;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(ChaKanWuLiuActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(ChaKanWuLiuActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_ALLDINDAN_FAILURE:
				dissDialog();
				Toast.makeText(ChaKanWuLiuActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取信息成功
			case HANDLER_LOOKWULIU_SUCCESS:
				dissDialog();
				lookWuLiuAdapter.NotifyList(lookWuLiuList);
				lookWuLiuAdapter.notifyDataSetChanged();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chakanwuliu);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		iv_pic = (ImageView) findViewById(R.id.iv_pic);
		tv_wlzt = (TextView) findViewById(R.id.tv_wlzt);
		tv_cyly = (TextView) findViewById(R.id.tv_cyly);
		tv_ydbh = (TextView) findViewById(R.id.tv_ydbh);
		tv_gfdh = (TextView) findViewById(R.id.tv_gfdh);
		ListView lv = (ListView) findViewById(R.id.lv);
		
		lookWuLiuList = new ArrayList<Map<String, String>>();
		
		lookWuLiuAdapter = new LookWuLiuAdapter(ChaKanWuLiuActivity.this);
		lv.setAdapter(lookWuLiuAdapter);
		
		if (getIntent() != null) {
			showLoading();
			String order_id = getIntent().getStringExtra("order_id");
			wuliuInfoHttpPost(order_id);
		}
		
		line_back.setOnClickListener(this);
	}

	// 获取物流信息接口
	private void wuliuInfoHttpPost(String order_id) {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(ChaKanWuLiuActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(ChaKanWuLiuActivity.this));
		params.addBodyParameter("order_id", order_id);
		httpUtils.send(HttpMethod.POST, JiekouUtils.CHAKANWULIU, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						chakanWuLiuListInfo(arg0.result);

					}
				});
	}

	protected void chakanWuLiuListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONObject info = object.getJSONObject("info");
				String img = info.getString("img");
				String shipping_name = info.getString("shipping_name");
				String shipping_sn = info.getString("shipping_sn");
				String status = info.getString("status");
				String tel = info.getString("tel");
				tv_wlzt.setText(status);
				tv_cyly.setText(shipping_name);
				tv_ydbh.setText(shipping_sn);
				tv_gfdh.setText(tel);
				BitmapUtils bitmapUtils = new BitmapUtils(ChaKanWuLiuActivity.this);
				bitmapUtils.display(iv_pic, img);
				JSONArray array = object.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = array.getJSONObject(i);
					chakanwuliuDetailListInfo(json);
				}
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(HANDLER_ALLDINDAN_FAILURE, message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private void chakanwuliuDetailListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String context = json.getString("context");
			String ftime = json.getString("ftime");
			String time = json.getString("time");
			Map<String, String> lookWuLiuMap = new HashMap<String, String>();
			lookWuLiuMap.put("context", context);
			lookWuLiuMap.put("ftime", ftime);
			lookWuLiuMap.put("time", time);
			lookWuLiuList.add(lookWuLiuMap);
			handler.sendEmptyMessage(HANDLER_LOOKWULIU_SUCCESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 加载中动画
	private Dialog loadingDialog;
	private LookWuLiuAdapter lookWuLiuAdapter;
	private ImageView iv_pic;
	private TextView tv_wlzt;
	private TextView tv_cyly;
	private TextView tv_ydbh;
	private TextView tv_gfdh;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(ChaKanWuLiuActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				ChaKanWuLiuActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(ChaKanWuLiuActivity.this,
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

		default:
			break;
		}
	}
}
