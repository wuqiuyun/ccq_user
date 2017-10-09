package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.AllQuBean;



/**
 * 地址 - 区- 适配器
 * 
 * @author meihuali
 */
public class QulvAdapter extends BaseAdapter {
	private Context context;
	// private List<Map<String, String>> list;
	private List<AllQuBean> data;

	public QulvAdapter(Activity context) {
		this.context = context;
		// this.list = new ArrayList<Map<String, String>>();
		this.data = new ArrayList<AllQuBean>();

	}

	// public void NotifyList(List<Map<String, String>> list) {
	// this.list.clear();
	// this.list.addAll(list);
	// notifyDataSetChanged();
	// }

	public void NotifyList(List<AllQuBean> data) {
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
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_sy_fenlei, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_name.setText(data.get(position).getArea_name());
		return convertView;
	}

	static class ViewHolder {
		TextView tv_name;
	}

}
