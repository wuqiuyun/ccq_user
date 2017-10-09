package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.List;

import ccj.yun28.com.R;
import ccj.yun28.com.bean.BundingGoodsInfo;
import ccj.yun28.com.view.MyListView;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * ccq商品详情 适配器
 * 
 * @author meihuali
 */
public class CcqProDetaillistAdapter extends BaseAdapter {
	private Context context;
	private List<BundingGoodsInfo> data;

	public CcqProDetaillistAdapter(Activity context) {
		this.context = context;
		this.data = new ArrayList<BundingGoodsInfo>();

	}

	public void NotifyList(List<BundingGoodsInfo> data) {
		this.data.clear();
		this.data.addAll(data);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return data.size();
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
					R.layout.item_ccqprodetaillist, null);

			holder.tv_choose = (TextView) convertView.findViewById(R.id.tv_choose);
			holder.lv_goods = (MyListView) convertView.findViewById(R.id.lv_goods);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (data.get(position).getName() == null || "".equals(data.get(position).getName())) {
			holder.tv_choose.setVisibility(View.GONE);
		}else{
			holder.tv_choose.setVisibility(View.VISIBLE);
			holder.tv_choose.setText(data.get(position).getName());
		}
		
		final CcqZheKouJuanDetaillistAdapter ccqZheKouJuanDetaillistAdapter = new CcqZheKouJuanDetaillistAdapter(
				context);
		ccqZheKouJuanDetaillistAdapter.NotifyList(data.get(position).getList());
		holder.lv_goods.setAdapter(ccqZheKouJuanDetaillistAdapter);
		return convertView;
	}

	static class ViewHolder {
		TextView tv_choose;
		MyListView lv_goods;
	}

}
