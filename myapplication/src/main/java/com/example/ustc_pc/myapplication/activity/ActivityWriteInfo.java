package com.example.ustc_pc.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.ustc_pc.myapplication.MainActivity;
import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.net.NetUtil;
import com.example.ustc_pc.myapplication.viewUnit.LineEditText;

import static android.view.View.OnFocusChangeListener;

public class ActivityWriteInfo extends ActionBarActivity implements View.OnClickListener, OnFocusChangeListener{


    RadioGroup mGenderRG, mStudentTypeRG;
    RadioButton mManRB;
//    DatePicker mBirthDP;
    LineEditText mNickNameET, mRealNameET, mEmailET, mSourceCollegeET, mSourceMajorET, m1TargetCollegeET
            ,m1TargetMajorET, m2TargetCollegeET, m2TargetMajorET;

    Button mSubmitBT;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_info);
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView() {
        mNickNameET = (LineEditText)findViewById(R.id.editText_nick_name);
        mNickNameET.setOnFocusChangeListener(this);
        mRealNameET = (LineEditText)findViewById(R.id.editText_real_name);
        mRealNameET.setOnFocusChangeListener(this);
        mGenderRG = (RadioGroup)findViewById(R.id.radioGroup_select_gender);
        mManRB = (RadioButton)findViewById(R.id.radioButton_man);
        mManRB.setChecked(true);

        mStudentTypeRG = (RadioGroup)findViewById(R.id.radioGroup_select_student_type);
        mEmailET = (LineEditText)findViewById(R.id.editText_email_regist);
        mEmailET.setOnFocusChangeListener(this);
        mSourceCollegeET = (LineEditText)findViewById(R.id.editText_source_college);
        mSourceCollegeET.setOnFocusChangeListener(this);
        mSourceMajorET = (LineEditText)findViewById(R.id.editText_source_major);
        mSourceMajorET.setOnFocusChangeListener(this);
        m1TargetCollegeET = (LineEditText)findViewById(R.id.editText_first_target_college);
        m1TargetCollegeET.setOnFocusChangeListener(this);
        m1TargetMajorET = (LineEditText)findViewById(R.id.editText_first_target_major);
        m1TargetMajorET.setOnFocusChangeListener(this);
        m2TargetCollegeET = (LineEditText)findViewById(R.id.editText_second_target_college);
        m2TargetCollegeET.setOnFocusChangeListener(this);
        m2TargetMajorET = (LineEditText)findViewById(R.id.editText_second_target_major);
        m2TargetMajorET.setOnFocusChangeListener(this);

        mSubmitBT = (Button)findViewById(R.id.button_submit_info);
        mSubmitBT.setOnClickListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        LineEditText lineEditText = (LineEditText)v;
        if(hasFocus)lineEditText.setFocus();
        else lineEditText.setUnfocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_write_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_skip:
                startMainActivity();
                finish();
                return true;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_submit_info:
                submitInfo();
                break;
        }
    }

    private void submitInfo() {
        String nickName = mNickNameET.getText().toString();
        if(nickName == null)nickName = "";

        String realName = mRealNameET.getText().toString();
        if(realName == null)realName = "";

        int iGender = 1;
        switch (mGenderRG.getCheckedRadioButtonId()){
            case R.id.radioButton_woman:
                iGender=0;
                break;
            case R.id.radioButton_man:
                iGender = 1;
                break;
        }
        String email = mEmailET.getText().toString();
        if(email == null)email="";
        int iUserType = 0;
        switch (mStudentTypeRG.getCheckedRadioButtonId()){
            case R.id.radioButton_yinjie_regist:
                iUserType = 0;
                break;
            case R.id.radioButton_wangjie_regist:
                iUserType = 1;
                break;
        }
        String sourceC = mSourceCollegeET.getText().toString();
        if(sourceC == null)sourceC = "";
        String sourceM = mSourceMajorET.getText().toString();
        if(sourceM == null)sourceM = "";
        String fTargetC = m1TargetCollegeET.getText().toString();
        if(fTargetC == null)fTargetC = "";
        String fTargetM = m1TargetMajorET.getText().toString();
        if(fTargetM == null)fTargetM = "";
        String sTargetC = m2TargetCollegeET.getText().toString();
        if(sTargetC == null)sTargetC = "";
        String sTargetM = m2TargetMajorET.getText().toString();
        if(sTargetM == null)sTargetM = "";
        UploadingAsyncTask uploadingAsyncTask = new UploadingAsyncTask();
        uploadingAsyncTask.execute(nickName, realName, String.valueOf(iGender), email, String.valueOf(iUserType), sourceC, sourceM, fTargetC, fTargetM,
                sTargetC, sTargetM);
    }



    class UploadingAsyncTask extends AsyncTask<String, Integer, Integer>{

        UserSharedPreference userSharedPreference ;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(ActivityWriteInfo.this, null,getResources().getString(R.string.loading));
            userSharedPreference = new UserSharedPreference(ActivityWriteInfo.this);
        }
        @Override
        protected Integer doInBackground(String... params) {
            String nickName = params[0];
            if(nickName != null && nickName.length() > 0){
                userSharedPreference.setStrUserName(nickName);
            }
            String realName = params[1];
            if(realName != null && realName.length() > 0){
                userSharedPreference.setRealName(realName);
            }
            String strGender = params[2];
            userSharedPreference.setGender(Integer.valueOf(strGender));

            String email = params[3];
            if(email != null && email.length() > 0){
                userSharedPreference.setEmail(email);
            }
            String userType = params[4];
            userSharedPreference.setUserType(Integer.valueOf(userType));

            String sourceC = params[5];
            if(sourceC != null && sourceC.length() > 0 ){
                userSharedPreference.setSourceCollege(sourceC);
            }
            String sourceM = params[6];
            if(sourceM != null && sourceM.length() > 0){
                userSharedPreference.setSourceMajor(sourceM);
            }
            String fTargetC = params[7];
            if(fTargetC != null && fTargetC.length() > 0){
                userSharedPreference.setFirstTC(fTargetC);
            }
            String fTargetM = params[8];
            if(fTargetM != null && fTargetM.length() > 0){
                userSharedPreference.setFirstTM(fTargetM);
            }

            String sTargetC = params[9];
            if(fTargetC != null && fTargetC.length() > 0){
                userSharedPreference.setSecondTC(sTargetC);
            }
            String sTargetM = params[10];
            if(fTargetM != null && fTargetM.length() > 0){
                userSharedPreference.setSecondTM(sTargetM);
            }
            int result = NetUtil.uploadingUserInfo(ActivityWriteInfo.this);
            return result;
        }

        @Override
        protected void onPostExecute(Integer result){
            UserSharedPreference userSharedPreference = new UserSharedPreference(ActivityWriteInfo.this);
            if(result == 1){
                //upload finished , then set user info unchanged
                userSharedPreference.setIsUserInfoChanged(false);
                Toast.makeText(ActivityWriteInfo.this, "Wrtie Info success!",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ActivityWriteInfo.this, "Wrtie Info failed!",Toast.LENGTH_SHORT).show();
                //userSharedPreference.setIsUploading(false);
            }
            progressDialog.dismiss();
            startMainActivity();
        }
    }

    private void startMainActivity(){
        Intent intent  = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
