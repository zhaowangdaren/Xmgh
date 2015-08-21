package com.example.ustc_pc.myapplication.db;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.ustc_pc.myapplication.unit.Util;

import java.io.ByteArrayInputStream;

/**
 * Created by ustc-pc on 2015/3/19.
 */
public class UserSharedPreference {

    static SharedPreferences mSharedPre;
    Context mContext;

    private Integer iUserID;

    private String strThirdID = "";

    private String strPhoneNumber = "";
    private String strUserName = "";

    private Integer iGender;
    private String strEmail = "";
    private String strAboutMe = "";
    private Integer iUserType;
    private String strSourceCollege = "";
    private String strSourceMajor = "";
    private String strFirstTargetCollege = "";
    private String strFirstTargetMajor = "";
    private String strSecondTargetCollege = "";
    private String strSecondTargetMajor = "";
    private String strAcceptedCollege = "";
    private String strAcceptedMajor = "";

    private int iLastCourseID;
    private Boolean isLogin;
    private int iAccountType;

    private Boolean isNeverSelectedCourse;
    private Integer iAssessmentScore;

    public UserSharedPreference(Context context){
        mContext = context;
        mSharedPre = context.getSharedPreferences("",Activity.MODE_PRIVATE);
    }


    public boolean hasUploadUserIcon(){
        return mSharedPre.getBoolean("hasUploadUserIcon", false);
    }

    public void setUserPhoto(String strPhoto){
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putString("userPhoto", strPhoto);
        editor.putBoolean("hasUploadUserIcon", false);
        editor.apply();
    }

    public Bitmap getUserPhotoBitmap(){
        String imageString = mSharedPre.getString("userPhoto", "");
        if(imageString.length() > 0) {
            try {
                //第二步:利用Base64将字符串转换为ByteArrayInputStream
                byte[] byteArray = Base64.decode(imageString, Base64.DEFAULT);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
                //第三步:利用ByteArrayInputStream生成Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
                return bitmap;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }else{
            return null;
        }
    }

    public void setIsUserInfoChanged(boolean isChanged){
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putBoolean("isUserInfoChanged", isChanged);
        editor.apply();
    }

    public boolean isUserInfoChanged(){
        return mSharedPre.getBoolean("isUserInfoChanged", false);
    }


    public void setRealName(String realName){
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putString("realName", realName);
        editor.putBoolean("isUserInfoChanged", true);
        editor.apply();
    }

    public String getAccountNumber(){
        return mSharedPre.getString("accountNumber", "");
    }

//    /**
//     *
//     * @param iUserId
//     */
//    public void setIUserId(int iUserId){
//        SharedPreferences.Editor editor = mSharedPre.edit();
//        editor.putInt("iUserID", iUserId);
//        editor.apply();
//    }
//
//    public int getIUserID(){
//        return mSharedPre.getInt("iUserID", -1);
//    }

    public void setPassword(String password){
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putString("password", password);
        editor.apply();
    }

    public String getPassword(){
        return mSharedPre.getString("password", "");
    }

    public void setSumAssessmentScore(String courseName, int sum_assessment_score) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putInt(courseName,sum_assessment_score);
        editor.apply();
    }

    public int getSumAssessmentScore(String courseName){
        return mSharedPre.getInt(courseName,0);
    }

