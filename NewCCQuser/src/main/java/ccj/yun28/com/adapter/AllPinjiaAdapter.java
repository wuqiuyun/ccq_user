package ccj.yun28.com.adapter;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.ProductpinjiaBean;
import ccj.yun28.com.view.CircleImageView;
import ccj.yun28.com.view.star.StarLayoutParams;
import ccj.yun28.com.view.star.StarLinearLayout;

import com.lidroid.xutils.BitmapUtils;
import com.squareup.picasso.Picasso;

/**
 * 餐餐抢-所有评价
 * 
 * @author meihuali
 * 
 */
public class AllPinjiaAdapter extends BaseAdapter {

	private Context context;
	private List<ProductpinjiaBean> list;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();

	// private List<FenleiBean> data;

	public AllPinjiaAdapter(Activity context) {
		this.context = context;
		this.list = new ArrayList<ProductpinjiaBean>();
		// this.data = new ArrayList<FenleiBean>();

	}

	public void NotifyList(List<ProductpinjiaBean> list) {
//		this.list.clear();
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
		if (temp.get(position) == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_ccq_allpinjia, null);
			holder.img_circle = (CircleImageView) convertView
					.findViewById(R.id.img_circle);
			holder.tv_user_name = (TextView) convertView
					.findViewById(R.id.tv_user_name);
			holder.tv_pinjiadetail = (TextView) convertView
					.findViewById(R.id.tv_pinjiadetail);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.starsLayout = (StarLinearLayout) convertView
					.findViewById(R.id.starsLayout);
			temp.put(position, convertView);
			convertView.setTag(holder);
		} else {
			convertView = temp.get(position);
			holder = (ViewHolder) convertView.getTag();
		}

		String snum = list.get(position).getGeval_scores();
		int num = 1;
		if ("0.5".equals(snum) || "1".equals(snum)) {
			num = 1;
		} else if ("1.5".equals(snum) || "2".equals(snum)) {
			num = 2;
		} else if ("2.5".equals(snum) || "3".equals(snum)) {
			num = 3;
		} else if ("3.5".equals(snum) || "4".equals(snum)) {
			num = 4;
		} else if ("0".equals(snum) || "4.5".equals(snum) || "5".equals(snum)) {
			num = 5;
		}
		starsTest(holder, num);
//		BitmapUtils bitmapUtils = new BitmapUtils(context);
//		bitmapUtils.display(holder.img_circle, list.get(position).getAvator());
		Picasso.with(context).load(list.get(position).getAvator()).placeholder(R.drawable.xinpin).error(R.drawable.xinpin).resize(100,100).centerCrop().into(holder.img_circle);

		holder.tv_user_name.setText(list.get(position)
				.getGeval_frommembername());
		holder.tv_pinjiadetail.setText(list.get(position).getGeval_content());

		/**
		 * 时间戳转换成具体时间形式
		 */
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssE",
				Locale.getDefault());
		// 当前时间对象
		Date curDate = new Date((Integer.parseInt(list.get(position)
				.getGeval_addtime())) * 1000L);
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
		CircleImageView img_circle;
		TextView tv_user_name;
		TextView tv_pinjiadetail;
		TextView tv_time;
		StarLinearLayout starsLayout;
	}

	private void starsTest(ViewHolder holder, int num) {
		StarLayoutParams params = new StarLayoutParams();
		params.setNormalStar(
				context.getResources().getDrawable(R.drawable.starh))
				.setSelectedStar(
						context.getResources().getDrawable(R.drawable.stard))
				.setSelectable(false).setSelectedStarNum(num)
				.setTotalStarNum(5).setStarHorizontalSpace(6);
		holder.starsLayout.setStarParams(params);
	}
}
