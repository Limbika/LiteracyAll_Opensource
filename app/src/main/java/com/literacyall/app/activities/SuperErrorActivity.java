package com.literacyall.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.literacyall.app.R;
import com.literacyall.app.utilities.ApplicationMode;
import com.literacyall.app.utilities.ImageProcessing;
import com.literacyall.app.utilities.StaticAccess;

public class SuperErrorActivity extends BaseActivity {

    RelativeLayout rlBackGroungError;
    ImageView ivError;
    TextView tvErrorMessage;
    ImageProcessing imageProcessing;
    SuperErrorActivity activity;
    String getErrorImagePath, getErrorText,getErrorSound="";
    int getColor, getMode;
    private static long TIME_STAY,TARGET;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_error);
        activity = this;
        imageProcessing = new ImageProcessing(activity);
        getErrorImagePath = getIntent().getExtras().getString(StaticAccess.TAG_PLAY_ERROR_IMAGE);
        getErrorText = getIntent().getExtras().getString(StaticAccess.TAG_PLAY_ERROR_TEXT);
        getColor = getIntent().getExtras().getInt(StaticAccess.TAG_PLAY_ERROR_COLOR);
        getMode = getIntent().getExtras().getInt(StaticAccess.TAG_TASK_MODE_KEY);
        TARGET = getIntent().getExtras().getLong(StaticAccess.TARGET_KEY,-1);
        getErrorSound=getIntent().getExtras().getString(StaticAccess.TAG_PLAY_ERROR_SOUND);

        rlBackGroungError = (RelativeLayout) findViewById(R.id.rlError);
        ivError = (ImageView) findViewById(R.id.ivError);
        tvErrorMessage = (TextView) findViewById(R.id.tvErrorMessage);

        if (getErrorText.length() > 0 && getErrorText != null) {
            tvErrorMessage.setText(getErrorText);
        } else {
            tvErrorMessage.setText(getResources().getString(R.string.emptyErrorMsg));
        }
        if (getErrorImagePath.length() > 0 && getErrorImagePath != null) {
            imageProcessing.setImageWith_loader(ivError, getErrorImagePath);
        } else {
            ivError.setImageResource(R.drawable.img_women);
        }
        if (getColor != 0) {
            rlBackGroungError.setBackgroundColor(getColor);
        } else {
            rlBackGroungError.setBackgroundColor(getResources().getColor(R.color.redLight));
        }
        if(getErrorSound!=null&&getErrorSound.length()>0){
         playSound(getErrorSound);
        }else {

        }


        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent returnIntent = new Intent();
                returnIntent.putExtra(StaticAccess.TAG_PLAY_ERROR_RESPONSE, StaticAccess.TAG_PLAY_ERROR_RESPONSE);
                if(TARGET!=-1)
                returnIntent.putExtra(StaticAccess.TARGET_KEY, TARGET);

                if (getMode == StaticAccess.TAG_TASK_GENERAL_MODE) {
                    setResult(StaticAccess.TAG_TASK_GENERAL_MODE, returnIntent);
                } else if (getMode == StaticAccess.TAG_TASK_DRAG_AND_DROP_MODE) {
                    setResult(StaticAccess.TAG_TASK_DRAG_AND_DROP_MODE, returnIntent);
                } else if (getMode == StaticAccess.TAG_TASK_ASSISTIVE_MODE) {
                    setResult(StaticAccess.TAG_TASK_ASSISTIVE_MODE, returnIntent);
                }
                mpSoundRelease();
                finish();
            }
        }, errorScreenDisplayTime());

    }

    private void playSound(String getErrorSound) {
        if (getErrorSound.length() > 0) {
            mpSoundRelease();
            mp = new MediaPlayer();
            try {
                mp.setDataSource(Environment.getExternalStorageDirectory() + "/Android/Data/" + activity.getPackageName() + "/Sound/" + getErrorSound);
                mp.prepare();
                mp.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            // default sound set
        }
    }

    // sound release created by reaz
    public void mpSoundRelease() {
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }




    public long errorScreenDisplayTime(){
        if(!ApplicationMode.devMode){
            TIME_STAY = 15000;
        }else {
            TIME_STAY = 5000;
        }
        return TIME_STAY;
    }

}
