package com.quanliren.quan_two.activity.shop.product;

import android.content.Intent;
import android.widget.EditText;

import com.quanliren.quan_two.util.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.bean.ProductBean;
import com.quanliren.quan_two.fragment.SetingMoreFragment;
import com.quanliren.quan_two.util.URL;
import com.quanliren.quan_two.util.Util;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EActivity(R.layout.product_exchange)
@OptionsMenu(R.menu.filter_people_menu)
public class ProductExchangeActivity extends BaseActivity{

	@Extra
	ProductBean bean;
	
	@ViewById
	EditText name;
	@ViewById
	EditText phone;
	@ViewById
	EditText email;
	@ViewById
	EditText address;
	@ViewById
	EditText remark;
	
	@OptionsItem
	void ok(){
		String str_name=Util.FilterEmoji(name.getText().toString());
		String str_phone=Util.FilterEmoji(phone.getText().toString());
		String str_email=Util.FilterEmoji(email.getText().toString());
		String str_address=Util.FilterEmoji(address.getText().toString());
		String str_remark=Util.FilterEmoji(remark.getText().toString());
		
		if(!Util.isStrNotNull(str_name)){
			showCustomToast("请填写姓名");
			name.requestFocus();
			return;
		}
		if(!Util.isMobileNO(str_phone)){
			showCustomToast("请填写正确的手机号码");
			phone.requestFocus();
			return;
		}
		if(Util.isStrNotNull(str_email)&&!Util.isEmail(str_email)){
			showCustomToast("请填写正确的邮箱");
			address.requestFocus();
			return;
		}
		if(!Util.isStrNotNull(str_address)){
			showCustomToast("请填写地址");
			address.requestFocus();
			return;
		}
		
		RequestParams rp=getAjaxParams();
		rp.put("gid", bean.getGid());
		rp.put("truename", str_name);
		rp.put("mobile", str_phone);
		rp.put("address", str_address);
		rp.put("email", str_email);
		rp.put("remark", str_remark);
		ac.finalHttp.post(URL.APPLY_EXCHANGE,rp, new JsonHttpResponseHandler(){
			@Override
			public void onStart() {
				customShowDialog("正在提交信息");
			}
			@Override
			public void onFailure() {
				customDismissDialog();
				showIntentErrorToast();
			}
			@Override
			public void onSuccess(JSONObject response) {
				try {
					int status=response.getInt(URL.STATUS);
					switch (status) {
					case 0:
						showCustomToast("提交成功，请耐心等候");
						Intent i = new Intent(SetingMoreFragment.UPDATE_USERINFO);
						sendBroadcast(i);
						finishActivity();
						break;
					default:
						showFailInfo(response);
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					customDismissDialog();
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getSupportActionBar().setTitle("填写信息");
	}
	
}
