package com.literacyall.app.fragment;

//This fragment holds the main editor class of the Mind app containing Saving/Loading/Editing


import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dmk.limbikasdk.pojo.LimbikaViewItemValue;
import com.dmk.limbikasdk.views.LimbikaView;
import com.literacyall.app.R;
import com.literacyall.app.activities.MainActivity;
import com.literacyall.app.dao.Item;
import com.literacyall.app.dao.Task;
import com.literacyall.app.manager.DatabaseManager;
import com.literacyall.app.manager.IDatabaseManager;
import com.literacyall.app.utilities.CustomList;
import com.literacyall.app.utilities.CustomToast;
import com.literacyall.app.utilities.FileProcessing;
import com.literacyall.app.utilities.ImageProcessing;
import com.literacyall.app.utilities.ItemType;
import com.literacyall.app.utilities.StaticAccess;
import com.literacyall.app.utilities.TaskType;
import com.literacyall.app.utilities.TypeFace_MY;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by RAFI on 02-Apr-16.
 * this class has some controllers. If you modify anything u must check
 * 1. load task
 * 2. copy task
 * 3. copy paste
 * 4. error listener of limbika view
 * <p>
 * Work logics:::::
 * ShowedBy/HiddenBy:::: This is handled in a way that the child(where u single tap) only knows the target(where u double tap) the target doesnt know anything
 * Drag and drop:: the target knows its a target by allowdragDrop(0/1), and child knows its targets key
 */
public class TaskEditorFragment extends Fragment {


    LimbikaView limbikaView;
    MainActivity mainActivity;
    Context context;
    public FrameLayout flEditor;
    private IDatabaseManager databaseManager;
    private Bitmap map;
    LimbikaView limbikaViewForEdit;
    //used in all task . where ever you double tap it has the key of the view
    public Long currentKey;
    ImageProcessing imageProcessing;
    FileProcessing fileProcessing;
    public Task currentTask;
    LimbikaView previousSelectedView;
    String errorImagePath = "";

    // Contain editor items with all properties before saving to Database
    //they way this item map is initialized is a bit tricky, it is controlled by
    // setSquareBlank() rather its controlled by dragfinish listener fron limbika view which is called
    // from savestate of limbika view
    public LinkedHashMap<Long, Item> itemMap = new LinkedHashMap<>();

    // Delete erased item from editor and database according to this collection
    ArrayList<Item> itemsForEraseFromDB = new ArrayList<>();

    // Maintain Sequence of items using Send to Back/Front
    ArrayList<Long> viewSequence = new ArrayList<>();
    public LinkedHashMap<Long, LimbikaView> allLimbikaViews = new LinkedHashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = (Context) getActivity();
        databaseManager = new DatabaseManager(context);

        intializeMandatoryVariables();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.editor_fragment, null);

        long itemId = System.currentTimeMillis();
        limbikaView = new LimbikaView(context);
        mainActivity = (MainActivity) getActivity();
        imageProcessing = new ImageProcessing(context);
        fileProcessing = new FileProcessing(context);

        flEditor = (FrameLayout) v.findViewById(R.id.flEditor);
        currentTask = new Task();
        return v;

    }

    // Set Square
    public void setSquare(Bitmap map) {
        this.map = map;
        if (limbikaViewForEdit != null) {
            limbikaViewForEdit.setImage(map);
            limbikaViewForEdit.setLayoutParams(new FrameLayout.LayoutParams(500, 500));
            // flEditor.addView(limbikaViewForEdit);
            prepareListners(limbikaViewForEdit);
            limbikaViewForEdit.setLimbikaView(limbikaViewForEdit);
            prepareListners(limbikaViewForEdit);
        }

    }

    //Setting Text properties
    public void setText(String text, int size, int textColor, int fontTypeFace, int fontTypeAlin) {
        if (limbikaViewForEdit != null) {
            limbikaViewForEdit.setText(text);
            limbikaViewForEdit.setTextSize(size);
            limbikaViewForEdit.setTextColor(textColor);
            limbikaViewForEdit.setFontType(fontTypeFace);
            limbikaViewForEdit.setLimbikaTextAlignment(fontTypeAlin);
            setFontType(fontTypeFace, limbikaViewForEdit);
            prepareListners(limbikaViewForEdit);
            limbikaViewForEdit.setLimbikaView(limbikaViewForEdit);
            limbikaViewForEdit.saveViewState();
        }
    }

    public void setNewSound() {
        long itemId = System.currentTimeMillis();
        limbikaView = new LimbikaView(context, itemId, ItemType.Sound);
        limbikaView.setVisibility(View.VISIBLE);
    }

    public void setNewVideo() {
        // limbikaView.setVisibility(View.VISIBLE);


    }

    // Scaling bitmap by Mir
    private Bitmap scaleBitmap(Bitmap bm) {
        int width = bm.getWidth() + 1;
        int height = bm.getHeight() + 1;

        //Log.v("Pictures", "after scaling Width and height are " + width + "--" + height);

        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;

        /*int maxWidth = 512;
        int maxHeight = 512;

        if (width > 1500) {
            maxWidth = 650;
            maxHeight = 650;
        } else if (width > 2000) {
            maxWidth = 750;
            maxHeight = 750;
        }
        if (width > height) {
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

        //Log.v("Pictures", "after scaling Width and height are " + width + "--" + height);

      /*  bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;*/
    }

    //Setting background Image
    public void setImage(Bitmap bitmap) {
        if (limbikaViewForEdit != null) {
            //scale bitmap
            Bitmap b = scaleBitmap(bitmap);
            bitmap.recycle();

            String previousImage = null;
            if (itemMap.containsKey(currentKey)) {
                previousImage = itemMap.get(currentKey).getImagePath();
            }

            limbikaViewForEdit.setImage(b);
            prepareListners(limbikaViewForEdit);
            limbikaViewForEdit.setLimbikaView(limbikaViewForEdit);
            limbikaViewForEdit.saveViewState();

            if (previousImage != null) {
                imageProcessing.deleteImage(previousImage);
            }

        }
    }

    //Setting background color
    public void setBackgroundColor(int color) {
        if (limbikaViewForEdit != null) {
            limbikaViewForEdit.setBackgroundColor(color);
            prepareListners(limbikaViewForEdit);
            limbikaViewForEdit.setLimbikaView(limbikaViewForEdit);
            limbikaViewForEdit.saveViewState();
        }
    }

    //Setting border color
    public void setBorderColor(int color, int borderSize) {
        if (limbikaViewForEdit != null) {
            limbikaViewForEdit.outerBorderColor(color, true, borderSize);
            prepareListners(limbikaViewForEdit);
            limbikaViewForEdit.setLimbikaView(limbikaViewForEdit);
            limbikaViewForEdit.saveViewState();
        }
    }

    /* //Setting border pixel color
     public void setOuterBorderSize(int borderSize) {

         if (limbikaViewForEdit != null) {
             limbikaViewForEdit.setOuterBorderSize(borderSize);
             prepareListners(limbikaViewForEdit);
             limbikaViewForEdit.setLimbikaView(limbikaViewForEdit);
             limbikaViewForEdit.saveViewState();
         }*/
