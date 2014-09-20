package com.quanliren.quan_two.activity.shop.product;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.activity.image.*;
import com.quanliren.quan_two.adapter.ImageAdapter;
import com.quanliren.quan_two.adapter.ProductGridAdapter.IProductGridListener;
import com.quanliren.quan_two.bean.ProductBean;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.custom.GalleryNavigator;
import com.quanliren.quan_two.pull.PullToRefreshLayout;
import com.quanliren.quan_two.pull.lib.ActionBarPullToRefresh;
import com.quanliren.quan_two.pull.lib.listeners.OnRefreshListener;
import com.quanliren.quan_two.util.ImageUtil;
import com.quanliren.quan_two.util.URL;
import com.quanliren.quan_two.util.Util;
import com.quanliren.quan_two.util.http.JsonHttpResponseHandler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EActivity(R.layout.product_detail)
public class ProductDetailActivity extends BaseActivity implements
        OnRefreshListener, IProductGridListener, OnPageChangeListener {

    @Extra
    ProductBean bean;

    @ViewById
    PullToRefreshLayout layout;

    @ViewById
    ViewPager viewpager;
    @ViewById
    GalleryNavigator gallerynavigator;
    @ViewById
    TextView title;
    @ViewById
    TextView detail;
    @ViewById
    TextView remark;
    @ViewById
    Button buy_btn;
    @ViewById
    View hide_ll;
    @ViewById
    View exchange_ll;
    @ViewById
    TextView exchange_detail;

    ImageAdapter adapter;

    @AfterViews
    void initView() {
        ActionBarPullToRefresh.from(this).allChildrenArePullable()
                .setAutoStart(true).listener(this).setup(layout);
        viewpager.setOnPageChangeListener(this);

        buy_btn.setVisibility(View.GONE);
        ViewHelper.setTranslationY(buy_btn, ImageUtil.dip2px(this, 45));
    }

    @Override
    public void onRefreshStarted(View view) {
        RequestParams rp = getAjaxParams();
        if (!"".equals(bean.getEaid())) {
            rp.put("eaid", bean.getEaid());
        }
        rp.put("gid", bean.getGid());
        ac.finalHttp.post(URL.PRODUCT_DETAIL,
                rp,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure() {
                        layout.setRefreshComplete();
                        showIntentErrorToast();
                    }

                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            int status = response.getInt(URL.STATUS);
                            switch (status) {
                                case 0:
                                    ProductBean beans = new Gson().fromJson(
                                            response.getString(URL.RESPONSE),
                                            new TypeToken<ProductBean>() {
                                            }.getType());
                                    if (beans != null) {
                                        bean = beans;
                                    }
                                    init();
                                    break;
                                default:
                                    showFailInfo(response);
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            layout.setRefreshComplete();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getSupportActionBar().setTitle(bean.getTitle());
    }

    void init() {
        if (adapter == null) {
            adapter = new ImageAdapter(bean.getImglist(), this);
            viewpager.setAdapter(adapter);
        } else {
            adapter.setList(bean.getImglist());
        }

        if (bean.getImglist().size() > 1) {
            gallerynavigator.setVisibility(View.VISIBLE);
        } else {
            gallerynavigator.setVisibility(View.GONE);
        }
        gallerynavigator.setSize(bean.getImglist().size());
        gallerynavigator.setPaints(Color.RED,
                getResources().getColor(R.color.darkgray));
        adapter.notifyDataSetChanged();

        title.setText(bean.getTitle());

        hide_ll.setVisibility(View.VISIBLE);

        Pattern p = Pattern.compile("\\d{1,}");
        Matcher m = p.matcher(bean.getDetail().toString());
        String code = "";
        while (m.find()) {
            code = m.group();
            break;
        }

        if (!"".equals(bean.getStatus())) {
            exchange_ll.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            sb.append("姓名 : " + bean.getTruename()).append("\n");
            sb.append("手机号码 : " + bean.getMobile()).append("\n");
            if (Util.isStrNotNull(bean.getEmail()))
                sb.append("邮箱 : " + bean.getEmail()).append("\n");
            sb.append("联系地址 : " + bean.getAddress()).append("\n");
            if (Util.isStrNotNull(bean.getExchremark()))
                sb.append("兑换备注 : " + bean.getExchremark());
            exchange_detail.setText(sb.toString());

            if (bean.getStatus().equals("0")) {
                buy_btn.setText("待处理");
            } else if (bean.getStatus().equals("1")) {
                buy_btn.setText("兑换成功");
            } else if (bean.getStatus().equals("2")) {
                buy_btn.setText("兑换失败");
            }
            buy_btn.setEnabled(false);
        }

        String str = bean.getDetail().replace(code, "<font color=\"#e71d1d\">" + code + "</font>");
        str = str.replace("\n", "<br/>");
        detail.setText(Html.fromHtml(str + "<br/>" + "备注 : " + bean.getRemark()));

        ViewPropertyAnimator.animate(buy_btn).translationY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                buy_btn.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void imgClick(View view) {
        ImageBrowserActivity_.intent(this).mProfile(bean.getImglist()).mPosition(position).start();
    }

    int position = 0;

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        position = arg0;
        gallerynavigator.setPosition(arg0);
        gallerynavigator.invalidate();
    }

    @Click
    void buy_btn() {
        User user = getHelper().getUserInfo();
        if (user == null) {
            startLogin();
            return;
        }
        if (user.getCoin() < bean.getGcoin()) {
            goCoin();
            return;
        }
        ProductExchangeActivity_.intent(this).bean(bean).start();
    }
}
