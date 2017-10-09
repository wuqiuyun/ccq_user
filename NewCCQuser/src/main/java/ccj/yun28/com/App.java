package ccj.yun28.com;

import android.app.Application;
import android.content.Context;

public class App extends Application{
	
	private static App _instance;
	public static Context mContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		_instance = this;
		
	}
	
	public static App instance() {
		return _instance;
	}

}
