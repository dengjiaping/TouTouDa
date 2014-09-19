package com.quanliren.quan_two.util;

public class URL {
    //	public static final String URL="http://182.92.158.55";
//	public static final String IP="182.92.158.55";
    public static final String URL = "http://192.168.1.21:8888";
    public static final String IP = "192.168.1.21";

    //	public static final String URL="http://192.168.1.9:8080";
//    public static final String IP="192.168.1.9";
//	public static final String URL="http://www.bjqlr.com";
//	public static final String IP="www.bjqlr.com";
    public static final Integer PORT = 30003;


    public static final String STATUS = "status";
    public static final String RESPONSE = "responses";
    public static final String INFO = "info";
    public static final String PAGEINDEX = "p";
    public static final String TOTAL = "total";
    public static final String COUNT = "count";
    public static final String LIST = "list";

    /**
     * 注册第一步填写手机号*
     */
    public static final String REG_FIRST = URL + "/client/send_user_mobile.php";
    /**
     * 注册第二步填写验证码*
     */
    public static final String REG_SENDCODE = URL + "/client/send_user_auth.php";
    /**
     * 注册第三步填写基本信息*
     */
    public static final String REG_THIRD = URL + "/client/send_reg_info.php";
    /**
     * 找回密码第一步*
     */
    public static final String FINDPASSWORD_FIRST = URL + "/client/forget_pwd_one.php";
    /**
     * 找回密码第二部*
     */
    public static final String FINDPASSWORD_SECOND = URL + "/client/forget_pwd_two.php";
    /**
     * 修改密码*
     */
    public static final String MODIFYPASSWORD = URL + "/client/user/alert_pwd.php";
    /**
     * 退出*
     */
    public static final String LOGOUT = URL + "/client/logout.php";
    /**
     * 登陆*
     */
    public static final String LOGIN = URL + "/client/user_login.php";
    /**
     * 编辑用户信息*
     */
    public static final String EDIT_USER_INFO = URL + "/client/user/edit_info.php";
    /**
     * 上传用户头像*
     */
    public static final String UPLOAD_USER_LOGO = URL + "/client/user/img/avatar/upload.php";
    /**
     * 获取用户详细信息*
     */
    public static final String GET_USER_INFO = URL + "/get_user_detail.php";
    /**
     * 上传相册*
     */
    public static final String UPLOAD_ALBUM_IMG = URL + "/client/user/img/album/upload.php";
    /**
     * 删除动态*
     */
    public static final String DELETE_DONGTAI = URL + "/client/user/dynamic/update_dy.php";
    /**
     * 删除约会*
     */
    public static final String DELETE_DATE = URL + "/client/user/dating/cancel_dating.php";
    /**
     * 获取用户详细信息*
     */
    public static final String SET_USERLOGO = URL + "/client/user/setting_avatar.php";
    /**
     * 删除头像*
     */
    public static final String DELETE_USERLOGO = URL + "/client/user/img/avatar/update_num.php";
    /**
     * 发表动态*
     */
    public static final String PUBLISH_TXT = URL + "/client/user/dynamic/pub_text.php";
    /**
     * 评论我的*
     */
    public static final String COMMENT_ME = URL + "/client/user/dynamic/comment_my_new.php";
    /**
     * 发表动态图片*
     */
    public static final String PUBLISH_IMG = URL + "/client/user/dynamic/pub_img.php";
    /**
     * 关注*
     */
    public static final String CONCERN = URL + "/client/user/atten/concern_he.php";
    /**
     * 取消关注*
     */
    public static final String CANCLECONCERN = URL + "/client/user/atten/cancel_atten.php";
    /**
     * 关注列表*
     */
    public static final String CONCERNLIST = URL + "/client/user/atten/concern_list.php";
    /**
     * 获取附近的人列表*
     */
    public static final String NearUserList = URL + "/nearby_user_list.php";
    /**
     * 动态*
     */
    public static final String DONGTAI = URL + "/dy_list.php";
    /**
     * 好友动态*
     */
    public static final String DONGTAI_FRIEND = URL + "/client/user/dynamic/friend_dylist.php";
    /**
     * 约会列表*
     */
    public static final String DATE_LIST = URL + "/c_dating_list.php";
    /**
     * 我的约会列表*
     */
    public static final String MY_DATE_LIST = URL + "/client/user/dating/my_dtlist.php";
    /**
     * 我参加的约会列表*
     */
    public static final String MY_APPLY_DATE_LIST = URL + "/client/user/dating/my_apply_dtlist.php";
    /**
     * 报名管理列表*
     */
    public static final String DATE_APPLY_MANAGE = URL + "/client/user/dating/dt_apply_users.php";
    /**
     * 选择某人*
     */
    public static final String DATE_CHOSE_SOMEONE = URL + "/client/user/dating/choose_he.php";
    /**
     * 约会详情*
     */
    public static final String DATE_DETAIL = URL + "/dating_detail.php";
    /**
     * 约会报名*
     */
    public static final String DATE_APPLY = URL + "/client/user/dating/apply_dating.php";
    /**
     * 设置用户状态*
     */
    public static final String SET_STATE = URL + "/client/user/setting/user_state.php";
    /**
     * 个人动态*
     */
    public static final String PERSONALDONGTAI = URL + "/user_dy_list.php";
    /**
     * 我收藏的约会*
     */
    public static final String DATE_MY_FAVORITE = URL + "/client/user/dating/my_collect_dtlist.php";
    /**
     * 获取动态详情*
     */
    public static final String GETDONGTAI_DETAIL = URL + "/dynamic_detail.php";
    /**
     * 评论*
     */
    public static final String REPLY_DONGTAI = URL + "/client/user/dynamic/reply_dy.php";
    /**
     * 评论约会*
     */
    public static final String REPLY_DATE = URL + "/client/user/dating/reply_dating.php";
    /**
     * 获取支付宝单号*
     */
    public static final String GETALIPAY = URL + "/client/pay/build_alipay.php";
    /**
     * 举报并拉黑*
     */
    public static final String JUBAOANDBLACK = URL + "/client/user/black/report_and_black.php";
    /**
     * 加入黑名单*
     */
    public static final String ADDTOBLACK = URL + "/client/user/black/add_black.php";
    /**
     * 取消黑名单*
     */
    public static final String CANCLEBLACK = URL + "/client/user/black/cancel_black.php";
    /**
     * 黑名单列表*
     */
    public static final String BLACKLIST = URL + "/client/user/black/black_list.php";
    /**
     * 我兑换的记录*
     */
    public static final String MYEXCHANGELIST = URL + "/client/user/exch/my_list.php";
    /**
     * 删除兑换*
     */
    public static final String DELETEMYEXCHANGE = URL + "/client/user/exch/del_exch.php";
    /**
     * 访客记录*
     */
    public static final String VISITLIST = URL + "/client/user/visit/v_list.php";
    /**
     * 删除访客记录*
     */
    public static final String DELETE_VISITLIST = URL + "/client/user/visit/del_visit.php";
    /**
     * 发送语音图片*
     */
    public static final String SENDFILE = URL + "/client/msg/send_file_msg.php";
    /**
     * 赠送体力*
     */
    public static final String GIVETILI = URL + "/client/user/to_give_power.php";
    /**
     * 统计*
     */
    public static final String TONGJI = URL + "/reg_channel.php";
    /**
     * 上传背景*
     */
    public static final String UPLOAD_USERINFO_BG = URL + "/client/user/img/avatar/up_bgimg.php";
    /**
     * 查找好友*
     */
    public static final String SEARCH_FRIEND = URL + "/client/user/atten/find_friends.php";
    /**
     * 发布约会*
     */
    public static final String PUB_DATA = URL + "/client/user/dating/pub_dating.php";
    /**
     * 收藏约会*
     */
    public static final String DATE_COLLECT = URL + "/client/user/dating/collect_dating.php";
    /**
     * 检查更新*
     */
    public static final String CHECK_VERSION = URL + "/upgrade_version.php";
    /**
     * 兌換商品列表*
     */
    public static final String PRODUCT_LIST = URL + "/goods_list.php";
    /**
     * 兑换商品详情*
     */
    public static final String PRODUCT_DETAIL = URL + "/goods_detail.php";
    /**
     * 兑换商品填写信息*
     */
    public static final String APPLY_EXCHANGE = URL + "/client/user/exch/apply_exch.php";
    /**
     * 表情下载列表*
     */
    public static final String EMOTICON_DOWNLOAD_LIST = URL + "/client/user/phiz/get_phiz_list.php";
    /**
     * 表情详情*
     */
    public static final String EMOTICON_DETAIL = URL + "/client/user/phiz/get_phiz_detail.php";
    /**
     * 下载表情*
     */
    public static final String DOWNLOAD_EMOTICON_FIRST = URL + "/client/user/phiz/ready_down.php";
    /**
     * 表情管理*
     */
    public static final String EMOCTION_MANAGE = URL + "/client/user/phiz/buy_phiz_list.php";
    /**
     * 发送错误日志*
     */
    public static final String SEND_LOG = URL + "/uploading_log.php";

}
