package com.example.student.doyouevenliftbro;

/**
 * Created by student on 3/23/16.
 */
public class Muscle {
    private int _id;
    private String _name;

    public Muscle() {
        _id = 0;
        _name = "";
    }

    public void AddMuscle(int id, String name) {
        _id = id;
        _name = name;
    }

    public Integer GetMuscleId() {
        return _id;
    }

    public String GetMuscleName() {
        return _name;
    }
}
