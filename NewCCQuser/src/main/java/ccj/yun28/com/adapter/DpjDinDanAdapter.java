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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.AllDinDanBean;
import ccj.yun28.com.bean.AllDinDanGoodsBean;
import ccj.yun28.com.bean.ZhuAllDinDanBean;
import ccj.yun28.com.mine.ChaKanJuanMaActivity;

/**
 * 全部订单-待评价 适配器
 * 
 * @author meihuali
 */
public class DpjDinDanAdapter extends BaseAdapter {
	private Context context;
	private List<AllDinDanBean> data;
	private ZhuAllDinDanBean allDinDanBean;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();

	private String union_type;
	private boolean flag = false;

	public DpjDinDanAdapter(Activity context) {
		this.context = context;
		this.data = new ArrayList<AllDinDanBean>();

	}

	public void NotifyList(List<AllDinDanBean> data) {
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
					R.layout.item_dpjdindan_shop, null);
			holder.line_mylv = (LinearLayout) convertView.findViewById(R.id.line_mylv);
			holder.tv_shopname = (TextView) convertView
					.findViewById(R.id.tv_shopname);
			holder.lv_goods = (ListView) convertView
					.findViewById(R.id.lv_goods);
			// 订单信息
			holder.line_orderinfo = (LinearLayout) convertView
					.findViewById(R.id.line_orderinfo);
			holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
			holder.tv_status = (TextView) convertView
					.findViewById(R.id.tv_status);
			holder.tv_jiage = (TextView) convertView
					.findViewById(R.id.tv_jiage);
			// 待评价
			holder.tv_chakanjuanma = (TextView) convertView
					.findViewById(R.id.tv_chakanjuanma);
			holder.tv_qupingjia = (TextView) convertView
					.findViewById(R.id.tv_qupingjia);

			temp.put(position, convertView);
			convertView.setTag(holder);
		} else {
			convertView = temp.get(position);
			holder = (ViewHolder) convertView.getTag();
		}

		// 0 普通商品 1套餐(废弃) 2先消费后买单 3餐餐抢套餐
		union_type = data.get(position).getUnion_type();
		if ("3".equals(union_type)) {
			holder.line_orderinfo.setVisibility(View.VISIBLE);
			holder.tv_chakanjuanma.setVisibility(View.VISIBLE);
			holder.tv_qupingjia.setVisibility(View.VISIBLE);
		} else if ("2".equals(union_type)) {
			holder.line_orderinfo.setVisibility(View.GONE);
			holder.tv_chakanjuanma.setVisibility(View.GONE);
			holder.tv_qupingjia.setVisibility(View.VISIBLE);
		} else if ("0".equals(union_type)) {
			holder.line_orderinfo.setVisibility(View.VISIBLE);
			holder.tv_chakanjuanma.setVisibility(View.GONE);
			holder.tv_qupingjia.setVisibility(View.VISIBLE);
		}
		holder.tv_shopname.setText(data.get(position).getStore_name());
		holder.tv_jiage.setText("¥" + data.get(position).getOrder_amount());
		holder.tv_num.setText("共计" + data.get(position).getGoods_num() + "件商品");
		holder.tv_status.setText("待评价");
		
		String order_sn = data.get(position).getOrder_sn();
		String order_amount = data.get(position).getOrder_amount();
		String goods_amount = data.get(position).getGoods_amount();
		final AllDinDanGoodsAdapter alldindangoodsadapter = new AllDinDanGoodsAdapter(
				context);
		alldindangoodsadapter.NotifyList(data.get(position).getGoods_list(),
				union_type,order_sn,order_amount,goods_amount);
		holder.lv_goods.setAdapter(alldindangoodsadapter);

		showdpjOnclick(holder, position);
		holder.lv_goods.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
