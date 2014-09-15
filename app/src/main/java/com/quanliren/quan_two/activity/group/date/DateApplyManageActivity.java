package com.quanliren.quan_two.activity.group.date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import com.a.dd.CircularProgressButton;
import com.a.loopj.android.http.JsonHttpResponseHandler;
import com.a.loopj.android.http.RequestParams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.activity.user.UserInfoActivity_;
import com.quanliren.quan_two.activity.user.UserOtherInfoActivity_;
import com.quanliren.quan_two.adapter.DateApplyManageAdapter;
import com.quanliren.quan_two.adapter.DateApplyManageAdapter.IDateAdapterListener;
import com.quanliren.quan_two.bean.DateBean;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.pull.PullToRefreshLayout;
import com.quanliren.quan_two.pull.XListView;
import com.quanliren.quan_two.pull.XListView.IXListViewListener;
import com.quanliren.quan_two.pull.lib.ActionBarPullToRefresh;
import com.quanliren.quan_two.pull.lib.listeners.OnRefreshListener;
import com.quanliren.quan_two.util.URL;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.black_user)
public class DateApplyManageActivity extends BaseActivity implements
		OnRefreshListener, IXListViewListener, IDateAdapterListener {

	private static final String TAG = "DateApplyManageActivity";

	@ViewById
	PullToRefreshLayout layout;
	@ViewById
	XListView listview;
	@Extra
	DateBean bean;
	int p = 0;
	RequestParams rp;
	DateApplyManageAdapter adapter;

	@AfterViews
	void initView() {
		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.setAutoStart(true).listener(this).setup(layout);
		adapter = new DateApplyManageAdapter(this, new ArrayList(), this);
		adapter.isFinish = bean.getDtstate() == 1 ? true : false;
		listview.setAdapter(adapter);
		listview.setXListViewListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getSupportActionBar().setTitle("报名管理");
	}

	@Override
	public void onLoadMore() {
		rp.put("p", p).put("dtid", bean.getDtid());
		ac.finalHttp.post(URL.DATE_APPLY_MANAGE, rp, callBack);
	}

	@Override
	public void onRefreshStarted(View view) {
		p = 0;
		rp = getAjaxParams();
		onLoadMore();
	}

	JsonHttpResponseHandler callBack = new JsonHttpResponseHandler() {
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
				layout.setRefreshComplete();
				listview.stop();
			}
		};

		public void onFailure() {
			layout.setRefreshComplete();
			listview.stop();
			showIntentErrorToast();
		};
	};

	@ItemClick
	void listview(int position) {
		User user = (User) adapter.getItem(position);
		if (ac.getLoginUserId().equals(user.getId())) {
			UserInfoActivity_.intent(this).start();
		} else {
			UserOtherInfoActivity_.intent(this).userId(user.getId()).start();
		}
	}

	CircularProgressButton agreeBtn;

	@Override
	public void agreeClick(final View v) {
		final User user = (User) v.getTag();
		new AlertDialog.Builder(this).setTitle("提示").setMessage("您确定要和"+user.getNickname()+"约会吗？").setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				adapter.selectUser=(User) v.getTag();
				adapter.notifyDataSetChanged();
				agreeBtn = (CircularProgressButton) v;
				if (agreeBtn.getProgress() == 0) {
					agreeBtn.setProgress(50);
					RequestParams rp = getAjaxParams();
					rp.put("dtid", bean.getDtid());
					rp.put("otherid", user.getId());
					ac.finalHttp.post(URL.DATE_CHOSE_SOMEONE, rp, new choseCallBack(
							user));
				}
			}
		}).create().show();;
	
	}

	@UiThread(delay = 500)
	void doSuccess() {
		agreeBtn.setProgress(100);
	}

	@UiThread(delay = 500)
	void doFail() {
		agreeBtn.setProgress(-1);
		doRstore();
	}

	@UiThread(delay = 1500)
	void doRstore() {
		agreeBtn.setProgress(0);
		adapter.selectUser = null;
		adapter.notifyDataSetChanged();
	}

	class choseCallBack extends JsonHttpResponseHandler {
		User user;

		public choseCallBack(User user) {
			this.user = user;
		}

		@Override
		public void onSuccess(JSONObject response) {
			try {
				int status = response.getInt(URL.STATUS);
				switch (status) {
				case 0:
					showCustomToast("已同意");
					setResult(1);
					adapter.isFinish=true;
					doSuccess();
					break;
				default:
					doFail();
					showFailInfo(response);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure() {
			doFail();
			showIntentErrorToast();
		}
	};
}
