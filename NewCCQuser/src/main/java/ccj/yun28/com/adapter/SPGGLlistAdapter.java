package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.ProductGuiZeBean;
import ccj.yun28.com.bean.ProductGuizeNameBean;
import ccj.yun28.com.view.zdygridview.Tag;
import ccj.yun28.com.view.zdygridview.TagListView;
import ccj.yun28.com.view.zdygridview.TagListView.OnTagClickListener;
import ccj.yun28.com.view.zdygridview.TagView;

/**
 * 商品规格 - 适配器
 * 
 * @author meihuali
 */
public class SPGGLlistAdapter extends BaseAdapter {
	private Context context;
	private List<ProductGuizeNameBean> groups;
	private List<List<ProductGuiZeBean>> children;
	private final List<Tag> mTags = new ArrayList<Tag>();
	private Map<String, String> jhmap = new HashMap<String, String>();

	public SPGGLlistAdapter(List<ProductGuizeNameBean> groups,
			List<List<ProductGuiZeBean>> children, Context context) {
		this.context = context;
		this.groups = groups;
		this.children = children;

	}

	public void NotifyList(List<ProductGuizeNameBean> list) {
		this.groups.clear();
		this.groups.addAll(list);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return groups.size();
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
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_spgg, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tagview = (TagListView) convertView
					.findViewById(R.id.tagview);
			setUpData(0, position);
			holder.tagview.setTags(mTags);
			holder.tagview.setOnTagClickListener(new OnTagClickListener() {
				@Override
				public void onTagClick(TagView tagView, Tag tag) {
					setUpData(tag.getId(), position);
					holder.tagview.setTags(mTags);
					jhmap.put(position + "", tag.getId() + "");
					if (mObtainDateListener != null) {
						mObtainDateListener.obtainDate(jhmap);
					}
				}
			});
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_name.setText(groups.get(position).getName());

		return convertView;
	}

	private void setUpData(int click, int position) {
		mTags.clear();
		for (int i = 0; i < children.get(position).size(); i++) {
			Tag tag = new Tag();
			tag.setId(Integer.parseInt(children.get(position).get(i).getId()));
			tag.setChecked(true);
			tag.setTitle(children.get(position).get(i).getName());
			if (Integer.parseInt(children.get(position).get(i).getId()) == click) {
				tag.setBackgroundResId(R.drawable.zylmxz);
				int c = context.getResources().getColor(R.color.color_white);
				tag.setTextcolorId(c);
			} else {
				tag.setBackgroundResId(R.drawable.zylm);
				int c = context.getResources().getColor(
						R.color.color_text_black);
				tag.setTextcolorId(c);
			}
			mTags.add(tag);
		}
	}

	static class ViewHolder {
		TextView tv_name;
		TagListView tagview;
	}

	private ObtainDateListener mObtainDateListener;

	public interface ObtainDateListener {
		void obtainDate(Map<String, String> jhmap);
	}

	public void setOnObtainDateListener(ObtainDateListener listener) {
		mObtainDateListener = listener;
	}

}
