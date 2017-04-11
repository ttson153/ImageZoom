package com.example.tts.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import static android.view.View.GONE;

/**
 * Created by tts on 4/11/17.
 * Use to calculate required info and pass to animation layout to animate
 */

public class AnimationController implements AnimationLayout.OnEventListener{
    private ImageView currentImageView;
    private int currentPosition;
    private int[] imgViewLocation = new int[2];
    private Rect drawRegion;
    private TransformData currentPlaceHolder;
    private int clipTopAddition = AndroidUtilities.dp(56);
    private int clipBottomAddition = AndroidUtilities.dp(0);

    private int[]   mImageId;
    private Context mContext;
    private AnimationLayout mAnimationLayout;
    private RecyclerView    mRecyclerView;

    public AnimationController(Context context) {
        mContext = context;
    }

    public void setInfo(AnimationLayout animationLayout, RecyclerView recyclerView, int[] imageId) {
        mAnimationLayout = animationLayout;
        mRecyclerView    = recyclerView;
        mImageId         = imageId;
    }

    public void openPhoto(View view, int position) {
        currentPosition = position;
        currentImageView = (ImageView) ((FrameLayout) view).getChildAt(0);
        currentImageView.getLocationInWindow(imgViewLocation);
        int left = imgViewLocation[0];
        int top = imgViewLocation[1];
        int right = left + currentImageView.getWidth();
        int bottom = top + currentImageView.getHeight();
        Log.d("Loc ", imgViewLocation[0] + " " + imgViewLocation[1] + " " + right + " " + bottom);

        Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), mImageId[position]);

        ViewGroup.LayoutParams params = currentImageView.getLayoutParams();

        drawRegion = new Rect(left, top, right, bottom);
        if (position == 4) {
            currentPlaceHolder = new TransformData()
                    .setThumbImage(bm)
                    .setRadius(params.width / 2)
                    .setClipTopAddition(clipTopAddition)
                    .setClipBottomAddition(clipBottomAddition);
        } else {
            currentPlaceHolder = new TransformData()
                    .setThumbImage(bm)
                    .setClipTopAddition(clipTopAddition)
                    .setClipBottomAddition(clipBottomAddition);
        }

        mAnimationLayout.expand(drawRegion, currentPlaceHolder);
    }

    public void closePhoto() {
        RecyclerViewAdapter.ViewHolder curr = (RecyclerViewAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(currentPosition);
        if (curr != null) {
            currentImageView = curr.mImageView;
            currentImageView.getLocationInWindow(imgViewLocation);
            int left = imgViewLocation[0];
            int top = imgViewLocation[1];
            int right = left + currentImageView.getWidth();
            int bottom = top + currentImageView.getHeight();
            drawRegion = new Rect(left, top, right, bottom);

            Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), mImageId[currentPosition]);

            ViewGroup.LayoutParams params = currentImageView.getLayoutParams();

            if (currentPosition == 4) {
                currentPlaceHolder = new TransformData()
                        .setThumbImage(bm)
                        .setRadius(params.width / 2)
                        .setClipTopAddition(clipTopAddition)
                        .setClipBottomAddition(clipBottomAddition);
            } else {
                currentPlaceHolder = new TransformData()
                        .setThumbImage(bm)
                        .setClipTopAddition(clipTopAddition)
                        .setClipBottomAddition(clipBottomAddition);
            }
//                currentImageView.setVisibility(View.INVISIBLE);
        } else {
            drawRegion = null;
            currentPlaceHolder = null;
        }

        mAnimationLayout.shrink(drawRegion, currentPlaceHolder);
    }

    @Override
    public void onPreExpanded() {
        currentImageView.setVisibility(View.INVISIBLE);
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
        currentImageView.setVisibility(View.VISIBLE);
        mAnimationLayout.getAnimatingImageView().setVisibility(GONE);
        mAnimationLayout.onOrientationChanged();
    }
}
