package com.example.anshengqiang.learnin.Utils;

import java.util.Calendar;

/**
 * Created by anshengqiang on 2017/6/14.
 */

public class DateNumber {
    private Calendar mCalendar;

    public DateNumber(){
        mCalendar = Calendar.getInstance();
    }

    public String getDateString(int subtractorNum){

        String monthString, dayString;

        mCalendar.add(Calendar.DAY_OF_MONTH, -subtractorNum);

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH) + 1;
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        if (month < 10){
            monthString = "0" + month;
        }else {
            monthString = String.valueOf(month);
        }

        if (day < 10){
            dayString = "0" + day;
        }else {
            dayString = String.valueOf(day);
        }


        return new String(year + monthString + dayString);
    }
}
