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
import ccj.yun28.com.bean.Fenji_dizhibean;

/**
 * 餐餐抢城市适配器
 * 
 * @author meihuali
 */
public class Near_nereaAdapter extends BaseAdapter {
	private Context context;
	private List<Fenji_dizhibean> data;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();

	public Near_nereaAdapter(Activity context) {
		this.context = context;
		this.data = new ArrayList<Fenji_dizhibean>();

	}

	public void NotifyList(List<Fenji_dizhibean> data) {
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
					R.layout.item_ccq_city, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);

			temp.put(position, convertView);
			convertView.setTag(holder);
		} else {
			convertView = temp.get(position);
			holder = (ViewHolder) convertView.getTag();
		}

		// holder.tv_name.setText(list.get(position).get("area_name"));
		holder.tv_name.setText(data.get(position).getName());
		return convertView;
	}

	static class ViewHolder {
		TextView tv_name;
	}

}
