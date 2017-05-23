package com.literacyall.app.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.literacyall.app.R;
import com.literacyall.app.activities.TaskPackOldActivity;
import com.literacyall.app.dao.TaskPack;
import com.literacyall.app.manager.DatabaseManager;
import com.literacyall.app.manager.IDatabaseManager;
import com.literacyall.app.utilities.CustomToast;

import java.util.Date;

public class TaskPackDialog extends Dialog {

    Context context;
    TaskPackOldActivity taskPackOldActivity;
    EditText edtTaskPackName, edtTaskPackLebel, edtTaskPackId;
    Button btnDone;
    public TaskPack pack;
    private IDatabaseManager databaseManager;
    TaskPackDialog dialog;
    String level;
    int levelTaskPackID=0;

    public TaskPackDialog(){
        super(null);
    }

    public TaskPackDialog(Context context, TaskPackOldActivity taskPackOldActivity) {
        super(context, R.style.CustomAlertDialog);
        this.context = context;
        this.taskPackOldActivity = taskPackOldActivity;
        dialog = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_pack_dialog);

        pack = new TaskPack();
        databaseManager = new DatabaseManager(context);

        //    EditText edtTaskPackName, edtTaskPackLebel,TaskPackId;

        edtTaskPackName = (EditText) findViewById(R.id.edtTaskPackName);
        edtTaskPackLebel = (EditText) findViewById(R.id.edtTaskPackLebel);
        edtTaskPackId = (EditText) findViewById(R.id.TaskPackId);

        btnDone = (Button) findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTaskPackName.getText().toString().length() == 0) {
                    CustomToast.t(taskPackOldActivity, context.getResources().getString(R.string.addText));
                } else {
                    //level = checkBlank(edtTaskPackLebel.getText().toString());
                    levelTaskPackID = checkBlank(edtTaskPackId.getText().toString());
                    pack.setName(edtTaskPackName.getText().toString());
                    pack.setCreatedAt(new Date());
                    pack.setLevel(level);
                    pack.setFirstLayerTaskID(levelTaskPackID);
                    pack.setState(false);
                    databaseManager.insertTaskPack(pack);
                    edtTaskPackName.setText("");
                   // level=0;
                    levelTaskPackID=0;
                    taskPackOldActivity.fillGridview();
                    dialog.dismiss();
                }
            }
        });


    }
    public int checkBlank(String str) {
        int intValue = 0;
        if (str.length() > 0) {
            intValue = Integer.parseInt(str);
            return intValue;
        } else {
            return intValue;
        }
    }
    }
