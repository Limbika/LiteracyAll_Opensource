package com.literacyall.app.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.literacyall.app.R;
import com.literacyall.app.dao.Item;
import com.literacyall.app.dao.ItemDao;
import com.literacyall.app.dao.Task;
import com.literacyall.app.dao.TaskDao;
import com.literacyall.app.dao.TaskPack;
import com.literacyall.app.dao.TaskPackDao;
import com.literacyall.app.dao.User;
import com.literacyall.app.dao.UserDao;
import com.literacyall.app.logging.L;
import com.literacyall.app.manager.DatabaseManager;
import com.literacyall.app.manager.IDatabaseManager;
import com.literacyall.app.utilities.StaticAccess;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by RAFI on 31-Mar-16.
 */
public class Share {

    Context context;
    private IDatabaseManager databaseManager;
    String appDataPath, appImagePath, appSoundPath, shareJSONPath, shareImagePath, shareSoundPath,
            receivedJSONPath, receivedImagePath, receivedSoundPath, zipPath, jsonFileName, zipCreatePath, receivedMainPath,generatedPathDel;


    public Share(Context context) {
        this.context = context;
        databaseManager = new DatabaseManager(context);
        appDataPath = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName();
        appImagePath = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_IMAGE;
        appSoundPath = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_SOUND;

        shareJSONPath = Environment.getExternalStorageDirectory() + StaticAccess.SD_CARD_ROOT_LITERACY_APP_GENERATED + StaticAccess.JSON;
        // StaticAccess.SD_CARD_ROOT_MIND_APP_GENERATED_IMAGE;
        shareImagePath = Environment.getExternalStorageDirectory() + StaticAccess.SD_CARD_ROOT_LITERACY_APP_GENERATED + StaticAccess.ANDROID_DATA_PACKAGE_IMAGE;
        // StaticAccess.SD_CARD_ROOT_MIND_APP_GENERATED_SOUND
        shareSoundPath = Environment.getExternalStorageDirectory() + StaticAccess.SD_CARD_ROOT_LITERACY_APP_GENERATED + StaticAccess.ANDROID_DATA_PACKAGE_SOUND;
        receivedMainPath = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName() + StaticAccess.RECEIVED_PATH;
        //StaticAccess.RECEIVED_JSON_PATH
        receivedJSONPath = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName() + StaticAccess.RECEIVED_PATH + StaticAccess.JSON;
        // StaticAccess.RECEIVED_IMAGE_PATH
        receivedImagePath = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName() + StaticAccess.RECEIVED_PATH + StaticAccess.ANDROID_DATA_PACKAGE_IMAGE;
        //StaticAccess.RECEIVED_SOUND_PATH
        receivedSoundPath = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName() + StaticAccess.RECEIVED_PATH + StaticAccess.ANDROID_DATA_PACKAGE_SOUND;
        ;

        jsonFileName = StaticAccess.JSON_FILE_NAME;
        zipPath = Environment.getExternalStorageDirectory() + StaticAccess.LITERACY_APP_GENERATED_ROOT_SLASH;
        generatedPathDel=Environment.getExternalStorageDirectory() + StaticAccess.SD_CARD_ROOT_LITERACY_APP_GENERATED;
        zipCreatePath = Environment.getExternalStorageDirectory() + StaticAccess.LITERACY_APP_NAME;
    }

