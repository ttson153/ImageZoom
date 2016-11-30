package com.example.tts.myapplication;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
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

//        View rowView= inflater.inflate(R.layout.list_item, null, true);
//        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
//        imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), imageId[position]));
//        return rowView;

        FrameLayout row = new FrameLayout(MyApplication.getAppContext());
        ClippingImageView animatingImgView = new ClippingImageView(MyApplication.getAppContext());
        animatingImgView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), imageId[position]));
        animatingImgView.setRadius(5);
        animatingImgView.setLeft(0);
        animatingImgView.setTop(0);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(300, 170);
        params.gravity = Gravity.TOP|Gravity.RIGHT;
        params.topMargin = 5; params.bottomMargin = 5;
        row.setBackgroundColor(Color.argb(0, 255, 255, 255));
//        row.getBackground().setAlpha(128);
        animatingImgView.setAlpha(1);
        row.addView(animatingImgView, params);

        return row;
    }

    @Override
    public int getCount() {
        return imageId.length;
    }
}
