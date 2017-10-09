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
import android.widget.ImageView;
import android.widget.TextView;
import ccj.yun28.com.R;

import com.lidroid.xutils.BitmapUtils;

/**
 * 搜索商品适配器
 * 
 * @author meihuali
 * 
 */
public class SearchProductlvAdapter extends BaseAdapter {

	private Context context;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();
	 private List<Map<String, String>> list;

//	private List<BBsearchBean> data;

	public SearchProductlvAdapter(Activity context) {
		this.context = context;
		 this.list = new ArrayList<Map<String, String>>();
//		this.data = new ArrayList<BBsearchBean>();

	}

	// public void NotifyList(List<Map<String, String>> list) {
	// this.list.clear();
	// this.list.addAll(list);
	// notifyDataSetChanged();
	// }

	public void NotifyList(List<Map<String, String>> list,boolean flag) {
		// TODO Auto-generated method stub
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
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	private ViewHolder vh = new ViewHolder();

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (temp.get(position) == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_search_product, null);
			vh.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			vh.tv_product_name = (TextView) convertView
					.findViewById(R.id.tv_product_name);
			vh.tv_shop_name = (TextView) convertView
					.findViewById(R.id.tv_shop_name);
			vh.tv_price = (TextView) convertView.findViewById(R.id.tv_price);

			temp.put(position, convertView);
			convertView.setTag(vh);
		} else {
			convertView = temp.get(position);
			vh = (ViewHolder) convertView.getTag();
		}
		BitmapUtils bitmapUtils = new BitmapUtils(context);
		bitmapUtils.display(vh.iv_pic, list.get(position).get("goods_image"));
		vh.tv_product_name.setText(list.get(position).get("goods_name"));
		vh.tv_shop_name.setText(list.get(position).get("store_name"));
		vh.tv_price.setText(list.get(position).get("goods_price"));
		return convertView;
	}

	static class ViewHolder {
		ImageView iv_pic;
		TextView tv_product_name;
		TextView tv_shop_name;
		TextView tv_price;
	}

}
