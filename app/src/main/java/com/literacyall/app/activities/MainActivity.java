package com.literacyall.app.activities;


//This activity is the editor activity. It contains TaskEditorFragment. This activity and fragment manages the task editor.

//1. Every item is created by using "Limbikaview"
//2. A task contains several items.
//3. 4 drawers are there to manage the application LEFT - TOP - BOTTOM - RIGHT
//4. Drawers are open based on necessity.
//5. Left drawers are Item Categories
//6. Right drawers are to manage the items
//7. Top is for color selector
//8. Bottom drawer is for displaying all the tasks, Save, Delete, Re-Ordering
/**
 * example for add item column goto dao-->Generator --> Add Column showedByTarget -->Build -->Database Manager > loadTaskWiseItem()  add showedByTarget put in a Items array for Task wise Items
 * --> for Share(Export) or Import got to Share>generateTaskPackJSON showedByTarget put this itemObject for Share(Export) or readSharedTaskPackJSONtoDatabase   item setShowedByTarget
 * /
 * /*All queries are managed inside IDatabaseManager
 */

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.dmk.limbikasdk.views.LimbikaView;
import com.literacyall.app.Dialog.DialogNavBarHide;
import com.literacyall.app.Dialog.FeedbackEditorDialog;
import com.literacyall.app.Dialog.ImageSearchCustomDialog;
import com.literacyall.app.Dialog.ImageSelectionDialog;
import com.literacyall.app.Dialog.ListInstalledAppsDialog;
import com.literacyall.app.Dialog.SequenceManageDialog;
import com.literacyall.app.Dialog.TaskGridDialog;
import com.literacyall.app.R;
import com.literacyall.app.adapter.FontAdapter;
import com.literacyall.app.adapter.GifAdapter;
import com.literacyall.app.adapter.SoundDelayAdapter;
import com.literacyall.app.customui.BottomDrawerCustomeView;
import com.literacyall.app.customui.CircularTextView;
import com.literacyall.app.customui.TopDrawerCustomView;
import com.literacyall.app.dao.Item;
import com.literacyall.app.dao.Task;
import com.literacyall.app.fragment.TaskEditorFragment;
import com.literacyall.app.listener.OnDoubleTapListener;
import com.literacyall.app.logging.L;
import com.literacyall.app.manager.DatabaseManager;
import com.literacyall.app.manager.IDatabaseManager;
import com.literacyall.app.utilities.Animanation;
import com.literacyall.app.utilities.ApplicationMode;
import com.literacyall.app.utilities.CustomList;
import com.literacyall.app.utilities.CustomToast;
import com.literacyall.app.utilities.ExceptionHandler;
import com.literacyall.app.utilities.FileProcessing;
import com.literacyall.app.utilities.ImageProcessing;
import com.literacyall.app.utilities.InternalStorageContentProvider;
import com.literacyall.app.utilities.RegularExpCheck;
import com.literacyall.app.utilities.StaticAccess;
import com.literacyall.app.utilities.TaskType;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class MainActivity extends BaseActivity implements OnDoubleTapListener.GestureListener_Custom, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    int intent_source = 0;
    ImageProcessing imgProc;
    int minimumVal = 2;
    MainActivity activity;
    String urlTag = StaticAccess.HTTP;
    int errorMandatory = 0;
    String pro_pic;

    Boolean setImage = false;
    private static final int SELECT_PICTURE = 0x1;
    private static final int SELECT_PICTURE_ERROR = 0x101;
    private static final int SELECT_PICTURE_FEEDBACK = 0x102;
    private static final int CAMERA_REQUEST = 0x1;
    private static final int FILE_CHOOSER = 0x1;
    private static final int MATERIAL_FILE_PICKER = 0x3;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x8;
    public static final int REQUEST_CODE_TAKE_PICTURE_ERROR = 0x108;
    public static final int REQUEST_CODE_TAKE_PICTURE_FEEDBACK = 0x109;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x7;
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public int CURRENT_FILE_PICKER_ITEM;
    public int CURRENT_ANIMATION_ITEM;
    public ImageSearchCustomDialog imageSearchDialog = null;
    Bitmap preview_bitmap;
    private String appImagePath = null;
    public File mFileTemp;
    public boolean isImageSearchDialogShow = false;
    /*
 * when isTaskErrorPic  is true that time Task Error Image Picker Will be Fire
 * when isTaskErrorPic  is false that time Item Image Picker Will be Fire
 */
    public boolean isTaskErrorPic = false;
    public boolean isFeedbackImages = false;
    boolean isCircularImage = false;
    public String currentType;
    private Long currentItem;

    public long taskId, taskPackId;
    //String getTaskPackTaskType;
    FileProcessing fileProcessing;
    View view;
    LinearLayout llBottomDrawer, llTopDrawer;
    BottomDrawerCustomeView bottomDrawerCustomeView;
    TopDrawerCustomView topDrawerCustomView;
    LinearLayout llBottomDrawerItem, llTopDrawerItem, lnGeneralCorrect, lnGeneralInCorrect, lnDrag, lnAssistive, lnTarget, lnChild, lnReadText, lnWriteText, llSequenceText;
    OnDoubleTapListener touchListener;
    TaskEditorFragment taskEditorFragment;
    public View currentRightDrawer = null;
    public int taskBackgroundColor;

    ArrayList<BottomDrawerCustomeView> bottomDrawerCustomeViews = new ArrayList<>();
    ArrayList<TopDrawerCustomView> topDrawerCustomViews = new ArrayList<>();
    ArrayList<Task> tasks;


    boolean isBottomDrawerShown = false, isTopDrawerShown = false, isLeftDrawerShown = false, isRightDrawerShown = false, isGridShown = false;

    int textSize = 0;
    int color;
    int fontValue = 0;
    int soundDelayValue = 0;
    private IDatabaseManager databaseManager;
    private int mScrollDistance;
    View incLeftDrawerEditor, incRightDrawerEditorEdit, incRightDrawerEditorForm, incRightDrawerEditorAlign, incRightDrawerEditorScreenAlign,
            incRightDrawerEditorInfo, incRightDrawerEditorPosition, incRightDrawerEditorError,
            incRightDrawerEditorText, incWidgetArrangement;

    SeekBar seekBarText;
    public ImageSelectionDialog imageSelectionDialog;
    public boolean isMainBgColor = false, isItemBgColor = false, isErrorBgColor = false, isItemTextColor = false, isItemBorderColor = false, isDragDropMode = false, isSendToBack = false,
            isSendToFront = false, isSendToBackMost = false, isSendToFrontMost = false, isBorderCorner = false, isShowedBy = false, isHideBy = false;
    public int selectedAlignment;
    public String taskType;
    String filePath, soundFileName;

    public FloatingActionButton fbtnShowLeftDrawer;
    FrameLayout flContentView;

    ImageButton ibtnBack, ibtnSquareAdd, ibtnCircleAdd, ibtnEdit, ibtnForm, ibtnInfo, ibtnCoppyPast, ibtnAlign, ibtnScreenAlign,
            ibtnPosition, ibtnAnimation, ibtnError, ibtnGridOn, ibtnSound, ibtnFeedBack, ibtnShowTopDrawer, ibtnShowBottomDrawer, ibtnSequenceText;
    ImageButton ibtnURLEdit, ibtnAddImageEdit, ibtnAddTextEdit, ibtnItemSoundEdit, ibtnReadText, ibtnWriteText;
    Switch swCloseAppEdit, swNavigatesEdit, swOpenAppEdit;
    //EditText edtURLEdit;
    ImageButton ibtnBorderpxForm, ibtnDoneForm;
    TextView tvBorderpxForm;
    CircularTextView tvBgColorForm, tvBorderColorForm, tvBgColorError;
    TextView tvError;
    Switch swRoundCornerForm;
    ImageButton ibtnDragDropInfo, ibtnLinkdToInfo, ibtnDelaySoundInfo;
    TextView tvDelaySoundInfo;
    public Switch swCorrectInfo, swIncorrectInfo, swTargetInfo, swChildInfo, swShowedByInfo, swHiddenByInfo, swAutoplayInfo, swMandatoryInfo;
    ImageButton ibtnAlignCenterAlign, ibtnAlignLeftAlign, ibtnAlignRightAlign, ibtnAlignBottomAlign, ibtnAlignTopAlign,
            ibtnAlignXCenter, ibtnAlignYCenter;

    ImageButton ibtnAlignXScreenCenter, ibtnAlignYScreenCenter, ibtnAlignScreenCenterAlign,
            ibtnAlignScreenTopAlign, ibtnAlignScreenBottomAlign, ibtnAlignScreenLeftAlign, ibtnAlignScreenRightAlign;

    ImageButton ibtnFrontTopPosition, ibtnBackTopPosition, ibtnFrontStepPosition, ibtnBackStepPosition;
    ImageButton ibtnAddImageError, ibtnAddTextError, ibtnDoneError;
    ImageView ivImageError;
    ImageButton ibtnAlignLeftText, ibtnAlignCenterText, ibtnAlignAllText, ibtnAlignRightText, ibtnText, ibtnFontText, ibtnDoneText;
    CircularTextView tvShadowText;
    TextView tvFontText, tvText, tvReadText, tvWriteText;
    EditText edtURLText, edtText, edtTextEdit, edtWriteEdit;

    ImageButton IBSlideToBottom;
    ImageButton ibtnBottomCopyPaste, ibtnBottomNew, ibtNewSameTaskMode, ibtnBottomSave, ibtnBottomDelete, ibtnBottomPreview;
    ImageButton ibtnDragAndDrop, ibtnGeneral, ibtnAssistiveTuch;

    TextView tvPositive, tvNegative, tvFeedBack, tvAnimPositive, tvAnimNegative, tvAnimFeedBack, tvItemSound, tvReadable, tvWriteable;

    // for Sound
    MediaPlayer mp;

    // by defualt text center alignment
    int fontAlign = LimbikaView.ALIGN_CENTER;

    private ProgressDialog pDialog;
    FeedbackEditorDialog feedbackEditorDialog;
    ArrayList<String> feedBackImages;
    /*
     * when isItemImage  is true that time Task Error Image Picker Will be Fire
     * when isItemImage  is false that time Item Image Picker Will be Fire
     */
    Boolean isItemImage = false;

    /*
 * when itemPicFlag  is 0 that time  Image Picker Will be execute for Item
 * when itemPicFlag  is 1that time  Image Picker Will be execute for Task Error
 */
    int itemPicFlag = 0;
    int TaskErrorFlag = 1;
    public int feedbackImageFlag = 2;
    // its use for only new Task its value replace in taskTyp
    //String taskTypeNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        databaseManager = new DatabaseManager(this);

        if (getIntent().getExtras() != null) {
            taskPackId = getIntent().getLongExtra(StaticAccess.INTENT_TASK_PACK_ID, -1);
            taskId = getIntent().getLongExtra(StaticAccess.INTENT_TASK_ID, -1);
            taskType = getIntent().getStringExtra(StaticAccess.INTENT_TASK_TYPE);
        }

        activity = this;
        checkScreenDensity();
        loadFragment();
        //targetEnable();

        imgProc = new ImageProcessing(activity);
        appImagePath = imgProc.getImageDir();
        llTopDrawer = (LinearLayout) findViewById(R.id.llTopDrawer);
        llBottomDrawer = (LinearLayout) findViewById(R.id.llBottomDrawer);


        findViewByIdListenerCOLOR();

        // Load Specific task for Edit
