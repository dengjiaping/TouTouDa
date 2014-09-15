package com.quanliren.quan_two.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class TabLinearLayout extends LinearLayout {

	OnTabClickListener listener;
	List<TextView> tabs=new ArrayList<TextView>();
	
	public TabLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TabLinearLayout(Context context) {
		super(context);
		init();
	}
	
	void init(){
		setBackgroundResource(R.color.bottom_bg);
		setGravity(Gravity.CENTER_VERTICAL);
	}
	
	public void setDate(List<TabBean> list){
		tabs.clear();
		removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			LinearLayout ll=createLinearLayout();
			ll.addView(createImageView(list.get(i).getImg()));
			TextView tv;
			ll.addView(tv=createTextView(list.get(i).getText()));
			if(i==0){
				tv.setTextColor(getResources().getColor(R.color.nav_press_txt));
			}
			final int j=i;
			ll.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					for (TextView lll : tabs) {
						lll.setTextColor(Color.WHITE);
					}
					tabs.get(j).setTextColor(getResources().getColor(R.color.nav_press_txt));
					if(listener!=null){
						listener.onTabClick(j);
					}
				}
			});
			tabs.add(tv);
			addView(ll);
		}
	}
	
	public void setCurrentIndex(int position){
		for (TextView lll : tabs) {
			lll.setTextColor(Color.WHITE);
		}
		tabs.get(position).setTextColor(getResources().getColor(R.color.nav_press_txt));
	}
	
	public LinearLayout createLinearLayout(){
		
		LinearLayout ll=new LinearLayout(getContext());
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
		lp.weight=1;
		ll.setGravity(Gravity.CENTER);
		ll.setBackgroundResource(R.drawable.bottom_btn);
		ll.setLayoutParams(lp);
		ll.setClickable(true);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		
		return ll;
	}
	
	public ImageView createImageView(int img){
		ImageView iv=new ImageView(getContext());
		LayoutParams lp = new LayoutParams(ImageUtil.dip2px(getContext(), 20),ImageUtil.dip2px(getContext(), 20));
		iv.setLayoutParams(lp);
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setImageResource(img);
		return iv;
	}
	
	public TextView createTextView(String text){
		TextView iv=new TextView(getContext());
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,ImageUtil.dip2px(getContext(), 25));
		lp.leftMargin=ImageUtil.dip2px(getContext(), 4);
		iv.setGravity(Gravity.CENTER_VERTICAL);
		iv.setLayoutParams(lp);
		iv.setText(text);
		iv.setTextColor(getResources().getColor(R.color.nav_bar_text));
		iv.setTextSize(16);
		return iv;
	}
	
	public static class TabBean{
		int img;
		String text;
		public int getImg() {
			return img;
		}
		public void setImg(int img) {
			this.img = img;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public TabBean() {
			super();
			// TODO Auto-generated constructor stub
		}
		public TabBean(int img, String text) {
			super();
			this.img = img;
			this.text = text;
		}
	}
	
	public interface OnTabClickListener{
		void onTabClick(int position);
	}
	
	public void setListener(OnTabClickListener listener) {
		this.listener = listener;
	}
}