//				Intent intent;
//				if ("0".equals(alldindangoodsadapter.getData().get(arg2).getUnion_type())) {
//					intent = new Intent(context, TuiKuanDinDanDetailActivity.class);
//					intent.putExtra("order_id", alldindangoodsadapter.getData().get(arg2).getOrder_id());
//					context.startActivity(intent);
//				}else if ("3".equals(alldindangoodsadapter.getData().get(arg2).getUnion_type())) {
//					intent = new Intent(context, CCQDinDanDetailActivity.class);
//					intent.putExtra("order_id", alldindangoodsadapter.getData().get(arg2).getOrder_id());
//					context.startActivity(intent);
//				}else if ("2".equals(alldindangoodsadapter.getData().get(arg2).getUnion_type())) {
//					intent = new Intent(context, XiaofeiMaidanDetailActivity.class);
//					intent.putExtra("order_id", alldindangoodsadapter.getData().get(arg2).getOrder_id());
//					context.startActivity(intent);
//				}
				String order_id = alldindangoodsadapter.getData().get(arg2).getOrder_id();
				String union_type = alldindangoodsadapter.getData().get(arg2).getUnion_type();
				if (mDinDanDateListener != null) {
					mDinDanDateListener.dinDanDate(order_id, union_type);
				}
			}
		});
		return convertView;
	}


	private void showdpjOnclick(ViewHolder holder, final int position) {
		// TODO Auto-generated method stub
		// 查看券码
		holder.tv_chakanjuanma.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, ChaKanJuanMaActivity.class);
				intent.putExtra("order_id", data.get(position).getOrder_id());
				context.startActivity(intent);
			}
		});
		// 去评价、
		holder.tv_qupingjia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Integer.parseInt(data.get(position).getGoods_num()) > 1) {
					if ("0".equals(data.get(position).getUnion_type())) {
//						Intent intent = new Intent(context,
//								XSQuPingJiaActivity.class);
//						intent.putParcelableArrayListExtra("list",
//								data.get(position).getGoods_list());
//						intent.putExtra("order_id", data.get(position)
//								.getOrder_id());
//						context.startActivity(intent);
						ArrayList<AllDinDanGoodsBean> list = data.get(position).getGoods_list();
						String order_id = data.get(position).getOrder_id();
						mDinDanqpjduoDateListener.dinDanqpjduoDate(list, order_id);
					}
				} else {
//					Intent intent = new Intent(context, QuPingJiaActivity.class);
//					intent.putExtra("order_id", data.get(position)
//							.getOrder_id());
//					intent.putExtra("goods_id", data.get(position)
//							.getGoods_list().get(0).getGoods_id());
//					intent.putExtra("pic", data.get(position).getGoods_list()
//							.get(0).getImage());
//					context.startActivity(intent);
					String order_id = data.get(position).getOrder_id();
					String goods_id = data.get(position).getGoods_list().get(0).getGoods_id();
					String pic = data.get(position).getGoods_list().get(0).getImage();
					mDinDanqpjDateListener.dinDanqpjDate(order_id, goods_id, pic);
				}
			}
		});
	}

	static class ViewHolder {
		LinearLayout line_mylv;
		TextView tv_shopname;
		TextView tv_status;
		ListView lv_goods;
		LinearLayout line_orderinfo;
		TextView tv_num;
		TextView tv_jiage;
		TextView tv_chakanjuanma;
		TextView tv_qupingjia;
	}

	public List<AllDinDanBean> getData(){
		return data;
	}

	//点击item详情页
		private DinDanDateListener mDinDanDateListener;

		public interface DinDanDateListener {
			void dinDanDate(String order_id,String union_type);
		}
		public void setDinDanDateListener(DinDanDateListener listener) {
			mDinDanDateListener = listener;
		}
		
		//订单去评价（单个）
		private DinDanqpjDateListener mDinDanqpjDateListener;
		
		public interface DinDanqpjDateListener {
			void dinDanqpjDate(String order_id,String goods_id,String pic);
		}
		public void setDinDanqpjDateListener(DinDanqpjDateListener listener) {
			mDinDanqpjDateListener = listener;
		}
		
		//订单去评价（多个）
		private DinDanqpjduoDateListener mDinDanqpjduoDateListener;
		
		public interface DinDanqpjduoDateListener {
			void dinDanqpjduoDate(ArrayList<AllDinDanGoodsBean> list, String order_id);
		}
		public void setDinDanqpjduoDateListener(DinDanqpjduoDateListener listener) {
			mDinDanqpjduoDateListener = listener;
		}
		
	
}
