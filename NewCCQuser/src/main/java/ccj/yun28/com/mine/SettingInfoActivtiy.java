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
import ccj.yun28.com.R;
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
 * 我的-设置(个人资料)(废弃)
 * 
 * @author meihuali
 * 
 */
public class SettingInfoActivtiy extends BaseActivity implements OnClickListener {

	private String path = "";
	private File file;
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
	private WheelView year;
	private WheelView month;
	private WheelView day;
	private int mYear = 2016;
	private int mMonth = 0;
	private int mDay = 1;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取我的信息成功
	private static final int HANDLER_MYINFO_SUCCESS = 3;
	private static final int HANDLER_EDITSEX_SUCCESS = 4;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(SettingInfoActivtiy.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(SettingInfoActivtiy.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(SettingInfoActivtiy.this,
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

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settinginfo);
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
		// 我的二维码
		LinearLayout line_myerweima = (LinearLayout) findViewById(R.id.line_myerweima);
		// 收货地址
		LinearLayout line_shouhuodizhi = (LinearLayout) findViewById(R.id.line_shouhuodizhi);
		rela_choose = (RelativeLayout) findViewById(R.id.rela_choose);
		TextView tv_quxiao = (TextView) findViewById(R.id.tv_quxiao);
		TextView tv_queren = (TextView) findViewById(R.id.tv_queren);
		ll = (LinearLayout) findViewById(R.id.ll);
		
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(SettingInfoActivtiy.this);
		
		showLoading();
		myInfoHttpPost();
		ll.addView(getDataPick());
		line_back.setOnClickListener(this);
		line_touxiang.setOnClickListener(this);
		line_nicheng.setOnClickListener(this);
		line_xingbie.setOnClickListener(this);
		line_chushengriqi.setOnClickListener(this);
		line_myerweima.setOnClickListener(this);
		line_shouhuodizhi.setOnClickListener(this);
		tv_quxiao.setOnClickListener(this);
		tv_queren.setOnClickListener(this);
	}

	// 我的信息接口
	private void myInfoHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(SettingInfoActivtiy.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(SettingInfoActivtiy.this));
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
				String member_name = jsonObject.getString("member_name");
				member_nickname = jsonObject.getString("member_nickname");
				String member_sex = jsonObject.getString("member_sex");
				String member_birthday = jsonObject
						.getString("member_birthday");
				String avator = jsonObject.getString("avator");

				tv_yonghuming.setText(member_name);
				tv_nicheng.setText(member_nickname);
				tv_xingbie.setText(member_sex);
				tv_chushengriqi.setText(member_birthday);

				BitmapUtils bitmapUtils = new BitmapUtils(
						SettingInfoActivtiy.this);
				bitmapUtils.display(iv_touxiang, avator);

				handler.sendEmptyMessage(HANDLER_MYINFO_SUCCESS);
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
			View view = LayoutInflater.from(SettingInfoActivtiy.this).inflate(
					R.layout.dialog_paizhao, null);
			TextView tv_dialog_cancel = (TextView) view
					.findViewById(R.id.tv_dialog_cancel);
			TextView tv_paizhao = (TextView) view.findViewById(R.id.tv_paizhao);
			TextView tv_xiangce = (TextView) view.findViewById(R.id.tv_xiangce);
			tv_dialog_cancel.setOnClickListener(this);
			tv_paizhao.setOnClickListener(this);
			tv_xiangce.setOnClickListener(this);
			touxiangdialog = new Dialog(SettingInfoActivtiy.this,
					R.style.mDialogStyle);
			touxiangdialog.setContentView(view);
			touxiangdialog.setCanceledOnTouchOutside(false);
			touxiangdialog.show();
			break;
		case R.id.line_nicheng:
			intent = new Intent(SettingInfoActivtiy.this, NichengActivity.class);
			intent.putExtra("nicheng", member_nickname);
			startActivityForResult(intent, 100);
			break;
		case R.id.line_xingbie:
			View sexview = LayoutInflater.from(SettingInfoActivtiy.this)
					.inflate(R.layout.dialog_xingbie, null);
			TextView tv_nv = (TextView) sexview.findViewById(R.id.tv_nv);
			TextView tv_nan = (TextView) sexview.findViewById(R.id.tv_nan);
			TextView tv_xbdialog_cancel = (TextView) sexview
					.findViewById(R.id.tv_xbdialog_cancel);

			tv_nv.setOnClickListener(this);
			tv_nan.setOnClickListener(this);
			tv_xbdialog_cancel.setOnClickListener(this);

			xingbiedialog = new Dialog(SettingInfoActivtiy.this,
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
			intent = new Intent(SettingInfoActivtiy.this, YqzcActivity.class);
			startActivity(intent);
			break;
		case R.id.line_shouhuodizhi:
			intent = new Intent(SettingInfoActivtiy.this,
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
		case R.id.tv_paizhao:
			touxiangdialog.dismiss();
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				File file = createFile();
				if (file == null) {
					Toast.makeText(SettingInfoActivtiy.this, "未读取到储存卡",
							Toast.LENGTH_LONG).show();
				} else {
					Log.e("ee", "file:" + file);
					path = file.getAbsolutePath();

					Log.e("ee", "p:" + path);
					Toast.makeText(SettingInfoActivtiy.this, "请横向拍照！",
							Toast.LENGTH_LONG).show();
					// Intent intent = new Intent(
					// "android.media.action.IMAGE_CAPTURE");
					// intent.putExtra(MediaStore.EXTRA_OUTPUT,
					// Uri.fromFile(file));
					// startActivityForResult(intent, 2);
					intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// 指定一个图片路径对应的file对象
					// 启动activity
					startActivityForResult(intent, 2);
				}
			}
			break;
		case R.id.tv_xiangce:
			touxiangdialog.dismiss();
			intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
			startActivityForResult(intent, 1);
			break;
		case R.id.tv_dialog_cancel:
			touxiangdialog.dismiss();
			break;
		default:
			break;
		}
	}

