package com.example.tts.myapplication;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.MotionEvent;
import android.view.View;

import com.tts.libimagezoomanim.AndroidUtilities;
import com.tts.libimagezoomanim.AnimationController;
import com.tts.libimagezoomanim.AnimationLayout;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * Created by tts on 4/11/17.
 */

public class FullScreenImageTouchListener implements View.OnTouchListener {
    public static final float DELTA_Y_THRESH = AndroidUtilities.dp(150);

    private AnimationLayout mAnimationLayout;
    private AnimationController mAnimationController;
    float startingX, startingY, deltaX, deltaY, animatingImageViewOriginX, animatingImageViewOriginY;
    boolean locked, lockedX;

    public FullScreenImageTouchListener() {

    }


    /** Warning: must be called BEFORE any touch event occur, or null pointer exception will be thrown
     * @param animLayout
     */
    public void setAnimationLayout(AnimationLayout animLayout) {
        mAnimationLayout = animLayout;
    }

    /** Warning: must be called BEFORE any touch event occur, or null pointer exception will be thrown
     * @param animController
     */
    public void setAnimationController(AnimationController animController) {
        mAnimationController = animController;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mAnimationLayout.isExpanded()) {
            switch (event.getAction()) {
                case ACTION_DOWN:
                    startingY = event.getY();
                    startingX = event.getX();
                    animatingImageViewOriginY = mAnimationLayout.getAnimatingImageView().getTranslationY();
                    animatingImageViewOriginX = mAnimationLayout.getAnimatingImageView().getTranslationX();
                    locked = false;
                    lockedX = false;
                    break;
                case ACTION_MOVE:
                    deltaY = event.getY() - startingY;
                    deltaX = event.getX() - startingX;
                    if (!locked) {
                        lockedX = (Math.abs(deltaY) <= Math.abs(deltaX));
                        locked  = true;
                    } else {
                        if (!lockedX) {
                            // process swiping up/down
                            mAnimationLayout.getAnimatingImageView().setTranslationY(animatingImageViewOriginY + deltaY);
                            mAnimationLayout.getBackgroundDrawable().setAlpha(255 - Math.abs(255 * (int) deltaY / AndroidUtilities.displaySizePixel.y));
                        } else {
                            // process swiping left/right
//                            mAnimationLayout.getAnimatingImageView().setTranslationX(animatingImageViewOriginX + deltaX);
//                            mAnimationLayout.getBackgroundDrawable().setAlpha(255 - Math.abs(255 * (int) deltaX / AndroidUtilities.displaySizePixel.x));
                        }
                    }
                    break;
                case ACTION_UP:
                    if (Math.abs(event.getY() - startingY) > DELTA_Y_THRESH) {
                        mAnimationController.closePhoto();
                    } else {
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(
                                ObjectAnimator.ofFloat(mAnimationLayout.getAnimatingImageView(), "translationY", animatingImageViewOriginY),
                                ObjectAnimator.ofInt(mAnimationLayout.getBackgroundDrawable(), "alpha", 255));
                        animatorSet.setDuration(200).start();
                    }
                    break;
            }
            return true;
        } else {
            return false;
        }

    }
}
