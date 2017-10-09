package ccj.yun28.com.sy;

import java.security.MessageDigest;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.PayBindPhoneActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.bean.ZhuBuyZkjBean;
import ccj.yun28.com.bean.wx.ZhuWXPayInfo;
import ccj.yun28.com.ccq.CcqShouYinTaiActivity;
import ccj.yun28.com.common.SharedCommon;
import ccj.yun28.com.mine.ZhifuSuccessActivity;
import ccj.yun28.com.utils.DButil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.SharedUtil;
import ccj.yun28.com.utils.Utils;
import ccj.yun28.com.utils.pay.AlipayUtil;
import ccj.yun28.com.utils.pay.PayResultDialog;
import ccj.yun28.com.utils.pay.WXPayUtil;
import ccj.yun28.com.utils.pay.receiver.PayResultReceiver;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 购买折扣券-填写订单页
 *
 * @author meihuali
 */
public class BuyZKJActivity extends BaseActivity implements OnClickListener {

    // 黑色版-商家信息
    private ImageView iv_shop_pic;
    private TextView tv_shop_addr;
    private TextView tv_shop_phone;
    // 黑色版-绑定手机
    private LinearLayout line_member_phone;
    private TextView tv_member_phone;
    private TextView tv_gobind_phone;
    // 店铺名
    private TextView tv_shop_name;
    // 商品图
    private ImageView iv_pic;
    // 商品名
    private TextView tv_pro_name;
    // 价格
    private TextView tv_price;
    // 实付款
    private TextView tv_zf_price;
    private TextView tv_tjdd;
    // 支付方式
    private TextView tv_zffs;
    private ImageView iv_zffs;
    private TextView tv_zffsmr;
    // 支付方式id
    private String zffs_id = "12";
    // 商品id
    private String goods_id;
    // 订单金额
    private String order_amount;
    // 订单号
    private String order_sn;
    // 什么支付方式
    private String zffs;
    // 发票id
    private String sfyfapiao;
    // 输入密码dialog
    private Dialog tianxiedindanDialog;

    private EditText et_password;
    private ZhuWXPayInfo zhuWXPayInfo;
    public static IWXAPI wxApi;
    private PayResultReceiver payResultReceiver;

    // 是否绑定手机后调用
    private boolean resultGo = false;
    // 是否可以提交订单
    private boolean sfdj = false;
    // 网络异常
    protected static final int HANDLER_NET_FAILURE = 0;
    // 异常
    protected static final int HANDLER_NN_FAILURE = 1;
    // 获取信息失败
    private static final int HANDLER_GETINFO_FAILURE = 2;
    // 获取订单详情信息成功
    private static final int HANDLER_DINDANDETAILINFO_SUCCESS = 3;
    // 获取提交订单信息数据成功
    private static final int HANDLER_TJDD_SUCCESS = 4;
    // 获取微信信息正确
    private static final int HANDLER_GETWEIXINCANSHU_SUCCESS = 5;
    // 支付密码正确
    private static final int HANDLER_CHECKPASSWORD_SUCCESS = 6;
    private static final int HANDLER_GETZHIFUBAOJIAMI_SUCCESS = 7;
    private static final int HANDLER_WXHUIDIAO_SUCCESS = 8;
    private static final int ZFFS_RESULT = 101;
    private static final int BIND_PHONE = 201;