//        if (taskType == null) {
        tasks = (ArrayList<Task>) databaseManager.listTasksByTAskPackId(taskPackId);
        if (tasks.size() > 0) {
            taskType = tasks.get(0).getType();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (taskType.equals(TaskType.Normal)) {
                        fbtnShowLeftDrawer.setImageResource(R.drawable.ic_general_mode);
                        // CustomToast.t(activity, getResources().getString(R.string.selectedGeneral));
                        setGeneralEnable(true);
                        setDragDropEnable(false);
                        setAssistiveEnable(false);
                        setReadEnable(false);
                        setWriteEnable(false);
                        setSequenceEnable(false);
                        ibtnEdit.performClick();
                    } else if (taskType.equals(TaskType.DragDrop)) {
                        // resetViews();
                        //CustomToast.t(activity, getResources().getString(R.string.selectedDragDrop));
                        fbtnShowLeftDrawer.setImageResource(R.drawable.ic_info_drag_drop);

                        setGeneralEnable(false);
                        setAssistiveEnable(false);
                        setDragDropEnable(true);
                        setReadEnable(false);
                        setWriteEnable(false);
                        setSequenceEnable(false);
                        ibtnEdit.performClick();

                    } else if (taskType.equals(TaskType.Assistive)) {
                        // resetViews();
                        //CustomToast.t(activity, getResources().getString(R.string.selectedAssistiveTouch));
                        fbtnShowLeftDrawer.setImageResource(R.drawable.ic_info_showed_by);
                        setGeneralEnable(false);
                        setDragDropEnable(false);
                        setAssistiveEnable(true);
                        setReadEnable(false);
                        setWriteEnable(false);
                        setSequenceEnable(false);
                        ibtnEdit.performClick();
                    } else if (taskType.equals(TaskType.Read)) {
                        // resetViews();
                        //CustomToast.t(activity, getResources().getString(R.string.selectedAssistiveTouch));
                        fbtnShowLeftDrawer.setImageResource(R.drawable.ic_info_showed_by);
                        setGeneralEnable(true);
                        setDragDropEnable(false);
                        setAssistiveEnable(false);
                        setReadEnable(true);
                        setWriteEnable(false);
                        setSequenceEnable(false);
                        ibtnEdit.performClick();
                    } else if (taskType.equals(TaskType.Write)) {

                        fbtnShowLeftDrawer.setImageResource(R.drawable.ic_info_showed_by);
                        setGeneralEnable(true);
                        setDragDropEnable(false);
                        setAssistiveEnable(false);
                        setReadEnable(false);
                        setWriteEnable(true);
                        setSequenceEnable(false);
                        ibtnEdit.performClick();

                    } else if (taskType.equals(TaskType.Sequence)) {
                        // resetViews();
                        //CustomToast.t(activity, getResources().getString(R.string.selectedAssistiveTouch));
                        fbtnShowLeftDrawer.setImageResource(R.drawable.ic_info_showed_by);
                        setGeneralEnable(true);
                        setDragDropEnable(false);
                        setAssistiveEnable(false);
                        setReadEnable(false);
                        setWriteEnable(false);
                        setSequenceEnable(true);
                        ibtnEdit.performClick();
                    }
                    taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
                    taskEditorFragment.loadTask(tasks.get(0));
                }
            }, 100);

        } else {

            // create task new
            if (taskType != null)
                if (taskType.equals(TaskType.DragDrop)) {
                    //CustomToast.t(activity, getResources().getString(R.string.selectedDragDrop));
                    fbtnShowLeftDrawer.setImageResource(R.drawable.ic_info_drag_drop);
//drag
                    setGeneralEnable(false);
                    setAssistiveEnable(false);
                    setDragDropEnable(true);
                    setReadEnable(false);
                    setWriteEnable(false);
                    setSequenceEnable(false);
                } else if (taskType.equals(TaskType.Normal)) {

                    fbtnShowLeftDrawer.setImageResource(R.drawable.ic_general_mode);
                    //CustomToast.t(activity, getResources().getString(R.string.selectedGeneral));
                    setGeneralEnable(true);
                    setDragDropEnable(false);
                    setAssistiveEnable(false);
                    setReadEnable(false);
                    setWriteEnable(false);
                    setSequenceEnable(false);
                } else if (taskType.equals(TaskType.Assistive)) {
                    // resetViews();
                    //CustomToast.t(activity, getResources().getString(R.string.selectedAssistiveTouch));
                    fbtnShowLeftDrawer.setImageResource(R.drawable.ic_info_showed_by);
                    setGeneralEnable(false);
                    setDragDropEnable(false);
                    setAssistiveEnable(true);
                    setReadEnable(false);
                    setWriteEnable(false);
                    setSequenceEnable(false);

                } else if (taskType.equals(TaskType.Read)) {

                    fbtnShowLeftDrawer.setImageResource(R.drawable.ic_general_mode);
                    //CustomToast.t(activity, getResources().getString(R.string.selectedGeneral));
                    setGeneralEnable(true);
                    setDragDropEnable(false);
                    setAssistiveEnable(false);
                    setReadEnable(true);
                    setWriteEnable(false);
                    setSequenceEnable(false);
                } else if (taskType.equals(TaskType.Write)) {

                    fbtnShowLeftDrawer.setImageResource(R.drawable.ic_general_mode);
                    //CustomToast.t(activity, getResources().getString(R.string.selectedGeneral));
                    setGeneralEnable(true);
                    setDragDropEnable(false);
                    setAssistiveEnable(false);
                    setReadEnable(false);
                    setWriteEnable(true);
                    setSequenceEnable(false);
                } else if (taskType.equals(TaskType.Sequence)) {

                    fbtnShowLeftDrawer.setImageResource(R.drawable.ic_general_mode);
                    //CustomToast.t(activity, getResources().getString(R.string.selectedGeneral));
                    setGeneralEnable(true);
                    setDragDropEnable(false);
                    setAssistiveEnable(false);
                    setReadEnable(false);
                    setWriteEnable(false);
                    setSequenceEnable(true);
                }
        }

    }


    @Override
    public void onBackPressed() {
        {
            //super.onBackPressed();
            // System.exit(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_openRight) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Show Bottom Drawer
    public void showBottomDrawer(final View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0, 0, view.getHeight(), 0);
        animate.setDuration(500);
        animate.setFillAfter(true);

        animate.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);
    }


    public void opendBottomDrawerHide() {
        if (isBottomDrawerShown) {
            hideBottomDrawer(llBottomDrawer);
            isBottomDrawerShown = false;
        }
    }


    // Hide Bottom Drawer
    public void hideBottomDrawer(final View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);

        animate.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    /************************************************
     * Bottom drawer
     ************************************************************/
    // Create / Refresh bottom drawer
    private void createBottomDrawer() {
        tasks = (ArrayList<Task>) databaseManager.listTasksByTAskPackId(taskPackId);
        if (tasks != null)
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                bottomDrawerCustomeView = new BottomDrawerCustomeView(activity);
                bottomDrawerCustomeView.setIds(i);
                bottomDrawerCustomeView.setTextId(i);
                bottomDrawerCustomeView.setText();
                bottomDrawerCustomeView.setImage(task.getTaskImage());
                bottomDrawerCustomeView.setTaskType(task.getType()); //add reaz
                bottomDrawerCustomeView.setTask(task);
                bottomDrawerCustomeView.setTag("Bottom");
                bottomDrawerCustomeView.setOnDragListener(new ChoiceDragListener());
                bottomDrawerCustomeView.setOnTouchListener(touchListener);
                bottomDrawerCustomeViews.add(bottomDrawerCustomeView);
            }
    }

    /**
     * load the view
     **/
    @SuppressLint("NewApi")
    public void reGenerateBottomDrawer() {
        llBottomDrawerItem.removeAllViews();
        ArrayList<BottomDrawerCustomeView> view_temp = new ArrayList<BottomDrawerCustomeView>();

        for (int i = 0; i < bottomDrawerCustomeViews.size(); i++) {
            BottomDrawerCustomeView v = bottomDrawerCustomeViews.get(i); // updating ids of custom view
            v.setIds(i);
            v.setTextId(v.getTextIds());
            v.setText();
            v.setImage(v.getImage());
            v.setTaskType(v.getTaskType());//add reaz
            v.setOnDragListener(new ChoiceDragListener());
            v.setOnTouchListener(touchListener);
            v.setTask(v.getTask());

            view_temp.add(v);
            llBottomDrawerItem.addView(v);
        }
        bottomDrawerCustomeViews.clear();
        bottomDrawerCustomeViews = view_temp;
    }

    class EditTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(getString(R.string.taskLoading));
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        @Override
        protected String doInBackground(String... params) {
            reGenerateBottomDrawer();
            generateTopDrawer();
            switcherListener();
            createBottomDrawer();

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (pDialog.isShowing()) {
                pDialog.dismiss();

            }
        }


    }

    /**
     * Generate Top Drawer
     **/
    @SuppressLint("NewApi")
    public void generateTopDrawer() {
        llTopDrawerItem.removeAllViews();
        int[] colorPicker = getResources().getIntArray(R.array.colorPicker);
        ArrayList<TopDrawerCustomView> view_temp = new ArrayList<>();

        for (int i = 0; i < colorPicker.length; i++) {
            TopDrawerCustomView v = topDrawerCustomViews.get(i); // updating ids of custom view
            v.setBackground(colorPicker[i]);

            v.setTopColorPic(i);
            view_temp.add(v);
            llTopDrawerItem.addView(v);

        }
        topDrawerCustomViews.clear();
        topDrawerCustomViews = view_temp;
    }

    /**
     * calculating the positions after drag
     **/
    public void calculateDrag(int pos_whichviewDragging, int pos_whereDropped) {
        BottomDrawerCustomeView viewDragging = bottomDrawerCustomeViews.get(pos_whichviewDragging);
        BottomDrawerCustomeView viewDropped = bottomDrawerCustomeViews.get(pos_whereDropped);
        bottomDrawerCustomeViews.remove(pos_whichviewDragging);
        bottomDrawerCustomeViews.add(pos_whereDropped, viewDragging);
        for (BottomDrawerCustomeView v : bottomDrawerCustomeViews) {
            Log.e("Allids:", String.valueOf(v.getIds()));
        }

        CustomToast.M(getResources().getString(R.string.CalculateDrag), getResources().getString(R.string.called));
        CustomToast.t(this, getResources().getString(R.string.Deletecell) + pos_whichviewDragging + getResources().getString(R.string.NewCell) + pos_whereDropped);
        reGenerateBottomDrawer();

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


        textSize = progress;
        // seekBar.setProgress(progress);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {


    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {


    }

    //********************************This is the top bottom listener******************************////
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private final class ChoiceTouchListener implements View.OnTouchListener {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @SuppressLint("NewApi")
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                return false;
            } else {
                return false;
            }
        }
    }

    //********************************This is the top bottom listener******************************////
    @SuppressLint("NewApi")
    private class ChoiceDragListener implements View.OnDragListener {

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @SuppressLint("NewApi")
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    View view = (View) event.getLocalState();
                    BottomDrawerCustomeView droppeing = (BottomDrawerCustomeView) view; // the view which
                    // was dragged
                    int droppingid = droppeing.getIds();
                    BottomDrawerCustomeView dropTarget = (BottomDrawerCustomeView) v; // where the view is
                    // droopeed
                    int dropTargetid = dropTarget.getIds();
                    calculateDrag(droppingid, dropTargetid);

                    Task task = tasks.get(droppingid);

                    if (droppingid > dropTargetid) {
                        droppingid++;
                    } else {
                        dropTargetid++;
                    }
                    if (task != null) {
                        tasks.add(dropTargetid, task);
                        tasks.remove(droppingid);

                        databaseManager.deleteTasksByTaskPack(task.getTaskPackId());
                        for (int i = 0; i < tasks.size(); i++) {
                            Task task1 = tasks.get(i);
                            task1.setSlideSequence(i + 1);
                            long aa = databaseManager.insertTask(task1);
                        }
                    }
                    touchListener.disable_drag();
                    break;
            }
            return true;

        }
    }

    @Override
    public void onDoubleTap(MotionEvent e, View v) {
        CustomToast.t(this, getResources().getString(R.string.OnDoubleTap));

    }

    @Override
    public void onSingleTapListener(MotionEvent e, View v) {

        if (v.getTag().equals("Top")) {
            TopDrawerCustomView topDrawerCustomView = (TopDrawerCustomView) v;
            color = topDrawerCustomView.getColor();
            //btntvShadowColor = topDrawerCustomView.getColor();

            if (isMainBgColor) {
                changeAppBackground(color);
                isMainBgColor = false;
                taskBackgroundColor = color;
            } else if (isItemBgColor) {
                isItemBgColor = false;
                tvBgColorForm.setSolidColor(color);
            } else if (isErrorBgColor) {
                isErrorBgColor = false;
                tvBgColorError.setSolidColor(color);
            } else if (isItemTextColor) {
                isItemTextColor = false;
                tvShadowText.setSolidColor(color);
            } else if (isItemBorderColor) {
                tvBorderColorForm.setSolidColor(color);
                isItemBorderColor = false;
            }

            if (isTopDrawerShown) {
                hideTopDrawer(llTopDrawer);
                isTopDrawerShown = false;
            }

        }
        //**********************************************Bottom drawer listener reaz ***************************************************
        else if (v.getTag().equals("Bottom")) {
//            if()
            BottomDrawerCustomeView bottomDrawerCustomeView = (BottomDrawerCustomeView) v;
            taskEditorFragment.clearItems();
            taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
//            taskEditorFragment.loadTask(bottomDrawerCustomeView.getTask());

            if (bottomDrawerCustomeView.getTaskType().equals(TaskType.Normal)) {
                taskType = TaskType.Normal;
                //resetViews();
                fbtnShowLeftDrawer.setImageResource(R.drawable.ic_general_mode);
                // CustomToast.t(activity, getResources().getString(R.string.selectedGeneral));
                setGeneralEnable(true);
                setDragDropEnable(false);
                setAssistiveEnable(false);
                setReadEnable(false);
                setWriteEnable(false);
                taskEditorFragment.loadTask(bottomDrawerCustomeView.getTask());

                ibtnEdit.performClick();

            } else if (bottomDrawerCustomeView.getTaskType().equals(TaskType.DragDrop)) {

                taskType = TaskType.DragDrop;
                // resetViews();
                //CustomToast.t(activity, getResources().getString(R.string.selectedDragDrop));
                fbtnShowLeftDrawer.setImageResource(R.drawable.ic_info_drag_drop);
                setGeneralEnable(false);
                setAssistiveEnable(false);
                setDragDropEnable(true);
                setReadEnable(false);
                setWriteEnable(false);
                taskEditorFragment.loadTask(bottomDrawerCustomeView.getTask());

                ibtnEdit.performClick();

            } else if (bottomDrawerCustomeView.getTaskType().equals(TaskType.Assistive)) {
                taskType = TaskType.Assistive;
                // resetViews();
                //CustomToast.t(activity, getResources().getString(R.string.selectedAssistiveTouch));
                fbtnShowLeftDrawer.setImageResource(R.drawable.ic_info_showed_by);
                taskEditorFragment.loadTask(bottomDrawerCustomeView.getTask());
                setGeneralEnable(false);
                setDragDropEnable(false);
                setAssistiveEnable(true);
                setReadEnable(false);
                setWriteEnable(false);
                ibtnEdit.performClick();

            } else if (bottomDrawerCustomeView.getTaskType().equals(TaskType.Read)) {
                taskType = TaskType.Read;
                // resetViews();
                //CustomToast.t(activity, getResources().getString(R.string.selectedAssistiveTouch));
                fbtnShowLeftDrawer.setImageResource(R.drawable.ic_info_showed_by);
                taskEditorFragment.loadTask(bottomDrawerCustomeView.getTask());
                setGeneralEnable(true);
                setDragDropEnable(false);
                setAssistiveEnable(false);
                setReadEnable(true);
                setWriteEnable(false);
                ibtnEdit.performClick();

            } else if (bottomDrawerCustomeView.getTaskType().equals(TaskType.Write)) {
                taskType = TaskType.Write;
                // resetViews();
                //CustomToast.t(activity, getResources().getString(R.string.selectedAssistiveTouch));
                fbtnShowLeftDrawer.setImageResource(R.drawable.ic_info_showed_by);
                taskEditorFragment.loadTask(bottomDrawerCustomeView.getTask());
                setGeneralEnable(true);
                setDragDropEnable(false);
                setAssistiveEnable(false);
                setReadEnable(false);
                setWriteEnable(true);
                ibtnEdit.performClick();

            } else if (bottomDrawerCustomeView.getTaskType().equals(TaskType.Sequence)) {
                taskType = TaskType.Sequence;
                // resetViews();
                //CustomToast.t(activity, getResources().getString(R.string.selectedAssistiveTouch));
                fbtnShowLeftDrawer.setImageResource(R.drawable.ic_info_showed_by);
                taskEditorFragment.loadTask(bottomDrawerCustomeView.getTask());
                setGeneralEnable(true);
                setDragDropEnable(false);
                setAssistiveEnable(false);
                setReadEnable(false);
                setWriteEnable(false);
                ibtnEdit.performClick();

            }


        }


    }

    @Override
    public void onLongClick(MotionEvent e, View v) {

        Vibrator vib = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        vib.vibrate(500);

    }

    @Override
    public void onDown(MotionEvent e, View v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFling_to_Close() {
        // TODO Auto-generated method stub

    }


    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        //getSupportActionBar().hide();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            if (hasFocus) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

    // Show Top Drawer
    public void showTopDrawer(final View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0, 0, -view.getHeight(), 0);
        animate.setDuration(500);
        animate.setFillAfter(true);

        animate.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);

    }

    // Hide Bottom Drawer
    public void hideTopDrawer(final View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, -view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);

        animate.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    ////////////////////////////////////////////////****DRAWER WORKS ARE DONE *****************////////////////////////
    private void loadFragment() {
        taskEditorFragment = new TaskEditorFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transactionMonth = fragmentManager.beginTransaction();
        transactionMonth.addToBackStack(null);
        transactionMonth.replace(R.id.flContentView, taskEditorFragment, "TaskEditorFragment").commit();

    }

    /**
     * onclick listener
     *********************************************************************************************************/
    @Override
    public void onClick(View v) {
        taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
        switch (v.getId()) {
            case R.id.flContentView:
                if (isBottomDrawerShown) {
                    hideBottomDrawer(llBottomDrawer);
                    isBottomDrawerShown = false;
                }

                if (isTopDrawerShown) {
                    hideTopDrawer(llTopDrawer);
                    isTopDrawerShown = false;
                }

                if (isLeftDrawerShown) {
                    if (incLeftDrawerEditor.getVisibility() == View.VISIBLE)
                        hideLeftmDrawer(incLeftDrawerEditor);
                    isLeftDrawerShown = false;
                    fbtnShowLeftDrawer.setVisibility(View.VISIBLE);
                }

                if (isRightDrawerShown) {
                    hideRightmDrawer(currentRightDrawer);
                    isRightDrawerShown = false;
                }


                break;


            /*********************************************** 1 Left Drawer Options Start ****************************************/
            case R.id.fbtnShowLeftDrawer:
                showLeftDrawer(incLeftDrawerEditor);
                isLeftDrawerShown = true;
                // buttonShowHide(1);
                fbtnShowLeftDrawer.setVisibility(View.INVISIBLE);
                break;


            //1.1
            case R.id.ibtnBack:

                if (taskEditorFragment.itemMap.size() > 0) {
                    if (taskEditorFragment.itemMap.size() > 0) {
                        taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
                        if (taskEditorFragment.saveTask() != 0) {
                      /*  bottomDrawerCustomeViews.clear();
                        createBottomDrawer();
                        reGenerateBottomDrawer();


                       */

                            Intent intent = new Intent(activity, TaskPackActivity.class);
                            intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, taskPackId);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Intent intent = new Intent(activity, TaskPackActivity.class);
                        intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, taskPackId);
                        startActivity(intent);
                        finish();

                    }
                } else {
                    CustomToast.t(activity, getResources().getString(R.string.addItem));
                }
                // dialogBackPermission();

                break;


            //1.2
            case R.id.ibtnSquareAdd:

                taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
                taskEditorFragment.setSquareBlank();
                fbtnShowLeftDrawer.performClick();
                ibtnEdit.performClick();
                break;

            //1.3
            case R.id.ibtnCircleAdd:

                taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
                taskEditorFragment.setCircleBlank();
                fbtnShowLeftDrawer.performClick();
                ibtnEdit.performClick();
                break;


            //1.4
            case R.id.ibtnEdit:

                if (taskEditorFragment.currentKey != null) {
                    if (currentRightDrawer != null) {
                        hideRightmDrawer(currentRightDrawer);
                    }
                    showRightDrawer(incRightDrawerEditorEdit);
                    isRightDrawerShown = true;
                    currentRightDrawer = incRightDrawerEditorEdit;

                    Item item = taskEditorFragment.getItemFromKey(taskEditorFragment.currentKey);
                    tvReadText.setText(item.getReadText() == null ? "" : item.getReadText());
                    tvWriteText.setText(item.getWriteText() == null ? "" : item.getWriteText());


                   /* if (currentItem != null)
                        taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
                    final Item i = taskEditorFragment.getItemFromKey(taskEditorFragment.currentKey);
                    edtWriteEdit.setText(i.getWriteText() == null ? "" : i.getWriteText());*/
                    //tvWriteText.setText(i.getWriteText() == null ? "" : i.getWriteText());


                    if (item != null) {
                        // Setting Navigate To
                        swNavigatesEdit.setChecked(item.getNavigateTo() > 0 ? true : false);

                        //Setting Open App
                        if (item.getOpenApp() != null) {
                            swOpenAppEdit.setChecked(item.getOpenApp().toString().length() > 0 ? true : false);
                        } else {
                            swOpenAppEdit.setChecked(false);
                        }
//                        tvReadText.setText(item.getReadText());
                        //Setting Close App
                        swCloseAppEdit.setChecked(item.getCloseApp() == 1 ? true : false);
                    }
                }
                break;

            //1.5
            case R.id.ibtnForm:
                if (taskEditorFragment.currentKey != null) {
                    if (currentRightDrawer != null) {
                        hideRightmDrawer(currentRightDrawer);
                    }
                    showRightDrawer(incRightDrawerEditorForm);
                    isRightDrawerShown = true;
                    currentRightDrawer = incRightDrawerEditorForm;

                    Item item = taskEditorFragment.getItemFromKey(taskEditorFragment.currentKey);
                    if (item != null) {

                        swNavigatesEdit.setChecked(item.getNavigateTo() > 0 ? true : false);

                        if (item.getBackgroundColor() != null) {
                            tvBgColorForm.setSolidColor(item.getBackgroundColor() == -15066598 ? Color.BLACK : item.getBackgroundColor());
                        }
                        if (item.getBorderColor() != null)

                            tvBorderColorForm.setSolidColor(item.getBorderColor() == -15066598 ? Color.BLACK : item.getBorderColor());
                        if (item.getCornerRound() != 0) {
                            swRoundCornerForm.setChecked(true);
                            isBorderCorner = true;
                        } else {
                            swRoundCornerForm.setChecked(false);
                            isBorderCorner = false;
                        }


                        tvBorderpxForm.setText(String.valueOf(item.getBorderPixel() == 0 ? 0 : item.getBorderPixel()));


                     /*   } else {
                            swOpenAppEdit.setChecked(false);
                        }*/
                    }
                }

                break;

            //1.6
            case R.id.ibtnInfo:
                if (taskEditorFragment.currentKey != null) {

                    if (currentRightDrawer != null) {
                        hideRightmDrawer(currentRightDrawer);
                    }
                    showRightDrawer(incRightDrawerEditorInfo);
                    isRightDrawerShown = true;
                    currentRightDrawer = incRightDrawerEditorInfo;

                    Item item = taskEditorFragment.getItemFromKey(taskEditorFragment.currentKey);
                    if (item != null) {
                        // Setting Results
                        if (item.getResult() != null && !item.getResult().equals("")) {
                            swCorrectInfo.setChecked(item.getResult().equals(TaskType.NORMAL_TRUE) ? true : false);
                            swIncorrectInfo.setChecked(item.getResult().equals(TaskType.NORMAL_FALSE) ? true : false);
                        } else {
                            swCorrectInfo.setChecked(false);
                            swIncorrectInfo.setChecked(false);
                        }

                        swShowedByInfo.setChecked(item.getShowedBy() == 1 ? true : false);
                        swHiddenByInfo.setChecked(item.getHideBy() == 1 ? true : false);

//                        asdasdasdasdasd
//                        swAutoplayInfo.setChecked(item.getAutoPlay() == 1 ? true : false);
                    }

//                    setTvDelayMode(item.getSoundDelay());

                    if (taskType.equals(TaskType.DragDrop)) {

                        /*if (taskEditorFragment.checkTarget()) {
                            swTargetInfo.setChecked(true);
                        } else {
                            swTargetInfo.setChecked(false);
                        }
                        if (taskEditorFragment.checkChild()) {
                            swChildInfo.setChecked(true);
                        } else {
                            swChildInfo.setChecked(false);
                        }*/
                        targetChildEnable();
                        ibtnLinkdToInfo.setEnabled(false);
                    } else if (taskType.equals(TaskType.Assistive)) {
                        targetChildEnable();
                        ibtnDragDropInfo.setEnabled(false);

                    }
                }

                break;

            //1.7
            case R.id.ibtnCoppyPast:
                if (taskEditorFragment.currentKey != null) {
                    if (taskEditorFragment.itemMap.size() > 0) {
                        taskEditorFragment.copyPaste();
                        ibtnEdit.performClick();
                    } else {
                        CustomToast.t(activity, getResources().getString(R.string.addItem));
                    }
                }


                break;

            //1.8
            case R.id.ibtnAlign:
                if (taskEditorFragment.currentKey != null) {
                    if (currentRightDrawer != null) {
                        hideRightmDrawer(currentRightDrawer);
                    }
                    showRightDrawer(incRightDrawerEditorAlign);
                    isRightDrawerShown = true;
                    currentRightDrawer = incRightDrawerEditorAlign;
                }


                break;
            //1.9
            case R.id.ibtnScreenAlign:
                if (taskEditorFragment.currentKey != null) {
                    if (currentRightDrawer != null) {
                        hideRightmDrawer(currentRightDrawer);
                    }
                    showRightDrawer(incRightDrawerEditorScreenAlign);
                    isRightDrawerShown = true;
                    currentRightDrawer = incRightDrawerEditorScreenAlign;
                }
                break;


            case R.id.ibtnSequenceText:
              /*  if (taskEditorFragment.currentKey != null) {
                    if (currentRightDrawer != null) {
                        hideRightmDrawer(currentRightDrawer);
                    }
                    showRightDrawer(incRightDrawerEditorPosition);
                    isRightDrawerShown = true;
                    currentRightDrawer = incRightDrawerEditorPosition;

                }*/

                taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
                ArrayList<CustomList> customLists = new ArrayList<>();
                customLists = taskEditorFragment.getAllCorrectViews();
                ImageProcessing imageProcessing = new ImageProcessing(activity);
                ArrayList<CustomList> sequenceList = new ArrayList<>();
                if (taskEditorFragment.currentTask != null) {
                    if (taskEditorFragment.currentTask.getSequenceText() != null) {
                        String str = taskEditorFragment.currentTask.getSequenceText();
                        ArrayList aList = new ArrayList(Arrays.asList(str.split(",")));
                        for (int i = 0; i < aList.size(); i++) {
                            //System.out.println(" -->" + aList.get(i));
                            if (aList.get(i).toString().length() > 0) {
                                //Long key = Long.valueOf(aList.get(i).toString());

                                CustomList customList = new CustomList();
                                customList.key = Long.valueOf(aList.get(i).toString().replaceAll("\\s+", ""));
                                customList.imageName = imageProcessing.takeScreenshot(taskEditorFragment.allLimbikaViews.get(customList.key));
                                sequenceList.add(customList);
                            }
                        }
                    }
                }

                SequenceManageDialog sequenceManageDialog = new SequenceManageDialog(activity, sequenceList, customLists);
                sequenceManageDialog.setCancelable(false);
                DialogNavBarHide.navBarHide(this, sequenceManageDialog);

                break;

            //1.10
            case R.id.ibtnPosition:
                if (taskEditorFragment.currentKey != null) {
                    if (currentRightDrawer != null) {
                        hideRightmDrawer(currentRightDrawer);
                    }
                    showRightDrawer(incRightDrawerEditorPosition);
                    isRightDrawerShown = true;
                    currentRightDrawer = incRightDrawerEditorPosition;
                }
                break;

            //1.11
            case R.id.ibtnAnimation:
                hideLeftmDrawer(incLeftDrawerEditor);
                setAnimationMode();


                break;

            // 1.12
            case R.id.ibtnError:

                if (currentRightDrawer != null) {
                    hideRightmDrawer(currentRightDrawer);
                }
                showRightDrawer(incRightDrawerEditorError);
                isRightDrawerShown = true;
                currentRightDrawer = incRightDrawerEditorError;

                if (taskEditorFragment.currentTask != null) {
                    if (taskEditorFragment.currentTask.getErrortext() != null && taskEditorFragment.currentTask.getErrortext().length() > 0) {
                        tvError.setText(taskEditorFragment.currentTask.getErrortext());
                    } else {
                        tvError.setText(getResources().getString(R.string.emptyErrorMsg));
                    }
                    if (taskEditorFragment.currentTask.getErrorBgColor() != 0) {
                        tvBgColorError.setSolidColor(taskEditorFragment.currentTask.getErrorBgColor());
                    } else {
                        tvBgColorError.setSolidColor(getResources().getColor(R.color.redLight));
                    }


                    if (taskEditorFragment.currentTask.getErrorImage() != null && taskEditorFragment.currentTask.getErrorImage().length() > 0) {
                        imgProc.setImageWith_loader(ivImageError, taskEditorFragment.currentTask.getErrorImage());
                    } else {
                        ivImageError.setImageResource(R.drawable.img_women);
                    }
//                  sda
                    if (taskEditorFragment.currentTask.getErrorMandatoryScreen() != -1) {
                        swMandatoryInfo.setChecked(taskEditorFragment.currentTask.getErrorMandatoryScreen() == 1 ? true : false);
                    }

                }

                break;


            //1.13
            case R.id.ibtnGridOn:

                if (isGridShown == true) {
                    incWidgetArrangement.setVisibility(View.INVISIBLE);
                    isGridShown = false;
                } else {
                    incWidgetArrangement.setVisibility(View.VISIBLE);
                    isGridShown = true;
                }

                break;

            //1.14
            case R.id.ibtnSound:
                hideLeftmDrawer(incLeftDrawerEditor);
                setSoundMode();

                break;

            //1.15
            case R.id.ibtnFeedBack:

                isItemImage = false;
                isTaskErrorPic = false;
                isFeedbackImages = true;
                FeedbackAsyncTask feedbackAsyncTask = new FeedbackAsyncTask();
                feedbackAsyncTask.execute();

                break;
            //1.16
            case R.id.ibtnShowTopDrawer:
                hideLeftmDrawer(incLeftDrawerEditor);
                showTopDrawer(llTopDrawer);
                isTopDrawerShown = true;
                isMainBgColor = true;
                break;

            //1.17
            case R.id.ibtnShowBottomDrawer:

                hideLeftmDrawer(incLeftDrawerEditor);
                //hideRightmDrawer(currentRightDrawer);
                if (isRightDrawerShown) {
                    hideRightmDrawer(currentRightDrawer);
                    isRightDrawerShown = false;
                }

                showBottomDrawer(llBottomDrawer);
                isBottomDrawerShown = true;
                break;


            /************************************************* 1.4.0 rightDrawer editor edit *****************************************/
            //1.4.4
            case R.id.ibtnURLEdit:

                dialogForURL();

                break;


            //1.4.5
            case R.id.ibtnAddImageEdit:
                isTaskErrorPic = false;
                isFeedbackImages = false;
                imageSelectionDialog = new ImageSelectionDialog(activity, activity, itemPicFlag);
                DialogNavBarHide.navBarHide(this, imageSelectionDialog);
                isCircularImage = false;

                break;


            //1.4.6
            case R.id.ibtnAddTextEdit:
                if (currentRightDrawer != null) {
                    hideRightmDrawer(currentRightDrawer);
                }
                showRightDrawer(incRightDrawerEditorText);
                isRightDrawerShown = true;
                currentRightDrawer = incRightDrawerEditorText;
                taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
                Item item = taskEditorFragment.getItemFromKey(taskEditorFragment.currentKey);

                if (item != null) {
                    tvShadowText.setSolidColor(item.getTextColor() == -15066598 ? Color.BLACK : item.getTextColor());
                    seekBarText.setProgress(item.getTextSize() != -1 ? item.getTextSize() : 25);
                    tvFontText.setText(StaticAccess.fontName[item.getFontTypeFace()]);
                    tvText.setText(item.getUserText());

                    if (item.getFontAlign() == LimbikaView.ALIGN_LEFT) {
                        ibtnAlignLeftText.setBackgroundColor(getResources().getColor(R.color.black));
                        ibtnAlignCenterText.setBackgroundColor(Color.TRANSPARENT);
                        ibtnAlignAllText.setBackgroundColor(Color.TRANSPARENT);
                        ibtnAlignRightText.setBackgroundColor(Color.TRANSPARENT);

                    } else if (item.getFontAlign() == LimbikaView.ALIGN_RIGHT) {
                        ibtnAlignRightText.setBackgroundColor(getResources().getColor(R.color.black));
                        ibtnAlignLeftText.setBackgroundColor(Color.TRANSPARENT);
                        ibtnAlignCenterText.setBackgroundColor(Color.TRANSPARENT);
                        ibtnAlignAllText.setBackgroundColor(Color.TRANSPARENT);

                    } else if (item.getFontAlign() == LimbikaView.ALIGN_CENTER) {
                        ibtnAlignCenterText.setBackgroundColor(getResources().getColor(R.color.black));
                        ibtnAlignLeftText.setBackgroundColor(Color.TRANSPARENT);
                        ibtnAlignRightText.setBackgroundColor(Color.TRANSPARENT);
                        ibtnAlignAllText.setBackgroundColor(Color.TRANSPARENT);
                    }

                }

                break;
            //1.4.7
            case R.id.ibtnReadText:
                dialogForReadText();
//                dialogForURL();

                break;

            //1.4.8
            case R.id.ibtnWriteText:
                dialogForWriteText();
                break;


            //1.4.9
            case R.id.ibtnItemSoundEdit:
                setItemSound();
                break;
            /****************************************** 1.5.0 right Drawer editor form   *********************************/

            //1.5.1
            case R.id.tvBgColorForm:
                hideLeftmDrawer(incLeftDrawerEditor);
                showTopDrawer(llTopDrawer);
                isTopDrawerShown = true;
                isItemBgColor = true;
                isMainBgColor = false;
                isItemBorderColor = false;

                break;


            //1.5.2
            case R.id.tvBorderColorForm:

                hideLeftmDrawer(incLeftDrawerEditor);
                showTopDrawer(llTopDrawer);
                isTopDrawerShown = true;
                isItemBgColor = false;
                isMainBgColor = false;
                isItemBorderColor = true;
                break;


            //1.5.3
            case R.id.ibtnBorderpxForm:
                dialogBorderPixel();
                break;


            //1.5.5
            case R.id.ibtnDoneForm:

                // changed by reaz
                taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
                Item item1 = taskEditorFragment.getItemFromKey(taskEditorFragment.currentKey);
                taskEditorFragment.setBackgroundColor(tvBgColorForm.getSolidColor());
                taskEditorFragment.setBorderColor(tvBorderColorForm.getSolidColor(), Integer.parseInt(tvBorderpxForm.getText().toString()));

                if (isRightDrawerShown) {
                    hideRightmDrawer(currentRightDrawer);
                    isRightDrawerShown = false;
                }

                if (item1 != null) {
                    if (isBorderCorner) {
                        item1.setCornerRound(1);
                        taskEditorFragment.roundCorner(true);
                        isBorderCorner = false;
                    } else {
                        item1.setCornerRound(0);
                        taskEditorFragment.roundCorner(false);
                    }
                }

                break;


            /****************************************** 1.6.0 right Drawer editor info   *********************************/

            //1.6.3
            case R.id.ibtnDragDropInfo:

                taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
                if (taskEditorFragment.currentKey != null && isRightDrawerShown) {
                    isDragDropMode = true;
                    taskEditorFragment.setDropTarget();
                    hideRightmDrawer(currentRightDrawer);
                    hideLeftmDrawer(incLeftDrawerEditor);
                    isRightDrawerShown = false;
                    targetChildEnable();
                    CustomToast.t(this, getResources().getString(R.string.DropTarget));
                }

                break;

            //1.6.4
            case R.id.ibtnLinkdToInfo:
                if (isRightDrawerShown) {
                    hideRightmDrawer(currentRightDrawer);
                    hideLeftmDrawer(incLeftDrawerEditor);
                    isRightDrawerShown = false;
                }

                if (taskEditorFragment.currentKey != null) {
                    isDragDropMode = true;
                    taskEditorFragment.setDropTarget();
                    targetChildEnable();
                    CustomToast.t(this, getResources().getString(R.string.DropTarget));
                }

                break;


            /************************************************** 1.8.0 rightDrawer editor Align  *********************************************/

            //1.8.1
            case R.id.ibtnAlignCenterAlign:

                selectedAlignment = StaticAccess.ALIGN_PARENT_CENTER;
                flContentView.performClick();

                break;

            //1.8.2
            case R.id.ibtnAlignLeftAlign:

                selectedAlignment = StaticAccess.ALIGN_PARENT_LEFT;
                flContentView.performClick();

                break;

            //1.8.3
            case R.id.ibtnAlignRightAlign:

                selectedAlignment = StaticAccess.ALIGN_PARENT_RIGHT;
                flContentView.performClick();

                break;

            //1.8.4
            case R.id.ibtnAlignBottomAlign:

                selectedAlignment = StaticAccess.ALIGN_PARENT_BOTTOM;
                flContentView.performClick();

                break;

            //1.8.5
            case R.id.ibtnAlignTopAlign:

                selectedAlignment = StaticAccess.ALIGN_PARENT_TOP;
                flContentView.performClick();

                break;


            //1.8.6
            case R.id.ibtnAlignXCenter:
                selectedAlignment = StaticAccess.ALIGN_PARENT_CENTER_X;
                flContentView.performClick();

                break;

            //1.8.7
            case R.id.ibtnAlignYCenter:

                selectedAlignment = StaticAccess.ALIGN_PARENT_CENTER_Y;
                flContentView.performClick();
                break;


            /************************************************** 1.9.0 rightDrawer editor Screen Align  ******************************/

            //1.9.1
            case R.id.ibtnAlignScreenCenterAlign:
                selectedAlignment = StaticAccess.ALIGN_SCREEN_CENTER;
                flContentView.performClick();
                break;

            //1.9.2
            case R.id.ibtnAlignScreenTopAlign:

                selectedAlignment = StaticAccess.ALIGN_SCREEN_TOP;
                flContentView.performClick();
                break;

            //1.9.3
            case R.id.ibtnAlignScreenBottomAlign:

                selectedAlignment = StaticAccess.ALIGN_SCREEN_BOTTOM;
                flContentView.performClick();
                break;

            //1.9.4
            case R.id.ibtnAlignScreenLeftAlign:

                selectedAlignment = StaticAccess.ALIGN_SCREEN_LEFT;
                flContentView.performClick();
                break;

            //1.9.5
            case R.id.ibtnAlignScreenRightAlign:

                selectedAlignment = StaticAccess.ALIGN_SCREEN_RIGHT;
                flContentView.performClick();
                break;


            //1.9.6
            case R.id.ibtnAlignXScreenCenter:
                selectedAlignment = StaticAccess.ALIGN_SCREEN_CENTER_X;
                flContentView.performClick();
                break;

            //1.9.7
            case R.id.ibtnAlignYScreenCenter:
                selectedAlignment = StaticAccess.ALIGN_SCREEN_CENTER_Y;
                flContentView.performClick();
                break;


            /*********************************************** 1.10.0 rightDrawer editor Position  *****************************************/

            //1.10.1
            case R.id.ibtnFrontTopPosition:
                isSendToBack = true;
                break;

            //1.10.2
            case R.id.ibtnBackTopPosition:
                isSendToFront = true;
                break;

            //1.10.3
            case R.id.ibtnFrontStepPosition:
                taskEditorFragment.sendToBackMost();
                break;

            //1.10.4
            case R.id.ibtnBackStepPosition:
                taskEditorFragment.sendToFrontMost();
                break;


            /*********************************************** 1.12.0 rightDrawer editor Error  *****************************************/

            //1.12.2
            case R.id.tvBgColorError:
                hideLeftmDrawer(incLeftDrawerEditor);
                showTopDrawer(llTopDrawer);
                isTopDrawerShown = true;
                isErrorBgColor = true;
                isItemBgColor = false;
                isMainBgColor = false;
                isItemBorderColor = false;
                break;

            //1.12.3
            case R.id.ibtnAddImageError:

                imageSelectionDialog = new ImageSelectionDialog(activity, activity, TaskErrorFlag);
                DialogNavBarHide.navBarHide(this, imageSelectionDialog);
                isCircularImage = false;
                isTaskErrorPic = true;
                isFeedbackImages = false;

                break;


            //1.12.4
            case R.id.ibtnAddTextError:
                if (isTopDrawerShown) {
                    hideTopDrawer(llTopDrawer);
                    isTopDrawerShown = false;
                }
                dialogErrorText();
                break;


            //1.12.5
            case R.id.ibtnDoneError:

                if (taskEditorFragment.currentTask != null) {
                    if (tvError != null && tvError.getText().length() > 0) {
                        taskEditorFragment.setErrorText(tvError.getText().toString());
                    } else {
                        taskEditorFragment.setErrorText(getResources().getString(R.string.emptyErrorMsg));
                    }
                }
                if (taskEditorFragment.currentTask != null) {
                    if (taskEditorFragment.getErrorImage() != null && taskEditorFragment.getErrorImage().length() > 0) {
                        taskEditorFragment.setErrorImagePath(taskEditorFragment.getErrorImage());
                    } else {
                        taskEditorFragment.setErrorImagePath("");
                    }
                }
                if (taskEditorFragment.currentTask != null) {
                    taskEditorFragment.setErrorColor(tvBgColorError.getSolidColor());
                }
                if (taskEditorFragment.currentTask != null) {
                    taskEditorFragment.setErrorMandatoryScreen(errorMandatory);
                }
                hideRightmDrawer(currentRightDrawer);
                isRightDrawerShown = false;
                break;


            /*********************************************** 1.17.0 Bottom Drawer editor   *****************************************/

            //1.17.1
            case R.id.ibtnBottomCopyPaste:
                copyDialog();
                break;

            //1.17.2
            case R.id.ibtnBottomNew:
                if (taskEditorFragment.itemMap.size() > 0) {
                    taskEditorFragment.saveTask();
                    bottomDrawerCustomeViews.clear();
                    createBottomDrawer();
                    reGenerateBottomDrawer();
                    taskEditorFragment.newTask();
                    fbtnShowLeftDrawer.performClick();
                    currentRightDrawer = null;
                } else {
                    flContentView.performClick();
                }
                Intent intent = new Intent(activity, TransparrentSlideTypeActivity.class);
                intent.putExtra(TransparrentSlideTypeActivity.ChooserTransparentDialog, 1);
                startActivityForResult(intent, StaticAccess.TAG_TRANSPARENT_DIALOG_ACTIVITY);


//                    CustomToast.t(activity,"value"+taskType);
//                    Log.e("TAG",taskType);


                break;


            //1.17.3
            case R.id.ibtNewSameTaskMode:

                if (taskEditorFragment.itemMap.size() > 0) {
//                    CustomToast.t(activity,"value"+taskType);
//                    Log.e("TAG",taskType);
                    newTaskWithOutModeDialog();
                } else {
                    flContentView.performClick();
                }
                break;

            //1.17.4
            case R.id.ibtnBottomSave:

                taskEditorFragment.saveTask();
                bottomDrawerCustomeViews.clear();
                createBottomDrawer();
                reGenerateBottomDrawer();
                break;

            //1.17.5
            case R.id.ibtnBottomDelete:
                break;
            //1.17.6
            case R.id.ibtnBottomPreview:
                if (taskEditorFragment.itemMap.size() > 0) {
                    Long taskID = taskEditorFragment.saveTask();
                    bottomDrawerCustomeViews.clear();
                    createBottomDrawer();
                    reGenerateBottomDrawer();
                    Intent intentPrvPlayer = new Intent(activity, PlayerActivity.class);
                    intentPrvPlayer.putExtra(StaticAccess.INTENT_TASK_PACK_ID, taskPackId);
                    intentPrvPlayer.putExtra(StaticAccess.INTENT_TASK_ID, taskID);
                    intentPrvPlayer.putExtra(StaticAccess.INTENT_TASK_PREVIEW, 1);
                    startActivityForResult(intentPrvPlayer, StaticAccess.TAG_CREATE_TASK_PREVIEW_MODE);
                } else {
                    CustomToast.t(activity, getResources().getString(R.string.anyTask));
                }

                break;


            /********************************************************* 1.4.6.0 right Drawer editor Text **********************************************/

            //1.4.6.2
            case R.id.ibtnAlignLeftText:
                fontAlign = LimbikaView.ALIGN_LEFT;
                CustomToast.t(activity, getResources().getString(R.string.TextLeftAlign));
                selectedTextAlignment();

                break;

            //1.4.6.3
            case R.id.ibtnAlignCenterText:
                fontAlign = LimbikaView.ALIGN_CENTER;
                CustomToast.t(activity, getResources().getString(R.string.TextCenterAlign));
                selectedTextAlignment();

                break;

            //1.4.6.4
            case R.id.ibtnAlignAllText:

                break;

            //1.4.6.5
            case R.id.ibtnAlignRightText:
                fontAlign = LimbikaView.ALIGN_RIGHT;
                CustomToast.t(activity, getResources().getString(R.string.TextRighttAlign));
                selectedTextAlignment();
                break;


            //1.4.6.6
            case R.id.ibtnFontText:

                dialogFont();
                break;

            //1.4.6.7
            case R.id.tvShadowText:
                hideLeftmDrawer(incLeftDrawerEditor);
                showTopDrawer(llTopDrawer);
                isTopDrawerShown = true;
                isItemTextColor = true;
                break;

            //1.4.6.8
            case R.id.ibtnText:

                if (isTopDrawerShown) {
                    hideTopDrawer(llTopDrawer);
                    isTopDrawerShown = false;
                }

                dialogForText();
                break;


            //1.4.6.9
            case R.id.ibtnDoneText:

                if (isRightDrawerShown) {
                    hideRightmDrawer(currentRightDrawer);
                    isRightDrawerShown = false;
                }

                if (tvText.getText().toString().length() == 0) {
                    CustomToast.t(this, getResources().getString(R.string.emptyTextField));
                    return;
                }
                taskEditorFragment.setText(tvText.getText().toString(), seekBarText.getProgress(), tvShadowText.getSolidColor(), StaticAccess.getTextFontValue(tvFontText.getText().toString()), fontAlign);


                break;

        }

    }


    private void checkScreenDensity() {
        int density = getResources().getDisplayMetrics().densityDpi;

        switch (density) {
            case DisplayMetrics.DENSITY_LOW:
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                break;
            case DisplayMetrics.DENSITY_HIGH:
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                break;
        }
    }

    // Show Left Drawer
    public void showLeftDrawer(final View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(-view.getWidth(), 0, 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);

        animate.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);

        // view.setVisibility(View.GONE);
        //view.setVisibility(View.INVISIBLE);
    }

    // Hide Left Drawer
    public void hideLeftmDrawer(final View view) {
        TranslateAnimation animate = new TranslateAnimation(0, -view.getWidth(), 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);

        animate.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // Show Right Drawer
    public void showRightDrawer(final View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(view.getWidth(), 0, 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);

        animate.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);

        // view.setVisibility(View.GONE);
        //view.setVisibility(View.INVISIBLE);
    }

    // Hide Right Drawer
    public void hideRightmDrawer(final View view) {
        TranslateAnimation animate = new TranslateAnimation(0, view.getWidth(), 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);

        animate.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // load image from Internet by Rokan
    public void loadImageInternet(int internetFlag) {

        if (isImageSearchDialogShow == false) {
            imageSearchDialog = new ImageSearchCustomDialog(activity, activity, internetFlag);
            DialogNavBarHide.navBarHide(this, imageSearchDialog);
            isImageSearchDialogShow = true;
        }

    }


    // load image from Gallery by Rokan
    public void loadImageGallery(int selectFlag) {
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        if (selectFlag == itemPicFlag) {
            isTaskErrorPic = false;
            isFeedbackImages = false;
            intent.putExtra(StaticAccess.TAG_INTENT_IMG, StaticAccess.TAG_SELECT_PICTURE);
            startActivityForResult(intent, StaticAccess.SELECT_PICTURE);
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        } else if (selectFlag == TaskErrorFlag) {
            isTaskErrorPic = true;
            isFeedbackImages = false;
            intent.putExtra(StaticAccess.TAG_INTENT_IMG, StaticAccess.TAG_SELECT_PICTURE_ERROR);
            startActivityForResult(intent, StaticAccess.SELECT_PICTURE_ERROR);

        } else if (selectFlag == feedbackImageFlag) {
            isTaskErrorPic = false;
            isFeedbackImages = true;
            intent.putExtra(StaticAccess.TAG_INTENT_IMG, StaticAccess.TAG_SELECT_PICTURE_FEEDBACK);
            startActivityForResult(intent, StaticAccess.SELECT_PICTURE_FEEDBACK);

        }

        intent_source = 1;
    }


    // load image from camera by Rokan
    public void loadImageCamera(int flag) {
        // flag for using item or task
        File sdCardDirectory = new File(Environment.getExternalStorageDirectory() + appImagePath);
        if (!sdCardDirectory.exists()) {
            sdCardDirectory.mkdirs();
        }
        String state1 = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state1)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory() + appImagePath, TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(getFilesDir() + appImagePath, TEMP_PHOTO_FILE_NAME);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            Uri mImageCaptureUri = null;
            String state2 = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state2)) {
                mImageCaptureUri = Uri.fromFile(mFileTemp);
            } else {
                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
            }
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);

            if (flag == itemPicFlag) {
                isTaskErrorPic = false;
                isFeedbackImages = false;
                startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
            } else if (flag == TaskErrorFlag) {
                isTaskErrorPic = true;
                isFeedbackImages = false;
                startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE_ERROR);

            } else if (flag == feedbackImageFlag) {
                isTaskErrorPic = false;
                isFeedbackImages = true;
                startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE_FEEDBACK);
            }

        } catch (ActivityNotFoundException e) {
        }

        intent_source = 2;
    }

    // image path by Rokan
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // image Croping by Rokan
    /*public void startCropImage(File file) {

        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, file.getPath());
        intent.putExtra(CropImage.SCALE, true);
        intent.putExtra(CropImage.ASPECT_X, 3);
        intent.putExtra(CropImage.ASPECT_Y, 2);
        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);

    }*/


    // Image getting from Camera, Gallery internet by Rokan
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fileProcessing = new FileProcessing(activity);
        taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
        Bitmap widgetImage = null;

        if (resultCode == RESULT_OK) {
            // This is use for supper Error created by reaz
          /*  if (requestCode == SELECT_PICTURE_ERROR && intent_source == 1) {
                openCropper(data.getData());
                isTaskErrorPic = true;
                isFeedbackImages = false;
            }*/
            //intent_source from camera =2 and from Gallery intent_source=1;
            // load image from Camera
            if (requestCode == REQUEST_CODE_TAKE_PICTURE && intent_source == 2) {
                openCropper(Uri.fromFile(new File(mFileTemp.getAbsolutePath())));
                isTaskErrorPic = false;
                isFeedbackImages = false;
            }

            // This is use for supper Error created by reaz
            if (requestCode == REQUEST_CODE_TAKE_PICTURE_ERROR && intent_source == 2) {
                openCropper(Uri.fromFile(new File(mFileTemp.getAbsolutePath())));
                isTaskErrorPic = true;
                isFeedbackImages = false;
            }


            if (requestCode == REQUEST_CODE_TAKE_PICTURE_FEEDBACK && intent_source == 2) {

                openCropper(Uri.fromFile(new File(mFileTemp.getAbsolutePath())));
                isFeedbackImages = true;
                isTaskErrorPic = false;
            }


            /*if (requestCode == REQUEST_CODE_CROP_IMAGE) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgProc.getCroppedImage().getPath());
                if (isImageSearchDialogShow == true) {
                    imageSearchDialog.dismiss();
                    isImageSearchDialogShow = false;
                }
                widgetImage = bitmap;
            }*/

            // Load image after Cropping (Transparent)
            if (requestCode == com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                com.theartofdev.edmodo.cropper.CropImage.ActivityResult result = com.theartofdev.edmodo.cropper.CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (isTaskErrorPic) {
                    setTaskErrorPic(bitmap);
                    isTaskErrorPic = false;
                } else if (isFeedbackImages) {
                    setFeedBackPic(bitmap);
                } else {
                    setTemporaryImage(bitmap);
                }

            } else if (resultCode == com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                L.t(activity, "Crop Error");
            }


            // Getting file with Material File picker


            /*// Getting file with Material File picker
            if (requestCode == StaticAccess.MATERIAL_FILE_PICKER) {
                filePath = data.getStringExtra(StaticAccess.TAG_SELECT_SOUND);
                int intFileSize = fileProcessing.fileSize(filePath);

                if (intFileSize <= StaticAccess.TAG_SOUND_FILE_SIZE) {
                    switch (CURRENT_FILE_PICKER_ITEM) {
                        case StaticAccess.TAG_SOUND_NEGATIVE:
                            if (mp != null)
                                mp.release();

                            soundPlay(filePath);
                            soundFileName = fileProcessing.createSoundFile(filePath);
                            taskEditorFragment.setNegativeSound(soundFileName);
                            tvNegative.setText(filePath);
                            break;
                        case StaticAccess.TAG_SOUND_POSITIVE:
                            if (mp != null)
                                mp.release();

                            soundPlay(filePath);
                            soundFileName = fileProcessing.createSoundFile(filePath);
                            taskEditorFragment.setPositiveSound(soundFileName);
                            tvPositive.setText(filePath);
                            break;
                        case StaticAccess.TAG_SOUND_FEEDBACK:
                            if (mp != null)
                                mp.release();

                            soundPlay(filePath);
                            soundFileName = fileProcessing.createSoundFile(filePath);
                            taskEditorFragment.setFeedBackSound(soundFileName);
                            tvFeedBack.setText(filePath);
                            break;
                        case StaticAccess.TAG_SOUND_VIDEO:
                            taskEditorFragment.setVideo(filePath);
                            break;
                        case StaticAccess.TAG_ITEM_SOUND:
                            taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
                            if (mp != null)
                                mp.release();

                            soundPlay(filePath);
                            soundFileName = fileProcessing.createSoundFile(filePath);
                            taskEditorFragment.setItemSound(soundFileName);
                            tvItemSound.setText(filePath);
                            break;


                        default:
                            break;
                    }
                } else {
                    CustomToast.t(activity, getResources().getString(R.string.notSupportedFile));
                }


                dialogShowSoundPath(filePath);
            }*/

            if (requestCode == StaticAccess.TAG_CREATE_TASK_PREVIEW_MODE) {
                CustomToast.t(activity, getResources().getString(R.string.TaskEditMode));
            }


        }

        if (requestCode == StaticAccess.TAG_TRANSPARENT_DIALOG_ACTIVITY) {
            if (resultCode != -1)
                switch (resultCode) {
                    case 0:
                        taskType = TaskType.Assistive;
                        // resetViews();
                        //CustomToast.t(activity, getResources().getString(R.string.selectedAssistiveTouch));
                        fbtnShowLeftDrawer.setImageResource(R.drawable.ic_info_showed_by);
                        setGeneralEnable(false);
                        setDragDropEnable(false);
                        setAssistiveEnable(true);
                        setReadEnable(false);
                        setWriteEnable(false);
                        setSequenceEnable(false);
                        ibtnEdit.performClick();

                        break;
                    case 1:
                        taskType = TaskType.Normal;

                        fbtnShowLeftDrawer.setImageResource(R.drawable.ic_general_mode);
                        //CustomToast.t(activity, getResources().getString(R.string.selectedGeneral));
                        setGeneralEnable(true);
                        setDragDropEnable(false);
                        setAssistiveEnable(false);
                        setReadEnable(false);
                        setWriteEnable(false);
                        setSequenceEnable(false);
                        ibtnEdit.performClick();
                        break;
                    case 2:
                    /*    taskType = TaskType.Sequence;

                        fbtnShowLeftDrawer.setImageResource(R.drawable.ic_general_mode);
                        //CustomToast.t(activity, getResources().getString(R.string.selectedGeneral));
                        setGeneralEnable(true);
                        setDragDropEnable(false);
                        setAssistiveEnable(false);
                        setReadEnable(false);
                         setWriteEnable(false);
                        setSequenceEnable(true);*/
                        break;
                    case 3:
                        taskType = TaskType.Read;

                        fbtnShowLeftDrawer.setImageResource(R.drawable.ic_general_mode);
                        //CustomToast.t(activity, getResources().getString(R.string.selectedGeneral));
                        setGeneralEnable(true);
                        setDragDropEnable(false);
                        setAssistiveEnable(false);
                        setReadEnable(true);
                        setWriteEnable(false);
                        setSequenceEnable(false);
                        break;
                    case 4:
                        taskType = TaskType.DragDrop;
                        // resetViews();
                        //CustomToast.t(activity, getResources().getString(R.string.selectedDragDrop));
                        fbtnShowLeftDrawer.setImageResource(R.drawable.ic_info_drag_drop);

                        setGeneralEnable(false);
                        setAssistiveEnable(false);
                        setDragDropEnable(true);
                        setReadEnable(false);
                        setWriteEnable(false);
                        setSequenceEnable(false);
                        ibtnEdit.performClick();
                        break;
                    case 5:
                        taskType = TaskType.Sequence;

                        fbtnShowLeftDrawer.setImageResource(R.drawable.ic_general_mode);
                        //CustomToast.t(activity, getResources().getString(R.string.selectedGeneral));
                        setGeneralEnable(true);
                        setDragDropEnable(false);
                        setAssistiveEnable(false);
                        setReadEnable(false);
                        setWriteEnable(false);
                        setSequenceEnable(true);
                        break;

                    case 6:
                        taskType = TaskType.Write;

                        fbtnShowLeftDrawer.setImageResource(R.drawable.ic_general_mode);
                        //CustomToast.t(activity, getResources().getString(R.string.selectedGeneral));
                        setGeneralEnable(true);
                        setDragDropEnable(false);
                        setAssistiveEnable(false);
                        setReadEnable(false);
                        setWriteEnable(true);
                        setSequenceEnable(false);
                        break;


                }
        }

        if (requestCode == StaticAccess.SELECT_PICTURE && intent_source == 1) {
            if (data != null) {
                // fetch the message String
                String filePath = data.getStringExtra(StaticAccess.TAG_SELECT_PICTURE);
                openCropper(Uri.fromFile(new File(filePath)));
            }
//                CustomToast.t(activity,);
        }
        // This is use for supper Error created by reaz
        if (requestCode == StaticAccess.SELECT_PICTURE_ERROR && intent_source == 1) {
            if (data != null) {
                // fetch the message String
                String filePath = data.getStringExtra(StaticAccess.TAG_SELECT_PICTURE_ERROR);
                openCropper(Uri.fromFile(new File(filePath)));
            }

            isTaskErrorPic = true;
            isFeedbackImages = false;
        }
        if (requestCode == StaticAccess.SELECT_PICTURE_FEEDBACK && intent_source == 1) {

            String filePath = data.getStringExtra(StaticAccess.TAG_SELECT_PICTURE_FEEDBACK);
            openCropper(Uri.fromFile(new File(filePath)));
            isFeedbackImages = true;
            isTaskErrorPic = false;


        }

        if (requestCode == MATERIAL_FILE_PICKER) {
            filePath = data.getStringExtra(StaticAccess.TAG_SELECT_SOUND);
            int intFileSize = fileProcessing.fileSize(filePath);

            if (intFileSize <= StaticAccess.TAG_SOUND_FILE_SIZE) {
                switch (CURRENT_FILE_PICKER_ITEM) {
                    case StaticAccess.TAG_SOUND_NEGATIVE:
                        if (mp != null)
                            mp.release();
                        if (filePath.length() > 0) {
                            soundPlay(filePath);
                            soundFileName = fileProcessing.createSoundFile(filePath);
                            taskEditorFragment.setNegativeSound(soundFileName);
                        } else {
                            filePath = taskEditorFragment.currentTask.getNegativeSound();
                            taskEditorFragment.setNegativeSound(filePath);
                        }
                        tvNegative.setText(filePath);
                        break;
                    case StaticAccess.TAG_SOUND_POSITIVE:
                        if (mp != null)
                            mp.release();
                        if (filePath.length() > 0) {
                            soundPlay(filePath);
                            soundFileName = fileProcessing.createSoundFile(filePath);
                            taskEditorFragment.setPositiveSound(soundFileName);
                        } else {
                            filePath = taskEditorFragment.currentTask.getPositiveSound();
                            taskEditorFragment.setPositiveSound(filePath);
                        }
                        tvPositive.setText(filePath);
                        break;
                    case StaticAccess.TAG_SOUND_FEEDBACK:
                        if (mp != null)
                            mp.release();
                        if (filePath.length() > 0) {

                            soundPlay(filePath);
                            soundFileName = fileProcessing.createSoundFile(filePath);
                            taskEditorFragment.setFeedBackSound(soundFileName);
                        } else {
                            filePath = taskEditorFragment.currentTask.getFeedbackSound();
                            taskEditorFragment.setFeedBackSound(filePath);
                        }
                        tvFeedBack.setText(filePath);
                        break;
                    case StaticAccess.TAG_SOUND_VIDEO:
                        taskEditorFragment.setVideo(filePath);
                        break;
                    case StaticAccess.TAG_ITEM_SOUND:
                        taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
                        if (mp != null)
                            mp.release();
                        if (filePath.length() > 0) {
                            soundPlay(filePath);
                            soundFileName = fileProcessing.createSoundFile(filePath);
                            taskEditorFragment.setItemSound(soundFileName);
                        } else {
                            Item item = taskEditorFragment.getItemFromKey(taskEditorFragment.currentKey);
                            if (item != null)
                                filePath = item.getItemSound();
                            taskEditorFragment.setItemSound(filePath);
                        }
                        tvItemSound.setText(filePath);
                        break;

                    default:
                        break;
                }
            } else {
                CustomToast.t(activity, getResources().getString(R.string.notSupportedFile));
            }

            dialogShowSoundPath(filePath);
        }
        setTemporaryImage(widgetImage);
        intent_source = 0;

    }

    //Test purpose sound create by Reaz
    public void soundPlay(String path) {
        mp = new MediaPlayer();
        try {
            mp.setDataSource(path);//Write your location here
            mp.prepare();
            mp.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // set Image by Rokan
    public void setTemporaryImage(Bitmap bitmap) {
        taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
        if (bitmap != null) {
            taskEditorFragment.setImage(bitmap);
            isItemImage = false;
        }
        setImage = true;
    }

    public void setCurrentType(String type) {
        this.currentType = type;
    }

    public void setCurrentItem(Long item) {
        this.currentItem = item;
    }

    //Change fragment background color
    private void changeAppBackground(int color) {
        //flContentView.setBackgroundColor(color);
        taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
        taskEditorFragment.flEditor.setBackgroundColor(color);
    }

    public boolean checkAlignMode() {
        if (currentRightDrawer == incRightDrawerEditorAlign) {
            return true;
        } else {
            return false;
        }
    }


    public boolean checkScreenAlignMode() {
        if (currentRightDrawer == incRightDrawerEditorScreenAlign) {
            return true;
        } else {
            return false;
        }
    }


    public void hideAllDrawer() {
        flContentView.performClick();
    }

    public void setBackgroundColor(int color, View view) {
        this.color = color;
        ShapeDrawable biggerCircle = new ShapeDrawable(new OvalShape());
        biggerCircle.setIntrinsicHeight(60);
        biggerCircle.setIntrinsicWidth(60);
        biggerCircle.setBounds(new Rect(0, 0, 60, 60));
        biggerCircle.getPaint().setColor(color);

        ShapeDrawable smallerCircle = new ShapeDrawable(new OvalShape());
        smallerCircle.setIntrinsicHeight(10);
        smallerCircle.setIntrinsicWidth(10);
        smallerCircle.setBounds(new Rect(0, 0, 10, 10));
        smallerCircle.getPaint().setColor(Color.WHITE);
        smallerCircle.setPadding(2, 2, 2, 2);
        Drawable[] d = {smallerCircle, biggerCircle};

        LayerDrawable layerDrawable = new LayerDrawable(d);
        view.setBackground(layerDrawable);
    }

    /*public void resetViews() {
        //edtText.setText("");
        edtTextEdit.setText("");

    }
*/
    //  All switcher Listener By Rokan
    public void switcherListener() {
        taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);

        /***************************************** right drawer editor Edit Switcher ***************************/
        // 1.4.1
        swCloseAppEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskEditorFragment.closeApp(swCloseAppEdit.isChecked() ? 1 : 0);
            }
        });


        // 1.4.2
        swNavigatesEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swNavigatesEdit.setChecked(!swNavigatesEdit.isChecked());
                ArrayList<Task> tasks = (ArrayList<Task>) databaseManager.listTasksByTAskPackId(taskPackId);
                if (tasks != null) {
                    TaskGridDialog taskGridDialog = new TaskGridDialog(activity, tasks);
                    taskGridDialog.show();
                }
            }
        });

        // 1.4.3
        swOpenAppEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swOpenAppEdit.isChecked()) {
                    swOpenAppEdit.setChecked(!swOpenAppEdit.isChecked());
                    ListInstalledAppsDialog listInstalledAppsDialog = new ListInstalledAppsDialog(activity);
                    listInstalledAppsDialog.show();
                } else {
                    taskEditorFragment.openApp("");
                }
            }
        });

        swOpenAppEdit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // swOpenAppEdit.setChecked(!swOpenAppEdit.isChecked());
            }

        });


        /***************************************** right drawer editor Form Switcher ***************************/

        //1.5.4
        swRoundCornerForm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    isBorderCorner = true;

                } else {
                    isBorderCorner = false;
                }
            }
        });

        /***************************************** right drawer editor Info Switcher ***************************/
