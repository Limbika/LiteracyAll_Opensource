package com.literacyall.app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;

import com.limbika.material.dialog.FileDialog;
import com.limbika.material.dialog.SelectorDialog;
import com.literacyall.app.Dialog.DialogNavBarHide;
import com.literacyall.app.R;
import com.literacyall.app.adapter.ImagePickerAdapter;
import com.literacyall.app.adapter.ImagePickerGridAdapter;
import com.literacyall.app.pojo.ImageModel;
import com.literacyall.app.utilities.SharedPreferenceValue;
import com.literacyall.app.utilities.StaticAccess;

import java.io.File;
import java.util.ArrayList;


public class ImagePickerActivity extends BaseActivity {

    ImagePickerActivity activity;
    RecyclerView rvImagePicker;
    ImagePickerAdapter imagePickerAdapter;
    private ArrayList<ImageModel> imageList;
    ImageButton ibtnImagePickerSelect;
    public String which;
    EditText edtImagePickerSearch;
    GridView gvImagePicker;
    ImagePickerGridAdapter imagePickerGridAdapter;
    private FileDialog fileDialog;
    private ImageButton ibtnBackImagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        activity = this;

        ibtnImagePickerSelect = (ImageButton) findViewById(R.id.ibtnImagePickerSelect);
        which = getIntent().getExtras().getString(StaticAccess.TAG_INTENT_IMG);
        edtImagePickerSearch = (EditText) findViewById(R.id.edtImagePickerSearch);
        gvImagePicker = (GridView) findViewById(R.id.gvImagePicker);
        ibtnBackImagePicker = (ImageButton) findViewById(R.id.ibtnBackImagePicker);
      /*  rvImagePicker = (RecyclerView) findViewById(R.id.rvImagePicker);
        rvImagePicker.setHasFixedSize(true);
        rvImagePicker.setItemAnimator(new DefaultItemAnimator());
        rvImagePicker.setLayoutManager(new LinearLayoutManager(activity));
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL);
        rvImagePicker.setLayoutManager(gridLayoutManager);*/
        if (which.equals(StaticAccess.TAG_SELECT_SOUND)) {
            if (SharedPreferenceValue.getFileSoundPath(activity) != null) {
                imageList = getFilePickerSounds(SharedPreferenceValue.getFileSoundPath(activity));
                gridView(imageList);
            }
        } else {
            if (SharedPreferenceValue.getFilePath(activity) != null) {

                imageList = getImagePickerImages(SharedPreferenceValue.getFilePath(activity));

                if (imageList != null)
                    gridView(imageList);
            }
        }


        ibtnImagePickerSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileDialog = new FileDialog(activity, FileDialog.Strategy.DIR);
                fileDialog.setCancelable(false);
                if (which.equals(StaticAccess.TAG_SELECT_SOUND)) {
                    fileDialog.setPath(SharedPreferenceValue.getFileSoundPath(activity));
                } else {
                    fileDialog.setPath(SharedPreferenceValue.getFilePath(activity));

                }
                DialogNavBarHide.navBarHide(activity, fileDialog);
                fileDialog.setOnSelectListener(new SelectorDialog.OnSelectListener() {
                    @Override
                    public void onSelect(String data) {

                        if (which.equals(StaticAccess.TAG_SELECT_SOUND)) {
                            SharedPreferenceValue.setFileSoundPath(activity, data);
                            imageList = getFilePickerSounds(data);
                            gridView(imageList);
                        } else {
                            SharedPreferenceValue.setFilePath(activity, data);
                            imageList = getImagePickerImages(data);
                            gridView(imageList);
                        }

                    }
                });
            }
        });


        edtImagePickerSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                String searchString = edtImagePickerSearch.getText().toString();
                int textlength = searchString.length();
                if (textlength > 0) {
                    ArrayList<ImageModel> tempArrayList = new ArrayList<ImageModel>();
                    tempArrayList.clear();
                    if (imageList != null && imageList.size() > 0) {   ///// checker added by sumon
                        for (ImageModel c : imageList) {
                            if (textlength <= c.getFileName().length()) {
                                if (c.getFileName().toLowerCase().contains(cs.toString().toLowerCase())) {
//                            if(searchString.equalsIgnoreCase(c.getModelName().substring(0,textlength))||searchString.equalsIgnoreCase(c.getDMSCode().substring(0,textlength))){
                                    tempArrayList.add(c);
                                }
                            }
                        }
                    }

                    gridView(tempArrayList);
                } else {
                    gridView(imageList);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ibtnBackImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                if (which.equals(StaticAccess.TAG_SELECT_SOUND)) {
                    returnIntent.putExtra(StaticAccess.TAG_SELECT_SOUND, "");
                    setResult(StaticAccess.MATERIAL_FILE_PICKER, returnIntent);
                    finish();
                } else
                    finish();

            }
        });
    }

    // Getting SD card images for ImagePicker
    public ArrayList<ImageModel> getImagePickerImages(String path) {

        imageList = new ArrayList<ImageModel>();
        File[] listFile;
        File file = new File(path);

//        if (file.isDirectory()) {
        if (file.listFiles() != null) {
            listFile = file.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                if (isAcceptedImage(listFile[i].getAbsolutePath())) {
                    ImageModel imageModel = new ImageModel();
                    imageModel.setFileName(listFile[i].getAbsolutePath().substring(listFile[i].getAbsolutePath().lastIndexOf('/') + 1));
                    imageModel.setFilePath(listFile[i].getAbsolutePath());

                    imageList.add(imageModel);
                }
            }


        }