    public static BuyZKJActivity buyZKJActivity;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                // 网络异常
                case HANDLER_NET_FAILURE:
                    sfdj = true;
                    dissDialog();
                    Toast.makeText(BuyZKJActivity.this, "当前网络不可用,请检查网络",
                            Toast.LENGTH_SHORT).show();
                    break;
                // 错误
                case HANDLER_NN_FAILURE:
                    sfdj = true;
                    dissDialog();
                    Toast.makeText(BuyZKJActivity.this, "当前网络出错,请检查网络",
                            Toast.LENGTH_SHORT).show();
                    break;
                // 获取信息失败
                case HANDLER_GETINFO_FAILURE:
                    sfdj = true;
                    dissDialog();
                    if (msg.obj != null) {

                        Toast.makeText(BuyZKJActivity.this,
                                msg.obj.toString().trim(), Toast.LENGTH_SHORT)
                                .show();

                    }
                    break;
                // 获取订单详情数据成功
                case HANDLER_DINDANDETAILINFO_SUCCESS:
                    dissDialog();
                    break;
                // 获取提交订单信息数据成功
                case HANDLER_TJDD_SUCCESS:
                    if ("支付宝".equals(zffs)) {
                        zfbjiamihttphost();
                        initReceiver();
                    } else if ("微信支付".equals(zffs)) {
                        // 下面这两句是在微信支付的时候一定要加啊!!!!!!!!!!!!!!!!!
                        getwxcanshuHttpPost();
                        initReceiver();
                        wxApi = WXAPIFactory.createWXAPI(BuyZKJActivity.this,
                                Utils.WX_APP_ID);
                        wxApi.registerApp(Utils.WX_APP_ID);

                    } else {
                        sfdj = true;
                        dissDialog();
                        Toast.makeText(BuyZKJActivity.this, "购买成功",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BuyZKJActivity.this,
                                ZhifuSuccessActivity.class);
                        intent.putExtra("order_amount", order_amount);
                        intent.putExtra("order_id", order_id);
                        intent.putExtra("type", "3");
                        startActivity(intent);
                        finish();
                    }
                    break;
                // 获取订单详情数据成功
                case HANDLER_GETWEIXINCANSHU_SUCCESS:
                    // dissDialog();
                    WXPayUtil wxPayUtil = new WXPayUtil(zhuWXPayInfo.getData());// 使用时需要进去替换当前的activity
                    // WXPayUtil wxPayUtil = new
                    // WXPayUtil(BuyZKJActivity.this,zhuWXPayInfo.getData());//
                    // 使用时需要进去替换当前的activity
                    wxPayUtil.pay();
                    dissDialog();
                    break;
                // 检查密码成功
                case HANDLER_CHECKPASSWORD_SUCCESS:
                    submitOrderHttpPost();
                    break;
                case HANDLER_WXHUIDIAO_SUCCESS:
                    dissDialog();
                    Intent intent = new Intent(BuyZKJActivity.this,
                            ZhifuSuccessActivity.class);
                    intent.putExtra("order_amount", order_amount);
                    intent.putExtra("order_id", order_id);
                    intent.putExtra("type", "3");
                    startActivity(intent);
                    finish();
                    break;
                case HANDLER_GETZHIFUBAOJIAMI_SUCCESS:
                    // dissDialog();
                    // String info =
                    // "partner=\"2088121666678942\"&seller_id=\"appfad@28yun.com\"&out_trade_no=\"20161100007984011404\"&subject=\"重庆小面\"&body=\"重庆小面\"&total_fee=\"1\"&notify_url=\"http://app.28yun.com/index.php/webapi/Pay/AlipayReturn\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&sign=\"XBeZIm0EsC3%2BkIX3VwwaSl7kXXbsESRkdPS0MwnOa9yNEkKcNw5ovRPnTRy%2FIHyORNLzncuWNP9Rr8QfySTrvOsAmrO9QHOQAVKMUW44uv1fMIqf3rPcbE%2FPqdKE5chR9kVhtRlykbMX0WMLiQDQPHRFwASAQIOHAS%2FgFtL7sAQ%3D\"&sign_type=\"RSA\"";
                    if (msg.obj != null) {
                        String info = msg.obj.toString().trim();
                        alipayDemo(info);
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private String member_mobile_bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_buyzkja);
        setContentView(R.layout.activity_buyzkj);// 黑色版本-绑定手机号
        // 返回
        LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
        // 店铺名
        tv_shop_name = (TextView) findViewById(R.id.tv_shop_name);
        iv_shop_pic = (ImageView) findViewById(R.id.iv_shop_pic);
        tv_shop_addr = (TextView) findViewById(R.id.tv_shop_addr);
        tv_shop_phone = (TextView) findViewById(R.id.tv_shop_phone);
        // 商品图
        iv_pic = (ImageView) findViewById(R.id.iv_pic);
        // 商品名
        tv_pro_name = (TextView) findViewById(R.id.tv_pro_name);
        // 价格
        tv_price = (TextView) findViewById(R.id.tv_price);
        // 支付方式
        LinearLayout line_zffs = (LinearLayout) findViewById(R.id.line_zffs);
        tv_zffs = (TextView) findViewById(R.id.tv_zffs);
        tv_zffsmr = (TextView) findViewById(R.id.tv_zffsmr);
        iv_zffs = (ImageView) findViewById(R.id.iv_zffs);
        // 实付款
        tv_zf_price = (TextView) findViewById(R.id.tv_zf_price);
        // 提交订单
        tv_tjdd = (TextView) findViewById(R.id.tv_goto_pay);

