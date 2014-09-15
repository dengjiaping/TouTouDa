package com.quanliren.quan_two.activity.group;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ViewGroup;

import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.activity.group.quan.QuanAboutMeFragment_;
import com.quanliren.quan_two.activity.group.quan.QuanPullListViewFragment;
import com.quanliren.quan_two.activity.group.quan.QuanPullListViewFragment_;
import com.quanliren.quan_two.custom.TabLinearLayout;
import com.quanliren.quan_two.custom.TabLinearLayout.OnTabClickListener;
import com.quanliren.quan_two.fragment.impl.LoaderImpl;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.quan)
public class QuanActivity extends BaseActivity implements OnTabClickListener {

	@ViewById
	TabLinearLayout bottom_tab;
	@ViewById
	ViewPager viewpager;
	List<Fragment> views = new ArrayList<Fragment>();

	@AfterViews
	void initView() {

		List<TabLinearLayout.TabBean> list = new ArrayList<TabLinearLayout.TabBean>();
		list.add(new TabLinearLayout.TabBean(R.drawable.ic_date_tab2, "留言板"));
		list.add(new TabLinearLayout.TabBean(R.drawable.ic_dongtai_tab1, "好友动态"));
		list.add(new TabLinearLayout.TabBean(R.drawable.ic_dongtai_tab2, "评价我的"));
		bottom_tab.setDate(list);
		bottom_tab.setListener(this);

		getSupportActionBar().setTitle(R.string.quan);

		QuanPullListViewFragment_ pull = new QuanPullListViewFragment_();
		Bundle b = new Bundle();
		b.putInt("type", QuanPullListViewFragment.ALL);
		pull.setArguments(b);
		views.add(pull);
		
		QuanPullListViewFragment_ pull1 = new QuanPullListViewFragment_();
		Bundle b1 = new Bundle();
		b1.putInt("type", QuanPullListViewFragment.MYCARE);
		pull1.setArguments(b1);
		views.add(pull1);
		
		QuanAboutMeFragment_ pull2=new QuanAboutMeFragment_();
		views.add(pull2);
		
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
	public void onTabClick(int position) {
		if(position>0){
			if(getHelper().getUser()==null){
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
			((LoaderImpl)views.get(position)).refresh();
			super.setPrimaryItem(container, position, object);
		}
	}
}
