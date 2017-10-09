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

/**
 * 我的-订单- 物流消息适配器
 * 
 * @author meihuali
 * 
 */
public class LookWuLiuAdapter extends BaseAdapter {

	private Context context;
	private List<Map<String, String>> list;

	// private List<FenleiBean> data;

	public LookWuLiuAdapter(Activity context) {
		this.context = context;
		this.list = new ArrayList<Map<String, String>>();
		// this.data = new ArrayList<FenleiBean>();

	}

	public void NotifyList(List<Map<String, String>> list) {
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}

	// public void NotifyList(List<FenleiBean> data) {
	// // TODO Auto-generated method stub
	// this.data.clear();
	// this.data.addAll(data);
	// notifyDataSetChanged();
	// }

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
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_xuliu_info, null);
			holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.tv_context = (TextView) convertView
					.findViewById(R.id.tv_context);
			holder.tv_shijian = (TextView) convertView
					.findViewById(R.id.tv_shijian);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_context.setText(list.get(position).get("context"));
		holder.tv_shijian.setText(list.get(position).get("time"));

		if (list.size() == 1) {
			holder.iv_pic.setBackgroundResource(R.drawable.liang);
		}else if (list.size() > 1){
			if (position == 0) {
				holder.iv_pic.setBackgroundResource(R.drawable.liang);
			}else if (position == list.size() - 1) {
				holder.iv_pic.setBackgroundResource(R.drawable.liangk);
			}else{
				holder.iv_pic.setBackgroundResource(R.drawable.liangc);
			}
		}
		
		return convertView;
	}

	static class ViewHolder {
		ImageView iv_pic;
		TextView tv_context;
		TextView tv_shijian;
	}

}
