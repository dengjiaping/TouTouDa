package com.quanliren.quan_two.activity.group.date;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ViewGroup;

import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.custom.TabLinearLayout;
import com.quanliren.quan_two.custom.TabLinearLayout.OnTabClickListener;
import com.quanliren.quan_two.fragment.impl.LoaderImpl;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.date_nav)
public class DateListActivity extends BaseActivity implements OnTabClickListener {

    @ViewById
    TabLinearLayout bottom_tab;
    @ViewById
    ViewPager viewpager;
    List<Fragment> views = new ArrayList<Fragment>();

    @AfterViews
    void initView() {
        List<TabLinearLayout.TabBean> list = new ArrayList<TabLinearLayout.TabBean>();
        list.add(new TabLinearLayout.TabBean(R.drawable.ic_date_tab1, "偷偷约"));
        list.add(new TabLinearLayout.TabBean(R.drawable.ic_date_tab2, "我发布的"));
        list.add(new TabLinearLayout.TabBean(R.drawable.ic_date_tab3, "我报名的"));
        bottom_tab.setDate(list);
        bottom_tab.setListener(this);

        views.add(new DateListFragment_());
        views.add(new MyDateListFragment_());
        views.add(new MyApplyDateListFragment_());

        viewpager.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageSelected(int arg0) {
                bottom_tab.setCurrentIndex(arg0);
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
            }
        });
        viewpager.setAdapter(new mPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getSupportActionBar().setTitle("偷偷约");
    }

    @Override
    public void onTabClick(int position) {
        if (position > 0) {
            if (getHelper().getUser() == null) {
                startLogin();
                return;
            }
        }
        viewpager.setCurrentItem(position);
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
}
