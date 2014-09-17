package com.quanliren.quan_two.activity.user;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.quanliren.quan_two.util.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.a.nineoldandroids.animation.Animator;
import com.a.nineoldandroids.animation.AnimatorListenerAdapter;
import com.a.nineoldandroids.animation.AnimatorSet;
import com.a.nineoldandroids.animation.ObjectAnimator;
import com.a.nineoldandroids.animation.ValueAnimator;
import com.a.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.activity.location.GDLocation;
import com.quanliren.quan_two.activity.reg.ForgetPassWordActivity1_;
import com.quanliren.quan_two.activity.reg.RegFirst_;
import com.quanliren.quan_two.adapter.ParentsAdapter;
import com.quanliren.quan_two.bean.LoginUser;
import com.quanliren.quan_two.bean.MoreLoginUser;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.bean.UserTable;
import com.quanliren.quan_two.custom.CustomRelativeLayout;
import com.quanliren.quan_two.custom.CustomRelativeLayout.OnSizeChangedListener;
import com.quanliren.quan_two.custom.RoundImageProgressBar;
import com.quanliren.quan_two.db.DBHelper;
import com.quanliren.quan_two.pull.lib.listeners.OnRefreshListener;
import com.quanliren.quan_two.util.StaticFactory;
import com.quanliren.quan_two.util.URL;
import com.quanliren.quan_two.util.Util;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.a.nineoldandroids.view.ViewPropertyAnimator.animate;

@EActivity
public class LoginActivity extends BaseActivity implements OnRefreshListener {

	@ViewById
	EditText username;
	@ViewById
	EditText password;
	@ViewById
	View username_ll;
	@ViewById
	CustomRelativeLayout crl;
	@ViewById
	TextView forgetpassword;
	@ViewById
	Button loginBtn;
	@ViewById
	TextView regBtn;
	@ViewById
	LinearLayout margin_ll;
	@ViewById
	View delete_username_btn;
	@ViewById
	View delete_password_btn;
	@ViewById
	View more_username_btn;
	@ViewById
	RoundImageProgressBar round_img1;
	@ViewById
	RoundImageProgressBar round_img2;
	@ViewById
	View userlogo_ll;
	@ViewById
	ImageView userlogo;
	@ViewById
	ScrollView scrollview;
	boolean isShow = false; // 更多用户名是否展开
	private PopupWindow pop;
	private PopupAdapter adapter;
	private ListView listView;
	private List<MoreLoginUser> names = new ArrayList<MoreLoginUser>();
	String str_username, str_password;
	GDLocation location;
	private int _oldh = -1;
	private boolean isOpenEdit = false;

	@OrmLiteDao(helper = DBHelper.class, model = MoreLoginUser.class)
	public Dao<MoreLoginUser, Integer> moreLoginUserDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		getSupportActionBar().setTitle(R.string.login);
		// forgetpassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
		setListener();
		location = new GDLocation(getApplicationContext(), null, true);

