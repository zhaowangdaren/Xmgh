package com.example.ustc_pc.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.dao.DoneQuestion;
import com.example.ustc_pc.myapplication.db.DoneQuestionDBHelper;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.net.OkHttpUtil;
import com.example.ustc_pc.myapplication.unit.Util;
import com.example.ustc_pc.myapplication.unit.QuestionUnmultiSon;
import com.example.ustc_pc.myapplication.viewUnit.ScrollViewWithGridView;
import com.example.ustc_pc.myapplication.unit.UnmultiSonAnalysis;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Check Answer for base test
 * @author zy
 */
public class CABaseTestActivity extends AppCompatActivity implements View.OnClickListener{
    List<QuestionUnmultiSon> mQuestions;
    List<UnmultiSonAnalysis> mAnalysises;
    List<DoneQuestion> mDoneQuestions;


    private int miTestID;
    private int mICourseID;
    private int mIQuestionType;
    private String mStrKPName, mStrKPID;
    private int mCorrectNum = 0;
    private long mlTestSpendTime, mlTestStartTime, mlTestEndTime;

    private Button mShowAllAnalysisBT;
    private TextView mKPNameTV, mSpendTime, mScoreTV, mSumQueNumTV;
    private ScrollViewWithGridView mAnswerSheetGV;
    private String mStrTestSpendTime = "00:00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_answer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mQuestions = (List<QuestionUnmultiSon>)intent.getSerializableExtra("questions");
        if(mQuestions == null)mQuestions = new ArrayList<>();
        miTestID = intent.getIntExtra("miTestID", -1);
        mICourseID = intent.getIntExtra("mICourseID", -1);
        mIQuestionType = intent.getIntExtra("mIQuestionType", -1);
        mStrKPID = intent.getStringExtra("mStrKPID");
        mStrKPName = intent.getStringExtra("mStrKPName");
        mlTestStartTime = intent.getLongExtra("mlTestStartTime", 0);
        mlTestEndTime = intent.getLongExtra("mlTestEndTime", 0);
        mlTestSpendTime = intent.getLongExtra("mlTestSpendTime", 0);
        mStrTestSpendTime = intent.getStringExtra("mStrTestSpendTime");

        mShowAllAnalysisBT = (Button) findViewById(R.id.button_show_all_analysis);
        mShowAllAnalysisBT.setOnClickListener(this);
        mKPNameTV = (TextView)findViewById(R.id.textView_test_kp_name);
        mKPNameTV.setText(mStrKPName);
        mSpendTime = (TextView)findViewById(R.id.textView_test_spend_time);
        mSpendTime.setText(mStrTestSpendTime);

        mScoreTV = (TextView)findViewById(R.id.textView_score);
        mSumQueNumTV = (TextView)findViewById(R.id.textView_sum_questions_num);
        mSumQueNumTV.setText(""+mQuestions.size());

        mAnswerSheetGV = (ScrollViewWithGridView)findViewById(R.id.gridView_answer_sheets);

