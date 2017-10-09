package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.TuiKuanShouHouBean;



/**
 * 退款售后 - 适配器
 * 
 * @author meihuali
 */
public class TuiKuanShouHouDinDanAdapter extends BaseAdapter {
	private Context context;
	private List<TuiKuanShouHouBean> data;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();

	public TuiKuanShouHouDinDanAdapter(Activity context) {
		this.context = context;
		this.data = new ArrayList<TuiKuanShouHouBean>();

	}

	public void NotifyList(List<TuiKuanShouHouBean> data) {
		// TODO Auto-generated method stub
		this.data.clear();
		this.data.addAll(data);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
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
		if (temp.get(position) == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_tuikuanshouhou, null);
			holder.tv_shopname = (TextView) convertView
					.findViewById(R.id.tv_shopname);
			holder.tv_status = (TextView) convertView
					.findViewById(R.id.tv_status);
			holder.lv_goods = (ListView) convertView
					.findViewById(R.id.lv_goods);
			//订单信息
			holder.line_orderinfo = (LinearLayout) convertView
					.findViewById(R.id.line_orderinfo);
			holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
			holder.tv_jiage = (TextView) convertView
					.findViewById(R.id.tv_jiage);

			temp.put(position, convertView);
			convertView.setTag(holder);
		} else {
			convertView = temp.get(position);
			holder = (ViewHolder) convertView.getTag();
		}
		// 0 普通商品 1套餐(废弃) 2先消费后买单 3餐餐抢套餐
		String union_type = data.get(position).getUnion_type();
		holder.tv_shopname.setText(data.get(position).getStore_name());
		//0 未申请 1 退款/货中 3 已退款/货
		String xsstatus = "";
		String status = data.get(position).getRefund_state();
		if ("1".equals(status)) {
			xsstatus = "退款/货中";
			
		}else if ("3".equals(status)) {
			xsstatus = "已退款/货";
		}
		holder.tv_status.setText(xsstatus);
		holder.tv_jiage.setText("¥" + data.get(position).getOrder_amount());
		holder.tv_num.setText("共计" + data.get(position).getGoods_all_num() + "件商品");

		final TuiKuanAdapter tuikuanAdapter = new TuiKuanAdapter(
				context);
		tuikuanAdapter.NotifyList(data.get(position).getGoods_list(),
				union_type);

		holder.lv_goods.setAdapter(tuikuanAdapter);
		return convertView;
	}
	
	static class ViewHolder {
		TextView tv_shopname;
		TextView tv_status;
		ListView lv_goods;
		LinearLayout line_orderinfo;
		TextView tv_num;
		TextView tv_jiage;
	}
}
