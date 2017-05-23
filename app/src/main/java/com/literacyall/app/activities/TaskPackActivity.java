package com.literacyall.app.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.limbika.material.dialog.FileDialog;
import com.limbika.material.dialog.SelectorDialog;
import com.literacyall.app.Dialog.DialogNavBarHide;
import com.literacyall.app.R;
import com.literacyall.app.adapter.TaskPackAdapter;
import com.literacyall.app.adapter.TaskPackOldAdapter;
import com.literacyall.app.customui.CircularCartView;
import com.literacyall.app.dao.Item;
import com.literacyall.app.dao.Task;
import com.literacyall.app.dao.TaskPack;
import com.literacyall.app.interfaces.MultichoiceAdapterInterface;
import com.literacyall.app.manager.DatabaseManager;
import com.literacyall.app.manager.IDatabaseManager;
import com.literacyall.app.share.Share;
import com.literacyall.app.utilities.CustomToast;
import com.literacyall.app.utilities.FileProcessing;
import com.literacyall.app.utilities.ImageProcessing;
import com.literacyall.app.utilities.StaticAccess;
import com.literacyall.app.utilities.TaskType;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class TaskPackActivity extends MultichoiceViewActivity {

    MultichoiceAdapterInterface.ControlMethods multiChoice_listener;
    long singelTappedTaskPack = -1;
    GridView gvTaskPackCase;
    private IDatabaseManager databaseManager;
    public ArrayList<TaskPack> taskPacks = new ArrayList<>();
    TaskPackAdapter taskPackAdapter;
    TaskPackOldAdapter taskPackAdapter_Dialog;
    TaskPackActivity activity;
    public Vibrator vibe;
    ArrayList<TaskPack> taskPacksTemp = new ArrayList<>();
    boolean multiChoiceMode = false;
    TaskPack currentSelectedPack = null;
    private static final int MATERIAL_FILE_PICKER = 0x1;
    EditText edtSearchTaskPack;
    ProgressDialog pDialog;
    String selectedPackType;
    CircularCartView ctvCart;
    String appImagePath, appSoundPath;
    ImageProcessing imageProcessing;
    FileProcessing fileProcessing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        databaseManager = new DatabaseManager(this);
        edtSearchTaskPack = (EditText) findViewById(R.id.edtSearchTaskPack);
        ctvCart = (CircularCartView) findViewById(R.id.ctvCart);
        gvTaskPackCase = (GridView) findViewById(R.id.gvTaskPackCase);
        loadAllTaskPacks();
        searchTaskPack();
        selectedPackType = TaskType.All;
        btnAll.setTextColor(ContextCompat.getColor(activity, R.color.black));
        btnAll.performClick();
        vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        appImagePath = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_IMAGE;
        appSoundPath = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_SOUND;
        hideKeyboard();
        imageProcessing = new ImageProcessing(activity);
        fileProcessing = new FileProcessing(activity);


    }

    private void loadAllTaskPacks() {
        taskPacks = (ArrayList<TaskPack>) databaseManager.listTaskPacks();
        if (taskPacks != null) {
            taskPackAdapter = new TaskPackAdapter(this, taskPacks);
            gvTaskPackCase.setAdapter(taskPackAdapter);
            currentSelectedPack = null;
        }
    }

    //********************************** CONTROL LOGIC *******************************************//
    //called from multichoice Adapter
    @Override
    public void setMultichoiceListener(MultichoiceAdapterInterface.ControlMethods listener) {
        this.multiChoice_listener = listener;
    }

    @Override
    public void singleTapModeOn(long key) {
        singelTappedTaskPack = key;
    }

    @Override
    public void singleTapDone(long key) {
        /*Intent intent = new Intent(activity, TaskPackActivity.class);
        intent.putExtra("key", key);
        startActivity(intent);
        finish();*/
        //Toast.makeText(this,String.valueOf(key),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void singleTapModeClear() {
        singelTappedTaskPack = -1;
    }


    @Override
    public void multiChoiceModeEnter(ArrayList<TaskPack> taskPacks, boolean mode) {
        //do not call listener from here
        singelTappedTaskPack = -1;
        taskPacksTemp = taskPacks;
        multiChoiceMode = mode;
        ctvCart.setText(String.valueOf(taskPacksTemp.size()));
    }

    @Override
    public void multiChoiceClear() {
        singelTappedTaskPack = -1;
        taskPacksTemp.clear();
        multiChoiceMode = false;
        currentSelectedPack = null;
        ctvCart.setText(String.valueOf(taskPacksTemp.size()));
        // multiChoice_listener.clearMultichoiceMode();
    }

    @Override
    public void rendomOutSideClicked(View v) {
        singelTappedTaskPack = -1;
        taskPacksTemp.clear();
        multiChoiceMode = false;
        if (taskPacks.size() > 0 && taskPacks != null) {
            multiChoice_listener.clearMultichoiceMode();
            multiChoice_listener.clearSingleChoiceMode();
        }
        hideKeyboard();
        ctvCart.setText(String.valueOf(taskPacksTemp.size()));
    }

    @Override
    public void hideSoftKeyBoard() {
        hideKeyboard();
    }

    @Override
    public void eraseButtonClicked(View v) {
        if (taskPacksTemp.size() > 0) {
            dialogEliminatePermission();
        } else {
            CustomToast.t(activity, getResources().getString(R.string.PackNotSelectedForDelete));
        }
    }


    @Override
    public void cartButtonClicked(View v) {

    }

    @Override
    public void shareButtonClicked(View v) {
        dialogShare();
    }

    @Override
    public void packEditButtonClicked(View v) {
        if (currentSelectedPack == null) {
            CustomToast.t(activity, getResources().getString(R.string.selectForEdit));
            return;
        }
        dialogTaskPack();
    }

    @Override
    public void searchButtonClicked(View v) {
       /* if (selectedPackType.equals(TaskType.All)) {
            taskPacks = (ArrayList<TaskPack>) databaseManager.getTaskPacksByName(edtSearchTaskPack.getText().toString());
        } else {
            taskPacks = (ArrayList<TaskPack>) databaseManager.getTaskPacksByName(edtSearchTaskPack.getText().toString(), selectedPackType);
        }
        if (taskPacks != null) {
            taskPackAdapter = new TaskPackAdapter(this, taskPacks);
            gvTaskPackCase.setAdapter(taskPackAdapter);
            currentSelectedPack = null;
        }*/
        CustomToast.t(activity, getResources().getString(R.string.Nothinghere));
    }

    @Override
    public void exportButtonClicked(View v) {


        FileDialog fileDialog = new FileDialog(activity, FileDialog.Strategy.FILE);
        fileDialog.setCancelable(false);
        fileDialog.show();
        fileDialog.setOnSelectListener(new SelectorDialog.OnSelectListener() {
            @Override
            public void onSelect(String filePath) {
                if (filePath != null && filePath.endsWith(StaticAccess.DOT_EXTENSION)) {
                    new JsonReading(filePath).execute();
                } else
                    CustomToast.t(activity, getResources().getString(R.string.notSupportedFile));
                Toast.makeText(activity, filePath, Toast.LENGTH_LONG).show();
            }
        });


       /* new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(MATERIAL_FILE_PICKER)
                .withFilter(Pattern.compile(".*\\.lit$")) // Filtering files and directories by file name using regexp
                .withFilterDirectories(false) // Set directories filterable (false by default)
                .withHiddenFiles(true) // Show hidden files and folders
                .start();*/
    }

    @Override
    public void newPackButtonClicked(View v) {
        /*if (currentSelectedPack == null) {
            CustomToast.t(activity, getResources().getString(R.string.SelectPack));
            return;
        }
        dialogEntryLevel();*/
        dialogTaskPack();
    }

    @Override
    public void editButtonClicked(View v) {
        if (currentSelectedPack == null) {
            CustomToast.t(activity, getResources().getString(R.string.SelectPack));
            return;
        }
        /*ArrayList<Task> tasks = (ArrayList<Task>) databaseManager.listTasksByTAskPackId(currentSelectedPack.getId());
        if (tasks.size() == 0) {
            CustomToast.t(activity, getResources().getString(R.string.EmptyPack));
            return;
        }*/

        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, currentSelectedPack.getId());
        intent.putExtra(StaticAccess.INTENT_TASK_TYPE, currentSelectedPack.getType());
        startActivity(intent);
        finish();
    }

    @Override
    public void playButtonClicked(View v) {

        if (currentSelectedPack == null) {
            CustomToast.t(activity, getResources().getString(R.string.SelectPack));
            return;
        }
        /*ArrayList<Task> tasks = (ArrayList<Task>) databaseManager.listTasksByTAskPackId(currentSelectedPack.getId());
        if (tasks.size() == 0) {
            CustomToast.t(activity, getResources().getString(R.string.EmptyPack));
            return;
        }*/
        ArrayList<Task> tasks = (ArrayList<Task>) databaseManager.listTasksByTAskPackId(currentSelectedPack.getId());
        if (currentSelectedPack != null && tasks != null && tasks.size() > 0) {
            Intent intent = new Intent(activity, PlayerActivity.class);
            intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, currentSelectedPack.getId());
            intent.putExtra(StaticAccess.POSITION, 0);
            startActivity(intent);
            finish();
        } else {
            CustomToast.t(activity, getResources().getString(R.string.anyTask));
        }

    }

    @Override
    public void allButtonClicked(View v) {
        loadAllTaskPacks();
        selectedPackType = TaskType.All;
        btnAll.setTextColor(ContextCompat.getColor(activity, R.color.black));
        btnAnswer.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnClassificate.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnMatch.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnRead.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnWrite.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnSequence.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
    }

    @Override
    public void answerButtonClicked(View v) {
        selectedPackType = TaskType.Normal;
        loadTaskPackByType(TaskType.Normal);
        btnAll.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnAnswer.setTextColor(ContextCompat.getColor(activity, R.color.black));
        btnClassificate.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnMatch.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnRead.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnWrite.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnSequence.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
    }

    @Override
    public void classificateButtonClicked(View v) {
        selectedPackType = TaskType.DragDrop;
        loadTaskPackByType(TaskType.DragDrop);
        btnAll.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnAnswer.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnClassificate.setTextColor(ContextCompat.getColor(activity, R.color.black));
        btnMatch.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnRead.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnWrite.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnSequence.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
    }

    @Override
    public void matchButtonClicked(View v) {
        selectedPackType = TaskType.Assistive;
        loadTaskPackByType(TaskType.Assistive);
        btnAll.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnAnswer.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnClassificate.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnMatch.setTextColor(ContextCompat.getColor(activity, R.color.black));
        btnRead.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnWrite.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnSequence.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
    }

    @Override
    public void readButtonClicked(View v) {
        selectedPackType = TaskType.Read;
        loadTaskPackByType(TaskType.Read);
        btnAll.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnAnswer.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnClassificate.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnMatch.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnRead.setTextColor(ContextCompat.getColor(activity, R.color.black));
        btnSequence.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnWrite.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
    }

    @Override
    public void writeButtonClicked(View v) {
        selectedPackType = TaskType.Write;
        loadTaskPackByType(TaskType.Write);
        btnAll.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnAnswer.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnClassificate.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnMatch.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnRead.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnSequence.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnWrite.setTextColor(ContextCompat.getColor(activity, R.color.black));

    }

    @Override
    public void sequenceButtonClicked(View v) {

        selectedPackType = TaskType.Sequence;
        loadTaskPackByType(TaskType.Sequence);
        btnAll.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnAnswer.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnClassificate.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnMatch.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnRead.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnWrite.setTextColor(ContextCompat.getColor(activity, R.color.greyDark));
        btnSequence.setTextColor(ContextCompat.getColor(activity, R.color.black));

    }

    @Override
    public void backButtonClicked(View v) {
        Intent intent = new Intent(activity, LauncherActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void cloneButtonClicked(View v) {
        if (currentSelectedPack == null) {
            return;
        }
        clonePack(currentSelectedPack);
        loadSelectedTab();
    }

    @Override
    public void onItemClickForGridView(AdapterView<?> parent, View view, int position, long id) {
        if (multiChoice_listener != null) {
            if (multiChoiceMode) {
                taskPacksTemp.add(taskPacks.get(position));
                multiChoice_listener.setSingleModeKey(taskPacks.get(position));
            } else {
                taskPacksTemp.clear();
                taskPacksTemp.add(taskPacks.get(position));
                multiChoice_listener.setSingleModeKey(taskPacks.get(position));
            }
            currentSelectedPack = taskPacks.get(position);
            ctvCart.setText(String.valueOf(taskPacksTemp.size()));
        }
    }

    @Override
    public void onItemLongClickForGridView(AdapterView<?> parent, View view, int position, long id) {
        if (multiChoice_listener != null) {
            if (taskPacks != null) {

                //this call will automatically Direct to multiChoiceModeEnter() int his activity
                //if you press on single tap and then press some other grid
                // long click then the single tap will be deselected
                if (!multiChoiceMode)
                    multiChoice_listener.clearSingleChoiceMode();

                multiChoice_listener.addMultichoiseModeKey(taskPacks.get(position));
                vibe.vibrate(100);
                multiChoiceMode = true;
                currentSelectedPack = taskPacks.get(position);
            }
        }
    }

    private void dialogEliminatePermission() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_eliminate_permission);

        final TextView tvPermission = (TextView) dialog.findViewById(R.id.tvPermission);
        ImageButton btnCancelPermission = (ImageButton) dialog.findViewById(R.id.btnCancelPermission);
        ImageButton btnOkPermission = (ImageButton) dialog.findViewById(R.id.btnOkPermission);

        btnCancelPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOkPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (TaskPack taskPack : taskPacksTemp) {
                    deleteTaskPack(taskPack);
                }
                dialog.dismiss();
                loadSelectedTab();

            }
        });
        DialogNavBarHide.navBarHide(this, dialog);
    }


    private void dialogEntryLevel() {
        Intent in = new Intent(this, TransparrentSlideTypeActivity.class);
        in.putExtra(TransparrentSlideTypeActivity.ChooserTransparentDialog, 2);
        startActivityForResult(in, StaticAccess.TAG_TRANSPARENT_DIALOG_ACTIVITY);
    }


    private void dialogShare() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_share);
        dialog.setCancelable(false);

        GridView gvShare = (GridView) dialog.findViewById(R.id.gvShare);

       /* if (taskPacksTemp != null) {
            taskPackAdapter_Dialog = new TaskPackAdapter(this, taskPacksTemp);
            gvShare.setAdapter(taskPackAdapter_Dialog);
        }*/
        if (taskPacksTemp != null) {
            taskPackAdapter_Dialog = new TaskPackOldAdapter(this, taskPacksTemp);
            gvShare.setAdapter(taskPackAdapter_Dialog);
        }

        ImageButton ibtnEliminate = (ImageButton) dialog.findViewById(R.id.ibtnEliminate);
        ImageButton btnCancelPermission = (ImageButton) dialog.findViewById(R.id.btnCancelPermission);
        ImageButton btnOkPermission = (ImageButton) dialog.findViewById(R.id.btnOkPermission);

        ibtnEliminate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                taskPacksTemp.clear();
            }
        });

        btnCancelPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        btnOkPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskPacksTemp.size() == 0) {
                    CustomToast.t(activity, getResources().getString(R.string.PackNotSelected));
                    return;
                }
                Share share = new Share(activity);
                try {
                    share.generateTaskPackJSON(taskPacksTemp);
                    share.shareFiles(share.zipDir());
                    share.deleteGeneratedFolder();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        DialogNavBarHide.navBarHide(this, dialog);
    }


    private void loadTaskPackByType(String type) {
        taskPacks = (ArrayList<TaskPack>) databaseManager.getTaskPacksByType(type);
        if (taskPacks != null) {
            taskPackAdapter = new TaskPackAdapter(this, taskPacks);
            gvTaskPackCase.setAdapter(taskPackAdapter);
            currentSelectedPack = null;
        }
    }

    public void startMainActivity(String taskType, Context ctx) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, currentSelectedPack.getId());
        intent.putExtra(StaticAccess.INTENT_TASK_TYPE, taskType);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == StaticAccess.TAG_TRANSPARENT_DIALOG_ACTIVITY) {
            if (resultCode != -1)
                switch (resultCode) {
                    case 0:
                        startMainActivity(TaskType.Assistive, TaskPackActivity.this);
                        break;
                    case 1:
                        startMainActivity(TaskType.Normal, TaskPackActivity.this);
                    case 2:
                        startMainActivity(TaskType.Write, TaskPackActivity.this);
                        break;
                    case 3:
                        startMainActivity(TaskType.Read, TaskPackActivity.this);
                        break;
                    case 4:
                        startMainActivity(TaskType.DragDrop, TaskPackActivity.this);
                        break;
                    case 5:
                        startMainActivity(TaskType.Sequence, TaskPackActivity.this);
                        break;

                }
        }

        if (resultCode == RESULT_OK) {

            if (requestCode == MATERIAL_FILE_PICKER) {
                String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                new JsonReading(filePath).execute();
            }
        }
    }

    /**
     * Managing Task Pack Dialog
     */
    public void dialogTaskPack() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_edit);
        dialog.setCancelable(false);

        final EditText edtName, edtLevel, edtSort;
        final TextView tvName, tvLevel, tvSort;

        final Spinner spLevel = (Spinner) dialog.findViewById(R.id.spLevel);
        final Spinner spSort = (Spinner) dialog.findViewById(R.id.spSort);
        tvSort = (TextView) dialog.findViewById(R.id.tvSortType);

        String[] LEVEL = getResources().getStringArray(R.array.level);
        String[] TASKPACK_TYPE = getResources().getStringArray(R.array.taskPackType);

