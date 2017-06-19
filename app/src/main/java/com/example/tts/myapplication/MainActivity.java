package com.example.tts.myapplication;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tts.libimagezoomanim.AndroidUtilities;
import com.tts.libimagezoomanim.AnimationController;
import com.tts.libimagezoomanim.AnimationLayout;
import com.tts.libimagezoomanim.TransformData;

public class MainActivity extends AppCompatActivity {

    private int[] imageId = {
            R.drawable.rsz_20160731_101852,
            R.drawable.rsz_20160820_083712,
            R.drawable.rsz_20160820_083749,
            R.drawable.rsz_20160820_084236,
            R.drawable.square,
            R.drawable.pano_portrait,
            R.drawable.pano_landscape,
    };

    public static final String LIST_STATE_KEY = "recycler_list_state";
    Parcelable mListState;

    public static final int clipBottomAddition = AndroidUtilities.dp(0);
    public static final int clipTopAddition    = AndroidUtilities.dp(56);

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerViewAdapter adapter;

    private AnimationLayout animationLayout;
    private AnimationController animationController;

    private View getViewAtPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
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

        // RecyclerView
        adapter = new RecyclerViewAdapter(this, imageId);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(adapter);

        // Animation Layout
        animationLayout = new AnimationLayout(this);

        // Animation Controller
        animationController = new AnimationController(this);
        animationController.setAnimationLayout(animationLayout);
        animationLayout.setOnEventListener(animationController);

        // Image swipe processor
        FullScreenImageTouchListener animatingImageViewTouchListener = new FullScreenImageTouchListener();
        animatingImageViewTouchListener.setAnimationLayout(animationLayout);
        animatingImageViewTouchListener.setAnimationController(animationController);
        animationLayout.setOnTouchListener(animatingImageViewTouchListener);

        ((FrameLayout) findViewById(R.id.main_frame_layout))
                .addView(animationLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                View imgView = ((FrameLayout) view).getChildAt(0);

                // generate data for animation
                //TODO more efficient way to access image view's bitmap
                Bitmap bm = BitmapFactory.decodeResource(getResources(), imageId[position]);
                ViewGroup.LayoutParams params = imgView.getLayoutParams();
                TransformData data = new TransformData()
                        .setThumbImage(bm)
                        .setClipTopAddition(clipTopAddition)
                        .setClipBottomAddition(clipBottomAddition);
                if (position == RecyclerViewAdapter.CIRCLE_IMAGE_POSITION) {
                    data.setRadius(params.width / 2);
                }

                animationController.openPhoto(imgView, data);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onBackPressed() {
        if (animationLayout.isExpanded()) {
            animationController.closePhoto();
        } else {
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AndroidUtilities.checkDisplaySize(this, newConfig);
        adapter.notifyDataSetChanged();
        animationLayout.onOrientationChanged();
    }
}