//comment by reaz setOnCheckedChangeListener replace OnClickedListener
    /*    //1.6.1
        swCorrectInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    taskEditorFragment.setAnswer(TaskType.NORMAL_TRUE);
                    //CustomToast.t(activity, getResources().getString(R.string.CorrectAnswer));
                    swIncorrectInfo.setChecked(false);
                } else {
                    taskEditorFragment.setAnswer("");
                    swIncorrectInfo.setChecked(false);

                }

            }

        });*/

        //1.6.1
        swCorrectInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch switcher = (Switch) v;
                if (switcher.isChecked()) {
//                    item.setResult(TaskType.NORMAL_TRUE);
                    taskEditorFragment.setAnswer(TaskType.NORMAL_TRUE);
                    //CustomToast.t(activity, getResources().getString(R.string.CorrectAnswer));
                    swIncorrectInfo.setChecked(false);

                } else {
//                    item.setResult("");
                    taskEditorFragment.setAnswer("");
                    swIncorrectInfo.setChecked(false);
                }

            }
        });
        //comment by reaz setOnCheckedChangeListener replace OnClickedListener
      /*  //1.6.2
        swIncorrectInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    taskEditorFragment.setAnswer(TaskType.NORMAL_FALSE);
                    // CustomToast.t(activity, getResources().getString(R.string.InCorrectAnswer));
                    swCorrectInfo.setChecked(false);
                } else {
                    taskEditorFragment.setAnswer("");
                    swCorrectInfo.setChecked(false);
                }
            }

        });*/

        //1.6.2
        swIncorrectInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch switcher = (Switch) v;
                if (switcher.isChecked()) {
                    switcher.setChecked(true);

                    taskEditorFragment.setAnswer(TaskType.NORMAL_FALSE);
                    // CustomToast.t(activity, getResources().getString(R.string.InCorrectAnswer));
                    swCorrectInfo.setChecked(false);

                } else {
                    taskEditorFragment.setAnswer("");
                    swCorrectInfo.setChecked(false);
                }

            }
        });
        // 1.6.5
        swTargetInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch switcher = (Switch) v;
                if (switcher.isChecked()) {

                    switcher.setChecked(false);
                    CustomToast.t(MainActivity.this, getResources().getString(R.string.SwitcherText));


                } else {
                    if (taskEditorFragment.checkTarget()) {
                        taskEditorFragment.deleteTarget();
                        CustomToast.t(activity, getResources().getString(R.string.targetDeleted));
                    }

                }

            }
        });

        // 1.6.6
        swChildInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch switcher = (Switch) v;
                if (switcher.isChecked()) {
                    switcher.setChecked(false);
                    CustomToast.t(MainActivity.this, getResources().getString(R.string.SwitcherText));

                } else {

                    if (taskEditorFragment.checkChild()) {
                        taskEditorFragment.deleteChild();
                        CustomToast.t(activity, getResources().getString(R.string.childDeleted));
                    }
                }

            }
        });

        //1.6.7
        swShowedByInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isRightDrawerShown) {
                    hideRightmDrawer(currentRightDrawer);
                    hideLeftmDrawer(incLeftDrawerEditor);
                    isRightDrawerShown = false;
                }


                if (taskEditorFragment.itemMap.size() > 1) {
                    swShowedByInfo.setChecked(!swNavigatesEdit.isChecked());
                    if (swShowedByInfo.isChecked()) {
                        CustomToast.t(activity, getResources().getString(R.string.TapItemToSetShow));
                        isShowedBy = true;
                    } else {
                        CustomToast.t(activity, getResources().getString(R.string.SelectItemToHide));
                        swShowedByInfo.setChecked(false);
                    }
                } else {
                    CustomToast.t(activity, getResources().getString(R.string.NeedItemAdded));
                    swShowedByInfo.setChecked(false);
                }


            }
        });

        //1.6.8
        swHiddenByInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isRightDrawerShown) {
                    hideRightmDrawer(currentRightDrawer);
                    hideLeftmDrawer(incLeftDrawerEditor);
                    isRightDrawerShown = false;
                }


                if (taskEditorFragment.itemMap.size() > 1) {
                    swHiddenByInfo.setChecked(!swNavigatesEdit.isChecked());
                    if (swHiddenByInfo.isChecked()) {
                        CustomToast.t(activity, getResources().getString(R.string.TapItemToSetHide));
                        isHideBy = true;
                    } else {
                        CustomToast.t(activity, getResources().getString(R.string.NeedItemAdded));
                        swHiddenByInfo.setChecked(false);
                    }
                } else {
                    CustomToast.t(activity, getResources().getString(R.string.NeedItemAdded));
                    swHiddenByInfo.setChecked(false);
                }
            }
        });


        //1.12.1
        swMandatoryInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (swMandatoryInfo.isChecked()) {
                    errorMandatory = 1;
                } else {
                    errorMandatory = 0;
                }
            }
        });


    }

    public void hideCurrentRightDrawer() {
        if (isRightDrawerShown == true && currentRightDrawer != null) {
            hideRightmDrawer(currentRightDrawer);
            currentRightDrawer = null;
            isRightDrawerShown = false;
        }
    }

    // set TaskType
    public void setTaskType() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.widzet_selection);
        dialog.setCancelable(false);
        ibtnDragAndDrop = (ImageButton) dialog.findViewById(R.id.ibtnDragAndDrop);
        ibtnGeneral = (ImageButton) dialog.findViewById(R.id.ibtnGeneral);
        ibtnAssistiveTuch = (ImageButton) dialog.findViewById(R.id.ibtnAssistiveTuch);

        ibtnDragAndDrop.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                taskType = TaskType.DragDrop;
                // resetViews();
                // CustomToast.t(activity, getResources().getString(R.string.selectedDragDrop));
                dialog.dismiss();
                fbtnShowLeftDrawer.setImageResource(R.drawable.ic_info_drag_drop);
