package com.literacyall.app.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.literacyall.app.R;
import com.literacyall.app.activities.MainActivity;

public class ImageSelectionDialog extends Dialog implements View.OnClickListener {

    MainActivity mainActivity;
    Context context;
    ImageButton ibtnCamera, ibtnGallery, ibtnInternet;
    // flag for using item or task
    int flag;

    public ImageSelectionDialog(){
        super(null);
    }

    public ImageSelectionDialog(Context context, MainActivity mainActivity, int flag) {

        // flag for using item or task
        super(context, R.style.CustomAlertDialog);
        this.context = context;
        this.mainActivity = mainActivity;
        this.flag=flag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_selection_dialog);

        ibtnCamera = (ImageButton) findViewById(R.id.ibtnCamera);
        ibtnGallery = (ImageButton) findViewById(R.id.ibtnGallery);
        ibtnInternet = (ImageButton) findViewById(R.id.ibtnInternet);


        ibtnCamera.setOnClickListener(this);
        ibtnGallery.setOnClickListener(this);
        ibtnInternet.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ibtnCamera:
                mainActivity.loadImageCamera(flag);
                mainActivity.imageSelectionDialog.dismiss();
                break;

            case R.id.ibtnGallery:
                mainActivity.loadImageGallery(flag);
                mainActivity.imageSelectionDialog.dismiss();
                break;

            case R.id.ibtnInternet:

                    mainActivity.loadImageInternet(flag);
                    mainActivity.imageSelectionDialog.dismiss();


                break;
        }

    }
}
