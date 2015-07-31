package com.example.ustc_pc.myapplication.activity;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.adapter.ActivityPapersAdapter;
import com.example.ustc_pc.myapplication.db.DBHelper;
import com.example.ustc_pc.myapplication.myInterface.DownloadFinishedInterface;
import com.example.ustc_pc.myapplication.net.NetUtil;
import com.example.ustc_pc.myapplication.unit.FileOperation;
import com.example.ustc_pc.myapplication.unit.Paper;
import com.example.ustc_pc.myapplication.unit.Strings;
import com.example.ustc_pc.myapplication.viewUnit.CircularProgressDrawable;
import com.example.ustc_pc.myapplication.viewUnit.ViewArc;

import java.io.File;
import java.util.ArrayList;

public class ActivityPapers extends ActionBarActivity implements AdapterView.OnItemClickListener,
        DownloadFinishedInterface, View.OnClickListener{

    TextView knowledgePointTV;
    LinearLayout mPapersSectorLL;
    ListView papersLV;

    private int iFinished = 0;
    private String kpID, kpName, fileName;

    ArrayList<Paper> papers;
    ActivityPapersAdapter papersAdapter;

    private LinearLayout mNoNetLL;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_papers);

        Intent intent = getIntent();
        kpID = intent.getStringExtra("ID");
        kpName = intent.getStringExtra("NAME");

        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initData();


    }

    @Override
    protected  void onResume(){
        super.onResume();
        getFinishedPapers();
//        Toast.makeText(this, "onResume()",Toast.LENGTH_SHORT).show();

        //Circular drawable
        ivDrawable = (ImageView)findViewById(R.id.iv_drawable);
        drawable = new CircularProgressDrawable.Builder()
                .setRingWidth(getResources().getDimensionPixelSize(R.dimen.drawable_ring_width))
                .setOutlineColor(getResources().getColor(android.R.color.darker_gray))
                .setRingColor(getResources().getColor(android.R.color.holo_green_light))
                .create();
        ivDrawable.setImageDrawable(drawable);
        ivDrawable.setOnClickListener(this);
    }

    private void initView(){
        mNoNetLL = (LinearLayout)findViewById(R.id.linearLayout_no_network);
        mNoNetLL.setOnClickListener(this);
        knowledgePointTV = (TextView) findViewById(R.id.textView_knowledge_point);
        knowledgePointTV.setText(kpName);
        mPapersSectorLL = (LinearLayout) findViewById(R.id.linearLayout_papers_progress);

        papers = new ArrayList<>();
        papersLV = (ListView) findViewById(R.id.listView_papers);
        papersAdapter = new ActivityPapersAdapter(this, papers);
        papersLV.setAdapter(papersAdapter);

        papersLV.setOnItemClickListener(this);




    }

    public ImageView ivDrawable;

    private CircularProgressDrawable drawable;

    private void startAnimation(){
        Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();
        AnimatorSet animation = new AnimatorSet();
        ObjectAnimator progressAnimation = ObjectAnimator.ofFloat(drawable,"progress", 0f, 1f);
        progressAnimation.setDuration(3600);
        progressAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        ObjectAnimator colorAnimator = ObjectAnimator.ofInt(drawable,"ringColor",
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_light));
        colorAnimator.setEvaluator(new ArgbEvaluator());
        colorAnimator.setDuration(3600);
        animation.playTogether(progressAnimation, colorAnimator);
        animation.start();
    }

    private void startAnimation(View view){
        CircularProgressDrawable drawable = ((ActivityPapersAdapter.PapersViewHolder)view.getTag()).getDrawable();
        AnimatorSet animation = new AnimatorSet();
        ObjectAnimator progressAnimation = ObjectAnimator.ofFloat(drawable,"progress", 0f, 1f);
        progressAnimation.setDuration(3600);
        progressAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        ObjectAnimator colorAnimator = ObjectAnimator.ofInt(drawable,"ringColor",
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_light));
        colorAnimator.setEvaluator(new ArgbEvaluator());
        colorAnimator.setDuration(3600);
        animation.playTogether(progressAnimation, colorAnimator);
        animation.start();
    }
    private void initData(){
        //Paper(int id, String name, int num, String course , String firstKP, String secondKP, String thirdKP)
        fileName = "kp_papers_"+kpID + ".json";
        if(!NetUtil.isConnect(this)){//No Network
            File file = new File(FileOperation.APP_PATH + fileName);
            if(!file.exists()){//paper file is not exist
                //show no network
                mNoNetLL.setVisibility(View.VISIBLE);

            }else{//paper 文件存在 ，but no network
                downloadFinished();
                mNoNetLL.setVisibility(View.GONE);
            }
        }else{//has Network
            GetPapersAsyncTask getPapersAsyncTask = new GetPapersAsyncTask();
            getPapersAsyncTask.execute(kpID);
        }
        papersAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_activity_papers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Circular Progress

        startAnimation(view);
//        Paper paper = papers.get(position);
//        Intent intent = new Intent();
//        intent.setClass(this, ActivityPaperG.class);
//        intent.putExtra("PAPER_NAME", paper._strName);
//        intent.putExtra("PAPER_ID", paper._id);
//        intent.putExtra("TYPE", Paper.PAPER_BASIC);
//        startActivity(intent);

    }

    @Override
    public void downloadFinished() {
        String fileName = "kp_papers_"+kpID + ".json";
        ArrayList<Paper> kpPapersArrayList = FileOperation.getPapers(fileName);
        if(kpPapersArrayList != null && !kpPapersArrayList.isEmpty()){
            papers = kpPapersArrayList;
        }
    }


    public void getFinishedPapers() {
        GetFinishedPapers getFinishedPapers = new GetFinishedPapers();
        getFinishedPapers.execute();
    }


    class GetFinishedPapers extends AsyncTask<String, Integer, Integer>{

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Integer doInBackground(String... params) {
            //check papers
            iFinished = 0;
            DBHelper dbHelper = DBHelper.getInstance(ActivityPapers.this);
            SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.query(Strings.TABLE_NAME_HISTORY_PAPERS, new String[]{"paperID","score"},null,null,null,null,null);
            int size = papers.size();
            while(cursor.moveToNext()){
                String paperId = cursor.getString(0);
                for(int i =0; i< size; i++){
                    if(papers.get(i)._id.equals(paperId)){
                        iFinished++;
                        papers.get(i)._iScore = cursor.getInt(1);
                        papers.get(i)._isDid = true;
                    }
                }
            }
            cursor.close();
            int arg = 0;
            if(size > 0)arg = iFinished * 360 / size ;
            dbHelper.destoryInstance();
            checkPaperIsLocked();
            return arg;
        }

        @Override
        protected void onPostExecute(Integer result){
            ViewArc viewArc = new ViewArc(ActivityPapers.this, result, 0);
            viewArc.invalidate();
            mPapersSectorLL.removeAllViews();
            mPapersSectorLL.addView(viewArc);
            papersAdapter.notifyDataSetChanged();
        }

        private void checkPaperIsLocked() {
            for (int i = 0; i < papers.size(); i++) {
                String fileName = "paper_" + papers.get(i)._id + ".json";
                String filePath = FileOperation.APP_PATH + fileName;
                File file = new File(filePath);
                if (file.exists()) {//paper file is not exist
                    papers.get(i)._isLocked = false;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linearLayout_no_network:
                refresh();
                break;
            case R.id.iv_drawable:
                startAnimation();
                break;
        }
    }

    /**
     * 重新加载页面
     */
    private void refresh(){
        GetPapersAsyncTask getPapersAsyncTask = new GetPapersAsyncTask();
        getPapersAsyncTask.execute(kpID);
    }


    class GetPapersAsyncTask extends AsyncTask<String, Integer, Integer>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(ActivityPapers.this, null, getResources().getString(R.string.loading));
        }
        @Override
        protected Integer doInBackground(String... params) {
            //get paper list
            ArrayList<Paper> kpPapersArrayList = FileOperation.getPapers(params[0]);
            if(kpPapersArrayList != null && !kpPapersArrayList.isEmpty()){
                papers.clear();
                papers.addAll(kpPapersArrayList);
            }else{
                return -1;
            }

            return 1;
        }

        @Override
        protected void onPostExecute(Integer result){
            if(result == -1){//get papers failed

            }else{//get papers success
                getFinishedPapers();
            }
            progressDialog.dismiss();
            if(!papers.isEmpty()) {
                mNoNetLL.setVisibility(View.GONE);
            }else{
                mNoNetLL.setVisibility(View.VISIBLE);
            }
            papersAdapter.notifyDataSetChanged();
        }
    }
}
