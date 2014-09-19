package com.quanliren.quan_two.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.user.UserInfoActivity_;
import com.quanliren.quan_two.activity.user.UserOtherInfoActivity_;
import com.quanliren.quan_two.bean.DongTaiBean;
import com.quanliren.quan_two.custom.UserNickNameRelativeLayout;
import com.quanliren.quan_two.util.ImageUtil;
import com.quanliren.quan_two.util.StaticFactory;
import com.quanliren.quan_two.util.Util;

import java.util.ArrayList;
import java.util.List;

public class QuanAdapter extends ParentsAdapter {

    IQuanAdapter listener;
    int imgWidth = 0;

    public QuanAdapter(Context c, List list, IQuanAdapter listener) {
        super(c, list);
        imgWidth = (c.getResources().getDisplayMetrics().widthPixels - ImageUtil.dip2px(c, 88)) / 3;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(c, R.layout.quan_item, null);
            holder.gridView = (GridView) convertView.findViewById(R.id.pic_gridview);

            holder.userlogo = (ImageView) convertView
                    .findViewById(R.id.userlogo);
            holder.username = (TextView) convertView
                    .findViewById(R.id.nickname);
            holder.sex = (TextView) convertView.findViewById(R.id.sex);
            holder.signature = (TextView) convertView
                    .findViewById(R.id.signature);
            holder.time = (TextView) convertView.findViewById(R.id.time);
//			holder.delete = convertView.findViewById(R.id.delete);
            holder.adapter = new QuanPicAdapter(c, new ArrayList<String>(), imgWidth);
            holder.gridView.setAdapter(holder.adapter);
            holder.lp = new LayoutParams(LayoutParams.WRAP_CONTENT, imgWidth);
            holder.lp.addRule(RelativeLayout.BELOW, R.id.signature);
            holder.location = (TextView) convertView.findViewById(R.id.location);
            holder.reply_btn = (TextView) convertView.findViewById(R.id.reply_btn);
            holder.gridView.setLayoutParams(holder.lp);
            holder.vip = convertView.findViewById(R.id.vip);
            holder.reply_ll = convertView.findViewById(R.id.reply_ll);
            holder.content_rl = convertView.findViewById(R.id.content_rl);
            holder.location_ll = convertView.findViewById(R.id.location_ll);
            holder.nick_ll = (UserNickNameRelativeLayout) convertView.findViewById(R.id.nickname_rl);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DongTaiBean db = (DongTaiBean) list.get(position);
        if (db.getImglist() == null || db.getImglist().size() == 0) {
            holder.gridView.setVisibility(View.GONE);
        } else {
            holder.gridView.setVisibility(View.VISIBLE);
            holder.adapter.setList(db.getImglist());
            holder.adapter.notifyDataSetChanged();
            holder.lp = (LayoutParams) holder.gridView.getLayoutParams();
            holder.lp.height = imgWidth * Util.getLines(db.getImglist().size(), 3);
            int num = (db.getImglist().size() > 3 ? 3 : db.getImglist().size());
            int lpwidth = ((num - 1) * ImageUtil.dip2px(c, 4)) + num * imgWidth;
            holder.lp.width = lpwidth;
            holder.gridView.setNumColumns(num);
            holder.gridView.setLayoutParams(holder.lp);
        }
        ImageLoader.getInstance().displayImage(
                db.getAvatar() + StaticFactory._160x160, holder.userlogo, ac.options_userlogo);
        holder.nick_ll.setUser(db.getNickname(), Integer.valueOf(db.getSex()), db.getAge(), db.getIsvip());
        holder.time.setText(Util.getTimeDateStr(db.getCtime()));
        if (db.getContent().trim().length() > 0) {
            holder.signature.setVisibility(View.VISIBLE);
            holder.signature.setText(db.getContent());
        } else {
            holder.signature.setVisibility(View.GONE);
        }

        holder.userlogo.setTag(position);
        holder.userlogo.setOnClickListener(userlogo);
        if (Util.isStrNotNull(db.getArea())) {
            holder.location_ll.setVisibility(View.VISIBLE);
            holder.location.setText(db.getArea());
        } else {
            holder.location_ll.setVisibility(View.GONE);
        }
        holder.reply_btn.setText(db.getCnum());
        holder.content_rl.setTag(db);
        holder.content_rl.setOnClickListener(detailClick);
        if (Integer.valueOf(db.getCnum()) <= 0) {
            holder.reply_ll.setVisibility(View.GONE);
        } else {
            holder.reply_ll.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView userlogo;
        GridView gridView;
        QuanPicAdapter adapter;
        TextView username, sex, time, signature, reply_btn, location;
        LayoutParams lp;
        View content_rl;
        View vip, reply_ll, location_ll;
        UserNickNameRelativeLayout nick_ll;
    }


    OnClickListener detailClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            listener.detailClick((DongTaiBean) v.getTag());
        }
    };


    OnClickListener userlogo = new OnClickListener() {

        @Override
        public void onClick(View v) {
            DongTaiBean db = (DongTaiBean) list.get(Integer.valueOf(v.getTag()
                    .toString()));
            Intent i = new Intent(c, db.getUserid()
                    .equals(ac.getLoginUserId()) ? UserInfoActivity_.class
                    : UserOtherInfoActivity_.class);
            i.putExtra("userId", db.getUserid());
            c.startActivity(i);
        }
    };

    public interface IQuanAdapter {
        public void detailClick(Object bean);
    }
}
