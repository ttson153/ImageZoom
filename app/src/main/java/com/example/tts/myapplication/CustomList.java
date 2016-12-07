package com.example.tts.myapplication;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

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
        FrameLayout cell = new FrameLayout(MyApplication.getAppContext());
        ImageView imageView = new ImageView(MyApplication.getAppContext());
        imageView.setTranslationX(AndroidUtilities.dp(100));
        imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), imageId[position]));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(AndroidUtilities.dp(250), AndroidUtilities.dp(140));
        params.topMargin = 5; params.bottomMargin = 5;
        cell.addView(imageView, params);

        return cell;
    }

    @Override
    public int getCount() {
        return imageId.length;
    }
}
