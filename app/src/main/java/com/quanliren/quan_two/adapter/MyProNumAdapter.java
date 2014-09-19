package com.quanliren.quan_two.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.bean.ProBean;

import java.util.List;

public class MyProNumAdapter extends ParentsAdapter {

    public MyProNumAdapter(Context c, List list) {
        super(c, list);
        // TODO Auto-generated constructor stub
    }

    int[] colors = new int[]{R.color.color1, R.color.color2, R.color.color3};

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(c, R.layout.my_pro_num_item, null);
            holder.color_view = convertView.findViewById(R.id.color_view);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.num = (TextView) convertView.findViewById(R.id.num);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ProBean pb = (ProBean) list.get(position);
        StringBuilder sb = new StringBuilder("您参加");
        sb.append("<font color=\"#329fcd\">" + pb.getTitle() + "</font>").append("的抽奖编号为：");
        holder.title.setText(Html.fromHtml(sb.toString()));
        holder.num.setText(pb.getCode());
        holder.time.setText("抽奖日期：" + pb.getCtime());
        switch (pb.getCodestatus()) {
            case 0:
                holder.status.setText("[待开奖]");
                break;
            case 1:
                holder.status.setText("[未中奖]");
                break;
            case 2:
                holder.status.setText("[已中奖]");
                break;
            case 3:
                holder.status.setText("[已删除]");
                break;
        }
        holder.color_view.setBackgroundColor(c.getResources().getColor(colors[position % 3]));
        return convertView;
    }

    class ViewHolder {
        View color_view;
        TextView title, num, time, status;
    }
}
