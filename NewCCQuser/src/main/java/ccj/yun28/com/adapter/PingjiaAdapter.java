package ccj.yun28.com.adapter;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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
 * 评价适配器
 * 
 * @author meihuali
 * 
 */
public class PingjiaAdapter extends BaseAdapter {

	private Context context;
	private List<Map<String, String>> list;

	// private List<FenleiBean> data;

	public PingjiaAdapter(Activity context) {
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
					R.layout.item_pingjia, null);
			holder.tv_detail = (TextView) convertView
					.findViewById(R.id.tv_detail);
			holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.tv_status = (TextView) convertView
					.findViewById(R.id.tv_status);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		BitmapUtils bitmapUtils = new BitmapUtils(context);
		bitmapUtils.display(holder.iv_pic, list.get(position).get("images"));

		holder.tv_status.setText(list.get(position).get("message_title"));
		holder.tv_detail.setText(list.get(position).get("message_body"));

		/**
		 * 时间戳转换成具体时间形式
		 */
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssE",
				Locale.getDefault());
		// 当前时间对象
		Date curDate = new Date((Integer.parseInt(list.get(position).get(
				"message_time"))) * 1000L);
		String defaultTimeZoneID = TimeZone.getDefault().getID();// America/New_York
		// System.out.println("默认时区(美国/纽约)：" + defaultTimeZoneID);
		// System.out.println("默认时区(美国/纽约)：" + format.format(curDate));

		// 在格式化日期前使用一个新的时区
		String newTimeZoneID = "Asia/Shanghai"; // Asia/Shanghai
		format.setTimeZone(TimeZone.getTimeZone(newTimeZoneID));
		// System.out.println("新的时区(中国/上海)：" + newTimeZoneID);
		// System.out.println("新的时区(中国/上海)：" + format.format(curDate));
		holder.tv_time.setText(format.format(curDate));
		return convertView;
	}

	static class ViewHolder {
		TextView tv_time;
		ImageView iv_pic;
		TextView tv_status;
		TextView tv_detail;
	}

}
