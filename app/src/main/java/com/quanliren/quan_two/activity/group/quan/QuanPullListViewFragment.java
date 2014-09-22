package com.quanliren.quan_two.activity.group.quan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.quanliren.quan_two.activity.group.DongTaiDetailActivity_;
import com.quanliren.quan_two.activity.group.PublishActivity_;
import com.quanliren.quan_two.adapter.QuanAdapter;
import com.quanliren.quan_two.adapter.QuanAdapter.IQuanAdapter;
import com.quanliren.quan_two.bean.CacheBean;
import com.quanliren.quan_two.bean.DongTaiBean;
import com.quanliren.quan_two.bean.LoginUser;
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
@OptionsMenu(R.menu.quan)
public class QuanPullListViewFragment extends MenuFragmentBase implements
        LoaderImpl, IXListViewListener, IQuanAdapter {

    public static final String TAG = "QuanPullListViewFragment";
    public String CACHEKEY = "";
    @ViewById
    XXListView listview;
    QuanAdapter adapter;
    View mView;
    int type, p = 0;
    RequestParams ap;
    public static final int ALL = 1;
    public static final int MYCARE = 2;

//    @ViewById
//    LineToMenu empty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        type = getArguments().getInt("type");
        super.onCreate(savedInstanceState);
        LoginUser user = getHelper().getUser();
        if (user == null) {
            CACHEKEY = TAG + type;
        } else {
            CACHEKEY = user.getId() + TAG + type;
        }
    }

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
            List<User> list = new ArrayList<User>();
            CacheBean cb = cacheDao.queryForId(CACHEKEY);
            if (cb != null) {
                list = new Gson().fromJson(cb.getValue(),
                        new TypeToken<ArrayList<DongTaiBean>>() {
                        }.getType());
            }
            View view = new View(getActivity());
            view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, ImageUtil.dip2px(getActivity(), 50)));
            listview.addFooterView(view);
            adapter = new QuanAdapter(getActivity(), list, this);

//            listview.setEmptyView(empty);

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

    @OnActivityResult(1)
    void onPublishResult(int result, Intent data) {
            if (result == 1) {
                refere();
            } else if (result == 2) {
                DongTaiBean bean = (DongTaiBean) data
                        .getSerializableExtra("bean");
                List<DongTaiBean> beans = adapter.getList();
                int position = -1;
                for (DongTaiBean b : beans) {
                    if (b.getDyid().equals(bean.getDyid())) {
                        position = beans.indexOf(b);
                        break;
                    }
                }
                if (position != -1)
                    deleteAnimate(position);
            }
    }

    @Override
    public void onLoadMore() {
        String url = URL.DONGTAI;
        switch (type) {
            case MYCARE:
                url = URL.DONGTAI_FRIEND;
                break;
        }
        ap.put("p", p + "");
        ac.finalHttp.post(url, ap, callBack);
    }

    JsonHttpResponseHandler callBack = new JsonHttpResponseHandler() {

        public void onFailure() {
            showIntentErrorToast();
            listview.stop();
        }

        ;

        public void onSuccess(JSONObject jo) {
            try {
                int status = jo.getInt(URL.STATUS);
                switch (status) {
                    case 0:
                        jo = jo.getJSONObject(URL.RESPONSE);
                        List<DongTaiBean> list = new Gson().fromJson(
                                jo.getString(URL.LIST),
                                new TypeToken<ArrayList<DongTaiBean>>() {
                                }.getType());
                        if (p == 0) {
                            LoginUser user = getHelper().getUser();
                            if (user == null) {
                                CACHEKEY = TAG + type;
                            } else {
                                CACHEKEY = user.getId() + TAG + type;
                            }
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
//            empty.setVisibility(View.GONE);
//            listview.setVisibility(View.VISIBLE);
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
    public void detailClick(Object bean) {
        DongTaiDetailActivity_.intent(this).bean((DongTaiBean) bean)
                .startForResult(1);
    }

    @Override
    public void onRefresh() {
        ap = getAjaxParams();
        String url = URL.DONGTAI;
        switch (type) {
            case MYCARE:
                url = URL.DONGTAI_FRIEND;
                break;
            default:
                ap.put("latitude", ac.cs.getLat());
                ap.put("longitude", ac.cs.getLng());
                break;
        }
        p = 0;

        ap.put("p", p + "");
        ac.finalHttp.post(url, ap, callBack);

    }
}
