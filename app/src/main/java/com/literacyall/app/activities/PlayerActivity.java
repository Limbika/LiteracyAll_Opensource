package com.literacyall.app.activities;

// This is the player fragment. All kind of task can be played through this activity.

//1. Items of a specific task generate as "Limbikaview" from Database
//2.

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dmk.limbikasdk.pojo.LimbikaViewItemValue;
import com.dmk.limbikasdk.views.DrawAssistiveLine;
import com.dmk.limbikasdk.views.LimbikaView;
import com.literacyall.app.Dialog.DialogNavBarHide;
import com.literacyall.app.R;
import com.literacyall.app.adapter.SoundDelayAdapter;
import com.literacyall.app.customui.PanelMapping;
import com.literacyall.app.dao.Item;
import com.literacyall.app.dao.Task;
import com.literacyall.app.dao.TaskPack;
import com.literacyall.app.listener.ErrorInterface;
import com.literacyall.app.manager.DatabaseManager;
import com.literacyall.app.manager.IDatabaseManager;
import com.literacyall.app.player.DragAndDropMode;
import com.literacyall.app.player.GeneralMode;
import com.literacyall.app.player.ReadMode;
import com.literacyall.app.player.WriteMode;
import com.literacyall.app.utilities.Animanation;
import com.literacyall.app.utilities.ApplicationMode;
import com.literacyall.app.utilities.CustomCircleStrokeAnimation;
import com.literacyall.app.utilities.CustomToast;
import com.literacyall.app.utilities.CustomVariousColorCircleAnimationView;
import com.literacyall.app.utilities.CustomVariousColorCircleStrokeAnimationView;
import com.literacyall.app.utilities.ImageProcessing;
import com.literacyall.app.utilities.Limbika_mesurement;
import com.literacyall.app.utilities.OnTouchCustomView;
import com.literacyall.app.utilities.StaticAccess;
import com.literacyall.app.utilities.TaskType;
import com.literacyall.app.utilities.TypeFace_MY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import de.hdodenhof.circleimageview.CircleImageView;


// This is the player fragment. All kind of task can be played through this activity.

//1. Items of a specific task generate as "Limbikaview" from Database
//2.
// 3. currentTaskIndex value -1 means its a Editor mode for preview
// 4.getPreview,getTaskID value -1 means its a Play Screen Mode For Play


public class PlayerActivity extends BaseActivity implements View.OnClickListener, PanelMapping.GETClickValue, ErrorInterface.ErrorListner {


    private float cx, cy;
    private RelativeLayout rlPlayer;
    private OnTouchCustomView touchCustomView;

    private IDatabaseManager databaseManager;

    private String LOG_TAG = "VoiceRecognition";

    private SpeechRecognizer speech = null;

    //this is the upper layer which is only used in assistive touch
    RelativeLayout assistiveRelLayout;
    FrameLayout flPlay;
    LimbikaView limbikaView;
    public ImageButton ibtnBackPlay, ibtnMicroPhone;
    PlayerActivity activity;
    public LinkedHashMap<Long, LimbikaView> dropTargetsMap = new LinkedHashMap<>();
    public LinkedHashMap<Long, LimbikaView> dropItemsMap = new LinkedHashMap<>();

    LinkedHashMap<Long, LimbikaView> NoItemsMap = new LinkedHashMap<>();

    //both are used in assistive and drag & drop
    HashMap<Long, ArrayList<LimbikaView>> targets_with_droapItems_map = new HashMap<>();
    public ArrayList<Long> alreadyCorrectDragandDrop = new ArrayList<>();

    //all the dropable Items are here
    LinkedHashMap<Long, Item> itemLinkedHashMap = new LinkedHashMap<>();
    // Holds view all items
    LinkedHashMap<Long, LimbikaView> itemViewHolderMap = new LinkedHashMap<>();

    //in assistive and drag and drop only the DROP ITEMS are in this list
    public ArrayList<Long> positiveResult = new ArrayList<>();
    ArrayList<Long> negativeResult = new ArrayList<>();

    ArrayList<Task> tasks = new ArrayList<>();
    //this one holds all the the  limbika views of a specific task whick is used to clear animation and gets reset
    //in every task
    HashMap<Long, LimbikaView> allTheViewsToClearAnimation = new HashMap<Long, LimbikaView>();
    ImageProcessing imageProcessing;
    public long taskPackId;
    //ArrayList<LimbikaView> showedByList = new ArrayList<>();
    LinkedHashMap<Long, Item> showedByList = new LinkedHashMap<>();

    public MediaPlayer mp;

    //this collection contains all the limbika views (using this one to send target list to each limbikaview )
    HashMap<Long, LimbikaView> alltheViews = new HashMap<>();
    //this collection contains only  drop targets in Drag and drop
    ArrayList<LimbikaView> allthetargets = new ArrayList<>();
    PanelMapping mapping;
    public static int screenheight, screenwidth;
    private double density;
    public int currentTaskIndex;
    Task task;
    public LinkedHashMap<Long, Item> items;
    public boolean isBlockUser = false;
    boolean isInnerBlock = false;
    int soundTypeCount = 0;
    ErrorInterface errorInterface;
    public ErrorInterface.ErrorListner errorListner;

    public boolean error_mode_on_dragDrop = false;
    long assistiveTargetKey = -1;

    int dragDropcopunt = 0;
    //0=nothing ,1= move to error screen
    public int madatoryError = 0;
    TaskPack taskPack;
    int getPreview;
    Long getTaskID;
    DragAndDropMode dragAndDropMode;
    ReadMode readMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        rlPlayer = (RelativeLayout) findViewById(R.id.rlPlayer);
        assistiveRelLayout = (RelativeLayout) findViewById(R.id.parentRelLayout);
        flPlay = (FrameLayout) findViewById(R.id.flPlay);

        calculateDisplaySize();
        task = new Task();
        mapping = new PanelMapping(PlayerActivity.this, screenheight, screenwidth, this);
        assistiveRelLayout.addView(mapping);

        ibtnMicroPhone = (ImageButton) findViewById(R.id.ibtnMicroPhone);
        ibtnMicroPhone.setOnClickListener(PlayerActivity.this);

        ibtnBackPlay = (ImageButton) findViewById(R.id.ibtnBackPlay);
        ibtnBackPlay.setOnClickListener(this);
        activity = this;
        databaseManager = new DatabaseManager(activity);
        imageProcessing = new ImageProcessing(activity);
        errorInterface = new ErrorInterface(this);
        errorListner = errorInterface.getListener();
        if (getIntent().getExtras() != null) {
            taskPackId = getIntent().getLongExtra(StaticAccess.INTENT_TASK_PACK_ID, -1);
            currentTaskIndex = getIntent().getIntExtra(StaticAccess.POSITION, -1);

            getTaskID = getIntent().getLongExtra(StaticAccess.INTENT_TASK_ID, -1);
            getPreview = getIntent().getIntExtra(StaticAccess.INTENT_TASK_PREVIEW, -1);

            taskPack = databaseManager.getTaskPackById(taskPackId);
            tasks = (ArrayList<Task>) databaseManager.listTasksByTAskPackId(taskPackId);
            if (currentTaskIndex != -1) {
                loadTask(currentTaskIndex);
            } else {
                loadTaskPreview(getTaskID);
            }

        }

