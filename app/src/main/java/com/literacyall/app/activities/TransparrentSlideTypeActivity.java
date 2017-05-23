package com.literacyall.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.literacyall.app.R;

public class TransparrentSlideTypeActivity extends BaseActivity implements View.OnClickListener {

    TransparrentSlideTypeActivity activity;
    RelativeLayout rlOutSlide;
    LinearLayout llTopSlideEntry, llBottomSlideEntry;
    Button btnEntryTopMatch, btnEntryTopAnswer, btnEntryTopWrite, btnEntryTopRead, btnEntryTopClassificate,
            btnEntryBottomMatch, btnEntryBottomAnswer, btnEntryBottomWrite, btnEntryBottomRead, btnEntryBottomClassificate,btnEntryTopSequence,btnEntryBottomSequence;


    //main activity=1, taskpack=2
    int chooser=0;
    public static final String ChooserTransparentDialog ="chooserDialog";
    int result=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transparrent_slide);

        activity = this;

        rlOutSlide = (RelativeLayout) findViewById(R.id.rlOutSlide);
        if(getIntent().getExtras()!=null)
        chooser=getIntent().getExtras().getInt(ChooserTransparentDialog,-1);

        llTopSlideEntry = (LinearLayout) findViewById(R.id.llTopSlideEntry);
        llBottomSlideEntry = (LinearLayout) findViewById(R.id.llBottomSlideEntry);

        if(chooser==1){
            llTopSlideEntry.setVisibility(View.INVISIBLE);
            //topToBottomFeedBackAnimation(llTopSlideEntry);
        }else if(chooser==2){
            llBottomSlideEntry.setVisibility(View.INVISIBLE);
            //bottomToTopFeedBackAnimation(llBottomSlideEntry);
        }

        btnEntryTopMatch = (Button) findViewById(R.id.btnEntryTopMatch);
        btnEntryTopAnswer = (Button) findViewById(R.id.btnEntryTopAnswer);
        btnEntryTopWrite = (Button) findViewById(R.id.btnEntryTopWrite);
        btnEntryTopRead = (Button) findViewById(R.id.btnEntryTopRead);
        btnEntryTopClassificate = (Button) findViewById(R.id.btnEntryTopClassificate);
        btnEntryTopSequence = (Button) findViewById(R.id.btnEntryTopSequence);

        btnEntryBottomMatch = (Button) findViewById(R.id.btnEntryBottomMatch);
        btnEntryBottomAnswer = (Button) findViewById(R.id.btnEntryBottomAnswer);
        btnEntryBottomWrite = (Button) findViewById(R.id.btnEntryBottomWrite);
        btnEntryBottomRead = (Button) findViewById(R.id.btnEntryBottomRead);
        btnEntryBottomClassificate = (Button) findViewById(R.id.btnEntryBottomClassificate);
        btnEntryBottomSequence= (Button) findViewById(R.id.btnEntryBottomSequence);


        rlOutSlide.setOnClickListener(this);
        llTopSlideEntry.setOnClickListener(this);
        llBottomSlideEntry.setOnClickListener(this);

        btnEntryTopMatch.setOnClickListener(this);
        btnEntryTopAnswer.setOnClickListener(this);
        btnEntryTopWrite.setOnClickListener(this);
        btnEntryTopRead.setOnClickListener(this);
        btnEntryTopClassificate.setOnClickListener(this);
        btnEntryTopSequence.setOnClickListener(this);
        btnEntryBottomMatch.setOnClickListener(this);
        btnEntryBottomAnswer.setOnClickListener(this);
        btnEntryBottomWrite.setOnClickListener(this);
        btnEntryBottomRead.setOnClickListener(this);
        btnEntryBottomClassificate.setOnClickListener(this);
        btnEntryBottomSequence.setOnClickListener(this);
    }
    public void finishActivity2(){
        setResult(result);
        finish();
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.llTopSlideEntry) {

        } else if (v.getId() == R.id.llBottomSlideEntry) {

        } else if (v.getId() == R.id.rlOutSlide) {
            finishActivity2();
        } else if (v.getId() == R.id.btnEntryTopMatch) {
            result=0;
            finishActivity2();
        } else if (v.getId() == R.id.btnEntryTopAnswer) {
            result=1;
            finishActivity2();
        } else if (v.getId() == R.id.btnEntryTopWrite) {
            result=2;
            finishActivity2();
        } else if (v.getId() == R.id.btnEntryTopRead) {
            result=3;
            finishActivity2();
        } else if (v.getId() == R.id.btnEntryTopClassificate) {
            result=4;
            finishActivity2();
        } else if (v.getId() == R.id.btnEntryTopSequence) {
            result=5;
            finishActivity2();
        } else if (v.getId() == R.id.btnEntryBottomMatch) {
            result=0;
            finishActivity2();
        } else if (v.getId() == R.id.btnEntryBottomAnswer) {
            result=1;
            finishActivity2();
        } else if (v.getId() == R.id.btnEntryBottomWrite) {
            result=2;
            finishActivity2();
        } else if (v.getId() == R.id.btnEntryBottomRead) {
            result=3;
            finishActivity2();
        } else if (v.getId() == R.id.btnEntryBottomClassificate) {
            result=4;
            finishActivity2();
        }else if (v.getId() == R.id.btnEntryBottomSequence) {
            result=5;
            finishActivity2();
        }


    }
}
