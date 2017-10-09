package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.AllDinDanGoodsBean;

import com.lidroid.xutils.BitmapUtils;


/**
 * 全部订单-全部 -商品 适配器
 * 
 * @author meihuali
 */
public class AllDinDanGoodsAdapter extends BaseAdapter {
	private Context context;
	// private List<Map<String, String>> list;
	private List<AllDinDanGoodsBean> data;
	private String union_type;
	private String order_sn;
	private String order_amount;
	private String goods_amount;

	public AllDinDanGoodsAdapter(Context context) {
		this.context = context;
		// this.list = new ArrayList<Map<String, String>>();
		this.data = new ArrayList<AllDinDanGoodsBean>();
		this.union_type = "";
		this.order_sn = "";
		this.order_amount = "";
		this.goods_amount = "";

	}

	// public void NotifyList(List<Map<String, String>> list) {
	// this.list.clear();
	// this.list.addAll(list);
	// notifyDataSetChanged();
	// }
	public void NotifyList(List<AllDinDanGoodsBean> data, String union_type, String order_sn, String order_amount, String goods_amount) {
		// TODO Auto-generated method stub
		this.data.clear();
		this.data.addAll(data);
		this.union_type = union_type;
		this.order_sn = order_sn;
		this.order_amount = order_amount;
		this.goods_amount = goods_amount;
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
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_alldindan_goods, null);
			holder.iv_pro_pic = (ImageView) convertView.findViewById(R.id.iv_pro_pic);
			holder.line_haveproname = (LinearLayout) convertView.findViewById(R.id.line_haveproname);
			holder.tv_pro_name = (TextView) convertView.findViewById(R.id.tv_pro_name);
			holder.tv_des = (TextView) convertView.findViewById(R.id.tv_des);
			holder.line_xxfhmd = (LinearLayout) convertView.findViewById(R.id.line_xxfhmd);
			holder.tv_ddh = (TextView) convertView.findViewById(R.id.tv_ddh);
			holder.tv_xfje = (TextView) convertView.findViewById(R.id.tv_xfje);
			holder.tv_sfje = (TextView) convertView.findViewById(R.id.tv_sfje);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//0 普通商品 2先消费后买单 3餐餐抢套餐
		if ("0".equals(union_type)) {
			holder.line_haveproname.setVisibility(View.VISIBLE);
			holder.line_xxfhmd.setVisibility(View.GONE);
			holder.tv_des.setVisibility(View.VISIBLE);
			holder.tv_pro_name.setText(data.get(position).getGoods_name());
			holder.tv_des.setText(data.get(position).getSpec_value());
		}else if ("2".equals(union_type)) {
			holder.line_xxfhmd.setVisibility(View.VISIBLE);
			holder.line_haveproname.setVisibility(View.GONE);
			holder.tv_ddh.setText(order_sn);
			holder.tv_xfje.setText(goods_amount);
			holder.tv_sfje.setText(order_amount);
			
		}else if ("3".equals(union_type)) {
			holder.line_haveproname.setVisibility(View.VISIBLE);
			holder.line_xxfhmd.setVisibility(View.GONE);
			holder.tv_des.setVisibility(View.GONE);
			holder.tv_pro_name.setText(data.get(position).getGoods_name());
		}
		
		BitmapUtils bitmapUtils = new BitmapUtils(context);
		bitmapUtils.display(holder.iv_pro_pic, data.get(position).getImage());
		
		return convertView;
	}

	static class ViewHolder {
		ImageView iv_pro_pic;
		LinearLayout line_haveproname;
		TextView tv_pro_name;
		TextView tv_des;
		LinearLayout line_xxfhmd;
		TextView tv_ddh;
		TextView tv_xfje;
		TextView tv_sfje;
	}
	public List<AllDinDanGoodsBean> getData(){
		return data;
	}
	

}
