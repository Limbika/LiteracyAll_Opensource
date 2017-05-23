package com.literacyall.app.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.literacyall.app.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by ibrar on 4/6/2016.
 */
public class ImageProcessing {

    String imageNameForSDCard;
    private String appImagePath = null;


    Context ctx;

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    protected ImageLoader imageLoaderRound = ImageLoader.getInstance();
    DisplayImageOptions options, optionsRound, optionRowItem;

    public ImageProcessing(Context context) {
        ctx = context;
        System.out.println(ctx.getResources().getString(R.string.image_folder_path));
//        appImagePath = "/Android/Data/" + ctx.getPackageName() + "/Images/";
        appImagePath = StaticAccess.ANDROID_DATA + ctx.getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_IMAGE;
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new FadeInBitmapDisplayer(0))
                .resetViewBeforeLoading(true)
                .build();
        imageLoader.clearDiscCache();

        imageLoaderRound.init(ImageLoaderConfiguration.createDefault(context));
        optionsRound = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(100))
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new RoundedBitmapDisplayer(125))
                .build();
        imageLoaderRound.clearDiscCache();

        optionRowItem = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new FadeInBitmapDisplayer(0))
                .build();
        imageLoader.clearDiscCache();

    }

    public void setImageWith_loaderFullPath(ImageView iv, String path) {
//        imageLoader.displayImage("file://+/" + path, iv, options);
        imageLoader.displayImage(StaticAccess.FILE_SLASH + StaticAccess.SLASH + path, iv, options);
    }

    public void setImageWith_loader(ImageView iv, String path) {
        imageLoader.displayImage(StaticAccess.FILE_SLASH + StaticAccess.SLASH + getAbsolutepath_Of_Image(path), iv, options);
    }

    public void setImageWith_loaderRound(ImageView iv, String path) {
        imageLoaderRound.displayImage(StaticAccess.FILE_SLASH + StaticAccess.SLASH + getAbsolutepath_Of_Image(path), iv, optionsRound);
    }

    public void setImageWith_loaderRowItem(ImageView iv, String path) {
        imageLoader.displayImage(StaticAccess.FILE_SLASH + StaticAccess.SLASH + getAbsolutepath_Of_Image(path), iv, optionRowItem);
    }

    public static String encodeTobase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        return imageEncoded;
    }

    // image decode method
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    // Save image to SD card
    public String imageSave(Bitmap bmp) {
        try {
            File sdCardDirectory = new File(Environment.getExternalStorageDirectory() + appImagePath);
            if (!sdCardDirectory.exists()) {
                sdCardDirectory.mkdirs();
            }

            imageNameForSDCard = StaticAccess.FILE_FORMAT_NAME + System.currentTimeMillis() + StaticAccess.DOT_IMAGE_FORMAT;
            File image = new File(sdCardDirectory, imageNameForSDCard);
            FileOutputStream outStream;

            outStream = new FileOutputStream(image);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            bmp.recycle();
            return imageNameForSDCard;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAbsolutepath_Of_Image(String imageName) {
        return Environment.getExternalStorageDirectory() + appImagePath + imageName;
    }

    // Getting Image from SD Card
    public Bitmap getImage(String imageName) {
        File image = new File(getAbsolutepath_Of_Image(imageName));
        if (image.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(image.getAbsolutePath());
            return bmp;
        } else {
            return null;
        }
    }

    private Bitmap resize(Bitmap originalImage, int width, int height) {
        Bitmap background = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        float originalWidth = originalImage.getWidth(), originalHeight = originalImage.getHeight();
        Canvas canvas = new Canvas(background);
        float scale = width / originalWidth;
        float xTranslation = 0.0f, yTranslation = (height - originalHeight * scale) / 2.0f;
        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(originalImage, transformation, paint);
        originalImage.recycle();
        return background;
    }

    public boolean deleteImage(String imageName) {
        boolean deleted = false;
        File image = new File(getAbsolutepath_Of_Image(imageName));
        if (image.exists()) {
            deleted = image.delete();
        }
        return deleted;
    }

    public String getImageDir() {
        return appImagePath;
    }

    /**
     * Save image to SD card for crop
     **/
    public File imageSaveToCrop(Bitmap bmp, File imageFile) {
        try {
            File sdCardDirectory = new File(Environment.getExternalStorageDirectory() + appImagePath);
            if (!sdCardDirectory.exists()) {
                sdCardDirectory.mkdirs();
            }

            imageFile = new File(sdCardDirectory, StaticAccess.TEMP_IMAGE_NAME);
            FileOutputStream outStream;

            outStream = new FileOutputStream(imageFile);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            //bmp.recycle();
            return imageFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public File getCroppedImage() {
        File image = new File(getAbsolutepath_Of_Image(StaticAccess.TEMP_IMAGE_NAME));
        if (image.exists()) {
            return image;
        } else {
            return null;
        }
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 50;
        int targetHeight = 50;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2, ((float) targetHeight - 1) / 2, (Math.min(((float) targetWidth), ((float) targetHeight)) / 2), Path.Direction.CCW);
        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight()), new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    public String takeScreenshot(View view) {
        String imageName = null;
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);


            imageName = imageSave(bitmap);

        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }

        return imageName;
    }

    //save Image To sd card into package folder FeedBack
    // Save image to SD card
    public String feedBackImageSave(Bitmap bmp) {
        try {
            File sdCardDirectory = new File(Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + ctx.getPackageName()+StaticAccess.FEEDBACK_IMG_PATH);
            if (!sdCardDirectory.exists()) {
                sdCardDirectory.mkdirs();
            }
            imageNameForSDCard = StaticAccess.FILE_FORMAT_NAME + System.currentTimeMillis() + StaticAccess.DOT_IMAGE_FORMAT;
            File image = new File(sdCardDirectory, imageNameForSDCard);
            FileOutputStream outStream;

            outStream = new FileOutputStream(image);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            bmp.recycle();
            return getAbsolutepath_Of_FeedBack_Image(imageNameForSDCard);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAbsolutepath_Of_FeedBack_Image(String imageName) {
        return Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + ctx.getPackageName()+StaticAccess.FEEDBACK_IMG_PATH + imageName;
    }

}
