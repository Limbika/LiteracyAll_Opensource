package com.literacyall.app.utilities;

/**
 * Created by DENNOH on 5/13/2016.
 */
public class StaticAccess {

    //For Alignment Static value created by Reaz
    public static final int ALIGN_PARENT_LEFT = 0x1;
    public static final int ALIGN_PARENT_RIGHT = 0x2;
    public static final int ALIGN_PARENT_TOP = 0x3;
    public static final int ALIGN_PARENT_BOTTOM = 0x4;
    public static final int ALIGN_PARENT_CENTER = 0x5;
    public static final int ALIGN_PARENT_CENTER_X = 0x6;
    public static final int ALIGN_PARENT_CENTER_Y = 0x7;
    public static final int ALIGN_SCREEN_CENTER_X = 0x8;
    public static final int ALIGN_SCREEN_CENTER_Y = 0x9;

    public static final int ALIGN_SCREEN_TOP = 0x10;
    public static final int ALIGN_SCREEN_LEFT = 0x11;
    public static final int ALIGN_SCREEN_RIGHT = 0x12;
    public static final int ALIGN_SCREEN_BOTTOM = 0x13;
    public static final int ALIGN_SCREEN_CENTER = 0x14;


    //For Sound Static value created by Reaz
    public static final int TAG_SOUND_NEGATIVE = 0x1;
    public static final int TAG_SOUND_POSITIVE = 0x2;
    public static final int TAG_SOUND_FEEDBACK = 0x3;
    public static final int TAG_SOUND_VIDEO = 0x4;
    //For Sound Static value created by Reaz
    public static final int TAG_ITEM_SOUND = 0x5;

    //for File Size Static Value Created bt Reaz
    public static final int TAG_SOUND_FILE_SIZE = 3000;

    //For Anim Static value  created by Reaz
    public static final int TAG_ANIM_NEGATIVE = 0x1;
    public static final int TAG_ANIM_POSITIVE = 0x2;
    public static final int TAG_ANIM_FEEDBACK = 0x3;
    public static final int SELECT_PICTURE                                   = 0x1;



    //For Repositioning Item sequence
    public static final int SEND_TO_BACK = 0x1;
    public static final int SEND_TO_FRONT = 0x2;
    public static final int SEND_TO_BACK_MOST = 0x3;
    public static final int SEND_TO_TOP_MOST = 0x4;


    //For Font Static value  created by Reaz
    public static final int TAG_TYPE_NORMAL = 0;
    public static final int TAG_TYPE_FACE_SEGOE_SCRIPT = 1;
    public static final int TAG_TYPE_FACE_DANCING = 2;
    public static final int TAG_TYPE_FACE_ROBOTO_THIN = 3;
    public static final int TAG_TYPE_FACE_ROAD_BRUSH = 4;
    public static final int TAG_TYPE_FACE_ROBOTO_CONDENSED = 5;
    public static final int TAG_TYPE_FACE_RANCHO = 6;

    public static final String FONT_ROBOTO = "font/roboto.ttf";
    public static final String FONT_DANCING = "font/dancing_script.ttf";
    public static final String FONT_ROBOTO_THIN = "font/roboto_thin.ttf";
    public static final String FONT_ROAD_BRUSH = "font/roadbrush.ttf";
    public static final String FONT_ROBOTO_CONDENSED = "font/roboto_condensed.ttf";
    public static final String FONT_RANCHO = "font/rancho3.ttf";
    public static final String FONT_SEGOE_SCRIPT = "font/segoe_script.ttf";
    public static final String TAG_SELECT_SOUND                         = "selectSound";
    public static final int MATERIAL_FILE_PICKER                        = 0x3;

    //For Font Static array value  created by Reaz
    public static final String fontName[] = {"DEFAULT", "SEGOE SCRIPT", "DANCING ", "ROBOTO THIN", "ROAD BRUSH", "ROBOTO CONDENSED", "RANCHO"};
    public static final String soundDelay[] = {"None", "1 second ", "3 second", "5 second", "7 second", "10 second"};


    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";

    //For Feedback Type Vaue by RAFI
    public static final int TAG_TYPE_RECTANGULAR = 0;
    public static final int TAG_TYPE_CIRCULAR = 1;

    //For sound delay Static value  created by Reaz
    public static final int TAG_TYPE_SOUND_DELAY_NONE = 0;
    public static final int TAG_TYPE_SOUND_DELAY_ONE_SEC = 1;
    public static final int TAG_TYPE_SOUND_DELAY_THREE_SEC = 3;
    public static final int TAG_TYPE_SOUND_DELAY_FIVE_SEC = 5;
    public static final int TAG_TYPE_SOUND_DELAY_SEVEN_SEC = 7;
    public static final int TAG_TYPE_SOUND_DELAY_TEN_SEC = 10;

    //For sound delay Static value  created by Reaz
    public static final int TAG_TYPE_NONE = 0;
    public static final int TAG_TYPE_ONE = 1;
    public static final int TAG_TYPE_TWO = 2;
    public static final int TAG_TYPE_THREE = 3;
    public static final int TAG_TYPE_FOUR = 4;
    public static final int TAG_TYPE_FIVE = 5;


    //For sound  Static value in play mode created by Reaz

    public static final int TAG_ITEM_SOUND_PLAY = 0;
    public static final int TAG_END_SINGLE_TAP_TASK = 1;
    public static final int TAG_POSITIVE_SOUND_PLAY = 2;
    public static final int TAG_NEGATIVE_SOUND_PLAY = 3;
//  public static final int TAG_FEEDBACK_SOUND                     = 3;


