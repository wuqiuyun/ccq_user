package ccj.yun28.com.ccq;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.ccqsharesdk.onekeyshare.OnekeyShare;
import com.example.ccqsharesdk.onekeyshare.ShareContentCustomizeCallback;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.LoginActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.adapter.CcqProDetaillistAdapter;
import ccj.yun28.com.adapter.CcqQuanLvAdapter;
import ccj.yun28.com.bean.ZhuCCQProInfoBean;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.db.DBHelper;
import ccj.yun28.com.lunbotu.ADInfo;
import ccj.yun28.com.lunbotu.ImageCycleView;
import ccj.yun28.com.lunbotu.ImageCycleView.ImageCycleViewListener;
import ccj.yun28.com.mine.VIPhuiyuanActivity;
import ccj.yun28.com.sy.BuyZKJActivity;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.ImageUtil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.view.MyListView;
import ccj.yun28.com.view.WebviewActivity;
import ccj.yun28.com.view.star.StarLayoutParams;
import ccj.yun28.com.view.star.StarLinearLayout;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;

/**
 * 餐餐抢店铺商品详情
 *
 * @author meihuali
 */
public class NewCcqProDetailActivity extends BaseActivity implements
        OnClickListener {
    // 商品图
    private ImageCycleView ad_view;
    // 商品名字
    private TextView tv_name;
    // 折扣度
    private TextView tv_zhekou_num;
    // 销售量
    private TextView tv_sale_num;
    // 优惠价
    private TextView tv_yh_price;
    // 原价
    private TextView tv_yuan_price;
    private TextView tv_detail_shuoming;
    // 评价数
    private TextView tv_pinjia_num;
    // 地址
    private TextView tv_adress;
    private TextView tv_two_adress;
    private TextView tv_distance;
    // 有效期
    private TextView tv_yxq;
    // 消费时间
    private TextView tv_xfsj;
    // 是否有发票
    private TextView tv_sfyfp;
    // 是否需要携带身份证
    private TextView tv_sfxdsfz;
    // 是否需要预约
    private TextView tv_sfxyyy;
    // 是否有性别要求
    private TextView tv_sfyqxb;
    // 用餐人数
    private TextView tv_ycpeopel;
    // 说明
    private TextView tv_sm;
    private TextView tv_sfy_child_play;
    private TextView tv_sfy_wifi;
    private TextView tv_sfysmoke_place;
    // 商品id
    private String ccqgoods_id;
    private String ccqstore_id;

    private DBHelper myDB;
    // private CcqZheKouJuanDetaillistAdapter zheKouJuanDetaillistAdapter;
    private CcqProDetaillistAdapter ccqProDetaillistAdapter;

    // 轮播图
    private ArrayList<ADInfo> infos = new ArrayList<ADInfo>();
    // 拨打电话dialog
    private Dialog calldialog;

    // 餐餐抢商品信息
    private ZhuCCQProInfoBean ccqProInfoBean;

    // 网络异常
    protected static final int HANDLER_NET_FAILURE = 0;
    // 异常
    protected static final int HANDLER_NN_FAILURE = 1;
    // 获取信息失败
    private static final int HANDLER_GETINFO_FAILURE = 2;
    // 获取信息成功
    private static final int HANDLER_CCQPRODETAIL_SUCCESS = 3;
    // 获取是否vip成功
    private static final int HANDLER_IS_VIP_SUCCESS = 4;
    // 获取分享信息成功
    private static final int HANDLER_SHAREINFO_SUCCESS = 5;

    private static final int HANDLER_CCQPROQUAN_SUCCESS = 6;
    private static final int HANDLER_CCQPROQUAN_NOMORE = 7;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                // 网络异常
                case HANDLER_NET_FAILURE:
                    dissDialog();
                    Toast.makeText(NewCcqProDetailActivity.this, "当前网络不可用,请检查网络",
                            Toast.LENGTH_SHORT).show();
                    break;
                // 错误
                case HANDLER_NN_FAILURE:
                    dissDialog();
                    Toast.makeText(NewCcqProDetailActivity.this, "当前网络出错,请检查网络",
                            Toast.LENGTH_SHORT).show();
                    break;
                // 获取信息失败
                case HANDLER_GETINFO_FAILURE:
                    dissDialog();
                    Toast.makeText(NewCcqProDetailActivity.this,
                            msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
                    break;
                // 获取分享信息成功
                case HANDLER_SHAREINFO_SUCCESS:
                    dissDialog();
                    showShare();
                    break;
                // 获取商品信息成功
                case HANDLER_CCQPRODETAIL_SUCCESS:
                    dissDialog();
                    ccqProDetaillistAdapter.NotifyList(ccqProInfoBean.getData()
                            .getGoods_bunding_group());
                    ccqProDetaillistAdapter.notifyDataSetChanged();
                    break;
                // 获取是否vip成功
                case HANDLER_IS_VIP_SUCCESS:
                    dissDialog();
                    if ("1".equals(msg.obj.toString().trim())) {
                        Intent intent = new Intent(NewCcqProDetailActivity.this,
                                BuyZKJActivity.class);
                        intent.putExtra("goods_id", ccqgoods_id);
                        startActivity(intent);
                    } else {
                        Toast.makeText(NewCcqProDetailActivity.this, "VIP才可以购买",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(NewCcqProDetailActivity.this,
                                VIPhuiyuanActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
                // 推荐附近餐餐抢商品
                case HANDLER_CCQPROQUAN_SUCCESS:
                    dissDialog();
                    refreshLayout.finishRefresh();
                    refreshLayout.finishRefreshLoadMore();
                    ccqQuanLvAdapter.NotifyList(ccqProQuanList);
                    ccqQuanLvAdapter.notifyDataSetChanged();
                    break;
                // 没有更多
                case HANDLER_CCQPROQUAN_NOMORE:
                    dissDialog();
                    refreshLayout.finishRefresh();
                    refreshLayout.finishRefreshLoadMore();
                    ccqQuanLvAdapter.NotifyList(ccqProQuanList);
                    ccqQuanLvAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private static final int REQUESTCODE = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ccq1);
        View topView = LayoutInflater.from(NewCcqProDetailActivity.this)
                .inflate(R.layout.activity_ccqprodetail, null);
        LinearLayout fragment_ccq_store_top = (LinearLayout) findViewById(R.id.fragment_ccq_store_top);
        fragment_ccq_store_top.setVisibility(View.VISIBLE);
        // 返回
        LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
        // 分享
        LinearLayout line_fenx = (LinearLayout) findViewById(R.id.line_fenx);
        // 商品名字
        tv_name = (TextView) findViewById(R.id.tv_name);
        // 商品图片
        RelativeLayout relay_lunbo = (RelativeLayout) topView
                .findViewById(R.id.relay_lunbo);
        ad_view = (ImageCycleView) topView.findViewById(R.id.ad_view);
        tv_pro_kucun = (TextView) topView.findViewById(R.id.tv_pro_kucun);
        // 折扣度
        tv_zhekou_num = (TextView) topView.findViewById(R.id.tv_zhekou_num);
        // 销售量
        tv_sale_num = (TextView) topView.findViewById(R.id.tv_sale_num);
        // 优惠价
        tv_yh_price = (TextView) topView.findViewById(R.id.tv_yh_price);
        // 原价
        tv_yuan_price = (TextView) topView.findViewById(R.id.tv_yuan_price);
        // 一元购买
        TextView tv_yiyuan_buy = (TextView) topView
                .findViewById(R.id.tv_yiyuan_buy);
        // 更详细说明
        TextView tv_detail_shuoming = (TextView) topView
                .findViewById(R.id.tv_detail_shuoming);
        starsLayout = (StarLinearLayout) topView.findViewById(R.id.starsLayout);
        // 整个评价
        LinearLayout line_pinjia = (LinearLayout) topView
                .findViewById(R.id.line_pinjia);
        // 评价数
        tv_pinjia_num = (TextView) topView.findViewById(R.id.tv_pinjia_num);
        // 店铺信息
        tv_store_name = (TextView) topView.findViewById(R.id.tv_store_name);
        // 地址
        tv_adress = (TextView) topView.findViewById(R.id.tv_adress);
        tv_two_adress = (TextView) topView.findViewById(R.id.tv_two_adress);
        tv_distance = (TextView) topView.findViewById(R.id.tv_distance);
        // 电话
        LinearLayout line_call = (LinearLayout) topView
                .findViewById(R.id.line_call);
        // 折扣内容
        MyListView prodetail_lv = (MyListView) topView
                .findViewById(R.id.prodetail_lv);
        // 有效期
        tv_yxq = (TextView) topView.findViewById(R.id.tv_yxq);
        // 消费时间
        tv_xfsj = (TextView) topView.findViewById(R.id.tv_xfsj);
        // 是否有发票
        tv_sfyfp = (TextView) topView.findViewById(R.id.tv_sfyfp);
        // 是否需要携带身份证
        tv_sfxdsfz = (TextView) topView.findViewById(R.id.tv_sfxdsfz);
        // 是否需要预约
        tv_sfxyyy = (TextView) topView.findViewById(R.id.tv_sfxyyy);
        // 是否有性别要求
        tv_sfyqxb = (TextView) topView.findViewById(R.id.tv_sfyqxb);
        // 用餐人数
        tv_ycpeopel = (TextView) topView.findViewById(R.id.tv_ycpeopel);
        // 说明
        tv_sm = (TextView) topView.findViewById(R.id.tv_sm);
        tv_sfy_child_play = (TextView) topView
                .findViewById(R.id.tv_sfy_child_play);
        tv_sfy_wifi = (TextView) topView.findViewById(R.id.tv_sfy_wifi);
        tv_sfysmoke_place = (TextView) topView
                .findViewById(R.id.tv_sfysmoke_place);
        ImageView iv_daohang = (ImageView) topView
                .findViewById(R.id.iv_daohang);

        lv = (ListView) findViewById(R.id.lv);

        ccqQuanLvAdapter = new CcqQuanLvAdapter(NewCcqProDetailActivity.this);
        lv.setAdapter(ccqQuanLvAdapter);
        refreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setLoadMore(true);
        lv.addHeaderView(topView);

        myDB = new DBHelper(this);
        bitmapUtils = new BitmapUtils(NewCcqProDetailActivity.this);

        httpUtils = new HttpUtils();
        utils = new Utils();
        verstring = utils.getVersionInfo(NewCcqProDetailActivity.this);

        // zheKouJuanDetaillistAdapter = new CcqZheKouJuanDetaillistAdapter(
        // CcqProDetailActivity.this);
        // lv.setAdapter(zheKouJuanDetaillistAdapter);
        ccqProDetaillistAdapter = new CcqProDetaillistAdapter(
                NewCcqProDetailActivity.this);
        prodetail_lv.setAdapter(ccqProDetaillistAdapter);

        Intent intent = getIntent();
        if (getIntent() != null) {
            latitude = getIntent().getStringExtra("starlat");
            longitude = getIntent().getStringExtra("starlng");
            ccqgoods_id = getIntent().getStringExtra("ccqgoods_id");
            ccqstore_id = getIntent().getStringExtra("store_id");
            city = getIntent().getStringExtra("city");
            district = getIntent().getStringExtra("district");
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("city", city);
        map.put("district", district);
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        ccqQuanLvAdapter.setExtra(map);

        showLoading();
        ccqprodetailHttpPost();

        ccqProQuanList = new ArrayList<Map<String, String>>();
        //
        initListView();

        int[] info = utils.getWindowInfo(NewCcqProDetailActivity.this);
        // 获取当前控件的布局对象
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        // 获取当前控件的布局对象
        params.height = info[0] / 3 * 2;// 设置当前控件布局的高度
        relay_lunbo.setLayoutParams(params);

        prodetail_lv.setFocusable(false);

        tv_detail_shuoming.setOnClickListener(this);
        // iv_daohang.setOnClickListener(this);
        line_back.setOnClickListener(this);
        line_fenx.setOnClickListener(this);
        tv_yiyuan_buy.setOnClickListener(this);
        line_pinjia.setOnClickListener(this);
        line_call.setOnClickListener(this);

    }

    /**
     * 新加
     */
    private void initListView() {
        page++;
        referralGoodsHttpPost();
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {

            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                // zheli shua xin
                refreshLayout.finishRefresh();
                /*
                 * try { page=1; referralGoodsHttpPost(); } catch (Exception e)
				 * { // TODO: handle exception
				 * handler.sendEmptyMessage(HANDLER_NN_FAILURE); }
				 */
            }

            @Override
            public void onRefreshLoadMore(
                    MaterialRefreshLayout materialRefreshLayout) {
                // super.onRefreshLoadMore(materialRefreshLayout);
                try {
                    page++;
                    referralGoodsHttpPost();
                } catch (Exception e) {
                    handler.sendEmptyMessage(HANDLER_NN_FAILURE);
                }
            }
        });
    }

    /**
     * 推荐附近餐餐抢商品请求接口
     */
    private void referralGoodsHttpPost() {
        RequestParams params = new RequestParams();
        params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
                + "," + verstring[2]);
        params.addBodyParameter("api_v", "v3");

        String memberId = new DButil()
                .getMember_id(NewCcqProDetailActivity.this);
        params.addBodyParameter("member_id", memberId);
        params.addBodyParameter("goods_id", ccqgoods_id);
        params.addBodyParameter("store_id", ccqstore_id);
        params.addBodyParameter("lat1", latitude);
        params.addBodyParameter("lng1", longitude);
        params.addBodyParameter("city_id", city);
        params.addBodyParameter("narea_s_name", district);
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("goods_images_op", "!m");
        params.addBodyParameter("goods_images_size", "220x180");

        // Log.e("log","latitude:"+latitude+"    longitude:"+longitude+"");

        httpUtils.send(HttpMethod.POST, JiekouUtils.NEWREFERRALGOODS, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        handler.sendEmptyMessage(HANDLER_NET_FAILURE);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        // Log.e("log", "推荐附近餐餐抢商品请求:"+arg0.result);
                        referralGoodsJsonInfo(arg0.result);
                    }
                });

    }

    //
    protected void referralGoodsJsonInfo(String result) {
        try {
            JSONObject object = new JSONObject(result);
            String status = object.getString("code");
            if ("200".equals(status)) {
                if (page == 1) {
                    ccqProQuanList.clear();
                }
                JSONArray data = object.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject json = data.getJSONObject(i);
                    referralGoodsListInfo(json);
                }
            } else if ("300".equals(status)) {
                String message = object.getString("message");
                handler.sendMessage(handler.obtainMessage(
                        HANDLER_CCQPROQUAN_NOMORE, message));
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

    //
    private void referralGoodsListInfo(JSONObject json) {
        try {
            String goods_id = json.getString("goods_id");
            String store_id = json.getString("store_id");
            String goods_name = json.getString("goods_name");
            String goods_image = json.getString("goods_image");
            String goods_price = json.getString("goods_price");
            String goods_marketprice = json.getString("goods_marketprice");
            String goods_storage = json.getString("goods_storage");
            String store_name = json.getString("store_name");
            String store_address = json.getString("store_address");
            String distance_value = json.getString("distance_value");
            String distance = json.getString("distance");
            String discount = json.getString("discount");

            Map<String, String> ccqProQuanMap = new HashMap<String, String>();
            ccqProQuanMap.put("goods_id", goods_id);
            ccqProQuanMap.put("store_id", store_id);
            ccqProQuanMap.put("goods_name", goods_name);
            ccqProQuanMap.put("goods_image", goods_image);
            ccqProQuanMap.put("goods_price", goods_price);
            ccqProQuanMap.put("goods_marketprice", goods_marketprice);
            ccqProQuanMap.put("goods_storage", goods_storage);
            ccqProQuanMap.put("store_name", store_name);
            ccqProQuanMap.put("store_address", store_address);
            ccqProQuanMap.put("distance_value", distance_value);
            ccqProQuanMap.put("distance", distance);
            ccqProQuanMap.put("discount", discount);
            ccqProQuanList.add(ccqProQuanMap);
            handler.sendEmptyMessage(HANDLER_CCQPROQUAN_SUCCESS);
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(HANDLER_NN_FAILURE);
        }
    }

    // 餐餐抢商品详情接口请求
    private void ccqprodetailHttpPost() {
        RequestParams params = new RequestParams();
        params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
                + "," + verstring[2]);
        params.addBodyParameter("api_v", "v3");
        params.addBodyParameter("goods_id", ccqgoods_id);
        params.addBodyParameter("lat1", latitude);
        params.addBodyParameter("lng1", longitude);

        httpUtils.send(HttpMethod.POST, JiekouUtils.NewCCQPRODETAIL, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        Log.e("ee", "失败:" + arg1);
                        handler.sendEmptyMessage(HANDLER_NET_FAILURE);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        ccqprodetailListInfo(arg0.result);

                        Log.e("log", "餐餐抢商品详情数据-->arg0.result:  " + arg0.result);
                    }
                });
    }

    // 餐餐抢商品详情数据解析
    protected void ccqprodetailListInfo(String result) {
        try {
            JSONObject object = new JSONObject(result);
            String code = object.getString("code");
            if ("200".equals(code)) {
                Gson gson = new Gson();
                ccqProInfoBean = gson.fromJson(result, ZhuCCQProInfoBean.class);
                tv_name.setText(ccqProInfoBean.getData().getGoods_info()
                        .getGoods_name());
                tv_pro_kucun.setText("库存剩下： "
                        + ccqProInfoBean.getData().getGoods_info()
                        .getGoods_storage() + " 张 ");
                tv_zhekou_num.setText(ccqProInfoBean.getData()
                        .getGoods_bunding_info().getDiscount()
                        + "折");
                tv_sale_num.setText("已售："
                        + ccqProInfoBean.getData().getGoods_info()
                        .getGoods_salenum());
                tv_yh_price.setText("餐餐抢券后："
                        + ccqProInfoBean.getData().getGoods_info()
                        .getGoods_price() + "元 ");
                tv_yuan_price.setText("原价："
                        + ccqProInfoBean.getData().getGoods_info()
                        .getGoods_marketprice() + "元 ");
                tv_pinjia_num.setText(ccqProInfoBean.getData()
                        .getGeval_goods_num() + " 个评价   ");
                tv_adress.setText(ccqProInfoBean.getData().getStore_info()
                        .getLive_store_address());
                tv_adress.setText(ccqProInfoBean.getData().getStore_info()
                        .getLive_store_address());
                tv_store_name.setText(ccqProInfoBean.getData().getStore_info()
                        .getStore_name());
                store_id = ccqProInfoBean.getData().getStore_info()
                        .getStore_id();
                tv_two_adress.setText(ccqProInfoBean.getData().getStore_info()
                        .getLive_store_address());
                tv_distance.setText(ccqProInfoBean.getData().getStore_info()
                        .getM());
                tv_sm.setText(ccqProInfoBean.getData().getGoods_bunding_info()
                        .getRemark());

                tv_yxq.setText(ccqProInfoBean.getData().getGoods_bunding_info()
                        .getValid_start_time()
                        + " -- "
                        + ccqProInfoBean.getData().getGoods_bunding_info()
                        .getValid_end_time());
                tv_xfsj.setText(ccqProInfoBean.getData()
                        .getGoods_bunding_info_new().getService_start_time()
                        + " - "
                        + ccqProInfoBean.getData().getGoods_bunding_info_new()
                        .getService_end_time());

                if ("1".equals(ccqProInfoBean.getData()
                        .getGoods_bunding_info_new().getIs_ticket())) {// 是否有发票
                    tv_sfyfp.setText("5，提供发票");
                }
                if ("1".equals(ccqProInfoBean.getData().getGoods_bunding_info()
                        .getIs_voucher())) {// 是否需要携带证件 0 不需要 1身份证
                    tv_sfxdsfz.setText("2，需要身份证");
                }
                if ("1".equals(ccqProInfoBean.getData().getGoods_bunding_info()
                        .getIs_order())) {// 是否需要预约 0 不需要 1需要
                    tv_sfxyyy.setText("1，需要预约");
                }
                if ("0".equals(ccqProInfoBean.getData().getGoods_bunding_info()
                        .getPeople())) {
                    tv_ycpeopel.setText("4，用餐人数:单人");
                } else if ("1".equals(ccqProInfoBean.getData()
                        .getGoods_bunding_info().getPeople())) {
                    tv_ycpeopel.setText("4，用餐人数:双人");
                }

                if ("1".equals(ccqProInfoBean.getData()
                        .getGoods_bunding_info_new().getIs_child())) {
                    tv_sfy_child_play.setText("6，提供儿童乐园");
                }
                if ("1".equals(ccqProInfoBean.getData()
                        .getGoods_bunding_info_new().getIs_wifi())) {
                    tv_sfy_wifi.setText("7，提供免费wifi");
                }
                if ("1".equals(ccqProInfoBean.getData()
                        .getGoods_bunding_info_new().getIs_smoke())) {
                    tv_sfysmoke_place.setText("8，提供吸烟区");
                }

                tv_sfyqxb.setText(yesornosex(ccqProInfoBean.getData()
                        .getGoods_bunding_info().getIs_sex()));// 是否有性别要求 0 不要求
                // 1 只限男性 2 只限女性

                if (ccqProInfoBean.getData().getGoods_image_mobile().size() > 0) {
                    for (int i = 0; i < ccqProInfoBean.getData()
                            .getGoods_image_mobile().size(); i++) {
                        ADInfo info = new ADInfo();
                        info.setPic(ccqProInfoBean.getData()
                                .getGoods_image_mobile().get(i));
                        infos.add(info);
                    }
                }
                // tv_pinjia_num.setText(ccqProInfoBean.getData().getGoods_info().getEvaluation_count());
                if ("0.5".equals(ccqProInfoBean.getData().getGoods_info()
                        .getEvaluation_good_star())) {
                    starsTest("1");
                } else if ("1.5".equals(ccqProInfoBean.getData()
                        .getGoods_info().getEvaluation_good_star())) {
                    starsTest("2");
                } else if ("2.5".equals(ccqProInfoBean.getData()
                        .getGoods_info().getEvaluation_good_star())) {
                    starsTest("3");
                } else if ("3.5".equals(ccqProInfoBean.getData()
                        .getGoods_info().getEvaluation_good_star())) {
                    starsTest("4");
                } else if ("4.5".equals(ccqProInfoBean.getData()
                        .getGoods_info().getEvaluation_good_star())) {
                    starsTest("5");
                } else {
                    starsTest(ccqProInfoBean.getData().getGoods_info()
                            .getEvaluation_good_star());
                }
                ad_view.setImageResources(infos, mAdCycleViewListener);
                handler.sendEmptyMessage(HANDLER_CCQPRODETAIL_SUCCESS);
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

    private ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {

        @Override
        public void onImageClick(ADInfo info, int position, View imageView) {
        }

        @Override
        public void displayImageWM(String imageURL, ImageView imageView) {
            try {
                ImageUtil.display(imageURL, imageView);
            } catch (Exception e) {
            }

        }
    };

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.line_back:
                onBackPressed();
                break;
            case R.id.line_fenx:
                getShareInfoHttpPost();
                // showShare();
                break;
            case R.id.tv_yiyuan_buy:
                if (isLogin()) {
                    showLoading();
                    sfVIPHttpPost();
                } else {
                    intent = new Intent(NewCcqProDetailActivity.this,
                            LoginActivity.class);
                    intent.putExtra("type", "qt");
                    startActivity(intent);
                }
                break;
            case R.id.tv_detail_shuoming:
                intent = new Intent(NewCcqProDetailActivity.this,
                        WebviewActivity.class);
                intent.putExtra("title", "帮助");
                intent.putExtra("url",
                        SharedUtil.getStringValue(SharedCommon.MALL_HELP, ""));
                startActivity(intent);
                break;
            case R.id.line_pinjia:
                intent = new Intent(NewCcqProDetailActivity.this,
                        AllPinjiaActivtiy.class);
                intent.putExtra("goods_id", ccqgoods_id);

                Log.e("log", "点击，查看全部评价：ccqgoods_id---》 " + ccqgoods_id);

                intent.putExtra("type", "pro");
                startActivity(intent);
                break;
            case R.id.line_call:
                View view = LayoutInflater.from(NewCcqProDetailActivity.this)
                        .inflate(R.layout.dialog_red_moban, null);
                TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
                TextView tv_dialog_cancel = (TextView) view
                        .findViewById(R.id.tv_dialog_cancel);
                TextView dialog_text = (TextView) view
                        .findViewById(R.id.dialog_text);
                dialog_text.setText("是否拨打： "
                        + ccqProInfoBean.getData().getStore_info()
                        .getLive_store_tel());

                calldialog = new Dialog(NewCcqProDetailActivity.this,
                        R.style.mDialogStyle);

                calldialog.setContentView(view);
                calldialog.setCanceledOnTouchOutside(false);
                calldialog.show();
                tv_dialog_cancel.setOnClickListener(this);
                tv_ok.setOnClickListener(this);
                break;
            case R.id.tv_dialog_cancel:// 取消拨号
                calldialog.dismiss();
                break;
            case R.id.tv_ok:// 确定拨号
                calldialog.dismiss();
                call();
                break;

            case R.id.tv_phone_num:
                calldialog.dismiss();
                if (ccqProInfoBean.getData().getStore_info().getLive_store_tel() != null
                        && !"".equals(ccqProInfoBean.getData().getStore_info()
                        .getLive_store_tel())
                        && !"null".equals(ccqProInfoBean.getData().getStore_info()
                        .getLive_store_tel())) {

                    call();
                } else {
                    Toast.makeText(NewCcqProDetailActivity.this, "暂无电话",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            // case R.id.iv_daohang:
            // if (TextUtils.isEmpty(latitude)
            // || TextUtils.isEmpty(longitude)
            // || TextUtils.isEmpty(ccqProInfoBean.getData()
            // .getStore_info().getLatitude())
            // || TextUtils.isEmpty(ccqProInfoBean.getData()
            // .getStore_info().getLongitude())) {
            // Toast.makeText(CcqProDetailActivity.this, "定位失败，请到空旷的地方重新定位",
            // Toast.LENGTH_SHORT).show();
            // } else {
            // Intent intent = new Intent(CcqProDetailActivity.this,
            // GPSNaviActivity.class);
            // intent.putExtra("endlat", ccqProInfoBean.getData()
            // .getStore_info().getLatitude());
            // intent.putExtra("endlng", ccqProInfoBean.getData()
            // .getStore_info().getLongitude());
            // intent.putExtra("starlat", latitude);
            // intent.putExtra("starlng", longitude);
            // startActivity(intent);
            // }
            // break;
            default:
                break;
        }
    }

    /**
     * 拨号
     */
    private void call() {
        //检查权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            //没有权限，申请权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUESTCODE);

        } else {
            //已经拥有权限进行拨打
            Intent callintent = new Intent(Intent.ACTION_CALL,
                    Uri.parse("tel:"
                            + ccqProInfoBean.getData().getStore_info()
                            .getLive_store_tel()));

            startActivity(callintent);
        }
    }

    /**
     * 为了更好的用户体验，在用户选择了不再询问和拒绝之后，
     * 创建一个AlertDialog来询问用户是否需要重新授予权限，是的话，跳转到应用设置界面。
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTCODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                    AskForPermission();
                }
            }
        }
    }

    private void AskForPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permission!");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName())); // 根据包名打开对应的设置界面
                startActivity(intent);
            }
        });
        builder.create().show();
    }

    // 获取分享信息
    private void getShareInfoHttpPost() {
        RequestParams params = new RequestParams();
        params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
                + "," + verstring[2]);
        params.addBodyParameter("api_v", "v3");
        params.addBodyParameter("share_type", "goods_share");
        params.addBodyParameter("goods_id", ccqgoods_id);
        httpUtils.send(HttpMethod.POST, JiekouUtils.GETSHAREINFO, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        Log.e("ee", "失败:" + arg1);
                        handler.sendEmptyMessage(HANDLER_NET_FAILURE);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        getShareInfo(arg0.result);
                        Log.e("log", "分享 arg0.result-->  " + arg0.result);
                    }
                });
    }

    protected void getShareInfo(String result) {
        try {
            JSONObject object = new JSONObject(result);
            String code = object.getString("code");

            if ("200".equals(code)) {
                JSONObject ob = object.getJSONObject("data");
                sharetitle = ob.getString("title");
                shareimg = ob.getString("img");
                shareurl = ob.getString("url");
                sharedescription = ob.getString("description");
                handler.sendEmptyMessage(HANDLER_SHAREINFO_SUCCESS);
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

    // 是不是VIP接口
    private void sfVIPHttpPost() {
        // TODO Auto-generated method stub
        RequestParams params = new RequestParams();
        params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
                + "," + verstring[2]);
        params.addBodyParameter("token",
                new DButil().gettoken(NewCcqProDetailActivity.this));
        params.addBodyParameter("member_id",
                new DButil().getMember_id(NewCcqProDetailActivity.this));
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
        try {
            JSONObject object = new JSONObject(result);
            String code = object.getString("code");
            if ("200".equals(code)) {
                JSONObject ob = object.getJSONObject("data");
                String is_vip = ob.getString("is_vip");
                handler.sendMessage(handler.obtainMessage(
                        HANDLER_IS_VIP_SUCCESS, is_vip));
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

    private void showShare() {
        ShareSDK.initSDK(NewCcqProDetailActivity.this, "171a7e7c3c736");
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();

        //单独设置微博分享内容格式
       /* oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                if (SinaWeibo.NAME.equals(platform.getName())) {
                    paramsToShare.setText(sharedescription + shareurl);
                    paramsToShare.setImageUrl(shareimg);
                }
            }
        });*/

        // 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
        // oks.setNotification(R.drawable.ic_launcher,
        // getString(R.string.app_name));
        // 信息分享时电话
        oks.setAddress("");
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(sharetitle);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(shareurl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(sharedescription);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("");// 确保SDcard下面存在此张图片
        oks.setImageUrl(shareimg);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(shareurl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(shareurl);

        // 启动分享GUI
        oks.show(NewCcqProDetailActivity.this);
    }

    private void starsTest(String num) {
        int nu = Integer.parseInt(num);
        StarLayoutParams params = new StarLayoutParams();
        params.setNormalStar(getResources().getDrawable(R.drawable.starh))
                .setSelectedStar(getResources().getDrawable(R.drawable.stard))
                .setSelectable(false).setSelectedStarNum(nu).setTotalStarNum(5)
                .setStarHorizontalSpace(15);
        starsLayout.setStarParams(params);
    }

    // 加载中动画
    private Dialog loadingDialog;
    private StarLinearLayout starsLayout;
    private String latitude;
    private String longitude;
    private TextView tv_store_name;
    private BitmapUtils bitmapUtils;
    private String store_id;
    private String sharetitle;
    private String shareimg;
    private String shareurl;
    private String sharedescription;
    private HttpUtils httpUtils;
    private Utils utils;
    private String[] verstring;
    private ListView lv;
    private CcqQuanLvAdapter ccqQuanLvAdapter;
    private MaterialRefreshLayout refreshLayout;
    private TextView tv_pro_kucun;

    private String city = "";
    private String district = "";
    private int page = 1;
    // 推荐附近餐餐抢商品list
    private List<Map<String, String>> ccqProQuanList;

    // 加载动画
    private void showLoading() {
        View view = LayoutInflater.from(NewCcqProDetailActivity.this).inflate(
                R.layout.loading, null);
        ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
        Animation animation = AnimationUtils.loadAnimation(
                NewCcqProDetailActivity.this, R.anim.loading_anim);
        image.startAnimation(animation);
        loadingDialog = new Dialog(NewCcqProDetailActivity.this,
                R.style.mDialogStyle);
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

    // 解析性别数字意思
    private String yesornosex(String shuzi) {
        String jiexi = "";
        if ("0".equals(shuzi)) {
            jiexi = "3，没有性别要求";
        } else if ("1".equals(shuzi)) {
            jiexi = "3，要求男性";
        } else if ("2".equals(shuzi)) {
            jiexi = "3，要求女性";
        }
        return jiexi;
    }

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
        }
        return true;
    }
}
