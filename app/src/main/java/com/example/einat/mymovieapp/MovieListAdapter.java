package com.example.einat.mymovieapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.einat.mymovieapp.utils.AppHelper;

/**
 * Created by Einat on 22/05/2015.
 */
public class MovieListAdapter extends CursorAdapter {

    ImageView posterImg;
    Context context;

    public MovieListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.movie_list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //connect to the views to populate in inflated custom list item
        TextView textTitle = (TextView)view.findViewById(R.id.textTitle);
        TextView textLength = (TextView)view.findViewById(R.id.textLength);
        TextView textWatched = (TextView)view.findViewById(R.id.textWatched);
        posterImg = (ImageView) view.findViewById(R.id.smallPosterImg);

        // extract data from cursor
        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        String length = cursor.getString(cursor.getColumnIndexOrThrow("length"));
        int watched = cursor.getInt(cursor.getColumnIndex("watched"));
        float rating = cursor.getFloat(cursor.getColumnIndex("rating"));
        String poster = cursor.getString(cursor.getColumnIndex("img_url"));


        //populate fields with extracted data
        textTitle.setText(title);
        textLength.setText(length);
        if (watched == 1){
            textWatched.setText("watched");
        }else{
            textWatched.setText("didn't watch");
        }
        //change title color according to rating
        if (rating <= 2 ){ //red
            textTitle.setTextColor(Color.parseColor("#FFE4080B"));
        }else
        if (rating >= 3 && rating < 4){ //yellow
            textTitle.setTextColor(Color.parseColor("#FFF1B00F"));
        }else
        if (rating <= 5 ){ //green
            textTitle.setTextColor(Color.parseColor("#FF219514"));
        }

        AppHelper.getPosterFromPhone(poster, context, posterImg, null);
    }

}
