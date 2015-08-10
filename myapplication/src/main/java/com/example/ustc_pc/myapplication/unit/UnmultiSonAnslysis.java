package com.example.ustc_pc.myapplication.unit;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ustc-pc on 2015/8/10.
 */
public class UnmultiSonAnslysis implements Serializable, Comparable<UnmultiSonAnslysis>{
    private Integer iQuestionID;
    private Integer iMultiSonQuestion;
    private String strAnalysis;
    private List<String> answer;


    public UnmultiSonAnslysis(Integer iQuestionID, Integer iMultiSonQuestion, String strAnalysis, List<String> answer) {
        this.iQuestionID = iQuestionID;
        this.iMultiSonQuestion = iMultiSonQuestion;
        this.strAnalysis = strAnalysis;
        this.answer = answer;
    }

    public Integer getiQuestionID() {
        return iQuestionID;
    }

    public void setiQuestionID(Integer iQuestionID) {
        this.iQuestionID = iQuestionID;
    }

    public Integer getiMultiSonQuestion() {
        return iMultiSonQuestion;
    }

    public void setiMultiSonQuestion(Integer iMultiSonQuestion) {
        this.iMultiSonQuestion = iMultiSonQuestion;
    }

    public String getStrAnalysis() {
        return strAnalysis;
    }

    public void setStrAnalysis(String strAnalysis) {
        this.strAnalysis = strAnalysis;
    }

    public List<String> getAnswer() {
        return answer;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }

    @Override
    public int compareTo(UnmultiSonAnslysis unmultiSonAnslysis) {
        return this.iQuestionID.compareTo(unmultiSonAnslysis.getiQuestionID());
    }
}
