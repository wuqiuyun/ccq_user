package ccj.yun28.com.mine;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.CircleImageView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 实名认证
 * 
 * @author meihuali
 * 
 */
public class ShiMingRenZhengActivity extends BaseActivity implements
		OnClickListener {
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取信息成功
	private static final int HANDLER_GETINFO_SUCCESS = 3;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(ShiMingRenZhengActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(ShiMingRenZhengActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(ShiMingRenZhengActivity.this,
						msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
				break;
			// 获取信息成功
			case HANDLER_GETINFO_SUCCESS:
				dissDialog();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shimingrenzheng);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		img_circle = (CircleImageView) findViewById(R.id.img_circle);
		tv_status = (TextView) findViewById(R.id.tv_status);
		tv_true_name = (TextView) findViewById(R.id.tv_true_name);
		tv_zhengjian_type = (TextView) findViewById(R.id.tv_zhengjian_type);
		tv_zhengjian_num = (TextView) findViewById(R.id.tv_zhengjian_num);
		tv_weitongguo_info = (TextView) findViewById(R.id.tv_weitongguo_info);
		tv_chongxinrenzheng = (TextView) findViewById(R.id.tv_chongxinrenzheng);

		showLoading();
		getInfoHttpPost();

		line_back.setOnClickListener(this);
		tv_chongxinrenzheng.setOnClickListener(this);
	}

	// 获取实名认证信息
	private void getInfoHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(ShiMingRenZhengActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token",
				new DButil().gettoken(ShiMingRenZhengActivity.this));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(ShiMingRenZhengActivity.this));
		httpUtils.send(HttpMethod.POST, JiekouUtils.GETSHIMINGINFO, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e("ee", "失败:" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						getInfoListInfo(arg0.result);
					}
				});
	}

	protected void getInfoListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONObject data = object.getJSONObject("data");
				String id = data.getString("id");
				String member_id = data.getString("member_id");
				String type = data.getString("type");
				String name = data.getString("name");
				String number = data.getString("number");
				String add_time = data.getString("add_time");
				// 0审核中1审核通过2审核不通过
				String status = data.getString("status");
				String remark = data.getString("remark");
				String thumb = data.getString("thumb");
				if ("0".equals(status)) {
					tv_status.setText("信息审核中");
					tv_weitongguo_info.setVisibility(View.GONE);
					tv_chongxinrenzheng.setVisibility(View.GONE);
				} else if ("1".equals(status)) {
					tv_status.setText("您已通过实名认证");
					tv_weitongguo_info.setVisibility(View.GONE);
					tv_chongxinrenzheng.setVisibility(View.GONE);
				} else if ("2".equals(status)) {
					tv_status.setText("认证未通过");
					tv_weitongguo_info.setVisibility(View.VISIBLE);
					tv_chongxinrenzheng.setVisibility(View.VISIBLE);
					tv_weitongguo_info.setText(remark);
				}
				// 1身份证 2军官证 3其它
				if ("1".equals(type)) {
					tv_zhengjian_type.setText("身份证");
				} else if ("2".equals(type)) {
					tv_zhengjian_type.setText("军官证");
				} else if ("3".equals(type)) {
					tv_zhengjian_type.setText("其他");
				}
				tv_true_name.setText(name);
				tv_zhengjian_num.setText(number);
				BitmapUtils bitmapUtils = new BitmapUtils(
						ShiMingRenZhengActivity.this);
				bitmapUtils.display(img_circle, thumb);
				handler.sendEmptyMessage(HANDLER_GETINFO_SUCCESS);
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETINFO_FAILURE, message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.tv_chongxinrenzheng:
			Intent intent = new Intent(ShiMingRenZhengActivity.this, WeiShiMingRenZhengActivity.class);
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
	}

	// 加载中动画
	private Dialog loadingDialog;
	private TextView tv_status;
	private TextView tv_weitongguo_info;
	private TextView tv_chongxinrenzheng;
	private TextView tv_zhengjian_type;
	private TextView tv_true_name;
	private TextView tv_zhengjian_num;
	private CircleImageView img_circle;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(ShiMingRenZhengActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				ShiMingRenZhengActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(ShiMingRenZhengActivity.this,
				R.style.mDialogStyle);
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
