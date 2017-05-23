package com.literacyall.app.customui;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.literacyall.app.R;
import com.literacyall.app.dao.Task;
import com.literacyall.app.utilities.ImageProcessing;


public class BottomDrawerCustomeView extends RelativeLayout {

    Context context;
    ImageView ivBottomDrawer;
    TextView txt;
    ImageProcessing imageProcessing;
    Task task;
    int id = 0;
    String imageName;
    String taskType;
    String slideType;

    int text_id = 0;

    public BottomDrawerCustomeView(Context context) {
        super(context);
        this.context = context;
        init(context);
        imageProcessing = new ImageProcessing(context);

    }

    public BottomDrawerCustomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public BottomDrawerCustomeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
    }

    public void setIds(int id) {
        this.id = id;
    }

    public int getIds() {
        return id;
    }


    public void setTextId(int id) {
        this.text_id = id;
    }

    public int getTextIds() {
        return text_id;
    }


    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.bottom_drawer, null);
        txt = (TextView) view.findViewById(R.id.txt);
        ivBottomDrawer = (ImageView)  view.findViewById(R.id.ivBottomDrawer);
        addView(view);
    }

    public void setText() {
        txt.setText(String.valueOf(this.text_id));
    }

    public void setImage(String image) {
//        ivBottomDrawer.setImageBitmap(imageProcessing.getImage(image));
        imageProcessing.setImageWith_loader(ivBottomDrawer,image);
        imageName = image;
    }

    public void setTask(Task task)
    {
        this.task = task;
    }

    public Task getTask()
    {
        return this.task;
    }

    public String getImage()
    {
        return imageName;
    }
// change by reaz for Task Type change
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
    // change by reaz for Task Type change
    public String getTaskType()
    {
        return taskType;
    }


    public void setSlideType(String slideType){
        this.slideType = slideType;
    }

    public  String  getSlideType(){
        return slideType;
    }
}
