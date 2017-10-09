package ccj.yun28.com.utils;

import ccj.yun28.com.R;
import ccj.yun28.com.listener.OnDoubleDialogListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * @Description 自定义Dialog
 * @Author wuqiuyun
 */
public class DialogUtil {

    private static Dialog loadingDialog = null;

    /**
     * @Description 显示加载状态框
     */
    public static void showDialogLoading(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.loading, null);
        ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.loading_anim);
        image.startAnimation(animation);
        loadingDialog = new Dialog(context, R.style.mDialogStyle);
        loadingDialog.setContentView(view);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
    }

    /**
     * @Description 关闭加载框
     */
    public static void hideDialogLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /**
     * @param context   上下文
     * @param prompt    提示说明
     * @param cancel    取消提示文字
     * @param determine 确定提示文字
     * @param listener  按键监听
     * @Description 双文本选择框
     */
    public static void showDoubleTextSelection(Context context, String prompt, String cancel, String determine, final OnDoubleDialogListener listener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.theme_dialog);
        builder.setCancelable(false);
        View view = UIUtils.inflate(R.layout.dialog_double_text_selection);
        TextView mTvPrompt = (TextView) view.findViewById(R.id.tv_prompt);
        Button mBtnCancel = (Button) view.findViewById(R.id.btn_cancel);
        Button mBtnDetermine = (Button) view.findViewById(R.id.btn_determine);

        final AlertDialog dialog = builder.create();

        mTvPrompt.setText(prompt);
        if (!StringUtil.isEmpty(cancel)) {
            mBtnCancel.setText(cancel);
        }
        if (!StringUtil.isEmpty(determine)) {
            mBtnDetermine.setText(determine);
        }

        mBtnDetermine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (listener != null) {
                    listener.onClick(v, true);
                }
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (listener != null) {
                    listener.onClick(v, false);
                }
            }
        });
        dialog.show();
        dialog.setContentView(view);
    }
}
