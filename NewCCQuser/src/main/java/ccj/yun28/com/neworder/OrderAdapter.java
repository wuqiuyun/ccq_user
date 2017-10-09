package ccj.yun28.com.neworder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.newbean.OrderDataBean;
import ccj.yun28.com.ccq.NewCcqProDetailActivity;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.listener.OnDoubleDialogListener;
import ccj.yun28.com.mine.AllDinDanActivtiy;
import ccj.yun28.com.mine.ChaKanJuanMaActivity;
import ccj.yun28.com.mine.Mine_Dindan_TianxieDindanActivity;
import ccj.yun28.com.mine.QuPingJiaActivity;
import ccj.yun28.com.sy.BuyZKJActivity;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.DialogUtil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.StringUtil;
import ccj.yun28.com.utils.Utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.squareup.picasso.Picasso;

/**
 * 待付款 - 上面的订单信息
 */

public class OrderAdapter extends BaseAdapter {

	private List<OrderDataBean> list;
	private Context mContext;
	private int type = -1;
	private OnRefreshListener mListener;
	private int fType = 1;

	public OrderAdapter(Context context) {
		this.mContext = context;
		this.list = new ArrayList<OrderDataBean>();
	}

	// 此方法传递数据源
	public void NoPayAdapter(List<OrderDataBean> list) {
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}

	// 判断是哪个fragment使用适配器
	public void setFragmentType(int type) {
		this.fType = type;
	}

	public void setOnRefreshListener(OnRefreshListener listener) {
		this.mListener = listener;
	}

	public interface OnRefreshListener {
		void onRefresh();// 刷新

		void onAllOrder();// 查看全部
	}

	// type:4 查看全部
	public void setType(int mType) {
		this.type = mType;
	}

	@Override
	public int getCount() {
		if (type == 4) {
			return list.size();
		} else {
			return list.size() > 3 ? 3 : list.size();
		}

	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_order, null);

			holder.orderNumber = (TextView) convertView
					.findViewById(R.id.tv_order_number);
			holder.storeName = (TextView) convertView
					.findViewById(R.id.tv_store_name);
			holder.goodsName = (TextView) convertView
					.findViewById(R.id.tv_goods_name);
			holder.check = (TextView) convertView.findViewById(R.id.tv_check);
			holder.orderAddTime = (TextView) convertView
					.findViewById(R.id.tv_order_add_time);
			holder.orderAmount = (TextView) convertView
					.findViewById(R.id.tv_order_amount);
			holder.ccqVoucherNumber = (TextView) convertView
					.findViewById(R.id.tv_ccq_voucher_number);
			holder.deleteOrder = (ImageView) convertView
					.findViewById(R.id.iv_delete_order);
			holder.gotoPay = (ImageView) convertView
					.findViewById(R.id.iv_goto_pay);
			holder.iv_again = (ImageView) convertView
					.findViewById(R.id.iv_again);
			holder.iv_feedback = (ImageView) convertView
					.findViewById(R.id.iv_feedback);
			holder.iv_chakanwaiting = (ImageView) convertView
					.findViewById(R.id.iv_chakanwaiting);
			holder.iv_order_pic = (ImageView) convertView
					.findViewById(R.id.iv_order_pic);

			holder.all_order = (LinearLayout) convertView
					.findViewById(R.id.all_order);
			holder.ll_all_order = (LinearLayout) convertView
					.findViewById(R.id.ll_all_order);
			holder.ll_order = (LinearLayout) convertView
					.findViewById(R.id.ll_order);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.clear(holder);

		final OrderDataBean bean = list.get(position);

		holder.orderNumber.setText("订单号: " + bean.getOrder_sn());
		holder.storeName.setText(bean.getStore_name());
		holder.goodsName.setText(bean.getGoods_name());

		if (bean.getState() != null) {
			holder.check.setText(bean.getState().getOrder_state_name());
		}

		Long ltime=Long.parseLong(bean.getAdd_time())*1000;
		String time = getDateTimeFromMillisecond(ltime);

//		String addtime = bean.getAdd_time();
//		long currenttime = System.currentTimeMillis();
//		Log.e("log", "下单时间:" + addtime + "   当前时间：" + currenttime+"  下单时间："+time);

