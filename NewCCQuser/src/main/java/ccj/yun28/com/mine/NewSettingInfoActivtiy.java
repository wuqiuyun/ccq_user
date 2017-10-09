package ccj.yun28.com.mine;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.R.color;
import ccj.yun28.com.ShuRuNewPhone_yzmActivity;
import ccj.yun28.com.YuanPhone_yzmActivity;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.sy.YqzcActivity;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;

import com.byl.datepicker.wheelview.NoDayOnWheelScrollListener;
import com.byl.datepicker.wheelview.NumericWheelAdapter;
import com.byl.datepicker.wheelview.WheelView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 我的-设置(个人资料)
 * 
 * @author meihuali
 * 
 */
public class NewSettingInfoActivtiy extends BaseActivity implements OnClickListener {

	private String type;
	private String zmpath = "";
	private String fmpath = "";
	private File file;
	private File fmfile;
	// 头像
	private ImageView iv_touxiang;
	// 昵称
	private TextView tv_nicheng;
	// 用户名
	private TextView tv_yonghuming;
	// 性别
	private TextView tv_xingbie;
	// 出生日期
	private TextView tv_chushengriqi;
	// 身份证号码
	private TextView tv_sfz_num;
	private ImageView iv_sfzone;
	private ImageView iv_sfztwo;
	// 关联联系
	private TextView tv_bangdinglianxi;
	// 实名认证
	private TextView tv_shimingrenz;
	private LinearLayout line_again;
	// 关联联系
	private String member_mobile;

	private WheelView year;
	private WheelView month;
	private WheelView day;
	private int mYear = 2016;
	private int mMonth = 0;
	private int mDay = 1;

