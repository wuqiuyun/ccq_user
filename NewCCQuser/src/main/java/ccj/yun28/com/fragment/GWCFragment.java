package ccj.yun28.com.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.ShopcartExpandableListViewAdapter;
import ccj.yun28.com.adapter.ShopcartExpandableListViewAdapter.CheckInterface;
import ccj.yun28.com.adapter.ShopcartExpandableListViewAdapter.ModifyCountInterface;
import ccj.yun28.com.bean.gwc.GroupInfo;
import ccj.yun28.com.bean.gwc.ProductInfo;
import ccj.yun28.com.bean.gwc.mybean.GwcBaseInfo;
import ccj.yun28.com.gwc.TianxieDindanActivity;
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
 * 购物车页
 * 
 * @author meihuali
 * 
 */
public class GWCFragment extends Fragment implements CheckInterface,
		OnClickListener, ModifyCountInterface {

	private ExpandableListView exListView;
	private CheckBox cb_check_all;
	private TextView tv_total_price;
	private TextView tv_delete;
	private TextView tv_shared;
	private TextView tv_go_to_pay;
	private TextView subtitle;
	private LinearLayout line_heji;
	private LinearLayout line_bianji;

	private double totalPrice = 0.00;// 购买的商品总价
	private int totalCount = 0;// 购买的商品总数量

	private GwcBaseInfo gwclist;

	private ShopcartExpandableListViewAdapter selva;
	private List<GroupInfo> groups = new ArrayList<GroupInfo>();// 组元素数据列表
	private Map<String, List<ProductInfo>> children = new HashMap<String, List<ProductInfo>>();// 子元素数据列表

	// 获取的token值
	private String token;
	// token获取是否成功
	private boolean tag = false;
	// 删除商品弹框
	private Dialog gwc_deletedialog;
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 错误
	private static final int HANDLER_NN_FAILURE = 1;
	// 获取购物车列表获取成功
	private static final int HANDLER_GWCLIST_SUCCESS = 3;
	// 获取购删除成功
	private static final int HANDLER_GWCDELETE_SUCCESS = 4;
	// 获取购数量变化成功
	private static final int HANDLER_GWCNUMCHANGE_SUCCESS = 5;
	private static final int HANDLER_GWCDELETE_FAILURE = 6;
	// 没有数据
	private static final int HANDLER_GETINFO_FAILURE = 7;

	// 删除商品id
	private String deletecardid = "";
	// 结算商品id
	private String jiesuancardid = "";

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				line_net_error.setVisibility(View.VISIBLE);
				if (getActivity() != null) {
					Toast.makeText(getActivity(), "当前网络不可用,请检查网络",
							Toast.LENGTH_SHORT).show();
				}
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				line_net_error.setVisibility(View.VISIBLE);
				if (getActivity() != null) {
					Toast.makeText(getActivity(), "当前网络出错,请检查网络",
							Toast.LENGTH_SHORT).show();
				}
				break;
			// 获取购物车列表获取成功
			case HANDLER_GWCLIST_SUCCESS:
				line_net_error.setVisibility(View.GONE);
				dissDialog();
				virtualData();
				break;
			// 获取购删除成功
			case HANDLER_GWCDELETE_SUCCESS:
				line_net_error.setVisibility(View.GONE);
				gwcListHttpPost(getActivity());
				if (getActivity() != null) {
					if (msg.obj != null) {
						Toast.makeText(getActivity(),
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();
					}
				}

				break;
			case HANDLER_GWCDELETE_FAILURE:
				dissDialog();
				if (getActivity() != null) {
					if (msg.obj != null) {

						Toast.makeText(getActivity(),
								msg.obj.toString().trim(), Toast.LENGTH_SHORT)
								.show();
					}
				}
				break;
			case HANDLER_GWCNUMCHANGE_SUCCESS:
				line_net_error.setVisibility(View.GONE);
				dissDialog();
				break;
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				if (getActivity() != null) {
					if (msg.obj != null) {
						if ("购物车中暂无数据".equals(msg.obj.toString().trim())) {
							tv_net_error.setVisibility(View.VISIBLE);
							tv_net_error.setText("购物车中暂无数据");
							iv_net_error.setVisibility(View.GONE);
						}else{
							
							line_net_error.setVisibility(View.VISIBLE);
							Toast.makeText(getActivity(),
									msg.obj.toString().trim(), Toast.LENGTH_SHORT)
									.show();
						}
					}
				}
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_shopcart_main, null);
		line_dibu = (LinearLayout) view.findViewById(R.id.line_dibu);
		tv_net_error = (TextView) view.findViewById(R.id.tv_net_error);
		iv_net_error = (ImageView) view.findViewById(R.id.iv_net_error);
		subtitle = (TextView) view.findViewById(R.id.subtitle);
		line_net_error = (LinearLayout) view.findViewById(R.id.line_net_error);
		tv_notnet_refresh = (TextView) view
				.findViewById(R.id.tv_notnet_refresh);
		exListView = (ExpandableListView) view.findViewById(R.id.exListView);
		cb_check_all = (CheckBox) view.findViewById(R.id.all_chekbox);
		tv_total_price = (TextView) view.findViewById(R.id.tv_total_price);
		tv_shared = (TextView) view.findViewById(R.id.tv_shared);
		tv_delete = (TextView) view.findViewById(R.id.tv_delete);
		line_bianji = (LinearLayout) view.findViewById(R.id.line_bianji);
		line_heji = (LinearLayout) view.findViewById(R.id.line_heji);
		tv_go_to_pay = (TextView) view.findViewById(R.id.tv_go_to_pay);
		
		showLoading();
		gwcListHttpPost(getActivity());

		tv_notnet_refresh.setOnClickListener(this);
		subtitle.setOnClickListener(this);
		tv_delete.setOnClickListener(this);
		cb_check_all.setOnClickListener(this);
		tv_delete.setOnClickListener(this);
		tv_go_to_pay.setOnClickListener(this);

		return view;
	}

	// 获取购物车列表接口
	public void gwcListHttpPost(Context context) {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(context);
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token", new DButil().gettoken(context));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(context));
		httpUtils.send(HttpMethod.POST, JiekouUtils.GWCLIST, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						gwcListInfo(arg0.result);
					}
				});
	}

	// 购物车接口返回解析
	protected void gwcListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {

				Gson gson = new Gson();
				gwclist = gson.fromJson(result, GwcBaseInfo.class);
				line_dibu.setVisibility(View.VISIBLE);

				handler.sendEmptyMessage(HANDLER_GWCLIST_SUCCESS);

			} else {
				line_dibu.setVisibility(View.GONE);
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETINFO_FAILURE, message));

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