		holder.orderAddTime.setText("下单时间: " + time);
		holder.orderAmount.setText("订单金额: " + bean.getOrder_amount() + "元");
		holder.ccqVoucherNumber.setText("餐餐抢券码: " + bean.getCheck_number());

		Picasso.with(mContext).load(bean.getGoods_image())
				.placeholder(R.drawable.xinpin).error(R.drawable.xinpin)
				.resize(100, 100).centerCrop().into(holder.iv_order_pic);
		if (bean.getState() != null) {
			if (bean.getState().getOrder_del().equals("1")) {// 删除订单
				holder.deleteOrder.setVisibility(View.VISIBLE);
			}
			if (bean.getState().getOrder_pay().equals("1")) {// 去付款
				holder.gotoPay.setVisibility(View.VISIBLE);
			}
			if (bean.getState().getOrder_check().equals("1")) {// 查看
				holder.iv_chakanwaiting.setVisibility(View.VISIBLE);
			}
			if (bean.getState().getOrder_more_one().equals("1")) {// 再抢一次
				holder.iv_again.setVisibility(View.VISIBLE);
			}
			if (bean.getState().getOrder_evaluate().equals("1")) {// 去评价
				holder.iv_feedback.setVisibility(View.VISIBLE);
			}
			if ((position == getCount() - 1) && (type != 4)) {
				holder.all_order.setVisibility(View.VISIBLE);
			}
		}

