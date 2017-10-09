package ccj.yun28.com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import ccj.yun28.com.bean.newbean.RecommendGoodsBean;
import ccj.yun28.com.json.CommonList;
import ccj.yun28.com.neworder.RecommendAllGoodsAdapter;
import ccj.yun28.com.utils.DialogUtil;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class RecmmendGoodsActivity extends BaseActivity implements OnClickListener{
	
	private String starlat = "";
	private String starlng = "";
	private String city = "";
	private String district = "";
	
	private RecommendAllGoodsAdapter recommendAllGoodsAdapter;
	private List<RecommendGoodsBean> list;
	
	private LinearLayout line_back;
	private ListView lv;
	//private MaterialRefreshLayout refreshLayout;
	
	private int page = 1;
	private HttpUtils httpUtils;
	private Utils utils;
	private String[] verstring;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recmmend_goods);
		
		Intent intent = getIntent();
		if (intent != null) {
			starlat = intent.getStringExtra("starlat");
			starlng = intent.getStringExtra("starlng");
			city = intent.getStringExtra("city");
			district = intent.getStringExtra("district");
		}
		initView();
		setListener();
		initData();
		loadAllRecommendGoods();
	}
	
	private void initView() {
		line_back = (LinearLayout) findViewById(R.id.line_back);
		lv = (ListView) findViewById(R.id.lv);
		//refreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh_layout);
		//refreshLayout.setLoadMore(true);
	}
	
	private void setListener() {
		// TODO Auto-generated method stub
		line_back.setOnClickListener(this);
		/*refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {

			@Override
			public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
				refreshLayout.finishRefresh();
			}

			@Override
			public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
				page++;
				loadAllRecommendGoods();
			}
		});*/
	}

	private void initData() {
		utils = new Utils();
		verstring = utils.getVersionInfo(this);
		httpUtils = new HttpUtils();
		if (null == list) {
			list = new ArrayList<RecommendGoodsBean>();
		}
		if (null == recommendAllGoodsAdapter) {
			recommendAllGoodsAdapter = new RecommendAllGoodsAdapter(this, list);
			lv.setAdapter(recommendAllGoodsAdapter);
		} else {
			recommendAllGoodsAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 加载全部推荐商品
	 */
	@SuppressWarnings("static-access")
	private void loadAllRecommendGoods() {
		page++;
		DialogUtil.showDialogLoading(this);
		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion() 
				+ "," + verstring[0]
				+ "," + verstring[2]);
		params.addBodyParameter("api_v", "v2");
		params.addBodyParameter("page", "");

		httpUtils.send(HttpMethod.POST, JiekouUtils.NEWORDER_RECOMMENDLIST,
				params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						DialogUtil.hideDialogLoading();
						Toast.makeText(RecmmendGoodsActivity.this, "当前网络不可用,请检查网络",
								Toast.LENGTH_SHORT).show();
					}

					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						DialogUtil.hideDialogLoading();
						CommonList<RecommendGoodsBean> goods = CommonList.fromJson(
								arg0.result, RecommendGoodsBean.class);

						Log.e("log", "推荐全部->goods: " + goods.toString());
						//Toast.makeText(RecmmendGoodsActivity.this, "获取成功",Toast.LENGTH_SHORT).show();

						if ("200".equals(goods.getCode())) {
							list = goods.getData();
							//refreshLayout.finishRefresh();
							//refreshLayout.finishRefreshLoadMore();
							recommendAllGoodsAdapter.setData(list);
							
							Map<String, String> map = new HashMap<String, String>();
							map.put("city", city);
							map.put("district", district);
							map.put("latitude", starlat);
							map.put("longitude", starlng);
							recommendAllGoodsAdapter.setCityInfo(map);
							
							recommendAllGoodsAdapter.notifyDataSetChanged();
						} else {
							Toast.makeText(RecmmendGoodsActivity.this, goods.getMessage(),Toast.LENGTH_SHORT).show();
						}
					}
				});
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.line_back:
			finish();
			break;

		default:
			break;
		}
	}	
}
