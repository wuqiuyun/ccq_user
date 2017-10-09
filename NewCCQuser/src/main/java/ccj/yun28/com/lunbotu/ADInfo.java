package ccj.yun28.com.lunbotu;

public class ADInfo {
	String auto_id;
	String pic;
	String url;
	String sort;
	String add_time;
	String tagger_id;
	String tagger_type;
	
	
	private String data;
	private String item_type;
	private String goods_id;
	private String store_id;
	
	
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
	public String getAuto_id() {
		return auto_id;
	}
	public void setAuto_id(String auto_id) {
		this.auto_id = auto_id;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getAdd_time() {
		return add_time;
	}
	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}
	public String getTagger_id() {
		return tagger_id;
	}
	public void setTagger_id(String tagger_id) {
		this.tagger_id = tagger_id;
	}
	public String getTagger_type() {
		return tagger_type;
	}
	public void setTagger_type(String tagger_type) {
		this.tagger_type = tagger_type;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getItem_type() {
		return item_type;
	}
	public void setItem_type(String item_type) {
		this.item_type = item_type;
	}
	@Override
	public String toString() {
		return "ADInfo [auto_id=" + auto_id + ", pic=" + pic + ", url=" + url
				+ ", sort=" + sort + ", add_time=" + add_time + ", tagger_id="
				+ tagger_id + ", tagger_type=" + tagger_type + ", data=" + data
				+ ", item_type=" + item_type + ", goods_id=" + goods_id
				+ ", store_id=" + store_id + "]";
	}
	
	

	
}