//    }
    public void setCirculerView(Bitmap map) {

        long itemId = System.currentTimeMillis();
        LimbikaView limbikaView = new LimbikaView(context, itemId, ItemType.Circle);
        limbikaView.setImage(map);
        limbikaView.setCircleView(true);
        limbikaView.setLayoutParams(new FrameLayout.LayoutParams(500, 500));
        flEditor.addView(limbikaView);
        prepareListners(limbikaView);

    }

    //after controlling limbika view with gesture the double tap is followed by a single tap so we hacked and solved it
    boolean doubleTap = false;

    //Listners for all interfaces of limbika view
    public void prepareListners(final LimbikaView limbikaView) {
        limbikaView.setSingleTapListener(new LimbikaView.SingleTapListener() {
            @Override
            public void onSingleTap(View v, LimbikaViewItemValue limbikaViewItemValue) {

                //hack for double tap
                if (doubleTap) {
                    doubleTap = false;
                } else {
                    //Checking Alignment
                    if (mainActivity.checkAlignMode() || mainActivity.checkScreenAlignMode()) {
                        if (limbikaViewForEdit != null) {
                            if (mainActivity.selectedAlignment == 0) {
                                CustomToast.t(context, getResources().getString(R.string.allignmentTypeSelectFirst));
                                return;
                            }
                            if (mainActivity.selectedAlignment == StaticAccess.ALIGN_PARENT_CENTER) {
                                limbikaViewForEdit.alignIt_fullCenter(limbikaViewItemValue.getLimbikaView());
                                limbikaViewItemValue.getLimbikaView().saveViewState();
                                /*if (currentKey != null) {
                                    settingAlignment(currentKey, limbikaViewItemValue.getKey(), StaticAccess.ALIGN_PARENT_CENTER);
                                }*/
                            } else if (mainActivity.selectedAlignment == StaticAccess.ALIGN_PARENT_LEFT) {
                                limbikaViewForEdit.alignIt_withMeLeftEnd_X(limbikaViewItemValue.getLimbikaView());
                                limbikaViewItemValue.getLimbikaView().saveViewState();
                                /*if (currentKey != null) {
                                    settingAlignment(currentKey, limbikaViewItemValue.getKey(), StaticAccess.ALIGN_PARENT_LEFT);
                                }*/
                            } else if (mainActivity.selectedAlignment == StaticAccess.ALIGN_PARENT_RIGHT) {
                                limbikaViewForEdit.alignIt_withMeRightEnd_X(limbikaViewItemValue.getLimbikaView());
                                limbikaViewItemValue.getLimbikaView().saveViewState();
                                /*if (currentKey != null) {
                                    settingAlignment(currentKey, limbikaViewItemValue.getKey(), StaticAccess.ALIGN_PARENT_RIGHT);
                                }*/
                            } else if (mainActivity.selectedAlignment == StaticAccess.ALIGN_PARENT_BOTTOM) {
                                limbikaViewForEdit.alignIt_withMeBottom_Y(limbikaViewItemValue.getLimbikaView());
                                limbikaViewItemValue.getLimbikaView().saveViewState();
                                /*if (currentKey != null) {
                                    settingAlignment(currentKey, limbikaViewItemValue.getKey(), StaticAccess.ALIGN_PARENT_BOTTOM);
                                }*/
                            } else if (mainActivity.selectedAlignment == StaticAccess.ALIGN_PARENT_TOP) {
                                limbikaViewForEdit.alignIt_withMeTop_Y(limbikaViewItemValue.getLimbikaView());
                                limbikaViewItemValue.getLimbikaView().saveViewState();
                                /*if (currentKey != null) {
                                    settingAlignment(currentKey, limbikaViewItemValue.getKey(), StaticAccess.ALIGN_PARENT_TOP);
                                }*/
                            } else if (mainActivity.selectedAlignment == StaticAccess.ALIGN_PARENT_CENTER_X) {
                                limbikaViewForEdit.alignIt_withMeCenterX(limbikaViewItemValue.getLimbikaView());
                                limbikaViewItemValue.getLimbikaView().saveViewState();
                                /*if (currentKey != null) {
                                    settingAlignment(currentKey, limbikaViewItemValue.getKey(), StaticAccess.ALIGN_PARENT_CENTER_X);
                                }*/
                            } else if (mainActivity.selectedAlignment == StaticAccess.ALIGN_PARENT_CENTER_Y) {
                                limbikaViewForEdit.alignIt_withMeCenterY(limbikaViewItemValue.getLimbikaView());
                                limbikaViewItemValue.getLimbikaView().saveViewState();
                                /*if (currentKey != null) {
                                    settingAlignment(currentKey, limbikaViewItemValue.getKey(), StaticAccess.ALIGN_PARENT_CENTER_Y);
                                }*/
                            } else if (mainActivity.selectedAlignment == StaticAccess.ALIGN_SCREEN_CENTER_X) {
                                limbikaViewItemValue.getLimbikaView().alignIt_centerscreenX();
                                limbikaViewItemValue.getLimbikaView().saveViewState();
                                /*if (currentKey != null) {
                                    settingAlignment(currentKey, limbikaViewItemValue.getKey(), StaticAccess.ALIGN_SCREEN_CENTER_X);
                                }*/
                            } else if (mainActivity.selectedAlignment == StaticAccess.ALIGN_SCREEN_CENTER_Y) {
                                limbikaViewItemValue.getLimbikaView().alignIt_centerscreenY();
                                limbikaViewItemValue.getLimbikaView().saveViewState();
                                /*if (currentKey != null) {
                                    settingAlignment(currentKey, limbikaViewItemValue.getKey(), StaticAccess.ALIGN_SCREEN_CENTER_Y);
                                }*/
                            }
                            //Screen top,bottom,left,Right added by reaz
                            else if (mainActivity.selectedAlignment == StaticAccess.ALIGN_SCREEN_TOP) {
                                limbikaViewItemValue.getLimbikaView().alignIt_topOfScreen();
                                limbikaViewItemValue.getLimbikaView().saveViewState();
                                /*if (currentKey != null) {
                                    settingAlignment(currentKey, limbikaViewItemValue.getKey(), StaticAccess.ALIGN_SCREEN_TOP);
                                }*/
                            } else if (mainActivity.selectedAlignment == StaticAccess.ALIGN_SCREEN_BOTTOM) {
                                limbikaViewItemValue.getLimbikaView().alignIt_bottomOfScreen();
                                limbikaViewItemValue.getLimbikaView().saveViewState();
                                /*if (currentKey != null) {
                                    settingAlignment(currentKey, limbikaViewItemValue.getKey(), StaticAccess.ALIGN_SCREEN_BOTTOM);
                                }*/
                            } else if (mainActivity.selectedAlignment == StaticAccess.ALIGN_SCREEN_LEFT) {
                                limbikaViewItemValue.getLimbikaView().alignIt_LeftOfScreen();
                                limbikaViewItemValue.getLimbikaView().saveViewState();
                                /*if (currentKey != null) {
                                    settingAlignment(currentKey, limbikaViewItemValue.getKey(), StaticAccess.ALIGN_SCREEN_LEFT);
                                }*/
                            } else if (mainActivity.selectedAlignment == StaticAccess.ALIGN_SCREEN_CENTER) {
                                limbikaViewItemValue.getLimbikaView().alignItCenterOfScreen();
                                limbikaViewItemValue.getLimbikaView().saveViewState();
                                /*if (currentKey != null) {
                                    settingAlignment(currentKey, limbikaViewItemValue.getKey(), StaticAccess.ALIGN_SCREEN_CENTER);
                                }*/
                            } else if (mainActivity.selectedAlignment == StaticAccess.ALIGN_SCREEN_RIGHT) {
                                limbikaViewItemValue.getLimbikaView().alignIt_RightOfScreen();
                                limbikaViewItemValue.getLimbikaView().saveViewState();
                                /*if (currentKey != null) {
                                    settingAlignment(currentKey, limbikaViewItemValue.getKey(), StaticAccess.ALIGN_SCREEN_RIGHT);
                                }*/
                            }
                            // Toast.makeText(getActivity(), "Single", Toast.LENGTH_SHORT).show();
                            mainActivity.hideAllDrawer();
                            limbikaViewForEdit = null;
                        }
                    } else if (mainActivity.isDragDropMode == true) {
                        setDropItems(limbikaViewItemValue.getKey());
                    } else if (mainActivity.isSendToBack == true) {
                        sendToBack(limbikaViewItemValue.getKey());
                    } else if (mainActivity.isSendToFront == true) {
                        sendToFront(limbikaViewItemValue.getKey());
                    } else if (mainActivity.isSendToBackMost == true) {
                        sendToBack(limbikaViewItemValue.getKey());
                    } else if (mainActivity.isSendToFrontMost == true) {
                        sendToBack(limbikaViewItemValue.getKey());
                    } else if (mainActivity.isShowedBy == true) {
                        showedBy(limbikaViewItemValue.getKey());
                    } else if (mainActivity.isHideBy == true) {
                        hideBy(limbikaViewItemValue.getKey());
                    }

                }
            }
        });


        //Long Tap Listener
        limbikaView.setLongTapListener(new LimbikaView.LongTapListener() {
            @Override
            public void onLongTap(View v) {
                // CustomToast.t(context, "OnLong TAP");
            }
        });


        //drag Listener
        limbikaView.setDragListener(new LimbikaView.DragListener() {
            @Override
            public void onDrag(float x, float y) {
                // CustomToast.t(context, "Drag Event: view at (" + x + "," + y + ")");
            }
        });


        //Rotation Listener
        limbikaView.setRotationListener(new LimbikaView.RotationListener() {
            @Override
            public void onRotate(int rotation) {
                // CustomToast.t(context, "Rotation at " + rotation);
            }
        });


        //Double Tap Listener
        limbikaView.setDoubleTapListener(new LimbikaView.DoubleTapListener() {
            @Override
            public void onDoubleTap(float x, float y, LimbikaViewItemValue limbikaViewItemValue) {
                mainActivity.fbtnShowLeftDrawer.performClick();
                mainActivity.setCurrentType(limbikaViewItemValue.getType());
                mainActivity.setCurrentItem(limbikaViewItemValue.getKey());
                limbikaViewForEdit = limbikaViewItemValue.getLimbikaView();
                currentKey = limbikaViewItemValue.getKey();
                mainActivity.hideCurrentRightDrawer();
                mainActivity.opendBottomDrawerHide();
                //limbikaViewForEdit.saveViewState();
                // Toast.makeText(getActivity(), "Double", Toast.LENGTH_SHORT).show();
                doubleTap = true;
                setSelected(limbikaViewForEdit);

                changeColors(limbikaViewItemValue.getKey());
            }

            @Override
            public void onDoubleTap_2(LimbikaViewItemValue key) {

            }
        });

        //Drag finish listner
        limbikaView.setDragFinishListener(new LimbikaView.DragFinishListner() {
            @Override
            public void onDragFinish(LimbikaViewItemValue limbikaViewItemValue) {
                //Hash map integration only from the limbika view
                Item item = new Item();
                if (itemMap.containsKey(limbikaViewItemValue.getKey())) {
                    item = itemMap.get(limbikaViewItemValue.getKey());
                }
                item.setX(limbikaViewItemValue.getX());
                item.setY(limbikaViewItemValue.getY());
                item.setRotation(limbikaViewItemValue.getRotation());
                item.setKey(limbikaViewItemValue.getKey());
                item.setIsCircleView(limbikaViewItemValue.getIsCircleView());
                item.setCircleColor(limbikaViewItemValue.getCircleColor());
                item.setUserText(limbikaViewItemValue.getUserText() == null ? "" : limbikaViewItemValue.getUserText());
                item.setTextColor(limbikaViewItemValue.getTextColor());
                item.setTextSize(limbikaViewItemValue.getTextSize());
                item.setBorderColor(limbikaViewItemValue.getBorderColor());
                item.setBackgroundColor(limbikaViewItemValue.getBackgroundColor());
                item.setDrawable(limbikaViewItemValue.getDrawable());
                item.setWidth(limbikaViewItemValue.getWidth());
                item.setHeight(limbikaViewItemValue.getHeight());
                item.setLeft(limbikaViewItemValue.getLeft());
                item.setRight(limbikaViewItemValue.getRight());
                item.setTop(limbikaViewItemValue.getTop());
                item.setBottom(limbikaViewItemValue.getBottom());
                item.setImagePath(limbikaViewItemValue.getImagePath() == null ? "" : limbikaViewItemValue.getImagePath());
                item.setType(limbikaViewItemValue.getType() == null ? "" : limbikaViewItemValue.getType());
                item.setCornerRound(limbikaViewItemValue.getCornerRound());
                item.setFontTypeFace(limbikaViewItemValue.getFontTypeFace());
                item.setFontAlign(limbikaViewItemValue.getFontAlign());
                item.setBorderPixel(limbikaViewItemValue.getBorderPixel());

                itemMap.put(limbikaViewItemValue.getKey(), item);
                allLimbikaViews.put(limbikaViewItemValue.getKey(), limbikaViewItemValue.getLimbikaView());
            }
        });

        // Item Erase listener
        limbikaView.setEraseListner(new LimbikaView.EraseListner() {
            @Override
            public void onErase(final LimbikaViewItemValue limbikaViewItemValue) {
                final Handler handler = new Handler();
                final Runnable runnable = new Runnable() {
                    public void run() {
                        if (itemMap.containsKey(limbikaViewItemValue.getKey())) {
                            if (currentTask != null) {
                                itemsForEraseFromDB.add(itemMap.get(limbikaViewItemValue.getKey()));
                            }
                            Item item = itemMap.get(limbikaViewItemValue.getKey());
                            imageProcessing.deleteImage(item.getImagePath());
                            fileProcessing.deleteSound(item.getItemSound());

                            //if it is a target better remove it from all of its child then delete it
                            //call it before current key is not null
                            if (checkIfAlreadyTarget(limbikaViewItemValue.getKey()))
                                deleteTarget();
                            //Relax if it was in the child map it will be deleted otherwise nothing will happen
                            onlyAllChild.remove(limbikaViewItemValue.getKey());

                            //remove from the target list of showed by and clear its childs
                            if (isShowedTarget(limbikaViewItemValue.getKey())) {
                                for (long key : showedMap.get(limbikaViewItemValue.getKey())) {
                                    Item newItem = itemMap.get(key);
                                    newItem.setShowedBy(0);
                                    itemMap.put(newItem.getKey(), newItem);
                                }
                                showedMap.remove(limbikaViewItemValue.getKey());

                            }

                            //remove from the target list of hiden by and remove its childs
                            if (isHidenTarget(limbikaViewItemValue.getKey())) {
                                for (long key : hideMap.get(limbikaViewItemValue.getKey())) {
                                    Item newItem = itemMap.get(key);
                                    newItem.setHideBy(0);
                                    itemMap.put(newItem.getKey(), newItem);
                                }
                                hideMap.remove(limbikaViewItemValue.getKey());
                            }


                            itemMap.remove(limbikaViewItemValue.getKey());
                            allLimbikaViews.remove(limbikaViewItemValue.getKey());

                            viewSequence.remove(limbikaViewItemValue.getKey());
                            //CustomToast.t(context, getResources().getString(R.string.itemErased));
                            currentKey = null;
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        });


    }

    // Set Square blank
    public void setSquareBlank() {
        long itemId = System.currentTimeMillis();
        limbikaView = new LimbikaView(context, itemId, ItemType.Square);
        limbikaView.setLayoutParams(new FrameLayout.LayoutParams(500, 500));
        flEditor.addView(limbikaView);
        prepareListners(limbikaView);
        limbikaView.setLimbikaView(limbikaView);
        limbikaView.saveViewState();
        viewSequence.add(itemId);

        // setSelected(limbikaView);a
        performDoubleTapAutomatically(limbikaView);

    }

    //Set circle blank
    public void setCircleBlank() {
        long itemId = System.currentTimeMillis();
        limbikaView = new LimbikaView(context, itemId, ItemType.Circle);
        limbikaView.setLayoutParams(new FrameLayout.LayoutParams(500, 500));
        limbikaView.setCircleView(true);
        flEditor.addView(limbikaView);
        prepareListners(limbikaView);
        limbikaView.setLimbikaView(limbikaView);
        limbikaView.saveViewState();
        viewSequence.add(itemId);

        //setSelected(limbikaView);
        performDoubleTapAutomatically(limbikaView);
    }

    //Set text blank
    public void setTextBlank(String text, int size, int textColor) {
        long itemId = System.currentTimeMillis();
        LimbikaView limbikaView = new LimbikaView(context, itemId, ItemType.Text);
        limbikaView.setText(text);
        limbikaView.setTextSize(size);
        limbikaView.setTextColor(textColor);
        limbikaView.setLayoutParams(new FrameLayout.LayoutParams(500, 500));
        flEditor.addView(limbikaView);
        prepareListners(limbikaView);
        limbikaView.setLimbikaView(limbikaView);
        limbikaView.saveViewState();
    }

    //Set circuler text by Rokan
    public void setTextCircular(String text, int size, int textColor) {
        long itemId = System.currentTimeMillis();
        LimbikaView limbikaView = new LimbikaView(context, itemId, ItemType.Text);
        limbikaView.setCircleView(true);
        limbikaView.setCircleColor(Color.GREEN);
        limbikaView.setText(text);
        limbikaView.setTextSize(size);
        limbikaView.setTextColor(textColor);
        limbikaView.setLayoutParams(new FrameLayout.LayoutParams(500, 500));
        flEditor.addView(limbikaView);
        prepareListners(limbikaView);
        limbikaView.setLimbikaView(limbikaView);
        limbikaView.saveViewState();
    }

    //Setting correct ans
    public void setAnswer(String ans) {

        if (itemMap.containsKey(currentKey)) {
            Item item = itemMap.get(currentKey);
            item.setResult(checkNull(ans));
            itemMap.put(currentKey, item);
//            limbikaViewForEdit.setLimbikaResult(ans);
        }
        //CustomToast.t(context, getResources().getString(R.string.CorrectAnswer) + currentKey);
    }

    //Save Task, Items, Results to database
    public Long saveTask() {

        Long taskId = null;
        if (itemMap.size() > 0) {

            //  Updating a new Item to Database
            if (currentTask.getId() != null) {
                Task task = currentTask;
                task.setUpdatedAt(new Date());
                task.setBackgroundColor(mainActivity.taskBackgroundColor);
                if (task.getTaskImage() != null && limbikaViewForEdit != null) {
                    imageProcessing.deleteImage(task.getTaskImage());
                    limbikaViewForEdit.hideRectSelectedLine(true);/// hide selection for screen shoot
                    task.setTaskImage(imageProcessing.takeScreenshot(flEditor));
                    limbikaViewForEdit.changeSelection(true);
                }
                databaseManager.updateTask(task);
                taskId = currentTask.getId();
                //CustomToast.t(context, getResources().getString(R.string.updateSuccessfully));
            }

            // Inserting an existing item to Database
            else {

                currentTask.setUniqId(System.currentTimeMillis());
                currentTask.setCreatedAt(new Date());
                currentTask.setUpdatedAt(new Date());
                currentTask.setUserId(1);// after user create then it will be change comment by reaz
                currentTask.setName(currentTask.getName() == null ? "" : currentTask.getName());
                limbikaViewForEdit.hideRectSelectedLine(true);/// hide selection for screen shoot
                currentTask.setTaskImage(imageProcessing.takeScreenshot(flEditor));//currentTask.getTaskImage()==null ? "" :currentTask.getTaskImage()
                currentTask.setType(checkNull(mainActivity.taskType));
                currentTask.setBackgroundColor(mainActivity.taskBackgroundColor);
                currentTask.setSlideSequence(databaseManager.getMaxTaskPosition(mainActivity.taskPackId) + 1);
                currentTask.setActive(true);
                currentTask.setTaskPackId(mainActivity.taskPackId);
                currentTask.setFeedbackImage(currentTask.getFeedbackImage() == null ? "" : currentTask.getFeedbackImage());
                currentTask.setFeedbackType(currentTask.getFeedbackType() == -1 ? 0 : currentTask.getFeedbackType());
                currentTask.setPositiveSound(currentTask.getPositiveSound() == null ? "" : currentTask.getPositiveSound());
                currentTask.setFeedbackSound(currentTask.getFeedbackSound() == null ? "" : currentTask.getFeedbackSound());
                currentTask.setNegativeSound(currentTask.getNegativeSound() == null ? "" : currentTask.getNegativeSound());

                currentTask.setErrortext(currentTask.getErrortext() == null ? "" : currentTask.getErrortext());
                currentTask.setErrorImage(currentTask.getErrorImage() == null ? "" : currentTask.getErrorImage());
                currentTask.setErrorBgColor(currentTask.getErrorBgColor());

                currentTask.setErrorMandatoryScreen(currentTask.getErrorMandatoryScreen());
                currentTask.setSequenceText(currentTask.getSequenceText() == null ? "" : currentTask.getSequenceText());
                taskId = databaseManager.insertTask(currentTask);
                currentTask.setId(taskId);
                //CustomToast.t(context, getResources().getString(R.string.savedSuccessfully));
            }

            // Deleting Item before Inserting of Task
            databaseManager.deleteItemsByTask(taskId);

            // Inserting Items of a Task to Database
            for (Map.Entry<Long, Item> value : itemMap.entrySet()) {
                Item item = value.getValue();
                item.setId(null);
                item.setTask(taskId);
                item.setUserText(item.getUserText() == null ? "" : item.getUserText());
                item.setOpenApp(item.getOpenApp() == null ? "" : item.getOpenApp());
                item.setOpenUrl(item.getOpenUrl() == null ? "" : item.getOpenUrl());
                item.setItemSound(item.getItemSound() == null ? "" : item.getItemSound());
                item.setResult(item.getResult() == null ? "" : item.getResult());
                item.setImagePath(item.getImagePath() == null ? "" : item.getImagePath());
                item.setType(item.getType() == null ? "" : item.getType());
                item.setFontTypeFace(item.getFontTypeFace() == 0 ? 0 : item.getFontTypeFace());
                item.setReadText(item.getReadText() == null ? "" : item.getReadText());
                item.setWriteText(item.getWriteText() == null ? "" : item.getWriteText());
                item.setCreatedAt(new Date());

                //fill targets list in the target of shwed from
                if (isShowedTarget(item.getKey())) {
                    item.setShowedByTarget(formatStringShowedHidden(showedMap.get(item.getKey())));
                } else {
                    item.setShowedByTarget("");
                }

                ///fill targets list in the target of hidden from
                if (isHidenTarget(item.getKey())) {
                    item.setHiddenByTarget(formatStringShowedHidden(hideMap.get(item.getKey())));

                } else
                    item.setHiddenByTarget("");

                long itemID = databaseManager.insertItem(item);
                long jj = itemID;
            }

            // Erasing item from DB
            if (itemsForEraseFromDB != null) {
                for (Item item : itemsForEraseFromDB) {
                    databaseManager.deleteItemById(item.getId());
                }
            }


        } else {
            CustomToast.t(context, getResources().getString(R.string.addItem));

        }
        return taskId;
    }

    // copy Tast created By Reaz
    public void copyTask() {
        if (itemMap.size() > 0) {
            Long taskId;
            Task copyTask = new Task();
            copyTask.setCreatedAt(new Date());
            copyTask.setUpdatedAt(new Date());
            copyTask.setUserId(1);
            copyTask.setName(currentTask.getName() == null ? "" : currentTask.getName());
            copyTask.setTaskImage(imageProcessing.takeScreenshot(flEditor));
            copyTask.setType(currentTask.getType() == null ? "" : currentTask.getType());
            copyTask.setBackgroundColor(mainActivity.taskBackgroundColor);

            copyTask.setSlideSequence(databaseManager.getMaxTaskPosition(mainActivity.taskPackId) + 1);
            copyTask.setActive(true);
            copyTask.setTaskPackId(mainActivity.taskPackId);
            copyTask.setSequenceText(currentTask.getSequenceText() == null ? "" : currentTask.getSequenceText());
//            copyTask.setFeedbackImage(currentTask.getFeedbackImage() == null ? "" : currentTask.getFeedbackImage());
            if (currentTask.getFeedbackImage() != null && currentTask.getFeedbackImage().length() > 0) {
                copyTask.setFeedbackImage(imageProcessing.imageSave(imageProcessing.getImage(currentTask.getFeedbackImage())));
            } else {
                copyTask.setFeedbackImage("");
            }
            copyTask.setFeedbackAnimation(currentTask.getFeedbackAnimation() == 0 ? 0 : currentTask.getFeedbackAnimation());
            copyTask.setNegativeAnimation(currentTask.getNegativeAnimation() == 0 ? 0 : currentTask.getNegativeAnimation());
            copyTask.setPositiveAnimation(currentTask.getPositiveAnimation() == 0 ? 0 : currentTask.getPositiveAnimation());
//            String newImage = imageProcessing.imageSave(imageProcessing.getImage(imgCopyName));

            if (currentTask.getPositiveSound().length() > 0 && currentTask.getPositiveSound() != null) {
                copyTask.setPositiveSound(fileProcessing.createSoundFile(fileProcessing.getAbsolutepath_Of_Sound(currentTask.getPositiveSound())));
            } else {
                copyTask.setPositiveSound("");
            }
            if (currentTask.getFeedbackSound().length() > 0 && currentTask.getFeedbackSound() != null) {
                copyTask.setFeedbackSound(fileProcessing.createSoundFile(fileProcessing.getAbsolutepath_Of_Sound(currentTask.getFeedbackSound())));
            } else {
                copyTask.setFeedbackSound("");
            }
            if (currentTask.getNegativeSound().length() > 0 && currentTask.getNegativeSound() != null) {
                copyTask.setNegativeSound(fileProcessing.createSoundFile(fileProcessing.getAbsolutepath_Of_Sound(currentTask.getNegativeSound())));
            } else {
                copyTask.setNegativeSound("");
            }
            copyTask.setFeedbackType(currentTask.getFeedbackType() == -1 ? 0 : currentTask.getFeedbackType());
            copyTask.setErrortext(currentTask.getErrortext() == null ? "" : currentTask.getErrortext());

            if (currentTask.getErrorImage() != null && currentTask.getErrorImage().length() > 0) {
                copyTask.setErrorImage(imageProcessing.imageSave(imageProcessing.getImage(copyTask.getErrorImage())));
            } else {
                copyTask.setErrorImage("");
            }
            copyTask.setErrorBgColor(currentTask.getErrorBgColor());
            copyTask.setErrorMandatoryScreen(currentTask.getErrorMandatoryScreen());
            taskId = databaseManager.insertTask(copyTask);
            copyTask.setId(taskId);
            //CustomToast.t(context, getResources().getString(R.string.copySuccessFully));


            //Inserting Item to DB of a Task
            for (Map.Entry<Long, Item> value : itemMap.entrySet()) {
                Item item = value.getValue();
                item.setTask(taskId);
//                if (item.getId() == null) {
                /////////////////////////////////
                ///// Apply condition Reaz /////
                ///////////////////////////////

                item.setId(null);
                item.setUserText(item.getUserText() == null ? "" : item.getUserText());
                item.setOpenApp(item.getOpenApp() == null ? "" : item.getOpenApp());
                item.setOpenUrl(item.getOpenUrl() == null ? "" : item.getOpenUrl());
                item.setReadText(item.getReadText() == null ? "" : item.getReadText());
                item.setWriteText(item.getWriteText() == null ? "" : item.getWriteText());
//
                String itemSound = item.getItemSound();
                if (itemSound.length() > 0 && itemSound != null) {
                    String itemSoundCOpy = fileProcessing.createSoundFile(fileProcessing.getAbsolutepath_Of_Sound(itemSound));
                    item.setItemSound(itemSoundCOpy);
                } else {
                    item.setItemSound("");
                }
//                item.setItemSound(item.getItemSound() == null ? "" : item.getItemSound());
                if (item.getImagePath() != null && item.getImagePath().length() > 0) {
                    item.setImagePath(imageProcessing.imageSave(imageProcessing.getImage(item.getImagePath())));
                } else {
                    item.setImagePath("");
                }
                item.setResult(item.getResult() == null ? "" : item.getResult());
//                item.setImagePath(item.getImagePath() == null ? "" : item.getImagePath());
                item.setType(item.getType() == null ? "" : item.getType());
                //fill targets list in the target of shwed from
                if (isShowedTarget(item.getKey())) {
                    item.setShowedByTarget(formatStringShowedHidden(showedMap.get(item.getKey())));
                } else {
                    item.setShowedByTarget("");
                }

                ///fill targets list in the target of hidden from
                if (isHidenTarget(item.getKey())) {
                    item.setHiddenByTarget(formatStringShowedHidden(hideMap.get(item.getKey())));

                } else
                    item.setHiddenByTarget("");

                item.setCreatedAt(new Date());
                databaseManager.insertItem(item);
            }

        } else {
            CustomToast.t(context, getResources().getString(R.string.addItem));
        }

    }

    //Clean all items and starts for new
    public void newTask() {
        flEditor.removeAllViews();
        //this clears all the maps that was hollding informations
        clearMaps();
        currentTask = new Task();
        mainActivity.taskBackgroundColor = ContextCompat.getColor(mainActivity, R.color.white);
        flEditor.setBackgroundColor(mainActivity.taskBackgroundColor);
    }

    //Load Items according to task
    public void loadTask(Task task) {
        //this clears all the maps that was hollding informations
        clearMaps();

        currentTask = task;
        flEditor.setBackgroundColor(task.getBackgroundColor());
        mainActivity.taskBackgroundColor = task.getBackgroundColor();
        final LinkedHashMap<Long, Item> items = databaseManager.loadTaskWiseItem(task);
        LimbikaView firstItem_v = null;

        if (items != null) {
            for (Map.Entry<Long, Item> itemValue : items.entrySet()) {
                Item item = itemValue.getValue();
                limbikaView = new LimbikaView(context, item.getKey());
                createLimbikaView(limbikaView, item);

                itemMap.put(item.getKey(), item);
                viewSequence.add(item.getKey());

                if (item.getAllowDragDrop() == 1) {
                    //target
                    dragAndDropHappened(0, item.getKey(), item, -1, null);
                } else if (item.getDragDropTarget() > 0) {
                    //child
                    dragAndDropHappened(1, item.getDragDropTarget(), null, item.getKey(), item);
                }

                if (item.getShowedBy() > 0) {
                    //showedMap.put(item.getShowedBy(),item.getKey());
                    addShowed(item.getShowedBy(), item.getKey());
                }
                if (item.getHideBy() > 0) {
                    //hideMap.put(item.getHideBy(),item.getKey());
                    addHidden(item.getShowedBy(), item.getKey());
                }

                if (firstItem_v == null) {
                    performDoubleTapAutomatically(limbikaView);
                    firstItem_v = limbikaView;
                } else {
                    setDeselected(limbikaView);
                }
            }
        }
    }

    //Setting current task
    public void setCurrentTask(Task task) {
        this.currentTask = task;
    }

    //Cleaning view from frame
    public void clearItems() {
        flEditor.removeAllViews();
    }

    //Getting item from key
    public Item getItemFromKey(Long key) {
        return itemMap.get(key);
    }

    //Getting current item
    public Item getCurrentItem() {
        if (!itemMap.containsKey(currentKey)) {
            return null;
        }
        return itemMap.get(currentKey);
    }

    //Copying Items
    public void copyPaste() {
        Item item = new Item();
        if (itemMap.containsKey(currentKey)) {
            item = itemMap.get(currentKey);
        }

        long newKey = System.currentTimeMillis();
        String imgCopyName = item.getImagePath();
        // for Copy new image save with currentTimeMillis
        String newImage = imageProcessing.imageSave(imageProcessing.getImage(imgCopyName));

        Item clonedItem = cloneObject(item);
        clonedItem.setKey(newKey);
        clonedItem.setX((float) 0);
        clonedItem.setY((float) 0);

        clonedItem.setImagePath(newImage);
        clonedItem.setImagePath(checkNull(newImage));
        String soundTemp = checkNull(item.getItemSound());
        if (soundTemp.length() > 0 && soundTemp != null) {
            String itemSoundCopy = fileProcessing.createSoundFile(fileProcessing.getAbsolutepath_Of_Sound(item.getItemSound()));
            clonedItem.setItemSound(itemSoundCopy);
        } else {
            clonedItem.setItemSound("");
        }

        //when you clone you must add the keys to the rehandle drag and drop toshow colors
        if (clonedItem.getAllowDragDrop() == 1) {
            // for target (you cannot copy a views target attribute otherwise its childs will have two parents but
            // in our system we only controlled one target in child so its better left it like it)

            //dragAndDropHappened(0,clonedItem.getKey(),clonedItem,-1,null);
            //clonedItem.setAllowDragDrop(0);

        } else if (clonedItem.getDragDropTarget() > 0) {
            //for child
            long TargetKey = clonedItem.getDragDropTarget();
            dragAndDropHappened(1, TargetKey, null, clonedItem.getKey(), clonedItem);
        }

        /* FOR CHILD COPY WE MUST ADD IT TO THE  LIST*/
        if (clonedItem.getShowedBy() > 0) {
            addShowed(clonedItem.getShowedBy(), clonedItem.getKey());
        }
        if (clonedItem.getHideBy() > 0) {
            addHidden(clonedItem.getShowedBy(), clonedItem.getKey());
        }

        /* NO NEED TO USE SHOWED BY HIDDEN BY (TARGET COPY) HERE BECAUSE A CHILD HAS ONLY ONE PARENT AND
           A PARENT DOESNT HAVE A CLUE WHEATER ITS PARENT OR CHILD NO FIELD FOR IT NOW 20-10-16*/

        itemMap.put(newKey, clonedItem);
        limbikaView = new LimbikaView(context, clonedItem.getKey());
        createLimbikaView(limbikaView, clonedItem);
        viewSequence.add(clonedItem.getKey());
        allLimbikaViews.put(clonedItem.getKey(), limbikaView);


//        setSelected(limbikaView);
//        currentKey = clonedItem.getKey();
        performDoubleTapAutomatically(limbikaView);
    }

    //Send to Back
    public void sendToBack(Long underItem) {
        mainActivity.isSendToBack = false;

        if (currentKey.equals(underItem)) {
            CustomToast.t(context, getResources().getString(R.string.cannotSelectSameItem));
            return;
        }
        int newPosition = viewSequence.indexOf(underItem);
        int prePosition = viewSequence.indexOf(currentKey);

        if (prePosition < newPosition) {
            CustomToast.t(context, getResources().getString(R.string.alreadyInbackSelectedItem));
            return;
        }

        viewSequence.remove(prePosition);
        viewSequence.add(newPosition, currentKey);

        flEditor.removeAllViews();
        LinkedHashMap<Long, Item> temp = new LinkedHashMap<>();

        for (Long key : viewSequence) {
            if (itemMap.containsKey(key)) {
                Item item = itemMap.get(key);
                limbikaView = new LimbikaView(context, item.getKey());
                createLimbikaView(limbikaView, item);
                temp.put(item.getKey(), item);


                setDeselected(limbikaView);
                if (key == currentKey) {
                    setSelected(limbikaView);
                }
            }
        }
        itemMap.clear();
        itemMap = temp;


    }

    //Round Corner
    public void roundCorner(boolean isRoundCorner) {
        if (limbikaViewForEdit != null) {
            limbikaViewForEdit.roundCorner(isRoundCorner);
        }
    }

    public void centreAlign(LimbikaView lView) {
        if (limbikaViewForEdit != null) {
            limbikaViewForEdit.alignIt_fullCenter(lView);
        }

    }

    public void setFeedbackImage(String Image, int type) {
        if (currentTask != null) {
            currentTask.setFeedbackImage(checkNull(Image));
            currentTask.setFeedbackType(type);
            //CustomToast.t(context, getResources().getString(R.string.feedbackImagesetSuccessFully));
        }

    }

    public void setFeedbackAnim(int animType) {
        if (currentTask != null)
            currentTask.setFeedbackAnimation(animType);
        //CustomToast.t(context, getResources().getString(R.string.feedbackAnimationsetSuccessFully));

    }

    // positive anim created by reaz
    public void setPositiveAnim(int animType) {
        if (currentTask != null)
            currentTask.setPositiveAnimation(animType);
        //CustomToast.t(context, getResources().getString(R.string.positiveAnimationSuccessFully));

    }

    // negative anim created by reaz
    public void setNegativeAnim(int animType) {
        if (currentTask != null)
            currentTask.setNegativeAnimation(animType);
        //CustomToast.t(context, getResources().getString(R.string.negativeAnimationsetSuccessFully));

    }

    // setOpenUrl  created by reaz
    public void setOpenUrl(String url) {

        if (currentKey != null) {
            if (itemMap.containsKey(currentKey)) {
                Item item = itemMap.get(currentKey);
                item.setOpenUrl(checkNull(url));
                itemMap.put(currentKey, item);
//                limbikaViewForEdit.setOpenUrl(checkNull(url));
            }

        }

    }

    // set sequenceText created by reaz
    public void setSequenceText(String sequenceText) {
        if (currentTask != null)
            currentTask.setSequenceText(checkNull(sequenceText));
        //CustomToast.t(context, getResources().getString(R.string.negativeAnimationsetSuccessFully));

    }

    public void navigateTo(Task task) {
        if (currentKey != null) {
            if (itemMap.containsKey(currentKey)) {
                Item item = itemMap.get(currentKey);
                item.setNavigateTo(task.getUniqId());
                itemMap.put(currentKey, item);
                limbikaViewForEdit.setNavigateTo(task.getId());
            }
        }
    }

    public void openApp(String packageName) {
        if (currentKey != null) {
            if (itemMap.containsKey(currentKey)) {
                Item item = itemMap.get(currentKey);
//                item.setOpenApp(checkNull(packageName));
                item.setOpenApp(item.getOpenApp() == null ? "" : packageName);
                itemMap.put(currentKey, item);
                limbikaViewForEdit.setOpenApp(item.getOpenApp() == null ? "" : packageName);
            }
        }
    }

    // item wise ReadText set added by Reaz
    public void setItemReadText(String readText) {
        if (currentKey != null) {
            if (itemMap.containsKey(currentKey)) {
                Item item = itemMap.get(currentKey);
//                item.setOpenApp(checkNull(packageName));
                item.setReadText(checkNull(readText));
                itemMap.put(currentKey, item);

            }

        }
    }


    // item wise WriteText set added by Rokan
    public void setItemWriteText(String writeText) {
        if (currentKey != null) {
            if (itemMap.containsKey(currentKey)) {
                Item item = itemMap.get(currentKey);
//                item.setOpenApp(checkNull(packageName));
                item.setWriteText(checkNull(writeText));
                itemMap.put(currentKey, item);

            }

        }
    }

    // item wise sound set added by Reaz
    public void setItemSound(String itemSound) {
        if (currentKey != null) {
            if (itemMap.containsKey(currentKey)) {
                Item item = itemMap.get(currentKey);
//                item.setOpenApp(checkNull(packageName));
                item.setItemSound(checkNull(itemSound));
                itemMap.put(currentKey, item);
                limbikaViewForEdit.setItemSound(itemSound);
            }

        }
    }

    // Setting showed by
    public void showedBy(long showedBy) {
        if (currentKey != null) {
            if (itemMap.containsKey(showedBy)) {
                Item item = itemMap.get(showedBy);
                item.setShowedBy(currentKey);
                //this one is only for showing child key as target doesnt hold childs address
                addShowed(currentKey, item.getKey());

                itemMap.put(showedBy, item);
                mainActivity.swShowedByInfo.setChecked(true);
                mainActivity.isShowedBy = false;

                changeColorsForChild(showedBy, 1);
                CustomToast.t(context, getResources().getString(R.string.ItemSetShowedBy));
            }
        }
    }

    public void hideBy(long hideBy) {
        if (currentKey != null) {
            if (itemMap.containsKey(hideBy)) {
                Item item = itemMap.get(hideBy);
                item.setHideBy(currentKey);
                itemMap.put(hideBy, item);
                addHidden(currentKey, item.getKey());
                mainActivity.swHiddenByInfo.setChecked(true);
                mainActivity.isHideBy = false;
                changeColorsForChild(hideBy, 2);
                CustomToast.t(context, getResources().getString(R.string.ItemSetHideBy));
            }
        }
    }

    public Item getAllTextProperties() {
        if (currentKey != null) {
            if (itemMap.containsKey(currentKey)) {
                Item item = itemMap.get(currentKey);
                return item;
            }
        }
        return null;
    }

    // Negative sound  method created by reaz
    public void setNegativeSound(String path) {

        if (currentTask != null)
            currentTask.setNegativeSound(checkNull(path));
//        CustomToast.t(mainActivity, path);

    }

    // Positive Sound  method created by reaz
    public void setPositiveSound(String path) {
        if (currentTask != null)
            currentTask.setPositiveSound(checkNull(path));
//        CustomToast.t(mainActivity, path);
    }

    // FeedBack  Sound method created by reaz
    public void setFeedBackSound(String path) {
        if (currentTask != null)
            currentTask.setFeedbackSound(path);
//        CustomToast.t(mainActivity, path);
    }

    // video method created by reaz
    public void setVideo(String path) {
//        CustomToast.t(mainActivity, path);
    }

    //null check methods
    public String checkNull(String value) {

        if (value != null) {
            return value;
        } else {
            return value = "";
        }
    }

    // Set item selected
    public void setSelected(LimbikaView limbikaView) {
        limbikaView.changeSelection(true);
        if (previousSelectedView != null && previousSelectedView != limbikaView) {
            previousSelectedView.hideRectSelectedLine(true);

        }
        previousSelectedView = limbikaView;
    }

    // this method only called from loadtask for the first time to deselect all except the 1st view
    public void setDeselected(LimbikaView v) {
        if (v != null)
            v.hideRectSelectedLine(true);
    }

    //Set CloseApp
    public void closeApp(int state) {
        if (currentKey != null) {
            if (itemMap.containsKey(currentKey)) {
                Item item = itemMap.get(currentKey);
                item.setCloseApp(state);
                itemMap.put(currentKey, item);
//                limbikaViewForEdit.setCloseApp(state);
            }
        }
    }

    //Send to Back Most
    public void sendToBackMost() {
        int prePosition = viewSequence.indexOf(currentKey);

        viewSequence.remove(prePosition);
        viewSequence.add(0, currentKey);

        flEditor.removeAllViews();
        LinkedHashMap<Long, Item> temp = new LinkedHashMap<>();

        for (Long key : viewSequence) {
            if (itemMap.containsKey(key)) {
                Item item = itemMap.get(key);
                limbikaView = new LimbikaView(context, item.getKey());
                createLimbikaView(limbikaView, item);
                temp.put(item.getKey(), item);

                setDeselected(limbikaView);
                if (key == currentKey) {
                    setSelected(limbikaView);
                }
            }
        }
        itemMap.clear();
        itemMap = temp;

    }

    //Send to Front Most
    public void sendToFrontMost() {

        int prePosition = viewSequence.indexOf(currentKey);
        viewSequence.remove(prePosition);
        viewSequence.add(viewSequence.size(), currentKey);


        flEditor.removeAllViews();
        LinkedHashMap<Long, Item> temp = new LinkedHashMap<>();


        for (Long key : viewSequence) {
            if (itemMap.containsKey(key)) {
                Item item = itemMap.get(key);
                limbikaView = new LimbikaView(context, item.getKey());
                createLimbikaView(limbikaView, item);
                temp.put(item.getKey(), item);

                setDeselected(limbikaView);
                if (key == currentKey) {
                    setSelected(limbikaView);
                }
            }
        }
        itemMap.clear();
        itemMap = temp;

    }

    //Send to Back
    public void sendToFront(Long itemKey) {
        mainActivity.isSendToFront = false;
        if (currentKey.equals(itemKey)) {
            CustomToast.t(context, getResources().getString(R.string.sameItem));
            return;
        }
        int newPosition = viewSequence.indexOf(itemKey);
        int prePosition = viewSequence.indexOf(currentKey);

        if (prePosition > newPosition) {
            CustomToast.t(context, getResources().getString(R.string.alreadyInFrontSelectedItem));
            return;
        }

        viewSequence.remove(prePosition);
        viewSequence.add(newPosition, currentKey);

        flEditor.removeAllViews();
        LinkedHashMap<Long, Item> temp = new LinkedHashMap<>();

        for (Long key : viewSequence) {
            if (itemMap.containsKey(key)) {
                Item item = itemMap.get(key);
                limbikaView = new LimbikaView(context, item.getKey());
                createLimbikaView(limbikaView, item);

                temp.put(item.getKey(), item);
                setDeselected(limbikaView);
                if (key == currentKey) {
                    setSelected(limbikaView);
                }
            }
        }
        itemMap.clear();
        itemMap = temp;

    }

    // Setting font type
    public void setFontType(int fontType, LimbikaView limbikaView) {
        switch (fontType) {
            case StaticAccess.TAG_TYPE_NORMAL:
                limbikaView.setCustomTypeFace(TypeFace_MY.getRoboto(context));
                break;
            // getSegoeScript
            case StaticAccess.TAG_TYPE_FACE_SEGOE_SCRIPT:
                limbikaView.setCustomTypeFace(TypeFace_MY.getSegoeScript(context));
                break;
            case StaticAccess.TAG_TYPE_FACE_DANCING:
                limbikaView.setCustomTypeFace(TypeFace_MY.getDancing(context));
                break;
            case StaticAccess.TAG_TYPE_FACE_ROBOTO_THIN:
                limbikaView.setCustomTypeFace(TypeFace_MY.getRobotoThin(context));
                break;
            case StaticAccess.TAG_TYPE_FACE_ROAD_BRUSH:
                limbikaView.setCustomTypeFace(TypeFace_MY.getRoadBrush(context));
                break;
            case StaticAccess.TAG_TYPE_FACE_ROBOTO_CONDENSED:
                limbikaView.setCustomTypeFace(TypeFace_MY.getRoboto_condensed(context));
                break;
            case StaticAccess.TAG_TYPE_FACE_RANCHO:
                limbikaView.setCustomTypeFace(TypeFace_MY.getRancho(context));
                break;
        }

    }

    public void performDoubleTapAutomatically(LimbikaViewItemValue limbikaViewItemValue, LimbikaView v) {
        mainActivity.fbtnShowLeftDrawer.performClick();
        mainActivity.setCurrentType(limbikaViewItemValue.getType());
        mainActivity.setCurrentItem(limbikaViewItemValue.getKey());
        limbikaViewForEdit = v;
        currentKey = limbikaViewItemValue.getKey();
        mainActivity.hideCurrentRightDrawer();
        mainActivity.opendBottomDrawerHide();

        // Toast.makeText(getActivity(), "Double", Toast.LENGTH_SHORT).show();

        setSelected(limbikaViewForEdit);
    }

    public void performDoubleTapAutomatically(LimbikaView v) {
        if (v != null) {
            LimbikaViewItemValue limbikaViewItemValue = v.getLimbikaViewItemValue();
            mainActivity.fbtnShowLeftDrawer.performClick();
            mainActivity.setCurrentType(limbikaViewItemValue.getType());
            mainActivity.setCurrentItem(limbikaViewItemValue.getKey());
            limbikaViewForEdit = v;
            currentKey = limbikaViewItemValue.getKey();
            mainActivity.hideCurrentRightDrawer();
            mainActivity.opendBottomDrawerHide();
            setSelected(limbikaViewForEdit);

            changeColors(limbikaViewItemValue.getKey());
        }
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    private static Item cloneObject(Item obj) {
        try {
            Item clone = obj.getClass().newInstance();
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                field.set(clone, field.get(obj));
            }
            return clone;
        } catch (Exception e) {
            return null;
        }
    }

    //Set autoPlay add by reaz
    public void setAutoPlay(int autoPlay) {
        if (currentKey != null) {
            if (itemMap.containsKey(currentKey)) {
                Item item = itemMap.get(currentKey);
                item.setAutoPlay(autoPlay);
                itemMap.put(currentKey, item);
            }
        }
    }

    //Set autoPlay add by reaz
    public void setSoundDelay(int delay) {
        if (currentKey != null) {
            if (itemMap.containsKey(currentKey)) {
                Item item = itemMap.get(currentKey);
                item.setSoundDelay(delay);
                itemMap.put(currentKey, item);
            }
        }
    }

    // Creating a new Limbika View
    private void createLimbikaView(LimbikaView limbikaView, Item item) {
        LimbikaViewItemValue limbikaViewItemValue = new LimbikaViewItemValue();
        limbikaViewItemValue.setX(item.getX());
        limbikaViewItemValue.setY(item.getY());
        limbikaViewItemValue.setRotation(item.getRotation());
        limbikaViewItemValue.setKey(item.getKey());
        limbikaViewItemValue.setCircleColor(item.getCircleColor());
        limbikaViewItemValue.setUserText(item.getUserText());
        limbikaViewItemValue.setIsCircleView(item.getIsCircleView());
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
        limbikaViewItemValue.setFontAlign(item.getFontAlign());
        limbikaViewItemValue.setFontTypeFace(item.getFontTypeFace());
        limbikaViewItemValue.setBorderPixel(item.getBorderPixel());
        if (limbikaViewItemValue.getUserText().length() > 0) {
            setFontType(limbikaViewItemValue.getFontTypeFace(), limbikaView);
        }
        limbikaView.onResume(limbikaViewItemValue);
        limbikaView.setLayoutParams(new FrameLayout.LayoutParams(500, 500));
        flEditor.addView(limbikaView);
        prepareListners(limbikaView);
        limbikaView.setLimbikaView(limbikaView);
        limbikaView.saveViewState();

    }

    // for error Task created by reaz
    public void setError(int color, String ImagePath, String errorText) {
        if (currentTask != null) {
            if (color != -1 && ImagePath != null && ImagePath.length() > 0 && errorText.length() > 0)
                currentTask.setErrorBgColor(color);
            currentTask.setErrorImage(ImagePath);
            currentTask.setErrortext(errorText);
            CustomToast.t(context, getResources().getString(R.string.errorFeedback));
        }

    }

    public void setErrorMandatoryScreen(int errorMandatory) {
        if (currentTask != null) {
            currentTask.setErrorMandatoryScreen(errorMandatory);
        } else {
            currentTask.setErrorMandatoryScreen(0);
//            CustomToast.t(context, getResources().getString(R.string.errorFeedback));
        }
    }

    // for error Task created by reaz
    public void setErrorColor(int color) {
        if (currentTask != null) {
            if (color != -1)
                currentTask.setErrorBgColor(color);
        } else {
            currentTask.setErrorBgColor(-1);
//            CustomToast.t(context, getResources().getString(R.string.errorFeedback));
        }

    }

    public void setErrorImagePath(String errorImagePath) {
        if (currentTask != null) {
            if (errorImagePath != null && errorImagePath.length() > 0)
                currentTask.setErrorImage(errorImagePath);
        } else {
            currentTask.setErrorImage("");
//            CustomToast.t(context, getResources().getString(R.string.errorFeedback));
        }
    }

    public void setErrorText(String errorText) {
        if (currentTask != null) {
            if (errorText != null && errorText.length() > 0)
                currentTask.setErrortext(errorText);
        } else {
            currentTask.setErrortext("");
//            CustomToast.t(context, getResources().getString(R.string.errorFeedback));
        }

    }

    public void setErrorImage(Bitmap bitmap) {
        //scale bitmap
        Bitmap b = scaleBitmap(bitmap);
        errorImagePath = imageProcessing.imageSave(b);
        b.recycle();
    }

    public String getErrorImage() {
        //scale bitmap
        return errorImagePath;
    }

    ////////////////////////////////////Drag and Drop Rehandled////////////////////////////////////
    //Setting Drop items
    public void setDropItems(Long key) {
        //if I select same view as target and child this block gets called
        if (key == currentKey) {
            CustomToast.t(context, getResources().getString(R.string.dropTargetItemCannotSelect));
            mainActivity.isDragDropMode = false;
            return;
        }
        Item item = new Item();
        if (itemMap.containsKey(key)) {
            item = itemMap.get(key);
            item.setDragDropTarget(currentKey);
            itemMap.put(key, item);
            mainActivity.isDragDropMode = false;


            dragAndDropHappened(1, currentKey, null, key, item);
            //the change color method wont work here so call this method instead
            changeColorsForChild(key, 0);

            CustomToast.t(context, getResources().getString(R.string.itemSelectedCurrentDropTarget));

        }
    }

    //Setting Drop Target
    public void setDropTarget() {
        Item item = new Item();
        if (itemMap.containsKey(currentKey)) {
            //dont add if its a child to something else
            if (!checkIfAlreadyChild(currentKey)) {
                item = itemMap.get(currentKey);
                item.setAllowDragDrop(1);
                itemMap.put(currentKey, item);

                dragAndDropHappened(0, currentKey, item, -1, null);
            } else
                CustomToast.t(getActivity(), getResources().getString(R.string.dropTargetAlready));
        }
    }


    public boolean checkTarget() {

        Item item;
        item = itemMap.get(currentKey);
        if (item.getAllowDragDrop() > 0) {
            return true;
        } else {
            return false;
        }
    }


    public boolean checkChild() {

        Item item;
        item = itemMap.get(currentKey);
        if (item.getDragDropTarget() > 0) {
            return true;
        } else {
            return false;
        }
    }


    // the map contains targets key and the list of views assosiated with it
    HashMap<Long, ArrayList<Long>> targetAlongWithChild = new HashMap<>();
    HashMap<Long, Long> onlyAllChild = new HashMap<>();

    //called from oncreate
    public void intializeMandatoryVariables() {
        targetAlongWithChild = new HashMap<>();
        allLimbikaViews.clear();
    }

    //before putting anything as target
    public boolean checkIfAlreadyTarget(long key) {
        //if already target dont add
        boolean res = false;

        if (targetAlongWithChild.get(key) != null)
            res = true;

        return res;
    }

    //before putting anything as target
    public boolean checkIfAlreadyChild(long key) {
        //if already target dont add
        boolean res = false;

        /*for (Map.Entry<Long, Item> map : itemMap.entrySet()) {*/

        Item item = itemMap.get(key);
        //when child, this function returns key which is greater than 0
        if (item.getDragDropTarget() > 0) {
            res = true;

        }
        // }

        return res;
    }

    //this is called from the right drawer target button
    public void deleteTarget() {

        /**
         * called from activity right drawer 1.5.5 and erase
         * 1.delete the target from list
         * 2.delete the targetkey from child from the list
         * if current key creates any trouble get the the value from limbika view itself
         */

        if (currentKey != null) {
            ArrayList<Long> allChilds = targetAlongWithChild.get(currentKey);
            if (allChilds != null) {
                for (long key : allChilds) {
                    //itemMap has all the view keys and responsible for database operations
                    Item itemChild = itemMap.get(key);
                    itemChild.setDragDropTarget(0);
                    itemMap.put(key, itemChild);
                }
            }
            //delete it from list
            targetAlongWithChild.remove(currentKey);
        }

        //delete it from item map
        Item itemTarget = itemMap.get(currentKey);
        itemTarget.setAllowDragDrop(0);
        itemMap.put(currentKey, itemTarget);

    }

    public void deleteChild() {
        //just to ignore iteration exception
        HashMap<Long, ArrayList<Long>> tempMap = (HashMap<Long, ArrayList<Long>>) targetAlongWithChild.clone();

        if (currentKey != null) {
            for (Map.Entry<Long, ArrayList<Long>> map : tempMap.entrySet()) {
                long target = map.getKey();
                ArrayList<Long> allChilds = targetAlongWithChild.get(target);
                boolean found = false;
                if (allChilds != null) {
                    for (long key : allChilds) {
                        //itemMap has all the view keys and responsible for database operations
                        Item itemChild = itemMap.get(key);

                        if (itemChild.getDragDropTarget() > 0 && itemChild.getKey() == currentKey) {
                            allChilds.remove(key);
                            targetAlongWithChild.put(target, allChilds);
                            found = true;
                            break;
                        }
                    }
                }
                if (found)
                    break;
            }
        }


        //delete it from item map
        Item itemChild = itemMap.get(currentKey);
        itemChild.setDragDropTarget(0);
        itemMap.put(currentKey, itemChild);
    }


    //1. the child and the target both must know its limbika views
    //2. the process is handled like the itemMap has all the items and its associated targets
    //   only child limbika view knows its target (until today date:13-10-16)
    public void dragAndDropHappened(int chooser, long targetKey, Item targetItem, long childKey, Item childItem) {
        if (chooser == 0) {
            /////////////////target block
            ArrayList<Long> childList = targetAlongWithChild.get(targetKey);
            if (childList == null)
                targetAlongWithChild.put(targetKey, new ArrayList<Long>());
            else targetAlongWithChild.put(targetKey, childList);
        } else if (chooser == 1) {
            //after single tap child
            ArrayList<Long> childList = targetAlongWithChild.get(targetKey);
            if (childList == null)
                childList = new ArrayList<>();

            childList.add(childKey);
            targetAlongWithChild.put(targetKey, childList);

            onlyAllChild.put(childKey, childKey);
        } else if (chooser == 2) {
            //delete map
        }

    }


    ////////////////************ SHOW COLORS WHEN ****************///////////////////////

    public void changeColorsForChild(long key, int chooser) {
        LimbikaView child = allLimbikaViews.get(key);

        switch (chooser) {
            case 0:
                //drag drop
                child.drawMostOuterRectangle(getResources().getColor(R.color.orangeLight));
                break;
            case 1:
                //showed
                child.drawMostOuterRectangle(getResources().getColor(R.color.purple));
                break;
            case 2:
                //hidden
                child.drawMostOuterRectangle(getResources().getColor(R.color.greenLight));
                break;
        }


    }

    public void changeColors(long key) {
        //for drag and drop and assistive mode
        if (targetAlongWithChild.containsKey(key)) {
            ArrayList<Long> keys_list = targetAlongWithChild.get(key);
            if (keys_list != null) {
                for (long keyTemp : keys_list) {
                    LimbikaView v = allLimbikaViews.get(keyTemp);
                    if (v != null)
                        v.drawMostOuterRectangle(getResources().getColor(R.color.orangeLight));
                }
            }
        } else if (onlyAllChild.containsKey(key)) {
            //this block only colors the target not the child
            Item childItem = itemMap.get(key);
            long targetkey = childItem.getDragDropTarget();
            LimbikaView targetView = allLimbikaViews.get(targetkey);
            if (targetView != null)
                targetView.drawMostOuterRectangle(getResources().getColor(R.color.orangeLight));
        }

        ///works for showed by and hidden by
        Item item = itemMap.get(key);
        if (item.getShowedBy() > 0) {
            long showed_by_key = item.getShowedBy();
            LimbikaView v = allLimbikaViews.get(showed_by_key);
            if (v != null)
                v.drawMostOuterRectangle(getResources().getColor(R.color.purple));
        } else if (isShowedTarget(item.getKey())) {
            //this is when target is clicked and it doesnt have the child in its map
            ArrayList<Long> childList = showedMap.get(item.getKey());
            if (childList != null)
                for (long childKey : childList) {
                    LimbikaView v = allLimbikaViews.get(childKey);
                    if (v != null)
                        v.drawMostOuterRectangle(getResources().getColor(R.color.purple));
                }
        }
        /////////////hidden by//////////////////////////
        if (item.getHideBy() > 0) {
            long hiden_by_key = item.getHideBy();
            LimbikaView v = allLimbikaViews.get(hiden_by_key);
            if (v != null)
                v.drawMostOuterRectangle(getResources().getColor(R.color.greenLight));
        } else if (isHidenTarget(item.getKey())) {
            //this is when target is clicked and it doesnt have the child in its map
            ArrayList<Long> childList = hideMap.get(item.getKey());
            if (childList != null)
                for (long childKey : childList) {
                    LimbikaView v = allLimbikaViews.get(childKey);
                    if (v != null)
                        v.drawMostOuterRectangle(getResources().getColor(R.color.greenLight));
                }
        }
    }

    //handling hide by and showed by only targets are handled
    HashMap<Long, ArrayList<Long>> showedMap = new HashMap<>();
    HashMap<Long, ArrayList<Long>> hideMap = new HashMap<>();

    void addShowed(long keyTarget, long child) {
        ArrayList<Long> childList = (ArrayList<Long>) showedMap.get(keyTarget);
        if (childList == null) {
            childList = new ArrayList<>();
            childList.add(child);
            showedMap.put(keyTarget, childList);
        } else {
            childList.add(child);
            showedMap.put(keyTarget, childList);
        }
    }

    void addHidden(long keyTarget, long child) {
        ArrayList<Long> childList = (ArrayList<Long>) hideMap.get(keyTarget);
        if (childList == null) {
            childList = new ArrayList<>();
            childList.add(child);
            hideMap.put(keyTarget, childList);
        } else {
            childList.add(child);
            hideMap.put(keyTarget, childList);
        }
    }


    public String formatStringShowedHidden(ArrayList<Long> list) {
        String keys = "";
        for (int i = 0; i < list.size(); i++) {
            long key = list.get(i);
            if (i == (list.size() - 1))
                keys += String.valueOf(key);
            else
                keys += String.valueOf(key) + ",";
        }
        return keys;
    }

    boolean isShowedTarget(long key) {
        return showedMap.containsKey(key);
    }

    boolean isHidenTarget(long key) {
        return hideMap.containsKey(key);
    }


    //clear all the maps
    public void clearMaps() {
        itemMap.clear();
        allLimbikaViews.clear();
        targetAlongWithChild.clear();
        onlyAllChild.clear();

        showedMap.clear();
        hideMap.clear();
    }

    // Get All Limbika view
    public LinkedHashMap<Long, LimbikaView> getAllLimbikaView() {
        return allLimbikaViews;
    }

    public ArrayList<CustomList> getAllCorrectViews() {
        ArrayList<CustomList> customLists = new ArrayList<>();
        for (Map.Entry<Long, Item> itemValue : itemMap.entrySet()) {
            Item item = itemValue.getValue();

            if (item.getResult() != null)
                if (item.getResult().equals(TaskType.NORMAL_TRUE)) {

                    LimbikaView limbikaView = allLimbikaViews.get(itemValue.getKey());
                    ImageProcessing imageProcessing = new ImageProcessing(context);
                    CustomList customList = new CustomList();
                    customList.key = itemValue.getKey();
                    customList.imageName = imageProcessing.takeScreenshot(limbikaView);
                    customLists.add(customList);
                }
        }
        return customLists;
    }
}
