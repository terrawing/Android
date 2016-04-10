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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewExercisesByMuscleActivity extends AppCompatActivity {

    private Intent activityExerciseByMuscleListIntent;
    private ArrayList<Muscle> allMusclesArraylist = new ArrayList<Muscle>();
    private ArrayList<String> all_namelist = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_exercises_by_muscle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d("ListView", "List muscles is running...");

        DatabaseHelper dbhelper = new DatabaseHelper(getApplicationContext());
        allMusclesArraylist = dbhelper.getMusclesFromDatabase();

        int i = 0;
        while (i < allMusclesArraylist.size()) {
            all_namelist.add((allMusclesArraylist.get(i)).GetMuscleName());
            i++;
        }

        String[] muscleName_Array = new String[all_namelist.size()];
        muscleName_Array = all_namelist.toArray(muscleName_Array);


        ListView lv = (ListView) findViewById(R.id.viewAllMusclesList);
        ArrayAdapter aa = new ArrayAdapter<String>(this, R.layout.content_view_all_exercise__list_view, muscleName_Array);
        lv.setAdapter(aa);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewExercisesByMuscleActivity.this.activityExerciseByMuscleListIntent = new Intent(getApplicationContext(), ExercisesByMuscleActivity.class);
                Muscle mus = new Muscle();
                mus = ViewExercisesByMuscleActivity.this.allMusclesArraylist.get((int) id); //get the muscle _id
                Log.d("ID", Integer.toString(mus.GetMuscleId()));
                ViewExercisesByMuscleActivity.this.activityExerciseByMuscleListIntent.putExtra("muscleid", mus.GetMuscleId());
                startActivity(ViewExercisesByMuscleActivity.this.activityExerciseByMuscleListIntent);
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
            activityExerciseByMuscleListIntent = new Intent(this, MainActivity.class);
            //activityExerciseByMuscleListIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(activityExerciseByMuscleListIntent);
        }

        return super.onOptionsItemSelected(item);
    }

}
