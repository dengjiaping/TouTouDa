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
import com.quanliren.quan_two.activity.user.UserInfoActivity_;
import com.quanliren.quan_two.activity.user.UserOtherInfoActivity_;
import com.quanliren.quan_two.adapter.QuanAdapter.IQuanAdapter;
import com.quanliren.quan_two.bean.DateBean;
import com.quanliren.quan_two.custom.StateTextViewBg;
import com.quanliren.quan_two.custom.UserNickNameRelativeLayout;
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
            convertView = View.inflate(c, R.layout.date_item_new, null);
            holder.userlogo = (ImageView) convertView
                    .findViewById(R.id.userlogo);
            holder.nick_ll = (UserNickNameRelativeLayout) convertView
                    .findViewById(R.id.nick_ll);
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
            holder.reply_ll = convertView.findViewById(R.id.reply_ll);
            holder.content_rl = convertView.findViewById(R.id.top);
            holder.img_state= (ImageView) convertView.findViewById(R.id.img_state);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DateBean db = (DateBean) getItem(position);
        ImageLoader.getInstance().displayImage(
                db.getAvatar() + StaticFactory._160x160, holder.userlogo);
        holder.nick_ll.setUser(db.getNickname(), db.getSex(), db.getAge(), db.getIsvip());
        holder.userlogo.setTag(position);
        holder.userlogo.setOnClickListener(userlogo);
        holder.state.setState(db.getDtype());

        switch (db.getDtype()){
            case 1:
                holder.img_state.setImageResource(R.drawable.ic_state_dinner_largest);
                break;
            case 2:
                holder.img_state.setImageResource(R.drawable.ic_state_movie_largest);
                break;
            case 3:
                holder.img_state.setImageResource(R.drawable.ic_state_car_largest);
                break;
            case 4:
                holder.img_state.setImageResource(R.drawable.ic_state_friend_largest);
                break;
            case 5:
                holder.img_state.setImageResource(R.drawable.ic_state_girl_largest);
                break;
        }

        if (Integer.valueOf(db.getCnum()) <= 0) {
            holder.reply_ll.setVisibility(View.GONE);
        } else {
            holder.reply_ll.setVisibility(View.VISIBLE);
            holder.reply_tv.setText(db.getCnum() + "");
        }
        if (db.getApplynum() > 0) {
            holder.bm_people_num.setText(db.getApplynum() + "人已报名");
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
                holder.coin
                        .setText("");
                break;
            case 2:
                holder.coin
                        .setText(Html
                                .fromHtml("赠送靓点　<font color=\"#228ada\">"
                                        + db.getCoin() + "</font>"));
                break;
            case 1:
                holder.coin
                        .setText(Html
                                .fromHtml("我要靓点　<font color=\"#228ada\">"
                                        + db.getCoin() + "</font>"));
                break;
        }

        if (db.getDtype() == 5) {
            holder.aim_tv.setVisibility(View.VISIBLE);
            holder.aim_tv.setText(Html.fromHtml("目的　<font color=\"#228ada\">"
                    + db.getAim() + "</font>"));
            holder.people_num_tv.setVisibility(View.GONE);
        } else {
            holder.aim_tv.setVisibility(View.GONE);
            holder.people_num_tv.setVisibility(View.VISIBLE);
            holder.people_num_tv.setText(Html
                    .fromHtml("人数　<font color=\"#228ada\">" + db.getPeoplenum() + "</font>人"));
        }
        holder.place_tv.setText(Html.fromHtml("<font color=\"#393939\">"
                + db.getAddress() + "</font>"));
        holder.sex_tv.setText(Html.fromHtml("性别　<font color=\"#228ada\">"
                + db.getObjsex() + "</font>"));
        holder.money_tv.setText(Html.fromHtml("消费　<font color=\"#228ada\">"
                + db.getWhopay() + "</font>"));
        holder.time_tv.setText(Html.fromHtml("时间　<font color=\"#228ada\">"
                + db.getDtime() + "</font>"));
        if(db.getRemark()==null||db.getRemark().equals("")){
            holder.remark_tv.setText(Html.fromHtml("<font color=\"#393939\">无</font>"));
        }else {
            holder.remark_tv.setText(Html.fromHtml("<font color=\"#393939\">"
                    + db.getRemark() + "</font>"));
        }
        holder.content_rl.setTag(db);
        holder.content_rl.setOnClickListener(detail);
        holder.content_rl.setOnLongClickListener(detailLongClick);
        return convertView;
    }

    class ViewHolder {
        ImageView userlogo;
        TextView  location_tv, coin, place_tv, people_num_tv,
                aim_tv, sex_tv, money_tv, time_tv, remark_tv, bm_people_num,
                reply_tv;
        UserNickNameRelativeLayout nick_ll;
        StateTextViewBg state;
        View   reply_ll, content_rl;
        ImageView img_state;
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
