package com.quanliren.quan_two.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.quanliren.quan_two.bean.emoticon.EmoticonActivityListBean.EmoticonZip.EmoticonJsonBean;
import com.quanliren.quan_two.service.SocketManage;
import com.quanliren.quan_two.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "DfMessage")
public class DfMessage implements Serializable {
    public static final String TABLENAME = "DfMessage";
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(index = true)
    private String msgid;
    @DatabaseField(index = true)
    private String userid;
    @DatabaseField(index = true)
    private String receiverUid;
    @DatabaseField(index = true)
    private String sendUid;
    @DatabaseField
    private String content;
    @DatabaseField(index = true)
    private int isRead = 0;// 是否已读
    @DatabaseField(index = true)
    private String ctime;// 信息发送时间
    private boolean showTime = false;// 是否显示信息
    @DatabaseField(index = true)
    private int msgtype = 0;// 0、文字 1、图片 2、语音
    @DatabaseField(index = true)
    private int download = 0;// 0 未下载 1已下载
    @DatabaseField
    private int timel = 0;// 语音长度
    @DatabaseField
    private String userlogo;
    @DatabaseField
    private String nickname;
    @DatabaseField
    private int resendCount = 0;


    public int getResendCount() {
        return resendCount;
    }

    public void setResendCount(int resendCount) {
        this.resendCount = resendCount;
    }

    public int getDownload() {
        return download;
    }

    public void setDownload(int download) {
        this.download = download;
    }

    private boolean playing;

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }


    public String getUserlogo() {
        return userlogo;
    }

    public void setUserlogo(String userlogo) {
        this.userlogo = userlogo;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }


    public void setMsgtype(Integer msgtype) {
        this.msgtype = msgtype;
    }

    public int getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(int msgtype) {
        this.msgtype = msgtype;
    }

    public DfMessage() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static JSONObject getMessage(User user, String content, User friend,
                                        int msgtype, int timel) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("content", content);
            /*try {
				java.net.URL url = new java.net.URL("http://www.bjtime.cn");// 取得资源对象
				URLConnection uc = url.openConnection();// 生成连接对象
				uc.connect(); // 发出连接
				long ld = uc.getDate(); // 取得网站日期时间
				Date date = new Date(ld); // 转换为标准时间对象
				msg.put("ctime", Util.fmtDateTime.format(date));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg.put("ctime", Util.fmtDateTime.format(new Date()));
			}*/
            msg.put("ctime", Util.fmtDateTime.format(new Date()));
            msg.put("receiverUid", friend.getId());
            msg.put("userid", user.getId());
            msg.put("timel", timel);
            msg.put("userlogo", user.getAvatar());
            msg.put("nickname", user.getNickname());
            msg.put("sendUid", user.getId());
//			msg.put("download", 1);
            msg.put("msgtype", msgtype);
            msg.put(SocketManage.MESSAGE_ID, String.valueOf(new Date().getTime()));
            return msg;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getMessage(DfMessage d) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("content", d.getContent());
            msg.put("ctime", d.getCtime());
            msg.put("receiverUid", d.getReceiverUid());
            msg.put("userid", d.getUserid());
            msg.put("timel", d.getTimel());
            msg.put("userlogo", d.getUserlogo());
            msg.put("nickname", d.getNickname());
            msg.put("sendUid", d.getSendUid());
            msg.put("msgtype", d.getMsgtype());
            msg.put(SocketManage.MESSAGE_ID, d.getMsgid());
            return msg;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public DfMessage(int id, String msgid, String userid, String receiverUid,
                     String sendUid, String content, int isRead, String ctime,
                     boolean showTime, int msgtype, int download, int timel,
                     String userlogo, String nickname) {
        super();
        this.id = id;
        this.msgid = msgid;
        this.userid = userid;
        this.receiverUid = receiverUid;
        this.sendUid = sendUid;
        this.content = content;
        this.isRead = isRead;
        this.ctime = ctime;
        this.showTime = showTime;
        this.msgtype = msgtype;
        this.download = download;
        this.timel = timel;
        this.userlogo = userlogo;
        this.nickname = nickname;
    }

    public int getTimel() {
        return timel;
    }

    public void setTimel(int timel) {
        this.timel = timel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getSendUid() {
        return sendUid;
    }

    public void setSendUid(String sendUid) {
        this.sendUid = sendUid;
    }

    public String getContent() {
        return content;
    }

    public EmoticonJsonBean getGifContent() {
        return new Gson().fromJson(content, new TypeToken<EmoticonJsonBean>() {
        }.getType());
    }

    public OtherMessage getOtherContent() {
        return new Gson().fromJson(content, new TypeToken<OtherMessage>() {
        }.getType());
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static class OtherMessage implements Serializable {
        private int infoType;
        private String dyid;
        private String dcid;
        private String dtid;
        private String text;

        public OtherMessage(int infoType, String dyid, String dcid,
                            String dtid, String text) {
            super();
            this.infoType = infoType;
            this.dyid = dyid;
            this.dcid = dcid;
            this.dtid = dtid;
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public OtherMessage() {
            super();
            // TODO Auto-generated constructor stub
        }

        public int getInfoType() {
            return infoType;
        }

        public void setInfoType(int infoType) {
            this.infoType = infoType;
        }

        public String getDyid() {
            return dyid;
        }

        public void setDyid(String dyid) {
            this.dyid = dyid;
        }

        public String getDcid() {
            return dcid;
        }

        public void setDcid(String dcid) {
            this.dcid = dcid;
        }

        public String getDtid() {
            return dtid;
        }

        public void setDtid(String dtid) {
            this.dtid = dtid;
        }
    }
}
