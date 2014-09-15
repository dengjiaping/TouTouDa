package com.quanliren.quan_two.activity.user;

import android.os.Bundle;
import android.os.Handler;

import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.fragment.message.MyCareGragment;

import org.androidannotations.annotations.EActivity;

@EActivity
public class MyCareActivity extends BaseActivity{

	public static final String TAG="MyCareActivity";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_care_activity);
		final MyCareGragment friend=new MyCareGragment();
		Bundle friendB=new Bundle();
		friendB.putInt("type", 1);
		friend.setArguments(friendB);
		getSupportActionBar().setTitle("我的好友");
		getSupportFragmentManager().beginTransaction().replace(R.id.content, friend).commit();
		new Handler().post(new Runnable() {
			
			@Override
			public void run() {
				friend.refresh();
			}
		});
	}

}
