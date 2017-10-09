package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ccj.yun28.com.R;

/**
 * 餐餐抢(字符串)适配器
 * 
 * @author meihuali
 */
public class CCQStringAdapter extends BaseAdapter {
	private Context context;
	private String[] list;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();

	public CCQStringAdapter(Activity context) {
		this.context = context;
		this.list = new String[]{};

	}

	public void NotifyList(String[] list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.length;
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
					R.layout.item_ccq_city, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);

			temp.put(position, convertView);
			convertView.setTag(holder);
		} else {
			convertView = temp.get(position);
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_name.setText(list[position]);
		return convertView;
	}

	static class ViewHolder {
		TextView tv_name;
	}

}
