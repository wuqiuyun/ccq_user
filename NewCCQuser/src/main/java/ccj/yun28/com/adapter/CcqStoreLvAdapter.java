package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ccj.yun28.com.R;
import ccj.yun28.com.adapter.CcqJuanLvAdapter.ViewHolder;
import ccj.yun28.com.view.star.StarLayoutParams;
import ccj.yun28.com.view.star.StarLinearLayout;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 餐餐抢首页店铺适配器
 * 
 * @author meihuali
 */
public class CcqStoreLvAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> list;
//	private HashMap<Integer, View> temp = new HashMap<Integer, View>();

	public CcqStoreLvAdapter(Activity context) {
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
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView== null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_ccq_store, null);
			holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.tv_store_name = (TextView) convertView
					.findViewById(R.id.tv_store_name);
			holder.tv_ccq_juli = (TextView) convertView
					.findViewById(R.id.tv_ccq_juli);
			holder.tv_ccq_sale = (TextView) convertView
					.findViewById(R.id.tv_ccq_sale);
			holder.tv_ccq_pingjia = (TextView) convertView
					.findViewById(R.id.tv_ccq_pingjia);
			holder.starsLayout = (StarLinearLayout) convertView
					.findViewById(R.id.starsLayout);
			holder.tv_store_address = (TextView) convertView.findViewById(R.id.tv_store_address);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		String snum = list.get(position).get("store_credit");
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

		Picasso.with(context).load(list.get(position).get("union_img")).placeholder(R.drawable.xinpin).error(R.drawable.xinpin).resize(100,100).centerCrop().into(holder.iv_pic);
		holder.tv_store_name.setText(list.get(position).get("store_name"));
		holder.tv_ccq_juli.setText(list.get(position).get("distance"));
		holder.tv_ccq_sale.setText(list.get(position).get("sales_num"));
		holder.tv_ccq_pingjia.setText(list.get(position).get("evaluate"));
		holder.tv_store_address.setText(list.get(position).get("store_address"));

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
		ImageView iv_pic;
		TextView tv_store_name;
		TextView tv_ccq_juli;
		TextView tv_ccq_sale;
		TextView tv_ccq_pingjia;
		StarLinearLayout starsLayout;
		TextView tv_store_address;
	}

}
