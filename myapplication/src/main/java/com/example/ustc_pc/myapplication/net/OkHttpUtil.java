package com.example.ustc_pc.myapplication.net;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.ustc_pc.myapplication.dao.Course;
import com.example.ustc_pc.myapplication.dao.DoneQuestion;
import com.example.ustc_pc.myapplication.dao.KPs;
import com.example.ustc_pc.myapplication.dao.User;
import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
                .add("strPhoneNumber",phoneNumber)
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
                .add("strPhoneNumber",phoneNumber)
                .add("strPassword",strPassword)
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
                .add("iAccountType",String.valueOf(iAccountType))
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

    /**
     *
     * @param iAccountType
     * @param iUserID
     * @param strThirdID
     * @param strPassword
     * @param userInfo
     * @return
     * @throws IOException
     */
    public boolean setPersonInfo(int iAccountType, int iUserID, String strThirdID, String strPassword, User userInfo) throws IOException {

        RequestBody formBody = new FormEncodingBuilder()
                .add("iAccountType",String.valueOf(iAccountType))
                .add("iUserID", String.valueOf(iUserID))
                .add("strThirdID", strThirdID)
                .add("strPassword", strPassword)
                .add("strPersonalInfo", new Gson().toJson(userInfo))
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

    /**
     *
     * @param iAccountType
     * @param iUserID
     * @param strPassword
     * @param strThirdID
     * @return
     * @throws IOException
     */
    public User getPersonalInfo(int iAccountType, int iUserID, String strPassword, String strThirdID) throws IOException {
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
        if(!response.isSuccessful())throw new IOException("Unexcepted cod "+response);
        JSONObject result = JSONObject.parseObject(response.body().string());
        User user = new Gson().fromJson(response.body().toString(), User.class);
        return user;
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
    public boolean addCourse(int iUserID, ArrayList<Integer> courseIDs) throws IOException {
        if(courseIDs.size() <= 0)return true;
        StringBuffer stringBuffer = new StringBuffer();
        for(int i: courseIDs){
            stringBuffer.append(String.valueOf(i)+",");
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
     * @param courseIDs
     * @return
     * @throws IOException
     */
    public boolean deleteCourse(int iUserID, ArrayList<Integer> courseIDs) throws IOException {
        if(courseIDs.size() <= 0)return true;
        StringBuffer stringBuffer = new StringBuffer();
        for(int i: courseIDs){
            stringBuffer.append(String.valueOf(i)+",");
        }
        if(stringBuffer.charAt(stringBuffer.length() - 1)==',')//delete the last ','
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        RequestBody formBody = new FormEncodingBuilder()
                .add("iUserID", String.valueOf(iUserID))
                .add("strDeletedCoursesID", stringBuffer.toString())
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

    public ArrayList<KPs> getKPs(int iUserID, int iCourseID) throws IOException {
        ArrayList<KPs> kPses = new ArrayList<>();
        RequestBody formBody = new FormEncodingBuilder()
                .add("iUserID", String.valueOf(iUserID))
                .add("iCoueseID", String.valueOf(iCourseID))
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
                .url(Util.URL_GET_KPs)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful())throw new IOException("Unexcepted cod "+ response);
        JSONObject jsonObject = JSONObject.parseObject(response.body().string());
        return new URL(jsonObject.getString("strDonUrl"));
    }

    /**
     * Upload done questions
     * @param iUserID
     * @param doneQuestions
     * @return
     */
    public Boolean uploadDoneQuestions(int iUserID, ArrayList<DoneQuestion> doneQuestions) throws IOException {
        if(doneQuestions.size() == 0)return true;
        JSONArray jsonArray = new JSONArray();

        Gson gson = new Gson();
        for(DoneQuestion doneQuestion : doneQuestions){
            jsonArray.add(gson.toJson(doneQuestion));
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("questions", jsonArray.toJSONString());


        RequestBody formBody = new FormEncodingBuilder()
                .add("iUserID", String.valueOf(iUserID))
                .add("strQuestions", jsonObject.toJSONString())
                .build();
        Request request = new Request.Builder()
                .url(Util.URL_GET_KPs)
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
}
