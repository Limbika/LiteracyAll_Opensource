package com.literacyall.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.literacyall.app.R;
import com.literacyall.app.utilities.ImageProcessing;

import java.util.ArrayList;

/**
 * Created by ibrar on 7/21/2016.
 */
public class GifAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater = null;
    ImageProcessing imageProcessing;

    public String serial[];
    public String animationName[];
    ArrayList<Integer> lstgifImg;

    public GifAdapter(Context context, String serial[], String animationName[], ArrayList<Integer> lstgifImg) {
        this.context = context;
        this.serial = serial;
        this.animationName = animationName;
        this.lstgifImg = lstgifImg;
        imageProcessing = new ImageProcessing(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return serial.length;
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
        TextView tvNumber, tvAnim;
        ImageView ivGif;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.gif_item_cell, null);
            holder.tvNumber = (TextView) convertView.findViewById(R.id.tvNumber);
            holder.tvAnim = (TextView) convertView.findViewById(R.id.tvAnim);
            holder.ivGif = (ImageView) convertView.findViewById(R.id.ivGif);
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvNumber.setText(serial[position]);
        holder.tvAnim.setText(animationName[position]);
        holder.ivGif.setImageResource(lstgifImg.get(position));

        return convertView;
    }
}
