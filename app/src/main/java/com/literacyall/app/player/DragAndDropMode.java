package com.literacyall.app.player;

import android.content.Context;

import com.dmk.limbikasdk.views.LimbikaView;
import com.literacyall.app.activities.PlayerActivity;
import com.literacyall.app.dao.Item;
import com.literacyall.app.dao.Task;
import com.literacyall.app.utilities.StaticAccess;


/**
 * Created by RAFI on 11/13/2016.
 */

public class DragAndDropMode {
    Context context;
    Task task;
    Item item;
    PlayerActivity activity;

    public DragAndDropMode(Context context, Task task) {
        this.context = context;
        this.task = task;
        activity=(PlayerActivity)context;

    }
// drag and drop single Tap handle
    public void singleTap(Item item){
        if (!activity.isBlockUser) {
            activity.isBlockUser = true;

            //now no Items will handle showed and hidden too
            if (item.getDragDropTarget() == 0 && item.getAllowDragDrop() == 0)
                activity.SingleTapTask(item, 0, true, true);
            else
                activity.SingleTapTask(item, 0, true, false);
        }
    }
    // drag and drop drag target handle
    public void onDropTarget(LimbikaView view,Long dropTargetKey ){
        Item selectedItem = activity.items.get(view.getLimbikaViewItemValue().getKey());
        Item selectedItemTarget = activity.items.get(dropTargetKey);
        if (selectedItem.getDragDropTarget() == dropTargetKey) {
            LimbikaView limbikaView = activity.dropItemsMap.get(selectedItem.getKey());
            limbikaView.showPositiveTag();
            LimbikaView limbikaViewTarget = activity.dropTargetsMap.get(dropTargetKey);


//                                        playSound(task.getPositiveSound());
            activity.positiveResult.remove(selectedItem.getKey());
            activity.alreadyCorrectDragandDrop.add(selectedItem.getKey());

            //check if all the dropItems of the target is already droped on it or anything left
            //if nothing is left unvlock the other views
            if (activity.error_mode_on_dragDrop)
                if (activity.ifTargetTotallyPlayed(dropTargetKey)) {
                    activity.makeAllViewsDragable();
                }


            if (task.getPositiveAnimation() > 0) {
                activity.showAnimation(limbikaViewTarget, task.getPositiveAnimation());
            }

            activity.playSoundDragAndAssistive(task, task.getPositiveSound());
            //when the item is dropped on target then show the hide/show item
            if (!selectedItem.getShowedByTarget().equals(""))
                activity.showHideDragandDrop(selectedItem);
            //if target has showedby/hiden by
            if (!selectedItemTarget.getHiddenByTarget().equals(""))
                activity.showHideDragandDrop(selectedItemTarget);
        }

    }
    // drag and drop drag target wrong handle
    public  void onDroppedonWrongTarge(LimbikaView view, Long dropTargetKey){
        if (dropTargetKey != -1) {
            Item selectedItem = activity.items.get(view.getLimbikaViewItemValue().getKey());
            LimbikaView limbikaView = activity.dropItemsMap.get(selectedItem.getKey());

            limbikaView.showNegativeTag();
            LimbikaView limbikaViewTarget = activity.dropTargetsMap.get(dropTargetKey);
            //we must draw negative animation on target
            if (activity.madatoryError == 1)
                activity.errorListner.onErrorListner(task, StaticAccess.TAG_TASK_DRAG_AND_DROP_MODE,
                        dropTargetKey);
            else {
                activity.playSoundDragAndAssistive(task, task.getNegativeSound());
            }
//                                        playSound(task.getNegativeSound());
            if (task.getNegativeAnimation() > 0) {
                //mir change
                activity.showAnimation(limbikaViewTarget, task.getNegativeAnimation());
            }
        }

    }
}
