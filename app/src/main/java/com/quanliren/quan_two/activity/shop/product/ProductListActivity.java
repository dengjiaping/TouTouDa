package com.quanliren.quan_two.activity.shop.product;

import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.adapter.ProductGridAdapter.IProductGridListener;
import com.quanliren.quan_two.adapter.ProductListAdapter;
import com.quanliren.quan_two.bean.CacheBean;
import com.quanliren.quan_two.bean.ProductBean;
import com.quanliren.quan_two.bean.ProductListBean;
import com.quanliren.quan_two.pull.PullToRefreshLayout;
import com.quanliren.quan_two.pull.lib.ActionBarPullToRefresh;
import com.quanliren.quan_two.pull.lib.listeners.OnRefreshListener;
import com.quanliren.quan_two.util.URL;
import com.quanliren.quan_two.util.http.JsonHttpResponseHandler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EActivity(R.layout.product_list)
public class ProductListActivity extends BaseActivity implements OnRefreshListener, IProductGridListener {

    private static final String TAG = "ProductListActivity";

    @ViewById
    PullToRefreshLayout layout;
    @ViewById
    ListView listview;

    ProductListAdapter adapter;

    @AfterViews
    void initView() {
        try {
            List<ProductListBean> list = new ArrayList<ProductListBean>();
            CacheBean cb = cacheDao.queryForId(TAG);
            if (cb != null) {
                list = new Gson().fromJson(cb.getValue(), new TypeToken<ArrayList<ProductListBean>>() {
                }.getType());
            }
            adapter = new ProductListAdapter(this, list);
            listview.setAdapter(adapter);

            ActionBarPullToRefresh.from(this).allChildrenArePullable().setAutoStart(true).listener(this).setup(layout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle("靓点兑换");
    }

    @Override
    public void onRefreshStarted(View view) {
        ac.finalHttp.post(URL.PRODUCT_LIST, getAjaxParams(), new JsonHttpResponseHandler() {
            @Override
            public void onFailure() {
                layout.setRefreshComplete();
                showIntentErrorToast();
            }

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    int status = response.getInt(URL.STATUS);
                    switch (status) {
                        case 0:
                            String str = response.getString(URL.RESPONSE);
                            CacheBean cb = new CacheBean(TAG, str, new Date().getTime());
                            cacheDao.deleteById(TAG);
                            cacheDao.create(cb);
                            List<ProductListBean> list = new Gson().fromJson(response.getString(URL.RESPONSE), new TypeToken<ArrayList<ProductListBean>>() {
                            }.getType());
                            adapter.setList(list);
                            adapter.notifyDataSetChanged();
                            break;
                        default:
                            showFailInfo(response);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    layout.setRefreshComplete();
                }
            }
        });
    }

    @Override
    public void imgClick(View view) {
        ProductBean bean = (ProductBean) view.getTag();
        ProductDetailActivity_.intent(this).bean(bean).start();
    }

}
