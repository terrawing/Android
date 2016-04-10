package com.example.student.doyouevenliftbro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

public class AllExerciseDisplayActivity extends AppCompatActivity {

    private Intent activityAllExerciseIntent;
    private int exerciseId;
    private Exercise selectedExercise;
    private String imageLink;
    private ProgressDialog progress;
    private int act;
    private ArrayList<String> muscleNames = new ArrayList<String>();
    private ArrayList<String> equipmentNames = new ArrayList<String>();

    //private ArrayList<Exercise> allExercisesArraylist = new ArrayList<Exercise>();
    //private Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allexercisedisplay);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        bundle = getIntent().getExtras();
        Exercise selectedExercise = new Exercise();
        selectedExercise = bundle.getParcelable("selected_exercise_class_object");
        allExercisesArraylist = bundle.getParcelableArrayList("allexercises_arraylist");

        activityAllExerciseIntent = new Intent(getApplicationContext(), ViewAllExercisesActivity.class);
        bundle.putParcelableArrayList("allexercises_arraylist", this.allExercisesArraylist);
        activityAllExerciseIntent.putExtras(bundle);

        setResult(Activity.RESULT_OK, activityAllExerciseIntent);
*/

        exerciseId = getIntent().getExtras().getInt("exerciseid");
        selectedExercise = new Exercise();
        DatabaseHelper dbhelper = new DatabaseHelper(getApplicationContext());
        selectedExercise =  dbhelper.getAnExerciseFromDatabase(exerciseId);
        imageLink = dbhelper.getImageLinkFromDatabase(exerciseId);
        muscleNames = dbhelper.getMuscleNamesByIdFromDatabase(selectedExercise.GetExerciseMusclesMain());
        equipmentNames = dbhelper.getEquipmentNamesByIdFromDatabase(selectedExercise.GetEquipment());

        Log.d("ImageLink", imageLink);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(selectedExercise.GetExerciseName());

        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setTitle("Loading Exercise");
        progress.setMessage("Please wait while downloading...");
        progress.show();


        new LoadImageFromUrl().execute(imageLink);




        //http://stackoverflow.com/questions/7488643/how-to-convert-comma-separated-string-to-arraylist ************************************


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
            activityAllExerciseIntent = new Intent(this, MainActivity.class);
            //activityAllExerciseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(activityAllExerciseIntent);
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

            Button addExerciseBtn = (Button) findViewById(R.id.addExercise);

            addExerciseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AllExerciseDisplayActivity.this.activityAllExerciseIntent = new Intent(getApplicationContext(), AddExerciseActivity.class);
                    Log.d("ID", Integer.toString(exerciseId));
                    AllExerciseDisplayActivity.this.activityAllExerciseIntent.putExtra("exerciseid", exerciseId);
                    startActivity(AllExerciseDisplayActivity.this.activityAllExerciseIntent);
                }
            });



            AllExerciseDisplayActivity.this.progress.dismiss(); //Disable the loading box when done downloading
        }
    }



}
