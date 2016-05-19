package com.example.einat.mymovieapp.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.example.einat.mymovieapp.Movie;
import com.example.einat.mymovieapp.R;
import com.example.einat.mymovieapp.utils.InterfaceBank;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Einat on 18/05/2016.
 */
public class DownloadImageTask extends AsyncTask<String, Integer, Bitmap> {

    private ProgressDialog dialog;
    private Context context;
    private InterfaceBank.DownloadImageTaskCallBack downloadImageTaskCallBack;
    private Movie movie;

    public DownloadImageTask(Context context, Movie movie, InterfaceBank.DownloadImageTaskCallBack listener) {
        this.context = context;
        this.downloadImageTaskCallBack = listener;
        this.movie = movie;
    }

    protected void onPreExecute() {

        // Reset the progress bar
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
        dialog.setMessage(context.getString(R.string.EditMovieAct_loadImgDialog_msg));
        dialog.setProgress(0);
        dialog.show();

    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        Log.d("doInBackground", "starting download of image");

        Bitmap bitmap = null;
        URL url;
        try {
            url = new URL(urls[0]);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();

            InputStream is = httpCon.getInputStream();
            int fileLength = httpCon.getContentLength();

///****************
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead, totalBytesRead = 0;
            byte[] data = new byte[2048];

            dialog.setMax(fileLength);

            // Read the image bytes in chunks of 2048 bytes
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
                totalBytesRead += nRead;
                publishProgress(totalBytesRead);
            }

            buffer.flush();
            byte[] image = buffer.toByteArray();


            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
//*****************/

            // this is a much easier option - but no progress!!
            //bitmap = BitmapFactory.decodeStream(httpCon.getInputStream());

        } catch (Exception e) {
            Log.e("ERROR!", e.getMessage());
        }

        if (bitmap != null && movie != null) {
            movie.setPoster(bitmap);
        }

        return bitmap;
    }

    protected void onProgressUpdate(Integer... progress) {
        dialog.setProgress(progress[0]);
    }

    protected void onPostExecute(Bitmap result) {
        // Close the progress dialog
        dialog.dismiss();

        downloadImageTaskCallBack.onDownloadImageTaskComplete(context, result);
//        if (result != null) {
//            poster.setImageBitmap(result);
//            AppHelper.saveToExternalStorage(context, result, title);
//        } else {
//            Toast.makeText(context, getString(R.string.EditMovieAct_ToastProb), Toast.LENGTH_LONG).show();
//        }
    }
}
