package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.HashMap;
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
public class NewProductHorizontalListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> list;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();

	public NewProductHorizontalListViewAdapter(Context context) {
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

	private ViewHolder vh = new ViewHolder();

	private static class ViewHolder {
		private TextView tv_name;
		private ImageView iv_pic;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (temp.get(position) == null) {
			convertView = mInflater.inflate(R.layout.item_newproductlv, null);
			vh.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			vh.tv_name = (TextView) convertView.findViewById(R.id.tv_name);

			temp.put(position, convertView);
			convertView.setTag(vh);
		} else {
			convertView = temp.get(position);
			vh = (ViewHolder) convertView.getTag();
		}
		vh.tv_name.setText("¥ " + list.get(position).get("goods_price"));
		BitmapUtils bitmapUtils = new BitmapUtils(context);
		bitmapUtils.display(vh.iv_pic, list.get(position).get("image"));
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.xinpin);
		return convertView;
	}
}
