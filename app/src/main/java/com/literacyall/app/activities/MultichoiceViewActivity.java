package com.literacyall.app.activities;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.literacyall.app.R;
import com.literacyall.app.customui.CircularCartView;
import com.literacyall.app.dao.TaskPack;
import com.literacyall.app.interfaces.MultichoiceAdapterInterface;
import com.literacyall.app.utilities.Animanation;

import java.util.ArrayList;

public abstract class MultichoiceViewActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    GridView gvTaskPackCase;
    ImageButton ibtnErase,  ibtnShare, ibtnSearch, ibtnImport, ibtnNewPack, ibtnEdit, ibtnPlay, ibtnPackEdit, ibtnBack, ibtnClone;
    CircularCartView ctvCart;
    LinearLayout llWholeLayOut;
    Button btnAll, btnAnswer, btnClassificate, btnMatch, btnRead, btnWrite, btnSequence;
    LinearLayout linearUpTaskpackContainer;
    MultichoiceViewActivity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multichoice_view);
        activity = this;
        findViewById();
    }


    // All Id initialization and listener set(this) by Rokan
    public void findViewById() {

        llWholeLayOut = (LinearLayout) findViewById(R.id.llWholeLayOut);
        gvTaskPackCase = (GridView) findViewById(R.id.gvTaskPackCase);
        ibtnErase = (ImageButton) findViewById(R.id.ibtnErase);
        ctvCart = (CircularCartView) findViewById(R.id.ctvCart);
        ibtnShare = (ImageButton) findViewById(R.id.ibtnShare);
        ibtnPackEdit = (ImageButton) findViewById(R.id.ibtnPackEdit);
        ibtnSearch = (ImageButton) findViewById(R.id.ibtnSearch);
        ibtnImport = (ImageButton) findViewById(R.id.ibtnImport);
        ibtnNewPack = (ImageButton) findViewById(R.id.ibtnNewPack);
        ibtnEdit = (ImageButton) findViewById(R.id.ibtnEdit);
        ibtnPlay = (ImageButton) findViewById(R.id.ibtnPlay);
        ibtnBack = (ImageButton) findViewById(R.id.ibtnBack);
        ibtnClone = (ImageButton) findViewById(R.id.ibtnClone);
        btnAll = (Button) findViewById(R.id.btnAll);
        btnAnswer = (Button) findViewById(R.id.btnAnswer);
        btnClassificate = (Button) findViewById(R.id.btnClassificate);
        btnMatch = (Button) findViewById(R.id.btnMatch);
        btnRead = (Button) findViewById(R.id.btnRead);
        btnWrite = (Button) findViewById(R.id.btnWrite);
        btnSequence = (Button) findViewById(R.id.btnSequence);

        linearUpTaskpackContainer = (LinearLayout) findViewById(R.id.linearUpTaskpackContainer);

        gvTaskPackCase.setOnItemClickListener(this);
        gvTaskPackCase.setOnItemLongClickListener(this);
        ibtnErase.setOnClickListener(this);
        ctvCart.setOnClickListener(this);
        ibtnShare.setOnClickListener(this);
        ibtnPackEdit.setOnClickListener(this);
        ibtnSearch.setOnClickListener(this);
        ibtnImport.setOnClickListener(this);
        ibtnNewPack.setOnClickListener(this);
        ibtnEdit.setOnClickListener(this);
        ibtnPlay.setOnClickListener(this);
        btnAll.setOnClickListener(this);
        btnAnswer.setOnClickListener(this);
        btnClassificate.setOnClickListener(this);
        btnMatch.setOnClickListener(this);
        btnRead.setOnClickListener(this);
        btnWrite.setOnClickListener(this);
        btnSequence.setOnClickListener(this);
        ibtnBack.setOnClickListener(this);
        ibtnClone.setOnClickListener(this);
        linearUpTaskpackContainer.setOnClickListener(this);

    }



    // Abstract method called  according to listener by Rokan

    @Override
    public void onClick(View v) {

        Animanation.zoomOut(v);

        switch (v.getId()) {

            case R.id.ibtnErase:
                eraseButtonClicked(v);
                break;

            case R.id.ctvCart:
                cartButtonClicked(v);
                break;

            case R.id.ibtnShare:
                shareButtonClicked(v);
                break;

            case R.id.ibtnPackEdit:
                packEditButtonClicked(v);
                break;

            case R.id.ibtnSearch:
                searchButtonClicked(v);
                break;

            case R.id.ibtnImport:
                exportButtonClicked(v);
                break;

            case R.id.ibtnNewPack:
                newPackButtonClicked(v);
                break;

            case R.id.ibtnEdit:
                editButtonClicked(v);
                break;

            case R.id.ibtnPlay:
                playButtonClicked(v);
                break;

            case R.id.btnAll:
                rendomOutSideClicked(v);
                allButtonClicked(v);
                break;

            case R.id.btnAnswer:
                rendomOutSideClicked(v);
                answerButtonClicked(v);
                break;

            case R.id.btnClassificate:
                rendomOutSideClicked(v);
                classificateButtonClicked(v);
                break;

            case R.id.btnMatch:
                rendomOutSideClicked(v);
                matchButtonClicked(v);
                break;

            case R.id.btnRead:
                rendomOutSideClicked(v);
                readButtonClicked(v);
                break;

            case R.id.btnWrite:
                rendomOutSideClicked(v);
                writeButtonClicked(v);
                break;

            case R.id.btnSequence:
                rendomOutSideClicked(v);
                sequenceButtonClicked(v);
                break;


            case R.id.linearUpTaskpackContainer:
                rendomOutSideClicked(v);
                break;

            case R.id.ibtnClone:
                cloneButtonClicked(v);
                break;

            case R.id.ibtnBack:
                backButtonClicked(v);
            case R.id.llWholeLayOut:
                hideSoftKeyBoard();
                break;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onItemClickForGridView(parent, view, position, id);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        onItemLongClickForGridView(parent, view, position, id);
        return true;
    }


    // All abstract method created according to listener by Rokan
    public abstract void eraseButtonClicked(View v);

    public abstract void cartButtonClicked(View v);

    public abstract void shareButtonClicked(View v);

    public abstract void packEditButtonClicked(View v);

    public abstract void searchButtonClicked(View v);

    public abstract void exportButtonClicked(View v);

    public abstract void newPackButtonClicked(View v);

    public abstract void editButtonClicked(View v);

    public abstract void playButtonClicked(View v);

    public abstract void allButtonClicked(View v);

    public abstract void answerButtonClicked(View v);

    public abstract void classificateButtonClicked(View v);

    public abstract void matchButtonClicked(View v);

    public abstract void readButtonClicked(View v);

    public abstract void writeButtonClicked(View v);

    public abstract void sequenceButtonClicked(View v);

    public abstract void backButtonClicked(View v);

    public abstract void cloneButtonClicked(View v);

    public abstract void onItemClickForGridView(AdapterView<?> parent, View view, int position, long id);

    public abstract void onItemLongClickForGridView(AdapterView<?> parent, View view, int position, long id);


    //this is the communication bridge between the Multichoice base adapter and the Activity
    //only the adapter that extends Multichoice Adapter calls these methods we made it just to be sure whocevers uses this mechanism has these functions
    public abstract void setMultichoiceListener(MultichoiceAdapterInterface.ControlMethods listener);

    public abstract void singleTapDone(long key);

    public abstract void singleTapModeOn(long key);

    public abstract void singleTapModeClear();

    public abstract void multiChoiceClear();

    public abstract void multiChoiceModeEnter(ArrayList<TaskPack> taskPacks, boolean mode);

    public abstract void rendomOutSideClicked(View v);

    public abstract void hideSoftKeyBoard();

}
