package com.quanliren.quan_two.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class StateTextViewBg extends TextView {

    AtomicBoolean b = new AtomicBoolean(false);
    List<StateBean> list = new ArrayList<StateBean>();
    boolean large;

    public StateTextViewBg(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    public StateTextViewBg(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, 0);
    }

    public StateTextViewBg(Context context) {
        super(context);
        init(context, null, 0);
    }

    public void init(Context context, AttributeSet attrs, int defStyleAttr) {

        try {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.StateTextView, defStyleAttr, 0);

            large = a.getBoolean(R.styleable.StateTextView_large, false);
            a.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        list.add(new StateBean(R.drawable.ic_state_normal_large,
                R.drawable.ic_state_normal_large, R.string.state_normal,
                R.color.state_quite_color));
        list.add(new StateBean(R.drawable.ic_state_dinner,
                R.drawable.ic_state_dinner_large, R.string.state_dinner,
                R.color.state_dinner_color));
        list.add(new StateBean(R.drawable.ic_state_movie,
                R.drawable.ic_state_movie_large, R.string.state_movie,
                R.color.state_movie_color));
        list.add(new StateBean(R.drawable.ic_state_car,
                R.drawable.ic_state_car_large, R.string.state_car,
                R.color.state_car_color));
        list.add(new StateBean(R.drawable.ic_state_friend,
                R.drawable.ic_state_friend_large, R.string.state_friend,
                R.color.state_friend_color));
        list.add(new StateBean(R.drawable.ic_state_girl,
                R.drawable.ic_state_girl_large, R.string.state_girl,
                R.color.state_girl_color));

        setGravity(Gravity.CENTER_VERTICAL);

        setState(1);
    }

    public void setState(int state) {
        if (state >= list.size()) {
            return;
        }
        StateBean sb = list.get(state);
        setText(sb.String);
        Drawable icon = getResources().getDrawable(sb.img);
        icon.setBounds(0, 0, ImageUtil.dip2px(getContext(), 15),
                ImageUtil.dip2px(getContext(), 15));
        setTextColor(Color.WHITE);

        setBackgroundColor(Color.parseColor(getResources().getString(sb.color)));
        setPadding(ImageUtil.dip2px(getContext(), 2), 0, 0, 0);

        setHeight(ImageUtil.dip2px(getContext(), 20));

        setGravity(Gravity.CENTER_VERTICAL);

        setTextSize(14);

        Drawable right = getResources().getDrawable(
                R.drawable.ic_date_state_right);
        right.setBounds(0, 0, ImageUtil.dip2px(getContext(), 20),
                ImageUtil.dip2px(getContext(), 20));

        setCompoundDrawables(icon, null, right, null);
        setCompoundDrawablePadding(ImageUtil.dip2px(getContext(), 4));
    }

    class StateBean {
        int img;
        int img_large;
        int String;
        int color;

        public StateBean() {
            super();
            // TODO Auto-generated constructor stub
        }

        public StateBean(int img, int img_large, int string, int color) {
            super();
            this.img = img;
            this.img_large = img_large;
            String = string;
            this.color = color;
        }

    }
}