		holder.ll_order.setOnClickListener(new OnClickListener() {// 点击整个item跳转到商品详情

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext,
								NewCcqProDetailActivity.class);

						intent = new Intent(mContext,
								NewCcqProDetailActivity.class);
						String starlat = SharedUtil.getStringValue(
								SharedCommon.LATITUDE, "");
						String starlng = SharedUtil.getStringValue(
								SharedCommon.LONGITUDE, "");
						String city = SharedUtil.getStringValue(
								SharedCommon.CITY, "");

						intent.putExtra("starlat", starlat);// 经度
						intent.putExtra("starlng", starlng);// 纬度
						intent.putExtra("ccqgoods_id", bean.getGoods_id());// goods_id
						intent.putExtra("store_id", bean.getStore_id());
						intent.putExtra("city", city);// 所在市
						intent.putExtra("district", "");// 所在区

						/*
						 * intent.putExtra("starlat",
						 * StringUtil.isEmpty(cityInfo .get("latitude")) ?
						 * latitude : cityInfo.get("latitude"));
						 * intent.putExtra( "starlng",
						 * StringUtil.isEmpty(cityInfo.get("longitude")) ?
						 * longitude : cityInfo.get("longitude"));
						 * intent.putExtra("mylat", latitude);// 当前定位经度
						 * intent.putExtra("mylng", longitude);// 当前定位纬度
						 * intent.putExtra("ccqgoods_id",
						 * list.get(position).get("goods_id"));
						 * intent.putExtra("store_id",
						 * list.get(position).get("store_id"));
						 * intent.putExtra("city", cityInfo.get("city"));// 所在市
						 * intent.putExtra("district",
						 * cityInfo.get("district"));// 所在区
						 */
						mContext.startActivity(intent);
					}
				});

		/*
		 * 查看全部订单
		 */
		holder.all_order.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 旧界面
				/*
				 * Intent intent = new Intent(mContext,
				 * AllDinDanActivtiy.class); mContext.startActivity(intent);
				 */
				Intent intent = new Intent(mContext, MyOrderFrament.class);
				intent.putExtra("fType", fType);
				mContext.startActivity(intent);
			}
		});
		/*
		 * 删除订单
		 */
		holder.deleteOrder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogUtil.showDoubleTextSelection(mContext,
						"客官，你真的不买了了吗？店里有好多好吃的，你真的不考虑一下么？", "下狠心", "买了吧",
						new OnDoubleDialogListener() {

							@Override
							public void onClick(View v, boolean bl) {
								if (bl) {
									// 去付款
									Intent intent = new Intent(mContext,
											BuyZKJActivity.class);
									intent.putExtra("goods_id",
											bean.getGoods_id());
									mContext.startActivity(intent);
								} else {
									shanchuHttpPost(position);// 删除订单
								}
							}
						});

				// shanchuHttpPost(position);
			}
		});
		/*
		 * 去付款
		 */
		holder.gotoPay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 旧界面
				/*
				 * Intent intent = new Intent(mContext,
				 * Mine_Dindan_TianxieDindanActivity.class);
				 * intent.putExtra("order_id", bean.getOrder_id());
				 * mContext.startActivity(intent);
				 */

				Intent intent = new Intent(mContext, BuyZKJActivity.class);
				intent.putExtra("goods_id", bean.getGoods_id());
				mContext.startActivity(intent);

			}
		});
		/*
		 * 查看
		 */
		holder.iv_chakanwaiting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ChaKanJuanMaActivity.class);
				intent.putExtra("order_id", bean.getOrder_id());
				mContext.startActivity(intent);
			}
		});

		holder.iv_again.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 再抢一次
				Intent intent = new Intent(mContext,
						NewCcqProDetailActivity.class);// TODO
				intent.putExtra("starlat", "");// 经度
				intent.putExtra("starlng", "");// 纬度
				intent.putExtra("ccqgoods_id", bean.getGoods_id());// goods_id
				intent.putExtra("store_id", !StringUtil.isEmpty(bean
						.getStore_id()) ? bean.getStore_id() : "1792");
				intent.putExtra("city", "");// 所在市
				intent.putExtra("district", "");// 所在区
				mContext.startActivity(intent);
			}
		});

		holder.iv_feedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 去评价
				Intent intent = new Intent(mContext, QuPingJiaActivity.class);
				intent.putExtra("order_id", bean.getOrder_id());
				intent.putExtra("goods_id", bean.getGoods_id());
				// intent.putExtra("goods_id",
				// !StringUtil.isEmpty(bean.getGoods_id())?bean.getGoods_id():"118096");
				intent.putExtra("pic", bean.getGoods_image());
				mContext.startActivity(intent);
			}
		});

		return convertView;
	}

	private class ViewHolder {
		LinearLayout ll_order;// 整个订单item布局，用于点击跳转到详情页
		TextView orderNumber;// 订单编号
		TextView storeName;// 商家名称
		TextView goodsName;// 商品名称
		TextView check;// 查看
		TextView orderAddTime;// 下单时间
		TextView orderAmount;// 订单总额(折扣后 实付)
		TextView ccqVoucherNumber;// 券码
		ImageView deleteOrder;// 删除订单
		ImageView gotoPay;// 去付款
		ImageView iv_again;// 再抢一次
		ImageView iv_feedback;// 去评价
		ImageView iv_chakanwaiting;// 查看
		ImageView iv_order_pic;
		LinearLayout all_order;
		LinearLayout ll_all_order;// 查看全部

		public void clear(ViewHolder holder) {
			// TODO Auto-generated method stub
			holder.all_order.setVisibility(View.GONE);
			holder.deleteOrder.setVisibility(View.GONE);
			holder.gotoPay.setVisibility(View.GONE);
			holder.iv_again.setVisibility(View.GONE);
			holder.iv_feedback.setVisibility(View.GONE);
			holder.iv_chakanwaiting.setVisibility(View.GONE);
		}
	}

	private Utils utils;
	private String[] verstring;
	private HttpUtils httpUtils;

	// 删除订单
	protected void shanchuHttpPost(int position) {
		// TODO Auto-generated method stub
		DialogUtil.showDialogLoading(mContext);
		utils = new Utils();
		verstring = utils.getVersionInfo(mContext);
		httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("token", new DButil().gettoken(mContext));
		params.addBodyParameter("order_id", list.get(position).getOrder_id());
		httpUtils.send(HttpMethod.POST, JiekouUtils.DELETEDINDAN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						DialogUtil.hideDialogLoading();
						Toast.makeText(mContext, "当前网络不可用,请检查网络",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						DialogUtil.hideDialogLoading();
						try {
							JSONObject object = new JSONObject(arg0.result);
							String code = object.getString("code");
							String message = object.getString("message");
							if (mListener != null) {
								mListener.onRefresh();
							}
							Toast.makeText(mContext, message,
									Toast.LENGTH_SHORT).show();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(mContext, "当前网络出错,请检查网络",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	/**
	 * 将毫秒转化成固定格式的时间 时间格式: yyyy-MM-dd HH:mm:ss
	 * 
	 * @param millisecond
	 * @return
	 */
	private String getDateTimeFromMillisecond(Long millisecond) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date date = new Date(millisecond);
		String dateStr = simpleDateFormat.format(date);
		return dateStr;
	}

}
