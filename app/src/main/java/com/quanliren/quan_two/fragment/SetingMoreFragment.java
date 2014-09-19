package com.quanliren.quan_two.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_two.activity.PropertiesActivity;
import com.quanliren.quan_two.activity.PropertiesActivity.ITitle;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.group.date.MyFavoriteDateListActivity_;
import com.quanliren.quan_two.activity.seting.AboutUsActivity_;
import com.quanliren.quan_two.activity.seting.EmoticonListActivity_;
import com.quanliren.quan_two.activity.seting.HtmlActivity_;
import com.quanliren.quan_two.activity.seting.InviteFriendActivity_;
import com.quanliren.quan_two.activity.seting.RemindMessageActivity_;
import com.quanliren.quan_two.activity.shop.product.MyExchangeListActivity_;
import com.quanliren.quan_two.activity.user.BlackListActivity_;
import com.quanliren.quan_two.activity.user.ModifyPasswordActivity_;
import com.quanliren.quan_two.activity.user.MyVisitActivity_;
import com.quanliren.quan_two.activity.user.PersonalDongTaiActivity_;
import com.quanliren.quan_two.activity.user.UserInfoActivity_;
import com.quanliren.quan_two.adapter.ParentsAdapter;
import com.quanliren.quan_two.adapter.SetAdapter;
import com.quanliren.quan_two.bean.ExchangeRemindBean;
import com.quanliren.quan_two.bean.LoginUser;
import com.quanliren.quan_two.bean.SetBean;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.bean.UserTable;
import com.quanliren.quan_two.custom.CustomVip;
import com.quanliren.quan_two.custom.StateTextView;
import com.quanliren.quan_two.db.DBHelper;
import com.quanliren.quan_two.fragment.base.MenuFragmentBase;
import com.quanliren.quan_two.util.BroadcastUtil;
import com.quanliren.quan_two.util.ImageUtil;
import com.quanliren.quan_two.util.StaticFactory;
import com.quanliren.quan_two.util.URL;
import com.quanliren.quan_two.util.Util;
import com.quanliren.quan_two.util.http.JsonHttpResponseHandler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.seting)
public class SetingMoreFragment extends MenuFragmentBase implements ITitle {

    public static final String UPDATE_USERINFO = "com.quanliren.quan_two.fragment.SetingMoreFragment.UPDATE_USERINFO";
    @ViewById
    ListView listview;
    List<SetBean> list = new ArrayList<SetBean>();
    ImageView userlogo;
    TextView nickname;
    CustomVip vip;
    View head = null;
    View vip_txt, ld;
    TextView vip_num, ld_txt;
    SetBean clearSb, exchangeSb, visitSb, stateSb;
    SetAdapter adapter;
    ChoseAdapter cadapter;

    @OrmLiteDao(helper = DBHelper.class, model = ExchangeRemindBean.class)
    Dao<ExchangeRemindBean, String> exchangeDao;

    @AfterViews
    void initView() {

        listview.addHeaderView(head = View.inflate(getActivity(),
                R.layout.seting_head, null));
        userlogo = (ImageView) head.findViewById(R.id.userlogo);
        nickname = (TextView) head.findViewById(R.id.nickname);
        vip = (CustomVip) head.findViewById(R.id.vip);
        vip_txt = head.findViewById(R.id.vip_txt);
        ld = head.findViewById(R.id.ld);
        vip_num = (TextView) head.findViewById(R.id.vip_num);
        ld_txt = (TextView) head.findViewById(R.id.ld_txt);

        Intent i = new Intent(getActivity(), PersonalDongTaiActivity_.class);
        i.putExtra("otherid", "");
        list.add(new SetBean(R.drawable.set_icon_1, "个人动态", 0, i, true));

        list.add(exchangeSb = new SetBean(R.drawable.set_icon_2, "我的兑换", 1,
                new Intent(getActivity(), MyExchangeListActivity_.class), true));
        list.add(visitSb = new SetBean(R.drawable.set_icon_3, "访客记录", 1,
                new Intent(getActivity(), MyVisitActivity_.class), true));
        list.add(stateSb = new SetBean(R.drawable.set_icon_4, "状态设置", 1, null,
                true));
        list.add(new SetBean(R.drawable.set_icon_5, "我的收藏", 1, new Intent(
                getActivity(), MyFavoriteDateListActivity_.class), true));
        list.add(new SetBean(R.drawable.set_icon_14, "表情下载", 2, new Intent(
                getActivity(), EmoticonListActivity_.class), true));

        list.add(new SetBean(R.drawable.set_icon_6, "修改密码", 0, new Intent(
                getActivity(), ModifyPasswordActivity_.class), true));
        list.add(clearSb = new SetBean(R.drawable.set_icon_7, "清除缓存", 1, null,
                false));
        list.add(new SetBean(R.drawable.set_icon_8, "邀请好友", 1, new Intent(
                getActivity(), InviteFriendActivity_.class), false));
        list.add(new SetBean(R.drawable.set_icon_9, "黑名单", 1, new Intent(
                getActivity(), BlackListActivity_.class), true));
        list.add(new SetBean(R.drawable.set_icon_12, "消息通知", 2, new Intent(
                getActivity(), RemindMessageActivity_.class), false));

        Intent help = new Intent(getActivity(), HtmlActivity_.class);
        help.putExtra("title", "用户帮助");
        help.putExtra("url", "file:///android_asset/function.html");
        list.add(new SetBean(R.drawable.set_icon_10, "用户帮助", 0, help, false));
        list.add(new SetBean(R.drawable.set_icon_11, "关于我们", 1, new Intent(
                getActivity(), AboutUsActivity_.class), false));
        // list.add(new SetBean(R.drawable.set_icon_12, "聊天背景", 1, new Intent(
        // getActivity(), ShopVipDetail_.class),false));
        list.add(new SetBean(R.drawable.set_icon_13, "退出当前账号", 2, null, true));

        View v = new View(getActivity());
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, ImageUtil.dip2px(
                getActivity(), 8));
        v.setLayoutParams(lp);
        listview.addFooterView(v);
        listview.setAdapter(adapter = new SetAdapter(getActivity(), list));
        getFileSize();

