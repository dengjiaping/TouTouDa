package com.a.dd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

import com.quanliren.quan_two.activity.R;

public class CircularProgressButton extends Button {

	public static final int IDLE_STATE_PROGRESS = 0;
	public static final int ERROR_STATE_PROGRESS = -1;
	public static final int ENABLE_STATE_PROGRESS = -2;
	public static final int ENABLE_STATE_OTHER = -3;

	private StrokeGradientDrawable background;
	private CircularAnimatedDrawable mAnimatedDrawable;
	private CircularProgressDrawable mProgressDrawable;

	private State mState;
	private String mIdleText;

	private String mOtherText;

	public void setmOtherText(String mOtherText) {
		this.mOtherText = mOtherText;
	}

	public void setmIdleText(String mIdleText) {
		this.mIdleText = mIdleText;
	}

	private String mCompleteText;

	public void setmCompleteText(String mCompleteText) {
		this.mCompleteText = mCompleteText;
	}

	private String mErrorText;

	public void setmErrorText(String mErrorText) {
		this.mErrorText = mErrorText;
	}

	private String mEnableText;

	public void setmEnableText(String mEnableText) {
		this.mEnableText = mEnableText;
	}

	private int mColorIdle;

	public void setmColorIdle(int mColorIdle) {
		this.mColorIdle = mColorIdle;
	}

	private int mColorOther;
	private int mColorError;
	private int mColorEnable;
	private int mColorProgress;
	private int mColorComplete;
	private int mColorIndicator;
	private int mColorIndicatorBackground;
	private int mIconComplete;
	private int mIconError;
	private int mStrokeWidth;
	private int mPaddingProgress;
	private float mCornerRadius;
	private boolean mIndeterminateProgressMode;

	private enum State {
		PROGRESS, IDLE, COMPLETE, ERROR, ENABLE, OTHER
	}

	private int mMaxProgress;
	private int mProgress;

	private int mmMaxProgress;
	private int mmProgress;

	public int getMmMaxProgress() {
		return mmMaxProgress;
	}

	public void setMmMaxProgress(int mmMaxProgress) {
		this.mmMaxProgress = mmMaxProgress;
	}

	public int getMmProgress() {
		return mmProgress;
	}

	public void setMmProgress(int mmProgress) {
		this.mmProgress = mmProgress;
	}

	private boolean mMorphingInProgress;

	public CircularProgressButton(Context context) {
		super(context);
		init(context, null);
	}

	public CircularProgressButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public CircularProgressButton(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attributeSet) {
		mStrokeWidth = (int) getContext().getResources().getDimension(
				R.dimen.stroke_width);

		initAttributes(context, attributeSet);

		mMaxProgress = 100;
		mState = State.IDLE;

		setText(mIdleText);
		setTextColor(Color.WHITE);
		GradientDrawable gradientDrawable = (GradientDrawable) context
				.getResources().getDrawable(R.drawable.background).mutate();
		gradientDrawable.setColor(mColorIdle);
		gradientDrawable.setCornerRadius(mCornerRadius);
		background = new StrokeGradientDrawable(gradientDrawable);
		background.setStrokeColor(mColorIdle);
		background.setStrokeWidth(mStrokeWidth);
		setBackgroundCompat(gradientDrawable);
	}

