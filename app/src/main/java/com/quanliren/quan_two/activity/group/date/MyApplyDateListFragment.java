package com.quanliren.quan_two.activity.group.date;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;

import com.a.loopj.android.http.JsonHttpResponseHandler;
import com.a.loopj.android.http.RequestParams;
import com.a.me.maxwin.view.XXListView;
import com.a.me.maxwin.view.XXListView.IXListViewListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.adapter.DateAdapter;
import com.quanliren.quan_two.adapter.QuanAdapter.IQuanAdapter;
import com.quanliren.quan_two.bean.CacheBean;
import com.quanliren.quan_two.bean.DateBean;
import com.quanliren.quan_two.fragment.base.MenuFragmentBase;
import com.quanliren.quan_two.fragment.impl.LoaderImpl;
import com.quanliren.quan_two.util.ImageUtil;
import com.quanliren.quan_two.util.URL;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EFragment
public class MyApplyDateListFragment extends MenuFragmentBase implements
		IXListViewListener, LoaderImpl,IQuanAdapter {
	private static final String TAG = "MyApplyDateListFragment";
	private String CACHEKEY=TAG;
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
		CACHEKEY+=ac.getLoginUserId();
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
			adapter = new DateAdapter(getActivity(), list,this);
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
		ac.finalHttp.post(URL.MY_APPLY_DATE_LIST, ap, callBack);
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
		};
	};


	public void onResume() {
		super.onResume();
	}

	@Override
	public void onRefresh() {
		p = 0;
		ap=getAjaxParams().put("p", p + "");
		ac.finalHttp.post(URL.MY_APPLY_DATE_LIST, ap, callBack);
	}
	

	@Override
	public void detailClick(Object bean) {
		DateDetailActivity_.intent(this).bean((DateBean)bean).startForResult(DETAIL);
	}
	
}