//        }
        return imageList;
    }

    public boolean isAcceptedImage(String filename) {
        int idx = filename.lastIndexOf(".");
        if (idx > 0) {
            String ext = filename.substring(idx + 1).toLowerCase();
            return ext.equals("jpg")
                    || ext.equals("jpeg")
                    || ext.equals("png");
        } else {
            return false;
        }
    }

    public void gridView(final ArrayList<ImageModel> imageList) {
        if (imageList != null) {
            imagePickerGridAdapter = new ImagePickerGridAdapter(activity, imageList);

            gvImagePicker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent returnIntent = new Intent();
                    if (which.equals(StaticAccess.TAG_SELECT_SOUND)) {
                        returnIntent.putExtra(StaticAccess.TAG_SELECT_SOUND, imageList.get(position).getFilePath());
                        setResult(StaticAccess.MATERIAL_FILE_PICKER, returnIntent);
                        finish();
                    } else if (which.equals(StaticAccess.TAG_SELECT_PICTURE)) {
                        returnIntent.putExtra(StaticAccess.TAG_SELECT_PICTURE, imageList.get(position).getFilePath());
                        setResult(StaticAccess.SELECT_PICTURE, returnIntent);
                    } else if (which.equals(StaticAccess.TAG_SELECT_PICTURE_ERROR)) {
                        returnIntent.putExtra(StaticAccess.TAG_SELECT_PICTURE_ERROR, imageList.get(position).getFilePath());
                        setResult(StaticAccess.SELECT_PICTURE_ERROR, returnIntent);
                    } else if (which.equals(StaticAccess.TAG_SELECT_PICTURE_FEEDBACK)) {
                        returnIntent.putExtra(StaticAccess.TAG_SELECT_PICTURE_FEEDBACK, imageList.get(position).getFilePath());
                        setResult(StaticAccess.SELECT_PICTURE_FEEDBACK, returnIntent);
                    }
                    finish();

                }
            });


        }
        gvImagePicker.setAdapter(imagePickerGridAdapter);
        // rvImagePicker.setAdapter(imagePickerAdapter);

    }

    //************************************** Sound Picker ***********************************
    public ArrayList<ImageModel> getFilePickerSounds(String path) {

        imageList = new ArrayList<ImageModel>();
        File[] listFile;
        File file = new File(path);

//        if (file.isDirectory()) {
        listFile = file.listFiles();
        if (listFile != null)
            for (int i = 0; i < listFile.length; i++) {
                if (isAcceptedSound(listFile[i].getAbsolutePath())) {
                    ImageModel imageModel = new ImageModel();
                    imageModel.setFileName(listFile[i].getAbsolutePath().substring(listFile[i].getAbsolutePath().lastIndexOf('/') + 1));
                    imageModel.setFilePath(listFile[i].getAbsolutePath());
                    imageList.add(imageModel);
                }

            }

//        }
        return imageList;
    }

    private boolean isAcceptedSound(String absolutePath) {
        int idx = absolutePath.lastIndexOf(".");
        if (idx > 0) {
            String ext = absolutePath.substring(idx + 1).toLowerCase();
            return ext.equals("mp3")
                    || ext.equals("ogg")
                    || ext.equals("wav");
        } else {
            return false;
        }
    }


    @Override
    public void onBackPressed() {

        finish();

    }
}