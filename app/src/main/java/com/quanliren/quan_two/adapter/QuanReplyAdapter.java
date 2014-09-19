package com.quanliren.quan_two.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.adapter.QuanDetailReplyAdapter.IQuanDetailReplyAdapter;
import com.quanliren.quan_two.bean.DongTaiReplyBean;
import com.quanliren.quan_two.util.StaticFactory;
import com.quanliren.quan_two.util.Util;

import java.util.List;

public class QuanReplyAdapter extends ParentsAdapter {
    IQuanDetailReplyAdapter listener;

    public QuanReplyAdapter(Context c, List list,
                            IQuanDetailReplyAdapter listener) {
        super(c, list);
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(c, R.layout.quan_reply_my, null);
            holder = new ViewHolder();
            holder.username = (TextView) convertView
                    .findViewById(R.id.nickname);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.userlogo = (ImageView) convertView
                    .findViewById(R.id.userlogo);
            holder.vip = convertView.findViewById(R.id.vip);
            holder.sex = (TextView) convertView.findViewById(R.id.sex);
            holder.signature = (TextView) convertView
                    .findViewById(R.id.signature);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DongTaiReplyBean bean = (DongTaiReplyBean) list.get(position);
        holder.username.setText(bean.getNickname());
        holder.userlogo.setTag(bean);
        holder.userlogo.setOnClickListener(viewClick);
        holder.time.setText(Util.getTimeDateStr(bean.getCtime()));
        if (bean.getReplyuid() != null && !bean.getReplyuid().equals("") && !bean.getReplyuid().equals("-1")) {
            holder.signature.setText("回复 " + bean.getReplyuname() + " : " + bean.getContent());
        } else {
            holder.signature.setText(bean.getContent());
        }
        ImageLoader.getInstance().displayImage(
                bean.getAvatar() + StaticFactory._160x160, holder.userlogo,
                ac.options_userlogo);
        switch (Integer.valueOf(bean.getSex())) {
            case 0:
                holder.sex.setBackgroundResource(R.drawable.girl_icon);
                break;
            case 1:
                holder.sex.setBackgroundResource(R.drawable.boy_icon);
                break;
            default:
                break;
        }
        holder.sex.setText(bean.getAge() + "");
        if (bean.getIsvip() == 1) {
            holder.vip.setVisibility(View.VISIBLE);
            holder.username.setTextColor(c.getResources().getColor(
                    R.color.vip_name));
        } else {
            holder.vip.setVisibility(View.GONE);
            holder.username.setTextColor(c.getResources().getColor(
                    R.color.username));
        }
        holder.signature.setTag(bean);
        holder.signature.setOnClickListener(viewClick);
        return convertView;
    }

    class ViewHolder {
        ImageView userlogo;
        TextView username, sex, time, signature;
        View vip;
    }

    OnClickListener viewClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.userlogo:
                    listener.logoCick((DongTaiReplyBean) v.getTag());
                    break;
                case R.id.signature:
                    listener.contentClick((DongTaiReplyBean) v.getTag());
                    break;
            }
        }
    };
}
