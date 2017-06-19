package com.tts.libimagezoomanim;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import static android.view.View.GONE;

/**
 * Created by tts on 4/11/17.
 * Use to calculate required info and pass to animation layout to animate
 */

public class AnimationController implements AnimationLayout.OnEventListener{
    private View currentAnimationView;
    private int[] imgViewLocation = new int[2];
    private Rect drawRegion;
    private TransformData currentPlaceHolder;

    private Context mContext;
    private AnimationLayout mAnimationLayout;

    public AnimationController(Context context) {
        mContext = context;
    }

    public void setAnimationLayout(AnimationLayout animationLayout) {
        mAnimationLayout = animationLayout;
    }

    /** Call to update current data to provide the correct close animation
     * @param data
     */
    public void updateCurrentInfo(View view, TransformData data) {
        currentAnimationView = view;
        currentPlaceHolder = data;
    }

    public void openPhoto(View view, TransformData data) {
        currentAnimationView = view;
        currentAnimationView.getLocationInWindow(imgViewLocation);
        int left = imgViewLocation[0];
        int top = imgViewLocation[1];
        int right = left + currentAnimationView.getWidth();
        int bottom = top + currentAnimationView.getHeight();
        Log.d("Loc ", imgViewLocation[0] + " " + imgViewLocation[1] + " " + right + " " + bottom);

        drawRegion = new Rect(left, top, right, bottom);
        currentPlaceHolder = data;
        mAnimationLayout.expand(drawRegion, currentPlaceHolder);
    }

    public void closePhoto() {
        if (mAnimationLayout.isExpanded()) {
            currentAnimationView.getLocationInWindow(imgViewLocation);
            int left = imgViewLocation[0];
            int top = imgViewLocation[1];
            int right = left + currentAnimationView.getWidth();
            int bottom = top + currentAnimationView.getHeight();
            drawRegion = new Rect(left, top, right, bottom);

            mAnimationLayout.shrink(drawRegion, currentPlaceHolder);
        }
    }

    @Override
    public void onPreExpanded() {
        currentAnimationView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPostExpanded() {
        mAnimationLayout.onOrientationChanged();
    }

    @Override
    public void onPreShrunk() {

    }

    @Override
    public void onPostShrunk() {
        currentAnimationView.setVisibility(View.VISIBLE);
        mAnimationLayout.getAnimatingImageView().setVisibility(GONE);
        mAnimationLayout.onOrientationChanged();
    }
}
