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

public class DisplayAndDoExercisesActivity extends AppCompatActivity {

    private Intent activityDisplayAndDoIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_and_do_exercises);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Manually add the days of a week to the listview, because it is always fixed to 7 days.
        ArrayList<String> daysPerWeek = new ArrayList<>();
        daysPerWeek.add("Monday");
        daysPerWeek.add("Tuesday");
        daysPerWeek.add("Wednesday");
        daysPerWeek.add("Thursday");
        daysPerWeek.add("Friday");
        daysPerWeek.add("Saturday");
        daysPerWeek.add("Sunday");

        ListView lv = (ListView) findViewById(R.id.displayNDoExerciseList);
        ArrayAdapter aa = new ArrayAdapter<String>(this, R.layout.content_view_all_exercise__list_view,daysPerWeek);
        lv.setAdapter(aa);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DisplayAndDoExercisesActivity.this.activityDisplayAndDoIntent = new Intent(getApplicationContext(), ExercisesPerDayActivity.class);
                DisplayAndDoExercisesActivity.this.activityDisplayAndDoIntent.putExtra("dayperweek", (position + 1));
                startActivity(DisplayAndDoExercisesActivity.this.activityDisplayAndDoIntent);
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
            activityDisplayAndDoIntent = new Intent(this, MainActivity.class);
            //activityDisplayAndDoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(activityDisplayAndDoIntent);
        }

        return super.onOptionsItemSelected(item);
    }

}
