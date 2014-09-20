package com.quanliren.quan_two.activity.group.date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.a.mirko.android.datetimepicker.date.DatePickerDialog;
import com.a.mirko.android.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.a.mirko.android.datetimepicker.time.RadialPickerLayout;
import com.a.mirko.android.datetimepicker.time.TimePickerDialog;
import com.a.mirko.android.datetimepicker.time.TimePickerDialog.OnTimeSetListener;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.activity.location.GDLocation;
import com.quanliren.quan_two.activity.location.ILocationImpl;
import com.quanliren.quan_two.activity.shop.*;
import com.quanliren.quan_two.util.URL;
import com.quanliren.quan_two.util.Util;
import com.quanliren.quan_two.util.http.JsonHttpResponseHandler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@EActivity(R.layout.date_publish)
@OptionsMenu(R.menu.date_publish_menu)
public class DatePublishActivity extends BaseActivity implements ILocationImpl {

    @ViewById
    TextView title_btn, want_btn, sex_btn, xiaofei_btn, money_btn, date_btn, time_btn;
    @ViewById(R.id.place)
    EditText place_et;
    @ViewById
    View want_ll, people_ll, money_ll;
    @ViewById
    EditText remark_et;
    @ViewById
    RadioButton want_rb, give_rb, none_rb;

    @ViewById
    RadioGroup radioGroup;

    String[] title = {"我想约吃饭", "我想看电影", "我想找游伴", "我想请陪同", "我要临时情侣"};
    String[] want = {"安慰老妈", "阻击相亲", "充场面", "试着交往"};
    String[] sex = {"女", "男"};
    String[] xiaofei = {"AA", "我请客", "你请客"};
    String[] money = {"0", "5", "10", "20", "30", "40", "50", "60", "70",
            "80", "90", "100"};

    GDLocation location;

    RequestParams rp;

    @OptionsItem
    void ok() {
        int dtype = Arrays.asList(title)
                .indexOf(title_btn.getText().toString());
        if (dtype < 0) {
            showCustomToast("请选择题目");
            return;
        } else {
            dtype++;
        }

        int aim = -1;
        if (dtype == 5) {
            aim = Arrays.asList(want).indexOf(want_btn.getText().toString());
            if (aim == -1) {
                showCustomToast("请选择目的");
                return;
            }
        }

        String time = date_btn.getText().toString();
        if (time.equals("")) {
            showCustomToast("请选择日期");
            return;
        }

        String time1 = time_btn.getText().toString();
        if (time1.equals("")) {
            showCustomToast("请选择时间");
            return;
        }

        String place = Util.FilterEmoji(place_et.getText().toString().trim());
        if (place.equals("")) {
            showCustomToast("请输入地点");
            return;
        }

        int sex = Arrays.asList(this.sex).indexOf(sex_btn.getText().toString());
        if (sex < 0) {
            showCustomToast("请选择性别");
            return;
        }

        int xf = Arrays.asList(xiaofei).indexOf(
                xiaofei_btn.getText().toString());
        if (xf < 0) {
            showCustomToast("请选择消费");
            return;
        }

        int ctype = 0;
        int coin = 0;
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        switch (radioButtonId) {
            case R.id.none_rb:
                ctype = 0;
                coin = 0;
                break;
            case R.id.want_rb:
                ctype = 1;
                coin = checkRadio();
                break;
            case R.id.give_rb:
                ctype = 2;
                coin = checkRadio();
                break;
        }
        if (coin == -1) {
            showCustomToast("请选择靓点数额");
            return;
        }

        String remark = Util.FilterEmoji(remark_et.getText().toString());

        rp = getAjaxParams();
        rp.put("dtype", dtype);
        if (aim > -1)
            rp.put("aim", aim);
        rp.put("dtime", time + " " + time1);
        rp.put("address", place);
        rp.put("peoplenum", 1);
        rp.put("objsex", sex);
        rp.put("whopay", xf);
        rp.put("ctype", ctype);
        rp.put("coin", coin);
        rp.put("remark", remark);
        rp.put("longitude", ac.cs.getLng());
        rp.put("latitude", ac.cs.getLat());

        customShowDialog("正在定位");
        location.startLocation();
    }

    public int checkRadio() {
        int xf = Arrays.asList(money).indexOf(money_btn.getText().toString());
        if (xf > -1) {
            return Integer.valueOf(money[xf]);
        }
        return -1;
    }

