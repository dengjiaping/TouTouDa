package com.quanliren.quan_two.custom.emoji;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;

import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.application.AppClass;
import com.quanliren.quan_two.custom.HandyTextView;
import com.quanliren.quan_two.util.DrawableCache;
import com.quanliren.quan_two.util.ImageUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.DrawableCallBack;
import pl.droidsonroids.gif.GifDrawable;

public class EmoticonsTextView extends HandyTextView {

	public EmoticonsTextView(Context context) {
		super(context);
	}

	public EmoticonsTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	int imgSize=0;
	
	public EmoticonsTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		imgSize=ImageUtil.dip2px(context, 30);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.NickNameEditText);
		if (a != null) {
			imgSize = a.getDimensionPixelSize(R.styleable.NickNameEditText_imgSize,
					imgSize);
			a.recycle();
		}
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		callBack=null;
		num=0;
		if (!TextUtils.isEmpty(text)) {
			super.setText(replace(text), type);
		} else {
			super.setText(text, type);
		}
	}

	private Pattern buildPattern() {
		StringBuilder patternString = new StringBuilder(
				AppClass.mEmoticons.size() * 3);
		patternString.append('(');
		for (int i = 0; i < AppClass.mEmoticons.size(); i++) {
			String s = AppClass.mEmoticons.get(i);
			patternString.append(Pattern.quote(s));
			patternString.append('|');
		}
		patternString.replace(patternString.length() - 1,
				patternString.length(), ")");
		return Pattern.compile(patternString.toString());
	}
	
	private int num=0;

	public static Map<String, Integer> mEmoticonsId = new HashMap<String, Integer>();
	
	private CharSequence replace(CharSequence text) {
		try {
			SpannableStringBuilder builder = new SpannableStringBuilder(text);
			Pattern pattern = buildPattern();
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				if (AppClass.mEmoticonsId.containsKey(matcher.group())) {
					int id = AppClass.mEmoticonsId.get(matcher.group());
					
					GifDrawable bitmap = (GifDrawable) DrawableCache.getInstance().getDrawable(id, getContext());
//					Bitmap bitmap = ImageLoader.getInstance().loadImageSync("drawable://" + id);
					if (bitmap != null) {
						num++;
//						if(num>9){
//							bitmap.stop();
//						}
						if(callBack==null){
							bitmap.setCallBack(callBack=new DrawableCallBack(){
								public void invalidateDrawable(int time,Drawable draw) {
									if(num>9){
										return;
									}
									postInvalidate();
								};
							});
						}
						bitmap.setBounds(8, 0, imgSize,
								imgSize);
						ImageSpan span = new ImageSpan(bitmap,ImageSpan.ALIGN_BOTTOM);
						builder.setSpan(span, matcher.start(), matcher.end(),
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			}
			return builder;
		} catch (Exception e) {
			return text;
		}
	}
	
	DrawableCallBack callBack;
}
