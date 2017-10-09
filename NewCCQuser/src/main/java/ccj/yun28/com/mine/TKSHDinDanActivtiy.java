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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.TuiKuanShouHouDinDanAdapter;
import ccj.yun28.com.bean.ZhuTuikuanShouHouBean;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 退款售后订单
 * 
 * @author meihuali
 * 
 */
public class TKSHDinDanActivtiy extends BaseActivity implements OnClickListener, OnItemClickListener {
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 错误
	private static final int HANDLER_NN_FAILURE = 1;
	private static final int HANDLER_GETINFO_FAILURE = 3;
	private static final int HANDLER_TUIKUANSHOUHUO_SUCCESS = 4;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(TKSHDinDanActivtiy.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(TKSHDinDanActivtiy.this, "当前网络出错,请检查网络", Toast.LENGTH_SHORT)
						.show();
				break;
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					String result = msg.obj.toString().trim();
					Toast.makeText(TKSHDinDanActivtiy.this, result,
							Toast.LENGTH_SHORT).show();
				}
				break;
				// 获取信息成功
			case HANDLER_TUIKUANSHOUHUO_SUCCESS:
				dissDialog();
				tuiKuanShouHouDinDanAdapter.NotifyList(zhuTuikuanShouHouBean.getData());
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dindan);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		ListView lv = (ListView) findViewById(R.id.lv);

		tv_title.setText("退款/售后");
		
		tuiKuanShouHouDinDanAdapter = new TuiKuanShouHouDinDanAdapter(TKSHDinDanActivtiy.this); 
		lv.setAdapter(tuiKuanShouHouDinDanAdapter);
		
		showLoading();
		tuikuanshouhouDinDanHttpPost();

		line_back.setOnClickListener(this);
		lv.setOnItemClickListener(this);
	}

	private void tuikuanshouhouDinDanHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(TKSHDinDanActivtiy.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(TKSHDinDanActivtiy.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(TKSHDinDanActivtiy.this));
		httpUtils.send(HttpMethod.POST, JiekouUtils.TUIKUANSHOUHOU, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						tuikuanshouhouListInfo(arg0.result);

					}
				});
	}

	protected void tuikuanshouhouListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				Gson gson = new Gson();
				zhuTuikuanShouHouBean = gson.fromJson(result, ZhuTuikuanShouHouBean.class);
				handler.sendEmptyMessage(HANDLER_TUIKUANSHOUHUO_SUCCESS);
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

	// 加载中动画
	private Dialog loadingDialog;
	private ZhuTuikuanShouHouBean zhuTuikuanShouHouBean;
	private TuiKuanShouHouDinDanAdapter tuiKuanShouHouDinDanAdapter;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(TKSHDinDanActivtiy.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				TKSHDinDanActivtiy.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(TKSHDinDanActivtiy.this,
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
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		onBackPressed();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(TKSHDinDanActivtiy.this, TuiKuanDinDanDetailActivity.class);
		intent.putExtra("order_id", zhuTuikuanShouHouBean.getData().get(position).getOrder_id());
		startActivity(intent);
	}

}
