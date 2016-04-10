package com.example.student.doyouevenliftbro;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ExercisesByMuscleActivity extends AppCompatActivity {

    private Intent activityExercisesFromMuscleListIntent;
    private ArrayList<Exercise> allExercisesByMuscleArrayList = new ArrayList<Exercise>();
    private ArrayList<String> all_namelist = new ArrayList<String>();
    private int muscleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_by_muscle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d("ListView", "List exercises by muscle is running...");

        muscleId = getIntent().getExtras().getInt("muscleid");

        DatabaseHelper dbhelper = new DatabaseHelper(getApplicationContext());
        allExercisesByMuscleArrayList = dbhelper.getExercisesByMuscleFromDatabase(muscleId);

        int i = 0;
        while (i < allExercisesByMuscleArrayList.size()) {
            all_namelist.add((allExercisesByMuscleArrayList.get(i)).GetExerciseName());
            i++;
        }

        String[] exerciseName_Array = new String[all_namelist.size()];
        exerciseName_Array = all_namelist.toArray(exerciseName_Array);


        ListView lv = (ListView) findViewById(R.id.viewAllExerciseByMuscleList);
        ArrayAdapter aa = new ArrayAdapter<String>(this, R.layout.content_view_all_exercise__list_view, exerciseName_Array);
        lv.setAdapter(aa);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("ListView", "clicked exercises by muscle");
                //Just need to pass and use  1 class object instead of an arraylist of class objects
                ExercisesByMuscleActivity.this.activityExercisesFromMuscleListIntent = new Intent(getApplicationContext(), AllExerciseDisplayActivity.class);
                Exercise ex = new Exercise();
                ex = ExercisesByMuscleActivity.this.allExercisesByMuscleArrayList.get((int) id); //get the exercise _id
                Log.d("ID", Integer.toString(ex.GetExerciseId()));
                ExercisesByMuscleActivity.this.activityExercisesFromMuscleListIntent.putExtra("exerciseid", ex.GetExerciseId());

                startActivity(ExercisesByMuscleActivity.this.activityExercisesFromMuscleListIntent);
            }
        });

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
            activityExercisesFromMuscleListIntent = new Intent(this, MainActivity.class);
            //activityExercisesFromMuscleListIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(activityExercisesFromMuscleListIntent);
        }

        return super.onOptionsItemSelected(item);
    }

}
