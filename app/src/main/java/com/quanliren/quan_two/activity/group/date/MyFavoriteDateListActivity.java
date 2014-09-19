package com.quanliren.quan_two.activity.group.date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.adapter.DateAdapter;
import com.quanliren.quan_two.adapter.DateAdapter.IDateAdapterListener;
import com.quanliren.quan_two.adapter.QuanAdapter.IQuanAdapter;
import com.quanliren.quan_two.bean.CacheBean;
import com.quanliren.quan_two.bean.DateBean;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.pull.PullToRefreshLayout;
import com.quanliren.quan_two.pull.XListView;
import com.quanliren.quan_two.pull.XListView.IXListViewListener;
import com.quanliren.quan_two.pull.lib.ActionBarPullToRefresh;
import com.quanliren.quan_two.pull.lib.listeners.OnRefreshListener;
import com.quanliren.quan_two.util.URL;
import com.quanliren.quan_two.util.http.JsonHttpResponseHandler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EActivity(R.layout.personaldongtai)
public class MyFavoriteDateListActivity extends BaseActivity implements IXListViewListener, IQuanAdapter, OnRefreshListener, IDateAdapterListener {

    public static final String TAG = "MyFavoriteDateListActivity";

    private static final int DETAIL = 3;
    public String CACHEKEY = "";
    @ViewById
    XListView listview;
    @ViewById
    PullToRefreshLayout layout;
    DateAdapter adapter;
    View mView;
    int p = 0;
    RequestParams ap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        CACHEKEY = ac.getLoginUserId() + TAG;
        getSupportActionBar().setTitle("我的收藏");
    }

    @AfterViews
    void initView() {
        initAdapter();
        ActionBarPullToRefresh.from(this).setAutoStart(true).allChildrenArePullable().listener(this).setup(layout);
    }

    public void initAdapter() {
        try {
            List<User> list = new ArrayList<User>();
            CacheBean cb = cacheDao.queryForId(CACHEKEY);
            if (cb != null) {
                list = new Gson().fromJson(cb.getValue(),
                        new TypeToken<ArrayList<DateBean>>() {
                        }.getType());
            }
            adapter = new DateAdapter(this, list, this);
            adapter.setDlistener(this);
            listview.setAdapter(adapter);
            listview.setXListViewListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoadMore() {
        ap.put("p", p + "");
        ac.finalHttp.post(URL.DATE_MY_FAVORITE, ap, callBack);
    }

    JsonHttpResponseHandler callBack = new JsonHttpResponseHandler() {

        public void onFailure() {
            showIntentErrorToast();
            listview.stop();
            layout.setRefreshComplete();
        }

        ;

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
                layout.setRefreshComplete();
            }
        }

        ;
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
        layout.setRefreshing(true, true);
    }

    @OnActivityResult(DETAIL)
    void onDetailResult(int result, Intent data) {
        if (result == 4) {
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
        } else if (result == 5) {
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
            if (position == -1) {
                adapter.addFirstItem(bean);
                adapter.notifyDataSetChanged();
            }
        }
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
    public void detailClick(Object bean) {
        DateDetailActivity_.intent(this).bean((DateBean) bean).startForResult(DETAIL);
    }

    @Override
    public void onRefreshStarted(View view) {
        p = 0;
        ap = getAjaxParams();
        onLoadMore();
    }

    @Override
    public void longClick(final Object bean) {
        AlertDialog dialog = new AlertDialog.Builder(this).setItems(new String[]{"删除收藏"}, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancleFavorite((DateBean) bean);
            }
        }).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }

    void cancleFavorite(final DateBean bean) {
        RequestParams rp = getAjaxParams();
        rp.put("dtid", bean.getDtid());
        rp.put("type", 1);
        ac.finalHttp.post(URL.DATE_COLLECT, rp, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                customShowDialog("正在发送请求");
            }

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    int status = response.getInt(URL.STATUS);
                    switch (status) {
                        case 0:
                            showCustomToast("删除成功");
                            Intent i = new Intent();
                            i.putExtra("bean", (DateBean) bean);
                            onDetailResult(2, i);
                            break;
                        default:
                            showFailInfo(response);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    customDismissDialog();
                }
            }

            @Override
            public void onFailure() {
                customDismissDialog();
                showIntentErrorToast();
            }
        });
    }
}