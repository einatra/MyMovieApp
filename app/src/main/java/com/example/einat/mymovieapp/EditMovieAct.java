package com.example.einat.mymovieapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.einat.mymovieapp.network.DownloadImageTask;
import com.example.einat.mymovieapp.network.FillTask;
import com.example.einat.mymovieapp.utils.AppHelper;
import com.example.einat.mymovieapp.utils.InterfaceBank;

import java.io.File;
import java.io.FileOutputStream;

public class EditMovieAct extends ActionBarActivity implements View.OnClickListener {

    private ImageView poster;
    private MovieDBManager manager;
    private long id;
    private EditText editTitle,editPlot, editUrl, editYear, editLength;
    private RatingBar ratingBar;
    private CheckBox watched;
    private Button btnClosePic, btnTakePic;
    private int checked = 0;
    private int key = 0;
    private Movie movie;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_movie);

        findViews();

        manager = new MovieDBManager(this, 1);

        Intent intent = getIntent();
        key = intent.getIntExtra("key", -1);  //who called?

        switch (key) {
            //from internet
            case (1):
                String imdbID = intent.getStringExtra("imdbID");
                FillTask task = new FillTask(this, new InterfaceBank.FillTaskCallback() {
                    @Override
                    public void onFillTaskComplete(String title, String plot, String imgURL, String year, String length, float rating) {
                        editTitle.setText(title);
                        editPlot.setText(plot);
                        editUrl.setText(imgURL);
                        editYear.setText(year);
                        editLength.setText(length);
                        ratingBar.setRating(rating / 2f);
                    }
                });
                task.execute("http://www.omdbapi.com/?i=", imdbID);
                break;

            //from list
            case (2):
                id = intent.getLongExtra("_ID", -1);
                setMovieDetails();
                break;

            //from add manually
            case (3):

                break;
        }

        setOnClickListeners();
        checkCamera();

        //set button for closing poster invisible as long as "show" button isn't clicked
        btnClosePic.setVisibility(View.GONE);
        btnClosePic.setOnClickListener(this);
    }

    private void checkCamera() {
        // check if the appliance has a camera
        PackageManager pm = this.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            btnTakePic.setOnClickListener(this);
        } else { //if not make btnPic invisible
            btnTakePic.setVisibility(View.INVISIBLE);
        }
    }

    private void setOnClickListeners() {
        findViewById(R.id.btnSave).setOnClickListener(this);
        findViewById(R.id.btnCancel).setOnClickListener(this);
        findViewById(R.id.btnShow).setOnClickListener(this);
        findViewById(R.id.wchCheck).setOnClickListener(this);
        findViewById(R.id.btnShare).setOnClickListener(this);
    }

    private void findViews() {
        editTitle = (EditText) findViewById(R.id.editTitle);
        editPlot = (EditText) findViewById(R.id.editPlot);
        editUrl = (EditText) findViewById(R.id.picUrlEdit);
        editYear = (EditText) findViewById(R.id.yearEdit);
        editLength = (EditText) findViewById(R.id.lengthEdit);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        watched = (CheckBox) findViewById(R.id.wchCheck);
        btnClosePic = (Button) findViewById(R.id.btnClosePic);
        poster = (ImageView) findViewById(R.id.posterView);
        btnTakePic = (Button) findViewById(R.id.btnPic);
    }

    private static final int REQUEST_IMAGE_CAPTURE = 0;

    @Override
    public void onClick(View view) {
        title = editTitle.getText().toString();
        switch (view.getId()) {

            case (R.id.btnCancel):
                finish();
                break;

            case (R.id.btnSave):

                //create movie obj
                // title = editTitle.getText().toString();

                if (!title.equals("")) {
                    getMovieDetails();
                    if (key == 2) {
                        // comes from list - just update db
                        manager.updateMovie(movie, id);
                    } else {
                        //add new movie to db
                        manager.insertMovie(movie);
                    }
                    finish();
                    break;
                } else
                    Toast.makeText(EditMovieAct.this, getString(R.string.EditMovieAct_ToastTitle), Toast.LENGTH_LONG).show();
                break;

            case (R.id.btnShow):
                String pointer = editUrl.getText().toString();
                if (pointer.equals("") || pointer.length() < 4) {
                    Toast.makeText(EditMovieAct.this, getString(R.string.EditMovieAct_ToastImage), Toast.LENGTH_LONG).show();
                } else {
                    String http = pointer.substring(0, 4);
                    if (http.equals("http")) {
                        showPosterFromNet(pointer);
                    } else {
                        AppHelper.getPosterFromPhone(pointer, this, poster, btnClosePic);
                    }
                }
                break;

            case (R.id.btnShare):

                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");

                String msg = "It's about " + editPlot.getText().toString() + " And it's rated: " + ratingBar.getRating() + "\nWhat do you say?";
                intent.putExtra(Intent.EXTRA_SUBJECT, "Want to watch " + editTitle.getText().toString() + "?");
                intent.putExtra(Intent.EXTRA_TEXT, msg);

                startActivity(Intent.createChooser(intent, "Share with:"));
                break;

            case (R.id.wchCheck):

                if (watched.isChecked()) {
                    checked = 1;
                } else {
                    checked = 0;
                }
                break;

            case (R.id.btnPic):
                Intent takePicIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE);
                break;

            case (R.id.btnClosePic):
                poster.setVisibility(View.GONE);
                btnClosePic.setVisibility(View.GONE);
        }
    }

    private void showPosterFromNet(String pointer) {
        DownloadImageTask task = new DownloadImageTask(this, movie, new InterfaceBank.DownloadImageTaskCallBack() {
            @Override
            public void onDownloadImageTaskComplete(Context context, Bitmap result) {
                if (result != null) {
                    poster.setImageBitmap(result);
                    AppHelper.saveToExternalStorage(context, result, title);
                } else {
                    Toast.makeText(context, getString(R.string.EditMovieAct_ToastProb), Toast.LENGTH_LONG).show();
                }
            }
        });
        task.execute(pointer);
        //poster and button for closing poster appears
        poster.setVisibility(View.VISIBLE);
        btnClosePic.setVisibility(View.VISIBLE);
    }

    private void setMovieDetails() {
        Movie movie = manager.getMovieByID(id);
        editTitle.setText(movie.getTitle());
        editPlot.setText(movie.getPlot());
        editUrl.setText(movie.getImgUrl());
        editYear.setText(movie.getYear());
        editLength.setText(movie.getLength());
        ratingBar.setRating(movie.getRating());
        if (movie.getWatched() == 1) {
            watched.setChecked(true);
        } else {
            watched.setChecked(false);
        }
    }

    private void getMovieDetails() {
        String plot = editPlot.getText().toString();
        String imgUrl = editUrl.getText().toString();
        String year = editYear.getText().toString();
        String length = editLength.getText().toString();
        float rating = ratingBar.getRating();
        int watched = checked;
        movie = new Movie(title, plot, imgUrl, year, length, rating, watched);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {

                    poster.setVisibility(View.VISIBLE);
                    btnClosePic.setVisibility(View.VISIBLE);

                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    poster.setImageBitmap(photo);

                    //get Uri from bitmap
                    Uri tempUri = AppHelper.getImgUri(getApplicationContext(), photo);

                    //get the actual file path
                    File theFile = new File(AppHelper.getRealPathFromUri(getApplicationContext(), tempUri));
                    editUrl.setText(theFile.toString());
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void saveToExternalStorage(Bitmap bitmap, String filename) {

        if (AppHelper.isExternalStorageWritable()) {

            File folder = AppHelper.getAlbumStorageDir(this);

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
                    editUrl.setText(posterPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}