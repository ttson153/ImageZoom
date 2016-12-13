package com.example.tts.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import static android.R.attr.orientation;

public class MainActivity extends AppCompatActivity {
    Integer[] imageId = {
            R.drawable.rsz_20160731_101852,
            R.drawable.rsz_20160820_083712,
            R.drawable.rsz_20160820_083749,
            R.drawable.rsz_20160820_084236,
            R.drawable.rsz_20160820_121959,
            R.drawable.square,
            R.drawable.fourbythree,
    };

    FrameLayout animatingLayout;
    ImageView stillImageView;
    Rect drawRegion;
    ClippingImageView animatingImageView;
    PlaceHolder currentPlaceHolder;
    FrameLayout main;
    ListView list;
    float[][] animationValues = new float[2][8];

    private ColorDrawable backgroundDrawable = new ColorDrawable(0xff000000);

//    boolean isStatusBar = (Build.VERSION.SDK_INT >= 21);
    boolean isStatusBar = false;

    int statusBarHeight;

    private boolean zoomIn = false;
    private long animatingDuration = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusBarHeight = AndroidUtilities.statusBarHeight;

        animatingLayout = (FrameLayout) findViewById(R.id.animation_layout);
        animatingImageView = new ClippingImageView(this);
        backgroundDrawable.setAlpha(0);
        animatingLayout.setBackgroundDrawable(backgroundDrawable);
        animatingLayout.addView(animatingImageView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        final int[] imgViewLocation = new int[2];
        ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, imageId);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stillImageView = (ImageView) ((FrameLayout) view).getChildAt(0);
                stillImageView.getLocationInWindow(imgViewLocation);
                int left = imgViewLocation[0];
                int top = imgViewLocation[1];
                int right = left + stillImageView.getWidth();
                int bottom = top + stillImageView.getHeight();
                Log.d("Loc ", imgViewLocation[0] + " " + imgViewLocation[1] + " " + right + " " + bottom);

                stillImageView.buildDrawingCache();
                Bitmap bm = Bitmap.createBitmap(stillImageView.getDrawingCache());
                stillImageView.destroyDrawingCache();

                animatingImageView.setTranslationX(imgViewLocation[0]);
                animatingImageView.setTranslationY(imgViewLocation[1]);
                ViewGroup.LayoutParams params = animatingImageView.getLayoutParams();
                params.width = stillImageView.getWidth();
                params.height = stillImageView.getHeight();
                animatingImageView.setLayoutParams(params);

                drawRegion = new Rect(left, top, right, bottom);
                if (position == 5) {
                    currentPlaceHolder = new PlaceHolder(bm, params.width / 2);
                }
                else {
                    currentPlaceHolder = new PlaceHolder(bm, AndroidUtilities.dp(2));
                }

