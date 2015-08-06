package com.example.ustc_pc.myapplication.activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
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
import com.example.ustc_pc.myapplication.unit.Answer;
import com.example.ustc_pc.myapplication.unit.QuestionNew;

import java.util.List;

public class ShowAnalysisActivity extends AppCompatActivity {

    List<DoneQuestion> mDoneQuestions;
    List<Answer> mAnswers;
    List<QuestionNew> mQuestionNews;
    String mStrKPName;
    ViewPager mViewPager;

    private boolean mHasEditDoneQuestion = false;
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
        QuestionPagerAdapter questionPagerAdapter = new QuestionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(questionPagerAdapter);
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
        switch (id){
            case R.id.action_favorite_question:
                int index = mViewPager.getCurrentItem();
                if(mDoneQuestions.get(index).getIsFavorite())
                    mDoneQuestions.get(index).setIsFavorite(false);
                else mDoneQuestions.get(index).setIsFavorite(true);
                mHasEditDoneQuestion = true;
                return true;
            case R.id.action_note:
                showNoteDialog();
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
