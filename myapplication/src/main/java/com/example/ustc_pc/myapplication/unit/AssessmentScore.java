package com.example.ustc_pc.myapplication.unit;

import java.util.ArrayList;

/**
 * Created by ustc-pc on 2015/3/26.
 */
public class AssessmentScore {
    public int _iCourseID = 1;
    public int _iSumScore = 0;
    public int _iFirstKpNum = 0;
    public ArrayList<DoneKpSocre> _arrayDoneKps;
    public String _strComment = "";

    public int _iFinishedArg = 0, _iUnfinishedArg = 0;
    public AssessmentScore(int sumScore, int kpNum){
        _iSumScore = sumScore;
        _iFirstKpNum = kpNum;
        _arrayDoneKps = new ArrayList<>();

    }

    public AssessmentScore(int courseID, int sumScore, int firstKPNum) {
        _iCourseID = courseID;
        _iSumScore = sumScore;
        _iFirstKpNum = firstKPNum;
       _arrayDoneKps = new ArrayList<>();
    }

    public void addDoneKpSocre(String id, String name, double score){
        DoneKpSocre doneKpSocre = new DoneKpSocre(id, name, score);
        _arrayDoneKps.add(doneKpSocre);
    }

    public class DoneKpSocre{
        public String _id = "";
        public String _name = "";
        public double _iScore = 0;
        public int _iColor= 813727872;//tran_gray
        public DoneKpSocre(String id, String name, double score){
            _id= id;
            _name = name;
            _iScore = score;

        }
    }
}
