package com.quanliren.quan_two.activity.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.a.loopj.android.http.JsonHttpResponseHandler;
import com.a.loopj.android.http.RequestParams;
import com.a.nineoldandroids.animation.Animator;
import com.a.nineoldandroids.animation.AnimatorListenerAdapter;
import com.a.nineoldandroids.animation.ValueAnimator;
import com.a.nineoldandroids.view.ViewHelper;
import com.a.nineoldandroids.view.ViewPropertyAnimator;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.adapter.NearPeopleAdapter;
import com.quanliren.quan_two.bean.CacheBean;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.pull.PullToRefreshLayout;
import com.quanliren.quan_two.pull.XListView;
import com.quanliren.quan_two.pull.XListView.IXListViewListener;
import com.quanliren.quan_two.pull.lib.ActionBarPullToRefresh;
import com.quanliren.quan_two.pull.lib.listeners.OnRefreshListener;
import com.quanliren.quan_two.util.URL;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EActivity
public class MyVisitActivity extends BaseActivity implements
		IXListViewListener, OnRefreshListener {
	private static final String TAG = "MyVisitActivity";
	private String CACHEKEY = "";
	int p = 0;
	@ViewById
	XListView listview;
	@ViewById
	PullToRefreshLayout layout;
	NearPeopleAdapter adapter;
	RequestParams ap = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.black_user);
		CACHEKEY = TAG + ac.getLoginUserId();
		initAdapter();
		getSupportActionBar().setTitle("访客记录");
		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.setAutoStart(true).listener(this).setup(layout);
	}

	public void initAdapter() {
		List<User> list = new ArrayList<User>();
		try {
			CacheBean cb = cacheDao.queryForId(CACHEKEY);
			if (cb != null) {
				list = new Gson().fromJson(cb.getValue(),
						new TypeToken<ArrayList<User>>() {
						}.getType());
			}
			adapter = new NearPeopleAdapter(this, list);
			listview.setAdapter(adapter);
			listview.setXListViewListener(this);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@ItemClick
	void listview(int position) {
		if (position <= adapter.getCount()) {
			User user = (User) adapter.getItem(position);
			Intent i = new Intent(MyVisitActivity.this, user.getId().equals(
					ac.getLoginUserId()) ? UserInfoActivity_.class
					: UserOtherInfoActivity_.class);
			i.putExtra("userId", user.getId());
			startActivity(i);
		}
	}

	@Override
	public void onLoadMore() {
		onLocationSuccess();
	}

	public void initParam() {
		if (p == 0) {
			ap = getAjaxParams();
			ap.put("p", p + "");
		}
	}

	JsonHttpResponseHandler callBack = new JsonHttpResponseHandler() {
		public void onFailure() {
			listview.stop();
			layout.setRefreshComplete();
		};

		public void onSuccess(JSONObject jo) {
			try {
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					jo = jo.getJSONObject(URL.RESPONSE);
					List<User> list = new Gson().fromJson(
							jo.getString(URL.LIST),
							new TypeToken<ArrayList<User>>() {
							}.getType());
					if (p == 0) {
						CacheBean cb = new CacheBean(CACHEKEY,
								jo.getString(URL.LIST), new Date().getTime());
						cacheDao.deleteById(CACHEKEY);
						cacheDao.create(cb);
						adapter.setList(list);
					} else {
						adapter.addNewsItems(list);
					}
					adapter.notifyDataSetChanged();
					listview.setPage(p = jo.getInt(URL.PAGEINDEX));
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				listview.stop();
				layout.setRefreshComplete();
			}
		};
	};

	public void onLocationSuccess() {
		initParam();
		ap.put("p", p + "");
		ac.finalHttp.post(URL.VISITLIST, ap, callBack);
	}

	@Override
	public void onRefreshStarted(View view) {
		p = 0;
		onLocationSuccess();
	}
	
	@ItemLongClick(R.id.listview)
	void listviewlong(final int position) {
		AlertDialog dialog=new AlertDialog.Builder(this)
				.setItems(new String[] { "删除这条记录" },
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								menuClick(position);
							}
						}).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	public void menuClick(int position) {
		RequestParams ap = getAjaxParams();
		ap.put("uvid", ((User) adapter.getItem(position)).getUvid());
		ac.finalHttp.post(URL.DELETE_VISITLIST, ap, new setLogoCallBack(position));
	}

	class setLogoCallBack extends JsonHttpResponseHandler {

		int position;

		public setLogoCallBack(int position) {
			this.position = position;
		}

		public void onStart() {
			customShowDialog("正在发送请求");
		};

		public void onFailure() {
			customDismissDialog();
			showIntentErrorToast();
		};

		public void onSuccess(JSONObject jo) {
			customDismissDialog();
			try {
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					deleteAnimate(position);
					showCustomToast("删除成功");
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};

	private void performDismiss(final View dismissView, final int position) {
		final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();// 获取item的布局参数
		final int originalHeight = dismissView.getHeight();// item的高度

		ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0)
				.setDuration(200);
		animator.start();

		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				adapter.removeObj(position);
				// 这段代码很重要，因为我们并没有将item从ListView中移除，而是将item的高度设置为0
				// 所以我们在动画执行完毕之后将item设置回来
				ViewHelper.setAlpha(dismissView, 1f);
				ViewHelper.setTranslationX(dismissView, 0);
				ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
				lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
				dismissView.setLayoutParams(lp);

				adapter.notifyDataSetChanged();

				if (adapter.getCount() == 0) {
					layout.setRefreshing(true, true);
				}
			}
		});

		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				// 这段代码的效果是ListView删除某item之后，其他的item向上滑动的效果
				lp.height = (Integer) valueAnimator.getAnimatedValue();
				dismissView.setLayoutParams(lp);
			}
		});

	}

	public void deleteAnimate(final int position) {
		final View view = listview.getChildAt((position)
				- listview.getFirstVisiblePosition());
		if (view != null) {
			ViewPropertyAnimator.animate(view).alpha(0).setDuration(200)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							performDismiss(view, position);
						}
					});

		} else {
			adapter.removeObj(position);
			adapter.notifyDataSetChanged();
		}
	}
}