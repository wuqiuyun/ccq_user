package ccj.yun28.com.mine;

import java.util.ArrayList;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.ProGoodsList;
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
 * 线上商品-去评价
 * 
 * @author meihuali
 * 
 */
public class ProDetailXSQuPingJiaActivity extends BaseActivity implements OnClickListener {

	private String oneposition;
	String content = "";
	String num = "";
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
				Toast.makeText(ProDetailXSQuPingJiaActivity.this, "当前网络不可用,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(ProDetailXSQuPingJiaActivity.this, "当前网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_ALLDINDAN_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(ProDetailXSQuPingJiaActivity.this,
							msg.obj.toString().trim(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			// 获取信息成功
			case HANDLER_TIJIAOPINGLUN_SUCCESS:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(ProDetailXSQuPingJiaActivity.this,
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
		setContentView(R.layout.activity_xsqupingjia);
		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		LinearLayout line_tijiao = (LinearLayout) findViewById(R.id.line_tijiao);
		lv = (ListView) findViewById(R.id.lv);
		cb_niming = (CheckBox) findViewById(R.id.cb_niming);

		if (getIntent() != null) {
			list = getIntent().getParcelableArrayListExtra("list");
			order_id = getIntent().getStringExtra("order_id");
		}

		XSqupingjiaAdapter xsqupingjiaAdapter = new XSqupingjiaAdapter();
		lv.setAdapter(xsqupingjiaAdapter);

		line_back.setOnClickListener(this);
		line_tijiao.setOnClickListener(this);

	}

	private class XSqupingjiaAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(ProDetailXSQuPingJiaActivity.this)
						.inflate(R.layout.item_xsqupingjia, null);

				holder.iv_pic = (ImageView) convertView
						.findViewById(R.id.iv_pic);
				holder.et_pingjia = (EditText) convertView
						.findViewById(R.id.et_pingjia);
				holder.tv_zishu = (TextView) convertView
						.findViewById(R.id.tv_zishu);
				holder.starsLayout = (StarLinearLayout) convertView
						.findViewById(R.id.starsLayout);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			starsTest(holder);
			BitmapUtils bitmapUtils = new BitmapUtils(ProDetailXSQuPingJiaActivity.this);
			bitmapUtils.display(holder.iv_pic, list.get(position).getGoods_image());

			showzishuchange(holder, position);
			return convertView;
		}

	}

	static class ViewHolder {
		EditText et_pingjia;
		ImageView iv_pic;
		TextView tv_zishu;
		StarLinearLayout starsLayout;
	}

	private void starsTest(ViewHolder holder) {
		StarLayoutParams params = new StarLayoutParams();
		params.setNormalStar(getResources().getDrawable(R.drawable.starh))
				.setSelectedStar(getResources().getDrawable(R.drawable.stard))
				.setSelectable(true).setSelectedStarNum(5).setTotalStarNum(5)
				.setStarHorizontalSpace(20);
		holder.starsLayout.setStarParams(params);
	}

	// 加载中动画
	private Dialog loadingDialog;
	private ListView lv;
	private String order_id = "";
	private CheckBox cb_niming;
	private String anonymity = "";
	private String goods_id = "";
	private ArrayList<ProGoodsList> list;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(ProDetailXSQuPingJiaActivity.this).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(
				ProDetailXSQuPingJiaActivity.this, R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(ProDetailXSQuPingJiaActivity.this,
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

	// 订单字数变化
	public void showzishuchange(final ViewHolder holder, int position) {
		// TODO Auto-generated method stub
		holder.et_pingjia.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				String pingjia = holder.et_pingjia.getText().toString().trim();
				if (500 > pingjia.length() && pingjia.length() > 0) {
					holder.tv_zishu.setText(pingjia.length() + "/500");
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			onBackPressed();
			break;
		case R.id.line_tijiao:
			if (canPost()) {
				showLoading();
				tijiaoPinjiaHttpPost();
			}
			break;

		default:
			break;
		}
	}

	// 提交评价接口
	private void tijiaoPinjiaHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(ProDetailXSQuPingJiaActivity.this);

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("member_id",
				new DButil().getMember_id(ProDetailXSQuPingJiaActivity.this));
		params.addBodyParameter("goods_id", goods_id);
		params.addBodyParameter("order_id", order_id);
		params.addBodyParameter("geval_scores", num);
		params.addBodyParameter("geval_content", content);
		params.addBodyParameter("anonymity", anonymity);
		httpUtils.send(HttpMethod.POST, JiekouUtils.XSPROTIJIAOPINGJIA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
						Log.e("ee", arg1);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						tijiaoPinjiaListInfo(arg0.result);
					}
				});
	}

	protected void tijiaoPinjiaListInfo(String result) {
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

	// 是否可以请求接口
	private boolean canPost() {
		// TODO Auto-generated method stub
		for (int i = 0; i < list.size(); i++) {
			EditText pingjia = (EditText) lv.getChildAt(i).findViewById(
					R.id.et_pingjia);
			StarLinearLayout starnum = (StarLinearLayout) lv.getChildAt(i)
					.findViewById(R.id.starsLayout);
			String shurucontent = pingjia.getText().toString().trim();
			if (TextUtils.isEmpty(shurucontent)) {
				shurucontent = "好评";
			}
			if (list.size() > 1) {

				if (i == 0) {
					content = shurucontent.replace("^", "") + "^";
					num = starnum.getLogic().getCurStarNum() + "^";
					goods_id = list.get(i).getGoods_id() + "^";
				} else if (i == list.size() - 1) {
					content = content + shurucontent.replace("^", "");
					num = num + starnum.getLogic().getCurStarNum();
					goods_id = goods_id + list.get(i).getGoods_id();
				} else {
					content = content + shurucontent.replace("^", "") + "^";
					num = num + starnum.getLogic().getCurStarNum() + "^";
					goods_id = goods_id + list.get(i).getGoods_id() + "^";

				}
			} else {

				content = content + shurucontent.replace("^", "");
				num = num + starnum.getLogic().getCurStarNum();
				goods_id = goods_id + list.get(i).getGoods_id();
			}
		}
		if (cb_niming.isChecked()) {
			anonymity = "1";
		} else {
			anonymity = "0";

		}
		return true;
	}

}
