package com.example.einat.mymovieapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;

/**
 * Created by jbt on 19/05/2015.
 */
public class MovieDBManager extends SQLiteOpenHelper {

    public final String MOVIE_TABLE = "movies";
    public final String MOVIE_ID_COL = "_id";
    public final String MOVIE_TITLE_COL = "title";
    public final String MOVIE_PLOT_COL = "plot";
    public final String MOVIE_IMG_URL_COL = "img_url";
    public final String MOVIE_YEAR_COL = "year";
    public final String MOVIE_LENGTH_COL = "length";
    public final String MOVIE_RATING_COL = "rating";
    public final String MOVIE_WATCHED_COL = "watched";

    public MovieDBManager(Context context, int version) {
        super(context, "movie.db", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MOVIE_TABLE + "( " + MOVIE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MOVIE_TITLE_COL + " TEXT UNIQUE, " + MOVIE_PLOT_COL + " TEXT, " + MOVIE_IMG_URL_COL + " TEXT, " +
                MOVIE_YEAR_COL + " TEXT, " + MOVIE_LENGTH_COL + " TEXT, " + MOVIE_RATING_COL + " REAL, " +
                MOVIE_WATCHED_COL + " INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //***************************************************************************************************

    public void insertMovie(Movie m){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MOVIE_TITLE_COL, m.getTitle());
        values.put(MOVIE_PLOT_COL, m.getPlot());
        values.put(MOVIE_IMG_URL_COL, m.getImgUrl());
        values.put(MOVIE_YEAR_COL, m.getYear());
        values.put(MOVIE_LENGTH_COL, m.getLength());
        values.put(MOVIE_RATING_COL, m.getRating());
        values.put(MOVIE_WATCHED_COL, m.getWatched());
        db.insert(MOVIE_TABLE, null, values);
        db.close();
    }

    public void updateMovie(Movie m, long id){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MOVIE_PLOT_COL, m.getPlot());
        values.put(MOVIE_IMG_URL_COL, m.getImgUrl());
        values.put(MOVIE_YEAR_COL, m.getYear());
        values.put(MOVIE_LENGTH_COL, m.getLength());
        values.put(MOVIE_RATING_COL, m.getRating());
        values.put(MOVIE_WATCHED_COL, m.getWatched());
        db.update(MOVIE_TABLE, values, MOVIE_ID_COL + "=" + id, null);
        db.close();

    }

    public Cursor getAllMovies(){
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + MOVIE_TABLE, null);
    }

    public Movie getMovieByID(long id){
        SQLiteDatabase db = getReadableDatabase();
        //call this function on main onCreate and refresh
        Cursor c = db.rawQuery("SELECT * FROM " + MOVIE_TABLE + " WHERE " + MOVIE_ID_COL + "=" + id, null);
        Movie movie;
        c.moveToFirst();
        String title  = (c.getString(c.getColumnIndex(MOVIE_TITLE_COL)));
        String plot = (c.getString(c.getColumnIndex(MOVIE_PLOT_COL)));
        String url = (c.getString(c.getColumnIndex(MOVIE_IMG_URL_COL)));
        String year = (c.getString(c.getColumnIndex(MOVIE_YEAR_COL)));
        String length = (c.getString(c.getColumnIndex(MOVIE_LENGTH_COL)));
        float rating = (c.getFloat(c.getColumnIndex(MOVIE_RATING_COL)));
        int watched = (c.getInt(c.getColumnIndex(MOVIE_WATCHED_COL)));
        movie = new Movie(title, plot, url, year, length, rating, watched);

        return movie;
    }

    public Cursor getMovieByWatched(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + MOVIE_TABLE + " WHERE " + MOVIE_WATCHED_COL + "=1", null);
        return c;
    }

    public Cursor getMovieByNotWatched(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + MOVIE_TABLE + " WHERE " + MOVIE_WATCHED_COL + "=0", null);
        return c;
    }

    public void deleteMovieByID(long id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MOVIE_TABLE, MOVIE_ID_COL + "=" + id, null);
        db.close();
    }

    public void deleteAllData(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MOVIE_TABLE, null,null);
        db.close();
    }
}