    @AfterViews
    void initView() {
        radioGroup
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        int radioButtonId = group.getCheckedRadioButtonId();
                        switch (radioButtonId) {
                            case R.id.none_rb:
                                money_ll.setVisibility(View.GONE);
                                break;
                            default:
                                money_ll.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                });
        location = new GDLocation(this, this, false);
    }

    @Click({R.id.title_btn, R.id.want_btn, R.id.sex_btn, R.id.xiaofei_btn,
            R.id.money_btn, R.id.date_btn, R.id.time_btn})
    void Click(final View v) {
        switch (v.getId()) {
            case R.id.date_btn:
                Calendar calendar = Calendar.getInstance(Locale.CHINA);
//			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//				android.app.DatePickerDialog dialog_date = new android.app.DatePickerDialog(
//						this, dateListener1, calendar.get(Calendar.YEAR),
//						calendar.get(Calendar.MONTH),
//						calendar.get(Calendar.DAY_OF_MONTH));
//				dialog_date.show();
//			} else {
                DatePickerDialog datePickerDialog = DatePickerDialog
                        .newInstance(dateListener, calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setYearRange(calendar.get(Calendar.YEAR), datePickerDialog.getMaxYear());
                datePickerDialog.show(getSupportFragmentManager(), "");
//			}
                break;
            case R.id.time_btn:
                Calendar mCalendar = Calendar.getInstance(Locale.CHINA);
//			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//				android.app.TimePickerDialog dialog_time=new android.app.TimePickerDialog(this, timeListener, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar
//						.get(Calendar.MINUTE), true);
//				dialog_time.show();
//			} else {
                TimePickerDialog timePickerDialog24h = TimePickerDialog
                        .newInstance(timeListener1, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar
                                .get(Calendar.MINUTE), true);
                timePickerDialog24h.show(getSupportFragmentManager(), "");
//			}
                break;
            default:
                String[] str = null;
                switch (v.getId()) {
                    case R.id.title_btn:
                        str = title;
                        break;
                    case R.id.want_btn:
                        str = want;
                        break;
                    case R.id.sex_btn:
                        str = sex;
                        break;
                    case R.id.xiaofei_btn:
                        str = xiaofei;
                        break;
                    case R.id.money_btn:
                        str = money;
                        break;
                }
                final String[] cstr = str;
                AlertDialog dialog = new AlertDialog.Builder(this).setItems(str,
                        new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((TextView) v).setText(cstr[which]);

                                if (v.getId() == R.id.title_btn) {
                                    if (which == 4) {
                                        want_ll.setVisibility(View.VISIBLE);
                                        people_ll.setVisibility(View.GONE);
                                    } else {
                                        want_ll.setVisibility(View.GONE);
                                        people_ll.setVisibility(View.VISIBLE);
                                    }
                                } else if (v.getId() == R.id.xiaofei_btn) {
                                    allEnable();
                                    switch (which) {
                                        case 0:
                                            break;
                                        case 1:
                                            want_rb.setEnabled(false);
                                            if (!give_rb.isChecked()) {
                                                none_rb.setChecked(true);
                                            }
                                            break;
                                        case 2:
                                            give_rb.setEnabled(false);
                                            if (!want_rb.isChecked()) {
                                                none_rb.setChecked(true);
                                            }
                                            break;
                                    }
                                }
                            }
                        }).create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                break;
        }

    }

    void allEnable() {
        want_rb.setEnabled(true);
        give_rb.setEnabled(true);
        none_rb.setEnabled(true);
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    private static String pad2(int c) {
        if (c == 12)
            return String.valueOf(c);
        if (c == 00)
            return String.valueOf(c + 12);
        if (c > 12)
            return String.valueOf(c - 12);
        else
            return String.valueOf(c);
    }

    OnTimeSetListener timeListener1 = new OnTimeSetListener() {

        @Override
        public void onTimeSet(RadialPickerLayout view,
                              int hourOfDay, int minute) {
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            if (!date_btn.getText().equals("")) {
                try {
                    Date date = Util.fmtDate.parse(date_btn.getText().toString());
                    Calendar cdate = Calendar.getInstance();
                    cdate.setTime(date);
                    dateAndTime.set(Calendar.YEAR, cdate.get(Calendar.YEAR));
                    dateAndTime.set(Calendar.MONTH, cdate.get(Calendar.MONTH));
                    dateAndTime.set(Calendar.DAY_OF_MONTH, cdate.get(Calendar.DAY_OF_MONTH));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (calendar.getTime().after(dateAndTime.getTime())) {
                showCustomToast("请选择今天或今天以后的日期");
                return;
            }

            time_btn.setText(new StringBuilder()
                    .append(pad(hourOfDay)).append(":")
                    .append(pad(minute)).toString());
        }
    };

    android.app.TimePickerDialog.OnTimeSetListener timeListener = new android.app.TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            if (calendar.getTime().after(dateAndTime.getTime())) {
                showCustomToast("请选择今天或今天以后的日期");
                return;
            }
            time_btn.setText(new StringBuilder()
                    .append(pad(hourOfDay)).append(":")
                    .append(pad(minute)).toString());
        }
    };

    OnDateSetListener dateListener = new OnDateSetListener() {

        @Override
        public void onDateSet(DatePickerDialog dialog, int year,
                              int monthOfYear, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            if (calendar.getTime().after(dateAndTime.getTime())) {
                showCustomToast("请选择今天或今天以后的日期");
                return;
            }
            date_btn.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
        }
    };

    android.app.DatePickerDialog.OnDateSetListener dateListener1 = new android.app.DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            dateListener.onDateSet(null, year, monthOfYear, dayOfMonth);
        }
    };

    @Override
    public void onLocationSuccess() {
        customDismissDialog();
        ac.finalHttp.post(URL.PUB_DATA, rp, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                customShowDialog("正在上传");
            }

            @Override
            public void onFailure() {
                customDismissDialog();
                showIntentErrorToast();
            }

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    int status = response.getInt(URL.STATUS);
                    switch (status) {
                        case 0:
                            showCustomToast("发布成功");
                            setResult(1);
                            finish();
                            break;
                        case 1:
                            String info = response.getJSONObject(URL.RESPONSE).getString(URL.INFO);
                            if (info.indexOf("会员") > -1) {
                                new AlertDialog.Builder(DatePublishActivity.this)
                                        .setMessage(info)
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
                                                        ShopVipDetail_.intent(DatePublishActivity.this).start();
                                                    }
                                                }).create().show();
                            } else {
                                showFailInfo(response);
                            }
                            break;
                        default:
                            showFailInfo(response);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    customDismissDialog();
                }
            }
        });
    }

    @Override
    public void onLocationFail() {
        showCustomToast("定位失败");
    }

}
