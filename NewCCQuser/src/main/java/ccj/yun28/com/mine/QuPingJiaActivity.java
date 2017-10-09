package ccj.yun28.com.mine;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.star.StarLayoutParams;
import ccj.yun28.com.view.star.StarLinearLayout;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;


/**
 * 去评价
 * 
 * @author meihuali
 * 
 */
public class QuPingJiaActivity extends BaseActivity implements OnClickListener,
		TextWatcher {

	private String pingjia;
	private CheckBox cb_niming;
	private String order_id;
	private BitmapUtils bitmapUtils;
	private StarLinearLayout starsLayout;
	private TextView tv_zishu;
	private EditText et_pingjia;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 错误
	private static final int HANDLER_NN_FAILURE = 1;
	// 提交评论
	private static final int HANDLER_TIJIAOPINGLUN_SUCCESS = 2;
	private static final int HANDLER_ALLDINDAN_FAILURE = 3;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(QuPingJiaActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(QuPingJiaActivity.this, "当前网络出错,请检查网络", Toast.LENGTH_SHORT)
						.show();
				break;
			case HANDLER_ALLDINDAN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(QuPingJiaActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// 获取信息成功
			case HANDLER_TIJIAOPINGLUN_SUCCESS:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(QuPingJiaActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				Intent intent = new Intent();
				setResult(101, intent);
				finish();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qupingjia);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_tijiao = (LinearLayout) findViewById(R.id.line_tijiao);
		ImageView iv_pic = (ImageView) findViewById(R.id.iv_pic);
		et_pingjia = (EditText) findViewById(R.id.et_pingjia);
		tv_zishu = (TextView) findViewById(R.id.tv_zishu);
		cb_niming = (CheckBox) findViewById(R.id.cb_niming);
		starsLayout = (StarLinearLayout) findViewById(R.id.starsLayout);
		
		if (getIntent() != null) {
			order_id = getIntent().getStringExtra("order_id");
			goods_id = getIntent().getStringExtra("goods_id");
			String pic = getIntent().getStringExtra("pic");
			bitmapUtils = new BitmapUtils(QuPingJiaActivity.this);
			bitmapUtils.display(iv_pic, pic);
		}
		
		starsTest();
		line_back.setOnClickListener(this);
		line_tijiao.setOnClickListener(this);
		et_pingjia.addTextChangedListener(this);
	}

	private void starsTest() {
		StarLayoutParams params = new StarLayoutParams();
		params.setNormalStar(getResources().getDrawable(R.drawable.starq))
				.setSelectedStar(getResources().getDrawable(R.drawable.stard))
				.setSelectable(true).setSelectedStarNum(5).setTotalStarNum(5)
				.setStarHorizontalSpace(20);
		starsLayout.setStarParams(params);
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		pingjia = et_pingjia.getText().toString().trim();
		if (500 > pingjia.length() && pingjia.length() > 0) {
			tv_zishu.setText(pingjia.length() + "/500");
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_tijiao:
			showLoading();
			tijiaoPingLunHttppost();

			break;

		default:
			break;
		}
	}

	private void tijiaoPingLunHttppost() {
		// TODO Auto-generated method stub
		
		String num = starsLayout.getLogic().getCurStarNum() + "";
		if (TextUtils.isEmpty(pingjia)) {
			pingjia = "好评";
		}
		String anonymity = "0";
		if (cb_niming.isChecked()) {
			anonymity = "1";
		}
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(QuPingJiaActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("member_id",
				new DButil().getMember_id(QuPingJiaActivity.this));
		params.addBodyParameter("goods_id", goods_id);
		params.addBodyParameter("order_id", order_id);
		params.addBodyParameter("geval_scores", num);
		params.addBodyParameter("geval_content", pingjia);
		params.addBodyParameter("anonymity", anonymity);
		httpUtils.send(HttpMethod.POST, JiekouUtils.TIJIAOPINGJIA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						tijiaoPingLunListInfo(arg0.result);

					}
				});
	}

	protected void tijiaoPingLunListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			String message = object.getString("message");
			if ("200".equals(status)) {
				handler.sendMessage(handler.obtainMessage(
						HANDLER_TIJIAOPINGLUN_SUCCESS, message));
			} else {
				handler.sendMessage(handler.obtainMessage(
						HANDLER_ALLDINDAN_FAILURE, message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 加载中动画
	private Dialog loadingDialog;
	private String goods_id;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(QuPingJiaActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				QuPingJiaActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(QuPingJiaActivity.this, R.style.mDialogStyle);
		loadingDialog.setContentView(view);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.show();
	}

	// 关闭加载dialog
	private void dissDialog() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
	}
}
