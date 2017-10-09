package ccj.yun28.com.mine;

import java.util.List;
import java.util.Map;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.ccq.CCQStoreDetailActivity;
import ccj.yun28.com.ccq.OldCCQStoreDetailActivity;
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
 * 查看券码
 * 
 * @author meihuali
 * 
 */
public class ChaKanJuanMaActivity extends BaseActivity implements OnClickListener {
	private List<Map<String, String>> lookWuLiuList;
	private ImageView iv_pic;
	private TextView tv_shopname;
	private TextView tv_yzm;
	private TextView tv_sfsy;
	private TextView tv_yxq;
	private String store_id;
	private String order_id="";

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 错误
	private static final int HANDLER_NN_FAILURE = 1;
	// 待收货
	private static final int HANDLER_CHAKANJUANMA_SUCCESS = 2;
	private static final int HANDLER_ALLDINDAN_FAILURE = 3;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(ChaKanJuanMaActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(ChaKanJuanMaActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_ALLDINDAN_FAILURE:
				dissDialog();
				Toast.makeText(ChaKanJuanMaActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取信息成功
			case HANDLER_CHAKANJUANMA_SUCCESS:
				dissDialog();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chakanjuanma);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_ccqstore = (LinearLayout) findViewById(R.id.line_ccqstore);
		iv_pic = (ImageView) findViewById(R.id.iv_pic);
		tv_shopname = (TextView) findViewById(R.id.tv_shopname);
		tv_yzm = (TextView) findViewById(R.id.tv_yzm);
		tv_sfsy = (TextView) findViewById(R.id.tv_sfsy);
		tv_yxq = (TextView) findViewById(R.id.tv_yxq);
		
		if (getIntent() != null) {
			 order_id = getIntent().getStringExtra("order_id");
			showLoading();
			chakanJuanmaHttpPost(order_id);
		}
		line_back.setOnClickListener(this);
		line_ccqstore.setOnClickListener(this);
	}

	// 获取查看券码信息接口
	private void chakanJuanmaHttpPost(String order_id) {
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(ChaKanJuanMaActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("order_id", order_id);
		httpUtils.send(HttpMethod.POST, JiekouUtils.CHAKANJUANMA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						chakanjuanmaListInfo(arg0.result);

					}
				});
	}

	protected void chakanjuanmaListInfo(String result) {
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONObject data = object.getJSONObject("data");
				String buyer_id = data.getString("buyer_id");
				String check_number = data.getString("check_number");
				String check_number_img = data.getString("check_number_img");
				String order_id = data.getString("order_id");
				String order_sn = data.getString("order_sn");
				String order_state = data.getString("order_state");
				String payment_time = data.getString("payment_time");
				store_id = data.getString("store_id");
				String store_name = data.getString("store_name");
				String validity = data.getString("validity");
				tv_shopname.setText(store_name);
				tv_yzm.setText(check_number);
				tv_yxq.setText(validity);
				//0(已取消)10(默认):待付款;20:已付款;30:待收货;40:已完成;
				if ("0".equals(order_state)) {
					tv_sfsy.setText("已取消");
				}else if ("10".equals(order_state)) {
					tv_sfsy.setText("待付款");
				}else if ("20".equals(order_state)) {
					tv_sfsy.setText("已付款");
				}else if ("30".equals(order_state)) {
					tv_sfsy.setText("待收货");
				}else if ("40".equals(order_state)) {
					tv_sfsy.setText("已完成");
				}
				BitmapUtils bitmapUtils = new BitmapUtils(ChaKanJuanMaActivity.this);
				bitmapUtils.display(iv_pic, check_number_img);
				handler.sendEmptyMessage(HANDLER_CHAKANJUANMA_SUCCESS);
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

	// 加载中动画
	private Dialog loadingDialog;
	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(ChaKanJuanMaActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				ChaKanJuanMaActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(ChaKanJuanMaActivity.this,
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
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_ccqstore://CCQDinDanDetailActivity
//			Intent intent = new Intent(ChaKanJuanMaActivity.this, CCQStoreDetailActivity.class);
			Intent intent = new Intent(ChaKanJuanMaActivity.this, CCQStoreDetailActivity.class);
			intent.putExtra("store_id", store_id);
			startActivity(intent);
			
			//跳转到券码详情页
			/*Intent intent = new Intent(ChaKanJuanMaActivity.this, CCQDinDanDetailActivity.class);
			intent.putExtra("order_id", order_id);
			startActivity(intent);*/
			
			break;

		default:
			break;
		}
	}
}
