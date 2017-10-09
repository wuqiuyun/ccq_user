package ccj.yun28.com.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.mine.AllDinDanActivtiy;
import ccj.yun28.com.mine.DSHDinDanActivtiy;
import ccj.yun28.com.mine.DSYDinDanActivtiy;
import ccj.yun28.com.mine.MyAddressActivity;
import ccj.yun28.com.mine.NewMyWalletActivity;
import ccj.yun28.com.mine.NewOpenStoreActivtiy;
import ccj.yun28.com.mine.NewSettingInfoActivtiy;
import ccj.yun28.com.mine.NewYunBiORYuEDetailActivity;
import ccj.yun28.com.mine.SettingActivtiy;
import ccj.yun28.com.mine.SettingInfoActivtiy;
import ccj.yun28.com.mine.VIPhuiyuanActivity;
import ccj.yun28.com.mine.YunBiORYuEDetailActivity;
import ccj.yun28.com.sy.MessageActivcity;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.CircleImageView;
import cn.sharesdk.framework.ShareSDK;

import com.example.ccqsharesdk.onekeyshare.OnekeyShare;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.umeng.analytics.MobclickAgent;

/**
 * 新版我的页(废弃)
 * 
 * @author meihuali
 * 
 */
public class MEFragment extends Fragment implements OnClickListener {

	private DBHelper myDB;
	// 头像
	private CircleImageView img_circle;
	// vip标志
	private ImageView iv_vip;
	// 昵称
	private TextView tv_nicheng;
	private LinearLayout line_nicheng;
	// vip时间
	private RelativeLayout rel_viptime;
	private TextView tv_viptime;
	private LinearLayout line_viptime;
	// 续费
	private TextView tv_xufei;
	// 关联手机
	private LinearLayout line_bangdingphone;
	private TextView tv_bangdingphone;
	//升级成为VIP
	private TextView tv_shengji;
	// 默认地址
	private LinearLayout line_morendizhi;
	private TextView tv_morendizhi;
	// 餐餐券
	private TextView tv_ccj;
	// 收藏店铺
	private TextView tv_scdp;
	// 余额
	private TextView tv_yue;
	// 云币
	private TextView tv_yunbi;
	// 最新消息
	private ImageView iv_zxxx;

	//分享信息
	private String fxmsg = "";
	//分享url
	private String fxurl = "";
	
	private String member_qrcode;
	private String member_name;
	
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 错误
	private static final int HANDLER_NN_FAILURE = 1;
	private static final int HANDLER_MYINFO_SUCCESS = 2;
	protected static final int HANDLER_GETINFO_FAILURE = 3;
	private static final int HANDLER_ERWEIMA_SUCCESS = 4;
	private static final int HANDLER_TOKEN_FAILURE = 5;

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				if (getActivity() != null) {
					Toast.makeText(getActivity(), "当前网络不可用,请检查网络",
							Toast.LENGTH_SHORT).show();
				}
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				if (getActivity() != null) {
					Toast.makeText(getActivity(), "当前网络出错,请检查网络",
							Toast.LENGTH_SHORT).show();
				}
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (getActivity() != null) {
					if (msg.obj != null) {
						Toast.makeText(getActivity(),
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();
					}
				}
				break;
			// 获取信息成功
			case HANDLER_MYINFO_SUCCESS:
				dissDialog();
				break;
				// 获取二维码信息成功
			case HANDLER_ERWEIMA_SUCCESS:
				dissDialog();
				fxmsg = "您的朋友【" + member_name + "】邀请您来餐餐抢消费，天天抢红包，天天抢折扣券哦~~餐餐抢一起与你省钱到底！";
				fxurl = SharedUtil.getStringValue(SharedCommon.REG, "")+ "?inviter_id=" + new DButil().getMember_id(getActivity());
				showShare();
				break;
				// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (getActivity() != null) {
					if (msg.obj != null) {
						Toast.makeText(getActivity(),
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();
					}
				}
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_me, null);
		// 设置
		LinearLayout line_setting = (LinearLayout) view
				.findViewById(R.id.line_setting);
		// 头像
		img_circle = (CircleImageView) view.findViewById(R.id.img_circle);
		// vip标志
		iv_vip = (ImageView) view.findViewById(R.id.iv_vip);
		// 昵称
		line_nicheng = (LinearLayout) view.findViewById(R.id.line_nicheng);
		tv_nicheng = (TextView) view.findViewById(R.id.tv_nicheng);
		// VIP时间
		rel_viptime = (RelativeLayout) view.findViewById(R.id.rel_viptime);
		tv_viptime = (TextView) view.findViewById(R.id.tv_viptime);
		line_viptime = (LinearLayout) view.findViewById(R.id.line_viptime);
		// 续费
		tv_xufei = (TextView) view.findViewById(R.id.tv_xufei);
		// 已关联手机
		line_bangdingphone = (LinearLayout) view
				.findViewById(R.id.line_bangdingphone);
		tv_bangdingphone = (TextView) view.findViewById(R.id.tv_bangdingphone);
		//升级成为vip
		tv_shengji = (TextView) view.findViewById(R.id.tv_shengji);
		// 默认收货地址
		line_morendizhi = (LinearLayout) view
				.findViewById(R.id.line_morendizhi);
		tv_morendizhi = (TextView) view.findViewById(R.id.tv_morendizhi);
		// 餐餐券
		LinearLayout line_ccq = (LinearLayout) view.findViewById(R.id.line_ccq);
		tv_ccj = (TextView) view.findViewById(R.id.tv_ccj);
		// 收藏店铺
		LinearLayout line_scstore = (LinearLayout) view.findViewById(R.id.line_scstore);
		tv_scdp = (TextView) view.findViewById(R.id.tv_scdp);
		// 余额
		LinearLayout line_yue = (LinearLayout) view.findViewById(R.id.line_yue);
		tv_yue = (TextView) view.findViewById(R.id.tv_yue);
		// 云币
		LinearLayout line_yunbi = (LinearLayout) view.findViewById(R.id.line_yunbi);
		tv_yunbi = (TextView) view.findViewById(R.id.tv_yunbi);
		// 个人资料
		LinearLayout line_grzl = (LinearLayout) view
				.findViewById(R.id.line_grzl);
		// 收货地址管理
		LinearLayout line_shdzgl = (LinearLayout) view
				.findViewById(R.id.line_shdzgl);
		// 我的订单
		LinearLayout line_wddd = (LinearLayout) view
				.findViewById(R.id.line_wddd);
		// 云币管理
		LinearLayout line_ybgl = (LinearLayout) view
				.findViewById(R.id.line_ybgl);
		// 资金管理
		LinearLayout line_zjgl = (LinearLayout) view
				.findViewById(R.id.line_zjgl);
		// 最新消息
		LinearLayout line_zxxx = (LinearLayout) view
				.findViewById(R.id.line_zxxx);
		iv_zxxx = (ImageView) view.findViewById(R.id.iv_zxxx);
		