        if (!ApplicationMode.devMode) {
            if (currentTaskIndex == -1) {
                ibtnBackPlay.setImageResource(R.drawable.ic_eye);
                ibtnBackPlay.setVisibility(View.VISIBLE);
            } else {
                ibtnBackPlay.setVisibility(View.INVISIBLE);
            }
        } else {
            if (currentTaskIndex == -1) {
                ibtnBackPlay.setImageResource(R.drawable.ic_eye);
            }
            ibtnBackPlay.setVisibility(View.VISIBLE);
        }


    }

    //Load Items according to task
    public void loadTask(final int currentTaskIndex) {
        //Global object thats why need to clear item task
        this.currentTaskIndex = currentTaskIndex;
        task = tasks.get(currentTaskIndex);

        clearSequence();

        //mir // FIXME: 31/08/2016 clearing all the hasmaps with each task
        assistiveRelLayout.removeAllViews();
        if (mapping != null)
            assistiveRelLayout.addView(mapping);
        dropTargetsMap.clear();
        dropItemsMap.clear();
        itemLinkedHashMap.clear();
        NoItemsMap.clear();
        itemViewHolderMap.clear();
        negativeResult.clear();
        error_mode_on_dragDrop = false;
        assistiveTargetKey = -1;
        alltheViews.clear();

        targets_with_droapItems_map.clear();
        alreadyCorrectDragandDrop.clear();//to calculate already correct dropitems

        //clearing assistive touch collection
        measurement_list.clear();
        limbikaview_list.clear();
        items = new LinkedHashMap<>();

        madatoryError = task.getErrorMandatoryScreen();
        loadTaskValue(task);

    }

    void playSoundOnlyGeneral(final String itemsound, final String posSound, final String negSound, final Task task, final Item item) {
        //when item sound is available
        if (itemsound.length() > 0 && itemsound != null) {
            mpSoundRelease();
            mp = new MediaPlayer();
            try {
                mp.setDataSource(Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + activity.getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_SOUND + itemsound);

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mpSoundRelease();
                        mp = new MediaPlayer();

                        String playString = "";

                        if (posSound != null && !posSound.equals(""))
                            playString = posSound;
                        else
                            playString = negSound;
                        //if itemsound is only available no pos or neg sound
                        if (playString.equals("") || playString == null) {

                            if (positiveResult.size() == 0) {
                                if (task.getFeedbackImage().length() > 0) {
                                    delayfeedBack(task.getFeedbackImage(), task.getFeedbackAnimation(), currentTaskIndex, task.getFeedbackSound(), task.getFeedbackType());
                                } else {
                                    if (item.getResult().equals(TaskType.NORMAL_TRUE))
                                        delayGotoNextTask(currentTaskIndex);
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
                                            if (positiveResult.size() == 0) {
                                                if (task.getFeedbackImage().length() > 0) {
                                                    delayfeedBack(task.getFeedbackImage(), task.getFeedbackAnimation(), currentTaskIndex, task.getFeedbackSound(), task.getFeedbackType());
                                                } else {
                                                    if (item.getResult().equals(TaskType.NORMAL_TRUE))
                                                        delayGotoNextTask(currentTaskIndex);
                                                }
                                            }
                                        } else {
                                            //for negative
                                            //if mandatory is true then send it to uper error else do nothing
                                            if (madatoryError == 1)
                                                errorListner.onErrorListner(task, StaticAccess.TAG_TASK_GENERAL_MODE);
                                        }
                                    }
                                });
                                mp.prepare();

                             /*do not play negative sound if mandatory supererror is on.
                                 then the sound will be played on Super activity */
                                if (madatoryError == 1 && negSound != null && !negSound.equals("")) {
                                    errorListner.onErrorListner(task, StaticAccess.TAG_TASK_GENERAL_MODE);
                                } else
                                    mp.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                });

                mp.prepare();
                mp.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //no item sound and no Positive or negetive sound
        else if (itemsound.equals("") && posSound.equals("") && negSound.equals("")) {
            if (positiveResult.size() == 0) {
                if (task.getFeedbackImage().length() > 0) {
                    delayfeedBack(task.getFeedbackImage(), task.getFeedbackAnimation(), currentTaskIndex, task.getFeedbackSound(), task.getFeedbackType());
                } else {
                    if (item.getResult().equals(TaskType.NORMAL_TRUE))
                        delayGotoNextTask(currentTaskIndex);
                }
            }
        }
        //no item sound but positive or negative sound is available
        else {
            mpSoundRelease();
            mp = new MediaPlayer();

            String playString = "";

            if (posSound != null && !posSound.equals(""))
                playString = posSound;
            else
                playString = negSound;

            try {

                mp.setDataSource(Environment.getExternalStorageDirectory() +
                        StaticAccess.ANDROID_DATA + activity.getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_SOUND + playString);
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (!posSound.equals("") || posSound != null) {
                            if (positiveResult.size() == 0) {
                                if (task.getFeedbackImage().length() > 0) {
                                    delayfeedBack(task.getFeedbackImage(), task.getFeedbackAnimation(), currentTaskIndex, task.getFeedbackSound(), task.getFeedbackType());
                                } else {
                                    if (item.getResult().equals(TaskType.NORMAL_TRUE))
                                        delayGotoNextTask(currentTaskIndex);
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
                mp.prepare();
                /*do not play negative sound if mandatory supererror is on
                                 then the sound will be played on activity */
                if (madatoryError == 1 && negSound != null && !negSound.equals("")) {
                    errorListner.onErrorListner(task, StaticAccess.TAG_TASK_GENERAL_MODE);
                } else
                    mp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public void playSoundDragAndAssistive(final Task task, String sound) {
//  isBlockUser is true because single tap need to block  and after complete the sound isBlockUser is false
        if (!sound.equals("")) {
            try {
                isBlockUser = true;
                mpSoundRelease();
                mp = new MediaPlayer();
                mp.setLooping(false);
                mp.setDataSource(Environment.getExternalStorageDirectory() +
                        StaticAccess.ANDROID_DATA + activity.getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_SOUND + sound);
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        isBlockUser = false;
                        if (positiveResult.size() == 0) {
                            if (task.getFeedbackImage().length() > 0) {
                                delayfeedBack(task.getFeedbackImage(), task.getFeedbackAnimation(), currentTaskIndex, task.getFeedbackSound(), task.getFeedbackType());
                            } else {
                                delayGotoNextTask(currentTaskIndex);
                            }
                        }

                    }
                });
                mp.prepare();
                mp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            isBlockUser = false;
            if (positiveResult.size() == 0) {
                if (task.getFeedbackImage().length() > 0) {
                    delayfeedBack(task.getFeedbackImage(), task.getFeedbackAnimation(), currentTaskIndex, task.getFeedbackSound(), task.getFeedbackType());
                } else {
                    delayGotoNextTask(currentTaskIndex);
                }
            }
        }

    }

    //Setting Drop Target
    private void setDropTarget() {
        // Setting Targets to the Items
        for (Map.Entry<Long, LimbikaView> itemValue : dropItemsMap.entrySet()) {
            Item item = itemLinkedHashMap.get(itemValue.getKey());
            LimbikaView dropItem = itemValue.getValue();
            if (item != null)
                if (dropTargetsMap.containsKey(item.getDragDropTarget())) {
                    LimbikaView targetView = dropTargetsMap.get(item.getDragDropTarget());
                    itemValue.getValue().setDropTarget(targetView);
                    itemValue.getValue().setTargetKey(targetView.getLimbikaViewItemValue().getKey());

                    //each drop target now has its assosiative child views in targets_with_droapItems_map
                    ArrayList<LimbikaView> savedViews = null;
                    if (targets_with_droapItems_map.get(item.getDragDropTarget()) != null)
                        savedViews = (ArrayList<LimbikaView>)
                                targets_with_droapItems_map.get(item.getDragDropTarget()).clone();
                    if (savedViews == null) {
                        savedViews = new ArrayList<>();
                        savedViews.add(dropItem);
                    } else {
                        savedViews.add(dropItem);
                    }
                    targets_with_droapItems_map.put(item.getDragDropTarget(), savedViews);
                }
        }
    }

    //set Drop target map this saves all the drop targets to each limbika view
    private void setDropTargetMap() {
        // Setting Targets to the Items
        for (Map.Entry<Long, LimbikaView> itemValue : dropItemsMap.entrySet()) {
            itemValue.getValue().setDropTargetsMap(dropTargetsMap);
        }
    }

    // Move to next Task when current one is completed
    private void gotoNextTask(int currentTaskIndex) {
        currentTaskIndex++;
        mpSoundRelease();
        if (getPreview == -1 && getTaskID == -1) {
            if (tasks.size() == currentTaskIndex) {
                Intent intent = new Intent(PlayerActivity.this, TaskPackActivity.class);
                intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, taskPackId);
                startActivity(intent);
                finish();
            } else {
                //if animation is there its time to clear it
                for (Map.Entry<Long, LimbikaView> entry : allTheViewsToClearAnimation.entrySet()) {
                    long key = entry.getKey();
                    LimbikaView v = entry.getValue();
                    if (v != null)
                        v.clearAnimation();
                }

                allTheViewsToClearAnimation.clear();
                flPlay.removeAllViews();
                loadTask(currentTaskIndex);
            }
        } else {
            returnEditor();
        }

    }

    // Move to specific task
    public void gotoSpecificTask(long unidId) {
        mpSoundRelease();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getUniqId() == unidId) {
                flPlay.removeAllViews();
                loadTask(i);
            }
        }
    }

    // Displaying Feedback image in dialog
    private void displayRectangularFeedback(String imagePath, int animation, final int currentTaskIndex, String soundClip) {

        final Dialog dialog = new Dialog(activity, R.style.CustomAlertDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_feedback_player);

        RelativeLayout llFeedbackDialog = (RelativeLayout) dialog.findViewById(R.id.llFeedbackDialog);
        ImageView ivFeedback = (ImageView) dialog.findViewById(R.id.ivFeedback);
        ivFeedback.setImageBitmap(imageProcessing.getImage(imagePath));
        playSound(soundClip);
        ivFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpSoundRelease();
                dialog.dismiss();
                delayGotoNextTask(currentTaskIndex);


            }
        });

        DialogNavBarHide.navBarHide(this, dialog);


        // Setting animation
        showFeedBackAnimation(llFeedbackDialog, animation);
    }

    //Opening an external application
    public void openApp(String packageName) {

        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (LaunchIntent != null)
            startActivity(LaunchIntent);
    }

    //Show Animation
    public void showAnimation(View view, int type) {
        switch (type) {
            case Animanation.shake1:
                Animanation.shakeAnimation(view);
                break;
            case Animanation.shake2:
                Animanation.shakeAnimation2(view);
                break;
            case Animanation.blink1:
                Animanation.blink(view);
                break;
            case Animanation.slideDownUp:
                Animanation.bottomToTop(view);
                break;
            case Animanation.slideUpDown:
                Animanation.topToBottom(view);
                break;
        }
    }

    //created by Reaz
    public void openWebLink(String Url) {

        //limbikaView need to Check This null
        if (Url != null || Url.length() != 0) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
            startActivity(browserIntent);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtnBackPlay:
                dialogBackPermission();
                break;
            case R.id.ibtnMicroPhone:
                break;

        }

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
                if (getPreview == 1) {
                    returnEditor();
                } else {
                    Intent intent = new Intent(activity, TaskPackActivity.class);
                    intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, taskPackId);
                    startActivity(intent);
                    finish();
                }

                dialog.dismiss();
            }
        });
        DialogNavBarHide.navBarHide(this, dialog);


    }

    // set Font Type add by reaz
    public void setFontType(int fontType, LimbikaView limbikaView) {

        switch (fontType) {
            case StaticAccess.TAG_TYPE_NORMAL:
                limbikaView.setCustomTypeFace(TypeFace_MY.getRoboto(activity));
                break;

            case StaticAccess.TAG_TYPE_FACE_SEGOE_SCRIPT:
                limbikaView.setCustomTypeFace(TypeFace_MY.getSegoeScript(activity));
                break;
            case StaticAccess.TAG_TYPE_FACE_DANCING:
                limbikaView.setCustomTypeFace(TypeFace_MY.getDancing(activity));
                break;
            case StaticAccess.TAG_TYPE_FACE_ROBOTO_THIN:

                limbikaView.setCustomTypeFace(TypeFace_MY.getRobotoThin(activity));
                break;
            case StaticAccess.TAG_TYPE_FACE_ROAD_BRUSH:

                limbikaView.setCustomTypeFace(TypeFace_MY.getRoadBrush(activity));
                break;
            case StaticAccess.TAG_TYPE_FACE_ROBOTO_CONDENSED:

                limbikaView.setCustomTypeFace(TypeFace_MY.getRoboto_condensed(activity));
                break;

            case StaticAccess.TAG_TYPE_FACE_RANCHO:

                limbikaView.setCustomTypeFace(TypeFace_MY.getRancho(activity));
                break;
        }

        limbikaView.setLimbikaView(limbikaView);
        limbikaView.saveViewState();
    }

    // for delay sound in FeedBack Dialog by reaz
    public void delayfeedBack(final String imagePath, final int animation, final int currentTaskIndex, final String soundClip, final int feedBackType) {
//        final String SoundPath = soundClip;
        int pos = 0;
        if (mp != null) {
            pos = mp.getDuration() - mp.getCurrentPosition();
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                mpSoundRelease();
                if (feedBackType == StaticAccess.TAG_TYPE_CIRCULAR) {
                    displayCircularFeedback(imagePath, animation, currentTaskIndex, soundClip);
                } else {
                    displayRectangularFeedback(imagePath, animation, currentTaskIndex, soundClip);
                }

            }
        }, pos);
    }

    // Displaying Circular Feedback image in dialog
    private void displayCircularFeedback(String imagePath, int animation, final int currentTaskIndex, String soundClip) {

        final Dialog dialog = new Dialog(activity, R.style.CustomAlertDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_feedback_circular_player);

        RelativeLayout llFeedbackDialog = (RelativeLayout) dialog.findViewById(R.id.llFeedbackDialog);
        CircleImageView ivFeedback = (CircleImageView) dialog.findViewById(R.id.ivFeedback);
        ivFeedback.setImageBitmap(imageProcessing.getImage(imagePath));

        ivFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mpSoundRelease();
                delayGotoNextTask(currentTaskIndex);

            }
        });
        playSound(soundClip);
        DialogNavBarHide.navBarHide(this, dialog);

        // Setting animation
        showFeedBackAnimation(llFeedbackDialog, animation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mpSoundRelease();

    }

    //feedback Dialog show animation add by reaz
    private void showFeedBackAnimation(View view, int type) {
        switch (type) {
            case Animanation.shake1:
                Animanation.shakeFeedBackAnimation(view);
                break;
            case Animanation.shake2:
                Animanation.shakeFeedBackAnimation2(view);
                break;
            case Animanation.blink1:
                Animanation.blinkFeedBackAnimation(view);
                break;
            case Animanation.slideDownUp:
                Animanation.bottomToTopFeedBackAnimation(view);
                break;
            case Animanation.slideUpDown:
                Animanation.topToBottomFeedBackAnimation(view);
                break;
        }
    }


    //calculating dispaly
    public void calculateDisplaySize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenheight = metrics.heightPixels;
        screenwidth = metrics.widthPixels;
        density = getDensity(metrics.density);
        double x = Math.pow(metrics.widthPixels / density, 2);
        double y = Math.pow(metrics.heightPixels / density, 2);
        double screenInches = Math.sqrt(x + y);
