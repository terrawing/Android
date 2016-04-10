package com.example.student.doyouevenliftbro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Intent activityIntent;
    private String stringExerciseJSON = "";
    //private ArrayList<String> allExercisesArrayList = new ArrayList<String>();
    //private ArrayList<Exercise> allExercisesArrayList = new ArrayList<Exercise>();
    //private Bundle bundle = new Bundle();
    //private int flag = 0;
    private DatabaseHelper dbhelper;
    private ProgressDialog progress;
    private Button viewAllExerciseBtn;
    private Button viewExerciseByMuscleBtn;
    private Button displayNDoExerciseBtn;
    private Button optionsBtn;

    private final String api = "https://wger.de/api/v2/exercise.json/?language=2&ordering=id&ordering=name&status=2&limit=200";
    private final String api2 = "https://wger.de/api/v2/muscle.json/?ordering=name";
    private final String api3 = "https://wger.de/api/v2/exerciseimage.json/?limit=250&ordering=exercise&ordering=id&ordering=image";
    private final String api4 = "https://wger.de/api/v2/equipment.json/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //http://stackoverflow.com/questions/31096275/how-to-remove-app-title-from-toolbar
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar.setTitle("");
        //toolbar.setSubtitle("");

        viewAllExerciseBtn = (Button) findViewById(R.id.viewAllExerciseBtn);
        viewExerciseByMuscleBtn = (Button) findViewById(R.id.viewExerciseByMuscleBtn);
        displayNDoExerciseBtn = (Button) findViewById(R.id.displayNDoExercisesBtn);
        optionsBtn = (Button) findViewById(R.id.optionsBtn);

        if(!checkDatabaseExist()) { //if database is not created (first time running) get data from the net

            Log.d("Database", "Not Exists");
            //Disable buttons first
            viewAllExerciseBtn.setEnabled(false);
            viewExerciseByMuscleBtn.setEnabled(false);
            displayNDoExerciseBtn.setEnabled(false);
            optionsBtn.setEnabled(false);

            //http://stackoverflow.com/questions/12841803/best-way-to-show-a-loading-spinner
            //Show a downloading spinner, so the user knows something is happening
            progress = new ProgressDialog(this);
            progress.setCancelable(false);
            progress.setTitle("Loading");
            progress.setMessage("Please wait while downloading...");
            progress.show();

            //Do all the database work such as create the database, tables, parse the JSON text and insert into tables
            //asyncronously, so it doesn't crash the app on the UI thread.

            //https://developer.android.com/reference/android/os/AsyncTask.html
            //http://hmkcode.com/android-parsing-json-data/
            new DownloadAPI().execute(api); //Download all exercises
            new DownloadMuscleAPI().execute(api2); //Download all muscle groups
            new DownloadImageAPI().execute(api3); //Download all image links
            new DownloadEquipmentAPI().execute(api4); //Download all equipment types
            new CreateExercisePerDayTable().execute(); //Create a table for the week
        }

        viewAllExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.activityIntent = new Intent(getApplicationContext(), ViewAllExercisesActivity.class);
                //MainActivity.this.activityIntent.putExtra("allexercises_arraylist", MainActivity.this.allExercisesArrayList);
                //MainActivity.this.bundle.putParcelableArrayList("allexercises_arraylist", MainActivity.this.allExercisesArrayList);
                //MainActivity.this.activityIntent.putExtras(MainActivity.this.bundle);

                startActivity(MainActivity.this.activityIntent);
            }
        });

        viewExerciseByMuscleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.activityIntent = new Intent(getApplicationContext(), ViewExercisesByMuscleActivity.class);
                //MainActivity.this.activityIntent.putExtra("jsonapi_allexercises", MainActivity.this.stringExerciseJSON);
                startActivity(MainActivity.this.activityIntent);
            }
        });

        displayNDoExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.activityIntent = new Intent(getApplicationContext(), DisplayAndDoExercisesActivity.class);
                startActivity(MainActivity.this.activityIntent);
            }
        });

        optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.activityIntent = new Intent(getApplicationContext(), OptionsActivity.class);
                startActivity(MainActivity.this.activityIntent);
            }
        });
        //*********** END OF Get a reference to all buttons on the home screen and set the on click listeners ****************


    }


    private class DownloadAPI extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                String jsonReturned = getAPI(urls[0]);

                //**************************** CALL SQLITE DATABASE HERE *****************************
                MainActivity.this.dbhelper = new DatabaseHelper(getApplicationContext());

                MainActivity.this.dbhelper.insertExercisesIntoDatabase(jsonReturned);
                //MainActivity.this.allExercisesArrayList = MainActivity.this.dbhelper.getExercisesFromDatabase(MainActivity.this.dbhelper);

                return "";
                //return getAPI(urls[0]);
            } catch (Exception e) {
                Log.d("AsyncException", e.toString());
            }

            return null;
        }
    }

    private class DownloadMuscleAPI extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                String jsonReturned = getAPI(urls[0]);

                //**************************** CALL SQLITE DATABASE HERE *****************************
                MainActivity.this.dbhelper = new DatabaseHelper(getApplicationContext());
                MainActivity.this.dbhelper.insertMusclesIntoDatabase(jsonReturned);

                return "";
                //return getAPI(urls[0]);
            } catch (Exception e) {
                Log.d("AsyncException", e.toString());
            }

            return null;
        }
    }

    private class DownloadImageAPI extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                String jsonReturned = getAPI(urls[0]);

                //**************************** CALL SQLITE DATABASE HERE *****************************
                MainActivity.this.dbhelper = new DatabaseHelper(getApplicationContext());
                MainActivity.this.dbhelper.insertImageLinkIntoDatabase(jsonReturned);

                return "";

            } catch (Exception e) {
                Log.d("AsyncException", e.toString());
            }

            return null;
        }
    }

    private class DownloadEquipmentAPI extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                String jsonReturned = getAPI(urls[0]);

                //**************************** CALL SQLITE DATABASE HERE *****************************
                MainActivity.this.dbhelper = new DatabaseHelper(getApplicationContext());
                MainActivity.this.dbhelper.insertEquipmentsIntoDatabase(jsonReturned);

                return "";

            } catch (Exception e) {
                Log.d("AsyncException", e.toString());
            }

            return null;
        }
    }

    private class CreateExercisePerDayTable extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            try {
                //**************************** CALL SQLITE DATABASE HERE *****************************
                MainActivity.this.dbhelper = new DatabaseHelper(getApplicationContext());
                MainActivity.this.dbhelper.insertDays();

                return "";
            } catch (Exception e) {
                Log.d("AsyncException", e.toString());
            }

            return null;
        }

        //This is the last async process, so enable the activity UI and dismiss the loader when everything is done
        protected void onPostExecute(String result) {
            Log.d("Post Executed", result);

            MainActivity.this.progress.dismiss(); //Disable the loading box when done downloading

            //MainActivity.this.flag = 1; //Enable all the menu items again
            //invalidateOptionsMenu();

            //Enable all the main menu buttons again
            MainActivity.this.viewAllExerciseBtn.setEnabled(true);
            MainActivity.this.viewExerciseByMuscleBtn.setEnabled(true);
            MainActivity.this.displayNDoExerciseBtn.setEnabled(true);
            MainActivity.this.optionsBtn.setEnabled(true);

        }
    }

    //http://zenit.senecac.on.ca/wiki/index.php/MAP524/DPS924_Lab_7
    //Use the same code to check for a HttpURLConnection that works and doesn't give an error
    private String getAPI(String urlString) throws IOException {
        InputStream in = null;
        int response = -1;
        String result = "";

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }

        result = convertInputStreamToString(in);

        return result;
    }

    //http://zenit.senecac.on.ca/wiki/index.php/MAP524/DPS924_Lab_7
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    //Check if there is an existing database already, no point in download and creating new one if it is there.
    private boolean checkDatabaseExist() {
        String path = "/data/data/" + getApplicationContext().getPackageName() + "/databases/fitness_database.db";

        File file = new File(path);

        return file.exists() ? true : false;
    }
}
