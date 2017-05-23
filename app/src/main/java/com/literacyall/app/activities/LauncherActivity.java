package com.literacyall.app.activities;

// This is the launcher activity.
//1. First screen
//2. There could be options for user to access and use the application

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.literacyall.app.Dialog.DialogNavBarHide;
import com.literacyall.app.R;
import com.literacyall.app.share.Share;
import com.literacyall.app.utilities.StaticAccess;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.IOException;
import java.util.Locale;

import static com.literacyall.app.utilities.Animanation.moveAnimation;

public class LauncherActivity extends BaseActivity implements View.OnClickListener {

    Intent intent;
    LauncherActivity activity;
    ProgressDialog pDialog;

    ImageButton ibtnTutorialCorrectAnswer, ibtnTutorialClassificate, ibtnTutorialMatch, ibtnTutorialRead, ibtnTutorialWrite, ibtnTutorialSequence;
    ImageButton ibtnMain, ibtnFile, ibtnInfoLauncher, ibtnBox1, ibtnBox2, ibtnBox3, ibtnEuropeanEn;
    ImageView ivLogo, ivBoy;
    private static final int MATERIAL_FILE_PICKER = 0x1;


    private String NORMAL_URL = "https://www.youtube.com/embed/y7kR6HrWXJE";
    private String DRAG_AND_DROP_URL = "https://www.youtube.com/embed/pA9UuUiaxKU";
    private String ASSISTIVE_URL = "https://www.youtube.com/embed/Pax7KbsJJIQ";
    private String READ_URL = "https://www.youtube.com/embed/yFvpac_IF0Q";
    private String SEQUENCE_URL = "https://www.youtube.com/embed/wsz6wxFh8Jo";
    private String WRITE_URL = "https://www.youtube.com/embed/mWahET7PMbA";
    // PDF Link
    private String METHODOLOGY_URL = "https://docs.google.com/viewer?url=http://www.literallcy.com/wp-content/uploads/2017/02/Literallcy-Methodology-guide-Teachers-ES.pdf";
    //private String METHODOLOGY_URL = "http://www.literallcy.com/wp-content/uploads/2017/02/Literallcy-Methodology-guide-Teachers-ES.pdf";
    private String QUICK_GUIDE_URL = "https://docs.google.com/viewer?url=http://www.literallcy.com/wp-content/uploads/2017/01/Literallcy_Quick_Guide_ES.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        activity = this;
        ibtnMain = (ImageButton) findViewById(R.id.ibtnMain);
        ivLogo = (ImageView) findViewById(R.id.ivLogo);
        ivBoy = (ImageView) findViewById(R.id.ivBoy);
        ibtnInfoLauncher = (ImageButton) findViewById(R.id.ibtnInfoLauncher);
        ibtnBox1 = (ImageButton) findViewById(R.id.ibtnBox1);
        ibtnBox2 = (ImageButton) findViewById(R.id.ibtnBox2);
        ibtnBox3 = (ImageButton) findViewById(R.id.ibtnBox3);
        ibtnEuropeanEn = (ImageButton) findViewById(R.id.ibtnEuropeanEn);

        ibtnMain.setOnClickListener(this);
        ibtnInfoLauncher.setOnClickListener(this);
        ibtnBox1.setOnClickListener(this);
        ibtnBox2.setOnClickListener(this);
        ibtnBox3.setOnClickListener(this);


        String locale = Locale.getDefault().getLanguage();
        if (locale.equals("es")) {
            ibtnEuropeanEn.setImageResource(R.drawable.img_european_es);
        } else {
            ibtnEuropeanEn.setImageResource(R.drawable.img_european_en);
        }


        ivLogo.setVisibility(View.INVISIBLE);
        moveAnimation(ibtnMain);


        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                animate(ivLogo);
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ibtnMain:
                intent = new Intent(LauncherActivity.this, TaskPackActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.ibtnInfoLauncher:
                dialogInfo();
                break;

            case R.id.ibtnBox1:
                dialogTutorial();
                break;

            case R.id.ibtnBox2:
                goToPDF(QUICK_GUIDE_URL);
                break;

            case R.id.ibtnBox3:
                goToPDF(METHODOLOGY_URL);

                break;

            case R.id.ibtnTutorialCorrectAnswer:
                goToYoutube(NORMAL_URL);

                break;
            case R.id.ibtnTutorialClassificate:
                goToYoutube(DRAG_AND_DROP_URL);
                break;
            case R.id.ibtnTutorialMatch:
                goToYoutube(ASSISTIVE_URL);
                break;
            case R.id.ibtnTutorialRead:
                goToYoutube(READ_URL);
                break;
            case R.id.ibtnTutorialWrite:
                goToYoutube(WRITE_URL);
                break;
            case R.id.ibtnTutorialSequence:
                goToYoutube(SEQUENCE_URL);
                break;

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == MATERIAL_FILE_PICKER) {
                String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                Share share = new Share(this);
                try {
                    share.unZip(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                share.readSharedTaskPackJSONtoDatabase();
                share.deleteReceivedFolder();
            }

        }

    }


    public void animate(final View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(-1000, 0, 0, 0);
        animate.setDuration(800);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                ivBoy.setImageResource(R.drawable.img_boy_emotion);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);
    }

    // Info dialog for showing information
    private void dialogInfo() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_info);
        DialogNavBarHide.navBarHide(this, dialog);
    }

    // Tutorial dialog for showing 6 video tutorial
    private void dialogTutorial() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_tutorial);

        ibtnTutorialCorrectAnswer = (ImageButton) dialog.findViewById(R.id.ibtnTutorialCorrectAnswer);
        ibtnTutorialClassificate = (ImageButton) dialog.findViewById(R.id.ibtnTutorialClassificate);
        ibtnTutorialMatch = (ImageButton) dialog.findViewById(R.id.ibtnTutorialMatch);
        ibtnTutorialRead = (ImageButton) dialog.findViewById(R.id.ibtnTutorialRead);
        ibtnTutorialWrite = (ImageButton) dialog.findViewById(R.id.ibtnTutorialWrite);
        ibtnTutorialSequence = (ImageButton) dialog.findViewById(R.id.ibtnTutorialSequence);

        ibtnTutorialCorrectAnswer.setOnClickListener(this);
        ibtnTutorialClassificate.setOnClickListener(this);
        ibtnTutorialMatch.setOnClickListener(this);
        ibtnTutorialRead.setOnClickListener(this);
        ibtnTutorialWrite.setOnClickListener(this);
        ibtnTutorialSequence.setOnClickListener(this);


        DialogNavBarHide.navBarHide(this, dialog);
    }


    void goToYoutube(String url) {
        Intent goTOYoutube = new Intent(activity, YoutubeActivity.class);
        goTOYoutube.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        goTOYoutube.putExtra(StaticAccess.TAG_INTENT_YOUTUBE, url);
        startActivity(goTOYoutube);
        finish();
    }

    void goToPDF(String url) {
        Intent goToPdf = new Intent(activity, PDFActivity.class);
        goToPdf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        goToPdf.putExtra(StaticAccess.TAG_INTENT_PDF_LINK, url);
        startActivity(goToPdf);
        finish();
    }
}