	private LinearLayout ll;
	private RelativeLayout rela_choose;
	private Dialog touxiangdialog;
	private String sex_id;
	// 实名认证状态 0:未认证 1:已认证 2:审核中 3:认证不通过
	private String real_verify;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取我的信息成功
	private static final int HANDLER_MYINFO_SUCCESS = 3;
	private static final int HANDLER_EDITSEX_SUCCESS = 4;
	private static final int HANDLER_TOKEN_FAILURE = 5;
	private static final int GO_XIANGCE = 100;
	private static final int GO_PAIZHAO = 200;
	private static final int GO_NICHENG = 300;
	private static final int NEW_BANDINGPHONE = 400;
	private static final int GO_TOUXIANG = 500;
	private static final int SHIMINGRENZHENG = 600;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(NewSettingInfoActivtiy.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(NewSettingInfoActivtiy.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(NewSettingInfoActivtiy.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取我的信息成功
			case HANDLER_MYINFO_SUCCESS:
				dissDialog();
				break;
			// 获取我的信息成功
			case HANDLER_EDITSEX_SUCCESS:
				myInfoHttpPost();
				break;
			// token失效
			case HANDLER_TOKEN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(NewSettingInfoActivtiy.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent intent = new Intent(NewSettingInfoActivtiy.this,
						LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
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
		setContentView(R.layout.activity_newsettinginfo);
		// 返回
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 头像
		LinearLayout line_touxiang = (LinearLayout) findViewById(R.id.line_touxiang);
		iv_touxiang = (ImageView) findViewById(R.id.iv_touxiang);
		// 用户名
		tv_yonghuming = (TextView) findViewById(R.id.tv_yonghuming);
		// 昵称
		LinearLayout line_nicheng = (LinearLayout) findViewById(R.id.line_nicheng);
		tv_nicheng = (TextView) findViewById(R.id.tv_nicheng);
		// 性别
		LinearLayout line_xingbie = (LinearLayout) findViewById(R.id.line_xingbie);
		tv_xingbie = (TextView) findViewById(R.id.tv_xingbie);
		// 出生日期
		LinearLayout line_chushengriqi = (LinearLayout) findViewById(R.id.line_chushengriqi);
		tv_chushengriqi = (TextView) findViewById(R.id.tv_chushengriqi);
		// 身份证号码
		tv_sfz_num = (TextView) findViewById(R.id.tv_sfz_num);
		iv_sfzone = (ImageView) findViewById(R.id.iv_sfzone);
		iv_sfztwo = (ImageView) findViewById(R.id.iv_sfztwo);
		// 关联联系
		LinearLayout line_bangdinglianxi = (LinearLayout) findViewById(R.id.line_bangdinglianxi);
		tv_bangdinglianxi = (TextView) findViewById(R.id.tv_bangdinglianxi);
		// 实名认证
		LinearLayout line_shimingrenz = (LinearLayout) findViewById(R.id.line_shimingrenz);
		line_again = (LinearLayout) findViewById(R.id.line_again);
		tv_shimingrenz = (TextView) findViewById(R.id.tv_shimingrenz);
		rela_choose = (RelativeLayout) findViewById(R.id.rela_choose);
		TextView tv_quxiao = (TextView) findViewById(R.id.tv_quxiao);
		TextView tv_queren = (TextView) findViewById(R.id.tv_queren);
		ll = (LinearLayout) findViewById(R.id.ll);
		
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(NewSettingInfoActivtiy.this);
		
		showLoading();
		myInfoHttpPost();
		ll.addView(getDataPick());
		line_back.setOnClickListener(this);
		line_touxiang.setOnClickListener(this);
		line_nicheng.setOnClickListener(this);
		line_xingbie.setOnClickListener(this);
		line_chushengriqi.setOnClickListener(this);
		line_again.setOnClickListener(this);
		iv_sfzone.setOnClickListener(this);
		iv_sfztwo.setOnClickListener(this);
		line_bangdinglianxi.setOnClickListener(this);
		line_shimingrenz.setOnClickListener(this);
		tv_quxiao.setOnClickListener(this);
		tv_queren.setOnClickListener(this);
	}

	// 我的信息接口
	private void myInfoHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token",
				new DButil().gettoken(NewSettingInfoActivtiy.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(NewSettingInfoActivtiy.this));
		httpUtils.send(HttpMethod.POST, JiekouUtils.MYINFO, params,
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

	protected void myInfoListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONObject jsonObject = object.getJSONObject("data");
				String true_name = jsonObject.getString("true_name");
				member_nickname = jsonObject.getString("member_nickname");
				String member_sex = jsonObject.getString("member_sex");
				String member_birthday = jsonObject
						.getString("member_birthday");
				String avator = jsonObject.getString("avator");
				member_mobile = jsonObject.getString("member_mobile");
				// 实名认证状态 0:未认证 1:已认证 2:审核中 3:认证不通过
				real_verify = jsonObject.getString("real_verify");
				String number = jsonObject.getString("number");
				String identity_card_img1 = jsonObject
						.getString("identity_card_img1");
				String identity_card_img2 = jsonObject
						.getString("identity_card_img2");

				tv_yonghuming.setText(true_name);
				tv_shimingrenz.setText(true_name);
				tv_nicheng.setText(member_nickname);
				tv_xingbie.setText(member_sex);
				tv_chushengriqi.setText(member_birthday);
				tv_sfz_num.setText(number);

				if ("1".equals(real_verify) || "3".equals(real_verify)) {
					line_again.setVisibility(View.VISIBLE);
				} else {
					line_again.setVisibility(View.GONE);
				}

				if ("未关联".equals(member_mobile)) {
					tv_bangdinglianxi.setTextColor(Color.RED);
					tv_bangdinglianxi.setText(member_mobile);
				} else {
					tv_bangdinglianxi.setText(member_mobile);

				}

				BitmapUtils bitmapUtils = new BitmapUtils(
						NewSettingInfoActivtiy.this);
				bitmapUtils.display(iv_touxiang, avator);
				bitmapUtils.display(iv_sfzone, identity_card_img1);
				bitmapUtils.display(iv_sfztwo, identity_card_img2);

				handler.sendEmptyMessage(HANDLER_MYINFO_SUCCESS);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_touxiang:
			intent = new Intent(NewSettingInfoActivtiy.this,
					TouXiangActivity.class);
			intent.putExtra("nicheng", member_nickname);
			startActivityForResult(intent, GO_TOUXIANG);
			break;
		case R.id.line_nicheng:
			intent = new Intent(NewSettingInfoActivtiy.this,
					NichengActivity.class);
			intent.putExtra("nicheng", member_nickname);
			startActivityForResult(intent, GO_NICHENG);
			break;
		case R.id.line_xingbie:
			View sexview = LayoutInflater.from(NewSettingInfoActivtiy.this)
					.inflate(R.layout.dialog_xingbie, null);
			TextView tv_nv = (TextView) sexview.findViewById(R.id.tv_nv);
			TextView tv_nan = (TextView) sexview.findViewById(R.id.tv_nan);
			TextView tv_xbdialog_cancel = (TextView) sexview
					.findViewById(R.id.tv_xbdialog_cancel);

			tv_nv.setOnClickListener(this);
			tv_nan.setOnClickListener(this);
			tv_xbdialog_cancel.setOnClickListener(this);

			xingbiedialog = new Dialog(NewSettingInfoActivtiy.this,
					R.style.mDialogStyle);
			xingbiedialog.setContentView(sexview);
			xingbiedialog.setCanceledOnTouchOutside(false);
			xingbiedialog.show();
			break;
		case R.id.line_chushengriqi:
			rela_choose.setVisibility(View.VISIBLE);
			ll.setVisibility(View.VISIBLE);
			break;
		case R.id.line_myerweima:
			intent = new Intent(NewSettingInfoActivtiy.this, YqzcActivity.class);
			startActivity(intent);
			break;
		case R.id.line_shouhuodizhi:
			intent = new Intent(NewSettingInfoActivtiy.this,
					MyAddressActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_nv:
			sex = "女";
			sex_id = "2";
			editsexHttpPost(sex_id);
			break;
		case R.id.tv_nan:
			sex = "男";
			sex_id = "1";
			editsexHttpPost(sex_id);
			break;
		case R.id.tv_xbdialog_cancel:
			sex = "保密";
			sex_id = "3";
			editsexHttpPost(sex_id);
			break;
		case R.id.line_bangdinglianxi:
			if ("未关联".equals(member_mobile)) {
				intent = new Intent(NewSettingInfoActivtiy.this,
						ShuRuNewPhone_yzmActivity.class);
				startActivityForResult(intent, NEW_BANDINGPHONE);
			} else {
				intent = new Intent(NewSettingInfoActivtiy.this,
						YuanPhone_yzmActivity.class);
				intent.putExtra("yuan_phone", tv_bangdinglianxi.getText()
						.toString().trim());
				startActivityForResult(intent, NEW_BANDINGPHONE);

			}
			break;
		case R.id.line_shimingrenz:
			// 实名认证状态 0:未认证 1:已认证 2:审核中 3:认证不通过
			if ("0".equals(real_verify)) {
				intent = new Intent(NewSettingInfoActivtiy.this,
						NewWeiShiMingRenZhengActivity.class);
				intent.putExtra("title", "实名认证");
				startActivityForResult(intent, SHIMINGRENZHENG);
			}
			break;
		case R.id.line_again:
			intent = new Intent(NewSettingInfoActivtiy.this,
					NewWeiShiMingRenZhengActivity.class);
			intent.putExtra("title", "重新实名认证");
			startActivityForResult(intent, SHIMINGRENZHENG);
			break;
		case R.id.tv_queren:
			birthdayHttpPost();
			tv_chushengriqi.setText(birthday);
			rela_choose.setVisibility(View.GONE);
			ll.setVisibility(View.GONE);
			break;
		case R.id.tv_quxiao:
			rela_choose.setVisibility(View.GONE);
			ll.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}

	// 选择拍照还是相册dialog
	private void showChoosePicDialog() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(NewSettingInfoActivtiy.this).inflate(
				R.layout.dialog_paizhao, null);
		TextView tv_dialog_cancel = (TextView) view
				.findViewById(R.id.tv_dialog_cancel);
		TextView tv_paizhao = (TextView) view.findViewById(R.id.tv_paizhao);
		TextView tv_xiangce = (TextView) view.findViewById(R.id.tv_xiangce);
		tv_dialog_cancel.setOnClickListener(this);
		tv_paizhao.setOnClickListener(this);
		tv_xiangce.setOnClickListener(this);
		touxiangdialog = new Dialog(NewSettingInfoActivtiy.this,
				R.style.mDialogStyle);
		touxiangdialog.setContentView(view);
		touxiangdialog.setCanceledOnTouchOutside(false);
		touxiangdialog.show();
	}

	// 修改生日接口
	private void birthdayHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token",
				new DButil().gettoken(NewSettingInfoActivtiy.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(NewSettingInfoActivtiy.this));
		params.addBodyParameter("member_birthday", birthday);
		httpUtils.send(HttpMethod.POST, JiekouUtils.EDITMYINFO, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						editsexListInfo(arg0.result);
					}
				});
	}

	// 修改性别接口
	private void editsexHttpPost(String sex2) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("api_v", "v3");
		params.addBodyParameter("token",
				new DButil().gettoken(NewSettingInfoActivtiy.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(NewSettingInfoActivtiy.this));
		params.addBodyParameter("member_sex", sex2);
		httpUtils.send(HttpMethod.POST, JiekouUtils.EDITMYINFO, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						editsexListInfo(arg0.result);
					}
				});
	}

	protected void editsexListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				handler.sendEmptyMessage(HANDLER_EDITSEX_SUCCESS);
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

	// 提交头像图片
	private void touxiangpicHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("member_id",
				new DButil().getMember_id(NewSettingInfoActivtiy.this));
		// upfile 根据接口的不同，这个值也不同，我的理解是；服务器规定，这个key对应的值，就是文件形式的
		// params.addBodyParameter("upfile", file,mime);
		params.addBodyParameter("member_avator", file);
		String u = SharedUtil.getStringValue(SharedCommon.IMG_DOMAIN, "");
		httpUtils.send(HttpMethod.POST, u + JiekouUtils.TOUXIANGPIC, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						touxiangpicJsonInfo(arg0.result);
					}
				});
	}

	protected void touxiangpicJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				handler.sendEmptyMessage(HANDLER_EDITSEX_SUCCESS);
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

	// 加载中动画
	private Dialog loadingDialog;
	private Dialog xingbiedialog;
	private String member_nickname;
	private String sex;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(NewSettingInfoActivtiy.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				NewSettingInfoActivtiy.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(NewSettingInfoActivtiy.this,
				R.style.mDialogStyle);
		loadingDialog.setContentView(view);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.show();
	}

	// 关闭加载dialog
	private void dissDialog() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		} else if (xingbiedialog != null && xingbiedialog.isShowing()) {
			xingbiedialog.dismiss();
		}
	}

	private View getDataPick() {
		Calendar c = Calendar.getInstance();
		int norYear = c.get(Calendar.YEAR);
		// int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
		// int curDate = c.get(Calendar.DATE);

		int curYear = mYear;
		int curMonth = mMonth + 1;
		int curDate = mDay;
		View view = LayoutInflater.from(NewSettingInfoActivtiy.this).inflate(
				R.layout.wheel_date_picker, null);

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

		day = (WheelView) view.findViewById(R.id.day);
		initDay(curYear, curMonth);
		day.setLabel("日");
		day.setCyclic(true);

		year.setCurrentItem(curYear - 1950);
		month.setCurrentItem(curMonth - 1);
		day.setCurrentItem(curDate - 1);

		// year = (WheelView) view.findViewById(R.id.year);
		// NumericWheelAdapter numericWheelAdapter1 = new NumericWheelAdapter(
		// this, 1950, norYear);
		// numericWheelAdapter1.setLabel("年");
		// year.setViewAdapter(numericWheelAdapter1);
		// year.setCyclic(false);// 是否可循环滑动
		// year.addScrollingListener(scrollListener);
		//
		// month = (WheelView) view.findViewById(R.id.month);
		// NumericWheelAdapter numericWheelAdapter2 = new NumericWheelAdapter(
		// this, 1, 12, "%02d");
		// numericWheelAdapter2.setLabel("月");
		// month.setViewAdapter(numericWheelAdapter2);
		// month.setCyclic(false);
		// month.addScrollingListener(scrollListener);
		//
		// day = (WheelView) view.findViewById(R.id.day);
		// initDay(curYear, curMonth);
		// day.setCyclic(false);
		//
		// year.setVisibleItems(6);// 设置显示行数
		// month.setVisibleItems(6);
		// day.setVisibleItems(6);
		//
		// year.setCurrentItem(curYear - 1950);
		// month.setCurrentItem(curMonth - 1);
		// day.setCurrentItem(curDate - 1);

		// Button bt = (Button) view.findViewById(R.id.set);
		// bt.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // birthday = (year.getCurrentItem()+1950) + "-"+
		// (month.getCurrentItem()+1)+"-"+(day.getCurrentItem()+1);
		// String birthday=new
		// StringBuilder().append((year.getCurrentItem()+1950)).append("-").append((month.getCurrentItem()
		// + 1) < 10 ? "0" + (month.getCurrentItem() + 1) :
		// (month.getCurrentItem() +
		// 1)).append("-").append(((day.getCurrentItem()+1) < 10) ? "0" +
		// (day.getCurrentItem()+1) : (day.getCurrentItem()+1)).toString();
		// Toast.makeText(getApplicationContext(), birthday, 1).show();
		// }
		// });
		// Button cancel = (Button) view.findViewById(R.id.cancel);
		// cancel.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if(null!=menuWindow){
		// menuWindow.dismiss();
		// }
		// }
		// });
		return view;
	}

	private String birthday;
	NoDayOnWheelScrollListener scrollListener = new NoDayOnWheelScrollListener() {

		@Override
		public void onScrollingStarted(WheelView wheel) {

		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			int n_year = year.getCurrentItem() + 1950;// 年
			int n_month = month.getCurrentItem() + 1;// 月

			initDay(n_year, n_month);

			birthday = new StringBuilder()
					.append((year.getCurrentItem() + 1950))
					.append("-")
					.append((month.getCurrentItem() + 1) < 10 ? "0"
							+ (month.getCurrentItem() + 1) : (month
							.getCurrentItem() + 1))
					.append("-")
					.append(((day.getCurrentItem() + 1) < 10) ? "0"
							+ (day.getCurrentItem() + 1) : (day
							.getCurrentItem() + 1)).toString();

		}
	};
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

	/**
	 */
	private void initDay(int arg1, int arg2) {
		day.setAdapter(new NumericWheelAdapter(1, getDay(arg1, arg2), "%02d"));
	}

	// private void initDay(int arg1, int arg2) {
	// NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(this,
	// 1, getDay(arg1, arg2), "%02d");
	// numericWheelAdapter.setLabel("日");
	// day.setViewAdapter(numericWheelAdapter);
	// }


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (data != null) {
			
			if (requestCode == GO_TOUXIANG) {
				showLoading();
				myInfoHttpPost();
			}
			if (resultCode == 301) {
				// String nicheng = data.getStringExtra("nicheng");
				// tv_nicheng.setText(nicheng);
				showLoading();
				myInfoHttpPost();
			}
			if (resultCode == 401) {
				showLoading();
				myInfoHttpPost();
			}
			if (requestCode == SHIMINGRENZHENG) {
				showLoading();
				myInfoHttpPost();
			}
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
