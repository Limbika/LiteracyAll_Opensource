package com.literacyall.app.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.literacyall.app.activities.MultichoiceViewActivity;
import com.literacyall.app.dao.TaskPack;
import com.literacyall.app.interfaces.MultichoiceAdapterInterface;

import java.util.ArrayList;

/**
 * Created by Macbook on 28/09/2016.
 * PLEASE FOLLOW THE USER MODEL OF THIS PROJECT IF YOU WANT TO RE USE IT IN OTHER WAYS YOU MUST CHANGE IT ON YOU OWN
 * THE SEARCHING AND HANDLING IS DEPENDENT ON A LONG KEY SO YOUR MODEL MUST HAVE A LONG KEY
 */

public abstract class MultiChoiceAdapter extends BaseAdapter implements MultichoiceAdapterInterface.ControlMethods {

    //this array list contains keys of selected items
    public ArrayList<TaskPack> taskPacksTemp = new ArrayList<>();
    //this variable holds the key of single item selected
    public long SingleModeKey = -1;

    ArrayList<TaskPack> taskPacks = new ArrayList<>();
    //MultichoiceViewActivity acticity;
    Context ctx;

    boolean multichoiceMode = false;

    public MultiChoiceAdapter(MultichoiceViewActivity activity, ArrayList<TaskPack> taskPacks) {
        this.taskPacks = taskPacks;
        //this.acticity = activity;
        ctx = activity;
        activity.setMultichoiceListener(this);
    }

    //////////////////////////////////////////CONTROL METHODS OF INTERFACE/////////////////
    //Called from the Implemented Activity through MultichoiceInterface
    @Override
    public void setSingleModeKey(TaskPack user) {
        //this block will be entered when previously u have selected an item
        long singleModeKey = user.getId();
        if (!multichoiceMode) {
            if (SingleModeKey != -1) {

                //checking previous view and current view is same or not
                if (singleModeKey == SingleModeKey) {
                    singleTapModeDone(SingleModeKey);

                } else {

                    changeToSelectedOrDeselect(singleModeKey, SingleModeKey);
                    SingleModeKey = singleModeKey;
                    singleTapModeOn(SingleModeKey);
                }
            } else {

                changeToSelectedOrDeselect(singleModeKey, SingleModeKey);
                SingleModeKey = singleModeKey;
                singleTapModeOn(SingleModeKey);
            }
        } else {
            //multichoice mode on so add it in list
            if(!isInMultichoicealready(user)) {
                SingleModeKey = -1;
                multichoiceMode = true;
                taskPacksTemp.add(user);
                //notify adapter
                changeToSelectedOrDeselectMultiChoice(user,true);
                //notifying activity
                multiChoiceModeOn((ArrayList<TaskPack>) taskPacksTemp.clone(), multichoiceMode);
            }else{
                removeSelectionInSingleClick(user);
                //notifying adapter
                changeToSelectedOrDeselectMultiChoice(user,false);
            }
        }
    }

    @Override
    public void addMultichoiseModeKey(TaskPack u) {
        if(!isInMultichoicealready(u)) {
            multichoiceMode = true;
            taskPacksTemp.add(u);
            changeToSelectedOrDeselectMultiChoice(u,true);
            multiChoiceModeOn((ArrayList<TaskPack>) taskPacksTemp.clone(), multichoiceMode);
        }else{
            //if already selected deselect it from activity
            removeSelectionInSingleClick(u);
            //notifying adapter
            changeToSelectedOrDeselectMultiChoice(u,false);
        }
    }

    @Override
    public void clearMultichoiceMode() {
        taskPacksTemp.clear();
        multichoiceMode = false;
        changeToSelectedOrDeselect(-1, -1);
        multiChoiceModeOff();
    }

    @Override
    public void clearSingleChoiceMode() {
        SingleModeKey = -1;
        changeToSelectedOrDeselect(-1,-1);
        singleTapModeOff();
    }


    public boolean isInMultichoicealready(TaskPack taskpack){
        long key=taskpack.getId();
        for(TaskPack taskTemp:taskPacksTemp){
            if(taskTemp.getId()==key){
                return true;
            }
        }
        return  false;
    }

    public void removeSelectionInSingleClick(TaskPack taskpack){
        long key=taskpack.getId();
        ArrayList<TaskPack> tempList= (ArrayList<TaskPack>) taskPacksTemp.clone();
        for(TaskPack taskTemp:tempList){
            if(taskTemp.getId()==key){
                taskPacksTemp.remove(taskpack);
            }
        }
        multiChoiceModeOn((ArrayList<TaskPack>) taskPacksTemp.clone(), multichoiceMode);
    }

    //////////////////////////////////////////CONTROL METHODS OF INTERFACE FINISH/////////////////

    abstract void singleTapModeOn(long key);

    abstract void singleTapModeDone(long key);

    abstract void multiChoiceModeOn(ArrayList<TaskPack> userList, boolean mode);

    abstract void singleTapModeOff();

    abstract void multiChoiceModeOff();

    abstract void notifyDatasetChangeCustom(ArrayList<TaskPack> userList);


    /// Class Methods

    //use this method to select or desect keys from both single and multichoice
    //key=-1,prevKey=-1===== deselect/clear all
    //key=value, prevKey=-1 ===== select item
    // key=value, prevKey =value ==== select key and deselt prevkey
    public void changeToSelectedOrDeselect(long key, long prevKey) {
        //1.clearing everything
        ArrayList<TaskPack> cloneUsers = new ArrayList<>();
        if (prevKey == -1 && key == -1) {
            for (TaskPack taskPack : taskPacks) {
                taskPack.setState(false);
                cloneUsers.add(taskPack);
            }
            notifyDatasetChangeCustom((ArrayList<TaskPack>) cloneUsers.clone());
        }
        //2. Select Item
        if (prevKey == -1 && key > -1) {
            for (TaskPack user : taskPacks) {
                if (user.getId() == key)
                    user.setState(true);
                else
                    user.setState(false);
                cloneUsers.add(user);
            }
            notifyDatasetChangeCustom((ArrayList<TaskPack>) cloneUsers.clone());
        }

        //2. Deselect prev & Select Item
        if (prevKey > -1 && key > -1) {
            for (TaskPack user : taskPacks) {
                if (user.getId() == prevKey) {
                    user.setState(false);
                    cloneUsers.add(user);
                } else if (user.getId() == key) {
                    user.setState(true);
                    cloneUsers.add(user);
                } else {
                    user.setState(false);
                    cloneUsers.add(user);
                }
            }
            notifyDatasetChangeCustom((ArrayList<TaskPack>) cloneUsers.clone());
        }

    }

    public void changeToSelectedOrDeselectMultiChoice(TaskPack u, boolean select) {
        ArrayList<TaskPack> cloneUsers = new ArrayList<>();

        for (TaskPack user : taskPacks) {
            if (user.getId() == u.getId()) {
                if(select)
                user.setState(true);
                else user.setState(false);

                break;
            }

        }
        notifyDatasetChangeCustom((ArrayList<TaskPack>) taskPacks.clone());
    }



}
