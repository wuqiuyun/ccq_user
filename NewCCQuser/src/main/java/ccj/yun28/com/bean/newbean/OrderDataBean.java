package ccj.yun28.com.bean.newbean;

import java.io.Serializable;

/**
 * 订单界面数据 bean
 *
 * @author wuqiuyun
 */
public class OrderDataBean implements Serializable {

    private String order_id;
    private String store_name;
    private String order_sn;
    private String goods_amount;
    private String order_amount;
    private String order_state;
    private String add_time;
    private String payment_time;
    private String finnshed_time;
    private String check_number;
    private String union_type;
    private String evaluation_state;
    private String goods_name;
    private String goods_image;
    
    private String goods_id;
	private String store_id;


	
    private OrderStateBean state;

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public String getOrder_sn() {
		return order_sn;
	}

	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}

	public String getGoods_amount() {
		return goods_amount;
	}

	public void setGoods_amount(String goods_amount) {
		this.goods_amount = goods_amount;
	}

	public String getOrder_amount() {
		return order_amount;
	}

	public void setOrder_amount(String order_amount) {
		this.order_amount = order_amount;
	}

	public String getOrder_state() {
		return order_state;
	}

	public void setOrder_state(String order_state) {
		this.order_state = order_state;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getPayment_time() {
		return payment_time;
	}

	public void setPayment_time(String payment_time) {
		this.payment_time = payment_time;
	}

	public String getFinnshed_time() {
		return finnshed_time;
	}

	public void setFinnshed_time(String finnshed_time) {
		this.finnshed_time = finnshed_time;
	}

	public String getCheck_number() {
		return check_number;
	}

	public void setCheck_number(String check_number) {
		this.check_number = check_number;
	}

	public String getUnion_type() {
		return union_type;
	}

	public void setUnion_type(String union_type) {
		this.union_type = union_type;
	}

	public String getEvaluation_state() {
		return evaluation_state;
	}

	public void setEvaluation_state(String evaluation_state) {
		this.evaluation_state = evaluation_state;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getGoods_image() {
		return goods_image;
	}

	public void setGoods_image(String goods_image) {
		this.goods_image = goods_image;
	}

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	public String getStore_id() {
		return store_id;
	}

	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	public OrderStateBean getState() {
		return state;
	}

	public void setState(OrderStateBean state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "OrderDataBean [order_id=" + order_id + ", store_name="
				+ store_name + ", order_sn=" + order_sn + ", goods_amount="
				+ goods_amount + ", order_amount=" + order_amount
				+ ", order_state=" + order_state + ", add_time=" + add_time
				+ ", payment_time=" + payment_time + ", finnshed_time="
				+ finnshed_time + ", check_number=" + check_number
				+ ", union_type=" + union_type + ", evaluation_state="
				+ evaluation_state + ", goods_name=" + goods_name
				+ ", goods_image=" + goods_image + ", goods_id=" + goods_id
				+ ", store_id=" + store_id + ", state=" + state + "]";
	}

    
    
    
}
