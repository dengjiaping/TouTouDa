package com.quanliren.quan_two.activity.shop.product;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
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
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.Where;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.adapter.MyProAdapter;
import com.quanliren.quan_two.bean.CacheBean;
import com.quanliren.quan_two.bean.ExchangeRemindBean;
import com.quanliren.quan_two.bean.ProductBean;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.db.DBHelper;
import com.quanliren.quan_two.pull.PullToRefreshLayout;
import com.quanliren.quan_two.pull.XListView;
import com.quanliren.quan_two.pull.XListView.IXListViewListener;
import com.quanliren.quan_two.pull.lib.ActionBarPullToRefresh;
import com.quanliren.quan_two.pull.lib.listeners.OnRefreshListener;
import com.quanliren.quan_two.util.URL;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EActivity
public class MyExchangeListActivity extends BaseActivity implements
		IXListViewListener, OnRefreshListener {
	private static final String TAG = "MyExchangeListActivity";
	private String CACHEKEY = "";
	int p = 0;
	@ViewById
	XListView listview;
	@ViewById
	PullToRefreshLayout layout;
	MyProAdapter adapter;
	RequestParams ap = null;
	
	@OrmLiteDao(helper = DBHelper.class, model = ExchangeRemindBean.class)
	Dao<ExchangeRemindBean, String> exchangeDao;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.black_user);
		CACHEKEY = TAG + getHelper().getUser().getId();
		initAdapter();
		getSupportActionBar().setTitle("我兑换的");
		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.setAutoStart(true).listener(this).setup(layout);
		
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).
		cancel(("MyExchangeListActivity__通知").hashCode());
	}

	public void initAdapter() {
		List<User> list = new ArrayList<User>();
		try {
			CacheBean cb = cacheDao.queryForId(CACHEKEY);
			if (cb != null) {
				list = new Gson().fromJson(cb.getValue(),
						new TypeToken<ArrayList<ProductBean>>() {
						}.getType());
			}
			adapter = new MyProAdapter(this, list);
			listview.setAdapter(adapter);
			listview.setXListViewListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@ItemClick
	void listview(int position) {
		if (position <= adapter.getCount()) {
			ProductBean bean = (ProductBean) adapter.getItem(position);
			ProductDetailActivity_.intent(this).bean(bean).start();
		}
	}

	@ItemLongClick(R.id.listview)
	void listviewlong(final int position) {
		AlertDialog dialog = new AlertDialog.Builder(this).setItems(
				new String[] { "删除兑换记录" },
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						menuClick(position);
					}
				}).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	public void menuClick(int position) {
		RequestParams ap = getAjaxParams();
		ProductBean pb=((ProductBean) adapter.getItem(position));
		if(pb.getStatus().equals("0")){
			showCustomToast("兑换正在处理中，不能删除");
			return;
		}
		ap.put("eaid", ((ProductBean) adapter.getItem(position)).getEaid());
		ac.finalHttp.post(URL.DELETEMYEXCHANGE, ap, new setLogoCallBack(position));
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

	@Override
	public void onLoadMore() {
		ap=getAjaxParams();
		ap.put("p", p + "");
		ac.finalHttp.post(URL.MYEXCHANGELIST, ap, callBack);
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
							new TypeToken<ArrayList<ProductBean>>() {
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
					
					DeleteBuilder<ExchangeRemindBean, String> builder= exchangeDao.deleteBuilder();
					Where where =builder.where().eq("userId", getHelper().getUser().getId());
					builder.delete();
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


	@Override
	public void onRefreshStarted(View view) {
		p = 0;
		onLoadMore();
	}

}
