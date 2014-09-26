package com.quanliren.quan_two.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.Where;
import com.quanliren.quan_two.activity.Noti;
import com.quanliren.quan_two.activity.PropertiesActivity;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.shop.product.MyExchangeListActivity_;
import com.quanliren.quan_two.activity.user.ChatActivity;
import com.quanliren.quan_two.activity.user.ChatActivity_;
import com.quanliren.quan_two.application.AppClass;
import com.quanliren.quan_two.bean.ChatListBean;
import com.quanliren.quan_two.bean.DfMessage;
import com.quanliren.quan_two.bean.ExchangeRemindBean;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.db.DBHelper;
import com.quanliren.quan_two.fragment.message.MyLeaveMessageFragment;
import com.quanliren.quan_two.service.QuanPushService.ConnectionThread;
import com.quanliren.quan_two.util.BitmapCache;
import com.quanliren.quan_two.util.BroadcastUtil;
import com.quanliren.quan_two.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

public class ClientHandlerWord {
    public static final String TAG = "ClientHandlerWord";

    AppClass ac;
    Context c;
    Uri alert;
    MediaPlayer player;
    AudioManager audioManager;
    User user;
    long time = 0;
    int num = 0;

    DBHelper helper = null;
    Dao<DfMessage, Integer> messageDao;
    Dao<ChatListBean, Integer> chatlistDao;
    Dao<ExchangeRemindBean, String> exchangeDao;

