package com.quanliren.quan_two.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.bean.CustomFilterBean;

import java.util.List;

public class CustomFilterAdapter extends ParentsAdapter {

    public CustomFilterAdapter(Context c, List list) {
        super(c, list);
        // TODO Auto-generated constructor stub
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(c, R.layout.custom_filter_item, null);
            holder.btn = convertView.findViewById(R.id.btn);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CustomFilterBean cfb = (CustomFilterBean) list.get(position);
//		holder.title.setText(cfb.title);
//		holder.text.setText(cfb.defaultValue);
        return convertView;
    }

    class ViewHolder {
        View btn;
        TextView title, text;
    }

}
