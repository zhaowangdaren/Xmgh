package com.example.ustc_pc.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVOSCloud;
import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.net.OkHttpUtil;
import com.example.ustc_pc.myapplication.unit.Util;

import java.io.IOException;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener{



    ViewAnimator mVA;

    //View phone
    View mPhoneView;
    EditText mPhoneNumET;
    Button mNextStepBT;

    //View reset password
    View mResetPassView;
    EditText mAuthCodeET, mNewPassword;
    Button mSubmitBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
    }

    private void initView() {
        mVA = (ViewAnimator) findViewById(R.id.viewAnimator_forget_password);
        mPhoneView = View.inflate(this, R.layout.layout_phone_register, null);
        mResetPassView = View.inflate(this, R.layout.layout_reset_password, null);

        mVA.addView(mPhoneView, 0);
        mVA.addView(mResetPassView, 0);

        //View phone
        mPhoneNumET = (EditText) mPhoneView.findViewById(R.id.editText_phone_register);
        mNextStepBT = (Button) mPhoneView.findViewById(R.id.button_next_step);
        mNextStepBT.setOnClickListener(this);

        //View reset password
        mAuthCodeET = (EditText)mResetPassView.findViewById(R.id.editText_auth_code);
        mNewPassword = (EditText)mResetPassView.findViewById(R.id.editText_new_password);
        mSubmitBT = (Button)mResetPassView.findViewById(R.id.button_submit);
        mSubmitBT.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_activity_forget_password, menu);
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
        switch (v.getId()){
            case R.id.button_next_step:
                sendAuthCodeSms();
                break;
            case R.id.button_submit:
                sumitNewPassword();
                break;
        }
    }

    private String mStrPhoneNum;
    private void sendAuthCodeSms() {
        String strPhone = mPhoneNumET.getText().toString();
        if(strPhone == null || strPhone.length() < 11 || !Util.isPhoneNumber(strPhone)){
            Toast.makeText(this, R.string.wrong_phone_number, Toast.LENGTH_SHORT).show();
            return;
        }

        mStrPhoneNum = strPhone;
        if(Util.isConnect(this)){
            PhoneCheckAsync phoneCheckAsync = new PhoneCheckAsync(this);
            phoneCheckAsync.execute(strPhone);
        }else{
            Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }
    }


    class PhoneCheckAsync extends AsyncTask<String, Integer, Boolean> {

        ProgressDialog progressDialog;
        Context context;
        public PhoneCheckAsync(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(context,null,getString(R.string.loading));
        }
        @Override
        protected Boolean doInBackground(String... params) {
            String strPhone = params[0];
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            try {
                boolean result = okHttpUtil.hasRegisted(strPhone);
                if(result){//registered
                    //send sms auth code
                    AVOSCloud.requestSMSCode(strPhone, getResources().getString(R.string.app_name), getResources().getString(R.string.regist), 10);
                }
                return result;
            }catch (IOException e){
                e.printStackTrace();
                Log.i("Network IOException", "PhoneCheck");
            } catch (AVException e) {
                e.printStackTrace();
                Log.e("AVOSCloud:", "Ask for send sms error");
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result){
            progressDialog.dismiss();
            if(!result){//Unregistered
                new AlertDialog.Builder(context)
                        .setTitle(R.string.hint)
                        .setMessage(R.string.unregistered)
                        .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }else{
                mVA.showNext();
            }
        }
    }

    private void sumitNewPassword() {
        String authCode = mAuthCodeET.getText().toString();
        if(authCode == null || authCode.length() < 4){
            Toast.makeText(this, R.string.error_checkNumber, Toast.LENGTH_SHORT).show();
            return;
        }
        final String newPassword = mNewPassword.getText().toString();
        if(newPassword == null || newPassword.length() < 6){
            Toast.makeText(this, R.string.wrong_password, Toast.LENGTH_SHORT).show();
            return;
        }

        if(!Util.isConnect(this)){
            Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }

        AVOSCloud.verifySMSCodeInBackground(authCode, mStrPhoneNum, new AVMobilePhoneVerifyCallback() {
            @Override
            public void done(AVException e) {
                SubmitAsync registAsyncTask = new SubmitAsync(ForgetPasswordActivity.this);
                registAsyncTask.execute(mStrPhoneNum, newPassword);
            }
        });

    }


    class SubmitAsync extends AsyncTask<String, Integer, Boolean>{
        Context context;
        ProgressDialog progressDialog;
        public SubmitAsync(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(context, null, getString(R.string.loading));
        }
        @Override
        protected Boolean doInBackground(String... params) {
            String authCode = params[0];
            String newPassword = params[1];
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            return okHttpUtil.resetPassword(authCode, newPassword);
        }

        @Override
        protected void onPostExecute(Boolean isSuccess){
            if(isSuccess){
                Toast.makeText(context, R.string.reset_password_success, Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(context, R.string.reset_password_failed, Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        }
    }
}
