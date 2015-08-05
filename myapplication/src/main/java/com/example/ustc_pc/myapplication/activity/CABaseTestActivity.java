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
import com.example.ustc_pc.myapplication.net.Util;
import com.example.ustc_pc.myapplication.unit.Answer;
import com.example.ustc_pc.myapplication.unit.QuestionNew;
import com.example.ustc_pc.myapplication.unit.ScrollViewWithGridView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Check Answer for base test
 * @author zy
 */
public class CABaseTestActivity extends AppCompatActivity implements View.OnClickListener{
    List<QuestionNew> mQuestions;
    List<Answer> mAnswers;
    List<DoneQuestion> mDoneQuestions;


    private int mICourseID;
    private int mIQuestionType;
    private String mStrKPName, mStrKPID;
    private int mCorrectNum = 0;

    private Button mShowAllAnalysisBT;
    private TextView mKPNameTV, mSpendTime, mScoreTV, mSumQueNumTV;
    private ScrollViewWithGridView mAnswerSheetGV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_answer);

        Intent intent = getIntent();
        mQuestions = (List<QuestionNew>)intent.getSerializableExtra("questions");
        if(mQuestions == null)mQuestions = new ArrayList<>();
        mICourseID = intent.getIntExtra("mICourseID", -1);
        mIQuestionType = intent.getIntExtra("mIQuestionType", -1);
        mStrKPID = intent.getStringExtra("mStrKPID");
        mStrKPName = intent.getStringExtra("mStrKPName");

        mShowAllAnalysisBT = (Button) findViewById(R.id.button_show_all_analysis);
        mShowAllAnalysisBT.setOnClickListener(this);
        mKPNameTV = (TextView)findViewById(R.id.textView_test_kp_name);
        mSpendTime = (TextView)findViewById(R.id.textView_test_spend_time);
        mScoreTV = (TextView)findViewById(R.id.textView_score);
        mSumQueNumTV = (TextView)findViewById(R.id.textView_sum_questions_num);
        mSumQueNumTV.setText(""+mQuestions.size());

        mAnswerSheetGV = (ScrollViewWithGridView)findViewById(R.id.gridView_answer_sheets);

        parseAnswer();
    }

    private void parseAnswer() {
        if(mICourseID < 0 || mIQuestionType < 0)return;
        ParseAnswerAsync parseAnswerAsync = new ParseAnswerAsync(this);
        parseAnswerAsync.execute(String.valueOf(mICourseID), String.valueOf(mIQuestionType), mStrKPID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_check_answer, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_show_all_analysis:
                Intent intent = new Intent(this, ShowAnalysisActivity.class);
                intent.putExtra("mDoneQuestions", (Serializable)mDoneQuestions);
                intent.putExtra("mQuestions",(Serializable)mQuestions);
                intent.putExtra("mAnswers", (Serializable)mAnswers);
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
            String strKPID = strings[2];
            String[] kpIDs = strKPID.split("\\.");

            //relative path
            String strKpIDRPPath = "";
            for(int i=0; i< kpIDs.length; i++){
                strKpIDRPPath += kpIDs + "/";
            }
            //absolute path
            String strKpIdAPPath = Util.APP_PATH + strCourseID + "/" + strQuestionType + "/" + strKpIDRPPath;
            if( !(new File(strKpIdAPPath).exists())){
                Log.e("Error Mutl...Activity", "Kp id absolute un exists");
                return null;
            }
            String[] questionsAPath = Util.getAllQuestionsAPath(strKpIdAPPath);
            List<Answer> answers = Util.parseAnswerFromFile(questionsAPath);

            if(answers != null && !answers.isEmpty()){
                List<DoneQuestion> doneQuestions = recordResult(answers);
                return doneQuestions;
            }
            return null;
        }

        private List<DoneQuestion> recordResult(List<Answer> answers) {
            if(mQuestions.size() != answers.size()){
                Log.e("Error","The questions num does not match the answers num!");
                return null;
            }
            Collections.sort(mQuestions);
            Collections.sort(answers);


            for(int i = 0; i < answers.size(); i++){
                List<Answer.AnswerSon> answerSons = answers.get(i).getAnswerSons();
                List<Answer.AnswerSon.AnswerOption> answerOptions = answerSons.get(0).getAnswer();
                for(int j = 0; j<answerOptions.size(); j++){
                    int ID = answerOptions.get(i).getID();
                    setAnswer(i, ID);
                }
            }
            List<DoneQuestion> result = new ArrayList<>(answers.size());
            for(int i = 0; i < answers.size(); i++){
                QuestionNew questionNew = mQuestions.get(i);
                boolean isCorrect = true;
                StringBuffer strUserAnswer = new StringBuffer("");
                List<QuestionNew.QuestionSon.QuestionOption> questionOptions = questionNew.getQuestionSons().get(0).getOptions();
                for(int j = 0; j< questionOptions.size(); j++){
                    if(!(questionOptions.get(j).isAnswer && questionOptions.get(j).isSelected)){
                        isCorrect = false;
                    }
                    if(questionOptions.get(j).isAnswer)strUserAnswer.append(questionOptions.get(j).ID + ",");
                }
                DoneQuestion doneQuestion = new DoneQuestion(
                        mICourseID,mIQuestionType,questionNew.getiQuestionID(),
                        false,isCorrect,null,strUserAnswer.toString(),
                        questionNew.getQuestionSons().get(0).getISpendTime());
                result.add(doneQuestion);

                if(isCorrect)mCorrectNum++;
            }
            return result;
        }

        private void setAnswer(int position, int answerID){
            List<QuestionNew.QuestionSon.QuestionOption> questionOptions = mQuestions.get(position).getQuestionSons().get(0).getOptions();
            int optionNum = questionOptions.size();
            for(int i = 0; i<optionNum; i++){
                if(questionOptions.get(i).ID == answerID){
                    questionOptions.get(i).isAnswer = true;
                }
            }
        }
        @Override
        protected void onPostExecute(List<DoneQuestion> result){
            if(result != null && !result.isEmpty()){
                mDoneQuestions = result;
                AnswerSheetGVAdapter answerSheetGVAdapter = new AnswerSheetGVAdapter();
                mAnswerSheetGV.setAdapter(answerSheetGVAdapter);

                mScoreTV.setText(""+mCorrectNum);
            }
        }
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