//drag
                setGeneralEnable(false);
                setAssistiveEnable(false);
                setDragDropEnable(true);

            }
        });
        ibtnGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskType = TaskType.Normal;
                //resetViews();
                fbtnShowLeftDrawer.setImageResource(R.drawable.ic_general_mode);
                // CustomToast.t(activity, getResources().getString(R.string.selectedGeneral));
                dialog.dismiss();
                setGeneralEnable(true);
                setDragDropEnable(false);
                setAssistiveEnable(false);

            }
        });

        ibtnAssistiveTuch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskType = TaskType.Assistive;
                // resetViews();
                //CustomToast.t(activity, getResources().getString(R.string.selectedAssistiveTouch));
                dialog.dismiss();
                fbtnShowLeftDrawer.setImageResource(R.drawable.ic_info_showed_by);

                setGeneralEnable(false);
                setDragDropEnable(false);
                setAssistiveEnable(true);

            }
        });
        DialogNavBarHide.navBarHide(this, dialog);
    }

    //when general mode Select that time Assistive and Drag Mode Visibility GONE
    public boolean setGeneralEnable(Boolean flag) {

        if (flag) {
            lnGeneralCorrect.setVisibility(View.VISIBLE);
            lnGeneralInCorrect.setVisibility(View.VISIBLE);

            lnAssistive.setVisibility(View.GONE);
            lnDrag.setVisibility(View.GONE);
            lnChild.setVisibility(View.GONE);
            lnTarget.setVisibility(View.GONE);
            swCorrectInfo.setEnabled(flag);
            swIncorrectInfo.setEnabled(flag);
        } else {
            lnGeneralCorrect.setVisibility(View.GONE);
            lnGeneralInCorrect.setVisibility(View.GONE);
            swTargetInfo.setEnabled(flag);
            swChildInfo.setEnabled(flag);
        }

        if (!flag) {
            swTargetInfo.setEnabled(flag);
            swChildInfo.setEnabled(flag);
        }

        return true;
    }

    public boolean setDragDropEnable(Boolean flag) {
        if (flag) {
            lnGeneralCorrect.setVisibility(View.GONE);
            lnGeneralInCorrect.setVisibility(View.GONE);
            lnAssistive.setVisibility(View.GONE);
            lnDrag.setVisibility(View.VISIBLE);
            lnChild.setVisibility(View.VISIBLE);
            lnTarget.setVisibility(View.VISIBLE);
        }
        ibtnDragDropInfo.setEnabled(flag);
        swTargetInfo.setEnabled(flag);
        swChildInfo.setEnabled(flag);
        return true;
    }

    public boolean setAssistiveEnable(Boolean flag) {
        if (flag) {
            lnGeneralCorrect.setVisibility(View.GONE);
            lnGeneralInCorrect.setVisibility(View.GONE);
            lnAssistive.setVisibility(View.VISIBLE);
            lnDrag.setVisibility(View.GONE);
            lnChild.setVisibility(View.VISIBLE);
            lnTarget.setVisibility(View.VISIBLE);
        }
        ibtnLinkdToInfo.setEnabled(flag);
//        ibtnDragDropInfo.setEnabled(flag);
        swTargetInfo.setEnabled(flag);
        swChildInfo.setEnabled(flag);

        return true;
    }

    public boolean setReadEnable(Boolean flag) {
        if (flag) {
            lnReadText.setVisibility(View.VISIBLE);
            lnGeneralInCorrect.setVisibility(View.GONE);
            tvReadable.setText(getResources().getString(R.string.tvReadable));
        } else {
            lnReadText.setVisibility(View.GONE);
        }

        return true;
    }

    public boolean setWriteEnable(Boolean flag) {
        if (flag) {
            lnWriteText.setVisibility(View.VISIBLE);
            lnGeneralInCorrect.setVisibility(View.GONE);
            tvReadable.setText(getResources().getString(R.string.tvWriteable));
        } else {
            lnWriteText.setVisibility(View.GONE);
        }

        return true;
    }


    public boolean setSequenceEnable(Boolean flag) {
        if (flag) {
            llSequenceText.setVisibility(View.VISIBLE);
        } else {
            llSequenceText.setVisibility(View.GONE);
        }

        return true;
    }

    // Dialog for creating new task By Rokan
    private void newTaskDialog() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_new_task);
        dialog.setCancelable(false);

        final TextView tvCopyText = (TextView) dialog.findViewById(R.id.tvNewTaskPermission);
        Button btnCancelPermission = (Button) dialog.findViewById(R.id.btnCancelPermission);
        Button btnOkPermission = (Button) dialog.findViewById(R.id.btnOkPermission);
        tvCopyText.setText(getResources().getString(R.string.newTask));


        btnCancelPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskEditorFragment.newTask();
                fbtnShowLeftDrawer.performClick();
                currentRightDrawer = null;
                setTaskType();
                dialog.dismiss();
            }
        });
        btnOkPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                taskEditorFragment.saveTask();
                bottomDrawerCustomeViews.clear();
                createBottomDrawer();
                reGenerateBottomDrawer();
                taskEditorFragment.newTask();
                fbtnShowLeftDrawer.performClick();
                currentRightDrawer = null;
                setTaskType();
                dialog.dismiss();

            }
        });
        hideBottomDrawer(llBottomDrawer);
        DialogNavBarHide.navBarHide(this, dialog);
    }


    // Setting Feedback Image
    public void setFeedbackImage(String image, int type) {
        taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
        taskEditorFragment.setFeedbackImage(image, type);
    }

    // Setting Feedback Animation
    public void setFeedbackAnimation(int animType) {
        taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
        taskEditorFragment.setFeedbackAnim(animType);
    }

    //created Reaz for positive animation pass animType from anim_model dialog
    public void setPositiveAnimation(int animType) {
        taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
        taskEditorFragment.setPositiveAnim(animType);
    }

    //created Reaz for negative animation pass animType from anim_model dialog
    public void setNegativeAnimation(int animType) {
        taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
        taskEditorFragment.setNegativeAnim(animType);
    }

    // Setting navigate to
    public void navigateTo(Task task) {
        taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
        taskEditorFragment.navigateTo(task);
        swNavigatesEdit.setChecked(true);
    }

    // Setting Open App
    public void openApp(String packageName) {
        taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
        taskEditorFragment.openApp(packageName);
        swOpenAppEdit.setChecked(true);
    }


    // sound mode custom by reaz
    public void setSoundMode() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_sound_mode);
        dialog.setCancelable(false);
        tvPositive = (TextView) dialog.findViewById(R.id.tvPositive);
        tvNegative = (TextView) dialog.findViewById(R.id.tvNegative);
        tvFeedBack = (TextView) dialog.findViewById(R.id.tvFeedBack);


        ImageButton ibtnPositiveSoundClose = (ImageButton) dialog.findViewById(R.id.ibtnPositiveSoundClose);
        ImageButton ibtnNegativeSoundClose = (ImageButton) dialog.findViewById(R.id.ibtnNegativeSoundClose);
        ImageButton ibtnFBSoundClose = (ImageButton) dialog.findViewById(R.id.ibtnFBSoundClose);
        ImageButton ibtnSoundDone = (ImageButton) dialog.findViewById(R.id.ibtnSoundDone);


        if (taskEditorFragment.currentTask != null) {
            tvPositive.setText(taskEditorFragment.currentTask.getPositiveSound());
            tvNegative.setText(taskEditorFragment.currentTask.getNegativeSound());
            tvFeedBack.setText(taskEditorFragment.currentTask.getFeedbackSound());
        }

        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CustomToast.t(activity, "tvPositive Clicked");
                pickerSound();
                CURRENT_FILE_PICKER_ITEM = StaticAccess.TAG_SOUND_POSITIVE;


            }

        });
        tvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                CustomToast.t(activity, "tvNegative Clicked");
                pickerSound();
                CURRENT_FILE_PICKER_ITEM = StaticAccess.TAG_SOUND_NEGATIVE;

            }
        });

        tvFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickerSound();
                CURRENT_FILE_PICKER_ITEM = StaticAccess.TAG_SOUND_FEEDBACK;

            }

        });

        ibtnNegativeSoundClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvNegative.getText().length() > 0) {
                    if (taskEditorFragment.currentTask != null)
                        taskEditorFragment.currentTask.setNegativeSound("");
                    tvNegative.setText("");
                }

                /*if (tvNegative.getText().length() > 0)
                    if (fileProcessing.deleteSound(soundFileName)) {
                        tvNegative.setText("");
                    }*/


            }

        });
        ibtnPositiveSoundClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvPositive.getText().length() > 0) {
                    if (taskEditorFragment.currentTask != null)
                        taskEditorFragment.currentTask.setPositiveSound("");
                    tvPositive.setText("");
                }

             /*   if (tvPositive.getText().length() > 0)
                    if(soundFileName!=null)
                    if (fileProcessing.deleteSound(soundFileName)) {
                        tvPositive.setText("");
                    }*/


            }

        });

        ibtnFBSoundClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvFeedBack.getText().length() > 0) {
                    if (taskEditorFragment.currentTask != null)
                        taskEditorFragment.currentTask.setFeedbackSound("");
                    tvFeedBack.setText("");
                }
               /* if (tvFeedBack.getText().length() > 0)
                    if (fileProcessing.deleteSound(soundFileName)) {
                        tvFeedBack.setText("");
                    }*/

            }

        });


        ibtnSoundDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });


        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mp != null)
