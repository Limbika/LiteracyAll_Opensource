package com.literacyall.app.listener;

import com.literacyall.app.dao.Task;

/**
 * Created by RAFI on 9/21/2016.
 */

public class ErrorInterface {

    ErrorListner listener;

    public ErrorInterface(ErrorListner listener) {
        this.listener = listener;
    }

    public interface ErrorListner {
        void onErrorListner(Task task,int taskMode);
        void onErrorListner(Task task,int taskMode,long targetKey);
    }

    public ErrorListner getListener() {
        return listener;
    }
}
