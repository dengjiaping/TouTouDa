package com.quanliren.quan_two.activity.reg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.a.loopj.android.http.JsonHttpResponseHandler;
import com.a.loopj.android.http.RequestParams;
import com.a.net.simonvt.numberpicker.NumberPicker;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.activity.user.LoginActivity_;
import com.quanliren.quan_two.application.AM;
import com.quanliren.quan_two.bean.LoginUser;
import com.quanliren.quan_two.bean.MoreLoginUser;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.bean.UserTable;
import com.quanliren.quan_two.db.DBHelper;
import com.quanliren.quan_two.share.CommonShared;
import com.quanliren.quan_two.util.URL;
import com.quanliren.quan_two.util.Util;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

@EActivity
@OptionsMenu(R.menu.filter_people_menu)
public class RegSecond extends BaseActivity {
	@Extra
	String phone;
	@ViewById
	TextView age;
	@ViewById
	EditText password;
	@ViewById
	EditText confirm_password;
	@ViewById
	EditText nickname;
	@ViewById
	TextView sex;
	@ViewById
	TextView txt_num;
	@ViewById
	TextView face;
	@ViewById
	TextView love;
	@ViewById
	TextView money;

	@OrmLiteDao(helper = DBHelper.class, model = MoreLoginUser.class)
	public Dao<MoreLoginUser, Integer> moreLoginUserDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg_second);
		getSupportActionBar().setTitle("输入信息(3/3)");

		setListener();
	}

	public void setListener() {
	}

	@OptionsItem(R.id.ok)
	public void rightClick() {
		String str_nickname = nickname.getText().toString().trim();
		String str_sex = sex.getTag()==null?"":sex.getTag().toString().trim();
		String str_password = password.getText().toString().trim();
		String str_confirm_password = confirm_password.getText().toString()
				.trim();
		String str_age = age.getText().toString().trim();
		String str_face = face.getText().toString().trim();
		String str_love = love.getTag()==null?"":love.getTag().toString().trim();
		String str_money = money.getTag()==null?"":money.getTag().toString().trim();

		if (str_nickname.length() == 0) {
			showCustomToast("请输入昵称");
			return;
		} else if (str_password.length() > 16 || str_password.length() < 6) {
			showCustomToast("密码长度为6-16个字符");
			return;
		} else if (!str_password.matches("^[a-zA-Z0-9 -]+$")) {
			showCustomToast("密码中不能包含特殊字符");
			return;
		} else if (!str_confirm_password.equals(str_password)) {
			showCustomToast("确认密码与密码不同");
			return;
		} else if (str_age.length() == 0) {
			showCustomToast("请选择出生日期");
			return;
		} else if (!Util.isStrNotNull(str_sex)) {
			showCustomToast("请选择性别");
			return;
		} else if (!Util.isStrNotNull(str_face)) {
			showCustomToast("请选择外貌");
			return;
		} else if (!Util.isStrNotNull(str_love)) {
			showCustomToast("请选择情感");
			return;
		} else if (!Util.isStrNotNull(str_money)) {
			showCustomToast("请选择收入");
			return;
		}

		CommonShared cs = new CommonShared(getApplicationContext());

		RequestParams ap = getAjaxParams();
		ap.put("mobile", phone);
		ap.put("nickname", str_nickname);
		ap.put("pwd", str_password);
		ap.put("repwd", str_confirm_password);
		ap.put("birthday", str_age);
		ap.put("sex", str_sex);
		ap.put("cityid", String.valueOf(cs.getLocationID()));
		ap.put("longitude", ac.cs.getLng());
		ap.put("latitude", ac.cs.getLat());
		ap.put("emotion", str_love);
		ap.put("income", str_money);
		ap.put("appearance", str_face);

		User lou = new User();
		lou.setMobile(phone);
		lou.setPwd(str_password);
		ac.finalHttp.post(URL.REG_THIRD, ap, new callBack(lou));
	}

	class callBack extends JsonHttpResponseHandler {
		User u;

		public callBack(User u) {
			this.u = u;
		}

		public void onStart() {
			customShowDialog(1);
		};

		public void onFailure() {
			customDismissDialog();
			showIntentErrorToast();
		};

		public void onSuccess(JSONObject jo) {
			customDismissDialog();
			try {
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					showCustomToast("注册成功");

					moreLoginUserDao.delete(moreLoginUserDao.deleteBuilder()
							.where().eq("username", u.getMobile())
							.prepareDelete());
					moreLoginUserDao.create(new MoreLoginUser(u.getMobile(), u
							.getPwd()));

					User user = new Gson().fromJson(jo.getString(URL.RESPONSE),
							User.class);
					LoginUser lu = new LoginUser(user.getId(), u.getMobile(),
							u.getPwd(), user.getToken());

					// 保存用户
					userTableDao.deleteById(user.getId());
					userTableDao.create(new UserTable(user));

					// 保存登陆用户
					TableUtils.clearTable(getConnectionSource(),
							LoginUser.class);
					loginUserDao.create(lu);

					AM.getActivityManager().popActivity(
							LoginActivity_.class.getName());
					
					ac.startServices();
					
					finish();
					break;
				default:
					showCustomToast(jo.getJSONObject(URL.RESPONSE).getString(
							URL.INFO));
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};

	Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);

	@Click(R.id.sex)
	public void editsex(View v) {
		final CharSequence[] items = { "男", "女" };
		AlertDialog.Builder builder = new AlertDialog.Builder(RegSecond.this);
		builder.setItems(items, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int position) {
				if (position == 0) {
					sex.setText("男");
					sex.setTag(1);
				} else {
					sex.setText("女");
					sex.setTag(0);
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);
		alert.show();
	}

	public void onBackPressed() {
		dialogFinish();
	};

	@Override
	public void finishActivity() {
		dialogFinish();
	}

	public void dialogFinish() {
		new AlertDialog.Builder(RegSecond.this).setTitle("提示")
				.setMessage("您确定要放弃本次注册吗？")
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

	String[] height = new String[101];
	String[] weight = new String[71];
	String[] body = { "活泼可爱", "温柔可人", "娴静端庄", "秀外慧中", "妩媚妖艳", "火爆性感" ,"高大威猛", "气宇轩昂", "清新俊秀", "文质彬彬", "血性精悍", "儒雅风趣"};

	@Click
	void face() {
		for (int i = 0; i < 101; i++) {
			height[i] = 100 + i + "cm";
		}

		for (int i = 0; i < 71; i++) {
			weight[i] = 30 + i + "kg";
		}
		View choseView = View.inflate(this, R.layout.chose_face, null);
		final NumberPicker npHeight = (NumberPicker) choseView
				.findViewById(R.id.height);
		npHeight.setMaxValue(height.length - 1);
		npHeight.setMinValue(0);
		npHeight.setFocusable(true);
		npHeight.setFocusableInTouchMode(true);
		npHeight.setDisplayedValues(height);
		npHeight.setValue(176);
		final NumberPicker npWeight = (NumberPicker) choseView
				.findViewById(R.id.weight);
		npWeight.setMaxValue(weight.length - 1);
		npWeight.setMinValue(0);
		npWeight.setFocusable(true);
		npWeight.setFocusableInTouchMode(true);
		npWeight.setDisplayedValues(weight);
		npWeight.setValue(30);
		final NumberPicker npBody = (NumberPicker) choseView
				.findViewById(R.id.body);
		npBody.setMaxValue(body.length - 1);
		npBody.setMinValue(0);
		npBody.setFocusable(true);
		npBody.setFocusableInTouchMode(true);
		npBody.setDisplayedValues(body);

		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("身高、体重、类型").setNegativeButton("取消", null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						face.setText(height[npHeight.getValue()] + " "
								+ weight[npWeight.getValue()] + " "
								+ body[npBody.getValue()]);
					}
				});
		AlertDialog dialog = builder.create();
		dialog.setView(choseView, 0, 0, 0, 0);
		dialog.show();
	}

	String[] moneys = { "4000元以下", "4001-6000元", "6001-10000元", "10001-15000元",
			"15001-20000元", "20001-50000元", "50000元以上" };

	@Click
	void money() {
		AlertDialog.Builder builder = new AlertDialog.Builder(RegSecond.this);
		builder.setItems(moneys, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int position) {
				money.setTag(position + "");
				money.setText(moneys[position]);
			}
		});
		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);
		alert.show();
	}

	String[] loves = { "单身", "恋爱中", "已婚" };

	@Click
	void love() {
		AlertDialog.Builder builder = new AlertDialog.Builder(RegSecond.this);
		builder.setItems(loves, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int position) {
				love.setTag(position + "");
				love.setText(loves[position]);
			}
		});
		
		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);
		alert.show();
	}
}
