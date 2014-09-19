package com.quanliren.quan_two.fragment.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.shop.ShopVipDetail_;
import com.quanliren.quan_two.activity.user.LoginActivity_;
import com.quanliren.quan_two.application.AppClass;
import com.quanliren.quan_two.bean.CacheBean;
import com.quanliren.quan_two.bean.LoginUser;
import com.quanliren.quan_two.custom.CustomProgressBar;
import com.quanliren.quan_two.db.DBHelper;
import com.quanliren.quan_two.util.URL;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OrmLiteDao;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

@EFragment
public class MenuFragmentBase extends Fragment {

    @App
    public AppClass ac;

    @OrmLiteDao(helper = DBHelper.class, model = CacheBean.class)
    public Dao<CacheBean, String> cacheDao;

    public AtomicBoolean init = new AtomicBoolean(false);

    public void closeInput() {
        if (getActivity().getCurrentFocus() != null) {
            ((InputMethodManager) getActivity().getSystemService(
                    getActivity().INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getActivity().getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
            ((InputMethodManager) getActivity().getSystemService(
                    getActivity().INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getActivity().getCurrentFocus()
                            .getWindowToken(), 0);
        }
    }

    public void showKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public RequestParams getAjaxParams() {
        LoginUser user = OpenHelperManager.getHelper(getActivity(),
                DBHelper.class).getUser();
        RequestParams ap = new RequestParams();
        ap.put("versionName", ac.cs.getVersionName());
        ap.put("versionCode", String.valueOf(ac.cs.getVersionCode()));
        ap.put("channel", ac.cs.getChannel());
        ap.put("clientType", "android");
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

    Toast toast = null;

    public void showCustomToast(String str) {
        if (getActivity() == null) {
            return;
        }
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // 根据指定的布局文件创建一个具有层级关系的View对象
        // 第二个参数为View对象的根节点，即LinearLayout的ID
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) getActivity().findViewById(R.id.toast_layout_root));

        // 查找ImageView控件
        // 注意是在layout中查找
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(str);

        toast = new Toast(getActivity().getApplicationContext());
        // 设置Toast的位置
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        // 让Toast显示为我们自定义的样子
        toast.setView(layout);
        toast.show();
    }

    public void showFailInfo(JSONObject jo) {
        try {
            int status = jo.getInt(URL.STATUS);
            switch (status) {
                case 2:
                    showCustomToast(jo.getJSONObject(URL.RESPONSE).getString(
                            URL.INFO));
                    loginOut();
                    break;
                case 1:
                    showCustomToast(jo.getJSONObject(URL.RESPONSE).getString(
                            URL.INFO));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loginOut() {
        try {
            TableUtils.clearTable(
                    OpenHelperManager.getHelper(getActivity(), DBHelper.class)
                            .getConnectionSource(), LoginUser.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        startLogin();
    }

    public CustomProgressBar parentDialog;

    public void customDismissDialog() {
        if (parentDialog != null && parentDialog.isShowing())
            parentDialog.dismiss();
    }

    public void customShowDialog(String str) {
        if (getActivity() == null) {
            return;
        }
        parentDialog = new CustomProgressBar(getActivity(), R.style.dialog);
        parentDialog.setMessage(str);
        parentDialog.show();
    }

    String[] str = new String[]{"", "正在获取数据", "正在登录", "正在提交", "请稍等"};

    public void customShowDialog(int i) {
        if (getActivity() == null) {
            return;
        }
        parentDialog = new CustomProgressBar(getActivity(), R.style.dialog);
        parentDialog.setMessage(str[i]);
        parentDialog.show();
    }

    public void showIntentErrorToast() {
        showCustomToast("网络连接失败");
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    public void goVip() {
        new AlertDialog.Builder(getActivity()).setTitle("提示")
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
                                ShopVipDetail_.intent(MenuFragmentBase.this).start();
                            }
                        }).create().show();
    }

    public DBHelper getHelper() {
        DBHelper helper = OpenHelperManager.getHelper(getActivity(),
                DBHelper.class);
        return helper;
    }

    public ActionBar getSupportActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    public void startLogin() {
        LoginActivity_.intent(getActivity()).start();
    }
}
