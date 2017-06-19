package com.tts.libimagezoomanim;

import android.graphics.Bitmap;

/**
 * Created by tts on 12/13/16.
 */
public class TransformData {
    public int viewX;
    public int viewY;
    private Bitmap thumbImage;
    private String fullImagePath;
    private int size;
    private int radius;
    private int clipBottomAddition;
    private int clipTopAddition;
    private float scale = 1.0f;

    public TransformData() {
    }

    public Bitmap getThumbImage() {
        return thumbImage;
    }

    public TransformData setThumbImage(Bitmap bm) {
        thumbImage = bm;
        return this;
    }

    public String getFullImagePath() {
        return fullImagePath;
    }

    public TransformData setFullImagePath(String path) {
        fullImagePath = path;
        return this;
    }

    public int getRadius() {
        return radius;
    }

    public TransformData setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public float getScale() {
        return scale;
    }

    public TransformData setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public int getClipTopAddition() {
        return clipTopAddition;
    }

    public TransformData setClipTopAddition(int addition) {
        clipTopAddition = addition;
        return this;
    }

    public int getClipBottomAddition() {
        return clipBottomAddition;
    }

    public TransformData setClipBottomAddition(int addition) {
        clipBottomAddition = addition;
        return this;
    }
}
