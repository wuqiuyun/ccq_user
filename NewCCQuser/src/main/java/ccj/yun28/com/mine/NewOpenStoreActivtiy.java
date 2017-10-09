package ccj.yun28.com.mine;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.IDUtils;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 新我要开店--商家入驻
 * 
 * @author meihuali
 * 
 */
public class NewOpenStoreActivtiy extends BaseActivity implements OnClickListener {

	// 店铺名称
	private EditText merchant_storename_et;
	// 所属行业
	private TextView tv_sshy;
	// 当前位置
	private TextView merchant_address_tb;
	private TextView merchant_address_tv;
	private EditText merchant_address_et;
	// 商家法人
	private EditText merchant_fr_name_et;
	// 身份证
	private EditText merchant_fr_id_et;
	// 联系电话
	private EditText merchant_fr_mobile_connect_et;
	// 商家服务电话
	private EditText merchant_fr_mobile_service_et;
	// 二级行业id
	private String erjiid;
	// 上传正面
	private ImageView img_id_front_iv;
	// 上传反面
	private ImageView img_id_back_iv;
	// 营业执照
	private ImageView img_license_iv;
	// 上传商家相片
	private ImageView img_merchant_first_iv;
	private ImageView img_merchant_second_iv;
	private ImageView img_merchant_third_iv;
	// 上传厨房相片
	private ImageView img_kitchen_first_iv;
	private ImageView img_kitchen_second_iv;
	private ImageView img_kitchen_third_iv;

	// 上传图片类型(1商品图片 2套餐图片 3添加商家基本图片 4添加商家商家相片 5添加商家厨房相片)
	private int imageUploadType = 1;
	// 上传图片具体哪一张
	private String detailpictype = "";
	// 省市区街道
	private String shengstr = "0";
	private String shistr = "0";
	private String qustr = "0";
	private String jiedaostr = "0";
	// 经 度
	private String longitude = "";
	// 纬 度
	private String latitude = "";
	// 定位
	private AMapLocationClient locationClient = null;
	private AMapLocationClientOption locationOption = new AMapLocationClientOption();

	private String path = "";
	private File file;

