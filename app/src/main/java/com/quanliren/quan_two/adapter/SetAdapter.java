package com.quanliren.quan_two.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.bean.SetBean;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class SetAdapter extends ParentsAdapter {

	public SetAdapter(Context c, List list) {
		super(c, list);
	}

	@Override
	public int getItemViewType(int position) {
		SetBean sb = (SetBean) list.get(position);
		return sb.isFirst;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			
			switch (getItemViewType(position)) {
			case 0:
				convertView = View.inflate(c, R.layout.seting_item_top, null);
				break;
			case 1:
				convertView = View.inflate(c, R.layout.seting_item_mid, null);
				break;
			default:
				convertView = View.inflate(c, R.layout.seting_item_btm, null);
				break;
			}
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SetBean sb = (SetBean) list.get(position);
		holder.icon.setImageResource(sb.icon);
		holder.text.setText(sb.title);
		
		if(sb.title.equals("清除缓存")){
			holder.caret.setVisibility(View.GONE);
			holder.source.setVisibility(View.VISIBLE);
			holder.source.setText(sb.getSource());
		}else{
			try {
				holder.caret.setVisibility(View.VISIBLE);
				holder.source.setVisibility(View.GONE);
			} catch (Exception e) {
			}
		}
		if(holder.setcount!=null){
			if(sb.count>0){
				holder.setcount.setVisibility(View.VISIBLE);
			}else{
				holder.setcount.setVisibility(View.GONE);
			}
		}
		return convertView;
	}

	class ViewHolder {
		@InjectView(R.id.icon)
		ImageView icon;
		@InjectView(R.id.text)
		TextView text;
		@Optional @InjectView(R.id.source)
		TextView source;
		@Optional @InjectView(R.id.caret)
		View caret;
		@Optional @InjectView(R.id.setcount)
		View setcount;
		
		public ViewHolder(View view){
			ButterKnife.inject(this, view);
		}
		
	}
}
