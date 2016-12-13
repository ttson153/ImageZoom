package com.example.tts.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import static android.R.attr.orientation;

/**
 * Created by tts on 12/13/16.
 */

public class AnimationLayout extends FrameLayout {

    private static final long ANIMATION_DURATION_MS = 400;

    public interface OnEventListener {
        void onPreExpanded();
        void onPostExpanded();
        void onPreShrunk();
        void onPostShrunk();
    }

    private long mAnimDuration;
    private ClippingImageView animatingImageView;
    private float[][] animationValues = new float[2][8];
    private ColorDrawable backgroundDrawable = new ColorDrawable(0xff000000);

    private OnEventListener mListener;
    public void setOnEventListener(OnEventListener listener) {
        mListener = listener;
    }

    public ClippingImageView getAnimatingImageView() {
        return animatingImageView;
    }

    private int statusBarHeight;
    private boolean useOccupyStatusBar = false;
    public void setUseOccupyStatusBar(boolean occupyStatusBar) {
        useOccupyStatusBar = occupyStatusBar;
    }

    private boolean animating;
    private boolean isExpanded;
    public boolean isExpanded() {
        return isExpanded;
    }
    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
    public boolean isAnimating() {
        return animating;
    }

    public AnimationLayout(Context context) {
        super(context);
        mAnimDuration = ANIMATION_DURATION_MS;
        statusBarHeight = AndroidUtilities.statusBarHeight;

        animating = false;
        animatingImageView = new ClippingImageView(context);
        backgroundDrawable.setAlpha(0);
        this.setBackgroundDrawable(backgroundDrawable);
        this.addView(animatingImageView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void expand(Rect drawRegion, TransformData object) {
        isExpanded = true;
        animating = true;
        bringToFront();

        animatingImageView.setAnimationValues(animationValues);

        animatingImageView.setVisibility(View.VISIBLE);
        animatingImageView.setRadius(object.radius);
        animatingImageView.setOrientation(orientation);
        animatingImageView.setNeedRadius(object.radius != 0);
        animatingImageView.setImageBitmap(object.thumbImage);

        animatingImageView.setAlpha(1.0f);
        animatingImageView.setPivotX(0.0f);
        animatingImageView.setPivotY(0.0f);
        animatingImageView.setScaleX(object.scale);
        animatingImageView.setScaleY(object.scale);

        animatingImageView.setTranslationX(drawRegion.left);
        animatingImageView.setTranslationY(drawRegion.top);
        final ViewGroup.LayoutParams layoutParams = animatingImageView.getLayoutParams();
        layoutParams.width = (drawRegion.right - drawRegion.left);
        layoutParams.height = (drawRegion.bottom - drawRegion.top);
        animatingImageView.setLayoutParams(layoutParams);

        float scaleX = (float) AndroidUtilities.displaySize.x / layoutParams.width;
        float scaleY = (float) (AndroidUtilities.displaySize.y + (useOccupyStatusBar ? 0 : statusBarHeight)) / layoutParams.height;
        float scale = scaleX > scaleY ? scaleY : scaleX;
        float width = layoutParams.width * scale;        //width  of full image
        float height = layoutParams.height * scale;      //height of full image
        float xPos = (AndroidUtilities.displaySize.x - width) / 2.0f;
        float yPos = ((AndroidUtilities.displaySize.y + (useOccupyStatusBar ? 0 : statusBarHeight)) - height) / 2.0f;

        int clipTop = 0;
        int clipBottom = 0;
        if (drawRegion.bottom > (AndroidUtilities.displaySizePixel.y - object.clipBottomAddition)) {
            clipBottom = drawRegion.bottom - ((AndroidUtilities.displaySizePixel.y) - object.clipBottomAddition);
        }
        if (drawRegion.top < ((useOccupyStatusBar ? statusBarHeight : 0) + object.clipTopAddition)) {
            clipTop = -drawRegion.top + (useOccupyStatusBar ? statusBarHeight : 0) + object.clipTopAddition;
        }

        animationValues[0][0] = animatingImageView.getScaleX();
        animationValues[0][1] = animatingImageView.getScaleY();
        animationValues[0][2] = animatingImageView.getTranslationX();
        animationValues[0][3] = animatingImageView.getTranslationY() - (useOccupyStatusBar ? statusBarHeight : 0);
//        animationValues[0][4] = clipHorizontal * object.scale;
        animationValues[0][5] = clipTop * object.scale;
        animationValues[0][6] = clipBottom * object.scale;
        animationValues[0][7] = animatingImageView.getRadius();

        Log.d("MyLog",  "Draw Region " + drawRegion.left + " " + drawRegion.top + " " + drawRegion.right + " " + drawRegion.bottom + " ");
        Log.d("MyLog",  "layoutParam " + layoutParams.width + " " + layoutParams.height);
        Log.d("MyLog",  "Clip Value "  + clipTop + " " + clipBottom);

        animationValues[1][0] = scale;
        animationValues[1][1] = scale;
        animationValues[1][2] = xPos;
        animationValues[1][3] = yPos;
        animationValues[1][4] = 0;
        animationValues[1][5] = 0;
        animationValues[1][6] = 0;
        animationValues[1][7] = 0;

        animatingImageView.setAnimationProgress(0);
        backgroundDrawable.setAlpha(0);

        Log.d("MyLog", "ClipIVSize  " + animatingImageView.getLeft() + " " + animatingImageView.getTop() + " " +
                animatingImageView.getMeasuredWidth() + " " + animatingImageView.getMeasuredHeight() + " " +
                animatingImageView.getTranslationX() + " " + animatingImageView.getTranslationY() + " " +
                animatingImageView.getX() + " " + animatingImageView.getY() + " " +
                animatingImageView.getParent().toString());

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(animatingImageView, "animationProgress", 0.0f, 1.0f),
                ObjectAnimator.ofInt(backgroundDrawable, "alpha", 0, 255)
        );
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mListener.onPreExpanded();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mListener.onPostExpanded();
            }
        });

        animatorSet.setDuration(mAnimDuration);
        animatorSet.start();

    }

    public void shrink(Rect drawRegion, TransformData object) {
        isExpanded = false;
        animating = true;
        bringToFront();

        final ViewGroup.LayoutParams layoutParams = animatingImageView.getLayoutParams();
        layoutParams.width = (drawRegion.right - drawRegion.left);
        layoutParams.height = (drawRegion.bottom - drawRegion.top);
        animatingImageView.setLayoutParams(layoutParams);

        int clipTop = 0;
        int clipBottom = 0;
        if (drawRegion.bottom > AndroidUtilities.displaySize.y) {
            clipBottom = drawRegion.bottom - AndroidUtilities.displaySizePixel.y + object.clipBottomAddition;
        }
        if (drawRegion.top < ((useOccupyStatusBar ? statusBarHeight : 0) + object.clipTopAddition)) {
            clipTop = -drawRegion.top + (useOccupyStatusBar ? statusBarHeight : 0) + object.clipTopAddition;
        }

        animationValues[0][0] = animatingImageView.getScaleX();
        animationValues[0][1] = animatingImageView.getScaleY();
        animationValues[0][2] = animatingImageView.getTranslationX();
        animationValues[0][3] = animatingImageView.getTranslationY();
//        animationValues[0][4] = 0;
        animationValues[0][5] = 0;
        animationValues[0][6] = 0;
        animationValues[0][7] = 0;

        animationValues[1][0] = object.scale;
        animationValues[1][1] = object.scale;
        animationValues[1][2] = drawRegion.left;
        animationValues[1][3] = drawRegion.top - (useOccupyStatusBar ? statusBarHeight : 0);
//        animationValues[1][4] = clipHorizontal * object.scale;
        animationValues[1][5] = clipTop * object.scale;
        animationValues[1][6] = clipBottom * object.scale;
        animationValues[1][7] = object.radius;

        animatingImageView.setAnimationProgress(0);

        Log.d("MyLog", "ClipIVSize  " + animatingImageView.getLeft() + " " + animatingImageView.getTop() + " " +
                animatingImageView.getMeasuredWidth() + " " + animatingImageView.getMeasuredHeight() + " " +
                animatingImageView.getTranslationX() + " " + animatingImageView.getTranslationY() + " " +
                animatingImageView.getX() + " " + animatingImageView.getY() + " " +
                animatingImageView.getParent().toString());

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(animatingImageView, "animationProgress", 0.0f, 1.0f),
                ObjectAnimator.ofInt(backgroundDrawable, "alpha", 255, 0)
        );
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mListener.onPostShrunk();
            }
        });

        animatorSet.setDuration(mAnimDuration);
        animatorSet.start();
    }
}
