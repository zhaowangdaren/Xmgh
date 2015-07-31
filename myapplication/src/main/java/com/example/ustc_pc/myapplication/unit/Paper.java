package com.example.ustc_pc.myapplication.unit;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ustc-pc on 2015/1/19.
 */
public class Paper implements Serializable {

    //the index of paper
    public String _id = "";
    /*
    * paper name
    * */
    public String _strName ="";

    //the number of questions
    public int _iNumQ = 5;

    public ArrayList<Question> _questions;
    //用户做题是否全对
    public boolean _isAllRight = true;

    public String _strCourse = "";//科目
    public String _strFirstKP = "";//一级知识点
    public String _strSecondKP = "";//二级知识点
    public String _strThirdKP = "";//三级知识点

    public long _lStartTime = 0;
    public long _lEndTime = 0;
    public long _lSpendTime = 0;//用户做卷时间
    public String _strSpendTime = "";
    public int _iScore = 0;
    public boolean _isDid = false;
    public boolean _isLocked = true;

    public int _iType = -1; //paper type,-1:none, 0:basic, 1:prediction,2:zhenTi, 3:recommend
    public int _iCourseId = 0;// Course ID

    public final static int PAPER_NONE_TYPE = -1, PAPER_BASIC = 0, PAPER_PREDICTION = 1,
            PAPER_ZHEN_TI =2, PAPER_RECOMMEND = 3, PAPER_ERROR = 4;
    public Paper(){

    }

    public Paper(String id, String name, int num, String course , String firstKP, String secondKP, String thirdKP){
        _id = id;
        _strName = name;
        _iNumQ = num;
        _strCourse = course;
        _strFirstKP = firstKP;
        _strSecondKP = secondKP;
        _strThirdKP = thirdKP;
    }


    public Paper(String id, String name, int sumQuestion, String firstKP, String secondKP, String thirdKP, ArrayList<Question> questionArrayList) {
        _id = id;
        _strName = name;
        _iNumQ = sumQuestion;
        _strFirstKP = firstKP;
        _strSecondKP = secondKP;
        _strThirdKP = thirdKP;
        _questions = questionArrayList;
    }
}
