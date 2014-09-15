package com.quanliren.quan_two.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.custom.StateTextView;
import com.quanliren.quan_two.custom.UserNickNameRelativeLayout;
import com.quanliren.quan_two.util.StaticFactory;
import com.quanliren.quan_two.util.Util;

import java.util.List;

public class NearPeopleAdapter extends ParentsAdapter {

	public NearPeopleAdapter(Context c, List list) {
		super(c, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(c, R.layout.near_people_item, null);
			holder.nickname = (TextView) convertView
					.findViewById(R.id.nickname);
			holder.userlogo = (ImageView) convertView
					.findViewById(R.id.userlogo);
			holder.sex = (TextView) convertView.findViewById(R.id.sex);
			holder.signature = (TextView) convertView
					.findViewById(R.id.signature);
			holder.state = (StateTextView) convertView.findViewById(R.id.state);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.vip = convertView.findViewById(R.id.vip);
			holder.nick_ll=(UserNickNameRelativeLayout) convertView.findViewById(R.id.nick_ll);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		User user = (User) list.get(position);
		holder.nick_ll.setUser(user);
		ImageLoader.getInstance().displayImage(
				user.getAvatar() + StaticFactory._320x320, holder.userlogo,
				ac.options_userlogo);
		holder.signature.setText(user.getSignature());
		if (user.getDistance() != null && !"".equals(user.getDistance())) {
			holder.time.setText(Util.getDistance(user.getDistance()));
			holder.time.setText(holder.time.getText().toString() + " | "
					+ Util.getTimeDateStr(user.getActivetime()));
		} else if (user.getLatitude() != 0 && user.getLongitude() != 0
				&& !ac.cs.getLat().equals("")) {
			holder.time.setText(Util.getDistance(
					Double.valueOf(ac.cs.getLng()),
					Double.valueOf(ac.cs.getLat()), user.getLongitude(),
					user.getLatitude())
					+ "km");
			holder.time.setText(holder.time.getText().toString() + " | "
					+ Util.getTimeDateStr(user.getActivetime()));
		} else {
			holder.time.setText("");
		}
		if (user.getUserstate() == 0) {
			holder.state.setVisibility(View.GONE);
		} else {
			holder.state.setVisibility(View.VISIBLE);
			holder.state.setState(user.getUserstate());
		}
		return convertView;
	}

	class ViewHolder {
		ImageView userlogo;
		TextView nickname, signature, sex, time;
		StateTextView state;
		View vip;
		UserNickNameRelativeLayout nick_ll;
	}
}
