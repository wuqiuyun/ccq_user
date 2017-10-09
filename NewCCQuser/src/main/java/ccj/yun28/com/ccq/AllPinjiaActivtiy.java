package ccj.yun28.com.ccq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.StorePingJiaAdapter;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;

/**
 * 餐餐抢评价列表页
 * 
 * @author meihuali
 * 
 */
public class AllPinjiaActivtiy extends BaseActivity implements OnClickListener {

	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;
	private int nowPage = 1;
	private String re_type = "0";

	// 没有数据
	private TextView tv_notdata;
	private RadioButton radio_all;
	private RadioButton radio_hao;
	private RadioButton radio_zhong;
	private RadioButton radio_cha;
	private RadioButton radio_huifu;
	private MaterialRefreshLayout refreshLayout;
	private ListView lv;
	private String store_id = "";
	private String goods_id = "";

	private StorePingJiaAdapter storePingJiaadapter;
	private List<Map<String, String>> storePingJiaList;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取信息成功
	private static final int HANDLER_STOREPINGJIA_SUCCESS = 3;
	// 没有更多数据
	private static final int HANDLER_NOMORE_MSG = 4;
	protected static final int HANDLER_TOKEN_FAILURE = 5;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				Toast.makeText(AllPinjiaActivtiy.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				dissDialog();
				Toast.makeText(AllPinjiaActivtiy.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				tv_notdata.setVisibility(View.VISIBLE);
				lv.setVisibility(View.GONE);
				Toast.makeText(AllPinjiaActivtiy.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取信息成功
			case HANDLER_STOREPINGJIA_SUCCESS:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				tv_notdata.setVisibility(View.GONE);
				storePingJiaadapter.NotifyList(storePingJiaList);
				dissDialog();
				break;
			// 没有更多信息
			case HANDLER_NOMORE_MSG:
				dissDialog();
				refreshLayout.finishRefresh();
				refreshLayout.finishRefreshLoadMore();
				storePingJiaadapter.NotifyList(storePingJiaList);
				if (nowPage == 1) {
					tv_notdata.setVisibility(View.VISIBLE);
				} else {
					tv_notdata.setVisibility(View.GONE);
				}
				if (msg.obj != null) {
					Toast.makeText(AllPinjiaActivtiy.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(AllPinjiaActivtiy.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent intent = new Intent(AllPinjiaActivtiy.this,
						LoginActivity.class);
				intent.putExtra("type", "near");
				startActivity(intent);
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_allpinjia);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 全部
		radio_all = (RadioButton) findViewById(R.id.radio_all);
		// 好评
		radio_hao = (RadioButton) findViewById(R.id.radio_hao);
		// 中评
		radio_zhong = (RadioButton) findViewById(R.id.radio_zhong);
		// 差评
		radio_cha = (RadioButton) findViewById(R.id.radio_cha);
		// 已回复
		radio_huifu = (RadioButton) findViewById(R.id.radio_huifu);

		tv_notdata = (TextView) findViewById(R.id.tv_notdata);

		lv = (ListView) findViewById(R.id.lv);
		storePingJiaadapter = new StorePingJiaAdapter(AllPinjiaActivtiy.this);
		lv.setAdapter(storePingJiaadapter);

		refreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh_layout);
		refreshLayout.setLoadMore(true);

		storePingJiaList = new ArrayList<Map<String, String>>();

		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(AllPinjiaActivtiy.this);

		if (getIntent() != null) {
			String type = getIntent().getStringExtra("type");
			if ("pro".equals(type)) {
				goods_id = getIntent().getStringExtra("goods_id");
				Log.e("log", "全部评价：itent返回值--》 " + goods_id);// TODO
			} else {
				store_id = getIntent().getStringExtra("store_id");
			}

		}
		showLoading();
		allPinJiaHttpPost();

		line_back.setOnClickListener(this);
		radio_all.setOnClickListener(this);
		radio_hao.setOnClickListener(this);
		radio_zhong.setOnClickListener(this);
		radio_cha.setOnClickListener(this);
		radio_huifu.setOnClickListener(this);

		initListView();
	}

	// 全部评价列表
	private void allPinJiaproHttpPost() {
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("store_id", store_id);
		params.addBodyParameter("goods_id", goods_id);// TODO

		Log.e("log", "查看全部评价：goods_id---》 " + goods_id);

		params.addBodyParameter("re_type", re_type);
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWCCQPINGJIA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						allPinJiaListInfo(arg0.result);
					}
				});
	}

