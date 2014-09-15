package com.quanliren.quan_two.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.bean.Advertisement;
import com.quanliren.quan_two.custom.RoundProgressBar;

import java.util.ArrayList;
import java.util.List;

public class LBAdapter extends PagerAdapter {

	public Context c;
	public Handler handler;
	private List<Advertisement> mPhotos = new ArrayList<Advertisement>();

	public LBAdapter(List<Advertisement> photos) {
		if (photos != null) {
			mPhotos = photos;
		}
	}

	public void setList(List<Advertisement> list) {
		mPhotos = list;
	}

	@Override
	public int getCount() {
		if (mPhotos.size() > 1) {
			return Integer.MAX_VALUE;
		}
		return mPhotos.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) {
		c=container.getContext();
		View view = View.inflate(container.getContext(), R.layout.poster_item,
				null);
		ImageView photoView = (ImageView) view.findViewById(R.id.photoview);
		final RoundProgressBar pb = (RoundProgressBar) view
				.findViewById(R.id.loadProgressBar);
		ImageLoader.getInstance().displayImage(
				mPhotos.get(position % mPhotos.size()).imgpath, photoView,
				null, ill, new ImageLoadingProgressListener() {

					@Override
					public void onProgressUpdate(String imageUri, View view,
							int current, int total) {
						if (current == total) {
							pb.setVisibility(View.GONE);
						} else {
							pb.setVisibility(View.VISIBLE);
							pb.setMax(total);
							pb.setProgress(current);
						}
					}
				});
		if(view_h!=-1){
			photoView.setLayoutParams(new RelativeLayout.LayoutParams(view_w, view_h));
		}
		container.addView(view,view_w,view_h);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
	
	private int view_w=-1;
	private int view_h=-1;
	
	SimpleImageLoadingListener ill = new SimpleImageLoadingListener() {

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (view_h == -1) {
				float scaleWidth1 = (float) c.getResources()
						.getDisplayMetrics().widthPixels
						/ loadedImage.getWidth();
				int height = (int) (loadedImage.getHeight() * scaleWidth1);
				view_w =  c.getResources()
						.getDisplayMetrics().widthPixels;
				view_h = height;
				view.setLayoutParams(new RelativeLayout.LayoutParams( c.getResources()
						.getDisplayMetrics().widthPixels,
						height));
			} else {
				view.setLayoutParams(new RelativeLayout.LayoutParams(view_w,
						view_h));
			}
			Message msg=handler.obtainMessage();
			msg.arg1=view_w;
			msg.arg2=view_h;
			msg.what=1;
			msg.sendToTarget();
		}
	};
}
