package ccj.yun28.com.sy.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.LikeGridAdapter;
import ccj.yun28.com.adapter.SPGGLlistAdapter;
import ccj.yun28.com.adapter.SPGGLlistAdapter.ObtainDateListener;
import ccj.yun28.com.bean.ZhuProductBean;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.gwc.TianxieDindanActivity;
import ccj.yun28.com.lunbotu.ADInfo;
import ccj.yun28.com.lunbotu.ImageCycleView;
import ccj.yun28.com.lunbotu.ImageCycleView.ImageCycleViewListener;
import ccj.yun28.com.mine.VIPhuiyuanActivity;
import ccj.yun28.com.sy.ProductDetailActivity;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.ImageUtil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.CircleImageView;
import ccj.yun28.com.view.MyGridView;
import ccj.yun28.com.view.MyListView;
import ccj.yun28.com.view.MyScrollView;
import ccj.yun28.com.view.OnRefreshListener;
import ccj.yun28.com.view.star.StarLayoutParams;
import ccj.yun28.com.view.star.StarLinearLayout;
import ccj.yun28.com.view.zdygridview.Tag;
import ccj.yun28.com.view.zdygridview.TagListView;
import cn.sharesdk.framework.ShareSDK;

import com.example.ccqsharesdk.onekeyshare.OnekeyShare;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 商品详情-- 商品
 * 
 * @author meihuali
 * 
 */