//                    if(mp.isPlaying()){
                    mp.release();
//                }
            }
        });


        DialogNavBarHide.navBarHide(this, dialog);

    }

    // Item Sound by Rokan
    public void setItemSound() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_item_sound);
        dialog.setCancelable(true);

        tvItemSound = (TextView) dialog.findViewById(R.id.tvItemSound);

        swAutoplayInfo = (Switch) dialog.findViewById(R.id.swAutoplayInfo);
        tvDelaySoundInfo = (TextView) dialog.findViewById(R.id.tvDelaySoundInfo);

//        swAutoplayInfo = (Switch) findViewById(R.id.swAutoplayInfo);


        if (taskEditorFragment.currentKey != null) {
            Item item = taskEditorFragment.getItemFromKey(taskEditorFragment.currentKey);
            if (item != null) {
                tvItemSound.setText(item.getItemSound() == null ? "" : item.getItemSound());
                swAutoplayInfo.setChecked(item.getAutoPlay() == 1 ? true : false);
                setTvDelayMode(item.getSoundDelay());

            }
        }
        ImageButton ibtnItemSoundClose = (ImageButton) dialog.findViewById(R.id.ibtnItemSoundClose);

        tvItemSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerSound();
                CURRENT_FILE_PICKER_ITEM = StaticAccess.TAG_ITEM_SOUND;

            }

        });

        ibtnItemSoundClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvItemSound.getText().length() > 0) {
                    if (taskEditorFragment.currentKey != null) {
                        Item item = taskEditorFragment.getItemFromKey(taskEditorFragment.currentKey);
                        if (item != null) {
                            item.setItemSound("");
                            tvItemSound.setText("");
                        }
                    }
                }
            }


        });

        swAutoplayInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                taskEditorFragment.setAutoPlay(swAutoplayInfo.isChecked() ? 1 : 0);
                if (swAutoplayInfo.isChecked()) {
                    dialogSoundDelay();
                }
            }
        });


        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mp != null)
