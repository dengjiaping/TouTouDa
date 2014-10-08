package com.quanliren.quan_two.activity.group.date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.a.dd.CircularProgressButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.activity.user.*;
import com.quanliren.quan_two.adapter.QuanDetailReplyAdapter;
import com.quanliren.quan_two.adapter.QuanDetailReplyAdapter.IQuanDetailReplyAdapter;
import com.quanliren.quan_two.bean.DateBean;
import com.quanliren.quan_two.bean.DongTaiReplyBean;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.custom.CustomRelativeLayout;
import com.quanliren.quan_two.custom.CustomRelativeLayout.OnSizeChangedListener;
import com.quanliren.quan_two.custom.StateTextViewBg;
import com.quanliren.quan_two.custom.UserNickNameRelativeLayout;
import com.quanliren.quan_two.custom.emoji.EmoteView;
import com.quanliren.quan_two.custom.emoji.EmoticonsEditText;
import com.quanliren.quan_two.pull.PullToRefreshLayout;
import com.quanliren.quan_two.pull.XListView;
import com.quanliren.quan_two.pull.XListView.IXListViewListener;
import com.quanliren.quan_two.pull.lib.ActionBarPullToRefresh;
import com.quanliren.quan_two.pull.lib.listeners.OnRefreshListener;
import com.quanliren.quan_two.util.ImageUtil;
import com.quanliren.quan_two.util.StaticFactory;
import com.quanliren.quan_two.util.URL;
import com.quanliren.quan_two.util.Util;
import com.quanliren.quan_two.util.http.JsonHttpResponseHandler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@EActivity(R.layout.date_detail)
@OptionsMenu(R.menu.quan_detail)
public class DateDetailActivity extends BaseActivity implements
        OnRefreshListener, IXListViewListener, IQuanDetailReplyAdapter {

    private static final int MANAGE = 1;

    @ViewById
    TextView collect_txt;
    @ViewById
    View bottom_ll;
    @ViewById
    PullToRefreshLayout layout;
    @OptionsMenuItem
    MenuItem delete;
    @Extra
    DateBean bean;
    @ViewById
    XListView listview;
    QuanDetailReplyAdapter adapter;
    View convertView;
    @ViewById
    CircularProgressButton join;
    @ViewById
    View favorite;
    @ViewById
    View reply;
    @ViewById
    View reply_ll;
    @ViewById
    EmoticonsEditText reply_content;
    @ViewById
    Button send_btn;
    @ViewById
    View emoji_btn;
    @FragmentById(R.id.chat_eiv_inputview)
    EmoteView gridview;
    @ViewById
    View chat_layout_emote;
    @ViewById
    CustomRelativeLayout crl;
    @ViewById
    View manage;
    @Extra
    DongTaiReplyBean selectBean;

    @AfterViews
    void initView() {
        join.setIndeterminateProgressMode(true);

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
        reply_ll.setVisibility(View.GONE);
        ViewHelper.setTranslationY(reply_ll, ImageUtil.dip2px(this, 50));

        View view = new View(this);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.FILL_PARENT,
                ImageUtil.dip2px(this, 58));
        view.setLayoutParams(lp);

        listview.addHeaderView(convertView = View.inflate(this,
                R.layout.date_item, null));

        listview.addFooterView(view);
        listview.setAdapter(adapter = new QuanDetailReplyAdapter(this,
                new ArrayList(), this));

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

        initPull();
    }

    @Click
    void send_btn() {
        String content = Util.FilterEmoji(reply_content.getText().toString()
                .trim());
        if (content.length() == 0) {
            showCustomToast("请输入内容");
            return;
        }
        RequestParams ap = getAjaxParams();
        ap.put("dtid", bean.getDtid() + "");
        ap.put("content", content);

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

        ac.finalHttp.post(URL.REPLY_DATE, ap, new replyCallBack(replayBean));

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
            adapter.notifyDataSetChanged();
        }

        ;

        public void onSuccess(JSONObject jo) {
            try {
                int status = jo.getInt(URL.STATUS);
                switch (status) {
                    case 0:
                        showCustomToast("回复成功");
                        String id = jo.getJSONObject(URL.RESPONSE).getString("id");
                        replayBean.setId(id);
                        bean.setCnum(bean.getCnum() + 1);
                        selectBean = replayBean;
                        setselection();
                        setHeadSource(bean);
                        break;
                    default:
                        showFailInfo(jo);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ;

        public void onFailure(Throwable t, int errorNo, String strMsg) {
            bean.getCommlist().remove(replayBean);
            adapter.notifyDataSetChanged();
        }

        ;
    }

    @UiThread(delay = 500)
    void initPull() {
        ActionBarPullToRefresh.from(this).allChildrenArePullable()
                .listener(this).setAutoStart(true).setup(layout);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getSupportActionBar().setTitle("偷偷约");
    }

    @Override
    public void onRefreshStarted(View view) {
        RequestParams rp = getAjaxParams();
        rp.put("dtid", bean.getDtid());
        ac.finalHttp.post(URL.DATE_DETAIL, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    int status = response.getInt(URL.STATUS);
                    switch (status) {
                        case 0:
                            DateBean db = new Gson().fromJson(
                                    response.getString(URL.RESPONSE),
                                    new TypeToken<DateBean>() {
                                    }.getType());
                            if (db != null) {
                                bean = db;
                            }
                            crl.setVisibility(View.VISIBLE);
                            setHeadSource(db);
                            ViewPropertyAnimator.animate(bottom_ll).translationY(0);
                            if (selectBean != null) {
                                contentClick(selectBean);
                            }
                            break;
                        default:
                            showFailInfo(response);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    layout.setRefreshComplete();
                    listview.stop();
                }
            }

            @Override
            public void onFailure() {
                layout.setRefreshComplete();
                listview.stop();
                showIntentErrorToast();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (bean.getUserid().equals(ac.getLoginUserId())) {
            delete.setVisible(true);
            join.setVisibility(View.GONE);
            favorite.setVisibility(View.GONE);
            manage.setVisibility(View.VISIBLE);
        } else {
            delete.setVisible(false);
            join.setVisibility(View.VISIBLE);
            favorite.setVisibility(View.VISIBLE);
            manage.setVisibility(View.GONE);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onLoadMore() {

    }

    class ViewHolder {
        ImageView userlogo;
        TextView nickname, sex, location_tv, coin, place_tv, people_num_tv,
                aim_tv, sex_tv, money_tv, time_tv, remark_tv, bm_people_num,
                reply_tv;
        StateTextViewBg state;
        View vip, people_num_ll, aim_ll, reply_ll, content_rl;
        UserNickNameRelativeLayout nick_ll;
    }

    public void setHeadSource(DateBean db) {
        ViewHolder holder;
        if (convertView.getTag() == null) {
            holder = new ViewHolder();
            holder.userlogo = (ImageView) convertView
                    .findViewById(R.id.userlogo);
            holder.nickname = (TextView) convertView
                    .findViewById(R.id.nickname);
            holder.sex = (TextView) convertView.findViewById(R.id.sex);
            holder.location_tv = (TextView) convertView
                    .findViewById(R.id.location_tv);
            holder.coin = (TextView) convertView.findViewById(R.id.coin_tv);
            holder.place_tv = (TextView) convertView
                    .findViewById(R.id.place_tv);
            holder.place_tv.setSingleLine(false);
            holder.people_num_tv = (TextView) convertView
                    .findViewById(R.id.people_num_tv);
            holder.sex_tv = (TextView) convertView.findViewById(R.id.sex_tv);
            holder.money_tv = (TextView) convertView
                    .findViewById(R.id.money_tv);
            holder.time_tv = (TextView) convertView.findViewById(R.id.time_tv);
            holder.remark_tv = (TextView) convertView
                    .findViewById(R.id.remark_tv);
            holder.remark_tv.setSingleLine(false);
            holder.aim_tv = (TextView) convertView.findViewById(R.id.aim_tv);
            holder.bm_people_num = (TextView) convertView
                    .findViewById(R.id.bm_people_num);
            holder.reply_tv = (TextView) convertView
                    .findViewById(R.id.reply_tv);
            holder.state = (StateTextViewBg) convertView
                    .findViewById(R.id.state);
            holder.vip = convertView.findViewById(R.id.vip);
            holder.people_num_ll = convertView.findViewById(R.id.people_num_ll);
            holder.aim_ll = convertView.findViewById(R.id.aim_ll);
            holder.reply_ll = convertView.findViewById(R.id.reply_ll);
            holder.content_rl = convertView.findViewById(R.id.content_rl);
            holder.nick_ll = (UserNickNameRelativeLayout) convertView
                    .findViewById(R.id.nick_ll);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.content_rl.setOnClickListener(content);
        ImageLoader.getInstance().displayImage(
                db.getAvatar() + StaticFactory._160x160, holder.userlogo);

        holder.nick_ll.setUser(db.getNickname(), db.getSex(), db.getAge(),
                db.getIsvip());

        holder.userlogo.setOnClickListener(userlogo);
        holder.state.setState(db.getDtype());

        if (Integer.valueOf(db.getCnum()) <= 0) {
            holder.reply_ll.setVisibility(View.GONE);
        } else {
            holder.reply_ll.setVisibility(View.VISIBLE);
            holder.reply_tv.setText(db.getCnum() + "");
        }
        if (db.getApplynum() > 0) {
            holder.bm_people_num.setText("已有" + db.getApplynum() + "人报名");
        } else {
            holder.bm_people_num.setText("");
        }

        if (db.getDistance() != null && !"".equals(db.getDistance())) {
            holder.location_tv.setText(Util.getDistance(db.getDistance()));
        } else if (db.getLatitude() != 0 && db.getLongitude() != 0
                && !ac.cs.getLat().equals("")) {
            holder.location_tv.setText(Util.getDistance(
                    Double.valueOf(ac.cs.getLng()),
                    Double.valueOf(ac.cs.getLat()), db.getLongitude(),
                    db.getLatitude())
                    + "km");
        }

        switch (db.getCtype()) {
            case 0:
                holder.coin.setVisibility(View.GONE);
                break;
            case 2:
                holder.coin.setVisibility(View.VISIBLE);
                holder.coin
                        .setText(Html
                                .fromHtml("<font color=\"#95948f\">赠送靓点：</font><font color=\"#228ada\">"
                                        + db.getCoin() + "</font>"));
                break;
            case 1:
                holder.coin.setVisibility(View.VISIBLE);
                holder.coin
                        .setText(Html
                                .fromHtml("<font color=\"#95948f\">我要靓点：</font><font color=\"#228ada\">"
                                        + db.getCoin() + "</font>"));
                break;
        }

        if (db.getDtype() == 5) {
            holder.aim_ll.setVisibility(View.VISIBLE);
            holder.aim_tv.setText(db.getAim() + "");
            holder.people_num_ll.setVisibility(View.GONE);
        } else {
            holder.aim_ll.setVisibility(View.GONE);
            holder.people_num_ll.setVisibility(View.VISIBLE);
            holder.people_num_tv.setText(Html
                    .fromHtml("<font color=\"#228ada\">" + db.getPeoplenum()
                            + "</font>" + "人"));
        }

        holder.place_tv.setText(db.getAddress());
        holder.sex_tv.setText(Html.fromHtml("<font color=\"#228ada\">"
                + db.getObjsex() + "</font>"));
        holder.money_tv.setText(Html.fromHtml("<font color=\"#228ada\">"
                + db.getWhopay() + "</font>"));
        holder.time_tv.setText(Html.fromHtml("<font color=\"#228ada\">"
                + db.getDtime() + "</font>"));
        holder.remark_tv.setText(db.getRemark());

        if (db.getDtstate() == 1 || db.getIsapply() == 1) {
            if (db.getDtstate() == 1) {
                join.setmEnableText("已结束");
            } else if (db.getIsapply() == 1) {
                join.setmEnableText("已报名");
            }
            join.setProgress(-2);
        }

        if (db.getIscollect() == 1) {
            collect_txt.setText("已收藏");
        } else {
            collect_txt.setText("收藏");
        }

        adapter.setList(db.getCommlist());
        adapter.notifyDataSetChanged();
    }

    OnClickListener content = new OnClickListener() {

        @Override
        public void onClick(View v) {
            contentClick(null);
        }
    };

    OnClickListener userlogo = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(DateDetailActivity.this, bean.getUserid()
                    .equals(ac.getLoginUserId()) ? UserInfoActivity_.class
                    : UserOtherInfoActivity_.class);
            i.putExtra("userId", bean.getUserid());
            startActivity(i);
        }
    };

    @Click
    void join() {
        if (getHelper().getUser() == null) {
            startLogin();
            return;
        }
        if (bean.getUserid().equals(ac.getLoginUserId())) {
            showCustomToast("这是自己的哟~");
            return;
        }
        if (join.getProgress() == 0) {
            join.setProgress(50);
            RequestParams rp = getAjaxParams();
            rp.put("dtid", bean.getDtid());
            ac.finalHttp.post(URL.DATE_APPLY, rp,
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            try {
                                int status = response.getInt(URL.STATUS);
                                switch (status) {
                                    case 0:
                                        bean.setApplynum(bean.getApplynum() + 1);
                                        ((ViewHolder) (convertView.getTag())).bm_people_num
                                                .setText("已有" + bean.getApplynum()
                                                        + "人报名");
                                        doSuccess();
                                        break;
                                    default:
                                        showFailInfo(response);
                                        doFail();
                                        break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure() {
                            doFail();
                        }
                    });
        }
    }

    @UiThread(delay = 500)
    void doSuccess() {
        join.setProgress(100);
        doRstore();
    }

    @UiThread(delay = 500)
    void doFail() {
        join.setProgress(-1);
        doRstoref();
    }

    @UiThread(delay = 1500)
    void doRstoref() {
        join.setProgress(0);
    }

    @UiThread(delay = 1500)
    void doRstore() {
        join.setmEnableText("已报名");
        join.setProgress(-2);
    }

    @Click
    void favorite() {
        if (getHelper().getUser() == null) {
            startLogin();
            return;
        }
        if (bean.getUserid().equals(ac.getLoginUserId())) {
            showCustomToast("这是自己的哟~");
            return;
        }
        RequestParams rp = getAjaxParams();
        rp.put("dtid", bean.getDtid());
        rp.put("type", bean.getIscollect());
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
                            if (bean.getIscollect() == 0) {
                                bean.setIscollect(1);
                                collect_txt.setText("已收藏");
                                showCustomToast("收藏成功");
                                Intent i = new Intent();
                                i.putExtra("bean", bean);
                                setResult(5, i);
                            } else {
                                bean.setIscollect(0);
                                collect_txt.setText("收藏");
                                showCustomToast("取消收藏成功");
                                Intent i = new Intent();
                                i.putExtra("bean", bean);
                                setResult(4, i);
                            }
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

    AtomicBoolean show = new AtomicBoolean(false);

    @Click
    void reply() {
        if (getHelper().getUser() == null) {
            startLogin();
            return;
        }
        selectBean = null;
        if (show.compareAndSet(false, true)) {
            ViewPropertyAnimator.animate(reply_ll).translationY(0)
                    .setListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationStart(Animator animation) {
                            reply_ll.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            reply_content.requestFocus();
                            showKeyBoard();
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        if (chat_layout_emote.getVisibility() == View.VISIBLE) {
            reply_content.requestFocus();
            chat_layout_emote.setVisibility(View.GONE);
            showKeyBoard();
            return;
        } else if (show.compareAndSet(true, false)) {
            ViewPropertyAnimator.animate(reply_ll)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            reply_ll.clearAnimation();
                            reply_ll.setVisibility(View.GONE);
                        }
                    }).translationY(ImageUtil.dip2px(this, 50));
            return;
        } else {
            super.onBackPressed();
        }
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

    @Override
    public void contentClick(DongTaiReplyBean bean) {
        if (getHelper().getUser() == null) {
            startLogin();
            return;
        }
        if (show.compareAndSet(false, true)) {
            ViewPropertyAnimator.animate(reply_ll).translationY(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            reply_ll.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            reply_content.requestFocus();
                            if (!crl.isOpen)
                                showKeyBoard();
                        }
                    });
        } else {
            reply_content.requestFocus();
            if (!crl.isOpen)
                showKeyBoard();
        }
        if (bean != null
                && !bean.getUserid().equals(getHelper().getUser().getId())) {
            reply_content.setTag(bean);
            reply_content.setHint("回复 " + bean.getNickname() + " :");
            selectBean = bean;
        } else {
            reply_content.setTag(null);
            reply_content.setHint("");
            selectBean = null;
        }
    }

    @UiThread
    void setselection() {
        if (selectBean != null) {
            if (adapter.getList().indexOf(selectBean) == -1) {
                List<DongTaiReplyBean> list = adapter.getList();
                boolean b = false;
                for (DongTaiReplyBean dongTaiReplyBean : list) {
                    if (selectBean.getId() != null && selectBean.getId().equals(dongTaiReplyBean.getId())) {
                        selectBean = dongTaiReplyBean;
                        b = true;
                    }
                }
                if (b) {
                    listview.setSelection(adapter.getList().indexOf(selectBean) + 1);
                }
            } else {
                listview.setSelection(adapter.getList().indexOf(selectBean) + 1);
            }
        }
    }

    @Override
    public void logoCick(DongTaiReplyBean bean) {
        Intent i = new Intent(this, bean.getUserid()
                .equals(ac.getLoginUserId()) ? UserInfoActivity_.class
                : UserOtherInfoActivity_.class);
        i.putExtra("userId", bean.getUserid());
        startActivity(i);
    }

    @OptionsItem(R.id.delete)
    public void rightClick() {
        new AlertDialog.Builder(this).setTitle("提示").setMessage("您确定要删除这条约会吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RequestParams ap = getAjaxParams();
                        ap.put("dtid", bean.getDtid() + "");
                        ac.finalHttp.post(URL.DELETE_DATE, ap,
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
    }

    ;

    @Click
    void manage() {
        DateApplyManageActivity_.intent(this).bean(bean).startForResult(MANAGE);
    }

    @OnActivityResult(MANAGE)
    void onManageResult(int result) {
        if (result == 1) {
            bean.setDtstate(1);
        }
    }
}
