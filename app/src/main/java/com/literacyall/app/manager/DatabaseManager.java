package com.literacyall.app.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.literacyall.app.dao.DaoMaster;
import com.literacyall.app.dao.DaoSession;
import com.literacyall.app.dao.Item;
import com.literacyall.app.dao.ItemDao;
import com.literacyall.app.dao.Result;
import com.literacyall.app.dao.ResultDao;
import com.literacyall.app.dao.Task;
import com.literacyall.app.dao.TaskDao;
import com.literacyall.app.dao.TaskPack;
import com.literacyall.app.dao.TaskPackDao;
import com.literacyall.app.dao.User;
import com.literacyall.app.dao.UserDao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import de.greenrobot.dao.async.AsyncOperation;
import de.greenrobot.dao.async.AsyncOperationListener;
import de.greenrobot.dao.async.AsyncSession;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author Octa
 */
public class DatabaseManager implements IDatabaseManager, AsyncOperationListener {

    /**
     * Class tag. Used for debug.
     */
    private static final String TAG = DatabaseManager.class.getCanonicalName();
    /**
     * Instance of DatabaseManager
     */
    private static DatabaseManager instance;
    /**
     * The Android Activity reference for access to DatabaseManager.
     */
    private Context context;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase database;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private AsyncSession asyncSession;
    private List<AsyncOperation> completedOperations;

    /**
     * Constructs a new DatabaseManager with the specified arguments.
     *
     * @param context The Android {@link android.content.Context}.
     */
    public DatabaseManager(final Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(this.context, "sample-database", null);
        completedOperations = new CopyOnWriteArrayList<AsyncOperation>();
    }

    /**
     * @param context The Android {@link android.content.Context}.
     * @return this.instance
     */
    public static DatabaseManager getInstance(Context context) {

        if (instance == null) {
            instance = new DatabaseManager(context);
        }

        return instance;
    }

    @Override
    public void onAsyncOperationCompleted(AsyncOperation operation) {
        completedOperations.add(operation);
    }

    private void assertWaitForCompletion1Sec() {
        asyncSession.waitForCompletion(1000);
        asyncSession.isCompleted();
    }

    /**
     * Query for readable DB
     */
    public void openReadableDb() throws SQLiteException {
        database = mHelper.getReadableDatabase();
        daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
        asyncSession = daoSession.startAsyncSession();
        asyncSession.setListener(this);
    }

    /**
     * Query for writable DB
     */
    public void openWritableDb() throws SQLiteException {
        database = mHelper.getWritableDatabase();
        daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
        asyncSession = daoSession.startAsyncSession();
        asyncSession.setListener(this);
    }