//                    if(mp.isPlaying()){
                    mp.release();
//                    }
            }
        });

        DialogNavBarHide.navBarHide(this, dialog);

    }


    // Opening file picker for Audio
    public void pickerSound() {

        Intent intSoundPicker = new Intent(activity, ImagePickerActivity.class);
        intSoundPicker.putExtra(StaticAccess.TAG_INTENT_IMG, StaticAccess.TAG_SELECT_SOUND);
        startActivityForResult(intSoundPicker, StaticAccess.MATERIAL_FILE_PICKER);

       /* new MaterialFilePicker()
                .withActivity(activity)
                .withRequestCode(MATERIAL_FILE_PICKER)
                .withFilter(Pattern.compile(".*\\.(aac|mp3|3gpp|wav|amr|m4a|ogg)$")) // Filtering files and directories by file name using regexp
                .withFilterDirectories(false) // Set directories filterable (false by default)
                .withHiddenFiles(false) // Show hidden files and folders
                .start();*/
    }

    // Animation mode created  by reaz
    public void setAnimationMode() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_anim_mode);
        dialog.setCancelable(false);
        tvAnimPositive = (TextView) dialog.findViewById(R.id.tvAnimPositive);
        tvAnimNegative = (TextView) dialog.findViewById(R.id.tvAnimNegative);
        tvAnimFeedBack = (TextView) dialog.findViewById(R.id.tvAnimFeedBack);


        ImageButton ibtnPositiveAnimClose = (ImageButton) dialog.findViewById(R.id.ibtnPositiveAnimClose);
        ImageButton ibtnNegativeAnimClose = (ImageButton) dialog.findViewById(R.id.ibtnNegativeAnimClose);
        ImageButton ibtnFBAnimClose = (ImageButton) dialog.findViewById(R.id.ibtnFBAnimClose);
        ImageButton ibtnAnimDone = (ImageButton) dialog.findViewById(R.id.ibtnAnimDone);


        if (taskEditorFragment.currentTask != null) {
            tvAnimFeedBack.setText(Animanation.animationName[taskEditorFragment.currentTask.getFeedbackAnimation()]);
            tvAnimPositive.setText(Animanation.animationName[taskEditorFragment.currentTask.getPositiveAnimation()]);
            tvAnimNegative.setText(Animanation.animationName[taskEditorFragment.currentTask.getNegativeAnimation()]);
        }
        tvAnimPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                CustomToast.t(activity, "tvPositive Clicked");

                CURRENT_ANIMATION_ITEM = StaticAccess.TAG_ANIM_POSITIVE;
                selectAnimDialog();

            }

        });
        tvAnimNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CustomToast.t(activity, "tvNegative Clicked");
                CURRENT_ANIMATION_ITEM = StaticAccess.TAG_ANIM_NEGATIVE;
                selectAnimDialog();

            }
        });

        tvAnimFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CURRENT_ANIMATION_ITEM = StaticAccess.TAG_ANIM_FEEDBACK;


                selectAnimDialog();
//                onClickCurrentAnim(CURRENT_ANIMATION_ITEM);

            }

        });

        ibtnNegativeAnimClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvAnimNegative.getText().length() > 0) {
                    setNegativeAnimation(0);
                    tvAnimNegative.setText("");
                }


            }

        });
        ibtnPositiveAnimClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvAnimPositive.getText().length() > 0) {
                    setPositiveAnimation(0);
                    tvAnimPositive.setText("");

                }


            }

        });

        ibtnFBAnimClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (tvAnimFeedBack.getText().length() > 0) {
                    setFeedbackAnimation(0);
                    tvAnimFeedBack.setText("");
                }


            }

        });

        ibtnAnimDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });


        DialogNavBarHide.navBarHide(this, dialog);

    }

    // get Animation Name from feedback dialog created by Reaz
    public void setAnimName(int animType) {

        onClickCurrentAnim(CURRENT_ANIMATION_ITEM, animType);
    }

    // Animation type for which button from feedback dialog created by Reaz
    public void onClickCurrentAnim(int type, int animType) {
        switch (type) {
            case StaticAccess.TAG_ANIM_NEGATIVE:
                setNegativeAnimation(animType);
                tvAnimNegative.setText(Animanation.animationName[animType]);
                break;
            case StaticAccess.TAG_ANIM_POSITIVE:
                setPositiveAnimation(animType);
                tvAnimPositive.setText(Animanation.animationName[animType]);
                break;
            case StaticAccess.TAG_ANIM_FEEDBACK:
                setFeedbackAnimation(animType);
                tvAnimFeedBack.setText(Animanation.animationName[animType]);
                break;

            default:

                break;


        }
    }

    // Animation dialog method created by Reaz
    public void selectAnimDialog() {

        String serial[] = {"0", "1", "2", "3", "4", "5"};
        final String animationName[] =  {"No Animation", "Alpha 1", "Alpha 2", "Blink 1", "Down Upside", "Upside Down"};


        ArrayList<Integer> lstgifImg = new ArrayList<Integer>();
        lstgifImg.add(R.drawable.ic_normal);
        lstgifImg.add(R.drawable.ic_alpha);
        lstgifImg.add(R.drawable.ic_alpha_2);
        lstgifImg.add(R.drawable.ic_blink);
        lstgifImg.add(R.drawable.ic_updown);
        lstgifImg.add(R.drawable.ic_updown_2);


        final Dialog dialog = new Dialog(activity, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_gif);
        dialog.setCancelable(true);
        ListView listView = (ListView) dialog.findViewById(R.id.lstGif);
        GifAdapter adapter = new GifAdapter(activity, serial, animationName, lstgifImg);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activity.setAnimName(position);
                dialog.dismiss();
            }
        });
        DialogNavBarHide.navBarHide(activity, dialog);
    }

    // Getting SD card images for Feedback
    public ArrayList<String> getSdcardImages() {

        ArrayList<String> feedBackImages = new ArrayList<String>();
        File[] listFile;
        File file = new File(android.os.Environment.getExternalStorageDirectory(), StaticAccess.ANDROID_DATA + activity.getPackageName() + StaticAccess.FEEDBACK_IMG_PATH);

        if (file.isDirectory()) {
            listFile = file.listFiles();

            for (int i = 0; i < listFile.length; i++) {
                feedBackImages.add(listFile[i].getAbsolutePath());
            }

        }
        return feedBackImages;
    }

    //  Feedback dialog images to SD card Saving
    public void saveToSdCard() {

        ArrayList<Integer> drawablesArr = new ArrayList<Integer>();
        drawablesArr.add(R.drawable.pr_1);
        drawablesArr.add(R.drawable.pr_2);
        drawablesArr.add(R.drawable.pr_3);
        drawablesArr.add(R.drawable.pr_4);
        drawablesArr.add(R.drawable.pr_5);
        drawablesArr.add(R.drawable.pr_6);
        drawablesArr.add(R.drawable.pr_7);
        drawablesArr.add(R.drawable.pr_8);
        drawablesArr.add(R.drawable.pr_9);
        drawablesArr.add(R.drawable.pr_10);
        drawablesArr.add(R.drawable.pr_11);


        for (int i = 0; i < drawablesArr.size(); i++) {

            Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), drawablesArr.get(i));
            File file = new File(Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + activity.getPackageName() + StaticAccess.FEEDBACK_IMG_PATH);

            if (!file.exists()) {
                file.mkdirs();
            }

            try {
                FileOutputStream outputStream = new FileOutputStream(file + "/img" + drawablesArr.get(i) + StaticAccess.DOT_IMAGE_FORMAT);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    // Opening Image Cropper (Transparent)
    public void openCropper(Uri uri) {
        isTaskErrorPic = false;
        com.theartofdev.edmodo.cropper.CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                .setRequestedSize(getBitmapMaxSize(uri), getBitmapMaxSize(uri), CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .start(activity);
    }

    public void openCropper(Uri uri, int flag) {
        if (flag == TaskErrorFlag) {
            isTaskErrorPic = true;
            isFeedbackImages = false;
            com.theartofdev.edmodo.cropper.CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                    .setRequestedSize(getBitmapMaxSize(uri), getBitmapMaxSize(uri), CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                    .start(activity);
        } else if (flag == feedbackImageFlag) {
            isTaskErrorPic = false;
            isFeedbackImages = true;
            com.theartofdev.edmodo.cropper.CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                    .setRequestedSize(getBitmapMaxSize(uri), getBitmapMaxSize(uri), CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                    .start(activity);
        }

    }

    // dialog for editText 1.3.6.12  created by reaz updated by Rokan
    private void dialogForText() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_edit_text_mode);
        dialog.setCancelable(false);

        edtTextEdit = (EditText) dialog.findViewById(R.id.edtTextEdit);
        Button btnCancelEdit = (Button) dialog.findViewById(R.id.btnCancelEdit);
        Button btnOkEdit = (Button) dialog.findViewById(R.id.btnOkEdit);
        //color = ContextCompat.getColor(activity, R.color.black);

        if (currentItem != null)
            taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);

        //edtText.setText("");
        final Item i = taskEditorFragment.getItemFromKey(taskEditorFragment.currentKey);
        edtTextEdit.setText(i.getUserText() == null ? "" : i.getUserText());
        //tvText.setText(i.getUserText() == null ? "" : i.getUserText());

        btnCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOkEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tvText.setText(i.getUserText() == null ? "" : i.getUserText());
                if (edtTextEdit.getText().toString().length() == 0) {
                    CustomToast.t(activity, getResources().getString(R.string.textAdd));
                    return;
                }
                i.setUserText(edtTextEdit.getText().toString());
                tvText.setText(i.getUserText() == null ? "" : i.getUserText());

                dialog.dismiss();
            }
        });
        DialogNavBarHide.navBarHide(this, dialog);

    }


    private void dialogErrorText() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_error_text_);
        dialog.setCancelable(false);

        final EditText edtTextError = (EditText) dialog.findViewById(R.id.edtTextError);
        Button btnCancelError = (Button) dialog.findViewById(R.id.btnCancelError);
        Button btnOkError = (Button) dialog.findViewById(R.id.btnOkError);
        if (taskEditorFragment.currentTask != null)
            if (taskEditorFragment.currentTask.getErrortext() != null && taskEditorFragment.currentTask.getErrortext().length() > 0) {
                edtTextError.setText(taskEditorFragment.currentTask.getErrortext());
            } else {
                edtTextError.setText(getResources().getString(R.string.emptyErrorMsg));
            }

        btnCancelError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOkError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tvText.setText(i.getUserText() == null ? "" : i.getUserText());
                if (edtTextError.getText().toString().length() == 0) {
                    CustomToast.t(activity, getResources().getString(R.string.textAdd));
                    return;
                }

                tvError.setText(edtTextError.getText().toString());


                dialog.dismiss();
            }
        });
        DialogNavBarHide.navBarHide(this, dialog);

    }


    // Dialog for back permission created by Rokan
    private void dialogBackPermission() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_back_permission);

        final TextView tvBackPermission = (TextView) dialog.findViewById(R.id.tvBackPermission);

        if (taskEditorFragment.itemMap.size() > 0) {
            tvBackPermission.setText(getResources().getText(R.string.savePermission));
            dialog.setCancelable(true);
        } else {
            tvBackPermission.setText(getResources().getText(R.string.BackPermission));
            dialog.setCancelable(false);
        }


        Button btnCancelPermission = (Button) dialog.findViewById(R.id.btnCancelPermission);
        Button btnOkPermission = (Button) dialog.findViewById(R.id.btnOkPermission);

        btnCancelPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (taskEditorFragment.itemMap.size() > 0) {
                    Intent intent = new Intent(activity, TaskPackActivity.class);
                    intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, taskPackId);
                    startActivity(intent);
                    finish();

                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }


            }
        });
        btnOkPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (taskEditorFragment.itemMap.size() > 0) {
                    taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
                    if (taskEditorFragment.saveTask() != 0) {
                        bottomDrawerCustomeViews.clear();
                        createBottomDrawer();
                        reGenerateBottomDrawer();

                        Intent intent = new Intent(activity, TaskPackActivity.class);
                        intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, taskPackId);
                        startActivity(intent);

                        finish();

                        dialog.dismiss();
                    }
                } else {
                    Intent intent = new Intent(activity, TaskPackActivity.class);
                    intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, taskPackId);
                    startActivity(intent);
                    finish();

                    dialog.dismiss();
                }
            }
        });
        DialogNavBarHide.navBarHide(this, dialog);

    }

    // dialog for Copy Task created by Rokan
    private void copyDialog() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_back_permission);
        dialog.setCancelable(false);

        final TextView tvCopyText = (TextView) dialog.findViewById(R.id.tvBackPermission);
        Button btnCancelPermission = (Button) dialog.findViewById(R.id.btnCancelPermission);
        Button btnOkPermission = (Button) dialog.findViewById(R.id.btnOkPermission);
        tvCopyText.setText(getResources().getString(R.string.copyTask));
       /* if (currentItem != null)
            taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
*/

        btnCancelPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOkPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
                if (taskEditorFragment.itemMap.size() > 0) {
                    taskEditorFragment.saveTask();
                    taskEditorFragment.copyTask();
                    bottomDrawerCustomeViews.clear();
                    createBottomDrawer();
                    reGenerateBottomDrawer();
                    taskEditorFragment.clearItems();
                    taskEditorFragment.loadTask(tasks.get(tasks.size() - 1));
  /*              }else {
                    taskEditorFragment.saveTask();
                    taskEditorFragment.copyTask();
                    bottomDrawerCustomeViews.clear();
                    createBottomDrawer();
                    reGenerateBottomDrawer();
                    taskEditorFragment.clearItems();
                    taskEditorFragment.loadTask(tasks.get(tasks.size()-1));*/
//                }
                } else {
                    CustomToast.t(activity, getResources().getString(R.string.addItem));
                }

                dialog.dismiss();
            }
        });
        DialogNavBarHide.navBarHide(this, dialog);

    }

    // font dialog
    private void dialogFont() {

        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_font_edit_mode);
        dialog.setCancelable(true);

        final ListView lvFont = (ListView) dialog.findViewById(R.id.lvFont);

        FontAdapter fontAdapter = new FontAdapter(activity, StaticAccess.fontName);
        lvFont.setAdapter(fontAdapter);
        lvFont.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fontValue = position;
