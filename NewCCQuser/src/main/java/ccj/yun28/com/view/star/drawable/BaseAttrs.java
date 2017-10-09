package ccj.yun28.com.view.star.drawable;

import android.content.Context;
import ccj.yun28.com.App;

public class BaseAttrs {
	public static final int INVAILD_VALUE = -1;
	
	protected Context mContext;
	
	protected BaseAttrs() {
		mContext = App.instance();
	}

}
