package com.literacyall.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.literacyall.app.R;
import com.literacyall.app.activities.ImagePickerActivity;
import com.literacyall.app.listener.RecyclerViewClickListener;
import com.literacyall.app.pojo.ImageModel;
import com.literacyall.app.utilities.ImageProcessing;
import com.literacyall.app.utilities.StaticAccess;

import java.util.ArrayList;

/**
 * Created by ibrar on 11/17/2016.
 */

public class ImagePickerGridAdapter extends BaseAdapter {

    private ArrayList<ImageModel> imageList;
    Context context;
    ImageProcessing imageProcessing;
    LayoutInflater inflater;
    ImagePickerActivity activity;

    public ImagePickerGridAdapter(Context context, ArrayList<ImageModel> imageList) {
        this.context = context;
        this.imageList = imageList;
        imageProcessing = new ImageProcessing(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        activity = (ImagePickerActivity) context;

    }


    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        public ImageView ivImagePickerItem;
        public TextView tvImagePickerItem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ImagePickerGridAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.image_picker_row, null);
            holder.ivImagePickerItem = (ImageView) convertView.findViewById(R.id.ivImagePickerItem);

            holder.tvImagePickerItem = (TextView) convertView.findViewById(R.id.tvImagePickerItem);
            holder.tvImagePickerItem.setText(imageList.get(position).getFileName());
            if (activity.which.equals(StaticAccess.TAG_SELECT_SOUND)) {
                holder.ivImagePickerItem.setImageResource(R.drawable.ic_new_sound);
            }else {
                imageProcessing.setImageWith_loaderFullPath(holder.ivImagePickerItem, imageList.get(position).getFilePath());
            }

            convertView.setTag(holder);

        } else {
            holder = (ImagePickerGridAdapter.ViewHolder) convertView.getTag();
        }

        return convertView;
    }
}
