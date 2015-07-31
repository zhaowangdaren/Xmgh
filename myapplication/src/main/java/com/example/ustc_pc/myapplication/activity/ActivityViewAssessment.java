package com.example.ustc_pc.myapplication.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.adapter.ActivityViewAssessmentAdapter;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.net.NetUtil;
import com.example.ustc_pc.myapplication.unit.AssessmentScore;
import com.example.ustc_pc.myapplication.unit.FileOperation;
import com.example.ustc_pc.myapplication.viewUnit.ViewArc;

public class ActivityViewAssessment extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener{

    private LinearLayout mNoNetLL;
    private LinearLayout mPercentLL;
    private TextView mScoreTV;
    private ListView mDetailLV;

    private String _accountNumberStr = "";

    private SwipeRefreshLayout swipeRefreshLayout;

    private ProgressDialog progressDialog;

    private int iCourseID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_assessment);

        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UserSharedPreference userSharedPreference = new UserSharedPreference(this);
        _accountNumberStr = userSharedPreference.getAccountNumber();
        initView();
        initData();
    }

    private void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(R.color.offical_green);
        mPercentLL =(LinearLayout)findViewById(R.id.linearLayout_score_percent);
        mScoreTV = (TextView)findViewById(R.id.textView_score);
        mDetailLV = (ListView)findViewById(R.id.listView_score_detail);

        mNoNetLL = (LinearLayout)findViewById(R.id.linearLayout_no_network);
        mNoNetLL.setOnClickListener(this);
    }

    private void initData(){
        iCourseID = getIntent().getIntExtra("COURSE_ID",1);
        if(!NetUtil.isConnect(this)){
            mNoNetLL.setVisibility(View.VISIBLE);
        }else {
            progressDialog = ProgressDialog.show(ActivityViewAssessment.this, null, getResources().getString(R.string.loading));
            GetAssessmentScore getAssessmentScore = new GetAssessmentScore();
            getAssessmentScore.execute(_accountNumberStr);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linearLayout_no_network:
                onRefresh();
                break;
        }
    }


    class GetAssessmentScore extends AsyncTask<String , Integer, Object>{

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Object doInBackground(String... params) {
            AssessmentScore assessmentScore = NetUtil.getAssessmentScore(params[0],iCourseID);
            if(assessmentScore != null) {
                //set color
                int size = assessmentScore._arrayDoneKps.size();
                int[] colorsR = FileOperation.randomCommon(0, 255, size);
                int[] colorsG = FileOperation.randomCommon(0, 255, size);
                int[] colorsB = FileOperation.randomCommon(0, 255, size);
                for (int i = 0; i < size; i++) {
                    assessmentScore._arrayDoneKps.get(i)._iColor = Color.argb(255, colorsR[i], colorsG[i], colorsB[i]);
                }

                //set arg
                int averageArg = 360 / assessmentScore._iFirstKpNum;
                assessmentScore._iFinishedArg = averageArg * assessmentScore._arrayDoneKps.size();
                assessmentScore._iUnfinishedArg = 360 - assessmentScore._iFinishedArg;
                return assessmentScore;
            }else{
                return null;
            }
        }

        @Override
        protected void onPostExecute(Object result){
            AssessmentScore assessmentScore = (AssessmentScore)result;
            if(assessmentScore != null) {
                mScoreTV.setText(String.valueOf(assessmentScore._iSumScore));
                ViewArc viewArc = new ViewArc(ActivityViewAssessment.this, assessmentScore._arrayDoneKps, assessmentScore._iFinishedArg, 2);
                viewArc.invalidate();
                mPercentLL.removeAllViews();
                mPercentLL.addView(viewArc);
                mPercentLL.getWidth();
                //set detail list
                ActivityViewAssessmentAdapter activityViewAssessmentAdapter = new ActivityViewAssessmentAdapter(assessmentScore._arrayDoneKps);
                mDetailLV.setAdapter(activityViewAssessmentAdapter);

                mNoNetLL.setVisibility(View.GONE);
            }else{
                mNoNetLL.setVisibility(View.VISIBLE);
            }
            swipeRefreshLayout.setRefreshing(false);

            progressDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_activity_assessment, menu);
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
    public void onRefresh() {
        GetAssessmentScore getAssessmentScore = new GetAssessmentScore();
        getAssessmentScore.execute(_accountNumberStr);
    }
}
