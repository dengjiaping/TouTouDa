package com.quanliren.quan_two.util;

public class URL {
//	public static final String URL="http://182.92.158.55";
//	public static final String IP="182.92.158.55";
//	public static final String URL="http://192.168.1.21:8888";
//	public static final String IP="192.168.1.21";
	
	public static final String URL="http://192.168.1.8:8080";
	public static final String IP="192.168.1.8";
//	public static final String URL="http://www.bjqlr.com";
//	public static final String IP="www.bjqlr.com";
	public static final Integer PORT=30003;
	
	
	public static final String STATUS="status";
	public static final String RESPONSE="responses";
	public static final String INFO="info";
	public static final String PAGEINDEX="p";
	public static final String TOTAL="total";
	public static final String COUNT="count";
	public static final String LIST="list";
	
	/**城市列表**/
	public static final String CITYS=URL+"/client/get_citys.php";
	/**首页轮播**/
	public static final String AD=URL+"/client/get_activity_imgs.php";
	/**首页列表**/
	public static final String HOME_USERLIST=URL+"/client/user/home_list.php";
	/**注册第一步填写手机号**/
	public static final String REG_FIRST=URL+"/client/send_user_mobile.php";
	/**注册第二步填写验证码**/
	public static final String REG_SENDCODE=URL+"/client/send_user_auth.php";
	/**注册第三步填写基本信息**/
	public static final String REG_THIRD=URL+"/client/send_reg_info.php";
	/**找回密码第一步**/
	public static final String FINDPASSWORD_FIRST=URL+"/client/forget_pwd_one.php";
	/**找回密码第二部**/
	public static final String FINDPASSWORD_SECOND=URL+"/client/forget_pwd_two.php";
	/**修改密码**/
	public static final String MODIFYPASSWORD=URL+"/client/user/alert_pwd.php";
	/**退出**/
	public static final String LOGOUT=URL+"/client/logout.php";
	/**登陆**/
	public static final String LOGIN=URL+"/client/user_login.php";
	/**编辑用户信息**/
	public static final String EDIT_USER_INFO=URL+"/client/user/edit_info.php";
	/**上传用户头像**/
	public static final String UPLOAD_USER_LOGO=URL+"/client/user/img/avatar/upload.php";
	/**获取用户详细信息**/
	public static final String GET_USER_INFO=URL+"/get_user_detail.php";
	/**上传相册**/
	public static final String UPLOAD_ALBUM_IMG=URL+"/client/user/img/album/upload.php";
	/**删除动态**/
	public static final String DELETE_DONGTAI=URL+"/client/user/dynamic/update_dy.php";
	/**删除约会**/
	public static final String DELETE_DATE=URL+"/client/user/dating/cancel_dating.php";
	/**获取用户详细信息**/
	public static final String SET_USERLOGO=URL+"/client/user/setting_avatar.php";
	/**删除头像**/
	public static final String DELETE_USERLOGO=URL+"/client/user/img/avatar/update_num.php";
	/**发表动态**/
	public static final String PUBLISH_TXT=URL+"/client/user/dynamic/pub_text.php";
	/**发表群动态**/
	public static final String PUBLISH_GROUP_TXT=URL+"/client/user/dynamic/crowd_pub_text.php";
	/**评论我的**/
	public static final String COMMENT_ME=URL+"/client/user/dynamic/comment_my_new.php";
	/**发表动态图片**/
	public static final String PUBLISH_IMG=URL+"/client/user/dynamic/pub_img.php";
	/**关注**/
	public static final String CONCERN=URL+"/client/user/atten/concern_he.php";
	/**取消关注**/
	public static final String CANCLECONCERN=URL+"/client/user/atten/cancel_atten.php";
	/**关注列表**/
	public static final String CONCERNLIST=URL+"/client/user/atten/concern_list.php";
	/**获取联系方式**/
	public static final String GETCONTACT=URL+"/client/user/contact_he.php";
	/**获取附近的人列表**/
	public static final String NearUserList=URL+"/nearby_user_list.php";
	/**穿越列表**/
	public static final String THROUGHUSERLIST=URL+"/client/user/roam_list.php";
	/**动态**/
	public static final String DONGTAI=URL+"/dy_list.php";
	/**好友动态**/
	public static final String DONGTAI_FRIEND=URL+"/client/user/dynamic/friend_dylist.php";
	/**约会列表**/
	public static final String DATE_LIST=URL+"/c_dating_list.php";
	/**我的约会列表**/
	public static final String MY_DATE_LIST=URL+"/client/user/dating/my_dtlist.php";
	/**我参加的约会列表**/
	public static final String MY_APPLY_DATE_LIST=URL+"/client/user/dating/my_apply_dtlist.php";
	/**报名管理列表**/
	public static final String DATE_APPLY_MANAGE=URL+"/client/user/dating/dt_apply_users.php";
	/**选择某人**/
	public static final String DATE_CHOSE_SOMEONE=URL+"/client/user/dating/choose_he.php";
	/**约会详情**/
	public static final String DATE_DETAIL=URL+"/dating_detail.php";
	/**约会报名**/
	public static final String DATE_APPLY=URL+"/client/user/dating/apply_dating.php";
	/**设置用户状态**/
	public static final String SET_STATE=URL+"/client/user/setting/user_state.php";
	/**个人动态**/
	public static final String PERSONALDONGTAI=URL+"/user_dy_list.php";
	/**我收藏的约会**/
	public static final String DATE_MY_FAVORITE=URL+"/client/user/dating/my_collect_dtlist.php";
	/**删除动态**/
	public static final String DELETEDONGTAI=URL+"/client/user/dynamic/update_dy.php";
	/**留言**/
	public static final String LEAVEMESSAGE=URL+"/client/user/prv/send_general_msg.php";
	/**创建群**/
	public static final String CREATEGROUP=URL+"/client/user/crowd/create_info.php";
	/**群头像**/
	public static final String CREATEGROUP_IMG=URL+"/client/user/crowd/create_img.php";
	/**获取群列表**/
	public static final String GETGROUPLIST=URL+"/client/user/crowd/crowd_list.php";
	/**获取群资料**/
	public static final String GETGROUPDETAIL=URL+"/client/user/crowd/crowd_detail.php";
	/**获取群成员**/
	public static final String GETGROUPMEMBERLIST=URL+"/client/user/crowd/member_list.php";
	/**设置或删除成员**/
	public static final String AMENTMEMBER=URL+"/client/user/crowd/amend_member.php";
	/**申请加入群**/
	public static final String JOINGROUP=URL+"/client/user/crowd/apply_crowd.php";
	/**邀请加入群**/
	public static final String INVITEGROUP=URL+"/client/user/crowd/invite_crowd.php";
	/**私信列表**/
	public static final String MESSAGELIST=URL+"/client/user/prv/msg_list.php";
	/**群消息列表**/
	public static final String GROUPMESSAGELIST=URL+"/client/user/prv/crowd_msgs.php";
	/**同意拒绝**/
	public static final String AGREEORNOT=URL+"/client/user/prv/dispose_info.php";
	/**删除消息**/
	public static final String DELETEMESSAGE=URL+"/client/user/prv/del_msg.php";
	/**删除群消息**/
	public static final String DELETEGROUPMESSAGE=URL+"/client/user/prv/del_crowd_msg.php";
	/**与某人私信列表**/
	public static final String CHATLISTWITHSOMEONE=URL+"/client/user/prv/member_msgs.php";
	/**群相册**/
	public static final String GROUPPHOTOLIST=URL+"/client/user/crowd/photo_list.php";
	/**上传群图片**/
	public static final String UPLOADGROUPPHOTO=URL+"/client/user/crowd/upload_photo.php";
	/**退出群**/
	public static final String EXITGROUP=URL+"/client/user/crowd/logout_crowd.php";
	/**删除群图片**/
	public static final String DELETEGROUPPHOTO=URL+"/client/user/crowd/del_photo.php";
	/**获取动态详情**/
	public static final String GETDONGTAI_DETAIL=URL+"/dynamic_detail.php";
	/**评论**/
	public static final String REPLY_DONGTAI=URL+"/client/user/dynamic/reply_dy.php";
	/**评论约会**/
	public static final String REPLY_DATE=URL+"/client/user/dating/reply_dating.php";
	/**获取支付宝单号**/
	public static final String GETALIPAY=URL+"/client/pay/build_alipay.php";
	/**举报并拉黑**/
	public static final String JUBAOANDBLACK=URL+"/client/user/black/report_and_black.php";
	/**加入黑名单**/
	public static final String ADDTOBLACK=URL+"/client/user/black/add_black.php";
	/**取消黑名单**/
	public static final String CANCLEBLACK=URL+"/client/user/black/cancel_black.php";
	/**黑名单列表**/
	public static final String BLACKLIST=URL+"/client/user/black/black_list.php";
	/**我兑换的记录**/
	public static final String MYEXCHANGELIST=URL+"/client/user/exch/my_list.php";
	/**删除兑换**/
	public static final String DELETEMYEXCHANGE=URL+"/client/user/exch/del_exch.php";
	/**访客记录**/
	public static final String VISITLIST=URL+"/client/user/visit/v_list.php";
	/**删除访客记录**/
	public static final String DELETE_VISITLIST=URL+"/client/user/visit/del_visit.php";
	/**发送语音图片**/
	public static final String SENDFILE=URL+"/client/msg/send_file_msg.php";
	/**获取广告图片**/
	public static final String ADBANNER=URL+"/client/get_act_banner.php";
	/**赠送体力**/
	public static final String GIVETILI=URL+"/client/user/to_give_power.php";
	/**我的物品**/
	public static final String MY_PRO=URL+"/client/user/get_my_goods.php";
	/**退还**/
	public static final String EXCHANGE_MONEY=URL+"/client/user/exchange_apply.php";
	/**抽奖号码**/
	public static final String PRONUM=URL+"/client/user/get_lottery_codes.php";
	/**删除单号**/
	public static final String DELETENUM=URL+"/client/user/activity/del_code.php";
	/**统计**/
	public static final String TONGJI=URL+"/reg_channel.php";
	/**上传背景**/
	public static final String UPLOAD_USERINFO_BG=URL+"/client/user/img/avatar/up_bgimg.php";
	/**查找好友**/
	public static final String SEARCH_FRIEND=URL+"/client/user/atten/find_friends.php";
	/**发布约会**/
	public static final String PUB_DATA=URL+"/client/user/dating/pub_dating.php";
	/**收藏约会**/
	public static final String DATE_COLLECT=URL+"/client/user/dating/collect_dating.php";
	/**检查更新**/
	public static final String CHECK_VERSION=URL+"/upgrade_version.php";
	/**兌換商品列表**/
	public static final String PRODUCT_LIST=URL+"/goods_list.php";
	/**兑换商品详情**/
	public static final String PRODUCT_DETAIL=URL+"/goods_detail.php";
	/**兑换商品填写信息**/
	public static final String APPLY_EXCHANGE=URL+"/client/user/exch/apply_exch.php";
	/**表情下载列表**/
	public static final String EMOTICON_DOWNLOAD_LIST=URL+"/client/user/phiz/get_phiz_list.php";
	/**表情详情**/
	public static final String EMOTICON_DETAIL=URL+"/client/user/phiz/get_phiz_detail.php";
	/**下载表情**/
	public static final String DOWNLOAD_EMOTICON_FIRST=URL+"/client/user/phiz/ready_down.php";
	/**表情管理**/
	public static final String EMOCTION_MANAGE=URL+"/client/user/phiz/buy_phiz_list.php";
	/**发送错误日志**/
	public static final String SEND_LOG=URL+"/uploading_log.php";
	
}
