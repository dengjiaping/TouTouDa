package com.quanliren.quan_two.activity.group.date;

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

@EActivity(R.layout.filter_date)
@OptionsMenu(R.menu.filter_people_menu)
public class FilterDateActivity extends BaseActivity {

	@ViewById
	View filter_all,filter_boy,filter_girl,filter_30min,filter_1day,filter_1hour,filter_3day,filter_state,filter_xing,filter_age,filter_ol;
	@ViewById
	TextView filter_state_txt;

	String[] states = { "全部",  "我想约吃饭", "我想看电影", "我想找游伴", "我想请陪同",
			"我要临时情侣"};

	Map<String, Integer> map = new HashMap<String, Integer>();

	AtomicBoolean cleared = new AtomicBoolean(false);

	@OrmLiteDao(helper = DBHelper.class, model = CustomFilterBean.class)
	Dao<CustomFilterBean, String> customFilterBeanDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		try {
			CustomFilterBean cfb = customFilterBeanDao.queryForId("pubtime");
			if (cfb == null) {
				customFilterBeanDao
						.create(new CustomFilterBean("pubtime", 0));
			}
			cfb = customFilterBeanDao.queryForId("dtype");
			if (cfb == null) {
				customFilterBeanDao
						.create(new CustomFilterBean("dtype", 0));
			}
			cfb = customFilterBeanDao.queryForId("date_sex");
			if (cfb == null) {
				customFilterBeanDao.create(new CustomFilterBean("date_sex", -1));
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

		int sex = map.get("date_sex");
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

		int time = map.get("pubtime");
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

		filter_state_txt.setText(states[map.get("dtype")]);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getSupportActionBar().setTitle("筛选");
	}

	@Click
	void filter_all() {
		map.put("date_sex", -1);
		initView();
	};

	@Click
	void filter_boy() {
		map.put("date_sex", 1);
		initView();
	};

	@Click
	void filter_girl() {
		map.put("date_sex", 0);
		initView();
	};

	@Click
	void filter_30min() {
		map.put("pubtime", 0);
		initView();
	};

	@Click
	void filter_1day() {
		map.put("pubtime", 2);
		initView();
	};

	@Click
	void filter_1hour() {
		map.put("pubtime", 1);
		initView();
	};

	@Click
	void filter_3day() {
		map.put("pubtime", 3);
		initView();
	};

	@Click
	void filter_state() {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setItems(states, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						map.put("dtype", which);
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
