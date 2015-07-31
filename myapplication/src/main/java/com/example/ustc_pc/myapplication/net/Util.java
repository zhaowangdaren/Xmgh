package com.example.ustc_pc.myapplication.net;

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


    public static boolean isPhoneNumber(String strPhone) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(strPhone);
        return m.matches();
    }

}
