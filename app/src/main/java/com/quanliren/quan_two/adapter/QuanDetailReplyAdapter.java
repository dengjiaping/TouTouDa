package com.quanliren.quan_two.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.bean.DongTaiReplyBean;
import com.quanliren.quan_two.util.StaticFactory;
import com.quanliren.quan_two.util.Util;

import java.util.List;

public class QuanDetailReplyAdapter extends ParentsAdapter {
	IQuanDetailReplyAdapter listener;

	public QuanDetailReplyAdapter(Context c, List list,
			IQuanDetailReplyAdapter listener) {
		super(c, list);
		this.listener = listener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(c, R.layout.quan_reply_item, null);
			holder = new ViewHolder();
			holder.username = (TextView) convertView
					.findViewById(R.id.nickname);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.content_rl=convertView.findViewById(R.id.content_rl);
			holder.userlogo = (ImageView) convertView
					.findViewById(R.id.userlogo);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		DongTaiReplyBean bean = (DongTaiReplyBean) list.get(position);
		holder.username.setText(bean.getNickname());
		if(bean.getReplyuid()!=null&&!bean.getReplyuid().equals("")&&!bean.getReplyuid().equals("-1")){
			holder.content.setText("回复 "+bean.getReplyuname()+" : "+bean.getContent());
		}else{
			holder.content.setText(bean.getContent());
		}
		holder.content_rl.setTag(bean);
		holder.content_rl.setOnClickListener(viewClick);
		holder.userlogo.setTag(bean);
		holder.userlogo.setOnClickListener(viewClick);
		holder.time.setText(Util.getTimeDateStr(bean.getCtime()));
		ImageLoader.getInstance().displayImage(
				bean.getAvatar() + StaticFactory._160x160, holder.userlogo,ac.options_userlogo);
		return convertView;
	}

	class ViewHolder {
		TextView username, time, content;
		View content_rl;
		ImageView userlogo;
	}

	public interface IQuanDetailReplyAdapter {
		void contentClick(DongTaiReplyBean bean);

		void logoCick(DongTaiReplyBean bean);
	}

	OnClickListener viewClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.userlogo:
				listener.logoCick((DongTaiReplyBean)v.getTag());
				break;
			case R.id.content_rl:
				listener.contentClick((DongTaiReplyBean)v.getTag());
				break;
			}
		}
	};
}
