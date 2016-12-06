package com.example.tts.myapplication;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import static android.R.attr.orientation;

public class MainActivity extends AppCompatActivity {
    Integer[] imageId = {
            R.drawable.rsz_20160731_101852,
            R.drawable.rsz_20160820_083712,
            R.drawable.rsz_20160820_083749,
            R.drawable.rsz_20160820_084236,
            R.drawable.rsz_20160820_121959,
    };

    ClippingImageView animatingImageView;
    FrameLayout main;
    ListView list;
    float[][] animationValues = new float[2][8];

    private ColorDrawable backgroundDrawable = new ColorDrawable(0xff000000);

    boolean isStatusBar = (Build.VERSION.SDK_INT >= 21);
    private boolean zoomIn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//         CustomList adapter = new CustomList(MainActivity.this, imageId);
//         list = (ListView) findViewById(R.id.list);
//         list.setAdapter(adapter);
//         list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//             @Override
//             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                 FrameLayout row = (FrameLayout) view;
//                 ClippingImageView animatingImageView = (ClippingImageView) row.getChildAt(0);
//                 Toast.makeText(MainActivity.this, "" + animatingImageView.getLeft() + " " + animatingImageView.getTop(), Toast.LENGTH_LONG).show();
//                 openPhoto(drawRegion, animatingImageView, row, null);
//             }
//         });


        AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());
        main = (FrameLayout) findViewById(R.id.activity_main);
        backgroundDrawable.setAlpha(0);
        main.setBackgroundDrawable(backgroundDrawable);
        animatingImageView = new ClippingImageView(this);
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.rsz_20160731_101852);
        animatingImageView.setImageBitmap(thumb);

        main.addView(animatingImageView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        animatingImageView.setTranslationX(AndroidUtilities.dp(100));
        animatingImageView.setTranslationY(AndroidUtilities.dp(-100));
        ViewGroup.LayoutParams params = animatingImageView.getLayoutParams();
        params.width = AndroidUtilities.dp(320);
        params.height = AndroidUtilities.dp(180);
        animatingImageView.setLayoutParams(params);
        //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        animatingImageView.invalidate();

        final Rect drawRegion = new Rect((int) animatingImageView.getTranslationX(), (int) animatingImageView.getTranslationY(),
                (int) animatingImageView.getTranslationX() + animatingImageView.getWidth(), (int) animatingImageView.getTranslationY() + animatingImageView.getHeight());

        animatingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPhoto(new Rect((int) animatingImageView.getTranslationX(), (int) animatingImageView.getTranslationY(),
                        (int) animatingImageView.getTranslationX() + animatingImageView.getWidth(), (int) animatingImageView.getTranslationY() + animatingImageView.getHeight()), animatingImageView, main, null);
            }
        });

        Log.d("MyLog", "Screen Pixels: " + AndroidUtilities.displaySizePixel.x + " " + AndroidUtilities.displaySizePixel.y);
        Log.d("MyLog", "ImageView: " + animatingImageView.getLeft() + " " + animatingImageView.getTop() + " " + animatingImageView.getWidth() + " " + animatingImageView.getHeight());
    }

    public class PlaceHolder {
        public int viewX;
        public int viewY;
        public Bitmap thumb;
        public int dialogId;
        public int index;
        public int size;
        public int radius;
        public int clipBottomAddition;
        public int clipTopAddition;
        public float scale = 1.0f;

        PlaceHolder() {
            viewX = 0; viewY = 372;
            radius = AndroidUtilities.dp(50);
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.rsz_20160731_101852);
        }
    }

    private void openPhoto(Rect drawRegion, ClippingImageView animatingImageView, FrameLayout parent, Bitmap fullImage) {
        //Rect drawRegion = new Rect(139,11,517,223);
        zoomIn = true;
        PlaceHolder object = new PlaceHolder();

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
        float scaleY = (float) (AndroidUtilities.displaySize.y + (isStatusBar ? AndroidUtilities.statusBarHeight : 0)) / layoutParams.height;
        float scale = scaleX > scaleY ? scaleY : scaleX;
        float width = layoutParams.width * scale;        //width  of full image
        float height = layoutParams.height * scale;      //height of full image
        float xPos = (AndroidUtilities.displaySize.x - width) / 2.0f;
        float yPos = ((AndroidUtilities.displaySize.y + (isStatusBar ? AndroidUtilities.statusBarHeight : 0)) - height) / 2.0f;
//        int clipHorizontal = Math.abs(drawRegion.left - object.imageReceiver.getImageX());
//        int clipVertical = Math.abs(drawRegion.top - object.imageReceiver.getImageY());

        int coords2[] = new int[2];
        parent.getLocationInWindow(coords2);
        int clipTop = coords2[1] - (isStatusBar ? 0 : AndroidUtilities.statusBarHeight) - (object.viewY + drawRegion.top) + object.clipTopAddition;
        if (clipTop < 0) {
            clipTop = 0;
        }
        int clipBottom = (object.viewY + drawRegion.top + layoutParams.height) - (coords2[1] + parent.getHeight() - (isStatusBar ? 0 : AndroidUtilities.statusBarHeight)) + object.clipBottomAddition;
        if (clipBottom < 0) {
            clipBottom = 0;
        }
        clipBottom = 0;
//        clipTop = Math.max(clipTop, clipVertical);
//        clipBottom = Math.max(clipBottom, clipVertical);

        if (drawRegion.bottom > AndroidUtilities.displaySize.y) {
            clipBottom = drawRegion.bottom - AndroidUtilities.displaySize.y;
        }
        if (drawRegion.top < 0) {
            clipTop = -drawRegion.top;
        }

        animationValues[0][0] = animatingImageView.getScaleX();
        animationValues[0][1] = animatingImageView.getScaleY();
        animationValues[0][2] = animatingImageView.getTranslationX();
        animationValues[0][3] = animatingImageView.getTranslationY();
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
        animatorSet.setDuration(2000);
        animatorSet.start();

        //animatingImageView.setImageBitmap(fullImage);
    }

    @Override
    public void onBackPressed() {
        if (zoomIn) {
            zoomIn = false;
            float temp;
            for (int i = 0; i < 8; i++) {
                temp = animationValues[0][i];
                animationValues[0][i] = animationValues[1][i];
                animationValues[1][i] = temp;
            }
            animatingImageView.setAnimationProgress(0);
//        backgroundDrawable.setAlpha(0);
//        containerView.setAlpha(0);
//        parent.setAlpha(0);

            final AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(animatingImageView, "animationProgress", 0.0f, 1.0f),
                    ObjectAnimator.ofInt(backgroundDrawable, "alpha", 255, 0)
//                ObjectAnimator.ofFloat(parent, "alpha", 0.0f, 1.0f)
            );
            animatorSet.setDuration(1500);
            animatorSet.start();
        }
    }
}
