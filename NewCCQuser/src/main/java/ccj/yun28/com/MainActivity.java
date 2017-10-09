package ccj.yun28.com;

import java.util.Set;

import android.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.fragment.CCQFragment;
import ccj.yun28.com.fragment.MEFragment;
import ccj.yun28.com.fragment.NearFragment;
import ccj.yun28.com.fragment.NewMEFragment;
import ccj.yun28.com.fragment.NewOrderFragment;
import ccj.yun28.com.fragment.OrderFragment;
import ccj.yun28.com.sy.sys.camera.CameraManager;
import ccj.yun28.com.systemstatus.SystemStatusManager;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.ExampleUtil;
import ccj.yun28.com.utils.SharedUtil;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import pub.devrel.easypermissions.EasyPermissions;

import com.umeng.analytics.MobclickAgent;

/**
 * 主页- 底部导航栏- 黑色版
 *
 * @author meihuali
 */
public class MainActivity extends FragmentActivity implements OnClickListener {

    private static final int RC_CAMERA_PERM = 201;
    private Fragment fragments[] = new Fragment[4];
    private DBHelper myDB;
    // 餐餐抢
    private RadioButton radio_ccq;
    // 附近
    private RadioButton radio_near;
    // 订单
    private RadioButton radio_order;
    // 我的
    private RadioButton radio_me;

    private Button first, sec, third;
    private CCQFragment ccqfragment;
    private NearFragment nearFragment;
    private NewOrderFragment orderFragment;
    private MEFragment mefragment;
    private String type;

    private long mLastTime = 0;
    private long mCurTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus();
        setContentView(R.layout.activity_main_black);
        verifyPermissionForLocation();//获取定位权限
        fragments[0] = new CCQFragment();
        fragments[1] = new NearFragment();
        fragments[2] = new NewOrderFragment();
        fragments[3] = new NewMEFragment();
        myDB = new DBHelper(this);
        // 餐餐抢
        radio_ccq = (RadioButton) findViewById(R.id.radio_ccq);
        // 附近
        radio_near = (RadioButton) findViewById(R.id.radio_near);
        // 订单
        radio_order = (RadioButton) findViewById(R.id.radio_order);
        // 我的
        radio_me = (RadioButton) findViewById(R.id.radio_me);

        registerMessageReceiver(); // used for receive msg
        init();

        String sfsetalias = SharedUtil.getStringValue(SharedCommon.SFSETALIAS,
                "no");
        if ("no".equals(sfsetalias)) {
            setAlias();
        }
        mLastTime = 0;
        radio_ccq.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastTime = mCurTime;
                        mCurTime = System.currentTimeMillis();
                        if (mCurTime - mLastTime < 500) {
                            Intent intent = new Intent();
                            intent.setAction(CCQFragment.SCROLLVIEW_TOP);
                            sendBroadcast(intent);
                        }
                        break;

