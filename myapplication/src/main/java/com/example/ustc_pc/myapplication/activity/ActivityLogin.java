package com.example.ustc_pc.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.example.ustc_pc.myapplication.CourseActivity;
import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.dao.Course;
import com.example.ustc_pc.myapplication.db.CourseDBHelper;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.net.OkHttpUtil;
import com.example.ustc_pc.myapplication.net.Util;

import java.io.IOException;
import java.util.ArrayList;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener{

    private final int iUNKNOWN_ERROR = -1, iUSERNAME_WRONG = 0, iPASSWORD_WRONG = 1, iLOGIN_SUCCESS = 2;
    EditText mUsernameET, mPasswordET;
    TextView mNoAccountTV, mForgetPasswordTV;
    Button mLoginBT;
    LinearLayout mQQLL, mWeiboLL;

    RelativeLayout mLoadFailedRL;
    ViewAnimator mViewAnimator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        initView();
    }

    private void initView() {
        mViewAnimator = (ViewAnimator)findViewById(R.id.viewAnimator_login);
        View viewLogin = View.inflate(this, R.layout.layout_login, null);
        mViewAnimator.addView(viewLogin,0);
        View viewLoadFailed = View.inflate(this, R.layout.layout_load_failed, null);
        mViewAnimator.addView(viewLoadFailed);


        mUsernameET = (EditText) viewLogin.findViewById(R.id.editText_username_phone_number);
        mPasswordET = (EditText)viewLogin.findViewById(R.id.editText_password);
        mNoAccountTV = (TextView)viewLogin.findViewById(R.id.textView_sigin);
        mLoginBT = (Button) viewLogin.findViewById(R.id.button_phone_login);
        mForgetPasswordTV = (TextView)viewLogin.findViewById(R.id.textView_forget_password);
        mQQLL = (LinearLayout)viewLogin.findViewById(R.id.linearLayout_qq_login);
        mWeiboLL = (LinearLayout)viewLogin.findViewById(R.id.linearLayout_weibo_login);

        mLoadFailedRL = (RelativeLayout)viewLoadFailed.findViewById(R.id.relativeLayout_load_failed);

        mForgetPasswordTV.setOnClickListener(this);
        mNoAccountTV.setOnClickListener(this);
        mLoginBT.setOnClickListener(this);
        mQQLL.setOnClickListener(this);
        mWeiboLL.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_activity_login, menu);
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
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_phone_login:
                login(Util.PHONE_LOGIN);
                break;
            case R.id.textView_sigin:
                sigin();
                break;
            case R.id.textView_forget_password:
                forgetPassword();
                break;
            case R.id.linearLayout_qq_login:
                login(Util.QQ_LOGIN);
                break;
            case R.id.linearLayout_weibo_login:
                login(Util.WEIBO_LOGIN);
                break;
        }
    }

    /**
     *
     * @param iAccountType
     */
    private void login(int iAccountType){
        LoginAsyncTask loginAsyncTask = new LoginAsyncTask(this);
        String strThirdID="";
        String strUserName = "";
        switch (iAccountType){
            case Util.PHONE_LOGIN:
                String strPhoneNumber = mUsernameET.getText().toString();
                String password = mPasswordET.getText().toString();
                if(strPhoneNumber == null || strPhoneNumber.length() < 1 || ! Util.isPhoneNumber(strPhoneNumber)){
                    Toast.makeText(ActivityLogin.this, R.string.error_account, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password == null || password.length() < 6){
                    Toast.makeText(ActivityLogin.this, R.string.error_password, Toast.LENGTH_SHORT).show();
                    return;
                }

                loginAsyncTask.execute(String.valueOf(Util.PHONE_LOGIN), strPhoneNumber, password);
                break;
            case Util.QQ_LOGIN:
                loginAsyncTask.execute(String.valueOf(Util.QQ_LOGIN), strThirdID, strUserName);
                break;
            case Util.WEIBO_LOGIN:
                loginAsyncTask.execute(String.valueOf(Util.WEIBO_LOGIN), strThirdID, strUserName);
                break;
            case Util.WECHAT_LOGIN:
                loginAsyncTask.execute(String.valueOf(Util.WECHAT_LOGIN), strThirdID,strUserName);
        }
    }

    private void forgetPassword() {
        Intent intent = new Intent(this, ActivityForgetPassword.class);
        startActivity(intent);
    }

    /**
     * 注册
     */
    private void sigin() {
        Intent intent = new Intent();
        intent.setClass(this, RegisterActivity.class);
        startActivity(intent);

    }

    class GetCoursesAsync extends AsyncTask<Integer, Integer, Boolean>{

        ProgressDialog progressDialog;
        Context context;
        public GetCoursesAsync(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(context, null,getString(R.string.getting_courses));
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            ArrayList<Course> selectedCourses = null;
            ArrayList<Course> allCourses = null;
            try {
                selectedCourses = okHttpUtil.getSelectedCourses(new UserSharedPreference(context).getiUserID());
                allCourses = okHttpUtil.getAllCoursesInfo();
            } catch (IOException e) {
                Log.e("Error:", "Get Courses AsyncTask"+ e.toString());
                return false;
            }
            if(allCourses.isEmpty() && selectedCourses.isEmpty())return false;
            //insert All Courses into db
            CourseDBHelper courseDBHelper = CourseDBHelper.getInstance(context);
            courseDBHelper.insertCourses(allCourses);
            if(selectedCourses == null || selectedCourses.isEmpty()){//已选科目数位0
                new UserSharedPreference(context).setIsNeverSelectedCourse(true);
            }else{
                new UserSharedPreference(context).setIsNeverSelectedCourse(false);
                courseDBHelper.updateCourses(selectedCourses);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(result){
                boolean isNeverSelected = new UserSharedPreference(context).getIsNeverSelectedCourse();
                if(isNeverSelected){
                    startSelectCourseActivity();
                }else{
                    startMainActivity();
                }
            }else{
                mViewAnimator.showNext();
            }
            progressDialog.dismiss();
        }
    }

    private void startMainActivity(){
        Intent intent = new Intent(this, CourseActivity.class);
        startActivity(intent);
        finish();
    }

    private void startSelectCourseActivity(){
        Intent intent = new Intent(this, SelecteCourseActivity.class);
        startActivity(intent);
        finish();
    }

    class LoginAsyncTask extends AsyncTask<String, Integer, Integer>{

        ProgressDialog progressDialog;
        Context context;
        String params1 ="", params2 = "";

        public LoginAsyncTask(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(context, null,getResources().getString(R.string.logining));
        }
        @Override
        protected Integer doInBackground(String... params) {
            int iLoginType = Integer.valueOf(params[0]);
            params1 = params[1]; params2 = params[2];
            UserSharedPreference userSharedPreference = new UserSharedPreference(context);
            userSharedPreference.setiAccountType(iLoginType);

            if(iLoginType == Util.PHONE_LOGIN){
                return phoneLogin(params[1], params[2]);
            }else{
                return thirdLogin(iLoginType, params[1], params[2]);
            }

        }

        @Override
        protected void onPostExecute(Integer result){
            if(result <= 0){
                Toast.makeText(context, getString(R.string.login_failed),Toast.LENGTH_SHORT).show();
            }else{
                //record user info
                UserSharedPreference userSharedPreference = new UserSharedPreference(context);
                userSharedPreference.setiUserID(result);
                int iAccountType = userSharedPreference.getiAccountType();
                if(iAccountType == Util.PHONE_LOGIN){
                    userSharedPreference.setStrPhoneNumber(params1);
                    userSharedPreference.setPassword(params2);
                }else{
                    userSharedPreference.setStrThirdID(params1);
                    userSharedPreference.setStrUserName(params2);
                }

                //get user selected course
                GetCoursesAsync getCoursesAsync = new GetCoursesAsync(context);
                getCoursesAsync.execute();
            }
            progressDialog.dismiss();

        }

        /**
         *
         * @param phoneNumber
         * @param strPassword
         * @return
         */
        private int phoneLogin(String phoneNumber, String strPassword){
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            try {
                return okHttpUtil.phoneLogin(phoneNumber, strPassword);
            } catch (IOException e) {
                Log.e("Error:", "Phone login "+ e.toString());
            }
            return Util.iNo_USERID;
        }

        /**
         *
         * @param strThirdID
         * @param strUserName
         * @return
         */
        private int thirdLogin(int iAccountType, String strThirdID, String strUserName){
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            try {
                return okHttpUtil.thirdLogin(iAccountType, strThirdID, strUserName);
            } catch (IOException e) {
                Log.e("Error: ", "thirdLogin " + e.toString());
            }
            return Util.iNo_USERID;
        }

    }



}
