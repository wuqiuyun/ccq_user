package ccj.yun28.com.neworder;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.newbean.RecommendList;
import ccj.yun28.com.utils.Utils;

import com.squareup.picasso.Picasso;

/**
 * 待付款 - 向你推荐其他热抢美食
 */

public class RecommendAdapter extends BaseAdapter {

	private List<RecommendList> list;
	private Activity mContext;
	private Utils utils = new Utils();

	public RecommendAdapter(Activity context) {
		this.mContext = context;
		this.list = new ArrayList<RecommendList>();
	}

	// 此方法传递数据源
	public void NoPayAdapter(List<RecommendList> list) {
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size() > 8 ? 8 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_recommend_food, null);

			holder.foodName = (TextView) convertView
					.findViewById(R.id.tv_goods_name);
			holder.ccqPrice = (TextView) convertView
					.findViewById(R.id.tv_ccq_price);
			holder.originalPrice = (TextView) convertView
					.findViewById(R.id.tv_original_price);
			holder.storeName = (TextView) convertView
					.findViewById(R.id.tv_store_name);
			// holder.address = (TextView) convertView
			// .findViewById(R.id.tv_address);
			// holder.distance = (TextView) convertView
			// .findViewById(R.id.tv_distance);
			holder.pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.ll_item = (LinearLayout) convertView
					.findViewById(R.id.ll_item);

			holder.ll_laypout = (LinearLayout) convertView
					.findViewById(R.id.ll_layout);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final RecommendList bean = list.get(position);

		// TODO
		int[] info = utils.getWindowInfo((Activity) mContext);
		// 获取当前控件的布局对象
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		// 获取当前控件的布局对象
		params.width = info[0] / 10 * 8;// 设置当前控件布局的高度
		holder.ll_laypout.setLayoutParams(params);

		
		holder.foodName.setText(bean.getGoods_name());
		holder.ccqPrice.setText("餐餐抢价：" + bean.getGoods_price() + "元");
		holder.originalPrice.setText("原价：" + bean.getGoods_marketprice() + "元");
		holder.storeName.setText(bean.getStore_name());
		// holder.address.setText(bean.getStore_address());
		// holder.distance.setText(bean.getDistance_value());

		holder.ll_item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				 * Intent intent = new Intent(mContext,
				 * NewCcqProDetailActivity.class); intent.putExtra("starlat",
				 * "");// 经度 intent.putExtra("starlng", "");// 纬度
				 * intent.putExtra("ccqgoods_id",
				 * !StringUtil.isEmpty(bean.getGoods_id
				 * ())?bean.getGoods_id():"118096");// goods_id
				 * intent.putExtra("store_id",
				 * !StringUtil.isEmpty(bean.getStore_id
				 * ())?bean.getStore_id():"1792"); intent.putExtra("city",
				 * "");// 所在市 intent.putExtra("district", "");// 所在区
				 * mContext.startActivity(intent);
				 */

				/*
				 * Intent intent = new Intent(mContext,
				 * NewCcqProDetailActivity.class); intent.putExtra("starlat",
				 * "");// 经度 intent.putExtra("starlng", "");// 纬度
				 * intent.putExtra("ccqgoods_id", bean.getGoods_id());//
				 * goods_id intent.putExtra("store_id", bean.getStore_id());
				 * intent.putExtra("city", "");// 所在市
				 * intent.putExtra("district", "");// 所在区
				 * mContext.startActivity(intent);
				 */
			}
		});
		Picasso.with(mContext).load(bean.getGoods_image())
				.placeholder(R.drawable.xinpin).error(R.drawable.xinpin)
				.resize(100, 100).centerCrop().into(holder.pic);
		return convertView;
	}

	private class ViewHolder {
		LinearLayout ll_laypout;
		TextView foodName;
		TextView ccqPrice;
		TextView originalPrice;
		TextView storeName;
		// TextView address;
		TextView distance;
		ImageView pic;
		LinearLayout ll_item;
	}
}