    public static final String TAG_PLAY_ERROR_IMAGE = "errorImage";
    public static final String TAG_PLAY_ERROR_TEXT = "errorText";
    public static final String TAG_PLAY_ERROR_COLOR = "errorColor";
    public static final String TAG_PLAY_ERROR_SOUND = "errorSound";
    public static final int TAG_PLAY_ERROR_RESPONSE_CODE = 1;
    public static final String TAG_PLAY_ERROR_RESPONSE = "ERROR";


    public static final int TAG_TASK_GENERAL_MODE = 0x01;
    public static final int TAG_TASK_DRAG_AND_DROP_MODE = 0x02;
    public static final int TAG_TASK_ASSISTIVE_MODE = 0x03;
    public static final String TAG_TASK_MODE_KEY = "MODE";
    public static final String TARGET_KEY = "TARGET";

    public static final int TAG_CREATE_TASK_PREVIEW_MODE = 0x1001;

    public static final int TAG_TRANSPARENT_DIALOG_ACTIVITY = 5009;

    public static final String TAG_INTENT_IMG                          = "filePickerType";
    public static final String TAG_SELECT_PICTURE                      = "selectPicture";
    public static final String TAG_SELECT_PICTURE_ERROR                = "selectPictureError";
    public static final String TAG_SELECT_PICTURE_FEEDBACK             = "selectPictureFeedBack";
    public static final int SELECT_PICTURE_ERROR                             = 0x101;
    public static final int SELECT_PICTURE_FEEDBACK                          = 0x102;




    /***********************************
     * ANDROID_DATA COMMON Static Value
     **************************************/
    public static final String ANDROID_DATA = "/Android/Data/";
    public static final String ANDROID_DATA_PACKAGE_IMAGE = "/Images/";
    public static final String ANDROID_DATA_PACKAGE_SOUND = "/Sound/";

    /**
     * File Manager static Value for Share
     */
    public static final String SD_CARD_ROOT_LITERACY_APP_GENERATED = "/Literacy/Generated";

    public static final String SD_CARD_ROOT_MIND_APP_GENERATED_JSON = "/Literacy/Generated/JSON/";
    public static final String SD_CARD_ROOT_MIND_APP_GENERATED_IMAGE = "/Literacy/Generated/Images/";
    public static final String SD_CARD_ROOT_MIND_APP_GENERATED_SOUND = "/Literacy/Generated/Sound/";

    public static final String RECEIVED_PATH = "/Received";

    public static final String RECEIVED_JSON_PATH = "/Received/JSON/";
    public static final String RECEIVED_IMAGE_PATH = "/Received/Images/";
    public static final String RECEIVED_SOUND_PATH = "/Received/Sound/";

    public static final String JSON_FILE_NAME = "Taskpacks.JSON";
    public static final String LITERACY_APP_GENERATED_ROOT_SLASH = "/Literacy/Generated/";
    public static final String LITERACY_APP_NAME = "/Literacy/";
    public static final String JSON = "/JSON/";
    public static final String DOT_JSON = ".json";
    // file name For encode json
    public static final String FILE_NAME = "Task";
    public static final String TASK_PACK_FILE_NAME = "LessonPack_";/// file name change TaskPack_ to LessonPack_ by sumon
    public static final String DOT_EXTENSION = ".lit";
    public static final String SLASH = "/";
    public static final String SET_TYPE = "application/zip";
    public static final String FILE_SLASH = "file://";

    /**
     * for image processing Static Value
     */

    public static final String FILE_FORMAT_NAME = "lit_";
    public static final String DOT_IMAGE_FORMAT = ".png";
    public static final String TEMP_IMAGE_NAME = "temp_photo.png";

    /**
     * for image processing Static Value
     */

    public static final String DOT_SOUND_FORMAT = ".3gpp";


    public static final String INTENT_TASK_PACK_ID = "taskPackId";
    public static final String POSITION = "position";
    public static final String INTENT_TASK_ID = "taskId";
    public static final String INTENT_TASK_TYPE = "taskType";
    public static final String INTENT_TASK_PREVIEW = "preView";
    public static final String TAG_INTENT_YOUTUBE = "youTube";
    public static final String TAG_INTENT_PDF_LINK = "youTube";
    public static final String EDIT_MODE = "E";

    public static final String FEEDBACK_IMG_PATH = "/FeedBackImages/";


    //  font Type Insert for which font selected
    public static int getTextFontValue(String fontName) {
        int index = 0;
//        String soundDelayArray[] = {"None", "1 second ", "3 second", "5 second", "7 second","10 second"};
        for (int i = 0; i < StaticAccess.fontName.length; i++) {
            if (fontName.equals(StaticAccess.fontName[i])) {
                index = i;
            }
        }
        return index;
    }


    // For touch animation static Value
    public static final int ANIM_TOUCH_NONE = 0;
    public static final int ANIM_TOUCH_COLOR_CIRCLE = 1;
    public static final int ANIM_TOUCH_COLOR_CIRCLE_STROKE = 2;
    public static final int ANIM_TOUCH_CIRCLE_STROKE = 3;
    public static final int ANIM_TOUCH_EVEN = 4;
/*    public static final int ANIM_TOUCH_NONE                       = 4;
    public static final int ANIM_TOUCH_NONE                        = 5;*/

    public static final String[] LEVEL = {"None", "Level 1", "Level 2", "Level 3", "Level 4", "Level 5", "Level 6", "Level 7", "Level 8", "Level 9"};
    public static final String[] TASKPACK_TYPE = {"ANSWER", "MATCH", "READ", "WRITE", "CLASSIFICATE", "SEQUENCE"};
    public static final String[] TASKPACK_TYPE_SHORT = {"N", "A", "R", "W", "D", "S"};



}
