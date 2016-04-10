package com.example.student.doyouevenliftbro;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class ViewAllExercisesActivity extends AppCompatActivity {

    private Intent activityExerciseIntent;
    //private Bundle bundle = new Bundle();
    private ArrayList<Exercise> allExercisesArraylist = new ArrayList<Exercise>();
    private ArrayList<String> all_namelist = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_exercises);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d("ListView", "List is running...");

        //if(getIntent().getExtras() == null) {
            DatabaseHelper dbhelper = new DatabaseHelper(getApplicationContext());
            allExercisesArraylist = dbhelper.getExercisesFromDatabase();
        //}
        /*else {
            //http://stackoverflow.com/questions/12092612/pass-list-of-objects-from-one-activity-to-other-activity-in-android
            bundle = getIntent().getExtras();
            allExercisesArraylist = bundle.getParcelableArrayList("allexercises_arraylist");
        }*/

        int i = 0;
        while (i < allExercisesArraylist.size()) {
            all_namelist.add((allExercisesArraylist.get(i)).GetExerciseName());
            i++;
        }

        String[] exerciseName_Array = new String[all_namelist.size()];
        exerciseName_Array = all_namelist.toArray(exerciseName_Array);


        ListView lv = (ListView) findViewById(R.id.viewAllExerciseList);
        ArrayAdapter aa = new ArrayAdapter<String>(this, R.layout.content_view_all_exercise__list_view, exerciseName_Array);
        lv.setAdapter(aa);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("ListView", "clicked");
                //Just need to pass and use  1 class object instead of an arraylist of class objects
                ViewAllExercisesActivity.this.activityExerciseIntent = new Intent(getApplicationContext(), AllExerciseDisplayActivity.class);
                Exercise ex = new Exercise();
                ex = ViewAllExercisesActivity.this.allExercisesArraylist.get((int) id); //get the exercise _id
                Log.d("ID", Integer.toString(ex.GetExerciseId()));
                ViewAllExercisesActivity.this.activityExerciseIntent.putExtra("exerciseid", ex.GetExerciseId());
                //ViewAllExercisesActivity.this.bundle.putParcelable("selected_exercise_class_object", ViewAllExercisesActivity.this.allExercisesArraylist.get(position));

                //ViewAllExercisesActivity.this.bundle.putParcelableArrayList("allexercises_arraylist", ViewAllExercisesActivity.this.allExercisesArraylist);
                //ViewAllExercisesActivity.this.activityExerciseIntent.putExtras(ViewAllExercisesActivity.this.bundle);
                //ViewAllExercisesActivity.this.activityExerciseIntent.putExtras(ViewAllExercisesActivity.this.bundle2);

                //startActivityForResult(ViewAllExercisesActivity.this.activityExerciseIntent, 100);
                startActivity(ViewAllExercisesActivity.this.activityExerciseIntent);
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
            activityExerciseIntent = new Intent(this, MainActivity.class);
            //activityExerciseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(activityExerciseIntent);
        }

        return super.onOptionsItemSelected(item);
    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //http://stackoverflow.com/questions/10407159/how-to-manage-startactivityforresult-on-android
        if (requestCode == 100) {
            if(resultCode == Activity.RESULT_OK){
                Log.d("Passback", "stuff was passed back");
                ViewAllExercisesActivity.this.allExercisesArraylist = data.getParcelableArrayListExtra("allexercises_arraylist");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
*/
}


