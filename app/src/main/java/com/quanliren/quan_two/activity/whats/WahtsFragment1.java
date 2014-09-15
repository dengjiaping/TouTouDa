package com.quanliren.quan_two.activity.whats;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.a.nineoldandroids.animation.Animator;
import com.a.nineoldandroids.animation.AnimatorSet;
import com.a.nineoldandroids.animation.ObjectAnimator;
import com.a.nineoldandroids.view.ViewHelper;
import com.a.nineoldandroids.view.ViewPropertyAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.fragment.base.MenuFragmentBase;
import com.quanliren.quan_two.fragment.impl.LoaderImpl;
import com.quanliren.quan_two.util.BitmapCache;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment
public class WahtsFragment1 extends MenuFragmentBase implements LoaderImpl{

	private static final String TAG = "WahtsFragment1";

	View view;
	@FragmentArg
	boolean have;
	@FragmentArg
	int res;
	@ViewById
	FrameLayout content;

	int screen_w, screen_h;
	int max_w = 640, max_h = 1136;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.whats1, null);
		} else {
			ViewParent parent = view.getParent();
			if (parent != null && parent instanceof ViewGroup) {
				((ViewGroup) parent).removeView(view);
			}
		}
		return view;
	}

	public void refresh() {
		if(getActivity()!=null&&init.compareAndSet(false, true)){
			screen_h = getResources().getDisplayMetrics().heightPixels;
			screen_w = getResources().getDisplayMetrics().widthPixels;
			createTopYun(50, 150, R.drawable.yunduo1);
			createBg(100, 202, res);
			if (have) {
				createImageView(45, 326, R.drawable.welcome_1_icon_1);
				createImageView(57, 546, R.drawable.welcome_1_icon_3);
				createImageView(484, 267, R.drawable.welcome_1_icon_2);
				createImageView(484, 476, R.drawable.welcome_1_icon_4);
				createImageView(409, 646, R.drawable.welcome_1_icon_5);
			}
			createTopYun2(412, 785, R.drawable.yunduo2);
		}
	}

	List<Animator> list = new ArrayList<Animator>();
	int num = 0;

	public ImageView getPositionImageView(int i_x, int i_y, int res) {
		float x_scale = (float) i_x / (float) max_w;
		float x = (float) x_scale * (float) screen_w;

		float y_scale = (float) i_y / (float) max_h;
		float y = (float) y_scale * (float) screen_h;

		ImageView img = new ImageView(getActivity());
		ViewHelper.setX(img, x);
		img.setScaleType(ScaleType.CENTER_CROP);
		ViewHelper.setY(img, y);
		img.setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT));
		return img;
	}

	public ImageView createImageByCache(int i_x, int i_y, int res) {
		ImageView img = getPositionImageView(i_x, i_y, res);
		Bitmap loadedImage = BitmapCache.getInstance().getBitmap(res,
				getActivity());
		int i_w = loadedImage.getWidth();
		int i_h = loadedImage.getHeight();

		float w_scale = (float) i_w / (float) max_w;
		int n_i_w = (int) ((float) w_scale * (float) screen_w);

		float n_w_scale = (float) n_i_w / (float) i_w;
		int n_i_h = (int) ((float) i_h * n_w_scale);

		img.setLayoutParams(new FrameLayout.LayoutParams(n_i_w, n_i_h));
		img.setImageBitmap(loadedImage);
		content.addView(img);
		return img;
	}

	public void createBg(int i_x, int i_y, int res) {
		ImageView img = createImageByCache(i_x, i_y, res);
		ViewHelper.setAlpha(img, 0);
		ViewPropertyAnimator.animate(img).alpha(1).setDuration(1000).start();
	}

	public void createTopYun(int i_x, int i_y, int res) {
		ImageView img = createImageByCache(i_x, i_y, res);
		float x = ViewHelper.getX(img);
		ViewHelper.setTranslationX(img, -img.getLayoutParams().width);
		ViewPropertyAnimator.animate(img).translationX(x).setDuration(1000)
				.start();
	}

	public void createTopYun2(int i_x, int i_y, int res) {
		ImageView img = createImageByCache(i_x, i_y, res);
		float x = ViewHelper.getX(img);
		ViewHelper.setTranslationX(img, screen_w + img.getLayoutParams().width);
		ViewPropertyAnimator.animate(img).translationX(x).setDuration(1000)
				.start();
	}

	public void createImageView(int i_x, int i_y, int res) {
		ImageView img = getPositionImageView(i_x, i_y, res);
		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(img, "scaleX", 0, 1)
				.setDuration(200), ObjectAnimator.ofFloat(img, "scaleY", 0, 1)
				.setDuration(200));
		list.add(set);
		ImageLoader.getInstance().displayImage("drawable://" + res, img,
				ac.options_no_default, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						int i_w = loadedImage.getWidth();
						int i_h = loadedImage.getHeight();

						float w_scale = (float) i_w / (float) max_w;
						int n_i_w = (int) ((float) w_scale * (float) screen_w);

						float n_w_scale = (float) n_i_w / (float) i_w;
						int n_i_h = (int) ((float) i_h * n_w_scale);

						view.setLayoutParams(new FrameLayout.LayoutParams(
								n_i_w, n_i_h));
						ViewHelper.setScaleX(view, 0);
						ViewHelper.setScaleY(view, 0);
						ViewHelper.setPivotX(view, n_i_w * 0.5f);
						ViewHelper.setPivotY(view, n_i_h * 0.5f);
						num++;
						if (num >= 5) {
							AnimatorSet setAll = new AnimatorSet();
							setAll.playSequentially(list);
							setAll.start();
						}
					}
				});
		content.addView(img);
	}

}
