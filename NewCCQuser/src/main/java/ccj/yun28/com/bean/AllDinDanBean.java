package ccj.yun28.com.bean;

import java.util.ArrayList;

/**
 * 全部订单 - 全部
 * 
 * @author meihuali
 * 
 */
public class AllDinDanBean {

	private String add_time;
	private String evaluation_state;
	private String finnshed_time;
	private ArrayList<AllDinDanGoodsBean> goods_list;
	private String goods_num;
	private String live_store_tel;
	private String order_amount;
	private String goods_amount;
	private String order_id;
	private String order_sn;
	private String order_state;
	private String payment_time;
	private String refund_state;
	private String store_id;
	private String store_name;
	private String union_type;
	public String getAdd_time() {
		return add_time;
	}
	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}
	public String getEvaluation_state() {
		return evaluation_state;
	}
	public void setEvaluation_state(String evaluation_state) {
		this.evaluation_state = evaluation_state;
	}
	public String getFinnshed_time() {
		return finnshed_time;
	}
	public void setFinnshed_time(String finnshed_time) {
		this.finnshed_time = finnshed_time;
	}
	public ArrayList<AllDinDanGoodsBean> getGoods_list() {
		return goods_list;
	}
	public void setGoods_list(ArrayList<AllDinDanGoodsBean> goods_list) {
		this.goods_list = goods_list;
	}
	public String getGoods_num() {
		return goods_num;
	}
	public void setGoods_num(String goods_num) {
		this.goods_num = goods_num;
	}
	public String getLive_store_tel() {
		return live_store_tel;
	}
	public void setLive_store_tel(String live_store_tel) {
		this.live_store_tel = live_store_tel;
	}
	public String getOrder_amount() {
		return order_amount;
	}
	public void setOrder_amount(String order_amount) {
		this.order_amount = order_amount;
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
	public String getPayment_time() {
		return payment_time;
	}
	public void setPayment_time(String payment_time) {
		this.payment_time = payment_time;
	}
	public String getRefund_state() {
		return refund_state;
	}
	public void setRefund_state(String refund_state) {
		this.refund_state = refund_state;
	}
	public String getStore_id() {
		return store_id;
	}
	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}
	public String getStore_name() {
		return store_name;
	}
	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}
	public String getUnion_type() {
		return union_type;
	}
	public void setUnion_type(String union_type) {
		this.union_type = union_type;
	}
	
	public String getGoods_amount() {
		return goods_amount;
	}
	public void setGoods_amount(String goods_amount) {
		this.goods_amount = goods_amount;
	}
	@Override
	public String toString() {
		return "AllDinDanBean [add_time=" + add_time + ", evaluation_state="
				+ evaluation_state + ", finnshed_time=" + finnshed_time
				+ ", goods_list=" + goods_list + ", goods_num=" + goods_num
				+ ", live_store_tel=" + live_store_tel + ", order_amount="
				+ order_amount + ", order_id=" + order_id + ", order_sn="
				+ order_sn + ", order_state=" + order_state + ", payment_time="
				+ payment_time + ", refund_state=" + refund_state
				+ ", store_id=" + store_id + ", store_name=" + store_name
				+ ", union_type=" + union_type + "]";
	}


}
