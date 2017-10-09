package ccj.yun28.com.bean;

/**
 * 8.商品详情-当前规则，（EG：颜色，尺码）
 * 
 * @author meihuali
 * 
 */
public class ProductDQGuizeBean {

	private String id;
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ProductGuizeBean [id=" + id + ", name=" + name + "]";
	}

}
