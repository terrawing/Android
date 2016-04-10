package com.example.student.doyouevenliftbro;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by student on 3/21/16.
 */
public class Exercise {
    private int _id;
    private String _name;
    private String _description;
    //private ArrayList<String> _muscles_main;
    private String _muscles_main;
    //private ArrayList<String> _muscles_secondary;
    private String _muscles_secondary;
    private String _equipment;
    private int _max_weight;
    private int _max_rep;
    private String _date;
    private int _set;

    public Exercise() {
        _id = 0;
        _name = "";
        _description = "";
        //_muscles_main = new ArrayList<String>();
       // _muscles_secondary = new ArrayList<String>();
        _muscles_main = "";
        _muscles_secondary = "";
        _equipment = "";
        _max_weight = 0;
        _max_rep = 0;
        _date = "";
        _set = 0;
    }

    public void AddExercise(int id, String name, String description, String muscles_main, String muscles_secondary, String equipment, String date, int max_weight, int max_rep, int set) {
        _id = id;
        _name = name;
        _description = description;
        _muscles_main = muscles_main;
        _muscles_secondary = muscles_secondary;
        _equipment = equipment;
        _date = date;
        _max_weight = max_weight;
        _max_rep = max_rep;
        _set = set;
    }

    public Exercise(Parcel in) {
        _id = in.readInt();
        _name = in.readString();
        _description = in.readString();
        _muscles_main = in.readString();
        _muscles_secondary = in.readString();
        _equipment = in.readString();
        _date = in.readString();
        _max_weight = in.readInt();
    }

    /*
    public void AddExercise(int id, String name, String description, ArrayList<String> muscles_main, ArrayList<String> muscles_secondary, String image) {
        _id = id;
        _name = name;
        _description = description;
        _muscles_main = muscles_main;
        _muscles_secondary = _muscles_secondary;
        _image = image;
    }*/

    public Integer GetExerciseId() { return _id; }

    public String GetExerciseName() {
        return _name;
    }

    public String GetExerciseDescription() {
        return _description;
    }

    public String GetExerciseMusclesMain() {
        return _muscles_main;
    }

    public String GetExerciseMusclesSecondary() {
        return _muscles_secondary;
    }

    public String GetEquipment() {
        return _equipment;
    }

    public String GetExerciseDate() {
        return _date;
    }

    public Integer GetMaxWeight() { return _max_weight; }

    public Integer GetMaxRep() { return _max_rep; }

    public Integer GetSet() { return _set; }



    //http://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents
    //http://stackoverflow.com/questions/12092612/pass-list-of-objects-from-one-activity-to-other-activity-in-android
    //Passing a class object for Intent putExtra because too much work passing each individual thing at a time, might as well pass the entire class.
   /*
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(_name);
        dest.writeString(_description);
        dest.writeString(_muscles_main);
        dest.writeString(_muscles_secondary);
        dest.writeString(_equipment);
        dest.writeString(_image);
        dest.writeInt(_max_weight);
    }

    public static final Parcelable.Creator<Exercise> CREATOR = new Parcelable.Creator<Exercise>()
    {
        public Exercise createFromParcel(Parcel in)
        {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };*/
}


