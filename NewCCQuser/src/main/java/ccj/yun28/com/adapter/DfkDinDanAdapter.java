package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.AllDinDanBean;
import ccj.yun28.com.bean.ZhuAllDinDanBean;
import ccj.yun28.com.mine.Mine_DinDan_BuyZKJActivity;
import ccj.yun28.com.mine.Mine_Dindan_TianxieDindanActivity;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 全部订单-代付款 适配器
 * 
 * @author meihuali
 */
public class DfkDinDanAdapter extends BaseAdapter {
	private Context context;
	private List<AllDinDanBean> data;
	private ZhuAllDinDanBean allDinDanBean;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();

	private boolean flag = false;
	private Utils utils;
	private String[] verstring;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 错误
	private static final int HANDLER_NN_FAILURE = 1;
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 删除成功
	private static final int HANDLER_SHANCHU_SUCCESS = 3;
	// 全部数据
	private static final int HANDLER_ALLDINDAN_SUCCESS = 4;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(context, "当前网络不可用,请检查网络", Toast.LENGTH_SHORT)
						.show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(context, "当前网络出错,请检查网络", Toast.LENGTH_SHORT).show();
				break;
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (msg.obj != null) {
					Toast.makeText(context, msg.obj.toString().trim(),
							Toast.LENGTH_SHORT).show();
				}
				break;
			// 删除成功
			case HANDLER_SHANCHU_SUCCESS:
				if (msg.obj != null) {
					Toast.makeText(context, msg.obj.toString().trim(),
							Toast.LENGTH_SHORT).show();
				}
				flag = true;
				showLoading();
				allDinDanHttpPost();
				break;
			case HANDLER_ALLDINDAN_SUCCESS:
				dissDialog();
				NotifyList(allDinDanBean.getData(),true);
				notifyDataSetChanged();
				break;
			}
		};
	};

	public DfkDinDanAdapter(Activity context) {
		this.context = context;
		this.data = new ArrayList<AllDinDanBean>();

	}

	public void NotifyList(List<AllDinDanBean> data,boolean flag) {
		// TODO Auto-generated method stub
		this.flag = flag;
		if (flag) {
			this.data.clear();
		}
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
		if (temp.get(position) == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_dfkdindan_shop, null);
			holder.tv_shopname = (TextView) convertView
					.findViewById(R.id.tv_shopname);
			holder.lv_goods = (ListView) convertView
					.findViewById(R.id.lv_goods);
			// 订单信息
			holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
			holder.tv_jiage = (TextView) convertView
					.findViewById(R.id.tv_jiage);
			// 代付款
			holder.tv_shanchu = (TextView) convertView
					.findViewById(R.id.tv_shanchu);
			holder.tv_quzhifu = (TextView) convertView
					.findViewById(R.id.tv_quzhifu);

			temp.put(position, convertView);
			convertView.setTag(holder);
		} else {
			convertView = temp.get(position);
			holder = (ViewHolder) convertView.getTag();
		}
		
		utils = new Utils();
		verstring = utils.getVersionInfo(context);

		// 0 普通商品 1套餐(废弃) 2先消费后买单 3餐餐抢套餐
		union_type = data.get(position).getUnion_type();
		holder.tv_shopname.setText(data.get(position).getStore_name());
		String status = data.get(position).getOrder_state();
		holder.tv_jiage.setText("¥" + data.get(position).getOrder_amount());
		holder.tv_num.setText("共计" + data.get(position).getGoods_num() + "件商品");

		String order_sn = data.get(position).getOrder_sn();
		String order_amount = data.get(position).getOrder_amount();
		String goods_amount = data.get(position).getGoods_amount();
		final AllDinDanGoodsAdapter alldindangoodsadapter = new AllDinDanGoodsAdapter(
				context);
		alldindangoodsadapter.NotifyList(data.get(position).getGoods_list(),
				union_type,order_sn,order_amount,goods_amount);
		holder.lv_goods.setAdapter(alldindangoodsadapter);
		showDaiFuKuanButton(holder, position);
		holder.lv_goods.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
