package com.quanliren.quan_two.activity.seting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import com.a.loopj.android.http.JsonHttpResponseHandler;
import com.a.loopj.android.http.RequestParams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.bean.VersionBean;
import com.quanliren.quan_two.util.URL;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EActivity(R.layout.aboutus)
public class AboutUsActivity extends BaseActivity {

	@ViewById
	Button updateBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.seting_about_us);
	}

	@Click
	void updateBtn() {
		checkVersion();
	}

	public void checkVersion() {
		RequestParams rp = getAjaxParams();
		rp.put("ostype", "0");
		rp.put("vcode", ac.cs.getVersionCode());
		ac.finalHttp.post(URL.CHECK_VERSION, rp, new JsonHttpResponseHandler() {
			@Override
			public void onStart() {
				customShowDialog("正在检查更新");
			}

			@Override
			public void onSuccess(JSONObject response) {
				try {
					int status = response.getInt(URL.STATUS);
					switch (status) {
					case 0:
						final VersionBean vb = new Gson().fromJson(
								response.getString(URL.RESPONSE),
								new TypeToken<VersionBean>() {
								}.getType());
						if (vb != null) {
							if (vb.getIsnewest() > 0) {
								new AlertDialog.Builder(AboutUsActivity.this)
										.setTitle("版本升级(" + vb.getVname() + ")")
										.setMessage(vb.getRemark())
										.setPositiveButton(
												"升级",
												new DialogInterface.OnClickListener() {

													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
                                                        Uri uri = Uri.parse(vb.getUrl());
                                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                        startActivity(intent);
                                                    }
												})
										.setNegativeButton(
												"取消",
												new DialogInterface.OnClickListener() {

													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
													}
												}).create().show();
							} else {
								showCustomToast("已是最新版本");
							}
						}
						break;
					default:
						showFailInfo(response);
						break;
					}
				} catch (Exception e) {
				} finally {
					customDismissDialog();
				}
			}

			@Override
			public void onFailure() {
				customDismissDialog();
				showIntentErrorToast();
			}
		});
	}
}
