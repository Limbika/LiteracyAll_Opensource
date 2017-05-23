package com.literacyall.app.utilities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;


public class Animanation {

    public static final int noAnimation = 0;
    public static final int shake1 = 1;
    public static final int shake2 = 2;
    public static final int blink1 = 3;
    public static final int slideDownUp = 4;
    public static final int slideUpDown = 5;
    public static final String animationName[] = {"No Animation", "Alpha 1", "Alpha 2", "Blink 1", "Down Upside", "Upside Down"};

    public static void clear(View view) {
        view.setAnimation(null);
    }

    // shakeAnimation Animation
    public static void shakeAnimation(View v) {
        RotateAnimation rotate = new RotateAnimation(-30, 60, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatCount(1);
        v.setAnimation(rotate);
    }

    // shakeAnimation2 Animation
    public static void shakeAnimation2(final View v) {
        RotateAnimation rotate = new RotateAnimation(-20, 20, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        rotate.setDuration(200);
//        rotate.setRepeatMode(2);
        rotate.setRepeatCount(1);
        v.setAnimation(rotate);
        rotate.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                v.clearAnimation();
            }
        });
    }

    // Blink Animation
    public static void blink(final View view) {
        /*final AlphaAnimation toAlpha = new AlphaAnimation(1, 0);
        final AlphaAnimation fromAlpha = new AlphaAnimation(0, 1);
        toAlpha.setDuration(1000);
        toAlpha.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                animation.setRepeatCount(1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                view.startAnimation(fromAlpha);
            }
        });

        fromAlpha.setDuration(1000);
        fromAlpha.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                animation.setRepeatCount(1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.startAnimation(toAlpha);
            }
        });
*/
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
//        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(1);
        view.startAnimation(anim);
//        view.startAnimation(toAlpha);
    }

    // Blink Animation2
    public static void blink2(final View view) {
      /*  final AlphaAnimation toAlpha = new AlphaAnimation(1, 0);
        final AlphaAnimation fromAlpha = new AlphaAnimation(0, 1);
        toAlpha.setDuration(400);
        toAlpha.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                animation.setRepeatCount(1);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.startAnimation(fromAlpha);
            }
        });

        fromAlpha.setDuration(400);
        fromAlpha.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                animation.setRepeatCount(1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.startAnimation(toAlpha);
            }
        });*/
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(2000); //You can manage the blinking time with this parameter
//        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(1);
        view.startAnimation(anim);
//        view.startAnimation(toAlpha);
    }

    /**
     * ANIMATION TO SLIDE up to DOWN
     **/
    public static void topToBottom(View v) {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, -400.0f, 0.0f);
        animation.setDuration(1000); // animation duration
        //animation.setRepeatCount(1);
        v.setVisibility(View.VISIBLE);
        v.startAnimation(animation);
    }

    public static void bottomToTop(View v) {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, 400.0f, 0.0f);
        animation.setDuration(1000); // animation duration
        //animation.setRepeatCount(1);
        v.setVisibility(View.VISIBLE);
        v.startAnimation(animation);
    }

    /**
     * ANIMATION TO SLIDE down to DOWN only for eventSeq
     **/
    /*public static void slideDown_to_Down(final View v, int id) {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 400.0f);
        animation.setDuration(1000);
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.INVISIBLE);
                ImageButton iv = (ImageButton) v;
                iv.setImageResource(R.drawable.img_white_face_pressed);
                topToBottom(v);
            }
        });
        v.startAnimation(animation);
    }*/


    /**********************************************************
     * Feedback Dialog Animation
     *****************************************************************/
    // shakeAnimation Animation
    public static void shakeFeedBackAnimation(View v) {
        RotateAnimation rotate = new RotateAnimation(-30, 60, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatCount(1);
        v.setAnimation(rotate);
    }

    // shakeAnimation2 Animation
    public static void shakeFeedBackAnimation2(final View v) {
        RotateAnimation rotate = new RotateAnimation(-20, 20, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        rotate.setDuration(200);
//        rotate.setRepeatMode(2);
        rotate.setRepeatCount(1);
        v.setAnimation(rotate);
        rotate.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                v.clearAnimation();
            }
        });
    }

    // Blink Animation
    public static void blinkFeedBackAnimation(final View view) {
        final AlphaAnimation toAlpha = new AlphaAnimation(1, 0);
        final AlphaAnimation fromAlpha = new AlphaAnimation(0, 1);
        toAlpha.setDuration(1000);
        toAlpha.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                animation.setRepeatCount(1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                view.startAnimation(fromAlpha);
            }
        });

        fromAlpha.setDuration(1000);
        fromAlpha.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                animation.setRepeatCount(1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.startAnimation(toAlpha);
            }
        });

    /*    Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
//        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(1);
        view.startAnimation(anim);*/
        view.startAnimation(toAlpha);
    }

    // Blink Animation2
    public static void blink2FeedBackAnimation(final View view) {
        final AlphaAnimation toAlpha = new AlphaAnimation(1, 0);
        final AlphaAnimation fromAlpha = new AlphaAnimation(0, 1);
        toAlpha.setDuration(400);
        toAlpha.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                animation.setRepeatCount(1);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.startAnimation(fromAlpha);
            }
        });

        fromAlpha.setDuration(400);
        fromAlpha.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                animation.setRepeatCount(1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.startAnimation(toAlpha);
            }
        });
      /*  Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
//        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(1);
        view.startAnimation(anim);*/
//        view.startAnimation(toAlpha);
    }

    /**
     * ANIMATION TO SLIDE up to DOWN
     **/
    public static void bottomToTopFeedBackAnimation(View v) {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, 400.0f, 0.0f);
        animation.setDuration(1000); // animation duration
        //animation.setRepeatCount(1);
        v.setVisibility(View.VISIBLE);
        v.startAnimation(animation);
    }


    public static void topToBottomFeedBackAnimation(View v) {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, -400.0f, 0.0f);
        animation.setDuration(1000); // animation duration
        //animation.setRepeatCount(1);
        v.setVisibility(View.VISIBLE);
        v.startAnimation(animation);
    }


    public static void moveAnimation(View v) {
        TranslateAnimation animation = new TranslateAnimation(-50.0f, 0.0f, 0.0f, 0.0f);
        animation.setDuration(1500); // animation duration
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(2);
        v.startAnimation(animation);
    }


    // zoomout animation
    public static void zoomOut(final View v) {
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(v,
                PropertyValuesHolder.ofFloat("scaleX", .80f),
                PropertyValuesHolder.ofFloat("scaleY", .80f));
        scaleDown.setDuration(200);

        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

        scaleDown.setRepeatCount(1);

        scaleDown.start();
    }

}
