package com.literacyall.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.literacyall.app.R;
import com.literacyall.app.dao.Task;
import com.literacyall.app.dao.TaskPack;
import com.literacyall.app.manager.DatabaseManager;
import com.literacyall.app.manager.IDatabaseManager;
import com.literacyall.app.utilities.ImageProcessing;

import java.util.ArrayList;

/**
 * TaskPackOldAdapter
 * Created by ibrar on 6/22/2016.
 */
public class TaskPackOldAdapter extends BaseAdapter {

    Context context;
    ImageProcessing imageProcessing;
    ArrayList<TaskPack> taskPacks;
    LayoutInflater inflater;
    private IDatabaseManager databaseManager;



    public TaskPackOldAdapter(Context context, ArrayList<TaskPack> taskPack){
        this.context = context;
        this.taskPacks = taskPack;
        imageProcessing = new ImageProcessing(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        databaseManager = new DatabaseManager(context);
    }


    @Override
    public int getCount() {
        return taskPacks.size();
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
        public ImageView ivProPic;
        public TextView tvUserName, tvLevel;
        public LinearLayout lnCellBg, lnTxtBG, customLnForRow;

    }
    // created by sumon
    public Drawable getSelectedDrawable(boolean selector) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(7);
        if (selector)
            drawable.setStroke(2, ContextCompat.getColor(context, R.color.greyDark));
        else drawable.setStroke(2, Color.TRANSPARENT);
        return drawable;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.cell_task_pack, null);
            holder.ivProPic = (ImageView) convertView.findViewById(R.id.ivProPic);
            holder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            holder.tvLevel = (TextView) convertView.findViewById(R.id.tvLevel);
            holder.lnCellBg = (LinearLayout) convertView.findViewById(R.id.lnCellBg);
            holder.lnTxtBG = (LinearLayout) convertView.findViewById(R.id.lnTxtBG);
            holder.customLnForRow = (LinearLayout) convertView.findViewById(R.id.customLnForRow);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        databaseManager = new DatabaseManager(context);
        ArrayList<Task> tasks = (ArrayList<Task>) databaseManager.listTasksByTAskPackId(taskPacks.get(position).getId());
        if(tasks != null && tasks.size() > 0)
            imageProcessing.setImageWith_loaderRound(holder.ivProPic, tasks.get(0).getTaskImage());
        //holder.ivProPic.setImageResource(R.drawable.ic_task_pack);
        holder.tvUserName.setText(taskPacks.get(position).getName());
        holder.tvLevel.setText(taskPacks.get(position).getLevel());

        if (taskPacks.get(position).getState())
            holder.customLnForRow.setBackground(getSelectedDrawable(true));
        else
            holder.customLnForRow.setBackground(getSelectedDrawable(false));

        return convertView;
    }
}
