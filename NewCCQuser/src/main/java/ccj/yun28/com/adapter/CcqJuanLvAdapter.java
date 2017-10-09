package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ccj.yun28.com.R;

import com.squareup.picasso.Picasso;

/**
 * 餐餐抢券适配器
 * 
 * @author meihuali
 */
public class CcqJuanLvAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> list;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();

	public CcqJuanLvAdapter(Activity context) {
		this.context = context;
		this.list = new ArrayList<Map<String, String>>();

	}

	public void NotifyList(List<Map<String, String>> list) {
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
//		if (temp.get(position) == null) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_ccq_juan, null);
			holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.tv_pro_name = (TextView) convertView
					.findViewById(R.id.tv_pro_name);
			holder.tv_yuan_price = (TextView) convertView
					.findViewById(R.id.tv_yuan_price);
			holder.tv_ccq_price = (TextView) convertView
					.findViewById(R.id.tv_ccq_price);
			holder.tv_discount = (TextView) convertView
					.findViewById(R.id.tv_discount);
			holder.tv_store_name = (TextView) convertView
					.findViewById(R.id.tv_store_name);
			holder.tv_store_address = (TextView) convertView.findViewById(R.id.tv_store_address);
			temp.put(position, convertView);
			convertView.setTag(holder);
		} else {
//			convertView = temp.get(position);
			holder = (ViewHolder) convertView.getTag();
		}
//		Picasso.with(context).load(list.get(position).get("goods_image")).into(holder.iv_pic);
		Picasso.with(context).load(list.get(position).get("goods_image")).placeholder(R.drawable.xinpin).error(R.drawable.xinpin).resize(100,100).centerCrop().into(holder.iv_pic);

		holder.tv_pro_name.setText(list.get(position).get("goods_name"));
		holder.tv_yuan_price.setText("原价：" + list.get(position).get("goods_marketprice") + "元");
		holder.tv_yuan_price.getPaint().setFlags(
				Paint.STRIKE_THRU_TEXT_FLAG); // 中间加横线
		holder.tv_ccq_price.setText(list.get(position).get("goods_price") + "元");
		holder.tv_discount
				.setText(list.get(position).get("discount") + "折");
		holder.tv_store_name.setText(list.get(position).get("store_name"));
		holder.tv_store_address.setText(list.get(position).get("store_address"));

		return convertView;
	}
	static class ViewHolder {
		ImageView iv_pic;
		TextView tv_pro_name;
		TextView tv_yuan_price;
		TextView tv_ccq_price;
		TextView tv_discount;
		TextView tv_store_name;
		TextView tv_store_address;
	}
	
}
