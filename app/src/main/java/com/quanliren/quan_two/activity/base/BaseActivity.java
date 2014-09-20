package com.quanliren.quan_two.activity.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.shop.*;
import com.quanliren.quan_two.activity.user.*;
import com.quanliren.quan_two.application.AM;
import com.quanliren.quan_two.application.AppClass;
import com.quanliren.quan_two.bean.CacheBean;
import com.quanliren.quan_two.bean.LoginUser;
import com.quanliren.quan_two.bean.UserTable;
import com.quanliren.quan_two.custom.CustomProgressBar;
import com.quanliren.quan_two.db.DBHelper;
import com.quanliren.quan_two.util.URL;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OrmLiteDao;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

@EActivity
public abstract class BaseActivity extends OrmLiteBaseActivity<DBHelper> {

    @App
    public AppClass ac;

    @OrmLiteDao(helper = DBHelper.class, model = UserTable.class)
    public Dao<UserTable, String> userTableDao;

    @OrmLiteDao(helper = DBHelper.class, model = LoginUser.class)
    public Dao<LoginUser, String> loginUserDao;

    @OrmLiteDao(helper = DBHelper.class, model = CacheBean.class)
    public Dao<CacheBean, String> cacheDao;

	/*@OrmLiteDao(helper=DBHelper.class,model=DfMessage.class)
	public Dao<DfMessage, Integer> messageDao;
	
	@OrmLiteDao(helper=DBHelper.class,model=MoreLoginUser.class)
	public Dao<MoreLoginUser, Integer> moreLoginUserDao;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AM.getActivityManager().pushActivity(this);
        super.onCreate(savedInstanceState);
        try {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeInput();
        if (toast != null)
            toast.cancel();
        AM.getActivityManager().popActivity(this);
    }

    @OptionsItem(android.R.id.home)
    public void finishActivity() {
        scrollToFinishActivity();
    }

    public void closeInput() {
        if (getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(), 0);
        }
    }

    protected void showKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    Toast toast = null;

    public void showCustomToast(String str) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(str);
        if (toast == null) {
            toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.setView(layout);
        toast.show();
    }

    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public void showIntentErrorToast() {
        showCustomToast("网络连接失败");
    }

    public CustomProgressBar parentDialog;

    public void customDismissDialog() {
        if (parentDialog != null && parentDialog.isShowing())
            parentDialog.dismiss();
    }

    public void customShowDialog(String str) {
        parentDialog = new CustomProgressBar(this, R.style.dialog);
        parentDialog.setMessage(str);
        parentDialog.show();
    }

    String[] str = new String[]{"", "正在获取数据", "正在登录", "正在提交", "请稍等"};

    public void customShowDialog(int i) {
        parentDialog = new CustomProgressBar(this, R.style.dialog);
        parentDialog.setMessage(str[i]);
        parentDialog.show();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
        }
        return super.onTouchEvent(event);
    }

    public RequestParams getAjaxParams() {
        LoginUser user = getHelper().getUser();
        RequestParams ap = new RequestParams();
        ap.put("versionName", ac.cs.getVersionName());
        ap.put("versionCode", String.valueOf(ac.cs.getVersionCode()));
        ap.put("channel", ac.cs.getChannel());
        ap.put("dtype", "0");
        ap.put("deviceid", ac.cs.getDeviceId());
        if (user != null) {
            ap.put("token", user.getToken());
        }
        return ap;
    }

    public RequestParams getAjaxParams(String str, String strs) {
        RequestParams ap = getAjaxParams();
        ap.put(str, strs);
        return ap;
    }

    public void showFailInfo(JSONObject jo) {
        try {
            int status = jo.getInt(URL.STATUS);
            switch (status) {
                case 1:
                    showCustomToast(jo.getJSONObject(URL.RESPONSE).getString(
                            URL.INFO));
                    break;
                case 2:
                    showCustomToast(jo.getJSONObject(URL.RESPONSE).getString(
                            URL.INFO));
                    loginOut();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void loginOut() {
        try {
            TableUtils.clearTable(getConnectionSource(), LoginUser.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
//		startActivity(new Intent(BaseActivity.this, LoginActivity.class));
//		AM.getActivityManager().popAllActivity();
        startLogin();
    }

    public void startLogin() {
        LoginActivity_.intent(this).start();
    }

    public void goVip() {
        new AlertDialog.Builder(this)
                .setMessage("只有成为会员之后才可以使用哦~")
                .setTitle("提示")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .setPositiveButton("成为会员",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                ShopVipDetail_.intent(BaseActivity.this).start();
                            }
                        }).create().show();
    }

    public void goCoin() {
        new AlertDialog.Builder(this)
                .setMessage("靓点不足，请充值~")
                .setTitle("提示")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .setPositiveButton("立刻充值",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                ShopVipDetail_.intent(BaseActivity.this).start();
                            }
                        }).create().show();
    }
}
