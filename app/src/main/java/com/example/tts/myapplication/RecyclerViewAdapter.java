package com.example.tts.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by tts on 11/30/16.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final Activity context;
    private final int[] imageId;

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

    private Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, pixels, pixels, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();
        return output;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout cell = new FrameLayout(MyApplication.getAppContext());
        ImageView   imageView = new ImageView(MyApplication.getAppContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        cell.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        cell.addView(imageView);

        ViewHolder viewHolder = new ViewHolder(cell, imageView);
        return viewHolder;
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
            holder.mImageView.setImageBitmap(imgPreview);
            params.width = previewWidth;
            params.height = previewHeight;
            params.topMargin = 5;
            params.bottomMargin = 5;
        }
        else if (imgPreview.getWidth() < imgPreview.getHeight()) {
            // portrait
            holder.mImageView.setTranslationX(AndroidUtilities.displaySize.x - (previewHeight + AndroidUtilities.dp(5)));
            holder.mImageView.setImageBitmap(imgPreview);
            params.width = previewHeight;
            params.height = previewWidth;
            params.topMargin = 5;
            params.bottomMargin = 5;
        }
        else {
            // square
            holder.mImageView.setTranslationX(AndroidUtilities.displaySize.x - (previewHeight + AndroidUtilities.dp(5)));
            holder.mImageView.setImageBitmap(getRoundedCornerBitmap(imgPreview, previewHeight * 2));
            params.width = previewHeight;
            params.height = previewHeight;
            params.topMargin = 5;
            params.bottomMargin = 5;
        }

        holder.mImageView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return imageId.length;
    }
}