//				Intent intent;
//				if ("0".equals(alldindangoodsadapter.getData().get(arg2).getUnion_type())) {
//					intent = new Intent(context, TuiKuanDinDanDetailActivity.class);
//					intent.putExtra("order_id", alldindangoodsadapter.getData().get(arg2).getOrder_id());
//					context.startActivity(intent);
//				}else if ("3".equals(alldindangoodsadapter.getData().get(arg2).getUnion_type())) {
//					intent = new Intent(context, CCQDinDanDetailActivity.class);
//					intent.putExtra("order_id", alldindangoodsadapter.getData().get(arg2).getOrder_id());
//					context.startActivity(intent);
//				}else if ("2".equals(alldindangoodsadapter.getData().get(arg2).getUnion_type())) {
//					intent = new Intent(context, XiaofeiMaidanDetailActivity.class);
//					intent.putExtra("order_id", alldindangoodsadapter.getData().get(arg2).getOrder_id());
//					context.startActivity(intent);
//				}
				String order_id = alldindangoodsadapter.getData().get(arg2).getOrder_id();
				String union_type = alldindangoodsadapter.getData().get(arg2).getUnion_type();
				if (mDinDanDateListener != null) {
					mDinDanDateListener.dinDanDate(order_id, union_type);
				}
			}
		});
		return convertView;
	}

	private void showDaiFuKuanButton(ViewHolder holder, final int position) {
		// TODO Auto-generated method stub
		// 删除
		holder.tv_shanchu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				shanchuHttpPost(position);
			}
		});
		// 去支付
		holder.tv_quzhifu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 0 普通商品 1套餐(废弃) 2先消费后买单 3餐餐抢套餐
				union_type = data.get(position).getUnion_type();
				if ("0".equals(union_type)) {
					Intent intent = new Intent(context,
							Mine_Dindan_TianxieDindanActivity.class);
					intent.putExtra("order_id", data.get(position)
							.getOrder_id());
					context.startActivity(intent);
				} else if ("3".equals(union_type)) {
					Intent intent = new Intent(context,
							Mine_DinDan_BuyZKJActivity.class);
					intent.putExtra("order_id", data.get(position)
							.getOrder_id());
					context.startActivity(intent);
				}
			}
		});
	}

	// 删除订单
	protected void shanchuHttpPost(int position) {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token", new DButil().gettoken(context));
		params.addBodyParameter("order_id", data.get(position).getOrder_id());
		httpUtils.send(HttpMethod.POST, JiekouUtils.DELETEDINDAN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						shanchuListInfo(arg0.result);
					}
				});
	}

	protected void shanchuListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			String message = object.getString("message");
			if ("200".equals(code)) {
				handler.sendMessage(handler.obtainMessage(
						HANDLER_SHANCHU_SUCCESS, message));
			} else {
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETINFO_FAILURE, message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	static class ViewHolder {
		TextView tv_shopname;
		TextView tv_status;
		ListView lv_goods;
		TextView tv_num;
		TextView tv_jiage;
		TextView tv_shanchu;
		TextView tv_quzhifu;
	}

	// 获取全部订单数据接口
	private void allDinDanHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token", new DButil().gettoken(context));
		params.addBodyParameter("member_id", new DButil().getMember_id(context));
		params.addBodyParameter("order_state", "10");
		params.addBodyParameter("page", "1");
		httpUtils.send(HttpMethod.POST, JiekouUtils.ALLDINDAN, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						allDinDanListInfo(arg0.result);

					}
				});
	}

	// 解析全部订单接口数据
	protected void allDinDanListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				allDinDanBean = null;
				Gson gson = new Gson();
				allDinDanBean = gson.fromJson(result, ZhuAllDinDanBean.class);
				handler.sendEmptyMessage(HANDLER_ALLDINDAN_SUCCESS);
			} else {
				String message = object.getString("message");
				handler.sendEmptyMessage(HANDLER_GETINFO_FAILURE);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 加载中动画
	private Dialog loadingDialog;
	private String union_type;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(context)
				.inflate(R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(context,
				R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(context, R.style.mDialogStyle);
		loadingDialog.setContentView(view);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.show();
	}

	// 关闭加载dialog
	private void dissDialog() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
	}
	public List<AllDinDanBean> getData(){
		return data;
	}
	
	//点击item详情页
		private DinDanDateListener mDinDanDateListener;

		public interface DinDanDateListener {
			void dinDanDate(String order_id,String union_type);
		}
		public void setDinDanDateListener(DinDanDateListener listener) {
			mDinDanDateListener = listener;
		}
}