//                setFontType(position);
                tvFontText.setText(StaticAccess.fontName[position]);
                dialog.dismiss();
            }
        });
        DialogNavBarHide.navBarHide(this, dialog);
    }

    // Sound delay dialog By Rokan
    private void dialogSoundDelay() {

        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sound_delay);
        dialog.setCancelable(true);

        final ListView lvSoundDelay = (ListView) dialog.findViewById(R.id.lvSoundDelay);

        SoundDelayAdapter soundDelayAdapter = new SoundDelayAdapter(activity, StaticAccess.soundDelay);
        lvSoundDelay.setAdapter(soundDelayAdapter);
        lvSoundDelay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                soundDelayValue = position;
//                setFontType(position);
                setSoundDelay(position);
                tvDelaySoundInfo.setText(StaticAccess.soundDelay[position]);

                dialog.dismiss();
            }
        });
        DialogNavBarHide.navBarHide(this, dialog);
    }


    //dialogForURL by Rokan
    private void dialogForURL() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_edit_url);
        dialog.setCancelable(false);

        final EditText edtTextEditURL = (EditText) dialog.findViewById(R.id.edtTextEditURL);
        Button btnCancelEditURL = (Button) dialog.findViewById(R.id.btnCancelEditURL);
        Button btnOkEditURL = (Button) dialog.findViewById(R.id.btnOkEditURL);

        RadioGroup rdoGrpURL = (RadioGroup) dialog.findViewById(R.id.rdoGrpURL);
        final RadioButton rdoUrlHttp = (RadioButton) dialog.findViewById(R.id.rdoUrlHttp);
        final RadioButton rdoUrlHttps = (RadioButton) dialog.findViewById(R.id.rdoUrlHttps);

        if (urlTag.equals(StaticAccess.HTTP))
            rdoGrpURL.check(R.id.rdoUrlHttp);
        else
            rdoGrpURL.check(R.id.rdoUrlHttps);

        rdoUrlHttp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urlTag = StaticAccess.HTTP;
            }
        });
        rdoUrlHttps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urlTag = StaticAccess.HTTPS;
            }
        });

        if (currentItem != null)
            taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
        final Item i = taskEditorFragment.getItemFromKey(taskEditorFragment.currentKey);
        String url = i.getOpenUrl() == null ? "" : i.getOpenUrl();
        if (url.startsWith(StaticAccess.HTTPS)) {
            edtTextEditURL.setText(url.substring(8));
            rdoUrlHttps.setChecked(true);
            rdoUrlHttp.setChecked(false);

        } else if (url.startsWith(StaticAccess.HTTP)) {
            edtTextEditURL.setText(url.substring(7));
            rdoUrlHttp.setChecked(true);
            rdoUrlHttps.setChecked(false);

        }

        btnCancelEditURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOkEditURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtTextEditURL.getText().toString().length() == 0) {
                    CustomToast.t(activity, getResources().getString(R.string.urlAdd));
                } else {
                    taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
                    if (RegularExpCheck.IsMatch(urlTag.concat(edtTextEditURL.getText().toString()))) {
                        taskEditorFragment.setOpenUrl(urlTag.concat(edtTextEditURL.getText().toString()));
                        CustomToast.t(activity, urlTag.concat(edtTextEditURL.getText().toString()));

                    } else {
                        CustomToast.t(activity, getResources().getString(R.string.InvalidUrl));
                    }
                    dialog.dismiss();
                }


            }
        });
        DialogNavBarHide.navBarHide(this, dialog);

    }


    //dialogForURL by Rokan
    private void dialogBorderPixel() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_pixel);
        dialog.setCancelable(false);

        final EditText edtBorderPixel = (EditText) dialog.findViewById(R.id.edtBorderPixel);
        Button btnCancelBorderPixel = (Button) dialog.findViewById(R.id.btnCancelBorderPixel);
        Button btnOkBorderPixel = (Button) dialog.findViewById(R.id.btnOkBorderPixel);


        if (currentItem != null)
            taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
        final Item i = taskEditorFragment.getItemFromKey(taskEditorFragment.currentKey);
        edtBorderPixel.setText(String.valueOf(i.getBorderPixel() <= 0 ? 0 : i.getBorderPixel()));


        btnCancelBorderPixel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOkBorderPixel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int borderPixel = 0;
                borderPixel = Integer.parseInt(edtBorderPixel.getText().toString());
                if (edtBorderPixel.getText().toString().length() == 0) {
                    CustomToast.t(activity, getResources().getString(R.string.AddPixel));
                } else {
                    if (borderPixel > 0 && borderPixel <= 100) {

                        tvBorderpxForm.setText(edtBorderPixel.getText().toString());

                        dialog.dismiss();
                    } else {
                        CustomToast.t(activity, getResources().getString(R.string.inputPixel));
                    }


                }


            }
        });
        DialogNavBarHide.navBarHide(this, dialog);

    }

    public void selectedTextAlignment() {

        if (fontAlign == LimbikaView.ALIGN_LEFT) {
            ibtnAlignLeftText.setBackgroundColor(getResources().getColor(R.color.black));
            ibtnAlignCenterText.setBackgroundColor(Color.TRANSPARENT);
            ibtnAlignAllText.setBackgroundColor(Color.TRANSPARENT);
            ibtnAlignRightText.setBackgroundColor(Color.TRANSPARENT);
        } else if (fontAlign == LimbikaView.ALIGN_RIGHT) {
            ibtnAlignRightText.setBackgroundColor(getResources().getColor(R.color.black));
            ibtnAlignLeftText.setBackgroundColor(Color.TRANSPARENT);
            ibtnAlignCenterText.setBackgroundColor(Color.TRANSPARENT);
            ibtnAlignAllText.setBackgroundColor(Color.TRANSPARENT);
        } else if (fontAlign == LimbikaView.ALIGN_CENTER) {
            ibtnAlignCenterText.setBackgroundColor(getResources().getColor(R.color.black));
            ibtnAlignLeftText.setBackgroundColor(Color.TRANSPARENT);
            ibtnAlignRightText.setBackgroundColor(Color.TRANSPARENT);
            ibtnAlignAllText.setBackgroundColor(Color.TRANSPARENT);
        }

    }

    // Asynctask for loading feedback dialog
    class FeedbackAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(getResources().getString(R.string.loadingFeedbackImage));
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            activity.saveToSdCard();
            feedBackImages = activity.getSdcardImages();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {


            taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
            Task task = taskEditorFragment.getCurrentTask();

            if (task != null) {

                if (feedbackEditorDialog != null && feedbackEditorDialog.isShowing()) {
                    feedbackEditorDialog.dismiss();
                }

                feedbackEditorDialog = new FeedbackEditorDialog(activity, feedBackImages, task.getFeedbackType());
                DialogNavBarHide.navBarHide(activity, feedbackEditorDialog);

                if (pDialog.isShowing()) {
                    pDialog.dismiss();

                }
            }


        }

    }

    // dialog for ShowSoundPath created by Rokan
    public void dialogShowSoundPath(String soundFileName) {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_show_sound_path);
        dialog.setCancelable(false);

        final TextView tvSoundPath = (TextView) dialog.findViewById(R.id.tvSoundPath);
        tvSoundPath.setText(soundFileName);

        Button btnSoundPathOk = (Button) dialog.findViewById(R.id.btnSoundPathOk);
        btnSoundPathOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mp != null) {
                    mp.release();
                }

                dialog.dismiss();
            }
        });
        DialogNavBarHide.navBarHide(this, dialog);


    }

    // set Delay  Created by reaz.
    private void setSoundDelay(int position) {
        taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
        switch (position) {
            case StaticAccess.TAG_TYPE_NONE:
                taskEditorFragment.setSoundDelay(StaticAccess.TAG_TYPE_SOUND_DELAY_NONE);
                break;
            case StaticAccess.TAG_TYPE_ONE:
                taskEditorFragment.setSoundDelay(StaticAccess.TAG_TYPE_SOUND_DELAY_ONE_SEC);
                break;
            case StaticAccess.TAG_TYPE_TWO:
                taskEditorFragment.setSoundDelay(StaticAccess.TAG_TYPE_SOUND_DELAY_THREE_SEC);
                break;
            case StaticAccess.TAG_TYPE_THREE:
                taskEditorFragment.setSoundDelay(StaticAccess.TAG_TYPE_SOUND_DELAY_FIVE_SEC);
                break;
            case StaticAccess.TAG_TYPE_FOUR:
                taskEditorFragment.setSoundDelay(StaticAccess.TAG_TYPE_SOUND_DELAY_SEVEN_SEC);
                break;
            case StaticAccess.TAG_TYPE_FIVE:
                taskEditorFragment.setSoundDelay(StaticAccess.TAG_TYPE_SOUND_DELAY_TEN_SEC);
                break;
        }
    }

    // textShow for Delay  created by reaz
    private void setTvDelayMode(int soundDelay) {
        switch (soundDelay) {
            case StaticAccess.TAG_TYPE_SOUND_DELAY_NONE:
                tvDelaySoundInfo.setText(StaticAccess.soundDelay[StaticAccess.TAG_TYPE_NONE]);
                break;
            case StaticAccess.TAG_TYPE_SOUND_DELAY_ONE_SEC:

                tvDelaySoundInfo.setText(StaticAccess.soundDelay[StaticAccess.TAG_TYPE_ONE]);
                break;
            case StaticAccess.TAG_TYPE_SOUND_DELAY_THREE_SEC:

                tvDelaySoundInfo.setText(StaticAccess.soundDelay[StaticAccess.TAG_TYPE_TWO]);
                break;
            case StaticAccess.TAG_TYPE_SOUND_DELAY_FIVE_SEC:
                tvDelaySoundInfo.setText(StaticAccess.soundDelay[StaticAccess.TAG_TYPE_THREE]);

                break;
            case StaticAccess.TAG_TYPE_SOUND_DELAY_SEVEN_SEC:
                tvDelaySoundInfo.setText(StaticAccess.soundDelay[StaticAccess.TAG_TYPE_FOUR]);

                break;
            case StaticAccess.TAG_TYPE_SOUND_DELAY_TEN_SEC:
                tvDelaySoundInfo.setText(StaticAccess.soundDelay[StaticAccess.TAG_TYPE_FIVE]);
                break;
        }
    }

    // for Error Image created by reaz
    public void setTaskErrorPic(Bitmap bitmap) {
        taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
        if (bitmap != null) {
            taskEditorFragment.setErrorImage(bitmap);

            imgProc.setImageWith_loader(ivImageError, taskEditorFragment.getErrorImage());
            isTaskErrorPic = false;
        }
    }

    // for Error Image created by reaz
    public void setFeedBackPic(Bitmap bitmap) {
        if (bitmap != null) {
            imgProc.feedBackImageSave(bitmap);
            isTaskErrorPic = false;
            isFeedbackImages = false;
            FeedbackAsyncTask feedbackAsyncTask = new FeedbackAsyncTask();
            feedbackAsyncTask.execute();

        }
    }

    public void targetChildEnable() {
        if (taskEditorFragment.checkTarget()) {
            swTargetInfo.setChecked(true);
        } else {
            swTargetInfo.setChecked(false);
        }
        if (taskEditorFragment.checkChild()) {
            swChildInfo.setChecked(true);
        } else {
            swChildInfo.setChecked(false);
        }
    }

    private void newTaskWithOutModeDialog() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_new_task);
        dialog.setCancelable(false);

        final TextView tvCopyText = (TextView) dialog.findViewById(R.id.tvNewTaskPermission);
        Button btnCancelPermission = (Button) dialog.findViewById(R.id.btnCancelPermission);
        Button btnOkPermission = (Button) dialog.findViewById(R.id.btnOkPermission);
        tvCopyText.setText(getResources().getString(R.string.newTask));


        btnCancelPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  taskEditorFragment.newTask();
                fbtnShowLeftDrawer.performClick();
                currentRightDrawer = null;
                setTaskType();*/
                dialog.dismiss();
            }
        });
        btnOkPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (taskType.equals(TaskType.Normal)) {
                    //genera
                    setGeneralEnable(true);
                    setDragDropEnable(false);
                    setAssistiveEnable(false);
                    setReadEnable(false);
                    setWriteEnable(false);
                    setSequenceEnable(false);

                } else if (taskType.equals(TaskType.Assistive)) {
                    // ass
                    setGeneralEnable(false);
                    setDragDropEnable(false);
                    setAssistiveEnable(true);
                    setReadEnable(false);
                    setWriteEnable(false);
                    setSequenceEnable(false);
                } else if (taskType.equals(TaskType.DragDrop)) {
                    //drag
                    setGeneralEnable(false);
                    setAssistiveEnable(false);
                    setDragDropEnable(true);
                    setReadEnable(false);
                    setWriteEnable(false);
                    setSequenceEnable(false);
                } else if (taskType.equals(TaskType.Read)) {
                    //drag
                    setGeneralEnable(true);
                    setAssistiveEnable(false);
                    setDragDropEnable(false);
                    setReadEnable(true);
                    setWriteEnable(false);
                    setSequenceEnable(false);
                } else if (taskType.equals(TaskType.Write)) {
                    //drag
                    setGeneralEnable(true);
                    setAssistiveEnable(false);
                    setDragDropEnable(false);
                    setReadEnable(false);
                    setWriteEnable(true);
                    setSequenceEnable(false);
                } else if (taskType.equals(TaskType.Sequence)) {
                    //drag
                    setGeneralEnable(true);
                    setAssistiveEnable(false);
                    setDragDropEnable(false);
                    setReadEnable(false);
                    setWriteEnable(false);
                    setSequenceEnable(true);
                }
                //setTaskType();
                taskEditorFragment.saveTask();
                bottomDrawerCustomeViews.clear();
                createBottomDrawer();
                reGenerateBottomDrawer();
                taskEditorFragment.newTask();
                fbtnShowLeftDrawer.performClick();
                currentRightDrawer = null;
                dialog.dismiss();
