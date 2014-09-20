package com.quanliren.quan_two.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.user.*;
import com.quanliren.quan_two.adapter.QuanAdapter.IQuanAdapter;
import com.quanliren.quan_two.bean.DateBean;
import com.quanliren.quan_two.custom.CustomVip;
import com.quanliren.quan_two.custom.StateTextViewBg;
import com.quanliren.quan_two.util.StaticFactory;
import com.quanliren.quan_two.util.Util;

import java.util.List;

public class DateAdapter extends ParentsAdapter {
    IQuanAdapter listener;
    IDateAdapterListener dlistener;

    public void setDlistener(IDateAdapterListener dlistener) {
        this.dlistener = dlistener;
    }

    public DateAdapter(Context c, List list, IQuanAdapter listener) {
        super(c, list);
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(c, R.layout.date_item, null);
            holder.userlogo = (ImageView) convertView
                    .findViewById(R.id.userlogo);
            holder.nickname = (TextView) convertView
                    .findViewById(R.id.nickname);
            holder.sex = (TextView) convertView.findViewById(R.id.sex);
            holder.location_tv = (TextView) convertView
                    .findViewById(R.id.location_tv);
            holder.coin = (TextView) convertView.findViewById(R.id.coin_tv);
            holder.place_tv = (TextView) convertView
                    .findViewById(R.id.place_tv);
            holder.people_num_tv = (TextView) convertView
                    .findViewById(R.id.people_num_tv);
            holder.sex_tv = (TextView) convertView.findViewById(R.id.sex_tv);
            holder.money_tv = (TextView) convertView
                    .findViewById(R.id.money_tv);
            holder.time_tv = (TextView) convertView.findViewById(R.id.time_tv);
            holder.remark_tv = (TextView) convertView
                    .findViewById(R.id.remark_tv);
            holder.aim_tv = (TextView) convertView.findViewById(R.id.aim_tv);
            holder.bm_people_num = (TextView) convertView
                    .findViewById(R.id.bm_people_num);
            holder.reply_tv = (TextView) convertView
                    .findViewById(R.id.reply_tv);
            holder.state = (StateTextViewBg) convertView
                    .findViewById(R.id.state);
            holder.vip = (CustomVip) convertView.findViewById(R.id.vip);
            holder.people_num_ll = convertView.findViewById(R.id.people_num_ll);
            holder.aim_ll = convertView.findViewById(R.id.aim_ll);
            holder.reply_ll = convertView.findViewById(R.id.reply_ll);
            holder.content_rl = convertView.findViewById(R.id.content_rl);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DateBean db = (DateBean) getItem(position);
        ImageLoader.getInstance().displayImage(
                db.getAvatar() + StaticFactory._160x160, holder.userlogo);
        holder.nickname.setText(db.getNickname());
        switch (Integer.valueOf(db.getSex())) {
            case 0:
                holder.sex.setBackgroundResource(R.drawable.girl_icon);
                break;
            case 1:
                holder.sex.setBackgroundResource(R.drawable.boy_icon);
                break;
            default:
                break;
        }
        holder.sex.setText(db.getAge());
        holder.userlogo.setTag(position);
        holder.userlogo.setOnClickListener(userlogo);
        holder.state.setState(db.getDtype());

        if (db.getIsvip() > 0) {
            holder.vip.setVisibility(View.VISIBLE);
            holder.vip.setVip(db.getIsvip());
            holder.nickname.setTextColor(c.getResources().getColor(
                    R.color.vip_name));
        } else {
            holder.vip.setVisibility(View.GONE);
            holder.nickname.setTextColor(c.getResources().getColor(
                    R.color.username));
        }
        if (Integer.valueOf(db.getCnum()) <= 0) {
            holder.reply_ll.setVisibility(View.GONE);
        } else {
            holder.reply_ll.setVisibility(View.VISIBLE);
            holder.reply_tv.setText(db.getCnum() + "");
        }
        if (db.getApplynum() > 0) {
            holder.bm_people_num.setText("已有" + db.getApplynum() + "人报名");
        } else {
            holder.bm_people_num.setText("");
        }

        if (db.getDistance() != null && !"".equals(db.getDistance())) {
            holder.location_tv.setText(Util.getDistance(db.getDistance()));
        } else if (db.getLatitude() != 0 && db.getLongitude() != 0
                && !ac.cs.getLat().equals("")) {
            holder.location_tv.setText(Util.getDistance(
                    Double.valueOf(ac.cs.getLng()),
                    Double.valueOf(ac.cs.getLat()), db.getLongitude(),
                    db.getLatitude())
                    + "km");
        }

        switch (db.getCtype()) {
            case 0:
                holder.coin.setVisibility(View.GONE);
                break;
            case 2:
                holder.coin.setVisibility(View.VISIBLE);
                holder.coin
                        .setText(Html
                                .fromHtml("<font color=\"#95948f\">赠送靓点：</font><font color=\"#228ada\">"
                                        + db.getCoin() + "</font>"));
                break;
            case 1:
                holder.coin.setVisibility(View.VISIBLE);
                holder.coin
                        .setText(Html
                                .fromHtml("<font color=\"#95948f\">我要靓点：</font><font color=\"#228ada\">"
                                        + db.getCoin() + "</font>"));
                break;
        }

        if (db.getDtype() == 5) {
            holder.aim_ll.setVisibility(View.VISIBLE);
            holder.aim_tv.setText(db.getAim() + "");
            holder.people_num_ll.setVisibility(View.GONE);
        } else {
            holder.aim_ll.setVisibility(View.GONE);
            holder.people_num_ll.setVisibility(View.VISIBLE);
            holder.people_num_tv.setText(Html
                    .fromHtml("<font color=\"#228ada\">" + db.getPeoplenum() + "</font>" + "人"));
        }

        holder.place_tv.setText(db.getAddress());
        holder.sex_tv.setText(Html.fromHtml("<font color=\"#228ada\">"
                + db.getObjsex() + "</font>"));
        holder.money_tv.setText(Html.fromHtml("<font color=\"#228ada\">"
                + db.getWhopay() + "</font>"));
        holder.time_tv.setText(Html.fromHtml("<font color=\"#228ada\">"
                + db.getDtime() + "</font>"));
        holder.remark_tv.setText(db.getRemark());
        holder.content_rl.setTag(db);
        holder.content_rl.setOnClickListener(detail);
        holder.content_rl.setOnLongClickListener(detailLongClick);
        return convertView;
    }

    class ViewHolder {
        ImageView userlogo;
        TextView nickname, sex, location_tv, coin, place_tv, people_num_tv,
                aim_tv, sex_tv, money_tv, time_tv, remark_tv, bm_people_num,
                reply_tv;
        StateTextViewBg state;
        View people_num_ll, aim_ll, reply_ll, content_rl;
        CustomVip vip;
    }

    OnClickListener userlogo = new OnClickListener() {

        @Override
        public void onClick(View v) {
            DateBean db = (DateBean) list.get(Integer.valueOf(v.getTag()
                    .toString()));
            Intent i = new Intent(
                    c,
                    db.getUserid().equals(ac.getLoginUserId()) ? UserInfoActivity_.class
                            : UserOtherInfoActivity_.class);
            i.putExtra("userId", db.getUserid());
            c.startActivity(i);
        }
    };
    OnClickListener detail = new OnClickListener() {

        @Override
        public void onClick(View v) {
            listener.detailClick(v.getTag());
        }
    };
    OnLongClickListener detailLongClick = new OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            if (dlistener != null) {
                dlistener.longClick(v.getTag());
                return true;
            }
            return false;
        }
    };

    public interface IDateAdapterListener {
        public void longClick(Object bean);
    }
}
