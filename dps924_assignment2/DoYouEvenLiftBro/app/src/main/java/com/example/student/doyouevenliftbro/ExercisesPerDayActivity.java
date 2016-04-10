package com.example.student.doyouevenliftbro;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ExercisesPerDayActivity extends AppCompatActivity {

    private String day;
    private String allExercisesIdCommaString;
    private ArrayList<Exercise> allExercisesPerDay = new ArrayList<>();
    private ArrayList<String> allExercisesNames = new ArrayList<>();
    private Intent exercisePerDayIntent;
    private DynamicListView listView;

    //http://stackoverflow.com/questions/7916834/android-adding-listview-sub-item-text
    private List<Map<String, String>> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_per_day);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get the string version of the day that is in an integer
        day = getTitle(getIntent().getExtras().getInt("dayperweek"));

        //http://stackoverflow.com/questions/31096275/how-to-remove-app-title-from-toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(day);
        //toolbar.setSubtitle("");

        DatabaseHelper dbhelper = new DatabaseHelper(getApplicationContext());
        allExercisesIdCommaString = dbhelper.getExercisesPerDayList(getIntent().getExtras().getInt("dayperweek"));

        //http://stackoverflow.com/questions/7488643/how-to-convert-comma-separated-string-to-arraylist
        ArrayList<String> exerciseIds = new ArrayList(Arrays.asList(allExercisesIdCommaString.split(",")));


        if(!allExercisesIdCommaString.equals("")) {
            for (int i = 0; i < exerciseIds.size(); i++) {
                allExercisesPerDay.add(dbhelper.getAnExerciseFromDatabase(Integer.parseInt(exerciseIds.get(i))));
                //allExercisesNames.add(allExercisesPerDay.get(i).GetExerciseName());

                //http://stackoverflow.com/questions/7916834/android-adding-listview-sub-item-text
                TreeMap<String, String> datum = new TreeMap<>();
                datum.put("exerciseid", Integer.toString(allExercisesPerDay.get(i).GetExerciseId()));
                datum.put("exercisename", allExercisesPerDay.get(i).GetExerciseName());
                datum.put("exercisemaxweight", Integer.toString(allExercisesPerDay.get(i).GetMaxWeight()));
                datum.put("exercisedate", allExercisesPerDay.get(i).GetExerciseDate());

                data.add(datum);
            }
        }

/*
        //This uses the regular simple adapter
        //Uncomment to use

        ListView lv = (ListView) findViewById(R.id.simpleListView);

        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.content_custom__list_view,
                new String[] {"exercisename", "exercisemaxweight", "exercisedate" },
                new int[] {R.id.exerciseNameText, R.id.exerciseWeightText, R.id.exerciseDateText });

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String exerciseId = "";
                String exerciseName = "";

                //http://www.mkyong.com/java/how-to-loop-a-map-in-java/
                //Get the id of the exercise, long id won't get the _id in the sqlite database
                for (Map.Entry<String, String> entry : data.get(position).entrySet()) {
                    if ((entry.getKey()).equals("exerciseid")) {
                        exerciseId = entry.getValue();
                    }

                    if ((entry.getKey()).equals("exercisename")) {
                        exerciseName = entry.getValue();
                    }

                }

                exercisePerDayIntent = new Intent(getApplicationContext(), DoExerciseDisplayActivity.class);
                //_intent.putExtra("newlist", getExercseList());
                exercisePerDayIntent.putExtra("exercisetitle", exerciseName);
                exercisePerDayIntent.putExtra("exerciseid", Integer.parseInt(exerciseId));

                startActivity(exercisePerDayIntent);
            }
        });

        registerForContextMenu(lv);
*/

/*

        Used for draggable listview.

        Can't get the onclick remove button to work by default inside a custom adapter listview. Unless I drag something first because
        I need the getPositionForID(itemID) method to run to get the view which has the button in the list. The getPositionForID(ItemID) is called in the handleCellSwitch()
        method. (Also it is sort of in a loop because I need the itemID by default to use the getPosition method which returns the item position ID that is itemID)

        The handleCellSwitch() method is called when AbsListView.OnScrollListener listens for any user cell swapping in the listview.
         I do not know how to check when the state is at default, which is when the user does not do anything yet before dragging an item.



   */

        //http://stackoverflow.com/questions/7916834/android-adding-listview-sub-item-text
        StableArrayAdapter adapter = new StableArrayAdapter(this, data, R.layout.content_custom__list_view, new String[] {"exercisename", "exercisemaxweight"}, new int[] {R.id.exerciseNameText, R.id.exerciseWeightText});
        listView = (DynamicListView) findViewById(R.id.draggableListView);

        listView.checkAndGetId(allExercisesPerDay);

        listView.setExerciseList(data); //MAKE SURE YOU SET THIS TOO WITH THE ARRAYLIST********************************
        listView.setDay(getIntent().getExtras().getInt("dayperweek"));
        listView.setActivity(this, getIntent());
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    //I need an onPause() method here because when the user rearrange an item, it will not update the database the order of the new
    //exercises per day. It is too complicated to write it directly in the DynamicListView class because I am having a hard time doing
    //a walkthrough and understanding when an exercise item is moved between each item.

    //So therefore when the user does anything that changes the view of this current activity. It will update the database with the new order
    //of exercises per day.
    @Override
    public void onPause() {
        super.onPause();

        //get the current order of the list, which should be rearranged already.
        String newExList = listView.getExerciseList();
        Log.d("OnPause", "Update list string: " + newExList);

        //UPDATE THE DATABASE WITH NEW REARRANGE ITEMS
        DatabaseHelper dbhelper = new DatabaseHelper(getApplicationContext());

        //Get the updated exercises per day string that is reordered, then update database to use the new reorder one
        //String newExList = dbhelper.getExercisesPerDayList(getIntent().getExtras().getInt("dayperweek"));

        dbhelper.updateExercisesPerDay(getIntent().getExtras().getInt("dayperweek"), newExList);

    }

    public String getTitle(int dayPerWeek) {
        String day = "";

        switch(dayPerWeek) {
            case 1:
                day = "Monday";
                break;

            case 2:
                day = "Tuesday";
                break;

            case 3:
                day = "Wednesday";
                break;

            case 4:
                day = "Thursday";
                break;

            case 5:
                day = "Friday";
                break;

            case 6:
                day = "Saturday";
                break;

            case 7:
                day = "Sunday";
                break;

            default:
        }

        return day;
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
            exercisePerDayIntent = new Intent(this, MainActivity.class);
            //exercisePerDayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(exercisePerDayIntent);
        }

        return super.onOptionsItemSelected(item);
    }

}