//                taskType= taskTypeNew;

            }
        });
        hideBottomDrawer(llBottomDrawer);
        DialogNavBarHide.navBarHide(this, dialog);
    }

    //activity id,color and Listener set
    private void findViewByIdListenerCOLOR() {
        /******************************************* Left Drawer Editor ***********************************************/
        fbtnShowLeftDrawer = (FloatingActionButton) findViewById(R.id.fbtnShowLeftDrawer);
        flContentView = (FrameLayout) findViewById(R.id.flContentView);

        ibtnBack = (ImageButton) findViewById(R.id.ibtnBack);
        ibtnSquareAdd = (ImageButton) findViewById(R.id.ibtnSquareAdd);
        ibtnCircleAdd = (ImageButton) findViewById(R.id.ibtnCircleAdd);
        ibtnEdit = (ImageButton) findViewById(R.id.ibtnEdit);
        ibtnForm = (ImageButton) findViewById(R.id.ibtnForm);
        ibtnInfo = (ImageButton) findViewById(R.id.ibtnInfo);
        ibtnCoppyPast = (ImageButton) findViewById(R.id.ibtnCoppyPast);
        ibtnAlign = (ImageButton) findViewById(R.id.ibtnAlign);
        ibtnScreenAlign = (ImageButton) findViewById(R.id.ibtnScreenAlign);
        ibtnPosition = (ImageButton) findViewById(R.id.ibtnPosition);
        ibtnAnimation = (ImageButton) findViewById(R.id.ibtnAnimation);
        ibtnError = (ImageButton) findViewById(R.id.ibtnError);
        ibtnGridOn = (ImageButton) findViewById(R.id.ibtnGridOn);
        ibtnSound = (ImageButton) findViewById(R.id.ibtnSound);
        ibtnFeedBack = (ImageButton) findViewById(R.id.ibtnFeedBack);
        ibtnShowTopDrawer = (ImageButton) findViewById(R.id.ibtnShowTopDrawer);
        ibtnShowBottomDrawer = (ImageButton) findViewById(R.id.ibtnShowBottomDrawer);
        llSequenceText = (LinearLayout) findViewById(R.id.llSequenceText);
        ibtnSequenceText = (ImageButton) findViewById(R.id.ibtnSequenceText);

        /***************************************** right drawer editor Edit  ***************************/
        swCloseAppEdit = (Switch) findViewById(R.id.swCloseAppEdit);
        swNavigatesEdit = (Switch) findViewById(R.id.swNavigatesEdit);
        swOpenAppEdit = (Switch) findViewById(R.id.swOpenAppEdit);
        ibtnURLEdit = (ImageButton) findViewById(R.id.ibtnURLEdit);
        //edtURLEdit = (EditText) findViewById(R.id.edtURLEdit);
        ibtnAddImageEdit = (ImageButton) findViewById(R.id.ibtnAddImageEdit);
        ibtnAddTextEdit = (ImageButton) findViewById(R.id.ibtnAddTextEdit);
        ibtnItemSoundEdit = (ImageButton) findViewById(R.id.ibtnItemSoundEdit);
        ibtnReadText = (ImageButton) findViewById(R.id.ibtnReadText);
        lnReadText = (LinearLayout) findViewById(R.id.lnReadText);
        tvReadText = (TextView) findViewById(R.id.tvReadText);

        ibtnWriteText = (ImageButton) findViewById(R.id.ibtnWriteText);
        lnWriteText = (LinearLayout) findViewById(R.id.lnWriteText);
        tvWriteText = (TextView) findViewById(R.id.tvWriteText);

        /****************************************** right drawer editor Form  ***********************************/

        tvBgColorForm = (CircularTextView) findViewById(R.id.tvBgColorForm);
        tvBorderColorForm = (CircularTextView) findViewById(R.id.tvBorderColorForm);
        ibtnBorderpxForm = (ImageButton) findViewById(R.id.ibtnBorderpxForm);
        tvBorderpxForm = (TextView) findViewById(R.id.tvBorderpxForm);
        ibtnDoneForm = (ImageButton) findViewById(R.id.ibtnDoneForm);
        swRoundCornerForm = (Switch) findViewById(R.id.swRoundCornerForm);


        /******************************************** right drawer editor Info  *******************************/
        swCorrectInfo = (Switch) findViewById(R.id.swCorrectInfo);
        swIncorrectInfo = (Switch) findViewById(R.id.swIncorrectInfo);
        swTargetInfo = (Switch) findViewById(R.id.swTargetInfo);
        swChildInfo = (Switch) findViewById(R.id.swChildInfo);
        ibtnDragDropInfo = (ImageButton) findViewById(R.id.ibtnDragDropInfo);
        ibtnLinkdToInfo = (ImageButton) findViewById(R.id.ibtnLinkdToInfo);
        swShowedByInfo = (Switch) findViewById(R.id.swShowedByInfo);
        swHiddenByInfo = (Switch) findViewById(R.id.swHiddenByInfo);
        //comment reaz
        swMandatoryInfo = (Switch) findViewById(R.id.swMandatoryInfo);
//        ibtnDelaySoundInfo = (ImageButton) findViewById(R.id.ibtnDelaySoundInfo);
//        tvDelaySoundInfo = (TextView) findViewById(R.id.tvDelaySoundInfo);


        lnGeneralCorrect = (LinearLayout) findViewById(R.id.lnGeneralCorrect);
        lnGeneralInCorrect = (LinearLayout) findViewById(R.id.lnGeneralInCorrect);
        lnDrag = (LinearLayout) findViewById(R.id.lnDrag);
        lnAssistive = (LinearLayout) findViewById(R.id.lnAssistive);
        lnTarget = (LinearLayout) findViewById(R.id.lnTarget);
        lnChild = (LinearLayout) findViewById(R.id.lnChild);
        tvReadable = (TextView) findViewById(R.id.tvReadable);


        /*************************************** right drawer editor Align  **********************************/
        ibtnAlignCenterAlign = (ImageButton) findViewById(R.id.ibtnAlignCenterAlign);
        ibtnAlignLeftAlign = (ImageButton) findViewById(R.id.ibtnAlignLeftAlign);
        ibtnAlignRightAlign = (ImageButton) findViewById(R.id.ibtnAlignRightAlign);
        ibtnAlignBottomAlign = (ImageButton) findViewById(R.id.ibtnAlignBottomAlign);
        ibtnAlignTopAlign = (ImageButton) findViewById(R.id.ibtnAlignTopAlign);
        ibtnAlignXCenter = (ImageButton) findViewById(R.id.ibtnAlignXCenter);
        ibtnAlignYCenter = (ImageButton) findViewById(R.id.ibtnAlignYCenter);


        /*************************************** right drawer editor Screen Align  **********************************/
        ibtnAlignScreenCenterAlign = (ImageButton) findViewById(R.id.ibtnAlignScreenCenterAlign);
        ibtnAlignScreenTopAlign = (ImageButton) findViewById(R.id.ibtnAlignScreenTopAlign);
        ibtnAlignScreenBottomAlign = (ImageButton) findViewById(R.id.ibtnAlignScreenBottomAlign);
        ibtnAlignScreenLeftAlign = (ImageButton) findViewById(R.id.ibtnAlignScreenLeftAlign);
        ibtnAlignScreenRightAlign = (ImageButton) findViewById(R.id.ibtnAlignScreenRightAlign);
        ibtnAlignXScreenCenter = (ImageButton) findViewById(R.id.ibtnAlignXScreenCenter);
        ibtnAlignYScreenCenter = (ImageButton) findViewById(R.id.ibtnAlignYScreenCenter);


        /********************************************** right drawer editor Position  ****************************/
        ibtnFrontTopPosition = (ImageButton) findViewById(R.id.ibtnFrontTopPosition);
        ibtnBackTopPosition = (ImageButton) findViewById(R.id.ibtnBackTopPosition);
        ibtnFrontStepPosition = (ImageButton) findViewById(R.id.ibtnFrontStepPosition);
        ibtnBackStepPosition = (ImageButton) findViewById(R.id.ibtnBackStepPosition);


        /********************************************** right drawer editor Error  ****************************/
        ibtnAddImageError = (ImageButton) findViewById(R.id.ibtnAddImageError);
        ibtnAddTextError = (ImageButton) findViewById(R.id.ibtnAddTextError);
        tvBgColorError = (CircularTextView) findViewById(R.id.tvBgColorError);
        ivImageError = (ImageView) findViewById(R.id.ivImageError);
        ibtnDoneError = (ImageButton) findViewById(R.id.ibtnDoneError);
        tvError = (TextView) findViewById(R.id.tvError);


        /************************************* right drawer editor Text   **********************************/

        ibtnAlignLeftText = (ImageButton) findViewById(R.id.ibtnAlignLeftText);
        ibtnAlignCenterText = (ImageButton) findViewById(R.id.ibtnAlignCenterText);
        ibtnAlignAllText = (ImageButton) findViewById(R.id.ibtnAlignAllText);
        ibtnAlignRightText = (ImageButton) findViewById(R.id.ibtnAlignRightText);

        // comment by reaz
        ibtnText = (ImageButton) findViewById(R.id.ibtnText);
        ibtnFontText = (ImageButton) findViewById(R.id.ibtnFontText);
        tvFontText = (TextView) findViewById(R.id.tvFontText);
        tvText = (TextView) findViewById(R.id.tvText);
        seekBarText = (SeekBar) findViewById(R.id.seekBarText);
        tvShadowText = (CircularTextView) findViewById(R.id.tvShadowText);
        ibtnDoneText = (ImageButton) findViewById(R.id.ibtnDoneText);
        /******************************************* Bottom Drawer Editor ***********************************************/
        ibtnBottomCopyPaste = (ImageButton) findViewById(R.id.ibtnBottomCopyPaste);
        ibtnBottomNew = (ImageButton) findViewById(R.id.ibtnBottomNew);
        ibtNewSameTaskMode = (ImageButton) findViewById(R.id.ibtNewSameTaskMode);
        ibtnBottomSave = (ImageButton) findViewById(R.id.ibtnBottomSave);
        ibtnBottomDelete = (ImageButton) findViewById(R.id.ibtnBottomDelete);
        ibtnBottomPreview = (ImageButton) findViewById(R.id.ibtnBottomPreview);

        /************************************************   Right Drawer editor Layout Initialize *************************************/
        incLeftDrawerEditor = findViewById(R.id.incLeftDrawerEditor);
        incRightDrawerEditorEdit = findViewById(R.id.incRightDrawerEditorEdit);
        incRightDrawerEditorForm = findViewById(R.id.incRightDrawerEditorForm);
        incRightDrawerEditorAlign = findViewById(R.id.incRightDrawerEditorAlign);
        incRightDrawerEditorScreenAlign = findViewById(R.id.incRightDrawerEditorScreenAlign);
        incRightDrawerEditorInfo = findViewById(R.id.incRightDrawerEditorInfo);
        incRightDrawerEditorPosition = findViewById(R.id.incRightDrawerEditorPosition);
        incRightDrawerEditorError = findViewById(R.id.incRightDrawerEditorError);
        incRightDrawerEditorText = findViewById(R.id.incRightDrawerEditorText);
        incWidgetArrangement = findViewById(R.id.incWidgetArrangement);


        /************************************************   Right Drawer editor Layout  ***********************************/
        incLeftDrawerEditor.setVisibility(View.GONE);
        incRightDrawerEditorEdit.setVisibility(View.GONE);
        incRightDrawerEditorForm.setVisibility(View.GONE);
        incRightDrawerEditorAlign.setVisibility(View.GONE);
        incRightDrawerEditorScreenAlign.setVisibility(View.GONE);
        incRightDrawerEditorInfo.setVisibility(View.GONE);
        incRightDrawerEditorPosition.setVisibility(View.GONE);
        incRightDrawerEditorError.setVisibility(View.GONE);
        incRightDrawerEditorText.setVisibility(View.GONE);
        incWidgetArrangement.setVisibility(View.GONE);


        /*********************************************** Left drawer editor listener ***************************************/
        fbtnShowLeftDrawer.setOnClickListener(this);
        flContentView.setOnClickListener(this);

        ibtnBack.setOnClickListener(this);
        ibtnSquareAdd.setOnClickListener(this);
        ibtnCircleAdd.setOnClickListener(this);
        ibtnEdit.setOnClickListener(this);
        ibtnForm.setOnClickListener(this);
        ibtnInfo.setOnClickListener(this);
        ibtnCoppyPast.setOnClickListener(this);
        ibtnAlign.setOnClickListener(this);
        ibtnScreenAlign.setOnClickListener(this);
        ibtnPosition.setOnClickListener(this);
        ibtnAnimation.setOnClickListener(this);
        ibtnError.setOnClickListener(this);
        ibtnGridOn.setOnClickListener(this);
        ibtnSound.setOnClickListener(this);
        ibtnFeedBack.setOnClickListener(this);
        ibtnShowTopDrawer.setOnClickListener(this);
        ibtnShowBottomDrawer.setOnClickListener(this);
        ibtnSequenceText.setOnClickListener(this);

        /************************************************  1.4 rightDrawer editor edit ********************************************/
        ibtnURLEdit.setOnClickListener(this);
        ibtnAddImageEdit.setOnClickListener(this);
        ibtnAddTextEdit.setOnClickListener(this);
        ibtnItemSoundEdit.setOnClickListener(this);
        ibtnReadText.setOnClickListener(this);
        ibtnWriteText.setOnClickListener(this);

        /************************************************* rightDrawer editor form *****************************************/

        tvBgColorForm.setOnClickListener(this);
        tvBorderColorForm.setOnClickListener(this);
        ibtnBorderpxForm.setOnClickListener(this);
        ibtnDoneForm.setOnClickListener(this);


        /********************************************** rightDrawer editor info  ******************************/
        ibtnDragDropInfo.setOnClickListener(this);
        ibtnLinkdToInfo.setOnClickListener(this);


        /******************************************* rightDrawer editor align ********************************/
        ibtnAlignCenterAlign.setOnClickListener(this);
        ibtnAlignLeftAlign.setOnClickListener(this);
        ibtnAlignRightAlign.setOnClickListener(this);
        ibtnAlignBottomAlign.setOnClickListener(this);
        ibtnAlignTopAlign.setOnClickListener(this);
        ibtnAlignXCenter.setOnClickListener(this);
        ibtnAlignYCenter.setOnClickListener(this);


        /******************************************* rightDrawer editor Screen align ********************************/
        ibtnAlignScreenCenterAlign.setOnClickListener(this);
        ibtnAlignScreenTopAlign.setOnClickListener(this);
        ibtnAlignScreenBottomAlign.setOnClickListener(this);
        ibtnAlignScreenLeftAlign.setOnClickListener(this);
        ibtnAlignScreenRightAlign.setOnClickListener(this);
        ibtnAlignXScreenCenter.setOnClickListener(this);
        ibtnAlignYScreenCenter.setOnClickListener(this);


        /**************************************** rightDrawer editor position  **********************************/
        ibtnFrontTopPosition.setOnClickListener(this);
        ibtnBackTopPosition.setOnClickListener(this);
        ibtnFrontStepPosition.setOnClickListener(this);
        ibtnBackStepPosition.setOnClickListener(this);


        /**************************************** rightDrawer editor Error  **********************************/
        ibtnAddImageError.setOnClickListener(this);
        ibtnAddTextError.setOnClickListener(this);
        ibtnDoneError.setOnClickListener(this);
        tvBgColorError.setOnClickListener(this);


        /*********************************************** rightDrawer editor Text ********************************************************/
        ibtnFontText.setOnClickListener(this);
        ibtnAlignLeftText.setOnClickListener(this);
        ibtnAlignCenterText.setOnClickListener(this);
        ibtnAlignAllText.setOnClickListener(this);
        ibtnAlignRightText.setOnClickListener(this);
        ibtnText.setOnClickListener(this);
        seekBarText.setOnSeekBarChangeListener(this);
        tvShadowText.setOnClickListener(this);
        ibtnDoneText.setOnClickListener(this);
        seekBarText.setProgress(25);


        /************************************* Bottom drawer editor *****************************************/
        ibtnBottomCopyPaste.setOnClickListener(this);
        ibtnBottomNew.setOnClickListener(this);
        ibtNewSameTaskMode.setOnClickListener(this);
        ibtnBottomSave.setOnClickListener(this);
        ibtnBottomDelete.setOnClickListener(this);
        ibtnBottomPreview.setOnClickListener(this);

        IBSlideToBottom = (ImageButton) findViewById(R.id.IBHideSlide);
        IBSlideToBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBottomDrawer(llBottomDrawer);
                fbtnShowLeftDrawer.setVisibility(View.VISIBLE);
            }
        });


        touchListener = new OnDoubleTapListener(this, this);

        /**creating the top drawer **/
        llTopDrawerItem = (LinearLayout) findViewById(R.id.llTopDrawerItem);
        int[] colorPicker = getResources().getIntArray(R.array.colorPicker);
        for (int i = 0; i < colorPicker.length; i++) {
            topDrawerCustomView = new TopDrawerCustomView(activity);
            topDrawerCustomView.setIds(i);
            topDrawerCustomView.setTextId(i);
            topDrawerCustomView.setText();
            topDrawerCustomView.setTag("Top");

            topDrawerCustomView.setTopColorPic(i);

            topDrawerCustomView.setOnTouchListener(touchListener);
            topDrawerCustomViews.add(topDrawerCustomView);
        }

        /**creating the bottom panel **/
        llBottomDrawerItem = (LinearLayout) findViewById(R.id.llBottomDrawerItem);

        createBottomDrawer();
        reGenerateBottomDrawer();
        generateTopDrawer();
        switcherListener();

        //new EditTask().execute();
        showLeftDrawer(incLeftDrawerEditor);
        isLeftDrawerShown = true;
        // buttonShowHide(1);
        fbtnShowLeftDrawer.setVisibility(View.INVISIBLE);

        tvShadowText.setSolidColor(ContextCompat.getColor(this, R.color.black));
        tvBgColorForm.setSolidColor(ContextCompat.getColor(this, R.color.white));
        tvBorderColorForm.setSolidColor(ContextCompat.getColor(this, R.color.white));
        tvBgColorError.setSolidColor(ContextCompat.getColor(this, R.color.redLight));

        taskBackgroundColor = ContextCompat.getColor(this, R.color.white);
        incWidgetArrangement.setVisibility(View.INVISIBLE);
    }

    // dialog for editText 1.4.7 for ReadText  created by reaz
    private void dialogForReadText() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_edit_text_mode);
        dialog.setCancelable(false);

        edtTextEdit = (EditText) dialog.findViewById(R.id.edtTextEdit);
        Button btnCancelEdit = (Button) dialog.findViewById(R.id.btnCancelEdit);
        Button btnOkEdit = (Button) dialog.findViewById(R.id.btnOkEdit);
        //color = ContextCompat.getColor(activity, R.color.black);

        if (currentItem != null)
            taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);

        //edtText.setText("");
        final Item i = taskEditorFragment.getItemFromKey(taskEditorFragment.currentKey);
        edtTextEdit.setText(i.getReadText() == null ? "" : i.getReadText());
        //tvReadText.setText(i.getReadText() == null ? "" : i.getReadText());

        btnCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOkEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tvText.setText(i.getUserText() == null ? "" : i.getUserText());
                if (edtTextEdit.getText().toString().length() == 0) {
                    CustomToast.t(activity, getResources().getString(R.string.textAdd));
                    return;
                }
                taskEditorFragment.setItemReadText(edtTextEdit.getText().toString());
                tvReadText.setText(i.getReadText() == null ? "" : i.getReadText());

                dialog.dismiss();
            }
        });
        DialogNavBarHide.navBarHide(this, dialog);

    }


    private void dialogForWriteText() {
        final Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_edit_write_mode);
        dialog.setCancelable(false);

        edtWriteEdit = (EditText) dialog.findViewById(R.id.edtWriteEdit);
        Button btnCancelWriteEdit = (Button) dialog.findViewById(R.id.btnCancelWriteEdit);
        Button btnOkWriteEdit = (Button) dialog.findViewById(R.id.btnOkWriteEdit);

        if (currentItem != null)
            taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);

        final Item i = taskEditorFragment.getItemFromKey(taskEditorFragment.currentKey);
        edtWriteEdit.setText(i.getWriteText() == null ? "" : i.getWriteText());
        //tvWriteText.setText(i.getWriteText() == null ? "" : i.getWriteText());

        btnCancelWriteEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOkWriteEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tvText.setText(i.getUserText() == null ? "" : i.getUserText());
                if (edtWriteEdit.getText().toString().length() == 0) {
                    CustomToast.t(activity, getResources().getString(R.string.textAdd));
                    return;
                }
                taskEditorFragment.setItemWriteText(edtWriteEdit.getText().toString());
                tvWriteText.setText(i.getWriteText() == null ? "" : i.getWriteText());

                dialog.dismiss();
            }
        });
        DialogNavBarHide.navBarHide(this, dialog);

    }


    public void setSequence(String seq) {
        taskEditorFragment = (TaskEditorFragment) getFragmentManager().findFragmentById(R.id.flContentView);
        if (taskEditorFragment.currentTask != null) {
            taskEditorFragment.currentTask.setSequenceText(seq);
        }
    }

    public int getBitmapMaxSize(Uri resultUri) {
        int maxSize = 512;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), resultUri);
            if (bitmap != null) {
                maxSize = getScallingConstant(bitmap.getWidth(), bitmap.getHeight());
                bitmap.recycle();
                Log.e("BitMap", "..........\n Max Size " + String.valueOf(maxSize));
            }


        } catch (Exception e) {
            //handle exception
        }

        return maxSize;
    }

    //scalling contant for cropper
    int getScallingConstant(int width, int height) {
        Log.e("BitMap", "............................\nbefore crop width " + String.valueOf(width) + "height  " + String.valueOf(height));
        int maxSize = 512;

        int maxWidth = 512;
        int maxHeight = 512;

        if (width > height) {
            if (width > 2000) {
                maxSize = 950;
            } else if (width > 1000) {
                maxSize = 800;
            } else if (width > 850) {
                maxSize = 750;
            }

        } else {
            if (height > 2000) {
                maxSize = 950;
            } else if (height > 1000) {
                maxSize = 800;
            } else if (height > 850) {
                maxSize = 750;
            }
        }
      /*  if (width > height) {
            // landscape
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int) (height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int) (width / ratio);
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }*/
        Log.e("BitMap", "........................\n Max Size " + String.valueOf(maxSize));
        return maxSize;
    }
}
