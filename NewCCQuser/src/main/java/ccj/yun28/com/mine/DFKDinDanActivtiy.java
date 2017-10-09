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
import ccj.yun28.com.adapter.DfkDinDanAdapter;
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
 * 待付款订单
 * 
 * @author meihuali
 * 
 */
public class DFKDinDanActivtiy extends BaseActivity implements OnScrollListener, OnClickListener, OnItemClickListener {
	// 页数
	private int nowPage = 1;

	private ZhuAllDinDanBean allDinDanBean;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 错误
	private static final int HANDLER_NN_FAILURE = 1;
	private static final int HANDLER_DFKDINDAN_SUCCESS = 2;
	private static final int HANDLER_ALLDINDAN_FAILURE = 3;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(DFKDinDanActivtiy.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(DFKDinDanActivtiy.this, "当前网络出错,请检查网络", Toast.LENGTH_SHORT)
						.show();
				break;
			case HANDLER_ALLDINDAN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					String result = msg.obj.toString().trim();
					Toast.makeText(DFKDinDanActivtiy.this, result,
							Toast.LENGTH_SHORT).show();
				}
				break;
			// 获取信息成功
			case HANDLER_DFKDINDAN_SUCCESS:
				dissDialog();
				dfkDinDanAdapter.NotifyList(allDinDanBean.getData(),flag);
				dfkDinDanAdapter.notifyDataSetChanged();
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

		tv_title.setText("待付款");
		dfkDinDanAdapter = new DfkDinDanAdapter(DFKDinDanActivtiy.this);
		lv.setAdapter(dfkDinDanAdapter);

		flag = true;
		showLoading();
		dfkDinDanHttpPost();

		line_back.setOnClickListener(this);
		lv.setOnScrollListener(this);
		lv.setOnItemClickListener(this);
	}

	// 获取代付款订单数据接口
	private void dfkDinDanHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(DFKDinDanActivtiy.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(DFKDinDanActivtiy.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(DFKDinDanActivtiy.this));
		params.addBodyParameter("order_state", "10");
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
						dfkDinDanListInfo(arg0.result);

					}
				});
	}

	// 解析代付款订单接口数据
	protected void dfkDinDanListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				allDinDanBean = null;
				Gson gson = new Gson();
				allDinDanBean = gson.fromJson(result, ZhuAllDinDanBean.class);
				handler.sendEmptyMessage(HANDLER_DFKDINDAN_SUCCESS);
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

	private DfkDinDanAdapter dfkDinDanAdapter;

	private boolean flag;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(DFKDinDanActivtiy.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				DFKDinDanActivtiy.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(DFKDinDanActivtiy.this, R.style.mDialogStyle);
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
					flag = false;
					dfkDinDanHttpPost();
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
		Intent intent;
		if ("0".equals(dfkDinDanAdapter.getData().get(position).getUnion_type())) {
			intent = new Intent(DFKDinDanActivtiy.this, TuiKuanDinDanDetailActivity.class);
			intent.putExtra("order_id", dfkDinDanAdapter.getData().get(position).getOrder_id());
			DFKDinDanActivtiy.this.startActivityForResult(intent,100);
		}else if ("3".equals(dfkDinDanAdapter.getData().get(position).getUnion_type())) {
			intent = new Intent(DFKDinDanActivtiy.this, CCQDinDanDetailActivity.class);
			intent.putExtra("order_id", dfkDinDanAdapter.getData().get(position).getOrder_id());
			DFKDinDanActivtiy.this.startActivityForResult(intent,200);
		}else if ("2".equals(dfkDinDanAdapter.getData().get(position).getUnion_type())) {
			intent = new Intent(DFKDinDanActivtiy.this, XiaofeiMaidanDetailActivity.class);
			intent.putExtra("order_id", dfkDinDanAdapter.getData().get(position).getOrder_id());
			DFKDinDanActivtiy.this.startActivityForResult(intent,300);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 101) {
			showLoading();
			nowPage = 1;
			flag = true;
			dfkDinDanHttpPost();
		}
	}

}
