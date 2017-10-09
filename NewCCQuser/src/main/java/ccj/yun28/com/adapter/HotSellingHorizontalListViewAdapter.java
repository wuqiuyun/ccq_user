package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 * 新品推荐横向listview适配器
 * 
 * @author meihuali
 * 
 */
public class HotSellingHorizontalListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> list;

	public HotSellingHorizontalListViewAdapter(Context context) {
		this.context = context;
		this.list = new ArrayList<Map<String, String>>();
		mInflater = LayoutInflater.from(context);

	}

	public void NotifyList(List<Map<String, String>> list) {
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	private LayoutInflater mInflater;

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private ViewHolder vh = new ViewHolder();

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_hotsellinglv, null);
			vh.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			vh.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			vh.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.tv_name.setText(list.get(position).get("goods_name"));
		vh.tv_price.setText("¥ " + list.get(position).get("goods_price"));
		BitmapUtils bitmapUtils = new BitmapUtils(context);
		bitmapUtils.display(vh.iv_pic, list.get(position).get("image"));

		return convertView;
	}

	private static class ViewHolder {
		private ImageView iv_pic;
		private TextView tv_name;
		private TextView tv_price;
	}

}
