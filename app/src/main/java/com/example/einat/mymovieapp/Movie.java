package com.example.einat.mymovieapp;

import android.graphics.Bitmap;

/**
 * Created by Einat on 25/05/2015.
 */
public class Movie {

    private String title, plot, imgUrl, year, length;
    private long id;
    float rating;
    int watched;
    private Bitmap poster;

    public Movie(String title, String plot, String imgUrl, String year, String length, float rating, int watched) {
        this.title = title;
        this.plot = plot;
        this.imgUrl = imgUrl;
        this.year = year;
        this.length = length;
        this.rating = rating;
        this.watched = watched;
    }

    public Bitmap getPoster() {
        return poster;
    }

    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }

    public Movie(String title, String plot, String imgUrl, String year, String length, long id, float rating, int watched, Bitmap poster) {

        this.title = title;
        this.plot = plot;
        this.imgUrl = imgUrl;
        this.year = year;
        this.length = length;
        this.id = id;
        this.rating = rating;
        this.watched = watched;
        this.poster = poster;
    }

    public int getWatched() {
        return watched;
    }

    public void setWatched(int watched) {
        this.watched = watched;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
