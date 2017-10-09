package ccj.yun28.com;

import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.fragment.CCQ1Fragment;
import ccj.yun28.com.fragment.GWCFragment;
import ccj.yun28.com.fragment.MEFragment;
import ccj.yun28.com.fragment.NewMEFragment;
import ccj.yun28.com.fragment.SYFragment;
import ccj.yun28.com.fragment.SignFragment;
import ccj.yun28.com.systemstatus.SystemStatusManager;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.ExampleUtil;
import ccj.yun28.com.utils.SharedUtil;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.umeng.analytics.MobclickAgent;

/**
 * 主页-底部导航栏
 * 
 * @author meihuali
 * 
 */
public class MainActivity1 extends FragmentActivity implements OnClickListener {

	private Fragment fragments[] = new Fragment[5];
	private DBHelper myDB;
	// 首页
	private RadioButton radio_sy;
	// 餐餐抢
	private RadioButton radio_ccq;
	// 签到
	private RadioButton radio_sign;
	// 购物车
	private RadioButton radio_gwc;
	// 我的
	private RadioButton radio_me;
	private Button first, sec, third;
	private CCQ1Fragment ccqfragment;
	private GWCFragment gwcfragment;
//	private MEFragment mefragment;
	private MEFragment mefragment;
	private SYFragment syFragment;
	private SignFragment signFragment;
	private String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTranslucentStatus();
		setContentView(R.layout.activity_main);

		fragments[0] = new CCQ1Fragment();
		fragments[1] = new SYFragment();
		fragments[2] = new SignFragment();
		fragments[3] = new GWCFragment();
//		fragments[4] = new MEFragment();
		fragments[4] = new NewMEFragment();
		myDB = new DBHelper(this);
		// 餐餐抢
		radio_ccq = (RadioButton) findViewById(R.id.radio_ccq);
		// 商城首页
		radio_sy = (RadioButton) findViewById(R.id.radio_sy);
		// 签到
		radio_sign = (RadioButton) findViewById(R.id.radio_sign);
		// 购物车
		radio_gwc = (RadioButton) findViewById(R.id.radio_gwc);
		// 我的
		radio_me = (RadioButton) findViewById(R.id.radio_me);

		registerMessageReceiver(); // used for receive msg
		init();

		String sfsetalias = SharedUtil.getStringValue(SharedCommon.SFSETALIAS,
				"no");
		if ("no".equals(sfsetalias)) {
			setAlias();
		}

		radio_ccq.setOnClickListener(this);
		radio_sy.setOnClickListener(this);
		radio_sign.setOnClickListener(this);
		radio_gwc.setOnClickListener(this);
		radio_me.setOnClickListener(this);
		type = "";
		if (getIntent() != null) {
			type = getIntent().getStringExtra("type");
			if ("sign".equals(type)) {
				showFragment(2);
				radio_sign.setChecked(true);
			} else if ("gwc".equals(type)) {
				showFragment(3);
				radio_gwc.setChecked(true);
			} else if ("me".equals(type)) {
				showFragment(4);
				radio_me.setChecked(true);
			} else {
				radio_ccq.setChecked(true);
				showFragment(0);
			}
		} else {
			pttype = SharedUtil.getStringValue(SharedCommon.DBDH, "");
			if ("sign".equals(pttype)) {
				showFragment(2);
				radio_sign.setChecked(true);
			} else if ("gwc".equals(pttype)) {
				showFragment(3);
				radio_gwc.setChecked(true);
			} else if ("me".equals(pttype)) {
				showFragment(4);
				radio_me.setChecked(true);
			} else {
				radio_ccq.setChecked(true);
				showFragment(0);
			}
		}
	}
	
	/**
	 * 设置状态栏背景状态
	 */
	private void setTranslucentStatus() 
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
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
		case R.id.radio_sy:
			SharedUtil.saveStringValue(SharedCommon.DBDH, "sy");
			showFragment(1);
			SYFragment sy = (SYFragment) fragments[1];
			radio_sy.setChecked(true);
			break;
		case R.id.radio_sign:
			if (isLogin()) {
				SharedUtil.saveStringValue(SharedCommon.DBDH, "sign");
				showFragment(2);
				SignFragment sign = (SignFragment) fragments[2];
				sign.signHttpPost(MainActivity1.this);
				radio_sign.setChecked(true);
			} else {
				Intent intent = new Intent(MainActivity1.this,
						LoginActivity.class);
				intent.putExtra("type", "sign");
				startActivity(intent);
			}
			break;
		case R.id.radio_gwc:
			if (isLogin()) {
				SharedUtil.saveStringValue(SharedCommon.DBDH, "gwc");
				showFragment(3);
				GWCFragment gwc = (GWCFragment) fragments[3];
				gwc.gwcListHttpPost(MainActivity1.this);
				radio_gwc.setChecked(true);
			} else {
				Intent intent = new Intent(MainActivity1.this,
						LoginActivity.class);
				intent.putExtra("type", "gwc");
				startActivity(intent);
			}
			break;
		case R.id.radio_me:
			if (isLogin()) {
				SharedUtil.saveStringValue(SharedCommon.DBDH, "me");
				showFragment(4);
				NewMEFragment me = (NewMEFragment) fragments[4];
				me.myInfoHttpPost(MainActivity1.this);
				radio_me.setChecked(true);
			} else {
				Intent intent = new Intent(MainActivity1.this,
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
			Toast.makeText(MainActivity1.this, "再按一次退出应用...",
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
		if ("sign".equals(pttype)) {
			radio_sign.setChecked(true);
		} else if ("sy".equals(pttype)) {
			radio_sy.setChecked(true);
		} else if ("gwc".equals(pttype)) {
			radio_gwc.setChecked(true);
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
			alias = new DButil().getMember_id(MainActivity1.this);

			if (TextUtils.isEmpty(alias)) {
				Toast.makeText(MainActivity1.this, R.string.error_alias_empty,
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (!ExampleUtil.isValidTagAndAlias(alias)) {
				Toast.makeText(MainActivity1.this, R.string.error_tag_gs_empty,
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
			showFragment(4);
			NewMEFragment me = (NewMEFragment) fragments[4];
			radio_me.setChecked(true);
		} else {
			Intent intent = new Intent(MainActivity1.this, LoginActivity.class);
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
