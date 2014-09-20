package com.quanliren.quan_two.fragment.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.a.me.maxwin.view.XXListView;
import com.a.me.maxwin.view.XXListView.IXListViewListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.user.*;
import com.quanliren.quan_two.adapter.NearPeopleAdapter;
import com.quanliren.quan_two.bean.CacheBean;
import com.quanliren.quan_two.bean.LoginUser;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.fragment.base.MenuFragmentBase;
import com.quanliren.quan_two.fragment.impl.LoaderImpl;
import com.quanliren.quan_two.util.URL;
import com.quanliren.quan_two.util.http.JsonHttpResponseHandler;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EFragment
public class MyCareGragment extends MenuFragmentBase implements IXListViewListener, LoaderImpl {

    public static final String TAG = "MyCareGragment";

    @ViewById
    XXListView listview;
    NearPeopleAdapter adapter;
    int p = 0;
    LoginUser user;
    int type = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getHelper().getUser();
        type = getArguments().getInt("type");
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.my_care_list, null);
        } else {
            ViewParent parent = view.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(view);
            }
        }
        return view;
    }


    public void initAdapter() {
        try {
            CacheBean cb = cacheDao.queryForId(TAG + user.getId() + type);
            List<User> users = new ArrayList<User>();
            if (cb != null) {
                users = new Gson().fromJson(cb.getValue(), new TypeToken<ArrayList<User>>() {
                }.getType());
            }
            adapter = new NearPeopleAdapter(getActivity(), users);
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
        if (position > 0 && position <= adapter.getCount()) {
            User user = (User) adapter.getItem(position - 1);
            Intent i = new Intent(getActivity(), user.getId().equals(getHelper().getUser().getId()) ? UserInfoActivity_.class : UserOtherInfoActivity_.class);
            i.putExtra("userId", user.getId());
            startActivity(i);
        }
    }

    @Override
    public void onRefresh() {
        p = 0;
        onLoadMore();
    }

    @Override
    public void onLoadMore() {
        RequestParams ap = getAjaxParams("p", "0");
        ap.put("type", type + "");
        ac.finalHttp.post(URL.CONCERNLIST, ap, callBack);
    }

    JsonHttpResponseHandler callBack = new JsonHttpResponseHandler() {
        public void onSuccess(JSONObject jo) {
            try {
                int status = jo.getInt(URL.STATUS);
                switch (status) {
                    case 0:
                        JSONObject response = jo.getJSONObject(URL.RESPONSE);
                        String list = response.getString(URL.LIST);
                        List<User> users = new Gson().fromJson(list, new TypeToken<ArrayList<User>>() {
                        }.getType());
                        if (p == 0) {
                            cacheDao.deleteById(TAG + user.getId() + type);
                            cacheDao.create(new CacheBean(TAG + user.getId() + type, list, new Date().getTime()));
                            adapter.setList(users);
                        } else {
                            adapter.addNewsItems(users);
                        }
                        adapter.notifyDataSetChanged();
                        listview.setPage(p = response.getInt(URL.PAGEINDEX));
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

        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            listview.stopRefresh();
        }

        ;
    };

    @Override
    public void refresh() {
        if (getActivity() != null && init.compareAndSet(false, true)) {
            initAdapter();
        }
    }

}
