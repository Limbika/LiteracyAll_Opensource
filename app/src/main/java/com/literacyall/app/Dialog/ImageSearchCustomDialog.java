package com.literacyall.app.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.literacyall.app.R;
import com.literacyall.app.activities.MainActivity;
import com.literacyall.app.utilities.ImageProcessing;

import java.io.File;
import java.util.Date;


public class ImageSearchCustomDialog extends Dialog implements android.view.View.OnClickListener {
    public Activity c;
    MainActivity mainActivity;
    ImageProcessing imgProc;
    public Dialog dialog;
    private WebView webView;
    private ImageButton ibtnTake, ibtnGoBack;
    private String appImagePath = null;
    // private File mFileTemp;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
   int  internetFlag;

    public ImageSearchCustomDialog(){
        super(null);
    }

    public ImageSearchCustomDialog(Activity a, MainActivity mainActivity, int internetFlag) {
        super(a, R.style.CustomAlertDialog);
        this.c = a;
        this.mainActivity = mainActivity;
        this.internetFlag=internetFlag;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        int screenHeight = (int) (metrics.heightPixels * 0.80);
        getWindow().setLayout(screenWidth, screenHeight);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.image_search_custom_dialog);
        setCancelable(true);


        webView = (WebView) findViewById(R.id.wvImageSearch);
        ibtnTake = (ImageButton) findViewById(R.id.ibtnTake);
        ibtnGoBack = (ImageButton) findViewById(R.id.ibtnGoBack);
        LoadImageSearchData();
        ibtnTake.setOnClickListener(this);
        ibtnGoBack.setOnClickListener(this);
        imgProc = new ImageProcessing(c);
        //appImagePath = "/Android/Data/"+ c.getPackageName()+"/Images/";
        appImagePath = imgProc.getImageDir();

        this.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mainActivity != null) {
                    mainActivity.isImageSearchDialogShow = false;
                }
            }
        });
    }


    private void LoadImageSearchData() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl("https://www.google.com/search?q=images&biw=1366&bih=667&source=lnms&tbm=isch&sa=X&ved=0ahUKEwiE1aHol67MAhWQkI4KHdpiAKAQ_AUIBygB");
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            View v = webView;
            v.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
            v.setDrawingCacheEnabled(false);

            if (mainActivity != null) {
                if(internetFlag==0){
                    mainActivity.openCropper(Uri.fromFile(new File(String.valueOf(imgProc.imageSaveToCrop(bitmap, mainActivity.mFileTemp)))));
                }else if(internetFlag==1){

                    mainActivity.openCropper(Uri.fromFile(new File(String.valueOf(imgProc.imageSaveToCrop(bitmap, mainActivity.mFileTemp)))),1);
                }
                else if(internetFlag==2){

                    mainActivity.openCropper(Uri.fromFile(new File(String.valueOf(imgProc.imageSaveToCrop(bitmap, mainActivity.mFileTemp)))),2);
                }


                dismiss();
            }

        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.ibtnTake) {
            takeScreenshot();
        } else if (v.getId() == R.id.ibtnGoBack) {
            webView.goBack();
        }

    }

}
