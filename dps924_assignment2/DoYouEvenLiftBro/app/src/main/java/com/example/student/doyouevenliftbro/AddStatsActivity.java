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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class AddStatsActivity extends AppCompatActivity {

    private int exerciseId;
    private String names;
    private String date;
    private int maxWeight = -1;
    private int maxRep = -1;
    private int numOfSets = 0;
    private Intent addStatsIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        names = getIntent().getExtras().getString("exercisetitle");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(names + " (Stats)");

        exerciseId = getIntent().getExtras().getInt("exerciseid");

        Button addStatsSubmitBtn = (Button) findViewById(R.id.addStatsSubmit);

        addStatsSubmitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatabaseHelper dbhelper = new DatabaseHelper(getApplicationContext());

                //http://stackoverflow.com/questions/6421874/how-to-get-the-date-from-the-datepicker-widget-in-android
                DatePicker datePicker = (DatePicker) findViewById(R.id.datepicker);
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();

                //EditText max weight
                EditText editTextMaxWeight = (EditText) findViewById(R.id.maxweight);
                String maxWeightString = editTextMaxWeight.getText().toString();
                if(!maxWeightString.equals(""))
                    maxWeight = Integer.parseInt(maxWeightString);

                //EditText num of sets
                EditText editTextSet = (EditText) findViewById(R.id.numofsets);
                String numOfSetsString = editTextSet.getText().toString();
                if(!numOfSetsString.equals(""))
                    numOfSets = Integer.parseInt(numOfSetsString);

                //EditText max rep
                EditText editTextMaxRep = (EditText) findViewById(R.id.maxrep);
                String maxRepString = editTextMaxRep.getText().toString();
                if(!maxRepString.equals(""))
                    maxRep = Integer.parseInt(maxRepString);

                Log.d("date", Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year));
                date = Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year);

                //Do some form checking here, to make sure the user has enter the right data and give an error message if something is wrong
                if(maxWeight == -1 || maxRep == -1) {
                    Toast.makeText(getBaseContext(), "Max weight or max reps cannot be empty", Toast.LENGTH_LONG).show();
                }
                else if(numOfSets == 0) {
                    Toast.makeText(getBaseContext(), "Cannot have zero sets, must be 1 or more", Toast.LENGTH_LONG).show();
                }
                else {
                    boolean flag = dbhelper.insertExerciseStatsIntoDatabase(exerciseId, date, maxWeight, maxRep, numOfSets);

                    if(flag) {
                        Toast.makeText(getBaseContext(), "Stats had been added to the exercise: " + names, Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getBaseContext(), "Error in database", Toast.LENGTH_LONG).show();
                    }

                    AddStatsActivity.this.addStatsIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(AddStatsActivity.this.addStatsIntent);
                }

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
            addStatsIntent = new Intent(this, MainActivity.class);
            //addStatsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(addStatsIntent);
        }

        return super.onOptionsItemSelected(item);
    }

}
