package com.example.tts.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import static android.R.attr.orientation;

/**
 * Created by tts on 12/13/16.
 */

public class AnimationLayout extends FrameLayout {

    private static final long ANIMATION_DURATION_MS = 200;

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

    public ColorDrawable getBackgroundDrawable() {
        return backgroundDrawable;
    }

    private int statusBarHeight;
    private boolean useOccupyStatusBar = (Build.VERSION.SDK_INT < 21);
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

        float thumbRatio = (float) object.thumbImage.getWidth() / object.thumbImage.getHeight();
        float imageViewRatio = (float) drawRegion.width() / drawRegion.height();

        final ViewGroup.LayoutParams layoutParams = animatingImageView.getLayoutParams();
        if (thumbRatio > imageViewRatio) {
            // Zoom out to fit width
            layoutParams.width = drawRegion.width();
            layoutParams.height = (int) (drawRegion.width() / thumbRatio);
            // Zoom in back fit height
            layoutParams.width = layoutParams.width * drawRegion.height() / layoutParams.height;
            layoutParams.height = drawRegion.height();
        }
        else if (thumbRatio < imageViewRatio){
            // Zoom out to fit height
            layoutParams.width = (int) (drawRegion.height() * thumbRatio);
            layoutParams.height = drawRegion.height();
            // Zoom in back fit width
            layoutParams.height = layoutParams.height * drawRegion.width() / layoutParams.width;
            layoutParams.width = drawRegion.width();
        }
        else {
            layoutParams.width = drawRegion.width();
            layoutParams.height = drawRegion.height();
        }
        animatingImageView.setLayoutParams(layoutParams);

        animatingImageView.setTranslationX(drawRegion.left + (drawRegion.width() - layoutParams.width) / 2);
        animatingImageView.setTranslationY(drawRegion.top - (useOccupyStatusBar ? statusBarHeight : 0) + (drawRegion.height() - layoutParams.height) / 2);

        float scaleX = (float) AndroidUtilities.displaySizePixel.x / layoutParams.width;
        float scaleY = (float) (AndroidUtilities.displaySizePixel.y - (useOccupyStatusBar ? statusBarHeight : 0)) / layoutParams.height;
//        float scaleX = (float) AndroidUtilities.displaySizePixel.x / layoutParams.width;
//        float scaleY = (float) (AndroidUtilities.displaySizePixel.y - (useOccupyStatusBar ? statusBarHeight : 0)) / object.thumbImage.getHeight();
        float scale = scaleX > scaleY ? scaleY : scaleX;
        float width = layoutParams.width * scale;        //width  of full image
        float height = layoutParams.height * scale;      //height of full image
        float xPos = (AndroidUtilities.displaySizePixel.x - width) / 2.0f;
        float yPos = ((AndroidUtilities.displaySizePixel.y - (useOccupyStatusBar ? statusBarHeight : 0)) - height) / 2.0f;

        int clipTop = 0;
        int clipBottom = 0;
        int clipHorizontal = 0;
        int clipVertical = 0;
        if (drawRegion.bottom > (AndroidUtilities.displaySizePixel.y - object.clipBottomAddition)) {
            clipBottom = drawRegion.bottom - ((AndroidUtilities.displaySizePixel.y) - object.clipBottomAddition);
        }
        if (drawRegion.top < (statusBarHeight + object.clipTopAddition)) {
            clipTop = -drawRegion.top + statusBarHeight + object.clipTopAddition;
        }
        if (thumbRatio > imageViewRatio) {
            clipHorizontal = (layoutParams.width - drawRegion.width()) / 2;
        }
        else if (thumbRatio < imageViewRatio) {
            clipVertical = (layoutParams.height - drawRegion.height()) / 2;
        }
        clipTop += clipVertical;
        clipBottom += clipVertical;

