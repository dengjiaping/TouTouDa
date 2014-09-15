package com.quanliren.quan_two.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.util.StaticFactory;

import java.util.ArrayList;
import java.util.List;

public class MyCarePeopleAdapter extends ParentsAdapter{

	public MyCarePeopleAdapter(Context c, List list) {
		super(c, list);
	}

	public boolean isInvite=false;
	public List<String> enableUser=new ArrayList<String>();
	public List<String> checkedUser=new ArrayList<String>();
	
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=View.inflate(c, R.layout.care_people_item, null);
			holder.nickname=(TextView) convertView.findViewById(R.id.nickname);
			holder.userlogo=(ImageView) convertView.findViewById(R.id.userlogo);
			holder.sex=(TextView) convertView.findViewById(R.id.sex);
			holder.signature=(TextView) convertView.findViewById(R.id.signature);
			holder.check=(ImageButton)convertView.findViewById(R.id.check);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		User user=(User) list.get(position);
		holder.nickname.setText(user.getNickname());
		ImageLoader.getInstance().displayImage(user.getAvatar()+StaticFactory._160x160, holder.userlogo);
		if(user.getSex().equals("0")){
			holder.sex.setBackgroundResource(R.drawable.girl_icon);
		}else{
			holder.sex.setBackgroundResource(R.drawable.boy_icon);
		}
		holder.sex.setText(user.getUserAge());
		holder.signature.setText(user.getSignature());
		if(isInvite){
			holder.check.setVisibility(View.VISIBLE);
			if(enableUser.contains(user.getId())){
				holder.check.setEnabled(false);
			}else{
				holder.check.setEnabled(true);
				holder.check.setOnClickListener(checkClick);
				holder.check.setTag(user);
			}
		}
		return convertView;
	}

	public User getItem(int position){
		return (User) list.get(position-1);
	}
	
	class ViewHolder{
		ImageView userlogo;
		TextView nickname,signature,sex;
		ImageButton check;
	}
	
	OnClickListener checkClick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v.isEnabled()){
				User user=(User)v.getTag();
				if(v.isSelected()){
					v.setSelected(false);
					checkedUser.remove(user.getId());
				}else{
					v.setSelected(true);
					checkedUser.add(user.getId());
				}
			}
		}
	};
}
