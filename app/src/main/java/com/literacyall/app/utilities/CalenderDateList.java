package com.literacyall.app.utilities;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by ibrar on 9/26/2016.
 */
public class CalenderDateList {


    ArrayList<String> dayList = new ArrayList<String>();

    public static String getCurrentWeekNumber() {

        Calendar cal = GregorianCalendar.getInstance(Locale.FRANCE);
        cal.setTime(new Date());
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        return String.valueOf(week);
    }

    public static String getCurrentWeekDay() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        return new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime());
    }

    public static int getDayValue(String dayName) {
        ArrayList<String> daySequence = new ArrayList<String>();
        daySequence.add("Mon");
        daySequence.add("Tue");
        daySequence.add("Wed");
        daySequence.add("Thu");
        daySequence.add("Fri");
        daySequence.add("Sat");
        daySequence.add("Sun");
        return daySequence.indexOf(dayName);
    }

    private static int getCurrentYearMaxWeek() {
        Calendar cal1 = GregorianCalendar.getInstance(Locale.FRANCE);
        cal1.set(Calendar.YEAR, Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date())));
        cal1.set(Calendar.MONTH, Calendar.DECEMBER);
        cal1.set(Calendar.DAY_OF_MONTH, 31);
        int ordinalDay = cal1.get(Calendar.DAY_OF_YEAR);
        int weekDay = cal1.get(Calendar.DAY_OF_WEEK) - 1; // Sunday = 0
        int numberOfWeeks = (ordinalDay - weekDay + 10) / 7;
        return numberOfWeeks;
    }

    public static String getDayWiseWeekNum(String dayName, String time) {
        int currentWeek = Integer.parseInt(getCurrentWeekNumber());
        String currentDay = new SimpleDateFormat("EEE", Locale.ENGLISH).format(new Date());
        Date currentTime = null;
        try {
            currentTime = new SimpleDateFormat("H:m").parse(new SimpleDateFormat("H:m").format(new Date()));
        } catch (ParseException e1) {

            e1.printStackTrace();
        }

        if (getDayValue(dayName) == getDayValue(currentDay)) {
            try {
                if (new SimpleDateFormat("H:m").parse(time).after(currentTime)) {

                } else {
                    if (getCurrentYearMaxWeek() != currentWeek) {
                        currentWeek++;
                    } else {
                        currentWeek = 1;
                    }
                }
            } catch (ParseException e) {

                e.printStackTrace();
            }
        } else if (getDayValue(dayName) < getDayValue(currentDay)) {
            currentWeek++;
        } else {

        }
        return String.valueOf(currentWeek);
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("HH:mm").format(new Date());
    }

    public static String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
    }

    public static List<String> getWeekNoAndDay(String Date) {

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = df.parse(Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = GregorianCalendar.getInstance(Locale.FRANCE);
        cal.setTime(date);
        String week = String.valueOf(cal.get(Calendar.WEEK_OF_YEAR));
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String dayOfWeek = new SimpleDateFormat("EE", Locale.ENGLISH).format(date);
        ArrayList<String> data = new ArrayList<>();
        data.add(dayOfWeek);
        data.add(week);
        data.add(year);
        return data;
    }

    public static String getWeekDayByDate(String Date) {

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = df.parse(Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String dayOfWeek = new SimpleDateFormat("EE", Locale.ENGLISH).format(date);
        return dayOfWeek;
    }

    public static int getDayCountOfMonthYear(String Date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date convertedDate = null;
        try {
            convertedDate = dateFormat.parse(Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(convertedDate);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        SimpleDateFormat monthFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
        int lastDayOfMonth = Integer.parseInt(monthFormat.format(c.getTime()));
        return lastDayOfMonth;
    }

    public static String getDateByWeekDay(String year, String weekNo, String dayOfWeek) {
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        cal.set(Calendar.WEEK_OF_YEAR,  Integer.parseInt(weekNo));
        cal.set(Calendar.DAY_OF_WEEK, getDayValue(dayOfWeek) + 2);
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        return sdf.format(cal.getTime());*/

        DateTime dt = new DateTime().withYear(Integer.parseInt(year)).withWeekOfWeekyear(Integer.parseInt(weekNo)).withDayOfWeek(getDayValue(dayOfWeek) + 1);
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

        return dateTimeFormatter.print(dt);

    }

    public static Date convertToDate(String text) {
        Date date = null;
        DateFormat sdf = null;
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        try {
            date = sdf.parse(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getCurrentYear() {
        return new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
    }
}
