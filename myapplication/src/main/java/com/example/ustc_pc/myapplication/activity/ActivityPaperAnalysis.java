package com.example.ustc_pc.myapplication.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.adapter.ActivityPaperAnalysisAdapter;
import com.example.ustc_pc.myapplication.db.DBHelper;
import com.example.ustc_pc.myapplication.unit.FileOperation;
import com.example.ustc_pc.myapplication.unit.Paper;
import com.example.ustc_pc.myapplication.unit.Question;
import com.example.ustc_pc.myapplication.unit.Strings;

public class ActivityPaperAnalysis extends ActionBarActivity implements ViewPager.OnPageChangeListener, View.OnClickListener{

    ViewPager mViewPager;
    private TextView mTypeTV, mIndexTV, mDivideSymbol, mSumTV;//题型，第几题，总题数
    private Paper _paper;
    ActivityPaperAnalysisAdapter activityPaperAnalysisAdapter;

    //note
    private RelativeLayout mRelativeLayoutNote;
    private TextView mNoteTV, mAddEditNoteTV;
    private Dialog dialog;
    private EditText dialogET;

    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper);

//        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initData();

    }

    private void initData() {
        dbHelper = DBHelper.getInstance(this);

        _paper = (Paper) getIntent().getSerializableExtra("PAPER");
        if(_paper == null){
            Log.e("Error:", "ActivityPaperAnalysis get null papaer!!!!");
            finish();
        }

        mTypeTV.setText(_paper._strName);
        mSumTV.setText(_paper._questions.size() +"");
        activityPaperAnalysisAdapter = new ActivityPaperAnalysisAdapter(this,_paper._questions, 0);
        mViewPager.setAdapter(activityPaperAnalysisAdapter);
        mViewPager.setOnPageChangeListener(this);
        int iStartPos = getIntent().getIntExtra("POSITION", 0);
        mViewPager.setCurrentItem(iStartPos);
        setNoteTV(iStartPos);
        mAddEditNoteTV.setOnClickListener(this);
    }

    private void initView() {
        mViewPager = (ViewPager)findViewById(R.id.viewPager_questions);
        mTypeTV = (TextView)findViewById(R.id.textView_question_type);
        mIndexTV = (TextView)findViewById(R.id.textView_question_index);
        mDivideSymbol = (TextView) findViewById(R.id.textView_divide_symbol);
        mSumTV = (TextView)findViewById(R.id.textView_question_sum);

        mRelativeLayoutNote = (RelativeLayout) findViewById(R.id.relativeLayout_note);
        mRelativeLayoutNote.setVisibility(View.VISIBLE);
        mNoteTV = (TextView)findViewById(R.id.textView_note);
        mAddEditNoteTV = (TextView)findViewById(R.id.textView_add_edit_note);
    }


    /**
     *
     * @param questionPosition
     */
    private void setNoteTV(int questionPosition){
        String note = _paper._questions.get(questionPosition)._strNote;
        if(note != null && note.length()>0){
            mNoteTV.setText(note);
            mAddEditNoteTV.setText(R.string.edit_note);
        }else{
            mNoteTV.setText("");
            mAddEditNoteTV.setText(R.string.add_note);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_paper_analysis, menu);
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
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_love:
                addFavoriteQuestion();
                return true;
//            case R.id.action_share:
//                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean addFavoriteQuestion() {
        int position = mViewPager.getCurrentItem();
        if(position < _paper._questions.size()) {
            final Question question = _paper._questions.get(position);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    logFavoriteQuestion(question);
                }
            }).start();
            Toast.makeText(this, R.string.action_favorite_success, Toast.LENGTH_SHORT).show();
        }
        return true;
    }


    private void logFavoriteQuestion(Question question){
        ContentValues contentValues = new ContentValues();
        contentValues.put("questionID", question._id);
        if( !question._strThirdKP.equals("") ){
            contentValues.put("firstKP", question._strThirdKP);
        }else if( ! question._strSecondKP.equals("")){
            contentValues.put("secondKP", question._strThirdKP);
        }else{
            contentValues.put("thirdKP", question._strFirstKP);
        }
        contentValues.put("isFavorite", 1);
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Strings.TABLE_NAME_LOG_QUESTIONS, new String[]{"questionID"},"questionID= ? ",new String[]{question._id},null,null,null);
        if(!cursor.moveToFirst()){//数据库表中不含有该记录
            long result = sqLiteDatabase.insert(Strings.TABLE_NAME_LOG_QUESTIONS, null, contentValues);
            if(result == -1){
                Log.e("DB insert to favorite:", "Error");
            }
        }else{//数据库表中含有该id的question
            sqLiteDatabase.update(Strings.TABLE_NAME_LOG_QUESTIONS, contentValues, "questionID = ?", new String[]{question._id});
        }

        cursor.close();
        sqLiteDatabase.close();
        dbHelper.destoryInstance();
        FileOperation.writeQuestion2SD(question, 0);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int index = position + 1;
        mIndexTV.setText("" + index);
        setNoteTV(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.textView_add_edit_note:
                showAddNoteDialog();
                break;
            case R.id.button_sure_dialog_add_note:
                noteDialogSureButtonAction();
                break;
        }
    }

    private void noteDialogSureButtonAction(){
        final String note = dialogET.getText().toString();
        final int index = mViewPager.getCurrentItem();
        if( !_paper._questions.get(index)._strNote.equals(note) ){
            _paper._questions.get(index)._strNote = note;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //FileOperation.writeQuestion2SD(_questions.get(position));
                    logNoteQuestion(_paper._questions.get(index)._id, note);
                }
            }).start();
            mNoteTV.setText(note);
            if(note.length() > 0)mAddEditNoteTV.setText(R.string.edit_note);
        }
        dialog.cancel();
    }

    private void logNoteQuestion(String id, String note){

        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Strings.TABLE_NAME_LOG_QUESTIONS, new String[]{"questionID"}, "questionID='"+id+"'",null, null,null,null);
        ContentValues contentValues = new ContentValues();
        contentValues.put("questionID", id);
        contentValues.put("note", note);
        if( !cursor.moveToFirst() ){//如果游标为空，则数据库中尚未存储过该question
            cursor.close();
            sqLiteDatabase.insert(Strings.TABLE_NAME_LOG_QUESTIONS, null, contentValues);
        }else{//数据库中存储过该数据
            sqLiteDatabase.update(Strings.TABLE_NAME_LOG_QUESTIONS, contentValues,"questionID = ?", new String[]{id});
        }
//        dbHelper.closeDatabase();
    }

    private void showAddNoteDialog(){

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_note_edittext);
        dialogET = (EditText) dialog.findViewById(R.id.editText_add_note);
        Button sureBT = (Button) dialog.findViewById(R.id.button_sure_dialog_add_note);
        sureBT.setOnClickListener(this);
        dialogET.setText(_paper._questions.get(mViewPager.getCurrentItem())._strNote);
        dialog.setTitle(R.string.add_note);
        dialog.show();
            /*
            Intent intent = new Intent();
            intent.setClass(_context, ActivityEditNote.class);
            _context.startActivity(intent);
            */
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        dbHelper.close();
    }
}