                openPhoto(drawRegion, animatingImageView, animatingLayout, null, currentPlaceHolder);
            }
        });
    }

    public class PlaceHolder {
        public int viewX;
        public int viewY;
        public Bitmap thumb;
        public int size;
        public int radius;
        public int clipBottomAddition;
        public int clipTopAddition;
        public float scale = 1.0f;

        PlaceHolder(Bitmap bitmap, int radius) {
//            viewX = 0; viewY = 372;
            this.radius = radius;
            thumb = bitmap;
        }
    }

    private void openPhoto(Rect drawRegion, final ClippingImageView animatingImageView, FrameLayout parent, Bitmap fullImage, PlaceHolder object) {
        zoomIn = true;

        animatingImageView.setAnimationValues(animationValues);

        animatingImageView.setVisibility(View.VISIBLE);
        animatingImageView.setRadius(object.radius);
        animatingImageView.setOrientation(orientation);
        animatingImageView.setNeedRadius(object.radius != 0);
        animatingImageView.setImageBitmap(object.thumb);

        animatingImageView.setAlpha(1.0f);
        animatingImageView.setPivotX(0.0f);
        animatingImageView.setPivotY(0.0f);
        animatingImageView.setScaleX(object.scale);
        animatingImageView.setScaleY(object.scale);
//        animatingImageView.setTranslationX(object.viewX + drawRegion.left * object.scale);
//        animatingImageView.setTranslationY(object.viewY + drawRegion.top * object.scale);
        final ViewGroup.LayoutParams layoutParams = animatingImageView.getLayoutParams();
        layoutParams.width = (drawRegion.right - drawRegion.left);
        layoutParams.height = (drawRegion.bottom - drawRegion.top);
        animatingImageView.setLayoutParams(layoutParams);

        float scaleX = (float) AndroidUtilities.displaySize.x / layoutParams.width;
        float scaleY = (float) (AndroidUtilities.displaySize.y + (isStatusBar ? 0 : statusBarHeight)) / layoutParams.height;
        float scale = scaleX > scaleY ? scaleY : scaleX;
        float width = layoutParams.width * scale;        //width  of full image
        float height = layoutParams.height * scale;      //height of full image
        float xPos = (AndroidUtilities.displaySize.x - width) / 2.0f;
        float yPos = ((AndroidUtilities.displaySize.y + (isStatusBar ? 0 : statusBarHeight)) - height) / 2.0f;
//        int clipHorizontal = Math.abs(drawRegion.left - object.imageReceiver.getImageX());
//        int clipVertical = Math.abs(drawRegion.top - object.imageReceiver.getImageY());

        int coords2[] = new int[2];
        parent.getLocationInWindow(coords2);
        int clipTop = 0;
        int clipBottom = 0;
//        int clipTop = coords2[1] - (isStatusBar ? 0 : statusBarHeight) - (object.viewY + drawRegion.top) + object.clipTopAddition;
//        if (clipTop < 0) {
//            clipTop = 0;
//        }
//        int clipBottom = (object.viewY + drawRegion.top + layoutParams.height) - (coords2[1] + parent.getHeight() - (isStatusBar ? 0 : statusBarHeight)) + object.clipBottomAddition;
//        if (clipBottom < 0) {
//            clipBottom = 0;
//        }
//        clipBottom = 0;
//        clipTop = Math.max(clipTop, clipVertical);
//        clipBottom = Math.max(clipBottom, clipVertical);

        if (drawRegion.bottom > AndroidUtilities.displaySize.y) {
//            clipBottom = drawRegion.bottom - (AndroidUtilities.displaySize.y + (isStatusBar ? statusBarHeight : 0));
            clipBottom = drawRegion.bottom - (AndroidUtilities.displaySizePixel.y);
        }
        if (drawRegion.top < ((isStatusBar ? statusBarHeight : 0) + AndroidUtilities.dp(56))) {
            clipTop = -drawRegion.top + (isStatusBar ? statusBarHeight : 0) + AndroidUtilities.dp(56);
        }

        animationValues[0][0] = animatingImageView.getScaleX();
        animationValues[0][1] = animatingImageView.getScaleY();
        animationValues[0][2] = animatingImageView.getTranslationX();
        animationValues[0][3] = animatingImageView.getTranslationY() - (isStatusBar ? statusBarHeight : 0);
//        animationValues[0][2] = coords2[0] + animatingImageView.getLeft();
//        animationValues[0][3] = coords2[1] + animatingImageView.getTop();
//        animationValues[0][2] = animatingImageView.getLeft();
//        animationValues[0][3] = animatingImageView.getTop();
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
//        containerView.setAlpha(0);
//        parent.setAlpha(0);

        Log.d("MyLog", "ClipIVSize  " + animatingImageView.getLeft() + " " + animatingImageView.getTop() + " " +
                animatingImageView.getMeasuredWidth() + " " + animatingImageView.getMeasuredHeight() + " " +
                animatingImageView.getTranslationX() + " " + animatingImageView.getTranslationY() + " " +
                animatingImageView.getX() + " " + animatingImageView.getY() + " " +
                animatingImageView.getParent().toString());

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(animatingImageView, "animationProgress", 0.0f, 1.0f),
                ObjectAnimator.ofInt(backgroundDrawable, "alpha", 0, 255)
//                ObjectAnimator.ofFloat(parent, "alpha", 0.0f, 1.0f)
        );
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                stillImageView.setVisibility(View.INVISIBLE);
            }
        });

        animatorSet.setDuration(animatingDuration);
        animatorSet.start();

    }

    private void closePhoto(Rect drawRegion, final ClippingImageView animatingImageView, FrameLayout parent, Bitmap fullImage, PlaceHolder object) {
        zoomIn = false;

        final ViewGroup.LayoutParams layoutParams = animatingImageView.getLayoutParams();
        layoutParams.width = (drawRegion.right - drawRegion.left);
        layoutParams.height = (drawRegion.bottom - drawRegion.top);
        animatingImageView.setLayoutParams(layoutParams);

        float scaleX = (float) AndroidUtilities.displaySize.x / layoutParams.width;
        float scaleY = (float) (AndroidUtilities.displaySize.y + (isStatusBar ? 0 : statusBarHeight)) / layoutParams.height;
        float scale = scaleX > scaleY ? scaleY : scaleX;
        float width = layoutParams.width * scale;        //width  of full image
        float height = layoutParams.height * scale;      //height of full image
        float xPos = (AndroidUtilities.displaySize.x - width) / 2.0f;
        float yPos = ((AndroidUtilities.displaySize.y + (isStatusBar ? 0 : statusBarHeight)) - height) / 2.0f;
//        int clipHorizontal = Math.abs(drawRegion.left - object.imageReceiver.getImageX());
//        int clipVertical = Math.abs(drawRegion.top - object.imageReceiver.getImageY());

        int coords2[] = new int[2];
        parent.getLocationInWindow(coords2);
        int clipTop = 0;
        int clipBottom = 0;
//        int clipTop = coords2[1] - (isStatusBar ? 0 : statusBarHeight) - (object.viewY + drawRegion.top) + object.clipTopAddition;
//        if (clipTop < 0) {
//            clipTop = 0;
//        }
//        int clipBottom = (object.viewY + drawRegion.top + layoutParams.height) - (coords2[1] + parent.getHeight() - (isStatusBar ? 0 : statusBarHeight)) + object.clipBottomAddition;
//        if (clipBottom < 0) {
//            clipBottom = 0;
//        }
//        clipBottom = 0;
//        clipTop = Math.max(clipTop, clipVertical);
//        clipBottom = Math.max(clipBottom, clipVertical);

        if (drawRegion.bottom > AndroidUtilities.displaySize.y) {
            clipBottom = drawRegion.bottom - AndroidUtilities.displaySizePixel.y;
        }
        if (drawRegion.top < ((isStatusBar ? statusBarHeight : 0) + AndroidUtilities.dp(56))) {
            clipTop = -drawRegion.top + (isStatusBar ? statusBarHeight : 0) + AndroidUtilities.dp(56);
        }

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
        animationValues[1][2] = drawRegion.left;
        animationValues[1][3] = drawRegion.top - (isStatusBar ? statusBarHeight : 0);
//        animationValues[1][4] = clipHorizontal * object.scale;
        animationValues[1][5] = clipTop * object.scale;
        animationValues[1][6] = clipBottom * object.scale;
        animationValues[1][7] = object.radius;

        animatingImageView.setAnimationProgress(0);
//        backgroundDrawable.setAlpha(0);
//        containerView.setAlpha(0);

        Log.d("MyLog", "ClipIVSize  " + animatingImageView.getLeft() + " " + animatingImageView.getTop() + " " +
                animatingImageView.getMeasuredWidth() + " " + animatingImageView.getMeasuredHeight() + " " +
                animatingImageView.getTranslationX() + " " + animatingImageView.getTranslationY() + " " +
                animatingImageView.getX() + " " + animatingImageView.getY() + " " +
                animatingImageView.getParent().toString());

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(animatingImageView, "animationProgress", 0.0f, 1.0f),
                ObjectAnimator.ofInt(backgroundDrawable, "alpha", 255, 0)
//                ObjectAnimator.ofFloat(parent, "alpha", 0.0f, 1.0f)
        );
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatingImageView.setVisibility(View.GONE);
                stillImageView.setVisibility(View.VISIBLE);
            }
        });

        animatorSet.setDuration(animatingDuration);
        animatorSet.start();

    }

    @Override
    public void onBackPressed() {
        if (zoomIn) {
            closePhoto(drawRegion, animatingImageView, animatingLayout, null, currentPlaceHolder);
        }
        else {
            super.onBackPressed();
        }
    }
}
