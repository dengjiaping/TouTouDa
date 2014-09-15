package com.quanliren.quan_two.activity.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.bean.CustomFilterBean;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.db.DBHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@EActivity(R.layout.filter_near_people)
@OptionsMenu(R.menu.filter_people_menu)
public class FilterNearPeopleActivity extends BaseActivity {

	@ViewById
	View filter_all;
	@ViewById
	View filter_boy;
	@ViewById
	View filter_girl;
	@ViewById
	View filter_30min;
	@ViewById
	View filter_1day;
	@ViewById
	View filter_1hour;
	@ViewById
	View filter_3day;
	@ViewById
	View filter_state;
	@ViewById
	View filter_xing;
	@ViewById
	View filter_age;
	@ViewById
	View filter_ol;
	@ViewById
	TextView filter_state_txt;
	@ViewById
	TextView filter_xing_txt;
	@ViewById
	TextView filter_age_txt;
	@ViewById
	TextView filter_ol_txt;

	String[] states = { "全部", "无状态", "我想约吃饭", "我想看电影", "我想找游伴", "我想请陪同",
			"我要临时情侣", "我想安静一会" };
	String[] xing = { "全部", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座",
			"处女座", "天秤座", "天蝎座", "射手座", "摩羯座" };
	String[] age = { "全部", "18以下", "18-25岁", "25-30岁", "30-35岁", "35岁以上" };
	String[] ol = { "全部", "计算机/互联网/通信", "生产/工艺/制造", "商业/服务业/个人体经营",
			"金融/银行／投资／保险", "文化／广告／传媒", "娱乐／艺术／表演", "医疗／护理／制药", "公务员／事业单位",
			"学生", "无" };

	Map<String, Integer> map = new HashMap<String, Integer>();

	AtomicBoolean cleared = new AtomicBoolean(false);

	@OrmLiteDao(helper = DBHelper.class, model = CustomFilterBean.class)
	Dao<CustomFilterBean, String> customFilterBeanDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		try {
			CustomFilterBean cfb = customFilterBeanDao.queryForId("activetype");
			if (cfb == null) {
				customFilterBeanDao
						.create(new CustomFilterBean("activetype", 0));
			}
			cfb = customFilterBeanDao.queryForId("userstate");
			if (cfb == null) {
				customFilterBeanDao
						.create(new CustomFilterBean("userstate", 0));
			}
			cfb = customFilterBeanDao.queryForId("constell");
			if (cfb == null) {
				customFilterBeanDao.create(new CustomFilterBean("constell", 0));
			}
			cfb = customFilterBeanDao.queryForId("age");
			if (cfb == null) {
				customFilterBeanDao.create(new CustomFilterBean("age", 0));
			}
			cfb = customFilterBeanDao.queryForId("job");
			if (cfb == null) {
				customFilterBeanDao.create(new CustomFilterBean("job", 0));
			}
			cfb = customFilterBeanDao.queryForId("sex");
			if (cfb == null) {
				customFilterBeanDao.create(new CustomFilterBean("sex", -1));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@AfterViews
	void initView() {

		List<View> vlist = new ArrayList<View>();
		vlist.add(filter_all);
		vlist.add(filter_boy);
		vlist.add(filter_girl);
		vlist.add(filter_30min);
		vlist.add(filter_1day);
		vlist.add(filter_1hour);
		vlist.add(filter_3day);
		for (View view : vlist) {
			view.setSelected(false);
		}

		if(cleared.compareAndSet(false, true)){
			try {
				List<CustomFilterBean> cfbList = customFilterBeanDao.queryForAll();
				for (CustomFilterBean customFilterBean : cfbList) {
					map.put(customFilterBean.getKey(), customFilterBean.getId());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		int sex = map.get("sex");
		switch (sex) {
		case 0:
			filter_girl.setSelected(true);
			break;
		case 1:
			filter_boy.setSelected(true);
			break;
		default:
			filter_all.setSelected(true);
			break;
		}

		int time = map.get("activetype");
		switch (time) {
		case 1:
			filter_1hour.setSelected(true);
			break;
		case 2:
			filter_1day.setSelected(true);
			break;
		case 3:
			filter_3day.setSelected(true);
			break;
		default:
			filter_30min.setSelected(true);
			break;
		}

		filter_state_txt.setText(states[map.get("userstate")]);
		filter_xing_txt.setText(xing[map.get("constell")]);
		filter_age_txt.setText(age[map.get("age")]);
		filter_ol_txt.setText(ol[map.get("job")]);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getSupportActionBar().setTitle("筛选");
	}

	@Click
	void filter_all() {
		map.put("sex", -1);
		initView();
	};

	@Click
	void filter_boy() {
		map.put("sex", 1);
		initView();
	};

	@Click
	void filter_girl() {
		map.put("sex", 0);
		initView();
	};

	@Click
	void filter_30min() {
		map.put("activetype", 0);
		initView();
	};

	@Click
	void filter_1day() {
		map.put("activetype", 2);
		initView();
	};

	@Click
	void filter_1hour() {
		map.put("activetype", 1);
		initView();
	};

	@Click
	void filter_3day() {
		map.put("activetype", 3);
		initView();
	};

	@Click
	void filter_state() {
		if(!checkUser()){
			return;
		}
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setItems(states, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						map.put("userstate", which);
						initView();
					}
				}).setNegativeButton("关闭", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}
	
	public boolean checkUser(){
		User user=getHelper().getUserInfo();
		if(user==null){
			startLogin();
			return false;
		}
		if(user.getIsvip()==0){
			goVip();
			return false;
		}
		return true;
	}

	@Click
	void filter_xing() {
		if(!checkUser()){
			return;
		}
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setItems(xing, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						map.put("constell", which);
						initView();
					}
				}).setNegativeButton("关闭", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	@Click
	void filter_age() {
		if(!checkUser()){
			return;
		}
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setItems(age, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						map.put("age", which);
						initView();
					}
				}).setNegativeButton("关闭", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	@Click
	void filter_ol() {
		if(!checkUser()){
			return;
		}
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setItems(ol, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						map.put("job", which);
						initView();
					}
				}).setNegativeButton("关闭", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	@OptionsItem
	void ok() {
		try {
			TableUtils.clearTable(getConnectionSource(), CustomFilterBean.class);
			for (String key : map.keySet()) {
				customFilterBeanDao.create(new CustomFilterBean(key, map.get(key)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		setResult(1);
		finishActivity();
	}
}
