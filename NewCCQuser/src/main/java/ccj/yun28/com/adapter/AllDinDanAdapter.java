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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.AllDinDanBean;
import ccj.yun28.com.bean.AllDinDanGoodsBean;
import ccj.yun28.com.bean.ZhuAllDinDanBean;
import ccj.yun28.com.mine.ChaKanJuanMaActivity;
import ccj.yun28.com.mine.ChaKanWuLiuActivity;
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
 * 全部订单-全部 适配器
 * 
 * @author meihuali
 */
public class AllDinDanAdapter extends BaseAdapter {
	private Context context;
	private List<AllDinDanBean> data;
	private ZhuAllDinDanBean allDinDanBean;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();
	private Utils utils;
	private HttpUtils httpUtils;
	private String[] verstring;

	private boolean flag = false;
	
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
				Toast.makeText(context, msg.obj.toString().trim(),
						Toast.LENGTH_SHORT).show();
				break;
			// 删除成功
			case HANDLER_SHANCHU_SUCCESS:
				Toast.makeText(context, msg.obj.toString().trim(),
						Toast.LENGTH_SHORT).show();
				flag = true;
				allDinDanHttpPost();
				break;
			// 删除成功
			case HANDLER_ALLDINDAN_SUCCESS:
				dissDialog();
				NotifyList(allDinDanBean.getData(),true);
				notifyDataSetChanged();
				break;
			}
		};
	};

	public AllDinDanAdapter(Activity context) {
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
					R.layout.item_alldindan_shop, null);
			holder.tv_shopname = (TextView) convertView
					.findViewById(R.id.tv_shopname);
			holder.tv_status = (TextView) convertView
					.findViewById(R.id.tv_status);
			holder.lv_goods = (ListView) convertView
					.findViewById(R.id.lv_goods);
			//订单信息
			holder.line_orderinfo = (LinearLayout) convertView
					.findViewById(R.id.line_orderinfo);
			holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
			holder.tv_jiage = (TextView) convertView
					.findViewById(R.id.tv_jiage);
			//待评价
			holder.line_daipingjia = (LinearLayout) convertView
					.findViewById(R.id.line_daipingjia);
			holder.tv_chakanjuanma = (TextView) convertView
					.findViewById(R.id.tv_chakanjuanma);
			holder.tv_qupingjia = (TextView) convertView
					.findViewById(R.id.tv_qupingjia);
			//代付款
			holder.line_daifukuan = (LinearLayout) convertView
					.findViewById(R.id.line_daifukuan);
			holder.tv_shanchu = (TextView) convertView
					.findViewById(R.id.tv_shanchu);
			holder.tv_quxiao = (TextView) convertView
					.findViewById(R.id.tv_quxiao);
			holder.tv_quzhifu = (TextView) convertView
					.findViewById(R.id.tv_quzhifu);
			//待收货
			holder.line_daishouhuo = (LinearLayout) convertView
					.findViewById(R.id.line_daishouhuo);
			holder.tv_wuliu = (TextView) convertView
					.findViewById(R.id.tv_wuliu);
			holder.tv_shouhuo = (TextView) convertView
					.findViewById(R.id.tv_shouhuo);
			//申请退款
			holder.line_tuikuan = (LinearLayout) convertView
					.findViewById(R.id.line_tuikuan);
			holder.tv_tuikuan = (TextView) convertView
					.findViewById(R.id.tv_tuikuan);
			//已完成-未评价
			holder.line_weipingjia = (LinearLayout) convertView
					.findViewById(R.id.line_weipingjia);
			holder.tv_tuihuo = (TextView) convertView
					.findViewById(R.id.tv_tuihuo);
			holder.tv_finish_pingjia = (TextView) convertView
					.findViewById(R.id.tv_finish_pingjia);

			temp.put(position, convertView);
			convertView.setTag(holder);
		} else {
			convertView = temp.get(position);
			holder = (ViewHolder) convertView.getTag();
		}

		
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(context);
		final String order = data.get(position).getOrder_id();
		
		// 0 普通商品 1套餐(废弃) 2先消费后买单 3餐餐抢套餐
		union_type = data.get(position).getUnion_type();
		holder.tv_shopname.setText(data.get(position).getStore_name());
		String status = data.get(position).getOrder_state();
		String xsstatus = "";
		// 0(已取消)10(默认):待付款;20:已付款;30:待收货;40:已完成;
		if ("0".equals(status)) {
			xsstatus = "已取消";
			holder.line_orderinfo.setVisibility(View.VISIBLE);
			holder.line_daifukuan.setVisibility(View.VISIBLE);
			holder.tv_shanchu.setVisibility(View.VISIBLE);
			holder.tv_quxiao.setVisibility(View.GONE);
			holder.tv_quzhifu.setVisibility(View.GONE);
			holder.line_daipingjia.setVisibility(View.GONE);
			holder.line_daishouhuo.setVisibility(View.GONE);
			holder.line_tuikuan.setVisibility(View.GONE);
			holder.line_weipingjia.setVisibility(View.GONE);
			showDaiFuKuanButton(holder, position);
		} else if ("10".equals(status)) {
			xsstatus = "待付款";
			holder.line_orderinfo.setVisibility(View.VISIBLE);
			holder.line_daifukuan.setVisibility(View.VISIBLE);
			holder.tv_quxiao.setVisibility(View.VISIBLE);
			holder.tv_shanchu.setVisibility(View.VISIBLE);
			holder.tv_quzhifu.setVisibility(View.VISIBLE);
			holder.line_daipingjia.setVisibility(View.GONE);
			holder.tv_chakanjuanma.setVisibility(View.GONE);
			holder.line_daishouhuo.setVisibility(View.GONE);
			holder.line_tuikuan.setVisibility(View.GONE);
			holder.line_weipingjia.setVisibility(View.GONE);
			showDaiFuKuanButton(holder, position);
		} else if ("20".equals(status)) {
			xsstatus = "已付款";
			holder.line_daishouhuo.setVisibility(View.GONE);
			holder.line_daifukuan.setVisibility(View.GONE);
			holder.line_weipingjia.setVisibility(View.GONE);
			if ("0".equals(union_type)) {
				holder.line_orderinfo.setVisibility(View.VISIBLE);
				holder.line_tuikuan.setVisibility(View.VISIBLE);
				holder.line_daipingjia.setVisibility(View.GONE);
				showTuiKuan(holder, position);
			}else if ("2".equals(union_type)) {
				holder.line_orderinfo.setVisibility(View.GONE);
				holder.line_tuikuan.setVisibility(View.GONE);
				holder.line_daipingjia.setVisibility(View.GONE);
			}else if ("3".equals(union_type)) {
				xsstatus = "待使用";
				holder.line_orderinfo.setVisibility(View.VISIBLE);
				holder.line_tuikuan.setVisibility(View.GONE);
				holder.line_daipingjia.setVisibility(View.VISIBLE);
				holder.tv_qupingjia.setVisibility(View.GONE);
				holder.tv_chakanjuanma.setVisibility(View.VISIBLE);
				showChaKanJuanMa(holder, position);
			}
		} else if ("30".equals(status)) {
			xsstatus = "待收货";
			holder.line_orderinfo.setVisibility(View.VISIBLE);
			holder.line_daishouhuo.setVisibility(View.VISIBLE);
			holder.line_daifukuan.setVisibility(View.GONE);
			holder.line_daipingjia.setVisibility(View.GONE);
			holder.line_tuikuan.setVisibility(View.GONE);
			holder.line_weipingjia.setVisibility(View.GONE);
			showDaiShouhuo(holder, position);
		} else if ("40".equals(status)) {
			xsstatus = "已完成";
			//0未评价，1已评价
			String sfpingjia = data.get(position).getEvaluation_state();
			if ("0".equals(sfpingjia)) {
				if ("0".equals(union_type)) {
					holder.line_orderinfo.setVisibility(View.VISIBLE);
					holder.line_weipingjia.setVisibility(View.VISIBLE);
					holder.tv_tuihuo.setVisibility(View.VISIBLE);
					holder.tv_finish_pingjia.setVisibility(View.VISIBLE);
					holder.line_daipingjia.setVisibility(View.GONE);
					holder.line_daishouhuo.setVisibility(View.GONE);
					holder.line_daifukuan.setVisibility(View.GONE);
					holder.line_tuikuan.setVisibility(View.GONE);
					holder.tv_chakanjuanma.setVisibility(View.GONE);
					showXSPingjiaClick(holder, position);
					showTuihuoClick(holder, position);
				} else if ("2".equals(union_type)) {
					holder.line_orderinfo.setVisibility(View.GONE);
					holder.line_daipingjia.setVisibility(View.VISIBLE);
					holder.tv_qupingjia.setVisibility(View.VISIBLE);
					holder.tv_chakanjuanma.setVisibility(View.GONE);
					holder.line_daishouhuo.setVisibility(View.GONE);
					holder.line_daifukuan.setVisibility(View.GONE);
					holder.line_tuikuan.setVisibility(View.GONE);
					holder.line_weipingjia.setVisibility(View.GONE);
					showpingjiaClick(holder, position);

				} else if ("3".equals(union_type)) {
					holder.line_orderinfo.setVisibility(View.VISIBLE);
					holder.line_daipingjia.setVisibility(View.VISIBLE);
					holder.tv_chakanjuanma.setVisibility(View.VISIBLE);
					holder.tv_qupingjia.setVisibility(View.VISIBLE);
					holder.line_daishouhuo.setVisibility(View.GONE);
					holder.line_daifukuan.setVisibility(View.GONE);
					holder.line_tuikuan.setVisibility(View.GONE);
					holder.line_weipingjia.setVisibility(View.GONE);
					showChaKanJuanMa(holder, position);
					showpingjiaClick(holder, position);
				}
			}else if ("1".equals(sfpingjia)) {
				if ("0".equals(union_type)) {
					holder.line_orderinfo.setVisibility(View.VISIBLE);
					holder.line_weipingjia.setVisibility(View.VISIBLE);
					holder.tv_tuihuo.setVisibility(View.VISIBLE);
					holder.tv_finish_pingjia.setVisibility(View.GONE);
					holder.line_daipingjia.setVisibility(View.GONE);
					holder.line_daishouhuo.setVisibility(View.GONE);
					holder.line_daifukuan.setVisibility(View.GONE);
					holder.line_tuikuan.setVisibility(View.GONE);
					holder.tv_chakanjuanma.setVisibility(View.GONE);
					showTuihuoClick(holder, position);
				} else if ("2".equals(union_type)) {
					holder.line_orderinfo.setVisibility(View.GONE);
					holder.line_daipingjia.setVisibility(View.GONE);
					holder.line_daishouhuo.setVisibility(View.GONE);
					holder.line_daifukuan.setVisibility(View.GONE);
					holder.line_tuikuan.setVisibility(View.GONE);
					holder.line_weipingjia.setVisibility(View.GONE);
				} else if ("3".equals(union_type)) {
					holder.line_orderinfo.setVisibility(View.VISIBLE);
					holder.line_daipingjia.setVisibility(View.VISIBLE);
					holder.tv_chakanjuanma.setVisibility(View.VISIBLE);
					holder.tv_qupingjia.setVisibility(View.GONE);
					holder.line_daishouhuo.setVisibility(View.GONE);
					holder.line_daifukuan.setVisibility(View.GONE);
					holder.line_tuikuan.setVisibility(View.GONE);
					holder.line_weipingjia.setVisibility(View.GONE);
					showChaKanJuanMa(holder, position);
				}
			}
		}
		holder.tv_status.setText(xsstatus);
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
		holder.lv_goods.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String order_id = alldindangoodsadapter.getData().get(arg2).getOrder_id();
				String union_type = alldindangoodsadapter.getData().get(arg2).getUnion_type();
				if (mDinDanDateListener != null) {
					mDinDanDateListener.dinDanDate(order_id, union_type);
				}
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
			}
		});
		return convertView;
	}
	//普通商品去评价
	private void showXSPingjiaClick(ViewHolder holder, final int position) {
		// TODO Auto-generated method stub
		holder.tv_finish_pingjia.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Integer.parseInt(data.get(position).getGoods_num()) > 1) {
//						Intent intent = new Intent(context,
//								XSQuPingJiaActivity.class);
//						intent.putParcelableArrayListExtra("list",
//								data.get(position).getGoods_list());
//						intent.putExtra("order_id", data.get(position)
//								.getOrder_id());
//						context.startActivity(intent);
						
						ArrayList<AllDinDanGoodsBean> list = data.get(position).getGoods_list();
						String order_id = data.get(position).getOrder_id();
						mDinDanqpjduoDateListener.dinDanqpjduoDate(list, order_id);
						
				} else {
//					Intent intent = new Intent(context, QuPingJiaActivity.class);
//					intent.putExtra("order_id", data.get(position)
//							.getOrder_id());
//					intent.putExtra("goods_id", data.get(position)
//							.getGoods_list().get(0).getGoods_id());
//					intent.putExtra("pic", data.get(position).getGoods_list()
//							.get(0).getImage());
//					context.startActivity(intent);
					String order_id = data.get(position).getOrder_id();
					String goods_id = data.get(position).getGoods_list().get(0).getGoods_id();
					String pic = data.get(position).getGoods_list().get(0).getImage();
					mDinDanqpjDateListener.dinDanqpjDate(order_id, goods_id, pic);
					
				}
			}
		});
	}

	//去评价
	private void showpingjiaClick(ViewHolder holder, final int position) {
		// TODO Auto-generated method stub
		holder.tv_qupingjia.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Integer.parseInt(data.get(position).getGoods_num()) > 1) {
					if ("0".equals(data.get(position).getUnion_type())) {
//						Intent intent = new Intent(context,
//								XSQuPingJiaActivity.class);
//						intent.putParcelableArrayListExtra("list",
//								data.get(position).getGoods_list());
//						intent.putExtra("order_id", data.get(position)
//								.getOrder_id());
//						context.startActivity(intent);
						ArrayList<AllDinDanGoodsBean> list = data.get(position).getGoods_list();
						String order_id = data.get(position).getOrder_id();
						mDinDanqpjduoDateListener.dinDanqpjduoDate(list, order_id);
					}
				} else {
//					Intent intent = new Intent(context, QuPingJiaActivity.class);
//					intent.putExtra("order_id", data.get(position)
//							.getOrder_id());
//					intent.putExtra("goods_id", data.get(position)
//							.getGoods_list().get(0).getGoods_id());
//					intent.putExtra("pic", data.get(position).getGoods_list()
//							.get(0).getImage());
//					context.startActivity(intent);
					String order_id = data.get(position).getOrder_id();
					String goods_id = data.get(position).getGoods_list().get(0).getGoods_id();
					String pic = data.get(position).getGoods_list().get(0).getImage();
					mDinDanqpjDateListener.dinDanqpjDate(order_id, goods_id, pic);
				}
			}
		});
	}

	//退货
	private void showTuihuoClick(ViewHolder holder, final int position) {
		// TODO Auto-generated method stub
		holder.tv_tuihuo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent(context, TuikuanTuihuoActivity.class);
//				intent.putExtra("order_id", data.get(position).getOrder_id());
//				intent.putExtra("num", data.get(position).getGoods_num());
//				context.startActivity(intent);
				String order_id = data.get(position).getOrder_id();
				String num = data.get(position).getGoods_num();
				mtkthDinDanduoDateListener.tkthdinDanduoDate(order_id,num);
			}
		});
	}
	private TextView tv_ok;
	private TextView tv_dialog_cancel;
	private Dialog querenshouhuodialog;
	private void showDaiShouhuo(ViewHolder holder, final int position) {
		// TODO Auto-generated method stub
		//查看物流
		holder.tv_wuliu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, ChaKanWuLiuActivity.class);
				intent.putExtra("order_id", data.get(position).getOrder_id());
				context.startActivity(intent);
			}
		});
		//确认收货
		holder.tv_shouhuo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				View view = LayoutInflater.from(context).inflate(
						R.layout.dialog_querenshouhuo, null);
				TextView tv_url = (TextView) view.findViewById(R.id.tv_url);
				tv_dialog_cancel = (TextView) view
						.findViewById(R.id.tv_dialog_cancel);
				tv_ok = (TextView) view.findViewById(R.id.tv_ok);
				tv_url.setText("是否确认收货");

				querenshouhuodialog = new Dialog(context,
						R.style.mDialogStyle);
				querenshouhuodialog.setContentView(view);
				querenshouhuodialog.setCanceledOnTouchOutside(false);
				querenshouhuodialog.show();
				
				showDiaLogOnclick(position);
			}
		});
	}
	protected void showDiaLogOnclick(final int position) {
		// TODO Auto-generated method stub
		tv_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				querenshoushuoHttpPost(data.get(position).getOrder_id());
			}
		});

		tv_dialog_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				querenshouhuodialog.dismiss();
			}
		});
	}
	//确认收货接口
		protected void querenshoushuoHttpPost(String order_id) {
			// TODO Auto-generated method stub
			RequestParams params = new RequestParams();
			params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
			params.addBodyParameter("token", new DButil().gettoken(context));
			params.addBodyParameter("member_id",
					new DButil().getMember_id(context));
			params.addBodyParameter("order_id", order_id);
			params.addBodyParameter("type", "1");
			httpUtils.send(HttpMethod.POST, JiekouUtils.QUERENSHOUHUODINDAN, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
							handler.sendEmptyMessage(HANDLER_NET_FAILURE);
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							// TODO Auto-generated method stub
							qrshDinDanListInfo(arg0.result);

						}
					});
		}

		protected void qrshDinDanListInfo(String result) {
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


	//退款
	private void showTuiKuan(ViewHolder holder, final int position) {
		// TODO Auto-generated method stub
		holder.tv_tuikuan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent(context, TuikuanTuihuoActivity.class);
//				intent.putExtra("order_id", data.get(position).getOrder_id());
//				intent.putExtra("num", data.get(position).getGoods_num());
//				context.startActivity(intent);
				String order_id = data.get(position).getOrder_id();
				String num = data.get(position).getGoods_num();
				mtkthDinDanduoDateListener.tkthdinDanduoDate(order_id,num);
			}
		});
	}

	//查看券码
	private void showChaKanJuanMa(ViewHolder holder, final int position) {
		// TODO Auto-generated method stub
		holder.tv_chakanjuanma.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, ChaKanJuanMaActivity.class);
				intent.putExtra("order_id", data.get(position).getOrder_id());
				context.startActivity(intent);
			}
		});
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
		// 取消
		holder.tv_quxiao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				quxiaoHttpPost(position);
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

	// 取消订单
	protected void quxiaoHttpPost(int position) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token", new DButil().gettoken(context));
		params.addBodyParameter("order_id", data.get(position).getOrder_id());
		httpUtils.send(HttpMethod.POST, JiekouUtils.QUXIAODINDAN, params,
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

	// 删除订单
	protected void shanchuHttpPost(int position) {
		// TODO Auto-generated method stub
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
		LinearLayout line_orderinfo;
		TextView tv_num;
		TextView tv_jiage;
		LinearLayout line_daipingjia;
		TextView tv_chakanjuanma;
		TextView tv_qupingjia;
		LinearLayout line_daifukuan;
		TextView tv_shanchu;
		TextView tv_quxiao;
		TextView tv_quzhifu;
		LinearLayout line_daishouhuo;
		TextView tv_wuliu;
		TextView tv_shouhuo;
		LinearLayout line_tuikuan;
		TextView tv_tuikuan;
		LinearLayout line_weipingjia;
		TextView tv_tuihuo;
		TextView tv_finish_pingjia;
	}

	// 获取全部订单数据接口
	private void allDinDanHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token", new DButil().gettoken(context));
		params.addBodyParameter("member_id", new DButil().getMember_id(context));
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
	
	//订单去评价（单个）
	private DinDanqpjDateListener mDinDanqpjDateListener;
	
	public interface DinDanqpjDateListener {
		void dinDanqpjDate(String order_id,String goods_id,String pic);
	}
	public void setDinDanqpjDateListener(DinDanqpjDateListener listener) {
		mDinDanqpjDateListener = listener;
	}
	
	//订单去评价（多个）
	private DinDanqpjduoDateListener mDinDanqpjduoDateListener;
	
	public interface DinDanqpjduoDateListener {
		void dinDanqpjduoDate(ArrayList<AllDinDanGoodsBean> list, String order_id);
	}
	public void setDinDanqpjduoDateListener(DinDanqpjduoDateListener listener) {
		mDinDanqpjduoDateListener = listener;
	}
	
	//退款退货
	private TkthDinDanquoDateListener mtkthDinDanduoDateListener;
	
	public interface TkthDinDanquoDateListener {
		void tkthdinDanduoDate(String order_id,String num);
	}
	public void settkthDinDanqpjduoDateListener(TkthDinDanquoDateListener listener) {
		mtkthDinDanduoDateListener = listener;
	}
}
