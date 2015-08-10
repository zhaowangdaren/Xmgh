package com.example.ustc_pc.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.net.OkHttpUtil;
import com.example.ustc_pc.myapplication.net.Util;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    ViewAnimator mVA;
//    View 1
    Button mNextStepBT;
    EditText mPhoneET;

//    View 2
    Button mSubmitBT;
    TextView  mPhoneTV;
    EditText mSMSAuthET, mPasswordET;


    String mStrPhoneNumber, mStrPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mVA = (ViewAnimator)findViewById(R.id.viewAnimator_register);
        View view1 = View.inflate(this, R.layout.layout_phone_register, null);
        mVA.addView(view1, 0);
        View view2 = View.inflate(this, R.layout.layout_register_input_password, null);
        mVA.addView(view2, 1);

        mNextStepBT = (Button)view1.findViewById(R.id.button_next_step);
        mNextStepBT.setOnClickListener(this);
        mPhoneET = (EditText)view1.findViewById(R.id.editText_phone_register);


        mSubmitBT = (Button)view2.findViewById(R.id.button_register_submit);
        mSubmitBT.setOnClickListener(this);
        mPhoneTV = (TextView)view2.findViewById(R.id.textView_phone_number);
        mSMSAuthET = (EditText)view2.findViewById(R.id.editText_auth_code_register);
        mPasswordET = (EditText)view2.findViewById(R.id.editText_password_register);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_next_step:
                startPhoneCheck();
                break;
            case R.id.button_register_submit:
                startSubmitInfo();
                break;
        }
    }

    private void startSubmitInfo() {
        if(mStrPhoneNumber == null) return;
        mPhoneTV.setText(mStrPhoneNumber);
        String strAuthCode = mSMSAuthET.getText().toString();
        final String strPassword = mPasswordET.getText().toString();
        if(strAuthCode == null || strAuthCode.length()< 4){
            Toast.makeText(this, getString(R.string.please_input_auth_code), Toast.LENGTH_SHORT).show();
            return;
        }
        if(strPassword == null || strPassword.length() <= 0){
            Toast.makeText(this, getString(R.string.please_input_password), Toast.LENGTH_SHORT).show();
            return;
        }

        if(strPassword.length() < 6){
            Toast.makeText(this, getString(R.string.password_length_mismatch_condition),Toast.LENGTH_SHORT).show();
            return;
        }

        if(!Util.isConnect(this)){
            Toast.makeText(this,getString(R.string.no_network_try_again), Toast.LENGTH_SHORT).show();
        }

        mStrPassword = strPassword;

//        AVOSCloud.verifySMSCodeInBackground(strAuthCode,mStrPhoneNumber,new AVMobilePhoneVerifyCallback() {
//            @Override
//            public void done(AVException e) {
//                RegistAsyncTask registAsyncTask = new RegistAsyncTask(RegisterActivity.this);
//                registAsyncTask.execute(mStrPhoneNumber, strPassword);
//            }
//        });
        RegistImageViewAsyncTask registAsyncTask = new RegistImageViewAsyncTask(RegisterActivity.this);
        registAsyncTask.execute(mStrPhoneNumber, strPassword);
    }

    private void startPhoneCheck() {
        Toast.makeText(this, "Start Phone Check", Toast.LENGTH_SHORT).show();
        String strPhone = mPhoneET.getText().toString();
        if(strPhone == null || strPhone.length()<=0){
            Toast.makeText(this, R.string.please_input_phone_numer, Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Util.isPhoneNumber(strPhone)){
            Toast.makeText(this, getString(R.string.phone_number_mismatch_condition), Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Util.isConnect(this)){
            Toast.makeText(this,getString(R.string.no_network_try_again), Toast.LENGTH_SHORT).show();
            return;
        }
        mStrPhoneNumber = strPhone;
        PhoneCheckImageViewAsync phoneCheckAsync = new PhoneCheckImageViewAsync(this);
        phoneCheckAsync.execute(mStrPhoneNumber);
    }

    class RegistImageViewAsyncTask extends AsyncTask<String, Integer, Integer> {

        ProgressDialog progressDialog;
        Context context;
        public RegistImageViewAsyncTask(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(context,null,getString(R.string.registting));
        }
        @Override
        protected Integer doInBackground(String... params) {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            int iUserID = -1;
            try {
               iUserID = okHttpUtil.registerByPhone(params[0], params[1]);
            }catch (IOException e){
                e.printStackTrace();
                return -1;
            }
            return iUserID;
        }

        @Override
        protected void onPostExecute(Integer result){
            if(result <= 0){//Error
                new AlertDialog.Builder(context)
                        .setTitle(R.string.hint)
                        .setMessage(R.string.network_error)
                        .show();
            }else{
                UserSharedPreference userSharedPreference = new UserSharedPreference(context);
                userSharedPreference.setiUserID(result);
                userSharedPreference.setStrPhoneNumber(mStrPhoneNumber);
                userSharedPreference.setPassword(mStrPassword);
            }
            progressDialog.dismiss();
        }
    }

    class PhoneCheckImageViewAsync extends AsyncTask<String, Integer, Boolean> {

        ProgressDialog progressDialog;
        Context context;
        public PhoneCheckImageViewAsync(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(context,null,getString(R.string.loading));
        }
        @Override
        protected Boolean doInBackground(String... params) {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            try {
                boolean result = okHttpUtil.hasRegisted(params[0]);
                if(!result){//unregistered
                    //send sms auth code
                    AVOSCloud.requestSMSCode(params[0], getResources().getString(R.string.app_name), getResources().getString(R.string.regist), 10);
                }
                return result;
            }catch (IOException e){
                e.printStackTrace();
                Log.i("Network IOException","PhoneCheck");
            } catch (AVException e) {
                e.printStackTrace();
                Log.e("AVOSCloud:", "Ask for send sms error");
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result){
            progressDialog.dismiss();
            if(result){//has registered
                new AlertDialog.Builder(context)
                        .setTitle(R.string.hint)
                        .setMessage(R.string.has_registered)
                        .show();
            }else{
                mPhoneTV.setText(mStrPhoneNumber);
                mVA.showNext();
            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_register, menu);
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


}
