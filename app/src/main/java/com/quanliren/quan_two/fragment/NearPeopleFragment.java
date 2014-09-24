package com.quanliren.quan_two.fragment;

import android.view.View;

import com.a.me.maxwin.view.XXListView;
import com.a.me.maxwin.view.XXListView.IXListViewListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_two.activity.PropertiesActivity.ITitle;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.location.GDLocation;
import com.quanliren.quan_two.activity.location.ILocationImpl;
import com.quanliren.quan_two.activity.user.FilterNearPeopleActivity_;
import com.quanliren.quan_two.activity.user.UserInfoActivity_;
import com.quanliren.quan_two.activity.user.UserOtherInfoActivity_;
import com.quanliren.quan_two.activity.user.perf.FilterPerfs_;
import com.quanliren.quan_two.adapter.NearPeopleAdapter;
import com.quanliren.quan_two.bean.CacheBean;
import com.quanliren.quan_two.bean.CustomFilterBean;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.db.DBHelper;
import com.quanliren.quan_two.fragment.base.MenuFragmentBase;
import com.quanliren.quan_two.util.URL;
import com.quanliren.quan_two.util.http.JsonHttpResponseHandler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EFragment(R.layout.near_people)
@OptionsMenu(R.menu.near_people_menu)
public class NearPeopleFragment extends MenuFragmentBase implements
        IXListViewListener, ILocationImpl, ITitle {
    private static final int FILTER = 1;
    private static final String TAG = "NearPeopleFragment";
    int p = 0;
    @ViewById
    XXListView listview;
    NearPeopleAdapter adapter;
    RequestParams ap = null;
    GDLocation location;
    @OrmLiteDao(helper = DBHelper.class, model = CustomFilterBean.class)
    Dao<CustomFilterBean, String> customFilterBeanDao;
    @ViewById
    View empty;
    @Pref
    FilterPerfs_ perf;

    @AfterViews
    void inits() {

        try {
            CustomFilterBean cfb = customFilterBeanDao.queryForId("activetype");
            if (cfb == null) {
                customFilterBeanDao
                        .create(new CustomFilterBean("activetype", 0));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        initAdapter();
        setListener();
        location = new GDLocation(getActivity().getApplicationContext(), this, false);

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        location.destory();
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
            adapter = new NearPeopleAdapter(getActivity(), list);

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

    public void setListener() {

    }

    @ItemClick
    void listview(int position) {
        if (position > 0) {
            User user = (User) adapter.getItem(position - 1);
            if (ac.getLoginUserId().equals(user.getId())) {
                UserInfoActivity_.intent(this).start();
            } else {
                UserOtherInfoActivity_.intent(this).userId(user.getId())
                        .start();
            }
        }
    }

    @Override
    public void onLoadMore() {
        onLocationSuccess();
    }

    public void initParam() {
        try {
            if (p == 0) {
                List<CustomFilterBean> listCB = customFilterBeanDao
                        .queryForAll();
                ap = getAjaxParams();

                if (listCB != null)
                    for (CustomFilterBean cfb : listCB) {
                        if ((cfb.getKey().equals("sex") && cfb.id > -1) || cfb.getKey().equals("activetype"))
                            ap.put(cfb.key, cfb.id + "");
                        else if (cfb.id > 0) {
                            ap.put(cfb.key, cfb.id - 1 + "");
                        }
                    }

                ap.put("longitude", ac.cs.getLng());
                ap.put("latitude", ac.cs.getLat());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    JsonHttpResponseHandler callBack = new JsonHttpResponseHandler() {
        @Override
        public void onFailure() {
            listview.stop();
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
                            cacheDao.deleteById(TAG);
                            cacheDao.create(cb);
                            adapter.setList(list);
                        } else {
                            adapter.addNewsItems(list);
                        }
                        adapter.notifyDataSetChanged();
                        listview.setPage(p = jo.getInt(URL.PAGEINDEX));

                        if(adapter.getCount()==0){
                            empty.setVisibility(View.VISIBLE);
                            listview.setVisibility(View.GONE);
                        }
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

        @Override
        public void onStart() {
            empty.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onLocationSuccess() {
        initParam();
        ap.put("p", p + "");
        ac.finalHttp.post(URL.NearUserList, ap, callBack);
    }

    @Override
    public void onLocationFail() {
        showCustomToast("定位失败");
        listview.stop();
    }

    ;

    public void onResume() {
        super.onResume();
    }

    @Override
    public String getTitle() {
        return "附近";
    }

    @OptionsItem(R.id.fillter)
    public void startFilter() {
        FilterNearPeopleActivity_.intent(this).startForResult(FILTER);
    }

    @OnActivityResult(FILTER)
    void onFilterResult(int result) {
        if (result == 1) {
            empty.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
            listview.startRefresh();
        }
    }

    @Override
    public void onRefresh() {
        p = 0;
        location.startLocation();
    }
}
