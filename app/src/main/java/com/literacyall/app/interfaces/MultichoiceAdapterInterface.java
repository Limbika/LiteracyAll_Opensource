package com.literacyall.app.interfaces;


import com.literacyall.app.dao.TaskPack;

/**
 * Created by Macbook on 28/09/2016.
 */

public class MultichoiceAdapterInterface {

    public interface ControlMethods{
        public void setSingleModeKey(TaskPack taskPack);
        public void addMultichoiseModeKey(TaskPack taskPack);
        public void clearMultichoiceMode();
        public void clearSingleChoiceMode();
    }

}
