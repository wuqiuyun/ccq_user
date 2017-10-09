package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.CcqJuanLvAdapter.ViewHolder;
import ccj.yun28.com.ccq.CcqProDetailActivity;
import ccj.yun28.com.ccq.NewCcqProDetailActivity;
import ccj.yun28.com.mine.VIPhuiyuanActivity;
import ccj.yun28.com.sy.BuyZKJActivity;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.StringUtil;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.star.StarLayoutParams;
import ccj.yun28.com.view.star.StarLinearLayout;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

/**
 * 餐餐抢首页券适配器
 * 
 * @author meihuali
 */
public class CcqQuanSYLvAdapter extends BaseAdapter {
	private Activity context;
	private String latitude;
	private String longitude;
	private List<Map<String, String>> list;
	private Map<String, String> cityInfo;
	// private HashMap<Integer, View> temp = new HashMap<Integer, View>();

	private Utils utils;
	private String[] verstring;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取是否vip成功
	private static final int HANDLER_IS_VIP_SUCCESS = 3;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				Toast.makeText(context, "当前网络不可用,请检查网络", Toast.LENGTH_SHORT)
						.show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				Toast.makeText(context, "当前网络出错,请检查网络", Toast.LENGTH_SHORT)
						.show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				Toast.makeText(context, msg.obj.toString().trim(),
						Toast.LENGTH_SHORT).show();
				break;
			// 获取是否vip成功
			case HANDLER_IS_VIP_SUCCESS:
				if ("1".equals(is_vip)) {
					Intent intent = new Intent(context, BuyZKJActivity.class);
					intent.putExtra("goods_id", goods_id);

					/*
					 * intent.putExtra("starlat",
					 * list.get(position).get("latitude"));//商家经度
					 * intent.putExtra("starlng",
					 * list.get(position).get("longitude"));//商家纬度
					 * intent.putExtra("mylat", latitude);//当前定位经度
					 * intent.putExtra("mylng", longitude);//当前定位纬度
					 * intent.putExtra("ccqgoods_id",
					 * list.get(position).get("goods_id"));
					 * intent.putExtra("store_id",
					 * list.get(position).get("store_id"));
					 * intent.putExtra("city", cityInfo.size() > 0 ?
					 * cityInfo.get(0) .get("city") : "");
					 * intent.putExtra("district", cityInfo.size() > 0 ?
					 * cityInfo.get(0).get("district") : "");
					 */

					context.startActivity(intent);
				} else {
					Toast.makeText(context, "VIP才可以购买", Toast.LENGTH_SHORT)
							.show();
					Intent intent = new Intent(context,
							VIPhuiyuanActivity.class);
					context.startActivity(intent);
				}
				break;
			default:
				break;
			}
		};
	};
	private String is_vip;
	private String goods_id;

	public CcqQuanSYLvAdapter(Activity context) {
		this.context = context;
		this.list = new ArrayList<Map<String, String>>();
		// this.cityInfo = new ArrayList<Map<String, String>>();
		this.cityInfo = new HashMap<String, String>();
		this.latitude = latitude;
		this.longitude = longitude;

	}

	public void NotifyList(List<Map<String, String>> list, String longitude,
			String latitude) {
		// temp.clear();
		this.list.clear();
		this.list.addAll(list);
		this.latitude = latitude;
		this.longitude = longitude;
		notifyDataSetChanged();
	}

	public void setCityInfo(Map<String, String> cityInfo) {
		this.cityInfo.clear();
		// this.cityInfo.addAll(cityInfo);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_ccq_sy_quan, null);
			holder.real_pic = (RelativeLayout) convertView
					.findViewById(R.id.real_pic);
			holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.tv_discount = (TextView) convertView
					.findViewById(R.id.tv_discount);
			holder.tv_pro_name = (TextView) convertView
					.findViewById(R.id.tv_pro_name);
			holder.starsLayout = (StarLinearLayout) convertView
					.findViewById(R.id.starsLayout);
			holder.tv_ccq_price = (TextView) convertView
					.findViewById(R.id.tv_ccq_price);
			holder.tv_yuan_price = (TextView) convertView
					.findViewById(R.id.tv_yuan_price);
			holder.tv_ccq_sale = (TextView) convertView
					.findViewById(R.id.tv_ccq_sale);
			holder.tv_ccq_liulan = (TextView) convertView
					.findViewById(R.id.tv_ccq_liulan);
			holder.tv_ccq_shuoming = (TextView) convertView
					.findViewById(R.id.tv_ccq_shuoming);
			holder.tv_store_address = (TextView) convertView
					.findViewById(R.id.tv_store_address);
			holder.tv_ccq_juli = (TextView) convertView
					.findViewById(R.id.tv_ccq_juli);
			holder.iv_ccq_qiang = (ImageView) convertView
					.findViewById(R.id.iv_ccq_qiang);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		utils = new Utils();
		int[] info = utils.getWindowInfo(context);
		// 获取当前控件的布局对象
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		// 获取当前控件的布局对象
		params.height = info[0] / 15 * 7;// 设置当前控件布局的高度
		holder.real_pic.setLayoutParams(params);

		Picasso.with(context).load(list.get(position).get("goods_image"))
				.placeholder(R.drawable.xinpin).error(R.drawable.xinpin)
				.resize(350, 180).into(holder.iv_pic);

		String snum = list.get(position).get("evaluation_good_star");
		int num = 1;
		if ("0.5".equals(snum) || "1".equals(snum)) {
			num = 1;
		} else if ("1.5".equals(snum) || "2".equals(snum)) {
			num = 2;
		} else if ("2.5".equals(snum) || "3".equals(snum)) {
			num = 3;
		} else if ("3.5".equals(snum) || "4".equals(snum)) {
			num = 4;
		} else if ("0".equals(snum) || "4.5".equals(snum) || "5".equals(snum)) {
			num = 5;
		}
		starsTest(holder, num);

		holder.tv_discount.setText(list.get(position).get("discount") + "折");

		holder.tv_pro_name.setText(list.get(position).get("goods_name").trim());
		holder.tv_ccq_price
				.setText(list.get(position).get("goods_price") + "元");
		holder.tv_yuan_price.setText("商家原价："
				+ list.get(position).get("goods_marketprice") + "元");
		// holder.tv_yuan_price.getPaint().setFlags(
		// Paint.STRIKE_THRU_TEXT_FLAG); // 中间加横线
		holder.tv_ccq_sale.setText(list.get(position).get("goods_salenum"));
		holder.tv_ccq_liulan.setText(list.get(position).get("goods_click"));
		holder.tv_ccq_shuoming.setText(list.get(position).get("remark"));
		holder.tv_store_address
				.setText(list.get(position).get("store_address"));
		holder.tv_ccq_juli.setText(list.get(position).get("distance_value"));

//		goods_id = list.get(position).get("goods_id");// TODO

		Log.e("log", "goods_id----->" + goods_id);

		holder.real_pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Log.e("log", "---->"+cityInfo+"  ---->"+cityInfo.size());
				/*
				 * Intent intent = new Intent(context,
				 * NewCcqProDetailActivity.class); intent.putExtra("starlat",
				 * list.get(position).get("latitude"));//商家经度
				 * intent.putExtra("starlng",
				 * list.get(position).get("longitude"));//商家纬度
				 * intent.putExtra("mylat", latitude);//当前定位经度
				 * intent.putExtra("mylng", longitude);//当前定位纬度
				 * intent.putExtra("ccqgoods_id",
				 * list.get(position).get("goods_id"));
				 * intent.putExtra("store_id",
				 * list.get(position).get("store_id")); intent.putExtra("city",
				 * cityInfo.size() > 0 ? cityInfo.get(0) .get("city") : "");
				 * intent.putExtra("district", cityInfo.size() > 0 ?
				 * cityInfo.get(0).get("district") : "");
				 * context.startActivity(intent);
				 */

				Intent intent = new Intent(context,
						NewCcqProDetailActivity.class);
				intent.putExtra("starlat", StringUtil.isEmpty(cityInfo
						.get("latitude")) ? latitude : cityInfo.get("latitude"));
				intent.putExtra(
						"starlng",
						StringUtil.isEmpty(cityInfo.get("longitude")) ? longitude
								: cityInfo.get("longitude"));
				// intent.putExtra("starlat",
				// list.get(position).get("latitude"));//商家经度
				// intent.putExtra("starlng",
				// list.get(position).get("longitude"));//商家纬度
				intent.putExtra("mylat", latitude);// 当前定位经度
				intent.putExtra("mylng", longitude);// 当前定位纬度
				intent.putExtra("ccqgoods_id",
						list.get(position).get("goods_id"));
				intent.putExtra("store_id", list.get(position).get("store_id"));
				intent.putExtra("city", cityInfo.get("city"));// 所在市
				intent.putExtra("district", cityInfo.get("district"));// 所在区
				// intent.putExtra("city", cityInfo.size() > 0 ?
				// cityInfo.get(0).get("city") : "");
				// intent.putExtra("district",cityInfo.size() > 0 ?
				// cityInfo.get(0).get("district"): "");
				context.startActivity(intent);
			}
		});
		holder.iv_ccq_qiang.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (utils.getisLogin(context)) {
					sfVIPHttpPost(list.get(position).get("goods_id"));
				} else {
					Intent intent = new Intent(context, LoginActivity.class);
					intent.putExtra("type", "ccq");
					context.startActivity(intent);
				}
			}
		});
		return convertView;
	}

	private void starsTest(ViewHolder holder, int num) {
		StarLayoutParams params = new StarLayoutParams();
		params.setNormalStar(
				context.getResources().getDrawable(R.drawable.starh))
				.setSelectedStar(
						context.getResources().getDrawable(R.drawable.star))
				.setSelectable(false).setSelectedStarNum(num)
				.setTotalStarNum(5).setStarHorizontalSpace(6);
		holder.starsLayout.setStarParams(params);
	}

	static class ViewHolder {
		RelativeLayout real_pic;
		ImageView iv_pic;
		TextView tv_discount;
		TextView tv_pro_name;
		StarLinearLayout starsLayout;
		TextView tv_ccq_price;
		TextView tv_yuan_price;
		TextView tv_ccq_sale;
		TextView tv_ccq_liulan;
		TextView tv_ccq_shuoming;
		TextView tv_store_address;
		TextView tv_ccq_juli;
		ImageView iv_ccq_qiang;
	}

	// 是不是VIP接口
	private void sfVIPHttpPost(final String goods_id) {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		verstring = utils.getVersionInfo(context);
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("token", new DButil().gettoken(context));
		params.addBodyParameter("member_id", new DButil().getMember_id(context));

		httpUtils.send(HttpMethod.POST, JiekouUtils.SFVIP, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Log.e("ee", "失败:" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						sfVIPListInfo(arg0.result,goods_id);
					}
				});
	}

	// 是不是VIP数据解析
	protected void sfVIPListInfo(String result,String id) {
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONObject ob = object.getJSONObject("data");
				is_vip = ob.getString("is_vip");
				goods_id=id;//TODO
				handler.sendEmptyMessage(HANDLER_IS_VIP_SUCCESS);
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETINFO_FAILURE, message));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

}
