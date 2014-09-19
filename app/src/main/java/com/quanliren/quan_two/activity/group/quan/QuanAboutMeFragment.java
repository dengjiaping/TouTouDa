package com.quanliren.quan_two.activity.group.quan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.quanliren.quan_two.util.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.a.me.maxwin.view.XXListView;
import com.a.me.maxwin.view.XXListView.IXListViewListener;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.group.DongTaiDetailActivity_;
import com.quanliren.quan_two.activity.group.PublishActivity_;
import com.quanliren.quan_two.activity.user.UserInfoActivity_;
import com.quanliren.quan_two.activity.user.UserOtherInfoActivity_;
import com.quanliren.quan_two.adapter.QuanDetailReplyAdapter.IQuanDetailReplyAdapter;
import com.quanliren.quan_two.adapter.QuanReplyAdapter;
import com.quanliren.quan_two.bean.CacheBean;
import com.quanliren.quan_two.bean.DongTaiBean;
import com.quanliren.quan_two.bean.DongTaiReplyBean;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.fragment.base.MenuFragmentBase;
import com.quanliren.quan_two.fragment.impl.LoaderImpl;
import com.quanliren.quan_two.util.ImageUtil;
import com.quanliren.quan_two.util.URL;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EFragment
public class QuanAboutMeFragment extends MenuFragmentBase implements
		LoaderImpl, IXListViewListener, IQuanDetailReplyAdapter {

	public static final String TAG = "QuanPullListViewFragment";
	public String CACHEKEY =TAG;
	@ViewById
	XXListView listview;
	QuanReplyAdapter adapter;
	View mView;
	int p = 0;
	RequestParams ap;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView != null) {
			ViewGroup parent = (ViewGroup) mView.getParent();
			if (parent != null) {
				parent.removeView(mView);
			}
		} else {
			mView = inflater.inflate(R.layout.quanpulllistview, null);
		}
		return mView;
	}

	public void initAdapter() {
		try {
			CACHEKEY+=ac.getLoginUserId();
			List<User> list = new ArrayList<User>();
			CacheBean cb = cacheDao.queryForId(CACHEKEY);
			if (cb != null) {
				list = new Gson().fromJson(cb.getValue(),
						new TypeToken<ArrayList<DongTaiReplyBean>>() {
						}.getType());
			}
			View view = new View(getActivity());
			view.setLayoutParams(new AbsListView.LayoutParams(
					AbsListView.LayoutParams.FILL_PARENT, ImageUtil.dip2px(
							getActivity(), 50)));
			listview.addFooterView(view);
			adapter = new QuanReplyAdapter(getActivity(), list, this);
			listview.setAdapter(adapter);
			listview.setXListViewListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void refresh() {
		if (getActivity() != null && init.compareAndSet(false, true)) {
			initAdapter();
		}
	}

	@OptionsItem
	void publish() {
		if (getHelper().getUser() == null) {
			startLogin();
			return;
		}
		PublishActivity_.intent(this).startForResult(1);
	}


	@Override
	public void onLoadMore() {
		String url = URL.COMMENT_ME;
		ap.put("p", p + "");
		ac.finalHttp.post(url, ap, callBack);
	}

	JsonHttpResponseHandler callBack = new JsonHttpResponseHandler() {

		public void onFailure() {
			showIntentErrorToast();
			listview.stop();
		};

		public void onSuccess(JSONObject jo) {
			try {
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					jo = jo.getJSONObject(URL.RESPONSE);
					List<DongTaiReplyBean> list = new Gson().fromJson(
							jo.getString(URL.LIST),
							new TypeToken<ArrayList<DongTaiReplyBean>>() {
							}.getType());
					if (p == 0) {
						CacheBean cb = new CacheBean(CACHEKEY,
								jo.getString(URL.LIST), new Date().getTime());
						cacheDao.delete(cb);
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
			}
		};
	};

	private void performDismiss(final View dismissView, final int position) {
		final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
		final int originalHeight = dismissView.getHeight();

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
					refere();
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

	public void refere() {
		if (getActivity() != null) {
			listview.startRefresh();
		}
	}

	public void deleteAnimate(final int position) {
		final View view = listview.getChildAt((position + 1)
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
	public void onRefresh() {
		ap = getAjaxParams();
		p = 0;
		onLoadMore();
	}

	@Override
	public void contentClick(DongTaiReplyBean bean) {
		DongTaiBean dbean=new DongTaiBean();
		dbean.setDyid(bean.getDyid());
		DongTaiDetailActivity_.intent(this).bean(dbean).selectBean(bean).init(false).start();
	}

	@Override
	public void logoCick(DongTaiReplyBean bean) {
		Intent i = new Intent(getActivity(), bean.getUserid()
				.equals(ac.getLoginUserId()) ? UserInfoActivity_.class
				: UserOtherInfoActivity_.class);
		i.putExtra("userId", bean.getUserid());
		startActivity(i);		
	}
}