package com.quanliren.quan_two.activity.group;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.activity.user.UserInfoActivity_;
import com.quanliren.quan_two.activity.user.UserOtherInfoActivity_;
import com.quanliren.quan_two.adapter.QuanDetailReplyAdapter;
import com.quanliren.quan_two.adapter.QuanDetailReplyAdapter.IQuanDetailReplyAdapter;
import com.quanliren.quan_two.adapter.QuanPicAdapter;
import com.quanliren.quan_two.bean.DongTaiBean;
import com.quanliren.quan_two.bean.DongTaiReplyBean;
import com.quanliren.quan_two.bean.LoginUser;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.custom.CustomRelativeLayout;
import com.quanliren.quan_two.custom.CustomRelativeLayout.OnSizeChangedListener;
import com.quanliren.quan_two.custom.CustomVip;
import com.quanliren.quan_two.custom.emoji.EmoteView;
import com.quanliren.quan_two.custom.emoji.EmoticonsEditText;
import com.quanliren.quan_two.pull.PullToRefreshLayout;
import com.quanliren.quan_two.pull.lib.ActionBarPullToRefresh;
import com.quanliren.quan_two.pull.lib.listeners.OnRefreshListener;
import com.quanliren.quan_two.util.ImageUtil;
import com.quanliren.quan_two.util.StaticFactory;
import com.quanliren.quan_two.util.URL;
import com.quanliren.quan_two.util.Util;
import com.quanliren.quan_two.util.http.JsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EActivity
@OptionsMenu(R.menu.quan_detail)
public class DongTaiDetailActivity extends BaseActivity implements
		IQuanDetailReplyAdapter, OnRefreshListener {
	public static final String TAG = "DongTaiDetailActivity";
	@ViewById
	ListView listview;
	@ViewById
	EmoticonsEditText reply_content;
	@ViewById
	View emoji_btn;
	@FragmentById(R.id.chat_eiv_inputview)
	EmoteView gridview;
	@ViewById
	View chat_layout_emote; 
	@ViewById
	Button send_btn;
    CustomVip vip;
	@Extra
	DongTaiBean bean;
	View headView;
	ImageView userlogo;
	QuanDetailReplyAdapter adapter;
	GridView gridView;
	QuanPicAdapter picadapter;
	TextView username, sex, time, signature, reply_btn, location;
	LayoutParams lp;
	View content_rl;
	int imgWidth;
	@ViewById
	View bottom_ll;
	RelativeLayout mHeaderViewContent;
	@ViewById
	CustomRelativeLayout crl;
	@ViewById
	PullToRefreshLayout layout;
	@OptionsMenuItem
	MenuItem delete;
	@Extra
	boolean init=true;
	// @OrmLiteDao(helper=DBHelper.class,model=DongTaiBeanTable.class)
	// Dao<DongTaiBeanTable, String> dongTaiBeanTableDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quan_detail);
		getSupportActionBar().setTitle("动态内容");

		listview.addHeaderView(headView = View.inflate(this,
				R.layout.quan_item, null));
		listview.addFooterView(new View(this));

		List<DongTaiReplyBean> list = new ArrayList<DongTaiReplyBean>();
		listview.setAdapter(adapter = new QuanDetailReplyAdapter(this, list,
				this));

		findheadview();

		ActionBarPullToRefresh.from(DongTaiDetailActivity.this)
				.setAutoStart(true).allChildrenArePullable().listener(this)
				.setup(layout);

		crl.setOnSizeChangedListener(new OnSizeChangedListener() {

			@Override
			public void open(int height) {
				setselection();
			}

			@Override
			public void close() {
			}
		});
		
		ViewHelper.setTranslationY(bottom_ll, ImageUtil.dip2px(this, 50));
	}

	@Extra
	DongTaiReplyBean selectBean;

	@UiThread
	void setselection() {
		if (selectBean != null){
			if(adapter.getList().indexOf(selectBean)==-1){
				List<DongTaiReplyBean> list=adapter.getList();
				boolean b=false;
				for (DongTaiReplyBean dongTaiReplyBean : list) {
					if(selectBean.getId()!=null&&selectBean.getId().equals(dongTaiReplyBean.getId())){
						selectBean=dongTaiReplyBean;
						b=true;
					}
				}
				if(b){
					listview.setSelection(adapter.getList().indexOf(selectBean) + 1);
				}
			}else{
				listview.setSelection(adapter.getList().indexOf(selectBean) + 1);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		isMy();
		return super.onCreateOptionsMenu(menu);
	}
	
	public void isMy(){
		if(delete!=null){
			if (bean.getUserid()!=null&&bean.getUserid().equals(ac.getLoginUserId())) {
				delete.setVisible(true);
			} else {
				delete.setVisible(false);
			}
		}
	}

	public void findheadview() {
		gridView = (GridView) headView.findViewById(R.id.pic_gridview);
		userlogo = (ImageView) headView.findViewById(R.id.userlogo);
		username = (TextView) headView.findViewById(R.id.nickname);
		sex = (TextView) headView.findViewById(R.id.sex);
		signature = (TextView) headView.findViewById(R.id.signature);
		time = (TextView) headView.findViewById(R.id.time);
		vip = (CustomVip) headView.findViewById(R.id.vip);
		imgWidth=(getResources().getDisplayMetrics().widthPixels-ImageUtil.dip2px(this, 88))/3;

		picadapter = new QuanPicAdapter(this, new ArrayList<String>(), imgWidth);
		gridView.setAdapter(picadapter);

		lp = new LayoutParams(
				LayoutParams.FILL_PARENT, imgWidth);
		lp.addRule(RelativeLayout.BELOW, R.id.signature);
		location = (TextView) headView.findViewById(R.id.location);
		reply_btn = (TextView) headView.findViewById(R.id.reply_btn);
		gridView.setLayoutParams(lp);
		content_rl = headView.findViewById(R.id.content_rl);

		content_rl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				contentClick(null);
			}
		});
		
		reply_content.setOnEditorActionListener(editListener);
		reply_content.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean b) {
				if (b) {
					chat_layout_emote.setVisibility(View.GONE);
				}
			}
		});
		reply_content.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				chat_layout_emote.setVisibility(View.GONE);
			}
		});

		gridview.setEditText(reply_content);
	}

	public void setHeadSource() {
		if (bean.getImglist() == null || bean.getImglist().size() == 0) {
			gridView.setVisibility(View.GONE);
		} else {
			gridView.setVisibility(View.VISIBLE);
			picadapter.setList(bean.getImglist());
			picadapter.notifyDataSetChanged();
			lp=(LayoutParams) gridView.getLayoutParams();
			lp.height=imgWidth*Util.getLines(bean.getImglist().size(), 3);
			int num=(bean.getImglist().size()>3?3:bean.getImglist().size());
			int lpwidth=((num-1)*ImageUtil.dip2px(this, 4))+num*imgWidth;
			lp.width=lpwidth;
			gridView.setNumColumns(num);
			gridView.setLayoutParams(lp);
		}
		
		
		ImageLoader.getInstance().displayImage(
				bean.getAvatar() + StaticFactory._160x160, userlogo,
				ac.options_userlogo);
		username.setText(bean.getNickname());
		time.setText(Util.getTimeDateStr(bean.getCtime()));
		if (bean.getContent().trim().length() > 0) {
			signature.setVisibility(View.VISIBLE);
			signature.setText(bean.getContent());
		} else {
			signature.setVisibility(View.GONE);
		}
		switch (Integer.valueOf(bean.getSex())) {
		case 0:
			sex.setBackgroundResource(R.drawable.girl_icon);
			break;
		case 1:
			sex.setBackgroundResource(R.drawable.boy_icon);
			break;
		default:
			break;
		}
		sex.setText(bean.getAge());

		userlogo.setOnClickListener(userlogoClick);
		location.setText(bean.getArea().equals("")?"火星":bean.getArea());
		reply_btn.setText(bean.getCnum());

		if (bean.getIsvip() > 0) {
			vip.setVisibility(View.VISIBLE);
            vip.setVip(bean.getIsvip());
			username.setTextColor(getResources().getColor(R.color.vip_name));
		} else {
			vip.setVisibility(View.GONE);
		}
		isMy();
		adapter.setList(bean.getCommlist());
		adapter.notifyDataSetChanged();
	}

	OnClickListener userlogoClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(DongTaiDetailActivity.this, bean.getUserid()
					.equals(ac.getLoginUserId()) ? UserInfoActivity_.class
					: UserOtherInfoActivity_.class);
			i.putExtra("userId", bean.getUserid());
			startActivity(i);
		}
	};

	@OptionsItem(R.id.delete)
	public void rightClick() {
		new AlertDialog.Builder(this).setTitle("提示").setMessage("您确定要删除这条动态吗？")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						RequestParams ap = getAjaxParams();
						ap.put("dyid", bean.getDyid() + "");
						ac.finalHttp.post(URL.DELETE_DONGTAI, ap,
								new JsonHttpResponseHandler() {
									@Override
									public void onStart() {
										customShowDialog("正在删除");
									}

									@Override
									public void onSuccess(JSONObject jo) {
										customDismissDialog();
										try {
											int status = jo.getInt(URL.STATUS);
											switch (status) {
											case 0:
												showCustomToast("删除成功");
												Intent i = new Intent();
												i.putExtra("bean", bean);
												setResult(2, i);
												finish();
												break;
											default:
												showFailInfo(jo);
												break;
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}

									@Override
									public void onFailure() {
										customDismissDialog();
										showIntentErrorToast();
									}
								});
					}
				}).create().show();
	};

	JsonHttpResponseHandler callBack = new JsonHttpResponseHandler() {

		public void onFailure() {
			layout.setRefreshComplete();
			showIntentErrorToast();
		};

		public void onSuccess(JSONObject jo) {
			try {
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					bean = new Gson().fromJson(jo.getString(URL.RESPONSE),
							new TypeToken<DongTaiBean>() {
							}.getType());
					
					crl.setVisibility(View.VISIBLE);
					setHeadSource();
					ViewPropertyAnimator.animate(bottom_ll).translationY(0);
					if(selectBean!=null){
						contentClick(selectBean);
					}
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				layout.setRefreshComplete();
			}
		};
	};

	@Click
	void send_btn() {
		if(getHelper().getUser()==null){
			startLogin();
			return;
		}
		
		String content = reply_content.getText().toString().trim();
		if (content.length() == 0) {
			showCustomToast("请输入内容");
			return;
		}
		RequestParams ap = getAjaxParams();
		ap.put("dyid", bean.getDyid() + "");ap.put("content", content);

		DongTaiReplyBean replayBean = new DongTaiReplyBean();

		Object obj = reply_content.getTag();
		if (obj != null) {
			DongTaiReplyBean rb = (DongTaiReplyBean) obj;
			ap.put("replyuid", rb.getUserid());
			replayBean.setReplyuid(rb.getUserid());
			replayBean.setReplyuname(rb.getNickname());
		}
		replayBean.setContent(content);
		User user = getHelper().getUserInfo();
		replayBean.setAvatar(user.getAvatar());
		replayBean.setNickname(user.getNickname());
		replayBean.setUserid(user.getId());
		replayBean.setCtime(Util.fmtDateTime.format(new Date()));

		ac.finalHttp.post(URL.REPLY_DONGTAI, ap, new replyCallBack(replayBean));

		reply_content.clearFocus();
		reply_content.setText("");
		reply_content.setHint("");
		reply_content.setTag(null);
		closeInput();
		chat_layout_emote.setVisibility(View.GONE);
	}

	OnEditorActionListener editListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
			if (arg1 == EditorInfo.IME_ACTION_SEND) {
				send_btn();
			}
			return false;
		}
	};

	class replyCallBack extends JsonHttpResponseHandler {
		DongTaiReplyBean replayBean;

		public replyCallBack(DongTaiReplyBean replayBean) {
			this.replayBean = replayBean;
		}

		public void onStart() {
			bean.getCommlist().add(replayBean);
		};

		public void onSuccess(JSONObject jo) {
			try {
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					showCustomToast("回复成功");
					String id = jo.getJSONObject(URL.RESPONSE).getString("id");
					replayBean.setId(id);
					reply_btn.setText((Integer.valueOf(reply_btn.getText()
							.toString()) + 1) + "");
					selectBean=replayBean;
					setselection();
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		public void onFailure(Throwable t, int errorNo, String strMsg) {
			bean.getCommlist().remove(replayBean);
			adapter.notifyDataSetChanged();
		};
	}

	@Override
	public void contentClick(DongTaiReplyBean bean) {
		LoginUser user=getHelper().getUser();
		if(user==null){
			startLogin();
			return;
		}
		if (bean != null
				&& !bean.getUserid().equals(user.getId())) {
			reply_content.setTag(bean);
			reply_content.setHint("回复 " + bean.getNickname() + " :");
			selectBean = bean;
		} else {
			reply_content.setTag(null);
			reply_content.setHint("");
			selectBean = null;
		}

		showKeyBoard();
		reply_content.requestFocus();
	}

	@Override
	public void logoCick(DongTaiReplyBean bean) {
		Intent i = new Intent(this, bean.getUserid()
				.equals(ac.getLoginUserId()) ? UserInfoActivity_.class
				: UserOtherInfoActivity_.class);
		i.putExtra("userId", bean.getUserid());
		startActivity(i);
	}

	@Click(R.id.emoji_btn)
	public void add_emoji_btn(View v) {
		if (chat_layout_emote.getVisibility() == View.VISIBLE) {
			chat_layout_emote.setVisibility(View.GONE);
			reply_content.requestFocus();
			showKeyBoard();
		} else {
			crl.setHideView(chat_layout_emote);
			closeInput();
		}
	}

	public void onBackPressed() {
		if (chat_layout_emote.getVisibility() == View.VISIBLE) {
			reply_content.requestFocus();
			chat_layout_emote.setVisibility(View.GONE);
			showKeyBoard();
			return;
		}
		super.onBackPressed();
	}

	@Override
	public void onRefreshStarted(View view) {
        RequestParams rp=getAjaxParams();
        rp.put("dyid", bean.getDyid() + "");
		ac.finalHttp.post(URL.GETDONGTAI_DETAIL,
				rp, callBack);
	}

}