	// 全部评价列表
	private void allPinJiaHttpPost() {
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("store_id", store_id);
		params.addBodyParameter("goods_id", goods_id);

		Log.e("log", "查看全部评价：goods_id---》 " + goods_id);

		params.addBodyParameter("re_type", re_type);
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWCCQPINGJIA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						allPinJiaListInfo(arg0.result);
					}
				});
	}

	// 评价返回数据解析
	protected void allPinJiaListInfo(String result) {
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				if (nowPage == 1) {
					storePingJiaList.clear();
				}
				JSONObject data = object.getJSONObject("data");
				String eva_bad = data.getString("eva_bad");
				String eva_goods = data.getString("eva_goods");
				String eva_posi = data.getString("eva_posi");
				String eva_rep = data.getString("eva_rep");
				String goods_eva = data.getString("goods_eva");

				radio_all.setText("全部\n" + goods_eva);
				radio_hao.setText("好评\n" + eva_goods);
				radio_zhong.setText("中评\n" + eva_posi);
				radio_cha.setText("差评\n" + eva_bad);
				radio_huifu.setText("已回复\n" + eva_rep);
				// Log.e("log", "评价数 --》全部:" + goods_eva+"  好评:" +
				// eva_goods+"  中评" + eva_posi+"  差评" + eva_bad+"  已回复" +
				// eva_rep);

				JSONArray list = data.getJSONArray("list");
				for (int i = 0; i < list.length(); i++) {
					JSONObject json = list.getJSONObject(i);
					storePingJiaListInfo(json);
				}
			} else if ("300".equals(status)) {
				if (nowPage == 1) {
					storePingJiaList.clear();
				}
				String message = object.getString("message");
				JSONObject data = object.getJSONObject("data");
				String eva_bad = data.getString("eva_bad");
				String eva_goods = data.getString("eva_goods");
				String eva_posi = data.getString("eva_posi");
				String eva_rep = data.getString("eva_rep");
				String goods_eva = data.getString("goods_eva");

				radio_all.setText("全部\n" + goods_eva);
				radio_hao.setText("好评\n" + eva_goods);
				radio_zhong.setText("中评\n" + eva_posi);
				radio_cha.setText("差评\n" + eva_bad);
				radio_huifu.setText("已回复\n" + eva_rep);

				handler.sendMessage(handler.obtainMessage(HANDLER_NOMORE_MSG,
						message));
			} else if ("700".equals(status)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TOKEN_FAILURE, message));
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETINFO_FAILURE, message));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private void storePingJiaListInfo(JSONObject json) {
		try {
			String fa_id = json.getString("fa_id");
			String geval_addtime = json.getString("geval_addtime");
			String geval_content = json.getString("geval_content");
			String geval_frommemberid = json.getString("geval_frommemberid");
			String geval_frommembername = json
					.getString("geval_frommembername");
			String geval_scores = json.getString("geval_scores");
			String geval_storeid = json.getString("geval_storeid");
			String goods_image = json.getString("goods_image");
			String store_reply = json.getString("store_reply");
			String member_sex = json.getString("member_sex");

			Map<String, String> ccqProQuanMap = new HashMap<String, String>();
			ccqProQuanMap.put("fa_id", fa_id);
			ccqProQuanMap.put("geval_addtime", geval_addtime);
			ccqProQuanMap.put("geval_content", geval_content);
			ccqProQuanMap.put("geval_frommemberid", geval_frommemberid);
			ccqProQuanMap.put("geval_frommembername", geval_frommembername);
			ccqProQuanMap.put("geval_scores", geval_scores);
			ccqProQuanMap.put("geval_storeid", geval_storeid);
			ccqProQuanMap.put("goods_image", goods_image);
			ccqProQuanMap.put("store_reply", store_reply);
			ccqProQuanMap.put("member_sex", member_sex);

			storePingJiaList.add(ccqProQuanMap);
			handler.sendEmptyMessage(HANDLER_STOREPINGJIA_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.radio_all:
			nowPage = 1;
			re_type = "0";
			showLoading();
			allPinJiaproHttpPost();
			break;
		case R.id.radio_hao:
			nowPage = 1;
			re_type = "1";
			showLoading();
			allPinJiaproHttpPost();
			break;
		case R.id.radio_zhong:
			nowPage = 1;
			re_type = "2";
			showLoading();
			allPinJiaproHttpPost();

			break;
		case R.id.radio_cha:
			nowPage = 1;
			re_type = "3";
			showLoading();
			allPinJiaproHttpPost();

			break;
		case R.id.radio_huifu:
			nowPage = 1;
			re_type = "4";
			showLoading();
			allPinJiaproHttpPost();

			break;

		default:
			break;
		}
	}

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(AllPinjiaActivtiy.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				AllPinjiaActivtiy.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(AllPinjiaActivtiy.this, R.style.mDialogStyle);
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

	/**
	 * @description 填充底部商品listview
	 **/
	private void initListView() {
		refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {

			@Override
			public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
				// zheli shua xin
				refreshLayout.finishRefresh();
			}

			@Override
			public void onRefreshLoadMore(
					MaterialRefreshLayout materialRefreshLayout) {
				// super.onRefreshLoadMore(materialRefreshLayout);
				try {
					nowPage++;
					allPinJiaHttpPost();

				} catch (Exception e) {
					handler.sendEmptyMessage(HANDLER_NN_FAILURE);
				}
			}
		});
	}

}
