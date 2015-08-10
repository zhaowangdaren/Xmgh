package com.example.ustc_pc.myapplication.net;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.ustc_pc.myapplication.dao.Course;
import com.example.ustc_pc.myapplication.dao.DoneQuestion;
import com.example.ustc_pc.myapplication.dao.KPs;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.unit.AssessmentScore;
import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by ustc-pc on 2015/7/25.
 */
public class OkHttpUtil {
    private final OkHttpClient client;

    public OkHttpUtil(){
        client  = new OkHttpClient();
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setWriteTimeout(10, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);
    }

    public OkHttpClient getClient(){
        return client;
    }
    /**
     * check phone number is registered
     * @param phoneNumber
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public boolean hasRegisted(String phoneNumber) throws IOException{
        RequestBody formBody = new FormEncodingBuilder()
                .add("strPhoneNumber", phoneNumber)
                .build();
        Request request = new Request.Builder()
                .url(Util.URL_PHONE_CHECK)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful())throw new IOException("Unexpected cod "+response);
        JSONObject result = JSONObject.parseObject(response.body().string());
        int iResult = result.getInteger("iRegistered");
        switch (iResult){
            case -1:
                return false;
            case 1:
                return true;
            default: return false;
        }
    }

    /**
     * If register success then return the userID, otherwise return -1
     * @param phoneNumber
     * @param strPassword
     * @return iUserID
     * @throws IOException
     */
    public int registerByPhone (String phoneNumber, String strPassword)throws IOException{
        RequestBody formBody = new FormEncodingBuilder()
                .add("strPhoneNumber", phoneNumber)
                .add("strPassword", strPassword)
                .build();
        Request request = new Request.Builder()
                .url(Util.URL_REGISTER_BY_PHONE)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful())throw new IOException("Unexcepted cod "+response);
        JSONObject result = JSONObject.parseObject(response.body().toString());
        int iResult = result.getIntValue("iResult");
        if(iResult == 1){
            return result.getIntValue("iUserID");
        }else{
            return -1;
        }

    }


    /**
     * If login success then return the userID, otherwise return -1
     * @param phoneNumber
     * @param strPassword
     * @return
     * @throws IOException
     */
    public int phoneLogin(String phoneNumber, String strPassword) throws IOException {
        RequestBody formBody = new FormEncodingBuilder()
                .add("strPhoneNumber", phoneNumber)
                .add("strPassword", strPassword)
                .build();
        Request request = new Request.Builder()
                .url(Util.URL_LOGIN_BY_PHONE)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful())throw new IOException("Unexcepted cod "+response);
        JSONObject result = JSONObject.parseObject(response.body().string());
        int iLoginResult = result.getIntValue("iLoginResult");
        if(iLoginResult == 1){
            return result.getIntValue("iUserID");
        }else{
            return -1;
        }
    }

    /**
     *
     * @param iAccountType
     * @param strThirdID
     * @param strUserName
     * @return
     * @throws IOException
     */
    public int thirdLogin(int iAccountType, String strThirdID, String strUserName) throws IOException {
        RequestBody formBody = new FormEncodingBuilder()
                .add("iAccountType", String.valueOf(iAccountType))
                .add("strThirdID", strThirdID)
                .add("strUserName", strUserName)
                .build();
        Request request = new Request.Builder()
                .url(Util.URL_LOGIN_BY_PHONE)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful())throw new IOException("Unexcepted cod "+response);
        JSONObject result = JSONObject.parseObject(response.body().string());
        int iLoginResult = result.getIntValue("iLoginResult");
        if(iLoginResult == 1){
            return result.getIntValue("iUserID");
        }else{
            return -1;
        }
    }

    public boolean setPersonInfo(Context context) throws IOException {
        UserSharedPreference userSharedPreference = new UserSharedPreference(context);
        int iAccountType = userSharedPreference.getiAccountType();
        int iUserID = userSharedPreference.getiUserID();
        String strThirdID = userSharedPreference.getStrThirdID();
        String strPassword = userSharedPreference.getPassword();

        JSONObject personInfoJson = new JSONObject();
        personInfoJson.put("iUserID", iUserID);
        personInfoJson.put("strUserName",userSharedPreference.getStrUserName());
        personInfoJson.put("iGender",userSharedPreference.getiGender());
        personInfoJson.put("strEmail",userSharedPreference.getStrEmail());
        personInfoJson.put("strAboutMe",userSharedPreference.getStrAboutMe());
        personInfoJson.put("iUserType",userSharedPreference.getiUserType());
        personInfoJson.put("strSourceCollege",userSharedPreference.getStrSourceCollege());
        personInfoJson.put("strSourceMajor",userSharedPreference.getStrSourceMajor());
        personInfoJson.put("strFirstTargetCollege",userSharedPreference.getStrFirstTargetCollege());
        personInfoJson.put("strFirstTargetMajor",userSharedPreference.getStrFirstTargetMajor());
        personInfoJson.put("strSecondTargetCollege",userSharedPreference.getStrSecondTargetCollege());
        personInfoJson.put("strSecondTargetMajor",userSharedPreference.getStrSecondTargetMajor());
        personInfoJson.put("strAcceptedCollege",userSharedPreference.getStrAcceptedCollege());
        personInfoJson.put("strAcceptedMajor",userSharedPreference.getStrAcceptedMajor());

        RequestBody formBody = new FormEncodingBuilder()
                .add("iAccountType",String.valueOf(iAccountType))
                .add("iUserID", String.valueOf(iUserID))
                .add("strThirdID", strThirdID)
                .add("strPassword", strPassword)
                .add("strPersonalInfo", personInfoJson.toJSONString())
                .build();
        Request request = new Request.Builder()
                .url(Util.URL_SET_PERSONAL_INFO)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful())throw new IOException("Unexcepted cod "+response);
        JSONObject result = JSONObject.parseObject(response.body().string());
        int iResult = result.getIntValue("iResult");
        return iResult == 1 ? true:false;
    }


    public boolean getPersonalInfo(Context context, int iAccountType, int iUserID, String strPassword, String strThirdID) {
        try {
            RequestBody formBody = new FormEncodingBuilder()
                    .add("iAccountType", String.valueOf(iAccountType))
                    .add("iUserID", String.valueOf(iUserID))
                    .add("strPassword", strPassword)
                    .add("strThirdID", strThirdID)
                    .build();
            Request request = new Request.Builder()
                    .url(Util.URL_GET_PERSONAL_INFO)
                    .post(formBody)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexcepted cod " + response);
            JSONObject result = JSONObject.parseObject(response.body().string());
            JSONObject personInfoJson = result.getJSONObject("strPersonInfo");
            UserSharedPreference userSharedPreference = new UserSharedPreference(context);
            userSharedPreference.setiUserID(personInfoJson.getIntValue("iUserID"));
            userSharedPreference.setStrUserName(personInfoJson.getString("strUserName"));
            userSharedPreference.setiGender(personInfoJson.getIntValue("iGender"));
            userSharedPreference.setStrEmail(personInfoJson.getString("strEmail"));
            userSharedPreference.setStrAboutMe(personInfoJson.getString("strAboutMe"));
            userSharedPreference.setiUserType(personInfoJson.getIntValue("iUserType"));
            userSharedPreference.setStrSourceCollege(personInfoJson.getString("strSourceCollege"));
            userSharedPreference.setStrSourceMajor(personInfoJson.getString("strSourceMajor"));
            userSharedPreference.setStrFirstTargetCollege(personInfoJson.getString("strFirstTargetCollege"));
            userSharedPreference.setStrFirstTargetMajor(personInfoJson.getString("strFirstTargetMajor"));
            userSharedPreference.setStrSecondTargetCollege(personInfoJson.getString("strSecondTargetCollege"));
            userSharedPreference.setStrSecondTargetMajor(personInfoJson.getString("strSecondTargetMajor"));
            userSharedPreference.setStrAcceptedCollege(personInfoJson.getString("strAcceptedCollege"));
            userSharedPreference.setStrAcceptedMajor(personInfoJson.getString("strAcceptedMajor"));
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     *
     * @return
     * @throws IOException
     */
    public ArrayList<Course> getAllCoursesInfo() throws IOException {

        Request request = new Request.Builder()
                .url(Util.URL_GET_COURSES_INFO)
                .build();
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful())throw new IOException("Unexcepted cod "+response);
        JSONObject jsonResult = JSONObject.parseObject(response.body().string());
        JSONArray jsonCourses = jsonResult.getJSONArray("courses");
        int size = jsonCourses.size();
        if(size <= 0)return null;

        ArrayList<Course> courses = new ArrayList<>();
        Gson gson = new Gson();
        for(int i = 0; i< size; i++){
            Course course = gson.fromJson(jsonCourses.get(i).toString(), Course.class);
            courses.add(course);
        }
        return courses;
    }

    public ArrayList<Course> getSelectedCourses(int iUserID) throws IOException {
       ArrayList<Course> courses = new ArrayList<>();

        RequestBody formBody = new FormEncodingBuilder()
                .add("iUserID", String.valueOf(iUserID))
                .build();
        Request request = new Request.Builder()
                .url(Util.URL_GET_SELECTED_COURSES)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful())throw new IOException("Unexcepted cod "+response);
        JSONObject jsonObject = JSONObject.parseObject(response.body().string());
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("courses");
            int size = jsonArray.size();
            Gson gson = new Gson();
            for(int i=0; i< size ; i++){
                Course course = gson.fromJson(jsonArray.getJSONObject(i).toJSONString(), Course.class);
                course.setIsSelected(true);
                courses.add(course);
            }
        }catch (JSONException e){

        }

        return courses;
    }
    /**
     *
     * @param iUserID
     * @param courseIDs
     * @return
     * @throws IOException
     */
    public boolean addCourse(int iUserID, List<Course> courseIDs) throws IOException {
        if(courseIDs.size() <= 0)return true;
        StringBuffer stringBuffer = new StringBuffer();
        for(Course course: courseIDs){
            stringBuffer.append(course.getICourseID()+",");
        }
        if(stringBuffer.charAt(stringBuffer.length() - 1)==',')//delete the last ','
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        RequestBody formBody = new FormEncodingBuilder()
                .add("iUserID", String.valueOf(iUserID))
                .add("strSelectedCoursesID", stringBuffer.toString())
                .build();
        Request request = new Request.Builder()
                .url(Util.URL_ADD_COURSE)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful())throw new IOException("Unexcepted cod "+ response);
        JSONObject jsonObject = JSONObject.parseObject( response.body().string());
        if(jsonObject.getIntValue("iResult") == 1) return true;
        return false;
    }

    /**
     *
     * @param iUserID
     * @param courses
     * @return
     * @throws IOException
     */
    public boolean deleteCourse(int iUserID, List<Course> courses) throws IOException {
        if(courses.size() <= 0)return true;
        StringBuffer stringBuffer = new StringBuffer();
        for(Course course : courses){
            stringBuffer.append(course.getICourseID()+",");
        }
        if(stringBuffer.charAt(stringBuffer.length() - 1)==',')//delete the last ','
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        RequestBody formBody = new FormEncodingBuilder()
                .add("iUserID", String.valueOf(iUserID))
                .add("strDeletedCoursesID", stringBuffer.toString())
                .build();
        Request request = new Request.Builder()
                .url(Util.URL_DEL_COURSE)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful())throw new IOException("Unexcepted cod "+ response);
        JSONObject jsonObject = JSONObject.parseObject( response.body().string());
        if(jsonObject.getIntValue("iResult") == 1) return true;
        return false;
    }

    public ArrayList<KPs> getKPs(int iUserID, int iCourseID) throws IOException {
        ArrayList<KPs> kPses = new ArrayList<>();
        RequestBody formBody = new FormEncodingBuilder()
                .add("iUserID", String.valueOf(iUserID))
                .add("iCourseID", String.valueOf(iCourseID))
                .build();
        Request request = new Request.Builder()
                .url(Util.URL_GET_KPs)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful())throw new IOException("Unexcepted cod "+ response);
        JSONObject jsonObject = JSONObject.parseObject(response.body().string());
        JSONArray jsonArray = jsonObject.getJSONArray("kps");
        Gson gson = new Gson();
        for(int i=0; i< jsonArray.size(); i++){
            KPs kPs =gson.fromJson(jsonArray.getJSONObject(i).toJSONString(), KPs.class);
            kPses.add(kPs);
        }
        return kPses;
    }

    /**
     * Get all Question download URL
     * @param iCourseID
     * @param iQuestionType
     * @param strKPID
     * @return
     * @throws IOException
     */
    public URL getQuestionsURL(int iCourseID, int iQuestionType, String strKPID) throws IOException {
        RequestBody formBody = new FormEncodingBuilder()
                .add("iCoueseID", String.valueOf(iCourseID))
                .add("iQuestionType", String.valueOf(iQuestionType))
                .add("strKPID", strKPID)
                .build();
        Request request = new Request.Builder()
                .url(Util.URL_GET_QUESTIONS)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful())throw new IOException("Unexcepted cod "+ response);
        String strResult = response.body().string();
        if(strResult.length() <= 0)throw new IOException("Unexcepted cod "+ response);
        JSONObject jsonObject = JSON.parseObject(strResult);
        String strURL = jsonObject.getString("strDonUrl");
        if(strURL == null || strURL.length() <= 0)
            throw new IOException("Unexcepted cod "+ response);
        return new URL(strURL);
    }

    public HashMap<String, Object> getBasicTestOnline(int iUserID, int iCourseID, int iQuestionType, String strKPID) throws IOException {
        RequestBody formBody = new FormEncodingBuilder()
                .add("iUserID", String.valueOf(iUserID))
                .add("iCourseID", String.valueOf(iCourseID))
                .add("strKPID", strKPID)
                .add("iQuestionType", String.valueOf(iQuestionType))
                .build();
        Request request = new Request.Builder()
                .url(Util.URL_GET_BASIC_TEST_ONLINE)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexcepted cod " + response);
        String strResult = response.body().string();
        if (strResult.length() <= 0) throw new IOException("Unexcepted cod " + response);
        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(strResult);
            int iTestID = jsonObject.getIntValue("iTestID");
            String strURL = jsonObject.getString("strURL");
            if (strURL == null || strURL.length() <= 0)
                throw new IOException("Unexcepted cod " + response);
            HashMap<String, Object> result = new HashMap<>();
            result.put("iTestID", iTestID);
            result.put("strURL", strURL);
            return result;
        }catch (JSONException e){
            Log.e("Error",e.toString());
            return null;
        }
    }
    public void downloadQuestions(int iCourseID, int iQuestionType, String strKPID)throws IOException{
        RequestBody formBody = new FormEncodingBuilder()
                .add("iCourseID", String.valueOf(iCourseID))
                .add("iQuestionType", String.valueOf(iQuestionType))
                .add("strKPID", strKPID)
                .build();
        Request request = new Request.Builder()
                .url(Util.URL_GET_QUESTIONS)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful())throw new IOException("Unexcepted cod "+ response);
        String strResult = response.body().string();
        if(strResult.length() <= 0)throw new IOException("Unexcepted cod "+ response);
        JSONObject jsonObject = JSON.parseObject(strResult);
        String strURL = jsonObject.getString("strDonUrl");
        if(strURL == null || strURL.length() <= 0)
            throw new IOException("Unexcepted cod "+ response);

        Response fileResponse = client.newCall(
                new Request.Builder().url(strURL).get().build()
                ).execute();
        if(!fileResponse.isSuccessful())throw new IOException("Unexcepted cod" + fileResponse);
        InputStream inputStream = null;
        inputStream = fileResponse.body().byteStream();
        OutputStream outputStream = new FileOutputStream(Util.APP_PATH);
        byte[] buff = new byte[1024 * 4];
        long downloaded = 0;
        long target = fileResponse.body().contentLength();
        while(true){
            int readed = inputStream.read(buff);
            if(readed == -1)break;

            //write buff
            outputStream.write(buff, 0, readed);
            downloaded += readed;
            if(target > 0){
//                publishProgress((int) (downloaded * 100 / target));
//                onProgressUpdate((int) (downloaded * 100 / target));
            }
        }
    }
    /**
     * Upload done questions
     * @param iUserID
     * @param doneQuestions
     * @return
     */
    public Boolean uploadDoneQuestions(int iUserID
            , long lTestID, String strTestKPID
            , List<DoneQuestion> doneQuestions) throws IOException {
        if(doneQuestions.size() == 0)return true;
        JSONArray jsonArray = new JSONArray();

        Gson gson = new Gson();
        for(DoneQuestion doneQuestion : doneQuestions){
            jsonArray.add(gson.toJson(doneQuestion));
        }

        JSONObject testItem = new JSONObject();
        testItem.put("lTestID", lTestID);
        testItem.put("strTestKPID", strTestKPID);
        testItem.put("questions", jsonArray);

        JSONArray test = new JSONArray();
        test.add(testItem);

        JSONObject tests = new JSONObject();
        tests.put("test",test);
        RequestBody formBody = new FormEncodingBuilder()
                .add("iUserID", String.valueOf(iUserID))
                .add("strQuestions", tests.toJSONString())
                .build();
        Request request = new Request.Builder()
                .url(Util.URL_UPLOAD_DONE_QUESTION)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful())throw new IOException("Unexcepted cod "+ response);
        JSONObject jsonResult = JSONObject.parseObject(response.body().string());
        return jsonResult.getBooleanValue("isSuccess");
    }

    public ArrayList<DoneQuestion> getDoneQuestions(int iUserID, int iCourseID, int iQuestionType) throws IOException {
        ArrayList<DoneQuestion> result = new ArrayList<>();

        RequestBody formBody = new FormEncodingBuilder()
                .add("iUserID", String.valueOf(iUserID))
                .add("iCourseID", String.valueOf(iCourseID))
                .add("iQuestionType", String.valueOf(iQuestionType))
                .build();
        Request request = new Request.Builder()
                .url(Util.URL_GET_KPs)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful())throw new IOException("Unexcepted cod "+ response);
        JSONObject jsonResult = JSONObject.parseObject(response.body().string());
        JSONArray jsonArray = jsonResult.getJSONArray("questions");
        Gson gson = new Gson();
        for(int i = 0; i< jsonArray.size(); i++){
            result.add(gson.fromJson(jsonArray.getJSONObject(i).toJSONString(), DoneQuestion.class));
        }
        return result;
    }

    public Bitmap getImageFromServer(String data) {
        return null;
    }


    public AssessmentScore getAssessedScore(int iUserID, int iCourseID) {
        RequestBody formBody = new FormEncodingBuilder()
                .add("iUserID", String.valueOf(iUserID))
                .add("iCourseID", String.valueOf(iCourseID))
                .build();
        Request request = new Request.Builder()
                .url(Util.URL_GET_ASSESSED_SCORE)
                .post(formBody)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if(!response.isSuccessful())throw new IOException("Unexcepted cod "+ response);
            AssessmentScore assessmentScore = new Gson().fromJson(
                    response.body().string(),
                    AssessmentScore.class
            );
            return assessmentScore;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
