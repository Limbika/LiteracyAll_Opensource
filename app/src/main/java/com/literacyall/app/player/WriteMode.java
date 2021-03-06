package com.literacyall.app.player;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dmk.limbikasdk.pojo.LimbikaViewItemValue;
import com.dmk.limbikasdk.views.LimbikaView;
import com.literacyall.app.Dialog.DialogNavBarHide;
import com.literacyall.app.R;
import com.literacyall.app.activities.PlayerActivity;
import com.literacyall.app.activities.TaskPackActivity;
import com.literacyall.app.dao.Item;
import com.literacyall.app.dao.Task;
import com.literacyall.app.utilities.Animanation;
import com.literacyall.app.utilities.StaticAccess;
import com.literacyall.app.utilities.TaskType;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by RAFI on 11/20/2016.
 */

public class WriteMode {
    Context context;
    Task task;
    Item item;
    PlayerActivity activity;


    public  WriteMode(Context context, Task task, Item item) {
        this.context = context;
        this.task = task;
        this.item = item;
        activity = (PlayerActivity) context;

    }


    public void writeModePlay(Item item, Item item1, View v,
                            LimbikaViewItemValue limbikaViewItemValue){
        LimbikaView limbikaView = (LimbikaView) v;
        if (item1.getResult().length() > 0 && item1.getResult() != null) {
            if (!activity.isListening) {
                if (item1.getResult().equals(TaskType.NORMAL_TRUE) &&
                        ifInthePositiveListGenearal(item1.getKey())) {
                    String savedtext = item1.getWriteText();
                   dialogTextSet(item, item1, limbikaView, savedtext, limbikaViewItemValue);
                    activity.isListening = true;
                }
            }
        } else {
            playSoundOnlyGeneral(item.getItemSound(), "", "", task, item1);
        }


        if (!activity.isListening) {
            //DONOT LET THE USERS USE POSITIVE NEGATIVE AND THE BELOW FUNCTIONS TOGETHER THIS WILL CREATE MANICE
            //get showed by list and show them
            if (item1.getShowedByTarget() != null && !item1.getShowedByTarget().equals("")) {
                String str = item1.getShowedByTarget();
                ArrayList<Long> list = activity.formatList(str);
                activity.showItemsFromShowedby(list);
            }
            //get hidden by list and show them
            if (item1.getHiddenByTarget() != null && !item1.getHiddenByTarget().equals("")) {
                String str = item1.getHiddenByTarget();
                ArrayList<Long> list = activity.formatList(str);
                activity.hideItemsFromShowedby(list);
            }


            if (item.getCloseApp() == 1) {
                Intent intent = new Intent(activity, TaskPackActivity.class);
                intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, activity.taskPackId);
                activity.startActivity(intent);
                activity.mpSoundRelease();
                activity.finish();
            }
            if (item.getOpenUrl().length() > 0 && item.getOpenApp() != null) {
                limbikaView.setDraggable(false);
                activity.openWebLink(item.getOpenUrl());
            }
            // single tap open 3rd party app added by reaz
            if (item.getOpenApp().length() > 0 && item.getOpenApp() != null) {
                activity.mpSoundRelease();
                activity.openApp(item.getOpenApp());
            }
            if (item.getNavigateTo() > 0) {
                limbikaView.setDraggable(false);
                activity.positiveResult.clear();
                activity.gotoSpecificTask(item.getNavigateTo());
            }
            activity.isBlockUser = false;
        }

    }
    boolean ifInthePositiveListGenearal(long key) {
        boolean res = false;
        for (long k : activity.positiveResult) {
            if (k == key)
                res = true;
        }
        return res;
    }

    private void playSoundOnlyGeneral(final String itemsound, final String posSound, final String negSound, final Task task, final Item item) {
        //when item sound is available
        if (itemsound.length() > 0 && itemsound != null) {
            activity.mpSoundRelease();
            activity.mp = new MediaPlayer();
            try {
                activity.mp.setDataSource(Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + activity.getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_SOUND + itemsound);

                activity.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        activity.mpSoundRelease();
                        mp = new MediaPlayer();

                        String playString = "";

                        if (posSound != null && !posSound.equals(""))
                            playString = posSound;
                        else
                            playString = negSound;
                        //if itemsound is only available no pos or neg sound
                        if (playString.equals("") || playString == null) {

                            if (activity.positiveResult.size() == 0) {
                                if (task.getFeedbackImage().length() > 0) {
                                    activity.delayfeedBack(task.getFeedbackImage(), task.getFeedbackAnimation(), activity.currentTaskIndex, task.getFeedbackSound(), task.getFeedbackType());
                                } else {
                                    if (item.getResult().equals(TaskType.NORMAL_TRUE))
                                        if(item.getNavigateTo()<1){
                                            activity.delayGotoNextTask(activity.currentTaskIndex);
                                        }

                                }
                            }


                        } else
                            try {
                                //Positive or negative sound available after item sound
                                mp.setDataSource(Environment.getExternalStorageDirectory() +
                                        StaticAccess.ANDROID_DATA + activity.getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_SOUND + playString);
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        if (posSound.equals("") || posSound == null) {
                                            if (activity.positiveResult.size() == 0) {
                                                if (task.getFeedbackImage().length() > 0) {
                                                    activity.delayfeedBack(task.getFeedbackImage(), task.getFeedbackAnimation(), activity.currentTaskIndex, task.getFeedbackSound(), task.getFeedbackType());
                                                } else {
                                                    if (item.getResult().equals(TaskType.NORMAL_TRUE)){
                                                        if(item.getNavigateTo()<1){
                                                            activity.delayGotoNextTask(activity.currentTaskIndex);
                                                        }
                                                    }


                                                }
                                            }
                                        } else {
                                            //for negative
                                            //if mandatory is true then send it to uper error else do nothing
                                            if (activity.madatoryError == 1)
                                                activity.errorListner.onErrorListner(task, StaticAccess.TAG_TASK_GENERAL_MODE);
                                        }
                                    }
                                });
                                mp.prepare();

                             /*do not play negative sound if mandatory supererror is on.
                                 then the sound will be played on Super activity */
                                if (activity.madatoryError == 1 && negSound != null && !negSound.equals("")) {
                                    activity.errorListner.onErrorListner(task, StaticAccess.TAG_TASK_GENERAL_MODE);
                                } else
                                    mp.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                });

                activity.mp.prepare();
                activity.mp.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //no item sound and no Positive or negetive sound
        else if (itemsound.equals("") && posSound.equals("") && negSound.equals("")) {
            if (activity.positiveResult.size() == 0) {
                if (task.getFeedbackImage().length() > 0) {
                    activity.delayfeedBack(task.getFeedbackImage(), task.getFeedbackAnimation(), activity.currentTaskIndex, task.getFeedbackSound(), task.getFeedbackType());
                } else {
                    if (item.getResult().equals(TaskType.NORMAL_TRUE)){
                        if(item.getNavigateTo()<1){
                            activity.delayGotoNextTask(activity.currentTaskIndex);
                        }
                    }

                }
            }
        }
        //no item sound but positive or negative sound is available
        else {
            activity.mpSoundRelease();
            activity.mp = new MediaPlayer();

            String playString = "";

            if (posSound != null && !posSound.equals(""))
                playString = posSound;
            else
                playString = negSound;

            try {

                activity.mp.setDataSource(Environment.getExternalStorageDirectory() +
                        StaticAccess.ANDROID_DATA + activity.getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_SOUND + playString);
                activity.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (!posSound.equals("") || posSound != null) {
                            if (activity.positiveResult.size() == 0) {
                                if (task.getFeedbackImage().length() > 0) {
                                    activity.delayfeedBack(task.getFeedbackImage(), task.getFeedbackAnimation(), activity.currentTaskIndex, task.getFeedbackSound(), task.getFeedbackType());
                                } else {
                                    if (item.getResult().equals(TaskType.NORMAL_TRUE)){
                                        if(item.getNavigateTo()<1){
                                            activity.delayGotoNextTask(activity.currentTaskIndex);
                                        }
                                    }

                                }
                            }
                        } else {
                            //for negative
                            //if mandatory is true then send it to uper error else do nothing
                            /*if (madatoryError == 1)
                                errorListner.onErrorListner(task, StaticAccess.TAG_TASK_GENERAL_MODE);*/
                        }
                    }
                });
                activity.mp.prepare();
                /*do not play negative sound if mandatory supererror is on
                                 then the sound will be played on activity */
                if (activity.madatoryError == 1 && negSound != null && !negSound.equals("")) {
                    activity.errorListner.onErrorListner(task, StaticAccess.TAG_TASK_GENERAL_MODE);
                } else
                    activity.mp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public void dialogTextSet(final Item item, final Item item1, final LimbikaView vv,
                              final String expectedText, final LimbikaViewItemValue limbikaViewItemValue) {
        final Dialog dialog = new Dialog(context, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_text_set);
        dialog.setCancelable(false);

        final EditText edtTextSet = (EditText) dialog.findViewById(R.id.edtTextSet);
        //edtTextSet.setText(expectedText);
        Animanation.blink2FeedBackAnimation(edtTextSet);

        Button btnTextSetOk = (Button) dialog.findViewById(R.id.btnTextSetOk);
        Button btnTextSetCancel = (Button) dialog.findViewById(R.id.btnTextSetCancel);
        btnTextSetOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtTextSet.getText() != null) {
                  doAfterWriteText(edtTextSet.getText().toString(), item, item1, (LimbikaView) vv, expectedText, limbikaViewItemValue);
                }

                dialog.dismiss();

            }
        });

        btnTextSetCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.isListening = false;
            }
        });
        DialogNavBarHide.navBarHide(activity, dialog);

    }

    public void doAfterWriteText(String inputtext, Item item, Item item1, LimbikaView v, String savedtext,
                                 LimbikaViewItemValue limbikaViewItemValue) {
        // "T==T"
        // the second condition(&&) diminishs the posibity of playing a positive view twice
        LimbikaView limbikaView = (LimbikaView) v;
        if (inputtext.equals(savedtext)) {
            if (item1.getResult().equals(TaskType.NORMAL_TRUE) &&
                    ifInthePositiveListGenearal(item1.getKey())) {
                //task.getNegative sound no need this  playSoundOnlyGeneral Methods that's why we send Blank String
                //all the sound and feed back related works are done here so that everything happens simultaneously


                limbikaView.changeText(savedtext);
                activity.positiveResult.remove(limbikaViewItemValue.getKey());

                if (task.getPositiveAnimation() > 0) {
                    activity.showAnimation(limbikaView, task.getPositiveAnimation());
                }
                playSoundOnlyGeneral(item1.getItemSound(), task.getPositiveSound(), "", task, item1);


                //positive end////
                //get showed by list and show them
                if (item1.getShowedByTarget() != null && !item1.getShowedByTarget().equals("")) {
                    String str = item1.getShowedByTarget();
                    ArrayList<Long> list = activity.formatList(str);
                    activity.showItemsFromShowedby(list);
                }
                //get hidden by list and show them
                if (item1.getHiddenByTarget() != null && !item1.getHiddenByTarget().equals("")) {
                    String str = item1.getHiddenByTarget();
                    ArrayList<Long> list = activity.formatList(str);
                    activity.hideItemsFromShowedby(list);
                }

            } else if (item1.getResult().equals(TaskType.NORMAL_FALSE)) {
                if (task.getNegativeAnimation() > 0) {
                    activity.showAnimation(v, task.getNegativeAnimation());

                }
                playSoundOnlyGeneral(item.getItemSound(), "", task.getNegativeSound(), task, item1);
                //playNegSoundGeneral(task, task.getNegativeSound());

                //LimbikaView limbikaView = (LimbikaView) v;
                //limbikaView.showNegativeTag();

            } else {
                //task.getNegative sound no need this  playSoundOnlyGeneral Methods that's why we send Blank String
                //all the sound and feed back related works are done here so that everything happens simultaneously
                playSoundOnlyGeneral(item.getItemSound(), "", "", task, item1);
            }
        } else {
            if (task.getNegativeAnimation() > 0) {
                activity.showAnimation(limbikaView, task.getNegativeAnimation());
            }
            playSoundOnlyGeneral(item.getItemSound(), "", task.getNegativeSound(), task, item1);
        }
        //DONOT LET THE USERS USE POSITIVE NEGATIVE AND THE BELOW FUNCTIONS TOGETHER THIS WILL CREATE MANICE
        if (item.getCloseApp() == 1) {
            Intent intent = new Intent(activity, TaskPackActivity.class);
            intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, activity.taskPackId);
            activity.startActivity(intent);
            activity.mpSoundRelease();
            activity.finish();
        }
        if (item.getOpenUrl().length() > 0 && item.getOpenApp() != null) {
            limbikaView.setDraggable(false);
            activity.openWebLink(item.getOpenUrl());
        }
        // single tap open 3rd party app added by reaz
        if (item.getOpenApp().length() > 0 && item.getOpenApp() != null) {
            activity.mpSoundRelease();
            activity.openApp(item.getOpenApp());
        }
        if (item.getNavigateTo() > 0) {
            limbikaView.setDraggable(false);
            activity.positiveResult.clear();
            activity.gotoSpecificTask(item.getNavigateTo());
        }
        activity.isBlockUser = false;
        activity.isListening = false;
        activity.doneListening();
    }

}
