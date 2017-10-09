package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.ShangJiaFenLeiBean;
import ccj.yun28.com.view.MyGridView;

import com.lidroid.xutils.BitmapUtils;

/**
 * 行业选择分类适配器
 * 
 * @author meihuali
 */
public class HangyeChooseAdapter extends BaseAdapter {
	private Context context;
	// private List<Map<String, String>> list;
	private List<ShangJiaFenLeiBean> data;

	public HangyeChooseAdapter(Activity context) {
		this.context = context;
		// this.list = new ArrayList<Map<String, String>>();
		this.data = new ArrayList<ShangJiaFenLeiBean>();

	}


	public void NotifyList(List<ShangJiaFenLeiBean> data) {
		// TODO Auto-generated method stub
		this.data.clear();
		this.data.addAll(data);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
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
					R.layout.item_suoshuhangye_lv, null);
			holder.iv_yiji_fenlei = (ImageView) convertView
					.findViewById(R.id.iv_yiji_fenlei);
			holder.tv_yiji_fenlei = (TextView) convertView
					.findViewById(R.id.tv_yiji_fenlei);
			holder.gv = (MyGridView) convertView.findViewById(R.id.gv);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final HangYeChooseErJiAdapter hangYeChooseErJiAdapter = new HangYeChooseErJiAdapter(
				context);
		hangYeChooseErJiAdapter.NotifyList(data.get(position).getClass2());
		holder.gv.setAdapter(hangYeChooseErJiAdapter);

		BitmapUtils bitmapUtils = new BitmapUtils(context);
		bitmapUtils.display(holder.iv_yiji_fenlei, data.get(position).getUrl());
		holder.tv_yiji_fenlei.setText(data.get(position).getGc_name());

		holder.gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String erjiname = hangYeChooseErJiAdapter.getData().get(arg2)
						.getGc_name();
				String erjiid = hangYeChooseErJiAdapter.getData().get(arg2)
						.getGc_id();
				if (mErjiHangyeListener != null) {
					mErjiHangyeListener.erjiHangyeDate(erjiname,erjiid);
				}
			}
		});
		return convertView;
	}

	static class ViewHolder {
		TextView tv_yiji_fenlei;
		ImageView iv_yiji_fenlei;
		MyGridView gv;
	}

	// 点击item详情页
	private ErjiHangyeListener mErjiHangyeListener;

	public interface ErjiHangyeListener {
		void erjiHangyeDate(String erjiname, String erjiid);
	}

	public void setErjiHangyeListener(ErjiHangyeListener listener) {
		mErjiHangyeListener = listener;
	}
}