//	private void initEvents() {
//		showLoading();
//		gwcListHttpPost();
//
//		tv_notnet_refresh.setOnClickListener(this);
//		subtitle.setOnClickListener(this);
//		tv_delete.setOnClickListener(this);
//		cb_check_all.setOnClickListener(this);
//		tv_delete.setOnClickListener(this);
//		tv_go_to_pay.setOnClickListener(this);
//	}

	/**
	 * 其键是组元素的Id(通常是一个唯一指定组元素身份的值)
	 */
	private void virtualData() {
		groups.clear();
		children.clear();
		for (int i = 0; i < gwclist.getCart_list().size(); i++) {

			groups.add(new GroupInfo(gwclist.getCart_list().get(i).getId(),
					gwclist.getCart_list().get(i).getName()));

			List<ProductInfo> products = new ArrayList<ProductInfo>();
			for (int j = 0; j < gwclist.getCart_list().get(i).getShop().size(); j++) {

				products.add(new ProductInfo(j + "", "商品", gwclist
						.getCart_list().get(i).getShop().get(j)
						.getGoods_image_url(), gwclist.getCart_list().get(i)
						.getShop().get(j).getGoods_name(), Double
						.parseDouble(gwclist.getCart_list().get(i).getShop()
								.get(j).getGoods_price()), Integer
						.parseInt(gwclist.getCart_list().get(i).getShop()
								.get(j).getGoods_num()), gwclist.getCart_list()
						.get(i).getShop().get(j).getCart_id()));
			}
			children.put(groups.get(i).getId(), products);// 将组元素的一个唯一值，这里取Id，作为子元素List的Key
		}

		selva = new ShopcartExpandableListViewAdapter(groups, children,
				getActivity());
		selva.setCheckInterface(this);// 关键步骤1,设置复选框接口
		selva.setModifyCountInterface(this);// 关键步骤2,设置数量增减接口
		exListView.setAdapter(selva);

		for (int i = 0; i < selva.getGroupCount(); i++) {
			exListView.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
		}
	}

	@Override
	public void onClick(View v) {
		AlertDialog alert;
		switch (v.getId()) {
		case R.id.subtitle:
			String text = subtitle.getText().toString().trim();
			if ("编辑".equals(text)) {
				subtitle.setText("完成");
				tv_go_to_pay.setVisibility(View.GONE);
				line_heji.setVisibility(View.GONE);
				line_bianji.setVisibility(View.VISIBLE);
			} else {
				subtitle.setText("编辑");
				tv_go_to_pay.setVisibility(View.VISIBLE);
				line_heji.setVisibility(View.VISIBLE);
				line_bianji.setVisibility(View.GONE);
			}
			break;
		case R.id.tv_notnet_refresh:
			showLoading();
			gwcListHttpPost(getActivity());
			break;
		case R.id.all_chekbox:
			doCheckAll();
			break;
		case R.id.tv_go_to_pay:
			if (totalCount == 0) {
				Toast.makeText(getActivity(), "请选择要支付的商品", Toast.LENGTH_LONG)
						.show();
				return;
			} else {
				List<GroupInfo> tojiesuanGroups = new ArrayList<GroupInfo>();// 组元素列表
				jiesuancardid = "";
				for (int i = 0; i < groups.size(); i++) {
					GroupInfo group = groups.get(i);
					List<ProductInfo> childs = children.get(group.getId());
					for (int j = 0; j < childs.size(); j++) {
						if (childs.get(j).isChoosed()) {
							jiesuancardid = jiesuancardid
									+ gwclist.getCart_list().get(i).getShop()
											.get(j).getCart_id()
									+ "|"
									+ gwclist.getCart_list().get(i).getShop()
											.get(j).getGoods_num() + ",";
						}
					}
				}
				Intent intent = new Intent(getActivity(),
						TianxieDindanActivity.class);
				intent.putExtra("ifcart", "1");
				intent.putExtra("cart_id", jiesuancardid);
				startActivity(intent);
			}

			break;
		case R.id.tv_delete:
			if (totalCount == 0) {
				Toast.makeText(getActivity(), "请选择要移除的商品", Toast.LENGTH_LONG)
						.show();
				return;
			} else {
				View view = LayoutInflater.from(getActivity()).inflate(
						R.layout.dialog_gwc_delete, null);
				TextView tv_dialog_cancel = (TextView) view
						.findViewById(R.id.tv_dialog_cancel);
				TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
				tv_dialog_cancel.setOnClickListener(this);
				tv_ok.setOnClickListener(this);
				gwc_deletedialog = new Dialog(getActivity(),
						R.style.mDialogStyle);
				gwc_deletedialog.setContentView(view);
				gwc_deletedialog.setCanceledOnTouchOutside(false);
				gwc_deletedialog.show();
			}
			break;
		case R.id.tv_dialog_cancel:
			gwc_deletedialog.dismiss();
			break;
		case R.id.tv_ok:
			doDelete();
			gwc_deletedialog.dismiss();
			break;
		}
	}

	// 购物车删除接口请求
	private void gwcDeleteHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(getActivity());
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("member_id",
				new DButil().getMember_id(getActivity()));
		params.addBodyParameter("cart_id", deletecardid);
		httpUtils.send(HttpMethod.POST, JiekouUtils.GWCDELETE, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						gwcDeletejsoninfo(arg0.result);
					}
				});
	}

	// 购物车删除接口返回解析
	protected void gwcDeletejsoninfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			String message = object.getString("message");
			if ("200".equals(code)) {
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GWCDELETE_SUCCESS, message));

			} else {
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GWCDELETE_FAILURE, message));

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	/**
	 * 删除操作<br>
	 * 1.不要边遍历边删除，容易出现数组越界的情况<br>
	 * 2.现将要删除的对象放进相应的列表容器中，待遍历完后，以removeAll的方式进行删除
	 */
	protected void doDelete() {
		List<GroupInfo> toBeDeleteGroups = new ArrayList<GroupInfo>();// 待删除的组元素列表
		for (int i = 0; i < groups.size(); i++) {
			GroupInfo group = groups.get(i);
			if (group.isChoosed()) {

				toBeDeleteGroups.add(group);
			}
			List<ProductInfo> toBeDeleteProducts = new ArrayList<ProductInfo>();// 待删除的子元素列表
			List<ProductInfo> childs = children.get(group.getId());
			for (int j = 0; j < childs.size(); j++) {
				if (childs.get(j).isChoosed()) {
					toBeDeleteProducts.add(childs.get(j));
					deletecardid = deletecardid
							+ gwclist.getCart_list().get(i).getShop().get(j)
									.getCart_id() + ",";
				}
			}
			childs.removeAll(toBeDeleteProducts);

		}
		groups.removeAll(toBeDeleteGroups);

		cb_check_all.setChecked(false);
		selva.notifyDataSetChanged();
		calculate();

		gwcDeleteHttpPost();
	}

	@Override
	public void doIncrease(int groupPosition, int childPosition,
			View showCountView, boolean isChecked) {

		ProductInfo product = (ProductInfo) selva.getChild(groupPosition,
				childPosition);
		int currentCount = product.getCount();
		currentCount++;
		product.setCount(currentCount);
		((TextView) showCountView).setText(currentCount + "");

		String numchange = gwclist.getCart_list().get(groupPosition).getShop()
				.get(childPosition).getCart_id();
		showLoading();
		numchangeHttpPost(numchange, currentCount++);

		selva.notifyDataSetChanged();
		calculate();
	}

	@Override
	public void doDecrease(int groupPosition, int childPosition,
			View showCountView, boolean isChecked) {

		ProductInfo product = (ProductInfo) selva.getChild(groupPosition,
				childPosition);
		int currentCount = product.getCount();
		if (currentCount == 1)
			return;
		currentCount--;

		product.setCount(currentCount);
		((TextView) showCountView).setText(currentCount + "");

		String numchange = gwclist.getCart_list().get(groupPosition).getShop()
				.get(childPosition).getCart_id();
		numchangeHttpPost(numchange, currentCount--);

		selva.notifyDataSetChanged();
		calculate();
	}

	// 数量变化接口请求
	private void numchangeHttpPost(String numchange, int count) {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(getActivity());
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("cart_id", numchange);
		params.addBodyParameter("quantity", count + "");
		params.addBodyParameter("member_id",
				new DButil().getMember_id(getActivity()));
		httpUtils.send(HttpMethod.POST, JiekouUtils.GWCNUMCHANGE, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						gwcnumchangeInfo(arg0.result);
					}
				});
	}

	// 数量变化接口解析
	protected void gwcnumchangeInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				handler.sendEmptyMessage(HANDLER_GWCNUMCHANGE_SUCCESS);
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(
						HANDLER_GETINFO_FAILURE, message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	@Override
	public void checkGroup(int groupPosition, boolean isChecked) {
		GroupInfo group = groups.get(groupPosition);
		List<ProductInfo> childs = children.get(group.getId());
		for (int i = 0; i < childs.size(); i++) {
			childs.get(i).setChoosed(isChecked);
		}
		if (isAllCheck())
			cb_check_all.setChecked(true);
		else
			cb_check_all.setChecked(false);
		selva.notifyDataSetChanged();
		calculate();
	}

	@Override
	public void checkChild(int groupPosition, int childPosiTion,
			boolean isChecked) {
		boolean allChildSameState = true;// 判断改组下面的所有子元素是否是同一种状态
		GroupInfo group = groups.get(groupPosition);
		List<ProductInfo> childs = children.get(group.getId());
		for (int i = 0; i < childs.size(); i++) {
			if (childs.get(i).isChoosed() != isChecked) {
				allChildSameState = false;
				break;
			}
		}
		if (allChildSameState) {
			group.setChoosed(isChecked);// 如果所有子元素状态相同，那么对应的组元素被设为这种统一状态
		} else {
			group.setChoosed(false);// 否则，组元素一律设置为未选中状态
		}

		if (isAllCheck())
			cb_check_all.setChecked(true);
		else
			cb_check_all.setChecked(false);
		selva.notifyDataSetChanged();
		calculate();
	}

	private boolean isAllCheck() {

		for (GroupInfo group : groups) {
			if (!group.isChoosed())
				return false;

		}

		return true;
	}

	/** 全选与反选 */
	private void doCheckAll() {
		for (int i = 0; i < groups.size(); i++) {
			groups.get(i).setChoosed(cb_check_all.isChecked());
			GroupInfo group = groups.get(i);
			List<ProductInfo> childs = children.get(group.getId());
			for (int j = 0; j < childs.size(); j++) {
				childs.get(j).setChoosed(cb_check_all.isChecked());
			}
		}
		selva.notifyDataSetChanged();
		calculate();
	}

	/**
	 * 统计操作<br>
	 * 1.先清空全局计数器<br>
	 * 2.遍历所有子元素，只要是被选中状态的，就进行相关的计算操作<br>
	 * 3.给底部的textView进行数据填充
	 */
	private void calculate() {
		totalCount = 0;
		totalPrice = 0.00;
		for (int i = 0; i < groups.size(); i++) {
			GroupInfo group = groups.get(i);
			List<ProductInfo> childs = children.get(group.getId());
			for (int j = 0; j < childs.size(); j++) {
				ProductInfo product = childs.get(j);
				if (product.isChoosed()) {
					totalCount++;
					totalPrice += product.getPrice() * product.getCount();
				}
			}
		}
		tv_total_price.setText("￥" + totalPrice);
		tv_go_to_pay.setText("去支付(" + totalCount + ")");
	}

	// 加载中动画
	private Dialog loadingDialog;
	private LinearLayout line_dibu;
	private LinearLayout line_net_error;
	private TextView tv_notnet_refresh;
	private TextView tv_net_error;
	private ImageView iv_net_error;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(getActivity(), R.style.mDialogStyle);
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
}
