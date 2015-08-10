package com.example.ustc_pc.myapplication.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.dao.DoneQuestion;
import com.example.ustc_pc.myapplication.db.DoneQuestionDBHelper;
import com.example.ustc_pc.myapplication.fragment.BasicAnalysisFragment;
import com.example.ustc_pc.myapplication.unit.QuestionUnmultiSon;
import com.example.ustc_pc.myapplication.unit.UnmultiSonAnslysis;

import java.util.List;

public class ShowAnalysisActivity extends AppCompatActivity implements BasicAnalysisFragment.OnFragmentInteractionListener{

    List<DoneQuestion> mDoneQuestions;
    List<UnmultiSonAnslysis> mAnalysises;
    List<QuestionUnmultiSon> mQuestions;
    String mStrKPName;
    ViewPager mViewPager;
    private MenuItem mFavoriteMenuItem;

    private boolean mHasEditDoneQuestion = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_analysis);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mDoneQuestions = (List<DoneQuestion>) intent.getSerializableExtra("mDoneQuestions");
        mAnalysises = (List<UnmultiSonAnslysis>) intent.getSerializableExtra("mAnalysises");
        mQuestions = (List<QuestionUnmultiSon>)intent.getSerializableExtra("mQuestions");
        mStrKPName = intent.getStringExtra("mStrKPName");


        mViewPager = (ViewPager)findViewById(R.id.viewPager_actvity_show_analysis);
        QuestionPagerAdapter questionPagerAdapter = new QuestionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(questionPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPagerChangeListener());
    }

    private class ViewPagerChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if(mDoneQuestions.get(position).getIsFavorite()){
                mFavoriteMenuItem.setIcon(getResources().getDrawable(R.drawable.ic_grade_amber_24dp));
            }else{
                mFavoriteMenuItem.setIcon(getResources().getDrawable(R.drawable.ic_grade_white_24dp));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_analysis, menu);
        mFavoriteMenuItem = menu.findItem(R.id.action_favorite_question);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_favorite_question:
                mFavoriteMenuItem = item;
                int index = mViewPager.getCurrentItem();
                if(mDoneQuestions.get(index).getIsFavorite()) {
                    mDoneQuestions.get(index).setIsFavorite(false);
                    item.setIcon(getResources().getDrawable(R.drawable.ic_grade_white_24dp));
                }
                else {
                    mDoneQuestions.get(index).setIsFavorite(true);
                    item.setIcon(getResources().getDrawable(R.drawable.ic_grade_amber_24dp));
                }
                mHasEditDoneQuestion = true;

                return true;
            case R.id.action_note:
                showNoteDialog();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showNoteDialog(){
        Dialog noteDialog = new Dialog(this);
        noteDialog.setContentView(R.layout.dialog_note_edittext);
        EditText dialogET = (EditText) noteDialog.findViewById(R.id.editText_add_note);
        Button sureBT = (Button)noteDialog.findViewById(R.id.button_sure_dialog_add_note);
        int index = mViewPager.getCurrentItem();
        String strNote = mDoneQuestions.get(index).getStrNote();
        if(strNote != null)dialogET.setText(strNote);
        noteDialog.setTitle(R.string.add_note);
        noteDialog.show();
        sureBT.setOnClickListener(new NoteDialogSureBTClick(noteDialog, index));
    }

    @Override
    public void finish(){
        super.finish();
        if(mHasEditDoneQuestion)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DoneQuestionDBHelper doneQuestionDBHelper =
                            DoneQuestionDBHelper.getInstance(ShowAnalysisActivity.this);
                    doneQuestionDBHelper.updateDoneQuestions(mDoneQuestions);
                }
            }).start();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class NoteDialogSureBTClick implements View.OnClickListener {
        Dialog noteDialog;
        int index;
        public NoteDialogSureBTClick(Dialog noteDialog, int index) {
            this.noteDialog = noteDialog;
            this.index = index;
        }

        @Override
        public void onClick(View view) {
            EditText noteET = (EditText) noteDialog.findViewById(R.id.editText_add_note);
            String strNote = noteET.getText().toString();
            mDoneQuestions.get(index).setStrNote(strNote);
            mHasEditDoneQuestion = true;
            noteDialog.dismiss();
        }
    }

    private class QuestionPagerAdapter extends FragmentStatePagerAdapter{

        public QuestionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = BasicAnalysisFragment.newInstance(
                    mDoneQuestions.get(position)
                    , mQuestions.get(position)
                    , mAnalysises.get(position)
                    , position
                    , mStrKPName
                    , getCount());
            return fragment;
        }

        @Override
        public int getCount() {
            return mDoneQuestions.size();
        }
    }


}
