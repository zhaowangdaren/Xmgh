package com.example.ustc_pc.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.dao.DoneQuestion;
import com.example.ustc_pc.myapplication.dao.KPs;
import com.example.ustc_pc.myapplication.db.DoneQuestionDBHelper;
import com.example.ustc_pc.myapplication.fragment.ShowRecordedQueFragment;
import com.example.ustc_pc.myapplication.unit.QuestionUnmultiSon;
import com.example.ustc_pc.myapplication.unit.UnmultiSonAnalysis;
import com.example.ustc_pc.myapplication.unit.Util;

import java.util.List;

public class ShowRecordedQueActivity extends AppCompatActivity implements ShowRecordedQueFragment.OnFragmentInteractionListener{
    public static  final String ARG_SHOW_QUE_TYPE = "ARG_SHOW_QUE_TYPE";
    public static final int TYPE_ERROR_QUE = 1, TYPE_FAV_QUE = 2, TYPE_NOTE_QUE = 3;

    private int mICourseID;
    private KPs mKps;
    private int mITestType;

    private int mShowType;


    private List<DoneQuestion> mDoneQuestionList;
    private List<QuestionUnmultiSon> mQuestionUnmultiSons;
    private List<UnmultiSonAnalysis> mUnmultiSonAnalysises;

    private int mISumQues = 0;
    //view
    private ViewPager mVP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recorded_que);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mICourseID = getIntent().getIntExtra("mICourseID",0);
        mITestType = getIntent().getIntExtra("mITestType",Util.BASIC_TEST);
        mKps = (KPs) getIntent().getSerializableExtra("mKps");
        mShowType = getIntent().getIntExtra(ARG_SHOW_QUE_TYPE,TYPE_ERROR_QUE);

        mVP = (ViewPager)findViewById(R.id.viewPager_actvity_show_recorded_que);

        GetRecordedQueAsync getRecordedQueAsync = new GetRecordedQueAsync(this);
        //strCourseID, strQueType, strKPID, strShowType
        getRecordedQueAsync.execute(String.valueOf(mICourseID), String.valueOf(mITestType), mKps.getStrKPID(), String.valueOf(mShowType));

    }

    private class QuestionsPagerAdapter extends FragmentStatePagerAdapter {

        public QuestionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = ShowRecordedQueFragment.newInstance(mDoneQuestionList.get(position),
                    mQuestionUnmultiSons.get(position), mUnmultiSonAnalysises.get(position)
                    ,mKps.getStrName(), position, mISumQues);
            return fragment;
        }

        @Override
        public int getCount() {
            return mDoneQuestionList.size();
        }
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class GetRecordedQueAsync extends AsyncTask<String, Integer, Boolean>{

        Context context;
        ProgressDialog progressDialog;

        public GetRecordedQueAsync(Context context) {
            this.context = context;
        }

        @Override
        protected  void onPreExecute(){
            progressDialog = ProgressDialog.show(context, null, getString(R.string.loading));
        }
        @Override
        protected Boolean doInBackground(String... strings) {
            String strCourseID = strings[0];
            String strTestType = strings[1];
            String strKPID = strings[2];
            String strShowType = strings[3];

            DoneQuestionDBHelper doneQuestionDBHelper = DoneQuestionDBHelper.getInstance(context);
            mDoneQuestionList = doneQuestionDBHelper.queryRecordedQue(Integer.valueOf(strCourseID),
                    Integer.valueOf(strTestType), strKPID, Integer.valueOf(strShowType));
            mQuestionUnmultiSons = Util.parseUnmultiSonQueFromFile(mDoneQuestionList);
            mUnmultiSonAnalysises = Util.parseUnmultiSonAnswerFromFile(mDoneQuestionList);
            if(mDoneQuestionList == null ||mUnmultiSonAnalysises == null || mQuestionUnmultiSons == null
                    || mDoneQuestionList.isEmpty() || mUnmultiSonAnalysises.isEmpty()
                    || mQuestionUnmultiSons.isEmpty()) return false;
            return true;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess){
            progressDialog.dismiss();
            if( !isSuccess){
                Toast.makeText(context, getString(R.string.parse_failed), Toast.LENGTH_SHORT).show();
            }else{
                mISumQues = mDoneQuestionList.size();
                QuestionsPagerAdapter questionsPagerAdapter = new QuestionsPagerAdapter(getSupportFragmentManager());
                mVP.setAdapter(questionsPagerAdapter);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_recorded_que, menu);
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
}
