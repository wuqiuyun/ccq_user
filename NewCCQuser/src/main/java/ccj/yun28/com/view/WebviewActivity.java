package ccj.yun28.com.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import ccj.yun28.com.BaseActivity;
import ccj.yun28.com.R;
import ccj.yun28.com.sy.ProductDetailActivity;

public class WebviewActivity extends BaseActivity implements OnClickListener {

	private WebView webview;
	private ProgressBar myProgressBar;
	private String url;

	// 标题
	private TextView tv_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);

		LinearLayout line_back = (LinearLayout) findViewById(R.id.line_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		webview = (WebView) findViewById(R.id.web_view);
		webview.getSettings().setJavaScriptEnabled(true);
		String title = getIntent().getExtras().getString("title");
		tv_title.setText(title);
		url = getIntent().getExtras().getString("url");
		webview.loadUrl(url);
		webview.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				if (url != null && url.contains("teyun:///buy:/goods_id")) {
					Intent intent = new Intent(WebviewActivity.this, ProductDetailActivity.class);
					String[] geturl = url.split("/");
					String goods_id = geturl[geturl.length - 1];
					intent.putExtra("goods_id", goods_id);
					startActivity(intent);
				}else{
					view.loadUrl(url);
				}
				return true;
			}
		});
		line_back.setOnClickListener(this);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		onBackPressed();
	}
}
