package ccj.yun28.com.sy.fragment;

import java.util.HashMap;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;
import ccj.yun28.com.R;
import ccj.yun28.com.sy.ProductDetailActivity;
import ccj.yun28.com.utils.FileUtils;
import ccj.yun28.com.utils.JiekouUtils;
import ccj.yun28.com.utils.Utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 商品详情 - 详情
 * 
 * @author meihuali
 * 
 */
public class DetailFragment extends Fragment {

	protected static final int HANDLER_NET_FAILURE = 0;
	private HashMap<Integer, View> temp = new HashMap<Integer, View>();
	private WebView webview;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_NET_FAILURE:
				dissDialog();
				Toast.makeText(getActivity(), "当前网络不可用", Toast.LENGTH_SHORT)
						.show();
				break;

			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_detail, null);
		webview = (WebView) view.findViewById(R.id.webview);

		showLoading();
		String goods_id = ((ProductDetailActivity) getActivity()).getGoods_id();
		productDetailPicHttpPost(goods_id);

		return view;
	}

	private void productDetailPicHttpPost(String goods_id) {
		// TODO Auto-generated method stub
		HttpUtils httpUtils = new HttpUtils();
		Utils utils = new Utils();
		String[] verstring = utils.getVersionInfo(getActivity());

		RequestParams params = new RequestParams();
		params.addHeader("ccq", utils.getSystemVersion()+","+ verstring[0]+","+verstring[2]);
		params.addBodyParameter("goods_id", goods_id);
		httpUtils.send(HttpMethod.POST, JiekouUtils.PRODUCTDETAILPIC, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(HANDLER_NET_FAILURE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						productDetailPicListInfo(arg0.result);
					}
				});
	}

	protected void productDetailPicListInfo(String result) {
		// TODO Auto-generated method stub
		toInfoDetail(result);
	}

	private void toInfoDetail(final String contentString) {
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected void onPreExecute() {
				// showDialog(GpsActivity.WAIT_DIALOG, "正在加载.....");
//				Toast.makeText(getActivity(), "正在加载.....", Toast.LENGTH_SHORT).show();

			};

			@Override
			protected void onPostExecute(Boolean result) {
				dissDialog();
				int index = contentString.indexOf("</html>");
                String res = contentString.substring(0,index+"</html>".length()-1);

				webview.loadDataWithBaseURL(null, res, "text/html", "utf-8",
		                null);
//				boolean isExist = FileUtils.fileIsExists(FileUtils.SavePath);
//				if (isExist) {
//					webview.getSettings().setJavaScriptEnabled(true);
//					webview.getSettings().setDefaultTextEncodingName("UTF-8");
//					String tophtmlString = "<html><body>";
//					String endhtmlString = "</body></html>";
//					webview.loadUrl("file:///" + FileUtils.SavePath);
//				} else {
//					Toast.makeText(getActivity(), "加载失败", 1).show();
//				}

			};

			@Override
			protected Boolean doInBackground(Void... params) {
				FileUtils.saveAsAddThingFileWriter(contentString);
				return true;
			}
		}.execute();
	}

	// 加载中动画
	private Dialog loadingDialog;

	// 加载动画
	private void showLoading() {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.loading, null);
		ImageView image = (ImageView) view.findViewById(R.id.iv_loading);
		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.loading_anim);
		image.startAnimation(animation);
		loadingDialog = new Dialog(getActivity(), R.style.mDialogStyle);
		loadingDialog.setContentView(view);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.show();
	}

	// 关闭加载dialog
	private void dissDialog() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
	}

}
