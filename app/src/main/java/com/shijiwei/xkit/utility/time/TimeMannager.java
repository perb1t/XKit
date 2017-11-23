package com.shijiwei.xkit.utility.time;

import android.os.Handler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by shijiwei on 2017/3/24.
 */
public class TimeMannager {

    private SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
    private DateFormatCallBack dateFormatCallBack;

    public String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) w = 0;
        return weekDays[w];
    }


    public IDate getDate() {
        Date date = new Date(System.currentTimeMillis());
        String dateFormatResult = sDateFormat.format(date);
        IDate iDate = new IDate();
        iDate.date = date;
        iDate.week = getWeekOfDate(date);
        iDate.year = dateFormatResult.substring(0, dateFormatResult.indexOf("年") + 1);
        iDate.monthWithDay = dateFormatResult.substring(dateFormatResult.indexOf("年") + 1, dateFormatResult.indexOf("日") + 1);
        iDate.time = dateFormatResult.substring(dateFormatResult.indexOf("日") + 1, dateFormatResult.length());
        return iDate;
    }

    public void startFormat(DateFormatCallBack callBack) {
        this.dateFormatCallBack = callBack;
        mDateHandler.post(mDateRunnabel);
    }

    private Handler mDateHandler = new Handler();
    private Runnable mDateRunnabel = new Runnable() {
        @Override
        public void run() {
            if (dateFormatCallBack != null) {
                dateFormatCallBack.onDateFormat(getDate());
                mDateHandler.postDelayed(this, 1000);
            }
        }
    };

    public interface DateFormatCallBack {
        void onDateFormat(IDate iDate);
    }

    public static class IDate {
        public Date date;
        public String year;
        public String monthWithDay;
        public String time;
        public String week;
    }

}
