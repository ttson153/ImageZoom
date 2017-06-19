package com.example.tts.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.tts.libimagezoomanim.AndroidUtilities;
import com.tts.libimagezoomanim.MyApplication;

/**
 * Created by tts on 11/30/16.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final Activity context;
    private final int[] imageId;

    public static final int CIRCLE_IMAGE_POSITION = 4;
    public class IMAGE_TYPE {
        static final short TYPE_NORMAL = 0;
        static final short TYPE_CIRCLE = 1;
    }

    private int previewWidth = AndroidUtilities.dp(250);
    private int previewHeight = AndroidUtilities.dp(140);

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public FrameLayout mFrameLayout;
        public ImageView   mImageView;
        public ViewHolder(FrameLayout v, ImageView imageView) {
            super(v);
            mFrameLayout = v;
            mImageView = imageView;
        }
    }

    public RecyclerViewAdapter(Activity context, int[] imageId) {
        this.context = context;
        this.imageId = imageId;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == CIRCLE_IMAGE_POSITION) {
            return IMAGE_TYPE.TYPE_CIRCLE;
        } else {
            return IMAGE_TYPE.TYPE_NORMAL;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout cell = new FrameLayout(MyApplication.getAppContext());
        ImageView imageView;
        switch (viewType) {
            case IMAGE_TYPE.TYPE_CIRCLE: imageView = new CircularImageView(MyApplication.getAppContext()); break;
            case IMAGE_TYPE.TYPE_NORMAL: imageView = new ImageView(MyApplication.getAppContext()); break;
            default: imageView = new ImageView(MyApplication.getAppContext());
        }
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        cell.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        cell.addView(imageView);

        return new ViewHolder(cell, imageView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bitmap imgPreview = BitmapFactory.decodeResource(context.getResources(), imageId[position]);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(0, 0);

        Drawable drawable = holder.mImageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bmd = (BitmapDrawable) drawable;
            bmd.getBitmap().recycle();
        }
        // 5dp from right
        if (imgPreview.getWidth() > imgPreview.getHeight()) {
            // landscape
            holder.mImageView.setTranslationX(AndroidUtilities.displaySize.x - (previewWidth + AndroidUtilities.dp(5)));
            params.width = previewWidth;
            params.height = previewHeight;
        }
        else if (imgPreview.getWidth() < imgPreview.getHeight()) {
            // portrait
            holder.mImageView.setTranslationX(AndroidUtilities.displaySize.x - (previewHeight + AndroidUtilities.dp(5)));
            params.width = previewHeight;
            params.height = previewWidth;
        }
        else {
            // square
            holder.mImageView.setTranslationX(AndroidUtilities.displaySize.x - (previewHeight + AndroidUtilities.dp(5)));
            params.width = previewHeight;
            params.height = previewHeight;
        }
        holder.mImageView.setImageBitmap(imgPreview);
        params.topMargin = 5;
        params.bottomMargin = 5;

        holder.mImageView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return imageId.length;
    }
}
