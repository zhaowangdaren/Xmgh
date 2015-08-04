package com.example.ustc_pc.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.ustc_pc.myapplication.MainActivity;
import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.adapter.ZhenTiAdapter;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.net.NetUtil;
import com.example.ustc_pc.myapplication.unit.FileOperation;
import com.example.ustc_pc.myapplication.unit.Paper;

import java.io.File;
import java.util.ArrayList;

public class ActivityZhenTi extends ActionBarActivity implements  View.OnClickListener, AdapterView.OnItemClickListener{

    private int _iCourse = 0;

    private ListView mListView;
    private LinearLayout mNonetworkLL;
    private ArrayList<Paper> mPapers;
    private ZhenTiAdapter zhenTiAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhenti);

//        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _iCourse = getIntent().getIntExtra(MainActivity.ARG_SECTION_NUMBER, 0);
        initView();
        initData();
    }

    private void initView() {
        mListView = (ListView)findViewById(R.id.listView_history_office);

        mPapers = new ArrayList<>();
        zhenTiAdapter = new ZhenTiAdapter(mPapers);
        mListView.setAdapter(zhenTiAdapter);
    }

    private void initData() {
        String fileName = "zhenti_"+_iCourse + ".json";
        if( NetUtil.isConnect(this)){//has network
            GetZhenTiPaperAsyncTask getZhenTiPaperAsyncTask = new GetZhenTiPaperAsyncTask();
            getZhenTiPaperAsyncTask.execute();
        }else{// no network
            File file = new File(FileOperation.APP_PATH + fileName);
            if( !file.exists()){// no file
                mNonetworkLL = (LinearLayout)findViewById(R.id.linearLayout_no_network);
                mNonetworkLL.setVisibility(View.VISIBLE);
                mNonetworkLL.setOnClickListener(this);
            }else{//has file
                mPapers = FileOperation.getPapers(fileName);
                zhenTiAdapter.notifyDataSetChanged();
            }
        }
    }

//    private void loadingData(){
//        SimpleAdapter simpleAdapter = new SimpleAdapter(this, getData(), R.layout.activity_error_book_item,
//                new String[]{"name"}, new int[]{R.id.textView_paperName_error_book});
//        mListView.setAdapter(simpleAdapter);
//        mListView.setOnItemClickListener(this);
//    }
//
//    private List<Map<String, Object>> getData(){
//        List<Map<String, Object>> list = new ArrayList<>();
//        Map<String, Object> map;
//        int size = mPapers.size();
//        for(int i = 0; i < size; i++){
//            map = new HashMap<>();
//            map.put("name", mPapers.get(i)._name);
//            list.add(map);
//        }
//        return list;
//    }

//    @Override
//    public void downloadFinished() {
//        String fileName = "zhenti_"+_iCourse + ".json";
//        mPapers = FileOperation.getKPPapers(fileName);
//        loadingData();
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linearLayout_no_network:
                initData();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.setClass(this, ActivityPaperG.class);
        intent.putExtra("PAPER_ID",mPapers.get(position)._id);
        intent.putExtra("PAPER_NAME", mPapers.get(position)._strName);
        intent.putExtra("TYPE", Paper.PAPER_ZHEN_TI);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_activity_history_office, menu);
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

    class GetZhenTiPaperAsyncTask extends AsyncTask<String, Integer, ArrayList<Paper>>{

        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(ActivityZhenTi.this, null, getResources().getString(R.string.loading));
        }
        @Override
        protected ArrayList<Paper> doInBackground(String... params) {
            UserSharedPreference userSharedPreference = new UserSharedPreference(ActivityZhenTi.this);
            String username = userSharedPreference.getAccountNumber();
            return NetUtil.getZhenTiPapers(username, _iCourse);
        }

        @Override
        protected void onPostExecute(ArrayList<Paper> result){
            progressDialog.dismiss();
            if(result == null || result.size() == 0)return ;
            mPapers = result;
            zhenTiAdapter.notifyDataSetChanged();

        }
    }

}
