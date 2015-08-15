package com.example.ustc_pc.myapplication.unit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.ustc_pc.myapplication.dao.DoneQuestion;
import com.google.gson.Gson;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ustc-pc on 2015/7/25.
 */
public class Util {
    public static final String URL_HOME = "http://120.26.210.13:8080/xmghProject/account";
    public static final String URL_PHONE_CHECK = URL_HOME+"/PhoneCheck";

    public static String URL_REGISTER_BY_PHONE = URL_HOME+"/PhoneRegister";
    public static String URL_LOGIN_BY_PHONE = URL_HOME+"/PhoneLogin";
    public static String URL_SET_PERSONAL_INFO = URL_HOME + "/SetPersonInfo";
    public static String URL_GET_PERSONAL_INFO = URL_HOME + "/GetPersonInfo";
    public static String URL_ADD_COURSE = URL_HOME + "/AddCourse";
    public static String  URL_DEL_COURSE = URL_HOME + "/DelCourse";
    public static String URL_GET_KPs = URL_HOME + "/GetKPs";
    public static String URL_GET_SELECTED_COURSES = URL_HOME + "/GetSelectedCourses";
    public static String URL_GET_COURSES_INFO = URL_HOME + "/GetCoursesInfo";
    public static String URL_GET_BASIC_TEST_ONLINE = URL_HOME + "/GetBasicTestOnline";
    public static String URL_UPLOAD_DONE_QUESTION = URL_HOME + "/UploadDoneQuestions";
    public static String URL_GET_ASSESSED_SCORE = URL_HOME + "/GetAssessedScore";
    public static String URL_CHECK_UPDATE = "http://120.26.210.13:8080/xmghProject/" + "version.json";

    public static int iNo_USERID = -1;
    public static int NO_LAST_COURSE = -1;

    public static final int PHONE_LOGIN = 0, QQ_LOGIN = 1, WEIBO_LOGIN = 2, WECHAT_LOGIN = 3;
    public static String DB_NAME = "cn.edu.ustc.xmgh.db";
    public static Integer FIRST_LEVEL_KP = 1, SECOND_LEVEL_KP = 2, THIRD_LEVEL_KP = 3, LAST_LEVEL_KP = 4;
    public static String URL_GET_QUESTIONS = URL_HOME + "/GetQuestions";
    public static String APP_PATH = Environment.getExternalStorageDirectory()+"/.cn.edu.ustc.xmgh";
    public static int BASIC_TEST = 1, REAL_TEST = 2, SPECIAL_TEST = 3, MOCK_TEST = 4;

    public static final int TYPE_QUESTION_LAYOUT_HEADER = 0, TYPE_QUESTION_LAYOUT_FATHER = 1,
            TYPE_QUESTION_LAYOUT_OPTION = 2, TYPE_QUESTION_LAYOUT_ANALYSIS = 4, TYPE_QUESTION_LAYOUT_NOTE = 5;

    public static final int MULTI_SON_QUESTION = 1, NO_MULTI_SON_QUESTION = -1;
    public static final int MULTI_SELECT = 1, UN_MULTI_SELECT = -1;

    public static final String FILE_NAME_QUESTION = "question_unMultiSonQuestion.json", FILE_NAME_QUESTION_ANOTHER = "question_MultiSonQuestion.json";
    public static final String FILE_NAME_ANALYSIS = "analysis_unMultiSonQuestion.json", FILE_NAME_ANALYSIS_ANOTHER = "analysis_MultiSonQuestion.json";
    public static final int NO_GENDER = 0, MAN = 1, WOMAN = 2;


