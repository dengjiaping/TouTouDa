package com.quanliren.quan_two.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.quanliren.quan_two.bean.emoticon.EmoticonActivityListBean.EmoticonZip.EmoticonJsonBean;

import java.io.Serializable;

@DatabaseTable(tableName = "ChatListBean")
public class ChatListBean implements Serializable {
    public static final String TableName = "ChatListBean";
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String userid;
    @DatabaseField
    private String friendid;
    @DatabaseField
    private String content;
    @DatabaseField
    private String ctime;
    @DatabaseField
    private String userlogo;
    @DatabaseField
    private String nickname;


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

    private User friend;
    private int msgCount = 0;

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    public ChatListBean() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ChatListBean(User user, DfMessage msg) {
        this.userid = user.getId();
        this.friendid = msg.getSendUid().equals(user.getId()) ? msg.getReceiverUid() : msg.getSendUid();
        this.ctime = msg.getCtime();
        this.userlogo = msg.getUserlogo();
        this.nickname = msg.getNickname();
        switch (msg.getMsgtype()) {
            case 0:
                this.content = msg.getContent();
                break;
            case 1:
                this.content = "[图片]";
                break;
            case 2:
                this.content = "[语音]";
                break;
            case 5:
                this.content = msg.getGifContent().flagName;
                break;
            case 4:
                this.content = msg.getOtherContent().getText();
                break;
            default:
                break;
        }

    }

    public ChatListBean(User user, DfMessage msg, User friend) {
        this.userid = user.getId();
        this.friendid = friend.getId();
        this.ctime = msg.getCtime();
        this.userlogo = friend.getAvatar();
        this.nickname = friend.getNickname();
        switch (msg.getMsgtype()) {
            case 0:
                this.content = msg.getContent();
                break;
            case 1:
                this.content = "[图片]";
                break;
            case 2:
                this.content = "[语音]";
                break;
            case 5:
                EmoticonJsonBean ejb = msg.getGifContent();
                this.content = ejb.getFlagName();
                break;
            default:
                break;
        }

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFriendid() {
        return friendid;
    }

    public void setFriendid(String friendid) {
        this.friendid = friendid;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }
}
