package com.quanliren.quan_two.activity.group;

import android.os.Bundle;

import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.fragment.ChosePositionFragment_;

import org.androidannotations.annotations.EActivity;

@EActivity
public class ChoseLocationActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chose_position_actvitiy);
		getSupportActionBar().setTitle("选择城市");
		getSupportFragmentManager().beginTransaction().replace(R.id.content, new ChosePositionFragment_()).commit();
	}
}
