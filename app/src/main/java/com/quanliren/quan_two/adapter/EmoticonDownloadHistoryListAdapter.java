package com.quanliren.quan_two.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a.dd.CircularProgressButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.adapter.ShopAdapter.IBuyListener;
import com.quanliren.quan_two.bean.emoticon.EmoticonActivityListBean.EmoticonZip;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class EmoticonDownloadHistoryListAdapter extends ParentsAdapter {

    IBuyListener listener;

    public EmoticonDownloadHistoryListAdapter(Context c, List list,
                                              IBuyListener listener) {
        super(c, list);
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        try {
            if (convertView == null) {
                convertView = View.inflate(c, R.layout.emoticonlist_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            EmoticonZip zip = (EmoticonZip) list.get(position);

            holder.name.setText(zip.getName());
            holder.title.setText(zip.getTitle());

            ImageLoader.getInstance().displayImage(zip.getIcoUrl(), holder.img);
            if (zip.isHave()) {
                holder.buy.setmOtherText("移除");
                holder.buy.setText("移除");
                holder.buy.setProgress(-3);
            } else if (holder.buy.getProgress() <= 0
                    || holder.buy.getProgress() >= 100) {
                switch (zip.getIsBuy()) {
                    case 1:
                        holder.buy.setmCompleteText("下载");
                        holder.buy.setText("下载");
                        holder.buy.setProgress(100);
                        break;
                    default:
                        holder.buy.setProgress(0);
                        break;
                }
            }
            holder.buy.setTag(zip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    class ViewHolder {
        @InjectView(R.id.name)
        TextView name;
        @InjectView(R.id.img)
        ImageView img;
        @InjectView(R.id.title)
        TextView title;
        @InjectView(R.id.buy)
        CircularProgressButton buy;
        @InjectView(R.id.buied)
        View buied;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        @OnClick(R.id.buy)
        void buy(View view) {
            listener.buyClick((CircularProgressButton) view);
        }
    }
}
