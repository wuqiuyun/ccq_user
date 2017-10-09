package ccj.yun28.com.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 普通商品订单详情商品详情
 * @author meihuali
 *
 */
public class ProGoodsList implements Parcelable{
	private String goods_id;
	private String goods_image;
	private String goods_name;
	private String goods_num;
	private String goods_price;
	private String order_id;
	private String rec_id;
	public ProGoodsList() {
		
	}
	public ProGoodsList(String goods_id,String goods_image,String goods_name,String goods_num,String goods_price,String order_id,String rec_id) {
		this.goods_id = goods_id; 
		this.goods_image = goods_image; 
		this.goods_name = goods_name; 
		this.goods_num = goods_num; 
		this.goods_price = goods_price; 
		this.order_id = order_id; 
		this.rec_id = rec_id; 
	}
	public String getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}
	public String getGoods_image() {
		return goods_image;
	}
	public void setGoods_image(String goods_image) {
		this.goods_image = goods_image;
	}
	public String getGoods_name() {
		return goods_name;
	}
	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}
	public String getGoods_num() {
		return goods_num;
	}
	public void setGoods_num(String goods_num) {
		this.goods_num = goods_num;
	}
	public String getGoods_price() {
		return goods_price;
	}
	public void setGoods_price(String goods_price) {
		this.goods_price = goods_price;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getRec_id() {
		return rec_id;
	}
	public void setRec_id(String rec_id) {
		this.rec_id = rec_id;
	}
	@Override
	public String toString() {
		return "ProGoodsList [goods_id=" + goods_id + ", goods_image="
				+ goods_image + ", goods_name=" + goods_name + ", goods_num="
				+ goods_num + ", goods_price=" + goods_price + ", order_id="
				+ order_id + ", rec_id=" + rec_id + "]";
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeString(goods_id);
		out.writeString(goods_image);
		out.writeString(goods_name);
		out.writeString(goods_num);
		out.writeString(goods_price);
		out.writeString(order_id);
		out.writeString(rec_id);
	}

	public static final Parcelable.Creator<ProGoodsList> CREATOR = new Creator<ProGoodsList>() {
		@Override
		public ProGoodsList[] newArray(int size) {
			return new ProGoodsList[size];
		}

		@Override
		public ProGoodsList createFromParcel(Parcel in) {
			return new ProGoodsList(in);
		}
	};

	public ProGoodsList(Parcel in) {
		goods_id = in.readString();
		goods_image = in.readString();
		goods_name = in.readString();
		goods_num = in.readString();
		goods_price = in.readString();
		order_id = in.readString();
		rec_id = in.readString();
	}
}
