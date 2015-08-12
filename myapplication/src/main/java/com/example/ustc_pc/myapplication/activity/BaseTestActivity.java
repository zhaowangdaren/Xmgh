package com.example.ustc_pc.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.menu.MenuView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.fragment.AnswerSheetFragment;
import com.example.ustc_pc.myapplication.fragment.BaseTestFragment;
import com.example.ustc_pc.myapplication.net.OkHttpUtil;
import com.example.ustc_pc.myapplication.unit.Util;
import com.example.ustc_pc.myapplication.unit.QuestionUnmultiSon;
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
import java.util.HashMap;
import java.util.List;

public class BaseTestActivity extends AppCompatActivity implements AnswerSheetFragment.OnFragmentInteractionListener, BaseTestFragment.OnFragmentInteractionListener{

    ViewAnimator mVA;
    View mBaseTestView, mErrorView;

    ViewPager mViewPager;
    List<QuestionUnmultiSon> mQuestions;
    QuestionsPagerAdapter mQuestionsPagerAdapter;

    private int mICourseID;
    private int mIQuestionType;
    private String mStrKPName, mStrKPID;
    private int miTestID;


    private long mlTestStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_test);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        mICourseID = intent.getIntExtra("iCourseID", 0);
        mIQuestionType = intent.getIntExtra("iQuestionType", -1);
        mStrKPName = intent.getStringExtra("strKPName");
        mStrKPID = intent.getStringExtra("strKPID");

        mVA = (ViewAnimator)findViewById(R.id.viewAnimator);
        mBaseTestView = View.inflate(this, R.layout.layout_base_test, null);
        mErrorView = View.inflate(this, R.layout.layout_load_failed, null);
        mErrorView.setOnClickListener(new ErrorViewClickListener(this));
        mVA.addView(mBaseTestView,0);
        mVA.addView(mErrorView, 1);

        mViewPager = (ViewPager) mBaseTestView.findViewById(R.id.viewPager_actvity_base_test);
        mQuestions = new ArrayList<>();
        DownloadQuestionsAsyncTask downloadQuestionsAsyncTask = new DownloadQuestionsAsyncTask(this);
        downloadQuestionsAsyncTask.execute(String.valueOf(mICourseID), String.valueOf(mIQuestionType), mStrKPID);

    }

    private int mCurrentPosition = 0;
    private class ViewPagerChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if(position < mQuestions.size()){
                mQuestions.get(position).setlStartTime(System.currentTimeMillis());
//                mQuestions.get(position).getQuestionSons().get(0).setlStartTime(
//                        System.currentTimeMillis());
                if(position > 0){
                    mQuestions.get(position - 1).setlStopTime(System.currentTimeMillis());
//                    mQuestions.get(position).getQuestionSons().get(0).setlStopTime(
//                        System.currentTimeMillis()
//                    );
                }
            }else{
                if(position == mQuestions.size()){
                    mQuestions.get(position - 1).setlStopTime(System.currentTimeMillis());
//                    mQuestions.get(position - 1).getQuestionSons().get(0).setlStopTime(
//                            System.currentTimeMillis());
                }
            }
            mQuestionsPagerAdapter.notifyDataSetChanged();
            mCurrentPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    }

    private long mlTestSpendTime;
    private String mStrTestSpendTime = "00:00";
    private MenuView.ItemView mTimerMenu;

    private void startTimer(){
        mlTestStartTime = System.currentTimeMillis();
        //init the first question start time
        if(mQuestions.size() > 0) mQuestions.get(0).setlStartTime(mlTestStartTime);
//        mQuestions.get(0).getQuestionSons().get(0).setlStartTime(mlTestStartTime);
        mUpdateTimerRun.run();
    }

    private Runnable mUpdateTimerRun = new Runnable() {
        @Override
        public void run() {
            long spentTime = System.currentTimeMillis() - mlTestStartTime;
            mlTestSpendTime = spentTime;
            //minius
            Long minius = (spentTime / 1000 ) / 60;
            //seconds
            Long seconds = (spentTime / 1000 ) % 60;

            Message msg = new Message();
            Bundle bundle = new Bundle();
            String strMinius = minius + "";
            if(minius < 10 ) strMinius = "0"+strMinius;
            String strSeconds = seconds + "";
            if(seconds < 10) strSeconds = "0" + strSeconds;
            String strTestSpendTime = strMinius+":"+strSeconds;
            bundle.putString("TEST_SPEND_TIME", strTestSpendTime);
            msg.setData(bundle);
            handlerTimer.sendMessage(msg);
            handlerTimer.postDelayed(this, 1000);
        }
    };

    private Handler handlerTimer = new Handler() {
      public void handleMessage(Message msg){
          mStrTestSpendTime = msg.getData().getString("TEST_SPEND_TIME");
          if(mStrTestSpendTime != null && mStrTestSpendTime.length() > 0){
              if(mTimerMenu == null)mTimerMenu = (MenuView.ItemView) findViewById(R.id.action_timer);
              mTimerMenu.setTitle(mStrTestSpendTime);
          }
      }
    };

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
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private long mlTestEndTime;
    /**
     * Capture submit answer event
     * @param uri
     */
    @Override
    public void onFragmentInteraction(Uri uri) {
        mlTestEndTime = System.currentTimeMillis();
        Intent intent = new Intent(this, CABaseTestActivity.class);
        intent.putExtra("questions", (Serializable)mQuestions);
        intent.putExtra("mICourseID",mICourseID);
        intent.putExtra("mIQuestionType",mIQuestionType);
        intent.putExtra("mStrKPID",mStrKPID);
        intent.putExtra("mStrKPName",mStrKPName);
        intent.putExtra("mlTestSpendTime", mlTestSpendTime);
        intent.putExtra("mlTestStartTime",mlTestStartTime);
        intent.putExtra("mlTestEndTime",mlTestEndTime);
        intent.putExtra("mStrTestSpendTime",mStrTestSpendTime);
        intent.putExtra("miTestID",miTestID);
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
                        mQuestions.get(position), mStrKPName, position, getCount() - 1);
            }else{
                fragment = AnswerSheetFragment.newInstance(mQuestions,miTestID);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return mQuestions.size() + 1;
        }

        @Override
        public int getItemPosition(Object object){
            if(object instanceof AnswerSheetFragment){
                ((AnswerSheetFragment)object).onResume();
            }
            return super.getItemPosition(object);
        }
    }
    private class ParseQuestionsAsyncTask extends AsyncTask<String, Integer, List<QuestionUnmultiSon>>{

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
        protected List<QuestionUnmultiSon> doInBackground(String... strings) {
            String strCourseID = strings[0];
            String strQuestionType = strings[1];
            String strKPID = strings[2];
            String strTestID = strings[3];
            /**
            String[] kpIDs = strKPID.split("\\.");

            //relative path
            String strKpIDRPPath = "";
            for(int i=0; i< kpIDs.length; i++){
                strKpIDRPPath += kpIDs[i] + "/";
            }
            //absolute path
            String strKpIdAPPath = Util.APP_PATH + "/" + strCourseID + "/" + strQuestionType + "/" + strKpIDRPPath;
            if( !(new File(strKpIdAPPath).exists())){
                Log.e("Error Mutl...Activity", "Kp id absolute un exists");
                return null;
            }
            String[] questionsAPath = Util.getAllQuestionsAPath(strKpIdAPPath);
            List<QuestionUnmultiSon> questions = Util.parseUnmultiSonQueFromFile(questionsAPath);
             */

            List<QuestionUnmultiSon> questions = Util.parseUnmultiSonQueFromFile(strCourseID
                    ,strQuestionType,strTestID, strKPID);
            return questions;
        }

        @Override
        protected void onPostExecute(List<QuestionUnmultiSon> result){
            if(result != null && !result.isEmpty()){
                mQuestions.addAll(result);
                mQuestionsPagerAdapter = new QuestionsPagerAdapter(getSupportFragmentManager());
                mViewPager.setAdapter(mQuestionsPagerAdapter);
                mViewPager.setOnPageChangeListener(new ViewPagerChangeListener());
                if(mVA.getCurrentView().equals(mErrorView))mVA.showPrevious();
                startTimer();
            }else{
                if(mVA.getCurrentView().equals(mBaseTestView))mVA.showNext();
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
            String strCourseID = strings[0];
            String strQuestionType = strings[1];
            String strKPID = strings[2];
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            InputStream inputStream = null;
            OutputStream outputStream = null;
            int iTestID ;
            try {
                HashMap<String, Object> result = okHttpUtil.getBasicTestOnline(
                        new UserSharedPreference(context).getiUserID()
                        , strCourseID
                        , strQuestionType
                        , strKPID);
                 iTestID = (int)result.get("iTestID");
                miTestID = iTestID;
                String strURL = (String)result.get("strURL");
                URL downloadURL = new URL(strURL);
                if(downloadURL == null )return false;
                strFileName = downloadURL.getFile();
                strFileName = strFileName.substring(strFileName.lastIndexOf("/"));
                if( ! Util.createFolder(Util.APP_PATH )){
                    Util.APP_PATH = Environment.getDownloadCacheDirectory()+"/.cn.edu.ustc.xmgh";
                    Util.createFolder(Util.APP_PATH);
                }
                //begin download file
                Response fileResponse = okHttpUtil.getClient().newCall(
                        new Request.Builder().url(downloadURL).get().build()
                ).execute();
                if(!fileResponse.isSuccessful())throw new IOException("Unexcepted cod" + fileResponse);
                inputStream = fileResponse.body().byteStream();
                String filePath = Util.APP_PATH + strFileName;

                outputStream = new FileOutputStream(new File(filePath));
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
                    if(outputStream != null) outputStream.close();
                }catch (IOException ignored){}

            }

            //Extract zip file
            String zipFilePath = Util.APP_PATH +strFileName;

            //the target path format is : APP_PATH/CourseId/QuestionType/testID/kpid
            String unZipTargetPath = Util.getQuesFolderPath(strCourseID, strQuestionType, String.valueOf(iTestID), strKPID);
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
                parseQuestionsAsyncTask.execute(
                        String.valueOf(mICourseID)
                        , String.valueOf(mIQuestionType)
                        , mStrKPID
                        , String.valueOf(miTestID)
                );
            }
            progressDialog.dismiss();
        }
    }

    private class ErrorViewClickListener implements View.OnClickListener {
        private Context context;
        public ErrorViewClickListener(Context context){
           this.context = context;
        }
        @Override
        public void onClick(View view) {
            DownloadQuestionsAsyncTask downloadQuestionsAsyncTask = new DownloadQuestionsAsyncTask(context);
            downloadQuestionsAsyncTask.execute(String.valueOf(mICourseID), String.valueOf(mIQuestionType), mStrKPID);
        }
    }
}
