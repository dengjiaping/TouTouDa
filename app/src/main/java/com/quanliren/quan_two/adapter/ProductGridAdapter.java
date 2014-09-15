package com.quanliren.quan_two.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.bean.ProductBean;
import com.quanliren.quan_two.util.StaticFactory;

import java.util.List;

public class ProductGridAdapter extends ParentsAdapter{
	IProductGridListener listener;
	public ProductGridAdapter(Context c, List list) {
		super(c, list);
		try {
			listener=(IProductGridListener)c;
		} catch (Exception e) {
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=View.inflate(c, R.layout.product_grid_item, null);
			holder.title=(TextView) convertView.findViewById(R.id.title);
			holder.img=(ImageView) convertView.findViewById(R.id.img);
			holder.click_ll=convertView.findViewById(R.id.click_ll);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		
		ProductBean bean=(ProductBean) getItem(position);
		holder.title.setText(bean.getTitle());
		ImageLoader.getInstance().displayImage(bean.getImgurl()+StaticFactory._160x160, holder.img);
		holder.click_ll.setTag(bean);
		holder.click_ll.setOnClickListener(imgClick);
		return convertView;
	}
	
	class ViewHolder{
		TextView title;
		View click_ll;
		ImageView img;
	}

	OnClickListener imgClick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(listener!=null){
				listener.imgClick(v);
			}
		}
	};
	
	public interface IProductGridListener{
		void imgClick(View view);	}
}