    public void setHasGetUserQuestionInfo(boolean hasGetUserQuestionInfo) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putBoolean("hasGetUserQuestionInfo", hasGetUserQuestionInfo);
        editor.apply();
    }

    public boolean isHasGetUserQuestionInfo() {
        return mSharedPre.getBoolean("hasGetUserQuestionInfo", false);
    }

    public void clear() {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.clear();
        editor.apply();
    }

    public int getLastCouresID(){
        return mSharedPre.getInt("lastCourseID", -1);
    }

    public void setLastCourseID(int position) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putInt("lastCourseID", position);
        editor.apply();
    }

    public Boolean getIsLogin() {
        return mSharedPre.getBoolean("isLogin",false);
    }

    public void setIsLogin(boolean isLogin){
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putBoolean("isLogin", isLogin);
        editor.apply();
    }

    public Integer getiUserID() {
        return mSharedPre.getInt("iUserID", Util.iNo_USERID);
    }

    public void setiUserID(Integer iUserID) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putInt("iUserID", iUserID);
        editor.apply();
    }

    public String getStrThirdID() {
        return mSharedPre.getString("strThirdID","");
    }

    public void setStrThirdID(String strThirdID) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putString("strThirdID",strThirdID);
        editor.apply();
    }

    public String getStrPhoneNumber() {
        return mSharedPre.getString("strPhoneNumber","");
    }

    public void setStrPhoneNumber(String strPhoneNumber) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putString("strPhoneNumber",strPhoneNumber);
        editor.apply();
    }

    public String getStrUserName() {
        return mSharedPre.getString("strUserName","");
    }

    public void setStrUserName(String strUserName) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putString("strUserName",strUserName);
        editor.apply();
    }

    public Integer getiGender() {
        return mSharedPre.getInt("iGender", 0);
    }

    public void setiGender(Integer iGender) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putInt("iGender", iGender);
        editor.apply();
    }

    public String getStrEmail() {
        return mSharedPre.getString("strEmail", "");
    }

    public void setStrEmail(String strEmail) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putString("strEmail",strEmail);
        editor.apply();
    }

    public String getStrAboutMe() {
        return mSharedPre.getString("strAboutMe", "");
    }

    public void setStrAboutMe(String strAboutMe) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putString("strAboutMe",strAboutMe);
        editor.apply();
    }

    public Integer getiUserType() {
        return mSharedPre.getInt("iUserType", 0);
    }

    public void setiUserType(Integer iUserType) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putInt("iUserType", iUserType);
        editor.apply();
    }

    public String getStrSourceCollege() {
        return mSharedPre.getString("strSourceCollege", "");
    }

    public void setStrSourceCollege(String strSourceCollege) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putString("strSourceCollege",strSourceCollege);
        editor.apply();
    }

    public String getStrSourceMajor() {
        return mSharedPre.getString("strSourceMajor", "");
    }

    public void setStrSourceMajor(String strSourceMajor) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putString("strSourceMajor",strSourceMajor);
        editor.apply();
    }

    public String getStrFirstTargetCollege() {
        return mSharedPre.getString("strFirstTargetCollege", "");
    }

    public void setStrFirstTargetCollege(String strFirstTargetCollege) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putString("strFirstTargetCollege",strFirstTargetCollege);
        editor.apply();
    }

    public String getStrFirstTargetMajor() {
        return mSharedPre.getString("strFirstTargetMajor", "");
    }

    public void setStrFirstTargetMajor(String strFirstTargetMajor) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putString("strFirstTargetMajor",strFirstTargetMajor);
        editor.apply();
    }

    public String getStrSecondTargetCollege() {
        return mSharedPre.getString("strSecondTargetCollege", "");
    }

    public void setStrSecondTargetCollege(String strSecondTargetCollege) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putString("strSecondTargetCollege",strSecondTargetCollege);
        editor.apply();
    }

    public String getStrSecondTargetMajor() {
        return mSharedPre.getString("strSecondTargetMajor", "");
    }

    public void setStrSecondTargetMajor(String strSecondTargetMajor) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putString("strSecondTargetMajor",strSecondTargetMajor);
        editor.apply();
    }

    public String getStrAcceptedCollege() {
        return mSharedPre.getString("strAcceptedCollege", "");
    }

    public void setStrAcceptedCollege(String strAcceptedCollege) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putString("strAcceptedCollege",strAcceptedCollege);
        editor.apply();
    }

    public String getStrAcceptedMajor() {
        return mSharedPre.getString("strAcceptedMajor", "");
    }

    public void setStrAcceptedMajor(String strAcceptedMajor) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putString("strAcceptedMajor",strAcceptedMajor);
        editor.apply();
    }

    public int getILastCourseID() {
        return mSharedPre.getInt("iLastCourseID",-1);
    }

    public void setILastCourseID(int iLastCourseID) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putInt("iLastCourseID", iLastCourseID);
        editor.apply();
    }

    public int getiAccountType() {
        return mSharedPre.getInt("iAccountType", Util.PHONE_LOGIN);
    }

    public void setiAccountType(int iAccountType) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putInt("iAccountType", iAccountType);
        editor.apply();
    }

    public Boolean getIsNeverSelectedCourse() {
        return mSharedPre.getBoolean("isNeverSelectedCourse",true);
    }

    public void setIsNeverSelectedCourse(Boolean isNeverSelectedCourse) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putBoolean("isNeverSelectedCourse", isNeverSelectedCourse);
        editor.apply();
    }

    public Integer getiAssessmentScore() {
        return mSharedPre.getInt("iAssessmentScore", -1);
    }

    public void setiAssessmentScore(int score){
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putInt("iAssessmentScore", score);
        editor.apply();
    }

}
