package com.example.student.doyouevenliftbro;

/**
 * Created by student on 3/27/16.
 */
public class Equipment {
    private int _id;
    private String _name;

    public Equipment() {
        _id = 0;
        _name = "";
    }

    public void AddEquipment(int id, String name) {
        _id = id;
        _name = name;
    }

    public Integer GetEquipmentId() { return _id; }
    public String GetEquipmentName() { return _name; }
}
