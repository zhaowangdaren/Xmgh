package com.example.ustc_pc.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.example.ustc_pc.myapplication.activity.ActivityLogin;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.unit.FileOperation;

import java.io.File;

public class ActivityWelcome extends Activity implements View.OnClickListener{

    private final int INIT_FINISHED = 0;
    private boolean isLogin = false;
    /**
     * 通知用户无网络的页面
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        View view = View.inflate(this, R.layout.activity_welcome, null);
//        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        initFilePath();
        initView();

        AVOSCloud.initialize(this, "pn9ak66f0nl9v4oj3e9fg4goq3s8gtay5i59tq612m1x9cb1", "eahgl2h6hjkvma2h28gg4wn6mlaeegwr3j8yyiyyqkkbqt1h");
        AVAnalytics.trackAppOpened(getIntent());
    }

    private void textAVOS(){
        AVObject avObject = new AVObject("TestObject");
        avObject.put("foo","bar");
        avObject.saveInBackground();
    }
    private void initView() {
        new Thread(new Runnable(){
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        //这里可以进行一些数据的初始化加载
                        try{
                            Thread.sleep(2000);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(INIT_FINISHED);
                    }

                }).start();
        UserSharedPreference userSharedPreference = new UserSharedPreference(this);
        isLogin = userSharedPreference.getIsLogin();
    }

    private void initFilePath() {
        File file = new File(Environment.getExternalStorageDirectory().getPath());
        if( !file.exists() ){
            FileOperation.setAppPath(this);
        }else{
            file = new File(FileOperation.APP_PATH);
            if( !file.exists()){
                file.mkdir();
            }
        }
    }


//    /**
//     * 这里没有判断本地courses.json文件是否与服务器上的相同；
//     * 在有网络的情况下，客户端都会从服务器获取文件
//     */
//    private void initView() {
//        mLinearLayoutNoNet = (LinearLayout)findViewById(R.id.linearLayout_no_network);
//        mLinearLayoutWelcome = (LinearLayout)findViewById(R.id.linearLayout_welcome);
//        mLinearLayoutNoNet.setOnClickListener(this);
//
//        if(!NetUtil.isConnect(this)){//No Network
//            if(!checkInitFileExist()){//知识点文件不存在
//                mLinearLayoutWelcome.setVisibility(View.GONE);
//                mLinearLayoutNoNet.setVisibility(View.VISIBLE);
//            }else{//知识点文件存在
//                Toast.makeText(this, "File Existed!!!", Toast.LENGTH_SHORT).show();
//                new Thread(new Runnable(){
//
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
//                        //这里可以进行一些数据的初始化加载
//                        try{
//                            Thread.sleep(1000);
//                        }catch(InterruptedException e){
//                            e.printStackTrace();
//                        }
//                        handler.sendEmptyMessage(INIT_FINISHED);
//                    }
//
//                }).start();
//            }
//        }else{//has Network
////            final ProgressDialog dialog =new ProgressDialog(this);
////            DownloadTask downloadTask = new DownloadTask(this, dialog, this);
////            downloadTask.execute(Strings.URL_HOME, "courses.json");
//            GetAssessmentScore getAssessmentScore = new GetAssessmentScore();
//            getAssessmentScore.execute();
//        }
//
//    }

//    class GetAssessmentScore extends AsyncTask<Integer , Integer, Integer> {
//
//        @Override
//        protected void onPreExecute(){
//            progressDialog = ProgressDialog.show(ActivityWelcome.this,null,getResources().getString(R.string.loading));
//        }
//
//        @Override
//        protected Integer doInBackground(Integer... params) {
//            String coursesJSON = NetUtil.getCoursesFromServer();
//            if(coursesJSON != null && coursesJSON.length() > 0) {
//                FileOperation.write2File(FileOperation.APP_PATH + "courses.json", coursesJSON, false);
//                return 1;
//            }
//            return 0;
//        }
//
//        @Override
//        protected void onPostExecute(Integer result){
//            if(result == 0){
//                Toast.makeText(ActivityWelcome.this, "Download Courses Failed", Toast.LENGTH_SHORT).show();
//            }else{
//                startMainActivityOrLogin();
//            }
//            progressDialog.dismiss();
//        }
//    }

    Handler handler = new  Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == INIT_FINISHED){
                startMainActivityOrLogin();
            }
        }
    };

    private void startMainActivityOrLogin(){
        if(isLogin){
            //has login,then show AllUI
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }else{
            //not login ,then show LoginUI
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), ActivityLogin.class);
            startActivity(intent);

        }
        finish();
    }
    /**
     * 检查三个科目的知识点文件是否存在
     * @return
     */
    private boolean checkInitFileExist(){
        File file1 = new File(FileOperation.APP_PATH);
        if( !file1.exists()){
            file1.mkdir();
            return false;
        }
        File file = new File(FileOperation.APP_PATH + "courses.json");

        return file.exists();
    }

    @Override
    public void onClick(View v) {
//        switch(v.getId()){
//            case R.id.linearLayout_no_network:
//                refreshView();
//                break;
//        }
    }

    private void refreshView() {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_welcome, menu);
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

//    @Override
//    public void downloadFinished() {
//        startMainActivityOrLogin();
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }
}
