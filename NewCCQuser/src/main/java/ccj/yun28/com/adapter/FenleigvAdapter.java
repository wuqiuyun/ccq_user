package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.ErjiFenleiBean;

import com.lidroid.xutils.BitmapUtils;

/**
 * 首页分类适配器
 * 
 * @author meihuali
 */
public class FenleigvAdapter extends BaseAdapter {
	private Context context;
	// private List<Map<String, String>> list;
	private List<ErjiFenleiBean> class2;

	public FenleigvAdapter(Activity context) {
		this.context = context;
		// this.list = new ArrayList<Map<String, String>>();
		this.class2 = new ArrayList<ErjiFenleiBean>();

	}

	// public void NotifyList(List<Map<String, String>> list) {
	// this.list.clear();
	// this.list.addAll(list);
	// notifyDataSetChanged();
	// }

	public void NotifyList(List<ErjiFenleiBean> class2) {
		// TODO Auto-generated method stub
		this.class2.clear();
		this.class2.addAll(class2);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return class2.size();
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
					R.layout.item_sy_erjifenlei, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		BitmapUtils bitmapUtils = new BitmapUtils(context);
		bitmapUtils.display(holder.iv_pic, class2.get(position).getGc_img());
		holder.tv_name.setText(class2.get(position).getGc_name());
		return convertView;
	}

	static class ViewHolder {
		ImageView iv_pic;
		TextView tv_name;
	}

}
