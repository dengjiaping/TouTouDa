package com.quanliren.quan_two.activity.seting.emoticon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.a.dd.CircularProgressButton;
import com.quanliren.quan_two.util.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.activity.seting.EmoticonListActivity;
import com.quanliren.quan_two.adapter.EmoticonDownloadHistoryListAdapter;
import com.quanliren.quan_two.adapter.ShopAdapter.IBuyListener;
import com.quanliren.quan_two.bean.CacheBean;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.bean.emoticon.EmoticonActivityListBean;
import com.quanliren.quan_two.bean.emoticon.EmoticonActivityListBean.EmoticonZip;
import com.quanliren.quan_two.custom.ScrollViewPager;
import com.quanliren.quan_two.db.DBHelper;
import com.quanliren.quan_two.pull.PullToRefreshLayout;
import com.quanliren.quan_two.pull.lib.ActionBarPullToRefresh;
import com.quanliren.quan_two.pull.lib.listeners.OnRefreshListener;
import com.quanliren.quan_two.util.BroadcastUtil;
import com.quanliren.quan_two.util.URL;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.emoticonlist)
public class EmoticonHistoryListActivity extends BaseActivity implements
		OnRefreshListener, IBuyListener {

	public static final String TAG = "EmoticonHistoryListActivity";
	public String CACHEKEY = TAG;

	@ViewById
	PullToRefreshLayout layout;
	@ViewById
	ListView listview;

	ScrollViewPager viewpager;

	EmoticonDownloadHistoryListAdapter adapter;

	@OrmLiteDao(helper = DBHelper.class, model = EmoticonZip.class)
	Dao<EmoticonZip, Integer> emoticonDao;

	User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		user = getHelper().getUserInfo();
		CACHEKEY += user.getId();

	}

	@Receiver(actions=EmoticonListActivity.EMOTICONDOWNLOAD_PROGRESS)
	public void receiver(Intent i ){
		String action = i.getAction();
		if (action.equals(EmoticonListActivity.EMOTICONDOWNLOAD_PROGRESS)) {
			int state = i.getExtras().getInt("state");
			EmoticonZip bean = (EmoticonZip) i.getSerializableExtra("bean");

			List<EmoticonZip> list = adapter.getList();
			int position = -1;
			for (EmoticonZip emoticonZip : list) {
				if (emoticonZip.getId() == bean.getId()) {
					position = list.indexOf(emoticonZip);
				}
			}
			if (position < 0) {
				return;
			}
			CircularProgressButton progress = (CircularProgressButton) listview
					.getChildAt(position).findViewById(R.id.buy);
			switch (state) {
			case 0:
				progress.setIndeterminateProgressMode(true);
				progress.setProgress(50);
				break;
			case 1:
				int gress = i.getExtras().getInt("progress");
				int total = i.getExtras().getInt("total");
				int percent = (int) (((float) gress / (float) total) * 100);
				if (percent > 0 && percent < 100) {
					progress.setIndeterminateProgressMode(false);
					progress.setProgress(percent);
				}
				break;
			case 2:
				doSuccess(progress);
				break;
			case -1:
				doFail(progress);
				break;
			}

		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getSupportActionBar().setTitle("表情管理");
	}


	@AfterViews
	void initView() {
		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.setAutoStart(true).listener(this).setup(layout);

		try {
			EmoticonActivityListBean list = null;
			CacheBean cb = cacheDao.queryForId(CACHEKEY);
			if (cb != null) {
				list = new Gson().fromJson(cb.getValue(),
						new TypeToken<EmoticonActivityListBean>() {
						}.getType());
			}
			initView(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@ItemClick
	void listview(int position) {
		EmoticonDetailActivity_.intent(this)
				.bean(((EmoticonZip) adapter.getItem(position))).start();
	}

	@Override
	public void onRefreshStarted(View view) {
		ac.finalHttp.post(URL.EMOCTION_MANAGE, getAjaxParams(),
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject response) {
						try {
							int status = response.getInt(URL.STATUS);
							switch (status) {
							case 0:
								CacheBean cb = new CacheBean(CACHEKEY, response
										.getString(URL.RESPONSE), new Date()
										.getTime());
								cacheDao.delete(cb);
								cacheDao.create(cb);
								EmoticonActivityListBean list = new Gson().fromJson(
										response.getString(URL.RESPONSE),
										new TypeToken<EmoticonActivityListBean>() {
										}.getType());
								initView(list);
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

					@Override
					public void onFailure() {
						layout.setRefreshComplete();
						showIntentErrorToast();
					}
				});
	}

	public void buyClick(final CircularProgressButton progress) {
		final EmoticonZip ez = (EmoticonZip) progress.getTag();
		User user = getHelper().getUserInfo();
		if (user == null) {
			return;
		}
		if (ez.getType() == 1 && user.getIsvip() == 0) {// 会员
			goVip();
			return;
		}
		if (ez.getType() == 2 && ez.getIsBuy() == 0) {// 付费
			return;
		}

		if (ez.isHave()) {
			try {
				emoticonDao.delete(ez);
				ez.setHave(false);
				Intent i = new Intent(
						EmoticonListActivity.DELETE_EMOTICONDOWNLOAD);
				i.putExtra("id", ez.getId());
				sendBroadcast(i);
				adapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			boolean isExists = false;
			try {
				EmoticonZip ezb = emoticonDao.queryForId(ez.getId());
				if (ezb != null && ezb.getUserId().equals(ac.getLoginUserId())) {
					isExists = true;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				if ((ez.getType() == 0 || ez.getIsBuy() == 1 || (ez.getType() == 1 && user
						.getIsvip() > 0)) && !isExists) {
					Intent i = new Intent(BroadcastUtil.DOWNLOADEMOTICON);
					i.putExtra("bean", ez);
					startService(i);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@UiThread(delay = 500)
	void doSuccess(CircularProgressButton mProgress) {
		if (mProgress != null) {
			mProgress.setmCompleteText("完成");
			mProgress.setProgress(100);
			doSuccess1(mProgress);
		}
	}

	@UiThread(delay = 1000)
	void doSuccess1(CircularProgressButton mProgress) {
		EmoticonZip ez = (EmoticonZip) mProgress.getTag();
		ez.setHave(true);
		adapter.notifyDataSetChanged();
	}

	@UiThread(delay = 500)
	void doFail(CircularProgressButton mProgress) {
		mProgress.setProgress(-1);
		doRstoref(mProgress);
	}

	@UiThread(delay = 1500)
	void doRstoref(CircularProgressButton mProgress) {
		mProgress.setProgress(0);
	}

	void initView(EmoticonActivityListBean bean) {
		if (bean == null) {
			return;
		}

		if (bean.getPlist() != null) {
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId", ac.getLoginUserId());
				List<EmoticonZip> list = emoticonDao.queryForFieldValues(map);

				for (EmoticonZip ez : bean.getPlist()) {
					for (EmoticonZip emoticonZip : list) {
						if (ez.getId() == emoticonZip.getId()) {
							ez.setHave(true);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (adapter == null) {
				adapter = new EmoticonDownloadHistoryListAdapter(this,
						bean.getPlist(), this);
				OnScrollListener listener = new PauseOnScrollListener(
						ImageLoader.getInstance(), false, true);
				listview.setOnScrollListener(listener);
				listview.setAdapter(adapter);
			} else {
				adapter.setList(bean.getPlist());
				adapter.notifyDataSetChanged();
			}
		}
	}

}
