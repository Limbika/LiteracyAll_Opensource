package com.literacyall.app.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "TASK_PACK".
 */
public class TaskPack {

    private Long id;
    private String name;
    /** Not-null value. */
    private String level;
    private int firstLayerTaskID;
    private int touchAnimation;
    private int itemOfAnimation;
    private java.util.Date createdAt;
    private Boolean state;
    private String type;

    public TaskPack() {
    }

    public TaskPack(Long id) {
        this.id = id;
    }

    public TaskPack(Long id, String name, String level, int firstLayerTaskID, int touchAnimation, int itemOfAnimation, java.util.Date createdAt, Boolean state, String type) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.firstLayerTaskID = firstLayerTaskID;
        this.touchAnimation = touchAnimation;
        this.itemOfAnimation = itemOfAnimation;
        this.createdAt = createdAt;
        this.state = state;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** Not-null value. */
    public String getLevel() {
        return level;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setLevel(String level) {
        this.level = level;
    }

    public int getFirstLayerTaskID() {
        return firstLayerTaskID;
    }

    public void setFirstLayerTaskID(int firstLayerTaskID) {
        this.firstLayerTaskID = firstLayerTaskID;
    }

    public int getTouchAnimation() {
        return touchAnimation;
    }

    public void setTouchAnimation(int touchAnimation) {
        this.touchAnimation = touchAnimation;
    }

    public int getItemOfAnimation() {
        return itemOfAnimation;
    }

    public void setItemOfAnimation(int itemOfAnimation) {
        this.itemOfAnimation = itemOfAnimation;
    }

    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}