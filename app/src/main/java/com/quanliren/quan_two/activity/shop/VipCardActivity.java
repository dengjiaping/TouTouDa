package com.quanliren.quan_two.activity.shop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.bean.LoginUser;
import com.quanliren.quan_two.bean.ShopBean;
import com.quanliren.quan_two.fragment.SetingMoreFragment;
import com.quanliren.quan_two.pull.PullToRefreshLayout;
import com.quanliren.quan_two.pull.lib.ActionBarPullToRefresh;
import com.quanliren.quan_two.pull.lib.listeners.OnRefreshListener;
import com.quanliren.quan_two.util.LogUtil;
import com.quanliren.quan_two.util.URL;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import java.util.concurrent.atomic.AtomicBoolean;

@EActivity(R.layout.actinfo)
@WindowFeature(value = Window.FEATURE_INDETERMINATE_PROGRESS)
public class VipCardActivity extends BaseActivity implements OnRefreshListener {
    @ViewById(R.id.webview)
    WebView wView;
    @ViewById
    View back;
    @ViewById
    View refere;
    @ViewById
    View go;
    @Extra
    String channelType = "";
    @Extra
    ShopBean sb;
    @ViewById
    PullToRefreshLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    void initView() {
        getSupportActionBar().setTitle(sb.getTitle());

        ActionBarPullToRefresh.from(this).allChildrenArePullable().setAutoStart(false).listener(this).setup(layout);
        layout.setEnabled(false);

        wView.setVerticalScrollBarEnabled(false);
        wView.setWebChromeClient(new WebChromeClient());
        wView.setHorizontalScrollBarEnabled(false);
        WebSettings webSettings = wView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setBlockNetworkImage(false);
        CookieManager.getInstance().setAcceptCookie(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        wView.setWebViewClient(new MyWebViewClient());
        wView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");

        LoginUser user = getHelper().getUser();
        if (user != null) {
            wView.loadUrl(URL.URL + "/client/pay/to_alipay.php?gnumber=" + sb.getId() + "&token="
                    + user.getToken());
        }
    }

    AtomicBoolean ab = new AtomicBoolean(false);
    String awid = "";
    String finishUrl = "";

    final class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.indexOf("ex_gateway_cashier") > -1) {
                finishUrl = url;
            }
            if (awid.equals("")) {
                if (url.indexOf("awid") > -1) {
                    String str = url.substring(url.indexOf("awid=")).replace(
                            "awid=", "");
                    awid = str;
                    if (!awid.equals("") && ab.compareAndSet(false, true)) {
                        view.loadUrl(("https://wappaygw.alipay.com/cashier/cashier_gateway_pay.htm?channelType="
                                + channelType + "&awid=" + awid));
                        awid = "";
                        return true;
                    }
                }
            }
            return false;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            LogUtil.d("WebView", url);
            super.onPageStarted(view, url, favicon);
            if (url.indexOf("ex_gateway_cashier") > -1) {
                finishUrl = url;
            }
            if (awid.equals("")) {
                if (url.indexOf("awid") > -1) {
                    String str = url.substring(url.indexOf("awid=")).replace(
                            "awid=", "");
                    awid = str;

                    if (!awid.equals("") && ab.compareAndSet(false, true)) {
                        view.loadUrl(("https://wappaygw.alipay.com/cashier/cashier_gateway_pay.htm?channelType="
                                + channelType + "&awid=" + awid));
                        awid = "";
                    }
                }
            }


        }

        public void onPageFinished(final WebView view, String url) {
            super.onPageFinished(view, url);
            /*if (!awid.equals("") && ab.compareAndSet(false, true)) {
				view.loadUrl("https://wappaygw.alipay.com/cashier/cashier_gateway_pay.htm?channelType="
						+ channelType + "&awid=" + awid);
				awid = "";
			}*/
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            handler.proceed();
        }
    }

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            // try {
            // ReadWriteFile.creatTxtFile();
            // ReadWriteFile.writeTxtFile(html);
            // } catch (IOException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            LogUtil.d("HTML", html);
        }

        @JavascriptInterface
        public void callBack() {
            sendb = true;
            Intent i = new Intent(SetingMoreFragment.UPDATE_USERINFO);
            sendBroadcast(i);
        }

        @JavascriptInterface
        public void close() {
            finish();
        }
    }

    boolean sendb = false;

    @Click(R.id.back)
    public void backs(View v) {
        if (!finishUrl.equals(wView.getUrl().toString()))
            wView.goBack();
    }

    @Click
    public void refere(View v) {
        wView.reload();
    }

    @Click
    public void go(View v) {
        wView.goForward();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (!sendb) {
            Intent i = new Intent(SetingMoreFragment.UPDATE_USERINFO);
            sendBroadcast(i);
        }
    }

    @Override
    public void onBackPressed() {
        dialogFinish();
    }

    @Override
    public void finishActivity() {
        dialogFinish();
    }

    public void dialogFinish() {
        new AlertDialog.Builder(VipCardActivity.this).setTitle("提示")
                .setMessage("您确定要取消本次交易吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        scrollToFinishActivity();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                }).create().show();
    }

    @Override
    public void onRefreshStarted(View view) {
        // TODO Auto-generated method stub

    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                layout.setRefreshComplete();
            } else {
                layout.setRefreshing(true, true);
            }
            super.onProgressChanged(view, newProgress);
        }

    }
}
