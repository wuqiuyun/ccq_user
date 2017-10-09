package ccj.yun28.com.sy;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.FenleigvAdapter;
import ccj.yun28.com.adapter.FenleilvAdapter;
import ccj.yun28.com.bean.ZhuFenleiBean;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 首页-分类
 * 
 * @author meihuali
 * 
 */
public class FenleiActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {

	private FenleilvAdapter adapter;
	private FenleigvAdapter gvadapter;

	private ZhuFenleiBean fenlei;
	// 第几组
	private int zu = 0;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;

	private static final int HANDLER_FENLEI_SUCCESS = 3;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(FenleiActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(FenleiActivity.this, "当前网络出错,请检查网络", Toast.LENGTH_SHORT)
						.show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(FenleiActivity.this, msg.obj.toString().trim(),
							Toast.LENGTH_SHORT).show();
				}
				break;
			case HANDLER_FENLEI_SUCCESS:
				dissDialog();

				adapter.NotifyList(fenlei.getData());

				gvadapter.NotifyList(fenlei.getData().get(0).getClass2());
				break;

			default:
				break;
			}
		};
	};
	private ListView lv;
	private GridView gv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fenlei);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_search = (LinearLayout) findViewById(R.id.line_search);
		LinearLayout line_erweima = (LinearLayout) findViewById(R.id.line_erweima);
		LinearLayout line_message = (LinearLayout) findViewById(R.id.line_message);

		lv = (ListView) findViewById(R.id.lv);
		gv = (GridView) findViewById(R.id.gv);

		adapter = new FenleilvAdapter(FenleiActivity.this);
		lv.setAdapter(adapter);

		gvadapter = new FenleigvAdapter(FenleiActivity.this);
		gv.setAdapter(gvadapter);

		showLoading();
		fenleiHttpPost();

		line_back.setOnClickListener(this);
		line_search.setOnClickListener(this);
		line_erweima.setOnClickListener(this);
		line_message.setOnClickListener(this);
		lv.setOnItemClickListener(this);
		gv.setOnItemClickListener(this);

	}

	// 一级分类网络请求
	private void fenleiHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(FenleiActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		httpUtils.send(HttpMethod.POST, JiekouUtils.SY_FENLEI,params,
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
						fenleiListInfo(arg0.result);
					}
				});
	}

	// 解析分类返回的数据
	protected void fenleiListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {

				Gson gson = new Gson();
				fenlei = gson.fromJson(result, ZhuFenleiBean.class);
				handler.sendEmptyMessage(HANDLER_FENLEI_SUCCESS);

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
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_search:
			Intent seaerchintent = new Intent(FenleiActivity.this,
					SearchActivity.class);
			startActivity(seaerchintent);
			break;
		case R.id.line_erweima:
			Intent intent = new Intent(FenleiActivity.this,
					SaoYiSaoActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.line_message:
			intent = new Intent(FenleiActivity.this, MessageActivcity.class);
			intent.putExtra("type", "宝贝");
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub

		if (parent == lv) {
			if (fenlei.getData().get(position).getClass2() == null) {
				Toast.makeText(FenleiActivity.this, "暂无数据", Toast.LENGTH_SHORT)
						.show();
			} else {
				zu = position;
				gvadapter
						.NotifyList(fenlei.getData().get(position).getClass2());
			}
		} else if (parent == gv) {
			SharedUtil.saveStringValue(SharedCommon.PRODUCTLIST_TYPE, "syfl");
			Intent intent = new Intent(FenleiActivity.this,
					SearchProductResultActivity.class);
			intent.putExtra("gc_id_2", fenlei.getData().get(zu).getClass2()
					.get(position).getGc_id());
			startActivity(intent);
		}
	}

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(FenleiActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(FenleiActivity.this,
				R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(FenleiActivity.this, R.style.mDialogStyle);
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

}
