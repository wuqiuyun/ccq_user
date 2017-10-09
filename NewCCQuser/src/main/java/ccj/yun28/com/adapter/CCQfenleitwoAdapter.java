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
import android.widget.TextView;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.ccq.FenleiXiajibean;

/**
 * 首页分类适配器
 * 
 * @author meihuali
 */
public class CCQfenleitwoAdapter extends BaseAdapter {
	private Context context;
	// private List<Map<String, String>> list;
	private List<FenleiXiajibean> class2;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();

	public CCQfenleitwoAdapter(Activity context) {
		this.context = context;
		// this.list = new ArrayList<Map<String, String>>();
		this.class2 = new ArrayList<FenleiXiajibean>();

	}

	// public void NotifyList(List<Map<String, String>> list) {
	// this.list.clear();
	// this.list.addAll(list);
	// notifyDataSetChanged();
	// }

	public void NotifyList(List<FenleiXiajibean> class2) {
		this.class2.clear();
		this.class2.addAll(class2);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return class2.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (temp.get(position) == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_ccq_fenleitwo, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);

			temp.put(position, convertView);
			convertView.setTag(holder);
		} else {
			convertView = temp.get(position);
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_name.setText(class2.get(position).getGc_name());
		return convertView;
	}

	static class ViewHolder {
		TextView tv_name;
	}

}
