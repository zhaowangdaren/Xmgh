package com.example.ustc_pc.myapplication.unit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ustc-pc on 2015/8/5.
 */
public class Answer implements Serializable, Comparable<Answer>{
    int iQuestionID;
    boolean isMultiSonQuestion;
    List<AnswerSon> questions;

    public Answer(int iQuestionID, boolean isMultiSonQuestion, List<AnswerSon> questions){
        this.iQuestionID = iQuestionID;
        this.isMultiSonQuestion = isMultiSonQuestion;
        this.questions = questions;
    }

    public Answer(int iQuestionID, boolean isMultiSonQuestion, JSONArray questionsJSON){
        this.iQuestionID = iQuestionID;
        this.isMultiSonQuestion = isMultiSonQuestion;
        questions = new ArrayList<>();
        for(int i=0; i< questionsJSON.size(); i++){
            JSONObject jsonObject = questionsJSON.getJSONObject(i);
            int id = jsonObject.getIntValue("iSonQuestionID");
            String strAnalysis = jsonObject.getString("strAnalysis");
            JSONArray jsonArrayOption = jsonObject.getJSONArray("answer");
            questions.add(new AnswerSon(id, jsonArrayOption,strAnalysis));

        }
    }

    public Integer getiQuestionID(){
        return iQuestionID;
    }

    public List<AnswerSon> getAnswerSons(){
        return questions;
    }

    @Override
    public int compareTo(Answer answer) {
        return this.getiQuestionID().compareTo(answer.getiQuestionID());
    }

    public class AnswerSon{
        int iSonQuestionID;
        List<AnswerOption> answer;
        String strAnalysis;
        public AnswerSon( int iSonQuestionID, List<AnswerOption> answer, String strAnalysis){
            this.iSonQuestionID = iSonQuestionID;
            this.answer = answer;
            this.strAnalysis = strAnalysis;
        }
        public AnswerSon(int iSonQuestionID, JSONArray answerJson, String strAnalysis){
            this.iSonQuestionID = iSonQuestionID;
            this.answer = new ArrayList<>();
            for(int i = 0; i< answerJson.size(); i++){
                JSONObject jsonObject = answerJson.getJSONObject(i);
                answer.add(new AnswerOption(jsonObject.getIntValue("ID")));
            }
            this.strAnalysis = strAnalysis;
        }

        public List<AnswerOption> getAnswer(){
            return answer;
        }
        public String getStrAnalysis(){return strAnalysis;}

        public class AnswerOption{
            int ID;
            public AnswerOption(int id){
                ID = id;
            }

            public int getID(){
                return ID;
            }
        }
    }
}
