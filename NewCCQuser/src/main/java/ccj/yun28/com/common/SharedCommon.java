package ccj.yun28.com.common;

/**
 * 存放shared数据的名字的
 * 
 * @author 123456
 * 
 */
public class SharedCommon {

	public static final String CHECKUPDATA = "checkupdata";//检查版本更新
	public static final String SFSETALIAS = "sfsetalias";//是否设置alias(推送)
	public static final String DOWNAPKURL = "downapkurl";//下载apk的url
	public static final String NETAPKNAME = "netapkname";//网络上apk的名字
	//是否有未设置登录密码    是否未设置用户名
	public static final String IS_LOGINMIMA_MEMBERNAME = "is_loginmima_membername";
	//微信登录临时保存（登陆后跳转）
	public static final String WXLOGINLSBC = "wxloginlsbc";
	// 保存上一次用户名
	public static final String DETAILNAME = "detailname";
	//是否区域选择
	public static final String SFCHOOSECITY = "sfchoosecity";
	/**
	 *  主页面底部导航
	 */
	public static final String DBDH = "dbdh";
	// api域名 例如 api.28yun.net
	public static final String API_DOMAIN = "api_domain";
	public static final String API_DOMAIN1 = "api_domain1";
	// 图片服务器域名 例如 http://58.67.219.121
	public static final String IMG_DOMAIN = "img_domain";
	public static final String IMG_DOMAIN1 = "img_domain1";
	// http:// 和 https:// 协议可以做切换
	public static final String HP = "hp";
	// 商家后台App二维码地址安卓版
	public static final String STORE_QRCODE_IMG_AZ = "store_qrcode_img_az";
	// 商家后台分享图片地址 http://www.28yun.com/mobile/api/payment/wxpay/ccqsj.png
	public static final String STORE_FX = "store_fx";
	// 商家后台App二维码地址安卓版url
	public static final String STORE_QRCODE_IMG_AZ_URL = "store_qrcode_img_az_url";
	// 商家后台App二维码地址IOS版
	public static final String STORE_QRCODE_IMG_IOS = "store_qrcode_img_ios";
	// 商家后台App二维码地址IOS版url
	public static final String STORE_QRCODE_IMG_IOS_URL = "store_qrcode_img_ios_url";
	// 商城App二维码地址安卓版
	public static final String MALL_QRCODE_IMG_AZ = "mall_qrcode_img_az";
	// 商城App分享图片地址 http://www.28yun.com/mobile/api/payment/wxpay/ccqyh.png
	public static final String MALL_FX = "mall_fx";
	// 商城App二维码地址安卓版url
	public static final String MALL_QRCODE_IMG_AZ_URL = "mall_qrcode_img_az_url";
	// 商城App二维码地址IOS版
	public static final String MALL_QRCODE_IMG_IOS = "mall_qrcode_img_ios";
	// 商城App二维码地址IOS版url
	public static final String MALL_QRCODE_IMG_IOS_URL = "mall_qrcode_img_ios_url";
	// 事业部App二维码地址安卓版
	public static final String MARKETER_QRCODE_IMG_AZ = "marketer_qrcode_img_az";
	// 事业部App分享图片地址 http://www.28yun.com/mobile/api/payment/wxpay/ccqsy.png
	public static final String MARKETER_FX = "marketer_fx";
	// 事业部App二维码地址安卓版url
	public static final String MARKETER_QRCODE_IMG_AZ_URL = "marketer_qrcode_img_az_url";
	// 事业部App二维码地址IOS版
	public static final String MARKETER_QRCODE_IMG_IOS = "marketer_qrcode_img_ios";
	// 事业部App二维码地址IOS版url
	public static final String MARKETER_QRCODE_IMG_IOS_URL = "marketer_qrcode_img_ios_url";
	// 商城App首屏广告图
	public static final String MALL_FP = "mall_fp";
	// 商家后台App首屏广告图
	public static final String STORE_FP = "store_fp";
	// 事业部App首屏广告图
	public static final String MARKETER_FP = "marketer_fp";
	// 商城App客服电话
	public static final String MALL_SERVICE = "mall_service";
	// 商家后台App客服电话
	public static final String STORE_SERVICE = "store_service";
	// 事业部App客服电话
	public static final String MARKETER_SERVICE = "marketer_service";
	// 事业部App管理员电话
	public static final String MARKETER_SERVICE_ADMIN = "marketer_service_admin";
	// 商家后台审核人员电话
	public static final String CHECK_PHONE = "check_phone";
	// 商家后台提现说明
	public static final String STORE_WITHDRAW_EXPLAIN = "store_withdraw_explain";
	// 商家帮助中心
	public static final String STORE_HELP = "store_help";
	// 商城提现说明
	public static final String MALL_WITHDRAW_EXPLAIN = "mall_withdraw_explain";
	// 商城帮助中心
	public static final String MALL_HELP = "mall_help";
	// 红包规则
	public static final String MALL_RED_PACKET = "mall_red_packet";
	// 活动页1
	public static final String ACTIVITY_1 = "activity_1";
	// 活动页2
	public static final String ACTIVITY_2 = "activity_2";
	// 活动页3
	public static final String ACTIVITY_3 = "activity_3";
	// 活动页4
	public static final String ACTIVITY_4 = "activity_4";
	// 安卓 给我评分
	public static final String ACTIVITY_5 = "activity_5";
	// 分享二维码链接
	public static final String REG = "reg";
	// 用户协议
	public static final String USERAGREEMENT = "useragreement";
	// 从哪里跳到商品列表页
	public static final String PRODUCTLIST_TYPE = "Productlist_type";
	// 从商品详情到评价
	public static final String PRODANPINJIA = "prodanpinjia";
	
	//**********************************************************
	/**
	 * 手动选择的城市Id
	 */
	public static final String SELECTOR_CITY_ID = "selector_city_id";
	/**
	 * 当前定位经度
	 */
	public static final String LATITUDE = "latitude";
	/**
	 * 当前定位纬度
	 */
	public static final String LONGITUDE = "longitude";
	/**
	 * 当前定位地址信息
	 */
	public static final String ADDRESS = "address";
	/**
	 * 当前定位城市/手动定位城市名称
	 */
	public static final String CITY = "city";

}
