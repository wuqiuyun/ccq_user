package ccj.yun28.com.neworder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amap.api.col.da;
import com.squareup.picasso.Picasso;

import ccj.yun28.com.R;

/**
 * 待付款 - 向你推荐其他热抢美食
 */

public class NoPayRecommendAdapter extends BaseAdapter {

    private List<Map<String, String>> list;
    private Context mContext;

    public NoPayRecommendAdapter(Context context) {
        this.mContext = context;
        this.list = new ArrayList<Map<String, String>>();
    }

    // 此方法传递数据源
    public void NoPayAdapter(List<Map<String, String>> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size() == 0 ? 10 : list.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_no_pay_recommend, null);
            
        	holder.foodName = (TextView) convertView.findViewById(R.id.tv_goods_name);
        	holder.ccqPrice = (TextView) convertView.findViewById(R.id.tv_ccq_price);
        	holder.originalPrice = (TextView) convertView.findViewById(R.id.tv_original_price);
        	holder.storeName = (TextView) convertView
					.findViewById(R.id.tv_store_name);
        	holder.address = (TextView) convertView
        			.findViewById(R.id.tv_address);
        	holder.distance = (TextView) convertView
        			.findViewById(R.id.tv_distance);
        	holder.pic = (ImageView) convertView
        			.findViewById(R.id.iv_pic);
        	
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
       Map<String, String> data = list.get(position);
       
//       holder.foodName.setText(data.get("goods_name"));
//       holder.ccqPrice.setText(data.get("goods_name"));
//       holder.originalPrice.setText(data.get("goods_name"));
//       holder.storeName.setText(data.get("goods_name"));
//       holder.address.setText(data.get("goods_name"));
//       holder.distance.setText(data.get("goods_name"));
       
       
        Picasso.with(mContext).load(list.get(position).get("goods_image"))
		.placeholder(R.drawable.xinpin).error(R.drawable.xinpin)
		.resize(100, 100).centerCrop().into(holder.pic);
        
        
        return convertView;
    }

    private class ViewHolder {
    	TextView foodName;
    	TextView ccqPrice;
    	TextView originalPrice;
    	TextView storeName;
    	TextView address;
    	TextView distance;
    	ImageView pic;
    	
    }
}
