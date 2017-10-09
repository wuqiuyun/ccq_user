package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ccj.yun28.com.R;
import ccj.yun28.com.view.star.StarLayoutParams;
import ccj.yun28.com.view.star.StarLinearLayout;

import com.lidroid.xutils.BitmapUtils;

/**
 * 搜索餐餐抢结果适配器
 * 
 * @author meihuali
 * 
 */
public class SearchccqlvAdapter extends BaseAdapter {

	private Context context;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();
	private List<Map<String, String>> list;
	private boolean flag;

	public SearchccqlvAdapter(Activity context) {
		this.context = context;
		this.list = new ArrayList<Map<String, String>>();
		this.flag = flag;
		// this.data = new ArrayList<BBsearchBean>();

	}

	public void NotifyList(List<Map<String, String>> list, boolean flag) {
		this.temp.clear();
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}

	// public void NotifyList(List<BBsearchBean> data) {
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
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private ViewHolder holder = new ViewHolder();

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (temp.get(position) == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_ccq_shop, null);
			holder.iv_store_pic = (ImageView) convertView.findViewById(R.id.iv_store_pic);
			holder.tv_store_name = (TextView) convertView
					.findViewById(R.id.tv_store_name);
			holder.tv_xianliang = (TextView) convertView
					.findViewById(R.id.tv_xianliang);
			holder.tv_pingjia = (TextView) convertView
					.findViewById(R.id.tv_pingjia);
			holder.tv_address = (TextView) convertView
					.findViewById(R.id.tv_address);
			holder.starsLayout = (StarLinearLayout) convertView
					.findViewById(R.id.starsLayout);
			holder.tv_juli = (TextView) convertView.findViewById(R.id.tv_juli);
			holder.tv_guanggao = (TextView) convertView.findViewById(R.id.tv_guanggao);
			holder.tv_fenshu = (TextView) convertView.findViewById(R.id.tv_fenshu);

			temp.put(position, convertView);
			convertView.setTag(holder);
		} else {
			convertView = temp.get(position);
			holder = (ViewHolder) convertView.getTag();
		}

		String snum = list.get(position).get("store_credit");
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
		BitmapUtils bitmapUtils = new BitmapUtils(context);
		bitmapUtils.display(holder.iv_store_pic, list.get(position).get("union_img"));
		holder.tv_store_name.setText(list.get(position).get("store_name"));
		holder.tv_xianliang.setText("销量：" + list.get(position).get("sales_num"));
		holder.tv_pingjia.setText("评价：" + list.get(position).get("evaluate"));
		holder.tv_address.setText("地址：" + list.get(position).get("store_address"));
		holder.tv_juli.setText(list.get(position).get("distance"));
		holder.tv_fenshu.setText(list.get(position).get("geval_scores") + "分");
		if (TextUtils.isEmpty(list.get(position).get("adv")) || list.get(position).get("adv") == null) {
			holder.tv_guanggao.setVisibility(View.GONE);
		}else{
			holder.tv_guanggao.setVisibility(View.VISIBLE);
			holder.tv_guanggao.setText(list.get(position).get("adv"));
		}
		return convertView;
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

	static class ViewHolder {
		ImageView iv_store_pic;
		TextView tv_store_name;
		TextView tv_xianliang;
		TextView tv_pingjia;
		TextView tv_address;
		StarLinearLayout starsLayout;
		TextView tv_juli;
		TextView tv_guanggao;
		TextView tv_fenshu;
	}

}
