package com.quanliren.quan_two.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ViewGroup;

import com.quanliren.quan_two.activity.PropertiesActivity.ITitle;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.user.SearchFriendActivity_;
import com.quanliren.quan_two.fragment.base.MenuFragmentBase;
import com.quanliren.quan_two.fragment.custom.PageTab;
import com.quanliren.quan_two.fragment.custom.PageTab.OnPageTitleClickListener;
import com.quanliren.quan_two.fragment.custom.PageTab_;
import com.quanliren.quan_two.fragment.impl.LoaderImpl;
import com.quanliren.quan_two.fragment.message.MyCareGragment;
import com.quanliren.quan_two.fragment.message.MyCareGragment_;
import com.quanliren.quan_two.fragment.message.MyLeaveMessageFragment_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.message_fragment)
@OptionsMenu(R.menu.nav_message_menu)
public class MessageFragment extends MenuFragmentBase implements OnPageTitleClickListener,ITitle{

	@ViewById ViewPager viewpager;
	List<Fragment> views=new ArrayList<Fragment>();
	ArrayList<String> string;
	PageTab pt;
	int positionb;
	
	@AfterViews
	void initView(){
		pt=new PageTab_();
		Bundle b=new Bundle();
		string=new ArrayList<String>();
		string.add("消息");
		string.add("好友");
		string.add("关注");
		string.add("粉丝");
		b.putStringArrayList("titles", string);
		pt.setArguments(b);
		pt.setListener(this);
		getChildFragmentManager().beginTransaction().replace(R.id.tab_content, pt).commit();
		
		
		views.add(new MyLeaveMessageFragment_());
		MyCareGragment friend=new MyCareGragment_();
		Bundle friendB=new Bundle();
		friendB.putInt("type", 1);
		friend.setArguments(friendB);
		views.add(friend);
		
		MyCareGragment care=new MyCareGragment_();
		Bundle careB=new Bundle();
		careB.putInt("type", 2);
		care.setArguments(careB);
		views.add(care);
		
		MyCareGragment carem=new MyCareGragment_();
		Bundle caremB=new Bundle();
		caremB.putInt("type", 3);
		carem.setArguments(caremB);
		views.add(carem);
		
		final MyOnPageChangeListener listener = new MyOnPageChangeListener();
		viewpager.setOnPageChangeListener(listener);
		viewpager.setAdapter(new mPagerAdapter(getChildFragmentManager()));
		viewpager.post(new Runnable() {
			@Override
			public void run() {
				listener.onPageSelected(0);
			}
		});
		getSupportActionBar().setTitle(string.get(0));
	}
	
	public class MyOnPageChangeListener implements OnPageChangeListener {

		public void onPageSelected(int position) {
			getSupportActionBar().setTitle(string.get(position));
			pt.setCurrendIndex(position);
			positionb=position;
		}

		public void onPageScrolled(int position, float bai, int x) {
			pt.onPageScroll(position, bai, x);
		}

		public void onPageScrollStateChanged(int arg0) {
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
			((LoaderImpl)views.get(position)).refresh();
			super.setPrimaryItem(container, position, object);
		}
	}

	@Override
	public void click(int index) {
		viewpager.setCurrentItem(index);
	};
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return string.get(positionb);
	}

	@OptionsItem
	void add(){
		SearchFriendActivity_.intent(this).start();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
}
