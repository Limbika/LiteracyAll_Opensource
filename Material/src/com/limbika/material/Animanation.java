package com.limbika.material;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/** Animation nation. */
public class Animanation {
	
	/**
	 * Clear all animations.
	 * @param view The view to clear animation.
	 */
	public static void clear(View view) {
		view.setAnimation(null);
	}
	
	/**
	 * A classic blink animation.
	 * @param view The view to animate.
	 */
	public static void blink(final View view) {
		final AlphaAnimation toAlpha = new AlphaAnimation(1, 0);
		final AlphaAnimation fromAlpha = new AlphaAnimation(0, 1);
		
		toAlpha.setDuration(1000);
		toAlpha.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				view.startAnimation(fromAlpha);
			}
		});
		
		fromAlpha.setDuration(1000);
		fromAlpha.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				view.startAnimation(toAlpha);
			}
		});
		
		view.startAnimation(toAlpha);
	}
	
	/**
	 * Alpha animation to set a view visibility {@link View#VISIBLE}.
	 * @param view The view to show.
	 */
	public static void toVisible(final View view) {
		if ( view.getVisibility() == View.VISIBLE ) return;
		
		AlphaAnimation animation = new AlphaAnimation(0, 1);
		animation.setDuration(250);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				view.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {}
		});
		view.startAnimation(animation);
	}
	
	/**
	 * Alpha animation to set a view visibility to {@link View#INVISIBLE}.
	 * @param view The view to hide.
	 */
	public static void toInvisible(final View view) {
		if ( view.getVisibility() == View.INVISIBLE ) return;
		
		AlphaAnimation animation = new AlphaAnimation(1, 0);
		animation.setDuration(250);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.INVISIBLE);
			}
		});
		view.startAnimation(animation);
	}
	
	/**
	 * Alpha animation to set a view visibility to {@link View#GONE}.
	 * @param view The view to hide.
	 */
	public static void toGone(final View view) {
		if ( view.getVisibility() == View.GONE ) return;
		
		AlphaAnimation animation = new AlphaAnimation(1, 0);
		animation.setDuration(250);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.GONE);
			}
		});
		view.startAnimation(animation);
	}

}
