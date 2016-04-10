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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class AddExerciseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String dateSelected = "Monday"; //default to Monday
    private int dateSelectedInteger;
    private int exerciseId = 0;
    private Exercise selectedExercise;
    private Intent addExerciseIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerDates);
        Button submitBtn = (Button) findViewById(R.id.addExerciseSubmit);

        exerciseId = getIntent().getExtras().getInt("exerciseid");

        if(exerciseId == 0) {
            submitBtn.setEnabled(false);
        }
        else {
            selectedExercise = new Exercise();
            DatabaseHelper dbhelper = new DatabaseHelper(getApplicationContext());
            selectedExercise = dbhelper.getAnExerciseFromDatabase(exerciseId);
        }

        //http://www.tutorialspoint.com/android/android_spinner_control.htm
        // Spinner Drop down elements
        ArrayList<String> dates = new ArrayList<String>();
        dates.add("Monday");
        dates.add("Tuesday");
        dates.add("Wednesday");
        dates.add("Thursday");
        dates.add("Friday");
        dates.add("Saturday");
        dates.add("Sunday");

        spinner.setOnItemSelectedListener(this);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dates);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        submitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatabaseHelper dbhelper = new DatabaseHelper(getApplicationContext());

                switch(AddExerciseActivity.this.dateSelected) {
                    case "Monday":
                        AddExerciseActivity.this.dateSelectedInteger = 1;
                        break;

                    case "Tuesday":
                        AddExerciseActivity.this.dateSelectedInteger = 2;
                        break;

                    case "Wednesday":
                        AddExerciseActivity.this.dateSelectedInteger = 3;
                        break;

                    case "Thursday":
                        AddExerciseActivity.this.dateSelectedInteger = 4;
                        break;

                    case "Friday":
                        AddExerciseActivity.this.dateSelectedInteger = 5;
                        break;

                    case "Saturday":
                        AddExerciseActivity.this.dateSelectedInteger = 6;
                        break;

                    case "Sunday":
                        AddExerciseActivity.this.dateSelectedInteger = 7;
                        break;

                    default:
                }

                boolean flag = dbhelper.insertExercisePerDayIntoDatabase(AddExerciseActivity.this.dateSelectedInteger, AddExerciseActivity.this.exerciseId);
                if(flag){
                    Toast.makeText(getBaseContext(), "Exercise has been added to " + AddExerciseActivity.this.dateSelected, Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getBaseContext(), "Exercise already set for this day, choose another exercise or day", Toast.LENGTH_LONG).show();
                }

                AddExerciseActivity.this.addExerciseIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(AddExerciseActivity.this.addExerciseIntent);

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        AddExerciseActivity.this.dateSelected = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
            addExerciseIntent = new Intent(this, MainActivity.class);
            //addExerciseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(addExerciseIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}
