package com.example.ustc_pc.myapplication.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import java.io.Serializable;

/**
 * Entity mapped to table "DONE_QUESTION".
 */
public class DoneQuestion implements Serializable, Comparable<DoneQuestion>{

    private Integer iCourseID;
    private Integer iQuestionType;
    private Long lQuestionID;
    private Boolean isFavorite = false;
    private Boolean isCorrect = false;
    private String strNote;
    private String strUserAnswer;
    private Long lSpendTime;
    private String strQuestionKpID;
    private Integer iTestID;

    public DoneQuestion() {
    }

    public DoneQuestion(Long lQuestionID) {
        this.lQuestionID = lQuestionID;
    }

    public DoneQuestion(Integer iCourseID, Integer iQuestionType, Long lQuestionID, Boolean isFavorite, Boolean isCorrect, String strNote, String strUserAnswer, Long lSpendTime, String strQuestionKpID, Integer iTestID) {
        this.iCourseID = iCourseID;
        this.iQuestionType = iQuestionType;
        this.lQuestionID = lQuestionID;
        this.isFavorite = isFavorite;
        this.isCorrect = isCorrect;
        this.strNote = strNote;
        this.strUserAnswer = strUserAnswer;
        this.lSpendTime = lSpendTime;
        this.strQuestionKpID = strQuestionKpID;
        this.iTestID = iTestID;
    }

    @Override
    public int compareTo(DoneQuestion doneQuestion) {
        return this.lQuestionID.compareTo(doneQuestion.lQuestionID);
    }

    public Integer getICourseID() {
        return iCourseID;
    }

    public void setICourseID(Integer iCourseID) {
        this.iCourseID = iCourseID;
    }

    public Integer getIQuestionType() {
        return iQuestionType;
    }

    public void setIQuestionType(Integer iQuestionType) {
        this.iQuestionType = iQuestionType;
    }

    public Long getLQuestionID() {
        return lQuestionID;
    }

    public void setLQuestionID(Long lQuestionID) {
        this.lQuestionID = lQuestionID;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public String getStrNote() {
        return strNote;
    }

    public void setStrNote(String strNote) {
        this.strNote = strNote;
    }

    public String getStrUserAnswer() {
        return strUserAnswer;
    }

    public void setStrUserAnswer(String strUserAnswer) {
        this.strUserAnswer = strUserAnswer;
    }

    public Long getLSpendTime() {
        return lSpendTime;
    }

    public void setLSpendTime(Long lSpendTime) {
        this.lSpendTime = lSpendTime;
    }

    public String getStrQuestionKpID() {
        return strQuestionKpID;
    }

    public void setStrQuestionKpID(String strQuestionKpID) {
        this.strQuestionKpID = strQuestionKpID;
    }

    public Integer getITestID() {
        return iTestID;
    }

    public void setITestID(Integer iTestID) {
        this.iTestID = iTestID;
    }


}