	protected static final int HANDLER_NET_FAILURE = 0;
	private static final int HANDLER_TIJIAOFANKUI_SUCCESS = 1;
	private static final int HANDLER_NN_FAILURE = 2;
	private static final int HANDLER_GETINFO_FAILURE = 3;
	protected static final int HANDLER_DINGWEI_FINISH = 4;
	private static final int HANDLER_PIC_SUCCESS = 5;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(NewOpenStoreActivtiy.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_TIJIAOFANKUI_SUCCESS:
				dissDialog();
				Toast.makeText(NewOpenStoreActivtiy.this, "提交成功",
						Toast.LENGTH_SHORT).show();
				finish();
				break;
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(NewOpenStoreActivtiy.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(NewOpenStoreActivtiy.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			case HANDLER_DINGWEI_FINISH:
				dissDialog();
				stopLocation();
				break;
			case HANDLER_PIC_SUCCESS:
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
		setContentView(R.layout.activity_newopenstore);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		// 店铺名称
		merchant_storename_et = (EditText) findViewById(R.id.merchant_storename_et);
		// 所属行业
		LinearLayout line_sshy = (LinearLayout) findViewById(R.id.line_sshy);
		tv_sshy = (TextView) findViewById(R.id.tv_sshy);
		// 当前位置
		merchant_address_tb = (TextView) findViewById(R.id.merchant_address_tb);
		merchant_address_tv = (TextView) findViewById(R.id.merchant_address_tv);
		merchant_address_et = (EditText) findViewById(R.id.merchant_address_et);
		// 商家法人
		merchant_fr_name_et = (EditText) findViewById(R.id.merchant_fr_name_et);
		// 身份证
		merchant_fr_id_et = (EditText) findViewById(R.id.merchant_fr_id_et);
		// 联系电话
		merchant_fr_mobile_connect_et = (EditText) findViewById(R.id.merchant_fr_mobile_connect_et);
		// 商家服务电话
		merchant_fr_mobile_service_et = (EditText) findViewById(R.id.merchant_fr_mobile_service_et);
		// 上传正面
		img_id_front_iv = (ImageView) findViewById(R.id.img_id_front_iv);
		Button img_id_front_btn = (Button) findViewById(R.id.img_id_front_btn);
		// 上传反面
		img_id_back_iv = (ImageView) findViewById(R.id.img_id_back_iv);
		Button img_id_back_btn = (Button) findViewById(R.id.img_id_back_btn);
		// 营业执照
		img_license_iv = (ImageView) findViewById(R.id.img_license_iv);
		Button img_license_btn = (Button) findViewById(R.id.img_license_btn);
		// 上传商家相片
		img_merchant_first_iv = (ImageView) findViewById(R.id.img_merchant_first_iv);
		Button img_merchant_first_btn = (Button) findViewById(R.id.img_merchant_first_btn);
		img_merchant_second_iv = (ImageView) findViewById(R.id.img_merchant_second_iv);
		Button img_merchant_second_btn = (Button) findViewById(R.id.img_merchant_second_btn);
		img_merchant_third_iv = (ImageView) findViewById(R.id.img_merchant_third_iv);
		Button img_merchant_third_btn = (Button) findViewById(R.id.img_merchant_third_btn);
		// 上传厨房相片
		img_kitchen_first_iv = (ImageView) findViewById(R.id.img_kitchen_first_iv);
		Button img_kitchen_first_btn = (Button) findViewById(R.id.img_kitchen_first_btn);
		img_kitchen_second_iv = (ImageView) findViewById(R.id.img_kitchen_second_iv);
		Button img_kitchen_second_btn = (Button) findViewById(R.id.img_kitchen_second_btn);
		img_kitchen_third_iv = (ImageView) findViewById(R.id.img_kitchen_third_iv);
		Button img_kitchen_third_btn = (Button) findViewById(R.id.img_kitchen_third_btn);
		// 提交
		TextView submit_audit_btn = (TextView) findViewById(R.id.submit_audit_btn);
		
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(NewOpenStoreActivtiy.this);

		map1 = new HashMap<String, String>();
		map2 = new HashMap<String, String>();
		map3 = new HashMap<String, String>();
		map3.put("id1", "");
		map3.put("id2", "");
		map3.put("id3", "");

		line_back.setOnClickListener(this);
		line_sshy.setOnClickListener(this);
		merchant_address_tb.setOnClickListener(this);
		img_id_front_btn.setOnClickListener(this);
		img_id_back_btn.setOnClickListener(this);
		img_license_btn.setOnClickListener(this);
		img_merchant_first_btn.setOnClickListener(this);
		img_merchant_second_btn.setOnClickListener(this);
		img_merchant_third_btn.setOnClickListener(this);
		img_kitchen_first_btn.setOnClickListener(this);
		img_kitchen_second_btn.setOnClickListener(this);
		img_kitchen_third_btn.setOnClickListener(this);
		submit_audit_btn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_sshy:
			Intent intent = new Intent(NewOpenStoreActivtiy.this,
					SuoShuHangYeActivity.class);
			startActivityForResult(intent, 100);
			break;
		case R.id.merchant_address_tb:
			showLoading();
			// 初始化定位
			initLocation();
			break;
		case R.id.img_id_front_btn:
			imageUploadType = 3;
			detailpictype = "sfzzm";
			paizhaoDialog();

			break;
		case R.id.img_id_back_btn:
			imageUploadType = 3;
			detailpictype = "sfzfm";
			paizhaoDialog();
			break;
		case R.id.img_license_btn:
			imageUploadType = 3;
			detailpictype = "yyzz";
			paizhaoDialog();
			break;
		case R.id.img_merchant_first_btn:
			imageUploadType = 4;
			detailpictype = "sjtpf";
			paizhaoDialog();
			break;
		case R.id.img_merchant_second_btn:
			imageUploadType = 4;
			detailpictype = "sjtps";
			paizhaoDialog();
			break;
		case R.id.img_merchant_third_btn:
			imageUploadType = 4;
			detailpictype = "sjtpt";
			paizhaoDialog();
			break;
		case R.id.img_kitchen_first_btn:
			imageUploadType = 5;
			detailpictype = "cftpf";
			paizhaoDialog();
			break;
		case R.id.img_kitchen_second_btn:
			imageUploadType = 5;
			detailpictype = "cftps";
			paizhaoDialog();
			break;
		case R.id.img_kitchen_third_btn:
			imageUploadType = 5;
			detailpictype = "cftpt";
			paizhaoDialog();
			break;
		case R.id.submit_audit_btn:
			if (canPost()) {
				showLoading();
				tijiaoHttpPost();
			} 
			break;
		case R.id.tv_paizhao:
			paizhaodialog.dismiss();
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				File file = createFile();
				if (file == null) {
					Toast.makeText(NewOpenStoreActivtiy.this, "未读取到储存卡",
							Toast.LENGTH_LONG).show();
				} else {
					Log.e("ee", "file:" + file);
					path = file.getAbsolutePath();

					Log.e("ee", "p:" + path);
					Toast.makeText(NewOpenStoreActivtiy.this, "请横向拍照！",
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
			paizhaodialog.dismiss();
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, 1);
			break;
		case R.id.tv_dialog_cancel:
			paizhaodialog.dismiss();
			break;
		default:
			break;
		}
	}

	// 提交添加商家接口
	private void tijiaoHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(NewOpenStoreActivtiy.this));
		params.addBodyParameter("member_name",
				new DButil().getMember_name(NewOpenStoreActivtiy.this));
		params.addBodyParameter("store_name", merchant_storename_et.getText()
				.toString().trim());
		params.addBodyParameter("company_master", merchant_fr_name_et.getText()
				.toString().trim());
		params.addBodyParameter("contacts_phone", merchant_fr_mobile_connect_et
				.getText().toString().trim());
		params.addBodyParameter("company_phone", merchant_fr_mobile_service_et
				.getText().toString().trim());
		params.addBodyParameter("id_card_no", merchant_fr_id_et.getText()
				.toString().trim());
		params.addBodyParameter("sc_id", erjiid);
		params.addBodyParameter("narea_p", shengstr);
		params.addBodyParameter("narea_s", shistr);
		params.addBodyParameter("narea_q", qustr);
		params.addBodyParameter("narea_z", jiedaostr);
		params.addBodyParameter("company_address_detail", merchant_address_et
				.getText().toString().trim());
		params.addBodyParameter("longitude", longitude);
		params.addBodyParameter("latitude", latitude);
		params.addBodyParameter("addstore_image_01", new Gson().toJson(map1));
		params.addBodyParameter("addstore_image_02", new Gson().toJson(map2));
		params.addBodyParameter("addstore_image_03",new Gson().toJson(map3));
		params.addBodyParameter("type", "0");
		
		Log.e("ee", "addstore_image_01"+new Gson().toJson(map1)+
				".....addstore_image_02"+new Gson().toJson(map2)+
				".....addstore_image_03"+new Gson().toJson(map3));
		
		
		httpUtils.send(HttpMethod.POST, JiekouUtils.ADDSHANGJIA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						tijiaoJsonInfo(arg0.result);
					}
				});
	}

	protected void tijiaoJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				handler.sendEmptyMessage(HANDLER_TIJIAOFANKUI_SUCCESS);
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

	private boolean canPost() {
		String errordata = "";
		String storename = merchant_storename_et.getText().toString();
		String merchantAddress = merchant_address_tv.getText().toString();
		String merchantdetailAddress = merchant_address_et.getText().toString();
		String legalName = merchant_fr_name_et.getText().toString();
		String legalID = merchant_fr_id_et.getText().toString();
		String contractMobile = merchant_fr_mobile_connect_et.getText()
				.toString();
		String serviceMobile = merchant_fr_mobile_service_et.getText()
				.toString();
		// 商家基本信息验证
		if (TextUtils.isEmpty(storename)) {
			merchant_storename_et.requestFocus();
			errordata = "店铺名称不能为空!";
			Toast.makeText(NewOpenStoreActivtiy.this, errordata,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(erjiid)) {
			errordata = "请选择所属行业!";
			Toast.makeText(NewOpenStoreActivtiy.this, errordata,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if ("点击当前位置定位".equals(merchantAddress)) {
			merchant_address_tv.requestFocus();
			errordata = "请在开阔位置点击定位图标获取位置!";
			Toast.makeText(NewOpenStoreActivtiy.this, errordata,
					Toast.LENGTH_SHORT).show();
			return false;

		} else if (TextUtils.isEmpty(merchantdetailAddress)) {
			merchant_address_et.requestFocus();
			errordata = "详细地址不能为空!";
			Toast.makeText(NewOpenStoreActivtiy.this, errordata,
					Toast.LENGTH_SHORT).show();
			return false;

		} else if (TextUtils.isEmpty(legalName)) {
			merchant_fr_name_et.requestFocus();
			errordata = "商家法人不能为空!";
			
			Toast.makeText(NewOpenStoreActivtiy.this, errordata,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(legalID)) {
			merchant_fr_id_et.requestFocus();
			errordata = "商家法人身份证不能为空!";
			Toast.makeText(NewOpenStoreActivtiy.this, errordata,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (!IDUtils.isCorrectID(legalID)) {
			merchant_fr_id_et.requestFocus();
			errordata = "商家法人身份证格式不正确!";
			Toast.makeText(NewOpenStoreActivtiy.this, errordata,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(contractMobile)) {
			merchant_fr_mobile_connect_et.requestFocus();
			errordata = "联系电话不能为空!";
			Toast.makeText(NewOpenStoreActivtiy.this, errordata,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(serviceMobile)) {
			merchant_fr_mobile_service_et.requestFocus();
			errordata = "服务电话不能为空!";
			Toast.makeText(NewOpenStoreActivtiy.this, errordata,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		// 下面是图片验证
		else if (TextUtils.isEmpty(map1.get("id1"))) {
			errordata = "请上传身份证正面!";
			Toast.makeText(NewOpenStoreActivtiy.this, errordata,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(map1.get("id2"))) {
			errordata = "请上传身份证反面!";
			Toast.makeText(NewOpenStoreActivtiy.this, errordata,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(map1.get("id3"))) {
			errordata = "请上传营业执照!";
			Toast.makeText(NewOpenStoreActivtiy.this, errordata,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(map2.get("id1"))) {
			errordata = "请上传商家图片1号图!";
			Toast.makeText(NewOpenStoreActivtiy.this, errordata,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(map2.get("id2"))) {
			errordata = "请上传商家图片2号图!";
			Toast.makeText(NewOpenStoreActivtiy.this, errordata,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(map2.get("id3"))) {
			errordata = "请上传商家图片3号图!";
			Toast.makeText(NewOpenStoreActivtiy.this, errordata,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		// else if(TextUtils.isEmpty(map3.get("id1"))){
		// errordata = "请上传厨房图片1号图!";
		// Toast.makeText(NewOpenStoreActivtiy.this, errordata,
		// Toast.LENGTH_SHORT).show();
		// return false;
		// }else if(TextUtils.isEmpty(map3.get("id2"))){
		// errordata = "请上传厨房图片2号图!";
		// Toast.makeText(NewOpenStoreActivtiy.this, errordata,
		// Toast.LENGTH_SHORT).show();
		// return false;
		// }else if(TextUtils.isEmpty(map3.get("id3"))){
		// errordata = "请上传厨房图片3号图!";
		// Toast.makeText(NewOpenStoreActivtiy.this, errordata,
		// Toast.LENGTH_SHORT).show();
		// return false;
		// }
		return true;
	}

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(NewOpenStoreActivtiy.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				NewOpenStoreActivtiy.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(NewOpenStoreActivtiy.this,
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

	/**
	 * 初始化定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 * 
	 */
	private void initLocation() {
		// 初始化client
		locationClient = new AMapLocationClient(
				NewOpenStoreActivtiy.this.getApplicationContext());
		// 设置定位参数
		locationClient.setLocationOption(getDefaultOption());
		// 设置定位监听
		locationClient.setLocationListener(locationListener);

		// 定位
		startLocation();
	}

	/**
	 * 默认的定位参数
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 * 
	 */
	private AMapLocationClientOption getDefaultOption() {
		AMapLocationClientOption mOption = new AMapLocationClientOption();
		mOption.setLocationMode(AMapLocationMode.Hight_Accuracy);// 可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
		mOption.setGpsFirst(false);// 可选，设置是否gps优先，只在高精度模式下有效。默认关闭
		mOption.setHttpTimeOut(30000);// 可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
		mOption.setInterval(2000);// 可选，设置定位间隔。默认为2秒
		mOption.setNeedAddress(true);// 可选，设置是否返回逆地理地址信息。默认是ture
		mOption.setOnceLocation(false);// 可选，设置是否单次定位。默认是false
		mOption.setOnceLocationLatest(false);// 可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
		AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP);// 可选，
																				// 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
		return mOption;
	}

	/**
	 * 定位监听
	 */

	AMapLocationListener locationListener = new AMapLocationListener() {

		@Override
		public void onLocationChanged(AMapLocation loc) {
			if (null != loc) {
				// 解析定位结果
				// String result = LocationUtils.getLocationStr(loc);
				if (loc.getErrorCode() == 0) {
					// 经 度
					longitude = loc.getLongitude() + "";
					// 纬 度
					latitude = loc.getLatitude() + "";
					shengstr = loc.getProvince();
					shistr = loc.getCity();
					qustr = loc.getDistrict();
					jiedaostr = loc.getStreet();
					// 省市区
					String address = loc.getProvince() + loc.getCity()
							+ loc.getDistrict();
					String detailaddr = loc.getStreet();
					if (!TextUtils.isEmpty(address)) {
						String street = loc.getStreet();
						merchant_address_tv.setText(address.trim());
						String detaileaddress = loc.getAddress();
						if (!TextUtils.isEmpty(detaileaddress)) {
							String qusplitaddr[] = detaileaddress.split("区");
							detailaddr = qusplitaddr[1];
							if (qusplitaddr.length < 2) {
								String zhensplit[] = detaileaddress.split("区");
								detailaddr = zhensplit[1];
							}
						}
						merchant_address_et.setText(detailaddr);

					}
				} else {
					// 定位失败
					// Toast.makeText(getActivity(), "正在定位中",
					// Toast.LENGTH_SHORT)
					// .show();
				}
			} else {
				// Toast.makeText(getActivity(), "正在定位中", Toast.LENGTH_SHORT)
				// .show();
			}
			handler.sendEmptyMessage(HANDLER_DINGWEI_FINISH);
		}
	};

	/**
	 * 开始定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 * 
	 */
	private void startLocation() {
		// 设置定位参数
		locationClient.setLocationOption(getDefaultOption());
		// 启动定位
		locationClient.startLocation();
	}

	/**
	 * 停止定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 * 
	 */
	private void stopLocation() {
		// 停止定位
		locationClient.stopLocation();
	}

	/**
	 * 销毁定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 * 
	 */
	private void destroyLocation() {
		if (null != locationClient) {
			/**
			 * 如果AMapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
			 */
			locationClient.onDestroy();
			locationClient = null;
			locationOption = null;
		}
	}

	private Dialog paizhaodialog;
	private Map<String, String> map1;
	private Map<String, String> map2;
	private Map<String, String> map3;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	// 拍照dialog
	private void paizhaoDialog() {
		View view = LayoutInflater.from(NewOpenStoreActivtiy.this).inflate(
				R.layout.dialog_paizhao, null);
		TextView tv_dialog_cancel = (TextView) view
				.findViewById(R.id.tv_dialog_cancel);
		TextView tv_paizhao = (TextView) view.findViewById(R.id.tv_paizhao);
		TextView tv_xiangce = (TextView) view.findViewById(R.id.tv_xiangce);
		tv_dialog_cancel.setOnClickListener(this);
		tv_paizhao.setOnClickListener(this);
		tv_xiangce.setOnClickListener(this);
		paizhaodialog = new Dialog(NewOpenStoreActivtiy.this,
				R.style.mDialogStyle);
		paizhaodialog.setContentView(view);
		paizhaodialog.setCanceledOnTouchOutside(false);
		paizhaodialog.show();
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
			if (resultCode == 301) {
				String erjiname = data.getStringExtra("erjiname");
				erjiid = data.getStringExtra("erjiid");
				tv_sshy.setText(erjiname);
			}
			// 相册返回处理
			if (requestCode == 1) {

				if (resultCode == RESULT_OK) {
					Uri uri = data.getData();
					if (uri == null) {
						Log.e("ee", "data:" + data.getExtras().get("data"));
						Toast.makeText(NewOpenStoreActivtiy.this,
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
						if ("sfzzm".equals(detailpictype)) {
							img_id_front_iv.setScaleType(ScaleType.CENTER_CROP);
							img_id_front_iv.setImageBitmap(bm);
						} else if ("sfzfm".equals(detailpictype)) {
							img_id_back_iv.setScaleType(ScaleType.CENTER_CROP);
							img_id_back_iv.setImageBitmap(bm);
						} else if ("yyzz".equals(detailpictype)) {
							img_license_iv.setScaleType(ScaleType.CENTER_CROP);
							img_license_iv.setImageBitmap(bm);
						} else if ("sjtpf".equals(detailpictype)) {
							img_merchant_first_iv
									.setScaleType(ScaleType.CENTER_CROP);
							img_merchant_first_iv.setImageBitmap(bm);
						} else if ("sjtps".equals(detailpictype)) {
							img_merchant_second_iv
									.setScaleType(ScaleType.CENTER_CROP);
							img_merchant_second_iv.setImageBitmap(bm);
						} else if ("sjtpt".equals(detailpictype)) {
							img_merchant_third_iv
									.setScaleType(ScaleType.CENTER_CROP);
							img_merchant_third_iv.setImageBitmap(bm);
						} else if ("cftpf".equals(detailpictype)) {
							img_kitchen_first_iv
									.setScaleType(ScaleType.CENTER_CROP);
							img_kitchen_first_iv.setImageBitmap(bm);
						} else if ("cftps".equals(detailpictype)) {
							img_kitchen_second_iv
									.setScaleType(ScaleType.CENTER_CROP);
							img_kitchen_second_iv.setImageBitmap(bm);
						} else if ("cftpt".equals(detailpictype)) {
							img_kitchen_third_iv
									.setScaleType(ScaleType.CENTER_CROP);
							img_kitchen_third_iv.setImageBitmap(bm);
						}
						showLoading();
						shangchuanpicHttpPost();

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
								if ("sfzzm".equals(detailpictype)) {
									img_id_front_iv
											.setScaleType(ScaleType.CENTER_CROP);
									img_id_front_iv.setImageBitmap(bitmap);
								} else if ("sfzfm".equals(detailpictype)) {
									img_id_back_iv
											.setScaleType(ScaleType.CENTER_CROP);
									img_id_back_iv.setImageBitmap(bitmap);
								} else if ("yyzz".equals(detailpictype)) {
									img_license_iv
											.setScaleType(ScaleType.CENTER_CROP);
									img_license_iv.setImageBitmap(bitmap);
								} else if ("sjtpf".equals(detailpictype)) {
									img_merchant_first_iv
											.setScaleType(ScaleType.CENTER_CROP);
									img_merchant_first_iv
											.setImageBitmap(bitmap);
								} else if ("sjtps".equals(detailpictype)) {
									img_merchant_second_iv
											.setScaleType(ScaleType.CENTER_CROP);
									img_merchant_second_iv
											.setImageBitmap(bitmap);
								} else if ("sjtpt".equals(detailpictype)) {
									img_merchant_third_iv
											.setScaleType(ScaleType.CENTER_CROP);
									img_merchant_third_iv
											.setImageBitmap(bitmap);
								} else if ("cftpf".equals(detailpictype)) {
									img_kitchen_first_iv
											.setScaleType(ScaleType.CENTER_CROP);
									img_kitchen_first_iv.setImageBitmap(bitmap);
								} else if ("cftps".equals(detailpictype)) {
									img_kitchen_second_iv
											.setScaleType(ScaleType.CENTER_CROP);
									img_kitchen_second_iv
											.setImageBitmap(bitmap);
								} else if ("cftpt".equals(detailpictype)) {
									img_kitchen_third_iv
											.setScaleType(ScaleType.CENTER_CROP);
									img_kitchen_third_iv.setImageBitmap(bitmap);
								}
								showLoading();
								shangchuanpicHttpPost();
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

	private void shangchuanpicHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(NewOpenStoreActivtiy.this));
		params.addBodyParameter("type", imageUploadType + "");
		// upfile 根据接口的不同，这个值也不同，我的理解是；服务器规定，这个key对应的值，就是文件形式的
		// params.addBodyParameter("upfile", file,mime);
		params.addBodyParameter("image", file);
		String u = SharedUtil.getStringValue(SharedCommon.IMG_DOMAIN, "");
		httpUtils.send(HttpMethod.POST, u + JiekouUtils.SHANGCHJUANSHANGJIAPIC,
				params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						shangchuanpicJsonInfo(arg0.result);
					}
				});
	}

	protected void shangchuanpicJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONObject job = object.getJSONObject("data");
				String picurl = job.getString("url");
				String picid = job.getString("id");
				if ("sfzzm".equals(detailpictype)) {
					map1.put("id1", picid);
				} else if ("sfzfm".equals(detailpictype)) {
					map1.put("id2", picid);
				} else if ("yyzz".equals(detailpictype)) {
					map1.put("id3", picid);
				} else if ("sjtpf".equals(detailpictype)) {
					map2.put("id1", picid);
				} else if ("sjtps".equals(detailpictype)) {
					map2.put("id2", picid);
				} else if ("sjtpt".equals(detailpictype)) {
					map2.put("id3", picid);
				} else if ("cftpf".equals(detailpictype)) {
					map3.put("id1", picid);
				} else if ("cftps".equals(detailpictype)) {
					map3.put("id2", picid);
				} else if ("cftpt".equals(detailpictype)) {
					map3.put("id3", picid);
				}
				handler.sendEmptyMessage(HANDLER_PIC_SUCCESS);
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