    @Override
    public void closeDbConnections() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
        if (database != null && database.isOpen()) {
            database.close();
        }
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
        if (instance != null) {
            instance = null;
        }
    }

    @Override
    public synchronized void dropDatabase() {
        try {
            openWritableDb();
            DaoMaster.dropAllTables(database, true); // drops all tables
            mHelper.onCreate(database);              // creates the tables
            asyncSession.deleteAll(User.class);    // clear all elements from a table
            asyncSession.deleteAll(Task.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized User insertUser(User user) {
        try {
            if (user != null) {
                openWritableDb();
                UserDao userDao = daoSession.getUserDao();
                userDao.insert(user);
                Log.d(TAG, "Inserted user: " + user.getName() + " to the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public synchronized ArrayList<User> listUsers() {
        List<User> users = null;
        try {
            openReadableDb();
            UserDao userDao = daoSession.getUserDao();
            users = userDao.loadAll();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (users != null) {
            return new ArrayList<>(users);
        }
        return null;
    }

    @Override
    public synchronized void updateUser(User user) {
        try {
            if (user != null) {
                openWritableDb();
                daoSession.update(user);
                Log.d(TAG, "Updated user: " + user.getName() + " from the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void deleteUserByEmail(String email) {
        try {
            openWritableDb();
            UserDao userDao = daoSession.getUserDao();
            QueryBuilder<User> queryBuilder = userDao.queryBuilder().where(UserDao.Properties.Name.eq(email));
            List<User> userToDelete = queryBuilder.list();
            for (User user : userToDelete) {
                userDao.delete(user);
            }
            daoSession.clear();
            Log.d(TAG, userToDelete.size() + " entry. " + "Deleted user: " + email + " from the schema.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized boolean deleteUserById(Long userId) {
        try {
            openWritableDb();
            UserDao userDao = daoSession.getUserDao();
            userDao.deleteByKey(userId);
            daoSession.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public synchronized User getUserById(Long userId) {
        User user = null;
        try {
            openReadableDb();
            UserDao userDao = daoSession.getUserDao();
            user = userDao.load(userId);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public synchronized void deleteUsers() {
        try {
            openWritableDb();
            UserDao userDao = daoSession.getUserDao();
            userDao.deleteAll();
            daoSession.clear();
            Log.d(TAG, "Delete all users from the schema.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Long insertTask(Task task) {
        Long id = null;
        try {
            if (task != null) {
                openWritableDb();
                TaskDao taskDao = daoSession.getTaskDao();
                id = taskDao.insert(task);
                Log.d(TAG, "Inserted task: " + task.getName() + " to the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public List<Task> listTasks() {
        List<Task> tasks = null;
        try {
            openReadableDb();
            TaskDao taskDao = daoSession.getTaskDao();
            tasks = taskDao.loadAll();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tasks != null) {
            return tasks = new ArrayList<>(tasks);
        }
        return tasks;
    }

    @Override
    public List<Task> listTasksByTAskPackId(long taskPackId) {
        List<Task> tasks = null;
        try {
            openWritableDb();
            TaskDao taskDao = daoSession.getTaskDao();
            QueryBuilder<Task> queryBuilder = taskDao.queryBuilder().where(TaskDao.Properties.TaskPackId.eq(taskPackId)).orderAsc(TaskDao.Properties.SlideSequence);
            tasks = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tasks != null) {
            return tasks = new ArrayList<>(tasks);
        }
        return tasks;
    }

    @Override
    public void updateTask(Task task) {
        try {
            if (task != null) {
                openWritableDb();
                daoSession.update(task);
                Log.d(TAG, "Updated task: " + task.getName() + " from the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean deleteTaskById(Long taskId) {
        try {
            openWritableDb();
            TaskDao taskDao = daoSession.getTaskDao();
            taskDao.deleteByKey(taskId);
            daoSession.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Task getTaskById(Long taskId) {
        Task task = null;
        try {
            openReadableDb();
            TaskDao taskDao = daoSession.getTaskDao();
            task = taskDao.load(taskId);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return task;
    }

    @Override
    public void deleteTasks() {
        try {
            openWritableDb();
            TaskDao taskDao = daoSession.getTaskDao();
            taskDao.deleteAll();
            daoSession.clear();
            Log.d(TAG, "Delete all tasks from the schema.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public User userWiseTask() {
        User user = null;
        try {
            openReadableDb();
            ArrayList<User> users = new ArrayList<>();
            Cursor cursor = daoSession.getDatabase().rawQuery("Select * from User", null);

            try {
                DateFormat dateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                if (cursor.moveToFirst()) {
                    do {
                        user = new User();
                        user.setId(Long.valueOf(cursor.getString(cursor.getColumnIndex(UserDao.Properties.Id.columnName))));
                        user.setName(cursor.getString(cursor.getColumnIndex(UserDao.Properties.Password.columnName)));
                        user.setPassword(cursor.getString(cursor.getColumnIndex(UserDao.Properties.Password.columnName)));
                        user.setActive(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(UserDao.Properties.Active.columnName))));
                        //user.setCreatedAt((Date) dateFormat.parse(cursor.getString(cursor.getColumnIndex(UserDao.Properties.CreatedAt.columnName))));
                        //user.setUpdatedAt((Date) dateFormat.parse(cursor.getString(cursor.getColumnIndex(UserDao.Properties.UpdatedAt.columnName))));
                        users.add(user);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
            return user;
        }
        return user;
    }

    @Override
    public long insertItem(Item item) {
        Long id = null;
        try {
            if (item != null) {
                openWritableDb();
                ItemDao itemDao = daoSession.getItemDao();
                id = itemDao.insert(item);
                Log.d(TAG, "Inserted task: " + item.getX() + " to the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public List<Item> listItems() {
        List<Item> items = null;
        try {
            openReadableDb();
            ItemDao itemDao = daoSession.getItemDao();
            items = itemDao.loadAll();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (items != null) {
            return new ArrayList<>(items);
        }
        return items;
    }

    @Override
    public void updateItem(Item item) {
        try {
            if (item != null) {
                openWritableDb();
                daoSession.update(item);
                Log.d(TAG, "Updated item: " + item.getX() + " from the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean deleteItemById(Long itemId) {
        try {
            openWritableDb();
            ItemDao itemDao = daoSession.getItemDao();
            itemDao.deleteByKey(itemId);
            daoSession.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Item getItemById(Long itemId) {
        Item item = null;
        try {
            openReadableDb();
            ItemDao itemDao = daoSession.getItemDao();
            item = itemDao.load(itemId);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public void deleteItems() {
        try {
            openWritableDb();
            ItemDao itemDao = daoSession.getItemDao();
            itemDao.deleteAll();
            daoSession.clear();
            Log.d(TAG, "Delete all items from the schema.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Result insertResult(Result result) {
        try {
            if (result != null) {
                openWritableDb();
                ResultDao resultDao = daoSession.getResultDao();
                resultDao.insert(result);
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Result> listResults() {
        List<Result> results = null;
        try {
            openReadableDb();
            ResultDao resultDao = daoSession.getResultDao();
            results = resultDao.loadAll();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (results != null) {
            return new ArrayList<>(results);
        }
        return results;
    }

    @Override
    public void updateResult(Result result) {
        try {
            if (result != null) {
                openWritableDb();
                daoSession.update(result);
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean deleteResultById(Long resultId) {
        try {
            openWritableDb();
            ResultDao resultDao = daoSession.getResultDao();
            resultDao.deleteByKey(resultId);
            daoSession.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Result getResultById(Long resultId) {
        Result result = null;
        try {
            openReadableDb();
            ResultDao resultDao = daoSession.getResultDao();
            result = resultDao.load(resultId);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void deleteResults() {
        try {
            openWritableDb();
            ResultDao resultDao = daoSession.getResultDao();
            resultDao.deleteAll();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Checking correct ans
    public boolean getCorrectResult(Long key) {
        boolean result = false;
        try {
            openReadableDb();
            ArrayList<User> users = new ArrayList<>();
            Cursor cursor = daoSession.getDatabase().rawQuery("Select * from Result where key =" + key, null);

            try {
                if (cursor.moveToFirst()) {
                    do {
                        result = true;
                    } while (cursor.moveToNext());
                } else {
                    result = false;
                }
            } finally {
                cursor.close();
            }
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
// read data Item wise

    @Override
    public LinkedHashMap<Long, Item> loadTaskWiseItem(Task task) {
        LinkedHashMap<Long, Item> items = new LinkedHashMap<>();
        try {
            openReadableDb();
            String query = "Select * from Item where task =" + task.getId();
            Cursor cursor = daoSession.getDatabase().rawQuery(query, null);

            try {
                if (cursor.moveToFirst()) {
                    do {
                        Item item = new Item();
                        item.setId(cursor.getLong(cursor.getColumnIndex(ItemDao.Properties.Id.columnName)));
                        item.setX(cursor.getFloat(cursor.getColumnIndex(ItemDao.Properties.X.columnName)));
                        item.setY(cursor.getFloat(cursor.getColumnIndex(ItemDao.Properties.Y.columnName)));
                        item.setRotation(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.Rotation.columnName)));
                        item.setKey(cursor.getLong(cursor.getColumnIndex(ItemDao.Properties.Key.columnName)));
                        item.setIsCircleView(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.IsCircleView.columnName)));
                        item.setCircleColor(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.CircleColor.columnName)));
                        item.setUserText(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.UserText.columnName)));
                        item.setTextColor(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.TextColor.columnName)));
                        item.setTextSize(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.TextSize.columnName)));
                        item.setBorderColor(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.BorderColor.columnName)));
                        item.setBackgroundColor(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.BackgroundColor.columnName)));
                        item.setDrawable(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.Drawable.columnName)));
                        item.setWidth(cursor.getFloat(cursor.getColumnIndex(ItemDao.Properties.Width.columnName)));
                        item.setHeight(cursor.getFloat(cursor.getColumnIndex(ItemDao.Properties.Height.columnName)));
                        item.setLeft(cursor.getFloat(cursor.getColumnIndex(ItemDao.Properties.Left.columnName)));
                        item.setRight(cursor.getFloat(cursor.getColumnIndex(ItemDao.Properties.Right.columnName)));
                        item.setTop(cursor.getFloat(cursor.getColumnIndex(ItemDao.Properties.Top.columnName)));
                        item.setBottom(cursor.getFloat(cursor.getColumnIndex(ItemDao.Properties.Bottom.columnName)));
                        item.setImagePath(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.ImagePath.columnName)));
                        item.setTaskId(cursor.getLong(cursor.getColumnIndex(ItemDao.Properties.TaskId.columnName)));
                        item.setType(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.Type.columnName)));
                        item.setResult(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.Result.columnName)));
                        item.setAllowDragDrop(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.AllowDragDrop.columnName)));
                        item.setDragDropTarget(cursor.getLong(cursor.getColumnIndex(ItemDao.Properties.DragDropTarget.columnName)));
                        item.setNavigateTo(cursor.getLong(cursor.getColumnIndex(ItemDao.Properties.NavigateTo.columnName)));


                        item.setOpenApp(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.OpenApp.columnName)));
                        item.setShowedBy(cursor.getLong(cursor.getColumnIndex(ItemDao.Properties.ShowedBy.columnName)));
                        item.setHideBy(cursor.getLong(cursor.getColumnIndex(ItemDao.Properties.HideBy.columnName)));
                        item.setCornerRound(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.CornerRound.columnName)));
                        item.setCloseApp(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.CloseApp.columnName)));
                        //item.setAllowAlign(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.AllowAlign.columnName)));
                        //item.setAlignTarget(cursor.getLong(cursor.getColumnIndex(ItemDao.Properties.AlignTarget.columnName)));
                        //item.setAlignType(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.AlignType.columnName)));
                        //this line add for get open Url added by reaz
                        item.setOpenUrl(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.OpenUrl.columnName)));
                        item.setItemSound(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.ItemSound.columnName)));
                        item.setFontTypeFace(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.FontTypeFace.columnName)));   // added reaz
                        item.setFontAlign(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.FontAlign.columnName)));        // added reaz

                        item.setAutoPlay(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.AutoPlay.columnName)));         // added reaz
                        item.setSoundDelay(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.SoundDelay.columnName)));    // added reaz
                        item.setBorderPixel(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.BorderPixel.columnName)));
                        item.setShowedByTarget(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.ShowedByTarget.columnName)));
                        item.setHiddenByTarget(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.HiddenByTarget.columnName)));
                        item.setReadText(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.ReadText.columnName)));
                        item.setWriteText(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.WriteText.columnName)));

                        items.put(cursor.getLong(cursor.getColumnIndex(ItemDao.Properties.Key.columnName)), item);


                    } while (cursor.moveToNext());

                }
            } finally {
                cursor.close();
            }
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    @Override
    public Long insertTaskPack(TaskPack taskPask) {
        Long id = null;
        try {
            if (taskPask != null) {
                openWritableDb();
                TaskPackDao taskPackDao = daoSession.getTaskPackDao();
                id = taskPackDao.insert(taskPask);
                Log.d(TAG, "Inserted task: " + taskPask.getName() + " to the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public List<TaskPack> listTaskPacks() {
        List<TaskPack> taskPacks = null;
        try {
            openReadableDb();
            TaskPackDao taskPackDao = daoSession.getTaskPackDao();
            QueryBuilder<TaskPack> queryBuilder = taskPackDao.queryBuilder()
                    .orderAsc(TaskPackDao.Properties.Level).orderAsc(TaskPackDao.Properties.Name);
            taskPacks = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (taskPacks != null) {
            return taskPacks = new ArrayList<>(taskPacks);
        }
        return taskPacks;
    }

    @Override
    public List<TaskPack> getTaskPacksByType(String type) {
        List<TaskPack> taskPacks = null;
        try {
            openReadableDb();
            TaskPackDao taskPackDao = daoSession.getTaskPackDao();
            QueryBuilder<TaskPack> queryBuilder = taskPackDao.queryBuilder().where(TaskPackDao.Properties.Type.eq(type))
                    .orderAsc(TaskPackDao.Properties.Level).orderAsc(TaskPackDao.Properties.Name);
            taskPacks = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (taskPacks != null) {
            return taskPacks = new ArrayList<>(taskPacks);
        }
        return taskPacks;
    }

    @Override
    public List<TaskPack> getTaskPacksByName(String name) {
        List<TaskPack> taskPacks = null;
        try {
            openReadableDb();
            TaskPackDao taskPackDao = daoSession.getTaskPackDao();
            QueryBuilder<TaskPack> queryBuilder = taskPackDao.queryBuilder().whereOr(TaskPackDao.Properties.Level.like("%" + name + "%"),
                    TaskPackDao.Properties.Name.like("%" + name + "%"));
            taskPacks = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (taskPacks != null) {
            return taskPacks = new ArrayList<>(taskPacks);
        }
        return taskPacks;
    }

    @Override
    public List<TaskPack> getTaskPacksByName(String name, String type) {
        List<TaskPack> taskPacks = null;
        try {
            openReadableDb();
            TaskPackDao taskPackDao = daoSession.getTaskPackDao();
            QueryBuilder<TaskPack> queryBuilder = taskPackDao.queryBuilder().where(TaskPackDao.Properties.Type.eq(type))
                    .whereOr(TaskPackDao.Properties.Level.like("%" + name + "%"), TaskPackDao.Properties.Name.like("%" + name + "%"));
            taskPacks = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (taskPacks != null) {
            return taskPacks = new ArrayList<>(taskPacks);
        }
        return taskPacks;
    }

    @Override
    public void updateTaskPack(TaskPack taskPack) {
        try {
            if (taskPack != null) {
                openWritableDb();
                daoSession.update(taskPack);
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean deleteTaskPackById(Long taskPackId) {
        try {
            openWritableDb();
            TaskPackDao taskPackDao = daoSession.getTaskPackDao();
            taskPackDao.deleteByKey(taskPackId);
            daoSession.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public TaskPack getTaskPackById(Long taskPackId) {
        TaskPack taskPack = null;
        try {
            openReadableDb();
            TaskPackDao taskPackDao = daoSession.getTaskPackDao();
            taskPack = taskPackDao.load(taskPackId);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taskPack;
    }

    @Override
    public void deleteTaskPacks() {

    }

    @Override
    public void deleteTasksByTaskPack(long taskPackId) {
        try {
            openWritableDb();
            TaskDao taskDao = daoSession.getTaskDao();
            QueryBuilder<Task> queryBuilder = taskDao.queryBuilder().where(TaskDao.Properties.TaskPackId.eq(taskPackId));
            List<Task> tasks = queryBuilder.list();
            for (Task task : tasks) {
                taskDao.delete(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getMaxTaskPosition(long taskPackId) {
        openWritableDb();
        TaskDao taskDao = daoSession.getTaskDao();
        QueryBuilder<Task> queryBuilder = taskDao.queryBuilder().where(TaskDao.Properties.TaskPackId.eq(taskPackId)).orderDesc(TaskDao.Properties.SlideSequence).limit(1);
        List<Task> tasks = queryBuilder.list();
        if (tasks.size() > 0) {
            return tasks.get(0).getSlideSequence();
        } else {
            return 0;
        }


    }

    @Override
    public void deleteItemsByTask(long taskId) {
        try {
            openWritableDb();
            ItemDao itemDao = daoSession.getItemDao();
            QueryBuilder<Item> queryBuilder = itemDao.queryBuilder().where(ItemDao.Properties.Task.eq(taskId));
            List<Item> items = queryBuilder.list();
            for (Item task : items) {
                itemDao.delete(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Item> loadItemByTask(Task task) {
        try {
            openWritableDb();
            ItemDao itemDao = daoSession.getItemDao();
            QueryBuilder<Item> queryBuilder = itemDao.queryBuilder().where(ItemDao.Properties.Task.eq(task.getId()));
            ArrayList<Item> items = (ArrayList<Item>) queryBuilder.list();
            daoSession.clear();

            return items;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

}
