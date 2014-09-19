package com.quanliren.quan_two.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.bean.ProductListBean;
import com.quanliren.quan_two.custom.NoScrollGridView;
import com.quanliren.quan_two.custom.SanJiaoTextView;

import java.util.List;

public class ProductListAdapter extends ParentsAdapter {

    public ProductListAdapter(Context c, List list) {
        super(c, list);
    }

    String[] colors = new String[]{"#f0b457", "#b1d64f", "#ee7474", "#59bcef"};

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(c, R.layout.product_list_item, null);
            holder.tv = (SanJiaoTextView) convertView.findViewById(R.id.title);
            holder.gridview = (NoScrollGridView) convertView.findViewById(R.id.gridview);
            holder.adapter = new ProductGridAdapter(c, null);
            holder.gridview.setAdapter(holder.adapter);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProductListBean bean = (ProductListBean) getItem(position);
        holder.tv.setText(bean.getCategroy());
        holder.tv.setBackgroundColor(Color.parseColor(colors[position % colors.length]));
        holder.adapter.setList(bean.getItemlist());
        holder.adapter.notifyDataSetChanged();
        return convertView;
    }

    class ViewHolder {
        SanJiaoTextView tv;
        NoScrollGridView gridview;
        ProductGridAdapter adapter;
    }

}
