package com.example.ustc_pc.myapplication.unit;

import com.example.ustc_pc.myapplication.net.Util;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ustc_zy on 2015/8/9.
 */
public class QuestionUnmultiSon implements Serializable, Comparable<QuestionUnmultiSon>{
    private Integer iQuestionID;
    private String strSubject;
    private int  iMultiSonQuestion = Util.NO_MULTI_SON_QUESTION;

    private int iMultiSelect = Util.UN_MULTI_SELECT;
    private String strPicFileName;
    private String strAudioFileName;
    private String strVideoUrl;

    private long lSpendTime, lStartTime, lStopTime;
    private List<QuestionOption> options;

    public QuestionUnmultiSon(Integer iQuestionID, String strSubject, int iMultiSonQuestion, int iMultiSelect, String strPicFileName, String strAudioFileName, String strVideoUrl, long lSpendTime, long lStartTime, long lStopTime, List<QuestionOption> options) {
        this.iQuestionID = iQuestionID;
        this.strSubject = strSubject;
        this.iMultiSonQuestion = iMultiSonQuestion;
        this.iMultiSelect = iMultiSelect;
        this.strPicFileName = strPicFileName;
        this.strAudioFileName = strAudioFileName;
        this.strVideoUrl = strVideoUrl;
        this.lSpendTime = lSpendTime;
        this.lStartTime = lStartTime;
        this.lStopTime = lStopTime;
        this.options = options;
    }

    public Integer getiQuestionID() {
        return iQuestionID;
    }

    public void setiQuestionID(Integer iQuestionID) {
        this.iQuestionID = iQuestionID;
    }

    public String getStrSubject() {
        return strSubject;
    }

    public void setStrSubject(String strSubject) {
        this.strSubject = strSubject;
    }

    public String getStrPicFileName() {
        return strPicFileName;
    }

    public void setStrPicFileName(String strPicFileName) {
        this.strPicFileName = strPicFileName;
    }

    public String getStrAudioFileName() {
        return strAudioFileName;
    }

    public void setStrAudioFileName(String strAudioFileName) {
        this.strAudioFileName = strAudioFileName;
    }

    public String getStrVideoUrl() {
        return strVideoUrl;
    }

    public void setStrVideoUrl(String strVideoUrl) {
        this.strVideoUrl = strVideoUrl;
    }

    public long getlSpendTime() {
        return lSpendTime;
    }

    public void setlSpendTime(long lSpendTime) {
        this.lSpendTime = lSpendTime;
    }

    public long getlStartTime() {
        return lStartTime;
    }

    public void setlStartTime(long lStartTime) {
        this.lStartTime = lStartTime;
    }

    public long getlStopTime() {
        return lStopTime;
    }

    public void setlStopTime(long lStopTime) {
        this.lStopTime = lStopTime;
    }

    public List<QuestionOption> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOption> options) {
        this.options = options;
    }

    public int getiMultiSonQuestion() {
        return iMultiSonQuestion;
    }

    public void setiMultiSonQuestion(int iMultiSonQuestion) {
        this.iMultiSonQuestion = iMultiSonQuestion;
    }

    public int getiMultiSelect() {
        return iMultiSelect;
    }

    public void setiMultiSelect(int iMultiSelect) {
        this.iMultiSelect = iMultiSelect;
    }

    public boolean isMultiSelect() {
        if(this.iMultiSelect == Util.UN_MULTI_SELECT)return false;
        else return true;
    }

    @Override
    public int compareTo(QuestionUnmultiSon questionUnmultiSon) {
        return this.getiQuestionID().compareTo(questionUnmultiSon.getiQuestionID());
    }

    public class QuestionOption implements Serializable{
        private String ID;
        private String strOption;
        private boolean isAnswer;
        private boolean isSelected;

        public QuestionOption(String ID, String strOption){
            this.ID = ID;
            this.strOption = strOption;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getStrOption() {
            return strOption;
        }

        public void setStrOption(String strOption) {
            this.strOption = strOption;
        }

        public boolean isAnswer() {
            return isAnswer;
        }

        public void setIsAnswer(boolean isAnswer) {
            this.isAnswer = isAnswer;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setIsSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }
    }
}
