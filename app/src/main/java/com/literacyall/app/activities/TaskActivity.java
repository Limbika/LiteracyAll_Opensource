package com.literacyall.app.activities;

// This is Task viewer activity. The tasks of a specific taskpack is shown in this activity as grid. User can edit/ share/ delete tasks
// from this activity.

//1.Task can be created
//2.Task can be shared
//3.Task can be deleted
//4.Task can be moved in another position.


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.literacyall.app.R;
import com.literacyall.app.adapter.TaskAdapter;
import com.literacyall.app.dao.Item;
import com.literacyall.app.dao.Task;
import com.literacyall.app.logging.L;
import com.literacyall.app.manager.DatabaseManager;
import com.literacyall.app.manager.IDatabaseManager;
import com.literacyall.app.share.Share;
import com.literacyall.app.utilities.ApplicationMode;
import com.literacyall.app.utilities.CustomToast;
import com.literacyall.app.utilities.ExceptionHandler;
import com.literacyall.app.utilities.FileProcessing;
import com.literacyall.app.utilities.ImageProcessing;
import com.literacyall.app.utilities.StaticAccess;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class TaskActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private IDatabaseManager databaseManager;
    TaskAdapter taskAdapter;
    public ArrayList<Task> tasks = new ArrayList<>();
    GridView gvTask;
    long taskPackId;
    public ArrayList<Task> selectedTasksForShare = new ArrayList<>();
    Share share;
    String selection;
    ArrayList<String> selections = new ArrayList<>();
    TaskActivity ref;
    String selectionMode = "";
    FloatingActionMenu fabMenu;
    FloatingActionButton fab;
    ActionMode multiSelectMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        databaseManager = new DatabaseManager(this);
        share = new Share(this);
        ref = this;

        gvTask = (GridView) findViewById(R.id.gvTask);
        gvTask.setOnItemClickListener(this);
        gvTask.setDrawSelectorOnTop(true);

        if (getIntent().getExtras() != null) {
            taskPackId = getIntent().getLongExtra(StaticAccess.INTENT_TASK_PACK_ID, -1);
            fillGridview();
        }

        gvTask.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gvTask.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.setTitle(getResources().getString(R.string.task));
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
                multiSelectMode = mode;
                final Task task = tasks.get(position);
                switch (gvTask.getCheckedItemCount()) {
                    case 1:
                        setMode();
                        break;
                    case 2:
                        if (selectionMode.equals("D")) {

                            // Moving task position
                            int droppingid = Integer.valueOf(selections.get(0));
                            int dropTargetid = position;

                            if (droppingid == dropTargetid) {
                                L.t(ref,getResources().getString(R.string.alreadyThisPosition) );
                                return;
                            }
                            Task t = tasks.get(droppingid);
                            if (droppingid > dropTargetid) {
                                droppingid++;
                            }
                            if (task != null) {
                                tasks.add(dropTargetid, t);
                                tasks.remove(droppingid);

                                databaseManager.deleteTasksByTaskPack(t.getTaskPackId());
                                for (int i = 0; i < tasks.size(); i++) {
                                    Task t2 = tasks.get(i);
                                    t2.setSlideSequence(i + 1);
                                    databaseManager.insertTask(t2);
                                }
                                fillGridview();
                                selectionMode = "";
                                selections.clear();
                                mode.finish();
                                return;
                            }
                        }
                        break;
                    default:
                }

                if (checked) {
                    selectedTasksForShare.add(task);
                    selections.add(String.valueOf(position));
                    mode.setSubtitle(getResources().getString(R.string.selectedTask) + TextUtils.join(", ", selections));
                } else {
                    if (selectedTasksForShare.contains(task)) {
                        selectedTasksForShare.remove(task);
                        selections.remove(String.valueOf(position));
                    }
                    mode.setSubtitle(getResources().getString(R.string.selectedTask) + TextUtils.join(", ", selections));
                }

                if (fabMenu.isOpen()) {
                    fabMenu.close(true);
                }
            }
        });

        setFab();
    }


    // Gridview on Item Click redirect to Player / Editor
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(TaskActivity.this, PlayerActivity.class);
        intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, taskPackId);
        intent.putExtra(StaticAccess.POSITION, position);
        startActivity(intent);
        finish();
    }

    // Filling gridview from Database
    private void fillGridview() {
        tasks = (ArrayList<Task>) databaseManager.listTasksByTAskPackId(taskPackId);
        if (tasks != null) {
            taskAdapter = new TaskAdapter(this, tasks);
            gvTask.setAdapter(taskAdapter);
        }
    }

    // Setting operation mode of selected task
    public void setMode() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_player_mode_selector);
        //prev. dialog.setCancelable(false) now its true by reaz
        dialog.setCancelable(true);
        ImageButton ibtnMove = (ImageButton) dialog.findViewById(R.id.ibtnMove);
        ImageButton ibtnEdit = (ImageButton) dialog.findViewById(R.id.ibtnEdit);
        ImageButton ibtnSingleItemDelete = (ImageButton) dialog.findViewById(R.id.ibtnSingleItemDelete);


        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (multiSelectMode != null && !selectionMode.equals("D")) {
                    multiSelectMode.finish();
                    selections.clear();
                }
            }
        });

        ibtnMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionMode = "D";
                dialog.dismiss();
                //CustomToast.t(ref, "Please, click now cross button from fab for delete");
            }
        });


        ibtnSingleItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Task task = tasks.get(Integer.valueOf(selections.get(0)));
                deleteTask(task.getId());
                fillGridview();
                selections.clear();
                CustomToast.t(ref,getResources().getString(R.string.taskDeleteMsg) );
                dialog.dismiss();

            }
        });

        ibtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionMode = StaticAccess.EDIT_MODE;
                Task task = tasks.get(Integer.valueOf(selections.get(0)));
                if (task != null) {
                    Intent intent = new Intent(TaskActivity.this, MainActivity.class);
                    intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, taskPackId);
                    intent.putExtra(StaticAccess.INTENT_TASK_ID, task.getId());
                    intent.putExtra(StaticAccess.INTENT_TASK_TYPE, task.getType());
                    startActivity(intent);
                    finish();
                }
            }

        });

        DialogNavBarHide.navBarHide(this, dialog);

    }



    // Setting up floating
    public void setFab() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.LayoutParams params = new com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.LayoutParams(80, 80);

        ImageView ivTaskAdd = new ImageView(this);
        ivTaskAdd.setImageResource(R.drawable.add);

        ImageView ivTaskBack = new ImageView(this);
        ivTaskBack.setImageResource(R.drawable.btn_back);

        ImageView ivTaskDelete = new ImageView(this);
        ivTaskDelete.setImageResource(R.drawable.ic_delete);

        SubActionButton sabTaskAdd = itemBuilder.setContentView(ivTaskAdd).build();
        sabTaskAdd.setLayoutParams(params);

        SubActionButton sabTaskBack = itemBuilder.setContentView(ivTaskBack).build();
        sabTaskBack.setLayoutParams(params);

        SubActionButton sabTaskDelete = itemBuilder.setContentView(ivTaskDelete).build();
        sabTaskDelete.setLayoutParams(params);

        sabTaskAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TaskActivity.this, MainActivity.class);
                intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, taskPackId);
                startActivity(intent);
                finish();
                fabMenu.close(true);
            }
        });


        sabTaskBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBackPermission();
                /*Intent intentBack = new Intent(TaskActivity.this, TaskPackOldActivity.class);
                startActivity(intentBack);
                fabMenu.close(true);
                finish();*/
            }
        });

        sabTaskDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTasksForShare.size() > 0) {
                    new AlertDialog.Builder(ref)
                            .setTitle(getResources().getString(R.string.deleteMsg))
                            .setMessage(getResources().getString(R.string.deleteAlertMsg))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    for (Task task : selectedTasksForShare) {
                                        deleteTask(task.getId());
                                    }
                                    fillGridview();
                                    selections.clear();
                                    selectedTasksForShare.clear();
                                    if (multiSelectMode != null)
                                        multiSelectMode.finish();
                                    CustomToast.t(ref, getResources().getString(R.string.taskDeleteMsg));

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    selections.clear();
                                    selectedTasksForShare.clear();
                                    if (multiSelectMode != null)
                                        multiSelectMode.finish();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    L.t(ref, getResources().getString(R.string.longTapDeleteMsg));
                }
                fabMenu.close(true);

            }
        });

        //attach the sub buttons
        fabMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(sabTaskDelete)
                .addSubActionView(sabTaskBack)
                .addSubActionView(sabTaskAdd)
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

    // dialog for BaclPermission created by Rokan
    private void dialogBackPermission() {
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
                fabMenu.close(true);
                dialog.dismiss();
            }
        });
        btnOkPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentBack = new Intent(TaskActivity.this, TaskPackOldActivity.class);
                startActivity(intentBack);
                fabMenu.close(true);
                finish();
                dialog.dismiss();
            }
        });
        DialogNavBarHide.navBarHide(this, dialog);

    }

    // Delete task
    public void deleteTask(long taskId) {
        Task task = databaseManager.getTaskById(taskId);
        ImageProcessing imageProcessing = new ImageProcessing(ref);
        imageProcessing.deleteImage(task.getTaskImage());
        imageProcessing.deleteImage(task.getFeedbackImage());
        imageProcessing.deleteImage(task.getErrorImage());

        FileProcessing fileProcessing = new FileProcessing(ref);
        fileProcessing.deleteSound(task.getFeedbackSound());
        fileProcessing.deleteSound(task.getPositiveSound());
        fileProcessing.deleteSound(task.getNegativeSound());

        LinkedHashMap<Long, Item> items = databaseManager.loadTaskWiseItem(task);
        for (Map.Entry<Long, Item> itemValue : items.entrySet()) {
            Item item = itemValue.getValue();
            imageProcessing.deleteImage(item.getImagePath());
            fileProcessing.deleteSound(item.getItemSound());
        }

        databaseManager.deleteTaskById(taskId);
        databaseManager.deleteItemsByTask(taskId);
    }
}
