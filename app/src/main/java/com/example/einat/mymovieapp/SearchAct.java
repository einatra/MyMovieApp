package com.example.einat.mymovieapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class SearchAct extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    // movie search result list:
    ArrayList<String> resultArrayList = new ArrayList<String>();

    // ID list for putExtra
    ArrayList<String> IDList = new ArrayList<String>();

    // adapter:
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        adapter = new ArrayAdapter<String>(this, R.layout.search_list_item, R.id.listText, resultArrayList);

        ListView listResult = (ListView) findViewById(R.id.listResult);
        listResult.setAdapter(adapter);

        listResult.setOnItemClickListener(this);
        findViewById(R.id.btnGo).setOnClickListener(this);
        findViewById(R.id.btnCancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case (R.id.btnGo):
                IDList.clear(); //clear last result
                String keyWord = ((EditText)findViewById(R.id.editSearch)).getText().toString();
                if (keyWord.equals("")) {
                    Toast.makeText(SearchAct.this, getString(R.string.search_act_no_keyword_toast), Toast.LENGTH_SHORT).show();
                    break;
                }else {
                    SearchTask task = new SearchTask();
                    task.execute("http://www.omdbapi.com/?s=", keyWord);
                    break;
                }
            case (R.id.btnCancel):
                finish();
                break;
        }
    }

    ProgressDialog dialog;

    class SearchTask extends AsyncTask<String, Void, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();

            //clear list
            resultArrayList.clear();
            adapter.notifyDataSetChanged();

            // show dialog:
            dialog = new ProgressDialog(SearchAct.this);
            dialog.setTitle(getString(R.string.search_act_loding_dialog_title));
            dialog.setMessage(getString(R.string.search_act_loding_dialog_msg));
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            BufferedReader input = null;
            HttpURLConnection connection = null;
            StringBuilder response = new StringBuilder();

            String query = null;
            try {
                query = URLEncoder.encode(params[1], "utf-8"); // convert search entry to utf-8

                //URL obj:
                URL url = new URL(params[0] + query);

                connection = (HttpURLConnection) url.openConnection();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line = "";
                while ((line = input.readLine()) != null) {
                    response.append(line + "\n");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
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
            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();

            if (result == null) {
                Toast.makeText(SearchAct.this, getString(R.string.search_act_no_movie_toast), Toast.LENGTH_LONG).show();
                return;
            }

            try {

                JSONObject obj = new JSONObject(result);
                JSONArray search = obj.getJSONArray("Search");
                for (int i = 0; i < search.length(); i++) {
                    JSONObject itemTitleJSON = search.getJSONObject(i);
                    JSONObject imdbIdJSON = search.getJSONObject(i);
                    String title = itemTitleJSON.get("Title").toString();
                    String imdbID = imdbIdJSON.get("imdbID").toString();
                    resultArrayList.add(title);
                    IDList.add(imdbID);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String imdbID = IDList.get(position);
        Intent intent = new Intent(SearchAct.this, EditMovieAct.class);
        intent.putExtra("imdbID", imdbID);
        intent.putExtra("key", 1); //opening EditMovieAct from internet Search
        startActivity(intent);
    }
}

