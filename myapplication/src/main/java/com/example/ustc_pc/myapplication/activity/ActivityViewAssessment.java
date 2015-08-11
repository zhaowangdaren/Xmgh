package com.example.ustc_pc.myapplication.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.dao.KPs;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.net.OkHttpUtil;
import com.example.ustc_pc.myapplication.unit.Util;
import com.example.ustc_pc.myapplication.unit.AssessmentScore;

import java.util.ArrayList;
import java.util.List;

public class ActivityViewAssessment extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener{

    private LinearLayout mNoNetLL;
    private LinearLayout mPercentLL;
    private TextView mScoreTV;
    private ListView mDetailLV;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ProgressDialog progressDialog;

    private int mICourseID = 0, mIUserID;

    private List<KPs> mFirstLevelKPses, mAllKPses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_assessment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mICourseID = getIntent().getIntExtra("mICourseID",0);
        mAllKPses = (List<KPs>) getIntent().getSerializableExtra("mAllKPses");
        UserSharedPreference userSharedPreference = new UserSharedPreference(this);
        mIUserID = userSharedPreference.getiUserID();
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
        if(!Util.isConnect(this)){
            mNoNetLL.setVisibility(View.VISIBLE);
        }else {
            progressDialog = ProgressDialog.show(ActivityViewAssessment.this, null, getResources().getString(R.string.loading));
            GetAssessmentScore getAssessmentScore = new GetAssessmentScore();
            getAssessmentScore.execute(mIUserID, mICourseID);
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


    class GetAssessmentScore extends AsyncTask<Integer , Integer, AssessmentScore>{

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected AssessmentScore doInBackground(Integer... integers) {
            int iUserID = integers[0];
            int iCourseID = integers[1];

            mFirstLevelKPses = new ArrayList<>();
            for(KPs kPs : mAllKPses){
                if(kPs.getILevel() == 1)mFirstLevelKPses.add(kPs);
            }

            OkHttpUtil okHttpUtil = new OkHttpUtil();
            AssessmentScore assessmentScore = okHttpUtil.getAssessedScore(iUserID, iCourseID);

            AssessmentScore assessmentScore1Level = new AssessmentScore();
            assessmentScore1Level.setiSumSocre(assessmentScore.getiSumSocre());

            for(AssessmentScore.AssessmentScoreKp assessmentScoreKp : assessmentScore.getAssessmentScoreKps()){
                for(KPs kPs : mFirstLevelKPses){
                    if(assessmentScoreKp.getStrKPID().equals(kPs.getStrKPID())){
                        assessmentScoreKp.setStrKPName(kPs.getStrName());
                        assessmentScore1Level.addkp(assessmentScoreKp);
                    }
                }
            }

            return assessmentScore1Level;
        }

        @Override
        protected void onPostExecute(AssessmentScore result){
            if(result != null) {
                mScoreTV.setText(String.valueOf(result.getiSumSocre()));
                // TODO : Draw a pic for sum score
                /**
                ViewArc viewArc = new ViewArc(ActivityViewAssessment.this, assessmentScore.getAssessmentScoreKps(), assessmentScore._iFinishedArg, 2);
                viewArc.invalidate();
                mPercentLL.removeAllViews();
                mPercentLL.addView(viewArc);
                mPercentLL.getWidth();
                 */
                //set detail list
                ActivityViewAssessmentAdapter activityViewAssessmentAdapter = new ActivityViewAssessmentAdapter(result.getAssessmentScoreKps());
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
        getAssessmentScore.execute(mIUserID, mICourseID);
    }

    public class ActivityViewAssessmentAdapter extends BaseAdapter {

        List<AssessmentScore.AssessmentScoreKp> datas;
        public ActivityViewAssessmentAdapter(List<AssessmentScore.AssessmentScoreKp> datas){
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                convertView = View.inflate(parent.getContext(), R.layout.activity_view_assessment_detail_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.colorIV = (ImageView)convertView.findViewById(R.id.imageView_score_detail_color);
                viewHolder.kpScoreTV = (TextView)convertView.findViewById(R.id.textView_score_detail_kp_score);
                viewHolder.kpNameTV = (TextView)convertView.findViewById(R.id.textView_score_detail_kp);

            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            //TODO : set color for AssessmentScoreKp
//        viewHolder.colorIV.setBackgroundColor(datas.get(position)._iColor);
            viewHolder.kpScoreTV.setText(""+datas.get(position).getiProgress());
            viewHolder.kpNameTV.setText(datas.get(position).getStrKPName());

            convertView.setTag(viewHolder);
            return convertView;
        }

        class ViewHolder {
            ImageView colorIV;
            TextView kpNameTV ,kpScoreTV;
        }
    }
}
