package ccj.yun28.com.mine;

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
import ccj.yun28.com.MainActivity;
import ccj.yun28.com.MainActivity1;
import ccj.yun28.com.R;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 支付成功页面
 * @author meihuali
 *
 */
public class ZhifuSuccessActivity extends BaseActivity implements OnClickListener{

	private String order_id;
	private String type;

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
					Toast.makeText(ZhifuSuccessActivity.this, "当前网络不可用,请检查网络",
							Toast.LENGTH_SHORT).show();
					break;
				// 错误
				case HANDLER_NN_FAILURE:
					dissDialog();
					Toast.makeText(ZhifuSuccessActivity.this, "当前网络出错,请检查网络",
							Toast.LENGTH_SHORT).show();
					break;
				case HANDLER_ALLDINDAN_FAILURE:
					dissDialog();
					if (msg.obj != null) {
						Toast.makeText(ZhifuSuccessActivity.this,
								msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
					}
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
		setContentView(R.layout.activity_zhifusuccess);
		LinearLayout line_finish = (LinearLayout) findViewById(R.id.line_finish); 
		TextView tv_jine = (TextView) findViewById(R.id.tv_jine); 
		iv_erweima = (ImageView) findViewById(R.id.iv_erweima);
		tv_djj_num = (TextView) findViewById(R.id.tv_djj_num); 
		TextView tv_home = (TextView) findViewById(R.id.tv_home); 
		TextView tv_look_dindan = (TextView) findViewById(R.id.tv_look_dindan); 
		
		if (getIntent() != null) {
			String order_amount = getIntent().getStringExtra("order_amount");
			order_id = getIntent().getStringExtra("order_id");
			type = getIntent().getStringExtra("type");
			//普通商品
			if ("3".equals(type)) {
				iv_erweima.setVisibility(View.VISIBLE);
				tv_djj_num.setVisibility(View.VISIBLE);
				showLoading();
				chakanJuanmaHttpPost(order_id);
			}else{
				iv_erweima.setVisibility(View.GONE);
				tv_djj_num.setVisibility(View.GONE);
				
			}
			tv_jine.setText("¥： " + order_amount);
		}
		line_finish.setOnClickListener(this);
		tv_home.setOnClickListener(this);
		tv_look_dindan.setOnClickListener(this);
	}
	
	// 获取查看券码信息接口
	private void chakanJuanmaHttpPost(String order_id) {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(ZhifuSuccessActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("order_id", order_id);
		httpUtils.send(HttpMethod.POST, JiekouUtils.CHAKANJUANMA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						chakanjuanmaListInfo(arg0.result);

					}
				});
	}

	protected void chakanjuanmaListInfo(String result) {
		// TODO Auto-generated method stub
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
				String store_id = data.getString("store_id");
				String store_name = data.getString("store_name");
				String validity = data.getString("validity");
				tv_djj_num.setText(check_number);
				BitmapUtils bitmapUtils = new BitmapUtils(ZhifuSuccessActivity.this);
				bitmapUtils.display(iv_erweima, check_number_img);
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
	private TextView tv_djj_num;
	private ImageView iv_erweima;
	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(ZhifuSuccessActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				ZhifuSuccessActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(ZhifuSuccessActivity.this,
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
		case R.id.line_finish:
			finish();
			break;
		case R.id.tv_home:
			Intent intent = new Intent(ZhifuSuccessActivity.this, MainActivity.class);
			SharedUtil.saveStringValue(SharedCommon.DBDH, "ccq");
			intent.putExtra("type", "ccq");
			startActivity(intent);
			finish();
			break;
		case R.id.tv_look_dindan:
			Intent intent2;
			if ("0".equals(type)) {
				intent = new Intent(ZhifuSuccessActivity.this, TuiKuanDinDanDetailActivity.class);
				intent.putExtra("order_id", order_id);
				ZhifuSuccessActivity.this.startActivity(intent);
				finish();
			}else if ("3".equals(type)) {
				intent = new Intent(ZhifuSuccessActivity.this, CCQDinDanDetailActivity.class);
				intent.putExtra("order_id", order_id);
				ZhifuSuccessActivity.this.startActivity(intent);
				finish();
			}else if ("2".equals(type)) {
				intent = new Intent(ZhifuSuccessActivity.this, XiaofeiMaidanDetailActivity.class);
				intent.putExtra("order_id", order_id);
				ZhifuSuccessActivity.this.startActivity(intent);
				finish();
			}
			break;

		default:
			break;
		}
	}
}
