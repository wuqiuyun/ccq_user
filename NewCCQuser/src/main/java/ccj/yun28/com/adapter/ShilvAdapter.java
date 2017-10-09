package ccj.yun28.com.adapter;

import java.util.ArrayList;
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
import ccj.yun28.com.bean.AllShiQuBean;


/**
 * 地址 - 市- 适配器
 * 
 * @author meihuali
 */
public class ShilvAdapter extends BaseAdapter {
	private Context context;
	// private List<Map<String, String>> list;
	private List<AllShiQuBean> data;

	public ShilvAdapter(Activity context) {
		this.context = context;
		// this.list = new ArrayList<Map<String, String>>();
		this.data = new ArrayList<AllShiQuBean>();

	}

	// public void NotifyList(List<Map<String, String>> list) {
	// this.list.clear();
	// this.list.addAll(list);
	// notifyDataSetChanged();
	// }

	public void NotifyList(List<AllShiQuBean> data) {
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
			holder.line_zhengeg = (LinearLayout) convertView.findViewById(R.id.line_zhengeg);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);

			convertView.setTag(holder);
		} else {
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

	static class ViewHolder {
		LinearLayout line_zhengeg;
		TextView tv_name;
	}
	
	public void setSelectItem(int selectItem) {
		this.selectItem = selectItem;
	}

	private int selectItem = -1;


}
