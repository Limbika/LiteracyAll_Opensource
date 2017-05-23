package com.literacyall.app.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.literacyall.app.R;
import com.literacyall.app.activities.MainActivity;
import com.literacyall.app.adapter.TaskAdapter;
import com.literacyall.app.dao.Task;

import java.util.ArrayList;

/**
 * Created by RAFI on 7/27/2016.
 */
public class TaskGridDialog extends Dialog {

    GridView gvTaskGrid;
    ArrayList<Task> tasks = new ArrayList<>();
    TaskAdapter taskAdapter;
    Context context;

    public TaskGridDialog(Context context, ArrayList<Task> tasks) {
        super(context);
        this.tasks = tasks;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_taskgrid);


        gvTaskGrid = (GridView) findViewById(R.id.gvTaskGrid);

        if (tasks != null) {
            taskAdapter = new TaskAdapter(context, tasks);
            gvTaskGrid.setAdapter(taskAdapter);
        }

        gvTaskGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (tasks != null) {
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.navigateTo(tasks.get(position));
                    dismiss();
                }

            }
        });

    }
}
