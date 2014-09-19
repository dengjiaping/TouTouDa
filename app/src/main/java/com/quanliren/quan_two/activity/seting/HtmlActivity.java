package com.quanliren.quan_two.activity.seting;

import android.webkit.WebSettings;
import android.webkit.WebView;

import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.serviceinfo)
public class HtmlActivity extends BaseActivity {

    @Extra
    String url;
    @Extra
    String title;
    @ViewById
    WebView webview;

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getSupportActionBar().setTitle(title);
    }

    @AfterViews
    void initView() {
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.loadUrl(url);
    }
}
