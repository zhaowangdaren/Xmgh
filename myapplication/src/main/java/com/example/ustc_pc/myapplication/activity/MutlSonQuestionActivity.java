package com.example.ustc_pc.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.fragment.MutlQuestionSonFragment;
import com.example.ustc_pc.myapplication.net.OkHttpUtil;
import com.example.ustc_pc.myapplication.net.Util;
import com.example.ustc_pc.myapplication.unit.QuestionNew;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MutlSonQuestionActivity extends AppCompatActivity {

    public static final int TYPE_QUESTION_LAYOUT_HEADER = 0, TYPE_QUESTION_LAYOUT_FATHER = 1,
            TYPE_QUESTION_LAYOUT_SON = 2;

    int mICourseID, mIQuestionType;
    String mStrKPID, mStrKPName;
    List<QuestionNew> mQuestions;

    //View
    ViewAnimator mViewAnimator;
    View mTestView;
    View mErrorView;
    RecyclerView mQuestionRecyclerView;

    // which question is showing
    int mQuestionShowIndex = 1;
    int mQuestionActuallyIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mult_son_que_test);

        Intent intent = getIntent();
        mICourseID = intent.getIntExtra("iCourseID", 0);
        mIQuestionType = intent.getIntExtra("iQuestionType", -1);
        mStrKPID = intent.getStringExtra("strKPID");
        mStrKPName = intent.getStringExtra("strKPName");

        mViewAnimator = (ViewAnimator)findViewById(R.id.viewAnimator_test_activity);
        mTestView = View.inflate(this,R.layout.layout_question, null);
        mErrorView = View.inflate(this, R.layout.layout_load_failed, null);
        mErrorView.setOnClickListener(new ErrorViewClickListener());
        mViewAnimator.addView(mTestView, 0);
        mViewAnimator.addView(mErrorView, 1);

        if( ! Util.isConnect(this) ){
            mViewAnimator.showNext();
            return;
        }

        if(mICourseID > 0 && mIQuestionType > -1) {
            parseQuestions(mICourseID, mIQuestionType, mStrKPID);
        }

    }

    private void initTestView() {
        mQuestionRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_layout_question);

    }

    private void parseQuestions(int iCourseID, int iQuestionType, String strKPID) {
        mQuestions = new ArrayList<>();

    }

    class QuestionRecyclerViewAdapter extends RecyclerView.Adapter<QuestionRecyclerViewAdapter.QuestionRecyclerViewHolder>{


        @Override
        public QuestionRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            QuestionRecyclerViewHolder holder = null;
            View view;
            switch (viewType){
                case TYPE_QUESTION_LAYOUT_HEADER:
                    view = View.inflate(getApplicationContext() ,R.layout.layout_question_header, null);
                    holder = new QuestionRecyclerViewHolder(view, viewType);
                    break;
                case TYPE_QUESTION_LAYOUT_FATHER:
                    view = View.inflate(getApplicationContext(), R.layout.layout_question_father, null);
                    holder = new QuestionRecyclerViewHolder(view, viewType);
                    break;
                case TYPE_QUESTION_LAYOUT_SON:
                    view = View.inflate(getApplicationContext(), R.layout.layout_question_son, null);
                    holder = new QuestionRecyclerViewHolder(view, viewType);
                    break;
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(QuestionRecyclerViewHolder holder, int position) {
            //the index in the question list
            switch (getItemViewType(position)){
                case TYPE_QUESTION_LAYOUT_HEADER:
                    holder.questionTypeTV.setText(mStrKPName);
                    holder.questionIndexTV.setText(""+mQuestionActuallyIndex + 1);
                    holder.questionSumTV.setText(""+mQuestions.size());
                    break;
                case TYPE_QUESTION_LAYOUT_FATHER:

                case TYPE_QUESTION_LAYOUT_SON:
                    break;
            }
        }

        /**
         * 0:layout_question_header, 1:layout_question_father, 2:layout_question_son
         * @return
         */
        @Override
        public int getItemCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position){
            switch (position){
                case 0:
                    return TYPE_QUESTION_LAYOUT_HEADER;
                case 1:
                    return TYPE_QUESTION_LAYOUT_FATHER;
                case 2:
                    return TYPE_QUESTION_LAYOUT_SON;
                default: return TYPE_QUESTION_LAYOUT_FATHER;
            }
        }

        public class QuestionRecyclerViewHolder extends RecyclerView.ViewHolder{
            // layout question header
            TextView questionTypeTV, questionIndexTV, questionSumTV;

            //layout question father
            TextView questionFatherTV;

            //layout question son
            ViewPager questionSonViewPager;

            public QuestionRecyclerViewHolder(View view, int iType){
                super(view);
                switch (iType){
                    case TYPE_QUESTION_LAYOUT_HEADER:
                        questionTypeTV = (TextView) view.findViewById(R.id.textView_layout_question_header_question_type);
                        questionIndexTV = (TextView)view.findViewById(R.id.textView_layout_question_header_index);
                        questionSumTV = (TextView)view.findViewById(R.id.textView_layout_question_header_sum);
                        break;
                    case TYPE_QUESTION_LAYOUT_FATHER:
                        questionFatherTV = (TextView)view.findViewById(R.id.textView_layout_question_father);
                        break;
                    case TYPE_QUESTION_LAYOUT_SON:
                        questionSonViewPager = (ViewPager)view.findViewById(R.id.viewPager_layout_question_son);
                        break;
                }
            }
        }
    }

    public class QuestionSonPagerAdapter extends FragmentStatePagerAdapter{

        int index;
        public QuestionSonPagerAdapter(FragmentManager fm, int index){
            super(fm);
            this.index = index;
        }
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = MutlQuestionSonFragment.newInstance(mQuestions.get(index));
            return fragment;
        }

        @Override
        public int getCount() {
            return mQuestions.get(index).getQuestionSons().size();
        }
    }
    class ErrorViewClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {

        }
    }

    class ParseQuestionsAsyncTask extends AsyncTask<String, Integer, List<QuestionNew>>{

        ProgressDialog progressDialog;
        Context context;
        public ParseQuestionsAsyncTask(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(context, null,getString(R.string.parseing_questions));
        }
        @Override
        protected List<QuestionNew> doInBackground(String... strings) {
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
            List<QuestionNew> questions = Util.parseQuestionsFromFile(questionsAPath);
            return questions;
        }

        @Override
        protected void onPostExecute(List<QuestionNew> result){

        }
    }

    private class DownloadQuestionsAsyncTask extends AsyncTask<String, Integer, Boolean>{

        ProgressDialog progressDialog;
        Context context;
        PowerManager.WakeLock wakeLock;

        String strFileName;
        public DownloadQuestionsAsyncTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(context);
//            progressDialog = ProgressDialog.show(context, null, getString(R.string.loading));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            int iCourseID = Integer.valueOf(strings[0]);
            int iQuestionType = Integer.valueOf(strings[1]);
            String strKPID = strings[2];
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                URL downloadURL = okHttpUtil.getQuestionsURL(iCourseID, iQuestionType, strKPID);
                if(downloadURL == null )return false;
                strFileName = downloadURL.getFile();
                //begin download file
                Response fileResponse = okHttpUtil.getClient().newCall(
                        new Request.Builder().url(downloadURL).get().build()
                ).execute();
                if(!fileResponse.isSuccessful())throw new IOException("Unexcepted cod" + fileResponse);
                inputStream = fileResponse.body().byteStream();
                outputStream = new FileOutputStream(Util.APP_PATH);
                byte[] buff = new byte[1024 * 4];
                long downloaded = 0;
                long target = fileResponse.body().contentLength();
                while (true){
                    int readed = inputStream.read(buff);
                    if(readed == -1)break;

                    //write buff
                    outputStream.write(buff, 0, readed);
                    downloaded += readed;
                    if(target > 0 ){
                        int precent = (int) (downloaded * 100 / target );
                        publishProgress(precent);
                        onProgressUpdate(precent);
                    }
                }

            }catch (IOException e){
                Log.e("Download failed ", e.toString());
                return false;
            }finally {
                try {
                    if (inputStream != null) inputStream.close();
                    if(outputStream != null)outputStream.close();
                }catch (IOException ignored){}
            }

            //Extract zip file
            String zipFilePath = Util.APP_PATH + "/" +strFileName;

            //the target path format is : APP_PATH/CourseId/QuestionType
            String unZipTargetPath = Util.APP_PATH + "/" + strings[0] + "/" + strings[1];
            boolean isUnzipSuccess = Util.unZip(zipFilePath, unZipTargetPath);

            return isUnzipSuccess;
        }

        @Override
        protected void onProgressUpdate(Integer... progress){
            super.onProgressUpdate(progress);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(!result)
                Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Download Successed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mult_son_que_activity, menu);
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
}
