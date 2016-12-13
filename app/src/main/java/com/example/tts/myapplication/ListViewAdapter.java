package com.example.tts.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
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

public class ListViewAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final Integer[] imageId;

    private int previewWidth = AndroidUtilities.dp(250);
    private int previewHeight = AndroidUtilities.dp(140);

    public ListViewAdapter(Activity context, Integer[] imageId) {
        super(context, R.layout.list_item);
        this.context = context;
        this.imageId = imageId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FrameLayout cell = new FrameLayout(MyApplication.getAppContext());
        ImageView imageView = new ImageView(MyApplication.getAppContext());

        Bitmap imgPreview = BitmapFactory.decodeResource(context.getResources(), imageId[position]);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(0, 0);

        // 5dp from right
        if (imgPreview.getWidth() > imgPreview.getHeight()) {
            // landscape
            imageView.setTranslationX(AndroidUtilities.displaySize.x - (previewWidth + AndroidUtilities.dp(5)));
            imageView.setImageBitmap(imgPreview);
            params.width = previewWidth;
            params.height = previewHeight;
            params.topMargin = 5;
            params.bottomMargin = 5;
        }
        else if (imgPreview.getWidth() < imgPreview.getHeight()) {
            // portrait
            imageView.setTranslationX(AndroidUtilities.displaySize.x - (previewHeight + AndroidUtilities.dp(5)));
            imageView.setImageBitmap(imgPreview);
            params.width = previewHeight;
            params.height = previewWidth;
            params.topMargin = 5;
            params.bottomMargin = 5;
        }
        else {
            // square
            imageView.setTranslationX(AndroidUtilities.displaySize.x - (previewHeight + AndroidUtilities.dp(5)));
            imageView.setImageBitmap(imgPreview);
            params.width = previewHeight;
            params.height = previewHeight;
            params.topMargin = 5;
            params.bottomMargin = 5;
        }

        cell.addView(imageView, params);
        return cell;
    }

    @Override
    public int getCount() {
        return imageId.length;
    }
}
