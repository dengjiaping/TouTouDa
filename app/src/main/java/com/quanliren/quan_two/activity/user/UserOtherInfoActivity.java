package com.quanliren.quan_two.activity.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseUserActivity;
import com.quanliren.quan_two.activity.image.ImageBrowserActivity_;
import com.quanliren.quan_two.activity.shop.ShopVipDetail_;
import com.quanliren.quan_two.bean.ImageBean;
import com.quanliren.quan_two.bean.LoginUser;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.bean.UserTable;
import com.quanliren.quan_two.custom.CustomDialogEditText;
import com.quanliren.quan_two.pull.lib.ActionBarPullToRefresh;
import com.quanliren.quan_two.pull.lib.listeners.OnRefreshListener;
import com.quanliren.quan_two.util.ImageUtil;
import com.quanliren.quan_two.util.URL;
import com.quanliren.quan_two.util.http.JsonHttpResponseHandler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.user_other_info)
@OptionsMenu(R.menu.other_userinfo_menu)
public class UserOtherInfoActivity extends BaseUserActivity implements
        OnRefreshListener {

    @Extra
    public String userId;
    @ViewById
    View guanzhu_btn;
    @ViewById
    TextView care_me_txt;
    @ViewById
    View leavemsg_btn;
    @ViewById
    View bottom_btn_ll;
    @ViewById
    View gift_btn;
    @OptionsMenuItem
    MenuItem jubao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

    }

    @OptionsItem(R.id.jubao)
    void jubao() {
        if (user == null) {
            return;
        }
        LoginUser my = getHelper().getUser();
        if (my == null) {
            startLogin();
            return;
        }
        if (my.getId().equals(user.getId())) {
            showCustomToast("这是自己哟~");
            return;
        }
        if (user.getIsblacklist() == 0) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setItems(new String[]{"拉黑", "举报并拉黑"},
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    choice(which);
                                }
                            }).create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } else {
            cancelBlack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        jubao.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @AfterViews
    protected void initView() {
        super.initView();
        bottom_btn_ll.setVisibility(View.GONE);
        ViewHelper.setTranslationY(bottom_btn_ll, ImageUtil.dip2px(this, 50));
        initPull();

        try {
            UserTable ut = userTableDao.queryForId(userId);
            if (ut != null) {
                user = ut.getUser();
                initViewByUser();
            }
        } catch (Exception e) {
        }

    }

    @UiThread(delay = 500)
    void initPull() {
        ActionBarPullToRefresh.from(this).allChildrenArePullable()
                .listener(this).setAutoStart(true).setup(layout);
    }

    @Override
    public void onRefreshStarted(View view) {
        RequestParams rp = getAjaxParams();
        rp.put("otherid", userId);
        rp.put("longitude", ac.cs.getLng());
        rp.put("latitude", ac.cs.getLat());
        ac.finalHttp.post(URL.GET_USER_INFO, rp, new JsonHttpResponseHandler() {
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
                            User temp = new Gson().fromJson(
                                    response.getString(URL.RESPONSE), User.class);
                            if (temp != null) {
                                user = temp;
                                UserTable dbUser = new UserTable(temp);
                                userTableDao.deleteById(dbUser.getId());
                                userTableDao.create(dbUser);
                                initViewByUser();
                                isblack();
                                careText();
                            }
                            break;
                        default:
                            showFailInfo(response);
                            break;
                    }
                } catch (Exception e) {

                } finally {
                    layout.setRefreshComplete();
                    layout.setEnabled(false);
                }
            }

            @Override
            public void onStart() {
            }
        });
    }

    @Click
    public void guanzhu_btn(View v) {
        if (user == null) {
            return;
        }
        LoginUser my = getHelper().getUser();
        if (my == null) {
            startLogin();
            return;
        }
        if (my.getId().equals(user.getId())) {
            showCustomToast("这是自己哟~");
            return;
        }
        String str = user.getAttenstatus().equals("0") ? "您确定要关注TA吗?"
                : "您确定要取消关注吗?";
        new AlertDialog.Builder(UserOtherInfoActivity.this)
                .setTitle("提示")
                .setMessage(str)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        RequestParams ap = getAjaxParams();
                        ap.put("otherid", userId);
                        String url = user.getAttenstatus().equals("0") ? URL.CONCERN
                                : URL.CANCLECONCERN;
                        ac.finalHttp.post(url, ap, concernCallBack);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                }).create().show();
    }

    JsonHttpResponseHandler concernCallBack = new JsonHttpResponseHandler() {
        public void onStart() {
            customShowDialog(1);
        }

        ;

        public void onFailure() {
            customDismissDialog();
            showIntentErrorToast();
        }

        ;

        public void onSuccess(JSONObject jo) {
            customDismissDialog();
            try {
                int status = jo.getInt(URL.STATUS);
                switch (status) {
                    case 0:
                        user.setAttenstatus(user.getAttenstatus().equals("0") ? "1"
                                : "0");

                        UserTable dbUser = new UserTable(user);
                        userTableDao.deleteById(dbUser.getId());
                        userTableDao.create(dbUser);

                        if (user.getAttenstatus().equals("1")) {
                            showCustomToast("已添加关注");
                        } else {
                            showCustomToast("已取消关注");
                        }
                        careText();
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
    };

    public void careText() {
        if (user != null && user.getAttenstatus() != null) {
            if (user.getAttenstatus().equals("0")) {
                care_me_txt.setText("关注我吧");
            } else {
                care_me_txt.setText("已关注");
            }
        }
    }

    public void isblack() {
        if (user == null) {
            return;
        }
        if (user.getIsblacklist() == 0) {
            ViewPropertyAnimator.animate(bottom_btn_ll).translationY(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            bottom_btn_ll.setVisibility(View.VISIBLE);
                        }
                    });
            jubao.setTitle("举报");
        } else {
            ViewPropertyAnimator.animate(bottom_btn_ll)
                    .translationY(ImageUtil.dip2px(this, 50))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            bottom_btn_ll.clearAnimation();
                            bottom_btn_ll.setVisibility(View.GONE);
                        }
                    });

            jubao.setTitle("取消拉黑");
        }
        jubao.setVisible(true);
    }

    public void cancelBlack() {
        RequestParams ap = getAjaxParams();
        ap.put("otherid", user.getId());
        ac.finalHttp.post(URL.CANCLEBLACK, ap, new setLogoCallBack());
    }

    public void choice(int witch) {
        switch (witch) {
            case 0:
                new AlertDialog.Builder(UserOtherInfoActivity.this)
                        .setTitle("提示")
                        .setMessage("您确定要拉黑该用户吗？")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        RequestParams ap = getAjaxParams();
                                        ap.put("otherid", user.getId());
                                        ac.finalHttp.post(URL.ADDTOBLACK, ap,
                                                addBlackCallBack);
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    }
                                }).create().show();
                break;
            case 1:
                new AlertDialog.Builder(UserOtherInfoActivity.this)
                        .setTitle("提示")
                        .setMessage("您确定要举报并拉黑该用户吗？")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialogChoiceReason();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    }
                                }).create().show();
                break;
        }
    }

    public void dialogChoiceReason() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setItems(
                        new String[]{"骚扰信息", "个人资料不当", "盗用他人资料", "垃圾广告",
                                "色情相关"},
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                RequestParams ap = getAjaxParams();
                                ap.put("otherid", user.getId());
                                ap.put("type", which + "");
                                ac.finalHttp.post(URL.JUBAOANDBLACK, ap,
                                        addBlackCallBack);
                            }
                        }).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    JsonHttpResponseHandler addBlackCallBack = new JsonHttpResponseHandler() {
        public void onStart() {
            customShowDialog("正在发送请求");
        }

        ;

        public void onSuccess(JSONObject jo) {
            try {
                int status = jo.getInt(URL.STATUS);
                switch (status) {
                    case 0:
                        showCustomToast("操作成功");
                        user.setIsblacklist(1);
                        user.setAttenstatus("0");
                        UserTable ut = new UserTable(user);
                        userTableDao.deleteById(user.getId());
                        userTableDao.create(ut);
                        isblack();
                        careText();
                        Intent i = new Intent(BlackListActivity.ADDEBLACKLIST);
                        i.putExtra("bean", user);
                        sendBroadcast(i);
                        break;
                    default:
                        showFailInfo(jo);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                customDismissDialog();
            }
        }

        ;

        public void onFailure() {
            customDismissDialog();
            showIntentErrorToast();
        }

        ;
    };

    class setLogoCallBack extends JsonHttpResponseHandler {

        public void onStart() {
            customShowDialog("正在发送请求");
        }

        ;

        public void onFailure() {
            customDismissDialog();
            showIntentErrorToast();
        }

        ;

        public void onSuccess(JSONObject jo) {
            customDismissDialog();
            try {
                int status = jo.getInt(URL.STATUS);
                switch (status) {
                    case 0:
                        showCustomToast("取消成功");
                        user.setIsblacklist(0);
                        UserTable ut = new UserTable(user);
                        userTableDao.deleteById(user.getId());
                        userTableDao.create(ut);
                        isblack();
                        Intent i = new Intent(BlackListActivity.CANCLEBLACKLIST);
                        i.putExtra("id", user.getId());
                        sendBroadcast(i);
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
    }

    @Click
    void userlogo() {
        if (user != null) {
            List<ImageBean> str = new ArrayList<ImageBean>();
            str.add(new ImageBean(user.getAvatar()));
            ImageBrowserActivity_.intent(this).mPosition(0).mProfile(str)
                    .start();
        }
    }

    CustomDialogEditText mdialog;

    @Click
    public void gift_btn() {
        if (user == null) {
            return;
        }
        LoginUser my = getHelper().getUser();
        if (my == null) {
            startLogin();
            return;
        }
        if (my.getId().equals(user.getId())) {
            showCustomToast("这是自己哟~");
            return;
        }
        mdialog = new CustomDialogEditText.Builder(this)
                .setTitle("请输入要赠送的靓点个数")
                .setPositiveButton("赠送", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String num = mdialog.getMessage().trim();
                        if (num.length() > 0) {
                            if (Integer.valueOf(num) > Integer
                                    .valueOf(getHelper().getUserInfo().getCoin())) {
                                goCoin();
                            } else {
                                RequestParams ap = getAjaxParams();
                                ap.put("otherid", user.getId());
                                ap.put("coin", num);
                                ac.finalHttp.post(URL.GIVETILI, ap,
                                        giveCallBack);
                            }
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        mdialog.show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                showKeyBoard();
            }
        }, 2);
    }

    ;

    JsonHttpResponseHandler giveCallBack = new JsonHttpResponseHandler() {
        public void onStart() {
            customShowDialog("正在赠送");
        }

        ;

        public void onSuccess(JSONObject jo) {
            try {
                int status = jo.getInt(URL.STATUS);
                switch (status) {
                    case 0:
                        showCustomToast("赠送成功");
                        int lastpower = jo.getJSONObject(URL.RESPONSE).getInt(
                                "lastcoin");
                        User user = getHelper().getUserInfo();
                        user.setCoin(lastpower);
                        UserTable ut = new UserTable(user);
                        userTableDao.deleteById(user.getId());
                        userTableDao.create(ut);
                        break;
                    default:
                        showFailInfo(jo);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                customDismissDialog();
            }
        }

        ;

        public void onFailure() {
            customDismissDialog();
            showIntentErrorToast();
        }

        ;
    };

    public void dialogBuyVip() {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("靓点不足").setMessage("立即获取？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("获取", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(UserOtherInfoActivity.this,
                                ShopVipDetail_.class);
                        startActivity(i);
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Click
    void leavemsg_btn() {
        LoginUser user = getHelper().getUser();
        if (user == null) {
            startLogin();
            return;
        }
        if (user.getId().equals(this.user.getId())) {
            showCustomToast("这是自己哟~");
            return;
        }
        ChatActivity_.intent(this).friend(this.user).start();
    }

	/*
     * @ViewById View guanzhu_btn;
	 * 
	 * @ViewById View leavemsg_btn;
	 * 
	 * @ViewById TextView care_me_txt;
	 * 
	 * @ViewById View bottom_btn_ll; boolean enable = false;
	 * 
	 * @ViewById View lx_ll;
	 * 
	 * @ViewById View qq_ll;
	 * 
	 * @ViewById TextView qq;
	 * 
	 * @ViewById View mobile_ll;
	 * 
	 * @ViewById TextView mobile;
	 * 
	 * @ViewById PullToRefreshLayout layout; String friendid = null;
	 * 
	 * @Override protected void onCreate(Bundle savedInstanceState) { friendid =
	 * getIntent().getExtras().getString("id");
	 * super.onCreate(savedInstanceState); try { UserTable ut =
	 * userTableDao.queryForId(friendid); if (ut != null) { user = ut.getUser();
	 * } } catch (SQLException e) { e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * @AfterViews void initView(){
	 * getSupportActionBar().setTitle(R.string.other_userinfo);
	 * initViewByUser(); isblack();
	 * pro_btn.setImageResource(R.drawable.userinfo_give_icon);
	 * pro_btn.setVisibility(View.VISIBLE); careText();
	 * 
	 * ActionBarPullToRefresh.from(this).allChildrenArePullable()
	 * .listener(this).setAutoStart(true).setup(layout); }
	 * 
	 * public void careText() { if (user != null && user.getAttenstatus() !=
	 * null) { if (user.getAttenstatus().equals("0")) {
	 * care_me_txt.setText("关注我吧"); } else { care_me_txt.setText("已关注"); } } }
	 * 
	 * public void initPicAdapter() { ac.finalHttp.post(URL.GET_USER_INFO,
	 * getAjaxParams("otherid", friendid), callBack); }
	 * 
	 * public void relation_btn(View v) { ac.finalHttp.post(URL.GETCONTACT,
	 * getAjaxParams("otherid", friendid), getContactCallBack); }
	 * 
	 * @Click public void guanzhu_btn(View v) { if (!enable) { return; } if
	 * (user == null) { return; } String str = user.getAttenstatus().equals("0")
	 * ? "您确定要关注TA吗?" : "您确定要取消关注吗?"; new
	 * IosCustomDialog.Builder(UserOtherInfoActivity.this) .setMessage(str)
	 * .setPositiveButton("确定", new DialogInterface.OnClickListener() {
	 * 
	 * public void onClick(DialogInterface dialog, int which) { RequestParams ap
	 * = getAjaxParams(); ap.put("otherid", friendid); ap.put("type",
	 * user.getAttenstatus() + ""); String url =
	 * user.getAttenstatus().equals("0") ? URL.CONCERN : URL.CANCLECONCERN;
	 * ac.finalHttp.post(url, ap, concernCallBack); } })
	 * .setNegativeButton("取消", new DialogInterface.OnClickListener() {
	 * 
	 * public void onClick(DialogInterface arg0, int arg1) { }
	 * }).create().show(); }
	 * 
	 * @Click public void leavemsg_btn(View v) { if (!enable) { return; } Intent
	 * i = new Intent(this, ChatActivity_.class); i.putExtra("friend", user);
	 * startActivity(i); }
	 * 
	 * public void personal_btn(View v) { Intent i = new Intent(this,
	 * QuanPersonalActivity_.class); i.putExtra("type",
	 * QuanPullListViewFragment.ONCE); i.putExtra("otherid", friendid);
	 * startActivity(i); }
	 * 
	 * JsonHttpResponseHandler callBack = new JsonHttpResponseHandler() {
	 * 
	 * public void onFailure() { layout.setRefreshComplete();
	 * showIntentErrorToast(); };
	 * 
	 * public void onSuccess(JSONObject jo) { layout.setRefreshComplete(); try {
	 * int status = jo.getInt(URL.STATUS); switch (status) { case 0: User temp =
	 * new Gson().fromJson(jo.getString(URL.RESPONSE), User.class); if (temp !=
	 * null) { user = temp; UserTable dbUser = new UserTable(temp);
	 * userTableDao.deleteById(dbUser.getId()); userTableDao.create(dbUser);
	 * initViewByUser(); isblack(); } enable = true; careText(); break; default:
	 * showFailInfo(jo); break; } } catch (Exception e) { e.printStackTrace(); }
	 * }; }; JsonHttpResponseHandler getContactCallBack = new
	 * JsonHttpResponseHandler() { public void onStart() { customShowDialog(1);
	 * };
	 * 
	 * public void onFailure() { customDismissDialog(); showIntentErrorToast();
	 * };
	 * 
	 * public void onSuccess(JSONObject jo) { customDismissDialog(); try { int
	 * status = jo.getInt(URL.STATUS); switch (status) { case 0: User temp = new
	 * Gson().fromJson(jo.getString(URL.RESPONSE), User.class); if
	 * (Util.isStrNotNull(temp.getMobile())) {
	 * mobile_ll.setVisibility(View.VISIBLE); mobile.setText(temp.getMobile());
	 * } else { mobile_ll.setVisibility(View.GONE); } if
	 * (Util.isStrNotNull(temp.getQq())) { qq_ll.setVisibility(View.VISIBLE);
	 * qq.setText(temp.getQq()); } else { qq_ll.setVisibility(View.GONE); }
	 * ViewHelper.setAlpha(lx_ll, 0); animate(lx_ll).setDuration(200).alpha(1)
	 * .setListener(new AnimatorListener() {
	 * 
	 * @Override public void onAnimationStart(Animator animation) {
	 * lx_ll.setVisibility(View.VISIBLE); }
	 * 
	 * @Override public void onAnimationRepeat(Animator animation) { }
	 * 
	 * @Override public void onAnimationEnd(Animator animation) { }
	 * 
	 * @Override public void onAnimationCancel(Animator animation) { }
	 * }).start(); break; default:
	 * showCustomToast(jo.getJSONObject(URL.RESPONSE).getString( URL.INFO));
	 * break; } } catch (Exception e) { e.printStackTrace(); } }; };
	 * JsonHttpResponseHandler concernCallBack = new JsonHttpResponseHandler() {
	 * public void onStart() { customShowDialog(1); };
	 * 
	 * public void onFailure() { customDismissDialog(); showIntentErrorToast();
	 * };
	 * 
	 * public void onSuccess(JSONObject jo) { customDismissDialog(); try { int
	 * status = jo.getInt(URL.STATUS); switch (status) { case 0:
	 * user.setAttenstatus(user.getAttenstatus().equals("0") ? "1" : "0");
	 * 
	 * UserTable dbUser = new UserTable(user);
	 * userTableDao.deleteById(dbUser.getId()); userTableDao.create(dbUser);
	 * 
	 * if (user.getAttenstatus().equals("1")) { user.setConnum(Integer.valueOf(
	 * Integer.valueOf(user.getConnum()) + 1) .toString());
	 * showCustomToast("已添加关注"); } else { user.setConnum(Integer.valueOf(
	 * Integer.valueOf(user.getConnum()) - 1) .toString());
	 * showCustomToast("已取消关注"); } // connum.setText(user.getConnum() + "");
	 * careText(); break; default: showFailInfo(jo); break; } } catch (Exception
	 * e) { e.printStackTrace(); } }; }; CustomDialogEditText mdialog;
	 * 
	 * public void pro_btn(View v) { if (!enable) { return; } mdialog = new
	 * CustomDialogEditText.Builder(this) .setTitle("请输入要赠送的体力个数")
	 * .setPositiveButton("赠送", new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { String
	 * num=mdialog.getMessage().trim(); if(num.length()>0){
	 * if(Integer.valueOf(num
	 * )>Integer.valueOf(getHelper().getUserInfo().getPowernum())){
	 * dialogBuyVip(); }else{ RequestParams ap=getAjaxParams();
	 * ap.put("otherid", user.getId()); ap.put("powernum", num);
	 * ac.finalHttp.post(URL.GIVETILI, ap,giveCallBack); } } } })
	 * .setNegativeButton("取消", new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { }
	 * }).create(); mdialog.show(); new Handler().postDelayed(new Runnable() {
	 * 
	 * @Override public void run() { showKeyBoard(); } }, 2); };
	 * 
	 * JsonHttpResponseHandler giveCallBack=new JsonHttpResponseHandler() {
	 * public void onStart() { customShowDialog("正在赠送"); }; public void
	 * onSuccess(JSONObject jo) { try { int status=jo.getInt(URL.STATUS); switch
	 * (status) { case 0: showCustomToast("赠送成功"); int
	 * lastpower=jo.getJSONObject(URL.RESPONSE).getInt("lastpower"); User
	 * user=getHelper().getUserInfo(); user.setPowernum(lastpower+""); UserTable
	 * ut=new UserTable(user); userTableDao.deleteById(user.getId());
	 * userTableDao.create(ut); break; default: showFailInfo(jo); break; } }
	 * catch (Exception e) { e.printStackTrace(); }finally{
	 * customDismissDialog(); } }; public void onFailure(Throwable t, int
	 * errorNo, String strMsg) { customDismissDialog();showIntentErrorToast();
	 * }; };
	 * 
	 * public void dialogBuyVip(){ new
	 * IosCustomDialog.Builder(this).setTitle("体力不足").setMessage("立即获取？")
	 * .setNegativeButton("取消", new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { }
	 * }).setPositiveButton("获取", new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { Intent
	 * i = new Intent(UserOtherInfoActivity.this,ShopVipDetail.class);
	 * startActivity(i); } }).create().show(); }
	 */

	/*
	 * @Override public void onRefreshStarted(View view) { initPicAdapter(); };
	 */
}
