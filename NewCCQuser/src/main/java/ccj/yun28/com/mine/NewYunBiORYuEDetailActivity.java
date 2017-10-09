package ccj.yun28.com.mine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.MyWattelZiJinOneDetailAdapter;
import ccj.yun28.com.adapter.MyWattelZiJinThreeDetailAdapter;
import ccj.yun28.com.adapter.MyWattelZiJinTwoDetailAdapter;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.MyListView;
import ccj.yun28.com.view.MyScrollView;
import ccj.yun28.com.view.OnRefreshListener;
import ccj.yun28.com.view.WebviewActivity;

import com.byl.datepicker.wheelview.NoDayOnWheelScrollListener;
import com.byl.datepicker.wheelview.NumericWheelAdapter;
import com.byl.datepicker.wheelview.WheelView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 新版云币或余额详情
 * 
 * @author meihuali
 * 
 */
public class NewYunBiORYuEDetailActivity extends BaseActivity implements
		OnClickListener {

	private List<Map<String, String>> yunbiDetailOneList;
	private List<Map<String, String>> yunbiDetailTwoList;
	private List<Map<String, String>> yunbiDetailThreeList;
	private MyListView lv;
	private String leixing;
	private LayoutInflater inflater = null;
	private WheelView year;
	private WheelView month;
	private WheelView day;
	private int mYear = 2017;
	private int mMonth = 0;
	private String card_id;
	// 剩余可用页面
	private LinearLayout line_sykyinfo;
	// 提现银行卡
	private LinearLayout line_bangdingbank;
	private TextView tv_bank_card;
	// 银行账户名
	private TextView tv_zhm_name;
	// 提现金额：
	private EditText et_tixianjine;
	// 首页无数据时显示
	private TextView tv_notdata;
	// 分类下的所有
	private LinearLayout line_fewnleixia;
	private LinearLayout line_gjhd;
	private TextView tv_gjhd;
	private LinearLayout line_yy_tj;
	private TextView tv_yy_tj;
	private LinearLayout line_djz;
	private TextView tv_djz;
	private LinearLayout line_syky;
	private TextView tv_syky;
	private LinearLayout line_choose_riqi;
	private TextView tv_riqi;
	private TextView tv_dangyue;
	private TextView tv_js;

	private LinearLayout ll;
	private RelativeLayout rela_choose;

	// 页数
	private int nowPage = 1;
	private MyScrollView scrollView;

	private boolean flag = true;
	// 是否已经实名认证 1:是 0:否
	private String is_real_verify = "0";
	// 资金类型
	private String account_name = "balance";
	// 操作类型
	private String operand = "1";
	private MyWattelZiJinOneDetailAdapter myWattelZiJinOneDetailAdapter;
	private MyWattelZiJinTwoDetailAdapter myWattelZiJinTwoDetailAdapter;
	private MyWattelZiJinThreeDetailAdapter myWattelZiJinThreeDetailAdapter;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取我的钱包信息成功
	private static final int HANDLER_WALLET_SUCCESS = 3;
	// token失效
	private static final int HANDLER_TOKEN_FAILURE = 4;
	// 没有数据
	private static final int HANDLER_NOTDATA = 5;
	// 获取银行卡信息
	private static final int HANDLER_GETBANKINFO_SUCCESS = 6;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(NewYunBiORYuEDetailActivity.this,
						"当前网络不可用,请检查网络", Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(NewYunBiORYuEDetailActivity.this,
						"当前网络出错,请检查网络", Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:

				dissDialog();
				if (flag) {
					lv.setVisibility(View.GONE);
				}
				scrollView.onResfreshFinish();
				if (msg.obj != null) {

					Toast.makeText(NewYunBiORYuEDetailActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// 获取我的钱包信息成功
			case HANDLER_WALLET_SUCCESS:
				dissDialog();
				lv.setVisibility(View.VISIBLE);
				scrollView.onResfreshFinish();
				// 操作类型 1: 总计获得 2:已用/提现 3:冻结中
				if ("1".equals(operand)) {
					myWattelZiJinOneDetailAdapter
							.NotifyList(yunbiDetailOneList);
					myWattelZiJinOneDetailAdapter.notifyDataSetChanged();

				} else if ("2".equals(operand)) {
					myWattelZiJinTwoDetailAdapter
							.NotifyList(yunbiDetailTwoList);
					myWattelZiJinTwoDetailAdapter.notifyDataSetChanged();

				} else if ("3".equals(operand)) {
					myWattelZiJinThreeDetailAdapter
							.NotifyList(yunbiDetailThreeList);
					myWattelZiJinThreeDetailAdapter.notifyDataSetChanged();

				}
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(NewYunBiORYuEDetailActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent intent = new Intent(NewYunBiORYuEDetailActivity.this,
						LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
				break;
			// 没有数据
			case HANDLER_NOTDATA:
				dissDialog();
				break;
			// 获取银行卡信息
			case HANDLER_GETBANKINFO_SUCCESS:
				dissDialog();
				if ("0".equals(is_real_verify)) {
					showGoSmrzDialog();
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
		setContentView(R.layout.activity_newyunbioryuedetail);

		scrollView = (MyScrollView) findViewById(R.id.scrollView);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);

		tv_notdata = (TextView) findViewById(R.id.tv_notdata);
		line_fewnleixia = (LinearLayout) findViewById(R.id.line_fewnleixia);

		// 剩余可用页面
		line_sykyinfo = (LinearLayout) findViewById(R.id.line_sykyinfo);
		// 提现银行卡
		line_bangdingbank = (LinearLayout) findViewById(R.id.line_bangdingbank);
		tv_bank_card = (TextView) findViewById(R.id.tv_bank_card);
		// 银行账户名
		tv_zhm_name = (TextView) findViewById(R.id.tv_zhm_name);
		// 提现金额：
		et_tixianjine = (EditText) findViewById(R.id.et_tixianjine);
		// 税提示 余额外都要显示
		TextView tv_suitishi = (TextView) findViewById(R.id.tv_suitishi);
		TextView tv_querentixian = (TextView) findViewById(R.id.tv_querentixian);
		TextView tv_tixianshuoming = (TextView) findViewById(R.id.tv_tixianshuoming);

		// 共计获得
		line_gjhd = (LinearLayout) findViewById(R.id.line_gjhd);
		tv_gjhd = (TextView) findViewById(R.id.tv_gjhd);
		// 已用/提现
		line_yy_tj = (LinearLayout) findViewById(R.id.line_yy_tj);
		tv_yy_tj = (TextView) findViewById(R.id.tv_yy_tj);
		// 冻结中
		line_djz = (LinearLayout) findViewById(R.id.line_djz);
		tv_djz = (TextView) findViewById(R.id.tv_djz);
		// 剩余可用
		line_syky = (LinearLayout) findViewById(R.id.line_syky);
		tv_syky = (TextView) findViewById(R.id.tv_syky);
		// 日期
		line_choose_riqi = (LinearLayout) findViewById(R.id.line_choose_riqi);
		line_riqi = (LinearLayout) findViewById(R.id.line_riqi);
		tv_riqi = (TextView) findViewById(R.id.tv_riqi);
		// 当月
		tv_dangyue = (TextView) findViewById(R.id.tv_dangyue);
		lv = (MyListView) findViewById(R.id.lv);

		tv_js = (TextView) findViewById(R.id.tv_js);
		// 日期控件
		rela_choose = (RelativeLayout) findViewById(R.id.rela_choose);
		TextView tv_quxiao = (TextView) findViewById(R.id.tv_quxiao);
		TextView tv_queren = (TextView) findViewById(R.id.tv_queren);
		ll = (LinearLayout) findViewById(R.id.ll);
		
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(NewYunBiORYuEDetailActivity.this);

		ll.addView(getDataPick());

		// YueguanliAdapter yueguanliAdapter = new YueguanliAdapter(
		// NewYunBiORYuEDetailActivity.this);
		myWattelZiJinOneDetailAdapter = new MyWattelZiJinOneDetailAdapter(
				NewYunBiORYuEDetailActivity.this);
		lv.setAdapter(myWattelZiJinOneDetailAdapter);

		yunbiDetailOneList = new ArrayList<Map<String, String>>();
		yunbiDetailTwoList = new ArrayList<Map<String, String>>();
		yunbiDetailThreeList = new ArrayList<Map<String, String>>();

		Calendar c = Calendar.getInstance();// 首先要获取日历对象
		mYear = c.get(Calendar.YEAR); // 获取当前年份
		mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份

		tv_riqi.setText(mYear + "年" + "    " + mMonth + "月");

		if (getIntent() != null) {
			String title = getIntent().getStringExtra("title");
			leixing = getIntent().getStringExtra("leixing");
			tv_title.setText(title);
			showLoading();
			// 资金类型 balance:余额 point:云币 merchants:招商奖金 promote:推广奖金 vip:奖金
			if ("yue".equals(leixing)) {
				account_name = "balance";
				tv_suitishi.setVisibility(View.GONE);
			} else if ("yunbi".equals(leixing)) {
				account_name = "point";
			} else if ("zs".equals(leixing)) {
				account_name = "merchants";
			} else if ("tg".equals(leixing)) {
				account_name = "promote";
			} else if ("vip".equals(leixing)) {
				account_name = "vip";
			}
			yueoryunbiDetailHttpPost();
		}

		initListView();

		line_back.setOnClickListener(this);
		line_gjhd.setOnClickListener(this);
		line_yy_tj.setOnClickListener(this);
		line_djz.setOnClickListener(this);
		line_syky.setOnClickListener(this);
		line_riqi.setOnClickListener(this);
		tv_quxiao.setOnClickListener(this);
		tv_queren.setOnClickListener(this);
		line_bangdingbank.setOnClickListener(this);
		tv_querentixian.setOnClickListener(this);
		tv_tixianshuoming.setOnClickListener(this);
	}

	private Dialog goSmrzDialog;

	// 去实名认证dialog
	protected void showGoSmrzDialog() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(NewYunBiORYuEDetailActivity.this)
				.inflate(R.layout.dialog_red_moban, null);
		TextView dialog_text = (TextView) view.findViewById(R.id.dialog_text);
		TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
		TextView tv_dialog_cancel = (TextView) view
				.findViewById(R.id.tv_dialog_cancel);
		dialog_text.setText("您还没有进行实名认证，当前功能不可用是否先去实名认证？");

		tv_ok.setTextColor(Color.RED);
		tv_dialog_cancel.setTextColor(Color.BLACK);

		tv_ok.setOnClickListener(this);
		tv_dialog_cancel.setOnClickListener(this);
		goSmrzDialog = new Dialog(NewYunBiORYuEDetailActivity.this,
				R.style.mDialogStyle);
		goSmrzDialog.setContentView(view);
		goSmrzDialog.setCanceledOnTouchOutside(false);
		goSmrzDialog.show();
	}

	// 详情接口
	private void yueoryunbiDetailHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token",
				new DButil().gettoken(NewYunBiORYuEDetailActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(NewYunBiORYuEDetailActivity.this));
		// 资金类型 balance:余额 point:云币 merchants:招商奖金 promote:推广奖金 vip:奖金
		params.addBodyParameter("account_name", account_name);
		// 操作类型 1: 总计获得 2:已用/提现 3:冻结中
		params.addBodyParameter("operand", operand);
		params.addBodyParameter("year", mYear + "");
		params.addBodyParameter("mon", mMonth + "");
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.ALLZIJINDETAIL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						if ("1".equals(operand)) {
							yueoryunbiOneDetailJsonInfo(arg0.result);
						} else {
							yueoryunbiTwoDetailJsonInfo(arg0.result);
						}
					}
				});
	}

	// 2:已用/提现3:冻结中
	protected void yueoryunbiTwoDetailJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");

			if ("200".equals(code)) {
				JSONObject data = object.getJSONObject("data");
				if (flag) {
					// 可用
					String current = data.getString("current");
					// 冻结
					String fee = data.getString("fee");
					// 共计获得
					String total = data.getString("total");
					// 已用/提现
					String used = data.getString("used");

					tv_gjhd.setText(total + "元");
					tv_yy_tj.setText(used + "元");
					tv_djz.setText(fee + "元");
					tv_syky.setText(current + "元");

				}

				JSONArray array = data.getJSONArray("list");

				if (array.length() > 0) {
					tv_notdata.setVisibility(View.GONE);
					line_fewnleixia.setVisibility(View.VISIBLE);
					for (int i = 0; i < array.length(); i++) {
						JSONObject json = array.getJSONObject(i);
						if ("2".equals(operand)) {
							yunbiDetailTwoListInfo(json);
						} else if ("3".equals(operand)) {
							yunbiDetailThreeListInfo(json);
						}
					}
				} else {
					tv_notdata.setVisibility(View.VISIBLE);
					line_fewnleixia.setVisibility(View.GONE);
					handler.sendEmptyMessage(HANDLER_NOTDATA);
				}
			} else if ("700".equals(code)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TOKEN_FAILURE, message));
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

	// 2:已用/提现
	private void yunbiDetailTwoListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String name = json.getString("name");
			String order_sn = json.getString("order_sn");
			String time = json.getString("time");
			String value_name = json.getString("value_name");
			Map<String, String> yunbiDetailMap = new HashMap<String, String>();
			yunbiDetailMap.put("name", name);
			yunbiDetailMap.put("order_sn", order_sn);
			yunbiDetailMap.put("time", time);
			yunbiDetailMap.put("value_name", value_name);
			yunbiDetailTwoList.add(yunbiDetailMap);
			handler.sendEmptyMessage(HANDLER_WALLET_SUCCESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 3:冻结中
	private void yunbiDetailThreeListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String info = json.getString("info");
			String message = json.getString("message");
			String name = json.getString("name");
			String time = json.getString("time");
			String value_name = json.getString("value_name");
			Map<String, String> yunbiDetailMap = new HashMap<String, String>();
			yunbiDetailMap.put("info", info);
			yunbiDetailMap.put("message", message);
			yunbiDetailMap.put("name", name);
			yunbiDetailMap.put("time", time);
			yunbiDetailMap.put("value_name", value_name);
			yunbiDetailThreeList.add(yunbiDetailMap);
			handler.sendEmptyMessage(HANDLER_WALLET_SUCCESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 1: 总计获得
	protected void yueoryunbiOneDetailJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");

			if ("200".equals(code)) {
				JSONObject data = object.getJSONObject("data");
				if (flag) {
					// 可用
					String current = data.getString("current");
					// 冻结
					String fee = data.getString("fee");
					// 本月共计获得
					String sum = data.getString("sum");
					// 共计获得
					String total = data.getString("total");
					// 已用/提现
					String used = data.getString("used");

					tv_gjhd.setText(total + "元");
					tv_yy_tj.setText(used + "元");
					tv_djz.setText(fee + "元");
					tv_syky.setText(current + "元");
					tv_dangyue.setText("当月：" + sum + "元");

				}

				JSONArray array = data.getJSONArray("list");
				if (array.length() > 0) {

					tv_notdata.setVisibility(View.GONE);
					line_fewnleixia.setVisibility(View.VISIBLE);

					for (int i = 0; i < array.length(); i++) {
						JSONObject json = array.getJSONObject(i);
						yunbiDetailOneListInfo(json);
					}
				} else {
					tv_notdata.setVisibility(View.VISIBLE);
					line_fewnleixia.setVisibility(View.GONE);
					handler.sendEmptyMessage(HANDLER_NOTDATA);
				}
			} else if ("700".equals(code)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TOKEN_FAILURE, message));
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

	// 1: 总计获得
	private void yunbiDetailOneListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String info = json.getString("info");
			String name = json.getString("name");
			String order_sn = json.getString("order_sn");
			String time = json.getString("time");
			String value_name = json.getString("value_name");
			Map<String, String> yunbiDetailMap = new HashMap<String, String>();
			yunbiDetailMap.put("info", info);
			yunbiDetailMap.put("name", name);
			yunbiDetailMap.put("order_sn", order_sn);
			yunbiDetailMap.put("time", time);
			yunbiDetailMap.put("value_name", value_name);
			yunbiDetailOneList.add(yunbiDetailMap);
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
		// 操作类型 1: 总计获得 2:已用/提现 3:冻结中 4：剩余可用
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_gjhd:
			myWattelZiJinOneDetailAdapter = new MyWattelZiJinOneDetailAdapter(
					NewYunBiORYuEDetailActivity.this);
			lv.setAdapter(myWattelZiJinOneDetailAdapter);
			line_gjhd.setBackgroundResource(R.drawable.gjhdh);
			line_yy_tj.setBackgroundResource(R.drawable.yiyh);
			line_djz.setBackgroundResource(R.drawable.djlan);
			line_syky.setBackgroundResource(R.drawable.sykyl);

			line_riqi.setVisibility(View.VISIBLE);
			tv_dangyue.setVisibility(View.VISIBLE);
			line_sykyinfo.setVisibility(View.GONE);
			line_fewnleixia.setVisibility(View.VISIBLE);
			flag = true;
			operand = "1";
			nowPage = 1;
			yunbiDetailOneList.clear();
			showLoading();
			yueoryunbiDetailHttpPost();
			break;
		case R.id.line_yy_tj:
			myWattelZiJinTwoDetailAdapter = new MyWattelZiJinTwoDetailAdapter(
					NewYunBiORYuEDetailActivity.this);
			lv.setAdapter(myWattelZiJinTwoDetailAdapter);
			line_gjhd.setBackgroundResource(R.drawable.gjhd);
			line_yy_tj.setBackgroundResource(R.drawable.yiyr);
			line_djz.setBackgroundResource(R.drawable.djlan);
			line_syky.setBackgroundResource(R.drawable.sykyl);

			line_riqi.setVisibility(View.VISIBLE);
			line_fewnleixia.setVisibility(View.VISIBLE);
			tv_dangyue.setVisibility(View.GONE);
			line_sykyinfo.setVisibility(View.GONE);
			flag = true;
			operand = "2";
			nowPage = 1;
			yunbiDetailTwoList.clear();
			showLoading();
			yueoryunbiDetailHttpPost();
			break;
		case R.id.line_djz:
			myWattelZiJinThreeDetailAdapter = new MyWattelZiJinThreeDetailAdapter(
					NewYunBiORYuEDetailActivity.this);
			lv.setAdapter(myWattelZiJinThreeDetailAdapter);
			line_gjhd.setBackgroundResource(R.drawable.gjhd);
			line_yy_tj.setBackgroundResource(R.drawable.yiyh);
			line_djz.setBackgroundResource(R.drawable.djzb);
			line_syky.setBackgroundResource(R.drawable.sykyl);
			line_riqi.setVisibility(View.VISIBLE);
			line_fewnleixia.setVisibility(View.VISIBLE);
			tv_dangyue.setVisibility(View.GONE);
			line_sykyinfo.setVisibility(View.GONE);
			flag = true;
			operand = "3";
			nowPage = 1;
			yunbiDetailThreeList.clear();
			showLoading();
			yueoryunbiDetailHttpPost();
			break;
		case R.id.line_syky:

			if ("yue".equals(leixing)) {
				tv_js.setText("* 最大不能超过剩余可用的金额哦");
			} else {
				tv_js.setText("* 提现金额必须是一百的倍数");
			}
			line_riqi.setVisibility(View.GONE);
			line_sykyinfo.setVisibility(View.VISIBLE);
			line_fewnleixia.setVisibility(View.GONE);
			tv_notdata.setVisibility(View.GONE);
			line_gjhd.setBackgroundResource(R.drawable.gjhd);
			line_yy_tj.setBackgroundResource(R.drawable.yiyh);
			line_djz.setBackgroundResource(R.drawable.djlan);
			line_syky.setBackgroundResource(R.drawable.syky);
			flag = true;
			showLoading();
			getBankInfoHttpPost();

			break;
		case R.id.line_riqi:
			line_choose_riqi.setVisibility(View.VISIBLE);
			// rela_choose.setVisibility(View.VISIBLE);
			// ll.setVisibility(View.VISIBLE);
			break;
		case R.id.tv_queren:
			line_choose_riqi.setVisibility(View.GONE);
			// rela_choose.setVisibility(View.GONE);
			// ll.setVisibility(View.GONE);

			showLoading();
			yueoryunbiDetailHttpPost();
			// tv_riqi.setText(n_year + "年" + "    " + n_month + "月");
			tv_riqi.setText(mYear + "年" + "    " + mMonth + "月");
			break;
		case R.id.tv_quxiao:
			line_choose_riqi.setVisibility(View.GONE);
			// rela_choose.setVisibility(View.GONE);
			// ll.setVisibility(View.GONE);
			break;
		case R.id.line_bangdingbank:
			if ("1".equals(is_real_verify)) {
				Intent bankintent = new Intent(
						NewYunBiORYuEDetailActivity.this,
						BankCardListActivity.class);
				startActivityForResult(bankintent, 100);
			} else {
				showGoSmrzDialog();
			}
			break;
		case R.id.tv_querentixian:
			if ("1".equals(is_real_verify)) {
				if (txcanPost()) {
					showLoading();
					tixianHttpPost();
				}
			} else {
				showGoSmrzDialog();
			}
			break;
		case R.id.tv_tixianshuoming:
			String url = SharedUtil.getStringValue(
					SharedCommon.MALL_WITHDRAW_EXPLAIN, "");
			Intent intent = new Intent(NewYunBiORYuEDetailActivity.this,
					WebviewActivity.class);
			intent.putExtra("url", url);
			intent.putExtra("title", "提现说明");
			startActivity(intent);
			break;
		case R.id.tv_ok:
			// Intent smintent = new Intent(NewYunBiORYuEDetailActivity.this,
			// ShiMingRenZhengActivity.class);
			goSmrzDialog.dismiss();
			// 是否实名认证 实名认证状态 0 未认证 1已认证 2待认证3认证不通过
			 if ("0".equals(is_real_verify)) {
				Intent smintent = new Intent(NewYunBiORYuEDetailActivity.this,
						NewWeiShiMingRenZhengActivity.class);
				smintent.putExtra("title", "实名认证");
				startActivity(smintent);
			} else {
				Intent wrzintent = new Intent(NewYunBiORYuEDetailActivity.this,
						ShiMingRenZhengActivity.class);
				startActivity(wrzintent);
			}

			break;
		case R.id.tv_dialog_cancel:
			goSmrzDialog.dismiss();
			break;

		default:
			break;
		}
	}

	// 获取银行卡信息接口
	private void getBankInfoHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token",
				new DButil().gettoken(NewYunBiORYuEDetailActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(NewYunBiORYuEDetailActivity.this));
		httpUtils.send(HttpMethod.POST, JiekouUtils.TIXIANGEZIJIN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						getBankJsonInfo(arg0.result);
					}
				});
	}

	protected void getBankJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");

			if ("200".equals(code)) {
				JSONObject data = object.getJSONObject("data");
				// 银行卡号
				String card_account = data.getString("card_account");
				// 姓名
				String true_name = data.getString("true_name");
				// 银行卡id
				card_id = data.getString("card_id");
				// 是否已经实名认证 1:是 0:否
				is_real_verify = data.getString("is_real_verify");

				tv_bank_card.setText(card_account);
				tv_zhm_name.setText(true_name);

				handler.sendEmptyMessage(HANDLER_GETBANKINFO_SUCCESS);

			} else if ("700".equals(code)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TOKEN_FAILURE, message));
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

	// 调用提现接口
	private void tixianHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token",
				new DButil().gettoken(NewYunBiORYuEDetailActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(NewYunBiORYuEDetailActivity.this));
		// 银行卡号
		params.addBodyParameter("card_id", card_id);
		// 金额
		params.addBodyParameter("amount", et_tixianjine.getText().toString()
				.trim());
		// 资金类型 balance:余额 point:云币 merchants:招商奖金 promote:推广奖金 vip:奖金
		params.addBodyParameter("account_name", account_name);
		httpUtils.send(HttpMethod.POST, JiekouUtils.TIXIANINFO, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						tixianJsonInfo(arg0.result);
					}
				});
	}

	protected void tixianJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");

			if ("700".equals(code)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TOKEN_FAILURE, message));
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

	// 检查是否可以体交提现
	private boolean txcanPost() {
		// TODO Auto-generated method stub
		String card_num = tv_bank_card.getText().toString().trim();
		String card_name = tv_zhm_name.getText().toString().trim();
		String txjine = et_tixianjine.getText().toString().trim();
		if (TextUtils.isEmpty(card_num)) {
			Toast.makeText(NewYunBiORYuEDetailActivity.this, "未选择提现银行卡",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(card_name)) {
			Toast.makeText(NewYunBiORYuEDetailActivity.this, "请填写银行账户名",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(txjine)) {
			Toast.makeText(NewYunBiORYuEDetailActivity.this, "提现金额不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
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
					if ("yue".equals(leixing)) {
						account_name = "point";
					} else if ("yunbi".equals(leixing)) {
						account_name = "merchants";
					}
					showLoading();
					yueoryunbiDetailHttpPost();
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

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(NewYunBiORYuEDetailActivity.this)
				.inflate(R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				NewYunBiORYuEDetailActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(NewYunBiORYuEDetailActivity.this,
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

	private View getDataPick() {
		Calendar c = Calendar.getInstance();
		int norYear = c.get(Calendar.YEAR);

		int curYear = mYear;
		int curMonth = mMonth + 1;

		// View view = inflater.inflate(R.layout.wheel_date_picker, null);

		View view = LayoutInflater.from(NewYunBiORYuEDetailActivity.this)
				.inflate(R.layout.wheel_date_picker, null);

		WheelView day = (WheelView) view.findViewById(R.id.day);
		day.setVisibility(View.GONE);

		year = (WheelView) view.findViewById(R.id.year);
		year.setAdapter(new NumericWheelAdapter(1950, norYear));
		year.setLabel("年");
		year.setCyclic(true);
		year.addScrollingListener(scrollListener);

		month = (WheelView) view.findViewById(R.id.month);
		month.setAdapter(new NumericWheelAdapter(1, 12, "%02d"));
		month.setLabel("月");
		month.setCyclic(true);
		month.addScrollingListener(scrollListener);

		year.setCurrentItem(curYear - 1950);
		month.setCurrentItem(curMonth - 1);

		return view;
	}

	NoDayOnWheelScrollListener scrollListener = new NoDayOnWheelScrollListener() {
		@Override
		public void onScrollingStarted(WheelView wheel) {

		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			// int n_year = year.getCurrentItem() + 1950;
			// int n_month = month.getCurrentItem() + 1;
			mYear = year.getCurrentItem() + 1950;
			mMonth = month.getCurrentItem() + 1;
		}
	};
	private LinearLayout line_riqi;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	/**
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	private int getDay(int year, int month) {
		int day = 30;
		boolean flag = false;
		switch (year % 4) {
		case 0:
			flag = true;
			break;
		default:
			flag = false;
			break;
		}
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			day = 31;
			break;
		case 2:
			day = flag ? 29 : 28;
			break;
		default:
			day = 30;
			break;
		}
		return day;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			String card_num = data.getStringExtra("cardnum");
			card_id = data.getStringExtra("card_id");
			tv_bank_card.setText(card_num);
		}
	}

}
