package com.literacyall.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.literacyall.app.R;

/**
 * Created by RAFI on 8/24/2016.
 */

public class FontAdapter  extends BaseAdapter {

    Context context;
    public String fontName[];
    LayoutInflater inflater;
    public FontAdapter(Context context, String fontName[]) {
        this.context=context;
        this.fontName=fontName;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return fontName.length;
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
        TextView tvFont;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        FontAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new FontAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.font_item_cell, null);
            holder.tvFont = (TextView) convertView.findViewById(R.id.tvFontName);

            convertView.setTag(holder);

        } else {
            holder = (FontAdapter.ViewHolder) convertView.getTag();
        }

        holder.tvFont.setText(fontName[position]);


        return convertView;
    }

}
