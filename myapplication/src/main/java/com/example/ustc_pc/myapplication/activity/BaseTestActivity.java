package com.example.ustc_pc.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.fragment.AnswerSheetFragment;
import com.example.ustc_pc.myapplication.fragment.BaseTestFragment;
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
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BaseTestActivity extends AppCompatActivity implements AnswerSheetFragment.OnFragmentInteractionListener{

    ViewPager mViewPager;
    List<QuestionNew> mQuestions;
    QuestionsPagerAdapter mQuestionsPagerAdapter;

    private int mICourseID;
    private int mIQuestionType;
    private String mStrKPName, mStrKPID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_test);

        Intent intent = getIntent();
        mICourseID = intent.getIntExtra("iCourseID", 0);
        mIQuestionType = intent.getIntExtra("iQuestionType", -1);
        mStrKPName = intent.getStringExtra("strKPName");
        mStrKPID = intent.getStringExtra("strKPID");

        mViewPager = (ViewPager)findViewById(R.id.viewPager_actvity_base_test);
        mQuestions = new ArrayList<>();
        mQuestionsPagerAdapter = new QuestionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mQuestionsPagerAdapter);
        DownloadQuestionsAsyncTask downloadQuestionsAsyncTask = new DownloadQuestionsAsyncTask(this);
        downloadQuestionsAsyncTask.execute(String.valueOf(mICourseID), String.valueOf(mIQuestionType), mStrKPID);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base_test, menu);
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

    /**
     * Capture submit answer event
     * @param uri
     */
    @Override
    public void onFragmentInteraction(Uri uri) {
        Intent intent = new Intent(this, CABaseTestActivity.class);
        intent.putExtra("questions", (Serializable)mQuestions);
        intent.putExtra("mICourseID",mICourseID);
        intent.putExtra("mIQuestionType",mIQuestionType);
        intent.putExtra("mStrKPID",mStrKPID);
        intent.putExtra("mStrKPName",mStrKPName);

        startActivity(intent);
        finish();
    }

    private class QuestionsPagerAdapter extends FragmentStatePagerAdapter{

        public QuestionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;
            if(position < mQuestions.size()){
                fragment = BaseTestFragment.newInstance(
                        mQuestions.get(position), mStrKPName, position, getCount());
            }else{
                fragment = AnswerSheetFragment.newInstance(mQuestions);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return mQuestions.size() + 1;
        }
    }
    private class ParseQuestionsAsyncTask extends AsyncTask<String, Integer, List<QuestionNew>>{

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
            if(result != null && !result.isEmpty()){
                mQuestions.addAll(result);
                mQuestionsPagerAdapter.notifyDataSetChanged();

            }
            progressDialog.dismiss();
        }
    }
    private class DownloadQuestionsAsyncTask extends AsyncTask<String, Integer, Boolean> {

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

        /**
         *
         * @param strings courseID , questionType, kpID
         * @return
         */
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
            else {
                Toast.makeText(context, "Download Successed", Toast.LENGTH_SHORT).show();
                ParseQuestionsAsyncTask parseQuestionsAsyncTask = new ParseQuestionsAsyncTask(context);
                parseQuestionsAsyncTask.execute(String.valueOf(mICourseID), String.valueOf(mIQuestionType), mStrKPID);
            }
        }
    }
}
