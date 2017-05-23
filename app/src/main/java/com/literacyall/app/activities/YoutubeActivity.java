package com.literacyall.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.literacyall.app.R;
import com.literacyall.app.utilities.StaticAccess;

/**
 * Created by RAFI on 1/25/2017.
 */

public class YoutubeActivity extends BaseActivity {
    YoutubeActivity activity;
    WebView wvYouTubeId;
    String getYouTubeLink;
    private ImageView ivCross;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);
        activity = this;
        wvYouTubeId = (WebView) findViewById(R.id.wvYouTubeId);
        getYouTubeLink = getIntent().getExtras().getString(StaticAccess.TAG_INTENT_YOUTUBE);
        ivCross = (ImageView) findViewById(R.id.ivCross);
        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wvYouTubeId.destroy();
                Intent intentU = new Intent(activity, LauncherActivity.class);
                startActivity(intentU);
                finish();
            }
        });
        wvYouTubeId.setWebViewClient(new MyBrowser());
        if (getYouTubeLink != null) {
            wvYouTubeId.getSettings().setLoadsImagesAutomatically(true);
            wvYouTubeId.getSettings().setJavaScriptEnabled(true);
            wvYouTubeId.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            wvYouTubeId.loadUrl(getYouTubeLink);
        }
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
