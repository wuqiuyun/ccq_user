package ccj.yun28.com.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.LikeGridAdapter;
import ccj.yun28.com.bean.ZhuSignBean;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.rili.CalendarEntity;
import ccj.yun28.com.sy.ProductDetailActivity;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.CircleImageView;
import ccj.yun28.com.view.MyGridView;
import ccj.yun28.com.view.MyScrollView;
import ccj.yun28.com.view.OnRefreshListener;
import ccj.yun28.com.view.WebviewActivity;
import ccj.yun28.com.view.zoumadeng.Sentence;
import ccj.yun28.com.view.zoumadeng.VerticalScrollTextView;
import cn.sharesdk.framework.ShareSDK;

import com.example.ccqsharesdk.onekeyshare.OnekeyShare;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 签到页
 * 
 * @author meihuali
 * 
 */
public class SignFragment extends Fragment implements OnClickListener,
		OnItemClickListener {

	private MyScrollView scrollView;
	// 关闭dialog
	private LinearLayout line_exit;
	// 头像
	private CircleImageView img_circle;
	// 用户名
	private TextView tv_user_name;
	// 昨日云币收益
	private TextView tv_yes_yun;
	// 点击签到
	private LinearLayout line_sign_bg;
	// 红包个数
	private TextView tv_hongbao;
	// 云币总收益
	private TextView tv_yunbi;
	// 广告图
	private ImageView iv_guanggao;
	// 拆红包
	private ImageView iv_chai;
	// 分享
	private TextView tv_fx;
	// 走马灯
	private VerticalScrollTextView mSampleView;
	// 拆红包dialog
	private Dialog caidialog;
	// 红包dialog
	private Dialog signdialog;
	// 走马灯当前数据
	private List lst;
	// 是否签到
	private String sflhb;
	// 是否可以领红包
	private String sfqd;
	// 猜我喜欢
	private MyGridView gv_like;
	private LikeGridAdapter likegridadapter;

	private BitmapUtils bitmapUtils;

	private List<Map<String, String>> guesslikeList;

	// 签到日历
	private GridView mGridView;
	private TextView tv_title;
	private TextView tv_count;

	private CalendarAdapter adapter;

	private Calendar mCalendar;
	private String[] weeks = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
	private int curDay;// 当前日子
	private int kong;

	private static final int HANDLER_GUESSLIKE_SUCCESS = 10;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	private static final int HANDLER_SIGNINFO_SUCCESS = 1;
	private static final int HANDLER_SIGN_SUCCESS = 2;
	private static final int HANDLER_NN_FAILURE = 3;
	private static final int HANDLER_CHAIHONGBAO_SUCCESS = 4;
	private static final int HANDLER_GETINFO_FAILURE = 5;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				line_net_error.setVisibility(View.VISIBLE);
				scrollView.onResfreshFinish();
				if (getActivity() != null) {
					Toast.makeText(getActivity(), "当前网络不可用,请检查网络", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// 获取失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				line_net_error.setVisibility(View.VISIBLE);
				scrollView.onResfreshFinish();
				if (getActivity() != null) {
					if (msg.obj != null) {
						Toast.makeText(getActivity(),
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();
					}
				}
				break;
			// 异常
			case HANDLER_NN_FAILURE:
				dissDialog();
				scrollView.onResfreshFinish();
				line_net_error.setVisibility(View.VISIBLE);
				if (getActivity() != null) {
					Toast.makeText(getActivity(), "当前网络出错,请检查网络", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// 获取签到信息成功
			case HANDLER_SIGNINFO_SUCCESS:
				dissDialog();
				line_net_error.setVisibility(View.GONE);
				// 给View传递数据
				mSampleView.setList(lst);
				// 更新View
				mSampleView.updateUI();
				if ("1".equals(sflhb)) {
					if (caidialog == null) {
						showHongBaoDialog();
					}
				}
				// "1",没有签到
				if ("1".equals(sfqd)) {
					line_sign_bg.setBackgroundResource(R.drawable.qiandao);
				} else {
					line_sign_bg.setBackgroundResource(R.drawable.yiqian);
				}
				break;
			// 签到成功
			case HANDLER_SIGN_SUCCESS:
				// dissDialog();
				line_net_error.setVisibility(View.GONE);
				Toast.makeText(getActivity(), msg.obj.toString().trim(),
						Toast.LENGTH_SHORT).show();
				sfqd = "0";
				signHttpPost(getActivity());
				line_sign_bg.setBackgroundResource(R.drawable.yiqian);
				break;
			// 猜你喜欢
			case HANDLER_GUESSLIKE_SUCCESS:
				dissDialog();
				line_net_error.setVisibility(View.GONE);
				scrollView.onResfreshFinish();
				likegridadapter.NotifyList(guesslikeList);
				likegridadapter.notifyDataSetChanged();
				break;
			// 拆红包成功
			case HANDLER_CHAIHONGBAO_SUCCESS:
				line_net_error.setVisibility(View.GONE);
				dissDialog();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_sign, null);

		line_net_error = (LinearLayout) view
				.findViewById(R.id.line_net_error);
		TextView tv_notnet_refresh = (TextView) view.findViewById(R.id.tv_notnet_refresh);
		scrollView = (MyScrollView) view.findViewById(R.id.scrollView);

		// 头像
		img_circle = (CircleImageView) view.findViewById(R.id.img_circle);
		// 用户名
		tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
		// 签到规则
		LinearLayout line_sign_rule = (LinearLayout) view
				.findViewById(R.id.line_sign_rule);
		// 昨日云币收益
		tv_yes_yun = (TextView) view.findViewById(R.id.tv_yes_yun);
		// 走马灯
		mSampleView = (VerticalScrollTextView) view
				.findViewById(R.id.sampleView1);
		// 是否签到签到背景
		line_sign_bg = (LinearLayout) view.findViewById(R.id.line_sign_bg);
		// 点击签到
		ImageView iv_sign = (ImageView) view.findViewById(R.id.iv_sign);
		// 红包个数
		tv_hongbao = (TextView) view.findViewById(R.id.tv_hongbao);
		// 云币总收益
		tv_yunbi = (TextView) view.findViewById(R.id.tv_yunbi);
		// 广告图
		iv_guanggao = (ImageView) view.findViewById(R.id.iv_guanggao);
		// 猜你喜欢gridview
		gv_like = (MyGridView) view.findViewById(R.id.gv_like);
		guesslikeList = new ArrayList<Map<String, String>>();
		likegridadapter = new LikeGridAdapter(getActivity());
		gv_like.setAdapter(likegridadapter);

		bitmapUtils = new BitmapUtils(getActivity());
		lst = new ArrayList<Sentence>();

		// 日历
		tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_count = (TextView) view.findViewById(R.id.tv_count);
		mGridView = (GridView) view.findViewById(R.id.calendar);

		mCalendar = Calendar.getInstance();
		tv_title.setText((mCalendar.get(Calendar.MONTH) + 1) + "月签到");

		// adapter = new CalendarAdapter(getDateList());
		// mGridView.setAdapter(adapter);
		
		showLoading();
		signHttpPost(getActivity());
		guesslikeHttpPost();

		tv_notnet_refresh.setOnClickListener(this);
		line_sign_rule.setOnClickListener(this);
		iv_sign.setOnClickListener(this);
		iv_guanggao.setOnClickListener(this);
		gv_like.setOnItemClickListener(this);
		// mGridView.setOnItemClickListener(this);

		initListView();
		mSampleView.setFocusable(false);
		mGridView.setFocusable(false);
		gv_like.setFocusable(false);
		return view;
	}

	private int nowPage = 1;

	// 猜你喜欢数据
	private void guesslikeHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(getActivity());
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("type", "1");
		params.addBodyParameter("member_id",
				new DButil().getMember_id(getActivity()));
		params.addBodyParameter("page", nowPage + "");
		httpUtils.send(HttpMethod.POST, JiekouUtils.GUESSLIKE, params,
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
						guesslikeJsonInfo(arg0.result);
					}
				});
	}

	// 解析猜你喜欢返回的数据
	protected void guesslikeJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				JSONArray data = object.getJSONArray("data");
				for (int i = 0; i < data.length(); i++) {
					JSONObject json = data.getJSONObject(i);
					guesslikeListInfo(json);
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

	private void guesslikeListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String goods_id = json.getString("goods_id");
			String goods_name = json.getString("goods_name");
			String goods_price = json.getString("goods_price");
			String goods_image = json.getString("goods_image");
			String union_goods = json.getString("union_goods");
			String store_id = json.getString("store_id");
			Map<String, String> guesslikeMap = new HashMap<String, String>();
			guesslikeMap.put("goods_id", goods_id);
			guesslikeMap.put("goods_name", goods_name);
			guesslikeMap.put("goods_price", goods_price);
			guesslikeMap.put("goods_image", goods_image);
			guesslikeMap.put("union_goods", union_goods);
			guesslikeMap.put("store_id", store_id);
			guesslikeList.add(guesslikeMap);
			handler.sendEmptyMessage(HANDLER_GUESSLIKE_SUCCESS);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 获取签到数据接口
	public void signHttpPost(Context context) {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(context);
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token", new DButil().gettoken(context));
		params.addBodyParameter("member_id", new DButil().getMember_id(context));
		httpUtils.send(HttpMethod.POST, JiekouUtils.SIGNINFO, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						signListInfo(arg0.result);
					}
				});
	}

	// 解析签到数据
	protected void signListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			JSONObject job = object.getJSONObject("data");
			if ("200".equals(status)) {
				zhuSignBean = null;
				Gson gson = new Gson();
				zhuSignBean = gson.fromJson(result, ZhuSignBean.class);

				bitmapUtils.display(img_circle, zhuSignBean.getData()
						.getAvator());
				tv_user_name.setText(zhuSignBean.getData().getMember_name());
				tv_yes_yun.setText(zhuSignBean.getData().getLast_red_packet());
				tv_hongbao.setText(zhuSignBean.getData().getNum());
				tv_yunbi.setText(zhuSignBean.getData().getSum());
				ggurl = zhuSignBean.getData().getBanner_url();
				bitmapUtils.display(iv_guanggao, zhuSignBean.getData()
						.getBanner());
				sfqd = zhuSignBean.getData().getIs_do_sign();
				sflhb = zhuSignBean.getData().getIs_red_packet();
				List<String> zoumadeng = zhuSignBean.getData().getOther();

				for (int i = 0; i < zoumadeng.size(); i++) {
					Sentence sen = new Sentence(i, zoumadeng.get(i));
					lst.add(i, sen);
				}
				tv_count.setText(zhuSignBean.getData().getSign_num());
				adapter = new CalendarAdapter(getDateList());
				mGridView.setAdapter(adapter);
				handler.sendEmptyMessage(HANDLER_SIGNINFO_SUCCESS);
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
		case R.id.tv_notnet_refresh:
			showLoading();
			signHttpPost(getActivity());
			guesslikeHttpPost();
			break;
		// 签到规则
		case R.id.line_sign_rule:
			String sign_rule = SharedUtil.getStringValue(
					SharedCommon.MALL_RED_PACKET, "");
			Intent intent = new Intent(getActivity(), WebviewActivity.class);
			intent.putExtra("url", sign_rule);
			intent.putExtra("title", "签到规则");
			getActivity().startActivity(intent);
			break;
		// 签到
		case R.id.iv_sign:
			if ("1".equals(sfqd)) {
				showLoading();
				qiandaoHttpPost();
			}
			break;
		// 广告图
		case R.id.iv_guanggao:
			Intent ggintent = new Intent(getActivity(), WebviewActivity.class);
			ggintent.putExtra("url", ggurl);
			ggintent.putExtra("title", "VIP会员专区");
			startActivity(ggintent);
			break;
		// 关闭
		case R.id.line_exit:
			signdialog.dismiss();
			showLoading();
			signHttpPost(getActivity());
			break;
		// 拆红包
		case R.id.iv_chai:
			caidialog.dismiss();
			showLoading();
			chaihongbaoHttpPost();
			View view2 = LayoutInflater.from(getActivity()).inflate(
					R.layout.dialog_sign, null);
			line_exit = (LinearLayout) view2.findViewById(R.id.line_exit);
			tv_fx = (TextView) view2.findViewById(R.id.tv_fx);
			tv_yunbi_num = (TextView) view2.findViewById(R.id.tv_yunbi_num);
			line_exit.setOnClickListener(this);
			tv_fx.setOnClickListener(this);
			signdialog = new Dialog(getActivity(), R.style.mDialogStyle);
			signdialog.setContentView(view2);
			signdialog.setCanceledOnTouchOutside(false);
			signdialog.show();
			break;
		// 分享
		case R.id.tv_fx:
			showShare();
			break;

		default:
			break;
		}
	}

	// 拆红包接口
	private void chaihongbaoHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(getActivity());
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token", new DButil().gettoken(getActivity()));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(getActivity()));
		httpUtils.send(HttpMethod.POST, JiekouUtils.CHAIHONGBAO, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						chaihongbaoListInfo(arg0.result);
					}
				});
	}

	protected void chaihongbaoListInfo(String result) {
		// TODO Auto-generated method stub
		// 解析签到接口返回数据
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONObject data = object.getJSONObject("data");
				String mon = data.getString("mon");
				String remainSize = data.getString("remainSize");
				tv_yunbi_num.setText(mon);
				handler.sendEmptyMessage(HANDLER_CHAIHONGBAO_SUCCESS);
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

	// 调用签到接口
	private void qiandaoHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(getActivity());
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token", new DButil().gettoken(getActivity()));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(getActivity()));
		params.addBodyParameter("type", "0");
		httpUtils.send(HttpMethod.POST, JiekouUtils.QIANDAO, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						qiandaoListInfo(arg0.result);
					}
				});
	}

	// 解析签到接口返回数据
	protected void qiandaoListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			String message = object.getString("message");
			handler.sendMessage(handler.obtainMessage(HANDLER_SIGN_SUCCESS,
					message));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}

	}

	private void showHongBaoDialog() {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_cai, null);
		iv_chai = (ImageView) view.findViewById(R.id.iv_chai);
		iv_chai.setOnClickListener(this);
		caidialog = new Dialog(getActivity(), R.style.mDialogStyle);
		caidialog.setContentView(view);
		caidialog.setCanceledOnTouchOutside(false);
		caidialog.show();
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
					guesslikeHttpPost();
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		});
	}

	// 加载中动画
	private Dialog loadingDialog;
	private String ggurl;
	private TextView tv_yunbi_num;
	private ZhuSignBean zhuSignBean;
	private LinearLayout line_net_error;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(getActivity(), R.style.mDialogStyle);
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

	private void showShare() {
		ShareSDK.initSDK(getActivity(), "171a7e7c3c736");
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// 信息分享时电话
		oks.setAddress("");
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("【餐餐抢】");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(SharedUtil.getStringValue(
				SharedCommon.MALL_QRCODE_IMG_AZ_URL, ""));
		// text是分享文本，所有平台都需要这个字段
		oks.setText("您的朋友【" + zhuSignBean.getData().getMember_name()
				+ "】在餐餐抢签到抢到啦一个大红包，快来围观啊~~~");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		String fx_pic = SharedUtil.getStringValue(SharedCommon.MALL_FX, "");
		oks.setImagePath(fx_pic);// 确保SDcard下面存在此张图片
		oks.setImageUrl(fx_pic);
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(SharedUtil.getStringValue(
				SharedCommon.MALL_QRCODE_IMG_AZ_URL, ""));
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(SharedUtil.getStringValue(
				SharedCommon.MALL_QRCODE_IMG_AZ_URL, ""));

		// 启动分享GUI
		oks.show(getActivity());
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
		intent.putExtra("goods_id", guesslikeList.get(position).get("goods_id"));
		startActivity(intent);
	}

	private List<CalendarEntity> getDateList() {

		int year = mCalendar.get(Calendar.YEAR);
		int month = mCalendar.get(Calendar.MONTH);
		curDay = mCalendar.get(Calendar.DATE);

		List<CalendarEntity> list = new ArrayList<CalendarEntity>();

		// ------星期-----
		for (int i = 0; i < weeks.length; i++) {
			CalendarEntity entity = new CalendarEntity();
			entity.setName(weeks[i]);
			entity.setHasSignIn(false);
			list.add(entity);
		}

		// -------获取当前月1号是星期几,意味着前面要空几格
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, 1);
		int kong = c.get(Calendar.DAY_OF_WEEK) - 1;

		for (int i = 0; i < kong; i++) {
			CalendarEntity entity = new CalendarEntity();
			entity.setName("");
			entity.setHasSignIn(false);
			list.add(entity);
		}
		// -------

		boolean flag = true;
		// -------获取当前月有多少天
		int monthOfDay = mCalendar.getActualMaximum(Calendar.DATE);
		for (int i = 0; i < monthOfDay; i++) {
			flag = true;
			CalendarEntity entity = new CalendarEntity();
			for (int j = 0; j < zhuSignBean.getData().getSign_list().size(); j++) {
				String signday = zhuSignBean.getData().getSign_list().get(j)
						.getDay();
				if (i + 1 == Integer.parseInt(signday)) {
					flag = false;
					entity.setName((i + 1) + "");
					entity.setHasSignIn(true);
				}
			}
			if (flag) {
				entity.setName((i + 1) + "");
				entity.setHasSignIn(false);
			}
			list.add(entity);
		}
		return list;
	}

	private class CalendarAdapter extends BaseAdapter {

		private List<CalendarEntity> list;

		public CalendarAdapter(List<CalendarEntity> list) {
			this.list = list;
		}

		public List<CalendarEntity> getList() {
			return list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_grid_calendar, null);
				holder.week = (TextView) convertView
						.findViewById(R.id.item_calendar_week);
				holder.date = (TextView) convertView
						.findViewById(R.id.item_calendar_date);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			CalendarEntity entity = list.get(position);
			if (position < 7) {// 星期
				holder.week.setVisibility(View.VISIBLE);
				holder.date.setVisibility(View.GONE);
				holder.week.setText(entity.getName());
			} else {
				if (TextUtils.isEmpty(entity.getName())) {// 前面空的几格
					holder.week.setVisibility(View.GONE);
					holder.date.setVisibility(View.GONE);
				} else {// 日期
					holder.week.setVisibility(View.GONE);
					holder.date.setVisibility(View.VISIBLE);
					holder.date.setText(entity.getName());

					int nowDate = Integer.parseInt(entity.getName());
					if (!entity.isHasSignIn()) {// 未签到
						if (curDay < nowDate) {// 以后的日期
							holder.date.setBackgroundResource(R.drawable.kong);
						} else if (curDay == nowDate) {// 当前日期
							holder.date.setBackgroundResource(R.drawable.bu);
						} else {// 前面的日期
							holder.date.setBackgroundResource(R.drawable.bu);
						}
					} else if (entity.isHasSignIn()) {
						holder.date.setBackgroundResource(R.drawable.qian);

					}

				}

			}

			return convertView;
		}
	}

	private class ViewHolder {
		TextView week;
		TextView date;
	}
}
