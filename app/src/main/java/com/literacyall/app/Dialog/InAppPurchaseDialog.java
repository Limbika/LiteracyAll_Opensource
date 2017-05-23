package com.literacyall.app.Dialog;

import com.literacyall.app.R;
import com.literacyall.app.activities.InAppPurchaseActivity;
import com.literacyall.app.utilities.CustomToast;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ibrar on 9/26/2016.
 */
public class InAppPurchaseDialog extends Dialog implements View.OnClickListener {

    Button btnCancel, btnSubscribe, btnSubscribeBuy, btnBuy;
    Context context;
    InAppPurchaseActivity activity;
    EditText edtSubscribe;
    TextView tvRemain;
    String bpPurchase;
    String remaining="";
    String mailAddress = "sat@groupuniq.com";

    public InAppPurchaseDialog(Context context, InAppPurchaseActivity activity,String remaining) {
        super(context, R.style.CustomAlertDialog);
        this.context = context;
        this.activity = activity;
        this.remaining = remaining;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.90);
        int screenHeight = (int) (metrics.heightPixels * 0.90);
        getWindow().setLayout(screenWidth, screenHeight);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.dialog_in_app_purchase);

        edtSubscribe = (EditText) findViewById(R.id.edtSubscribe);
        tvRemain = (TextView) findViewById(R.id.tvRemain);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSubscribe = (Button) findViewById(R.id.btnSubscribe);
        btnSubscribeBuy = (Button) findViewById(R.id.btnSubscribeBuy);
        btnBuy = (Button) findViewById(R.id.btnBuy);

        tvRemain.setText(remaining);

        btnCancel.setOnClickListener(this);
        btnSubscribe.setOnClickListener(this);
        btnSubscribeBuy.setOnClickListener(this);
        btnBuy.setOnClickListener(this);
        edtSubscribe.setText(getEmail(activity));


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnCancel:
                //activity.bp.purchase(activity, activity.PRODUCT_ID);
                dismiss();
                break;

            case R.id.btnSubscribe:

                //activity.bp.purchase(activity, activity.PRODUCT_ID);
               /* setCondition();
                dismiss();
                bpPurchase = "hide";*/
                sendMail();
                dismiss();
                break;

            case R.id.btnSubscribeBuy:

                sendMail();
                setCondition();
                dismiss();
                bpPurchase = "show";
                break;

            case R.id.btnBuy:

                // activity.bp.purchase(activity, activity.PRODUCT_ID);
                setCondition();
                bpPurchase = "show";
                dismiss();

                /*if (!activity.bp.isPurchased(activity.PRODUCT_ID)) {

                } else {
                    btnSubscribeBuy.setVisibility(View.GONE);
                }*/

                break;

        }

    }



    public void setCondition() {
        final Dialog dialog = new Dialog(activity, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_purchase_condition);
        dialog.setCancelable(false);
        TextView tvCondition = (TextView) dialog.findViewById(R.id.tvCondition);
        TextView tvContent = (TextView) dialog.findViewById(R.id.tvContent);
        tvContent.setMovementMethod(new ScrollingMovementMethod());

        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        Button btnAccept = (Button) dialog.findViewById(R.id.btnAccept);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                dialog.dismiss();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //activity.bp.purchase(activity, activity.PRODUCT_ID);
                setBpPurchase();
                //sendMail();
                dismiss();
                dialog.dismiss();
            }
        });

        DialogNavBarHide.navBarHide(activity, dialog);
    }



    public void setBpPurchase(){

        if(bpPurchase.equals("show")) {
            activity.bp.purchase(activity, activity.PRODUCT_ID);
        }else if( bpPurchase.equals("hide") ){
            CustomToast.m("a");
        }
    }


    /*public void checkValidation() {
        final Dialog dialog = new Dialog(activity, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.validation_check_dialog);
        dialog.setCancelable(true);
        TextView tvCondition = (TextView) dialog.findViewById(R.id.tvCondition);
        TextView tvContent = (TextView) dialog.findViewById(R.id.tvContent);
        tvContent.setMovementMethod(new ScrollingMovementMethod());

        Button btnCheck = (Button) dialog.findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // activity.bp.purchase(activity, activity.PRODUCT_ID);
                //dismiss();
                dialog.dismiss();

            }
        });

        DialogNavBarHide.navBarHide(activity, dialog);
    }*/


    private void sendMail() {

        Intent myIntent = new Intent(Intent.ACTION_SEND);
        PackageManager pm = activity.getPackageManager();
        Intent tempIntent = new Intent(Intent.ACTION_SEND);
        tempIntent.setType("*/*");
        List<ResolveInfo> resInfo = pm.queryIntentActivities(tempIntent, 0);
        for (int i = 0; i < resInfo.size(); i++) {
            ResolveInfo ri = resInfo.get(i);
            if (ri.activityInfo.packageName.contains("android.gm")) {
                myIntent.setComponent(new ComponentName(ri.activityInfo.packageName, ri.activityInfo.name));
                myIntent.setAction(Intent.ACTION_SEND);
                myIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mailAddress});
                myIntent.setType("message/rfc822");
                myIntent.putExtra(Intent.EXTRA_TEXT, activity.getResources().getString(R.string.emailText));
                myIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getResources().getString(R.string.emailSubject));
                // myIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("uri://your/uri/string"));



            }
        }
        activity.startActivity(myIntent);

    }

    static String getEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);

        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }



}
