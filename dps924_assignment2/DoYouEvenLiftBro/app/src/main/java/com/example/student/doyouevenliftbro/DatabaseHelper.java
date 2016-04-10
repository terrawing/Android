package com.example.student.doyouevenliftbro;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.Html;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by student on 3/19/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    private Context context;
    private SQLiteDatabase database;
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "fitness_database.db";
    private String DB_PATH;

    //Exercise
    private ArrayList<Integer> ids = new ArrayList<Integer>();
    private ArrayList<String> names = new ArrayList<String>();
    private ArrayList<String> descriptions = new ArrayList<String>();
    private ArrayList<String> muscles_main = new ArrayList<String>();
    private ArrayList<String> muscles_secondary = new ArrayList<>();
    private ArrayList<String> equipments = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();

    //Muscle
    private ArrayList<Integer> m_ids = new ArrayList<Integer>();
    private ArrayList<String>  m_names = new ArrayList<String>();

    //images
    private ArrayList<Integer> i_ids = new ArrayList<Integer>();
    private ArrayList<String> i_links = new ArrayList<String>();

    //equipments
    private ArrayList<Integer> e_ids = new ArrayList<Integer>();
    private ArrayList<String> e_names = new ArrayList<String>();

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d("Context", context.toString());
        this.context = context;
        this.DB_PATH = "/data/data/" + this.context.getPackageName() + "/databases/";
    }

    //Default databasehelper onCreate method where it creates the tables inside the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("SQLITE ON CREATE", "Creating...");
        this.database = db;
        db.execSQL("CREATE TABLE exercises (_id INTEGER PRIMARY KEY, name TEXT, description TEXT, muscle_main TEXT, muscle_secondary TEXT, equipment TEXT, date TEXT, max_weight INTEGER, max_rep INTEGER, num_set INTEGER);");
        db.execSQL("CREATE TABLE muscles (_id INTEGER PRIMARY KEY, name TEXT);");
        db.execSQL("CREATE TABLE images (_id INTEGER PRIMARY KEY, link TEXT);"); //the image id is the same as the exercise id
        db.execSQL("CREATE TABLE equipments (_id INTEGER PRIMARY KEY, name TEXT);");
        db.execSQL("CREATE TABLE exercise_dates (_id INTEGER PRIMARY KEY, exercise TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Fill the table for exercise dates using a number _id that corresponds to the days per week
    public void insertDays() {
        Log.d("INSERT DB", "Inserting just the days...");
        this.database = this.getWritableDatabase();

        this.database.execSQL("INSERT INTO exercise_dates VALUES(1, '');");
        this.database.execSQL("INSERT INTO exercise_dates VALUES(2, '');");
        this.database.execSQL("INSERT INTO exercise_dates VALUES(3, '');");
        this.database.execSQL("INSERT INTO exercise_dates VALUES(4, '');");
        this.database.execSQL("INSERT INTO exercise_dates VALUES(5, '');");
        this.database.execSQL("INSERT INTO exercise_dates VALUES(6, '');");
        this.database.execSQL("INSERT INTO exercise_dates VALUES(7, '');");

        this.database.close();
    }

    //Get the values entered by the user and update the exercises table for that exercise with new data
    public boolean insertExerciseStatsIntoDatabase(int exerciseId, String date, int maxWeight, int maxRep, int set) {
        Log.d("INSERT DB", "Update exercise stats per day...");

        boolean flag = false;

        this.database = this.getWritableDatabase();
        try {
            Log.d("UpdateD_Rep", date + " " + Integer.toString(maxWeight) + " " + Integer.toString(maxRep));
            this.database.execSQL("UPDATE exercises SET date = '" + date + "', max_weight = " + Integer.toString(maxWeight) + ", max_rep = " + Integer.toString(maxRep) + ", num_set = " + Integer.toString(set) + " WHERE _id = " + Integer.toString(exerciseId) + ";");
            flag = true;
        } catch (SQLException e) {
            Log.d("SQLUpError", e.toString());
        }

        this.database.close();

        return flag;
    }

    //Check for duplicate exercises in the exercises per day first before updating the string with a new string that is separated by commas
    public boolean insertExercisePerDayIntoDatabase(int day, int exerciseId) {
        Log.d("INSERT DB", "Update exercise per day...");

        String exerciseString = "";
        boolean flag = false;

        Cursor c = getReadableDatabase().rawQuery("SELECT exercise FROM exercise_dates WHERE _id = " + Integer.toString(day), null);

        if(c == null) {
            Log.d("ex db", "NOTHING");
        }

            if (c.getCount() == 0) {
                Log.d("NOTHING", "0 nothing");
            }

            c.moveToFirst();
            do {
                Log.d("exList", c.getString(c.getColumnIndex("exercise")));
                exerciseString += c.getString(c.getColumnIndex("exercise"));
            } while (c.moveToNext());

            c.close();
        getReadableDatabase().close();

        //Check for duplicate exercise in the string by separating by comma
        if(!exerciseString.equals("")) {
            ArrayList<String> exerciseIds = new ArrayList(Arrays.asList(exerciseString.split(",")));
            for (int i = 0; i < exerciseIds.size(); i++) {
                if (Integer.parseInt(exerciseIds.get(i)) == exerciseId)
                    return false;
            }
        }

        //append the exercise comma string with the new exercise
        Log.d("exString", exerciseString);
            if (exerciseString.equals("")) {
                exerciseString += Integer.toString(exerciseId);
            } else {
                exerciseString = exerciseString + "," + Integer.toString(exerciseId);
            }

        this.database = this.getWritableDatabase();
            try {
                Log.d("UpdateExPerDay", exerciseString);
                this.database.execSQL("UPDATE exercise_dates SET exercise = '" + exerciseString + "' WHERE _id = " + Integer.toString(day) + ";");
                flag = true;
            } catch (SQLException e) {
                Log.d("SQLUpError", e.toString());
            }

            this.database.close();


        return flag;
    }

    //Directly update the comma string of exercises per day
    //This is used for the ExercisesPerDayActivity / DynamicListView where I have to change and rearrange the order of the comma string
    public void updateExercisesPerDay(int day, String exercises) {
        Log.d("UPDATE DB", "Updating exercise list for day...");

        this.database = this.getWritableDatabase();
        try {
            Log.d("UpdateEPD", exercises);
            this.database.execSQL("UPDATE exercise_dates SET exercise = '" + exercises + "' WHERE _id = " + Integer.toString(day) + ";");
        } catch (SQLException e) {
            Log.d("SQLUpError", e.toString());
        }

        this.database.close();
    }

    //Get the JSON String and parse it to insert into the equipments table
    public void insertEquipmentsIntoDatabase(String jsonString) {
        Log.d("INSERT DB", "Inserting equipments...");

        try {
            JSONObject all = new JSONObject(jsonString);
            JSONArray results = all.getJSONArray("results");
            int i = 0;

            while(i < results.length()) {
                e_ids.add(results.getJSONObject(i).getInt("id"));
                e_names.add(results.getJSONObject(i).getString("name"));
                i++;
            }

        } catch (JSONException e) {
            Log.d("JSON ERROR", "JSON Parse Error " + e.getMessage());
        }

        //http://stackoverflow.com/questions/8082688/convert-arraylist-to-string-array-in-android
        Integer[] idArray = new Integer[e_ids.size()];
        idArray = e_ids.toArray(idArray);

        String[] nameArray = new String[e_names.size()];
        nameArray = e_names.toArray(nameArray);

        this.database = this.getWritableDatabase();
        for(int i = 0; i < e_ids.size(); i++) {
            this.database.execSQL("INSERT INTO equipments VALUES(" + idArray[i] + ", '" + nameArray[i] + "');");
        }

        this.database.close();
    }

    //Get the JSON String and parse it to insert into the images table
    public void insertImageLinkIntoDatabase(String jsonString) {
        Log.d("INSERT DB", "Inserting images...");

        try {
            JSONObject all = new JSONObject(jsonString);
            JSONArray results = all.getJSONArray("results");
            int i = 0;

            while(i < results.length()) {
                i_ids.add(results.getJSONObject(i).getInt("exercise"));
                i_links.add(results.getJSONObject(i).getString("image"));
                i++;
            }

        } catch (JSONException e) {
            Log.d("JSON ERROR", "JSON Parse Error " + e.getMessage());
        }

        //http://stackoverflow.com/questions/8082688/convert-arraylist-to-string-array-in-android
        Integer[] idArray = new Integer[i_ids.size()];
        idArray = i_ids.toArray(idArray);

        String[] linkArray = new String[i_links.size()];
        linkArray = i_links.toArray(linkArray);

        int duplicateFlag = 0;
        this.database = this.getWritableDatabase();
        for(int i = 0; i < i_ids.size(); i++) {

            //because the api has more than 1 image for the same exercise. I just want to use the first one.
            if(duplicateFlag != idArray[i]) {
                duplicateFlag = idArray[i];
                this.database.execSQL("INSERT INTO images VALUES(" + idArray[i] + ", '" + linkArray[i] + "');");
            }
        }

        this.database.close();
    }

    //Get the JSON String and parse it to insert into the muscles table
    public void insertMusclesIntoDatabase(String jsonString) {
        Log.d("INSERT DB", "Inserting muscles...");

        try {
            JSONObject all = new JSONObject(jsonString);
            JSONArray results = all.getJSONArray("results");
            int i = 0;

            while(i < results.length()) {
                m_ids.add(results.getJSONObject(i).getInt("id"));
                m_names.add(results.getJSONObject(i).getString("name"));
                i++;
            }

        } catch (JSONException e) {
            Log.d("JSON ERROR", "JSON Parse Error " + e.getMessage());
        }

        //http://stackoverflow.com/questions/8082688/convert-arraylist-to-string-array-in-android
        Integer[] idArray = new Integer[m_ids.size()];
        idArray = m_ids.toArray(idArray);

        String[] nameArray = new String[m_names.size()];
        nameArray = m_names.toArray(nameArray);

        this.database = this.getWritableDatabase();
        for(int i = 0; i < m_ids.size(); i++) {
            this.database.execSQL("INSERT INTO muscles VALUES(" + idArray[i] + ", '" + nameArray[i] + "');");
        }

        this.database.close();
    }

    //Get the JSON String and parse it to insert into the exercises table
    public void insertExercisesIntoDatabase(String jsonString) {
        Log.d("INSERT DB", "Inserting exercises...");

        try {
            JSONObject all = new JSONObject(jsonString);
            JSONArray results = all.getJSONArray("results");
            int i = 0;

            while(i < results.length()) {
                ids.add(results.getJSONObject(i).getInt("id"));
                names.add(results.getJSONObject(i).getString("name"));

                //http://stackoverflow.com/questions/2188049/parse-html-in-android
                //http://stackoverflow.com/questions/3056834/replace-n-and-r-n-with-br-in-java
                String desc = "";
                desc = results.getJSONObject(i).getString("description"); //*******************************************NEED TO ESCAPE /n
                try {
                    desc = Html.fromHtml(desc).toString();
                    desc = URLEncoder.encode(desc, "UTF-8"); //http://www.mkyong.com/java/how-to-encode-a-url-string-or-form-parameter-in-java/

                } catch (UnsupportedEncodingException e) {
                    Log.d("EncodingError", e.toString());
                }
                descriptions.add(desc);

                //http://stackoverflow.com/questions/6356231/android-how-to-convert-json-array-to-string-array

                //The for loops here are to make sure the middle of the string that is written end with a comma and the last one has no comma
                //Example: 12,56,98,50
                ArrayList<String> mainMusclesList = new ArrayList<String>();
                String concatMainMuscles = "";
                JSONArray mainMuscles = results.getJSONObject(i).getJSONArray("muscles");
                for (int j = 0; j < mainMuscles.length(); j++) {
                    mainMusclesList.add(mainMuscles.get(j).toString());
                }
                for(int j2 = 0; j2 < mainMusclesList.size(); j2++) {
                    if(j2 != (mainMusclesList.size() - 1))
                        concatMainMuscles = concatMainMuscles + mainMusclesList.get(j2) + ",";
                    else
                        concatMainMuscles += mainMusclesList.get(j2);
                }
                muscles_main.add(concatMainMuscles);

                ArrayList<String>  secondaryMusclesList = new ArrayList<String>();
                String concatSecondaryMuscles = "";
                JSONArray secondaryMuscles = results.getJSONObject(i).getJSONArray("muscles_secondary");
                for (int k = 0; k < secondaryMuscles.length(); k++) {
                    secondaryMusclesList.add(secondaryMuscles.get(k).toString());
                }
                for(int k2 = 0; k2 < secondaryMusclesList.size(); k2++) {
                    if(k2 != (secondaryMusclesList.size() - 1))
                        concatSecondaryMuscles = concatSecondaryMuscles + secondaryMusclesList.get(k2) + ",";
                    else
                        concatSecondaryMuscles += secondaryMusclesList.get(k2);
                }
                muscles_secondary.add(concatSecondaryMuscles);

                ArrayList<String>  equipmentsList = new ArrayList<String>();
                String concatEquipments = "";
                JSONArray eqs = results.getJSONObject(i).getJSONArray("equipment");

                if(eqs.length() > 0) {
                    for (int l = 0; l < eqs.length(); l++) {
                        equipmentsList.add(eqs.get(l).toString());
                    }
                    for (int l2 = 0; l2 < equipmentsList.size(); l2++) {
                        if (l2 != (equipmentsList.size() - 1))
                            concatEquipments = concatEquipments + equipmentsList.get(l2) + ",";
                        else
                            concatEquipments += equipmentsList.get(l2);
                    }
                }
                else {
                    concatEquipments += "0";
                }

                equipments.add(concatEquipments);

                //Get Image from another api ****************************************

                i++;
            }

        } catch (JSONException e) {
            Log.d("JSON ERROR", "JSON Parse Error " + e.getMessage());
        }

        //http://stackoverflow.com/questions/8082688/convert-arraylist-to-string-array-in-android
        Integer[] idArray = new Integer[ids.size()];
        idArray = ids.toArray(idArray);

        String[] nameArray = new String[names.size()];
        nameArray = names.toArray(nameArray);

        String[] descArray = new String[descriptions.size()];
        descArray = descriptions.toArray(descArray);

        String[] muscleMainArray = new String[muscles_main.size()];
        muscleMainArray = muscles_main.toArray(muscleMainArray);

        String[] muscleSecondaryArray = new String[muscles_secondary.size()];
        muscleSecondaryArray = muscles_secondary.toArray(muscleSecondaryArray);

        String[] equipmentArray = new String[equipments.size()];
        equipmentArray = equipments.toArray(equipmentArray);

        this.database = this.getWritableDatabase();
        for(int i = 0; i < ids.size(); i++) {
            this.database.execSQL("INSERT INTO exercises VALUES(" + idArray[i] + ", '" + nameArray[i] + "', '" + descArray[i] + "', '" + muscleMainArray[i] + "', '" + muscleSecondaryArray[i] + "', '" + equipmentArray[i] + "', '', 0, 0, 0);");
        }

        this.database.close();

    }

    //Get all exercises from the exercises table and return an arraylist class of exercises
    public ArrayList<Exercise> getExercisesFromDatabase() {
        Log.d("SELECT DB", "Selecting *....");

        //http://stackoverflow.com/questions/30611534/sqlitedatabase-rawqueryjava-lang-string-java-lang-string-on-a-null-object
        //http://stackoverflow.com/questions/2413427/how-to-use-sql-order-by-statement-to-sort-results-case-insensitive
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM exercises ORDER BY name COLLATE NOCASE", null);


        if(c == null) {
            Log.d("ex db", "NOTHING");
        }

            ArrayList<Exercise> temp = new ArrayList<Exercise>();

            c.moveToFirst();
            do {
                Exercise ex = new Exercise();
                ex.AddExercise(c.getInt(c.getColumnIndex("_id")),
                        c.getString(c.getColumnIndex("name")),
                        c.getString(c.getColumnIndex("description")),
                        c.getString(c.getColumnIndex("muscle_main")),
                        c.getString(c.getColumnIndex("muscle_secondary")),
                        c.getString(c.getColumnIndex("equipment")),
                        c.getString(c.getColumnIndex("date")),
                        c.getInt(c.getColumnIndex("max_weight")),
                        c.getInt(c.getColumnIndex("max_rep")),
                        c.getInt(c.getColumnIndex("num_set"))
                );

                temp.add(ex);
            } while (c.moveToNext());

            c.close();
        getReadableDatabase().close();

        return temp;
    }

    //Get an exercise from the exercises table and return a class of exercise
    public Exercise getAnExerciseFromDatabase(int exerciseId) {
        //Log.d("SELECT DB", "Selecting by id...");

        String sqlQuery = "SELECT * FROM exercises WHERE _id = " + exerciseId;
        Cursor c = getReadableDatabase().rawQuery(sqlQuery, null);

        if(c == null)
            Log.d("ex db", "NOTHING");

        Exercise ex = new Exercise();

        c.moveToFirst();
        do {

            ex.AddExercise(c.getInt(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex("name")),
                    c.getString(c.getColumnIndex("description")),
                    c.getString(c.getColumnIndex("muscle_main")),
                    c.getString(c.getColumnIndex("muscle_secondary")),
                    c.getString(c.getColumnIndex("equipment")),
                    c.getString(c.getColumnIndex("date")),
                    c.getInt(c.getColumnIndex("max_weight")),
                    c.getInt(c.getColumnIndex("max_rep")),
                    c.getInt(c.getColumnIndex("num_set"))
            );

        } while(c.moveToNext());

        c.close();
        getReadableDatabase().close();

        return ex;
    }

    //Get all muscles from the muscles table and return an arraylist class of muscles
    public ArrayList<Muscle> getMusclesFromDatabase() {
        Log.d("SELECT DB", "Selecting muscles....");

        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM muscles ORDER BY name COLLATE NOCASE", null);

        if(c == null)
            Log.d("ex db", "NOTHING");

        if(c.getCount() == 0) {
            Log.d("NOTHING", "0 nothing");
        }

        ArrayList<Muscle> temp = new ArrayList<Muscle>();

        c.moveToFirst();
        do {
            Muscle muscle = new Muscle();
            muscle.AddMuscle(c.getInt(c.getColumnIndex("_id")), c.getString(c.getColumnIndex("name")));

            temp.add(muscle);
        } while(c.moveToNext());

        c.close();
        getReadableDatabase().close();

        return temp;
    }

    //Get all muscle names from the muscles table and return an arraylist string of muscles
    public ArrayList<String> getMuscleNamesByIdFromDatabase(String commaString) {
        Log.d("SELECT DB", "Selecting muscles each one....");

        ArrayList<String> muscleNames = new ArrayList<String>();
        ArrayList<String> aList= new ArrayList<String>(Arrays.asList(commaString.split(",")));

        if(aList.size() > 1) {
            for (int i = 0; i < aList.size(); i++) {
                Cursor c = getReadableDatabase().rawQuery("SELECT * FROM muscles WHERE _id = " + aList.get(i), null);

                if (c == null)
                    Log.d("ex db", "NOTHING");

                if (c.getCount() == 0) {
                    Log.d("NOTHING", "0 nothing");
                }

                c.moveToFirst();
                do {
                    String name = c.getString(c.getColumnIndex("name"));
                    muscleNames.add(name);
                } while (c.moveToNext());

                c.close();
                getReadableDatabase().close();
            }
        }
        else {
            muscleNames.add("(none)");
        }

        return muscleNames;
    }

    //Get all exercises from the exercises table by their muscle group and return an arraylist class of exercises
    public ArrayList<Exercise> getExercisesByMuscleFromDatabase(int muscleId) {
        Log.d("SELECT DB", "Selecting exercises by muscle...");

        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM exercises WHERE muscle_main LIKE '%" + muscleId + "%' ORDER BY name COLLATE NOCASE", null);

        if(c == null)
            Log.d("ex db", "NOTHING");

        if(c.getCount() == 0) {
            Log.d("NOTHING", "0 nothing");
        }

        ArrayList<Exercise> temp = new ArrayList<Exercise>();

        c.moveToFirst();
        do {
            Exercise ex = new Exercise();
            ex.AddExercise(c.getInt(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex("name")),
                    c.getString(c.getColumnIndex("description")),
                    c.getString(c.getColumnIndex("muscle_main")),
                    c.getString(c.getColumnIndex("muscle_secondary")),
                    c.getString(c.getColumnIndex("equipment")),
                    c.getString(c.getColumnIndex("date")),
                    c.getInt(c.getColumnIndex("max_weight")),
                    c.getInt(c.getColumnIndex("max_rep")),
                    c.getInt(c.getColumnIndex("num_set"))
            );

            temp.add(ex);
        } while(c.moveToNext());

        c.close();
        getReadableDatabase().close();

        return temp;
    }

    //Get the url link of the image if it exists from the images table
    public String getImageLinkFromDatabase(int exerciseId) {
        Log.d("SELECT DB", "Selecting image...");

        Cursor c = getReadableDatabase().rawQuery("SELECT link FROM images WHERE _id = " + exerciseId, null);

        String link = "";

        try {
            if(c == null)
                Log.d("ex db", "NOTHING");

            if(c.getCount() == 0) {
                return "";
            }

            c.moveToFirst();
            do {
                link = c.getString(c.getColumnIndex("link"));
            } while (c.moveToNext());

        } catch (Exception e) {
            Log.d("ImageNothing", e.toString());
        } finally {
            c.close();
            getReadableDatabase().close();
        }



        return link;
    }

    //Get an arraylist of the equipment names from the equipments table based on the id
    public ArrayList<String> getEquipmentNamesByIdFromDatabase(String commaString) {
        Log.d("SELECT DB", "Selecting equipments each one....");

        ArrayList<String> equipmentNames = new ArrayList<String>();
        ArrayList<String> aList = new ArrayList(Arrays.asList(commaString.split(",")));

            for (int i = 0; i < aList.size(); i++) {
                Cursor c = getReadableDatabase().rawQuery("SELECT * FROM equipments WHERE _id = " + aList.get(i), null);

                if (c == null)
                    Log.d("ex db", "NOTHING");

                if (c.getCount() == 0) {
                    Log.d("NOTHING", "0 equipment needed");
                    equipmentNames.add("none (bodyweight exercise)");
                }
                else {
                    c.moveToFirst();
                    do {
                        String name = c.getString(c.getColumnIndex("name"));
                        equipmentNames.add(name);
                    } while (c.moveToNext());
                }
                c.close();
                getReadableDatabase().close();
            }

        return equipmentNames;
    }

    //Get the exercise comma string base on which day it is
    public String getExercisesPerDayList(int day) {
        Log.d("SELECT DB", "Selecting exercises each one per day....");

        Cursor c = getReadableDatabase().rawQuery("SELECT exercise FROM exercise_dates WHERE _id = " + Integer.toString(day), null);
        String exercisePerDayString = "";

        if(c == null) {
            Log.d("ex db", "NOTHING");
        }
        else {
            if (c.getCount() == 0) {
                Log.d("NOTHING", "0 nothing");
            }

            c.moveToFirst();
            do {
                exercisePerDayString += c.getString(c.getColumnIndex("exercise"));
            } while (c.moveToNext());

            c.close();
        }
        getReadableDatabase().close();

        return exercisePerDayString;
    }

    //Get the comma string of exercises per day first, find and remove the exercise from the string, then update table with the new string.
    public boolean removeAndUpdateExercises(int day, int exceriseId) {
        Log.d("REMOVE EX", "Removing an exercise from the list...");

        boolean flag = false;

        String exerciseString = "";
        String newExerciseString = "";

        Cursor c = getReadableDatabase().rawQuery("SELECT exercise FROM exercise_dates WHERE _id = " + Integer.toString(day), null);

        if(c == null) {
            Log.d("ex db", "NOTHING");
        }

        if (c.getCount() == 0) {
            Log.d("NOTHING", "0 nothing");
        }

        c.moveToFirst();
        do {
            Log.d("exList", c.getString(c.getColumnIndex("exercise")));
            exerciseString += c.getString(c.getColumnIndex("exercise"));
        } while (c.moveToNext());

        c.close();
        getReadableDatabase().close();

        //In case the list only has one item and it gets deleted from the database, the string will be empty, so there is nothing to split
        //with the comma. So check from empty string, so it doesn't need to run and check this part and just update the database.
        if(!exerciseString.equals("")) {
            ArrayList<String> exerciseIds = new ArrayList(Arrays.asList(exerciseString.split(",")));
            for (int i = 0; i < exerciseIds.size(); i++) {
                if (Integer.parseInt(exerciseIds.get(i)) == exceriseId) {
                    exerciseIds.remove(i);
                }
            }

            for (int i = 0; i < exerciseIds.size(); i++) {
                if (i != (exerciseIds.size() - 1)) {
                    newExerciseString = newExerciseString + exerciseIds.get(i) + ",";
                } else {
                    newExerciseString += exerciseIds.get(i);
                }
            }
        }


        this.database = this.getWritableDatabase();
        try {
            Log.d("Up_ExD_Ex", newExerciseString);
            this.database.execSQL("UPDATE exercise_dates SET exercise = '" + newExerciseString + "' WHERE _id = " + Integer.toString(day) + ";");
            this.database.execSQL("UPDATE exercises SET date = '', max_weight = 0, max_rep = 0, num_set = 0 WHERE _id = " + Integer.toString(exceriseId) + ";");
            flag = true;
        } catch (SQLException e) {
            Log.d("SQLUpError", e.toString());
        }

        this.database.close();

        return flag;
    }

    //Don't need to use it, was going to use to check if the database existed before doing anything
    //I already did that in the MainActivity checkDatabaseExist() method
    public boolean checkDatabase() {
        SQLiteDatabase tempDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            tempDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {
            Log.d("Check database error", e.getMessage());
        }

        if(tempDB != null) {
            tempDB.close();
        }

        return tempDB != null ? true : false;
    }

}
