package com.quanliren.quan_two.adapter.shop;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.adapter.ParentsAdapter;

import java.util.List;

public class ExchangeGridAdapter extends ParentsAdapter {

	public ExchangeGridAdapter(Context c, List list) {
		super(c, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View
					.inflate(c, R.layout.exchange_fragment1_grid_item, null);
			holder.logo = (ImageView)convertView.findViewById(R.id.logo);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.desc = (TextView) convertView.findViewById(R.id.desc);
			holder.submit = (Button) convertView.findViewById(R.id.submit);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		return convertView;
	}

	class ViewHolder{
		ImageView logo;
		TextView title,desc;
		Button submit;
	} 
}
