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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.DsyDinDanAdapter;
import ccj.yun28.com.bean.ZhuAllDinDanBean;
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
 * 待使用订单
 * 
 * @author meihuali
 * 
 */
public class DSYDinDanActivtiy extends BaseActivity implements OnScrollListener, OnClickListener, OnItemClickListener {
	// 页数
	private int nowPage = 1;

	private ZhuAllDinDanBean allDinDanBean;
	private DsyDinDanAdapter dsyDinDanAdapter;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 错误
	private static final int HANDLER_NN_FAILURE = 1;
	// 待收货
	private static final int HANDLER_DSYDINDAN_SUCCESS = 2;
	private static final int HANDLER_ALLDINDAN_FAILURE = 3;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(DSYDinDanActivtiy.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(DSYDinDanActivtiy.this, "当前网络出错,请检查网络", Toast.LENGTH_SHORT)
						.show();
				break;
			case HANDLER_ALLDINDAN_FAILURE:
				dissDialog();
				Toast.makeText(DSYDinDanActivtiy.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取信息成功
			case HANDLER_DSYDINDAN_SUCCESS:
				dissDialog();
				dsyDinDanAdapter.NotifyList(allDinDanBean.getData());
				dsyDinDanAdapter.notifyDataSetChanged();
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

		tv_title.setText("待使用");
		dsyDinDanAdapter = new DsyDinDanAdapter(DSYDinDanActivtiy.this);
		lv.setAdapter(dsyDinDanAdapter);

		showLoading();
		dsyDinDanHttpPost();

		line_back.setOnClickListener(this);
		lv.setOnScrollListener(this);
		lv.setOnItemClickListener(this);
	}

	// 获取待使用订单数据接口
	private void dsyDinDanHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(DSYDinDanActivtiy.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(DSYDinDanActivtiy.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(DSYDinDanActivtiy.this));
		params.addBodyParameter("order_state", "20");
		params.addBodyParameter("union_type", "3");
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.ALLDINDAN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						dsyDinDanListInfo(arg0.result);

					}
				});
	}

	// 解析待收货订单接口数据
	protected void dsyDinDanListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				allDinDanBean = null;
				Gson gson = new Gson();
				allDinDanBean = gson.fromJson(result, ZhuAllDinDanBean.class);
				handler.sendEmptyMessage(HANDLER_DSYDINDAN_SUCCESS);
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_ALLDINDAN_FAILURE, message));
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
		View view = LayoutInflater.from(DSYDinDanActivtiy.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				DSYDinDanActivtiy.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(DSYDinDanActivtiy.this, R.style.mDialogStyle);
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
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		switch (scrollState) {

		// 当不滚动时

		case OnScrollListener.SCROLL_STATE_IDLE:

			// 判断滚动到底部
			if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
				if (!loadingDialog.isShowing()) {
					nowPage++;
					showLoading();
					dsyDinDanHttpPost();
				}
				break;

			}
		}
	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		onBackPressed();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(DSYDinDanActivtiy.this, CCQDinDanDetailActivity.class);
		intent.putExtra("order_id", allDinDanBean.getData().get(position).getOrder_id());
		startActivityForResult(intent, 100);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 101) {
			showLoading();
			nowPage = 1;
			dsyDinDanHttpPost();
		}
	}

}