//        setBitmapSampleSize(screenInches);


    }

    // calculating density
    public double getDensity(double density) {

        double densityy = density * 160;

        return densityy;

    }

    //overrride method from GETClickValue
    LimbikaView startView = null;

    @Override
    public void startXYPanelPoint(float x, float y) {
        startView = calculateintersection(x, y);

    }

    boolean isInNoItems(LimbikaView v) {
        boolean answer = false;
        for (Map.Entry<Long, LimbikaView> itemValue : NoItemsMap.entrySet()) {
            if (itemValue.getValue().getLimbikaViewItemValue().getKey() == v.getLimbikaViewItemValue().getKey())
                answer = true;
        }
        return answer;
    }

    boolean isInDropItems(LimbikaView v) {
        boolean answer = false;
        for (Map.Entry<Long, LimbikaView> itemValue : dropItemsMap.entrySet()) {
            if (itemValue.getValue().getLimbikaViewItemValue().getKey() == v.getLimbikaViewItemValue().getKey())
                answer = true;
        }
        return answer;
    }

    @Override
    public void endXYPanelPoint(float x, float y) {
        LimbikaView endView = calculateintersection(x, y);
        if (startView != null && endView != null) {
            //these are targets dont confuse it with start and endview
            LimbikaView startTarget = dropTargetsMap.get(startView.getTargetKey());
            LimbikaView endTarget = dropTargetsMap.get(endView.getTargetKey());
            DrawAssistiveLine drawAssistiveLine = new DrawAssistiveLine(activity);

            //this checks if one of the view is noItem or not (DO NO MOVE THIS BLOCK)
            if (isInNoItems(startView) || isInNoItems(endView)) {
                //(DO NO MOVE THIS BLOCK) this block should be here
            }
            //now check if any drop target is there or not
            else if (startTarget != null || endTarget != null) {
                //checking the startTargetonly the end target is cheked later
                //DO NOT CONFUSE IT WITH START VIEW AND END VIEW THIS IS COMPELTELY DIFFERENT
                //startView==child && endview==target
                if (startTarget != null) {
                    //either targetkey is -1 (which means no error mode) or assistiveTargetKey and targetkey shd be same
                    if (assistiveTargetKey == -1 || (assistiveTargetKey == startTarget.getLimbikaViewItemValue().getKey()))
                        if (startTarget.getLimbikaViewItemValue().getKey() == endView.getLimbikaViewItemValue().getKey()) {
                            //endView is the target for startView so remove startview which is a child

                            positiveResult.remove(startView.getLimbikaViewItemValue().getKey());
                            alreadyCorrectDragandDrop.add(startView.getLimbikaViewItemValue().getKey());

                            if (task.getPositiveAnimation() > 0) {
                                showAnimation(startView, task.getPositiveAnimation());
                                showAnimation(endView, task.getPositiveAnimation());
                            }

                            //check if all the dropItems of the target is already droped on it or anything left
                            //if nothing is left unlock the other views
                            if (error_mode_on_dragDrop)
                                if (ifTargetTotallyPlayed(startTarget.getLimbikaViewItemValue().getKey())) {
                                    makeAllViewsDragable();
                                }

                            drawAssistiveLine.DrawAssistiveLine(startView, endView, true);
                            assistiveRelLayout.addView(drawAssistiveLine);

                            playSoundDragAndAssistive(task, task.getPositiveSound());

                            if (!items.get(startView.getLimbikaViewItemValue().getKey()).getShowedByTarget().equals(""))
                                showHideDragandDrop(items.get(startView.getLimbikaViewItemValue().getKey()));
                            //if target has showedby/hiden by
                            if (!items.get(endView.getLimbikaViewItemValue().getKey()).getHiddenByTarget().equals(""))
                                showHideDragandDrop(items.get(endView.getLimbikaViewItemValue().getKey()));

                          /*  showHideDragandDrop(items.get(startView.getLimbikaViewItemValue().getKey()));
                            showHideDragandDrop(items.get(endView.getLimbikaViewItemValue().getKey()));*/

                        } else if (startView == endView) {
                            //do nothing
                        } else if (isInDropItems(startView) && isInDropItems(endView)) {
                            //when both of the views are drop items do nothin
                        } else {

                            if (task.getNegativeAnimation() > 0) {
                                showAnimation(startView, task.getNegativeAnimation());
                                showAnimation(endView, task.getNegativeAnimation());
                            }
                            // drawAssistiveLine.DrawAssistiveLine(startView, endView, false);
                            //assistiveRelLayout.addView(drawAssistiveLine);
                            if (madatoryError == 1) {
                                errorListner.onErrorListner(task, StaticAccess.TAG_TASK_DRAG_AND_DROP_MODE,
                                        startTarget.getLimbikaViewItemValue().getKey());
                                assistiveTargetKey = startTarget.getLimbikaViewItemValue().getKey();
                            } else if (madatoryError == 0) {
                                // for negative task sound add by reaz
                                if (task.getNegativeSound().length() > 0) {
                                    playSoundDragAndAssistive(task, task.getNegativeSound());
                                }
                            }

                        }


                }
         /* ****************************** When startView is TARGET ****************************** */
                else if (endTarget != null) {
                    //either targetkey is -1 (which means no error mode) or assistiveTargetKey and targetkey shd be same
                    if (assistiveTargetKey == -1 || (assistiveTargetKey == endTarget.getLimbikaViewItemValue().getKey()))
                        if (endTarget.getLimbikaViewItemValue().getKey() == startView.getLimbikaViewItemValue().getKey()) {

                            positiveResult.remove(endView.getLimbikaViewItemValue().getKey());
                            alreadyCorrectDragandDrop.add(endView.getLimbikaViewItemValue().getKey());

                       /* if (task.getPositiveSound().length() > 0) {
                            playSound(task.getPositiveSound());
                        }*/
                            if (task.getPositiveAnimation() > 0) {
                                showAnimation(startView, task.getPositiveAnimation());
                                showAnimation(endView, task.getPositiveAnimation());
                            }


                            drawAssistiveLine.DrawAssistiveLine(startView, endView, true);
                            assistiveRelLayout.addView(drawAssistiveLine);

                            playSoundDragAndAssistive(task, task.getPositiveSound());

                            if (!items.get(endView.getLimbikaViewItemValue().getKey()).getShowedByTarget().equals(""))
                                showHideDragandDrop(items.get(endView.getLimbikaViewItemValue().getKey()));
                            //if target has showedby/hiden by
                            if (!items.get(startView.getLimbikaViewItemValue().getKey()).getHiddenByTarget().equals(""))
                                showHideDragandDrop(items.get(startView.getLimbikaViewItemValue().getKey()));


                          /*  showHideDragandDrop(items.get(endView.getLimbikaViewItemValue().getKey()));
                            showHideDragandDrop(items.get(startView.getLimbikaViewItemValue().getKey()));*/

                            //check if all the dropItems of the target is already droped on it or anything left
                            //if nothing is left unlock the other views
                            if (error_mode_on_dragDrop)
                                if (ifTargetTotallyPlayed(endTarget.getLimbikaViewItemValue().getKey())) {
                                    makeAllViewsDragable();
                                }

                            //Toast.makeText(this, "matched", Toast.LENGTH_LONG).show();
                        } else if (startView == endView) {
                            //do nothing
                        } else if (isInDropItems(startView) && isInDropItems(endView)) {
                            //when both of the views are drop items do nothin
                        } else {

                         /*   if (task.getNegativeSound().length() > 0) {
                                playSound(task.getNegativeSound());
                            }*/
                            if (task.getNegativeAnimation() > 0) {
                                showAnimation(startView, task.getNegativeAnimation());
                                showAnimation(endView, task.getNegativeAnimation());
                            }
                            if (madatoryError == 1) {
                                errorListner.onErrorListner(task, StaticAccess.TAG_TASK_DRAG_AND_DROP_MODE,
                                        endTarget.getLimbikaViewItemValue().getKey());
                                assistiveTargetKey = endTarget.getLimbikaViewItemValue().getKey();
                            } else if (madatoryError == 0) {
                                // for negative task sound add by reaz
                                if (task.getNegativeSound().length() > 0) {
                                    playSoundDragAndAssistive(task, task.getNegativeSound());
                                }
                            }
                            //drawAssistiveLine.DrawAssistiveLine(startView, endView, false);
                            //assistiveRelLayout.addView(drawAssistiveLine);
                        }
                }
            }
        }
        //when finger is lifted the saved view shall be null
        startView = null;
    }

    @Override
    public void singleTap(float x, float y) {
        Item item1 = null;
        LimbikaView v = calculateintersection(x, y);

        if (v != null) {
            item1 = items.get(v.getLimbikaViewItemValue().getKey());
            if (!isBlockUser) {
                isBlockUser = true;
                v.zoomOut(v);
                //now no Items will handle showed and hidden too
                if (item1.getDragDropTarget() == 0 && item1.getAllowDragDrop() == 0)
                    SingleTapTask(item1, 0, true, true);
                else
                    SingleTapTask(item1, 0, true, false);
            }
        }
    }

    @Override
    public void doubleTap(float x, float y) {

    }

    //colle
    ArrayList<Limbika_mesurement> measurement_list = new ArrayList<Limbika_mesurement>();
    HashMap<Long, LimbikaView> limbikaview_list = new HashMap<Long, LimbikaView>();

    LimbikaView calculateintersection(float x, float y) {
        boolean intersects = false;
        LimbikaView v = null;

        for (Limbika_mesurement measurement : measurement_list) {
            int xx = (int) x;
            int yy = (int) y;
            Rect r = new Rect(xx, yy, xx + 20, yy + 20);

            Log.e("Here: ", String.valueOf(xx) + " " + String.valueOf(yy));

            int xxx = (int) measurement.getX();
            int yyy = (int) measurement.getY();
            int w = (int) measurement.getWidth();
            int h = (int) measurement.getHeight();
            Rect measure = new Rect(xxx, yyy, xxx + w, yyy + h);

            Log.e(String.valueOf(xxx) + " " + String.valueOf(xxx + w), String.valueOf(yyy) + " " + String.valueOf(yyy + h));

            //Toast .makeText(this, measurement_list.size()+" doubleTapTouch: x, y = " + x + " " + y, Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "doubleTap2: x, y = " + w + " " + h, Toast.LENGTH_SHORT).show();
            intersects = r.intersect(measure);
            if (intersects) {
                Log.e("Done", "1");
                v = limbikaview_list.get(measurement.getKey());
                //  if(v!=null)
                // Toast.makeText(this, "intersext", Toast.LENGTH_SHORT).show();
            }

        }
        return v;
    }


    public void delaySoundPlay(int delay, final String play) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                playSound(play);
            }
        }, delay * 1000);
    }

    // sound release created by reaz
    public void mpSoundRelease() {
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }

    // after 1 sec task will going to be next  Task created by reaz
    public void delayGotoNextTask(final int currentTaskIndex) {
        int pos = 0;
        if (mp != null) {
            pos = mp.getDuration() - mp.getCurrentPosition();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                gotoNextTask(currentTaskIndex);

            }
        }, pos);

    }

    // Hiding items for showed by
    private void hideItems() {
        for (Map.Entry<Long, LimbikaView> views : itemViewHolderMap.entrySet()) {
            long key = views.getKey();
            Item item = items.get(key);
            long showedByKey = item.getShowedBy();
            if (showedByKey > 0) {
                itemViewHolderMap.get(showedByKey).setVisibility(View.INVISIBLE);
            }
            //itemViewHolderMap.get(items.get(views.getKey()).getShowedBy()).setVisibility(View.INVISIBLE);
        }
    }

    //will be called from the listeners of drag item
    public void showHideDragandDrop(Item item1) {
        //get showed by list and show them
        if (item1.getShowedByTarget() != null && !item1.getShowedByTarget().equals("")) {
            String str = item1.getShowedByTarget();
            ArrayList<Long> list = formatList(str);
            showItemsFromShowedby(list);
        }
        //get hidden by list and show them
        if (item1.getHiddenByTarget() != null && !item1.getHiddenByTarget().equals("")) {
            String str = item1.getHiddenByTarget();
            ArrayList<Long> list = formatList(str);
            hideItemsFromShowedby(list);
        }
       /* for (final Map.Entry<Long, Item> itemValue : items.entrySet()) {
            final Item item2 = itemValue.getValue();
            if (item2.getHideBy() == item.getKey()) {
                itemViewHolderMap.get(item2.getKey()).setVisibility(View.INVISIBLE);
            } else if (item2.getShowedBy() == item.getKey()) {
                itemViewHolderMap.get(item2.getKey()).setVisibility(View.VISIBLE);
            }*/
    }


    // close app,Sound, navigate to, open App, open url all implement single tap  created by reaz
    public void SingleTapTask(Item item, int soundTypeCount, boolean blockuser, boolean noItem) {
        if (blockuser) {
            this.soundTypeCount = soundTypeCount;
//        if (!isInnerBlock) {

            switch (soundTypeCount) {
                case StaticAccess.TAG_ITEM_SOUND_PLAY:
                    if (item.getItemSound().length() > 0 && item.getItemSound() != null) {
                        playSound(item.getItemSound(), item, noItem);
                    } else {
                        playSound(null, item, noItem);
                    }
                    break;

                case StaticAccess.TAG_END_SINGLE_TAP_TASK:

                    if (item.getCloseApp() == 1) {
                        Intent intent = new Intent(activity, TaskPackActivity.class);
                        intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, taskPackId);
                        startActivity(intent);
                        mpSoundRelease();
                        finish();
                    }
                    if (item.getOpenUrl().length() > 0 && item.getOpenApp() != null) {

                        openWebLink(item.getOpenUrl());
                    }
                    // double tap open 3rd party app added by reaz
                    if (item.getOpenApp().length() > 0 && item.getOpenApp() != null) {
                        mpSoundRelease();
                        openApp(item.getOpenApp());
                    }
                    if (item.getNavigateTo() > 0) {

                        positiveResult.clear();
                        gotoSpecificTask(item.getNavigateTo());
                    }

                    //showed by and hidden by will work here only if its no item otherwise showHideDragandDrop() will handle
                    if (noItem) {
                        //get showed by list and show them
                        if (item.getShowedByTarget() != null && !item.getShowedByTarget().equals("")) {
                            String str = item.getShowedByTarget();
                            ArrayList<Long> list = formatList(str);
                            showItemsFromShowedby(list);
                        }
                        //get hidden by list and show them
                        if (item.getHiddenByTarget() != null && !item.getHiddenByTarget().equals("")) {
                            String str = item.getHiddenByTarget();
                            ArrayList<Long> list = formatList(str);
                            hideItemsFromShowedby(list);
                        }
                    }
                    isBlockUser = false;

//                    isInnerBlock=true;
                    soundTypeCount = 0;
                    break;
            }
        }

    }

    // for Sound play method to method call Created by reaz
    private void playSound(String clipName, final Item item, final boolean noItem) {

        if (clipName == null) {
            soundTypeCount++;
            SingleTapTask(item, soundTypeCount, true, noItem);
        } else if (clipName.length() > 0) {
            mpSoundRelease();
            mp = new MediaPlayer();
            try {
                mp.setDataSource(Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + activity.getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_SOUND + clipName);
//                if(mp != null && mp.isPlaying()){
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        soundTypeCount++;
                        SingleTapTask(item, soundTypeCount, true, noItem);
                          /*  if (mp != null)
                                mp.release();*/
                    }

                });
