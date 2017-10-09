package ccj.yun28.com.mine;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.HangyeChooseAdapter;
import ccj.yun28.com.adapter.HangyeChooseAdapter.ErjiHangyeListener;
import ccj.yun28.com.bean.ZhuShangJiaFenLeiBean;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 商家入驻--所属行业
 * 
 * @author meihuali
 * 
 */
public class SuoShuHangYeActivity extends BaseActivity implements OnClickListener, ErjiHangyeListener {


	private ZhuShangJiaFenLeiBean shangJiaFenLei;

	private BitmapUtils bitmapUtils;
	// iocn类型
	private String resolution;

	protected static final int HANDLER_NET_FAILURE = 0;
	private static final int HANDLER_SUOSHUHANGYE_SUCCESS = 1;
	private static final int HANDLER_NN_FAILURE = 2;
	private static final int HANDLER_GETINFO_FAILURE = 3;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(SuoShuHangYeActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_SUOSHUHANGYE_SUCCESS:
				dissDialog();
				hangyeChooseAdapter.NotifyList(shangJiaFenLei.getData());
				hangyeChooseAdapter.notifyDataSetChanged();
				break;
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(SuoShuHangYeActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(SuoShuHangYeActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
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
		setContentView(R.layout.activity_suoshuhangye);

		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);

		ListView mylv = (ListView) findViewById(R.id.mylv);
		
		hangyeChooseAdapter = new HangyeChooseAdapter(SuoShuHangYeActivity.this);
		mylv.setAdapter(hangyeChooseAdapter);
		hangyeChooseAdapter.setErjiHangyeListener(this);
		
		bitmapUtils = new BitmapUtils(SuoShuHangYeActivity.this);

		showLoading();
		suoshuhangyeHttpPost();
		
		line_back.setOnClickListener(this);

	}

	// 获取屏幕分辨率 判断需要什么样的图
	private String getResolution() {
		// TODO Auto-generated method stub
		WindowManager windowManager = (SuoShuHangYeActivity.this)
				.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int screenWidth = screenWidth = display.getWidth();
		int screenHeight = screenHeight = display.getHeight();
		if (480 > screenWidth) {
			resolution = "1";
		} else if (480 <= screenWidth && screenWidth < 720) {
			resolution = "2";
		} else if (720 <= screenWidth && screenWidth < 1080) {
			resolution = "3";
		} else if (1080 >= screenWidth) {
			resolution = "4";
		}
		return resolution;
	}

	// 获取所属行业
	private void suoshuhangyeHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(SuoShuHangYeActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(SuoShuHangYeActivity.this));
		params.addBodyParameter("type", getResolution());
		httpUtils.send(HttpMethod.POST, JiekouUtils.SHANGJIAFENLEI, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						suoshuhangyeJsonInfo(arg0.result);
					}
				});
	}

	protected void suoshuhangyeJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject js = new JSONObject(result);
			String status = js.getString("code");
			if (status.equals("200")) {
				Gson gson = new Gson();
				shangJiaFenLei = gson.fromJson(result,
						ZhuShangJiaFenLeiBean.class);
				handler.sendEmptyMessage(HANDLER_SUOSHUHANGYE_SUCCESS);
			} else {
				String message = js.getString("message");
				handler.sendEmptyMessage(HANDLER_GETINFO_FAILURE);
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

	private HangyeChooseAdapter hangyeChooseAdapter;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(SuoShuHangYeActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				SuoShuHangYeActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(SuoShuHangYeActivity.this,
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
	public void erjiHangyeDate(String erjiname, String erjiid) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.putExtra("erjiname", erjiname);
		intent.putExtra("erjiid", erjiid);
		setResult(301,intent);
		finish();
	}

}
