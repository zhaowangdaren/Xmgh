package com.example.ustc_pc.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.dao.DoneQuestion;
import com.example.ustc_pc.myapplication.fragment.BasicAnalysisFragment;
import com.example.ustc_pc.myapplication.unit.Answer;
import com.example.ustc_pc.myapplication.unit.QuestionNew;

import java.util.List;

public class ShowAnalysisActivity extends AppCompatActivity {

    List<DoneQuestion> mDoneQuestions;
    List<Answer> mAnswers;
    List<QuestionNew> mQuestionNews;
    String mStrKPName;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_analysis);

        Intent intent = getIntent();
        mDoneQuestions = (List<DoneQuestion>) intent.getSerializableExtra("mDoneQuestions");
        mAnswers = (List<Answer>) intent.getSerializableExtra("mAnswers");
        mQuestionNews = (List<QuestionNew>)intent.getSerializableExtra("mQuestions");
        mStrKPName = intent.getStringExtra("mStrKPName");

        mViewPager = (ViewPager)findViewById(R.id.viewPager_actvity_show_analysis);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_analysis, menu);
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

    private class QuestionPagerAdapter extends FragmentStatePagerAdapter{

        public QuestionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = BasicAnalysisFragment.newInstance(
                    mDoneQuestions.get(position)
                    ,mQuestionNews.get(position)
                    ,mAnswers.get(position)
                    ,position
                    ,mStrKPName
                    ,getCount());
            return fragment;
        }

        @Override
        public int getCount() {
            return mDoneQuestions.size();
        }
    }
}
