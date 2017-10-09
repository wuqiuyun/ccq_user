package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.BundingGoodsdetailInfo;

/**
 * 餐餐抢折扣券详情
 * 
 * @author meihuali
 */
public class CcqZheKouJuanDetaillistAdapter extends BaseAdapter {
	private Context context;
	private List<BundingGoodsdetailInfo> list;

	public CcqZheKouJuanDetaillistAdapter(Context context) {
		this.context = context;
		this.list = new ArrayList<BundingGoodsdetailInfo>();

	}

	public void NotifyList(List<BundingGoodsdetailInfo> list) {
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_ccqzhekoujuandetail, null);
			holder.tv_detail_pro_name = (TextView) convertView
					.findViewById(R.id.tv_detail_pro_name);
			holder.tv_detail_num = (TextView) convertView
					.findViewById(R.id.tv_detail_num);
			holder.tv_detail_price = (TextView) convertView
					.findViewById(R.id.tv_detail_price);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
//		if (list.get(position).getGoods_union_name() != null && !"".equals(list.get(position).getGoods_union_name())) {
//			if (list.get(position).getGoods_union_name().length()> 16) {
//				holder.tv_detail_pro_name.setTextSize(14);
//				holder.tv_detail_pro_name.setText(list.get(position)
//						.getGoods_union_name());
//			}
//		}
		holder.tv_detail_pro_name.setText(list.get(position)
				.getGoods_name());
		holder.tv_detail_num.setText(list.get(position).getQuantity());
		holder.tv_detail_price.setText("¥" + list.get(position)
				.getGoods_costprice());

		return convertView;
	}

	static class ViewHolder {
		// 商品名
		TextView tv_detail_pro_name;
		// 商品数量
		TextView tv_detail_num;
		// 商品价格
		TextView tv_detail_price;
	}

}
