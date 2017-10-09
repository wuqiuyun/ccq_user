package ccj.yun28.com.neworder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.newbean.OrderDataBean;

/**
 * 待付款 - 上面部分的订单信息
 */

public class NoPayAdapter extends BaseAdapter {

	private Context mContext;
	private List<OrderDataBean> list;

	public NoPayAdapter(Context context) {
		this.mContext = context;
	}
	public NoPayAdapter(Context context,List<OrderDataBean> list) {
		this.mContext = context;
		this.list = list;
	}

	// 此方法传递数据源
	public void NoPayAdapter(List<OrderDataBean> list) {
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
		Log.d("log", ""+"");
	}

	@Override
	public int getCount() {
		return list.size() == 0 ? 3 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_no_pay, null);

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
			holder.pic = (ImageView) convertView.findViewById(R.id.iv_pic);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Map<String, String> data = list.get(position);
		//
		// holder.orderNumber.setText(data.get("order_sn"));
		// holder.storeName.setText(data.get("store_name"));
		// holder.goodsName.setText(data.get("goods_name"));
		// holder.check.setText(data.get("goods_name"));
		// holder.orderAddTime.setText(data.get("add_time"));
		// holder.orderAmount.setText(data.get("order_amount"));
		// holder.ccqVoucherNumber.setText(data.get("check_number"));
		//
		// Picasso.with(mContext).load(list.get(position).get("goods_image"))
		// .placeholder(R.drawable.xinpin).error(R.drawable.xinpin)
		// .resize(100, 100).centerCrop().into(holder.pic);

		return convertView;
	}
	
	

	private class ViewHolder {
		TextView orderNumber;// 订单编号
		TextView storeName;// 商家名称
		TextView goodsName;// 商品名称
		TextView check;// 查看
		TextView orderAddTime;// 下单时间
		TextView orderAmount;// 订单总额(折扣后 实付)
		TextView ccqVoucherNumber;// 券码
		ImageView deleteOrder;
		ImageView gotoPay;
		ImageView pic;

	}
}
