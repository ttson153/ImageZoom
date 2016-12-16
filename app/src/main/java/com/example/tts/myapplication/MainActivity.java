package com.example.tts.myapplication;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements AnimationLayout.OnEventListener {

    private Integer[] imageId = {
            R.drawable.rsz_20160731_101852,
            R.drawable.rsz_20160820_083712,
            R.drawable.rsz_20160820_083749,
            R.drawable.rsz_20160820_084236,
            R.drawable.rsz_20160820_121959,
            R.drawable.square,
            R.drawable.fourbythree,
    };

    private ListView list;
    private ImageView currentImageView;
    private AnimationLayout animationLayout;
    private Rect drawRegion;
    private TransformData currentPlaceHolder;
    private int clipTopAddition = AndroidUtilities.dp(56);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 21) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) (findViewById(R.id.header)).getLayoutParams();
            params.height += AndroidUtilities.statusBarHeight;
            (findViewById(R.id.header)).setLayoutParams(params);
            (findViewById(R.id.header)).setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
        }

        animationLayout = new AnimationLayout(this);
        animationLayout.setOnEventListener(this);
        ((FrameLayout) findViewById(R.id.main_frame_layout))
                .addView(animationLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        final int[] imgViewLocation = new int[2];
        ListViewAdapter adapter = new ListViewAdapter(this, imageId);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentImageView = (ImageView) ((FrameLayout) view).getChildAt(0);
                currentImageView.getLocationInWindow(imgViewLocation);
                int left = imgViewLocation[0];
                int top = imgViewLocation[1];
                int right = left + currentImageView.getWidth();
                int bottom = top + currentImageView.getHeight();
                Log.d("Loc ", imgViewLocation[0] + " " + imgViewLocation[1] + " " + right + " " + bottom);

                currentImageView.buildDrawingCache();
                Bitmap bm = Bitmap.createBitmap(currentImageView.getDrawingCache());
                currentImageView.destroyDrawingCache();

                ViewGroup.LayoutParams params = currentImageView.getLayoutParams();

                drawRegion = new Rect(left, top, right, bottom);
                if (position == 5) {
                    currentPlaceHolder = new TransformData().setThumbImage(bm).setRadius(params.width / 2).setClipTopAddition(clipTopAddition);
                }
                else {
                    currentPlaceHolder = new TransformData().setThumbImage(bm).setClipTopAddition(clipTopAddition);
                }

                animationLayout.expand(drawRegion, currentPlaceHolder);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (animationLayout.isExpanded()) {
            animationLayout.shrink(drawRegion, currentPlaceHolder);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPreExpanded() {
        currentImageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPostExpanded() {
//        animationLayout.getAnimatingImageView().setTranslationY(animationLayout.getAnimatingImageView().getTranslationY() - 150);
    }

    @Override
    public void onPreShrunk() {

    }

    @Override
    public void onPostShrunk() {
        currentImageView.setVisibility(View.VISIBLE);
        animationLayout.getAnimatingImageView().setVisibility(View.GONE);
    }
}
