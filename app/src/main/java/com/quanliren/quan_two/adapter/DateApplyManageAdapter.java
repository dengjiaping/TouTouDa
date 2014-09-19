package com.quanliren.quan_two.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a.dd.CircularProgressButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.custom.CustomVip;
import com.quanliren.quan_two.util.StaticFactory;
import com.quanliren.quan_two.util.Util;

import java.util.List;

public class DateApplyManageAdapter extends ParentsAdapter {

    public User selectUser = null;
    public boolean isFinish = false;

    public DateApplyManageAdapter(Context c, List list, IDateAdapterListener listener) {
        super(c, list);
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(c, R.layout.date_people_item, null);
            holder.nickname = (TextView) convertView
                    .findViewById(R.id.nickname);
            holder.userlogo = (ImageView) convertView
                    .findViewById(R.id.userlogo);
            holder.sex = (TextView) convertView.findViewById(R.id.sex);
            holder.signature = (TextView) convertView
                    .findViewById(R.id.signature);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.vip = (CustomVip) convertView.findViewById(R.id.vip);
            holder.agree = (CircularProgressButton) convertView
                    .findViewById(R.id.agree);
            holder.agree.setIndeterminateProgressMode(true);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        User user = (User) list.get(position);
        holder.nickname.setText(user.getNickname());
        ImageLoader.getInstance().displayImage(
                user.getAvatar() + StaticFactory._320x320, holder.userlogo,
                ac.options_userlogo);
        if (user.getSex().equals("0")) {
            holder.sex.setBackgroundResource(R.drawable.girl_icon);
            holder.agree.setmIdleText("和她约");
            if (holder.agree.getProgress() == 0)
                holder.agree.setText("和她约");
        } else {
            holder.sex.setBackgroundResource(R.drawable.boy_icon);
            holder.agree.setmIdleText("和他约");
            if (holder.agree.getProgress() == 0)
                holder.agree.setText("和他约");
        }
        holder.sex.setText(user.getUserAge());
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
        if (user.getIsvip() > 0) {
            holder.vip.setVisibility(View.VISIBLE);
            holder.vip.setVip(user.getIsvip());
            holder.nickname.setTextColor(c.getResources().getColor(
                    R.color.vip_name));
        } else {
            holder.vip.setVisibility(View.GONE);
            holder.nickname.setTextColor(c.getResources().getColor(
                    R.color.username));
        }
        holder.agree.setTag(user);
        holder.agree.setOnClickListener(agreeClick);

//		if(canRefere){
        if (selectUser != null) {
            if (selectUser.getId().equals(user.getId())) {
                holder.agree.setProgress(50);
            } else {
                holder.agree.setProgress(-2);
            }
        } else {
            if (user.getApplystate() == 1) {
                holder.agree.setProgress(100);
                holder.agree.setEnabled(false);
            } else {
                if (isFinish) {
                    holder.agree.setProgress(-2);
                } else {
                    holder.agree.setProgress(0);
                }
            }
        }
//		}
        return convertView;
    }

    class ViewHolder {
        ImageView userlogo;
        TextView nickname, signature, sex, time;
        CircularProgressButton agree;
        CustomVip vip;
    }

    OnClickListener agreeClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            listener.agreeClick(v);
        }
    };
    IDateAdapterListener listener;

    public interface IDateAdapterListener {
        void agreeClick(View v);
    }
}