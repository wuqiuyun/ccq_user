package ccj.yun28.com.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.R;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 地址列表适配器
 * 
 * @author meihuali
 * 
 */
public class MyAdressAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> list;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();
	private List<Map<String, String>> addressList;
	private Utils utils;
	private String[] verstring;

	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 异常
	protected static final int HANDLER_NN_FAILURE = 1;
	// 获取信息失败
	private static final int HANDLER_GETINFO_FAILURE = 2;
	// 获取信息成功
	private static final int HANDLER_ADDRESS_SUCCESS = 3;

	private static final int HANDLER_DELETEADDRESS_SUCCESS = 4;

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
				Toast.makeText(context, "当前网络出错,请检查网络", Toast.LENGTH_SHORT)
						.show();
				break;
			// 获取信息失败
			case HANDLER_GETINFO_FAILURE:
				dissDialog();
				Toast.makeText(context, msg.obj.toString().trim(),
						Toast.LENGTH_SHORT).show();
				break;
			// 获取地址信息成功
			case HANDLER_ADDRESS_SUCCESS:
				dissDialog();
				NotifyList(addressList);
				notifyDataSetChanged();
				break;
			// 删除地址信息成功
			case HANDLER_DELETEADDRESS_SUCCESS:
				addressList = new ArrayList<Map<String, String>>();
				addressHttpPost();
				break;
			default:
				break;
			}
		};
	};

	public MyAdressAdapter(Activity context) {
		this.context = context;
		this.list = new ArrayList<Map<String, String>>();
	}

	public void NotifyList(List<Map<String, String>> list) {
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
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
					R.layout.item_myadress, null);
			holder.iv_sf_check = (ImageView) convertView
					.findViewById(R.id.iv_sf_check);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_phone = (TextView) convertView
					.findViewById(R.id.tv_phone);
			holder.tv_adress = (TextView) convertView
					.findViewById(R.id.tv_adress);
			holder.line_edit = (LinearLayout) convertView
					.findViewById(R.id.line_edit);
			holder.line_delete = (LinearLayout) convertView
					.findViewById(R.id.line_delete);

			temp.put(position, convertView);
			convertView.setTag(holder);
		} else {
			convertView = temp.get(position);
			holder = (ViewHolder) convertView.getTag();
		}

		utils = new Utils();
		verstring = utils.getVersionInfo(context);
		
		holder.tv_name.setText(list.get(position).get("true_name"));
		holder.tv_phone.setText(list.get(position).get("mob_phone"));
		holder.tv_adress.setText(list.get(position).get("area_info")
				+ list.get(position).get("address"));
		String sfmr = list.get(position).get("is_default");
		if ("1".equals(sfmr)) {
			holder.iv_sf_check.setBackgroundResource(R.drawable.gouxred);
		} else {
			holder.iv_sf_check.setBackgroundResource(R.drawable.goux);
		}
		showEditDeleteButton(holder, position);
		return convertView;
	}

	// 删除，编辑按钮
	private void showEditDeleteButton(ViewHolder holder, final int position) {
		// TODO Auto-generated method stub
		holder.line_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent(context, AddAdressActivity.class);
				String address_id = list.get(position).get("address_id");
//				intent.putExtra("type", "edit");
//				intent.putExtra("address_id", address_id);
//				context.startActivity(intent);
				mEditAddressDateListener.editAddressDate("edit", address_id);
			}
		});
		holder.line_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showLoading();
				String address_id = list.get(position).get("address_id");
				deleteHttpPost(address_id);
			}
		});
	}

	static class ViewHolder {
		ImageView iv_sf_check;
		TextView tv_name;
		TextView tv_phone;
		TextView tv_adress;
		LinearLayout line_edit;
		LinearLayout line_delete;
	}

	// 删除地址
	protected void deleteHttpPost(String address_id) {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("member_id", new DButil().getMember_id(context));
		params.addBodyParameter("address_id", address_id);
		httpUtils.send(HttpMethod.POST, JiekouUtils.DELETEADDRESS, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e("ee", "失败:" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						deleteListInfo(arg0.result);
					}
				});
	}

	protected void deleteListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			String message = object.getString("message");
			if ("200".equals(code)) {
				handler.sendEmptyMessage(HANDLER_DELETEADDRESS_SUCCESS);
				// handler.sendMessage(handler.obtainMessage(
				// HANDLER_ADDRESS_SUCCESS, position));
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

	private void addressHttpPost() {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token", new DButil().gettoken(context));
		params.addBodyParameter("member_id", new DButil().getMember_id(context));
		httpUtils.send(HttpMethod.POST, JiekouUtils.MYADDRESS, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e("ee", "失败:" + arg1);
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						addressListInfo(arg0.result);
					}
				});
	}

	protected void addressListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				addressList.clear();
				JSONArray data = object.getJSONArray("data");
				for (int i = 0; i < data.length(); i++) {
					JSONObject json = data.getJSONObject(i);
					addressDetailListInfo(json);
				}
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

	private void addressDetailListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String address = json.getString("address");
			String address_id = json.getString("address_id");
			String area_id = json.getString("area_id");
			String area_info = json.getString("area_info");
			String city_id = json.getString("city_id");
			String dlyp_id = json.getString("dlyp_id");
			String is_default = json.getString("is_default");
			String member_id = json.getString("member_id");
			String mob_phone = json.getString("mob_phone");
			String tel_phone = json.getString("tel_phone");
			String true_name = json.getString("true_name");
			Map<String, String> addressMap = new HashMap<String, String>();
			addressMap.put("address", address);
			addressMap.put("address_id", address_id);
			addressMap.put("area_id", area_id);
			addressMap.put("area_info", area_info);
			addressMap.put("city_id", city_id);
			addressMap.put("dlyp_id", dlyp_id);
			addressMap.put("is_default", is_default);
			addressMap.put("member_id", member_id);
			addressMap.put("mob_phone", mob_phone);
			addressMap.put("tel_phone", tel_phone);
			addressMap.put("true_name", true_name);
			addressList.add(addressMap);
			handler.sendEmptyMessage(HANDLER_ADDRESS_SUCCESS);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 加载中动画
	private Dialog loadingDialog;

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

	// 编辑地址
	private EditAddressDateListener mEditAddressDateListener;

	public interface EditAddressDateListener {
		void editAddressDate(String type, String address_id);
	}

	public void setEditAddressDateListener(EditAddressDateListener listener) {
		mEditAddressDateListener = listener;
	}
}
