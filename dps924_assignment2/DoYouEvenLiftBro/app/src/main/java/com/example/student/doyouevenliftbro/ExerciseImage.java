package com.example.student.doyouevenliftbro;

/**
 * Created by student on 3/26/16.
 */
public class ExerciseImage {

    private int _id;
    private String _link;

    public ExerciseImage() {
        _id = 0;
        _link = "";
    }

    public void AddImage(int id, String link) {
        _id = id;
        _link = link;
    }

    public Integer GetImageId() {
        return _id;
    }

    public String GetLink() {
        return _link;
    }

}
