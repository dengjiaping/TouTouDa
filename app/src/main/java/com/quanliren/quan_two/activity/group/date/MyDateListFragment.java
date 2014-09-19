package com.quanliren.quan_two.activity.group.date;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;

import com.a.me.maxwin.view.XXListView;
import com.a.me.maxwin.view.XXListView.IXListViewListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.adapter.DateAdapter;
import com.quanliren.quan_two.adapter.QuanAdapter.IQuanAdapter;
import com.quanliren.quan_two.bean.CacheBean;
import com.quanliren.quan_two.bean.DateBean;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.fragment.base.MenuFragmentBase;
import com.quanliren.quan_two.fragment.impl.LoaderImpl;
import com.quanliren.quan_two.util.ImageUtil;
import com.quanliren.quan_two.util.URL;
import com.quanliren.quan_two.util.http.JsonHttpResponseHandler;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EFragment
@OptionsMenu(R.menu.date_nav_menu)
public class MyDateListFragment extends MenuFragmentBase implements
        IXListViewListener, LoaderImpl, IQuanAdapter {
    private static final String TAG = "MyDateListFragment";
    private String CACHEKEY = TAG;
    private static final int PUBLISH = 2;
    private static final int DETAIL = 3;
    int p = 0;
    @ViewById
    XXListView listview;
    DateAdapter adapter;
    RequestParams ap = null;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.quanpulllistview, null);
        } else {
            ViewParent parent = view.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(view);
            }
        }
        CACHEKEY += ac.getLoginUserId();
        return view;
    }

    @Override
    public void refresh() {
        if (getActivity() != null && init.compareAndSet(false, true)) {
            initAdapter();
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    public void initAdapter() {
        try {
            List<DateBean> list = new ArrayList<DateBean>();
            CacheBean cb = cacheDao.queryForId(CACHEKEY);
            if (cb != null) {
                list = new Gson().fromJson(cb.getValue(),
                        new TypeToken<ArrayList<DateBean>>() {
                        }.getType());
            }
            adapter = new DateAdapter(getActivity(), list, this);
            View view = new View(getActivity());
            view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, ImageUtil.dip2px(getActivity(), 50)));
            listview.addFooterView(view);
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


    @Override
    public void onLoadMore() {
        ap.put("p", p + "");
        ac.finalHttp.post(URL.MY_DATE_LIST, ap, callBack);
    }


    JsonHttpResponseHandler callBack = new JsonHttpResponseHandler() {
        @Override
        public void onFailure() {
            listview.stop();
        }

        public void onSuccess(JSONObject jo) {
            try {
                int status = jo.getInt(URL.STATUS);
                switch (status) {
                    case 0:
                        jo = jo.getJSONObject(URL.RESPONSE);
                        List<DateBean> list = new Gson().fromJson(
                                jo.getString(URL.LIST),
                                new TypeToken<ArrayList<DateBean>>() {
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
        }

        ;
    };


    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
        p = 0;
        ap = getAjaxParams();
        ap.put("p", p + "");
        ac.finalHttp.post(URL.MY_DATE_LIST, ap, callBack);
    }


    @OptionsItem
    void publish() {
        User user = getHelper().getUserInfo();
        if (user == null) {
            startLogin();
            return;
        }
        DatePublishActivity_.intent(this).startForResult(PUBLISH);
    }

    @OnActivityResult(PUBLISH)
    void onPublishResult(int result) {
        if (result == 1)
            listview.startRefresh();
    }

    @Override
    public void detailClick(Object bean) {
        DateDetailActivity_.intent(this).bean((DateBean) bean).startForResult(DETAIL);
    }

    @OnActivityResult(DETAIL)
    void onDetailResult(int result, Intent data) {
        if (result == 2) {
            DateBean bean = (DateBean) data
                    .getSerializableExtra("bean");
            List<DateBean> beans = adapter.getList();
            int position = -1;
            for (DateBean b : beans) {
                if (b.getDtid() == (bean.getDtid())) {
                    position = beans.indexOf(b);
                    break;
                }
            }
            if (position != -1)
                deleteAnimate(position);
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
                    listview.startRefresh();
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
}
