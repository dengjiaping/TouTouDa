package com.quanliren.quan_two.adapter.shop;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.adapter.ParentsAdapter;
import com.quanliren.quan_two.bean.ExchangeGroupBean;
import com.quanliren.quan_two.custom.PinnedHeaderListView;
import com.quanliren.quan_two.custom.PinnedHeaderListView.PinnedHeaderAdapter;

import java.util.Arrays;
import java.util.List;

public class ExchangeListAdapter extends ParentsAdapter implements
		SectionIndexer, PinnedHeaderAdapter, OnScrollListener {
	
	private int mLocationPosition = -1;  
    // 首字母集  
    private List<String> mFriendsSections;  
    private List<Integer> mFriendsPositions;  
    public void setmFriendsSections(List<String> mFriendsSections) {
		this.mFriendsSections = mFriendsSections;
	}

	public void setmFriendsPositions(List<Integer> mFriendsPositions) {
		this.mFriendsPositions = mFriendsPositions;
	}


	public ExchangeListAdapter(Context c, List list,List<String> sections,List<Integer> positions) {
		super(c, list);
		this.mFriendsSections=sections;
		this.mFriendsPositions=positions;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		int section = getSectionForPosition(position); 
		ExchangeGroupBean bean=(ExchangeGroupBean) list.get(position);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View
					.inflate(c, R.layout.exchange_fragment1_list_item, null);
			holder.head = (TextView)convertView.findViewById(R.id.now_position_txt);
			holder.gridview = (GridView) convertView.findViewById(R.id.gridview);
			holder.adapter=new ExchangeGridAdapter(c, bean.getItemlist());
			holder.gridview.setAdapter(holder.adapter);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
        if (getPositionForSection(section) == position) {  
        	holder.head.setVisibility(View.VISIBLE);  
            holder.head.setText(mFriendsSections.get(section).toUpperCase());  
        } else {  
        	holder.head.setVisibility(View.GONE);  
        }
       
        holder.adapter.setList(bean.getItemlist());
        holder.adapter.notifyDataSetChanged();
        return convertView;
	}
	
	class ViewHolder {
		TextView head;
		GridView gridview;
		ExchangeGridAdapter adapter;
	}

	@Override  
    public void onScrollStateChanged(AbsListView view, int scrollState) {  
        // TODO Auto-generated method stub  
  
    }  
  
    @Override  
    public void onScroll(AbsListView view, int firstVisibleItem,  
            int visibleItemCount, int totalItemCount) {  
        // TODO Auto-generated method stub  
        if (view instanceof PinnedHeaderListView) {  
            ((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);  
        }  
    }  
  
    @Override  
    public int getPinnedHeaderState(int position) {  
        int realPosition = position;  
        if (realPosition < 0  
                || (mLocationPosition != -1 && mLocationPosition == realPosition)) {  
            return PINNED_HEADER_GONE;  
        }  
        mLocationPosition = -1;  
        int section = getSectionForPosition(realPosition);  
        int nextSectionPosition = getPositionForSection(section + 1);  
        if (nextSectionPosition != -1  
                && realPosition == nextSectionPosition - 1) {  
            return PINNED_HEADER_PUSHED_UP;  
        }  
        return PINNED_HEADER_VISIBLE;  
    }  
  
    @Override  
    public void configurePinnedHeader(View header, int position, int alpha) {  
        // TODO Auto-generated method stub  
        int realPosition = position;  
        int section = getSectionForPosition(realPosition); 
        if(section<0){
        	return;
        }
        String title = (String) getSections()[section];  
        ((TextView) header.findViewById(R.id.list_header_text))  
                .setText(title.toUpperCase());  
    }  
  
    @Override  
    public Object[] getSections() {  
        // TODO Auto-generated method stub  
        return mFriendsSections.toArray();  
    }  
  
    @Override  
    public int getPositionForSection(int section) {  
        if (section < 0 || section >= mFriendsSections.size()) {  
            return -1;  
        }  
        return mFriendsPositions.get(section);  
    }  
  
    @Override  
    public int getSectionForPosition(int position) {  
        // TODO Auto-generated method stub  
        if (position < 0 || position >= getCount()) {  
            return -1;  
        }  
        int index = Arrays.binarySearch(mFriendsPositions.toArray(), position);  
        return index >= 0 ? index : -index - 2;  
    }  

}