	// 修改生日接口
	private void birthdayHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(SettingInfoActivtiy.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(SettingInfoActivtiy.this));
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
		params.addBodyParameter("token",
				new DButil().gettoken(SettingInfoActivtiy.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(SettingInfoActivtiy.this));
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
				new DButil().getMember_id(SettingInfoActivtiy.this));
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
		View view = LayoutInflater.from(SettingInfoActivtiy.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				SettingInfoActivtiy.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(SettingInfoActivtiy.this,
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
		View view = LayoutInflater.from(SettingInfoActivtiy.this).inflate(
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
		initDay(curYear,curMonth);
		day.setLabel("日");
		day.setCyclic(true);
		
		year.setCurrentItem(curYear - 1950);
		month.setCurrentItem(curMonth - 1);
		day.setCurrentItem(curDate - 1);

//		year = (WheelView) view.findViewById(R.id.year);
//		NumericWheelAdapter numericWheelAdapter1 = new NumericWheelAdapter(
//				this, 1950, norYear);
//		numericWheelAdapter1.setLabel("年");
//		year.setViewAdapter(numericWheelAdapter1);
//		year.setCyclic(false);// 是否可循环滑动
//		year.addScrollingListener(scrollListener);
//
//		month = (WheelView) view.findViewById(R.id.month);
//		NumericWheelAdapter numericWheelAdapter2 = new NumericWheelAdapter(
//				this, 1, 12, "%02d");
//		numericWheelAdapter2.setLabel("月");
//		month.setViewAdapter(numericWheelAdapter2);
//		month.setCyclic(false);
//		month.addScrollingListener(scrollListener);
//
//		day = (WheelView) view.findViewById(R.id.day);
//		initDay(curYear, curMonth);
//		day.setCyclic(false);
//
//		year.setVisibleItems(6);// 设置显示行数
//		month.setVisibleItems(6);
//		day.setVisibleItems(6);
//
//		year.setCurrentItem(curYear - 1950);
//		month.setCurrentItem(curMonth - 1);
//		day.setCurrentItem(curDate - 1);

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

			birthday = new StringBuilder().append((year.getCurrentItem()+1950)).append("-")
					.append((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1) : (month.getCurrentItem() + 1)).append("-").
					append(((day.getCurrentItem()+1) < 10) ? "0" + (day.getCurrentItem()+1) : (day.getCurrentItem()+1)).toString();
			
		}
	};
	private LinearLayout ll;
	private RelativeLayout rela_choose;
	private Dialog touxiangdialog;
	private String sex_id;
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
	
//	private void initDay(int arg1, int arg2) {
//		NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(this,
//				1, getDay(arg1, arg2), "%02d");
//		numericWheelAdapter.setLabel("日");
//		day.setViewAdapter(numericWheelAdapter);
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/** 创建文件 **/
	private File createFile() {
		try {
			File file = new File(getFolderPath());
			if (!file.exists()) {
				file.mkdirs();
			}
			File f = new File(file, getPhotoFileName());
			if (!file.exists()) {
				f.createNewFile();
			}
			return f;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}

	}

	private String getFolderPath() {
		return Environment.getExternalStorageDirectory() + "/" + "ccq";
	}

	/** 获取图片名 **/
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss", Locale.getDefault());
		return dateFormat.format(date) + ".jpg";
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (data != null) {
			if (resultCode == 101) {
				String nicheng = data.getStringExtra("nicheng");
				tv_nicheng.setText(nicheng);
			}
			// 相册返回处理
			if (requestCode == 1) {

				if (resultCode == RESULT_OK) {
					Uri uri = data.getData();
					if (uri == null) {
						Log.e("ee", "data:" + data.getExtras().get("data"));
						Toast.makeText(SettingInfoActivtiy.this,
								"从相册获取图片失败！请使用相机拍照获取！", 1).show();
					}
					Log.e("ee", "url:" + uri);
					try {
						String img_path;
						String[] proj = { MediaStore.Images.Media.DATA };
						Cursor actualimagecursor = getContentResolver().query(
								uri, proj, null, null, null);
						int actual_image_column_index = actualimagecursor
								.getColumnIndexOrThrow(MediaColumns.DATA);
						actualimagecursor.moveToFirst();
						img_path = actualimagecursor
								.getString(actual_image_column_index);
						file = new File(img_path);
						path = file.getAbsolutePath();
						Bitmap bitmap = revitionImageSize(path);
						bitmap = getBitmap(path, bitmap);
						String pathString = createFileName();
						path = Environment.getExternalStorageDirectory() + "/"
								+ "ccq" + "/" + pathString + ".jpg";
						savePhotoToSDCard(bitmap,
								Environment.getExternalStorageDirectory() + "/"
										+ "ccq/", pathString);
						Bitmap bm = BitmapFactory.decodeFile(path);
						// 将图片显示到ImageView中
						// img.setImageBitmap(bm);
						iv_touxiang.setScaleType(ScaleType.CENTER_CROP);
						iv_touxiang.setImageBitmap(bm);
						showLoading();
						touxiangpicHttpPost();

					} catch (Exception e) {
						// TODO: handle exception

					}
				}

			}

			// 拍照返回处理
			if (requestCode == 2) {
				Log.e("ee", "result:" + resultCode);
				if (resultCode == RESULT_OK) {
					if (data != null) {
						// 返回有缩略图
						if (data.hasExtra("data")) {
							// Bitmap bitmap = data.getParcelableExtra("data");
							Bitmap bitmap = (Bitmap) data.getExtras().get(
									"data");
							// 得到bitmap后的操作
							if (bitmap != null) {
								// Bitmap bitmap=util.compressImage(bitmap1);
								// pic.add(bitmap);
								String pathString = createFileName();

								path = Environment
										.getExternalStorageDirectory()
										+ "/"
										+ "ccq" + "/" + pathString + ".jpg";
								file = new File(path);
								savePhotoToSDCard(
										bitmap,
										Environment
												.getExternalStorageDirectory()
												+ "/" + "ccq/", pathString);
								// bitmap = BitmapFactory.decodeFile(path);
								iv_touxiang.setScaleType(ScaleType.CENTER_CROP);
								iv_touxiang.setImageBitmap(bitmap);
								showLoading();
								touxiangpicHttpPost();
							}
						}
					}
				}
				if (resultCode == 0) {
					path = "";
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/** 获取文件名称 **/
	private String createFileName() {

		String fileName = "";

		Date date = new Date(System.currentTimeMillis()); // 系统当前时间

		SimpleDateFormat dateFormat = new SimpleDateFormat(

		"'IMG'_yyyyMMdd_HHmmss", Locale.getDefault());

		fileName = dateFormat.format(date);

		return fileName;

	}

	/** 图片旋转度数 */
	private int getExifOrientation(String filepath) {
		int degree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filepath);
		} catch (IOException ex) {
			Log.e("test", "cannot read exif", ex);
		}
		if (exif != null) {
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, -1);
			if (orientation != -1) {
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				}
			}
		}
		return degree;
	}

