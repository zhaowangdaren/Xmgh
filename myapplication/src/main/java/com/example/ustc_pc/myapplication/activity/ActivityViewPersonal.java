package com.example.ustc_pc.myapplication.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.db.DoneQuestionDBHelper;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.imageView.CircleImageView;
import com.example.ustc_pc.myapplication.net.Util;

public class ActivityViewPersonal extends AppCompatActivity implements View.OnClickListener{

    CircleImageView mHeadIV;
    TextView mNickNameTV, mAccountNumberTV, mGenderTV, mEmailTV, mSourceCollegeTV, mSourceMajorTV, mAboutMeTV,
        mFirstTargetCollegeTV, mSecondTargetCollegeTV, mFirstTargetMajorTV, mSecondTargetMajorTV, mAcceptedCollegeTV, mAcceptedMajorTV;
    private Button mLogOutBT;

    RelativeLayout mNickNameRL, mAccountNumberRL, mGenderRL, mEmailRL, mAboutMeRL, mSourceCollegeRL, mSourceMajorRL,
        mFirstTCRL, mFirstTMRL, mSecondTCRL, mSecondTMRL, mAcceptedCRL, mAcceptedMRL;

    UserSharedPreference mUserSharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_personal);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();

    }

    @Override
    protected void onResume(){
        super.onResume();
        initData();
    }

    private void initData() {
        mUserSharedPreference = new UserSharedPreference(this);
        mNickNameTV.setText(mUserSharedPreference.getStrUserName());
        mAccountNumberTV.setText(mUserSharedPreference.getAccountNumber());
        int iGender = mUserSharedPreference.getiGender();
        String strGender;
        switch (iGender){
            case Util.WOMAN:
                strGender = getString(R.string.woman);
                break;
            case Util.MAN:
                strGender = getString(R.string.man);
                break;
            default: strGender = "";
        }
        mGenderTV.setText(strGender);
        mEmailTV.setText(mUserSharedPreference.getStrEmail());
        mSourceCollegeTV.setText(mUserSharedPreference.getStrSourceCollege());
        mSourceMajorTV.setText(mUserSharedPreference.getStrSourceMajor());
        mFirstTargetCollegeTV.setText(mUserSharedPreference.getStrFirstTargetCollege());
        mFirstTargetMajorTV.setText(mUserSharedPreference.getStrFirstTargetMajor());
        mSecondTargetCollegeTV.setText(mUserSharedPreference.getStrSecondTargetCollege());
        mSecondTargetMajorTV.setText(mUserSharedPreference.getStrSecondTargetMajor());
        mAcceptedCollegeTV .setText(mUserSharedPreference.getStrAcceptedCollege());
        mAcceptedMajorTV.setText(mUserSharedPreference.getStrAcceptedMajor());

        Bitmap bitmap = mUserSharedPreference.getUserPhotoBitmap();
        if(bitmap != null)mHeadIV.setImageBitmap(bitmap);
    }

    private void initView() {
        mNickNameRL = (RelativeLayout)findViewById(R.id.relativeLayout_nick_name);
        mAccountNumberRL = (RelativeLayout)findViewById(R.id.relativeLayout_account_number);
        mGenderRL = (RelativeLayout)findViewById(R.id.relativeLayout_gender);
        mEmailRL = (RelativeLayout)findViewById(R.id.relativeLayout_email);
        mAboutMeRL = (RelativeLayout)findViewById(R.id.relativeLayout_about_me);
        mSourceCollegeRL = (RelativeLayout)findViewById(R.id.relativeLayout_source_college);
        mSourceMajorRL = (RelativeLayout)findViewById(R.id.relativeLayout_source_major);
        mFirstTCRL = (RelativeLayout)findViewById(R.id.relativeLayout_first_target_college);
        mFirstTMRL = (RelativeLayout)findViewById(R.id.relativeLayout_first_target_major);
        mSecondTCRL = (RelativeLayout)findViewById(R.id.relativeLayout_second_target_college);
        mSecondTMRL = (RelativeLayout)findViewById(R.id.relativeLayout_second_target_major);
        mAcceptedCRL = (RelativeLayout)findViewById(R.id.relativeLayout_accepted_college);
        mAcceptedMRL = (RelativeLayout)findViewById(R.id.relativeLayout_accepted_major);



        mHeadIV = (CircleImageView)findViewById(R.id.imageView_personal_head);
        mNickNameTV = (TextView)findViewById(R.id.textView_nick_name);
        mAccountNumberTV = (TextView)findViewById(R.id.textView_account_number);
        mGenderTV = (TextView)findViewById(R.id.textView_gender);
        mEmailTV = (TextView)findViewById(R.id.textView_email);
        mAboutMeTV = (TextView)findViewById(R.id.textView_about_me);
        mSourceCollegeTV = (TextView)findViewById(R.id.textView_source_college);
        mSourceMajorTV = (TextView)findViewById(R.id.textView_source_major);
        mFirstTargetCollegeTV = (TextView)findViewById(R.id.textView_first_target_college);
        mFirstTargetMajorTV = (TextView)findViewById(R.id.textView_first_target_major);
        mSecondTargetCollegeTV = (TextView)findViewById(R.id.textView_second_target_college);
        mSecondTargetMajorTV = (TextView)findViewById(R.id.textView_second_target_major);
        mAcceptedCollegeTV = (TextView)findViewById(R.id.textView_accepted_college);
        mAcceptedMajorTV = (TextView)findViewById(R.id.textView_accepted_major);

        mLogOutBT = (Button)findViewById(R.id.button_log_out);

        mHeadIV.setOnClickListener(this);
        mNickNameRL.setOnClickListener(this);
        mAccountNumberRL.setOnClickListener(this);
        mGenderRL.setOnClickListener(this);
        mEmailRL.setOnClickListener(this);
        mAboutMeRL.setOnClickListener(this);
        mSourceCollegeRL.setOnClickListener(this);
        mSourceMajorRL.setOnClickListener(this);
        mFirstTCRL.setOnClickListener(this);
        mFirstTMRL.setOnClickListener(this);
        mSecondTCRL.setOnClickListener(this);
        mSecondTMRL.setOnClickListener(this);
        mAcceptedCRL.setOnClickListener(this);
        mAcceptedMRL.setOnClickListener(this);
        mLogOutBT.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_activity_view_personal, menu);
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

        if(id== android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_log_out:
                logout();
                break;
            case R.id.imageView_personal_head:
                showChangeHeaderImage();
                break;
            case R.id.relativeLayout_nick_name:
                showEditDialog(getResources().getString(R.string.nick_name), 1, mNickNameTV.getText().toString());
                break;
            case R.id.relativeLayout_gender:
                showEditDialog(getResources().getString(R.string.gender), 3,mGenderTV.getText().toString());
                break;
            case R.id.relativeLayout_email:
                showEditDialog(getResources().getString(R.string.email), 4, mEmailTV.getText().toString());
                break;
            case R.id.relativeLayout_about_me:
                showEditDialog(getResources().getString(R.string.about_me), 5, mAboutMeTV.getText().toString());
                break;
            case R.id.relativeLayout_source_college:
                showEditDialog(getResources().getString(R.string.source_college), 6, mSourceCollegeTV.getText().toString());
                break;
            case R.id.relativeLayout_source_major:
                showEditDialog(getResources().getString(R.string.source_major), 7, mSourceMajorTV.getText().toString());
                break;
            case R.id.relativeLayout_first_target_college:
                showEditDialog(getResources().getString(R.string.first_target_college), 8, mFirstTargetCollegeTV.getText().toString());
                break;
            case R.id.relativeLayout_first_target_major:
                showEditDialog(getResources().getString(R.string.first_target_major), 9, mFirstTargetMajorTV.getText().toString());
                break;
            case R.id.relativeLayout_second_target_college:
                showEditDialog(getResources().getString(R.string.second_target_college), 10, mSecondTargetCollegeTV.getText().toString());
                break;
            case R.id.relativeLayout_second_target_major:
                showEditDialog(getResources().getString(R.string.second_target_major), 11, mSecondTargetMajorTV.getText().toString());
                break;
            case R.id.relativeLayout_accepted_college:
                showEditDialog(getResources().getString(R.string.accepted_college), 12, mAcceptedCollegeTV.getText().toString());
                break;
            case R.id.relativeLayout_accepted_major:
                showEditDialog(getResources().getString(R.string.accepted_major), 13, mAcceptedMajorTV.getText().toString());
                break;
        }
    }

    public static final int CHANGE_HEADER_IMAGE = 10;
    private void showChangeHeaderImage() {
        Intent intent = new Intent();
        intent.setClass(this, ActivitySelectImage.class);
        startActivityForResult(intent, CHANGE_HEADER_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == 1) return;
        switch (requestCode){
            case CHANGE_HEADER_IMAGE:
                if(data != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    mHeadIV.setImageBitmap(bitmap);


                }
                break;
        }
    }


    private void logout() {
        //clear user info
        ClearUserInfoAsyncTask clearUserInfoAsyncTask = new ClearUserInfoAsyncTask(this);
        clearUserInfoAsyncTask.execute();
    }

    private void showEditDialog(String title, int type, final String defaultValue){
        View view = null;
        final int fType =type;
        if(type == 3){
            view = View.inflate(this, R.layout.dialog_checkbox_gender, null);
            final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup_edit_gender);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title)
                    .setView(view);

            if(defaultValue.equals(getResources().getString(R.string.woman))){
                radioGroup.check(R.id.radioButton_woman);
            }else{
                radioGroup.check(R.id.radioButton_man);
            }
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    int iDefaultValue = defaultValue.equals(getString(R.string.woman))?0:1;
                    int iGender = (checkedId == R.id.radioButton_woman) ? 0 : 1;
                    if(iDefaultValue != iGender){
                        setUserInfo(fType, iGender);
                        mUserSharedPreference.setIsUserInfoChanged(true);
                    }

                }
            });
            builder.setPositiveButton(R.string.sure,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            })
                    .setNegativeButton(R.string.cancle,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();

        }else{
            view = View.inflate(this, R.layout.dialog_edittext, null);
            final EditText editText = (EditText)view.findViewById(R.id.editText);
            editText.setText(defaultValue);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setView(view);

            builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String value = editText.getText().toString();
                    if(value != null && !value.equals(defaultValue)) {
                        setUserInfo(fType, value);
                        mUserSharedPreference.setIsUserInfoChanged(true);
                    }
                }
            });
            //builder.setCancelable(true);
            builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }

    }

    private void setUserInfo(int fType, int value) {
        if(fType == 3){//gender
            mUserSharedPreference.setiGender(value);
            switch (value){
                case Util.WOMAN:
                    mGenderTV.setText(R.string.woman);
                    break;
                case Util.MAN:
                    mGenderTV.setText(R.string.man);
                    break;
            }
        }
    }

    private void setUserInfo(int type, String value){
        switch (type){
            case 1:
                mUserSharedPreference.setStrUserName(value);
                mNickNameTV.setText(value);
                break;
            case 4:
                mUserSharedPreference.setStrEmail(value);
                mEmailTV.setText(value);
                break;
            case 5:
                mUserSharedPreference.setStrAboutMe(value);
                mAboutMeTV.setText(value);
                break;
            case 6:
                mUserSharedPreference.setStrSourceCollege(value);
                mSourceCollegeTV.setText(value);
                break;
            case 7:
                mUserSharedPreference.setStrSourceMajor(value);
                mSourceMajorTV.setText(value);
                break;
            case 8:
                mUserSharedPreference.setStrFirstTargetCollege(value);
                mFirstTargetCollegeTV.setText(value);
                break;
            case 9:
                mUserSharedPreference.setStrFirstTargetMajor(value);
                mFirstTargetMajorTV.setText(value);
                break;
            case 10:
                mUserSharedPreference.setStrSecondTargetCollege(value);
                mSecondTargetCollegeTV.setText(value);
                break;
            case 11:
                mUserSharedPreference.setStrSecondTargetMajor(value);
                mSecondTargetMajorTV.setText(value);
                break;
            case 12:
                mUserSharedPreference.setStrAcceptedCollege(value);
                mAcceptedCollegeTV.setText(value);
                break;
            case 13:
                mUserSharedPreference.setStrAcceptedMajor(value);
                mAcceptedMajorTV.setText(value);
                break;
        }
    }

    class ClearUserInfoAsyncTask extends AsyncTask<String, Integer, Integer>{

        private Context context;
        ProgressDialog progressDialog;
        public ClearUserInfoAsyncTask(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(ActivityViewPersonal.this, null, getResources().getString(R.string.logoutting));
        }

        @Override
        protected Integer doInBackground(String... params) {
            UserSharedPreference userSharedPreference = new UserSharedPreference(context);
            int iUserID = userSharedPreference.getiUserID();
            userSharedPreference.clear();

            //clear user paper and question info
            DoneQuestionDBHelper doneQuestionDBHelper = DoneQuestionDBHelper.getInstance(context);
            doneQuestionDBHelper.deleteAll(iUserID);
            return null;
        }

        @Override
        protected void onPostExecute(Integer result){
            progressDialog.dismiss();

            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }



}
