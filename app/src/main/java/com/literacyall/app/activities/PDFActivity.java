package com.literacyall.app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.literacyall.app.R;
import com.literacyall.app.utilities.StaticAccess;

/**
 * Created by RAFI on 1/25/2017.
 */

public class PDFActivity extends BaseActivity {
    PDFActivity activity;
    WebView wvPDFId;
    ImageView ivCross;
    private String getPDFLink;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        activity = this;
        progressDialog = new ProgressDialog(activity);
        wvPDFId = (WebView) findViewById(R.id.wvPDFId);
        ivCross = (ImageView) findViewById(R.id.ivCrossPDF);
        getPDFLink = getIntent().getExtras().getString(StaticAccess.TAG_INTENT_PDF_LINK);
        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentU = new Intent(activity, LauncherActivity.class);
                startActivity(intentU);
                finish();
            }
        });
        wvPDFId.setWebViewClient(new MyBrowser());
        if (getPDFLink != null) {
            wvPDFId.getSettings().setJavaScriptEnabled(true);
            wvPDFId.loadUrl(getPDFLink);
        }
        wvPDFId.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {
                progressDialog.setMessage(getString(R.string.pleaseWait));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();
                if (progress == 100)
                    progressDialog.dismiss();
            }
        });
    }


    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(activity, LauncherActivity.class));
        finish();
    }
}
