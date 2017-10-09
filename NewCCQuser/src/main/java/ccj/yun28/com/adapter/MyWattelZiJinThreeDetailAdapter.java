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
 * 我的钱包-资金详情适配器-冻结中
 * 
 * @author meihuali
 */
public class MyWattelZiJinThreeDetailAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> list;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();

	public MyWattelZiJinThreeDetailAdapter(Activity context) {
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
		if (temp.get(position) == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_mywattel_detail, null);
			holder.tv_biaoti = (TextView) convertView
					.findViewById(R.id.tv_biaoti);
			holder.tv_one = (TextView) convertView
					.findViewById(R.id.tv_one);
			holder.tv_two = (TextView) convertView
					.findViewById(R.id.tv_two);
			holder.tv_three = (TextView) convertView
					.findViewById(R.id.tv_three);
			holder.tv_four = (TextView) convertView.findViewById(R.id.tv_four);
			temp.put(position, convertView);
			convertView.setTag(holder);
		} else {
			convertView = temp.get(position);
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_biaoti.setText(list.get(position).get("name"));
		holder.tv_one.setText(list.get(position).get("message"));
		holder.tv_two.setText(list.get(position).get("info"));
		holder.tv_three.setText(list.get(position).get("time"));
		holder.tv_four.setText(list.get(position).get("value_name"));
		
		return convertView;
	}

	static class ViewHolder {
		TextView tv_biaoti;
		TextView tv_one;
		TextView tv_two;
		TextView tv_three;
		TextView tv_four;
	}

}
