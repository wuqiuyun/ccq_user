package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.AllDinDanBean;
import ccj.yun28.com.bean.ZhuAllDinDanBean;
import ccj.yun28.com.mine.CCQDinDanDetailActivity;
import ccj.yun28.com.mine.ChaKanJuanMaActivity;
import ccj.yun28.com.mine.TuiKuanDinDanDetailActivity;
import ccj.yun28.com.mine.XiaofeiMaidanDetailActivity;

/**
 * 全部订单-待使用 适配器
 * 
 * @author meihuali
 */
public class DsyDinDanAdapter extends BaseAdapter {
	private Context context;
	private List<AllDinDanBean> data;
	private ZhuAllDinDanBean allDinDanBean;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();

	private boolean flag = false;

	public DsyDinDanAdapter(Activity context) {
		this.context = context;
		this.data = new ArrayList<AllDinDanBean>();

	}

	public void NotifyList(List<AllDinDanBean> data) {
		// TODO Auto-generated method stub
		if (flag) {
			data.clear();
		}
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
					R.layout.item_dsydindan_shop, null);
			holder.tv_shopname = (TextView) convertView
					.findViewById(R.id.tv_shopname);
			holder.lv_goods = (ListView) convertView
					.findViewById(R.id.lv_goods);
			holder.tv_status = (TextView) convertView
					.findViewById(R.id.tv_status);
			// 订单信息
			holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
			holder.tv_jiage = (TextView) convertView
					.findViewById(R.id.tv_jiage);
			// 查看券码
			holder.tv_chakanjuanma = (TextView) convertView
					.findViewById(R.id.tv_chakanjuanma);

			temp.put(position, convertView);
			convertView.setTag(holder);
		} else {
			convertView = temp.get(position);
			holder = (ViewHolder) convertView.getTag();
		}

		// 0 普通商品 1套餐(废弃) 2先消费后买单 3餐餐抢套餐
		String union_type = data.get(position).getUnion_type();
		holder.tv_shopname.setText(data.get(position).getStore_name());
		String status = data.get(position).getOrder_state();
		holder.tv_jiage.setText("¥" + data.get(position).getOrder_amount());
		holder.tv_num.setText("共计" + data.get(position).getGoods_num() + "件商品");

		String order_sn = data.get(position).getOrder_sn();
		String order_amount = data.get(position).getOrder_amount();
		String goods_amount = data.get(position).getGoods_amount();
		final AllDinDanGoodsAdapter alldindangoodsadapter = new AllDinDanGoodsAdapter(
				context);
		alldindangoodsadapter.NotifyList(data.get(position).getGoods_list(),
				union_type, order_sn, order_amount, goods_amount);
		holder.lv_goods.setAdapter(alldindangoodsadapter);

		chakanjuanmaOnClick(holder, position);
		holder.lv_goods.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent;
				if ("0".equals(alldindangoodsadapter.getData().get(arg2)
						.getUnion_type())) {
					intent = new Intent(context,
							TuiKuanDinDanDetailActivity.class);
					intent.putExtra("order_id", alldindangoodsadapter.getData()
							.get(arg2).getOrder_id());
					context.startActivity(intent);
				} else if ("3".equals(alldindangoodsadapter.getData().get(arg2)
						.getUnion_type())) {
					intent = new Intent(context, CCQDinDanDetailActivity.class);
					intent.putExtra("order_id", alldindangoodsadapter.getData()
							.get(arg2).getOrder_id());
					context.startActivity(intent);
				} else if ("2".equals(alldindangoodsadapter.getData().get(arg2)
						.getUnion_type())) {
					intent = new Intent(context,
							XiaofeiMaidanDetailActivity.class);
					intent.putExtra("order_id", alldindangoodsadapter.getData()
							.get(arg2).getOrder_id());
					context.startActivity(intent);
				}
			}
		});
		return convertView;
	}

	private void chakanjuanmaOnClick(ViewHolder holder, final int position) {
		// TODO Auto-generated method stub
		holder.tv_chakanjuanma.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, ChaKanJuanMaActivity.class);
				intent.putExtra("order_id", data.get(position).getOrder_id());
				context.startActivity(intent);
			}
		});
	}

	static class ViewHolder {
		TextView tv_shopname;
		TextView tv_status;
		ListView lv_goods;
		TextView tv_num;
		TextView tv_jiage;
		TextView tv_chakanjuanma;
	}

	public List<AllDinDanBean> getData() {
		return data;
	}
}