//                }

                mp.prepare();
                mp.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Playing sound
    private void playSound(String clipName) {
        if (clipName.length() > 0) {
            mpSoundRelease();
            mp = new MediaPlayer();
            try {
                mp.setDataSource(Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + activity.getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_SOUND + clipName);
                mp.prepare();
                mp.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onErrorListner(Task task, int taskMode) {
//  go to error Screen Created By reaz
        Intent intSuperError = new Intent(activity, SuperErrorActivity.class);
        intSuperError.putExtra(StaticAccess.TAG_PLAY_ERROR_COLOR, task.getErrorBgColor());
        intSuperError.putExtra(StaticAccess.TAG_PLAY_ERROR_TEXT, task.getErrortext());
        intSuperError.putExtra(StaticAccess.TAG_PLAY_ERROR_IMAGE, task.getErrorImage());
        intSuperError.putExtra(StaticAccess.TAG_PLAY_ERROR_SOUND, task.getNegativeSound());
        if (taskMode == StaticAccess.TAG_TASK_GENERAL_MODE) {
            intSuperError.putExtra(StaticAccess.TAG_TASK_MODE_KEY, StaticAccess.TAG_TASK_GENERAL_MODE);
            startActivityForResult(intSuperError, StaticAccess.TAG_TASK_GENERAL_MODE);
        }

    }

    @Override
    public void onErrorListner(Task task, int taskMode, long targetKey) {
        //  go to error Screen Created By reaz
        Intent intSuperError = new Intent(activity, SuperErrorActivity.class);
        intSuperError.putExtra(StaticAccess.TAG_PLAY_ERROR_COLOR, task.getErrorBgColor());
        intSuperError.putExtra(StaticAccess.TAG_PLAY_ERROR_TEXT, task.getErrortext());
        intSuperError.putExtra(StaticAccess.TAG_PLAY_ERROR_IMAGE, task.getErrorImage());
        intSuperError.putExtra(StaticAccess.TAG_PLAY_ERROR_SOUND, task.getNegativeSound());

        error_mode_on_dragDrop = true;

        if (taskMode == StaticAccess.TAG_TASK_DRAG_AND_DROP_MODE) {
            intSuperError.putExtra(StaticAccess.TAG_TASK_MODE_KEY, StaticAccess.TAG_TASK_DRAG_AND_DROP_MODE);
            intSuperError.putExtra(StaticAccess.TARGET_KEY, targetKey);
            startActivityForResult(intSuperError, StaticAccess.TAG_TASK_DRAG_AND_DROP_MODE);
        } else if (taskMode == StaticAccess.TAG_TASK_ASSISTIVE_MODE) {
            intSuperError.putExtra(StaticAccess.TAG_TASK_MODE_KEY, StaticAccess.TAG_TASK_ASSISTIVE_MODE);
            intSuperError.putExtra(StaticAccess.TARGET_KEY, targetKey);
            startActivityForResult(intSuperError, StaticAccess.TAG_TASK_ASSISTIVE_MODE);
        }

    }

    // block only wrong View  created Mir
    void blockAllWrongViews() {
        for (long key : negativeResult) {
            LimbikaView v = allTheViewsToClearAnimation.get(key);
            if (v != null)
                v.setSingleTapListener(null);
        }
    }

    // block only wrong View  created Reaz
    public void allPositiveAnimationViews() {
        for (long key : positiveResult) {
            LimbikaView v = allTheViewsToClearAnimation.get(key);
            if (v != null)
                showAnimation(v, Animanation.slideDownUp);
        }
    }

    //checking if the drop item is already played and it is correct and on target
    boolean ifViewAlreadyCorrect(long key) {
        boolean res = false;
        for (long keys : alreadyCorrectDragandDrop) {
            if (keys == key)
                res = true;
        }
        return res;
    }

    //for drag and drop
    public void allPositiveAnimationViews(ArrayList<LimbikaView> list) {
        for (LimbikaView v : list) {

            if (v != null) {
                if (v.getVisibility() == View.VISIBLE)
                    showAnimation(v, Animanation.blink1);
                if (!ifViewAlreadyCorrect(v.getKey())) {
                    v.setDraggable(true);
                }
            }
        }
    }

    // set everyview to dragable false
    void makeAllViewsNotDragabble() {

        for (Map.Entry<Long, LimbikaView> itemValue : dropItemsMap.entrySet()) {
            LimbikaView v = itemValue.getValue();
            v.setDraggable(false);
        }
    }

    // making all views enable except the already right(which means already played) ones
    //this basically end of error mode for both assistive and drag & drop
    public void makeAllViewsDragable() {
        //unblockViews
        //this variable is only used only in asssistive mode as no
        assistiveTargetKey = -1;
        error_mode_on_dragDrop = false;

        for (Map.Entry<Long, LimbikaView> itemValue : dropItemsMap.entrySet()) {
            LimbikaView v = itemValue.getValue();
            if (!ifViewAlreadyCorrect(v.getKey()))
                v.setDraggable(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StaticAccess.TAG_TASK_GENERAL_MODE) {
            if (null != data) {
                // fetch the message String
                String message = data.getStringExtra(StaticAccess.TAG_PLAY_ERROR_RESPONSE);

                // Set the message string in textView
                if (message.equals(StaticAccess.TAG_PLAY_ERROR_RESPONSE)) {
                    blockAllWrongViews();
                    allPositiveAnimationViews();
                } else {

                }
            }
        } else if (requestCode == StaticAccess.TAG_TASK_DRAG_AND_DROP_MODE) {
            if (null != data) {
                // fetch the message String
                String message = data.getStringExtra(StaticAccess.TAG_PLAY_ERROR_RESPONSE);
                long target = data.getLongExtra(StaticAccess.TARGET_KEY, -1);

                if (target != -1) {
                    if (!ifTargetTotallyPlayed(target))
                        makeAllViewsNotDragabble();

                    allPositiveAnimationViews(targets_with_droapItems_map.get(target));
                }

            }
        } else if (requestCode == StaticAccess.TAG_TASK_ASSISTIVE_MODE) {
            if (null != data) {
                // fetch the message String
                String message = data.getStringExtra(StaticAccess.TAG_PLAY_ERROR_RESPONSE);
                long target = data.getLongExtra(StaticAccess.TARGET_KEY, -1);

                if (target != -1) {
                    if (!ifTargetTotallyPlayed(target))
                        makeAllViewsNotDragabble();
                    //refrshing the key (no need)
                    assistiveTargetKey = target;
                    allPositiveAnimationViews(targets_with_droapItems_map.get(target));
                }

            }
        }
    }

    // should we unblock all the views after blocking in error (it basically checks this target is totally played or not)
    public boolean ifTargetTotallyPlayed(long target) {
        boolean res = true;

        ArrayList<LimbikaView> list = targets_with_droapItems_map.get(target);

        if (list != null)
            for (LimbikaView v : list) {
                if (!ifViewAlreadyCorrect(v.getKey()))
                    res = false;
                break;
            }


        return res;
    }

    // use general mode negative Sound also manage item sound after go to error screen
    void playNegSoundGeneral(final Task task, String sound) {

        if (!sound.equals("")) {
            try {
                mpSoundRelease();
                mp = new MediaPlayer();
                mp.setLooping(false);
                mp.setDataSource(Environment.getExternalStorageDirectory() +
                        StaticAccess.ANDROID_DATA + activity.getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_SOUND + sound);
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        //if mandatory is true then send it to uper error else do nothing
                        if (madatoryError == 1)
                            errorListner.onErrorListner(task, StaticAccess.TAG_TASK_GENERAL_MODE);

                    }
                });
                mp.prepare();
                mp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        //if no negative sound then move on
        else {
            if (madatoryError == 1)
                errorListner.onErrorListner(task, StaticAccess.TAG_TASK_GENERAL_MODE);
            else {

            }
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        cx = ev.getX();
        cy = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cx = ev.getX();
                cy = ev.getY();
                setAnimationTouchFeedBack(taskPack.getTouchAnimation(), cx, cy, activity);

                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setAnimationTouchFeedBack(int type, float cx, float cy, Context context) {

        switch (type) {
            case StaticAccess.ANIM_TOUCH_NONE:
                break;
            case StaticAccess.ANIM_TOUCH_COLOR_CIRCLE:
                CustomVariousColorCircleAnimationView colorCircle = new CustomVariousColorCircleAnimationView(context, cx, cy);
                rlPlayer.addView(colorCircle);
                break;
            case StaticAccess.ANIM_TOUCH_COLOR_CIRCLE_STROKE:
                CustomVariousColorCircleStrokeAnimationView colorCircleStock = new CustomVariousColorCircleStrokeAnimationView(context, cx, cy);
                rlPlayer.addView(colorCircleStock);
                break;
            case StaticAccess.ANIM_TOUCH_CIRCLE_STROKE:
                CustomCircleStrokeAnimation circleStroke = new CustomCircleStrokeAnimation(context, cx, cy);
                rlPlayer.addView(circleStroke);
                break;
            case StaticAccess.ANIM_TOUCH_EVEN:
                touchCustomView = new OnTouchCustomView(context, cx, cy);
                rlPlayer.addView(touchCustomView);
                break;

        }

    }

    public void returnEditor() {
        Intent returnIntent = new Intent();
        setResult(StaticAccess.TAG_CREATE_TASK_PREVIEW_MODE, returnIntent);
        finish();
    }


    public void loadTaskPreview(final Long currentTaskID) {
        //Global object thats why need to clear item task
//        this.currentTaskIndex = currentTaskIndex;
        task = databaseManager.getTaskById(currentTaskID);

        //mir // FIXME: 31/08/2016 clearing all the hasmaps with each task
        assistiveRelLayout.removeAllViews();
        if (mapping != null)
            assistiveRelLayout.addView(mapping);
        dropTargetsMap.clear();
        dropItemsMap.clear();
        itemLinkedHashMap.clear();
        NoItemsMap.clear();
        itemViewHolderMap.clear();
        negativeResult.clear();
        error_mode_on_dragDrop = false;
        assistiveTargetKey = -1;

        targets_with_droapItems_map.clear();
        alreadyCorrectDragandDrop.clear();//to calculate already correct dropitems

        //clearing assistive touch collection
        measurement_list.clear();
        limbikaview_list.clear();
        items = new LinkedHashMap<>();

        madatoryError = task.getErrorMandatoryScreen();
        loadTaskValue(task);
    }

    private void loadTaskValue(final Task task) {
        if (task != null) {
            flPlay.setBackgroundColor(task.getBackgroundColor());
            items = databaseManager.loadTaskWiseItem(task);
            ibtnMicroPhone.setVisibility(View.GONE);

            if (items != null) {
                for (final Map.Entry<Long, Item> itemValue : items.entrySet()) {
                    final Item item = itemValue.getValue();
                    limbikaView = new LimbikaView(this, item.getKey(), true);
                    alltheViews.put(item.getKey(), limbikaView);

                    limbikaView.startOnTapAnimation(taskPack.getItemOfAnimation());
                    LimbikaViewItemValue limbikaViewItemValue = new LimbikaViewItemValue();
                    limbikaViewItemValue.setX(item.getX());
                    limbikaViewItemValue.setY(item.getY());
                    limbikaViewItemValue.setRotation(item.getRotation());
                    limbikaViewItemValue.setKey(item.getKey());
                    limbikaViewItemValue.setIsCircleView(item.getIsCircleView());
                    limbikaViewItemValue.setCircleColor(item.getCircleColor());
                    limbikaViewItemValue.setUserText(item.getUserText());
                    limbikaViewItemValue.setTextColor(item.getTextColor());
                    limbikaViewItemValue.setTextSize(item.getTextSize());
                    limbikaViewItemValue.setBorderColor(item.getBorderColor());
                    limbikaViewItemValue.setBackgroundColor(item.getBackgroundColor());
                    limbikaViewItemValue.setDrawable(item.getDrawable());
                    limbikaViewItemValue.setWidth(item.getWidth());
                    limbikaViewItemValue.setHeight(item.getHeight());
                    limbikaViewItemValue.setLeft(item.getLeft());
                    limbikaViewItemValue.setRight(item.getRight());
                    limbikaViewItemValue.setTop(item.getTop());
                    limbikaViewItemValue.setBottom(item.getBottom());
                    limbikaViewItemValue.setImagePath(item.getImagePath());
                    limbikaViewItemValue.setCornerRound(item.getCornerRound());
                    limbikaViewItemValue.setFontTypeFace(item.getFontTypeFace());
                    limbikaViewItemValue.setFontAlign(item.getFontAlign());
                    limbikaViewItemValue.setBorderPixel(item.getBorderPixel());


                    //this part is where u decide to show the upper layer or not
                    if (task.getType().equals(TaskType.Assistive)) {
                        assistiveRelLayout.setVisibility(View.VISIBLE);
                    } else {
                        assistiveRelLayout.setVisibility(View.GONE);
                    }


                    ////////////////////////////////  GENERAL TYPE START   /////////////////////////////////
                    // Checking Result for general types of tasks that accept only True/False
                    if (task.getType().equals(TaskType.Normal)) {
                        limbikaView.setEnabled(false);
                        if (item.getResult().equals(TaskType.NORMAL_TRUE))
                            positiveResult.add(item.getKey());
                        else if (item.getResult().equals(TaskType.NORMAL_FALSE))
                            negativeResult.add(item.getKey());

                        limbikaView.setSingleTapListener(new LimbikaView.SingleTapListener() {
                            @Override
                            public void onSingleTap(View v, LimbikaViewItemValue limbikaViewItemValue) {
                                Item item1 = items.get(limbikaViewItemValue.getKey());
                                GeneralMode generalMode = new GeneralMode(activity, task, item1);
                                generalMode.generalMode(v, limbikaViewItemValue);
                            }
                        });
                    }
                    ////////////////////////////////  GENERAL TYPE END   /////////////////////////////////


                    ////////////////////////////////   DRAG & DROP TYPE START   /////////////////////////////////
                    else if (task.getType().equals(TaskType.DragDrop)) {
                        limbikaView.hideBalls();
                        dragAndDropMode = new DragAndDropMode(activity, task);
                        limbikaView.setSingleTapListener(new LimbikaView.SingleTapListener() {
                            @Override
                            public void onSingleTap(View v, LimbikaViewItemValue limbikaViewItemValue) {
                                Item item1 = items.get(limbikaViewItemValue.getKey());
                                dragAndDropMode.singleTap(item1);

                                //this function communicates with the playsound and until its finished no other click will be executed
                               /* if (!isBlockUser) {
                                    isBlockUser = true;

                                    //now no Items will handle showed and hidden too
                                    if (item.getDragDropTarget() == 0 && item.getAllowDragDrop() == 0)
                                        SingleTapTask(item1, 0, true, true);
                                    else
                                        SingleTapTask(item1, 0, true, false);
                                }*/


                                //if (item1.getShowedBy() > 0) {
                                //itemViewHolderMap.get(item1.getShowedBy()).setVisibility(View.VISIBLE);
                                //}


                                /*if (item1.getHideBy() > 0) {
                                    itemViewHolderMap.get(item1.getHideBy()).setVisibility(View.INVISIBLE);
                                }*/
                            }
                        });

                        //Drop Target
                        if (item.getAllowDragDrop() == 1) {
                            limbikaView.setDraggable(false);
                            //gets the targets
                            dropTargetsMap.put(item.getKey(), limbikaView);
                        } //no need to set all the listeners here we will find it from collection and call methods
                        else if (item.getDragDropTarget() == 0 && item.getAllowDragDrop() == 0) {
                            limbikaView.setDraggable(false);
                            NoItemsMap.put(item.getKey(), limbikaView);
                        }

                        //Drop Items so it will move
                        else {
                            limbikaView.setDraggable(true);
                            limbikaView.setChildView(true);
                            dropItemsMap.put(item.getKey(), limbikaView);
                            itemLinkedHashMap.put(item.getKey(), item);
                            if (item.getDragDropTarget() != 0) {
                                positiveResult.add(item.getKey());
                            }
                            limbikaView.setDropTargetListener(new LimbikaView.DropTargetListener() {
                                @Override
                                public void onViewDropped(LimbikaView view, Long dropTargetKey) {
                                    dragAndDropMode.onDropTarget(view, dropTargetKey);
                                    // this function transfer a separate class which name is DragAndDropMode comment by Reaz
                                  /*  Item selectedItemTarget = items.get(dropTargetKey);
                                    // For positive Tag
                                    if (selectedItem.getDragDropTarget() == dropTargetKey) {
                                        LimbikaView limbikaView = dropItemsMap.get(selectedItem.getKey());
                                        limbikaView.showPositiveTag();
                                        LimbikaView limbikaViewTarget = dropTargetsMap.get(dropTargetKey);


//                                        playSound(task.getPositiveSound());
                                        positiveResult.remove(selectedItem.getKey());
                                        alreadyCorrectDragandDrop.add(selectedItem.getKey());

                                        //check if all the dropItems of the target is already droped on it or anything left
                                        //if nothing is left unvlock the other views
                                        if (error_mode_on_dragDrop)
                                            if (ifTargetTotallyPlayed(dropTargetKey)) {
                                                makeAllViewsDragable();
                                            }


                                        if (task.getPositiveAnimation() > 0) {
                                            showAnimation(limbikaViewTarget, task.getPositiveAnimation());

                                        }

                                        playSoundDragAndAssistive(task, task.getPositiveSound());
                                        //when the item is dropped on target then show the hide/show item
                                        if (!selectedItem.getShowedByTarget().equals(""))
                                            showHideDragandDrop(selectedItem);
                                        //if target has showedby/hiden by
                                        if (!selectedItemTarget.getHiddenByTarget().equals(""))
                                            showHideDragandDrop(selectedItemTarget);
                                    }*/
                                }

                                @Override
                                public void onViewDroppedonWrongTarge(LimbikaView view, Long dropTargetKey) {
                                    dragAndDropMode.onDroppedonWrongTarge(view, dropTargetKey);
                                    // this function transfer a separate class which name is DragAndDropMode comment by Reaz
                                  /*  if (dropTargetKey != -1) {
                                        Item selectedItem = items.get(view.getLimbikaViewItemValue().getKey());
                                        LimbikaView limbikaView = dropItemsMap.get(selectedItem.getKey());
                                        limbikaView.showNegativeTag();
                                        LimbikaView limbikaViewTarget = dropTargetsMap.get(dropTargetKey);
                                        //we must draw negative animation on target
                                        if (madatoryError == 1)
                                            errorListner.onErrorListner(task, StaticAccess.TAG_TASK_DRAG_AND_DROP_MODE,
                                                    dropTargetKey);
                                        else {
                                            playSoundDragAndAssistive(task, task.getNegativeSound());
                                        }
//                                        playSound(task.getNegativeSound());
                                        if (task.getNegativeAnimation() > 0) {
                                            //mir change
                                            showAnimation(limbikaViewTarget, task.getNegativeAnimation());
                                        }
                                    }*/

                                }
                            });
                        }
                    }
                    ////////////////////////////////   DRAG & DROP TYPE START   /////////////////////////////////

                    ////////////////////////////////   ASSISTIVE TYPE START   /////////////////////////////////
                    else if (task.getType().equals(TaskType.Assistive)) {
                        limbikaView.hideBalls();

                        //Drop Target
                        if (item.getAllowDragDrop() == 1) {
                            limbikaView.setDraggable(false);
                            //gets the targets
                            dropTargetsMap.put(item.getKey(), limbikaView);
                        }
                        //no need to set all the listeners here we will find it from collection and call methods
                        else if (item.getDragDropTarget() == 0 && item.getAllowDragDrop() == 0) {
                            limbikaView.setDraggable(false);
                            NoItemsMap.put(item.getKey(), limbikaView);
                        }

                        //Drop Items so it will move
                        else if (item.getAllowDragDrop() == 0) {
                            limbikaView.setDraggable(true);
                            limbikaView.setChildView(true);
                            dropItemsMap.put(item.getKey(), limbikaView);
                            itemLinkedHashMap.put(item.getKey(), item);
                            if (item.getDragDropTarget() != 0) {
                                positiveResult.add(item.getKey());
                            }
                            limbikaView.setDropTargetListener(new LimbikaView.DropTargetListener() {
                                @Override
                                public void onViewDropped(LimbikaView view, Long dropTargetKey) {
                                    Item selectedItem = items.get(view.getLimbikaViewItemValue().getKey());

                                    // For positive Tag
                                    if (selectedItem.getDragDropTarget() == dropTargetKey) {
                                        LimbikaView limbikaView = dropItemsMap.get(selectedItem.getKey());
                                        limbikaView.showPositiveTag();
                                        LimbikaView limbikaViewTarget = dropTargetsMap.get(dropTargetKey);


                                        playSound(task.getPositiveSound());
                                        positiveResult.remove(selectedItem.getKey());

                                        //check if all the dropItems of the target is already droped on it or anything left
                                        //if nothing is left unvlock the other views
                                        if (error_mode_on_dragDrop)
                                            if (ifTargetTotallyPlayed(dropTargetKey)) {
                                                makeAllViewsDragable();
                                            }

                                        if (task.getPositiveAnimation() > 0) {
                                            showAnimation(limbikaViewTarget, task.getPositiveAnimation());

                                        }

                                        //Rafi bhai here is a problem when result size 2 complete that time no execute only when navigate to implement.
                                        if (positiveResult.size() == 0) {
                                            // IF there are any showed by item, it wil show
                                            /*for (LimbikaView limbikaView1 : showedByList) {
                                                showedBy(limbikaView1);
                                            }*/

                                            if (task.getFeedbackImage().length() > 0) {
                                                delayfeedBack(task.getFeedbackImage(), task.getFeedbackAnimation(), currentTaskIndex, task.getFeedbackSound(), task.getFeedbackType());
                                            } else {
                                                delayGotoNextTask(currentTaskIndex);

                                            }
                                        }
                                    }

                                    //For Negative Tag
                                    else {
                                        // LimbikaView limbikaView = dropItemsMap.get(selectedItem.getKey());
                                        //we must draw negative animation on target
                                        // limbikaView.showNegativeTag();
                                        /*playSound(task.getNegativeSound());
                                        if (task.getNegativeAnimation() > 0) {
                                            //mir change
                                            showAnimation(limbikaView, task.getNegativeAnimation());
                                        }*/
                                    }
                                }

                                @Override
                                public void onViewDroppedonWrongTarge(LimbikaView view, Long dropTargetKey) {
                                    if (dropTargetKey != -1) {
                                        // Toast.makeText(PlayerActivity.this, "Gotcha Fuckn wrong", Toast.LENGTH_SHORT).show();
                                        Item selectedItem = items.get(view.getLimbikaViewItemValue().getKey());
                                        LimbikaView limbikaView = dropItemsMap.get(selectedItem.getKey());
                                        limbikaView.showNegativeTag();
                                        LimbikaView limbikaViewTarget = dropTargetsMap.get(dropTargetKey);
                                        //we must draw negative animation on target
                                        errorListner.onErrorListner(task, StaticAccess.TAG_TASK_ASSISTIVE_MODE, dropTargetKey);
//                                        playSound(task.getNegativeSound());
                                        if (task.getNegativeAnimation() > 0) {
                                            //mir change
                                            showAnimation(limbikaViewTarget, task.getNegativeAnimation());
                                        }
                                    }
//                                    if (mp != null)
//                                        mp.release();
                                }
                            });
                        }
                        // Assistive mode unnecessary  commented by reaz
                    }
                    ////////////////////////////////   ASSISTIVE TYPE START   /////////////////////////////////


                    ////////////////////////////////   READ TYPE START   /////////////////////////////////
                    // Checking Result for general types of tasks that accept only True/False
                    if (task.getType().equals(TaskType.Read)) {
                        limbikaView.setEnabled(false);

                        if (item.getResult().equals(TaskType.NORMAL_TRUE))
                            positiveResult.add(item.getKey());
                        else if (item.getResult().equals(TaskType.NORMAL_FALSE))
                            negativeResult.add(item.getKey());

                        limbikaView.setSingleTapListener(new LimbikaView.SingleTapListener() {
                            @Override
                            public void onSingleTap(final View v, final LimbikaViewItemValue limbikaViewItemValue) {

                                final Item item1 = items.get(limbikaViewItemValue.getKey());
                                readMode = new ReadMode(activity, task);
                                readMode.readModeGeneral(item, item1, v, limbikaViewItemValue);
                                // this function transfer a separate class which name is ReadMode comment by Reaz
                               /* //there should be no false here only true
                                if (item1.getResult().length() > 0 && item1.getResult() != null) {
                                    if (!isListening) {
                                        if (item1.getResult().equals(TaskType.NORMAL_TRUE) &&
                                            ifInthePositiveListGenearal(item1.getKey())) {
                                            String savedtext = item1.getReadText();
                                            startListening(item, item1, limbikaView, savedtext, limbikaViewItemValue);
                                            isListening = true;
                                        }
                                    }
                                } else {
                                    playSoundOnlyGeneral(item.getItemSound(), "", "", task, item1);
                                }


                                if (!isListening) {
                                    //DONOT LET THE USERS USE POSITIVE NEGATIVE AND THE BELOW FUNCTIONS TOGETHER THIS WILL CREATE MANICE
                                    //get showed by list and show them
                                    if (item1.getShowedByTarget() != null && !item1.getShowedByTarget().equals("")) {
                                        String str = item1.getShowedByTarget();
                                        ArrayList<Long> list = formatList(str);
                                        showItemsFromShowedby(list);
                                    }
                                    //get hidden by list and show them
                                    if (item1.getHiddenByTarget() != null && !item1.getHiddenByTarget().equals("")) {
                                        String str = item1.getHiddenByTarget();
                                        ArrayList<Long> list = formatList(str);
                                        hideItemsFromShowedby(list);
                                    }


                                    if (item.getCloseApp() == 1) {
                                        Intent intent = new Intent(activity, TaskPackActivity.class);
                                        intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, taskPackId);
                                        startActivity(intent);
                                        mpSoundRelease();
                                        finish();
                                    }
                                    if (item.getOpenUrl().length() > 0 && item.getOpenApp() != null) {
                                        limbikaView.setDraggable(false);
                                        openWebLink(item.getOpenUrl());
                                    }
                                    // single tap open 3rd party app added by reaz
                                    if (item.getOpenApp().length() > 0 && item.getOpenApp() != null) {
                                        mpSoundRelease();
                                        openApp(item.getOpenApp());
                                    }
                                    if (item.getNavigateTo() > 0) {
                                        limbikaView.setDraggable(false);
                                        positiveResult.clear();
                                        gotoSpecificTask(item.getNavigateTo());
                                    }
                                    isBlockUser = false;
                                }*/
                            }
                        });
                    }
                    ////////////////////////////////   READ TYPE END   /////////////////////////////////


                    ////////////////////////////////   Write TYPE START   /////////////////////////////////
                    // Checking Result for general types of tasks that accept only True/False
                    if (task.getType().equals(TaskType.Write)) {
                        limbikaView.setEnabled(false);

                        if (item.getResult().equals(TaskType.NORMAL_TRUE))
                            positiveResult.add(item.getKey());
                        else if (item.getResult().equals(TaskType.NORMAL_FALSE))
                            negativeResult.add(item.getKey());

                        limbikaView.setSingleTapListener(new LimbikaView.SingleTapListener() {
                            @Override
                            public void onSingleTap(final View v, final LimbikaViewItemValue limbikaViewItemValue) {
                                final Item item1 = items.get(limbikaViewItemValue.getKey());
                                WriteMode writeMode=new WriteMode(activity,task,item1);
                                writeMode.writeModePlay(item,item1,v,limbikaViewItemValue);
                            }
                        });
                    }
                    ////////////////////////////////   Write TYPE END   /////////////////////////////////

              //////////////////////   SEQUENCE TYPE START   /////////////////////////////////
                    // Checking Result for general types of tasks that accept only True/False
                    if (task.getType().equals(TaskType.Sequence)) {
                        limbikaView.setEnabled(false);

                        limbikaView.setSingleTapListener(new LimbikaView.SingleTapListener() {
                            @Override
                            public void onSingleTap(View v, LimbikaViewItemValue limbikaViewItemValue) {
                                Item item1 = items.get(limbikaViewItemValue.getKey());


                                if (item1.getResult().length() > 0 && item1.getResult() != null) {
                                    // "T==T"
                                    // the second condition(&&) diminishs the posibity of playing a positive view twice
                                    if (item1.getResult().equals(TaskType.NORMAL_TRUE) &&
                                            ifInthePositiveListSequence(item1.getKey())) {
                                        //task.getNegative sound no need this  playSoundOnlyGeneral Methods that's why we send Blank String
                                        //all the sound and feed back related works are done here so that everything happens simultaneously

                                        LimbikaView limbikaView = (LimbikaView) v;

                                        if (task.getPositiveAnimation() > 0) {
                                            showAnimation(limbikaView, task.getPositiveAnimation());
                                        }
                                        playSoundOnlyGeneral(item1.getItemSound(), task.getPositiveSound(), "", task, item1);

                                        //get showed by list and show them
                                        if (item1.getShowedByTarget() != null && !item1.getShowedByTarget().equals("")) {
                                            String str = item1.getShowedByTarget();
                                            ArrayList<Long> list = formatList(str);
                                            showItemsFromShowedby(list);
                                        }
                                        //get hidden by list and show them
                                        if (item1.getHiddenByTarget() != null && !item1.getHiddenByTarget().equals("")) {
                                            String str = item1.getHiddenByTarget();
                                            ArrayList<Long> list = formatList(str);
                                            hideItemsFromShowedby(list);
                                        }
                                        //positive end////

                                    } else if (item1.getResult().equals(TaskType.NORMAL_FALSE)) {
                                        if (task.getNegativeAnimation() > 0) {
                                            showAnimation(limbikaView, task.getNegativeAnimation());

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
                                    playSoundOnlyGeneral(item.getItemSound(), "", "", task, item1);
                                }


                                //DO NOT LET THE USERS USE POSITIVE NEGATIVE AND THE BELOW FUNCTIONS TOGETHER THIS WILL CREATE MANICE
                                if (item.getCloseApp() == 1) {
                                    Intent intent = new Intent(activity, TaskPackActivity.class);
                                    intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, taskPackId);
                                    startActivity(intent);
                                    mpSoundRelease();
                                    finish();
                                }
                                if (item.getOpenUrl().length() > 0 && item.getOpenApp() != null) {
                                    limbikaView.setDraggable(false);
                                    openWebLink(item.getOpenUrl());
                                }
                                // single tap open 3rd party app added by reaz
                                if (item.getOpenApp().length() > 0 && item.getOpenApp() != null) {
                                    mpSoundRelease();
                                    openApp(item.getOpenApp());
                                }
                                if (item.getNavigateTo() > 0) {
                                    limbikaView.setDraggable(false);
                                    positiveResult.clear();
                                    gotoSpecificTask(item.getNavigateTo());
                                }
                                isBlockUser = false;
                            }
                        });
                    }
                    ////////////////////////////////   SEQUENCE TYPE END   /////////////////////////////////


                    limbikaView.onResume(limbikaViewItemValue);
                    //setting measurement to calculate from upperlayer touch
                    if (task.getType().equals(TaskType.Assistive))
                        if (limbikaViewItemValue != null) {
                            Limbika_mesurement measurement = new Limbika_mesurement(limbikaView.getX()
                                    , limbikaView.getY(), limbikaView.canvasWidth,
                                    limbikaView.canvasHeight, limbikaView.getKey());
                            measurement_list.add(measurement);
                            limbikaview_list.put(limbikaViewItemValue.getKey(), limbikaView);
                        }


                    limbikaView.setLayoutParams(new FrameLayout.LayoutParams(500, 500));
                    flPlay.addView(limbikaView);

                    itemViewHolderMap.put(item.getKey(), limbikaView);
                    allTheViewsToClearAnimation.put(limbikaViewItemValue.getKey(), limbikaView);


                    setDropTarget();
                    // manageAlignment();
                    if (limbikaViewItemValue.getUserText().length() > 0) {
                        setFontType(limbikaViewItemValue.getFontTypeFace(), limbikaView);
                    }
                    // if sound delay and auto play  exists that time it only execute sound delay
                    if (item.getAutoPlay() > 0 && item.getItemSound().length() > 0) {
                        // if sound play after  few sec later check here
                        if (item.getSoundDelay() > 0 && item.getItemSound().length() > 0) {
                            delaySoundPlay(item.getSoundDelay(), item.getItemSound());
                        } else {
                            playSound(item.getItemSound());
                        }

                    }


//                    if (item.getShowedBy() > 0) {
//                        delaySoundPlay(item.getSoundDelay(), item.getItemSound());
//                    }

                    if (item.getShowedBy() > 0) {
                        limbikaView.setVisibility(View.INVISIBLE);
                    }

                }
                //end of loop
                //this sets all the targets to each limbika view
                setDropTargetMap();
                //hideItems();
            }


            //some preparation if the task is sequential
            preparationForSequential(task);

        }
    }

    ///convert a string to Array list of long for showed and hidden targets
    public ArrayList<Long> formatList(String commaSeparatedString) {
        ArrayList<Long> list = new ArrayList<>();
        String[] ar = commaSeparatedString.split(",");
        List<String> listStr = Arrays.asList(ar);
        for (String str : listStr) {
            if (!str.isEmpty()) {
                long k = Long.parseLong(str.trim());
                list.add(k);
            }
        }
        return list;
    }


    public void showItemsFromShowedby(ArrayList<Long> list) {
        for (long key : list) {
            LimbikaView v = alltheViews.get(key);
            v.setVisibility(View.VISIBLE);
        }
    }

    public void hideItemsFromShowedby(ArrayList<Long> list) {
        for (long key : list) {
            LimbikaView v = alltheViews.get(key);
            if(v!=null)
            v.setVisibility(View.INVISIBLE);
        }
    }





    /* ************************************** Read section **************************************/


    public boolean isListening = false;
    String recognizedText = null;

    Item item = null;
    Item item1 = null;
    LimbikaView limbiakview1;
    LimbikaViewItemValue limbikaViewItemValue = null;
    String savedText = null;

    public void doneListening() {

        this.item = null;
        this.item1 = null;
        limbiakview1 = null;
        savedText = null;
        this.limbikaViewItemValue = null;

        isListening = false;
        ibtnMicroPhone.setVisibility(View.GONE);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
            if (readMode != null) {
                readMode.speechOnPause();

            }


    }




    /* *************************  SEQUENCE WORKS **************************  */
    String sequenceText = null;
    ArrayList<Long> sequentialList = new ArrayList<>();
    //  sequence count will always be zero
    int sequenceCount = 0;

    public void preparationForSequential(Task task){
        //some preparation if the task is sequential
        if (task.getType().equals(TaskType.Sequence)) {
            sequenceText = task.getSequenceText();

            if (sequenceText != null) {
                sequentialList = formatList(sequenceText);
                if (sequentialList == null)
                    sequentialList = new ArrayList<>();
            }
            //this will do the magic where everything will work
            // like Sequenvce but mechanism will be like general
            positiveResult = sequentialList;
        }
    }

    public void clearSequence() {
        sequenceText = null;
        if (sequentialList != null)
            sequentialList.clear();
        sequenceCount = 0;
    }

    //general mode work:: checking the view is in positiveAnswer list
    boolean ifInthePositiveListSequence(long key) {
        boolean res = false;
        if (positiveResult.size() > 0)
            if (positiveResult.get(sequenceCount) == key) {
                res = true;
                positiveResult.remove(sequenceCount);
            }
        return res;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(activity, TaskPackActivity.class);
        startActivity(intent);
        finish();
    }


/* *********************** WRITE TEXT ***************************************************** */

}
