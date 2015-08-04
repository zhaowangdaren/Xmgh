package com.example.ustc_pc.myapplication.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ustc_pc.myapplication.R;

public class ActivityForgetPassword extends ActionBarActivity implements View.OnClickListener{

    EditText mAuthCodeET, mNewPassword;
    Button mSubmitBT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

//        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
    }

    private void initView() {
        mAuthCodeET = (EditText)findViewById(R.id.editText_auth_code);
        mNewPassword = (EditText)findViewById(R.id.editText_new_password);
        mSubmitBT = (Button)findViewById(R.id.button_submit);
        mSubmitBT.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_forget_password, menu);
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
            case R.id.button_submit:
                sumitNewPassword();
        }
    }

    private void sumitNewPassword() {
        String authCode = mAuthCodeET.getText().toString();
        if(authCode == null || authCode.length() <= 0)return;
        String newPassword = mNewPassword.getText().toString();
        if(newPassword == null || newPassword.length() <= 0)return;

        SubmitAsync submitAsync = new SubmitAsync();
        submitAsync.execute(authCode, newPassword);
    }

    class SubmitAsync extends AsyncTask<String, Integer, Integer>{

        @Override
        protected Integer doInBackground(String... params) {
            String authCode = params[0];
            String newPassword = params[1];
            return null;
        }
    }
}
