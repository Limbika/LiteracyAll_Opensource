package com.literacyall.app.utilities;

import android.content.Context;
import android.graphics.Typeface;

public class TypeFace_MY {


    public static Typeface getRoboto(Context ctx) {
        Typeface myTypeface =
                Typeface.createFromAsset(ctx.getAssets(), StaticAccess.FONT_ROBOTO);
        return myTypeface;

    }

    public static Typeface getSegoeScript(Context ctx) {
        Typeface myTypeface =
                Typeface.createFromAsset(ctx.getAssets(), StaticAccess.FONT_SEGOE_SCRIPT);
        return myTypeface;

    }


    public static Typeface getDancing(Context ctx) {
        Typeface myTypeface =
                Typeface.createFromAsset(ctx.getAssets(), StaticAccess.FONT_DANCING);
        return myTypeface;

    }

    public static Typeface getRobotoThin(Context ctx) {
        Typeface myTypeface =
                Typeface.createFromAsset(ctx.getAssets(), StaticAccess.FONT_ROBOTO_THIN);
        return myTypeface;

    }

    public static Typeface getRoadBrush(Context ctx) {
        Typeface myTypeface =
                Typeface.createFromAsset(ctx.getAssets(), StaticAccess.FONT_ROAD_BRUSH);
        return myTypeface;

    }

    public static Typeface getRoboto_condensed(Context ctx) {
        Typeface myTypeface =
                Typeface.createFromAsset(ctx.getAssets(), StaticAccess.FONT_ROBOTO_CONDENSED);
        return myTypeface;

    }


    public static Typeface getRancho(Context ctx) {
        Typeface tf = Typeface.createFromAsset(ctx.getAssets(), StaticAccess.FONT_RANCHO);
        return tf;
    }

}
