package com.example.ustc_pc.myapplication.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.ustc_pc.myapplication.unit.Answer;
import com.example.ustc_pc.myapplication.unit.FileOperation;
import com.example.ustc_pc.myapplication.unit.QuestionNew;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ustc-pc on 2015/7/25.
 */
public class Util {
    public static final String URL_HOME = "http://192.168.2.97/xmghProject/account";
    public static final String URL_PHONE_CHECK = URL_HOME+"/PhoneCheck";

    public static String URL_REGISTER_BY_PHONE = URL_HOME+"/PhoneRegister";
    public static String URL_LOGIN_BY_PHONE = URL_HOME+"/PhoneLogin";
    public static String URL_SET_PERSONAL_INFO = URL_HOME + "/SetPersonInfo";
    public static String URL_GET_PERSONAL_INFO = URL_HOME + "/GetPersonInfo";
    public static String URL_ADD_COURSE = URL_HOME + "/AddCourse";
    public static String URL_GET_KPs = URL_HOME + "/GetKPs";
    public static String URL_GET_SELECTED_COURSES = URL_HOME + "/GetSelectedCourses";
    public static String URL_GET_COURSES_INFO = URL_HOME + "/GetCoursesInfo";

    public static int iNo_USERID = -1;
    public static int NO_LAST_COURSE = -1;

    public static final int PHONE_LOGIN = 0, QQ_LOGIN = 1, WEIBO_LOGIN = 2, WECHAT_LOGIN = 3;
    public static String DB_NAME = "cn.edu.ustc.xmgh.db";
    public static Integer FIRST_LEVEL_KP = 1, SECOND_LEVEL_KP = 2, THIRD_LEVEL_KP = 3, LAST_LEVEL_KP = 4;
    public static String URL_GET_QUESTIONS = URL_HOME + "GetQuestions";
    public static String APP_PATH = Environment.getExternalStorageDirectory()+"/.cn.edu.ustc.xmgh/";
    public static int BASIC_TEST = 1, REAL_TEST = 2, SPECIAL_TEST = 3, MOCK_TEST = 4;

    public static final int TYPE_QUESTION_LAYOUT_HEADER = 0, TYPE_QUESTION_LAYOUT_FATHER = 1,
            TYPE_QUESTION_LAYOUT_OPTION = 2, TYPE_QUESTION_LAYOUT_ANALYSIS = 4;

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
        for(int i = 0; i<questionsFolderName.length; i++){
            questionsFolderName[i] = absolutePath + questionsFolderName[i];
        }
        return questionsFolderName;
    }

    public static List<QuestionNew> parseQuestionsFromFile(String[] questionsAPath) {
        if(questionsAPath.length <= 0)return null;
        List<QuestionNew> result = new ArrayList<>(questionsAPath.length);
        for(int i = 0; i< questionsAPath.length;i++){
            String strQuestion = FileOperation.getFileFromSD(questionsAPath[i] + "/" + "question.json");
            JSONObject jsonQuestion = JSON.parseObject(strQuestion);
            int iQuestionID = jsonQuestion.getIntValue("iQuestionID");
            String strSubject = jsonQuestion.getString("strSubject");
            boolean isMultiSonQuestion = jsonQuestion.getBooleanValue("isMultiSonQuestion");
            String strAudioFileName = jsonQuestion.getString("strAudioFileName");
            String strVideoUrl = jsonQuestion.getString("strVideoUrl");
            JSONArray jsonQuestionSons = jsonQuestion.getJSONArray("questions");
            QuestionNew questionNew = new QuestionNew(iQuestionID,strSubject,isMultiSonQuestion,strAudioFileName,strVideoUrl, jsonQuestionSons);
            result.add(questionNew);
        }
        return result;
    }



    public static List<Answer> parseAnswerFromFile(String[] questionsAPath) {
        if(questionsAPath.length <= 0)return null;
        List<Answer> result = new ArrayList<>(questionsAPath.length);
        for(int i = 0; i< questionsAPath.length; i++){
            String strAnswer = FileOperation.getFileFromSD(questionsAPath[i] + "/" + "analysis.json");
            JSONObject jsonAnswer = JSON.parseObject(strAnswer);
            int iQuestionID = jsonAnswer.getIntValue("iQuestionID");
            boolean isMultiSonQuestion = jsonAnswer.getBooleanValue("isMultiSonQuestion");
            JSONArray jsonArraySons = jsonAnswer.getJSONArray("questions");
            Answer answer = new Answer(iQuestionID,isMultiSonQuestion,jsonArraySons);
            result.add(answer);
        }
        return result;
    }
}
