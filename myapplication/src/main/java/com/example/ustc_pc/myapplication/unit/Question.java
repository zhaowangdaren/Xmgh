package com.example.ustc_pc.myapplication.unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ustc-pc on 2015/1/19.
 */
public class Question implements Serializable{
    public final static int SingleSelect = 1;
    public final static int MultiSelect = 2;

    public static int ERROR = -1;
    public String _id;
    public int _iCourseID = 0;
    public String _paperName = "";

    /*
    单项选择-1、多选选择-2、非选择题-3
     */
    public int _iType = 0;

    //multiselect & singleselect
    public int _iAnswerNum = 0;//答案个数
    public int _iSingleAnswer;
    public int[] _iAnswers;
    public HashMap<Integer, Boolean> _answerMap;
    public String _strAnswer = "";

    public String _strSubject;//题目
    public String _strSubjectSon;//子题目

    public ArrayList<String> _options;

    /*
    用户选择的选项Map
     */
    public HashMap<Integer, Boolean> _userSelectedMap;
    public String _strUserSelected = "";
    public int _iErrorTimes = 0;
    /*
    用户选择的选项个数
     */
    public int _iUserHasDone = 0;

    /*
    解析
     */
    public String _analysis = "";
    public String _analysisSon = "";
    /*
    题目来源
     */
    public String _strSource ="";

    /*
    笔记
     */
    public String _strNote = "";
    public String _strSpendTime = "";
    public long _lSpendTime = 0;
    public long _lStartTime = 0;
    public long _lEndTime = 0;

    public String _strFirstKP = "1";
    public String _strSecondKP= "2";
    public String _strThirdKP = "3";

     /*
    单选题，标识用户是否选对
    */
    public boolean _isRight = false;
    /*
    对于多选题，标识用户是否有选对的
     */
    public boolean _isHalfRight = false;

    public boolean _isFavorite = false;

    public Question(String questionId, int courseId, String paperName, String firstKP, String secondKP, String thirdKP,
                    boolean isFavorite, boolean isCorrect, String subject, String subjectSon, ArrayList<String> options,
                    String strAnswer, String strUserSelected, String detail, String detailSon, String note, long spendTime, int errorTimes) {
        _id = questionId;
        _iCourseID = courseId;
        _paperName = paperName;
        _strFirstKP = firstKP;
        _strSecondKP = secondKP;
        _strThirdKP = thirdKP;
        _isFavorite = isFavorite;
        _isRight = isCorrect;
        _strSubject = subject;
        _strSubjectSon = subjectSon;
        _options = options;
        _strAnswer = strAnswer;
        _strUserSelected = strUserSelected;
        _analysis = detail;
        _analysisSon = detailSon;
        _strNote = note;
        _lSpendTime = spendTime;
        _iErrorTimes = errorTimes;
    }

    public void setKP(String firstKP, String secondKP, String thirdKP){
        _strFirstKP = firstKP;
        _strSecondKP = secondKP;
        _strThirdKP = thirdKP;
    }

    public Question(){}
    /**
     *
     * @param id
     * @param strSubject
     * @param strSubjectSon
     * @param options
     * @param iAnswerNum
     * @param answers
     */
    public Question(String id, String strSubject, String strSubjectSon, ArrayList<String> options, int iAnswerNum, int[] answers){
        _id = id;
        _strSubject = strSubject;
        _strSubjectSon = strSubjectSon;
        _options = options;
        _iAnswerNum = iAnswerNum;
        _iAnswers = answers;
    }

    /**
     *
     * @param id
     * @param type
     * @param strSubject
     * @param strSubjectSon
     * @param options
     * @param iAnswerNum
     * @param answers
     */
    public Question(String id, int type, String strSubject, String strSubjectSon, ArrayList<String> options, int iAnswerNum, int[] answers){
        _id = id;
        _iType = type;
        _strSubject = strSubject;
        _strSubjectSon = strSubjectSon;
        _options = options;
        _userSelectedMap = new HashMap<>();
        for(int i=0; i< _options.size(); i++){
            _userSelectedMap.put(i, false);
        }
        if(type == 0){
            _iSingleAnswer = answers[0];
        }
        _iAnswerNum = iAnswerNum;
        _iAnswers = answers;
    }

    public Question(String q_id, int type, String subject, String subjectSon, ArrayList<String> options, HashMap<Integer, Boolean> hashMapAnswers, String detail, String detailSon) {
        _id  = q_id;
        _iType = type;
        _strSubject = subject;
        _strSubjectSon = subjectSon;
        _options = options;
        _answerMap = hashMapAnswers;
        _analysis = detail;
        _analysisSon = detailSon;
        _userSelectedMap = new HashMap<>();
        for(int i=0; i< _options.size(); i++){
            _userSelectedMap.put(i, false);
        }
        _iAnswers = new int[_options.size()];
        int iAN = 0;
        for(int i =0; i< _options.size(); i++){
            if(_answerMap.get(i) != null && _answerMap.get(i)){
                _iAnswers[iAN] = i;
                iAN ++;
            }
        }
        _iAnswerNum = iAN;
    }


    public void setAnswers(HashMap<Integer, Boolean> answers){
        _answerMap = answers;
    }

    public void initAnswerMap(){
        if(_answerMap == null){
            _answerMap = new HashMap<>();
        }
        for(int i = 0 ;i< _iAnswerNum; i++){
            _answerMap.put(_iAnswers[i], true);
        }
    }
}
