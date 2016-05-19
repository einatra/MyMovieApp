package com.example.einat.mymovieapp.utils;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by Einat on 18/05/2016.
 */
public class InterfaceBank {

    public interface FillTaskCallback{
         void onFillTaskComplete(String title, String plot, String imgURL,
                                       String year, String length, float rating);
    }

    public interface DownloadImageTaskCallBack{
        void onDownloadImageTaskComplete(Context context, Bitmap result);
    }
}
