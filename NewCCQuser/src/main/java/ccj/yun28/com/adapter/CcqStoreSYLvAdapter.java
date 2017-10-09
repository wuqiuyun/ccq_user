package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ccj.yun28.com.R;
import ccj.yun28.com.adapter.CcqJuanLvAdapter.ViewHolder;
import ccj.yun28.com.ccq.CCQStoreDetailActivity;
import ccj.yun28.com.ccq.CcqProDetailActivity;
import ccj.yun28.com.ccq.OldCCQStoreDetailActivity;
import ccj.yun28.com.utils.StringUtil;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.star.StarLayoutParams;
import ccj.yun28.com.view.star.StarLinearLayout;

import com.amap.location.navi.GPSNaviActivity;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

/**
 * 餐餐抢首页店铺适配器
 * 
 * @author meihuali
 */
public class CcqStoreSYLvAdapter extends BaseAdapter {
	private Activity context;
	private String latitude;
	private String longitude;
	private List<Map<String, String>> list;
	
	private Map<String, String> mMap;
//	private HashMap<Integer, View> temp = new HashMap<Integer, View>();

	public CcqStoreSYLvAdapter(Activity context) {
		this.context = context;
		this.list = new ArrayList<Map<String, String>>();
		this.latitude = latitude;
		this.longitude = longitude;
		this.mMap = new HashMap<String, String>();
	}

	public void NotifyList(List<Map<String, String>> list, String longitude,
			String latitude) {
		this.list.clear();
		this.list.addAll(list);
		this.latitude = latitude;
		this.longitude = longitude;
		notifyDataSetChanged();
	}

	public void setExtra(Map<String, String> map){
		this.mMap = map;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView==null) {
			// if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_ccq_sy_store, null);
			holder.rela_pic = (RelativeLayout) convertView
					.findViewById(R.id.rela_pic);
			holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.starsLayout = (StarLinearLayout) convertView
					.findViewById(R.id.starsLayout);
			holder.tv_store_name = (TextView) convertView
					.findViewById(R.id.tv_store_name);
			holder.tv_ccq_juli = (TextView) convertView
					.findViewById(R.id.tv_ccq_juli);
			holder.tv_ccq_liulan = (TextView) convertView
					.findViewById(R.id.tv_ccq_liulan);
			holder.tv_ccq_pingjia = (TextView) convertView
					.findViewById(R.id.tv_ccq_pingjia);
			holder.tv_phone = (TextView) convertView
					.findViewById(R.id.tv_phone);
			holder.tv_store_address = (TextView) convertView
					.findViewById(R.id.tv_store_address);
			holder.iv_store_address = (ImageView) convertView
					.findViewById(R.id.iv_store_address);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Utils utils = new Utils();
		int[] info = utils.getWindowInfo(context);
		// 获取当前控件的布局对象
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		// 获取当前控件的布局对象
		params.height = info[0] / 15 * 7;// 设置当前控件布局的高度
		holder.rela_pic.setLayoutParams(params);
		Picasso.with(context).load(list.get(position).get("union_img"))
				.placeholder(R.drawable.xinpin).error(R.drawable.xinpin)
				.resize(350, 180).into(holder.iv_pic);

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

		holder.tv_store_name.setText(list.get(position).get("store_name"));
		holder.tv_ccq_juli.setText(list.get(position).get("distance"));
		holder.tv_ccq_liulan.setText(list.get(position).get("store_traffic"));
		holder.tv_ccq_pingjia.setText(list.get(position).get("evaluate"));
		holder.tv_phone.setText(list.get(position).get("live_store_tel"));
		holder.tv_store_address
				.setText(list.get(position).get("store_address"));

		/*holder.rela_pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context,
						CCQStoreDetailActivity.class);
				intent.putExtra("starlat", latitude);
				intent.putExtra("starlng", longitude);
				intent.putExtra("store_id", list.get(position).get("store_id"));
				context.startActivity(intent);
			}
		});*/
		
		
		holder.rela_pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context,
						CCQStoreDetailActivity.class);
				intent.putExtra("starlat", StringUtil.isEmpty(mMap.get("latitude"))?latitude:mMap.get("latitude"));
				intent.putExtra("starlng", StringUtil.isEmpty(mMap.get("longitude"))?longitude:mMap.get("longitude"));
				intent.putExtra("store_id", list.get(position).get("store_id"));
				intent.putExtra("city", mMap.get("city"));// 所在市
				 intent.putExtra("district",mMap.get("district"));// 所在区
				context.startActivity(intent);
			}
		});
		
		holder.tv_phone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (!TextUtils
						.isEmpty(list.get(position).get("live_store_tel"))) {

					View view = LayoutInflater.from(context).inflate(
							R.layout.dialog_red_moban, null);
					TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
					TextView tv_dialog_cancel = (TextView) view
							.findViewById(R.id.tv_dialog_cancel);
					TextView dialog_text = (TextView) view
							.findViewById(R.id.dialog_text);
					dialog_text.setText("是否拨打： "
							+ list.get(position).get("live_store_tel"));
					final Dialog calldialog = new Dialog(context,
							R.style.mDialogStyle);
					calldialog.setContentView(view);
					calldialog.setCanceledOnTouchOutside(false);
					calldialog.show();

					tv_dialog_cancel.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							calldialog.dismiss();
						}
					});
					tv_ok.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							calldialog.dismiss();
							Intent callintent = new Intent(Intent.ACTION_CALL,
									Uri.parse("tel:"
											+ list.get(position).get(
													"live_store_tel")));
							context.startActivity(callintent);
						}
					});
				} else {
					Toast.makeText(context, "暂无电话", Toast.LENGTH_SHORT).show();
				}
			}
		});

		holder.iv_store_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty("latitude")
						|| TextUtils.isEmpty("longitude")) {
					Toast.makeText(context, "定位失败，请到空旷的地方重新定位",
							Toast.LENGTH_SHORT).show();
				} else if (TextUtils
						.isEmpty(list.get(position).get("latitude"))
						|| TextUtils.isEmpty(list.get(position)
								.get("longitude"))) {
					Toast.makeText(context, "没有商家经纬度", Toast.LENGTH_SHORT)
							.show();
				} else {
					Intent intent = new Intent(context, GPSNaviActivity.class);
					intent.putExtra("endlat", list.get(position)
							.get("latitude"));
					intent.putExtra("endlng",
							list.get(position).get("longitude"));
					intent.putExtra("starlat", latitude);
					intent.putExtra("starlng", longitude);
					context.startActivity(intent);
				}
			}
		});

		return convertView;
	}

	private void starsTest(ViewHolder holder, int num) {
		StarLayoutParams params = new StarLayoutParams();
		params.setNormalStar(
				context.getResources().getDrawable(R.drawable.starh))
				.setSelectedStar(
						context.getResources().getDrawable(R.drawable.star))
				.setSelectable(true).setSelectedStarNum(num).setTotalStarNum(5)
				.setStarHorizontalSpace(6);
		holder.starsLayout.setStarParams(params);
	}

	static class ViewHolder {
		RelativeLayout rela_pic;
		ImageView iv_pic;
		StarLinearLayout starsLayout;
		TextView tv_store_name;
		TextView tv_ccq_juli;
		TextView tv_ccq_liulan;
		TextView tv_ccq_pingjia;
		TextView tv_phone;
		TextView tv_store_address;
		ImageView iv_store_address;
	}

}
