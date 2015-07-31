package com.example.ustc_pc.myapplication.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.ustc_pc.myapplication.MainActivity;
import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.adapter.ActivityHistoryAdapter;
import com.example.ustc_pc.myapplication.db.DBHelper;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.net.NetUtil;
import com.example.ustc_pc.myapplication.unit.Paper;
import com.example.ustc_pc.myapplication.unit.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityHistoryTest extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener{

    private ArrayList<Paper> mPapers;

    private ListView mListView;
    private ActivityHistoryAdapter activityHistoryAdapter;
    private LinearLayout mNoNetLL;

    private int iCourseID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_test);
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iCourseID = getIntent().getIntExtra(MainActivity.ARG_SECTION_NUMBER,1);

        initView();
        initData();
    }

    private void initView() {
        mListView = (ListView)findViewById(R.id.listView_history_test);
        mNoNetLL = (LinearLayout)findViewById(R.id.linearLayout_no_network);
        mNoNetLL.setOnClickListener(this);
    }


    private void initData(){
        mPapers = new ArrayList<>();
        activityHistoryAdapter = new ActivityHistoryAdapter(mPapers);
        mListView.setAdapter(activityHistoryAdapter);
        mListView.setOnItemClickListener(ActivityHistoryTest.this);
        GetHistoryPaper getHistoryPaper = new GetHistoryPaper(this);
        getHistoryPaper.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linearLayout_no_network:
                refresh();
                break;
        }
    }

    private void refresh() {

    }

    class GetHistoryPaper extends AsyncTask<String, Integer, ArrayList<Paper>>{

        private ProgressDialog progressDialog;
        private Context context;
        private DBHelper dbHelper;
        public GetHistoryPaper(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute(){

            progressDialog = ProgressDialog.show(ActivityHistoryTest.this, null,getResources().getString(R.string.loading));
        }

        @Override
        protected ArrayList<Paper> doInBackground(String... params) {
            dbHelper = DBHelper.getInstance(context);
            SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.query(Strings.TABLE_NAME_HISTORY_PAPERS,new String[]{"paperID", "name", "score", "spendTime"},null,null,"name",null,null);
            ArrayList<Paper> result = new ArrayList<>();
            while(cursor.moveToNext()){
                Paper paper = new Paper();
                paper._id = cursor.getString(0);
                paper._strName = cursor.getString(1);
                paper._iScore = cursor.getInt(2);
                paper._strSpendTime = cursor.getString(3);
                result.add(paper);
            }
            cursor.close();
            if(result.isEmpty()){
                UserSharedPreference userSharedPreference = new UserSharedPreference(ActivityHistoryTest.this);
                ArrayList<Paper> fromServer = NetUtil.getFinishedPapers(userSharedPreference.getAccountNumber(), iCourseID);
                if(fromServer != null && !fromServer.isEmpty()){
                    savePaperInfo2DB(fromServer, sqLiteDatabase);
                    result = fromServer;
                }
            }
            dbHelper.close();
            return result;
        }

        private void savePaperInfo2DB(ArrayList<Paper> papers, SQLiteDatabase sqLiteDatabase){
            int size = papers.size();
            sqLiteDatabase.beginTransaction();
            try {
                for (int i = 0; i < size; i++) {
                    ContentValues values = new ContentValues();
                    String paperId = papers.get(i)._id;
                    values.put("paperID", paperId);
                    values.put("name", papers.get(i)._strName);
                    values.put("score", papers.get(i)._iScore);
                    values.put("spendTime", papers.get(i)._strSpendTime);
                    values.put("startTime", papers.get(i)._lStartTime);
                    values.put("endTime", papers.get(i)._lEndTime);
                    Cursor cursor = sqLiteDatabase.query(Strings.TABLE_NAME_HISTORY_PAPERS, new String[]{"paperID"}, "paperID='" + paperId + "'", null, null, null, null);
                    if (!cursor.moveToFirst()) {//表中不含有该id
                        sqLiteDatabase.insert(Strings.TABLE_NAME_HISTORY_PAPERS, null, values);
                    } else {
                        sqLiteDatabase.update(Strings.TABLE_NAME_HISTORY_PAPERS, values, "paperID = ?", new String[]{paperId});
                    }
                    cursor.close();
                }
                sqLiteDatabase.setTransactionSuccessful();
            }finally {
                sqLiteDatabase.endTransaction();
            }

        }

        @Override
        protected void onPostExecute(ArrayList<Paper> result){
            if(!result.isEmpty()){
                mPapers.clear();
                mPapers.addAll(result);
                activityHistoryAdapter.notifyDataSetChanged();
                mNoNetLL.setVisibility(View.GONE);
            }else{
                if(NetUtil.isConnect(ActivityHistoryTest.this))mNoNetLL.setVisibility(View.GONE);
            }
            progressDialog.dismiss();
        }


    }

    private List<Map<String, Object>> getData(){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map;
        int size = mPapers.size();
        for(int i = 0; i < size; i++){
            map = new HashMap<>();
            map.put("name", mPapers.get(i)._strName);
            map.put("spendTime", mPapers.get(i)._strSpendTime);
            map.put("score", mPapers.get(i)._iScore);
            list.add(map);
        }
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_activity_history_test, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
