package ccj.yun28.com.neworder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.newbean.RecommendGoodsBean;
import ccj.yun28.com.ccq.NewCcqProDetailActivity;
import ccj.yun28.com.mine.VIPhuiyuanActivity;
import ccj.yun28.com.sy.BuyZKJActivity;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.star.StarLayoutParams;
import ccj.yun28.com.view.star.StarLinearLayout;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.squareup.picasso.Picasso;

/**
 * 订单页面热门推荐
 * 
 * @author wuqiuyun
 * 
 */
public class RecommendAllGoodsAdapter extends BaseAdapter {
	private Map<String, String> mMap;
	private Activity mContext;
	private List<RecommendGoodsBean> list;
	private LayoutInflater inflater;
	private String[] verstring;
	private Utils utils = new Utils();

	@SuppressWarnings("static-access")
	public RecommendAllGoodsAdapter(Activity mContext,List<RecommendGoodsBean> list) {
		this.mContext = mContext;
		this.list = list;
		this.inflater = inflater.from(mContext);
		this.mMap = new HashMap<String, String>();

	}
	public void setCityInfo(Map<String, String> cityInfo) {
		this.mMap = cityInfo;
	}
	public void setData(List<RecommendGoodsBean> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public RecommendGoodsBean getItem(int position) {
		return list.get(position);
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
			convertView = inflater.inflate(R.layout.item_recommend_all_goods, null);
			holder.real_pic = (RelativeLayout) convertView
					.findViewById(R.id.real_pic);
			holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.tv_discount = (TextView) convertView
					.findViewById(R.id.tv_discount);
			holder.tv_pro_name = (TextView) convertView
					.findViewById(R.id.tv_pro_name);
			holder.starsLayout = (StarLinearLayout) convertView
					.findViewById(R.id.starsLayout);
			holder.tv_ccq_price = (TextView) convertView
					.findViewById(R.id.tv_ccq_price);
			holder.tv_yuan_price = (TextView) convertView
					.findViewById(R.id.tv_yuan_price);
			holder.tv_ccq_sale = (TextView) convertView
					.findViewById(R.id.tv_ccq_sale);
			holder.tv_ccq_liulan = (TextView) convertView
					.findViewById(R.id.tv_ccq_liulan);
			holder.tv_ccq_shuoming = (TextView) convertView
					.findViewById(R.id.tv_ccq_shuoming);
			holder.tv_store_address = (TextView) convertView
					.findViewById(R.id.tv_store_address);
			holder.tv_ccq_juli = (TextView) convertView
					.findViewById(R.id.tv_ccq_juli);
			holder.iv_ccq_qiang = (ImageView) convertView
					.findViewById(R.id.iv_ccq_qiang);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final RecommendGoodsBean goodsBean = getItem(position);

		int[] info = utils.getWindowInfo(mContext);
		// 获取当前控件的布局对象
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		// 获取当前控件的布局对象
		params.height = info[0] / 15 * 7;// 设置当前控件布局的高度
		holder.real_pic.setLayoutParams(params);
		
		
		Picasso.with(mContext).load(goodsBean.getGoods_image())
				.placeholder(R.drawable.xinpin).error(R.drawable.xinpin)
				.resize(350, 180).into(holder.iv_pic);

		String snum = goodsBean.getEvaluation_good_star();
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
		
		holder.tv_discount.setText(goodsBean.getDiscount() + "折");
		holder.tv_pro_name.setText(goodsBean.getGoods_name().trim());
		holder.tv_ccq_price.setText(goodsBean.getGoods_price() + "元");
		holder.tv_yuan_price.setText("商家原价：" + goodsBean.getGoods_marketprice()
				+ "元");
		// holder.tv_yuan_price.getPaint().setFlags(
		// Paint.STRIKE_THRU_TEXT_FLAG); // 中间加横线
		holder.tv_ccq_sale.setText(goodsBean.getGoods_salenum());
		holder.tv_ccq_liulan.setText(goodsBean.getGoods_click());
		holder.tv_ccq_shuoming.setText(goodsBean.getRemark());
		holder.tv_store_address.setText("地址："+goodsBean.getStore_address());
		//holder.tv_ccq_juli.setText(goodsBean.getDistance_value());

//		goods_id = goodsBean.getGoods_id();
		holder.real_pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/*Intent intent = new Intent(mContext, NewCcqProDetailActivity.class);//TODO
				intent.putExtra("starlat", goodsBean.getLatitude());
				intent.putExtra("starlng", goodsBean.getLongitude());
				intent.putExtra("ccqgoods_id", goodsBean.getGoods_id());
				intent.putExtra("store_id", goodsBean.getStore_id());
				intent.putExtra("city", "");
				intent.putExtra("district", "");
				mContext.startActivity(intent);*/
				
				
				Intent intent = new Intent(mContext, NewCcqProDetailActivity.class);//TODO
				intent.putExtra("starlat", mMap.get("latitude"));
				intent.putExtra("starlng", mMap.get("longitude"));
				intent.putExtra("ccqgoods_id", goodsBean.getGoods_id());
				intent.putExtra("store_id", goodsBean.getStore_id());
				intent.putExtra("city", mMap.get("city"));
				intent.putExtra("district", mMap.get("district"));
				mContext.startActivity(intent);
			}
		});
		holder.iv_ccq_qiang.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (utils.getisLogin(mContext)) {
//					sfVIPHttpPost(list.get(position).get("goods_id"));
					sfVIPHttpPost(getItem(position).getGoods_id());
				} else {
					Intent intent = new Intent(mContext, LoginActivity.class);
					intent.putExtra("type", "ccq");
					mContext.startActivity(intent);
				}
			}
		});
		return convertView;
	}

	private void starsTest(ViewHolder holder, int num) {
		StarLayoutParams params = new StarLayoutParams();
		params.setNormalStar(
				mContext.getResources().getDrawable(R.drawable.starh))
				.setSelectedStar(
						mContext.getResources().getDrawable(R.drawable.star))
				.setSelectable(false).setSelectedStarNum(num)
				.setTotalStarNum(5).setStarHorizontalSpace(6);
		holder.starsLayout.setStarParams(params);
	}

	static class ViewHolder {
		RelativeLayout real_pic;
		ImageView iv_pic;
		TextView tv_discount;
		TextView tv_pro_name;
		StarLinearLayout starsLayout;
		TextView tv_ccq_price;
		TextView tv_yuan_price;
		TextView tv_ccq_sale;
		TextView tv_ccq_liulan;
		TextView tv_ccq_shuoming;
		TextView tv_store_address;
		TextView tv_ccq_juli;
		ImageView iv_ccq_qiang;
	}

	// 是不是VIP接口
		private void sfVIPHttpPost(final String goods_id) {
			// TODO Auto-generated method stub
			HttpUtils httpUtils = new HttpUtils();
			RequestParams params = new RequestParams();
			verstring = utils.getVersionInfo(mContext);
			params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
					+ "," + verstring[2]);
			params.addBodyParameter("token", new DButil().gettoken(mContext));
			params.addBodyParameter("member_id", new DButil().getMember_id(mContext));
			httpUtils.send(HttpMethod.POST, JiekouUtils.SFVIP, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							Log.e("ee", "失败:" + arg1);
							handler.sendEmptyMessage(HANDLER_NET_FAILURE);
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							sfVIPListInfo(arg0.result,goods_id);
						}
					});
		}
		
		// 是不是VIP数据解析
		protected void sfVIPListInfo(String result,String id) {
			try {
				JSONObject object = new JSONObject(result);
				String code = object.getString("code");
				if ("200".equals(code)) {
					JSONObject ob = object.getJSONObject("data");
					is_vip = ob.getString("is_vip");
					goods_id=id;//TODO
					handler.sendEmptyMessage(HANDLER_IS_VIP_SUCCESS);
				} else {
					String message = object.getString("message");
					handler.sendMessage(handler.obtainMessage(
							HANDLER_GETINFO_FAILURE, message));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				handler.sendEmptyMessage(HANDLER_NN_FAILURE);
			}
		}
		
		private String is_vip;
		private String goods_id;
		// 网络异常
		protected static final int HANDLER_NET_FAILURE = 0;
		// 异常
		protected static final int HANDLER_NN_FAILURE = 1;
		// 获取信息失败
		private static final int HANDLER_GETINFO_FAILURE = 2;
		// 获取是否vip成功
		private static final int HANDLER_IS_VIP_SUCCESS = 3;
		
		private Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				// 网络异常
				case HANDLER_NET_FAILURE:
					Toast.makeText(mContext, "当前网络不可用,请检查网络", Toast.LENGTH_SHORT)
							.show();
					break;
				// 错误
				case HANDLER_NN_FAILURE:
					Toast.makeText(mContext, "当前网络出错,请检查网络", Toast.LENGTH_SHORT)
							.show();
					break;
				// 获取信息失败
				case HANDLER_GETINFO_FAILURE:
					Toast.makeText(mContext, msg.obj.toString().trim(),
							Toast.LENGTH_SHORT).show();
					break;
				// 获取是否vip成功
				case HANDLER_IS_VIP_SUCCESS:
					if ("1".equals(is_vip)) {
						Intent intent = new Intent(mContext, BuyZKJActivity.class);
						intent.putExtra("goods_id", goods_id);
						mContext.startActivity(intent);
					} else {
						Toast.makeText(mContext, "VIP才可以购买", Toast.LENGTH_SHORT)
								.show();
						Intent intent = new Intent(mContext,
								VIPhuiyuanActivity.class);
						mContext.startActivity(intent);
					}
					break;
				default:
					break;
				}
			};
		};

	
}