		// 自己入驻
		LinearLayout line_zjrz = (LinearLayout) view
				.findViewById(R.id.line_zjrz);
		// 推荐朋友入驻
		LinearLayout line_tjpyrz = (LinearLayout) view
				.findViewById(R.id.line_tjpyrz);
		// 和小伙伴分享
		LinearLayout line_hxhbfx = (LinearLayout) view
				.findViewById(R.id.line_hxhbfx);

		myDB = new DBHelper(getActivity());
		if (isLogin()) {
			showLoading();
			myInfoHttpPost(getActivity());
		} else {
			tv_nicheng.setText("未登录");
		}
		img_circle.setOnClickListener(this);
		line_nicheng.setOnClickListener(this);
		line_bangdingphone.setOnClickListener(this);
		line_viptime.setOnClickListener(this);
		line_setting.setOnClickListener(this);
		tv_xufei.setOnClickListener(this);
		tv_shengji.setOnClickListener(this);
		line_morendizhi.setOnClickListener(this);
		line_grzl.setOnClickListener(this);
		line_shdzgl.setOnClickListener(this);
		line_wddd.setOnClickListener(this);
		line_ybgl.setOnClickListener(this);
		line_zjgl.setOnClickListener(this);
		line_zxxx.setOnClickListener(this);
		line_zjrz.setOnClickListener(this);
		line_tjpyrz.setOnClickListener(this);
		line_hxhbfx.setOnClickListener(this);
		line_ccq.setOnClickListener(this);
		line_scstore.setOnClickListener(this);
		line_yue.setOnClickListener(this);
		line_yunbi.setOnClickListener(this);
		return view;
	}

	// 我的信息接口
	public void myInfoHttpPost(Context context) {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(context);
		RequestParams params = new RequestParams();
//		String as = new DButil().gettoken(getActivity());
//		String ds = new DButil().getMember_id(getActivity());
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v","v3");
		params.addBodyParameter("token", new DButil().gettoken(context));
//		params.addBodyParameter("member_id","3");
		params.addBodyParameter("member_id",
				new DButil().getMember_id(context));
		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWMYINFO, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						myInfoListInfo(arg0.result);
					}
				});
	}

	// 我的信息接口返回数据
	protected void myInfoListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				JSONObject data = object.getJSONObject("data");
				String address_def = data.getString("address_def");//默认地址
				String avator = data.getString("avator");//头像地址
				String ccq_num = data.getString("ccq_num");//待使用餐餐抢券数量
				String collect_store = data.getString("collect_store");//收藏店铺数量
				String countnum = data.getString("countnum");//未读消息数量 等于0时表示没有未读消息
				String current_balance = data.getString("current_balance");//余额
				String current_point = data.getString("current_point");//云币
				String member_mobile = data.getString("member_mobile");//关联手机号
				String member_nickname = data.getString("member_nickname");//昵称
				String vip_time = data.getString("vip_time");//vip截止时间
				tv_nicheng.setText(member_nickname);
				if (TextUtils.isEmpty(vip_time)) {
					tv_shengji.setVisibility(View.VISIBLE);
					rel_viptime.setVisibility(View.GONE);
					line_bangdingphone.setVisibility(View.GONE);
					iv_vip.setVisibility(View.GONE);
				}else{
					tv_shengji.setVisibility(View.GONE);
					rel_viptime.setVisibility(View.VISIBLE);
					line_bangdingphone.setVisibility(View.VISIBLE);
					tv_viptime.setText(vip_time);
					tv_bangdingphone.setText(member_mobile);
					iv_vip.setVisibility(View.VISIBLE);
				}
				
				if (TextUtils.isEmpty(address_def)) {
					line_morendizhi.setVisibility(View.GONE);
				}else{

					line_morendizhi.setVisibility(View.VISIBLE);
					tv_morendizhi.setText("默认收货地址： "+ address_def);
				}
				tv_ccj.setText(ccq_num);
				tv_scdp.setText(collect_store);
				tv_yue.setText(current_balance);
				tv_yunbi.setText(current_point);
				if ("0".equals(countnum)) {
					iv_zxxx.setBackgroundResource(R.drawable.zuixinxx);
				}else {
					iv_zxxx.setBackgroundResource(R.drawable.xxx);
				}
				
				BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
				bitmapUtils.display(img_circle, avator);
				handler.sendEmptyMessage(HANDLER_MYINFO_SUCCESS);
			} else if ("700".equals(status)){
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(HANDLER_TOKEN_FAILURE, message));
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(HANDLER_GETINFO_FAILURE, message));
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
		Intent intent;
		switch (v.getId()) {
		case R.id.img_circle:
			if (isLogin()) {
				intent = new Intent(getActivity(), NewSettingInfoActivtiy.class);
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
			}
			break;
		case R.id.line_nicheng:
			if (isLogin()) {
				intent = new Intent(getActivity(), NewSettingInfoActivtiy.class);
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
			}
			break;
		case R.id.line_bangdingphone:
			if (isLogin()) {
				intent = new Intent(getActivity(), NewSettingInfoActivtiy.class);
				startActivity(intent);
			}
			break;
		case R.id.line_viptime:
			if (isLogin()) {
				intent = new Intent(getActivity(), NewSettingInfoActivtiy.class);
				startActivity(intent);
			}
			break;
		case R.id.line_setting:
			if (isLogin()) {
				intent = new Intent(getActivity(), SettingActivtiy.class);
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
			}
			break;
		case R.id.tv_xufei:
			if (isLogin()) {
				intent = new Intent(getActivity(), VIPhuiyuanActivity.class);
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);

			}
			break;
		case R.id.tv_shengji:
			if (isLogin()) {
				intent = new Intent(getActivity(), VIPhuiyuanActivity.class);
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
				
			}
			break;
		case R.id.line_morendizhi:
			if (isLogin()) {
				intent = new Intent(getActivity(), MyAddressActivity.class);
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);

			}
			break;
		case R.id.line_grzl:
			if (isLogin()) {
//				intent = new Intent(getActivity(), SettingInfoActivtiy.class);
				intent = new Intent(getActivity(), NewSettingInfoActivtiy.class);
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
				
			}
			break;
		case R.id.line_shdzgl:
			if (isLogin()) {
				intent = new Intent(getActivity(), MyAddressActivity.class);
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
				
			}
			break;
		case R.id.line_wddd:
			if (isLogin()) {
				intent = new Intent(getActivity(), AllDinDanActivtiy.class);
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
				
			}
			break;
			
		case R.id.line_ybgl:
			if (isLogin()) {
				intent = new Intent(getActivity(),
						NewYunBiORYuEDetailActivity.class);
				intent.putExtra("title", "云币");
				intent.putExtra("leixing", "yunbi");
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
				
			}
			break;
		case R.id.line_zjgl:
			if (isLogin()) {
				intent = new Intent(getActivity(), NewMyWalletActivity.class);
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
				
			}
			break;
		case R.id.line_zxxx:
			if (isLogin()) {
				intent = new Intent(getActivity(), MessageActivcity.class);
				intent.putExtra("type", "宝贝");
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
				
			}
			break;
		case R.id.line_zjrz:
			if (isLogin()) {
				intent = new Intent(getActivity(), NewOpenStoreActivtiy.class);
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
				
			}
			break;
		case R.id.line_tjpyrz:
			if (isLogin()) {
//				tjfxmsg = "作为你的好朋友，我忍不住给你推荐一个平台帮你暴增客流量，提升营业额，别犹豫，相信我！马上入驻吧！";
//				tjfxurl = "http://www.28yun.com/api_wap/tmpl/merchant/merchant_enter.html";
//				tjfxpic = SharedUtil.getStringValue(SharedCommon.MALL_FX, "");
				tjshowShare();
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
			}
			break;
		case R.id.line_hxhbfx:
			if (isLogin()) {
				showLoading();
				erweimaHttpPost();
				
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
				
			}
			break;
		case R.id.line_ccq:
			if (isLogin()) {
				intent = new Intent(getActivity(), AllDinDanActivtiy.class);
				intent.putExtra("type", "dsy");
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
				
			}
			break;
		case R.id.line_scstore:
			if (isLogin()) {
				Toast.makeText(getActivity(), "此功能暂未开放", Toast.LENGTH_SHORT).show();
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
			}
			break;
		case R.id.line_yue:
			if (isLogin()) {
				intent = new Intent(getActivity(),
						NewYunBiORYuEDetailActivity.class);
				intent.putExtra("title", "余额");
				intent.putExtra("leixing", "yue");
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
				
			}
			break;
		case R.id.line_yunbi:
			if (isLogin()) {
				intent = new Intent(getActivity(),
						NewYunBiORYuEDetailActivity.class);
				intent.putExtra("title", "云币");
				intent.putExtra("leixing", "yunbi");
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
				
			}
			break;

		default:
			break;
		}
	}
	
	private void tjshowShare() {
		// TODO Auto-generated method stub
		ShareSDK.initSDK(getActivity(), "171a7e7c3c736");
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// 信息分享时电话
//		oks.setAddress(zhuproduct.getData().getStore_info().getStore_tel());
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("【餐餐抢】");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(SharedUtil.getStringValue(
				SharedCommon.MALL_QRCODE_IMG_AZ_URL, ""));
		// text是分享文本，所有平台都需要这个字段
		oks.setText("作为你的好朋友，我忍不住给你推荐一个平台帮你暴增客流量，提升营业额，别犹豫，相信我！马上入驻吧！");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath(SharedUtil.getStringValue(SharedCommon.MALL_FX, ""));// 确保SDcard下面存在此张图片
		oks.setImageUrl(SharedUtil.getStringValue(SharedCommon.MALL_FX, ""));
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://www.28yun.com/api_wap/tmpl/merchant/merchant_enter.html");
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

	private void erweimaHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(getActivity());
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(getActivity()));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(getActivity()));
		httpUtils.send(HttpMethod.POST, JiekouUtils.TUIGUANGERWEIMA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e("ee", "失败:" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
						// handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						erweimaJsonInfo(arg0.result);
					}
				});
	}

	protected void erweimaJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				member_qrcode = object.getString("member_qrcode");
				JSONObject jo = object.getJSONObject("data");
				member_name = jo.getString("member_name");
				String avator = jo.getString("avator");
				handler.sendEmptyMessage(HANDLER_ERWEIMA_SUCCESS);
			} else if ("700".equals(status)){
				String message = object.getString("message");
				handler.sendEmptyMessage(HANDLER_TOKEN_FAILURE);
			}else {
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

	private void showShare() {
		ShareSDK.initSDK(getActivity(), "171a7e7c3c736");
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// 信息分享时电话
		//oks.setAddress("");
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("【 餐餐抢 】");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(SharedUtil.getStringValue(
				SharedCommon.MALL_QRCODE_IMG_AZ_URL, ""));
		// text是分享文本，所有平台都需要这个字段
		oks.setText(fxmsg);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath("");// 确保SDcard下面存在此张图片
		oks.setImageUrl(member_qrcode);
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(fxurl);
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
	
	// 加载中动画
	private Dialog loadingDialog;

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

	// 校验登录与否
	private boolean isLogin() {
		try {
			if (myDB != null) {
				SQLiteDatabase db = myDB.getReadableDatabase();
				Cursor cursor = db.rawQuery(
						"select * from user where status = 1", null);
				if (cursor == null || cursor.getCount() == 0) {
					return false;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return true;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			if (isLogin()) {
				myInfoHttpPost(getActivity());
			} else {
				tv_nicheng.setText("未登录");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		MobclickAgent.onResume(getActivity());

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(getActivity());
	}

}
