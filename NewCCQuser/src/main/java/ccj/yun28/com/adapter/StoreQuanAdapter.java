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

import com.lidroid.xutils.BitmapUtils;

/**
 * 店铺详情_抢券适配器
 * 
 * @author meihuali
 */
public class StoreQuanAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> list;

	// private List<StoreProInfoBean> data;

	public StoreQuanAdapter(Activity context) {
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
		// TODO Auto-generated method stub
		return list.size();
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
					R.layout.item_storequan, null);
			holder.iv_pro_pic = (ImageView) convertView
					.findViewById(R.id.iv_pro_pic);
			holder.tv_pro_name = (TextView) convertView
					.findViewById(R.id.tv_pro_name);
			holder.tv_shop_price = (TextView) convertView
					.findViewById(R.id.tv_shop_price);
			holder.tv_jiesheng = (TextView) convertView
					.findViewById(R.id.tv_jiesheng);
			holder.tv_yanquan_price = (TextView) convertView
					.findViewById(R.id.tv_yanquan_price);
			holder.tv_pro_shuoming = (TextView) convertView
					.findViewById(R.id.tv_pro_shuoming);
			holder.tv_zhekou = (TextView) convertView
					.findViewById(R.id.tv_zhekou);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		BitmapUtils bitmapUtils = new BitmapUtils(context);
		bitmapUtils.display(holder.iv_pro_pic,
				list.get(position).get("goods_image"));
		holder.tv_pro_name.setText(list.get(position).get("goods_name"));
		holder.tv_shop_price.setText("商家销售：" + list.get(position).get("goods_marketprice")+ "元");
		holder.tv_jiesheng.setText(list.get(position).get("save"));
		holder.tv_yanquan_price.setText("验券价：" + list.get(position).get("goods_price")+ "元");
		holder.tv_pro_shuoming.setText("商品说明：" + list.get(position).get("remark"));
		holder.tv_zhekou.setText(list.get(position).get("discount")+ "折");

		return convertView;
	}

	static class ViewHolder {
		ImageView iv_pro_pic;
		TextView tv_pro_name;
		TextView tv_shop_price;
		TextView tv_jiesheng;
		TextView tv_yanquan_price;
		TextView tv_pro_shuoming;
		TextView tv_zhekou;
	}

}
