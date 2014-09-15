package com.quanliren.quan_two.fragment;

import android.view.View;

import com.quanliren.quan_two.activity.PropertiesActivity.ITitle;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.group.QuanActivity_;
import com.quanliren.quan_two.activity.group.ThroughActivity_;
import com.quanliren.quan_two.activity.group.date.DateListActivity_;
import com.quanliren.quan_two.activity.shop.ShopVipDetail_;
import com.quanliren.quan_two.activity.shop.product.ProductListActivity_;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.fragment.base.MenuFragmentBase;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.ground)
public class GroundFragment extends MenuFragmentBase implements ITitle{

	@ViewById
	View ground_msg;
	@ViewById
	View ground_date;
	@ViewById
	View ground_exchange;
	@ViewById
	View ground_shop;
	@ViewById
	View ground_through;
	
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "广场";
	}

	@Click
	void ground_msg(){
		QuanActivity_.intent(this).start();
	}
	
	@Click
	void ground_shop(){
		ShopVipDetail_.intent(this).start();
	}
	
	@Click
	void ground_through(){
		User user=getHelper().getUserInfo();
		if(user==null){
			startLogin();
			return;
		}
		if(user.getIsvip()==0){
			goVip();
			return;
		}
		ThroughActivity_.intent(this).start();
	}
	
	@Click
	void ground_date(){
		DateListActivity_.intent(this).start();
	}
	
	@Click
	void ground_exchange(){
		ProductListActivity_.intent(this).start();
	}
}
