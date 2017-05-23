package com.literacyall.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.literacyall.app.R;

/**
 * Created by ibrar on 9/6/2016.
 */
public class SoundDelayAdapter extends BaseAdapter {

    Context context;
    public String delayTime[];
    LayoutInflater inflater;

    public SoundDelayAdapter(Context context, String delayTime[]) {
        this.context=context;
        this.delayTime=delayTime;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return delayTime.length;
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
        TextView tvDelaySound;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        SoundDelayAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new SoundDelayAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.delay_sound_item_cell, null);
            holder.tvDelaySound = (TextView) convertView.findViewById(R.id.tvDelaySound);

            convertView.setTag(holder);

        } else {
            holder = (SoundDelayAdapter.ViewHolder) convertView.getTag();
        }

        holder.tvDelaySound.setText(delayTime[position]);


        return convertView;
    }


}
