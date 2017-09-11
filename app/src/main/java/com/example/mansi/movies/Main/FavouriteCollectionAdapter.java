package com.example.mansi.movies.Main;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mansi.movies.Database.MoviesContract;
import com.example.mansi.movies.R;

public class FavouriteCollectionAdapter extends CursorAdapter {

    public FavouriteCollectionAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.grid_item_poster, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = view.findViewById(R.id.home_poster);
        byte array[] = cursor.getBlob(cursor.getColumnIndex(MoviesContract.MoviesEntry.COL_IMAGE));
        Bitmap bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
        imageView.setImageBitmap(bitmap);
    }
}