        line_member_phone = (LinearLayout) findViewById(R.id.line_member_phone);
        tv_member_phone = (TextView) findViewById(R.id.tv_member_phone);
        tv_gobind_phone = (TextView) findViewById(R.id.tv_gobind_phone);

        httpUtils = new HttpUtils();
        utils = new Utils();
        verstring = utils.getVersionInfo(BuyZKJActivity.this);

        buyZKJActivity = this;

        if (getIntent() != null) {

            showLoading();
            goods_id = getIntent().getStringExtra("goods_id");
            dinDanDetailInfoHttpPost(goods_id);
        }

        line_back.setOnClickListener(this);
        line_zffs.setOnClickListener(this);
        tv_tjdd.setOnClickListener(this);
        tv_gobind_phone.setOnClickListener(this);
    }

    //支付宝服务端参数签名, 获得支付宝加密参数接口
    protected void zfbjiamihttphost() {
        RequestParams params = new RequestParams();
        params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
                + "," + verstring[2]);
        params.addBodyParameter("out_trade_no", "\"" + order_sn + "\"");
        params.addBodyParameter("subject", "\"" + goods_name + "\"");
        params.addBodyParameter("body", "\"" + goods_name + "\"");
        params.addBodyParameter("total_fee", "\"" + order_amount + "\"");
        httpUtils.send(HttpMethod.POST, JiekouUtils.GETZHIFUBAO, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        handler.sendEmptyMessage(HANDLER_NET_FAILURE);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        getzfbjiamiListInfo(arg0.result);
                    }
                });
    }

    protected void getzfbjiamiListInfo(String result) {
        try {
            JSONObject object = new JSONObject(result);
            String code = object.getString("code");
            String data = object.getString("data");
            if ("200".equals(code)) {
                handler.sendMessage(handler.obtainMessage(
                        HANDLER_GETZHIFUBAOJIAMI_SUCCESS, data));
            } else {
                handler.sendMessage(handler.obtainMessage(
                        HANDLER_GETINFO_FAILURE, data));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            handler.sendEmptyMessage(HANDLER_NN_FAILURE);
        }
    }

    // 获得微信支付参数接口
    protected void getwxcanshuHttpPost() {
        RequestParams params = new RequestParams();
        params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
                + "," + verstring[2]);
        httpUtils.send(HttpMethod.GET, JiekouUtils.GETWEIXINCANSHU
                        + "?order_sn=" + order_sn + "&order_amount=" + order_amount,
                params, new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        handler.sendEmptyMessage(HANDLER_NET_FAILURE);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        getweixincanshuListInfo(arg0.result);
                    }
                });
    }

    // 获得微信支付参数数据解析
    protected void getweixincanshuListInfo(String result) {
        try {
            JSONObject object = new JSONObject(result);
            String code = object.getString("code");
            if ("200".equals(code)) {
                Gson gson = new Gson();
                zhuWXPayInfo = gson.fromJson(result, ZhuWXPayInfo.class);
                handler.sendEmptyMessage(HANDLER_GETWEIXINCANSHU_SUCCESS);
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

    // 获取订单详情接口
    private void dinDanDetailInfoHttpPost(String goods_id) {
        RequestParams params = new RequestParams();
        params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
                + "," + verstring[2]);
        params.addBodyParameter("token",
                new DButil().gettoken(BuyZKJActivity.this));
        params.addBodyParameter("member_id",
                new DButil().getMember_id(BuyZKJActivity.this));
        params.addBodyParameter("cart_id", goods_id + "|1");
        params.addBodyParameter("ifcart", "3");
        httpUtils.send(HttpMethod.POST, JiekouUtils.DINDANDETAILINFO, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        handler.sendEmptyMessage(HANDLER_NET_FAILURE);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        dinDanDetailInfoListInfo(arg0.result);
                    }
                });
    }

    // 获取订单详情数据解析
    protected void dinDanDetailInfoListInfo(String result) {
        try {
            JSONObject object = new JSONObject(result);
            String code = object.getString("code");
            if ("200".equals(code)) {
                Gson gson = new Gson();
                buyZkjBean = gson.fromJson(result, ZhuBuyZkjBean.class);
                BitmapUtils bitmapUtils = new BitmapUtils(BuyZKJActivity.this);
                bitmapUtils.display(iv_pic, buyZkjBean.getBuy_list()
                        .getStore_cart_list().get(0).getGoods_list().get(0)
                        .getGoods_image_url());
                tv_shop_name.setText(buyZkjBean.getBuy_list().getStore_info()
                        .getStore_name());
                tv_pro_name.setText(buyZkjBean.getBuy_list()
                        .getStore_cart_list().get(0).getGoods_list().get(0)
                        .getGoods_name());
                tv_price.setText(buyZkjBean.getBuy_list().getStore_cart_list()
                        .get(0).getGoods_list().get(0).getGoods_price());
                tv_zf_price
                        .setText("¥ "
                                + buyZkjBean.getBuy_list().getStore_cart_list()
                                .get(0).getGoods_list().get(0)
                                .getGoods_price() + " 元");
                sfyfapiao = buyZkjBean.getBuy_list().getInv_info().getInv_id();

                member_mobile_bind = buyZkjBean.getBuy_list()
                        .getMember_info().getMember_mobile_bind();// 是否绑定手机
                // 1:已经绑定
                // 0:未绑定
                String member_mobile = buyZkjBean.getBuy_list()
                        .getMember_info().getMember_mobile();
                if ("1".equals(member_mobile_bind)) {
                    sfdj = true;
                    line_member_phone.setVisibility(View.VISIBLE);
                    tv_member_phone.setText(member_mobile);
                    tv_gobind_phone.setVisibility(View.GONE);
                    tv_tjdd.setBackgroundResource(R.drawable.yellowsure);
                } else {
                    sfdj = false;
                    line_member_phone.setVisibility(View.GONE);
                    tv_gobind_phone.setVisibility(View.VISIBLE);
                    tv_tjdd.setBackgroundResource(R.drawable.zkq);
                }

                bitmapUtils.display(iv_shop_pic, buyZkjBean.getBuy_list()
                        .getStore_info().getStore_img());
                tv_shop_addr.setText(buyZkjBean.getBuy_list().getStore_info()
                        .getStore_address());
                tv_shop_phone.setText("服务电话："
                        + buyZkjBean.getBuy_list().getStore_info()
                        .getLive_store_tel());
                if (resultGo == true) {
                    resultGo = false;
                } else {
                    handler.sendEmptyMessage(HANDLER_DINDANDETAILINFO_SUCCESS);
                }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.line_back:
                onBackPressed();
                break;
            case R.id.line_zffs://选择支付方式
                Intent intent = new Intent(BuyZKJActivity.this,
                        CcqShouYinTaiActivity.class);
                intent.putExtra("jine", buyZkjBean.getBuy_list()
                        .getStore_cart_list().get(0).getGoods_list().get(0)
                        .getGoods_price());
                startActivityForResult(intent, ZFFS_RESULT);
                break;
            case R.id.tv_goto_pay://去付款
                // 是否绑定手机
                // 1:已经绑定
                // 0:未绑定
                if ("0".equals(member_mobile_bind)) {
                    intent = new Intent(BuyZKJActivity.this, PayBindPhoneActivity.class);
                    startActivityForResult(intent, BIND_PHONE);
                    return;
                }
                if (sfdj) {
                    if (canTjdd()) {
                        if (!"微信支付".equals(tv_zffs.getText().toString().trim())
                                && !"支付宝".equals(tv_zffs.getText().toString()
                                .trim())) {
                            showmimaLoading();
                        } else {
                            sfdj = false;
                            showLoading();
                            submitOrderHttpPost();
                        }
                    }
                } else {
                    Toast.makeText(this, "现在不可以点击哦", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tv_dialog_cancel:
                tianxiedindanDialog.dismiss();
                break;

            case R.id.tv_gobind_phone:
                intent = new Intent(BuyZKJActivity.this, PayBindPhoneActivity.class);
                startActivityForResult(intent, BIND_PHONE);
                break;

            case R.id.tv_ok:
                if (canmima()) {
                    showLoading();
                    checkpasswordHttpPost();
                }

            default:
                break;
        }
    }

    private boolean canTjdd() {
        zffs = tv_zffs.getText().toString().trim();
        if ("".equals(zffs) || zffs == null) {
            Toast.makeText(BuyZKJActivity.this, "支付方式不能为空哦", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    private boolean canmima() {
        // TODO Auto-generated method stub
        String mima = et_password.getText().toString().trim();
        if ("".equals(mima) || mima == null) {
            Toast.makeText(BuyZKJActivity.this, "密码不能为空哦", Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else if (mima.length() < 6) {
            Toast.makeText(BuyZKJActivity.this, "密码不能少于六位哦", Toast.LENGTH_SHORT)
                    .show();
            return false;

        } else if (mima.length() > 20) {
            Toast.makeText(BuyZKJActivity.this, "密码不能多于二十位哦",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // 检查密码接口
    private void checkpasswordHttpPost() {
        RequestParams params = new RequestParams();
        params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
                + "," + verstring[2]);
        params.addBodyParameter("token",
                new DButil().gettoken(BuyZKJActivity.this));
        params.addBodyParameter("member_id",
                new DButil().getMember_id(BuyZKJActivity.this));
        params.addBodyParameter("paypwd", MD5(et_password.getText().toString()
                .trim()));
        httpUtils.send(HttpMethod.POST, JiekouUtils.CHECKZHIFUMIMA, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        Log.e("ee", "失败:" + arg1);
                        handler.sendEmptyMessage(HANDLER_NET_FAILURE);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        checkpasswordListInfo(arg0.result);
                    }
                });
    }

    protected void checkpasswordListInfo(String result) {
        try {
            JSONObject object = new JSONObject(result);
            String code = object.getString("code");
            if ("200".equals(code)) {
                handler.sendEmptyMessage(HANDLER_CHECKPASSWORD_SUCCESS);
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

    // 提交订单接口
    private void submitOrderHttpPost() {
        RequestParams params = new RequestParams();
        params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
                + "," + verstring[2]);
        params.addBodyParameter("token",
                new DButil().gettoken(BuyZKJActivity.this));
        params.addBodyParameter("member_id",
                new DButil().getMember_id(BuyZKJActivity.this));
        params.addBodyParameter("ifcart", "3");
        // params.addBodyParameter("cart_id", goods_id + "|1");
        params.addBodyParameter("cart_id", goods_id + "|1");
        params.addBodyParameter("address_id", "1");
        params.addBodyParameter("pay_name", "online");
        params.addBodyParameter("pay_code", zffs_id);
        // params.addBodyParameter("invoice_id", sfy fapiao);
        params.addBodyParameter("invoice_id", "0");
        params.addBodyParameter("order_message", "");
        httpUtils.send(HttpMethod.POST, JiekouUtils.TIJIAODINDAN, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        Log.e("ee", "失败:" + arg1);
                        handler.sendEmptyMessage(HANDLER_NET_FAILURE);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        tjddListInfo(arg0.result);
                    }
                });
    }

    // 提交订单数据解析
    protected void tjddListInfo(String result) {
        try {
            JSONObject object = new JSONObject(result);
            String code = object.getString("code");
            if ("200".equals(code)) {
                String pay_sn = object.getString("pay_sn");
                order_sn = object.getString("order_sn");
                JSONObject pay_info = object.getJSONObject("pay_info");
                goods_name = pay_info.getString("goods_name");
                order_id = pay_info.getString("order_id");
                order_amount = pay_info.getString("order_amount");
                handler.sendEmptyMessage(HANDLER_TJDD_SUCCESS);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 300) {
            zffs = data.getStringExtra("zffs").toString().trim();
            zffs_id = data.getStringExtra("zffs_id");
            tv_zffs.setText(zffs);
            tv_zffsmr.setText("支付方式");
            if ("微信支付".equals(zffs)) {

                iv_zffs.setBackgroundResource(R.drawable.wechatpay);
            } else if ("支付宝".equals(zffs)) {
                iv_zffs.setBackgroundResource(R.drawable.alipay);

            } else {
                iv_zffs.setBackgroundResource(R.drawable.moneyy);

            }
        } else if (resultCode == 202) {
            showLoading();
            resultGo = true;
            sfdj = true;
            dinDanDetailInfoHttpPost(goods_id);
            if (sfdj) {
                if (canTjdd()) {
                    if (!"微信支付".equals(tv_zffs.getText().toString().trim())
                            && !"支付宝".equals(tv_zffs.getText().toString()
                            .trim())) {
                        dissDialog();
                        showmimaLoading();
                    } else {
                        sfdj = false;
                        tv_loading.setText("提交中，请稍等");
                        submitOrderHttpPost();
                    }
                } else {
                    dissDialog();
                }
            } else {
                dissDialog();
                Toast.makeText(this, "现在不可以点击哦", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 密码框
    private void showmimaLoading() {
        View view = LayoutInflater.from(BuyZKJActivity.this).inflate(
                R.layout.dialog_tianxiedindan, null);
        et_password = (EditText) view.findViewById(R.id.et_password);
        TextView tv_dialog_cancel = (TextView) view
                .findViewById(R.id.tv_dialog_cancel);
        TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
        tv_dialog_cancel.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
        tianxiedindanDialog = new Dialog(BuyZKJActivity.this,
                R.style.mDialogStyle);
        tianxiedindanDialog.setContentView(view);
        tianxiedindanDialog.setCanceledOnTouchOutside(false);
        tianxiedindanDialog.show();
    }

    // 加载中动画
    private Dialog loadingDialog;
    private ZhuBuyZkjBean buyZkjBean;
    private String goods_name;
    private String order_id;
    private HttpUtils httpUtils;
    private Utils utils;
    private String[] verstring;
    private TextView tv_loading;

    // 加载动画
    private void showLoading() {
        View view = LayoutInflater.from(BuyZKJActivity.this).inflate(
                R.layout.loading, null);
        ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
        tv_loading = (TextView) view.findViewById(R.id.tv_loading);
        Animation animation = AnimationUtils.loadAnimation(BuyZKJActivity.this,
                R.anim.loading_anim);
        image.startAnimation(animation);
        loadingDialog = new Dialog(BuyZKJActivity.this, R.style.mDialogStyle);
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

    private void initReceiver() {
        payResultReceiver = new PayResultReceiver() {
            @Override
            public void payResult(String resultInfo) {
                if (TextUtils.equals(resultInfo, Utils.IS_ALIPAY)) {
                    // 支付宝支付成功
                    dissDialog();
                    Toast.makeText(BuyZKJActivity.this, "支付成功",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(BuyZKJActivity.this,
                            ZhifuSuccessActivity.class);
                    intent.putExtra("order_amount", order_amount);
                    intent.putExtra("order_id", order_id);
                    intent.putExtra("type", "3");
                    startActivity(intent);
                } else if (TextUtils.equals(resultInfo, Utils.IS_WXPAY)) {
                    // 微信支付成功
                    // 微信支付后回调
                    wxhuidiaoHttpPost();

                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Utils.pay_result_receiver_action);
        registerReceiver(payResultReceiver, intentFilter);
    }

    // 微信回调
    protected void wxhuidiaoHttpPost() {
        RequestParams params = new RequestParams();
        params.addHeader("ccq", utils.getSystemVersion() + "," + verstring[0]
                + "," + verstring[2]);
        httpUtils.send(HttpMethod.GET, JiekouUtils.WXHUIDIAO + "?order_sn="
                + order_sn, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                handler.sendEmptyMessage(HANDLER_NET_FAILURE);
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                wxhuidiaoJsonList(arg0.result);
            }
        });
    }

    protected void wxhuidiaoJsonList(String result) {
        // TODO Auto-generated method stub
        try {
            JSONObject object = new JSONObject(result);
            String code = object.getString("code");
            String message = object.getString("message");
            if ("200".equals(code)) {
                handler.sendEmptyMessage(HANDLER_WXHUIDIAO_SUCCESS);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (payResultReceiver != null)
            unregisterReceiver(payResultReceiver);
    }

    // MD5加密，32位
    public static String MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * 支付宝支付方法
     *
     * @param orderInfo 支付字符串，该值从后台获取。
     */
    private void alipayDemo(String orderInfo) {
        AlipayUtil alipayUtil = new AlipayUtil(
                new AlipayUtil.OnAlipayResponse() {
                    @Override
                    public void onResponse(int status, String tips) {
                        if (status == AlipayUtil.OnAlipayResponse.SUCCESS) {
                            // 支付成功
                            // 如果支付成功后需要做的操作是在本页面的则完成方法一,若在其他页面则完成方法二
                            /****************** 方法一 ****************/
                            // 然后再在sureListener()里面做支付完成后需要的操作
                            // PayResultDialog payResultDialog = new
                            // PayResultDialog(
                            // BuyZKJActivity.this) {
                            // @Override
                            // public void sureListener() {
                            // // 点击确定后的操作
                            // Toast.makeText(BuyZKJActivity.this, "支付成功",
                            // Toast.LENGTH_SHORT).show();
                            // }
                            // };
                            // payResultDialog.setHintInfo("支付成功");
                            // payResultDialog.show();
                            finish();
                            dissDialog();
                            Intent intent = new Intent(BuyZKJActivity.this,
                                    ZhifuSuccessActivity.class);
                            intent.putExtra("order_amount", order_amount);
                            intent.putExtra("order_id", order_id);
                            intent.putExtra("type", "3");
                            startActivity(intent);
                            /****************** 方法二 ****************/
                            // Intent intent = new Intent();
                            // intent.setAction(Constant.pay_result_receiver_action);
                            // intent.putExtra(Constant.pay_result_receiver_info,
                            // Constant.IS_ALIPAY);
                            // PayActivity.this.sendBroadcast(intent); // 广播发送
                        } else if (status == AlipayUtil.OnAlipayResponse.FAIL) {
                            // 支付失败
                            dissDialog();
                            sfdj = true;
                            PayResultDialog payResultDialog = new PayResultDialog(
                                    BuyZKJActivity.this) {
                                @Override
                                public void sureListener() {
                                    // 点击确定后的操作
                                }
                            };
                            // payResultDialog.setHintInfo("支付失败");
                            payResultDialog.setHintInfo("支付失败");
                            payResultDialog.show();
                        } else if (status == AlipayUtil.OnAlipayResponse.CONFIRMATION) {
                            // 支付结果确认中
                            dissDialog();
                            sfdj = true;
                            PayResultDialog payResultDialog = new PayResultDialog(
                                    BuyZKJActivity.this) {
                                @Override
                                public void sureListener() {
                                    // 点击确定后的操作
                                    /*Intent intent = new Intent(
											BuyZKJActivity.this,
											DFKDinDanActivtiy.class);
									startActivity(intent);*/
                                    finish();
                                }
                            };
                            payResultDialog.setHintInfo("支付结果确认中");
                            payResultDialog.show();
                        }
                    }
                });
        alipayUtil.pay(this, orderInfo);
    }

    @Override
    public void onResume() {
        super.onResume();
        sfdj = true;
    }
}
