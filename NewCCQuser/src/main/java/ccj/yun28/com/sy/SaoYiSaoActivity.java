package ccj.yun28.com.sy;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.ZhuCeActivity;
import ccj.yun28.com.ccq.CcqProDetailActivity;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.sy.sys.camera.CameraManager;
import ccj.yun28.com.sy.sys.decoding.CaptureActivityHandler;
import ccj.yun28.com.sy.sys.decoding.InactivityTimer;
import ccj.yun28.com.sy.sys.decoding.RGBLuminanceSource;
import ccj.yun28.com.sy.sys.view.ViewfinderView;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;
import pub.devrel.easypermissions.EasyPermissions;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class SaoYiSaoActivity extends BaseActivity implements Callback,
        View.OnClickListener {
    private static final int RC_CAMERA_PERM = 200;//动态获取权限请求码
    private SaoYiSaoActivity mActivity;

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    // 闪光灯是否打开
    private boolean sfkd = false;
    ;

    // 扫一扫结果
    private Dialog sys_resultdialog;

    private DBHelper myDB;

    private Camera m_Camera = null;

    // 二维码接口请求返回
    private String goods_id;
    private String type;

    private static final int REQUEST_CODE = 100;
    private static final int PARSE_BARCODE_SUC = 300;
    private static final int PARSE_BARCODE_FAIL = 303;
    private static final int HANDLER_NET_FAILURE = 0;
    // 扫码完成接口
    private static final int HANDLER_SANMAFINISH_SUCCESS = 1;
    private static final int HANDLER_SANMAFINISH_FAILURE = 2;
    private static final int HANDLER_NN_FAILURE = 3;
    private ProgressDialog mProgress;
    private String photo_path;
    private Bitmap scanBitmap;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_capture);
        mActivity = this;
        // ViewUtil.addTopView(getApplicationContext(), this,
        // R.string.scan_card);
        verifyPermissionForCamera();


        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
        Button bu_back = (Button) findViewById(R.id.bu_back);
        TextView tv_xc = (TextView) findViewById(R.id.tv_xc);
        TextView tv_kd = (TextView) findViewById(R.id.tv_kd);
        TextView tv_ewm = (TextView) findViewById(R.id.tv_ewm);

        myDB = new DBHelper(SaoYiSaoActivity.this);

        line_back.setOnClickListener(this);
        bu_back.setOnClickListener(this);
        tv_xc.setOnClickListener(this);
        tv_kd.setOnClickListener(this);
        tv_ewm.setOnClickListener(this);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

    }

    //动态获取权限
    public void verifyPermissionForCamera() {
        String[] perms = {Manifest.permission.CAMERA};
//        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mActivity, perms)) {
            CameraManager.init(getApplication());
        } else {
            EasyPermissions.requestPermissions(this, "程序需要打开摄像头权限和访问存储空间权限", RC_CAMERA_PERM, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.line_back:
            case R.id.bu_back://返回
                SaoYiSaoActivity.this.finish();
                break;
            case R.id.tv_xc://相册
                Intent wrapperIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
                // Intent wrapperIntent = new Intent(Intent.ACTION_PICK,
                // android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                wrapperIntent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                this.startActivityForResult(wrapperIntent, REQUEST_CODE);
                break;
            case R.id.tv_dialog_cancel:
                sys_resultdialog.dismiss();
                break;
            case R.id.tv_ok:

                break;
            case R.id.tv_kd://开灯
//			if (sfkd) {
//				sfkd = false;
//			} else {
//				sfkd = true;
//			}
                break;
            case R.id.tv_ewm://我的二维码
                if (isLogin()) {
                    Intent intent = new Intent(SaoYiSaoActivity.this,
                            YqzcActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SaoYiSaoActivity.this,
                            LoginActivity.class);
                    intent.putExtra("type", "qt");
                    startActivity(intent);
                }
                break;
        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case PARSE_BARCODE_SUC:
                    mProgress.dismiss();
                    // handleDecode((String)msg.obj, scanBitmap);
                    onResultHandler((String) msg.obj, scanBitmap);
                    break;
                case PARSE_BARCODE_FAIL:
                    mProgress.dismiss();
                    if (msg.obj != null) {
                        Toast.makeText(SaoYiSaoActivity.this, (String) msg.obj,
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case HANDLER_NET_FAILURE:
                    Toast.makeText(SaoYiSaoActivity.this, "当前网络不可用,请检查网络",
                            Toast.LENGTH_SHORT).show();
                    break;
                case HANDLER_NN_FAILURE:
                    Toast.makeText(SaoYiSaoActivity.this, "当前网络不可用,请检查网络", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case HANDLER_SANMAFINISH_SUCCESS:
                    Intent intent;
                    // 0 普通产品 2餐餐抢套餐 3 扫码注册
                    if ("0".equals(type)) {
                        intent = new Intent(SaoYiSaoActivity.this,
                                ProductDetailActivity.class);
                        intent.putExtra("goods_id", goods_id);
                        startActivity(intent);
                        finish();
                    } else if ("2".equals(type)) {
                        intent = new Intent(SaoYiSaoActivity.this,
                                CcqProDetailActivity.class);
                        intent.putExtra("ccqgoods_id", goods_id);
                        startActivity(intent);
                        finish();
                    } else if ("3".equals(type)) {
                        intent = new Intent(SaoYiSaoActivity.this,
                                ZhuCeActivity.class);
                        intent.putExtra("tjrgoods_id", goods_id);
                        startActivity(intent);
                        finish();
                    }
                    break;
                case HANDLER_SANMAFINISH_FAILURE:
                    Toast.makeText(SaoYiSaoActivity.this,
                            msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
                    break;

            }
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    // 获取选中图片的路径
                    // Cursor cursor = getContentResolver().query(data.getData(),
                    // null, null, null, null);
                    // if (cursor.moveToFirst()) {
                    // photo_path =
                    // cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    // }
                    // cursor.close();
                    Uri uri = data.getData();
                    String[] proj = {MediaStore.Images.Media.DATA};
                    // Cursor actualimagecursor = getContentResolver().query(uri,
                    // proj, null, null, null);
                    Cursor actualimagecursor = managedQuery(uri, proj, null, null,
                            null);
                    int actual_image_column_index = actualimagecursor
                            .getColumnIndexOrThrow(MediaColumns.DATA);
                    actualimagecursor.moveToFirst();
                    photo_path = actualimagecursor
                            .getString(actual_image_column_index);

                    mProgress = new ProgressDialog(SaoYiSaoActivity.this);
                    mProgress.setMessage("正在扫描...");
                    mProgress.setCancelable(false);
                    mProgress.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Result result = scanningImage(photo_path);
                            if (result != null) {
                                Message m = mHandler.obtainMessage();
                                m.what = PARSE_BARCODE_SUC;
                                m.obj = result.getText();
                                mHandler.sendMessage(m);
                            } else {
                                Message m = mHandler.obtainMessage();
                                m.what = PARSE_BARCODE_FAIL;
                                m.obj = "Scan failed!";
                                mHandler.sendMessage(m);
                            }
                        }
                    }).start();

                    break;

            }
        }
    }

    /**
     * 扫描二维码图片的方法
     *
     * @param path
     * @return
     */
    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); // 设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        CameraManager.init(getApplication());
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 处理扫描结果
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();

        saomaFinishHttpPost(resultString);

        // Toast.makeText(SaoYiSaoActivity.this, resultString,
        // Toast.LENGTH_SHORT)
        // .show();
        // showDialog(resultString);
        // onResultHandler(resultString, barcode);
    }

    // 扫码完成后判断二维码是否为商城二维码，跳转到相应页面
    private void saomaFinishHttpPost(String resultString) {
        // TODO Auto-generated method stub
        HttpUtils httpUtils = new HttpUtils();
        Utils utils = new Utils();
        String[] verstring = utils.getVersionInfo(SaoYiSaoActivity.this);

        RequestParams params = new RequestParams();
        params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0] + "," + verstring[2]);
        params.addBodyParameter("url", resultString);
        httpUtils.send(HttpMethod.POST, JiekouUtils.SAOYISAOFINISH, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        // TODO Auto-generated method stub
                        handler.sendEmptyMessage(HANDLER_NET_FAILURE);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        // TODO Auto-generated method stub
                        saomaFinishInfoList(arg0.result);
                    }
                });
    }

    // 解析扫码完成后接口返回数据
    protected void saomaFinishInfoList(String result) {
        // TODO Auto-generated method stub
        try {
            JSONObject object = new JSONObject(result);
            String code = object.getString("code");
            if ("200".equals(code)) {
                JSONObject jsonObject = object.getJSONObject("data");
                goods_id = jsonObject.getString("goods_id");
                type = jsonObject.getString("type");
                mHandler.sendEmptyMessage(HANDLER_SANMAFINISH_SUCCESS);
            } else {
                String message = object.getString("message");
                mHandler.sendMessage(handler.obtainMessage(
                        HANDLER_SANMAFINISH_FAILURE, message));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mHandler.sendEmptyMessage(HANDLER_NN_FAILURE);
        }

    }

    /**
     * 扫描完成后弹框
     */
    private void showDialog(String resultString) {
        View view = LayoutInflater.from(SaoYiSaoActivity.this).inflate(
                R.layout.dialog_sys_result, null);
        TextView tv_url = (TextView) view.findViewById(R.id.tv_url);
        TextView tv_dialog_cancel = (TextView) view
                .findViewById(R.id.tv_dialog_cancel);
        TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
        tv_url.setText(resultString);
        tv_dialog_cancel.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
        sys_resultdialog = new Dialog(SaoYiSaoActivity.this,
                R.style.mDialogStyle);
        sys_resultdialog.setContentView(view);
        sys_resultdialog.setCanceledOnTouchOutside(false);
        sys_resultdialog.show();
    }

    /**
     * 跳转到上一个页面
     *
     * @param resultString
     * @param bitmap
     */
    private void onResultHandler(String resultString, Bitmap bitmap) {
        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(SaoYiSaoActivity.this, "图片有误，解析失败！",
                    Toast.LENGTH_SHORT).show();
            return;
        } else {

            Toast.makeText(SaoYiSaoActivity.this, "解析中", Toast.LENGTH_SHORT)
                    .show();
            inactivityTimer.onActivity();

            saomaFinishHttpPost(resultString);
            // Toast.makeText(SaoYiSaoActivity.this, resultString,
            // Toast.LENGTH_SHORT).show();
            // showDialog(resultString);
        }
        // Intent resultIntent = new Intent();
        // Bundle bundle = new Bundle();
        // bundle.putString("result", resultString);
        // bundle.putParcelable("bitmap", bitmap);
        // resultIntent.putExtras(bundle);
        // this.setResult(RESULT_OK, resultIntent);
        // SaoYiSaoActivity.this.finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

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

}