package com.example.ustc_pc.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.MainActivity;
import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.db.DBHelper;
import com.example.ustc_pc.myapplication.unit.Paper;
import com.example.ustc_pc.myapplication.unit.Strings;

import java.util.ArrayList;

public class ActivityUserBook extends ActionBarActivity implements AdapterView.OnItemClickListener{
    public static final int TYPE_ERROR_BOOK = 0, TYPE_FAVORITE_BOOK = 1, TYPE_NOTE_BOOK = 2, TYPE_ERROR_TEST = 3;

    private int iType = 0;
    private int COURSE_ID = 1;

    private ListView mListView ;
    private ArrayList<String> mLabels;
    private TextView mNoQuestionTV;

//    private BookAdapter mBookAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_book);
//
//        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iType = getIntent().getIntExtra("TYPE", 0);
        COURSE_ID = getIntent().getIntExtra(MainActivity.ARG_SECTION_NUMBER, 1);
        switch (iType){
            case TYPE_ERROR_BOOK:
                setTitle(R.string.error_book);
                break;
            case TYPE_ERROR_TEST:
                setTitle(R.string.error_test);
                break;
            case TYPE_FAVORITE_BOOK:
                setTitle(R.string.favorite_book);
                break;
            case TYPE_NOTE_BOOK:
                setTitle(R.string.note_book);
                break;
        }

        initView();
        initData();
    }

    private void initData() {
        mLabels = new ArrayList<>();
        GetLabelNameFromDB getLabelNameFromDB = new GetLabelNameFromDB();
        getLabelNameFromDB.execute();
    }

    private void initView() {
        mListView = (ListView)findViewById(R.id.listView_user_book);
        mNoQuestionTV = (TextView)findViewById(R.id.textView_no_question);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_activity_user_book, menu);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = null;
        switch (iType){
            case TYPE_ERROR_TEST://error test
                intent = new Intent(this, ActivityPaperG.class);
                intent.putExtra("COURSE_ID", COURSE_ID);
                intent.putExtra("LABEL", mLabels.get(position));
                intent.putExtra("TYPE", Paper.PAPER_ERROR);
                startActivity(intent);
                break;
            default:
                intent = new Intent();
                intent.putExtra("LABEL",mLabels.get(position));
                intent.putExtra("TYPE", iType);
                intent.putExtra("COURSE_ID", COURSE_ID);
                intent.setClass(this, ActivityUserQuestions.class);
                startActivity(intent);
        }

    }

    class GetLabelNameFromDB extends AsyncTask<Integer, Integer, ArrayList<String>>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ActivityUserBook.this, null,getResources().getString(R.string.loading));
        }
        @Override
        protected ArrayList<String> doInBackground(Integer... params) {
            DBHelper dbHelper = DBHelper.getInstance(ActivityUserBook.this);
            SQLiteDatabase mDB = dbHelper.getReadableDatabase();
            ArrayList<String> result = null;
            Cursor cursor;
            String temp= "";
            switch (iType){
                case TYPE_ERROR_BOOK :
                    cursor = mDB.query(Strings.TABLE_NAME_LOG_QUESTIONS, new String[]{"firstKP","isCorrect"},
                            "isCorrect = 0", null, "firstKP", null, null);
                    result = new ArrayList<>();
                    while(cursor.moveToNext()){
                        String paperName = cursor.getString(0);
                        if( !temp.equals(paperName)){
                            temp = paperName;
                            result.add(paperName);
                        }
                    }
                    cursor.close();
                    break;
                case TYPE_ERROR_TEST :
                    cursor = mDB.query(Strings.TABLE_NAME_LOG_QUESTIONS, new String[]{"firstKP","isCorrect"},
                            "isCorrect = 0", null, "firstKP", null, null);
                    result = new ArrayList<>();
                    while(cursor.moveToNext()){
                        String paperName = cursor.getString(0);
                        if( !temp.equals(paperName)){
                            temp = paperName;
                            result.add(paperName);
                        }
                    }
                    cursor.close();
                    break;

                case TYPE_FAVORITE_BOOK:
                    cursor = mDB.query(Strings.TABLE_NAME_LOG_QUESTIONS, new String[]{"firstKP", "isFavorite"}, "isFavorite=1",
                            null, "firstKP", null, null);//groupby
                    result = new ArrayList<>();
                    while(cursor.moveToNext()){
                        String firstKP = cursor.getString(0);
                        if( !temp.equals(firstKP)){
                            temp = firstKP;
                            result.add(firstKP);
                        }
                    }
                    cursor.close();
                    break;
                case TYPE_NOTE_BOOK:
                    cursor = mDB.query(Strings.TABLE_NAME_LOG_QUESTIONS, new String[]{"firstKP", "note"}, "note is not null and note != ''", null , "firstKP", null, null);//groupby
                    result = new ArrayList<>();
                    while(cursor.moveToNext()){
                        String firstKP = cursor.getString(0);
                        if( !temp.equals(firstKP)){
                            temp = firstKP;
                            result.add(firstKP);
                        }
                    }
                    cursor.close();
                    break;
            }
            mDB.close();
            dbHelper.destoryInstance();
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result){
            mLabels = new ArrayList<>();
            if(result != null) {
                mLabels.addAll(result);
            }

            if(mLabels .isEmpty()){
                switch (iType){
                    case TYPE_NOTE_BOOK:
                        mNoQuestionTV.setText(getResources().getString(R.string.no_note_question));
                        break;
                    case TYPE_ERROR_BOOK:
                        mNoQuestionTV.setText(getResources().getString(R.string.no_error_question));
                        break;
                    case TYPE_FAVORITE_BOOK:
                        mNoQuestionTV.setText(getResources().getString(R.string.no_favorite_question));
                        break;
                }
                mNoQuestionTV.setVisibility(View.VISIBLE);
            }else {
                BookAdapter mBookAdapter = new BookAdapter();
                mListView.setAdapter(mBookAdapter);
                mListView.setOnItemClickListener(ActivityUserBook.this);
                mNoQuestionTV.setVisibility(View.GONE);
            }
            progressDialog.dismiss();
        }

    }

    class BookAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mLabels.size();
        }

        @Override
        public Object getItem(int position) {
            return mLabels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(iType == ActivityUserBook.TYPE_ERROR_TEST) {
                convertView = View.inflate(parent.getContext(), R.layout.layout_error_test_book_item, null);
                TextView textView = (TextView) convertView.findViewById(R.id.textView_labale_name_error);
                textView.setText(mLabels.get(position));
            }else{
                convertView = View.inflate(parent.getContext(), R.layout.layout_user_book_item, null);
                TextView textView = (TextView)convertView.findViewById(R.id.textView_labale_name_user_book);
                textView.setText(mLabels.get(position));
            }
            return convertView;
        }

    }
}