        animationValues[0][0] = animatingImageView.getScaleX();
        animationValues[0][1] = animatingImageView.getScaleY();
        animationValues[0][2] = animatingImageView.getTranslationX();
        animationValues[0][3] = animatingImageView.getTranslationY()/* - (useOccupyStatusBar ? statusBarHeight : 0)*/;
        animationValues[0][4] = clipHorizontal * object.scale;
        animationValues[0][5] = clipTop * object.scale;
        animationValues[0][6] = clipBottom * object.scale;
        animationValues[0][7] = animatingImageView.getRadius();

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
        final AnimatorSet animatorSet = new AnimatorSet();
        isExpanded = false;
        if (drawRegion != null) {
            animating = true;
            bringToFront();

            float thumbRatio = (float) object.thumbImage.getWidth() / object.thumbImage.getHeight();
            float imageViewRatio = (float) drawRegion.width() / drawRegion.height();

            final ViewGroup.LayoutParams layoutParams = animatingImageView.getLayoutParams();
            if (thumbRatio > imageViewRatio) {
                // Zoom out to fit width
                layoutParams.width = drawRegion.width();
                layoutParams.height = (int) (drawRegion.width() / thumbRatio);
                // Zoom in back fit height
                layoutParams.width = layoutParams.width * drawRegion.height() / layoutParams.height;
                layoutParams.height = drawRegion.height();
            } else if (thumbRatio < imageViewRatio) {
                // Zoom out to fit height
                layoutParams.width = (int) (drawRegion.height() * thumbRatio);
                layoutParams.height = drawRegion.height();
                // Zoom in back fit width
                layoutParams.height = layoutParams.height * drawRegion.width() / layoutParams.width;
                layoutParams.width = drawRegion.width();
            } else {
                layoutParams.width = drawRegion.width();
                layoutParams.height = drawRegion.height();
            }
            animatingImageView.setLayoutParams(layoutParams);

            int clipTop = 0;
            int clipBottom = 0;
            int clipHorizontal = 0;
            int clipVertical = 0;
            if (drawRegion.bottom > AndroidUtilities.displaySize.y) {
                clipBottom = drawRegion.bottom - AndroidUtilities.displaySizePixel.y + object.clipBottomAddition;
            }
            if (drawRegion.top < (statusBarHeight + object.clipTopAddition)) {
                clipTop = -drawRegion.top + statusBarHeight + object.clipTopAddition;
            }
            if (thumbRatio > imageViewRatio) {
                clipHorizontal = (layoutParams.width - drawRegion.width()) / 2;
            } else if (thumbRatio < imageViewRatio) {
                clipVertical = (layoutParams.height - drawRegion.height()) / 2;
            }
            clipTop += clipVertical;
            clipBottom += clipVertical;

            animationValues[0][0] = animatingImageView.getScaleX();
            animationValues[0][1] = animatingImageView.getScaleY();
            animationValues[0][2] = animatingImageView.getTranslationX();
            animationValues[0][3] = animatingImageView.getTranslationY();
            animationValues[0][4] = 0;
            animationValues[0][5] = 0;
            animationValues[0][6] = 0;
            animationValues[0][7] = 0;

            animationValues[1][0] = object.scale;
            animationValues[1][1] = object.scale;
            animationValues[1][2] = drawRegion.left + (drawRegion.width() - layoutParams.width) / 2;
            animationValues[1][3] = drawRegion.top - (useOccupyStatusBar ? statusBarHeight : 0) + (drawRegion.height() - layoutParams.height) / 2;
            animationValues[1][4] = clipHorizontal * object.scale;
            animationValues[1][5] = clipTop * object.scale;
            animationValues[1][6] = clipBottom * object.scale;
            animationValues[1][7] = object.radius;

            animatingImageView.setAnimationProgress(0);

            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(animatingImageView, "animationProgress", 0.0f, 1.0f),
//                    ObjectAnimator.ofInt(backgroundDrawable, "alpha", backgroundDrawable.getAlpha(), 0)
                    ObjectAnimator.ofInt(backgroundDrawable, "alpha", 0)
            );
        }
        else {
            int h = (AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
            animatorSet.playTogether(
                    ObjectAnimator.ofInt(backgroundDrawable, "alpha", 0),
                    ObjectAnimator.ofFloat(animatingImageView, "alpha", 0.0f),
                    ObjectAnimator.ofFloat(animatingImageView, "translationY", (animatingImageView.getTranslationY()) >= 0 ? h : -h)
            );

        }

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mListener.onPostShrunk();
            }
        });

        animatorSet.setDuration(mAnimDuration);
        animatorSet.start();
    }

    public void onOrientationChanged() {
        ViewGroup.LayoutParams params = animatingImageView.getLayoutParams();

        float scaleX = (float) AndroidUtilities.displaySizePixel.x / params.width;
        float scaleY = (float) (AndroidUtilities.displaySizePixel.y - (useOccupyStatusBar ? statusBarHeight : 0)) / params.height;
        float scale = scaleX > scaleY ? scaleY : scaleX;
        float width = params.width * scale;        //width  of full image
        float height = params.height * scale;      //height of full image
        float xPos = (AndroidUtilities.displaySizePixel.x - width) / 2.0f;
        float yPos = ((AndroidUtilities.displaySizePixel.y - (useOccupyStatusBar ? statusBarHeight : 0)) - height) / 2.0f;

        animatingImageView.setTranslationX(xPos);
        animatingImageView.setTranslationY(yPos);
        animatingImageView.setScaleX(scale);
        animatingImageView.setScaleY(scale);
    }
}
