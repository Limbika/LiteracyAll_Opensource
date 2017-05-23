package com.literacyall.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.literacyall.app.R;
import com.literacyall.app.activities.MultichoiceViewActivity;
import com.literacyall.app.dao.Task;
import com.literacyall.app.dao.TaskPack;
import com.literacyall.app.manager.DatabaseManager;
import com.literacyall.app.manager.IDatabaseManager;
import com.literacyall.app.utilities.ImageProcessing;

import java.util.ArrayList;

/**
 * Created by Golam Mahmud on 18-Oct-16.
 */

public class TaskPackAdapter extends MultiChoiceAdapter {

    Context context;
    ImageProcessing imageProcessing;
    public ArrayList<TaskPack> taskPacks = new ArrayList<>();
    LayoutInflater inflater;
    MultichoiceViewActivity activity;
    IDatabaseManager databaseManager;


    //this array list contains keys of selected items
    //public ArrayList<User> users = new ArrayList<>();
    //this variable holds the key of single item selected
    public long SingleModeKey = -1;

    public TaskPackAdapter(MultichoiceViewActivity activity, ArrayList<TaskPack> taskPacks) {
        super(activity, taskPacks);
        this.context = activity;
        this.activity = activity;
        this.taskPacks = taskPacks;
        imageProcessing = new ImageProcessing(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        if (tasks != null && tasks.size() > 0)
            imageProcessing.setImageWith_loaderRowItem(holder.ivProPic, tasks.get(0).getTaskImage());
        //holder.ivProPic.setImageResource(R.drawable.ic_task_pack);
        holder.tvUserName.setText(taskPacks.get(position).getName());
        holder.tvLevel.setText(taskPacks.get(position).getLevel());

        if (taskPacks.get(position).getState())
            holder.customLnForRow.setBackground(getSelectedDrawable(true));
        else
            holder.customLnForRow.setBackground(getSelectedDrawable(false));

        //holder.ivGridItem.setImageBitmap(imageProcessing.getImage(tasks.get(position).getTaskImage()));

        /*GradientDrawable[] shapes = getTheDesiredShape(position);
        GradientDrawable shape = shapes[0];
        GradientDrawable shapeText = shapes[1];*/


        /*holder.lnCellBg.setBackground(shape);
        holder.lnTxtBG.setBackground(shapeText);*/

        return convertView;
    }

    // created by sumon
    public Drawable getSelectedDrawable(boolean selector) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(7);
        if (selector)
            drawable.setStroke(2, ContextCompat.getColor(activity, R.color.greyDark));
        else drawable.setStroke(2, Color.TRANSPARENT);
        return drawable;
    }

    /////////////////////////CONTROL MECHANISM//////////////////////////////////
    //these Functions will call the Implemented Activity
    @Override
    void singleTapModeOn(long key) {
        activity.singleTapModeOn(key);
    }

    @Override
    void singleTapModeDone(long key) {
        activity.singleTapDone(key);
    }

    @Override
    void multiChoiceModeOn(ArrayList<TaskPack> userList, boolean mode) {
            activity.multiChoiceModeEnter(userList,mode);
    }

    @Override
    void singleTapModeOff() {
        activity.singleTapModeClear();
    }

    @Override
    void multiChoiceModeOff() {
        activity.multiChoiceClear();
    }

    @Override
    void notifyDatasetChangeCustom(ArrayList<TaskPack> taskPacks) {
        this.taskPacks = taskPacks;
        notifyDataSetChanged();
    }
}