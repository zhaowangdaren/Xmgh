package com.example.ustc_pc.myapplication.unit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.example.ustc_pc.myapplication.net.Util;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ustc-pc on 2015/2/10.
 */
public class FileOperation {
    public static final String ENCODING = "UTF-8";
    public static String APP_PATH = Environment.getExternalStorageDirectory()+"/.cn.edu.ustc.education/";
    public static void setAppPath(Context context){
       APP_PATH = context.getFilesDir() +"/.cn.edu.ustc.education/";

    }
    /**
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getFromAssets(Context context, String fileName){
        String result = "";
        try{
            InputStream inputStream = context.getResources().getAssets().open(fileName);
            //获取文件的字节数
            int lenght = inputStream.available();
            //创建byte数组
            byte[] buffer = new byte[lenght];
            //将文件中的数据读到byte数组中
            inputStream.read(buffer);
            result = EncodingUtils.getString(buffer, ENCODING);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param imageFilePath
     * @return Bitmap
     */
    public static Bitmap getImageFromSD(String imageFilePath){
        Bitmap result= null;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(imageFilePath));
            result = BitmapFactory.decodeStream(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

//    /**
//     *
//     * @param filePath
//     * @return
//     */
//    public static String getFileFromSD(String filePath){
//        String result  = null;
//        try{
//            InputStream inputStream = new FileInputStream(new File(filePath));
//            //获取文件的字节数
//            int lenght = inputStream.available();
//            //创建byte数组
//            byte[] buffer = new byte[lenght];
//            //将文件中的数据读到byte数组中
//            inputStream.read(buffer);
//            result = EncodingUtils.getString(buffer, ENCODING);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return result;
//    }

//    public static String getFileFromSD2(String filePath){
//        StringBuilder sb = new StringBuilder();
//        try {
//            File file = new File(filePath);
//            BufferedReader bf = new BufferedReader(new FileReader(file));
//            String content = "";
//            while(true){
//                content = bf.readLine();
//                if(content == null)break;
//                sb.append(content.trim());
//            }
//            bf.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return sb.toString();
//    }

    public static boolean createFile(String filePath, String fileName){
        File file1 = new File(filePath);
        if(!file1.exists()){
            try{
                file1.mkdir();
                String temp = filePath + "/"+fileName;
                File file2 = new File(temp);
                if(!file2.exists()){
                    file2.createNewFile();
                }
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    /**
     *
     * @param filePath
     * @param content
     * @return
     */
    public static boolean write2File(String filePath, String content, boolean append){
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        try {
            //创建FileWriter对象，用来写入字符流
            fileWriter = new FileWriter(filePath, append);
            //缓冲对文件的输出
            bufferedWriter = new BufferedWriter(fileWriter);
            //写入文件
            bufferedWriter.write(content);
            // 写入一个行分隔符
            bufferedWriter.newLine();
            //刷新改流的缓冲
            bufferedWriter.flush();
            bufferedWriter.close();
            fileWriter.close();

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

//    public static ArrayList<Course> getCourseFromFile(){
//        String strJSON = FileOperation.getFileFromSD(FileOperation.APP_PATH + "courses.json");
//        ArrayList<Course> courses = null;
//        try {
//            JSONObject jsonObjectSource = new JSONObject(strJSON);
//            JSONArray coursesJson = jsonObjectSource.getJSONArray("courses");
//            int size = coursesJson.length();
//            if (size > 0) {
//                courses = new ArrayList<>();
//                for (int i = 0; i < size; i++) {
//                    JSONObject jsonObject = coursesJson.getJSONObject(i);
//                    int id = jsonObject.getInt("ID");
//                    String name = jsonObject.getString("name");
//                    JSONArray knowledgePointsJSON = jsonObject.getJSONArray("knowledgePoints");
//                    int kpSizes = knowledgePointsJSON.length();
//                    ArrayList<KnowledgePoint> knowledgePoints = new ArrayList<>();
//                    for (int j = 0; j < kpSizes; j++) {
//                        JSONObject jsonObjectKP = knowledgePointsJSON.getJSONObject(j);
//                        String kp_id = jsonObjectKP.getString("ID");
//                        String kp_name = jsonObjectKP.getString("name");
//                        int level = jsonObjectKP.getInt("level");
//                        String fatherKP_id = jsonObjectKP.getString("fatherKP_id");
//                        boolean hasChild = jsonObjectKP.getBoolean("hasChild");
//                        boolean isExpand = jsonObjectKP.getBoolean("isExpand");
//                        KnowledgePoint knowledgePoint = new KnowledgePoint(kp_id, kp_name, level, fatherKP_id, hasChild, isExpand);
//                        knowledgePoints.add(knowledgePoint);
//                    }
//
//                    Course course = new Course(id, name, knowledgePoints);
//                    courses.add(course);
//                }
//            }
//        }catch(JSONException e){
//            e.printStackTrace();
//        }
//
//        //sort
//        for(int i=0; i< courses.size(); i++){
//            for(int j=0; j<courses.size(); j++){
//                if(j == (courses.size() - 1))break;
//                int id1 = courses.get(j)._id;
//                int id2 = courses.get(j + 1)._id;
//                if(id1 > id2) Collections.swap(courses, j, j+1);
//            }
//        }
//        return courses;
//    }


//    /**
//     * 获取对应课程内容
//     * @param index 0：英语一；1：英语二；2：政治
//     * @return
//     */
//    public static Course getCourse(int index){
//        String strJSON = FileOperation.getFileFromSD2(FileOperation.APP_PATH + "courses.json");
//        Course course = null;
//        try {
//            JSONObject jsonObjectSource = new JSONObject(strJSON);
//            JSONArray coursesJson = jsonObjectSource.getJSONArray("courses");
//            int size = coursesJson.length();
//
//            if (size >= index) {
//                JSONObject jsonObject = coursesJson.getJSONObject(index);
//                int id = jsonObject.getInt("ID");
//                String name = jsonObject.getString("name");
//                JSONArray knowledgePointsJSON = jsonObject.getJSONArray("knowledgePoints");
//                int kpSizes = knowledgePointsJSON.length();
//                ArrayList<KnowledgePoint> knowledgePoints = new ArrayList<>();
//                for (int j = 0; j < kpSizes; j++) {
//                    JSONObject jsonObjectKP = knowledgePointsJSON.getJSONObject(j);
//                    String kp_id = jsonObjectKP.getString("ID");
//                    String kp_name = jsonObjectKP.getString("name");
//                    int level = jsonObjectKP.getInt("level");
//                    String fatherKP_id = jsonObjectKP.getString("fatherKP_id");
//                    boolean hasChild = jsonObjectKP.getBoolean("hasChild");
//                    boolean isExpand = jsonObjectKP.getBoolean("isExpand");
//                    KnowledgePoint knowledgePoint = new KnowledgePoint(kp_id, kp_name, level, fatherKP_id, hasChild, isExpand);
//                    knowledgePoints.add(knowledgePoint);
//                }
//                course = new Course(id, name, knowledgePoints);
//            }
//        }catch (JSONException e){
//            e.printStackTrace();
//        }
//        return course;
//    }

//    public static boolean setPersonalInfo(Context context, String infoJSON){
//        UserSharedPreference userSharedPreference = new UserSharedPreference(context);
//        try {
//            JSONObject jsonObject = new JSONObject(infoJSON);
//            userSharedPreference.setAccountNumber(jsonObject.getString("account_number"));
//            userSharedPreference.setNickName(jsonObject.getString("nick_name"));
//            userSharedPreference.setGender(jsonObject.getInt("gender"));
//            userSharedPreference.setEmail(jsonObject.getString("user_email"));
//            userSharedPreference.setAboutMe(jsonObject.getString("about_me"));
//            userSharedPreference.setUserType(jsonObject.getInt("user_type"));
//            userSharedPreference.setSourceCollege(jsonObject.getString("source_college"));
//            userSharedPreference.setSourceMajor(jsonObject.getString("source_major"));
//            userSharedPreference.setFirstTC(jsonObject.getString("first_target_college"));
//            userSharedPreference.setFirstTM(jsonObject.getString("first_target_major"));
//            userSharedPreference.setSecondTC(jsonObject.getString("second_target_college"));
//            userSharedPreference.setSecondTM(jsonObject.getString("second_target_major"));
//            userSharedPreference.setAcceptedC(jsonObject.getString("accepted_college"));
//            userSharedPreference.setAcceptedM(jsonObject.getString("accepted_major"));
//            return true;
//        }catch (JSONException e){
//            e.printStackTrace();
//        }
//        return false;
//    }
//    public static Course getCourse(String strJSON, int index){
//        Course course = null;
//        try {
//            JSONObject jsonObjectSource = new JSONObject(strJSON);
//            JSONArray coursesJson = jsonObjectSource.getJSONArray("courses");
//            int size = coursesJson.length();
//
//            if (size >= index) {
//                JSONObject jsonObject = coursesJson.getJSONObject(index);
//                int id = jsonObject.getInt("ID");
//                String name = jsonObject.getString("name");
//                JSONArray knowledgePointsJSON = jsonObject.getJSONArray("knowledgePoints");
//                int kpSizes = knowledgePointsJSON.length();
//                ArrayList<KnowledgePoint> knowledgePoints = new ArrayList<>();
//                for (int j = 0; j < kpSizes; j++) {
//                    JSONObject jsonObjectKP = knowledgePointsJSON.getJSONObject(j);
//                    String kp_id = jsonObjectKP.getString("ID");
//                    String kp_name = jsonObjectKP.getString("name");
//                    int level = jsonObjectKP.getInt("level");
//                    String fatherKP_id = jsonObjectKP.getString("fatherKP_id");
//                    boolean hasChild = jsonObjectKP.getBoolean("hasChild");
//                    boolean isExpand = jsonObjectKP.getBoolean("isExpand");
//                    KnowledgePoint knowledgePoint = new KnowledgePoint(kp_id, kp_name, level, fatherKP_id, hasChild, isExpand);
//                    knowledgePoints.add(knowledgePoint);
//                }
//                course = new Course(id, name, knowledgePoints);
//            }
//        }catch (JSONException e){
//            e.printStackTrace();
//        }
//        return course;
//    }

    public static Paper getPaper(String filePath){
        Paper paper = null;
        String strJSON = Util.getFileFromSD(filePath);
        if(strJSON != null && strJSON.length() > 0){
            try {
                JSONObject jsonObjectSource = new JSONObject(strJSON);
                String id = jsonObjectSource.getString("ID");
                String name = jsonObjectSource.getString("name");

                String firstKP = jsonObjectSource.getString("firstKP");
                String secondKP = jsonObjectSource.getString("secondKP");
                String thirdKP = jsonObjectSource.getString("thirdKP");
                String questionKP = "";
                if(thirdKP.length() > 0)questionKP = thirdKP;
                else if(secondKP.length() > 0)questionKP = secondKP;
                else questionKP = firstKP;

                JSONArray questionsJSON = jsonObjectSource.getJSONArray("questions");
                ArrayList<Question> questionArrayList = new ArrayList<>();
                for(int i =0; i< questionsJSON.length(); i++){//question
                    JSONObject jsonObjectQ = questionsJSON.getJSONObject(i);
                    Question question = getQuestion(jsonObjectQ, questionKP);
                    questionArrayList.add(question);
                }
                int sumQuestion = questionArrayList.size();
                paper = new Paper(id, name, sumQuestion, firstKP, secondKP, thirdKP, questionArrayList);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return paper;
    }

    public static Paper getPaperFromJSONStr(String jsonStr){
        Paper paper = null;
        if(jsonStr != null && jsonStr.length() > 0){
            try {
                JSONObject jsonObjectSource = new JSONObject(jsonStr);
                String id = jsonObjectSource.getString("ID");
                String name = jsonObjectSource.getString("name");

                String firstKP = "";
                try {
                    firstKP=jsonObjectSource.getString("firstKp");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                String secondKP = "";
                try {
                     secondKP= jsonObjectSource.getString("secondKp");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                String thirdKP = "";
                try {
                     thirdKP= jsonObjectSource.getString("thirdKp");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                String questionKP = "";
                if(thirdKP.length() > 0)questionKP = thirdKP;
                else if(secondKP.length() > 0)questionKP = secondKP;
                else questionKP = firstKP;

                JSONArray questionsJSON = jsonObjectSource.getJSONArray("questions");
                ArrayList<Question> questionArrayList = new ArrayList<>();
                for(int i =0; i< questionsJSON.length(); i++){//question
                    JSONObject jsonObjectQ = questionsJSON.getJSONObject(i);
                    Question question = getQuestion(jsonObjectQ, questionKP);
                    questionArrayList.add(question);
                }
                int sumQuestion = questionArrayList.size();
                paper = new Paper(id, name, sumQuestion, firstKP, secondKP, thirdKP, questionArrayList);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return paper;
    }

    public static ArrayList<Paper> getKPPapersFromFile(String fileName){
        ArrayList<Paper> result = null;
        String filePath = APP_PATH + fileName;
        String strJSON = Util.getFileFromSD(filePath);
        if(strJSON != null && strJSON.length() > 0) {
            result = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(strJSON);
                JSONArray jsonArray = jsonObject.getJSONArray("papers");
                for(int i=0; i< jsonArray.length(); i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    Paper kpPapers = new Paper(jsonObject1.getString("ID"), jsonObject1.getString("name"), jsonObject1.getInt("numOfQuestion"), "", "","","");
                    result.add(kpPapers);
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return result;
    }
    public static Question getQuestionByID(String questionID) {
        String filePath = APP_PATH + questionID + ".json";

        return getQuestion(filePath);
    }

    public static Question getQuestion(String filePath){
        String strJSON = Util.getFileFromSD(filePath);
        Question question = null;
        if(strJSON != null && strJSON.length() > 0){
            try {
                JSONObject jsonObjectSource = new JSONObject(strJSON);
                question = getQuestion(jsonObjectSource);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return question;
    }

    private static Question getQuestion(JSONObject jsonObjectQ){
        Question question = null;
        try {
            String q_id = jsonObjectQ.getString("ID");
            int type = jsonObjectQ.getInt("type");
            String subject = jsonObjectQ.getString("subject");
            String subjectSon = jsonObjectQ.getString("subjectSon");
            JSONArray optionsJSON = jsonObjectQ.getJSONArray("options");
            ArrayList<String> options = new ArrayList<>();
            for (int j = 0; j < optionsJSON.length(); j++) {
                JSONObject jsonObjectOption = optionsJSON.getJSONObject(j);
                int option_id = jsonObjectOption.getInt("ID");
                String option = jsonObjectOption.getString("option");
                options.add(option_id, option);
            }
            JSONArray jsonArrayAnswers = jsonObjectQ.getJSONArray("answers");
            HashMap<Integer, Boolean> hashMapAnswers = new HashMap<>();
            int iSingleAnswer = 0;
            for (int j = 0; j < jsonArrayAnswers.length(); j++) {
                int answer = jsonArrayAnswers.getJSONObject(j).getInt("ID");
                hashMapAnswers.put(answer, true);
                if (type == 0) {
                    iSingleAnswer = answer;
                }
            }
            String strAnswer = jsonObjectQ.getString("strAnswer");
            String strUserSelected = jsonObjectQ.getString("userSelected");
            String detail = jsonObjectQ.getString("detail");
            String detailSon = jsonObjectQ.getString("detailSon");
           question = new Question(q_id, type, subject, subjectSon, options, hashMapAnswers, detail, detailSon);
            question._iSingleAnswer = iSingleAnswer;
            question._strAnswer = strAnswer;
            question._strUserSelected = strUserSelected;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return question;
    }

    private static Question getQuestion(JSONObject jsonObjectQ, String kp){
        Question question = null;
        try {
            String q_id = jsonObjectQ.getString("ID");
            int type = jsonObjectQ.getInt("type");
            String subject = "";
            try {
                subject = jsonObjectQ.getString("subject");
            }catch (JSONException e){
                e.printStackTrace();
            }

            String subjectSon = "";
            try {
               subjectSon = jsonObjectQ.getString("subjectSon");
            }catch (JSONException e){
                e.printStackTrace();
            }

            JSONArray optionsJSON = jsonObjectQ.getJSONArray("options");
            ArrayList<String> options = new ArrayList<>();
            for (int j = 0; j < optionsJSON.length(); j++) {
                JSONObject jsonObjectOption = optionsJSON.getJSONObject(j);
                int option_id = jsonObjectOption.getInt("ID");
                String option = jsonObjectOption.getString("option");
                options.add(option_id, option);
            }
            JSONArray jsonArrayAnswers = jsonObjectQ.getJSONArray("answers");
            HashMap<Integer, Boolean> hashMapAnswers = new HashMap<>();
            int iSingleAnswer = 0;
            for (int j = 0; j < jsonArrayAnswers.length(); j++) {
                int answer = jsonArrayAnswers.getJSONObject(j).getInt("ID");
                hashMapAnswers.put(answer, true);
                if (type == 0) {
                    iSingleAnswer = answer;
                }
            }
            String strAnswer = jsonObjectQ.getString("strAnswer");
            //String strUserSelected = jsonObjectQ.getString("userSelected");
            String detail = "";
            try {
                detail = jsonObjectQ.getString("detail");
            }catch (JSONException e){
                e.printStackTrace();
            }

            String detailSon = "";
            try {
                detailSon = jsonObjectQ.getString("detailSon");
            }catch (JSONException e){
                e.printStackTrace();
            }

            question = new Question(q_id, type, subject, subjectSon, options, hashMapAnswers, detail, detailSon);
            question._iSingleAnswer = iSingleAnswer;
            question._strAnswer = strAnswer;
            //question._strUserSelected = strUserSelected;
            question._strThirdKP = kp;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return question;
    }

    public static ArrayList<Question> parseUserQuestionInfo(JSONObject questionsJO){
        ArrayList<Question> questions = null;
        try {
        JSONArray jsonArray = questionsJO.getJSONArray("questions");
        int size = jsonArray.length();
            if(size > 0)questions = new ArrayList<>();
            for(int i =0; i< size; i++){
                JSONObject jsonObjectQ = jsonArray.getJSONObject(i);
                String questionId = jsonObjectQ.getString("ID");
                int courseId = jsonObjectQ.getInt("courseId");
                String paperName = "";
                try {
                  paperName =jsonObjectQ.getString("paperName");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                String firstKP = "";
                try {
                    firstKP = jsonObjectQ.getString("firstKp");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                String secondKP = "";
                try {
                   secondKP = jsonObjectQ.getString("secondKp");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                String thirdKP = "";
                try {
                   thirdKP = jsonObjectQ.getString("thirdKp");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                boolean isFavorite = jsonObjectQ.getBoolean("isFavorite");
                boolean isCorrect = jsonObjectQ.getBoolean("isCorrect");

                String subject = "";
                try {
                    subject = jsonObjectQ.getString("subject");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                String subjectSon = "";
                try {
                    subjectSon = jsonObjectQ.getString("subjectSon");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                JSONArray optionsJSON = jsonObjectQ.getJSONArray("options");
                ArrayList<String> options = new ArrayList<>();
                for (int j = 0; j < optionsJSON.length(); j++) {
                    JSONObject jsonObjectOption = optionsJSON.getJSONObject(j);
                    int option_id = jsonObjectOption.getInt("ID");
                    String option = jsonObjectOption.getString("option");
                    options.add(option_id, option);
                }

                String strAnswer = "";
                try {
                    strAnswer = jsonObjectQ.getString("strAnswer");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                String strUserSelected = "";
                try {
                    strUserSelected = jsonObjectQ.getString("userSelected");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                String detail = "";
                try {
                    detail = jsonObjectQ.getString("detail");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                String detailSon = "";
                try {
                    detailSon = jsonObjectQ.getString("detailSon");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                String note = "";
                try {
                    note = jsonObjectQ.getString("note");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                long spendTime = 0;
                try {
                    spendTime = jsonObjectQ.getLong("spendTime");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                int errorTimes = 0;
                try {
                    errorTimes = jsonObjectQ.getInt("errorTimes");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                Question question = new Question(questionId, courseId, paperName,firstKP, secondKP, thirdKP,
                        isFavorite, isCorrect, subject, subjectSon, options, strAnswer, strUserSelected,
                        detail,detailSon,note, spendTime, errorTimes);
                questions.add(question);
            }

        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return questions;
    }
    public static ArrayList<Question> getFavoriteQuestions(){
        ArrayList<Question> result  = null;
        String strJSON = Util.getFileFromSD(FileOperation.APP_PATH + "favorite_questions.json");
        try {
            result = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(strJSON);
            JSONArray jsonArray = jsonObject.getJSONArray("favorite_questions");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObjectQ = jsonArray.getJSONObject(i);
                Question question = getQuestion(jsonObjectQ);
                result.add(question);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return result;
    }

    public static void writeQuestion2SD(Question question, int type){
        String filePath = APP_PATH ;
        String fileName ;
        fileName = question._id + ".json";
        File file  = new File(filePath +fileName);
        if( !file.exists() ) {
            createFile(filePath, fileName);
        }
            JSONObject jsonObjectQ = new JSONObject();
            try {
                jsonObjectQ.put("ID", question._id);
                jsonObjectQ.put("type", question._iType);
                jsonObjectQ.put("subject", question._strSubject);
                jsonObjectQ.put("subjectSon", question._strSubjectSon);
                JSONArray jsonArrayOptions = new JSONArray();
                for (int i = 0; i < question._options.size(); i++) {
                    JSONObject jsonObjectOption = new JSONObject();
                    jsonObjectOption.put("ID", i);
                    jsonObjectOption.put("option", question._options.get(i));
                    jsonArrayOptions.put(jsonObjectOption);
                }
                jsonObjectQ.put("options", jsonArrayOptions);

                JSONArray jsonArrayAnswers = new JSONArray();
                if(question._answerMap != null) {

                    for (int i = 0; i < question._options.size(); i++) {
                        if (question._answerMap.get(i) != null && question._answerMap.get(i)) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("ID", i);
                            jsonArrayAnswers.put(jsonObject);
                        }
                    }
                    jsonObjectQ.put("answers", jsonArrayAnswers);
                }else{
                    jsonObjectQ.put("answers", jsonArrayAnswers);
                }

                jsonObjectQ.put("strAnswer", question._strAnswer);
                jsonObjectQ.put("detail", question._analysis);
                jsonObjectQ.put("detailSon", question._analysisSon);
                jsonObjectQ.put("userSelected",question._strUserSelected);
                jsonObjectQ.put("note", question._strNote);
                FileOperation.write2File(filePath + fileName, jsonObjectQ.toString(), false);

            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public static void writePaperReport2SD(Paper paper){
        String filePath = APP_PATH;
        String fileName = "paper_"+paper._id+"_report.json";
        File file = new File(filePath + fileName);
        if( !file.exists()){
            createFile(filePath, fileName);
        }
        String strPaperReport = parsePaperReport2Str(paper);
        write2File(APP_PATH + fileName, strPaperReport,false);
    }

    public static String getPaperReportStrFromSD(String paperID) {
        String fileName = "paper_"+paperID+"_report.json";
        String strJSON = Util.getFileFromSD(APP_PATH + fileName);
        return strJSON;
    }

    private static Paper parseStr2PaperReport(String strJson){
        Paper paper = null;
        if(strJson != null && strJson.length() > 0) {
            paper = new Paper();
            try {
                JSONObject jsonObject = new JSONObject(strJson);
                String id = jsonObject.getString("ID");
                long lStartTime = jsonObject.getLong("start_time");
                long lEndTime = jsonObject.getLong("end_time");
                int iType = jsonObject.getInt("type");
                ArrayList<Question> questions = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("questions");
                for(int i = 0; i<jsonArray.length(); i++){
                    JSONObject questionJSON = jsonArray.getJSONObject(i);
                    String questionID = questionJSON.getString("ID");
                    boolean isFavorite = questionJSON.getBoolean("isFavorite");
                    boolean isCorrect = questionJSON.getBoolean("isCorrect");
                    String note = questionJSON.getString("note");
                    String strUserSelected = questionJSON.getString("user_selected");
                    long lSpendTime = questionJSON.getLong("spend_time");
                    Question question = new Question();
                    question._id = questionID;
                    question._isFavorite = isFavorite;
                    question._isRight = isCorrect;
                    question._strNote = note;
                    question._strUserSelected = strUserSelected;
                    question._lSpendTime = lSpendTime;
                    questions.add(question);
                }

                paper._id = id;
                paper._lStartTime = lStartTime;
                paper._lEndTime = lEndTime;
                paper._iType = iType;
                paper._questions = questions;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return paper;
    }
    public static String parsePaperReport2Str(Paper paper){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID", paper._id);
            jsonObject.put("start_time", paper._lStartTime);
            jsonObject.put("end_time", paper._lEndTime);
            jsonObject.put("type", paper._iType);
            JSONArray jsonArrayQuestions = new JSONArray();
            for (int i = 0; i < paper._questions.size(); i++) {
                JSONObject question = new JSONObject();
                question.put("ID", paper._questions.get(i)._id);
                question.put("isFavorite", paper._questions.get(i)._isFavorite);
                question.put("isCorrect", paper._questions.get(i)._isRight);
                question.put("note", paper._questions.get(i)._strNote);
                question.put("user_selected", paper._questions.get(i)._strUserSelected);
                question.put("spend_time", paper._questions.get(i)._lSpendTime);

                jsonArrayQuestions.put(question);
            }
            jsonObject.put("questions", jsonArrayQuestions);
            return jsonObject.toString();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Individuation> getIndividuations(String fileName) {
        ArrayList<Individuation> result = null;
        String filePath = APP_PATH + fileName;
        String strJSON = Util.getFileFromSD(filePath);
        if(strJSON != null && strJSON.length() > 0){
            result = new ArrayList<>();
            try{
                JSONObject jsonObject = new JSONObject(strJSON);
                JSONArray jsonArray = jsonObject.getJSONArray("individuations");
                for(int i =0; i < jsonArray.length(); i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    int type = jsonObject1.getInt("type");
                    Individuation individuation = new Individuation(jsonObject1.getString("ID"),
                            jsonObject1.getString("name"), type);
                    individuation._imageUrl = jsonObject1.getString("imageUrl");
                    if(type == 0){
                        individuation._numOfQuestion = jsonObject1.getInt("numOfQuestion");
                    }
                    result.add(individuation);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return result;
    }

    public static AssessmentScore getAssessmentScoreFromJSONStr(String resultJSON) {
        try {
            JSONObject jsonObject = new JSONObject(resultJSON);
            int sumScore = jsonObject.getInt("sum_assessment_score");
            int firstKpNum = jsonObject.getInt("first_kp_num");
            AssessmentScore assessmentScore  = new AssessmentScore(sumScore, firstKpNum);
            JSONArray jsonArray = jsonObject.getJSONArray("done_kps");
            int size = jsonArray.length();
            for(int i=0; i< size; i++){
                String kpId = jsonArray.getJSONObject(i).getString("kp_id");
                String kpName = jsonArray.getJSONObject(i).getString("kp_name");
                float score = (float)jsonArray.getJSONObject(i).getDouble("assessment_score");
                assessmentScore.addDoneKpSocre(kpId, kpName, score);
            }
            return assessmentScore;

        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static int[] randomCommon(int min, int max, int n){
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while(count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if(num == result[j]){
                    flag = false;
                    break;
                }
            }
            if(flag){
                result[count] = num;
                count++;
            }
        }
        return result;
    }

    public static ArrayList<Paper> parsePapers(JSONObject jsonObject) {
        ArrayList<Paper> result = null;
        if(jsonObject != null ) {
            result = new ArrayList<>();
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("papers");
                for(int i=0; i< jsonArray.length(); i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    Paper kpPapers = new Paper(jsonObject1.getString("ID"), jsonObject1.getString("name"), jsonObject1.getInt("numOfQuestion"),"","","","");
                    result.add(kpPapers);
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return result;
    }

    public static AssessmentScore parseAssessmentScore(JSONObject jsonObject) {
        AssessmentScore result = null;
        try {
            int courseID = jsonObject.getInt("courseID");
            int sumScore = jsonObject.getInt("sumScore");
            int firstKPNum = jsonObject.getInt("firstKpNum");

            result = new AssessmentScore(courseID, sumScore, firstKPNum);
            JSONArray jsonArray = jsonObject.getJSONArray("doneKPs");
            int size = jsonArray.length();
            for(int i=0; i<size; i++){
                String kpID = jsonArray.getJSONObject(i).getString("kpID");
                String kpName = jsonArray.getJSONObject(i).getString("kpName");
                double score = jsonArray.getJSONObject(i).getDouble("score");
                result.addDoneKpSocre(kpID, kpName, score);
            }
            String generalComment = jsonObject.getString("generalComment");
            result._strComment = generalComment;
            return result;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<Paper> parseFinishedPapers(JSONObject jsonObject) {
        ArrayList<Paper> result = null;
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("papers");
            result = new ArrayList<>();
            int size = jsonArray.length();
            for(int i=0; i<size; i++){
                JSONObject paper = jsonArray.getJSONObject(i);
                String paperID = paper.getString("ID");
                long startTime = paper.getLong("start_time");
                long endTime = paper.getLong("end_time");
                long spendTime = endTime - startTime;
                Long minius = (spendTime/1000)/60;
                //計算目前已過秒數
                Long seconds = (spendTime/1000) % 60;
                String strSpendTime = minius + ":" + seconds;
                int paperType = paper.getInt("type");
                JSONArray questionJSONArray = paper.getJSONArray("questions");
                ArrayList<Question> questions = new ArrayList<>();
                int qSize = questionJSONArray.length();
                for(int j = 0; j<qSize; j++){
                    JSONObject questionJSON = questionJSONArray.getJSONObject(j);
                    String questionID = questionJSON.getString("ID");
                    boolean isFavorite = questionJSON.getBoolean("isFavorite");
                    boolean isCorrect = questionJSON.getBoolean("isCorrect");
                    String note = questionJSON.getString("note");
                    String userSelected = questionJSON.getString("user_selected");
                    long qpendTime = questionJSON.getLong("spend_time");
                    Question question = new Question();
                    questions.add(question);
                }
                Paper paper1 = new Paper();
                paper1._id = paperID;
                paper1._lStartTime = startTime;
                paper1._lEndTime = endTime;
                paper1._strSpendTime = strSpendTime;
                paper1._iType = paperType;
                paper1._questions = questions;
                result.add(paper1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


}
