package ccj.yun28.com.adapter;

import java.util.ArrayList;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.CcqShopLvAdapter.ViewHolder;
import ccj.yun28.com.view.CircleImageView;
import ccj.yun28.com.view.star.StarLayoutParams;
import ccj.yun28.com.view.star.StarLinearLayout;

import com.amap.api.col.el;
import com.lidroid.xutils.BitmapUtils;

/**
 * 店铺详情_评价适配器
 * 
 * @author meihuali
 */
public class StorePingJiaAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> list;

	// private List<StoreProInfoBean> data;

	public StorePingJiaAdapter(Activity context) {
		this.context = context;
		this.list = new ArrayList<Map<String, String>>();
		// this.data = new ArrayList<StoreProInfoBean>();

	}

	public void NotifyList(List<Map<String, String>> list) {
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}

	// public void NotifyList(List<StoreProInfoBean> data) {
	// // TODO Auto-generated method stub
	// this.data.clear();
	// this.data.addAll(data);
	// notifyDataSetChanged();
	// }

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_storepingjia, null);
			holder.img_circle = (CircleImageView) convertView
					.findViewById(R.id.img_circle);
			holder.tv_pro_name = (TextView) convertView
					.findViewById(R.id.tv_pro_name);
			holder.starsLayout = (StarLinearLayout) convertView
					.findViewById(R.id.starsLayout);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.iv_pro_pic = (ImageView) convertView
					.findViewById(R.id.iv_pro_pic);
			holder.tv_pingjia_text = (TextView) convertView
					.findViewById(R.id.tv_pingjia_text);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		BitmapUtils bitmapUtils = new BitmapUtils(context);

		// 评价人头像使用商品图片
		/*
		 * bitmapUtils.display(holder.img_circle,
		 * list.get(position).get("goods_image"));
		 */
		
		
		/*
		 *  评价人头像除了女的外其他都用男的头像
		 *  性别 1男 2女 3:保密  
		 */
		String sex = list.get(position).get("member_sex");
		if ("2".equals(sex)) {
			holder.img_circle.setImageResource(R.drawable.girl);
		} else {
			holder.img_circle.setImageResource(R.drawable.man);
		}

		bitmapUtils.display(holder.iv_pro_pic,
				list.get(position).get("goods_image"));
		holder.tv_pro_name.setText(list.get(position).get(
				"geval_frommembername"));
		holder.tv_time.setText(list.get(position).get("geval_addtime"));
		holder.tv_pingjia_text.setText(list.get(position).get("geval_content"));

		String snum = list.get(position).get("geval_scores");
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

		return convertView;
	}

	private void starsTest(ViewHolder holder, int num) {
		StarLayoutParams params = new StarLayoutParams();
		params.setNormalStar(
				context.getResources().getDrawable(R.drawable.starh))
				.setSelectedStar(
						context.getResources().getDrawable(R.drawable.star))
				.setSelectable(false).setSelectedStarNum(num)
				.setTotalStarNum(5).setStarHorizontalSpace(6);
		holder.starsLayout.setStarParams(params);
	}

	static class ViewHolder {
		CircleImageView img_circle;
		TextView tv_pro_name;
		StarLinearLayout starsLayout;
		TextView tv_time;
		ImageView iv_pro_pic;
		TextView tv_pingjia_text;
	}

}
