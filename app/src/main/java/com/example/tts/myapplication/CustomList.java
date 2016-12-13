package com.example.tts.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

    private int previewWidth = AndroidUtilities.dp(250);
    private int previewHeight = AndroidUtilities.dp(140);

    public CustomList(Activity context, Integer[] imageId) {
        super(context, R.layout.list_item);
        this.context = context;
        this.imageId = imageId;
    }

    private Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
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