//        ArrayAdapter<String> adapterLevel = new ArrayAdapter<String>(activity, R.layout.spinner_item, StaticAccess.LEVEL);//reaz string
        ArrayAdapter<String> adapterLevel = new ArrayAdapter<String>(activity, R.layout.spinner_item, LEVEL);
//        ArrayAdapter<String> adapterSort = new ArrayAdapter<String>(activity, R.layout.spinner_item, StaticAccess.TASKPACK_TYPE);//reaz string
        ArrayAdapter<String> adapterSort = new ArrayAdapter<String>(activity, R.layout.spinner_item, TASKPACK_TYPE);
        spLevel.setAdapter(adapterLevel);
        spSort.setAdapter(adapterSort);
        edtName = (EditText) dialog.findViewById(R.id.edtName);

        ImageButton btnCancelPermission = (ImageButton) dialog.findViewById(R.id.btnCancelPermission);
        ImageButton ibtnSaveTaskPack = (ImageButton) dialog.findViewById(R.id.ibtnSaveTaskPack);
        ImageButton ibtnExit = (ImageButton) dialog.findViewById(R.id.ibtnExit);

        if (currentSelectedPack != null) {
            edtName.setText(currentSelectedPack.getName());
            spLevel.setSelection(Arrays.asList(LEVEL).indexOf(currentSelectedPack.getLevel()));
            spSort.setSelection(Arrays.asList(StaticAccess.TASKPACK_TYPE_SHORT).indexOf(currentSelectedPack.getType()));
        } else {
            tvSort.setText(R.string.type);
        }

        btnCancelPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });


        ibtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TaskPackActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ibtnSaveTaskPack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSelectedPack == null) {
                    currentSelectedPack = new TaskPack();
                    currentSelectedPack.setName(edtName.getText().toString());
                    currentSelectedPack.setCreatedAt(new Date());
                    currentSelectedPack.setLevel(spLevel.getSelectedItem().toString());
                    currentSelectedPack.setType(StaticAccess.TASKPACK_TYPE_SHORT[spSort.getSelectedItemPosition()]);
                    currentSelectedPack.setState(false);
                    databaseManager.insertTaskPack(currentSelectedPack);

                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, currentSelectedPack.getId());
                    intent.putExtra(StaticAccess.INTENT_TASK_TYPE, StaticAccess.TASKPACK_TYPE_SHORT[spSort.getSelectedItemPosition()]);
                    startActivity(intent);
                    finish();
                } else {
                    currentSelectedPack.setName(edtName.getText().toString());
                    currentSelectedPack.setCreatedAt(new Date());
                    currentSelectedPack.setLevel(spLevel.getSelectedItem().toString());
                    currentSelectedPack.setType(StaticAccess.TASKPACK_TYPE_SHORT[spSort.getSelectedItemPosition()]);
                    currentSelectedPack.setState(false);
                    databaseManager.updateTaskPack(currentSelectedPack);
                    dialog.dismiss();
                    loadSelectedTab();
                }

            }
        });
        DialogNavBarHide.navBarHide(this, dialog);

    }

    /**
     * Importing Shared TaskPacks
     */
    class JsonReading extends AsyncTask<String, String, String> {
        String filePath;
        Share share;

        public JsonReading(String filePath) {
            this.filePath = filePath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            share = new Share(activity);
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(getResources().getString(R.string.jsonImport));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                share.unZip(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            share.readSharedTaskPackJSONtoDatabase();
            share.deleteReceivedFolder();

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
                loadAllTaskPacks();
            }
        }

    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtSearchTaskPack.getWindowToken(), 0);
    }

    /**
     * Deleting a TaskPack including all resource files
     */
    private void deleteTaskPack(TaskPack taskPack) {
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

                //Cleaning Related files
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

    //Load Current selected Tab
    private void loadSelectedTab() {
        if (selectedPackType.equals(TaskType.All)) {
            loadAllTaskPacks();
        } else {
            loadTaskPackByType(selectedPackType);
        }
        ctvCart.setText(String.valueOf(taskPacksTemp.size()));
    }

    private void clonePack(TaskPack taskPack) {
        Share share = new Share(activity);
        TaskPack oldTaskPack = cloneTaskPack(taskPack);
        taskPack.setId(null);
        taskPack.setState(false);
        Long taskPackId = databaseManager.insertTaskPack(taskPack);


        ArrayList<Task> tasks = (ArrayList<Task>) databaseManager.listTasksByTAskPackId(oldTaskPack.getId());
        for (Task task : tasks) {
            Task oldTask = cloneTask(task);
            // Copying Task Image
            /*share.copyForShare(task.getTaskImage(), appImagePath, appImagePath);
            share.copyForShare(task.getFeedbackImage(), appImagePath, appImagePath);
            share.copyForShare(task.getErrorImage(), appImagePath, appImagePath);
            // Copying Task Sound
            share.copyForShare(task.getFeedbackSound(), appSoundPath, appSoundPath);
            share.copyForShare(task.getPositiveSound(), appSoundPath, appSoundPath);
            share.copyForShare(task.getNegativeSound(), appSoundPath, appSoundPath);*/

            task.setId(null);
            task.setTaskPackId(taskPackId);

            if (task.getTaskImage().length() > 0)
                task.setTaskImage(imageProcessing.imageSave(imageProcessing.getImage(task.getTaskImage())));
            if (task.getFeedbackImage().length() > 0)
                task.setFeedbackImage(imageProcessing.imageSave(imageProcessing.getImage(task.getFeedbackImage())));
            if (task.getErrorImage().length() > 0)
                task.setErrorImage(imageProcessing.imageSave(imageProcessing.getImage(task.getErrorImage())));

            if (task.getFeedbackSound().length() > 0)
                task.setFeedbackSound(fileProcessing.createSoundFile(fileProcessing.getAbsolutepath_Of_Sound(task.getFeedbackSound())));
            if (task.getPositiveSound().length() > 0)
                task.setPositiveSound(fileProcessing.createSoundFile(fileProcessing.getAbsolutepath_Of_Sound(task.getPositiveSound())));
            if (task.getNegativeSound().length() > 0)
                task.setNegativeSound(fileProcessing.createSoundFile(fileProcessing.getAbsolutepath_Of_Sound(task.getNegativeSound())));
            Long taskId = databaseManager.insertTask(task);

            LinkedHashMap<Long, Item> items = databaseManager.loadTaskWiseItem(oldTask);
            if (items != null) {
                for (Map.Entry<Long, Item> itemValue : items.entrySet()) {
                    Item item = itemValue.getValue();


                    item.setId(null);
                    item.setTask(taskId);
                    //item.setKey(System.currentTimeMillis());
                    // Copying Item Images
                    //share.copyForShare(item.getImagePath(), appImagePath, appImagePath);
                    if (item.getImagePath().length() > 0)
                        item.setImagePath(imageProcessing.imageSave(imageProcessing.getImage(item.getImagePath())));
                    // Copying Item Sound
                    //share.copyForShare(item.getItemSound(), appSoundPath, appSoundPath);
                    if (item.getItemSound().length() > 0)
                        item.setItemSound(fileProcessing.createSoundFile(fileProcessing.getAbsolutepath_Of_Sound(item.getItemSound())));
                    databaseManager.insertItem(item);
                }
            }
        }
    }


    private static TaskPack cloneTaskPack(TaskPack obj) {
        try {
            TaskPack clone = obj.getClass().newInstance();
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                field.set(clone, field.get(obj));
            }
            return clone;
        } catch (Exception e) {
            return null;
        }
    }


    private static Task cloneTask(Task obj) {
        try {
            Task clone = obj.getClass().newInstance();
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                field.set(clone, field.get(obj));
            }
            return clone;
        } catch (Exception e) {
            return null;
        }
    }

    public void searchTaskPack() {
        edtSearchTaskPack.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (selectedPackType.equals(TaskType.All)) {
                    taskPacks = (ArrayList<TaskPack>) databaseManager.getTaskPacksByName(edtSearchTaskPack.getText().toString());
                } else {
                    taskPacks = (ArrayList<TaskPack>) databaseManager.getTaskPacksByName(edtSearchTaskPack.getText().toString(), selectedPackType);
                }
                if (taskPacks != null) {
                    taskPackAdapter = new TaskPackAdapter(activity, taskPacks);
                    gvTaskPackCase.setAdapter(taskPackAdapter);
                    currentSelectedPack = null;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


}

