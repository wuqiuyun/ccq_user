package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.ErjiShangJiaFenLeiiBean;


public class HangYeChooseErJiAdapter extends BaseAdapter{
	private Context context;
	// private List<Map<String, String>> list;
	private List<ErjiShangJiaFenLeiiBean> data;

	public HangYeChooseErJiAdapter(Context context) {
		this.context = context;
		// this.list = new ArrayList<Map<String, String>>();
		this.data = new ArrayList<ErjiShangJiaFenLeiiBean>();

	}

	public void NotifyList(List<ErjiShangJiaFenLeiiBean> data) {
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
	public Object getItem(int arg0) {
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
					R.layout.item_suoshuhangyeerji_gv, null);
			holder.tv_erjihangye = (TextView) convertView.findViewById(R.id.tv_erjihangye);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv_erjihangye.setText(data.get(position).getGc_name());

		return convertView;
	}
	static class ViewHolder {
		TextView tv_erjihangye;
	}
	
	public List<ErjiShangJiaFenLeiiBean> getData(){
		return data;
	}
}