    public static boolean createFile(String filePath, String fileName){
        File file1 = new File(filePath);
        String temp = filePath + fileName;
        if(!file1.exists()){
            try{
                file1.mkdir();
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        File file2 = new File(temp);
        if(!file2.exists()){
            try {
                file2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static boolean createFolder(String folderPath){
        File file = new File(folderPath);
        if(!file.exists()){
            try{
                file.mkdir();
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static String getFileFromSD(String filePath){
        StringBuilder sb = new StringBuilder();
        try {
            File file = new File(filePath);
            BufferedReader bf = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filePath), "GB2312"));
            String content = "";
            while(true){
                content = bf.readLine();
                if(content == null)break;
                sb.append(content.trim());
            }
            bf.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }
    /**
     * check net is allowable
     * @param context
     * @return
     */
    public static boolean isConnect(Context context){
        ConnectivityManager connManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        if( netInfo != null){
            return netInfo.isAvailable();
        }
        return false;
    }

    public static boolean isPhoneNumber(String strPhone) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(strPhone);
        return m.matches();
    }

    public static boolean unZip(String sourcePath, String targetPath) {
        try {
            ZipFile zipFile = new ZipFile(sourcePath);
            if (zipFile.isEncrypted()) {
                Log.e("Unzip failed", "The zip has been encrypted");
                return false;
            }
            zipFile.extractAll(targetPath);
        }catch (ZipException e){
            Log.e("Unzip failed", e.toString());
            return false;
        }
        return true;
    }


    public static String[] getAllQuestionsAPath(String absolutePath){
        File file  = new File(absolutePath);
        String[] questionsFolderName = file.list();
        List<String> result = new ArrayList<>();
        for(int i = 0; i<questionsFolderName.length; i++){
            try{
                Integer.valueOf(questionsFolderName[i]);
                questionsFolderName[i] = absolutePath + questionsFolderName[i];
                result.add(questionsFolderName[i]);
            }catch (Exception e) {
                continue;

            }
        }
        return (String[]) result.toArray(new String[result.size()]);
    }

    public static List<QuestionNew> parseMultiSonQuestionsFromFile(String[] questionsAPath) {
        if(questionsAPath.length <= 0)return null;
        List<QuestionNew> result = new ArrayList<>(questionsAPath.length);
        try {
            for (int i = 0; i < questionsAPath.length; i++) {
                String strQuestion = Util.getFileFromSD(questionsAPath[i] + "/" + Util.FILE_NAME_QUESTION);
                if(strQuestion == null || strQuestion.length() < 10)return null;
                JSONObject jsonQuestion = JSON.parseObject(strQuestion);
                if(jsonQuestion == null || jsonQuestion.isEmpty())return null;
                int iQuestionID = jsonQuestion.getIntValue("iQuestionID");
                String strSubject = jsonQuestion.getString("strSubject");
                boolean isMultiSonQuestion = jsonQuestion.getBooleanValue("isMultiSonQuestion");
                String strAudioFileName = jsonQuestion.getString("strAudioFileName");
                String strVideoUrl = jsonQuestion.getString("strVideoUrl");
                JSONArray jsonQuestionSons = jsonQuestion.getJSONArray("questions");
                QuestionNew questionNew = new QuestionNew(iQuestionID, strSubject, isMultiSonQuestion, strAudioFileName, strVideoUrl, jsonQuestionSons);
                result.add(questionNew);
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //get folder path , which save lots of question
    public static String getQuesFolderPath(String strCourseID, String strQueType, String strTestID, String strKPID){
        StringBuffer result = new StringBuffer( Util.APP_PATH + "/" + strCourseID + "/" + strQueType + "/" + strTestID );
        String[] strKPIDs = strKPID.split("\\.");
        for(int i = 0; i<strKPIDs.length; i++){
            result.append("/");
            result.append(strKPIDs[i]);
        }
        result.append("/");
        return result.toString();
    }

    public static List<QuestionUnmultiSon> parseUnmultiSonQueFromFile(String strCourseID
            , String strQuestionType, String strTestID, String strKPID){
        //absolute path
        String strKpIdAPPath = getQuesFolderPath(strCourseID, strQuestionType,strTestID,strKPID);
        if( !(new File(strKpIdAPPath).exists())){
            Log.e("Error Mutl...Activity", "Kp id absolute un exists");
            return null;
        }

        String[] questionsAPath = Util.getAllQuestionsAPath(strKpIdAPPath);
        List<QuestionUnmultiSon> questions = Util.parseUnmultiSonQueFromFile(questionsAPath);
        return questions;
    }

    private static List<QuestionUnmultiSon> parseUnmultiSonQueFromFile(String[] questionAPaths){
        if(questionAPaths == null || questionAPaths.length <= 0)return null;

        List<QuestionUnmultiSon> result = new ArrayList<>(questionAPaths.length);

        for(int i =0; i<questionAPaths.length; i++){
            String queFilePath = questionAPaths[i] + "/" + Util.FILE_NAME_QUESTION;
            File file = new File(queFilePath);
            if(!file.exists()){
                queFilePath = questionAPaths[i] + "/" + Util.FILE_NAME_QUESTION_ANOTHER;
                if( !(new File(queFilePath)).exists() ){
                    continue;
                }
            }
            String strQuestion = Util.getFileFromSD(queFilePath);
            if(strQuestion == null || strQuestion.length() < 10)return null;
            try {
                JSONObject jsonQuestion = JSON.parseObject(strQuestion);
            }catch (Exception e){
                e.printStackTrace();
                continue;
            }
            Gson gson = new Gson();
            QuestionUnmultiSon questionUnmultiSon = gson.fromJson(strQuestion,QuestionUnmultiSon.class);
            result.add(questionUnmultiSon);
        }
        return result;
    }

    public static List<QuestionUnmultiSon> parseUnmultiSonQueFromFile(List<DoneQuestion> doneQuestionList) {
        if(doneQuestionList == null) return null;
        List<QuestionUnmultiSon> result = new ArrayList<>(doneQuestionList.size());
        for(DoneQuestion doneQuestion : doneQuestionList){
            int iCourseID = doneQuestion.getICourseID();
            int iQuesType = doneQuestion.getIQuestionType();
            int iTestID = doneQuestion.getITestID();
            String strKPID = doneQuestion.getStrQuestionKpID();
            long iQueID = doneQuestion.getLQuestionID();

            String queFolderPath = getQuesFolderPath(String.valueOf(iCourseID), String.valueOf(iQuesType), String.valueOf(iTestID), strKPID);
            String quePath = queFolderPath + String.valueOf(iQueID) + "/";
            QuestionUnmultiSon questionUnmultiSon = parseUnmultiSonQueFromFile(quePath);
            if(questionUnmultiSon != null)result.add(questionUnmultiSon);
        }
        return result;
    }

    private static QuestionUnmultiSon parseUnmultiSonQueFromFile(String quePath) {
        if(quePath == null)return null;

        String queFilePath = quePath + Util.FILE_NAME_QUESTION;
        File file = new File(queFilePath);
        if(!file.exists()){
            queFilePath = quePath +Util.FILE_NAME_QUESTION_ANOTHER;
        }
        String strQuestion = Util.getFileFromSD(queFilePath);
        if(strQuestion == null || strQuestion.length() < 10)return null;
        try {
            JSONObject jsonQuestion = JSON.parseObject(strQuestion);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        Gson gson = new Gson();
        QuestionUnmultiSon questionUnmultiSon = gson.fromJson(strQuestion,QuestionUnmultiSon.class);
        return questionUnmultiSon;
    }

    public static List<Answer> parseMultiSonAnswerFromFile(String[] questionsAPath) {
        if(questionsAPath == null || questionsAPath.length <= 0)return null;
        List<Answer> result = new ArrayList<>(questionsAPath.length);
        for(int i = 0; i< questionsAPath.length; i++){
            String strAnswer = Util.getFileFromSD(questionsAPath[i] + "/" + Util.FILE_NAME_ANALYSIS);
            JSONObject jsonAnswer = JSON.parseObject(strAnswer);
            int iQuestionID = jsonAnswer.getIntValue("iQuestionID");
            boolean isMultiSonQuestion = jsonAnswer.getBooleanValue("isMultiSonQuestion");
            JSONArray jsonArraySons = jsonAnswer.getJSONArray("questions");
            Answer answer = new Answer(iQuestionID,isMultiSonQuestion,jsonArraySons);
            result.add(answer);
        }
        return result;
    }

    public static List<UnmultiSonAnalysis> parseUnmultiSonAnswerFromFile(String strCourseID, String strQuestionType, String strTestID, String strKPID) {
        //absolute path
        String strKpIdAPPath = getQuesFolderPath(strCourseID, strQuestionType,strTestID,strKPID);
        if( !(new File(strKpIdAPPath).exists())){
            Log.e("Error Mutl...Activity", "Kp id absolute un exists");
            return null;
        }
        String[] questionsAPath = Util.getAllQuestionsAPath(strKpIdAPPath);
        List<UnmultiSonAnalysis> analysises = Util.parseUnmultiSonAnswerFromFile(questionsAPath);
        return analysises;
    }

    private static List<UnmultiSonAnalysis> parseUnmultiSonAnswerFromFile(String[] questionsAPath){
        if(questionsAPath == null || questionsAPath.length <= 0)return null;
        List<UnmultiSonAnalysis> result = new ArrayList<>(questionsAPath.length);
        for(int i =0 ;i<questionsAPath.length; i++){
            String analysisFilePath = questionsAPath[i] + "/" + Util.FILE_NAME_ANALYSIS;
            if( !(new File(analysisFilePath).exists()))analysisFilePath = questionsAPath[i] + "/" + Util.FILE_NAME_ANALYSIS_ANOTHER;
            String strAnalysisJson = Util.getFileFromSD(analysisFilePath);
            if(strAnalysisJson == null)continue;
            JSONObject jsonAnalysis = JSON.parseObject(strAnalysisJson);
            int iQuestionID = jsonAnalysis.getIntValue("iQuestionID");
            int iMultiSonQuestion = jsonAnalysis.getIntValue("iMultiSonQuestion");
            String strAnalysis = jsonAnalysis.getString("strAnalysis");
            JSONArray answerJSONArray = jsonAnalysis.getJSONArray("answer");
            List<String> answers = new ArrayList<>(answerJSONArray.size());
            for(int j = 0; j<answerJSONArray.size(); j++){
                answers.add(answerJSONArray.getJSONObject(j).getString("ID"));
            }
            result.add(new UnmultiSonAnalysis(iQuestionID,iMultiSonQuestion,strAnalysis,answers));
        }
        return result;
    }


    public static List<UnmultiSonAnalysis> parseUnmultiSonAnswerFromFile(List<DoneQuestion> doneQuestionList) {
        if(doneQuestionList == null) return null;
        List<UnmultiSonAnalysis> result = new ArrayList<>(doneQuestionList.size());

        for(DoneQuestion doneQuestion : doneQuestionList){
            int iCourseID = doneQuestion.getICourseID();
            int iQuesType = doneQuestion.getIQuestionType();
            int iTestID = doneQuestion.getITestID();
            String strKPID = doneQuestion.getStrQuestionKpID();
            long iQueID = doneQuestion.getLQuestionID();

            String queFolderPath = getQuesFolderPath(String.valueOf(iCourseID), String.valueOf(iQuesType), String.valueOf(iTestID), strKPID);
            String quePath = queFolderPath + String.valueOf(iQueID) + "/";
            UnmultiSonAnalysis unmultiSonAnalysis = parseUnmultiSonAnswerFromFile(quePath);
            if(unmultiSonAnalysis != null)
                result.add(unmultiSonAnalysis);
        }
        return result;
    }

    private static UnmultiSonAnalysis parseUnmultiSonAnswerFromFile(String quePath) {
        if(quePath == null )return null;
        String analysisFilePath = quePath + Util.FILE_NAME_ANALYSIS;
        if( !(new File(analysisFilePath).exists()))analysisFilePath = quePath + Util.FILE_NAME_ANALYSIS_ANOTHER;
        String strAnalysisJson = Util.getFileFromSD(analysisFilePath);
        if(strAnalysisJson == null)return null;
        JSONObject jsonAnalysis = JSON.parseObject(strAnalysisJson);
        int iQuestionID = jsonAnalysis.getIntValue("iQuestionID");
        int iMultiSonQuestion = jsonAnalysis.getIntValue("iMultiSonQuestion");
        String strAnalysis = jsonAnalysis.getString("strAnalysis");
        JSONArray answerJSONArray = jsonAnalysis.getJSONArray("answer");
        List<String> answers = new ArrayList<>(answerJSONArray.size());
        for(int j = 0; j<answerJSONArray.size(); j++){
            answers.add(answerJSONArray.getJSONObject(j).getString("ID"));
        }
        return new UnmultiSonAnalysis(iQuestionID,iMultiSonQuestion,strAnalysis,answers);
    }


}
