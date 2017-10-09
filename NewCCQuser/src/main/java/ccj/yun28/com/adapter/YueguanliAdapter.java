package ccj.yun28.com.adapter;

import java.util.ArrayList;
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
 * 余额管理适配器
 * 
 * @author meihuali
 */
public class YueguanliAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> list;

	public YueguanliAdapter(Activity context) {
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
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_yue_yunbi_guanli, null);

			holder.tv_littlt_title = (TextView) convertView.findViewById(R.id.tv_littlt_title);
			holder.text_one = (TextView) convertView.findViewById(R.id.text_one);
			holder.text_two = (TextView) convertView.findViewById(R.id.text_two);
			holder.text_three = (TextView) convertView.findViewById(R.id.text_three);
			holder.text_four = (TextView) convertView.findViewById(R.id.text_four);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		return convertView;
	}



	static class ViewHolder {
		TextView tv_littlt_title;
		TextView text_one;
		TextView text_two;
		TextView text_three;
		TextView text_four;
	}

}
