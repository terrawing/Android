package com.example.student.doyouevenliftbro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

public class DoExerciseDisplayActivity extends AppCompatActivity {

    private Intent activityDoExerciseIntent;
    private int exerciseId;
    private Exercise selectedExercise;
    private String imageLink;
    private ProgressDialog progress;
    private String names;
    private ArrayList<String> muscleNames = new ArrayList<>();
    private ArrayList<String> equipmentNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_exercise_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get the put extras of the ExercisesPerDayActivity / DynamicListView
        names = getIntent().getExtras().getString("exercisetitle");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(names);

        exerciseId = getIntent().getExtras().getInt("exerciseid");
        selectedExercise = new Exercise();

        //Get the full exercise row from database,
        //Get the image link of the exercise,
        //Get the muscle names for the exercise,
        //Get the equipment names for the exercise
        DatabaseHelper dbhelper = new DatabaseHelper(getApplicationContext());
        selectedExercise =  dbhelper.getAnExerciseFromDatabase(exerciseId);
        imageLink = dbhelper.getImageLinkFromDatabase(exerciseId);
        muscleNames = dbhelper.getMuscleNamesByIdFromDatabase(selectedExercise.GetExerciseMusclesMain());
        equipmentNames = dbhelper.getEquipmentNamesByIdFromDatabase(selectedExercise.GetEquipment());

        Log.d("ImageLink", imageLink);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(selectedExercise.GetExerciseName());

        //Set a loader here to prevent the user from doing anything on the activity till the image is downloaded and displayed
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setTitle("Loading Exercise");
        progress.setMessage("Please wait while downloading...");
        progress.show();

        //Load image asyncronously in a separate thread
        new LoadImageFromUrl().execute(imageLink);

        //toolbar.setSubtitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //http://stackoverflow.com/questions/13160899/back-arrow-missing-in-my-activity
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.menu_home) {
            //http://stackoverflow.com/questions/3473168/clear-the-entire-history-stack-and-start-a-new-activity-on-android
            activityDoExerciseIntent = new Intent(this, MainActivity.class);
            //activityDoExerciseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(activityDoExerciseIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoadImageFromUrl extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... link) {
            //http://stackoverflow.com/questions/5776851/load-image-from-url

            try {
                URL url = new URL(link[0]);
                Bitmap bmp;

                //Check if image was downloaded successfully, whether if the image is missing, server problems, user loses wifi connection...etc
                try {
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (Exception e) { //return null if there is no internet, so use the default image
                    Log.d("ConnectionEr", e.toString());
                    bmp = null;
                }

                //https://guides.codepath.com/android/Working-with-the-ImageView
                //Bitmap bMapScaled = Bitmap.createScaledBitmap(bmp, 300, 100, true);
                return bmp;
                //return bMapScaled;
            } catch (Exception ex) {
                return null;
            }

            //return null;
        }

        protected void onPostExecute(Bitmap result) {
            ImageView img = (ImageView) findViewById(R.id.exerciseImage);

            //If no problems, the result will return anything besides null, just check for that and set the image.
            //If any problems, it will return null, so just use the default no image picture.
            if(result != null)
                img.setImageBitmap(result);
            else {
                img.setImageResource(R.drawable.no_image);
            }

            TextView dateTv = (TextView) findViewById(R.id.dateExercisePerformed);
            dateTv.setText(selectedExercise.GetExerciseDate());

            TextView maxWeightTv = (TextView) findViewById(R.id.maxWeightExercisePerformed);
            if(selectedExercise.GetMaxWeight() < 2)
                maxWeightTv.setText(Integer.toString(selectedExercise.GetMaxWeight()) + " lb");
            else
                maxWeightTv.setText(Integer.toString(selectedExercise.GetMaxWeight()) + " lbs");

            TextView setTv = (TextView) findViewById(R.id.maxSetExercisePerformed);
            setTv.setText(Integer.toString(selectedExercise.GetSet()));

            TextView maxRepTv = (TextView) findViewById(R.id.maxRepExercisePerformed);
            maxRepTv.setText(Integer.toString(selectedExercise.GetMaxRep()));

            TextView descTv = (TextView) findViewById(R.id.exerciseDescription);

            //Because the api returns some weird characters entered by the user who entered the data, it might use some illegal characters
            //that affect writing to the sqlite database, so I have to URLEncode it to make sure I can write to the database.
            //Right here, I am just decoding the weird characters back to a regular string and set it to the description
            try {
                descTv.setText(URLDecoder.decode(selectedExercise.GetExerciseDescription(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            TextView muscTv = (TextView) findViewById(R.id.targetMuscleForExercisePerformed);
            String mNames = "";

            for(int i = 0; i < muscleNames.size(); i++) {
                if(muscleNames.size() > 1) {
                    if(i != (muscleNames.size() - 1))
                        mNames = mNames + muscleNames.get(i) + ", ";
                    else
                        mNames += muscleNames.get(i);
                }
                else {
                    mNames += muscleNames.get(i);
                }
            }
            muscTv.setText(mNames);

            TextView eqpTv = (TextView) findViewById(R.id.equipmentUsedForExercisePerformed);
            String eNames = "";

            for(int i = 0; i < equipmentNames.size(); i++) {
                if(equipmentNames.size() > 1) {
                    if(i != (equipmentNames.size() - 1))
                        eNames = eNames + equipmentNames.get(i) + ", ";
                    else
                        eNames += equipmentNames.get(i);
                }
                else {
                    eNames += equipmentNames.get(i);
                }
            }
            eqpTv.setText(eNames);

            Button addStatsBtn = (Button) findViewById(R.id.addStats);

            addStatsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DoExerciseDisplayActivity.this.activityDoExerciseIntent = new Intent(getApplicationContext(), AddStatsActivity.class);
                    Log.d("ID", Integer.toString(exerciseId));
                    DoExerciseDisplayActivity.this.activityDoExerciseIntent.putExtra("exerciseid", exerciseId);
                    DoExerciseDisplayActivity.this.activityDoExerciseIntent.putExtra("exercisetitle", names);
                    startActivity(DoExerciseDisplayActivity.this.activityDoExerciseIntent);
                }
            });



            DoExerciseDisplayActivity.this.progress.dismiss(); //Disable the loading box when done downloading
        }
    }

}