        parseAnswer();
    }

    private void parseAnswer() {
        if(mICourseID < 0 || mIQuestionType < 0)return;
        ParseAnswerAsync parseAnswerAsync = new ParseAnswerAsync(this);
        parseAnswerAsync.execute(String.valueOf(mICourseID), String.valueOf(mIQuestionType), String.valueOf(miTestID), mStrKPID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_check_answer, menu);
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_show_all_analysis:
                Intent intent = new Intent(this, ShowAnalysisActivity.class);
                intent.putExtra("mDoneQuestions", (Serializable)mDoneQuestions);
                intent.putExtra("mQuestions",(Serializable)mQuestions);
                intent.putExtra("mAnalysises", (Serializable) mAnalysises);
                intent.putExtra("mStrKPName",mStrKPName);

                startActivity(intent);
        }
    }

    class ParseAnswerAsync extends AsyncTask<String,Integer, List<DoneQuestion>>{
        ProgressDialog progressDialog;
        Context context;
        public ParseAnswerAsync(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(context, null,getString(R.string.parseing_answer));
        }
        @Override
        protected List<DoneQuestion> doInBackground(String... strings) {
            String strCourseID = strings[0];
            String strQuestionType = strings[1];
            String strTestID = strings[2];
            String strKPID = strings[3];
            List<UnmultiSonAnalysis> answers =
                    Util.parseUnmultiSonAnswerFromFile(strCourseID, strQuestionType, strTestID, strKPID);
            if(answers != null && !answers.isEmpty()){
                mAnalysises = answers;
                List<DoneQuestion> doneQuestions = recordResult(answers);
                return doneQuestions;
            }
            return null;
        }

        private List<DoneQuestion> recordResult(List<UnmultiSonAnalysis> analysises) {
            if(mQuestions.size() != analysises.size()){
                Log.e("Error","The questions num does not match the answers num!");
                return null;
            }
            Collections.sort(mQuestions);
            Collections.sort(analysises);

            //set answer
            for(int i=0; i<analysises.size(); i++){
                List<String> answers = analysises.get(i).getAnswer();
                for(int j = 0; j<answers.size(); j++){
                    setAnswer(i, answers.get(j));
                }
            }

            List<DoneQuestion> result = new ArrayList<>(analysises.size());
            for(int i = 0; i < analysises.size(); i++){
                QuestionUnmultiSon questionUnmultiSon = mQuestions.get(i);
                boolean isCorrect = true;
                StringBuffer strUserAnswer = new StringBuffer("");
                List<QuestionUnmultiSon.QuestionOption> questionOptions = questionUnmultiSon.getOptions();
                for(int j = 0; j< questionOptions.size(); j++){
                    if( ! ( questionOptions.get(j).isAnswer() && questionOptions.get(j).isSelected())){
                        isCorrect = false;
                    }
                    if(questionOptions.get(j).isSelected())strUserAnswer.append(questionOptions.get(j).getID() + ",");
                }
                long lQuestionSpendTime = questionUnmultiSon.getlStopTime()
                        - questionUnmultiSon.getlStartTime();
                DoneQuestion doneQuestion = new DoneQuestion(
                        mICourseID,
                        mIQuestionType,
                        (long)(questionUnmultiSon.getiQuestionID()),
                        false,
                        isCorrect,
                        null,
                        strUserAnswer.toString(),
                        lQuestionSpendTime,
                        mStrKPID,
                        miTestID
                );
                result.add(doneQuestion);

                if(isCorrect)mCorrectNum++;
            }
            return result;
        }

        private void setAnswer(int position, String answerID){
            List<QuestionUnmultiSon.QuestionOption> questionOptions = mQuestions.get(position).getOptions();
            int optionNum = questionOptions.size();
            for(int i =0; i<optionNum; i++){
                if(questionOptions.get(i).getID().equals(answerID)) questionOptions.get(i).setIsAnswer(true);
            }
        }

        @Override
        protected void onPostExecute(List<DoneQuestion> result){
            if(result != null && !result.isEmpty()){
                mDoneQuestions = result;
                AnswerSheetGVAdapter answerSheetGVAdapter = new AnswerSheetGVAdapter();
                mAnswerSheetGV.setAdapter(answerSheetGVAdapter);

                mScoreTV.setText("" + mCorrectNum);

                saveDoneQuestion2DB();
                uploadDoneQuestion(new UserSharedPreference(context).getiUserID(), miTestID, mStrKPID, mDoneQuestions);
            }
            progressDialog.dismiss();
        }
    }

    private void uploadDoneQuestion(final int iUserID, final long lTestID
            , final String strTestKPID, final List<DoneQuestion> doneQuestions){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil okHttpUtil = new OkHttpUtil();
                try {
                    okHttpUtil.uploadDoneQuestions(iUserID, lTestID,strTestKPID,doneQuestions);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void saveDoneQuestion2DB(){
        if(mDoneQuestions == null || mDoneQuestions.isEmpty())return;
        DoneQuestionDBHelper doneQuestionDBHelper = DoneQuestionDBHelper.getInstance(this);
        doneQuestionDBHelper.insertDoneQue(mDoneQuestions);
    }

    class AnswerSheetGVAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mDoneQuestions.size();
        }

        @Override
        public DoneQuestion getItem(int i) {
            return mDoneQuestions.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ASViewHolder viewHolder = null;
            if(convertView == null){
                convertView = View.inflate(parent.getContext(), R.layout.layout_question_option_item, null);
                viewHolder = new ASViewHolder();
                viewHolder.textView = (CheckedTextView)convertView.findViewById(R.id.checkedTV_option);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ASViewHolder) convertView.getTag();
            }

            if(getItem(position).getIsCorrect()){
                viewHolder.textView.setBackgroundDrawable(parent.getContext().getResources().getDrawable(R.drawable.bg_answer_right));
                viewHolder.textView.setTextColor(-1);//white
            }else {
                viewHolder.textView.setBackgroundDrawable(parent.getContext().getResources().getDrawable(R.drawable.bg_answer_error));
                viewHolder.textView.setTextColor(-1);//white
            }
            int index = position + 1;
            viewHolder.textView.setText("" + index);

            return convertView;
        }

        class ASViewHolder{
            CheckedTextView textView;
        }
    }

}
