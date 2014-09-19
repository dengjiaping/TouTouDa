package com.quanliren.quan_two.activity.group;

import android.view.View;

import com.amap.api.maps2d.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.activity.user.UserInfoActivity_;
import com.quanliren.quan_two.activity.user.UserOtherInfoActivity_;
import com.quanliren.quan_two.activity.user.perf.FilterPerfs_;
import com.quanliren.quan_two.adapter.NearPeopleAdapter;
import com.quanliren.quan_two.bean.CacheBean;
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
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EActivity(R.layout.black_user)
public class ThroughListActivity extends BaseActivity implements
        OnRefreshListener, IXListViewListener {

    private static final String TAG = "ThroughListActivity";
    int p = 0;
    @ViewById
    XListView listview;
    NearPeopleAdapter adapter;
    RequestParams ap = null;
    @Extra
    LatLng ll;
    @ViewById
    PullToRefreshLayout layout;

    @Pref
    FilterPerfs_ perf;

    @AfterViews
    void inits() {
        initAdapter();

        ActionBarPullToRefresh.from(this).allChildrenArePullable().setAutoStart(true).listener(this).setup(layout);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    public void initAdapter() {
        try {
            List<User> list = new ArrayList<User>();
            CacheBean cb = cacheDao.queryForId(TAG);
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
        User user = (User) adapter.getItem(position);
        if (ac.getLoginUserId().equals(user.getId())) {
            UserInfoActivity_.intent(this).start();
        } else {
            UserOtherInfoActivity_.intent(this).userId(user.getId())
                    .start();
        }
    }

    @Override
    public void onLoadMore() {
        ap = getAjaxParams();
        ap.put("p", p + "");
        ap.put("longitude", ll.longitude);
        ap.put("latitude", ll.latitude);
        ac.finalHttp.post(URL.NearUserList, ap, callBack);
    }


    JsonHttpResponseHandler callBack = new JsonHttpResponseHandler() {
        @Override
        public void onFailure() {
            listview.stop();
            layout.setRefreshComplete();
            showIntentErrorToast();
        }

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
                            CacheBean cb = new CacheBean(TAG,
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


    public void onResume() {
        super.onResume();
        getSupportActionBar().setTitle("会员漫游");
    }


    @Override
    public void onRefreshStarted(View view) {
        p = 0;
        onLoadMore();
    }
}
