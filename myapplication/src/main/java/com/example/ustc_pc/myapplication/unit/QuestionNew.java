package com.example.ustc_pc.myapplication.unit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ustc-pc on 2015/8/3.
 */
public class QuestionNew implements Serializable {
    private int iQuestionID;
    private String strSubject;
    private boolean isMultiSonQuestion;
    private String strAudioFileName;
    private String strVideoUrl;
    private List<QuestionSon> questionSons;

    public QuestionNew(){}

    public QuestionNew(int iQuestionID,String strSubject, boolean isMultiSonQuestion, String strAudioFileName, String strVideoUrl, List<QuestionSon> questions){
        this.iQuestionID = iQuestionID;
        this.strSubject = strSubject;
        this.isMultiSonQuestion = isMultiSonQuestion;
        this.strAudioFileName = strAudioFileName;
        this.strVideoUrl = strVideoUrl;
        this.questionSons = questions;
    }

    public QuestionNew(int iQuestionID,String strSubject, boolean isMultiSonQuestion, String strAudioFileName, String strVideoUrl, JSONArray jsonQuestionSons){
        this.iQuestionID = iQuestionID;
        this.strSubject = strSubject;
        this.isMultiSonQuestion = isMultiSonQuestion;
        this.strAudioFileName = strAudioFileName;
        this.strVideoUrl = strVideoUrl;
        this.questionSons = new ArrayList<>();
        for(int i = 0; i< jsonQuestionSons.size(); i++){


            JSONObject jsonQuestionSon = jsonQuestionSons.getJSONObject(i);
            int iSonQuestionID = jsonQuestionSon.getIntValue("iSonQuestionID");
            String strSonSubject = jsonQuestionSon.getString("strSonSubject");
            boolean isMultiSelect = jsonQuestionSon.getBooleanValue("isMultiSelect");
            String strPicFileName = jsonQuestionSon.getString("strPicFileName");
            //option array
            JSONArray jsonOptions = jsonQuestionSon.getJSONArray("options");
            QuestionSon questionSon = new QuestionSon(iSonQuestionID, strSonSubject, isMultiSelect, jsonOptions, strPicFileName);
            questionSons.add(questionSon);
        }
    }

    public int getiQuestionID() {
        return iQuestionID;
    }

    public void setiQuestionID(int iQuestionID) {
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

    public List<QuestionSon> getQuestionSons() {
        return questionSons;
    }

    public void setQuestionSons(List<QuestionSon> questionSons) {
        this.questionSons = questionSons;
    }

    public class QuestionSon{
        public int iSonQuestionID;
        public String strSonSubject;
        public boolean isMultiSelect;
        public List<QuestionOption> options;
        public String strPicFileName;

        public QuestionSon(){

        }

        public QuestionSon(int iSonQuestionID, String strSonSubject, boolean isMultiSelect, List<QuestionOption> options, String strPicFileName){
            this.iSonQuestionID = iSonQuestionID;
            this.strSonSubject = strSonSubject;
            this.isMultiSelect = isMultiSelect;
            this.options = options;
            this.strPicFileName = strPicFileName;
        }

        public QuestionSon(int iSonQuestionID, String strSonSubject, boolean isMultiSelect, JSONArray jsonOptions, String strPicFileName){
            this.iSonQuestionID = iSonQuestionID;
            this.strSonSubject = strSonSubject;
            this.isMultiSelect = isMultiSelect;
            this.strPicFileName = strPicFileName;
            List<QuestionOption> questionOptions = new ArrayList<>();
            for(int i =0; i< jsonOptions.size(); i++){
                JSONObject jsonOption = jsonOptions.getJSONObject(i);
                int ID = jsonOption.getIntValue("ID");
                String strOption = jsonOption.getString("strOption");
                questionOptions.add(new QuestionOption(ID, strOption));
            }
        }
        public void setiSonQuestionID(int iSonQuestionID){
            this.iSonQuestionID = iSonQuestionID;
        }
        public int getiSonQuestionID(){
            return this.iSonQuestionID;
        }

        public void setStrSonSubject(String strSonSubject){
            this.strSonSubject = strSonSubject;
        }
        public String getStrSonSubject(){
            return strSonSubject;
        }

        public void setMultiSelect(boolean isMultiSelect){
            this.isMultiSelect = isMultiSelect;
        }

        public boolean getIsMultiSelect(){
            return isMultiSelect;
        }

        public void setOptions(List<QuestionOption> options){
            this.options = options;
        }
        public List<QuestionOption> getOptions(){
            return options;
        }

        public void setStrPicFileName(String strPicFileName){
            this.strPicFileName = strPicFileName;
        }

        public String getStrPicFileName(){
            return this.strPicFileName;
        }

        public class QuestionOption{
            public int ID;
            public String strOption;
            public boolean isAnswer;
            public boolean isSelected;

            public QuestionOption(int ID, String strOption){
                this.ID = ID;
                this.strOption = strOption;
            }


        }


    }


}
