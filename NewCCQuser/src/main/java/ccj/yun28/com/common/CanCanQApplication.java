package ccj.yun28.com.common;

import android.app.Application;
import android.content.Context;
import cn.jpush.android.api.JPushInterface;

import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;

//import com.baidu.mapapi.SDKInitializer;
//import com.fangdd.mobile.BaseImageApplication;
//import com.netcat.yilutong.mall.R;
//import com.netcat.yilutong.notify.CustomNotificationHandler;
//import com.umeng.message.PushAgent;
//import com.umeng.message.UTrack;
//import com.umeng.message.UmengMessageHandler;
//import com.umeng.message.entity.UMessage;

public class CanCanQApplication extends Application {
	// public final static String key =
	// "A3430665290BDD60286867CAD5093C28CA7496B4";
	public static Context mContext;

	// private PushAgent mPushAgent;

	 public static Context getContext() {
	        return mContext;
	    }
	 
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		// SDKInitializer.initialize(this);
		// toInitNotification();
		// if (CommonConstants.DEBUG_MODE) {
		// debug("当前环境：调试模式");
		// } else {
		// debug("当前环境：生产模式");
		// }
		
		//友盟   是否调试模式
//		MobclickAgent.setDebugMode(true);
		//友盟   场景类型设置    EScenarioType. E_UM_NORMAL　　普通统计场景类型
        MobclickAgent.setScenarioType(mContext, EScenarioType.E_UM_NORMAL);
        
		JPushInterface.setDebugMode(true);     // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);             // 初始化 JPush
	}

	// @Override
	// public boolean isDebugMode() {
	// return CommonConstants.DEBUG_MODE;
	// }
	// private void toInitNotification()
	// {
	// mPushAgent = PushAgent.getInstance(this);
	// mPushAgent.setDebugMode(true);
	//
	// UmengMessageHandler messageHandler = new UmengMessageHandler(){
	// /**
	// * 参考集成文档的1.6.3
	// * http://dev.umeng.com/push/android/integration#1_6_3
	// * */
	// @Override
	// public void dealWithCustomMessage(final Context context, final UMessage
	// msg) {
	// new Handler().post(new Runnable() {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// // 对自定义消息的处理方式，点击或者忽略
	// boolean isClickOrDismissed = true;
	// if(isClickOrDismissed) {
	// //自定义消息的点击统计
	// UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
	// } else {
	// //自定义消息的忽略统计
	// UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
	// }
	// Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
	// }
	// });
	// }
	//
	// /**
	// * 参考集成文档的1.6.4
	// * http://dev.umeng.com/push/android/integration#1_6_4
	// * */
	// @Override
	// public Notification getNotification(Context context,
	// UMessage msg) {
	// switch (msg.builder_id) {
	// case 1:
	// NotificationCompat.Builder builder = new
	// NotificationCompat.Builder(context);
	// RemoteViews myNotificationView = new
	// RemoteViews(context.getPackageName(), R.layout.notification_view);
	// myNotificationView.setTextViewText(R.id.notification_title, msg.title);
	// myNotificationView.setTextViewText(R.id.notification_text, msg.text);
	// myNotificationView.setImageViewBitmap(R.id.notification_large_icon,
	// getLargeIcon(context, msg));
	// myNotificationView.setImageViewResource(R.id.notification_small_icon,
	// getSmallIconId(context, msg));
	// builder.setContent(myNotificationView);
	// builder.setContentTitle(msg.title)
	// .setContentText(msg.text)
	// .setTicker(msg.ticker)
	// .setAutoCancel(true);
	// Notification mNotification = builder.build();
	// //由于Android
	// v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
	// mNotification.contentView = myNotificationView;
	// return mNotification;
	// default:
	// //默认为0，若填写的builder_id并不存在，也使用默认。
	// return super.getNotification(context, msg);
	// }
	// }
	// };
	// mPushAgent.setMessageHandler(messageHandler);
	//
	// /**
	// * 该Handler是在BroadcastReceiver中被调用，故
	// * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
	// * 参考集成文档的1.6.2
	// * http://dev.umeng.com/push/android/integration#1_6_2
	// * */
	// CustomNotificationHandler notificationClickHandler = new
	// CustomNotificationHandler();
	// mPushAgent.setNotificationClickHandler(notificationClickHandler);
	// }
	//
}
