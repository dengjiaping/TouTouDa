package com.quanliren.quan_two.activity.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseUserActivity;
import com.quanliren.quan_two.activity.group.PhotoAlbumMainActivity_;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.bean.UserTable;
import com.quanliren.quan_two.custom.NumberProgressBar;
import com.quanliren.quan_two.custom.RoundProgressBar;
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
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;

@EActivity(R.layout.user_info)
@OptionsMenu(R.menu.userinfo_menu)
public class UserInfoActivity extends BaseUserActivity implements
        OnRefreshListener {

    public static final String USERINFO_UPDATE_UI = "com.quanliren.quan_two.activity.user.UserInfoActivity.USERINFO_UPDATE_UI";

    private static final int CAMERA_USERLOGO = 1;
    private static final int ALBUM_USERLOGO = 2;
    private static final int EDITUSERINFO = 3;
    private static final int WALLPIC = 0;
    private static final int USERLOGO = 1;
    private static final int IMG = 2;

    @ViewById
    RoundProgressBar loadProgressBar;
    @ViewById
    NumberProgressBar numberbar1;

    private int uploadImgType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

    }

    @Receiver(actions = USERINFO_UPDATE_UI)
    public void receiver(Intent i) {
        String action = i.getAction();
        if (action.equals(USERINFO_UPDATE_UI)) {
            user = getHelper().getUserInfo();
            initViewByUser();
            if (user != null && !Util.isStrNotNull(user.getAvatar())) {
                userlogo.setImageResource(R.drawable.default_userlogo);
            }
        }
    }

    @OptionsItem
    void edit() {
        UserInfoEditActivity_.intent(this).startForResult(EDITUSERINFO);
    }

    @OnActivityResult(EDITUSERINFO)
    void editResult(int result) {
        if (result == 1) {
            layout.setEnabled(true);
            layout.setRefreshing(true, true);
        }
    }

    @AfterViews
    protected void initView() {

        super.initView();
        initPull();
        try {
            user = getHelper().getUserInfo();

            initViewByUser();
            if (user != null && !Util.isStrNotNull(user.getAvatar())) {
                userlogo.setImageResource(R.drawable.default_userlogo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @UiThread(delay = 500)
    void initPull() {
        ActionBarPullToRefresh.from(this).allChildrenArePullable()
                .listener(this).setAutoStart(true).setup(layout);
    }

    public void getUserDetail() {
        RequestParams rp = getAjaxParams();
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
                                if (user != null
                                        && !Util.isStrNotNull(user.getAvatar())) {
                                    userlogo.setImageResource(R.drawable.default_userlogo);
                                }
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

    @Override
    public void onRefreshStarted(View view) {
        getUserDetail();
    }

    String cameraUserLogo;

    @Click
    void userlogo() {
        uploadImgType = USERLOGO;
        uploadImg();
    }

    @OnActivityResult(CAMERA_USERLOGO)
    void onCameraUserLogoResult(int result, Intent data) {
        try {
            if (cameraUserLogo != null) {
                File fi = new File(cameraUserLogo);
                if (fi != null && fi.exists()) {
                    ImageUtil.downsize(cameraUserLogo, cameraUserLogo, this);
                    switch (uploadImgType) {
                        case WALLPIC:
                            ImageLoader.getInstance().displayImage(
                                    Util.FILE + cameraUserLogo, mHeadImg);
                            break;
                        case USERLOGO:
                            ImageLoader.getInstance().displayImage(
                                    Util.FILE + cameraUserLogo, userlogo);
                            break;
                    }
                    switch (uploadImgType) {
                        case 0:
                            uploadWallPic(fi);
                            break;
                        case 1:
                            uploadUserLogo(fi);
                            break;
                        case 2:

                            break;
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void uploadUserLogo(final File file) {
        try {
            RequestParams ap = getAjaxParams();
            ap.put("file", file);
            ac.finalHttp.post(URL.UPLOAD_USER_LOGO, ap,
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onProgress(int bytesWritten, int totalSize) {
                            // int progress = (int) (((float) bytesWritten /
                            // (float) totalSize) * 100);
                            if (bytesWritten > 0 && bytesWritten < totalSize) {
                                loadProgressBar.setVisibility(View.VISIBLE);
                                loadProgressBar.setMax(totalSize);
                                loadProgressBar.setProgress(bytesWritten);
                            }
                        }

                        @Override
                        public void onSuccess(JSONObject response) {
                            showCustomToast("上传成功");
                            try {
                                ImageLoader.getInstance().displayImage(
                                        response.getJSONObject(URL.RESPONSE)
                                                .getString("imgurl")
                                                + StaticFactory._320x320,
                                        userlogo, ac.options_no_default);
                                user.setAvatar(response.getJSONObject(
                                        URL.RESPONSE).getString("imgurl"));
                                UserTable ut = new UserTable(user);
                                userTableDao.delete(ut);
                                userTableDao.create(ut);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } finally {
                                loadProgressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure() {
                            AlertDialog dialog = new AlertDialog.Builder(
                                    UserInfoActivity.this)
                                    .setTitle("提示")
                                    .setMessage("上传失败，是否重试？")
                                    .setNegativeButton(
                                            "取消",
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {

                                                }
                                            })
                                    .setPositiveButton(
                                            "确定",
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    uploadUserLogo(file);
                                                }
                                            }).create();
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                        }
                    });
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void uploadWallPic(final File file) {
        try {
            RequestParams ap = getAjaxParams();
            ap.put("file", file);
            ac.finalHttp.post(URL.UPLOAD_USERINFO_BG, ap,
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onProgress(int bytesWritten, int totalSize) {
                            // int progress = (int) (((float) bytesWritten /
                            // (float) totalSize) * 100);
                            if (bytesWritten > 0 && bytesWritten < totalSize) {
                                numberbar1.setVisibility(View.VISIBLE);
                                numberbar1.setMax(totalSize);
                                numberbar1.setProgress(bytesWritten);
                            }
                        }

                        @Override
                        public void onSuccess(JSONObject response) {
                            showCustomToast("上传成功");
                            try {
                                ImageLoader.getInstance().displayImage(
                                        response.getJSONObject(URL.RESPONSE)
                                                .getString("imgurl")
                                                + StaticFactory._960x720,
                                        mHeadImg, ac.options_no_default);
                                user.setBgimg(response.getJSONObject(
                                        URL.RESPONSE).getString("imgurl"));

                                UserTable ut = new UserTable(user);
                                userTableDao.delete(ut);
                                userTableDao.create(ut);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                numberbar1.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure() {
                            AlertDialog dialog = new AlertDialog.Builder(
                                    UserInfoActivity.this)
                                    .setTitle("提示")
                                    .setMessage("上传失败，是否重试？")
                                    .setNegativeButton(
                                            "取消",
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {

                                                }
                                            })
                                    .setPositiveButton(
                                            "确定",
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    uploadWallPic(file);
                                                }
                                            }).create();
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                        }
                    });
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @OnActivityResult(ALBUM_USERLOGO)
    void onAlbumUserLogoResult(int result, Intent data) {
        if (data == null) {
            return;
        }
        ArrayList<String> list = data.getStringArrayListExtra("images");
        if (list.size() > 0) {
            cameraUserLogo = list.get(0);
            File fi = new File(cameraUserLogo);
            if (fi != null && fi.exists()) {
                ImageUtil.downsize(cameraUserLogo, cameraUserLogo, this);
                switch (uploadImgType) {
                    case WALLPIC:
                        ImageLoader.getInstance().displayImage(
                                Util.FILE + cameraUserLogo, mHeadImg);
                        break;
                    case USERLOGO:
                        ImageLoader.getInstance().displayImage(
                                Util.FILE + cameraUserLogo, userlogo);
                        break;
                }
                switch (uploadImgType) {
                    case 0:
                        uploadWallPic(fi);
                        break;
                    case 1:
                        uploadUserLogo(fi);
                        break;
                    case 2:

                        break;
                }
            }
        }
    }

    @Click
    void bg_click_ll() {
        uploadImgType = WALLPIC;
        uploadImg();
    }

    public void uploadImg() {
        AlertDialog dialog = new AlertDialog.Builder(this).setItems(
                new String[]{"相册", "拍照"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 1:
                                if (Util.existSDcard()) {
                                    Intent intent = new Intent(); // 调用照相机
                                    String messagepath = StaticFactory.APKCardPath;
                                    File fa = new File(messagepath);
                                    if (!fa.exists()) {
                                        fa.mkdirs();
                                    }
                                    cameraUserLogo = messagepath
                                            + new Date().getTime();// 图片路径
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(new File(cameraUserLogo)));
                                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(intent, CAMERA_USERLOGO);
                                } else {
                                    showCustomToast("亲，请检查是否安装存储卡!");
                                }
                                break;
                            case 0:
                                if (Util.existSDcard()) {
                                    String messagepath = StaticFactory.APKCardPath;
                                    File fa = new File(messagepath);
                                    if (!fa.exists()) {
                                        fa.mkdirs();
                                    }
                                    switch (uploadImgType) {
                                        case IMG:
                                            if (user.getIsvip() == 1)
                                                PhotoAlbumMainActivity_
                                                        .intent(UserInfoActivity.this)
                                                        .maxnum(16)
                                                        .startForResult(ALBUM_USERLOGO);
                                            else
                                                PhotoAlbumMainActivity_
                                                        .intent(UserInfoActivity.this)
                                                        .maxnum(16)
                                                        .startForResult(ALBUM_USERLOGO);
                                            break;
                                        default:
                                            PhotoAlbumMainActivity_
                                                    .intent(UserInfoActivity.this)
                                                    .maxnum(1)
                                                    .startForResult(ALBUM_USERLOGO);
                                            break;
                                    }
                                } else {
                                    showCustomToast("亲，请检查是否安装存储卡!");
                                }
                                break;
                        }
                    }
                }).create();
        switch (uploadImgType) {
            case WALLPIC:
                dialog.setTitle("更换背景");
                break;
            case USERLOGO:
                dialog.setTitle("更换头像");
            default:
                break;
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void addPic() {
        uploadImgType = IMG;
    }

}