		crl.setOnSizeChangedListener(new OnSizeChangedListener() {

			@Override
			public void close() {
				new Handler().post(new Runnable() {
					public void run() {
						scrollview.scrollTo(0, 0);
						getSupportActionBar().show();
					}
				});

			}

			@Override
			public void open(int height) {
				new Handler().post(new Runnable() {
					public void run() {
						scrollview.scrollTo(0, scrollview.getBottom());
						getSupportActionBar().hide();
					}
				});

			}
		});
		getSupportActionBar().setBackgroundDrawable(null);

	}

	public void setListener() {
		username.addTextChangedListener(usernameTW);
		password.addTextChangedListener(passwordTW);
	}

	@Click(R.id.regBtn)
	public void reg(View v) {
		RegFirst_.intent(this).start();
	}

	@Click
	public void delete_username_btn(View v) {
		username.setText("");
	}

	@Click
	public void delete_password_btn(View v) {
		password.setText("");
	}

	@Click
	public void more_username_btn(View v) {
		if (!isShow) {
			isShow = true;
			initUserNamePop();
		} else {
			isShow = false;
			initUserNamePop();
		}
	}

	/**
	 * 用户名输入框的输入事件
	 */
	TextWatcher usernameTW = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.toString().length() > 0) {
				delete_username_btn.setVisibility(View.VISIBLE);
			} else {
				delete_username_btn.setVisibility(View.GONE);
			}
			try {
				QueryBuilder<UserTable, String> qb = userTableDao
						.queryBuilder();
				qb.where().eq("mobile", s.toString());
				List<UserTable> uts = userTableDao.query(qb.prepare());
				if (uts.size() > 0) {
					UserTable ut = uts.get(0);
					ImageLoader.getInstance().displayImage(
							ut.getUser().getAvatar() + StaticFactory._320x320,
							userlogo,ac.options_userlogo);
				} else {
					userlogo.setImageResource(R.drawable.defalut_logo);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	};
	TextWatcher passwordTW = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.toString().length() > 0) {
				delete_password_btn.setVisibility(View.VISIBLE);
			} else {
				delete_password_btn.setVisibility(View.GONE);
			}
		}
	};

	class PopupAdapter extends ParentsAdapter {

		public PopupAdapter(Context c, List list) {
			super(c, list);
		}

		public View getView(final int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			final String name = ((MoreLoginUser) list.get(position))
					.getUsername();
			final String pass = ((MoreLoginUser) list.get(position))
					.getPassword();
			if (convertView == null) {
				convertView = View.inflate(c, R.layout.username_popup, null);
				holder = new ViewHolder();
				holder.tv = (TextView) convertView.findViewById(R.id.more_user);
				holder.iv = (ImageView) convertView
						.findViewById(R.id.more_clear);
				holder.ll = (LinearLayout) convertView
						.findViewById(R.id.more_user_ll);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv.setText(name);
			holder.ll.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					initUserNamePop();
					username.setText(name);
					password.setText(pass);
				}
			});

			holder.iv.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					names.remove(position);
					try {
						moreLoginUserDao.delete(moreLoginUserDao
								.deleteBuilder().where().eq("username", name)
								.prepareDelete());
					} catch (SQLException e) {
						e.printStackTrace();
					}
					adapter.notifyDataSetChanged();
					if (names.size() == 0) {
						initUserNamePop();
					}
				}
			});
			if (position == (list.size() - 1)) {
				holder.ll.setBackgroundResource(R.drawable.input_btm_btn);
			} else {
				holder.ll.setBackgroundResource(R.drawable.input_mid_btn);
			}
			return convertView;
		}

	}

	static class ViewHolder {
		TextView tv;
		ImageView iv;
		LinearLayout ll;
	}

	public void initUserNamePop() {
		try {
			if (pop == null) {
				if (adapter == null) {
					names = moreLoginUserDao.query(moreLoginUserDao
							.queryBuilder().orderBy("id", false).prepare());
					adapter = new PopupAdapter(getApplicationContext(), names);
					listView = new ListView(LoginActivity.this);
					int width = username_ll.getWidth();
					pop = new PopupWindow(listView, width,
							LayoutParams.WRAP_CONTENT);
					pop.setOutsideTouchable(true);
					listView.setItemsCanFocus(false);
					listView.setDivider(null);
					listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					listView.setAdapter(adapter);
					pop.setFocusable(false);
					pop.showAsDropDown(username_ll);
					isShow = true;
					animate(more_username_btn).setDuration(200).rotation(180)
							.start();
				}
			} else if (pop.isShowing()) {
				pop.dismiss();
				isShow = false;
				animate(more_username_btn).setDuration(200).rotation(0).start();
			} else if (!pop.isShowing()) {
				names = moreLoginUserDao.query(moreLoginUserDao.queryBuilder()
						.orderBy("id", false).prepare());
				adapter.setList(names);
				adapter.notifyDataSetChanged();
				pop.showAsDropDown(username_ll);
				isShow = true;
				animate(more_username_btn).setDuration(200).rotation(180)
						.start();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (isShow && pop != null) {
				initUserNamePop();
				return true;
			}
		}
		return super.onTouchEvent(event);
	}

	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (isShow && pop != null) {
			initUserNamePop();
			return;
		}
		super.onBackPressed();
	}

	ValueAnimator anim, anim1, anim2, anim3;

	@Click(R.id.loginBtn)
	public void login(View v) {

		str_username = username.getText().toString().trim();
		str_password = password.getText().toString().trim();

		if (!Util.isMobileNO(str_username)) {
			showCustomToast("请输入正确的用户名");
			return;
		} else if (!Util.isPassword(str_password)) {
			showCustomToast("请输入正确的密码");
			return;
		}

		closeInput();

		anim2 = ObjectAnimator.ofFloat(margin_ll, "alpha", 1, 0).setDuration(
				200);
		final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) userlogo_ll
				.getLayoutParams();
		int screenY = getResources().getDisplayMetrics().heightPixels / 2;
		screenY = (int) (screenY - lp.topMargin);
		screenY -= userlogo_ll.getHeight() / 2;

		anim3 = ObjectAnimator.ofInt(lp.topMargin, screenY).setDuration(200);

		anim3.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int n = (Integer) animation.getAnimatedValue();
				lp.topMargin = n;
				userlogo_ll.setLayoutParams(lp);
			}
		});

		final AnimatorSet set = new AnimatorSet();
		set.playTogether(anim2, anim3);
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				round_img1.setVisibility(View.VISIBLE);
				round_img2.setVisibility(View.VISIBLE);

				anim = ObjectAnimator.ofFloat(0f, 360f).setDuration(1000);
				anim.setInterpolator(new LinearInterpolator());
				anim.setRepeatCount(ValueAnimator.INFINITE);
				anim.addUpdateListener(round_img1);
				anim.setRepeatMode(ValueAnimator.RESTART);

				anim1 = ObjectAnimator.ofFloat(360f, 0f).setDuration(1000);
				anim1.setInterpolator(new LinearInterpolator());
				anim1.setRepeatCount(ValueAnimator.INFINITE);
				anim1.addUpdateListener(round_img2);
				anim1.setRepeatMode(ValueAnimator.RESTART);
				anim.start();
				anim1.start();
				margin_ll.clearAnimation();
				margin_ll.setVisibility(View.GONE);

				RequestParams ap = getAjaxParams();
				ap.put("mobile", str_username);
				ap.put("pwd", str_password);
				ap.put("cityid", String.valueOf(ac.cs.getLocationID()));
				ap.put("longitude", ac.cs.getLng());
				ap.put("latitude", ac.cs.getLat());
				ap.put("area", ac.cs.getArea());
				ap.put("dtype", "0");
				ap.put("deviceid", ac.cs.getDeviceId());

				ac.finalHttp.post(URL.LOGIN, ap, callBack);
			}
		});
		set.start();

	}

	@UiThread
	void stopAnimate() {
		round_img1.clearAnimation();
		round_img2.clearAnimation();
		round_img1.setVisibility(View.GONE);
		round_img2.setVisibility(View.GONE);
		if (anim2 != null)
			anim2.reverse();
		if (anim3 != null)
			anim3.reverse();
		if (anim != null)
			anim.cancel();
		if (anim1 != null)
			anim1.cancel();
	}

	JsonHttpResponseHandler callBack = new JsonHttpResponseHandler() {
		public void onSuccess(JSONObject jo) {
			try {
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					// 登陆记录
					moreLoginUserDao.delete(moreLoginUserDao.deleteBuilder()
							.where().eq("username", str_username)
							.prepareDelete());
					// .deleteByWhere(MoreLoginUser.class, "username='"
					// + str_username + "'");
					moreLoginUserDao.create(new MoreLoginUser(str_username,
							str_password));

					User u = new Gson().fromJson(jo.getString(URL.RESPONSE),
							User.class);
					LoginUser lu = new LoginUser(u.getId(), str_username,
							str_password, u.getToken());

					// 保存用户
					userTableDao.deleteById(u.getId());
					userTableDao.create(new UserTable(u));

					// 保存登陆用户
					TableUtils.clearTable(getConnectionSource(),
							LoginUser.class);
					loginUserDao.create(lu);

					ac.startServices();

					finish();
					break;
				default:
					showCustomToast(jo.getJSONObject(URL.RESPONSE).getString(
							URL.INFO));
					margin_ll.clearAnimation();
					margin_ll.setVisibility(View.VISIBLE);
					stopAnimate();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		public void onStart() {

		};

		public void onFailure() {
			margin_ll.clearAnimation();
			margin_ll.setVisibility(View.VISIBLE);
			stopAnimate();
			showIntentErrorToast();
		};
	};

	protected void onDestroy() {
		super.onDestroy();
		location.destory();
	};

	@Click(R.id.forgetpassword)
	public void findpassword(View v) {
		ForgetPassWordActivity1_.intent(this).start();
	}

	@Override
	public void onRefreshStarted(View view) {

	}

}
