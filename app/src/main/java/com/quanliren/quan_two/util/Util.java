package com.quanliren.quan_two.util;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static final SimpleDateFormat fmtDateTime = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat fmtDate = new SimpleDateFormat(
            "yyyy-MM-dd");
    public static final String FILE = "file://";

    public static final Pattern emoji = Pattern
            .compile(
                    "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                    Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

    public static String FilterEmoji(String str) {
        return emoji.matcher(str).replaceAll("");
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);

    }

    public static long locationTime;

    public static boolean isFastLocation() {
        long time = System.currentTimeMillis();
        long timeD = time - locationTime;
        if (0 < timeD && timeD < 15000) {
            return true;
        }
        return false;
    }

    public static boolean isMobileNO(String mobiles) {
        if (mobiles == null) {
            return false;
        }
        Pattern p = Pattern.compile("^[1][3-8]+\\d{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static int getLines(int sum, int num) {
        int lines = (int) (sum / num);
        if (sum % num > 0) {
            lines++;
        }
        return lines;
    }

    public static final boolean isPassword(String password) {
        if (password.length() > 16 || password.length() < 6) {
            return false;
        } else if (!password.matches("^[a-zA-Z0-9 -]+$")) {
            return false;
        }
        return true;
    }

    public static void shareMsg(Context context, String activityTitle,
                                String msgTitle, String msgText, String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/png");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));
    }

    public static int getAppVersionCode(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getChannel(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (Exception e) {
        }
        return "QUANONE";
    }

    public static String getAge(Date date) {
        Calendar cal = Calendar.getInstance();
        if (cal.getTime().before(date)) {
            return "0";
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(date);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                // monthNow==monthBirth
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                // monthNow>monthBirth
                age--;
            }
        }

        if (age < 0) {
            age = 0;
        }

        return age + "";
    }

    // 判断手机有无存储卡
    public static boolean existSDcard() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            return true;
        } else
            return false;
    }

    public long getSDFreeSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        // 返回SD卡空闲大小
        // return freeBlocks * blockSize; //单位Byte
        // return (freeBlocks * blockSize)/1024; //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    public static int getLengthString(String str) {
        try {
            byte[] b = str.getBytes("gb2312");
            return b.length;
        } catch (Exception ex) {
            return 0;
        }
    }

    public static boolean isStrNotNull(String str) {
        if (str != null && str.trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String getTimeDateStr(String dates) {
        String str1 = "";
        try {
            Date date = fmtDateTime.parse(dates);
            long result = Math.abs(new Date().getTime() - date.getTime());
            if (result < 60000)// 一分钟内
            {
                str1 = "刚刚";
            } else if (result >= 60000 && result < 3600000)// 一小时内
            {
                long seconds = result / 60000;
                str1 = seconds + "分钟前";
            } else if (result >= 3600000 && result < 86400000)// 一天内
            {
                long seconds = result / 3600000;
                str1 = seconds + "小时前";
            } else if (result >= 86400000 && result < 604800000l)// 日期格式
            {
                String[] temp = dates.split(" ");
                String o = temp[0];
                str1 = o.substring(o.indexOf("-") + 1, o.length());
            } else {
                String[] temp = dates.split(" ");
                String o = temp[0];
                str1 = o;
            }
        } catch (Exception e) {
        }
        return str1;
    }

    public static void doCopyFile(File from, File to) throws IOException {
        FileInputStream input = new FileInputStream(from);
        try {
            FileOutputStream output = new FileOutputStream(to);
            try {
                byte[] buffer = new byte[1024];
                int n = 0;
                while (-1 != (n = input.read(buffer))) {
                    output.write(buffer, 0, n);
                }
            } finally {
                try {
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException ioe) {
                    // ignore
                }
            }
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ioe) {
                // ignore
            }
        }
    }

    public static void setAlarmTime(Context context, long timeInMillis,
                                    String action, int time) {
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(action);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        int interval = time;
        am.setRepeating(AlarmManager.RTC, timeInMillis, interval, sender);
    }

    public static void canalAlarm(Context context, String action) {
        Intent intent = new Intent(action);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    }

    public static String getDistance(double lng1, double lat1, double lng2,
                                     double lat2) {
        // System.out.println(lng1+"--------"+lat1+"--------"+lng2+"--------"+lat2);
        double a = 2 * 6378.137;
        double b = Math.PI / 360;
        double c = Math.PI / 180;
        double s = a
                * Math.asin(Math.sqrt(Math.pow(Math.sin(b * (lat1 - lat2)), 2)
                + Math.cos(c * lat1) * Math.cos(lat2 * c)
                * Math.pow(Math.sin(b * (lng1 - lng2)), 2)));
        if (s * 1000 < 1) {
            s = 0.00;
        }
        return RoundOf(String.valueOf(s));
    }

    public static String RoundOf(String str) {
        return String
                .valueOf((double) Math.round(Double.valueOf(str) * 100) / 100);
    }

    public static String getDistance(String distance) {
        try {
            return RoundOf(String.valueOf(Double.valueOf(distance) / 1000))
                    + "km";
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static String getChatTime(String time) {
        try {
            Calendar c = Calendar.getInstance(Locale.CHINA);
            Calendar msgDate = Calendar.getInstance(Locale.CHINA);
            msgDate.setTime(Util.fmtDateTime.parse(time));

            if (msgDate.get(Calendar.YEAR) == c.get(Calendar.YEAR)) {
                if (msgDate.get(Calendar.DATE) == c.get(Calendar.DATE)) {
                    return (getNum(msgDate.get(Calendar.HOUR_OF_DAY)) + ":" + getNum(msgDate
                            .get(Calendar.MINUTE)));
                } else {
                    return (getNum(msgDate.get(Calendar.MONTH) + 1) + "-"
                            + getNum(msgDate.get(Calendar.DATE)) + " "
                            + getNum(msgDate.get(Calendar.HOUR_OF_DAY)) + ":" + getNum(msgDate
                            .get(Calendar.MINUTE)));
                }
            } else {
                return (getNum(msgDate.get(Calendar.YEAR)) + "-"
                        + getNum(msgDate.get(Calendar.MONTH) + 1) + "-"
                        + getNum(msgDate.get(Calendar.DATE)) + " "
                        + getNum(msgDate.get(Calendar.HOUR_OF_DAY)) + ":" + getNum(msgDate
                        .get(Calendar.MINUTE)));
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static String getNum(Integer num) {
        return num < 10 ? "0" + num : num + "";
    }

    public static int daysBetween(String strDate) {
        try {
            Date bdate = fmtDate.parse(fmtDate.format(new Date()));
            Date smdate = fmtDate.parse(strDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(smdate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(bdate);
            long time2 = cal.getTimeInMillis();
            long between_days = (time1 - time2) / (1000 * 3600 * 24);
            return Integer.parseInt(String.valueOf(between_days));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }
}
