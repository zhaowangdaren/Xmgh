package com.example.ustc_pc.myapplication.unit;

/**
 * Created by ustc-pc on 2015/3/16.
 */
public class Individuation {
    public String _id = "";
    public String _name ="";
    public String _imageUrl ="";
    public int _iType = 0;
    public int _numOfQuestion = 0;

    public Individuation(String id, String name, int type){
        _id = id;
        _name = name;
        _iType = type;
    }
}
