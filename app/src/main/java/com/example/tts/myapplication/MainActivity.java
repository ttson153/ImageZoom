package com.example.tts.myapplication;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import static android.R.attr.orientation;

public class MainActivity extends AppCompatActivity {

    ClippingImageView animatingImageView;
    FrameLayout main;
    float[][] animationValues = new float[2][8];

    boolean isStatusBar = (Build.VERSION.SDK_INT >= 21);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());
        main = (FrameLayout) findViewById(R.id.activity_main);

//        AvatarDrawable avatarDrawable = new AvatarDrawable();
//        BackupImageView avatarImage = (BackupImageView) findViewById(R.id.avatar_image);
//        avatarImage.setRoundRadius(AndroidUtilities.dp(16));
//        avatarImage.setPivotX(0);
//        avatarImage.setPivotY(0);
//        avatarImage.setImage("/storage/emulated/0/test.png", avatarDrawable);
        animatingImageView = new ClippingImageView(this);
        animatingImageView.setAnimationValues(animationValues);
        ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        animatingImageView.setLayoutParams(param);

        animatingImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.thumb));
        animatingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPhoto(new Rect(0,0,376,211), BitmapFactory.decodeResource(getResources(), R.drawable.full));
            }
        });

        main.addView(animatingImageView, new FrameLayout.LayoutParams(376, 211));
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
            radius = 5;
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.thumb);
        }
    }

    private void openPhoto(Rect drawRegion, Bitmap fullImage) {
        //Rect drawRegion = new Rect(139,11,517,223);
        PlaceHolder object = new PlaceHolder();

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
        animatingImageView.setTranslationX(object.viewX + drawRegion.left * object.scale);
        animatingImageView.setTranslationY(object.viewY + drawRegion.top * object.scale);
        final ViewGroup.LayoutParams layoutParams = animatingImageView.getLayoutParams();
        layoutParams.width = (drawRegion.right - drawRegion.left);
        //layoutParams.width = 380;
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
        main.getLocationInWindow(coords2);
        int clipTop = coords2[1] - (isStatusBar ? 0 : AndroidUtilities.statusBarHeight) - (object.viewY + drawRegion.top) + object.clipTopAddition;
        if (clipTop < 0) {
            clipTop = 0;
        }
        int clipBottom = (object.viewY + drawRegion.top + layoutParams.height) - (coords2[1] + main.getHeight() - (isStatusBar ? 0 : AndroidUtilities.statusBarHeight)) + object.clipBottomAddition;
        if (clipBottom < 0) {
            clipBottom = 0;
        }
//        clipTop = Math.max(clipTop, clipVertical);
//        clipBottom = Math.max(clipBottom, clipVertical);

        animationValues[0][0] = animatingImageView.getScaleX();
        animationValues[0][1] = animatingImageView.getScaleY();
        animationValues[0][2] = animatingImageView.getTranslationX();
        animationValues[0][3] = animatingImageView.getTranslationY();
//        animationValues[0][4] = clipHorizontal * object.scale;
        animationValues[0][5] = clipTop * object.scale;
        animationValues[0][6] = clipBottom * object.scale;
        animationValues[0][7] = animatingImageView.getRadius();

        Log.d("MyLog",  "Draw Region " + drawRegion.left + " " + drawRegion.top + " " + drawRegion.right + " " + drawRegion.bottom + " ");
        Log.d("MyLog",  "layoutParam " + layoutParams.width + " " + layoutParams.height);
        animationValues[1][0] = scale;
        animationValues[1][1] = scale;
        animationValues[1][2] = xPos;
        //animationValues[1][3] = yPos;
        animationValues[1][3] = yPos;
        animationValues[1][4] = 0;
        animationValues[1][5] = 0;
        animationValues[1][6] = 0;
        animationValues[1][7] = 0;

        animatingImageView.setAnimationProgress(0);
//        backgroundDrawable.setAlpha(0);
//        containerView.setAlpha(0);
//        main.setAlpha(0);

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(animatingImageView, "animationProgress", 0.0f, 1.0f)
//                ObjectAnimator.ofInt(backgroundDrawable, "alpha", 0, 255),
//                ObjectAnimator.ofFloat(main, "alpha", 0.0f, 1.0f)
        );
        animatorSet.setDuration(1500);
        animatorSet.start();

        //animatingImageView.setImageBitmap(fullImage);
    }
}
