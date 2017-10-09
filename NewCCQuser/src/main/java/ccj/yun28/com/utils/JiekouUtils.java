package ccj.yun28.com.utils;

import ccj.yun28.com.common.SharedCommon;

/**
 * 接口
 * 
 * @author meihuali
 * 
 */
public class JiekouUtils {

	/**
	 * 获取配置文件
	 */
	// 正式
	public static String PEIZHI = "http://app.28yun.com/index.php/webapi_v2/index/config";
	// 测试
	// public static String PEIZHI =
	// "http://over.28yun.cn/index.php/webapi_v2/index/config";
	// 上线时测试
	// public static String PEIZHI =
	// "http://b.hy.com/index.php/webapi_v2/index/config";

	// 接口前缀
	public static String QIANZUI = SharedUtil.getStringValue(SharedCommon.HP,
			"")
			+ SharedUtil.getStringValue(SharedCommon.API_DOMAIN1,
					SharedUtil.getStringValue(SharedCommon.API_DOMAIN, ""));
	// 微信支付成功后回调
	public static String WXHUIDIAO = QIANZUI + "Pay/WeixinReturn";
	// 获取token前缀
	public static String TOKEN_QZ = QIANZUI + "index/token";
	// 更新
	public static String UPDATAAPK = QIANZUI + "tool/versions";
	// 登录
	public static String LOGIN = QIANZUI + "index/login";
	public static String PHONELOGIN = QIANZUI + "index/login_sms";
	public static String WEIXINLOGIN = QIANZUI + "Weixin/get_member";
	// 获取分享信息
	public static String GETSHAREINFO = QIANZUI + "Common/Share";
	// 检查用户名是否存在
	public static String CHECK_MEMBERNAME = QIANZUI + "index/check_member_name";
	// 检查用户名是否能忘记密码
	public static String CHECK_MEMBERNAMEISFORGETPWD = QIANZUI
			+ "Member/check_member_name_d";
	// 检查是否需要图形验证
	public static String CHECK_NEEDPIC = QIANZUI + "Tool/check_gvcode";
	// 获取图片认证图片
	public static String PICRENZHENG_PIC = QIANZUI + "Tool/create_captcha";
	// 注册
	public static String REGIST = QIANZUI + "index/reg";
	// 发送短信验证码
	public static String LOGINFASONGYZM = QIANZUI + "tool/send_sms_dl";
	// public static String FASONGYZM = QIANZUI + "Member/phone_security";(废弃)
	public static String NEWFASONGYZM = QIANZUI + "Tool/phone_security";
	// 检查短信验证码
	public static String JIANCHAYZM = QIANZUI + "Member/check_code";
	public static String NEWJIANCHAYZM = QIANZUI + "Tool/check_code";
	// 支付密码验证
	public static String CHECKZHIFUMIMA = QIANZUI + "Buy/check_password";
	// 添加发票
	public static String ADDFAPIAO = QIANZUI + "Memberinvoice/invoice_add";
	// 购物车，线上商品，支付宝服务端参数签名
	public static String GETZHIFUBAO = QIANZUI + "pay/signatures";
	// 购物车，线上商品，餐餐抢微信参数获取
	public static String GETWEIXINCANSHU = QIANZUI + "Pay/WeixinRequest";
	// 购物车，线上商品，餐餐抢订单详情
	public static String DINDANDETAILINFO = QIANZUI + "Buy/buy_step1";
	// 购物车，线上商品，餐餐抢提交订单
	public static String TIJIAODINDAN = QIANZUI + "buy/buy_step2";
	// 是否是VIP(是否绑定手机和微信)
	public static String SFVIP = QIANZUI + "member/is_vip";
	// 获取账户各个金额
	public static String GETZHANGHUJINE = QIANZUI + "Pay/checkstand";
	// 首页
	public static String SHOUYE = QIANZUI + "index/index";
	// 首页-猜你喜欢
	public static String GUESSLIKE = QIANZUI + "Goods/you_like";
	// 首页-分类
	public static String SY_FENLEI = QIANZUI + "Goodclass/get_class_list";
	// 首页-个人推广二维码
	public static String TUIGUANGERWEIMA = QIANZUI + "Member/qrcode";
	// 消息
	public static String XIAOXI = QIANZUI + "membermessage/message";
	// 消息已读
	public static String XIAOXIYIDU = QIANZUI + "Membermessage/is_read";
	// 商品详细
	public static String PRODUCTDETAIL = QIANZUI + "goods/goods_detail";
	// 商品详请图片
	public static String PRODUCTDETAILPIC = QIANZUI + "goods/detail_body";
	// 加入购物车
	public static String ADDGWC = QIANZUI + "Membercart/cart_add";
	// 商品评价列表
	public static String ALLPINJIALIST = QIANZUI
			+ "Evaluate/goods_evaluate_list";
	// 首页-热销-换一批
	public static String REFRESH = QIANZUI + "index/hyh";
	// 首页-餐餐抢搜索-热门搜索
	// public static String HOT_CCQSEARCH = QIANZUI +
	// "Unionstore/hot_search";//废弃
	// 首页-搜索-热门搜索
	// public static String HOT_SEARCH = QIANZUI + "Goods/hot_search";//废弃
	public static String HOT_SEARCH = QIANZUI + "Wxchat/hot_search";
	// 首页-搜索-宝贝
	public static String BB_SEARCH = QIANZUI + "Goods/goods_list";
	// 首页-扫一扫完成后判断
	public static String SAOYISAOFINISH = QIANZUI + "Tool/parse_url";
	// 餐餐抢新首页
	public static String NEWCCQSY = QIANZUI + "Unionstore/index_ccq";
	// 餐餐抢新首页-超值名券
	public static String CHAOZHIJUAN = QIANZUI + "Unionstore/index_ccq_r";
	// 餐餐抢新首页-霸王餐
	public static String BAWANGCAN = QIANZUI + "Unionstore/index_ccq_l";
	// 餐餐抢头部列表
	public static String CCQTOPLIST = QIANZUI + "Unionstore/recommend";
	// 餐餐抢新首页-餐餐抢券
	public static String NEWSY_CANCANQIANGJUAN = QIANZUI
			+ "Unionstore/cqq_index_goods";
	// 餐餐抢新首页-未读消息
	public static String NEWSY_UNREAD_MESSAGE = QIANZUI
			+ "membermessage/unread_message";
	// 餐餐抢新订单-推荐商品列表
	public static String NEWORDER_RECOMMENDLIST = QIANZUI
			+ "Unionstore/recommend_list";
	// 餐餐抢新首页-餐餐抢店铺
	public static String NEWSY_CANCANQIANGSHOP = QIANZUI
			+ "Unionstore/cqq_index_store";
	// 新用户赠送一个月vip
	public static String GIFTVIP = QIANZUI + "Event/give_vip";
	// 餐餐抢已开通城市
	public static String CCQYIKAITONGCITY = QIANZUI
			+ "Unionstore/get_city_list";
	// 餐餐抢店铺列表
	public static String CCQSHOPLIST = QIANZUI + "Unionstore/unionstore_list";
	public static String CCQPROLIST = QIANZUI + "Wxchat/ccq_coupon";
	// 餐餐抢分类详情
	public static String CCQFENLEI = QIANZUI + "Unionstore/get_city";
	// 餐餐抢店铺详情
	public static String STOREETAIL = QIANZUI + "Unionstore/unionstore";
	// 餐餐抢店铺商品列表
	public static String STOREPRODETAIL = QIANZUI
			+ "Unionstore/unionstore_goods_list";
	// 餐餐抢商品详情
	// public static String CCQPRODETAIL = QIANZUI + "goods/goods_detail_union";
	public static String NewCCQPRODETAIL = QIANZUI + "Goods/ccq_goods";
	// 新版餐餐抢首页
	public static String NEWCCQDPDETAILSY = QIANZUI + "Ccqstore/ccq_store";
	// 新版餐餐抢店铺环境图全部
	public static String NEWCCQDPDHUANJINGALLPIC = QIANZUI
			+ "Ccqstore/ccq_store_info_more";
	// 新版餐餐抢评价
	public static String NEWCCQPINGJIA = QIANZUI
			+ "Ccqstore/ccq_store_evaluate";
	// 新版餐餐抢抢券
	public static String NEWCCQQIANGQUAN = QIANZUI
			+ "Ccqstore/ccq_store_goods_list";
	// 新版餐餐抢商家
	public static String NEWSHANGJIA = QIANZUI + "Ccqstore/ccq_store_info";
	public static String NEWSQUANTUIJIAN = QIANZUI + "Goods/referral_goods";
	// 新版推荐附近餐餐抢商品
	public static String NEWREFERRALGOODS = QIANZUI + "Goods/referral_goods";
	// 新版会员订单列表
	public static String NEWORDERLIST = QIANZUI + "Memberorder/order_list";
	// 新版推荐商品列表
	public static String NEWRECOMMENDLIST = QIANZUI
			+ "Unionstore/recommend_list";
	// 附近
	public static String FUJIN = QIANZUI + "ccqstore/nearly_store";
	// 附近-获取分级地址
	public static String FUJIN_NEREA = QIANZUI + "ccqstore/get_narea";
	// 餐餐抢获取城市下属分区
	public static String GETXIAJIFENQU = QIANZUI + "Unionstore/get_city_only";
	// 签到信息
	public static String SIGNINFO = QIANZUI + "Sign/sign_info";
	// 签到
	public static String QIANDAO = QIANZUI + "Sign/do_sign";
	// 拆红包
	public static String CHAIHONGBAO = QIANZUI + "Sign/red_packet";
	// 购物车列表
	public static String GWCLIST = QIANZUI + "Membercart/cart_list";
	// 购物车-删除
	public static String GWCDELETE = QIANZUI + "Membercart/cart_del";
	// 购物车-数量变化
	public static String GWCNUMCHANGE = QIANZUI
			+ "Membercart/cart_edit_quantity";
	// 我-主页
	public static String MYINFO = QIANZUI + "Member/member_info";
	// 新版我-主页
	public static String NEWMYINFO = QIANZUI + "Member/member_center_index";
	// 我-修改会员资料
	public static String EDITMYINFO = QIANZUI + "Member/updata_member_info";
	// 我-收货地址列表
	public static String MYADDRESS = QIANZUI + "member/address_list";
	// 我-新增地址
	public static String ADDADDRESS = QIANZUI + "member/address_add";
	// 我-删除地址
	public static String DELETEADDRESS = QIANZUI + "member/address_del";
	// 我-所有地区
	public static String ALLDIQU = QIANZUI + "member/get_area";
	// 我-单个地址详情
	public static String GETONEADDRESS = QIANZUI + "member/address_one";
	// 我-编辑地址
	public static String EDITADDRESS = QIANZUI + "member/address_edit";
	// 我-全部订单
	public static String ALLDINDAN = QIANZUI + "Memberorder/order_list";
	// 我-删除订单
	public static String DELETEDINDAN = QIANZUI + "Memberorder/order_del";
	// 我-取消订单
	public static String QUXIAODINDAN = QIANZUI + "memberorder/order_cancel";
	// 我-订单-去支付
	public static String QUZHIFUDINDAN = QIANZUI + "Pay/get_order_info";
	// 我-订单-去支付-执行支付
	public static String TIJIAOZHIFUDINDAN = QIANZUI + "pay/do_pay";
	// 我-订单-确认收货
	public static String QUERENSHOUHUODINDAN = QIANZUI
			+ "Memberorder/order_state";
	// 我-订单-查看物流
	public static String CHAKANWULIU = QIANZUI + "Memberorder/get_express";
	// 我-订单-退款售后
	public static String TUIKUANSHOUHOU = QIANZUI + "Memberorder/order_refund";
	// 我-订单-订单详情
	public static String DINDANDETAIL = QIANZUI + "Memberorder/order_one";
	// 我-订单-查看券码
	public static String CHAKANJUANMA = QIANZUI + "memberorder/checkqrcode";
	// 我-订单-申请退款退货
	public static String TUIKUANTUIHUO = QIANZUI + "Memberorder/refund";
	// 我-订单-提交评价
	public static String TIJIAOPINGJIA = QIANZUI
			+ "Evaluate/add_goods_evaluate";
	// 我-订单-普通商品提交评价
	public static String XSPROTIJIAOPINGJIA = QIANZUI
			+ "Evaluate/add_more_goods_evaluate";
	// 我-账户与安全
	public static String ZHANGHUYUANQUAN = QIANZUI + "Member/security";
	// 我-账户与安全-检查登录密码
	public static String CHECKMIMA = QIANZUI + "Member/check_pwd";
	// 我-账户与安全-修改登录密码
	public static String UPDATAMIMA = QIANZUI + "Member/member_pwd";
	// 我-账户与安全-检查支付密码
	public static String CHECKPAYMIMA = QIANZUI + "Member/check_paypwd";
	// 我-账户与安全-设置支付密码
	public static String SHEZHIPAYMIMA = QIANZUI + "Member/member_paypwd";
	// 我-账户与安全-提交实名认证
	public static String TIJIAOSHIMINGRENZHENG = QIANZUI + "member/real_verify";
	// 我-账户与安全-获取实名认证信息
	public static String GETSHIMINGINFO = QIANZUI + "member/check_real_verify";
	// 我-绑定手机
	public static String BINDPHONE = QIANZUI + "member/bind_phone";
	// 我-解除手机号绑定
	public static String CANCELBINDPHONE = QIANZUI
			+ "Member/unbind_member_mobile";
	// 我-解除微信绑定
	public static String CANCELBINDWECHAT = QIANZUI + "Weixin/del_openid";
	// 我-VIP页面
	public static String VIPINFO = QIANZUI + "Membervip/my_vip";
	// 我-钱包信息
	public static String MYWALLET = QIANZUI + "Memberwallet/wallet";
	// 资金详情-所有
	public static String ALLZIJINDETAIL = QIANZUI + "Memberwallet/money";
	// 我-钱包-云币详情
	public static String YUNBIDETAIL = QIANZUI + "Memberwallet/point";
	// 我-钱包-余额详情
	public static String YUEDETAIL = QIANZUI + "Memberwallet/balance";
	// 我-钱包-招商详情
	public static String ZHAOSHANGDETAIL = QIANZUI + "Memberwallet/merchants";
	// 我-钱包-推广详情
	public static String TUIGUANGDETAIL = QIANZUI + "Memberwallet/promote";
	// 我-钱包-vip详情
	public static String VIPDETAIL = QIANZUI + "Memberwallet/vip";
	// 我-银行卡列表
	public static String BANKLIST = QIANZUI + "Memberwallet/bank_card";
	public static String NEWBANKLIST = QIANZUI + "Memberwallet/card_list";
	// 我-删除银行卡
	public static String DELETEBANK = QIANZUI + "Memberwallet/bank_card_del";
	public static String NEWDELETEBANK = QIANZUI + "Memberwallet/del_card";
	// 我-添加银行卡
	public static String ADDBANK = QIANZUI + "memberwallet/add_bank_card";
	public static String NEWADDBANK = QIANZUI + "Memberwallet/add_card";
	// 我-添加银行卡-卡号获取开户银行
	public static String GETKAIHUBANK = QIANZUI
			+ "Memberwallet/discern_bankcard";
	// 我-添加银行卡图片
	public static String ADDBANKARDPIC = "/public/index.php?s=/webapi/img/upload_bank_card";
	public static String NEWADDBANKARDPIC = "/public/index.php?s=/webapi/Img2/upload_bank_card";
	// 我-头像图片
	public static String TOUXIANGPIC = "/public/index.php?s=/webapi/img/upload_member_avator";
	public static String NEWTOUXIANGPIC = "/public/index.php?s=/webapi/Img2/upload_member_avator";
	// 我-默认头像图片
	public static String MORENTOUXIANG = QIANZUI + "Member/update_avatar";
	// 实名认证图片
	public static String SHIMINGPIC = "/public/index.php?s=/webapi/img/upload_real_verify";
	public static String NEWSHIMINGPIC = "/public/index.php?s=/webapi/Img2/upload_real_verify";
	// 我-提交提现信息
	public static String TIXIANINFO = QIANZUI + "Memberwallet/withdraw";
	// 我-提现列表
	public static String TIXIANLIST = QIANZUI + "Memberwallet/withdraw_list";
	// 我-反馈
	public static String FANKUIXINXI = QIANZUI + "member/feedback";
	// 我-提现账户各资金
	public static String TIXIANGEZIJIN = QIANZUI + "Memberwallet/withdraw_data";
	// 我-提现记录详情
	public static String TIXIANJILUDETAIL = QIANZUI
			+ "Memberwallet/withdraw_list_one";
	// 我-商家入驻-添加商家
	public static String ADDSHANGJIA = QIANZUI + "Marketer/add_store";
	// 我-商家入驻-商家分类
	public static String SHANGJIAFENLEI = QIANZUI + "Goodclass/bunding_class";
	// 我-商家入驻-上传商家图片
	public static String SHANGCHJUANSHANGJIAPIC = "/public/index.php?s=/webapi/Img2/upload";

}
