package com.quanliren.quan_two.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.quanliren.quan_two.activity.user.ChatActivity;
import com.quanliren.quan_two.application.AppClass;
import com.quanliren.quan_two.bean.DfMessage;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.db.DBHelper;
import com.quanliren.quan_two.service.QuanPushService;
import com.quanliren.quan_two.service.SocketManage;
import com.quanliren.quan_two.util.BroadcastUtil;
import com.quanliren.quan_two.util.NetWorkUtil;
import com.quanliren.quan_two.util.Util;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
	private static final String TAG="AlarmReceiver";
	public void onReceive(Context context, Intent intent) {
//		Log.d(TAG, intent.getAction());
		NetWorkUtil netWorkUtil = new NetWorkUtil(context);
		AppClass ac = (AppClass) context.getApplicationContext();
		DBHelper helper = OpenHelperManager.getHelper(context, DBHelper.class);
		if (netWorkUtil.hasInternet()&&BroadcastUtil.ACTION_CHECKCONNECT.equals(intent.getAction())) {
			try {
				if (helper.getUser()!=null) {
					if (!Util.isServiceRunning(context,
							QuanPushService.class.getName())) {
						ac.startServices();
					} else if(ac.remoteService==null){
						ac.bindServices();
					} else if (!ac.isConnectSocket()) {
						Intent i = new Intent(BroadcastUtil.ACTION_RECONNECT);
						context.startService(i);
					}
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(BroadcastUtil.ACTION_OUTLINE.equals(intent.getAction())){
			NotificationManager nm= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancel(0);
			ac.dispose();
		}else if (netWorkUtil.hasInternet()
				&& BroadcastUtil.ACTION_CHECKMESSAGE.equals(intent.getAction())) {
			User user=helper.getUserInfo();
			try {
				if (user!=null) {
					if (ac.isConnectSocket()) {
						
						HashMap<String,Object> map = new HashMap<String,Object>(); 
						map.put("userid", user.getId());
						map.put("sendUid", user.getId());
						map.put("download", SocketManage.D_downloading);
						List<DfMessage> dms = helper.getDfMessageDao().queryForFieldValues(map);
						if (dms.size()==0) {
//							List<DfMessage> dms1 = ac.finalDb.findAllByWhere(DfMessage.class, "userid='"+user.getId()+"' and sendUid='"+user.getId()+"' and download="+SocketManage.D_downloading+"");
//							if(dms1.size()==0){
								Util.canalAlarm(context,BroadcastUtil.ACTION_CHECKMESSAGE); 
								Log.d(TAG, "cancle checkMessage");
//							}else{
//								dms.addAll(dms1);
//							}
						}
						for (DfMessage destoryMsg : dms) {
							if((new Date().getTime()-Util.fmtDateTime.parse(destoryMsg.getCtime()).getTime())>5*1000){
								if(destoryMsg.getResendCount()<=3){
									
									JSONObject msg=DfMessage.getMessage(destoryMsg);
									
									JSONObject jo = new JSONObject();
									jo.put(SocketManage.ORDER, SocketManage.ORDER_SENDMESSAGE);
									jo.put(SocketManage.SEND_USER_ID, user.getId());
									jo.put(SocketManage.RECEIVER_USER_ID, destoryMsg.getReceiverUid());
									jo.put(SocketManage.MESSAGE,msg);
									jo.put(SocketManage.MESSAGE_ID, msg.getString(SocketManage.MESSAGE_ID));
									ac.sendMessage(jo.toString());
									
									destoryMsg.setResendCount(destoryMsg.getResendCount()+1);
									helper.getDfMessageDao().update(destoryMsg);
								}else{
									destoryMsg.setDownload(SocketManage.D_destroy);
									helper.getDfMessageDao().update(destoryMsg);
									
									Intent i = new Intent(ChatActivity.CHANGESEND);
									i.putExtra("bean", destoryMsg);
									context.sendBroadcast(i);
								}
							}
						}
					}else{
						Intent i = new Intent(BroadcastUtil.ACTION_RECONNECT);
						context.startService(i);
					}
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			if(!netWorkUtil.hasInternet()){
				ac.hasNet=false;
//				Log.d(TAG, String.valueOf(ac.hasNet));
			}else{
				if(!ac.hasNet){
					ac.hasNet=true;
					Intent i = new Intent(BroadcastUtil.ACTION_CHECKCONNECT);
					context.sendBroadcast(i);
//					Log.d(TAG, String.valueOf(ac.hasNet));
				}
			}
		}
	}

}