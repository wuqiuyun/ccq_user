package ccj.yun28.com.mine;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.QulvAdapter;
import ccj.yun28.com.adapter.ShenglvAdapter;
import ccj.yun28.com.adapter.ShilvAdapter;
import ccj.yun28.com.bean.ZhuAllDiQuBean;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class ChooseShengShiQuActicity extends BaseActivity implements OnItemClickListener, OnClickListener {

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取全部地区成功
	private static final int HANDLER_ALLDIQU_SUCCESS = 3;
	private int sheng;
	private int shi;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(ChooseShengShiQuActicity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(ChooseShengShiQuActicity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(ChooseShengShiQuActicity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取全部地址信息成功
			case HANDLER_ALLDIQU_SUCCESS:
				dissDialog();
				shengAdapter.NotifyList(allDiQu.getData());
				shengAdapter.setSelectItem(0);
				shengAdapter.notifyDataSetChanged();
				shiAdapter.NotifyList(allDiQu.getData().get(0).getClass2());
				shiAdapter.notifyDataSetChanged();
				quAdapter.NotifyList(allDiQu.getData().get(0).getClass2().get(0).getClass3());
				quAdapter.notifyDataSetChanged();
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
		setContentView(R.layout.activity_alldiqu);

		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		lv_sheng = (ListView) findViewById(R.id.lv_sheng);
		lv_shi = (ListView) findViewById(R.id.lv_shi);
		lv_qu = (ListView) findViewById(R.id.lv_qu);
		
		shengAdapter = new ShenglvAdapter(ChooseShengShiQuActicity.this);
		lv_sheng.setAdapter(shengAdapter);
		
		shiAdapter = new ShilvAdapter(ChooseShengShiQuActicity.this);
		lv_shi.setAdapter(shiAdapter);
		quAdapter = new QulvAdapter(ChooseShengShiQuActicity.this);
		lv_qu.setAdapter(quAdapter);
		
		showLoading();
		allaiquHttpPost();
		
		line_back.setOnClickListener(this);
		lv_sheng.setOnItemClickListener(this);
		lv_shi.setOnItemClickListener(this);
		lv_qu.setOnItemClickListener(this);
	}

	// 获取所有地址
	private void allaiquHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(ChooseShengShiQuActicity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		httpUtils.send(HttpMethod.POST, JiekouUtils.ALLDIQU,params,
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
						alldiquListInfo(arg0.result);
					}
				});
	}

	protected void alldiquListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				Gson gson = new Gson();
				allDiQu = gson.fromJson(result, ZhuAllDiQuBean.class);
				handler.sendEmptyMessage(HANDLER_ALLDIQU_SUCCESS);

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
	private ShenglvAdapter shengAdapter;
	private ZhuAllDiQuBean allDiQu;
	private ListView lv_sheng;
	private ListView lv_shi;
	private ListView lv_qu;
	private ShilvAdapter shiAdapter;
	private QulvAdapter quAdapter;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(ChooseShengShiQuActicity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				ChooseShengShiQuActicity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(ChooseShengShiQuActicity.this,
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
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		if (parent == lv_sheng) {
			if (allDiQu.getData().get(position) == null) {
				Toast.makeText(ChooseShengShiQuActicity.this, "暂无数据", Toast.LENGTH_SHORT)
						.show();
			} else {
				sheng = position;
				shengAdapter.setSelectItem(position);
				shengAdapter.notifyDataSetChanged();
				shiAdapter.NotifyList(allDiQu.getData().get(position).getClass2());
				if (allDiQu.getData().get(position).getClass2() != null) {
					quAdapter.NotifyList(allDiQu.getData().get(position).getClass2().get(0).getClass3());
				}
			}
		} else if (parent == lv_shi) {
			if (allDiQu.getData().get(sheng).getClass2() == null) {
				Toast.makeText(ChooseShengShiQuActicity.this, "暂无数据", Toast.LENGTH_SHORT)
						.show();
			} else {
				shi = position;
				shiAdapter.setSelectItem(position);
				shiAdapter.notifyDataSetChanged();
				quAdapter.NotifyList(allDiQu.getData().get(sheng).getClass2().get(position).getClass3());
			}
		} else if (parent == lv_qu) {
			Intent intent = new Intent();
			intent.putExtra("area_id", allDiQu.getData().get(sheng).getClass2().get(shi).getClass3().get(position).getArea_id());
			intent.putExtra("city_id", allDiQu.getData().get(sheng).getClass2().get(shi).getClass3().get(position).getArea_parent_id());
			String area_info = allDiQu.getData().get(sheng).getArea_name()+"\t" +
					allDiQu.getData().get(sheng).getClass2().get(shi).getArea_name()+"\t" +
					allDiQu.getData().get(sheng).getClass2().get(shi).getClass3().get(position).getArea_name();
			intent.putExtra("area_info", area_info);
			setResult(200, intent);
			finish();
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		onBackPressed();
	}
}
