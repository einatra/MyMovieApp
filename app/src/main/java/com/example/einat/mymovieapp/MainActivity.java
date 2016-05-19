package com.example.einat.mymovieapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Locale;

public class MainActivity extends ActionBarActivity {

    ListView myMovieList;
    MovieDBManager manager;
    MovieListAdapter customAdapter;
    SharedPreferences pref;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkLanguage();
        setContentView(R.layout.activity_main);

        showWelcomeView();

        manager = new MovieDBManager(this, 1);
        myMovieList = (ListView) findViewById(R.id.listMovies);
        customAdapter = new MovieListAdapter(this, manager.getAllMovies());
        myMovieList.setAdapter(customAdapter);

        addMovieDialog();
        clickItemForEdit();
        longClickItemForDelete();

    }

    private void longClickItemForDelete() {
        //long click on item opens delete/edit dialog
        myMovieList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, final long id) {

                final AlertDialog deleteDialog = new AlertDialog.Builder(MainActivity.this).create();
                deleteDialog.setTitle(getString(R.string.MainActivity_deleteDialogTitle));
                deleteDialog.setMessage(getString(R.string.MainActivity_deleteDialogMessage));

                deleteDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.MainActivity_deleteDialog_btnP), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainActivity.this, EditMovieAct.class);
                        intent.putExtra("_ID", id);  //passing the id for extracting from DB
                        intent.putExtra("key", 2);  //key: comes from list
                        startActivity(intent);
                    }
                });

                deleteDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.MainActivity_deleteDialog_btnN), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final AlertDialog sureDialog = new AlertDialog.Builder(MainActivity.this).create();
                        sureDialog.setTitle(getString(R.string.MainActivity_delAllDialog_title));
                        sureDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.MainActivity_delAllDialog_btnP), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                manager.deleteMovieByID(id);
                                customAdapter.swapCursor(manager.getAllMovies());
                                dialog.dismiss();
                            }
                        });
                        sureDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.MainActivity_delAllDialog_btnN), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        sureDialog.show();
                    }
                });
                deleteDialog.show();
                return true;
            }
        });
    }

    private void clickItemForEdit() {
        //click on item opens in editing act
        myMovieList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent intent = new Intent(MainActivity.this, EditMovieAct.class);
                intent.putExtra("_ID", id); //passing the id for extracting from DB
                intent.putExtra("key", 2);  //key: comes from list
                startActivity(intent);
            }
        });
    }

    private void addMovieDialog() {
        //open dialog add new movie to list
        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
                dialog.setTitle(getString(R.string.MainActivity_dialog_setTitle));
                dialog.setMessage(getString(R.string.MainActivity_dialog_setMessage));

                //add from web:
                dialog.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.MainActivity_dialog_btnP), new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainActivity.this, SearchAct.class);
                        intent.putExtra("key", 1);  //key: comes from internet
                        startActivity(intent);
                    }
                });

                //add manually:
                dialog.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.MainActivity_dialog_btnN), new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainActivity.this, EditMovieAct.class);
                        intent.putExtra("key", 3);  //key: comes from manual
                        startActivity(intent);
                    }
                });

                dialog.show();
            }
        });
    }

    private void showWelcomeView() {
        if (pref.getBoolean("clicked", false)) {
            findViewById(R.id.welcomeText).setVisibility(View.GONE);
        } else {
            findViewById(R.id.welcomeText).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    findViewById(R.id.welcomeText).setVisibility(view.GONE);
                    edit = pref.edit();
                    edit.putBoolean("clicked", true);
                    edit.commit();
                }
            });
        }
    }

    private void checkLanguage() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = pref.getString("Lang", "");
        if (!lang.equals("")) {
            //call method to set language
            setLanguage(lang);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case (R.id.action_delete):
                final AlertDialog delAllDialog = new AlertDialog.Builder(MainActivity.this).create();
                delAllDialog.setTitle(getString(R.string.MainActivity_delAllDialog_title));
                delAllDialog.setMessage(getString(R.string.MainActivity_delAllDialog_msg));
                delAllDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.MainActivity_delAllDialog_btnP), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        manager.deleteAllData();
                        customAdapter.swapCursor(manager.getAllMovies());
                        dialog.dismiss();
                    }
                });
                delAllDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.MainActivity_delAllDialog_btnN), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                delAllDialog.show();
                break;

            case (R.id.action_exit):
                finish();
                break;

            case (R.id.action_language):
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                String lang = pref.getString("Lang", "");
                SharedPreferences.Editor edit = pref.edit();
                if (lang.equals("iw")) {
                    edit.putString("Lang", "en");
                    setLanguage("en");
                } else {
                    edit.putString("Lang", "iw");
                    setLanguage("iw");
                }
                edit.commit();

                Intent refresh = new Intent(this, MainActivity.class);
                startActivity(refresh);
                finish();
                break;

            case (R.id.action_filter_watched):
                customAdapter.swapCursor(manager.getMovieByWatched());
                break;
            case (R.id.action_filter_not_watched):
                customAdapter.swapCursor(manager.getMovieByNotWatched());
                break;
            case (R.id.action_filter_all):
                customAdapter.swapCursor(manager.getAllMovies());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //set language
    public void setLanguage(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    @Override
    protected void onResume() {
        super.onResume();
        customAdapter.swapCursor(manager.getAllMovies());
    }
}