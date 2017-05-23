package com.literacyall.app.activities;

// This activity contains taskpacks. Taskpack can be created/edited/deleted from this activity.

//1. User can create a taskpack
//2. User can delete a taskpack


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.literacyall.app.Dialog.DialogNavBarHide;
import com.literacyall.app.Dialog.TaskPackDialog;
import com.literacyall.app.R;
import com.literacyall.app.adapter.TaskPackOldAdapter;
import com.literacyall.app.dao.Item;
import com.literacyall.app.dao.Task;
import com.literacyall.app.dao.TaskPack;
import com.literacyall.app.manager.DatabaseManager;
import com.literacyall.app.manager.IDatabaseManager;
import com.literacyall.app.share.Share;
import com.literacyall.app.utilities.ApplicationMode;
import com.literacyall.app.utilities.ExceptionHandler;
import com.literacyall.app.utilities.FileProcessing;
import com.literacyall.app.utilities.ImageProcessing;
import com.literacyall.app.utilities.StaticAccess;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class TaskPackOldActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    TaskPackOldActivity activity;
    private IDatabaseManager databaseManager;
    TaskPackOldAdapter taskPackOldAdapter;
    public ArrayList<TaskPack> taskPacks = new ArrayList<>();
    GridView gvTaskPack;
    ImageButton ibtnBackTaskPack;
    ArrayList<TaskPack> selectedForShare = new ArrayList<>();
    private ProgressDialog pDialog;
    Share share;
    ArrayList<String> selections = new ArrayList<>();
    ActionMode actionMode;
    FloatingActionMenu fabMenu;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_pack_old);

        share = new Share(this);
        activity = this;
        databaseManager = new DatabaseManager(this);
        ibtnBackTaskPack = (ImageButton) findViewById(R.id.ibtnBackTaskPack);
        gvTaskPack = (GridView) findViewById(R.id.gvTaskPack);
        gvTaskPack.setOnItemClickListener(this);
        gvTaskPack.setDrawSelectorOnTop(true);
        ibtnBackTaskPack.setOnClickListener(this);

        if(!ApplicationMode.devMode){
            ibtnBackTaskPack.setVisibility(View.INVISIBLE);
        }

        gvTaskPack.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gvTaskPack.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.setTitle(getResources().getString(R.string.sharingTaskPack));
                mode.setSubtitle(getResources().getString(R.string.oneTaskPackSelected));
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                System.out.println("onDestroy");
            }

            @Override
            public void onItemCheckedStateChanged(final ActionMode mode, final int position, long id, final boolean checked) {
                actionMode = mode;
                if (checked) {
                    selectedForShare.add(taskPacks.get(position));
                    selections.add(String.valueOf(position));
                    mode.setSubtitle(getResources().getString(R.string.selectedTask) + TextUtils.join(", ", selections));
                } else {
                    if (selections.contains(String.valueOf(position))) {
                        selectedForShare.remove(taskPacks.get(position));
                        selections.remove(String.valueOf(position));
                        mode.setSubtitle(getResources().getString(R.string.selectedTask) + TextUtils.join(", ", selections));
                    }
                }
                if (fabMenu.isOpen()) {
                    fabMenu.close(true);
                }
            }

        });
        fillGridview();
        setFab();
    }

    // Filling data in gridview from Database
    public void fillGridview() {
        taskPacks = (ArrayList<TaskPack>) databaseManager.listTaskPacks();
        if (taskPacks != null) {
            taskPackOldAdapter = new TaskPackOldAdapter(this, taskPacks);
            gvTaskPack.setAdapter(taskPackOldAdapter);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (taskPacks != null) {
            TaskPack taskPack = taskPacks.get(position);
            Intent intent = new Intent(TaskPackOldActivity.this, TaskActivity.class);
            intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, taskPack.getId());
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtnBackTaskPack:
                dialogBackPermission();
                break;
        }
    }


    public void setFab() {

        fab = (FloatingActionButton) findViewById(R.id.fab);
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.LayoutParams params = new com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.LayoutParams(80, 80);

        ImageView ivTaskPackAdd = new ImageView(this);
        ivTaskPackAdd.setImageResource(R.drawable.add);

        ImageView ivTaskPackShare = new ImageView(this);
        ivTaskPackShare.setImageResource(R.drawable.share_icon);

        ImageView ivTaskPackDelete = new ImageView(this);
        ivTaskPackDelete.setImageResource(R.drawable.ic_delete);

        SubActionButton sabTaskPackAdd = itemBuilder.setContentView(ivTaskPackAdd).build();
        sabTaskPackAdd.setLayoutParams(params);

        SubActionButton sabTaskPackShare = itemBuilder.setContentView(ivTaskPackShare).build();
        sabTaskPackShare.setLayoutParams(params);

        SubActionButton sabTaskPackDelete = itemBuilder.setContentView(ivTaskPackDelete).build();
        sabTaskPackDelete.setLayoutParams(params);

        sabTaskPackAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskPackDialog dialog = new TaskPackDialog(activity, activity);
                DialogNavBarHide.navBarHide(activity, dialog);
                fabMenu.close(true);
            }
        });


        sabTaskPackShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (selectedForShare.size() > 0) {
                    try {
                        share.generateTaskPackJSON(selectedForShare);
                        share.shareFiles(share.zipDir());
                        share.deleteGeneratedFolder();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (actionMode != null) {
                    actionMode.finish();
                    selections.clear();
                    selectedForShare.clear();
                }*/
                new gesonGenerating().execute();
                fabMenu.close(true);
            }
        });

        sabTaskPackDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedForShare != null) {
                    for (TaskPack taskPack : selectedForShare) {
                        ImageProcessing imageProcessing = new ImageProcessing(activity);
                        FileProcessing fileProcessing = new FileProcessing(activity);
                        ArrayList<Task> tasks = (ArrayList<Task>) databaseManager.listTasksByTAskPackId(taskPack.getId());
                        if (tasks != null) {
                            for (Task task : tasks) {
                                LinkedHashMap<Long, Item> items = databaseManager.loadTaskWiseItem(task);
                                if (items != null) {
                                    for (Map.Entry<Long, Item> itemValue : items.entrySet()) {
                                        Item item = itemValue.getValue();
                                        imageProcessing.deleteImage(item.getImagePath());
                                        fileProcessing.deleteSound(item.getItemSound());
                                        databaseManager.deleteItemById(item.getId());
                                    }
                                }
                                imageProcessing.deleteImage(task.getTaskImage());
                                imageProcessing.deleteImage(task.getFeedbackImage());
                                imageProcessing.deleteImage(task.getErrorImage());
                                fileProcessing.deleteSound(task.getFeedbackSound());
                                fileProcessing.deleteSound(task.getPositiveSound());
                                fileProcessing.deleteSound(task.getNegativeSound());
                                databaseManager.deleteTaskById(task.getId());
                            }
                        }
                        databaseManager.deleteTaskPackById(taskPack.getId());
                    }
                }
                if (actionMode != null) {
                    actionMode.finish();
                    selections.clear();
                    selectedForShare.clear();
                }
                fillGridview();
                fabMenu.close(true);
            }
        });


        //attach the sub buttons
        fabMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(sabTaskPackDelete)
                .addSubActionView(sabTaskPackShare)
                .addSubActionView(sabTaskPackAdd)
                .attachTo(fab)
                .build();


        fabMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fab.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fab, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fab.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fab, pvhR);
                animation.start();
            }
        });


    }

    // dialog for BackPermission created by Rokan
    public void dialogBackPermission() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_back_permission);
        dialog.setCancelable(false);

        final TextView tvBackPermission = (TextView) dialog.findViewById(R.id.tvBackPermission);
        tvBackPermission.setText(getResources().getText(R.string.BackPermission));

        Button btnCancelPermission = (Button) dialog.findViewById(R.id.btnCancelPermission);
        Button btnOkPermission = (Button) dialog.findViewById(R.id.btnOkPermission);

        btnCancelPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOkPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, LauncherActivity.class);
               // intent.putExtra("taskPackId", taskPackId);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
        DialogNavBarHide.navBarHide(this, dialog);
    }

    class gesonGenerating extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(getResources().getString(R.string.sharePreparing));
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            if (selectedForShare.size() > 0) {
                try {
                    share.generateTaskPackJSON(selectedForShare);
                    share.shareFiles(share.zipDir());
                    share.deleteGeneratedFolder();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (actionMode != null) {
                //actionMode.finish();
                selections.clear();
                selectedForShare.clear();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
        }

    }


}
