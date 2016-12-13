package com.example.tts.myapplication;

import android.graphics.Bitmap;

/**
 * Created by tts on 12/13/16.
 */
public class TransformData {
    public int viewX;
    public int viewY;
    public Bitmap thumbImage;
    public String fullImagePath;
    public int size;
    public int radius;
    public int clipBottomAddition;
    public int clipTopAddition;
    public float scale = 1.0f;

    TransformData() {
    }

    public TransformData setThumbImage(Bitmap bm) {
        thumbImage = bm;
        return this;
    }

    public TransformData setFullImagePath(String path) {
        fullImagePath = path;
        return this;
    }

    public TransformData setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public TransformData setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public TransformData setClipTopAddition(int addition) {
        clipTopAddition = addition;
        return this;
    }

    public TransformData setClipBottomAddition(int addition) {
        clipBottomAddition = addition;
        return this;
    }
}
