package ccj.yun28.com.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 全部订单 - 全部 - 商品
 * 
 * @author meihuali
 * 
 */
public class AllDinDanGoodsBean implements Parcelable {

	private String goods_id;
	private String goods_name;
	private String goods_num;
	private String image;
	private String spec_value;
	private String order_id;
	private String union_type;

	public AllDinDanGoodsBean() {

	}
	public AllDinDanGoodsBean(String goods_id,String goods_name,String goods_num,String image,String spec_value,String order_id,String union_type) {
		this.goods_id = goods_id; 
		this.goods_name = goods_name; 
		this.goods_num = goods_num; 
		this.image = image; 
		this.spec_value = spec_value; 
		this.order_id = order_id; 
		this.union_type = union_type; 
	}

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getSpec_value() {
		return spec_value;
	}

	public void setSpec_value(String spec_value) {
		this.spec_value = spec_value;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	
	
	public String getUnion_type() {
		return union_type;
	}
	public void setUnion_type(String union_type) {
		this.union_type = union_type;
	}
	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeString(goods_id);
		out.writeString(goods_name);
		out.writeString(goods_num);
		out.writeString(image);
		out.writeString(spec_value);
		out.writeString(order_id);
		out.writeString(union_type);
	}


	public static final Parcelable.Creator<AllDinDanGoodsBean> CREATOR = new Creator<AllDinDanGoodsBean>() {
		@Override
		public AllDinDanGoodsBean[] newArray(int size) {
			return new AllDinDanGoodsBean[size];
		}

		@Override
		public AllDinDanGoodsBean createFromParcel(Parcel in) {
			return new AllDinDanGoodsBean(in);
		}
	};

	public AllDinDanGoodsBean(Parcel in) {
		goods_id = in.readString();
		goods_name = in.readString();
		goods_num = in.readString();
		image = in.readString();
		spec_value = in.readString();
		order_id = in.readString();
		union_type = in.readString();
	}

}
