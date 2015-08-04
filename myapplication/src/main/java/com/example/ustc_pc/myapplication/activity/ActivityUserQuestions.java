package com.example.ustc_pc.myapplication.activity;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.db.DBHelper;
import com.example.ustc_pc.myapplication.fragment.ScreenSlidePageFragment;
import com.example.ustc_pc.myapplication.unit.FileOperation;
import com.example.ustc_pc.myapplication.unit.Question;
import com.example.ustc_pc.myapplication.unit.Strings;

import java.util.ArrayList;

public class ActivityUserQuestions extends ActionBarActivity implements ViewPager.OnPageChangeListener{

    private ViewPager mViewPager;
    private TextView mPaperNameTV, mIndexTV, mSumTV;
    private LinearLayout mQuestionNumLL;
    //note
    private RelativeLayout mNoteRL;
    private TextView mNoteTV, mEditNoteTV;

    private int iType = 0;
    private String mLabel = "";

    private ArrayList<Question> mQuestions;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_questions);

//        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLabel = getIntent().getStringExtra("LABEL");
        iType = getIntent().getIntExtra("TYPE", 0);

        switch (iType){
            case ActivityUserBook.TYPE_ERROR_BOOK:
                setTitle(R.string.error_book);
                break;
            case ActivityUserBook.TYPE_FAVORITE_BOOK:
                setTitle(R.string.favorite_book);
                break;
            case ActivityUserBook.TYPE_NOTE_BOOK:
                setTitle(R.string.note_book);
                break;
        }

        initView();
        initData();
    }

    private void initData() {
        GetUserQuestionsAsync getUserQuestionsAsync = new GetUserQuestionsAsync();
        getUserQuestionsAsync.execute(mLabel);
    }

    private void initView() {
        mPaperNameTV = (TextView)findViewById(R.id.textView_error_question_name);
        mQuestionNumLL = (LinearLayout)findViewById(R.id.linearLayout_question_num);
        mIndexTV = (TextView)findViewById(R.id.textView_error_question_index);
        mSumTV = (TextView)findViewById(R.id.textView_error_question_sum);
        mViewPager = (ViewPager)findViewById(R.id.viewPager_error_questions);

        mNoteRL = (RelativeLayout)findViewById(R.id.relativeLayout_note);
        mNoteTV = (TextView)findViewById(R.id.textView_note);
        mEditNoteTV = (TextView)findViewById(R.id.textView_add_edit_note);
        mNoteRL.setVisibility(View.VISIBLE);

        mPaperNameTV.setText(mLabel);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_activity_user_questions, menu);
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
        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mIndexTV.setText(position + 1+"");
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    class GetUserQuestionsAsync extends AsyncTask<String, Integer, ArrayList<Question>>{

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ActivityUserQuestions.this, null, getResources().getString(R.string.loading));
        }

        @Override
        protected ArrayList<Question> doInBackground(String... params) {
            switch (iType){
                case ActivityUserBook.TYPE_ERROR_BOOK:
                    return getErrorQuestion(params[0]);
                case ActivityUserBook.TYPE_NOTE_BOOK:
                    return getNoteQuestion();
                case ActivityUserBook.TYPE_FAVORITE_BOOK:
                    return getFavoriteQuestion(params[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Question> result){
            if(result == null)mQuestions = new ArrayList<>();
            else mQuestions = result;
            mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mPagerAdapter);
            mViewPager.setOnPageChangeListener(ActivityUserQuestions.this);
            mSumTV.setText(mQuestions.size()+"");
            progressDialog.dismiss();
        }

        private ArrayList<Question> getFavoriteQuestion(String label) {
            ArrayList<Question> result;
            DBHelper dbHelper = DBHelper.getInstance(ActivityUserQuestions.this);
            SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.query(Strings.TABLE_NAME_LOG_QUESTIONS, new String[]{"firstKP", "questionID", "isFavorite", "note"}, "firstKP=? and isFavorite=1",
                    new String[]{label}, null, null, null);
            result = new ArrayList<>();
            while(cursor.moveToNext()){
                String questionID = cursor.getString(1);
                String note = cursor.getString(3);
                String fileName = questionID + ".json";
                Question question = FileOperation.getQuestion(FileOperation.APP_PATH + "/" + fileName);
                if(question != null){
                    if(note != null && note.length() > 0)question._strNote = note;
                    result.add(question);
                }
            }
            cursor.close();
            sqLiteDatabase.close();
            dbHelper.destoryInstance();
            return result;
        }

        private ArrayList<Question> getNoteQuestion() {
            ArrayList<Question> result;
            DBHelper dbHelper = DBHelper.getInstance(ActivityUserQuestions.this);
            SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.query(Strings.TABLE_NAME_LOG_QUESTIONS, new String[]{"firstKP", "questionID", "note"}, "note is not null and note!= ''",
                    null, null, null, null);
            result = new ArrayList<>();
            while(cursor.moveToNext()){
                String questionID = cursor.getString(1);
                String note = cursor.getString(2);
                String fileName = questionID + ".json";
                Question question = FileOperation.getQuestion(FileOperation.APP_PATH + "/" + fileName);
                if(question != null){
                    if(note != null && note.length() > 0)question._strNote = note;
                    result.add(question);
                }
            }
            cursor.close();
            sqLiteDatabase.close();
            dbHelper.destoryInstance();

            return result;
        }

        private ArrayList<Question> getErrorQuestion(String label){
            ArrayList<Question> result;
            DBHelper dbHelper = DBHelper.getInstance(ActivityUserQuestions.this);
            SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.query(Strings.TABLE_NAME_LOG_QUESTIONS, new String[]{"firstKP", "questionID", "isCorrect", "note"}, "firstKP=? and isCorrect=0",
                    new String[]{label}, null, null, null);
            result = new ArrayList<>();
            while(cursor.moveToNext()){
                String questionID = cursor.getString(1);
                String note = cursor.getString(3);
                String fileName = questionID + ".json";
                Question question = FileOperation.getQuestion(FileOperation.APP_PATH + "/" + fileName);
                if(question != null){
                    if(note != null && note.length() > 0)question._strNote = note;
                    result.add(question);
                }
            }
            cursor.close();
            sqLiteDatabase.close();
            dbHelper.destoryInstance();

            return result;
        }

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
            Bundle args = new Bundle();
            args.putSerializable("QUESTION", mQuestions.get(position));
            if(iType != ActivityUserBook.TYPE_ERROR_BOOK){
                args.putInt("TYPE", ScreenSlidePageFragment.TYPE_VIEW_NOTE_AND_FAVORITE_QUESTION);
            }else{
                args.putInt("TYPE", ScreenSlidePageFragment.TYPE_ANALYSIS);
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return mQuestions.size();
        }
    }

}
