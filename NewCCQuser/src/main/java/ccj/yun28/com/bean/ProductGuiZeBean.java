package ccj.yun28.com.bean;

/**
 * 10.商品详情-规则详情
 * 
 * @author meihuali
 * 
 */
public class ProductGuiZeBean {
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
