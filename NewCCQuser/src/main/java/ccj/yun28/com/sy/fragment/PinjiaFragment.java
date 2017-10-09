package ccj.yun28.com.sy.fragment;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.ZhuProductpinjiaBean;
import ccj.yun28.com.sy.ProductDetailActivity;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.CircleImageView;
import ccj.yun28.com.view.star.StarLayoutParams;
import ccj.yun28.com.view.star.StarLinearLayout;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 商品详情 - 评价
 * 
 * @author meihuali
 * 
 */
public class PinjiaFragment extends Fragment {

	protected static final int HANDLER_NET_FAILURE = 0;
	// 获取评价列表成功
	private static final int HANDLER_PINJIALIST_SUCCESS = 1;
	private static final int HANDLER_PINJIALIST_FAILURE = 2;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 3;

	// 没有数据
	private TextView tv_notdata;
	private ListView lv;
	private MyAdapter adapter;
	private ZhuProductpinjiaBean productpinjialist;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(getActivity(), "当前网络不可用,请检查网络", Toast.LENGTH_SHORT)
						.show();
				break;
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(getActivity(), "当前网络出错,请检查网络", Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_PINJIALIST_SUCCESS:
				dissDialog();
				tv_notdata.setVisibility(View.GONE);
				lv.setVisibility(View.VISIBLE);
				adapter = new MyAdapter();
				lv.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				break;
			case HANDLER_PINJIALIST_FAILURE:
				dissDialog();
				tv_notdata.setVisibility(View.VISIBLE);
				lv.setVisibility(View.GONE);
				if (msg.obj != null) {
					Toast.makeText(getActivity(), msg.obj.toString().trim(),
							Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_pinjia, null);
		lv = (ListView) view.findViewById(R.id.lv);
		tv_notdata = (TextView) view.findViewById(R.id.tv_notdata);
		RadioButton radio_pj = (RadioButton) getActivity().findViewById(
				R.id.radio_pj);
		radio_pj.setChecked(true);
		showLoading();
		String goods_id = ((ProductDetailActivity) getActivity()).getGoods_id();
		allPinJiaListHttpPost(goods_id);
		return view;
	}

	// 商品评价接口请求
	private void allPinJiaListHttpPost(String goods_id) {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(getActivity());

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("goods_id", goods_id);
//		params.addBodyParameter("goods_id", "114085");
		httpUtils.send(HttpMethod.POST, JiekouUtils.ALLPINJIALIST, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e("ee", "网络错误:" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						allPinJiaListInfo(arg0.result);
					}
				});
	}

	// 商品评价返回数据解析
	protected void allPinJiaListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				Gson gson = new Gson();
				productpinjialist = gson.fromJson(result,
						ZhuProductpinjiaBean.class);
				handler.sendEmptyMessage(HANDLER_PINJIALIST_SUCCESS);
			} else {
				String data = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_PINJIALIST_FAILURE, data));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return productpinjialist.getData().size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (temp.get(position) == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_product_pinjia, null);
				holder.img_circle = (CircleImageView) convertView
						.findViewById(R.id.img_circle);
				holder.tv_user_name = (TextView) convertView
						.findViewById(R.id.tv_user_name);
				holder.tv_pl = (TextView) convertView
						.findViewById(R.id.tv_pinjiadetail);
				holder.starsLayout = (StarLinearLayout) convertView
						.findViewById(R.id.starsLayout);
				holder.tv_time = (TextView) convertView
						.findViewById(R.id.tv_time);
				temp.put(position, convertView);
				convertView.setTag(holder);
			} else {
				convertView = temp.get(position);
				holder = (ViewHolder) convertView.getTag();
			}
			String snum	= productpinjialist.getData().get(position).getGeval_scores();
			int num = 1;
			if ( "0.5".equals(snum) || "1".equals(snum)) {
				num = 1;
			}else if ("1.5".equals(snum) || "2".equals(snum)) {
				num = 2;
			}else if ("2.5".equals(snum) || "3".equals(snum)) {
				num = 3;
			}else if ("3.5".equals(snum) || "4".equals(snum)) {
				num = 4;
			}else if ("0".equals(snum) ||"4.5".equals(snum) || "5".equals(snum)) {
				num = 5;
			}
			starsTest(holder,num);
			BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
			bitmapUtils.display(holder.img_circle, productpinjialist.getData()
					.get(position).getAvator());
			holder.tv_user_name.setText(productpinjialist.getData()
					.get(position).getGeval_frommembername());
			holder.tv_pl.setText(productpinjialist.getData().get(position)
					.getGeval_content());
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ssE", Locale.getDefault());
			// 当前时间对象
			Date curDate = new Date((Integer.parseInt(productpinjialist
					.getData().get(position).getGeval_addtime())) * 1000L);
			String defaultTimeZoneID = TimeZone.getDefault().getID();// America/New_York
			// 在格式化日期前使用一个新的时区
			String newTimeZoneID = "Asia/Shanghai"; // Asia/Shanghai
			format.setTimeZone(TimeZone.getTimeZone(newTimeZoneID));
			holder.tv_time.setText(format.format(curDate));

			return convertView;
		}

	}

	static class ViewHolder {
		CircleImageView img_circle;
		TextView tv_user_name;
		TextView tv_pl;
		StarLinearLayout starsLayout;
		TextView tv_time;
	}
	
	private void starsTest(ViewHolder holder, int num) {
		StarLayoutParams params = new StarLayoutParams();
		params.setNormalStar(getResources().getDrawable(R.drawable.starh))
				.setSelectedStar(getResources().getDrawable(R.drawable.stard))
				.setSelectable(false).setSelectedStarNum(num).setTotalStarNum(5)
				.setStarHorizontalSpace(15);
		holder.starsLayout.setStarParams(params);
	}

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(getActivity(), R.style.mDialogStyle);
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
