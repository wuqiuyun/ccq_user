package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.AllShengShiQuBean;


/**
 * 地址 - 省- 适配器
 * 
 * @author meihuali
 */
public class ShenglvAdapter extends BaseAdapter {
	private Context context;
	// private List<Map<String, String>> list;
	private List<AllShengShiQuBean> data;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();

	public ShenglvAdapter(Activity context) {
		this.context = context;
		// this.list = new ArrayList<Map<String, String>>();
		this.data = new ArrayList<AllShengShiQuBean>();

	}

	// public void NotifyList(List<Map<String, String>> list) {
	// this.list.clear();
	// this.list.addAll(list);
	// notifyDataSetChanged();
	// }

	public void NotifyList(List<AllShengShiQuBean> data) {
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
					R.layout.item_sy_fenlei, null);
			holder.line_zhengeg = (LinearLayout) convertView.findViewById(R.id.line_zhengeg);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);

			temp.put(position, convertView);
			convertView.setTag(holder);
		} else {
			convertView = temp.get(position);
			holder = (ViewHolder) convertView.getTag();
		}
			if (position == selectItem) {
				holder.line_zhengeg.setBackgroundColor(Color.parseColor("#f2f2f2"));
			}else{
				holder.line_zhengeg.setBackgroundColor(Color.parseColor("#ffffff"));
			}

		holder.tv_name.setText(data.get(position).getArea_name());
		return convertView;
	}
	
	public void setSelectItem(int selectItem) {
		this.selectItem = selectItem;
	}

	private int selectItem = -1;

	static class ViewHolder {
		LinearLayout line_zhengeg;
		TextView tv_name;
	}

}
