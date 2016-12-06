package com.example.tts.myapplication;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

/**
 * Created by tts on 11/30/16.
 */

public class CustomList extends ArrayAdapter<String> {
    private final Activity context;
    private final Integer[] imageId;
    public CustomList(Activity context, Integer[] imageId) {
        super(context, R.layout.list_item);
        this.context = context;
        this.imageId = imageId;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        FrameLayout row = new FrameLayout(MyApplication.getAppContext());
//        row.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        row.layout(0, 0, 540, 960);
        ClippingImageView animatingImgView = new ClippingImageView(MyApplication.getAppContext());
        animatingImgView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), imageId[position]));
        animatingImgView.setRadius(5);
        animatingImgView.setLeft(0);
        animatingImgView.setTop(0);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(300, 170);
        params.topMargin = 5; params.bottomMargin = 5;
//        row.setBackgroundColor(Color.argb(0, 255, 255, 255));
//        row.getBackground().setAlpha(128);
//        animatingImgView.setAlpha(1);
        row.addView(animatingImgView, params);

        return row;
    }

    @Override
    public int getCount() {
        return imageId.length;
    }
}