    // Generate JSON from Database
    public String generateJSON() throws JSONException {
        ArrayList<User> users = databaseManager.listUsers();

        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        for (User user : users) {
            JSONObject userData = new JSONObject();
            userData.put(UserDao.Properties.Id.columnName, user.getId());
            userData.put(UserDao.Properties.Name.columnName, user.getName());
            userData.put(UserDao.Properties.Password.columnName, user.getPassword());
            userData.put(UserDao.Properties.Active.columnName, user.getActive());
            userData.put(UserDao.Properties.CreatedAt.columnName, user.getCreatedAt());
            userData.put(UserDao.Properties.UpdatedAt.columnName, user.getUpdatedAt());
            jsonArray.put(userData);
        }
        json.put(UserDao.TABLENAME, jsonArray);
        L.t(context, "" + json);

        // Save JSON to SD card
        try {
            File sdCardDirectory = new File(Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName() + StaticAccess.JSON);
            if (!sdCardDirectory.exists()) {
                sdCardDirectory.mkdirs();
            }

            String fileName = StaticAccess.FILE_NAME + System.currentTimeMillis() + StaticAccess.DOT_JSON;
            File jsonFile = new File(sdCardDirectory, fileName);
            FileWriter writer = new FileWriter(jsonFile);
            writer.append(json.toString());
            writer.flush();
            writer.close();
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    // Read JSON from SD card
    public void readJSON(String jsonFile) {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName() + StaticAccess.JSON, jsonFile);
            FileInputStream fileInputStream = new FileInputStream(file);
            String jsonStr = null;
            try {
                FileChannel fileChannel = fileInputStream.getChannel();
                MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
                jsonStr = Charset.defaultCharset().decode(mappedByteBuffer).toString();
            } finally {
                fileInputStream.close();
            }

            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray jsonArray = jsonObj.getJSONArray(UserDao.TABLENAME);

            ArrayList<User> users = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                DateFormat dateFormat = new SimpleDateFormat("EEE MMM d hh:mm:ss z yyyy");
                User user = new User();
                user.setId(Long.valueOf(jsonObject.getString(UserDao.Properties.Id.columnName)));
                user.setName(jsonObject.getString(UserDao.Properties.Name.columnName));
                user.setPassword(jsonObject.getString(UserDao.Properties.Password.columnName));
                user.setActive(Boolean.valueOf(jsonObject.getString(UserDao.Properties.Active.columnName)));
                user.setCreatedAt((Date) dateFormat.parse(jsonObject.getString(UserDao.Properties.CreatedAt.columnName)));
                user.setUpdatedAt((Date) dateFormat.parse(jsonObject.getString(UserDao.Properties.UpdatedAt.columnName)));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String zipDir() throws Exception {
        //String dir = Environment.getExternalStorageDirectory() + "/Android/Data/" + context.getPackageName() + "/Generated";
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();

        String fileName = StaticAccess.TASK_PACK_FILE_NAME + dateFormat.format(date) + StaticAccess.DOT_EXTENSION;
        String zipFile = zipCreatePath + fileName;

        File dirObj = new File(zipPath);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
        System.out.println("Creating : " + zipFile);
        addDir(dirObj, out);
        out.close();

        return fileName;
    }

    static void addDir(File dirObj, ZipOutputStream out) throws IOException {
        File[] files = dirObj.listFiles();
        byte[] tmpBuf = new byte[1024];

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                addDir(files[i], out);
                continue;
            }
            FileInputStream in = new FileInputStream(files[i].getAbsolutePath());

            File curentPath = new File(files[i].getParent());

            System.out.println(" Adding: " + curentPath.getName().toString() + StaticAccess.SLASH + files[i].getName());
            out.putNextEntry(new ZipEntry(curentPath.getName().toString() + StaticAccess.SLASH + files[i].getName()));
            int len;
            while ((len = in.read(tmpBuf)) > 0) {
                out.write(tmpBuf, 0, len);
            }
            out.closeEntry();
            in.close();
        }
    }

    //
    public void shareFiles(String fileName) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File file = new File(zipCreatePath + fileName);

