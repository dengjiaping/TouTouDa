package com.quanliren.quan_two.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.util.StaticFactory;
import com.quanliren.quan_two.util.Util;

import java.util.List;

public class BlackPeopleAdapter extends ParentsAdapter{

	public BlackPeopleAdapter(Context c, List list) {
		super(c, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=View.inflate(c, R.layout.near_people_item, null);
			holder.nickname=(TextView) convertView.findViewById(R.id.nickname);
			holder.userlogo=(ImageView) convertView.findViewById(R.id.userlogo);
			holder.sex=(TextView) convertView.findViewById(R.id.sex);
			holder.signature=(TextView) convertView.findViewById(R.id.signature);
			holder.time=(TextView)convertView.findViewById(R.id.time);
			holder.vip=convertView.findViewById(R.id.vip);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		User user=(User) list.get(position);
		holder.nickname.setText(user.getNickname());
		ImageLoader.getInstance().displayImage(user.getAvatar()+StaticFactory._160x160, holder.userlogo,ac.options_userlogo);
		if(user.getSex().equals("0")){
			holder.sex.setBackgroundResource(R.drawable.girl_icon);
		}else{
			holder.sex.setBackgroundResource(R.drawable.boy_icon);
		}
		holder.sex.setText(user.getUserAge());
		holder.signature.setText(user.getSignature());
		if(user.getCtime()!=null)
			holder.time.setText("于"+Util.getTimeDateStr(user.getCtime())+"拉黑");
		else
			holder.time.setText("");
		holder.time.setCompoundDrawables(null, null, null, null);
		if(user.getIsvip()==1){
			holder.vip.setVisibility(View.VISIBLE);
			holder.nickname.setTextColor(c.getResources().getColor(R.color.vip_name));
		}else{
			holder.vip.setVisibility(View.GONE);
			holder.nickname.setTextColor(c.getResources().getColor(R.color.username));
		}
		return convertView;
	}

	
	class ViewHolder{
		ImageView userlogo;
		TextView nickname,signature,sex,time;
		View vip;
	}
}