        head.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                LoginUser user = getHelper().getUser();
                if (user == null) {
                    startLogin();
                } else {
                    UserInfoActivity_.intent(getActivity()).start();
                }
            }
        });
    }

    @Receiver(actions = {UPDATE_USERINFO,
            PropertiesActivity.PROPERTIESACTIVITY_NEWMESSAGE})
    public void receiver(Intent i) {
        String action = i.getAction();
        if (action.equals(UPDATE_USERINFO)) {
            ac.finalHttp.post(URL.GET_USER_INFO, getAjaxParams(), callBack);
        } else if (action
                .equals(PropertiesActivity.PROPERTIESACTIVITY_NEWMESSAGE)) {
            setCount();
        }
    }

    @Background
    void getFileSize() {
        BigDecimal bd = new BigDecimal(getFolderSize(ImageLoader.getInstance()
                .getDiskCache().getDirectory()));
        bd = bd.divide(new BigDecimal(1024 * 1024));
        clearSb.source = (Util.RoundOf(bd.toPlainString()) + "MB");
        notifyAdapter();
    }

    @UiThread
    void notifyAdapter() {
        adapter.notifyDataSetChanged();
    }

    public double getFolderSize(java.io.File file) {
        double size = 0;
        java.io.File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                size = size + getFolderSize(fileList[i]);
            } else {
                size = size + fileList[i].length();
            }
        }
        return (double) size;
    }

    @ItemClick
    void listview(int position) {
        if (position > list.size() || position < 1) {
            return;
        }
        User user = getHelper().getUserInfo();
        SetBean sb = list.get(position - 1);
        if (sb.login && user == null) {
            startLogin();
            return;
        }

        if (position == list.size()) {
            loginout();
        } else if (sb.equals(clearSb)) {
            clearCache();
        } else if (sb.equals(stateSb)) {
            setState();
        } else if (sb.equals(visitSb) && user.getIsvip() == 0) {
            goVip();
        } else {
            if (position > 0) {
                if (sb.clazz != null)
                    startActivity(sb.clazz);
            }
        }

    }

    ListView clistview;
    List<ChoiceBean> choicelist;

    public void setState() {
        choicelist = new ArrayList<ChoiceBean>();
        final User user = getHelper().getUserInfo();
        for (int i = 0; i < 7; i++) {
            if (i == user.getUserstate()) {
                choicelist.add(new ChoiceBean(i, true));
            } else {
                choicelist.add(new ChoiceBean(i, false));
            }

        }

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("状态设置")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int index = 0;
                        for (ChoiceBean cb : choicelist) {
                            if (cb.select) {
                                index = choicelist.indexOf(cb);
                            }
                        }
                        if (index != user.getUserstate()) {
                            RequestParams rp = getAjaxParams();
                            rp.put("userstate", index + "");
                            ac.finalHttp.post(URL.SET_STATE, rp,
                                    new stateCallBack(index));
                        }
                    }
                })
                .setAdapter(
                        cadapter = new ChoseAdapter(getActivity(), choicelist),
                        null).create();
        clistview = dialog.getListView();
        dialog.setCanceledOnTouchOutside(true);
        clistview.setFocusable(false);
        clistview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                for (ChoiceBean cacheBean : choicelist) {
                    cacheBean.setSelect(false);
                }
                choicelist.get(arg2).setSelect(true);
                cadapter.notifyDataSetChanged();
            }
        });
        dialog.show();
    }

    class ChoseAdapter extends ParentsAdapter {

        public ChoseAdapter(Context c, List list) {
            super(c, list);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(c, R.layout.chose_state, null);
                holder.tv = (StateTextView) convertView
                        .findViewById(R.id.textview);
                holder.rb = (RadioButton) convertView.findViewById(R.id.rb);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ChoiceBean cb = (ChoiceBean) list.get(position);
            holder.tv.setState(cb.index);
            if (cb.select) {
                holder.rb.setChecked(true);
            } else {
                holder.rb.setChecked(false);
            }
            return convertView;
        }

        class ViewHolder {
            StateTextView tv;
            RadioButton rb;
        }
    }

    JsonHttpResponseHandler callBack = new JsonHttpResponseHandler() {

        public void onSuccess(JSONObject jo) {
            try {
                int status = jo.getInt(URL.STATUS);
                switch (status) {
                    case 0:
                        User temp = new Gson().fromJson(jo.getString(URL.RESPONSE),
                                User.class);
                        UserTable ut = new UserTable(temp);
                        getHelper().getUserTableDao().delete(ut);
                        getHelper().getUserTableDao().create(ut);

                        initSource(temp);
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

    public void loginout() {
        new AlertDialog.Builder(getActivity())
                .setMessage("您确定要残忍的离开吗？")
                .setTitle("提示")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            ac.finalHttp.post(URL.LOGOUT, getAjaxParams(),
                                    new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode,
                                                              Header[] headers,
                                                              byte[] responseBody) {
                                        }

                                        @Override
                                        public void onFailure(int statusCode,
                                                              Header[] headers,
                                                              byte[] responseBody,
                                                              Throwable error) {
                                        }
                                    });
                            TableUtils.clearTable(getHelper()
                                    .getConnectionSource(), LoginUser.class);
                            showCustomToast("退出成功");
                            ac.stopServices();
                            Intent i = new Intent(BroadcastUtil.EXIT);
                            getActivity().sendBroadcast(i);
                            Util.canalAlarm(getActivity(), BroadcastUtil.ACTION_CHECKCONNECT);
                            Util.canalAlarm(getActivity(), BroadcastUtil.ACTION_CHECKMESSAGE);
                            onResume();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        User user = getHelper().getUserInfo();
        initSource(user);
        getFileSize();
        setCount();
    }

    public void setCount() {
        LoginUser user = getHelper().getUser();
        if (user == null) {
            exchangeSb.count = 0;
            adapter.notifyDataSetChanged();
            return;
        }
        try {
            QueryBuilder<ExchangeRemindBean, String> qb = exchangeDao
                    .queryBuilder();
            qb.where().eq("userId", user.getId());
            long count = qb.countOf();
            exchangeSb.count = (int) count;
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initSource(User user) {
        vip.setVisibility(View.GONE);
        vip_txt.setVisibility(View.GONE);
        vip_num.setVisibility(View.GONE);
        ld.setVisibility(View.GONE);
        ld_txt.setVisibility(View.GONE);
        nickname.setTextColor(getResources().getColor(R.color.username));
        if (user != null) {
            ImageLoader.getInstance().displayImage(
                    user.getAvatar() + StaticFactory._320x320, userlogo,
                    ac.options_userlogo);
            nickname.setText(user.getNickname());
            if (user.getIsvip() > 0) {
                vip.setVip(user.getIsvip());
                vip_txt.setVisibility(View.VISIBLE);
                vip_num.setVisibility(View.VISIBLE);
                vip_num.setText(Html.fromHtml("<font color=\"#e71d1d\">"
                        + Util.daysBetween(user.getViptime()) + "</font>"
                        + "天到期"));
                nickname.setTextColor(getResources().getColor(R.color.vip_name));
            }
            ld.setVisibility(View.VISIBLE);
            ld_txt.setVisibility(View.VISIBLE);
            ld_txt.setText(user.getCoin() + "");
        } else {
            nickname.setText("请登录");
            userlogo.setImageResource(R.drawable.defalut_logo);
        }
    }

    public void clearCache() {
        new AlertDialog.Builder(getActivity()).setMessage("您确定要清除缓存吗？")
                .setTitle("清除缓存")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customShowDialog("正在清理");
                        clear();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    @Background
    public void clear() {
        ImageLoader.getInstance().clearDiskCache();
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    customDismissDialog();
                    showCustomToast("清理完成");
                    clearSb.source = "0.0MB";
                    notifyAdapter();
                }
            });
        }
    }

    @Override
    public String getTitle() {
        return "设置";
    }

    class ChoiceBean {
        int index;
        boolean select;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public boolean isSelect() {
            return select;
        }

        public void setSelect(boolean select) {
            this.select = select;
        }

        public ChoiceBean(int index, boolean select) {
            super();
            this.index = index;
            this.select = select;
        }

        public ChoiceBean() {
            super();
            // TODO Auto-generated constructor stub
        }
    }

    class stateCallBack extends JsonHttpResponseHandler {

        int index = 0;

        public stateCallBack(int index) {
            this.index = index;
        }

        public void onStart() {
            customShowDialog(4);
        }

        ;

        public void onFailure() {
            customDismissDialog();
            showIntentErrorToast();
        }

        ;

        public void onSuccess(JSONObject response) {
            try {
                int status = response.getInt(URL.STATUS);
                switch (status) {
                    case 0:
                        showCustomToast("设置成功");
                        User user = getHelper().getUserInfo();
                        user.setUserstate(index);
                        UserTable ut = new UserTable(user);
                        getHelper().getUserTableDao().delete(ut);
                        getHelper().getUserTableDao().create(ut);
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

        ;
    }

    ;
}