                    default:
                        break;
                }
                return false;
            }
        });

        radio_ccq.setOnClickListener(this);
        radio_near.setOnClickListener(this);
        radio_order.setOnClickListener(this);
        radio_me.setOnClickListener(this);
        type = "";
        if (getIntent() != null) {
            type = getIntent().getStringExtra("type");
            if ("near".equals(type)) {
                showFragment(1);
                radio_near.setChecked(true);
            } else if ("order".equals(type)) {
                showFragment(2);
                radio_order.setChecked(true);
            } else if ("me".equals(type)) {
                showFragment(3);
                radio_me.setChecked(true);
            } else {
                radio_ccq.setChecked(true);
                showFragment(0);
            }
        } else {
            pttype = SharedUtil.getStringValue(SharedCommon.DBDH, "");
            if ("near".equals(pttype)) {
                showFragment(1);
                radio_near.setChecked(true);
            } else if ("order".equals(pttype)) {
                showFragment(2);
                radio_order.setChecked(true);
            } else if ("me".equals(pttype)) {
                showFragment(3);
                radio_me.setChecked(true);
            } else {
                radio_ccq.setChecked(true);
                showFragment(0);
            }
        }
    }

    //动态获取权限
    public void verifyPermissionForLocation() {
        String[] perms = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};//定位权限
        if (EasyPermissions.hasPermissions(MainActivity.this, perms)) {
//获的权限后需要做的
        } else {
            EasyPermissions.requestPermissions(MainActivity.this, "程序需要打开定位权限", RC_CAMERA_PERM, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 设置状态栏背景状态
     */
    private void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);
        }
        SystemStatusManager tintManager = new SystemStatusManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(0);//状态栏无背景
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radio_ccq:
                SharedUtil.saveStringValue(SharedCommon.DBDH, "ccq");
                showFragment(0);
                radio_ccq.setChecked(true);
                break;
            case R.id.radio_near:
                SharedUtil.saveStringValue(SharedCommon.DBDH, "near");
                showFragment(1);
                NearFragment near = (NearFragment) fragments[1];
                near.setIsCreateView();
                radio_near.setChecked(true);
                break;
            case R.id.radio_order:
                SharedUtil.saveStringValue(SharedCommon.DBDH, "order");
                showFragment(2);
                NewOrderFragment order = (NewOrderFragment) fragments[2];
                radio_order.setChecked(true);

                //先判断是否登录再跳转页面
            /*if (isLogin()) {
                SharedUtil.saveStringValue(SharedCommon.DBDH, "order");
				showFragment(2);
				NewOrderFragment order = (NewOrderFragment) fragments[2];
				radio_order.setChecked(true);
			} else {
				Intent intent = new Intent(MainActivity.this,
						LoginActivity.class);
				intent.putExtra("type", "order");
				startActivity(intent);
			}*/
                break;
            case R.id.radio_me:
                if (isLogin()) {
                    SharedUtil.saveStringValue(SharedCommon.DBDH, "me");
                    showFragment(3);
                    NewMEFragment me = (NewMEFragment) fragments[3];
                    me.myInfoHttpPost(MainActivity.this);
                    radio_me.setChecked(true);
                } else {
                    Intent intent = new Intent(MainActivity.this,
                            LoginActivity.class);
                    intent.putExtra("type", "me");
                    startActivity(intent);
                }
                break;
        }
    }

    private void showFragment(int i) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        for (int index = 0; index < fragments.length; index++) {
            if (i == index) {
                if (!fragments[index].isAdded()) {
                    transaction.add(R.id.DetailLayout, fragments[index]);
                }
                transaction.show(fragments[index]);
            } else {
                if (fragments[index].isAdded()) {
                    transaction.hide(fragments[index]);
                }
            }
        }
        transaction.commit();
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

    private long timeCount = 0;
    private String pttype;

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        long nowTime = System.currentTimeMillis();
        if ((nowTime - timeCount) >= 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出应用...",
                    Toast.LENGTH_SHORT).show();
            timeCount = nowTime;
        } else {
            SharedUtil.deleteValue(SharedCommon.DBDH);
            finish();
        }
    }

    // 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
    private void init() {
        JPushInterface.init(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        SharedUtil.deleteValue(SharedCommon.DBDH);
        SharedUtil.deleteValue(SharedCommon.CHECKUPDATA);
        SharedUtil.deleteValue(SharedCommon.SFSETALIAS);
        SharedUtil.deleteValue(SharedCommon.SFCHOOSECITY);
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onResume(this);

        pttype = SharedUtil.getStringValue(SharedCommon.DBDH, "");
        if ("near".equals(pttype)) {
            radio_near.setChecked(true);
        } else if ("order".equals(pttype)) {
            radio_order.setChecked(true);
        } else if ("me".equals(pttype)) {
            radio_me.setChecked(true);
        } else {
            radio_ccq.setChecked(true);
        }
        isForeground = true;
    }

    public static boolean isForeground = false;

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private static final int MSG_SET_ALIAS = 1001;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    // Log.e("ee", "Set alias in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            (String) msg.obj, null, mAliasCallback);
                    break;

                default:
                    // Log.e("ee", "Unhandled msg - " + msg.what);
            }
        }
    };

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    // logs = "Set tag and alias success";
                    // Log.i("ee", logs);
                    break;

                case 6002:
                    // logs =
                    // "Failed to set alias and tags due to timeout. Try again after 60s.";
                    // Log.i("ee", logs);
                    if (ExampleUtil.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(
                                mHandler.obtainMessage(MSG_SET_ALIAS, alias),
                                1000 * 60);
                    } else {
                        Log.i("ee", "No network");
                    }
                    break;

                default:
                    // logs = "Failed with errorCode = " + code;
                    // Log.e("ee", logs);
            }

            // ExampleUtil.showToast(logs, getApplicationContext());
        }

    };

    private void setAlias() {
        // EditText aliasEdit = (EditText) findViewById(R.id.et_alias);
        // String alias = aliasEdit.getText().toString().trim();
        String alias = "";
        if (isLogin()) {
            alias = new DButil().getMember_id(MainActivity.this);

            if (TextUtils.isEmpty(alias)) {
                Toast.makeText(MainActivity.this, R.string.error_alias_empty,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (!ExampleUtil.isValidTagAndAlias(alias)) {
                Toast.makeText(MainActivity.this, R.string.error_tag_gs_empty,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // 调用JPush API设置Alias
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
        }
    }

    // for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                setCostomMsg(showMsg.toString());
            }
        }
    }

    public void changeFragment() {
        if (isLogin()) {
            SharedUtil.saveStringValue(SharedCommon.DBDH, "me");
            showFragment(3);
            NewMEFragment me = (NewMEFragment) fragments[3];
            radio_me.setChecked(true);
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra("type", "me");
            startActivity(intent);
        }
    }

    private void setCostomMsg(String msg) {
        // if (null != msgText) {
        // msgText.setText(msg);
        // msgText.setVisibility(android.view.View.VISIBLE);
        // }
    }

}
