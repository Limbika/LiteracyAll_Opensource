package com.literacyall.app.player;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dmk.limbikasdk.pojo.LimbikaViewItemValue;
import com.dmk.limbikasdk.views.LimbikaView;
import com.literacyall.app.R;
import com.literacyall.app.activities.PlayerActivity;
import com.literacyall.app.activities.TaskPackActivity;
import com.literacyall.app.dao.Item;
import com.literacyall.app.dao.Task;
import com.literacyall.app.utilities.StaticAccess;
import com.literacyall.app.utilities.TaskType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by RAFI on 11/14/2016.
 */

public class ReadMode implements RecognitionListener {
    Context context;
    PlayerActivity activity;
    Task task;
    public boolean isListening = false;
    public String recognizedText = null;
    private String LOG_TAG = "VoiceRecognition";
    Item item = null;
    Item item1 = null;
    LimbikaView limbiakview1;
    LimbikaViewItemValue limbikaViewItemValue = null;
    String savedText = null;
    public Intent recognizerIntent;
    public SpeechRecognizer speech = null;

    public ReadMode(Context context, Task task) {
        this.context = context;
        this.task = task;
        activity = (PlayerActivity) context;

        String lang = "en";
        if (Locale.getDefault().equals("es"))
            lang = "es";

        speech = SpeechRecognizer.createSpeechRecognizer(context);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, lang);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

    }


    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        //Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
        doneListening();
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);


        if (matches != null && matches.size() > 0) {
            recognizedText = matches.get(0);
            doAfterSpeechRecognition(item, item1, limbiakview1, savedText, limbikaViewItemValue);
            doneListening();

        } else {
            Toast.makeText(activity, activity.getResources().getString(R.string.tryAgain), Toast.LENGTH_SHORT).show();
            //getResources().getString(R.string.tryAgain)
        }
        /*for (String result : matches)
            text += result + "\n";*/

    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.i(LOG_TAG, "onEvent");
    }

    public void doAfterSpeechRecognition(Item item, Item item1, LimbikaView v, String savedtext, LimbikaViewItemValue limbikaViewItemValue) {
        LimbikaView limbikaView = (LimbikaView) v;
        // Toast.makeText(activity, activity.getResources().getString(R.string.DoneRead) + recognizedText + activity.getResources().getString(R.string.savetext) + savedtext, Toast.LENGTH_SHORT).show();
        if (recognizedText != null)
            Log.d("read", "read savedtext" + savedtext);
        Log.d("read", "read" + recognizedText);
        if (savedtext.equalsIgnoreCase(recognizedText)) {

            if (item1.getResult().equals(TaskType.NORMAL_TRUE) &&
                    ifInthePositiveListGenearal(item1.getKey())) {
                //task.getNegative sound no need this  playSoundOnlyGeneral Methods that's why we send Blank String
                //all the sound and feed back related works are done here so that everything happens simultaneously

                limbikaView.showPositiveTag();
                activity.positiveResult.remove(limbikaViewItemValue.getKey());

                if (task.getPositiveAnimation() > 0) {
                    activity.showAnimation(limbikaView, task.getPositiveAnimation());
                }
                playSoundOnlyGeneral(item1.getItemSound(), task.getPositiveSound(), "", task, item1);

                Log.d("here", "This is the thing");

                //positive end////
                //get showed by list and show them
                if (item1.getShowedByTarget() != null && !item1.getShowedByTarget().equals("")) {
                    String str = item1.getShowedByTarget();
                    ArrayList<Long> list = activity.formatList(str);
                    activity.showItemsFromShowedby(list);
                }
                //get hidden by list and show them
                if (item1.getHiddenByTarget() != null && !item1.getHiddenByTarget().equals("")) {
                    String str = item1.getHiddenByTarget();
                    ArrayList<Long> list = activity.formatList(str);
                    activity.hideItemsFromShowedby(list);
                }

            } else if (item1.getResult().equals(TaskType.NORMAL_FALSE)) {
                if (task.getNegativeAnimation() > 0) {
                    activity.showAnimation(limbikaView, task.getNegativeAnimation());

                }
                playSoundOnlyGeneral(item.getItemSound(), "", task.getNegativeSound(), task, item1);
                //playNegSoundGeneral(task, task.getNegativeSound());

                //LimbikaView limbikaView = (LimbikaView) v;
                //limbikaView.showNegativeTag();

            } else {
                //task.getNegative sound no need this  playSoundOnlyGeneral Methods that's why we send Blank String
                //all the sound and feed back related works are done here so that everything happens simultaneously
                playSoundOnlyGeneral(item.getItemSound(), "", "", task, item1);
            }
        }
        //DONOT LET THE USERS USE POSITIVE NEGATIVE AND THE BELOW FUNCTIONS TOGETHER THIS WILL CREATE MANICE
        if (item.getCloseApp() == 1) {
            Intent intent = new Intent(activity, TaskPackActivity.class);
            intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, activity.taskPackId);
            context.startActivity(intent);
            activity.mpSoundRelease();
            activity.finish();
        }
        if (item.getOpenUrl().length() > 0 && item.getOpenApp() != null) {
            limbikaView.setDraggable(false);
            activity.openWebLink(item.getOpenUrl());
        }
        // single tap open 3rd party app added by reaz
        if (item.getOpenApp().length() > 0 && item.getOpenApp() != null) {
            activity.mpSoundRelease();
            activity.openApp(item.getOpenApp());
        }
        if (item.getNavigateTo() > 0) {
            limbikaView.setDraggable(false);
            activity.positiveResult.clear();
            activity.gotoSpecificTask(item.getNavigateTo());
        }
        activity.isBlockUser = false;

        activity.doneListening();
    }

    //general mode work:: checking the view is in positiveAnswer list
    boolean ifInthePositiveListGenearal(long key) {
        boolean res = false;
        for (long k : activity.positiveResult) {
            if (k == key)
                res = true;
        }
        Log.e(String.valueOf(res), "here");
        return res;
    }

    void playSoundOnlyGeneral(final String itemsound, final String posSound, final String negSound, final Task task, final Item item) {
        //when item sound is available
        if (itemsound.length() > 0 && itemsound != null) {
            activity.mpSoundRelease();
            activity.mp = new MediaPlayer();
            try {
                activity.mp.setDataSource(Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + activity.getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_SOUND + itemsound);

                activity.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        activity.mpSoundRelease();
                        mp = new MediaPlayer();

                        String playString = "";

                        if (posSound != null && !posSound.equals(""))
                            playString = posSound;
                        else
                            playString = negSound;
                        //if itemsound is only available no pos or neg sound
                        if (playString.equals("") || playString == null) {

                            if (activity.positiveResult.size() == 0) {
                                if (task.getFeedbackImage().length() > 0) {
                                    activity.delayfeedBack(task.getFeedbackImage(), task.getFeedbackAnimation(), activity.currentTaskIndex, task.getFeedbackSound(), task.getFeedbackType());
                                } else {
                                    if (item.getResult().equals(TaskType.NORMAL_TRUE))
                                        activity.delayGotoNextTask(activity.currentTaskIndex);
                                }
                            }


                        } else
                            try {
                                //Positive or negative sound available after item sound
                                mp.setDataSource(Environment.getExternalStorageDirectory() +
                                        StaticAccess.ANDROID_DATA + activity.getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_SOUND + playString);
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        if (posSound.equals("") || posSound == null) {
                                            if (activity.positiveResult.size() == 0) {
                                                if (task.getFeedbackImage().length() > 0) {
                                                    activity.delayfeedBack(task.getFeedbackImage(), task.getFeedbackAnimation(), activity.currentTaskIndex, task.getFeedbackSound(), task.getFeedbackType());
                                                } else {
                                                    if (item.getResult().equals(TaskType.NORMAL_TRUE))
                                                        activity.delayGotoNextTask(activity.currentTaskIndex);
                                                }
                                            }
                                        } else {
                                            //for negative
                                            //if mandatory is true then send it to uper error else do nothing
                                            if (activity.madatoryError == 1)
                                                activity.errorListner.onErrorListner(task, StaticAccess.TAG_TASK_GENERAL_MODE);
                                        }
                                    }
                                });
                                mp.prepare();

                             /*do not play negative sound if mandatory supererror is on.
                                 then the sound will be played on Super activity */
                                if (activity.madatoryError == 1 && negSound != null && !negSound.equals("")) {
                                    activity.errorListner.onErrorListner(task, StaticAccess.TAG_TASK_GENERAL_MODE);
                                } else
                                    mp.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                });

                activity.mp.prepare();
                activity.mp.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //no item sound and no Positive or negetive sound
        else if (itemsound.equals("") && posSound.equals("") && negSound.equals("")) {
            if (activity.positiveResult.size() == 0) {
                if (task.getFeedbackImage().length() > 0) {
                    activity.delayfeedBack(task.getFeedbackImage(), task.getFeedbackAnimation(), activity.currentTaskIndex, task.getFeedbackSound(), task.getFeedbackType());
                } else {
                    if (item.getResult().equals(TaskType.NORMAL_TRUE))
                        activity.delayGotoNextTask(activity.currentTaskIndex);
                }
            }
        }
        //no item sound but positive or negative sound is available
        else {
            activity.mpSoundRelease();
            activity.mp = new MediaPlayer();

            String playString = "";

            if (posSound != null && !posSound.equals(""))
                playString = posSound;
            else
                playString = negSound;

            try {

                activity.mp.setDataSource(Environment.getExternalStorageDirectory() +
                        StaticAccess.ANDROID_DATA + activity.getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_SOUND + playString);
                activity.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (!posSound.equals("") || posSound != null) {
                            if (activity.positiveResult.size() == 0) {
                                if (task.getFeedbackImage().length() > 0) {
                                    activity.delayfeedBack(task.getFeedbackImage(), task.getFeedbackAnimation(), activity.currentTaskIndex, task.getFeedbackSound(), task.getFeedbackType());
                                } else {
                                    if (item.getResult().equals(TaskType.NORMAL_TRUE))
                                        activity.delayGotoNextTask(activity.currentTaskIndex);
                                }
                            }
                        } else {
                            //for negative
                            //if mandatory is true then send it to uper error else do nothing
                            /*if (madatoryError == 1)
                                errorListner.onErrorListner(task, StaticAccess.TAG_TASK_GENERAL_MODE);*/
                        }
                    }
                });
                activity.mp.prepare();
                /*do not play negative sound if mandatory supererror is on
                                 then the sound will be played on activity */
                if (activity.madatoryError == 1 && negSound != null && !negSound.equals("")) {
                    activity.errorListner.onErrorListner(task, StaticAccess.TAG_TASK_GENERAL_MODE);
                } else
                    activity.mp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public String getErrorText(int errorCode) {
        String message;
        doneListening();
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    public void doneListening() {

        this.item = null;
        this.item1 = null;
        limbiakview1 = null;
        savedText = null;
        this.limbikaViewItemValue = null;
        isListening = false;
        activity.ibtnMicroPhone.setVisibility(View.GONE);
    }

    public void startListening(Item item, Item item1, LimbikaView v, String savedtext,
                               LimbikaViewItemValue limbikaViewItemValue) {

        this.item = item;
        this.item1 = item1;
        limbiakview1 = v;
        savedText = savedtext;
        this.limbikaViewItemValue = limbikaViewItemValue;

        activity.ibtnMicroPhone.setVisibility(View.VISIBLE);
        speech.startListening(recognizerIntent);
    }

    public void readModeGeneral(Item item, Item item1, View v,
                                LimbikaViewItemValue limbikaViewItemValue) {
        //there should be no false here only true
        LimbikaView limbikaView = (LimbikaView) v;
        if (item1.getResult().length() > 0 && item1.getResult() != null) {
            if (!isListening) {
                if (item1.getResult().equals(TaskType.NORMAL_TRUE) &&
                        ifInthePositiveListGenearal(item1.getKey())) {
                    String savedtext = item1.getReadText();
                    startListening(item, item1, limbikaView, savedtext, limbikaViewItemValue);
                    isListening = true;
                }
            }
        } else {
            playSoundOnlyGeneral(item.getItemSound(), "", "", task, item1);
        }


        if (!isListening) {
            //DONOT LET THE USERS USE POSITIVE NEGATIVE AND THE BELOW FUNCTIONS TOGETHER THIS WILL CREATE MANICE
            //get showed by list and show them
            if (item1.getShowedByTarget() != null && !item1.getShowedByTarget().equals("")) {
                String str = item1.getShowedByTarget();
                ArrayList<Long> list = activity.formatList(str);
                activity.showItemsFromShowedby(list);
            }
            //get hidden by list and show them
            if (item1.getHiddenByTarget() != null && !item1.getHiddenByTarget().equals("")) {
                String str = item1.getHiddenByTarget();
                ArrayList<Long> list = activity.formatList(str);
                activity.hideItemsFromShowedby(list);
            }


            if (item.getCloseApp() == 1) {
                Intent intent = new Intent(activity, TaskPackActivity.class);
                intent.putExtra(StaticAccess.INTENT_TASK_PACK_ID, activity.taskPackId);
                context.startActivity(intent);
                activity.mpSoundRelease();
                activity.finish();
            }
            if (item.getOpenUrl().length() > 0 && item.getOpenApp() != null) {
                limbikaView.setDraggable(false);
                activity.openWebLink(item.getOpenUrl());
            }
            // single tap open 3rd party app added by reaz
            if (item.getOpenApp().length() > 0 && item.getOpenApp() != null) {
                activity.mpSoundRelease();
                activity.openApp(item.getOpenApp());
            }
            if (item.getNavigateTo() > 0) {
                limbikaView.setDraggable(false);
                activity.positiveResult.clear();
                activity.gotoSpecificTask(item.getNavigateTo());
            }
            activity.isBlockUser = false;
        }
    }

    public void speechOnPause() {
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }

    }
}
