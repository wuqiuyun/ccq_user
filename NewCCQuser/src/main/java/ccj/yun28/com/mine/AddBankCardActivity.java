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
 * 添加银行卡
 * 
 * @author meihuali
 * 
 */
public class AddBankCardActivity extends BaseActivity implements OnClickListener {

	private EditText et_true_name;
	private EditText et_card_num;
	private EditText et_bank_address;
	private Dialog paizahodialog;
	private String path = "";
	private ImageView iv_bank_pic;
	// 网络异常
		protected static final int HANDLER_NET_FAILURE = 0;
		// 异常
		protected static final int HANDLER_NN_FAILURE = 1;
		// 获取信息失败
		private static final int HANDLER_GETINFO_FAILURE = 2;
		// 银行卡信息提交成功
		private static final int HANDLER_CARDINFO_SUCCESS = 3;
		// 银行卡图片提交成功
		private static final int HANDLER_CARDPIC_SUCCESS = 4;

		private Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				// 网络异常
				case HANDLER_NET_FAILURE:
					dissDialog();
					Toast.makeText(AddBankCardActivity.this, "当前网络不可用,请检查网络",
							Toast.LENGTH_SHORT).show();
					break;
				// 错误
				case HANDLER_NN_FAILURE:
					dissDialog();
					Toast.makeText(AddBankCardActivity.this, "当前网络出错,请检查网络", Toast.LENGTH_SHORT)
							.show();
					break;
				// 获取信息失败
				case HANDLER_GETINFO_FAILURE:

					dissDialog();
					Toast.makeText(AddBankCardActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
					break;
				// 提交银行卡信息成功
				case HANDLER_CARDINFO_SUCCESS:
					cardpicHttpPost();
					break;
					// 提交银行卡信息成功
				case HANDLER_CARDPIC_SUCCESS:
					dissDialog();
					finish();
					Toast.makeText(AddBankCardActivity.this,
							"提交成功", Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.activity_addbankcard);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		et_true_name = (EditText) findViewById(R.id.et_true_name);
		et_card_num = (EditText) findViewById(R.id.et_card_num);
		et_bank_address = (EditText) findViewById(R.id.et_bank_address);
		iv_bank_pic = (ImageView) findViewById(R.id.iv_bank_pic);
		TextView tv_tijiao = (TextView) findViewById(R.id.tv_tijiao);

		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(AddBankCardActivity.this);
		
		line_back.setOnClickListener(this);
		iv_bank_pic.setOnClickListener(this);
		tv_tijiao.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.iv_bank_pic:
			View view = LayoutInflater.from(AddBankCardActivity.this).inflate(
					R.layout.dialog_paizhao, null);
			TextView tv_dialog_cancel = (TextView) view
					.findViewById(R.id.tv_dialog_cancel);
			TextView tv_paizhao = (TextView) view.findViewById(R.id.tv_paizhao);
			TextView tv_xiangce = (TextView) view.findViewById(R.id.tv_xiangce);
			tv_dialog_cancel.setOnClickListener(this);
			tv_paizhao.setOnClickListener(this);
			tv_xiangce.setOnClickListener(this);
			paizahodialog = new Dialog(AddBankCardActivity.this,
					R.style.mDialogStyle);
			paizahodialog.setContentView(view);
			paizahodialog.setCanceledOnTouchOutside(false);
			paizahodialog.show();
			break;
		case R.id.tv_paizhao:
			paizahodialog.dismiss();
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				File file = createFile();
				if (file == null) {
					Toast.makeText(AddBankCardActivity.this,
							"未读取到储存卡", Toast.LENGTH_LONG).show();
				} else {
					Log.e("ee", "file:" + file);
					path = file.getAbsolutePath();

					Log.e("ee", "p:" + path);
					Toast.makeText(AddBankCardActivity.this,
							"请横向拍照！", Toast.LENGTH_LONG).show();
					// Intent intent = new Intent(
					// "android.media.action.IMAGE_CAPTURE");
					// intent.putExtra(MediaStore.EXTRA_OUTPUT,
					// Uri.fromFile(file));
					// startActivityForResult(intent, 2);
					Intent intent = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					// 指定一个图片路径对应的file对象
					// 启动activity
					startActivityForResult(intent, 2);
				}
			}
			break;
		case R.id.tv_xiangce:
			paizahodialog.dismiss();
			Intent intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.setDataAndType(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, 1);
			break;
		case R.id.tv_dialog_cancel:
			paizahodialog.dismiss();
			break;
		case R.id.tv_tijiao:
			if (canPost()) {
				showLoading();
				tijiaoBankCardHttpPost();
			}
			break;

		default:
			break;
		}
	}

	//提交银行卡图片
	private void cardpicHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("id", bank_card_id);
		//upfile 根据接口的不同，这个值也不同，我的理解是；服务器规定，这个key对应的值，就是文件形式的
