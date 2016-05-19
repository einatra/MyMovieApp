package com.example.einat.mymovieapp.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.einat.mymovieapp.R;
import com.example.einat.mymovieapp.utils.InterfaceBank;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Einat on 18/05/2016.
 */
public class FillTask extends AsyncTask<String, Void, String> {

    Context context;
    private InterfaceBank.FillTaskCallback fillTaskCallbackListener;

    public FillTask(Context context, InterfaceBank.FillTaskCallback listener){
        this.context = context;
        this.fillTaskCallbackListener = listener;
    }
    private ProgressDialog dialog;

    @Override
    public void onPreExecute() {
        super.onPreExecute();

        // show dialog:
        dialog = new ProgressDialog(context);
        dialog.setTitle(context.getString(R.string.EditMovieAct_fillDialog_title));
        dialog.setMessage(context.getString(R.string.EditMovieAct_fillDialog_msg));
        dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        BufferedReader input = null;
        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(params[0] + params[1]);
            connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                // not good
                return null;
            }

            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = input.readLine()) != null) {
                response.append(line).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        String result;
        result = response.toString();
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        dialog.dismiss();

        if (result == null) {
            Toast.makeText(context, context.getString(R.string.EditMovieAct_ToastNoData), Toast.LENGTH_LONG).show();
            return;
        }

        String title = null;
        String plot = null;
        String imgURL = null;
        String year = null;
        String length = null;
        float rating = 0;

        JSONObject obj = null;
        try {
            obj = new JSONObject(result);
            title = obj.getString("Title");
            plot = obj.getString("Plot");
            imgURL = obj.getString("Poster");
            year = obj.getString("Year");
            length = obj.getString("Runtime");
            rating = ((float) obj.getDouble("imdbRating"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        fillTaskCallbackListener.onFillTaskComplete(title, plot, imgURL, year, length, rating);
    }
}