	/** 图片旋转 */
	private Bitmap getBitmap(String path, Bitmap bmp) {
		int angle = getExifOrientation(path);

		Log.e("ee", "angle:" + angle);
		if (angle != 0) { // 如果照片出现了 旋转 那么 就更改旋转度数
			Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
					bmp.getHeight(), matrix, true);
		}
		return bmp;
	}

	/** 压缩图片 */
	public static Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 1000)
					&& (options.outHeight >> i <= 1000)) {
				in = new BufferedInputStream(
						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(3.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}

	private boolean checkSDCardAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * Save image to the SD card
	 * 
	 * @param photoBitmap
	 * @param photoName
	 * @param path
	 */
	private void savePhotoToSDCard(Bitmap photoBitmap, String path,
			String photoName) {
		if (checkSDCardAvailable()) {
			Log.e("checkSDCardAvailable", "sdcard");
			File dir = new File(path);
			if (!dir.exists()) {
				Log.e("dir", "Dir");
				dir.mkdirs();
			}

			File photoFile = new File(path, photoName + ".jpg");
			Log.e("photoFile", photoFile + "");
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
							fileOutputStream)) {
						fileOutputStream.flush();
						// fileOutputStream.close();
					}
				}
			} catch (FileNotFoundException e) {
				photoFile.delete();
				e.printStackTrace();
			} catch (IOException e) {
				photoFile.delete();
				e.printStackTrace();
			} finally {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
