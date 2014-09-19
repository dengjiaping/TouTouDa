package com.quanliren.quan_two.activity.group;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.activity.location.GDLocation;
import com.quanliren.quan_two.activity.location.ILocationImpl;
import com.quanliren.quan_two.bean.LoginUser;
import com.quanliren.quan_two.custom.CustomRelativeLayout;
import com.quanliren.quan_two.custom.emoji.EmoteView;
import com.quanliren.quan_two.custom.emoji.EmoticonsEditText;
import com.quanliren.quan_two.fragment.custom.AddPicFragment;
import com.quanliren.quan_two.fragment.custom.AddPicFragment.OnArticleSelectedListener;
import com.quanliren.quan_two.util.EmojiFilter;
import com.quanliren.quan_two.util.StaticFactory;
import com.quanliren.quan_two.util.URL;
import com.quanliren.quan_two.util.Util;
import com.quanliren.quan_two.util.http.JsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

@EActivity
@OptionsMenu(R.menu.filter_people_menu)
public class PublishActivity extends BaseActivity implements
        OnArticleSelectedListener, ILocationImpl {

    @FragmentById(R.id.chat_eiv_inputview)
    EmoteView gridview;
    @ViewById
    View chat_layout_emote;
    @ViewById(R.id.text)
    EmoticonsEditText edittxt;
    AddPicFragment fragment;
    @ViewById
    View add_emoji_btn;
    GDLocation location;
    @ViewById
    CustomRelativeLayout rlayout;

    ImageView tempImageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoader.getInstance().stop();
        setContentView(R.layout.publish);
        // setTitleRightTxt("发表");
        getSupportActionBar().setTitle("发表");
        setListener();
        location = new GDLocation(this, this, false);
    }

    public void setListener() {

        gridview.setEditText(edittxt);

        edittxt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                chat_layout_emote.setVisibility(View.GONE);
            }
        });
        fragment = (AddPicFragment) getSupportFragmentManager()
                .findFragmentById(R.id.picFragment);
    }

    public void add_pic_btn(View v) {
        tempImageView = (ImageView) v;
        chat_layout_emote.setVisibility(View.GONE);
        if (v.getTag().toString().equals(AddPicFragment.DEFAULT)) {
            AlertDialog dialog = new AlertDialog.Builder(this).setItems(
                    new String[]{"相册", "拍照"},
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            menuClick(which);
                        }
                    }).create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } else {
            AlertDialog dialog = new AlertDialog.Builder(this).setItems(
                    new String[]{"删除"},
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            menuDeleteClick();
                        }
                    }).create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }
        closeInput();
    }

    public void menuClick(int which) {
        switch (which) {
            case 1:
                if (Util.existSDcard()) {
                    Intent intent = new Intent(); // 调用照相机
                    String messagepath = StaticFactory.APKCardPath;
                    File fa = new File(messagepath);
                    if (!fa.exists()) {
                        fa.mkdirs();
                    }
                    fragment.cameraPath = messagepath + new Date().getTime();// 图片路径
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(fragment.cameraPath)));
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, AddPicFragment.Camera);
                } else {
                    Toast.makeText(getApplicationContext(), "亲，请检查是否安装存储卡!",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case 0:
                if (Util.existSDcard()) {
                    String messagepath = StaticFactory.APKCardPath;
                    File fa = new File(messagepath);
                    if (!fa.exists()) {
                        fa.mkdirs();
                    }
                    PhotoAlbumMainActivity_.intent(this).maxnum(6)
                            .paths(fragment.getSdibs())
                            .startForResult(AddPicFragment.Album);
                } else {
                    Toast.makeText(getApplicationContext(), "亲，请检查是否安装存储卡!",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void menuDeleteClick() {
        fragment.removeByView(tempImageView);
    }

    @Click
    public void add_emoji_btn(View v) {
        if (chat_layout_emote.getVisibility() == View.VISIBLE) {
            chat_layout_emote.setVisibility(View.GONE);
            edittxt.requestFocus();
            showKeyBoard();
        } else {
            rlayout.setHideView(chat_layout_emote);
            closeInput();
        }
    }

    public void onBackPressed() {
        if (chat_layout_emote.getVisibility() == View.VISIBLE) {
            edittxt.requestFocus();
            chat_layout_emote.setVisibility(View.GONE);
            showKeyBoard();
            return;
        } else {
            dialogFinish();
        }
    }

    @Override
    public void finishActivity() {
        dialogFinish();
    }

    public void dialogFinish() {
        new AlertDialog.Builder(PublishActivity.this)
                .setMessage("您确定要放弃本次编辑吗？").setTitle("提示")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        scrollToFinishActivity();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                }).create().show();
    }

    @OptionsItem(R.id.ok)
    public void rightClick() {
        String content = Util.FilterEmoji(edittxt.getText().toString().trim());
        // content=EmojiFilter.filterEmoji(content);
        if (!Util.isStrNotNull(content) && fragment.getCount() == 0) {
            showCustomToast("请输入内容或添加图片");
            return;
        }
        customShowDialog("正在定位");
        location.startLocation();
    }

    JsonHttpResponseHandler callBack = new JsonHttpResponseHandler() {
        public void onStart() {
            customShowDialog("正在上传");
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
                        if (fragment.getCount() > 0) {
                            dyid = jo.getJSONObject(URL.RESPONSE).getString("dyid");
                            uploadImg(0);
                        } else {
                            showCustomToast("发表成功");
                            setResult(1);
                            finish();
                        }
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

    private String dyid;

    public void uploadImg(int i) {
        if (i == fragment.getCount()) {
            return;
        }
        LoginUser user = getHelper().getUser();
        try {
            RequestParams ap = getAjaxParams();
            ap.put("file", new File(fragment.getItem(i)));
            ap.put("userid", user.getId());
            ap.put("dyid", dyid);
            ap.put("position", i + "");
            ac.finalHttp.post(URL.PUBLISH_IMG, ap, new callBack(i));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    class callBack extends JsonHttpResponseHandler {

        int index = 0;

        public callBack(int index) {
            this.index = index;
        }

        public void onFailure() {
            customDismissDialog();
            showIntentErrorToast();
        }

        ;

        public void onStart() {
            customShowDialog("正在上传第" + (index + 1) + "张图片");
        }

        ;

        public void onSuccess(JSONObject jo) {
            customDismissDialog();
            try {
                int status = jo.getInt(URL.STATUS);
                switch (status) {
                    case 0:
                        if (index == (fragment.getCount() - 1)) {
                            showCustomToast("上传成功");
                            setResult(1);
                            finish();
                        } else {
                            uploadImg(index + 1);
                        }
                        break;
                    default:
                        showCustomToast(jo.getJSONObject(URL.RESPONSE).getString(
                                URL.INFO));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (String str : fragment.getIbs()) {
            File file = new File(str);
            if (file.exists()) {
                file.delete();
            }
        }
        location.destory();
    }

    @Override
    public void onArticleSelected(View articleUri) {
        add_pic_btn(articleUri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fragment.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onLocationSuccess() {
        customDismissDialog();
        LoginUser user = getHelper().getUser();

        if (user != null) {
            RequestParams ap = getAjaxParams();
            ap.put("content",
                    EmojiFilter.filterEmoji(edittxt.getText().toString()));
            ap.put("area", ac.cs.getArea());
            ap.put("longitude", ac.cs.getLng());
            ap.put("latitude", ac.cs.getLat());
            ac.finalHttp.post(URL.PUBLISH_TXT, ap, callBack);
        }
    }

    @Override
    public void onLocationFail() {
        showCustomToast("定位失败");
    }
}
