package com.example.einat.mymovieapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Einat on 18/05/2016.
 */
public final class AppHelper {

    private static AppHelper instance;

    private AppHelper() {}

    public static AppHelper getInstance(){
        if (instance == null){
            synchronized (AppHelper.class) {
                if (instance == null) {
                    instance = new AppHelper();
                }
            }
        }
        return instance;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static File getAlbumStorageDir(Context context) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), "Posters");
        if (!file.mkdirs()) {
            Log.e("log", "Directory not created");
        }
        return file;
    }

    //get uri from the bitmap method
    public static Uri getImgUri(Context inContext, Bitmap inImg) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //inImg.compress(Bitmap.CompressFormat.JPEG, 100,bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImg, "Title", null);
        return Uri.parse(path);
    }

    // get the img on phone path
    public static String getRealPathFromUri(Context inContext, Uri uri) {
        Cursor c = inContext.getContentResolver().query(uri, null, null, null, null);
        c.moveToFirst();
        int idx = c.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        String imgPath = c.getString(idx);
        c.close();
        return imgPath;
    }

    public static void getPosterFromPhone(String pointer, Context context, ImageView poster, Button btn) {
        try {
            Uri imgUri = Uri.parse(pointer);
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor uriCursor = context.getContentResolver().query(imgUri, projection, null, null, null);
            uriCursor.moveToFirst();
            int column_index = uriCursor.getColumnIndex(projection[0]);
            String uriPath = uriCursor.getString(column_index);
            uriCursor.close();
            poster.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(uriPath), 200, 200, false));
        } catch (Exception e) {
            Log.i("btnShow", "Nexus camera");
        }
        try {
            File imgFile = new File(pointer);
            if (imgFile.exists()) {
                String absPath = imgFile.getAbsolutePath();
                Bitmap myBitmap = BitmapFactory.decodeFile(absPath);
                poster.setImageBitmap(myBitmap);
                poster.setVisibility(View.VISIBLE);
                if (btn != null){
                btn.setVisibility(View.VISIBLE);
                }

            }
        } catch (Exception e) {
            Log.i("btnShow", "unresolved camera app");
        }
    }

    public static void saveToExternalStorage(Context context, Bitmap bitmap, String filename /*EditText editUrl*/) {

        if (AppHelper.isExternalStorageWritable()) {

            File folder = AppHelper.getAlbumStorageDir(context);

            String posterPath = folder.getAbsoluteFile() + filename + ".jpg";
            File file = new File(posterPath);
            if (file.exists()) {
                Log.i("file", "file already exists");
            } else {

                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                    Log.i("file", "file stored successfully");
                   // editUrl.setText(posterPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
