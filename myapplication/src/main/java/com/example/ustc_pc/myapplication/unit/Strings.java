package com.example.ustc_pc.myapplication.unit;

/**
 * Created by ustc-pc on 2015/2/3.
 */
public class Strings {
    public static String URL_HOME = "http://123.59.59.57:8080/xmghProject/account/";
    public static String URL_SON = "yanzhaou/";
    public static String URL_ERROR_QUESTION = "";

    public static String ACCEPT_MISSION_URL = "";
    public static String PUBLISH_URL = "";
    public static String AUTH_URL = "";
    public static String REGISTER_URL = URL_HOME+"registerProfile";
    public static String LOGIN_URL = URL_HOME+"login";
    public static String GET_THE_CODE = "";
    public static String URL_INDIVIDUATION = URL_HOME+"";
    public static String ASSESSMENT_URL = "http://home.ustc.edu.cn/~yanzhaou/education/assessment_info.json";
    public static String COURSES_URL = URL_HOME+"generateCourse";
    public static String KPPAPERS_URL = URL_HOME+"getTestPaperList";
    public static String PAPER_URL = URL_HOME+"getTestPaper";
    public static String test = "http://home.ustc.edu.cn/~yanzhaou/";

    public static String URL_UPLOAD_PAPER_INFO = URL_HOME+"saveUserItemInfo";
    public static String URL_UPLOAD_USER_ICON = URL_HOME+"saveUserIcon";
    public static String URL_GET_USER_ICON = URL_HOME+"getUserIcon";
    public static String URL_GET_USER_QUESTION_INFO =URL_HOME+"getUserItemInfo";
    public static String URL_GET_FINISHED_PAPER = URL_HOME+"getUserFinishedPaper";
    public static String URL_ZHEN_TI = URL_HOME+"";
    public static String URL_GET_USER_SCORE = URL_HOME + "getUserScore";

    public static String DB_NAME= "cn.edu.ustc.education.db";
    public static String TABLE_NAME_HISTORY_PAPERS = "history_papers";
    public static String CREATE_HISTORY_PAPERS_TABLE ="CREATE TABLE IF NOT EXISTS history_papers("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            +"courseID INTEGER,"
            + "paperID TEXT,"
            +"name TEXT,"
            +"score INTEGER,"
            +"num INTEGER,"
            +"startTime INTEGER,"
            +"endTime INTEGER,"
            +"spendTime TEXT,"
            +"hasUpload INTEGER);";
    public static String TABLE_NAME_LOG_QUESTIONS = "log_questions";
    /**
     * (_id, paper id, question id, 试卷名， 一级知识点名， 二级知识点名， 三级知识点， 用户的答案， 笔记， 错误次数， 类型：)
     */
    public static String CREATE_LOG_QUESTIONS_TABLE = "CREATE TABLE IF NOT EXISTS log_questions("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            +"courseID INTEGER,"
            + "paperID TEXT,"
            + "questionID TEXT,"
            + "paperName TEXT,"
            + "firstKP TEXT,"
            + "secondKP TEXT,"
            + "thirdKP TEXT,"
            + "isCorrect INTEGER,"
            + "isFavorite INTEGER,"
            + "note TEXT,"
            + "errorTimes INTEGER,"
            + "spendTime INTEGER);";
    public static int TYPE_FAVORITE = 1, TYPE_ERROR = 2, TYPE_FAVORITE_AND_ERROR = 3, TYPE_NOTE = 4,
             TYPE_ERROR_TEST = 5, TYPE_PAPER = 6;
    public static String URL_CHECK_UPDATE = "http://home.ustc.edu.cn/~yanzhaou/xmgh/version.json";


    public String int2Letter(int index){
        String str = "";
        switch (index){
            case 0:
                str="A";
                break;
            case 1:
                str="B";
                break;
            case 2:
                str="C";
                break;
            case 3:
                str="D";
                break;
            case 4:
                str="E";
                break;
            case 5:
                str="F";
                break;
            default:
                str="G";
        }
        return str;
    }

}
