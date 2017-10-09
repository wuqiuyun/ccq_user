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
 * 猜我喜欢适配器
 * 
 * @author meihuali
 * 
 */
public class LikeGridAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> list;

	public LikeGridAdapter(Activity context) {
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
					R.layout.item_gv_like, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_price = (TextView) convertView
					.findViewById(R.id.tv_price);
			holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_name.setText(list.get(position).get("goods_name"));
		holder.tv_price.setText("¥ " + list.get(position).get("goods_price"));
		BitmapUtils bitmapUtils = new BitmapUtils(context);
		bitmapUtils.display(holder.iv_pic, list.get(position)
				.get("goods_image"));

		return convertView;
	}

	static class ViewHolder {
		TextView tv_name;
		TextView tv_price;
		ImageView iv_pic;
	}

}
