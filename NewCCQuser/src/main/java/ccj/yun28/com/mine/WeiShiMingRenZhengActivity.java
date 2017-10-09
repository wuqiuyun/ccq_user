package ccj.yun28.com.mine;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 未实名认证
 * 
 * @author meihuali
 * 
 */
public class WeiShiMingRenZhengActivity extends BaseActivity implements
		OnClickListener {
	private String path = "";
	private File file;

	private String zhengjian_num;
	private String true_name;
	private String type;
	private EditText et_zhengjian_num;
	private EditText et_true_name;
	private RadioButton radio_sfz;
	private RadioButton radio_jgz;
	private RadioButton radio_tbz;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 提交成功
	private static final int HANDLER_TIJIAO_SUCCESS = 3;
	private static final int HANDLER_EDITSEX_SUCCESS = 4;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(WeiShiMingRenZhengActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(WeiShiMingRenZhengActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(WeiShiMingRenZhengActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 提交成功
			case HANDLER_TIJIAO_SUCCESS:

				touxiangpicHttpPost();
				
				break;
			// 图片上传成功
			case HANDLER_EDITSEX_SUCCESS:
				dissDialog();
				finish();
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
		setContentView(R.layout.activity_weishimingrenzheng);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		radio_sfz = (RadioButton) findViewById(R.id.radio_sfz);
		radio_jgz = (RadioButton) findViewById(R.id.radio_jgz);
		radio_tbz = (RadioButton) findViewById(R.id.radio_tbz);
		et_zhengjian_num = (EditText) findViewById(R.id.et_zhengjian_num);
		et_true_name = (EditText) findViewById(R.id.et_true_name);
		iv_shangchuan = (ImageView) findViewById(R.id.iv_shangchuan);
		TextView tv_tijiao = (TextView) findViewById(R.id.tv_tijiao);
		
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(WeiShiMingRenZhengActivity.this);

		line_back.setOnClickListener(this);
		iv_shangchuan.setOnClickListener(this);
		tv_tijiao.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.iv_shangchuan:
			View view = LayoutInflater.from(WeiShiMingRenZhengActivity.this)
					.inflate(R.layout.dialog_paizhao, null);
			TextView tv_dialog_cancel = (TextView) view
					.findViewById(R.id.tv_dialog_cancel);
			TextView tv_paizhao = (TextView) view.findViewById(R.id.tv_paizhao);
			TextView tv_xiangce = (TextView) view.findViewById(R.id.tv_xiangce);
			tv_dialog_cancel.setOnClickListener(this);
			tv_paizhao.setOnClickListener(this);
			tv_xiangce.setOnClickListener(this);
			touxiangdialog = new Dialog(WeiShiMingRenZhengActivity.this,
					R.style.mDialogStyle);
			touxiangdialog.setContentView(view);
			touxiangdialog.setCanceledOnTouchOutside(false);
			touxiangdialog.show();
			break;
		case R.id.tv_tijiao:
			if (canPost()) {
				showLoading();
				tijiaoHttpPost();
			}
			break;
		case R.id.tv_paizhao:
			touxiangdialog.dismiss();
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				File file = createFile();
				if (file == null) {
					Toast.makeText(WeiShiMingRenZhengActivity.this, "未读取到储存卡",
							Toast.LENGTH_LONG).show();
				} else {
					Log.e("ee", "file:" + file);
					path = file.getAbsolutePath();

					Log.e("ee", "p:" + path);
					Toast.makeText(WeiShiMingRenZhengActivity.this, "请横向拍照！",
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
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, 1);
			break;
		case R.id.tv_dialog_cancel:
			touxiangdialog.dismiss();
			break;
		default:
			break;
		}
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
			// 相册返回处理
			if (requestCode == 1) {

				if (resultCode == RESULT_OK) {
					Uri uri = data.getData();
					if (uri == null) {
						Log.e("ee", "data:" + data.getExtras().get("data"));
						Toast.makeText(WeiShiMingRenZhengActivity.this,
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
						iv_shangchuan.setScaleType(ScaleType.CENTER_CROP);
						iv_shangchuan.setImageBitmap(bm);

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
								iv_shangchuan
										.setScaleType(ScaleType.CENTER_CROP);
								iv_shangchuan.setImageBitmap(bitmap);
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

	// 提交图片
	private void touxiangpicHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("id",rzid);
		// upfile 根据接口的不同，这个值也不同，我的理解是；服务器规定，这个key对应的值，就是文件形式的
		// params.addBodyParameter("upfile", file,mime);
		params.addBodyParameter("real_verify", file);
		String u = SharedUtil.getStringValue(SharedCommon.IMG_DOMAIN, "");
		httpUtils.send(HttpMethod.POST, u + JiekouUtils.SHIMINGPIC, params,
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

	// 提交实名认证接口
	private void tijiaoHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(WeiShiMingRenZhengActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(WeiShiMingRenZhengActivity.this));
		params.addBodyParameter("type", type);
		params.addBodyParameter("number", zhengjian_num);
		params.addBodyParameter("name", true_name);
		httpUtils.send(HttpMethod.POST, JiekouUtils.TIJIAOSHIMINGRENZHENG,
				params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e("ee", "失败:" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						tijiaoListInfo(arg0.result);
					}
				});
	}

	protected void tijiaoListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				rzid = object.getString("id");
				handler.sendEmptyMessage(HANDLER_TIJIAO_SUCCESS);
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

	private boolean canPost() {
		zhengjian_num = et_zhengjian_num.getText().toString().trim();
		true_name = et_true_name.getText().toString().trim();
		if (TextUtils.isEmpty(zhengjian_num)) {
			Toast.makeText(WeiShiMingRenZhengActivity.this, "证件号码不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(true_name)) {
			Toast.makeText(WeiShiMingRenZhengActivity.this, "真实姓名不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(path)) {
			Toast.makeText(WeiShiMingRenZhengActivity.this, "图片不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		}

		// 证件类型 1身份证2军官证3台胞证
		if (radio_sfz.isChecked()) {
			type = "1";
		} else if (radio_jgz.isChecked()) {
			type = "2";
		} else if (radio_tbz.isChecked()) {
			type = "3";
		}
		return true;
	}

	// 加载中动画
	private Dialog loadingDialog;
	private Dialog touxiangdialog;
	private ImageView iv_shangchuan;
	private String rzid;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(WeiShiMingRenZhengActivity.this)
				.inflate(R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				WeiShiMingRenZhengActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(WeiShiMingRenZhengActivity.this,
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
