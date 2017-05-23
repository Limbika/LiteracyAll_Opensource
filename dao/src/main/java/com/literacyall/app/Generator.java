package com.literacyall.app;


//1.To built the "Green Dao" library it need a custom builder.
//To create a library builder try
// -> Edit configuration from "Setup Run/Debug Configuration"
// -> "+"
// -> Application
// -> Put a name ex. "Dao"
// -> Mainclass - "Generator"
// -> Use classpath of Module
// -> "dao"
// -> Save changes.

//2.To add a new table, use "addTables" method.

//3.To add a new column just "addDateProperty" in the following table and build the dao library. It will automatically
// generate the model class for the table.


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class Generator {
    private static final String PROJECT_DIR = System.getProperty("user.dir").replace("\\", "/");
    private static final String OUT_DIR = PROJECT_DIR + "/app/src/main/java";

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "com.literacyall.app.dao");
        addTables(schema);
        new DaoGenerator().generateAll(schema, OUT_DIR);
    }

    /**
     * Create tables and the relationships between them
     */
    private static void addTables(Schema schema) {
        /* entities */
        Entity user = addUser(schema);
        Entity taskPack = addTaskPack(schema);
        Entity task = addTask(schema, user);
        Entity item = addItem(schema, task);
        Entity result = addResult(schema, task, item);
    }

    /**
     * Create user's Properties
     *
     * @return DBUser entity
     */
    private static Entity addUser(Schema schema) {
        Entity user = schema.addEntity("User");
        user.addIdProperty().primaryKey().autoincrement();
        user.addStringProperty("name").notNull();
        user.addStringProperty("password").notNull();
        user.addBooleanProperty("active").notNull();
        user.addDateProperty("createdAt").notNull();
        user.addDateProperty("updatedAt").notNull();
        return user;
    }

    /**
     * Create task Properties
     *
     * @return Task entity
     */
    private static Entity addTask(Schema schema, Entity user) {

        Entity task = schema.addEntity("Task");
        task.addLongProperty("id").primaryKey();
        task.addLongProperty("uniqId").notNull();
        task.addStringProperty("name").notNull();
        task.addStringProperty("taskImage").notNull();
        task.addStringProperty("type").notNull();
        task.addIntProperty("backgroundColor").notNull();
        task.addIntProperty("slideSequence").notNull();
        task.addBooleanProperty("active");
        task.addDateProperty("createdAt");
        task.addDateProperty("updatedAt");
        task.addLongProperty("taskPackId");
        task.addStringProperty("feedbackImage").notNull();
        task.addIntProperty("feedbackAnimation").notNull();
        task.addIntProperty("positiveAnimation").notNull();
        task.addIntProperty("negativeAnimation").notNull();
        task.addStringProperty("feedbackSound").notNull();
        task.addStringProperty("positiveSound").notNull();
        task.addStringProperty("negativeSound").notNull();
        task.addIntProperty("feedbackType").notNull();

        task.addIntProperty("errorBgColor").notNull();
        task.addStringProperty("errorImage").notNull();
        task.addStringProperty("errortext").notNull();
        task.addIntProperty("errorMandatoryScreen").notNull();
        task.addStringProperty("sequenceText").notNull();

        Property userIdProperty = task.addLongProperty("userId").notNull().getProperty();
        ToMany userToTask = user.addToMany(task, userIdProperty);
        userToTask.setName("tasks");

        return task;
    }

    /**
     * Create item Properties
     *
     * @return Item entity
     */
    private static Entity addItem(Schema schema, Entity task) {
        Entity item = schema.addEntity("Item");
        item.addIdProperty().primaryKey().autoincrement();
        item.addFloatProperty("x");
        item.addFloatProperty("y");
        item.addIntProperty("rotation");
        item.addLongProperty("key");
        item.addIntProperty("isCircleView");
        item.addIntProperty("circleColor");
        item.addStringProperty("userText").notNull();;
        item.addIntProperty("textColor");
        item.addIntProperty("textSize");
        item.addIntProperty("borderColor");
        item.addIntProperty("backgroundColor");
        item.addIntProperty("drawable");
        item.addFloatProperty("width");
        item.addFloatProperty("height");
        item.addFloatProperty("left");
        item.addFloatProperty("right");
        item.addFloatProperty("top");
        item.addFloatProperty("bottom");
        item.addStringProperty("imagePath").notNull();
        item.addStringProperty("type").notNull();
        item.addDateProperty("createdAt");
        item.addDateProperty("updatedAt");
        item.addLongProperty("task");
        item.addStringProperty("itemSound").notNull(); //reaz added
        item.addStringProperty("result").notNull();
        item.addStringProperty("openApp").notNull();
        item.addStringProperty("openUrl").notNull();
        item.addIntProperty("allowDragDrop").notNull();
        item.addLongProperty("dragDropTarget").notNull();
        item.addIntProperty("cornerRound").notNull();
        item.addLongProperty("navigateTo").notNull();
        item.addLongProperty("showedBy").notNull();
        item.addLongProperty("hideBy").notNull();
        item.addIntProperty("closeApp").notNull();
        item.addIntProperty("fontTypeFace").notNull(); //add by reaz
        item.addIntProperty("fontAlign").notNull(); //add by reaz
        item.addIntProperty("autoPlay").notNull(); //add by reaz
        item.addIntProperty("soundDelay").notNull(); //add by reaz
        item.addIntProperty("borderPixel").notNull(); //add by reaz
        item.addStringProperty("showedByTarget").notNull();
        item.addStringProperty("hiddenByTarget").notNull();
        item.addStringProperty("readText").notNull();
        item.addStringProperty("writeText").notNull();

        Property taskIdProperty = item.addLongProperty("taskId").notNull().getProperty();
        ToMany taskToItem = task.addToMany(item, taskIdProperty);
        taskToItem.setName("items");
        return item;
    }

    /**
     * Create result Properties
     *
     * @return Result entity
     */
    private static Entity addResult(Schema schema, Entity task, Entity item) {
        Entity result = schema.addEntity("Result");
        result.addIdProperty().primaryKey().autoincrement();
        result.addLongProperty("key");
        result.addBooleanProperty("active");
        result.addDateProperty("createdAt");
        result.addDateProperty("updatedAt");

        Property taskIdProperty = result.addLongProperty("taskId").notNull().getProperty();
        ToMany taskToResult = task.addToMany(result, taskIdProperty);
        taskToResult.setName("taskResults");

        Property itemIdProperty = result.addLongProperty("itemId").notNull().getProperty();
        ToMany itemToResult = item.addToMany(result, itemIdProperty);
        itemToResult.setName("results");

        return result;
    }

    /**
     * Create Task Pack Properties
     *
     * @return Result entity
     */
    private static Entity addTaskPack(Schema schema) {
        Entity taskPack = schema.addEntity("TaskPack");
        taskPack.addIdProperty().primaryKey().autoincrement();
        taskPack.addStringProperty("name");
        taskPack.addStringProperty("level").notNull();// only for use literacyall App
        taskPack.addIntProperty("firstLayerTaskID").notNull();// only for use literacyall App
        taskPack.addIntProperty("touchAnimation").notNull(); //its a touch mode animation
        taskPack.addIntProperty("itemOfAnimation").notNull();
        taskPack.addDateProperty("createdAt");
        taskPack.addBooleanProperty("state");
        taskPack.addStringProperty("type");
        return taskPack;
    }
}
