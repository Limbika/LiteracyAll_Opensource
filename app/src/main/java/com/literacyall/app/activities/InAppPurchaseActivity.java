package com.literacyall.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.literacyall.app.Dialog.InAppPurchaseDialog;
import com.literacyall.app.R;
import com.literacyall.app.utilities.ApplicationMode;
import com.literacyall.app.utilities.CalenderDateList;
import com.literacyall.app.utilities.CustomToast;
import com.literacyall.app.utilities.ExceptionHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InAppPurchaseActivity extends BaseActivity implements View.OnClickListener {
    //handling 30 days trial
    public static String BR = "com.uniqgroup.autonomyup.thirtyDays";
    //Start_BoughtTime_TAG is tag is for both the start of app install and the start of
    // app bought we calculate it to use in sbscrition dialog
    public String Start_BoughtTime_TAG = "start_bought_time";
    public String Oneyear_EndTime_TAG = "end_bought_time";
    public String FIRSTTIME_TAG = "first_time";
    public String PASS_TAG = "pass_dontpass";
    public String CALENDAR_PREF = "pref";
    public String Bought_within_thirtydays = "Bought_within_thirtydays";
    public String END_DATE = "endDate";

    long thirtyDay_time = 2592000000L;
    SharedPreferences sp;
    SharedPreferences.Editor editor_sp;
    //handling 30 days trial

    MediaPlayer mediaPlayer;
    MediaPlayer mediaPlayerInit;
    ToggleButton tglMusicOnOff;
    public static final int ALARM_MAKER_ID = 0x1;
    long numOfDaysRemaining = 0;
    ImageButton ibtnIAP;
    String daysRemaining = "0";
    String daysRemaining_tailtext = "";
    String itemPurchased = "";
    String serviceUnavailable = "";
    String itemNotPurchased = "";
    public  boolean appExit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_purchase);

        appExit = true;
        sp = getSharedPreferences(CALENDAR_PREF, 0);
        editor_sp = sp.edit();
        daysRemaining_tailtext = getResources().getString(R.string.daysLeft);
        itemPurchased = getResources().getString(R.string.itemPurchased);
        serviceUnavailable = getResources().getString(R.string.serviceUnavailable);
        itemNotPurchased = getResources().getString(R.string.itemNotPurchased);

        //handling 30 days trial
        boolean firstTime = sp.getBoolean(FIRSTTIME_TAG, true);

        if (firstTime) {
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            String todayAsString = dateFormat.format(today);

            editor_sp.putBoolean(FIRSTTIME_TAG, false);
            editor_sp.putString(END_DATE, new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date(CalenderDateList.convertToDate(CalenderDateList.getCurrentDate()).getTime() + thirtyDay_time)));
            //even if not bought we put this time to calulate timings of trial
            editor_sp.putString(Start_BoughtTime_TAG, todayAsString);
            editor_sp.commit();
        }

        ibtnIAP = (ImageButton) findViewById(R.id.ibtnIAP);
        ibtnIAP.setOnClickListener(this);
        appBilling();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ibtnIAP:
                InAppPurchaseDialog subscribeDialog = new InAppPurchaseDialog(InAppPurchaseActivity.this, InAppPurchaseActivity.this, daysRemaining + " " + daysRemaining_tailtext);
                subscribeDialog.show();
                break;


        }
    }

        // Copy dari sini
    public static final String PRODUCT_ID = "subscription_autonomyup";
    public static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyb+4yWo6gBviEIcOyi8yVCPB7M0j61htffgEvys/5pfMPtWeEHS6nMwBiK0RU8Umx4Z0EWJSzG0kUkg1y6rMvV/3luATk/HDjfVMM/SCvz/nbr7RCttNaZN8QKYChaDZ4gqLXUXJncflPuiz/aRNgcxFUX+ssKwCS5rY9sQH/rWo9G+LP7MbcKbTrZEI7rth8XN6h0Wb9JeL21gYI77/8yU8NX/jyFJcz9qIf/cFgT7MTxVO/XXcRMuuyyvsga9gj7Chv5iI9xgeJGItleiW/2O96YJiq/53Zmavk7lFdu/x+WttTh/v6zW3sUf1baQgiDTkdUbtxwTbFFytCKXo7wIDAQAB"; // PUT YOUR MERCHANT KEY HERE;
    public static final String MERCHANT_ID = "06211610386846258459";

    public BillingProcessor bp;
    public boolean readyToPurchase = false;

    // For InAppBilling
    public void appBilling() {
        if (!BillingProcessor.isIabServiceAvailable(this)) {
            CustomToast.t(this, serviceUnavailable);
        }

        bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {
                CustomToast.t(InAppPurchaseActivity.this, itemPurchased);
                //Start the timer
                saveTimeForOneYear();
            }

            @Override
            public void onBillingError(int errorCode, Throwable error) {
                CustomToast.t(InAppPurchaseActivity.this, itemNotPurchased);
            }

            @Override
            public void onBillingInitialized() {
                readyToPurchase = true;
            }


            @Override
            public void onPurchaseHistoryRestored() {

            }

        });

        if (bp.loadOwnedPurchasesFromGoogle()) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    //CALCULATIONS OF DATES******************************/////
    public void saveTimeForOneYear() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 365);
        Date OneYear = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        String todayAsString = dateFormat.format(today);
        String OneYearAsString = dateFormat.format(OneYear);
        // save it in shared prefs

        editor_sp.putBoolean(PASS_TAG, true);
        editor_sp.putBoolean(Bought_within_thirtydays, true);
        editor_sp.putString(Start_BoughtTime_TAG, todayAsString);
        editor_sp.putString(Oneyear_EndTime_TAG, OneYearAsString);
        editor_sp.commit();

    }
