package ccj.yun28.com.bean;

import java.util.ArrayList;

/**
 * 普通商品订单详情
 * 
 * @author meihuali
 * 
 */
public class ProDinDanDetailBean {

	private String add_time;
	private String buyer_id;
	private String evaluation_state;
	private FapiaoDizhiBean extend_order_common;
	private String finnshed_time;
	private String goods_num;
	private String order_amount;
	private String goods_amount;
	private ArrayList<ProGoodsList> order_goods_list;
	private String order_id;
	private String order_sn;
	private String order_state;
	private String payment_code;
	private String payment_time;
	private String shipping_fee;
	private String shipping_time;
	private ProStoreInfo store;
	private String store_id;
	public String getAdd_time() {
		return add_time;
	}
	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}
	public String getBuyer_id() {
		return buyer_id;
	}
	public void setBuyer_id(String buyer_id) {
		this.buyer_id = buyer_id;
	}
	public String getEvaluation_state() {
		return evaluation_state;
	}
	public void setEvaluation_state(String evaluation_state) {
		this.evaluation_state = evaluation_state;
	}
	public FapiaoDizhiBean getExtend_order_common() {
		return extend_order_common;
	}
	public void setExtend_order_common(FapiaoDizhiBean extend_order_common) {
		this.extend_order_common = extend_order_common;
	}
	public String getFinnshed_time() {
		return finnshed_time;
	}
	public void setFinnshed_time(String finnshed_time) {
		this.finnshed_time = finnshed_time;
	}
	public String getGoods_num() {
		return goods_num;
	}
	public void setGoods_num(String goods_num) {
		this.goods_num = goods_num;
	}
	public String getOrder_amount() {
		return order_amount;
	}
	public void setOrder_amount(String order_amount) {
		this.order_amount = order_amount;
	}
	public ArrayList<ProGoodsList> getOrder_goods_list() {
		return order_goods_list;
	}
	public void setOrder_goods_list(ArrayList<ProGoodsList> order_goods_list) {
		this.order_goods_list = order_goods_list;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getOrder_sn() {
		return order_sn;
	}
	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}
	public String getOrder_state() {
		return order_state;
	}
	public void setOrder_state(String order_state) {
		this.order_state = order_state;
	}
	public String getPayment_code() {
		return payment_code;
	}
	public void setPayment_code(String payment_code) {
		this.payment_code = payment_code;
	}
	public String getPayment_time() {
		return payment_time;
	}
	public void setPayment_time(String payment_time) {
		this.payment_time = payment_time;
	}
	public String getShipping_fee() {
		return shipping_fee;
	}
	public void setShipping_fee(String shipping_fee) {
		this.shipping_fee = shipping_fee;
	}
	public String getShipping_time() {
		return shipping_time;
	}
	public void setShipping_time(String shipping_time) {
		this.shipping_time = shipping_time;
	}
	public ProStoreInfo getStore() {
		return store;
	}
	public void setStore(ProStoreInfo store) {
		this.store = store;
	}
	public String getStore_id() {
		return store_id;
	}
	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}
	
	public String getGoods_amount() {
		return goods_amount;
	}
	public void setGoods_amount(String goods_amount) {
		this.goods_amount = goods_amount;
	}
	@Override
	public String toString() {
		return "ProDinDanDetailBean [add_time=" + add_time + ", buyer_id="
				+ buyer_id + ", evaluation_state=" + evaluation_state
				+ ", extend_order_common=" + extend_order_common
				+ ", finnshed_time=" + finnshed_time + ", goods_num="
				+ goods_num + ", order_amount=" + order_amount
				+ ", goods_amount=" + goods_amount + ", order_goods_list="
				+ order_goods_list + ", order_id=" + order_id + ", order_sn="
				+ order_sn + ", order_state=" + order_state + ", payment_code="
				+ payment_code + ", payment_time=" + payment_time
				+ ", shipping_fee=" + shipping_fee + ", shipping_time="
				+ shipping_time + ", store=" + store + ", store_id=" + store_id
				+ "]";
	}
	
	
}
