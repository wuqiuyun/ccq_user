package ccj.yun28.com.ccq;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.MainActivity;
import ccj.yun28.com.MainActivity1;
import ccj.yun28.com.R;
import ccj.yun28.com.RecmmendGoodsActivity;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.mine.VIPhuiyuanActivity;
import ccj.yun28.com.sy.ProductDetailActivity;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 新会员免费领取vip
 * 
 * @author meihuali
 * 
 */
public class NewHuiyuanGetVipActivity extends BaseActivity implements
		OnClickListener {

	private String status;

	private DBHelper myDB;
	private String[] infos;
	// 文案
	private String wadata;
	// 网络错误
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	private static final int HANDLER_NN_FAILURE = 1;
	// 获取信息成功
	private static final int HANDLER_GETINFO_SUCCESS = 2;
	// 获取信息失败
	private static final int HANDLER_TOKEN_FAILURE = 3;
	// token失效
	private static final int HANDLER_GETIONFO_FAILURE = 4;
	// 请求失败，系统出错
	private static final int HANDLER_XITONG_FAILURE = 5;
	private Dialog chucuoDialog;

	private Dialog laohuiyuanDialog;
	private Dialog vipDialog;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				if (NewHuiyuanGetVipActivity.this != null) {
					Toast.makeText(NewHuiyuanGetVipActivity.this,
							"当前网络不可用,请检查网络", Toast.LENGTH_SHORT).show();
				}
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				if (NewHuiyuanGetVipActivity.this != null) {
					Toast.makeText(NewHuiyuanGetVipActivity.this,
							"当前网络出错,请检查网络", Toast.LENGTH_SHORT).show();
				}
				break;
			// 获取信息成功
			case HANDLER_GETINFO_SUCCESS:
				dissDialog();
				if (msg.obj != null) {
					if ("1".endsWith(status)) {
						SQLiteDatabase db = myDB.getWritableDatabase();
						db.execSQL(
								"update user set is_vip=? where member_name=?",
								new Object[] { 1, infos[1] });
						db.close();
						Toast.makeText(NewHuiyuanGetVipActivity.this,
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();

						Intent intent = new Intent(
								NewHuiyuanGetVipActivity.this,
								MainActivity.class);

						SharedUtil.saveStringValue(SharedCommon.DBDH, "me");
						intent.putExtra("type", "me");
						startActivity(intent);
						finish();
					} else if ("2".endsWith(status)) {
						showVIPDialog();
					} else if ("3".endsWith(status)) {
						showLaohuiyuanDialog();
					}
				}
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (NewHuiyuanGetVipActivity.this != null) {
					if (msg.obj != null) {
						Toast.makeText(NewHuiyuanGetVipActivity.this,
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();
					}
				}
				Intent intent = new Intent(NewHuiyuanGetVipActivity.this,
						LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
				break;
			// 获取信息失败
			case HANDLER_GETIONFO_FAILURE:
				dissDialog();
				if (NewHuiyuanGetVipActivity.this != null) {
					if (msg.obj != null) {
						Toast.makeText(NewHuiyuanGetVipActivity.this,
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();
					}
				}
				break;
			// 请求失败，系统出错
			case HANDLER_XITONG_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					showChuCuoDialog(msg.obj.toString().trim());
				}
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newhuiyuangetvip);

		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		TextView tv_lingquvip = (TextView) findViewById(R.id.tv_lingquvip);

		myDB = new DBHelper(this);
		infos = getUserInfo();

		line_back.setOnClickListener(this);
		tv_lingquvip.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.tv_lingquvip:
			showLoading();
			// 新用户赠送一个月vip
			giftVipHttpPost();

			break;
		case R.id.tv_xwl:// 想歪了
			laohuiyuanDialog.dismiss();
			finish();
			break;
		case R.id.tv_wyqxs:
			// 打开VIP充值页.
			laohuiyuanDialog.dismiss();
			intent = new Intent(NewHuiyuanGetVipActivity.this,
					VIPhuiyuanActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_qiangquan:// 先去抢券了
			vipDialog.dismiss();

			intent = new Intent(NewHuiyuanGetVipActivity.this,
					RecmmendGoodsActivity.class);
			startActivity(intent);
			// finish();
			break;
		case R.id.tv_shengjizhongshen://升级终身VIP
			// 打开VIP充值页.
			vipDialog.dismiss();
			intent = new Intent(NewHuiyuanGetVipActivity.this,
					VIPhuiyuanActivity.class);
			startActivity(intent);
			break;
		// 出错dialog
		case R.id.tv_ok:
			chucuoDialog.dismiss();
			break;

		default:
			break;
		}
	}

	// 新用户赠送一个月vip
	private void giftVipHttpPost() {
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils
				.getVersionInfo(NewHuiyuanGetVipActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token",
				new DButil().gettoken(NewHuiyuanGetVipActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(NewHuiyuanGetVipActivity.this));
		httpUtils.send(HttpMethod.POST, JiekouUtils.GIFTVIP, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						giftVipJsonInfo(arg0.result);
					}
				});
	}

	protected void giftVipJsonInfo(String result) {
		try {

			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				// 1 未领取 2vip会员 3 旧会员
				String message = object.getString("message");
				status = object.getString("status");
				wadata = object.getString("data");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETINFO_SUCCESS, message));

			} else if ("700".equals(code)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TOKEN_FAILURE, message));
			} else if ("300".equals(code)) {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_XITONG_FAILURE, message));
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETIONFO_FAILURE, message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 请求失败，系统出错
	private void showChuCuoDialog(String msg) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(NewHuiyuanGetVipActivity.this).inflate(
				R.layout.dialog_red_moban, null);
		TextView dialog_text = (TextView) view.findViewById(R.id.dialog_text);
		TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
		TextView tv_dialog_cancel = (TextView) view
				.findViewById(R.id.tv_dialog_cancel);

		dialog_text.setText(msg);
		tv_dialog_cancel.setVisibility(View.GONE);

		tv_ok.setOnClickListener(this);
		chucuoDialog = new Dialog(NewHuiyuanGetVipActivity.this,
				R.style.mDialogStyle);
		chucuoDialog.setContentView(view);
		chucuoDialog.setCanceledOnTouchOutside(false);
		chucuoDialog.show();
	};

	// 老会员弹窗
	private void showLaohuiyuanDialog() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(NewHuiyuanGetVipActivity.this).inflate(
				R.layout.dialog_laohuiyuan, null);
		TextView tv_xwl = (TextView) view.findViewById(R.id.tv_xwl);
		TextView tv_wyqxs = (TextView) view.findViewById(R.id.tv_wyqxs);
		TextView lhytv_text = (TextView) view.findViewById(R.id.lhytv_text);

		lhytv_text.setText(wadata);
		lhytv_text.setLineSpacing(1, (float) 1.5);

		tv_xwl.setOnClickListener(this);
		tv_wyqxs.setOnClickListener(this);
		laohuiyuanDialog = new Dialog(NewHuiyuanGetVipActivity.this,
				R.style.mDialogStyle);
		laohuiyuanDialog.setContentView(view);
		laohuiyuanDialog.setCanceledOnTouchOutside(false);
		laohuiyuanDialog.show();
	}

	// 已经是VIP
	private void showVIPDialog() {
		View view = LayoutInflater.from(NewHuiyuanGetVipActivity.this).inflate(
				R.layout.dialog_yishivip, null);
		TextView tv_qiangquan = (TextView) view.findViewById(R.id.tv_qiangquan);
		TextView tv_shengjizhongshen = (TextView) view
				.findViewById(R.id.tv_shengjizhongshen);
		TextView tv_viptext = (TextView) view.findViewById(R.id.tv_viptext);

		tv_viptext.setText(wadata);

		tv_qiangquan.setOnClickListener(this);
		tv_shengjizhongshen.setOnClickListener(this);
		vipDialog = new Dialog(NewHuiyuanGetVipActivity.this,
				R.style.mDialogStyle);
		vipDialog.setContentView(view);
		vipDialog.setCanceledOnTouchOutside(false);
		vipDialog.show();
	}

	// 获取用户信息
	private String[] getUserInfo() {
		String info[] = new String[2];
		SQLiteDatabase db = myDB.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from user where status=1", null);
		if (cursor.moveToFirst()) {
			info[0] = cursor
					.getString(cursor.getColumnIndex("member_truename"));// 昵称
			info[1] = cursor.getString(cursor.getColumnIndex("member_name"));// 用户名
		}
		return info;
	}

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(NewHuiyuanGetVipActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				NewHuiyuanGetVipActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(NewHuiyuanGetVipActivity.this,
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
