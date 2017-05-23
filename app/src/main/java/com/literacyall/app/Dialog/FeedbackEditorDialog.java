package com.literacyall.app.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.literacyall.app.R;
import com.literacyall.app.activities.MainActivity;
import com.literacyall.app.adapter.FeedbackEditorAdapter;
import com.literacyall.app.listener.RecyclerViewClickListener;
import com.literacyall.app.utilities.ImageProcessing;
import com.literacyall.app.utilities.StaticAccess;

import java.util.ArrayList;

import static com.literacyall.app.R.id.ibtnFeedbackCamera;
import static com.literacyall.app.R.id.ibtnFeedbackGallery;
import static com.literacyall.app.R.id.ibtnFeedbackInternet;


/**
 * Created by RAFI on 7/19/2016.
 */
public class FeedbackEditorDialog extends Dialog implements View.OnClickListener {

    public Context context;
    MainActivity activity;
    ArrayList<String> feedBackImages;   // list of available files in  path
    ImageButton ibtnFeedbackImgSeclec;
    FeedbackEditorAdapter feedbackEditorAdapter;
    RecyclerView rvFeedback;
    ImageProcessing imageProcessing;
    FeedbackEditorDialog feedbackEditorDialog;
    CheckBox chkDialogType;
    int feedBackType;

    ImageButton ibtnCamera, ibtnGallery, ibtnInternet;

   /* public FloatingActionButton fab;
    public Toolbar toolbar;*/


    public FeedbackEditorDialog(Context context, ArrayList<String> feedBackImages, int type) {
        super(context, R.style.CustomAlertDialog);
        this.context = context;
        this.feedBackImages = feedBackImages;
        feedbackEditorDialog = this;
        activity = (MainActivity) context;
        feedBackType = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_feedback_editor);

        imageProcessing = new ImageProcessing(context);
        chkDialogType = (CheckBox) findViewById(R.id.chkDialogType);
        rvFeedback = (RecyclerView) findViewById(R.id.rvFeedback);
        rvFeedback.setHasFixedSize(true);
        rvFeedback.setItemAnimator(new DefaultItemAnimator());
        rvFeedback.setLayoutManager(new LinearLayoutManager(context));
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvFeedback.setLayoutManager(gridLayoutManager);
        chkDialogType.setChecked((feedBackType == StaticAccess.TAG_TYPE_CIRCULAR) ? true : false);

        /*ibtnFeedbackImgSeclec = (ImageButton) findViewById(R.id.ibtnFeedbackInternet);
        ibtnFeedbackImgSeclec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activity.imageSelectionDialog = new ImageSelectionDialog(context, activity, activity.feedbackImageFlag);
                DialogNavBarHide.navBarHide(activity, activity.imageSelectionDialog);
                feedbackEditorDialog.dismiss();
            }
        });*/

        // setFab();
        if (feedBackImages != null)
            feedbackEditorAdapter = new FeedbackEditorAdapter(context, feedBackImages, new RecyclerViewClickListener() {
                @Override
                public void recyclerViewListClicked(View v, int position) {
                    if (feedBackImages != null) {
                        MainActivity mainActivity = (MainActivity) context;
                        int type = chkDialogType.isChecked() ? StaticAccess.TAG_TYPE_CIRCULAR : StaticAccess.TAG_TYPE_RECTANGULAR;
                        mainActivity.setFeedbackImage(imageProcessing.imageSave(BitmapFactory.decodeFile(feedBackImages.get(position))), type);
                        feedbackEditorDialog.dismiss();
                    }
                }
            });
        rvFeedback.setAdapter(feedbackEditorAdapter);

        ibtnCamera = (ImageButton) findViewById(ibtnFeedbackCamera);
        ibtnGallery = (ImageButton) findViewById(ibtnFeedbackGallery);
        ibtnInternet = (ImageButton) findViewById(ibtnFeedbackInternet);



        ibtnCamera.setOnClickListener(this);
        ibtnGallery.setOnClickListener(this);
        ibtnInternet.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ibtnFeedbackCamera:
                activity.loadImageCamera(2);
                break;

            case R.id.ibtnFeedbackGallery:
                activity.loadImageGallery(2);
                break;

            case R.id.ibtnFeedbackInternet:
                activity.loadImageInternet(2);

                break;
        }

    }
}