//		params.addBodyParameter("upfile", file,mime);
		params.addBodyParameter("bank_card", file);
		String u = SharedUtil.getStringValue(SharedCommon.IMG_DOMAIN, "");
		httpUtils.send(HttpMethod.POST, u + JiekouUtils.ADDBANKARDPIC, params, new RequestCallBack<String>() {
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(HANDLER_NET_FAILURE);
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				cardpicJsonInfo(arg0.result);
			}
		});
	}
	protected void cardpicJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				handler.sendEmptyMessage(HANDLER_CARDPIC_SUCCESS);
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

	//提交银行卡信息
	private void tijiaoBankCardHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token", new DButil().gettoken(AddBankCardActivity.this));
		params.addBodyParameter("member_id", new DButil().getMember_id(AddBankCardActivity.this));
		params.addBodyParameter("true_name", true_name);
		params.addBodyParameter("card_address", card_address);
		params.addBodyParameter("card_account", card_account);
		httpUtils.send(HttpMethod.POST, JiekouUtils.ADDBANK, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(HANDLER_NET_FAILURE);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				tijiaoBankCardJsonInfo(arg0.result);
			}
		});
	}

	protected void tijiaoBankCardJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				bank_card_id = object.getString("bank_card_id");
				handler.sendEmptyMessage(HANDLER_CARDINFO_SUCCESS);
				
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
		true_name = et_true_name.getText().toString().trim();
		card_account = et_card_num.getText().toString().trim();
		card_address = et_bank_address.getText().toString().trim();
		if (TextUtils.isEmpty(true_name)) {
			Toast.makeText(AddBankCardActivity.this, "真实姓名不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(card_account)) {
			Toast.makeText(AddBankCardActivity.this, "银行卡号不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(card_address)) {
			Toast.makeText(AddBankCardActivity.this, "银行卡地址不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (card_account.length() > 19 || card_account.length() < 16) {
			Toast.makeText(AddBankCardActivity.this, "银行卡号应为16-19位",
					Toast.LENGTH_SHORT).show();
			return false;
		}
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
		return Environment.getExternalStorageDirectory() + "/"
				+ "ccq";
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

		// 相册返回处理
		if (requestCode == 1) {

			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				if (uri == null) {
					Log.e("ee", "data:" + data.getExtras().get("data"));
					Toast.makeText(AddBankCardActivity.this,
							"从相册获取图片失败！请使用相机拍照获取！", 1).show();
				}
				Log.e("ee", "url:" + uri);
				try {
					String img_path;
					String[] proj = { MediaStore.Images.Media.DATA };
					Cursor actualimagecursor = getContentResolver().query(uri,
							proj, null, null, null);
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
							+ "ccq" + "/" + pathString
							+ ".jpg";
					savePhotoToSDCard(bitmap,
							Environment.getExternalStorageDirectory() + "/"
									+ "ccq/", pathString);
					Bitmap bm = BitmapFactory.decodeFile(path);
					// 将图片显示到ImageView中
					// img.setImageBitmap(bm);
					iv_bank_pic.setScaleType(ScaleType.CENTER_CROP);
					iv_bank_pic.setImageBitmap(bm);

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
						Bitmap bitmap = (Bitmap) data.getExtras().get("data");
						// 得到bitmap后的操作
						if (bitmap != null) {
							// Bitmap bitmap=util.compressImage(bitmap1);
							// pic.add(bitmap);
							String pathString = createFileName();

							path = Environment.getExternalStorageDirectory()
									+ "/" + "ccq" + "/"
									+ pathString + ".jpg";
							file=new File(path);
							savePhotoToSDCard(bitmap,
									Environment.getExternalStorageDirectory()
											+ "/" + "ccq/",
									pathString);
							// bitmap = BitmapFactory.decodeFile(path);
							iv_bank_pic.setScaleType(ScaleType.CENTER_CROP);
							iv_bank_pic.setImageBitmap(bitmap);
						}
					}
				}
			}
			if (resultCode == 0) {
				path = "";
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

	//加载中动画
	private Dialog loadingDialog;
	private String true_name;
	private String card_account;
	private String card_address;
	private String bank_card_id;
	private File file;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;	

//加载动画
private void showLoading() {
	View view = LayoutInflater.from(AddBankCardActivity.this).inflate(R.layout.loading,
					null);
	ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
	Animation animation = AnimationUtils.loadAnimation(AddBankCardActivity.this,
	R.anim.loading_anim);
	image.startAnimation(animation);
	loadingDialog = new Dialog(AddBankCardActivity.this, R.style.mDialogStyle);
	loadingDialog.setContentView(view);
	loadingDialog.setCanceledOnTouchOutside(false);
	loadingDialog.show();
		}
//关闭加载dialog
private void dissDialog() {
	if (loadingDialog != null && loadingDialog.isShowing()) {
		loadingDialog.dismiss();
	}
}
}
