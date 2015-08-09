package com.example.ustc_pc.myapplication.unit;

import java.util.List;

/**
 * Created by ustc_zy on 2015/8/9.
 */
public class QuestionUnmultiSon {
    private Integer iQuestionID;
    private String strSubject;
    private boolean  isMultiSonQuestion;

    private boolean isMultiSelect;
    private String strPicFileName;
    private String strAudioFileName;
    private String strVideoUrl;

    private long lSpendTime, lStartTime, lStopTime;
    private List<QuestionOption> options;

    public QuestionUnmultiSon(Integer iQuestionID, String strSubject, boolean isMultiSonQuestion, boolean isMultiSelect, String strPicFileName, String strAudioFileName, String strVideoUrl, long lSpendTime, long lStartTime, long lStopTime, List<QuestionOption> options) {
        this.iQuestionID = iQuestionID;
        this.strSubject = strSubject;
        this.isMultiSonQuestion = isMultiSonQuestion;
        this.isMultiSelect = isMultiSelect;
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

    public boolean isMultiSonQuestion() {
        return isMultiSonQuestion;
    }

    public void setIsMultiSonQuestion(boolean isMultiSonQuestion) {
        this.isMultiSonQuestion = isMultiSonQuestion;
    }

    public boolean isMultiSelect() {
        return isMultiSelect;
    }

    public void setIsMultiSelect(boolean isMultiSelect) {
        this.isMultiSelect = isMultiSelect;
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

    public class QuestionOption{
        private int ID;
        private String strOption;
        private boolean isAnswer;
        private boolean isSelected;

        public QuestionOption(int ID, String strOption){
            this.ID = ID;
            this.strOption = strOption;
        }

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
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
