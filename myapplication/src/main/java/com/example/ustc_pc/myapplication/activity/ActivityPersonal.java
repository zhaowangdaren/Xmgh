package com.example.ustc_pc.myapplication.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.imageView.CircleImageView;
import com.example.ustc_pc.myapplication.net.OkHttpUtil;
import com.example.ustc_pc.myapplication.net.Util;

import java.io.IOException;

public class ActivityPersonal extends ActionBarActivity implements View.OnClickListener{

    RelativeLayout mHeadRL, mSettingRL;
    CircleImageView mHeaderCI;
    TextView mNickNameTV, mAccountNumberTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initData();
    }

    private void initView() {
        mHeadRL = (RelativeLayout)findViewById(R.id.relativeLayout_personal_head);
        mSettingRL = (RelativeLayout)findViewById(R.id.relativeLayout_personal_setting);
        mNickNameTV = (TextView)findViewById(R.id.textView_personal_nick_name);
        mAccountNumberTV = (TextView)findViewById(R.id.textView_personal_account_number);
        mHeadRL.setOnClickListener(this);
        mSettingRL.setOnClickListener(this);
        mHeaderCI = (CircleImageView)findViewById(R.id.imageView_personal_head);
    }

    private void initData() {
        UserSharedPreference userSharedPreference = new UserSharedPreference(this);
        Bitmap bitmap = userSharedPreference.getUserPhotoBitmap();
        if(bitmap != null) mHeaderCI.setImageBitmap(bitmap);
    }

    @Override
    protected void onResume(){
        super.onResume();
        UserSharedPreference userSharedPreference = new UserSharedPreference(this);
        boolean isLogin = userSharedPreference.getIsLogin();
        if( !isLogin ){// logged out已登出
            finish();
        }

        mNickNameTV.setText(userSharedPreference.getStrUserName());
        mAccountNumberTV.setText(userSharedPreference.getAccountNumber());
        Bitmap bitmap = userSharedPreference.getUserPhotoBitmap();
        if(bitmap != null) mHeaderCI.setImageBitmap(bitmap);

        if(Util.isConnect(this) && userSharedPreference.isUserInfoChanged()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpUtil okHttpUtil = new OkHttpUtil();
                    try {
                        okHttpUtil.setPersonInfo(ActivityPersonal.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_activity_personal, menu);
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.relativeLayout_personal_head:
                Intent intent = new Intent();
                intent.setClass(this, ActivityViewPersonal.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.relativeLayout_personal_setting:
                Intent intent1 = new Intent();
                intent1.setClass(this, ActivitySetting.class);
                startActivity(intent1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //boolean
    }
}
