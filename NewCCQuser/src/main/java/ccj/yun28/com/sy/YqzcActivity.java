package ccj.yun28.com.sy;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.MainActivity1;
import ccj.yun28.com.R;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.mine.ProblemActivity;
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

/**
 * 首页-邀请注册
 * 
 * @author meihuali
 * 
 */
public class YqzcActivity extends BaseActivity implements OnClickListener {

	// 更多图标
	private ImageView iv_more;
	// 弹窗
	private PopupWindow popupWindow;
	// 头像
	private CircleImageView img_circle;
	// 用户名
	private TextView tv_user;
	// 二维码
	private ImageView iv_erweima;

	private String member_name;
	private String member_qrcode;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取信息成功
	private static final int HANDLER_ERWEIMA_SUCCESS = 3;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(YqzcActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(YqzcActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(YqzcActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// 获取商品信息成功
			case HANDLER_ERWEIMA_SUCCESS:
				dissDialog();
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
		setContentView(R.layout.activity_yqzc);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 消息
		LinearLayout line_more = (LinearLayout) findViewById(R.id.line_more);
		iv_more = (ImageView) findViewById(R.id.iv_more);
		// 头像
		img_circle = (CircleImageView) findViewById(R.id.img_circle);
		// 用户名
		tv_user = (TextView) findViewById(R.id.tv_user);
		// 二维码
		iv_erweima = (ImageView) findViewById(R.id.iv_erweima);
		// 分享
		TextView tv_fx = (TextView) findViewById(R.id.tv_fx);

		showLoading();
		erweimaHttpPost();

		line_back.setOnClickListener(this);
		line_more.setOnClickListener(this);
		tv_fx.setOnClickListener(this);

	}

	private void erweimaHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(YqzcActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(YqzcActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(YqzcActivity.this));
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
				BitmapUtils bitmapUtils = new BitmapUtils(YqzcActivity.this);
				bitmapUtils.display(img_circle, avator);
				bitmapUtils.display(iv_erweima, member_qrcode);
				tv_user.setText(member_name);
				handler.sendEmptyMessage(HANDLER_ERWEIMA_SUCCESS);
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
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		// 分享
		case R.id.tv_fx:
			showShare();
			break;
		case R.id.line_more:
			View view = View.inflate(YqzcActivity.this,
					R.layout.popupwindow_message, null);

			TextView tv_home = (TextView) view.findViewById(R.id.tv_home);
			TextView tv_search = (TextView) view.findViewById(R.id.tv_search);
			TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
			TextView tv_problem = (TextView) view.findViewById(R.id.tv_problem);

			tv_home.setOnClickListener(this);
			tv_search.setOnClickListener(this);
			tv_message.setOnClickListener(this);
			tv_problem.setOnClickListener(this);

			popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, true);
			popupWindow.setTouchable(true);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setFocusable(true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.showAsDropDown(iv_more);
			break;
		// 首页
		case R.id.tv_home:
			popupWindow.dismiss();
			intent = new Intent(YqzcActivity.this, MainActivity1.class);
			SharedUtil.saveStringValue(SharedCommon.DBDH, "ccq");
			intent.putExtra("type", "ccq");
			startActivity(intent);
			finish();
			break;
		// 搜索
		case R.id.tv_search:
			popupWindow.dismiss();
			intent = new Intent(YqzcActivity.this, SearchActivity.class);
			startActivity(intent);
			finish();
			break;
		// 消息
		case R.id.tv_message:
			popupWindow.dismiss();
			intent = new Intent(YqzcActivity.this, MessageActivcity.class);
			startActivity(intent);
			finish();
			break;
		// 问题反馈
		case R.id.tv_problem:
			popupWindow.dismiss();
			intent = new Intent(YqzcActivity.this, ProblemActivity.class);
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
	}

	private void showShare() {
		ShareSDK.initSDK(YqzcActivity.this, "171a7e7c3c736");
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// 信息分享时电话
		// oks.setAddress("");
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("【 餐餐抢 】");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(SharedUtil.getStringValue(
				SharedCommon.MALL_QRCODE_IMG_AZ_URL, ""));
		// text是分享文本，所有平台都需要这个字段
		oks.setText("您的朋友【" + member_name
				+ "】邀请您来餐餐抢消费，天天抢红包，天天抢折扣券哦~~餐餐抢一起与你省钱到底！");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath(member_qrcode);// 确保SDcard下面存在此张图片
		oks.setImageUrl(member_qrcode);
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(SharedUtil.getStringValue(SharedCommon.REG, "")
				+ "?inviter_id=" + new DButil().getMember_id(YqzcActivity.this));
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(SharedUtil.getStringValue(
				SharedCommon.REG + "?inviter_id="
						+ new DButil().getMember_id(YqzcActivity.this), ""));

		// 启动分享GUI
		oks.show(YqzcActivity.this);
	}

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(YqzcActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(YqzcActivity.this,
				R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(YqzcActivity.this, R.style.mDialogStyle);
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
