package com.quanliren.quan_two.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.bean.User;

public class UserNickNameRelativeLayout extends RelativeLayout{

	private TextView nickname,sex;
	private CustomVip vip;
	
	public UserNickNameRelativeLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public UserNickNameRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		View view=View.inflate(context, R.layout.user_nick_name, null);
		nickname=(TextView) view.findViewById(R.id.nickname);
		sex=(TextView) view.findViewById(R.id.sex);
		vip=(CustomVip) view.findViewById(R.id.vip);
		addView(view);
	}

	public UserNickNameRelativeLayout(Context context) {
		super(context);
	}
	public void setUser(User user){
		setUser(user.getNickname(), Integer.valueOf(user.getSex()), user.getUserAge(), user.getIsvip());
	}
	public void setUser(String nickname,int sex,String age,int vip){
		this.nickname.setText(nickname);
		switch (sex) {
		case 0:
			this.sex.setBackgroundResource(R.drawable.girl_icon);
			break;
		case 1:
			this.sex.setBackgroundResource(R.drawable.boy_icon);
			break;
		default:
			break;
		}
		this.sex.setText(age);
		this.vip.setVip(vip);
		if (vip > 0) {
			this.nickname.setTextColor(getResources().getColor(
					R.color.vip_name));
		} else {
			this.nickname.setTextColor(getResources().getColor(
					R.color.username));
		}
	}

}
