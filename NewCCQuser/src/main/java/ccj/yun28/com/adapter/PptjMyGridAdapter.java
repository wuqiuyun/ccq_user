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
import android.widget.ImageView;
import android.widget.TextView;
import ccj.yun28.com.R;

import com.lidroid.xutils.BitmapUtils;

/**
 * 品牌推荐适配器
 * 
 * @author meihuali
 * 
 */
public class PptjMyGridAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> list;

	public PptjMyGridAdapter(Activity context) {
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
					R.layout.item_gv_pp, null);
			holder.tv_ppzq_name = (TextView) convertView
					.findViewById(R.id.tv_ppzq_name);
			holder.iv_ppzq = (ImageView) convertView.findViewById(R.id.iv_ppzq);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_ppzq_name.setText(list.get(position).get("data"));
		BitmapUtils bitmapUtils = new BitmapUtils(context);
		bitmapUtils.display(holder.iv_ppzq, list.get(position).get("image"));

		return convertView;
	}

	static class ViewHolder {
		TextView tv_ppzq_name;
		ImageView iv_ppzq;
	}

}
