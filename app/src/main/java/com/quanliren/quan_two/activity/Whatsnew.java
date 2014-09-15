package com.quanliren.quan_two.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.a.loopj.android.http.JsonHttpResponseHandler;
import com.a.loopj.android.http.RequestParams;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.activity.whats.WahtsFragment1_;
import com.quanliren.quan_two.fragment.impl.LoaderImpl;
import com.quanliren.quan_two.share.CommonShared;
import com.quanliren.quan_two.util.URL;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;

@EActivity
public class Whatsnew extends BaseActivity {

    ArrayList<Fragment> views = new ArrayList<Fragment>();

    @ViewById
    ViewPager whatsnew_viewpager;
    @ViewById
    ImageView page0;
    @ViewById
    ImageView page1;
    @ViewById
    ImageView page2;
    @ViewById
    ImageView page3;
    @ViewById
    Button enter_btn;
    @ViewById
    TextView text;
    @ViewById
    TextView con;
    @ViewById
    View pages;
    @ViewById
    View bg;

    String[] str = new String[]{"查看附近帅哥美女的约会状态", "各种有趣的约会", "内容丰富的个人页面",
            "用靓点来兑换心仪的礼物"};
    String[] strMin = new String[]{"来一场浪漫完美的约会", "按照你的要求筛选", "你与别人就此不同",
            "礼物等你来拿"};

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setSwipeBackEnable(false);

        createShorcut(R.drawable.icon);

        /** 统计 **/
        String firstsend = ac.cs.getIsFirstSend();
        if ("".equals(firstsend)) {
            RequestParams ap = new RequestParams();
            ap.put("appname", ac.cs.getVersionName());
            ap.put("appcode", ac.cs.getVersionCode() + "");
            ap.put("channelname", ac.cs.getChannel());
            ap.put("deviceid", ac.cs.getDeviceId());
            ap.put("devicetype", "0");
            ap.put("oscode", android.os.Build.VERSION.SDK);
            ap.put("osversion", android.os.Build.VERSION.RELEASE);
            ap.put("model", android.os.Build.MODEL);

            ac.finalHttp.post(URL.TONGJI, ap, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject jo) {
                    try {

                        int status = jo.getInt(URL.STATUS);
                        switch (status) {
                            case 0:
                                ac.cs.setIsFirstSend("1");
                                break;
                        }
                    } catch (Exception e) {
                    }
                }

            });
        }

        doSomeThing();
    }

    @UiThread
    public void doSomeThing() {
        String isFirstStart = ac.cs.getIsFirstStart();
        if ("".equals(isFirstStart)) {

            setContentView(R.layout.whatsnew_viewpager);

        	bg.setVisibility(View.VISIBLE);
            pages.setVisibility(View.VISIBLE);
            text.setText(str[0]);
            con.setText(strMin[0]);
            whatsnew_viewpager
                    .setOnPageChangeListener(new MyOnPageChangeListener());
            try {
                views.add(WahtsFragment1_.builder().have(true)
                        .res(R.drawable.welcome_1).build());
                views.add(WahtsFragment1_.builder().have(false)
                        .res(R.drawable.welcome_2).build());
                views.add(WahtsFragment1_.builder().have(false)
                        .res(R.drawable.welcome_3).build());
                views.add(WahtsFragment1_.builder().have(false)
                        .res(R.drawable.welcome_4).build());
            } catch (Exception e) {
                e.printStackTrace();
            }

            whatsnew_viewpager.setAdapter(new mPagerAdapter(
                    getSupportFragmentManager()));
        } else {
            Intent intent = new Intent(Whatsnew.this, PropertiesActivity_.class);
            startActivity(intent);
            ac.cs.setIsFirstStart("1");
            finish();
        }
    }

    class mPagerAdapter extends FragmentPagerAdapter {

        public mPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return views.get(arg0);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                                   Object object) {
            ((LoaderImpl) views.get(position)).refresh();
            super.setPrimaryItem(container, position, object);
        }
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {

        public void onPageSelected(int arg0) {
            switch (arg0) {
                case 0:
                    page0.setImageResource(R.drawable.page_now);
                    page1.setImageResource(R.drawable.page);
                    break;
                case 1:
                    page1.setImageResource(R.drawable.page_now);
                    page0.setImageResource(R.drawable.page);
                    page2.setImageResource(R.drawable.page);
                    break;
                case 2:
                    page2.setImageResource(R.drawable.page_now);
                    page1.setImageResource(R.drawable.page);
                    page3.setImageResource(R.drawable.page);
                    break;
                case 3:
                    page3.setImageResource(R.drawable.page_now);
                    page2.setImageResource(R.drawable.page);
                    break;
            }
            if (arg0 == 3) {
                enter_btn.setVisibility(View.VISIBLE);
            } else {
                enter_btn.setVisibility(View.GONE);
            }
            text.setText(str[arg0]);
            con.setText(strMin[arg0]);
        }

        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    @Click(R.id.enter_btn)
    public void startbutton(View v) {
        Intent intent = new Intent(Whatsnew.this, PropertiesActivity_.class);
        startActivity(intent);
        ac.cs.setIsFirstStart("1");
        finish();
    }

    private void createShorcut(int id) {
        if (ac.cs.getFastStartIcon() == CommonShared.OPEN) {
            return;
        } else {
            ac.cs.setFastStartIcon(CommonShared.OPEN);
        }
        Intent shortcutintent = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        shortcutintent.putExtra("duplicate", false);
        // 需要现实的名称
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                getString(R.string.app_name));
        // 快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(
                getApplicationContext(), id);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        // 点击快捷图片，运行的程序主入口

        // 点击快捷方式的操作
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(this, Whatsnew_.class);

        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        // 发送广播。OK
        sendBroadcast(shortcutintent);
    }
}
