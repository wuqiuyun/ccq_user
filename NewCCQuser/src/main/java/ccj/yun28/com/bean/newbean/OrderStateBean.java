package ccj.yun28.com.bean.newbean;

import java.io.Serializable;

/**
 * 订单状态Bean
 *
 * @author wuqiuyun
 */
public class OrderStateBean implements Serializable {

    private String order_del;
    private String order_pay;
    private String order_check;
    private String order_more_one;
    private String order_evaluate;
    private String order_state_name;

    public String getOrder_del() {
        return order_del;
    }

    public void setOrder_del(String order_del) {
        this.order_del = order_del;
    }

    public String getOrder_pay() {
        return order_pay;
    }

    public void setOrder_pay(String order_pay) {
        this.order_pay = order_pay;
    }

    public String getOrder_check() {
        return order_check;
    }

    public void setOrder_check(String order_check) {
        this.order_check = order_check;
    }

    public String getOrder_more_one() {
        return order_more_one;
    }

    public void setOrder_more_one(String order_more_one) {
        this.order_more_one = order_more_one;
    }

    public String getOrder_evaluate() {
        return order_evaluate;
    }

    public void setOrder_evaluate(String order_evaluate) {
        this.order_evaluate = order_evaluate;
    }

    public String getOrder_state_name() {
        return order_state_name;
    }

    public void setOrder_state_name(String order_state_name) {
        this.order_state_name = order_state_name;
    }

    @Override
    public String toString() {
        return "OrderStateBean{" +
                "order_del='" + order_del + '\'' +
                ", order_pay='" + order_pay + '\'' +
                ", order_check='" + order_check + '\'' +
                ", order_more_one='" + order_more_one + '\'' +
                ", order_evaluate='" + order_evaluate + '\'' +
                ", order_state_name='" + order_state_name + '\'' +
                '}';
    }
}
