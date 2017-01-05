package com.example.tts.myapplication;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements AnimationLayout.OnEventListener {

    private Integer[] imageId = {
            R.drawable.rsz_20160731_101852,
            R.drawable.rsz_20160820_083712,
            R.drawable.rsz_20160820_083749,
            R.drawable.rsz_20160820_084236,
            R.drawable.square,
            R.drawable.pano_portrait,
            R.drawable.pano_landscape,
    };

    final float DELTA_Y_THRESH = AndroidUtilities.dp(150);
    float startingY, deltaY, animatingImageViewOriginY;

    private View.OnTouchListener animatingImageViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (animationLayout.isExpanded()) {
                switch (motionEvent.getAction()) {
                    case ACTION_DOWN:
                        startingY = motionEvent.getY();
                        animatingImageViewOriginY = animationLayout.getAnimatingImageView().getTranslationY();
                        break;
                    case ACTION_MOVE:
                        deltaY = motionEvent.getY() - startingY;
                        animationLayout.getAnimatingImageView().setTranslationY(animatingImageViewOriginY + deltaY);
                        animationLayout.getBackgroundDrawable().setAlpha(255 - Math.abs(255 * (int) deltaY / AndroidUtilities.displaySizePixel.y));
                        break;
                    case ACTION_UP:
                        //TODO shrink images
                        if (Math.abs(motionEvent.getY() - startingY) > DELTA_Y_THRESH) {
                            closePhoto();
                        }
                        else {
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.playTogether(
                                    ObjectAnimator.ofFloat(animationLayout.getAnimatingImageView(), "translationY", animatingImageViewOriginY),
                                    ObjectAnimator.ofInt(animationLayout.getBackgroundDrawable(), "alpha", 255));
                            animatorSet.setDuration(200).start();
                        }
                        break;
                }
                return true;
            } else {
                return false;
            }
        }
    };


    public static final String LIST_STATE_KEY = "recycler_list_state";
    Parcelable mListState;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerViewAdapter adapter;

    private ImageView currentImageView;
    private int currentPosition;
    private int[] imgViewLocation = new int[2];
    private AnimationLayout animationLayout;
    private Rect drawRegion;
    private TransformData currentPlaceHolder;
    private int clipTopAddition = AndroidUtilities.dp(56);
    private int clipBottomAddition = AndroidUtilities.dp(0);

    private View getViewAtPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
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

        Bitmap bm = BitmapFactory.decodeResource(getResources(), imageId[position]);

        ViewGroup.LayoutParams params = currentImageView.getLayoutParams();

        drawRegion = new Rect(left, top, right, bottom);
        if (position == 4) {
            currentPlaceHolder = new TransformData()
                    .setThumbImage(bm)
                    .setRadius(params.width / 2)
                    .setClipTopAddition(clipTopAddition)
                    .setClipBottomAddition(clipBottomAddition);
        }
        else {
            currentPlaceHolder = new TransformData()
                    .setThumbImage(bm)
                    .setClipTopAddition(clipTopAddition)
                    .setClipBottomAddition(clipBottomAddition);
        }

        animationLayout.expand(drawRegion, currentPlaceHolder);
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

            Bitmap bm = BitmapFactory.decodeResource(getResources(), imageId[currentPosition]);

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
        }
        else {
            drawRegion = null;
            currentPlaceHolder = null;
        }

        animationLayout.shrink(drawRegion, currentPlaceHolder);
    }

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
        animationLayout.setOnTouchListener(animatingImageViewTouchListener);
        ((FrameLayout) findViewById(R.id.main_frame_layout))
                .addView(animationLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        adapter = new RecyclerViewAdapter(this, imageId);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
//        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                openPhoto(view, position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onBackPressed() {
        if (animationLayout.isExpanded()) {
            closePhoto();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        // Save list state
        mListState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, mListState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
        }
    }

    @Override
    public void onPreExpanded() {
        currentImageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPostExpanded() {
//        animationLayout.getAnimatingImageView().setTranslationY(animationLayout.getAnimatingImageView().getTranslationY() - 150);
        animationLayout.onOrientationChanged();
    }

    @Override
    public void onPreShrunk() {

    }

    @Override
    public void onPostShrunk() {
        currentImageView.setVisibility(View.VISIBLE);
        animationLayout.getAnimatingImageView().setVisibility(GONE);
        animationLayout.onOrientationChanged();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AndroidUtilities.checkDisplaySize(this, newConfig);
        adapter.notifyDataSetChanged();
        animationLayout.onOrientationChanged();
    }
}
