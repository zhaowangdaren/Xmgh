package com.example.ustc_pc.myapplication.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.adapter.ActivityPaperReportGVAdapter;
import com.example.ustc_pc.myapplication.db.DBHelper;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.net.NetUtil;
import com.example.ustc_pc.myapplication.unit.FileOperation;
import com.example.ustc_pc.myapplication.unit.Paper;
import com.example.ustc_pc.myapplication.unit.Question;
import com.example.ustc_pc.myapplication.unit.Strings;

import java.util.ArrayList;

public class ActivityPaperReport extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener{

    TextView mPaperNameTV, mSpendTimeTV, mScoreTV, mQuestionSumTV, mPaperDetialTV;
    GridView mAnswerSheetGV;
    Button mCheckAnalysisBT;
    Paper _paper;

    int _iScore = 0;//用户得分
    String _strSpendTime = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_report);

        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initData();

        savePaperInfo();
    }

    private void savePaperInfo(){
//        DataIntentService.startActionSavePaperInfo(this, _paper);
        SavePaperInfoAsyncTask savePaperInfoAsyncTask = new SavePaperInfoAsyncTask();
        savePaperInfoAsyncTask.execute(_paper);
    }


    private void initView() {
        mPaperNameTV = (TextView) findViewById(R.id.textView_paper_report_title);
        mSpendTimeTV = (TextView) findViewById(R.id.textView_paper_spend_time);
        mScoreTV = (TextView) findViewById(R.id.textView_score);
        mQuestionSumTV = (TextView) findViewById(R.id.textView_sum_questions);
        mPaperDetialTV = (TextView) findViewById(R.id.textView_paper_details);
        mPaperDetialTV.setVisibility(View.GONE);
        mAnswerSheetGV = (GridView) findViewById(R.id.gridView_answer_sheets);
        mCheckAnalysisBT = (Button) findViewById(R.id.button_check_analysis);
        mCheckAnalysisBT.setOnClickListener(this);

    }

    private void initData(){
        Intent intent = getIntent();
        _paper = (Paper) intent.getSerializableExtra("PAPER");
        if(_paper == null){
            Log.e("Error:","ActivityPaperReport can not get Paper from ActivityPaper");
        }else{
            mPaperNameTV.setText(_paper._strName);
            Long minius = (_paper._lSpendTime/1000)/60;
            //計算目前已過秒數
            Long seconds = (_paper._lSpendTime/1000) % 60;
            _strSpendTime = minius + ":" + seconds;
            mSpendTimeTV.setText(_strSpendTime);
            _paper._strSpendTime = _strSpendTime;
        }

        checkout();

        ActivityPaperReportGVAdapter activityPeperReportGVAdapter = new ActivityPaperReportGVAdapter(_paper._questions);
        mAnswerSheetGV.setAdapter(activityPeperReportGVAdapter);
        mAnswerSheetGV.setOnItemClickListener(this);
    }

    /**
     * 判断用户做题得分
     */
    private void checkout(){
        ArrayList<Question> questions = _paper._questions;
        int sumQuestion = questions.size();
        mQuestionSumTV.setText(""+sumQuestion);
        for(int i =0 ;i< sumQuestion; i++){
            Question question = questions.get(i);
            switch(question._iType){
                case 0://单选
                    checkoutSingleQuestion(question, i);
                    break;
                case 1://多选
                    checkoutMultipleQuestion(question, i);
                    break;
                case 2://问答
                    break;
            }
        }

        mScoreTV.setText(""+_iScore);
        _paper._iScore = _iScore;
    }

    /**
     * 多选题，判断用户对错
     * @param question
     */
    private void checkoutMultipleQuestion(Question question, int index) {
        String strUser = "";
        String strAnswer = "";
        int flag = 1 ;
        for(int i=0; i<question._options.size(); i++){
            if(question._userSelectedMap.get(i) != null && question._userSelectedMap.get(i)){
                strUser += int2Letter(i);//记录用户选择的选项
            }
            //判断用户选项的正确性
            if(question._answerMap.get(i) != null ){
                if(question._answerMap.get(i)){
                    strAnswer += int2Letter(i);//记录正确答案
                }
                //判断用户选项的正确性
                if(question._userSelectedMap.get(i) != null){
                    if(question._answerMap.get(i) != question._userSelectedMap.get(i)) {flag = 0;}
                    else _paper._questions.get(index)._isHalfRight = true;
                }else{
                    flag = 0;
                }
            }
        }

        if(flag == 1) {
            _iScore++;
            _paper._questions.get(index)._isRight = true;
        }else {
            _paper._questions.get(index)._isRight = false;
            _paper._isAllRight = false;

        }

        question._strUserSelected = strUser;
        question._strAnswer = strAnswer;
    }

    /**
     * 单选题，判断用户对错
     * @param question
     */
    private void checkoutSingleQuestion(Question question, int index) {
        int iRightAnswer = question._iSingleAnswer;
        question._strAnswer = int2Letter(iRightAnswer);
        if(question._userSelectedMap.get(iRightAnswer)) {
            _iScore++;
            _paper._questions.get(index)._isRight = true;
        }else{
            _paper._isAllRight = false;
        }

        for(int i=0 ;i < question._options.size(); i++){
            if(question._userSelectedMap.get(i) != null && question._userSelectedMap.get(i)){
                question._strUserSelected = int2Letter(i);
                break;
            }
        }
    }

    private String int2Letter(int index){
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_activity_paper_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.setClass(this, ActivityPaperAnalysis.class);
        intent.putExtra("PAPER", _paper);
        intent.putExtra("POSITION", position);
        startActivity(intent);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_check_analysis:
                Intent intent = new Intent();
                intent.setClass(this, ActivityPaperAnalysis.class);
                intent.putExtra("PAPER", _paper);
                startActivity(intent);
                break;
        }
    }

    class SavePaperInfoAsyncTask extends AsyncTask<Paper, Integer, Boolean>{

        ProgressDialog progressDialog;
        DBHelper dbHelper;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(ActivityPaperReport.this, null, getString(R.string.loading));
        }
        @Override
        protected Boolean doInBackground(Paper... params) {
            Paper paper = params[0];
            boolean result = false;
            dbHelper = DBHelper.getInstance(ActivityPaperReport.this);
            if(NetUtil.isConnect(ActivityPaperReport.this)){
                UserSharedPreference userSharedPreference = new UserSharedPreference(ActivityPaperReport.this);
                String username = userSharedPreference.getAccountNumber();
                result = NetUtil.upLoadPaperInfo(paper, username);
            }
            savePaperInfo2DB(paper, result);
            logErrorQuestion( paper);
            FileOperation.writePaperReport2SD(paper);
            dbHelper.destoryInstance();
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result){
            progressDialog.dismiss();
        }

        private void savePaperInfo2DB(Paper paper, boolean hasUpload){

            SQLiteDatabase mDB = dbHelper.getReadableDatabase();
            ContentValues values = new ContentValues();
            String paperId = paper._id;
            values.put("paperID", paperId);
            values.put("name", paper._strName);
            values.put("score", paper._iScore);
            values.put("spendTime", paper._strSpendTime);
            values.put("startTime", paper._lStartTime);
            values.put("endTime", paper._lEndTime);
            values.put("hasUpload", hasUpload?1:0);

            Cursor cursor = mDB.query(Strings.TABLE_NAME_HISTORY_PAPERS, new String[]{"paperID"}, "paperID='"+paperId+"'",null,null,null,null);
            if( !cursor.moveToFirst()){//表中不含有该id
                mDB.insert(Strings.TABLE_NAME_HISTORY_PAPERS, null, values);
            }else{
                mDB.update(Strings.TABLE_NAME_HISTORY_PAPERS, values, "paperID = ?", new String[]{paperId});
            }
            cursor.close();
            mDB.close();
        }

        private void logErrorQuestion( Paper paper){
            SQLiteDatabase mDB = dbHelper.getReadableDatabase();
            mDB.beginTransaction();
            try {
                for (int i = 0; i < paper._questions.size(); i++) {
                    Question question = paper._questions.get(i);
                    if (!question._isRight || question._isHalfRight) {//如果答案是错的或者半对
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("questionID", question._id);
                        contentValues.put("paperID", paper._id);
                        contentValues.put("paperName", paper._strName);
                        contentValues.put("firstKP", paper._strFirstKP);
                        contentValues.put("secondKP", paper._strSecondKP);
                        contentValues.put("thirdKP", paper._strThirdKP);
                        contentValues.put("isCorrect", 0);//错题
                        contentValues.put("spendTime", question._lSpendTime);
                        insert2Table(mDB, contentValues);

                    }
                }
                mDB.setTransactionSuccessful();
            }finally {
                mDB.endTransaction();
            }
            mDB.close();
            for(int i=0; i< paper._questions.size(); i++) {
                Question question = paper._questions.get(i);
                FileOperation.writeQuestion2SD(question, Question.ERROR);
            }
        }

        /**
         * error_questions("
         "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
         "paper_id INTEGER,"
         "question_id INTEGER,"
         "user_anser TEXT)"
         * @param values
         */
        private void insert2Table(SQLiteDatabase mDB,  ContentValues values){
            try{
                String questionId = values.getAsString("questionID");
                String firstKP = values.getAsString("firstKP");
                String secondKP = values.getAsString("secondKP");
                String thirdKP = values.getAsString("thirdKP");
                values.put("isCorrect", 0);


                Cursor cursor = mDB.query(Strings.TABLE_NAME_LOG_QUESTIONS, new String[]{"questionID"}, "questionID=?",new String[]{questionId},null,null,null);
                if( !cursor.moveToFirst()){//如果游标为空
                    cursor.close();
                    //mDB.close();
                    //mDB = dbHelper.getWritableDatabase();

                    long result = mDB.insert(Strings.TABLE_NAME_LOG_QUESTIONS, null, values);
                    if(result == -1){
                        Log.e("DB insert Error", "Error");
                    }
                }else{//表已有相同id的question
                    mDB.update(Strings.TABLE_NAME_LOG_QUESTIONS, values, "questionID='"+questionId+"'", null);

                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
