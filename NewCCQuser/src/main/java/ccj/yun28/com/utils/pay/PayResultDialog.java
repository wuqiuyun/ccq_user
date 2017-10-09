package ccj.yun28.com.utils.pay;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import ccj.yun28.com.R;


/**
 * Created by Administrator on 2015/12/10.
 */
public abstract class PayResultDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private TextView tv_info;
    private TextView tv_sure;

    public PayResultDialog(Context context) {
        super(context, R.style.MyDialogStyle);
        this.mContext = context;
        setContentView(R.layout.dialog_pay_result);
        initView();
    }

    private void initView() {
        tv_info = (TextView) findViewById(R.id.tv_dialog_pay_result_hint_info);
        tv_sure = (TextView) findViewById(R.id.tv_dialog_pay_result_sure);
        tv_sure.setOnClickListener(this);
    }

    public void setHintInfo(String hintInfo) {
        if (!TextUtils.isEmpty(hintInfo))
            tv_info.setText(hintInfo);
    }

    public void setTv_sure(int textColor) {
        tv_sure.setTextColor(textColor);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        sureListener();
    }

    public abstract void sureListener();
}