	private void initAttributes(Context context, AttributeSet attributeSet) {
		TypedArray attr = getTypedArray(context, attributeSet,
				R.styleable.CircularProgressButton);

		if (attr == null) {
			return;
		}

		try {
			mIdleText = attr
					.getString(R.styleable.CircularProgressButton_cpb_textIdle);
			mCompleteText = attr
					.getString(R.styleable.CircularProgressButton_cpb_textComplete);
			mErrorText = attr
					.getString(R.styleable.CircularProgressButton_cpb_textError);
			mEnableText = attr
					.getString(R.styleable.CircularProgressButton_cpb_textEnable);

			mOtherText = attr
					.getString(R.styleable.CircularProgressButton_cpb_textOther);

			mIconComplete = attr.getResourceId(
					R.styleable.CircularProgressButton_cpb_iconComplete, 0);
			mIconError = attr.getResourceId(
					R.styleable.CircularProgressButton_cpb_iconError, 0);
			mCornerRadius = attr.getDimension(
					R.styleable.CircularProgressButton_cpb_cornerRadius, 0);
			mPaddingProgress = attr.getDimensionPixelSize(
					R.styleable.CircularProgressButton_cpb_paddingProgress, 0);

			int blue = Color.BLUE;
			int red = getColor(R.color.holo_red_light);
			int green = getColor(R.color.holo_green_light);
			int white = Color.WHITE;
			int grey = getColor(R.color.grey);
			int other = getColor(R.color.holo_other_light);

			mColorIdle = attr.getColor(
					R.styleable.CircularProgressButton_cpb_colorIdle, blue);

			mColorOther = attr.getColor(
					R.styleable.CircularProgressButton_cpb_colorOther, other);
			mColorError = attr.getColor(
					R.styleable.CircularProgressButton_cpb_colorError, red);
			mColorComplete = attr
					.getColor(
							R.styleable.CircularProgressButton_cpb_colorComplete,
							green);
			mColorProgress = attr
					.getColor(
							R.styleable.CircularProgressButton_cpb_colorProgress,
							white);
			mColorIndicator = attr
					.getColor(
							R.styleable.CircularProgressButton_cpb_colorIndicator,
							blue);
			mColorEnable = attr.getResourceId(
					R.styleable.CircularProgressButton_cpb_colorEnable, grey);
			mColorIndicatorBackground = attr
					.getColor(
							R.styleable.CircularProgressButton_cpb_colorIndicatorBackground,
							grey);
		} finally {
			attr.recycle();
		}
	}

	protected int getColor(int id) {
		return getResources().getColor(id);
	}

