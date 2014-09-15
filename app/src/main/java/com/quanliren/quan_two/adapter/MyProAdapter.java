package com.quanliren.quan_two.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.bean.ProductBean;
import com.quanliren.quan_two.util.StaticFactory;

import java.util.List;

public class MyProAdapter extends ParentsAdapter{

	public MyProAdapter(Context c, List list) {
		super(c, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=View.inflate(c, R.layout.my_pro_item, null);
			holder.img=(ImageView) convertView.findViewById(R.id.img);
			holder.title=(TextView) convertView.findViewById(R.id.title);
			holder.state=(TextView)convertView.findViewById(R.id.state);
			holder.state_color=convertView.findViewById(R.id.state_color);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		ProductBean pb=(ProductBean) list.get(position);
		ImageLoader.getInstance().displayImage(pb.getImgurl()+StaticFactory._160x160, holder.img);
		holder.title.setText(Html.fromHtml("恭喜您已兑换<font color=\"#e71d1d\">"+pb.getTitle()+"</font>"));
		if (pb.getStatus().equals("0")) {
			holder.state.setText("待处理");
			holder.state_color.setBackgroundResource(R.color.exchange_orange);
		}else if(pb.getStatus().equals("1")){
			holder.state.setText("兑换成功");
			holder.state_color.setBackgroundResource(R.color.exchange_green);
		}else if(pb.getStatus().equals("2")){
			holder.state.setText("兑换失败");
			holder.state_color.setBackgroundResource(R.color.exchange_gray);
		}
		return convertView;
	}

	class ViewHolder{
		ImageView img;
		View state_color;
		TextView title,state;
	} 
}