public class ProductFragment extends Fragment implements OnClickListener,
		ObtainDateListener, OnItemClickListener {

//	MainActivity1 activity = (MainActivity1) getActivity();

	// 数量
	int zhnum = 1;
	private int nowPage = 1;

	private MyScrollView scrollView;
	// 商品图
	private ImageCycleView ad_view;
	// 商品名
	private TextView tv_pro_name;
	// 销售价
	private TextView tv_price;
	// 分享
	private LinearLayout line_share;
	// 原价
	private TextView tv_yuan_price;
	// 快递
	private TextView tv_kd;
	// 月销量
	private TextView tv_yxl;
	// 库存
	private TextView tv_kucun;
	// 规格
	private RelativeLayout relative_guige;
	// 店铺图
	private ImageView iv_shop;
	// 店铺名
	private TextView tv_shopname;
	// 好评率
	private TextView tv_hpl;
	private LinearLayout line_pinglun;
	// 评论人数
	private TextView tv_plr_num;
	// 好平率
	private TextView tv_hpl2;
	// 评论头像
	private CircleImageView img_circle;
	// 评论人名称
	private TextView tv_user_name;
	// 评论内容
	private TextView tv_pl;
	//
	private TextView tv_fenshu;
	// 查看全部评论
	private TextView tv_allpl;
	// 热销推荐
	private MyGridView gv_hotsale;
	// 解析完成
	private ZhuProductBean zhuproduct;
	// 热销推荐
	private List<Map<String, String>> hottuijianList;

	private LikeGridAdapter likegridadapter;

	private final List<Tag> mTags = new ArrayList<Tag>();

	// 轮播图
	private ArrayList<ADInfo> infos = new ArrayList<ADInfo>();
	// 网络异常
	protected static final int HANDLER_NET_FAILURE = 0;
	// 错误
	private static final int HANDLER_NN_FAILURE = 1;
	// 商品详情解析成功
	private static final int HANDLER_PRODUCT_SUCCESS = 2;
	// 热销推荐解析成功
	private static final int HANDLER_HOTTUIJIAN_SUCCESS = 3;
	// 加入购物车接口返回
	private static final int HANDLER_ADDGWC = 4;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 网络异常
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(getActivity(), "当前网络不可用,请检查网络", Toast.LENGTH_SHORT)
						.show();
				break;
			// 错误
			case HANDLER_NN_FAILURE:
				dissDialog();
				Toast.makeText(getActivity(), "当前网络出错,请检查网络", Toast.LENGTH_SHORT).show();
				break;
			// 商品详情解析成功
			case HANDLER_PRODUCT_SUCCESS:
				dissDialog();
				break;
			// 商品详情解析成功
			case HANDLER_HOTTUIJIAN_SUCCESS:
				scrollView.onResfreshFinish();
				dissDialog();
				likegridadapter.NotifyList(hottuijianList);
				likegridadapter.notifyDataSetChanged();
				break;
			// 加入购物车接口返回
			case HANDLER_ADDGWC:
				scrollView.onResfreshFinish();
				dissDialog();
				if (msg.obj != null) {
					String result = (String) msg.obj;
					Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT)
					.show();
					
				}
				break;

			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_product, null);

		scrollView = (MyScrollView) view.findViewById(R.id.scrollView);
		ad_view = (ImageCycleView) view.findViewById(R.id.ad_view);
		// 商品名
		tv_pro_name = (TextView) view.findViewById(R.id.tv_pro_name);
		// 销售价
		tv_price = (TextView) view.findViewById(R.id.tv_price);
		// 分享
		line_share = (LinearLayout) view.findViewById(R.id.line_share);
		// 原价
		tv_yuan_price = (TextView) view.findViewById(R.id.tv_yuan_price);
		// 快递
		tv_kd = (TextView) view.findViewById(R.id.tv_kd);
		// 月销量
		tv_yxl = (TextView) view.findViewById(R.id.tv_yxl);
		// 库存
		tv_kucun = (TextView) view.findViewById(R.id.tv_kucun);
		// 规格
		relative_guige = (RelativeLayout) view
				.findViewById(R.id.relative_guige);
		// 店铺图
		iv_shop = (ImageView) view.findViewById(R.id.iv_shop);
		// 店铺名
		tv_shopname = (TextView) view.findViewById(R.id.tv_shopname);
		// 好评率
		tv_hpl = (TextView) view.findViewById(R.id.tv_hpl);
		line_pinglun = (LinearLayout) view.findViewById(R.id.line_pinglun);
		// 评论人数
		tv_plr_num = (TextView) view.findViewById(R.id.tv_plr_num);
		// 好平率
		tv_hpl2 = (TextView) view.findViewById(R.id.tv_hpl2);
		// 评论头像
		img_circle = (CircleImageView) view.findViewById(R.id.img_circle);
		// 评论人名称
		tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
		// 评论内容
		tv_pl = (TextView) view.findViewById(R.id.tv_pl);
		starsLayout = (StarLinearLayout) view.findViewById(R.id.starsLayout);
		tv_fenshu = (TextView) view.findViewById(R.id.tv_fenshu);
		// 查看全部评论
		tv_allpl = (TextView) view.findViewById(R.id.tv_allpl);
		// 热销推荐
		gv_hotsale = (MyGridView) view.findViewById(R.id.gv_hotsale);
		LinearLayout line_kefu = (LinearLayout) view
				.findViewById(R.id.line_kefu);
		TextView tv_add_gwc = (TextView) view.findViewById(R.id.tv_add_gwc);
		TextView tv_buy = (TextView) view.findViewById(R.id.tv_buy);
		
		httpUtils = new HttpUtils();
		utils = new Utils();
		verstring = utils.getVersionInfo(getActivity());

		hottuijianList = new ArrayList<Map<String, String>>();

		likegridadapter = new LikeGridAdapter(getActivity());
		gv_hotsale.setAdapter(likegridadapter);

		myDB = new DBHelper(getActivity());

		showLoading();
		String goods_id = ((ProductDetailActivity) getActivity()).getGoods_id();
		productDetailHttp(goods_id);
		guesslikeHttpPost();
		sfVIPHttpPost();

		gv_hotsale.setFocusable(false);
		line_kefu.setOnClickListener(this);
		line_share.setOnClickListener(this);
		relative_guige.setOnClickListener(this);
		tv_allpl.setOnClickListener(this);
		tv_add_gwc.setOnClickListener(this);
		tv_buy.setOnClickListener(this);
		gv_hotsale.setOnItemClickListener(this);
		initListView();

		return view;
	}

	private void starsTest(String num) {
		// TODO Auto-generated method stub
		int nu = Integer.parseInt(num);
		StarLayoutParams params = new StarLayoutParams();
		params.setNormalStar(getResources().getDrawable(R.drawable.starh))
				.setSelectedStar(getResources().getDrawable(R.drawable.stard))
				.setSelectable(false).setSelectedStarNum(nu).setTotalStarNum(5)
				.setStarHorizontalSpace(20);
		starsLayout.setStarParams(params);
	}

	private void showShare() {
		ShareSDK.initSDK(getActivity(), "171a7e7c3c736");
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// 信息分享时电话
		oks.setAddress(zhuproduct.getData().getStore_info().getStore_tel());
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("餐餐抢【"
				+ zhuproduct.getData().getGoods_info().getGoods_name() + "】");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(SharedUtil.getStringValue(
				SharedCommon.MALL_QRCODE_IMG_AZ_URL, ""));
		// text是分享文本，所有平台都需要这个字段
		oks.setText("只要 ¥ "
				+ zhuproduct.getData().getGoods_info()
						.getGoods_promotion_price() + " ，隔壁老王在餐餐抢买了这个，你也看看吧~~");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath(zhuproduct.getData().getGoods_image_mobile().get(0));// 确保SDcard下面存在此张图片
		oks.setImageUrl(zhuproduct.getData().getGoods_image_mobile().get(0));
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(SharedUtil.getStringValue(
				SharedCommon.MALL_QRCODE_IMG_AZ_URL, ""));
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(SharedUtil.getStringValue(
				SharedCommon.MALL_QRCODE_IMG_AZ_URL, ""));

		// 启动分享GUI
		oks.show(getActivity());
	}

	/**
	 * @description 填充底部商品listview
	 **/
	private void initListView() {
		scrollView.getView();
		scrollView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadMoring() {
				// TODO Auto-generated method stub
				try {
					nowPage++;
					guesslikeHttpPost();
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(HANDLER_NN_FAILURE);
				}

			}
		});
	}

	// 热销推荐数据
	private void guesslikeHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("page", nowPage + "");
		params.addBodyParameter("type", "3");
		params.addBodyParameter("member_id",
				new DButil().getMember_id(getActivity()));
		httpUtils.send(HttpMethod.POST, JiekouUtils.GUESSLIKE, params,
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
						guesslikeJsonInfo(arg0.result);
					}
				});
	}

	// 解析猜你喜欢返回的数据
	protected void guesslikeJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				JSONArray data = object.getJSONArray("data");
				for (int i = 0; i < data.length(); i++) {
					JSONObject json = data.getJSONObject(i);
					guesslikeListInfo(json);
				}
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(HANDLER_ADDGWC,
						message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private void guesslikeListInfo(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			String goods_id = json.getString("goods_id");
			String goods_name = json.getString("goods_name");
			String goods_price = json.getString("goods_price");
			String goods_image = json.getString("goods_image");
			String union_goods = json.getString("union_goods");
			String store_id = json.getString("store_id");
			Map<String, String> hottuijianMap = new HashMap<String, String>();
			hottuijianMap.put("goods_id", goods_id);
			hottuijianMap.put("goods_name", goods_name);
			hottuijianMap.put("goods_price", goods_price);
			hottuijianMap.put("goods_image", goods_image);
			hottuijianMap.put("union_goods", union_goods);
			hottuijianMap.put("store_id", store_id);
			hottuijianList.add(hottuijianMap);
			handler.sendEmptyMessage(HANDLER_HOTTUIJIAN_SUCCESS);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 请求商品详情接口
	private void productDetailHttp(String id) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("goods_id", id);
		httpUtils.send(HttpMethod.POST, JiekouUtils.PRODUCTDETAIL, params,
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
						productDetailJsonInfo(arg0.result);
					}
				});
	}

	protected void productDetailJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String status = object.getString("code");
			if ("200".equals(status)) {
				JSONObject job = object.getJSONObject("data");
				Gson gson = new Gson();
				zhuproduct = gson.fromJson(result, ZhuProductBean.class);

				if (zhuproduct.getData().getGoods_info() != null) {
					tv_pro_name.setText(zhuproduct.getData().getGoods_info()
							.getGoods_name());
					tv_price.setText(zhuproduct.getData().getGoods_info()
							.getGoods_promotion_price());
					tv_yuan_price.setText("原价： "
							+ zhuproduct.getData().getGoods_info()
									.getGoods_marketprice());
					tv_yuan_price.getPaint().setFlags(
							Paint.STRIKE_THRU_TEXT_FLAG);
					tv_kd.setText(zhuproduct.getData().getGoods_info()
							.getGoods_freight());
					tv_yxl.setText(zhuproduct.getData().getGoods_info()
							.getGoods_salenum());
					tv_kucun.setText("库存： "
							+ zhuproduct.getData().getGoods_info()
									.getGoods_storage());
					provip = zhuproduct.getData().getGoods_info().getVip();
				}
				BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
				if (zhuproduct.getData().getStore_info() != null) {
					bitmapUtils.display(iv_shop, zhuproduct.getData()
							.getStore_info().getStore_img());
					tv_shopname.setText(zhuproduct.getData().getStore_info()
							.getStore_name());
					tv_hpl.setText(zhuproduct.getData().getStore_info()
							.getFeedbackrate());
				}

				tv_plr_num.setText(zhuproduct.getData().getGeval_goods_num());
				if ("0".equals(zhuproduct.getData().getGeval_goods_num())) {
					line_pinglun.setVisibility(View.GONE);
				}else{
					line_pinglun.setVisibility(View.VISIBLE);
				}

				
				if (zhuproduct.getData().getGoods_evaluate().size() > 0) {
					tv_hpl2.setText(zhuproduct.getData().getGoods_evaluate()
							.get(0).getFeedbackrate());
					bitmapUtils.display(iv_shop, zhuproduct.getData()
							.getGoods_evaluate().get(0).getAvator());
					tv_user_name.setText(zhuproduct.getData()
							.getGoods_evaluate().get(0)
							.getGeval_frommembername());
					tv_pl.setText(zhuproduct.getData().getGoods_evaluate()
							.get(0).getGeval_content());
					tv_fenshu.setText(zhuproduct.getData().getGoods_evaluate()
							.get(0).getGeval_scores()
							+ "分");
					if ("0.5".equals(zhuproduct.getData().getGoods_evaluate()
							.get(0).getGeval_scores())) {
						starsTest("1");
					} else if ("1.5".equals(zhuproduct.getData()
							.getGoods_evaluate().get(0).getGeval_scores())) {
						starsTest("2");
					} else if ("2.5".equals(zhuproduct.getData()
							.getGoods_evaluate().get(0).getGeval_scores())) {
						starsTest("3");
					} else if ("3.5".equals(zhuproduct.getData()
							.getGoods_evaluate().get(0).getGeval_scores())) {
						starsTest("4");
					} else if ("4.5".equals(zhuproduct.getData()
							.getGoods_evaluate().get(0).getGeval_scores())) {
						starsTest("5");
					} else {
						starsTest(zhuproduct.getData().getGoods_evaluate()
								.get(0).getGeval_scores());
					}
				} else {
					tv_pl.setText("暂无评论");
					tv_fenshu.setText("5分");
					starsTest("5");
				}

				if (zhuproduct.getData().getGoods_image_mobile().size() > 0) {
					// if (zhuproduct.getData().getGoods_image_mobile().size()
					// == 1) {
					//
					// }else if
					// (zhuproduct.getData().getGoods_image_mobile().size() >
					// 1){
					for (int i = 0; i < zhuproduct.getData()
							.getGoods_image_mobile().size(); i++) {
						ADInfo info = new ADInfo();
						info.setPic(zhuproduct.getData()
								.getGoods_image_mobile().get(i));
						infos.add(info);
					}
					// }
				}

				ad_view.setImageResources(infos, mAdCycleViewListener);
				// tv_time.setText(zhuproduct.getData().getGoods_evaluate().getGeval_content());
				handler.sendEmptyMessage(HANDLER_PRODUCT_SUCCESS);
			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(HANDLER_ADDGWC,
						message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	private ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {

		@Override
		public void onImageClick(ADInfo info, int position, View imageView) {
		}

		@Override
		public void displayImageWM(String imageURL, ImageView imageView) {
			try {
				ImageUtil.display(imageURL, imageView);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	};

	// 是不是VIP接口
	private void sfVIPHttpPost() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("token", new DButil().gettoken(getActivity()));
		params.addBodyParameter("member_id",
				new DButil().getMember_id(getActivity()));
		httpUtils.send(HttpMethod.POST, JiekouUtils.SFVIP, params,
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
						sfVIPListInfo(arg0.result);
					}
				});
	}

	// 是不是VIP数据解析
	protected void sfVIPListInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String code = object.getString("code");
			if ("200".equals(code)) {
				JSONObject ob = object.getJSONObject("data");
				is_vip = ob.getString("is_vip");

			} else {
				String message = object.getString("message");
				handler.sendMessage(handler.obtainMessage(HANDLER_ADDGWC,
						message));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	// 加载中动画
	private Dialog loadingDialog;

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

	// 拨打电话dialog
	private Dialog calldialog;

	private TagListView mTagListView;

	private TextView tv_num;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stubonc

		switch (v.getId()) {
		case R.id.line_kefu:

			View view = LayoutInflater.from(getActivity()).inflate(
					R.layout.dialog_call, null);
			TextView tv_dialog_cancel = (TextView) view
					.findViewById(R.id.tv_dialog_cancel);
			TextView tv_phone_num = (TextView) view
					.findViewById(R.id.tv_phone_num);
			tv_phone_num.setText("是否拨打： "
					+ zhuproduct.getData().getStore_info().getStore_tel());
			tv_dialog_cancel.setOnClickListener(this);
			tv_phone_num.setOnClickListener(this);
			calldialog = new Dialog(getActivity(), R.style.mDialogStyle);
			calldialog.setContentView(view);
			calldialog.setCanceledOnTouchOutside(false);
			calldialog.show();
			break;
		case R.id.tv_phone_num:

			if (zhuproduct.getData().getStore_info().getStore_tel() != null
					&& !"".equals(zhuproduct.getData().getStore_info()
							.getStore_tel())
					&& !"null".equals(zhuproduct.getData().getStore_info()
							.getStore_tel())) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ zhuproduct.getData().getStore_info().getStore_tel()));
				getActivity().startActivity(intent);
			} else {
				Toast.makeText(getActivity(), "暂无电话", Toast.LENGTH_SHORT)
						.show();
			}

			break;
		case R.id.tv_dialog_cancel:
			calldialog.dismiss();
			break;
		case R.id.tv_allpl:
			PinjiaFragment pinjiaFragment = new PinjiaFragment();
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.add(R.id.DetailLayout, pinjiaFragment);
			ft.commit();
			break;
		case R.id.line_share:
			showShare();
			break;
		case R.id.relative_guige:
			showGuiGeDialog();
			break;
		case R.id.tv_add_gwc:
			if (zhuproduct.getData().getGoods_info().getSpec_name().size() < 1) {
				// 是否登录
				if (isLogin()) {
					// 商品是否登录
					if ("1".equals(provip)) {
						// 用户是否登录
						if ("1".equals(is_vip)) {
							String dqgoods_id = zhuproduct.getData()
									.getGoods_info().getGoods_id();
							showLoading();
							addGwcHttpPost(dqgoods_id);
						} else {
							Intent intent = new Intent(getActivity(),
									VIPhuiyuanActivity.class);
							startActivity(intent);
						}
					} else {
						String dqgoods_id = zhuproduct.getData()
								.getGoods_info().getGoods_id();
						showLoading();
						addGwcHttpPost(dqgoods_id);
					}
				} else {
					Intent intent = new Intent(getActivity(),
							LoginActivity.class);
					startActivity(intent);
				}
			} else {
				showGuiGeDialog();
			}
			break;
		case R.id.tv_gg_add:
			String stnum = tv_num.getText().toString().trim();
			int inum = Integer.parseInt(stnum);
			tv_num.setText(++inum + "");
			zhnum = inum;
			break;
		case R.id.tv_gg_reduce:
			String stjnum = tv_num.getText().toString().trim();
			int ijnum = Integer.parseInt(stjnum);
			if (ijnum == 1) {
				Toast.makeText(getActivity(), "数量不能小于1", Toast.LENGTH_SHORT)
						.show();
			} else {
				tv_num.setText(--ijnum + "");
				zhnum = ijnum;
			}
			break;
		case R.id.tv_buy:
			if (zhuproduct.getData().getGoods_info().getSpec_name().size() < 1) {
				// 是否登录
				if (isLogin()) {
					// 商品是否vip
					if ("1".equals(provip)) {
						// 用户是否vip
						if ("1".equals(is_vip)) {
							Intent ddintent = new Intent(getActivity(),
									TianxieDindanActivity.class);
							String dqgoods_id = zhuproduct.getData()
									.getGoods_info().getGoods_id();
							ddintent.putExtra("ifcart", "0");
							ddintent.putExtra("cart_id", dqgoods_id + "|"
									+ zhnum);
							startActivity(ddintent);
						} else {
							Intent intent = new Intent(getActivity(),
									VIPhuiyuanActivity.class);
							startActivity(intent);
						}
					} else {
						Intent ddintent = new Intent(getActivity(),
								TianxieDindanActivity.class);
						String dqgoods_id = zhuproduct.getData()
								.getGoods_info().getGoods_id();
						ddintent.putExtra("ifcart", "0");
						ddintent.putExtra("cart_id", dqgoods_id + "|" + zhnum);
						startActivity(ddintent);
					}
				} else {
					Intent intent = new Intent(getActivity(),
							LoginActivity.class);
					startActivity(intent);
				}
			} else {
				showGuiGeDialog();
			}

			break;
		case R.id.tv_gg_addgwc:
			if (isLogin()) {
				// 商品是否vip
				if ("1".equals(provip)) {
					// 用户是否vip
					if ("1".equals(is_vip)) {
						int gg_size = zhuproduct.getData().getSpec_list()
								.size();
						if (gg_size <= 1) {
							String dqgoods_id = zhuproduct.getData()
									.getGoods_info().getGoods_id();
							guigeDialog.dismiss();
							showLoading();
							addGwcHttpPost(dqgoods_id);
						} else {
							guigeDialog.dismiss();
							showLoading();
							addGwcHttpPost(zdgoods_id);
						}
					} else {
						Intent intent = new Intent(getActivity(),
								VIPhuiyuanActivity.class);
						startActivity(intent);
					}
				} else {
					int gg_size = zhuproduct.getData().getSpec_list().size();
					if (gg_size <= 1) {
						String dqgoods_id = zhuproduct.getData()
								.getGoods_info().getGoods_id();
						guigeDialog.dismiss();
						showLoading();
						addGwcHttpPost(dqgoods_id);
					} else {
						guigeDialog.dismiss();
						showLoading();
						addGwcHttpPost(zdgoods_id);
					}
				}
			} else {
				guigeDialog.dismiss();
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
			}
			break;
		case R.id.tv_gg_buy:
			guigeDialog.dismiss();
			if (isLogin()) {
				// 商品是否vip
				if ("1".equals(provip)) {
					// 用户是否vip
					if ("1".equals(is_vip)) {
						int gg_size = zhuproduct.getData().getSpec_list()
								.size();
						if (gg_size <= 1) {
							Intent ddintent = new Intent(getActivity(),
									TianxieDindanActivity.class);
							String dqgoods_id = zhuproduct.getData()
									.getGoods_info().getGoods_id();

							ddintent.putExtra("ifcart", "0");
							ddintent.putExtra("cart_id", dqgoods_id + "|"
									+ zhnum);
							startActivity(ddintent);
						} else {
							Intent ddintent = new Intent(getActivity(),
									TianxieDindanActivity.class);
							String dqgoods_id = zhuproduct.getData()
									.getGoods_info().getGoods_id();

							ddintent.putExtra("ifcart", "0");
							ddintent.putExtra("cart_id", zdgoods_id + "|"
									+ zhnum);
							startActivity(ddintent);
						}
					} else {
						Intent intent = new Intent(getActivity(),
								VIPhuiyuanActivity.class);
						startActivity(intent);
					}
				} else {
					int gg_size = zhuproduct.getData().getSpec_list().size();
					if (gg_size <= 1) {
						Intent ddintent = new Intent(getActivity(),
								TianxieDindanActivity.class);
						String dqgoods_id = zhuproduct.getData()
								.getGoods_info().getGoods_id();

						ddintent.putExtra("ifcart", "0");
						ddintent.putExtra("cart_id", dqgoods_id + "|" + zhnum);
						startActivity(ddintent);
					} else {
						Intent ddintent = new Intent(getActivity(),
								TianxieDindanActivity.class);
						String dqgoods_id = zhuproduct.getData()
								.getGoods_info().getGoods_id();

						ddintent.putExtra("ifcart", "0");
						ddintent.putExtra("cart_id", zdgoods_id + "|" + zhnum);
						startActivity(ddintent);
					}
				}
			} else {
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				intent.putExtra("type", "qt");
				startActivity(intent);
			}
			break;
		case R.id.tv_kongbai:
			guigeDialog.dismiss();
			break;
		default:
			break;
		}
	}

	private void showGuiGeDialog() {
		// TODO Auto-generated method stub
		View guigeview = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_productdetail_guige, null);

		TextView tv_kongbai = (TextView) guigeview.findViewById(R.id.tv_kongbai);
		ImageView iv_gg_pic = (ImageView) guigeview
				.findViewById(R.id.iv_gg_pic);
		TextView tv_gg_name = (TextView) guigeview
				.findViewById(R.id.tv_gg_name);
		TextView tv_gg_kc = (TextView) guigeview.findViewById(R.id.tv_gg_kc);
		TextView tv_gg_addgwc = (TextView) guigeview
				.findViewById(R.id.tv_gg_addgwc);
		TextView tv_gg_buy = (TextView) guigeview.findViewById(R.id.tv_gg_buy);
		ImageView tv_gg_reduce = (ImageView) guigeview
				.findViewById(R.id.tv_gg_reduce);
		ImageView tv_gg_add = (ImageView) guigeview
				.findViewById(R.id.tv_gg_add);
		tv_num = (TextView) guigeview.findViewById(R.id.tv_num);
		MyListView listView = (MyListView) guigeview.findViewById(R.id.lv);
		SPGGLlistAdapter adapter = new SPGGLlistAdapter(zhuproduct.getData()
				.getGoods_info().getSpec_name(), zhuproduct.getData()
				.getGoods_info().getSpec_value(), getActivity());
		listView.setAdapter(adapter);
		adapter.setOnObtainDateListener(this);
		BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
		bitmapUtils.display(iv_gg_pic, zhuproduct.getData()
				.getGoods_image_mobile().get(0));
		tv_gg_name.setText(zhuproduct.getData().getGoods_info()
				.getGoods_price());
		tv_gg_kc.setText(zhuproduct.getData().getGoods_info()
				.getGoods_storage());

		tv_kongbai.setOnClickListener(this);
		tv_gg_addgwc.setOnClickListener(this);
		tv_gg_reduce.setOnClickListener(this);
		tv_gg_add.setOnClickListener(this);
		tv_gg_buy.setOnClickListener(this);
		guigeDialog = new Dialog(getActivity(), R.style.MyDialogStyle);
		// Window win = guigeDialog.getWindow();
		// win.getDecorView().setPadding(0, 0, 0, 0);
		// WindowManager.LayoutParams lp = win.getAttributes();
		//         lp.width = WindowManager.LayoutParams.FILL_PARENT;
		//         lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		//         win.setAttributes(lp);
		guigeDialog.setContentView(guigeview);
		guigeDialog.setCanceledOnTouchOutside(true);
		guigeDialog.show();
	}

	// 加入购物车
	private void addGwcHttpPost(String goods_id) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("member_id",
				new DButil().getMember_id(getActivity()));
		params.addBodyParameter("goods_id", goods_id);
		params.addBodyParameter("quantity", zhnum + "".trim());
		httpUtils.send(HttpMethod.POST, JiekouUtils.ADDGWC, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						addGwcJsonInfo(arg0.result);
					}
				});
	}

	// 加入购物车数据解析
	protected void addGwcJsonInfo(String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			String message = object.getString("message");
			handler.sendMessage(handler.obtainMessage(HANDLER_ADDGWC, message));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(HANDLER_NN_FAILURE);
		}
	}

	@Override
	public void obtainDate(Map<String, String> jhmap) {
		// TODO Auto-generated method stub
		String a = "";
		if (zhuproduct.getData().getGoods_info().getSpec_value().size() == jhmap
				.size()) {
			for (int i = 0; i < jhmap.size(); i++) {
				if (i == 0) {
					a = jhmap.get("0");
				} else {
					a = a + "|" + jhmap.get(i + "");
				}
			}

			for (int i = 0; i < zhuproduct.getData().getSpec_list().size(); i++) {
				if (zhuproduct.getData().getSpec_list().get(i).getSign()
						.equals(a.trim())) {
					zdgoods_id = zhuproduct.getData().getSpec_list().get(i)
							.getGoods_id();
				}
			}
		}
	}

	private DBHelper myDB;

	// 多规格匹配goods_id
	private String zdgoods_id;

	// 规格dislog
	private Dialog guigeDialog;

	private StarLinearLayout starsLayout;

	private String provip;

	private String is_vip;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	// 校验登录与否
	private boolean isLogin() {
		try {
			if (myDB != null) {
				SQLiteDatabase db = myDB.getReadableDatabase();
				Cursor cursor = db.rawQuery(
						"select * from user where status = 1", null);
				if (cursor == null || cursor.getCount() == 0) {
					return false;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
		intent.putExtra("goods_id", hottuijianList.get(position)
				.get("goods_id"));
		startActivity(intent);
	}

}