///////////////////calculation of time//////////////////////////

    public void calculateTrialPeriod() {
        long todayMili = 0, oneMonthMili = 0;
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        todayMili = calendar.getTimeInMillis();

        String startDay = sp.getString(Start_BoughtTime_TAG, "");

        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        String todayAsString = formatter.format(today);
        ;
        String res = "";

        Date dateStart = null, dateEnd = null, dateToday = null;
        try {
            //when was installed
            dateStart = formatter.parse(startDay);
            //todays date
            dateToday = formatter.parse(todayAsString);

            //calculate 30 days
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateStart);
            //forward 30 days
            cal.add(Calendar.DAY_OF_YEAR, 30);
            Date OneMonth = cal.getTime();
            oneMonthMili = cal.getTimeInMillis();


        } catch (ParseException e) {
            e.printStackTrace();
        }

        long milisec = oneMonthMili - todayMili;
        Log.e("difference", String.valueOf(milisec));

        if (milisec <= 0) {
            editor_sp.putBoolean(PASS_TAG, false);
            //editor_sp.putBoolean(Bought_within_thirtydays, false);
            editor_sp.commit();
        }

    }


    public void calulateTheSubscriptionPeriod() {

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        String todayAsString = formatter.format(today);
        String OneyearAsString = sp.getString(Oneyear_EndTime_TAG, "");

        Date dateStart = null, dateEnd = null;
        try {
            dateStart = formatter.parse(todayAsString);
            dateEnd = formatter.parse(OneyearAsString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(dateStart);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(dateEnd);

        long milisec = calendar2.getTimeInMillis() - calendar1.getTimeInMillis();
        Log.e("difference", String.valueOf(milisec));

        if (milisec <= 0) {
            editor_sp.putBoolean(PASS_TAG, false);
            editor_sp.putBoolean(Bought_within_thirtydays, false);
            editor_sp.commit();
        }

    }
////////////This part is only for subscrition dialog to show the days remaining

    public String calulateTrial() {

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        String startDay = sp.getString(Start_BoughtTime_TAG, "");

        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        String todayAsString = formatter.format(today);
        ;
        String res = "";
        // String startdate=formatter.parse(todayAsString);

        Date dateStart = null, dateEnd = null, dateToday = null;
        try {
            dateStart = formatter.parse(startDay);
            dateToday = formatter.parse(todayAsString);

            Calendar cal = Calendar.getInstance();
            cal.setTime(dateStart);
            //forward 30 days
            cal.add(Calendar.DAY_OF_YEAR, 30);
            Date OneMonth = cal.getTime();

            res = String.valueOf(daysBetween(dateToday, OneMonth));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return res;
    }

    public String calculateSubscriptionyear() {
        String res = "";
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        String todayAsString = sp.getString(Start_BoughtTime_TAG, "");
        String OneyearAsString = sp.getString(Oneyear_EndTime_TAG, "");

        Date dateStart = null, dateEnd = null;
        try {
            dateStart = formatter.parse(todayAsString);
            dateEnd = formatter.parse(OneyearAsString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(dateStart);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(dateEnd);

        res = String.valueOf(daysBetween(dateStart, dateEnd));
        return res;
    }

    public static Calendar getDatePart(Date date) {
        Calendar cal = Calendar.getInstance();       // get calendar instance
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
        cal.set(Calendar.MINUTE, 0);                 // set minute in hour
        cal.set(Calendar.SECOND, 0);                 // set second in minute
        cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second

        return cal;                                  // return the date part
    }

    public static long daysBetween(Date startDate, Date endDate) {
        Calendar sDate = getDatePart(startDate);
        Calendar eDate = getDatePart(endDate);

        long daysBetween = 0;
        while (sDate.before(eDate)) {
            sDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

}