	protected TypedArray getTypedArray(Context context,
			AttributeSet attributeSet, int[] attr) {
		return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mProgress > 0 && mState == State.PROGRESS && !mMorphingInProgress) {
			if (mIndeterminateProgressMode) {
				drawIndeterminateProgress(canvas);
			} else {
				drawProgress(canvas);
			}
		}
	}

	private void drawIndeterminateProgress(Canvas canvas) {
		if (mAnimatedDrawable == null) {
			int offset = (getWidth() - getHeight()) / 2;
			mAnimatedDrawable = new CircularAnimatedDrawable(mColorIndicator,
					mStrokeWidth);
			int left = offset + mPaddingProgress;
			int right = getWidth() - offset - mPaddingProgress;
			int bottom = getHeight() - mPaddingProgress;
			int top = mPaddingProgress;
			mAnimatedDrawable.setBounds(left, top, right, bottom);
			mAnimatedDrawable.setCallback(this);
			mAnimatedDrawable.start();
		} else {
			mAnimatedDrawable.draw(canvas);
		}
	}

	private void drawProgress(Canvas canvas) {
		if (mProgressDrawable == null) {
			int offset = (getWidth() - getHeight()) / 2;
			int size = getHeight() - mPaddingProgress * 2;
			mProgressDrawable = new CircularProgressDrawable(size,
					mStrokeWidth, mColorIndicator);
			int left = offset + mPaddingProgress;
			mProgressDrawable.setBounds(left, mPaddingProgress, left,
					mPaddingProgress);
		}
		float sweepAngle = (360f / mMaxProgress) * mProgress;
		int percent = (int) (((float) mProgress / (float) mMaxProgress) * 100);
		mProgressDrawable.setText(percent + "");
		mProgressDrawable.setSweepAngle(sweepAngle);
		mProgressDrawable.draw(canvas);

		int x = getWidth() / 2; // 获取圆心的x坐标
		int y = getHeight() / 2; // 获取圆心的x坐标
		// int percent = (int)(((float)progress / (float)max) * 100);
		// //中间的进度百分比，先转换成float在进行除法运算，不然都为0
		float textWidth = createTextPaint().measureText(percent + "%"); // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间

		canvas.drawText(percent + "%", x - textWidth / 2, y + 14 / 2,
				createTextPaint()); // 画出进度百分比
	}

	private Paint mTextPaint;

	private Paint createTextPaint() {
		if (mTextPaint == null) {
			mTextPaint = new Paint();
			mTextPaint.setAntiAlias(true);
			mTextPaint.setTextSize(14);
			mTextPaint.setColor(Color.BLACK);
		}
		return mTextPaint;
	}

	public boolean isIndeterminateProgressMode() {
		return mIndeterminateProgressMode;
	}

	public void setIndeterminateProgressMode(boolean indeterminateProgressMode) {
		this.mIndeterminateProgressMode = indeterminateProgressMode;
	}

	@Override
	protected boolean verifyDrawable(Drawable who) {
		return who == mAnimatedDrawable || super.verifyDrawable(who);
	}

	private MorphingAnimation createMorphing() {
		mMorphingInProgress = true;

		MorphingAnimation animation = new MorphingAnimation(this, background);
		animation.setFromCornerRadius(mCornerRadius);
		animation.setToCornerRadius(mCornerRadius);

		animation.setFromWidth(getWidth());
		animation.setToWidth(getWidth());
		return animation;
	}

	private MorphingAnimation createProgressMorphing(float fromCorner,
			float toCorner, int fromWidth, int toWidth) {
		mMorphingInProgress = true;

		MorphingAnimation animation = new MorphingAnimation(this, background);
		animation.setFromCornerRadius(fromCorner);
		animation.setToCornerRadius(toCorner);

		animation.setPadding(mPaddingProgress);

		animation.setFromWidth(fromWidth);
		animation.setToWidth(toWidth);
		return animation;
	}

	private void morphToProgress() {
		setWidth(getWidth());
		setText(null);
		MorphingAnimation animation = createProgressMorphing(mCornerRadius,
				getHeight(), getWidth(), getHeight());

		animation.setFromColor(mColorIdle);
		animation.setToColor(mColorProgress);

		animation.setFromStrokeColor(mColorIdle);
		animation.setToStrokeColor(mColorIndicatorBackground);

		animation.setListener(mProgressStateListener);

		animation.start();

		invalidate();
	}

	private void morphCompleteToProgress() {
		setWidth(getWidth());
		setText(null);
		MorphingAnimation animation = createProgressMorphing(mCornerRadius,
				getHeight(), getWidth(), getHeight());

		animation.setFromColor(mColorComplete);
		animation.setToColor(mColorProgress);

		animation.setFromStrokeColor(mColorComplete);
		animation.setToStrokeColor(mColorIndicatorBackground);

		animation.setListener(mProgressStateListener);

		animation.start();

		invalidate();
	}

	private OnAnimationEndListener mProgressStateListener = new OnAnimationEndListener() {
		@Override
		public void onAnimationEnd() {
			mMorphingInProgress = false;
			mState = State.PROGRESS;

			invalidate();
		}
	};

	private void morphProgressToComplete() {
		MorphingAnimation animation = createProgressMorphing(getHeight(),
				mCornerRadius, getHeight(), getWidth());

		animation.setFromColor(mColorProgress);
		animation.setToColor(mColorComplete);

		animation.setFromStrokeColor(mColorIndicator);
		animation.setToStrokeColor(mColorComplete);

		animation.setListener(mCompleteStateListener);

		animation.start();

		invalidate();
	}

	private void morphIdleToComplete() {
		/*
		 * MorphingAnimation animation = createMorphing();
		 * 
		 * animation.setFromColor(mColorIdle);
		 * animation.setToColor(mColorComplete);
		 * 
		 * animation.setFromStrokeColor(mColorIdle);
		 * animation.setToStrokeColor(mColorComplete);
		 * 
		 * animation.setListener(mCompleteStateListener);
		 * 
		 * animation.start();
		 * 
		 * invalidate();
		 */

		background.getGradientDrawable().setStroke(0, mColorIdle);
		background.getGradientDrawable().setColor(mColorComplete);

		removeIcon();
		setText(mCompleteText);
		setTextColor(Color.WHITE);
		mMorphingInProgress = false;
		mState = State.COMPLETE;
		invalidate();

	}

	private void morphOtherToComplete() {

		background.getGradientDrawable().setStroke(0, mColorOther);
		background.getGradientDrawable().setColor(mColorComplete);

		removeIcon();
		setText(mCompleteText);
		setTextColor(Color.WHITE);
		mMorphingInProgress = false;
		mState = State.COMPLETE;
		invalidate();

	}

	private void morphCompleteToOther() {

		background.getGradientDrawable().setStroke(0, mColorComplete);
		background.getGradientDrawable().setColor(mColorOther);

		removeIcon();
		setText(mOtherText);
		setTextColor(Color.BLACK);
		mMorphingInProgress = false;
		mState = State.OTHER;
		invalidate();

	}

	private OnAnimationEndListener mCompleteStateListener = new OnAnimationEndListener() {
		@Override
		public void onAnimationEnd() {
			if (mIconComplete != 0) {
				setIcon(mIconComplete);
			} else {
				setText(mCompleteText);
				setTextColor(Color.WHITE);
			}
			mMorphingInProgress = false;
			mState = State.COMPLETE;

			invalidate();
		}
	};

	public void morphCompleteToIdle() {
		MorphingAnimation animation = createMorphing();

		animation.setFromColor(mColorComplete);
		animation.setToColor(mColorIdle);

		animation.setFromStrokeColor(mColorComplete);
		animation.setToStrokeColor(mColorIdle);

		animation.setListener(mIdleStateListener);

		animation.start();

		invalidate();

	}

	public void morphEnableToIdle() {
		MorphingAnimation animation = createMorphing();

		animation.setFromColor(mColorEnable);
		animation.setToColor(mColorIdle);

		animation.setFromStrokeColor(mColorEnable);
		animation.setToStrokeColor(mColorIdle);

		animation.setListener(mIdleStateListener);

		animation.start();

		invalidate();
	}

	public void morphCompleteToEnable() {
		MorphingAnimation animation = createMorphing();

		animation.setFromColor(mColorIdle);
		animation.setToColor(mColorEnable);

		animation.setFromStrokeColor(mColorIdle);
		animation.setToStrokeColor(mColorEnable);

		animation.setListener(mEnableStateListener);

		animation.start();

		invalidate();
	}

	public void morphIdleToEnable() {
		background.getGradientDrawable().setStroke(0, mColorIdle);
		background.getGradientDrawable().setColor(mColorEnable);

		removeIcon();
		if (mEnableText == null || mEnableText.equals("")) {
			setText(mIdleText);
		} else {
			setText(mEnableText);
		}
		setTextColor(Color.BLACK);
		mMorphingInProgress = false;
		mState = State.ENABLE;
		setEnabled(false);
		invalidate();
	}

	public void morphIdleToOther() {
		background.getGradientDrawable().setStroke(0, mColorIdle);
		background.getGradientDrawable().setColor(mColorOther);

		removeIcon();
		if (mOtherText != null && !mOtherText.equals("")) {
			setText(mOtherText);
		}
		setTextColor(Color.BLACK);
		mMorphingInProgress = false;
		mState = State.OTHER;
		invalidate();
	}

	private void morphErrorToIdle() {
		MorphingAnimation animation = createMorphing();

		animation.setFromColor(mColorError);
		animation.setToColor(mColorIdle);

		animation.setFromStrokeColor(mColorError);
		animation.setToStrokeColor(mColorIdle);

		animation.setListener(mIdleStateListener);

		animation.start();

		invalidate();
	}

	private OnAnimationEndListener mIdleStateListener = new OnAnimationEndListener() {
		@Override
		public void onAnimationEnd() {
			removeIcon();
			setText(mIdleText);
			setTextColor(Color.WHITE);
			mMorphingInProgress = false;
			mState = State.IDLE;
			setEnabled(true);

			invalidate();
		}
	};

	private OnAnimationEndListener mEnableStateListener = new OnAnimationEndListener() {
		@Override
		public void onAnimationEnd() {
			removeIcon();
			if (mEnableText == null || mEnableText.equals("")) {
				setText(mIdleText);
			} else {
				setText(mEnableText);
			}
			mMorphingInProgress = false;
			mState = State.ENABLE;
			setTextColor(Color.BLACK);
			setEnabled(false);

			invalidate();
		}
	};

	private void morphIdleToError() {
		/*
		 * MorphingAnimation animation = createMorphing();
		 * 
		 * animation.setFromColor(mColorIdle);
		 * animation.setToColor(mColorError);
		 * 
		 * animation.setFromStrokeColor(mColorIdle);
		 * animation.setToStrokeColor(mColorError);
		 * 
		 * animation.setListener(mErrorStateListener);
		 * 
		 * animation.start();
		 * 
		 * invalidate();
		 */

		background.getGradientDrawable().setStroke(0, mColorIdle);
		background.getGradientDrawable().setColor(mColorError);

		removeIcon();
		setText(mErrorText);
		setTextColor(Color.WHITE);
		mMorphingInProgress = false;
		mState = State.ERROR;
		invalidate();
	}

	private void morphProgressToError() {
		MorphingAnimation animation = createProgressMorphing(getHeight(),
				mCornerRadius, getHeight(), getWidth());

		animation.setFromColor(mColorProgress);
		animation.setToColor(mColorError);

		animation.setFromStrokeColor(mColorIndicator);
		animation.setToStrokeColor(mColorError);
		animation.setListener(mErrorStateListener);

		animation.start();

		invalidate();
	}

	private OnAnimationEndListener mErrorStateListener = new OnAnimationEndListener() {
		@Override
		public void onAnimationEnd() {
			if (mIconComplete != 0) {
				setIcon(mIconError);
			} else {
				setText(mErrorText);
				setTextColor(Color.WHITE);
			}
			mMorphingInProgress = false;
			mState = State.ERROR;

			invalidate();
		}
	};

	private void morphProgressToIdle() {
		/*
		 * background.getGradientDrawable().setStroke(mStrokeWidth, mColorIdle);
		 * background.getGradientDrawable().setColor(mColorIdle);
		 * 
		 * removeIcon(); setText(mIdleText); setTextColor(Color.WHITE);
		 * mMorphingInProgress = false; mState = State.IDLE; invalidate();
		 */

		MorphingAnimation animation = createProgressMorphing(getHeight(),
				mCornerRadius, getHeight(), getWidth());

		animation.setFromColor(mColorProgress);
		animation.setToColor(mColorIdle);

		animation.setFromStrokeColor(mColorIndicator);
		animation.setToStrokeColor(mColorIdle);
		animation.setListener(mIdleStateListener);

		animation.start();

		invalidate();
	}

	private void setIcon(int icon) {
		Drawable drawable = getResources().getDrawable(icon);
		if (drawable != null) {
			int padding = (getWidth() / 2) - (drawable.getIntrinsicWidth() / 2);
			setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
			setPadding(padding, 0, 0, 0);
		}
	}

	protected void removeIcon() {
		setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		setPadding(0, 0, 0, 0);
	}

	/**
	 * Set the View's background. Masks the API changes made in Jelly Bean.
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void setBackgroundCompat(Drawable drawable) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			setBackground(drawable);
		} else {
			setBackgroundDrawable(drawable);
		}
	}

	public void setProgress(int progress) {
		if (mProgress == progress) {
			return;
		}
		mProgress = progress;

		if (mMorphingInProgress) {
			return;
		}

		if (mProgress >= mMaxProgress) {
			if (mState == State.PROGRESS) {
				morphProgressToComplete();
			} else if (mState == State.IDLE) {
				morphIdleToComplete();
			} else if (mState == State.OTHER) {
				morphOtherToComplete();
			}
		} else if (mProgress > IDLE_STATE_PROGRESS) {
			if (mState == State.IDLE) {
				morphToProgress();
			} else if (mState == State.PROGRESS) {
				invalidate();
			} else if (mState == State.COMPLETE) {
				morphCompleteToProgress();
			}
		} else if (mProgress == ERROR_STATE_PROGRESS) {
			if (mState == State.PROGRESS) {
				morphProgressToError();
			} else if (mState == State.IDLE) {
				morphIdleToError();
			}
		} else if (mProgress == IDLE_STATE_PROGRESS) {
			if (mState == State.COMPLETE) {
				morphCompleteToIdle();
			} else if (mState == State.PROGRESS) {
				morphProgressToIdle();
			} else if (mState == State.ERROR) {
				morphErrorToIdle();
			} else if (mState == State.ENABLE) {
				morphEnableToIdle();
			}
		} else if (mProgress == ENABLE_STATE_PROGRESS) {
			if (mState == State.IDLE) {
				morphIdleToEnable();
			} else {
				morphCompleteToEnable();
			}
		} else if (mProgress == ENABLE_STATE_OTHER) {
			if (mState == State.IDLE) {
				morphIdleToOther();
			} else if (mState == State.COMPLETE) {
				morphCompleteToOther();
			}
		}
	}

	public int getProgress() {
		return mProgress;
	}
}
