package ccj.yun28.com.listener;

import android.view.View;

/**
 * Dialog点击回调
 *
 * @author wuqiuyun
 */
public interface OnDoubleDialogListener {

    /**
     * @param v  当前点击的View
     * @param bl false:点击取消按钮,true:点击确定按钮
     */
    void onClick(View v, boolean bl);
}
