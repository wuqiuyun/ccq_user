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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.MyWattelZiJinOneDetailAdapter;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.MyListView;
import ccj.yun28.com.view.MyScrollView;
import ccj.yun28.com.view.OnRefreshListener;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 佣金详情
 * 
 * @author meihuali
 * 
 */
public class YongjinDetailActivity extends BaseActivity implements
		OnClickListener {
	// 页数
	private int nowPage = 1;
	private MyScrollView scrollView;

	private boolean flag = true;

	private String type = "0";
	private MyWattelZiJinOneDetailAdapter myWattelZiJinDetailAdapter;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取我的钱包信息成功
	private static final int HANDLER_WALLET_SUCCESS = 3;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(YongjinDetailActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(YongjinDetailActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:

				dissDialog();
				if (flag) {
					lv.setVisibility(View.GONE);
				}
				scrollView.onResfreshFinish();
				Toast.makeText(YongjinDetailActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取我的钱包信息成功
			case HANDLER_WALLET_SUCCESS:
				dissDialog();
				lv.setVisibility(View.VISIBLE);
				scrollView.onResfreshFinish();
				myWattelZiJinDetailAdapter.NotifyList(yunbiDetailList);
				myWattelZiJinDetailAdapter.notifyDataSetChanged();
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
		setContentView(R.layout.activity_yongjindetail);

		scrollView = (MyScrollView) findViewById(R.id.scrollView);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		TextView tv_leixing = (TextView) findViewById(R.id.tv_leixing);
		tv_dongjie = (TextView) findViewById(R.id.tv_dongjie);
		tv_yunbi_num = (TextView) findViewById(R.id.tv_yunbi_num);
		tv_all = (TextView) findViewById(R.id.tv_all);
		tv_shouru = (TextView) findViewById(R.id.tv_shouru);
		tv_zhichu = (TextView) findViewById(R.id.tv_zhichu);
		tv_tixian = (TextView) findViewById(R.id.tv_tixian);
		RadioButton radio_all = (RadioButton) findViewById(R.id.radio_all);
		RadioButton radio_shouru = (RadioButton) findViewById(R.id.radio_shouru);
		RadioButton radio_zhichu = (RadioButton) findViewById(R.id.radio_zhichu);
		RadioButton radio_yitixian = (RadioButton) findViewById(R.id.radio_yitixian);
		lv = (MyListView) findViewById(R.id.lv);
		

		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(YongjinDetailActivity.this);

		myWattelZiJinDetailAdapter = new MyWattelZiJinOneDetailAdapter(
				YongjinDetailActivity.this);
		lv.setAdapter(myWattelZiJinDetailAdapter);

		yunbiDetailList = new ArrayList<Map<String, String>>();

		if (getIntent() != null) {
			String title = getIntent().getStringExtra("title");
			leixing = getIntent().getStringExtra("leixing");
			if ("zs".equals(leixing)) {
				tv_leixing.setText("招商佣金（元）");
				showLoading();
				zhaoshangDetailHttpPost();
			} else if ("tg".equals(leixing)) {
				tv_leixing.setText("推广佣金（元）");
				showLoading();
				tuiguangDetailHttpPost();
			} else if ("vip".equals(leixing)) {
				tv_leixing.setText("vip佣金（元）");
				showLoading();
				vipDetailHttpPost();
			}
			tv_title.setText(title);
		}

		initListView();

		line_back.setOnClickListener(this);
		radio_all.setOnClickListener(this);
		radio_shouru.setOnClickListener(this);
		radio_zhichu.setOnClickListener(this);
		radio_yitixian.setOnClickListener(this);
	}

	// 招商详情接口
	private void zhaoshangDetailHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(YongjinDetailActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(YongjinDetailActivity.this));
		// 类型 0 全部 1 收入 2 支出 3 已提现
		params.addBodyParameter("type", type);
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.ZHAOSHANGDETAIL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						yunbiDetailJsonInfo(arg0.result);
					}
				});
	}

	// 推广详情接口
	private void tuiguangDetailHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(YongjinDetailActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(YongjinDetailActivity.this));
		// 类型 0 全部 1 收入 2 支出 3 已提现
		params.addBodyParameter("type", type);
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.TUIGUANGDETAIL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						yunbiDetailJsonInfo(arg0.result);
					}
				});
	}
	// vip详情接口
	private void vipDetailHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(YongjinDetailActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(YongjinDetailActivity.this));
		// 类型 0 全部 1 收入 2 支出 3 已提现
		params.addBodyParameter("type", type);
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.VIPDETAIL, params,
				new RequestCallBack<String>() {
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(HANDLER_NET_FAILURE);
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				yunbiDetailJsonInfo(arg0.result);
			}
		});
	}

	protected void yunbiDetailJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			String amount = object.getString("amount");
			String fee = object.getString("fee");
			String sum = object.getString("sum");
			tv_dongjie.setText(fee);
			tv_yunbi_num.setText(sum);
			if ("0".equals(type)) {
				tv_all.setText(sum);
				tv_zhichu.setText("");
				tv_shouru.setText("");
				tv_tixian.setText("");
			} else if ("1".equals(type)) {
				tv_shouru.setText(amount);
				tv_zhichu.setText("");
				tv_tixian.setText("");
				tv_all.setText("");
			} else if ("2".equals(type)) {
				tv_zhichu.setText(amount);
				tv_tixian.setText("");
				tv_all.setText("");
				tv_shouru.setText("");
			} else if ("3".equals(type)) {
				tv_tixian.setText(amount);
				tv_all.setText("");
				tv_shouru.setText("");
				tv_zhichu.setText("");
			}
			if ("200".equals(code)) {
				JSONArray data = object.getJSONArray("data");
				for (int i = 0; i < data.length(); i++) {
					JSONObject json = data.getJSONObject(i);
					yunbiDetailListInfo(json);
				}
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

	private void yunbiDetailListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String change_value = json.getString("change_value");
			String datetime = json.getString("datetime");
			String id = json.getString("id");
			String intro = json.getString("intro");
			String title = json.getString("title");
			String value = json.getString("value");
			Map<String, String> yunbiDetailMap = new HashMap<String, String>();
			yunbiDetailMap.put("change_value", change_value);
			yunbiDetailMap.put("datetime", datetime);
			yunbiDetailMap.put("id", id);
			yunbiDetailMap.put("intro", intro);
			yunbiDetailMap.put("title", title);
			yunbiDetailMap.put("value", value);
			yunbiDetailList.add(yunbiDetailMap);
			handler.sendEmptyMessage(HANDLER_WALLET_SUCCESS);
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
		case R.id.radio_all:
			flag = true;
			type = "0";
			yunbiDetailList.clear();
			if ("zs".equals(leixing)) {
				showLoading();
				zhaoshangDetailHttpPost();
			} else if ("tg".equals(leixing)) {
				showLoading();
				tuiguangDetailHttpPost();
			} else if ("vip".equals(leixing)) {
				showLoading();
				vipDetailHttpPost();
			}
			break;
		case R.id.radio_shouru:
			flag = true;
			type = "1";
			yunbiDetailList.clear();
			if ("zs".equals(leixing)) {
				showLoading();
				zhaoshangDetailHttpPost();
			} else if ("tg".equals(leixing)) {
				showLoading();
				tuiguangDetailHttpPost();
			} else if ("vip".equals(leixing)) {
				showLoading();
				vipDetailHttpPost();
			}
			break;
		case R.id.radio_zhichu:
			flag = true;
			type = "2";
			yunbiDetailList.clear();
			if ("zs".equals(leixing)) {
				showLoading();
				zhaoshangDetailHttpPost();
			} else if ("tg".equals(leixing)) {
				showLoading();
				tuiguangDetailHttpPost();
			} else if ("vip".equals(leixing)) {
				showLoading();
				vipDetailHttpPost();
			}
			break;
		case R.id.radio_yitixian:
			flag = true;
			type = "3";
			yunbiDetailList.clear();
			if ("zs".equals(leixing)) {
				showLoading();
				zhaoshangDetailHttpPost();
			} else if ("tg".equals(leixing)) {
				showLoading();
				tuiguangDetailHttpPost();
			} else if ("vip".equals(leixing)) {
				showLoading();
				vipDetailHttpPost();
			}
			break;

		default:
			break;
		}
	}

	/**
	 * @description 填充底部商品listview
	 **/
	private void initListView() {
		scrollView.getView();
		scrollView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadMoring() {
				// TODO Auto-generated method stub
				try {
					nowPage++;
					if ("zs".equals(leixing)) {
						showLoading();
						zhaoshangDetailHttpPost();
					} else if ("tg".equals(leixing)) {
						showLoading();
						tuiguangDetailHttpPost();
					} else if ("vip".equals(leixing)) {
						showLoading();
						vipDetailHttpPost();
					}
					flag = false;
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(HANDLER_NN_FAILURE);
				}

			}
		});
	}

	// 加载中动画
	private Dialog loadingDialog;
	private TextView tv_dongjie;
	private TextView tv_yunbi_num;
	private TextView tv_all;
	private TextView tv_shouru;
	private TextView tv_zhichu;
	private TextView tv_tixian;
	private List<Map<String, String>> yunbiDetailList;
	private MyListView lv;
	private String leixing;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(YongjinDetailActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				YongjinDetailActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(YongjinDetailActivity.this,
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
}
