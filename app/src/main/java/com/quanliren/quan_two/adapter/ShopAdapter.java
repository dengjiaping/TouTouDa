package com.quanliren.quan_two.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a.dd.CircularProgressButton;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.bean.ShopBean;

import java.util.List;

public class ShopAdapter extends ParentsAdapter {

    IBuyListener listener;

    public ShopAdapter(Context c, List list, IBuyListener listener) {
        super(c, list);
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        ShopBean sb = (ShopBean) list.get(position);
        return sb.getViewType();
    }

    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 4;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            switch (getItemViewType(position)) {
                case 0:
                    convertView = LayoutInflater.from(c).inflate(
                            R.layout.shop_item_title, null);
                    break;
                case 1:
                    convertView = LayoutInflater.from(c).inflate(
                            R.layout.shop_item_top, null);
                    break;
                case 2:
                    convertView = LayoutInflater.from(c).inflate(
                            R.layout.shop_item_mid, null);
                    break;
                case 3:
                    convertView = LayoutInflater.from(c).inflate(
                            R.layout.shop_item_btm, null);
                    break;
            }
            holder.title = (TextView) convertView.findViewById(R.id.title);
            try {
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.price = (CircularProgressButton) convertView
                        .findViewById(R.id.price);
                holder.price.setIndeterminateProgressMode(true);
            } catch (Exception e) {
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ShopBean sb = (ShopBean) list.get(position);

        try {
            holder.title.setText(sb.getTitle());
            holder.img.setImageResource(sb.getImg());
            holder.price.setmIdleText(String.valueOf(sb.getPrice()));
            holder.price.setText(String.valueOf(sb.getPrice()));

            holder.price.setTag(sb);
            final CircularProgressButton price = holder.price;
            price.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.buyClick(price);
                }
            });
        } catch (Exception e) {
        }
        return convertView;
    }

    class ViewHolder {
        TextView title;
        ImageView img;
        CircularProgressButton price;
    }

    public interface IBuyListener {
        void buyClick(CircularProgressButton progress);
    }
}
