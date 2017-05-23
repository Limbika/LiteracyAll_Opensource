package com.literacyall.app.manager;

import com.literacyall.app.dao.Item;
import com.literacyall.app.dao.Result;
import com.literacyall.app.dao.Task;
import com.literacyall.app.dao.TaskPack;
import com.literacyall.app.dao.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Interface that provides methods for managing the database inside the Application.
 *
 * @author Octa
 */
public interface IDatabaseManager {

    /**
     * Closing available connections
     */
    void closeDbConnections();

    /**
     * Delete all tables and content from our database
     */
    void dropDatabase();

    /**
     * Insert a user into the DB
     *
     * @param user to be inserted
     */
    User insertUser(User user);

    /**
     * List all the users from the DB
     *
     * @return list of users
     */
    ArrayList<User> listUsers();

    /**
     * Update a user from the DB
     *
     * @param user to be updated
     */
    void updateUser(User user);

    /**
     * Delete all users with a certain email from the DB
     *
     * @param email of users to be deleted
     */
    void deleteUserByEmail(String email);

    /**
     * Delete a user with a certain id from the DB
     *
     * @param userId of users to be deleted
     */
    boolean deleteUserById(Long userId);

    /**
     * @param userId - of the user we want to fetch
     * @return Return a user by its id
     */
    User getUserById(Long userId);

    /**
     * Delete all the users from the DB
     */
    void deleteUsers();

    /**
     * Insert a user into the DB
     *
     * @param task to be inserted
     */
    Long insertTask(Task task);

    /**
     * List all the users from the DB
     *
     * @return list of users
     */
    List<Task> listTasks();

    /**
     * List all the users from the DB
     *
     * @return list of tasks according to Task Pack Id
     */
    List<Task> listTasksByTAskPackId(long taskPackId);

    /**
     * Update a user from the DB
     *
     * @param task to be updated
     */
    void updateTask(Task task);

    /**
     * Delete a user with a certain id from the DB
     *
     * @param TaskId of users to be deleted
     */
    boolean deleteTaskById(Long TaskId);

    /**
     * @param TaskId - of the user we want to fetch
     * @return Return a user by its id
     */
    Task getTaskById(Long TaskId);

    /**
     * Delete all the users from the DB
     */
    void deleteTasks();

    /**
     * List all the users task from the DB
     *
     * @return list of users
     */
    User userWiseTask();


    /**
     * Insert a user into the DB
     *
     * @param item to be inserted
     */
    long insertItem(Item item);

    /**
     * List all the users from the DB
     *
     * @return list of items
     */
    List<Item> listItems();

    /**
     * Update a user from the DB
     *
     * @param item to be updated
     */
    void updateItem(Item item);

    /**
     * Delete a user with a certain id from the DB
     *
     * @param itemId of users to be deleted
     */
    boolean deleteItemById(Long itemId);

    /**
     * @param itemId - of the user we want to fetch
     * @return Return a user by its id
     */
    Item getItemById(Long itemId);

    /**
     * Delete all the users from the DB
     */
    void deleteItems();

    /**
     * Insert a user into the DB
     *
     * @param result to be inserted
     */
    Result insertResult(Result result);

    /**
     * List all the results from the DB
     *
     * @return list of results
     */
    List<Result> listResults();

    /**
     * Update a result from the DB
     *
     * @param result to be updated
     */
    void updateResult(Result result);

    /**
     * Delete a user with a certain id from the DB
     *
     * @param resultId of users to be deleted
     */
    boolean deleteResultById(Long resultId);

    /**
     * @param resultId - of the user we want to fetch
     * @return Return a user by its id
     */
    Result getResultById(Long resultId);

    /**
     * Delete all the users from the DB
     */
    void deleteResults();

    /**
     * Checking correct answer
     */
    boolean getCorrectResult(Long key);

    /**
     * Load task wise item
     */
    LinkedHashMap<Long, Item> loadTaskWiseItem(Task task);

    /**
     * Insert a user into the DB
     *
     * @param taskPask to be inserted
     */
    Long insertTaskPack(TaskPack taskPask);

    /**
     * List all the users from the DB
     *
     * @return list of TaskPacks
     */
    List<TaskPack> listTaskPacks();

    /**
     *Taskpack by type from the DB
     *
     * @return list of TaskPacks
     */
    List<TaskPack> getTaskPacksByType(String type);

    /**
     *Taskpack by Name/Level from the DB
     *
     * @return list of TaskPacks
     */
    List<TaskPack> getTaskPacksByName(String name);

    /**
     *Taskpack by Name/Level and type from the DB
     *
     * @return list of TaskPacks
     */
    List<TaskPack> getTaskPacksByName(String name, String type);
    /**
     * Update a user from the DB
     *
     * @param taskPack to be updated
     */
    void updateTaskPack(TaskPack taskPack);

    /**
     * Delete a user with a certain id from the DB
     *
     * @param taskPackId of taskPack to be deleted
     */
    boolean deleteTaskPackById(Long taskPackId);

    /**
     * @param taskPackId - of the user we want to fetch
     * @return Return a user by its id
     */
    TaskPack getTaskPackById(Long taskPackId);

    /**
     * Delete all the users from the DB
     */
    void deleteTaskPacks();

    /**
     * Delete tasks by TaskPackId
     */
    void deleteTasksByTaskPack(long taskPackId);

    /**
     * Get Task position to insert
     */
    int getMaxTaskPosition(long taskPackId);

    /**
     * Delete items by TaskId
     */
    void deleteItemsByTask(long taskId);
}
