package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ccj.yun28.com.R;
import ccj.yun28.com.ccq.NewCcqProDetailActivity;

import com.squareup.picasso.Picasso;

/**
 * 餐餐抢首页券适配器
 * 
 * @author meihuali
 */
public class CcqQuanLvAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> list;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();
	private Map<String, String> mMap;

	public CcqQuanLvAdapter(Activity context) {
		this.context = context;
		this.list = new ArrayList<Map<String, String>>();
		this.mMap = new HashMap<String, String>();
	}

	public void NotifyList(List<Map<String, String>> list) {
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	
	public void setExtra(Map<String, String> map){
		this.mMap = map;
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
		// if (temp.get(position) == null) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_ccq_quan, null);
			holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.tv_discount = (TextView) convertView
					.findViewById(R.id.tv_discount);
			holder.tv_ccq_kucun = (TextView) convertView
					.findViewById(R.id.tv_ccq_kucun);
			holder.tv_pro_name = (TextView) convertView
					.findViewById(R.id.tv_pro_name);
			holder.tv_ccq_price = (TextView) convertView
					.findViewById(R.id.tv_ccq_price);
			holder.tv_yuan_price = (TextView) convertView
					.findViewById(R.id.tv_yuan_price);
			holder.tv_ccq_storename = (TextView) convertView
					.findViewById(R.id.tv_ccq_storename);
			holder.tv_store_address = (TextView) convertView
					.findViewById(R.id.tv_store_address);
			holder.ll_item = (LinearLayout)convertView.findViewById(R.id.ll_item);
			temp.put(position, convertView);
			convertView.setTag(holder);
		} else {
			// convertView = temp.get(position);
			holder = (ViewHolder) convertView.getTag();
		}
		
		Picasso.with(context).load(list.get(position).get("goods_image"))
				.placeholder(R.drawable.xinpin).error(R.drawable.xinpin)
				.resize(100, 100).centerCrop().into(holder.iv_pic);
		
		holder.tv_discount.setText(list.get(position).get("discount") + "折");
		holder.tv_ccq_kucun.setText("库存剩下"
				+ list.get(position).get("goods_storage") + "张");
		holder.tv_pro_name.setText(list.get(position).get("goods_name"));
		holder.tv_ccq_price
				.setText(list.get(position).get("goods_price") + "元");
		holder.tv_yuan_price.setText("商家原价："
				+ list.get(position).get("goods_marketprice") + "元");
		holder.tv_ccq_storename.setText(list.get(position).get("store_name"));
		holder.tv_store_address.setText("地址："
				+ list.get(position).get("store_address"));

		holder.ll_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,
						 NewCcqProDetailActivity.class);
						 intent.putExtra("starlat", mMap.get("latitude"));// 经度
						 intent.putExtra("starlng", mMap.get("longitude"));// 纬度
						 intent.putExtra("ccqgoods_id", list.get(position).get("goods_id"));// goods_id
						 intent.putExtra("store_id", list.get(position).get("store_id"));
						 intent.putExtra("city", mMap.get("city"));// 所在市
						 intent.putExtra("district",mMap.get("district"));// 所在区
						 context.startActivity(intent);
			}
		});
		return convertView;
	}

	static class ViewHolder {
		ImageView iv_pic;
		TextView tv_discount;
		TextView tv_ccq_kucun;
		TextView tv_pro_name;
		TextView tv_ccq_price;
		TextView tv_yuan_price;
		TextView tv_ccq_storename;
		TextView tv_store_address;
		LinearLayout ll_item;
	}

}