        if (file.exists()) {
            intentShareFile.setType(StaticAccess.SET_TYPE);
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse(StaticAccess.FILE_SLASH + file.getAbsolutePath()));
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.mindAppTaskPack));
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "");
            context.startActivity(Intent.createChooser(intentShareFile, context.getResources().getString(R.string.shareFile)));
        }
    }

    // Unzipping files after receive
    public void unZip(String zipFile) throws ZipException, IOException {
        System.out.println(zipFile);
        int BUFFER = 2048;
        File file = new File(zipFile);

        ZipFile zip = new ZipFile(file);
        //String newPath = zipFile.substring(0, zipFile.length() - 4);

        //String newPath = Environment.getExternalStorageDirectory() + "/Android/Data/" + context.getPackageName() + "/Recieved";

        new File(receivedMainPath).mkdir();
        Enumeration zipFileEntries = zip.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();
            File destFile = new File(receivedMainPath, currentEntry);
            //destFile = new File(newPath, destFile.getName());
            File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            destinationParent.mkdirs();

            if (!entry.isDirectory()) {
                BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                int currentByte;
                // establish buffer for writing file
                byte data[] = new byte[BUFFER];

                // write the current file to disk
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

                // read and write until last byte is encountered
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, currentByte);
                }
                dest.flush();
                dest.close();
                is.close();
            }

            if (currentEntry.endsWith(StaticAccess.DOT_EXTENSION)) {
                // found a zip file, try to open
                unZip(destFile.getAbsolutePath());
            }
        }
    }

    //Copying images for sharing
    public void copyForShare(String inputFile, String inputPath, String outputPath) {
        // String appDataPath = Environment.getExternalStorageDirectory() + "/Android/Data/" + context.getPackageName();
        if (inputFile.length() > 0) {
            InputStream in = null;
            OutputStream out = null;
            try {

                //create output directory if it doesn't exist
                File dir = new File(outputPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                in = new FileInputStream(inputPath + inputFile);
                out = new FileOutputStream(outputPath + inputFile);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;

                // write the output file (You have now copied the file)
                out.flush();
                out.close();
                out = null;

            } catch (FileNotFoundException fnfe1) {
                Log.e("tag", fnfe1.getMessage());
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
        }
    }

    // Deleting after sending/receiving
    public void deleteGeneratedFolder() {
     /*   File dir = new File(zipPath);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }*/

        deleteFile(generatedPathDel);
    }

    // Generate JSON for Share taskpack from Database
    public void generateTaskPackJSON(ArrayList<TaskPack> taskPacks) throws JSONException {

        JSONArray mainArray = new JSONArray();
        JSONArray itemArray;

        JSONObject itemObject;
        JSONObject taskObject;
        JSONObject taskPackObject;
        JSONObject finalJsonObject = new JSONObject();

        for (TaskPack taskPack : taskPacks) {
            JSONArray taskArray = new JSONArray();
            ArrayList<Task> Tasks = (ArrayList<Task>) databaseManager.listTasksByTAskPackId(taskPack.getId());
            for (Task task : Tasks) {
                final LinkedHashMap<Long, Item> items = databaseManager.loadTaskWiseItem(task);
                itemArray = new JSONArray();
                if (items != null) {
                    for (Map.Entry<Long, Item> itemValue : items.entrySet()) {
                        Item item = itemValue.getValue();

                        itemObject = new JSONObject();
                        itemObject.put(ItemDao.Properties.X.columnName, item.getX());
                        itemObject.put(ItemDao.Properties.Y.columnName, item.getY());
                        itemObject.put(ItemDao.Properties.Rotation.columnName, item.getRotation());
                        itemObject.put(ItemDao.Properties.Key.columnName, item.getKey());
                        itemObject.put(ItemDao.Properties.IsCircleView.columnName, item.getIsCircleView());
                        itemObject.put(ItemDao.Properties.CircleColor.columnName, item.getCircleColor());
                        itemObject.put(ItemDao.Properties.UserText.columnName, item.getUserText());
                        itemObject.put(ItemDao.Properties.TextColor.columnName, item.getTextColor());
                        itemObject.put(ItemDao.Properties.TextSize.columnName, item.getTextSize());
                        itemObject.put(ItemDao.Properties.BorderColor.columnName, item.getBorderColor());
                        itemObject.put(ItemDao.Properties.BackgroundColor.columnName, item.getBackgroundColor());
                        itemObject.put(ItemDao.Properties.Drawable.columnName, item.getDrawable());
                        itemObject.put(ItemDao.Properties.Width.columnName, item.getWidth());
                        itemObject.put(ItemDao.Properties.Height.columnName, item.getHeight());
                        itemObject.put(ItemDao.Properties.Left.columnName, item.getLeft());
                        itemObject.put(ItemDao.Properties.Right.columnName, item.getRight());
                        itemObject.put(ItemDao.Properties.Top.columnName, item.getTop());
                        itemObject.put(ItemDao.Properties.Bottom.columnName, item.getBottom());
                        itemObject.put(ItemDao.Properties.ImagePath.columnName, item.getImagePath());
                        itemObject.put(ItemDao.Properties.Type.columnName, item.getType());/*
                        itemObject.put(ItemDao.Properties.CreatedAt.columnName, item.getCreatedAt() == null ? new Date() : item.getCreatedAt());
                        itemObject.put(ItemDao.Properties.UpdatedAt.columnName, item.getUpdatedAt() == null ? new Date() : item.getUpdatedAt());*/
                        itemObject.put(ItemDao.Properties.OpenApp.columnName, item.getOpenApp());
                        itemObject.put(ItemDao.Properties.Result.columnName, item.getResult());
                        itemObject.put(ItemDao.Properties.AllowDragDrop.columnName, item.getAllowDragDrop());
                        itemObject.put(ItemDao.Properties.DragDropTarget.columnName, item.getDragDropTarget());
                        itemObject.put(ItemDao.Properties.CornerRound.columnName, item.getCornerRound());
                        itemObject.put(ItemDao.Properties.NavigateTo.columnName, item.getNavigateTo());
                        itemObject.put(ItemDao.Properties.ShowedBy.columnName, item.getShowedBy());
                        itemObject.put(ItemDao.Properties.HideBy.columnName, item.getHideBy());
                        itemObject.put(ItemDao.Properties.CloseApp.columnName, item.getCloseApp());
                        itemObject.put(ItemDao.Properties.ItemSound.columnName, item.getItemSound());
                        itemObject.put(ItemDao.Properties.OpenUrl.columnName, item.getOpenUrl());
                        itemObject.put(ItemDao.Properties.FontTypeFace.columnName, item.getFontTypeFace());
                        itemObject.put(ItemDao.Properties.FontAlign.columnName, item.getFontAlign());
                        itemObject.put(ItemDao.Properties.AutoPlay.columnName, item.getAutoPlay());
                        itemObject.put(ItemDao.Properties.SoundDelay.columnName, item.getSoundDelay());
                        itemObject.put(ItemDao.Properties.BorderPixel.columnName, item.getBorderPixel());
                        itemObject.put(ItemDao.Properties.ShowedByTarget.columnName, item.getShowedByTarget());
                        itemObject.put(ItemDao.Properties.HiddenByTarget.columnName, item.getHiddenByTarget());
                        itemObject.put(ItemDao.Properties.ReadText.columnName,item.getReadText());
                        itemObject.put(ItemDao.Properties.WriteText.columnName,item.getWriteText());

                        itemArray.put(itemObject);

                        copyForShare(item.getImagePath(), appImagePath, shareImagePath);
                        //added by  reaz for item sound
                        copyForShare(item.getItemSound(), appSoundPath, shareSoundPath);
                    }
                }

                taskObject = new JSONObject();
                taskObject.put(TaskDao.Properties.UniqId.columnName, task.getUniqId());
                taskObject.put(TaskDao.Properties.Name.columnName, task.getName());
                taskObject.put(TaskDao.Properties.TaskImage.columnName, task.getTaskImage());
                taskObject.put(TaskDao.Properties.Type.columnName, task.getType());
                taskObject.put(TaskDao.Properties.BackgroundColor.columnName, task.getBackgroundColor());
                taskObject.put(TaskDao.Properties.Active.columnName, task.getActive());
                taskObject.put(TaskDao.Properties.CreatedAt.columnName, task.getCreatedAt());
                taskObject.put(TaskDao.Properties.UpdatedAt.columnName, task.getUpdatedAt());
                taskObject.put(TaskDao.Properties.UserId.columnName, task.getUserId());
                taskObject.put(TaskDao.Properties.SlideSequence.columnName, task.getSlideSequence());
                taskObject.put(TaskDao.Properties.FeedbackImage.columnName, task.getFeedbackImage());
                taskObject.put(TaskDao.Properties.FeedbackAnimation.columnName, task.getFeedbackAnimation());
                taskObject.put(TaskDao.Properties.PositiveAnimation.columnName, task.getPositiveAnimation());
                taskObject.put(TaskDao.Properties.NegativeAnimation.columnName, task.getNegativeAnimation());
                taskObject.put(TaskDao.Properties.FeedbackSound.columnName, task.getFeedbackSound());
                taskObject.put(TaskDao.Properties.PositiveSound.columnName, task.getPositiveSound());
                taskObject.put(TaskDao.Properties.NegativeSound.columnName, task.getNegativeSound());

                taskObject.put(TaskDao.Properties.FeedbackType.columnName, task.getFeedbackType());

                taskObject.put(TaskDao.Properties.ErrorBgColor.columnName, task.getErrorBgColor());
                taskObject.put(TaskDao.Properties.Errortext.columnName, task.getErrortext());
                taskObject.put(TaskDao.Properties.ErrorImage.columnName, task.getErrorImage());
                taskObject.put(TaskDao.Properties.ErrorMandatoryScreen.columnName, task.getErrorMandatoryScreen());// new
                taskObject.put(TaskDao.Properties.SequenceText.columnName, task.getSequenceText());// new
                taskObject.put(ItemDao.TABLENAME, itemArray);
                taskArray.put(taskObject);

                // Copying related resources to share folder
                copyForShare(task.getTaskImage(), appImagePath, shareImagePath);
                copyForShare(task.getFeedbackImage(), appImagePath, shareImagePath);
                // Copying related resources to share folder for error image
                copyForShare(task.getErrorImage(), appImagePath, shareImagePath);

                copyForShare(task.getFeedbackSound(), appSoundPath, shareSoundPath);
                copyForShare(task.getPositiveSound(), appSoundPath, shareSoundPath);
                copyForShare(task.getNegativeSound(), appSoundPath, shareSoundPath);

            }

            taskPackObject = new JSONObject();
            taskPackObject.put(TaskPackDao.Properties.Name.columnName, taskPack.getName());
            taskPackObject.put(TaskPackDao.Properties.Level.columnName, taskPack.getLevel());
            taskPackObject.put(TaskPackDao.Properties.FirstLayerTaskID.columnName, taskPack.getFirstLayerTaskID());
            taskPackObject.put(TaskPackDao.Properties.CreatedAt.columnName, taskPack.getCreatedAt());
            taskPackObject.put(TaskPackDao.Properties.TouchAnimation.columnName, taskPack.getTouchAnimation());
            taskPackObject.put(TaskPackDao.Properties.ItemOfAnimation.columnName, taskPack.getItemOfAnimation());
            taskPackObject.put(TaskPackDao.Properties.State.columnName, taskPack.getState());
            taskPackObject.put(TaskPackDao.Properties.Type.columnName, taskPack.getType());

            taskPackObject.put(TaskDao.TABLENAME, taskArray);
            mainArray.put(taskPackObject);
        }

        finalJsonObject.put(TaskPackDao.TABLENAME, mainArray);

        // Save JSON to SD card
        try {
            File sdCardDirectory = new File(shareJSONPath);
            if (!sdCardDirectory.exists()) {
                sdCardDirectory.mkdirs();
            }


            File jsonFile = new File(sdCardDirectory, jsonFileName);
            FileWriter writer = new FileWriter(jsonFile);
            writer.append(finalJsonObject.toString());
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    // Read taskpack JSON from SD to save in Database
    public void readSharedTaskPackJSONtoDatabase() {
        try {
            File file = new File(receivedJSONPath, jsonFileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            String jsonStr = null;
            try {
                FileChannel fileChannel = fileInputStream.getChannel();
                MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
                jsonStr = Charset.defaultCharset().decode(mappedByteBuffer).toString();
            } finally {
                fileInputStream.close();
            }

            JSONObject jsonObj = new JSONObject(jsonStr);
            DateFormat dateFormat = new SimpleDateFormat("EEE MMM d hh:mm:ss z yyyy");


            // Getting Taskpacks
            JSONArray taskPackArray = jsonObj.getJSONArray(TaskPackDao.TABLENAME);
            for (int h = 0; h < taskPackArray.length(); h++) {
                JSONObject taskPackObject = taskPackArray.getJSONObject(h);
                {
                    TaskPack taskPack = new TaskPack();
                    taskPack.setName(taskPackObject.getString(TaskPackDao.Properties.Name.columnName));
                    taskPack.setLevel(taskPackObject.getString(TaskPackDao.Properties.Level.columnName));
                    taskPack.setFirstLayerTaskID(taskPackObject.getInt(TaskPackDao.Properties.FirstLayerTaskID.columnName));
                    taskPack.setTouchAnimation(taskPackObject.getInt(TaskPackDao.Properties.TouchAnimation.columnName));
                    taskPack.setItemOfAnimation(taskPackObject.getInt(TaskPackDao.Properties.ItemOfAnimation.columnName));
                    taskPack.setType(taskPackObject.getString(TaskPackDao.Properties.Type.columnName));
                    taskPack.setState(false);
                    //taskPack.setCreatedAt((Date)dateFormat.parse(taskPackObject.getString(TaskPackDao.Properties.CreatedAt.columnName)));
                    Long taskPackId = databaseManager.insertTaskPack(taskPack);


                    // Getting Tasks
                    JSONArray taskArray = taskPackObject.getJSONArray(TaskDao.TABLENAME);
                    for (int i = 0; i < taskArray.length(); i++) {
                        JSONObject taskObject = taskArray.getJSONObject(i);


                        // Copying Task Image
                        copyForShare(taskObject.getString(TaskDao.Properties.TaskImage.columnName), receivedImagePath, appImagePath);
                        copyForShare(taskObject.getString(TaskDao.Properties.FeedbackImage.columnName), receivedImagePath, appImagePath);
                        copyForShare(taskObject.getString(TaskDao.Properties.ErrorImage.columnName), receivedImagePath, appImagePath);
                        // Copying Task Sound
                        copyForShare(taskObject.getString(TaskDao.Properties.FeedbackSound.columnName), receivedSoundPath, appSoundPath);
                        copyForShare(taskObject.getString(TaskDao.Properties.PositiveSound.columnName), receivedSoundPath, appSoundPath);
                        copyForShare(taskObject.getString(TaskDao.Properties.NegativeSound.columnName), receivedSoundPath, appSoundPath);

//                        copyForShare(taskObject.getString(TaskDao.Properties.ErrorImage.columnName), receivedSoundPath, appSoundPath);

                        Task task = new Task();
                        //Foreign key of taskpackId
                        task.setTaskPackId(taskPackId);
                        task.setUniqId(taskObject.getLong(TaskDao.Properties.UniqId.columnName));
                        task.setName(taskObject.getString(TaskDao.Properties.Name.columnName));
                        task.setTaskImage(taskObject.getString(TaskDao.Properties.TaskImage.columnName));
                        task.setType(taskObject.getString(TaskDao.Properties.Type.columnName));
                        task.setBackgroundColor(Integer.valueOf(taskObject.getString(TaskDao.Properties.BackgroundColor.columnName)));
                        task.setActive(Boolean.valueOf(taskObject.getString(TaskDao.Properties.Active.columnName)));

//                        task.setCreatedAt((Date) dateFormat.parse(taskObject.getString(TaskDao.Properties.CreatedAt.columnName)));
//                        task.setUpdatedAt((Date) dateFormat.parse(taskObject.getString(TaskDao.Properties.UpdatedAt.columnName)));

                        task.setUserId(Long.valueOf(taskObject.getString(TaskDao.Properties.UserId.columnName)));
                        task.setSlideSequence(Integer.valueOf(taskObject.getString(TaskDao.Properties.SlideSequence.columnName)));

                        task.setFeedbackImage(taskObject.getString(TaskDao.Properties.FeedbackImage.columnName));
                        task.setFeedbackAnimation(Integer.valueOf(taskObject.getString(TaskDao.Properties.FeedbackAnimation.columnName)));
                        task.setPositiveAnimation(Integer.valueOf(taskObject.getString(TaskDao.Properties.PositiveAnimation.columnName)));
                        task.setNegativeAnimation(Integer.valueOf(taskObject.getString(TaskDao.Properties.NegativeAnimation.columnName)));
                        task.setFeedbackSound(taskObject.getString(TaskDao.Properties.FeedbackSound.columnName));
                        task.setPositiveSound(taskObject.getString(TaskDao.Properties.PositiveSound.columnName));
                        task.setNegativeSound(taskObject.getString(TaskDao.Properties.NegativeSound.columnName));
                        task.setErrorBgColor(taskObject.getInt(TaskDao.Properties.ErrorBgColor.columnName));
                        task.setErrortext(taskObject.getString(TaskDao.Properties.Errortext.columnName));
                        task.setErrorImage(taskObject.getString(TaskDao.Properties.ErrorImage.columnName));
                        task.setErrorMandatoryScreen(taskObject.getInt(TaskDao.Properties.ErrorMandatoryScreen.columnName));
                        task.setFeedbackType(Integer.valueOf(taskObject.getString(TaskDao.Properties.FeedbackType.columnName)));
                        task.setSequenceText(taskObject.getString(TaskDao.Properties.SequenceText.columnName));
                        Long taskId = databaseManager.insertTask(task);

                        // Getting Items
                        JSONArray itemArray = taskObject.getJSONArray(ItemDao.TABLENAME);
                        for (int j = 0; j < itemArray.length(); j++) {
                            JSONObject itemObject = itemArray.getJSONObject(j);

                            // Copying Item Images
                            if (itemObject.getString(ItemDao.Properties.ImagePath.columnName).length() > 0) {
                                copyForShare(itemObject.getString(ItemDao.Properties.ImagePath.columnName), receivedImagePath, appImagePath);
                            }

                            // Copying Item Sound
                            if (itemObject.getString(ItemDao.Properties.ItemSound.columnName).length() > 0) {
                                copyForShare(itemObject.getString(ItemDao.Properties.ItemSound.columnName), receivedSoundPath, appSoundPath);
                            }

                            Item item = new Item();
                            // Foreign key of taskid
                            item.setTask(taskId);

                            item.setX(Float.valueOf(itemObject.getString(ItemDao.Properties.X.columnName)));
                            item.setY(Float.valueOf(itemObject.getString(ItemDao.Properties.Y.columnName)));
                            item.setRotation(Integer.valueOf(itemObject.getString(ItemDao.Properties.Rotation.columnName)));
                            item.setKey(Long.valueOf(itemObject.getString(ItemDao.Properties.Key.columnName)));
                            item.setIsCircleView(Integer.valueOf(itemObject.getString(ItemDao.Properties.IsCircleView.columnName)));
                            item.setCircleColor(Integer.valueOf(itemObject.getString(ItemDao.Properties.CircleColor.columnName)));
                            item.setUserText(itemObject.getString(ItemDao.Properties.UserText.columnName) == null ? "" : itemObject.getString(ItemDao.Properties.UserText.columnName));
                            item.setTextColor(Integer.valueOf(itemObject.getString(ItemDao.Properties.TextColor.columnName)));
                            item.setTextSize(Integer.valueOf(itemObject.getString(ItemDao.Properties.TextSize.columnName)));
                            item.setBorderColor(Integer.valueOf(itemObject.getString(ItemDao.Properties.BorderColor.columnName)));
                            item.setBackgroundColor(Integer.valueOf(itemObject.getString(ItemDao.Properties.BackgroundColor.columnName)));
                            item.setDrawable(Integer.valueOf(itemObject.getString(ItemDao.Properties.Drawable.columnName)));
                            item.setWidth(Float.valueOf(itemObject.getString(ItemDao.Properties.Width.columnName)));
                            item.setHeight(Float.valueOf(itemObject.getString(ItemDao.Properties.Height.columnName)));
                            item.setLeft(Float.valueOf(itemObject.getString(ItemDao.Properties.Left.columnName)));
                            item.setRight(Float.valueOf(itemObject.getString(ItemDao.Properties.Right.columnName)));
                            item.setTop(Float.valueOf(itemObject.getString(ItemDao.Properties.Top.columnName)));
                            item.setBottom(Float.valueOf(itemObject.getString(ItemDao.Properties.Bottom.columnName)));
                            item.setImagePath(itemObject.getString(ItemDao.Properties.ImagePath.columnName));
                            item.setType(itemObject.getString(ItemDao.Properties.Type.columnName));

                            // item.setCreatedAt((Date) dateFormat.parse(itemObject.getString(ItemDao.Properties.CreatedAt.columnName)));
                            //item.setUpdatedAt((Date) dateFormat.parse(itemObject.getString(ItemDao.Properties.UpdatedAt.columnName)));
                            item.setResult(itemObject.getString(ItemDao.Properties.Result.columnName) == null ? "" : itemObject.getString(ItemDao.Properties.Result.columnName));

                            item.setOpenApp(itemObject.getString(ItemDao.Properties.OpenApp.columnName) == null ? "" : itemObject.getString(ItemDao.Properties.OpenApp.columnName));
                            item.setAllowDragDrop(itemObject.getInt(ItemDao.Properties.AllowDragDrop.columnName));
                            item.setDragDropTarget(itemObject.getLong(ItemDao.Properties.DragDropTarget.columnName));
                            item.setCornerRound(itemObject.getInt(ItemDao.Properties.CornerRound.columnName));
                            item.setNavigateTo(itemObject.getLong(ItemDao.Properties.NavigateTo.columnName));
                            item.setShowedBy(itemObject.getLong(ItemDao.Properties.ShowedBy.columnName));
                            item.setHideBy(itemObject.getLong(ItemDao.Properties.HideBy.columnName));
                            item.setCloseApp(itemObject.getInt(ItemDao.Properties.CloseApp.columnName));
                            //item.setAllowAlign(itemObject.getInt(ItemDao.Properties.AllowAlign.columnName));
                            //item.setAlignTarget(itemObject.getLong(ItemDao.Properties.AlignTarget.columnName));
                            //item.setAlignType(itemObject.getInt(ItemDao.Properties.AlignType.columnName));
                            // added by reaz
                            item.setItemSound(itemObject.getString(ItemDao.Properties.ItemSound.columnName) == null ? "" : itemObject.getString(ItemDao.Properties.ItemSound.columnName));
                            item.setOpenUrl(itemObject.getString(ItemDao.Properties.OpenUrl.columnName) == null ? "" : itemObject.getString(ItemDao.Properties.OpenUrl.columnName));
                            item.setFontTypeFace(itemObject.getInt(ItemDao.Properties.FontTypeFace.columnName));
                            item.setFontAlign(itemObject.getInt(ItemDao.Properties.FontAlign.columnName));
                            item.setAutoPlay(itemObject.getInt(ItemDao.Properties.AutoPlay.columnName));
                            item.setSoundDelay(itemObject.getInt(ItemDao.Properties.SoundDelay.columnName));
                            item.setBorderPixel(itemObject.getInt(ItemDao.Properties.BorderPixel.columnName));
                            item.setShowedByTarget(itemObject.getString(ItemDao.Properties.ShowedByTarget.columnName));
                            item.setHiddenByTarget(itemObject.getString(ItemDao.Properties.HiddenByTarget.columnName));
                            item.setReadText(itemObject.getString(ItemDao.Properties.ReadText.columnName));
                            item.setWriteText(itemObject.getString(ItemDao.Properties.WriteText.columnName));

                            databaseManager.insertItem(item);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Deleting after receiving
    public void deleteReceivedFolder() {
        File dir = new File(receivedMainPath);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }


    // file delete function
    public void  deleteFile(String  path) {
        // First try the normal deletion
        File file = new File(path);
        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
            } catch (IOException e) {
            }
        }
    }
}
