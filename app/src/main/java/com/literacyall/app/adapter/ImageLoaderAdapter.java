package com.literacyall.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.literacyall.app.R;
import com.literacyall.app.dao.Task;
import com.literacyall.app.manager.IDatabaseManager;
import com.literacyall.app.utilities.ImageProcessing;

import java.util.ArrayList;

/**
 * Created by ibrar on 4/20/2016.
 */
public class ImageLoaderAdapter extends BaseAdapter {

    Context context;
    IDatabaseManager databaseManager;
    ArrayList<Task> tasks;
    LayoutInflater inflater;
    ImageProcessing imgProc;
    Task task;


    public ImageLoaderAdapter(Context context, ArrayList<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
        imgProc = new ImageProcessing(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return tasks.size();
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

        public ImageView ivGridItem;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.grid_image_view, null);
            holder.ivGridItem = (ImageView) convertView.findViewById(R.id.ivGridItem);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ivGridItem.setImageBitmap(imgProc.getImage(tasks.get(position).getTaskImage()));

        return convertView;


    }


}



