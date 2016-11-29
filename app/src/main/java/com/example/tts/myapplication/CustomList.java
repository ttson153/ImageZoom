package com.example.tts.myapplication;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

        View rowView= inflater.inflate(R.layout.list_item, null, true);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), imageId[position]));

        return rowView;
    }

    @Override
    public int getCount() {
        return imageId.length;
    }
}