    public ClientHandlerWord(Context context) {
        ac = (AppClass) context.getApplicationContext();
        this.c = context;

        helper = OpenHelperManager.getHelper(context, DBHelper.class);
        try {
            messageDao = helper.getDao(DfMessage.class);
            chatlistDao = helper.getDao(ChatListBean.class);
            exchangeDao = helper.getDao(ExchangeRemindBean.class);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sessionConnected(ConnectionThread session) {
        try {
            JSONObject jo = new JSONObject();
            jo.put(SocketManage.ORDER, SocketManage.ORDER_CONNECT);
            jo.put(SocketManage.TOKEN, helper.getUser().getToken());
            jo.put(SocketManage.DEVICE_TYPE, "0");
            jo.put(SocketManage.DEVICE_ID, ac.cs.getDeviceId());
            session.write(jo.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void messageReceived(ConnectionThread session, Object message)
            throws Exception {
        LogUtil.d(TAG, message.toString());
        user = helper.getUserInfo();
        JSONObject jo = new JSONObject(message.toString());
        String order = jo.getString(SocketManage.ORDER);
        if (order.equals(SocketManage.ORDER_SENDMESSAGE)) {
            getMessage(session, jo);
        } else if (order.equals(SocketManage.ORDER_SENDED)) {
            sended(jo);
        } else if (order.equals(SocketManage.ORDER_OUTLINE)) {
            Intent i = new Intent(BroadcastUtil.ACTION_OUTLINE);
            c.sendBroadcast(i);
        } else if (order.equals(SocketManage.ORDER_EXCHANGE)) {
            exchange(session,jo);
        }else if(order.equals(SocketManage.ORDER_SENDERROR)){
            sendederror(jo);
        }
    }

    public void exchange(ConnectionThread session,JSONObject jo) {

        try {
            if (jo.opt(SocketManage.MESSAGE_ID) != null) {
                JSONObject jos = new JSONObject();
                jos.put(SocketManage.ORDER, SocketManage.ORDER_SENDED);
                jos.put(SocketManage.MESSAGE_ID,
                        jo.opt(SocketManage.MESSAGE_ID));
                session.write(jos.toString());
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        ExchangeRemindBean rrb = new Gson().fromJson(jo.toString(),
                new TypeToken<ExchangeRemindBean>() {
                }.getType());
        try {
            rrb.setUserId(user.getId());
            DeleteBuilder<ExchangeRemindBean, String> builder = exchangeDao
                    .deleteBuilder();
            Where where = builder.where();
            where.and(where.eq("eaid", rrb.getEaid()),
                    where.eq("userId", rrb.getUserId()));
            builder.delete();
            exchangeDao.create(rrb);

            Intent intent = new Intent(c, Noti.class);
            intent.putExtra("activity", MyExchangeListActivity_.class);
            notify("MyExchangeListActivity__", "通知", "您有一条兑换信息", intent);

            Intent intentBro = new Intent(
                    PropertiesActivity.PROPERTIESACTIVITY_NEWMESSAGE);
            c.sendBroadcast(intentBro);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void notify(String nick, String title, String content, Intent intent) {
        int notificationId = (nick + title).hashCode();

        PendingIntent viewPendingIntent =

                PendingIntent.getActivity(c, num++, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                c)
                .setTicker(content)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_taskbar_icon)
                .setLargeIcon(
                        BitmapCache.getInstance().getBitmap(R.drawable.icon, c))
                .setContentTitle(title).setOnlyAlertOnce(true)
                .setContentText(content).setContentIntent(viewPendingIntent);

        if (ac.cs.getZHENOPEN() == 1 && ac.cs.getVIDEOOPEN() == 1) {
            notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        } else if (ac.cs.getZHENOPEN() == 1) {
            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        } else if (ac.cs.getVIDEOOPEN() == 1) {
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat
                .from(c);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public void sended(JSONObject jo) {
        try {
            String msgid = jo.getString(SocketManage.MESSAGE_ID);
            List<DfMessage> list = helper.getDao(DfMessage.class).queryForEq(
                    "msgid", msgid);
            if (list.size() > 0) {
                DfMessage m = list.get(0);
                m.setDownload(SocketManage.D_downloaded);
                helper.getDao(DfMessage.class).update(m);
                Intent i = new Intent(ChatActivity.CHANGESEND);
                i.putExtra("bean", m);
                c.sendBroadcast(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendederror(JSONObject jo) {
        try {
            String msgid = jo.getString(SocketManage.MESSAGE_ID);
            List<DfMessage> list = helper.getDao(DfMessage.class).queryForEq(
                    "msgid", msgid);
            if (list.size() > 0) {
                DfMessage m = list.get(0);
                m.setDownload(SocketManage.D_destroy);
                helper.getDao(DfMessage.class).update(m);
                Intent i = new Intent(ChatActivity.CHANGESEND);
                i.putExtra("bean", m);
                i.putExtra("type",jo.getInt(SocketManage.TYPE));
                c.sendBroadcast(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void getMessage(ConnectionThread session, JSONObject jo) {
        final DfMessage defMessage = new Gson().fromJson(
                jo.opt(SocketManage.MESSAGE).toString(), DfMessage.class);

        try {
            if (jo.opt(SocketManage.MESSAGE_ID) != null) {
                JSONObject jos = new JSONObject();
                jos.put(SocketManage.ORDER, SocketManage.ORDER_SENDED);
                jos.put(SocketManage.MESSAGE_ID,
                        jo.opt(SocketManage.MESSAGE_ID));
                session.write(jos.toString());
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        if (ac.cs.getMSGOPEN() != 1) {
            return;
        }
        if (defMessage.getMsgtype() > 0)
            defMessage.setDownload(SocketManage.D_nodownload);
        else
            defMessage.setDownload(SocketManage.D_downloaded);
        defMessage.setUserid(user.getId());

        ChatListBean cb = null;
        try {
            messageDao.create(defMessage);
            DeleteBuilder<ChatListBean, Integer> db = chatlistDao
                    .deleteBuilder();
            db.where().eq("userid", user.getId()).and()
                    .eq("friendid", defMessage.getSendUid());
            chatlistDao.delete(db.prepare());
            cb = new ChatListBean(user, defMessage);
            chatlistDao.create(cb);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Intent broad = new Intent(ChatActivity.ADDMSG);
        broad.putExtra("bean", defMessage);
        c.sendBroadcast(broad);

        broad = new Intent(MyLeaveMessageFragment.ADDMSG);
        broad.putExtra("bean", cb);
        c.sendBroadcast(broad);

        String content = null;
        if (defMessage.getMsgtype() == 0) {
            content = defMessage.getContent();
        } else if (defMessage.getMsgtype() == 1) {
            content = "[图片]";
        } else if (defMessage.getMsgtype() == 2) {
            content = "[语音]";
        } else if (defMessage.getMsgtype() == 5) {
            content = defMessage.getGifContent().flagName;
        } else if (defMessage.getMsgtype() == 4) {
            content = defMessage.getOtherContent().getText();
        }

        Intent intent = new Intent(c, Noti.class);
        intent.putExtra("activity", ChatActivity_.class);
        User friend = new User();
        friend.setId(defMessage.getSendUid());
        friend.setNickname(defMessage.getNickname());
        friend.setAvatar(defMessage.getUserlogo());
        intent.putExtra("friend", friend);

        notify(defMessage.getSendUid(), defMessage.getNickname(), content,
                intent);

    }

}
