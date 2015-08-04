package com.example.ustc_pc.myapplication.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.view.menu.MenuView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.db.DBHelper;
import com.example.ustc_pc.myapplication.fragment.ScreenSlidePageFragment;
import com.example.ustc_pc.myapplication.net.NetUtil;
import com.example.ustc_pc.myapplication.net.Util;
import com.example.ustc_pc.myapplication.unit.FileOperation;
import com.example.ustc_pc.myapplication.unit.Paper;
import com.example.ustc_pc.myapplication.unit.Question;
import com.example.ustc_pc.myapplication.unit.Strings;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ustc-pc on 2015/5/11.
 */
public class ActivityPaperG extends ActionBarActivity implements View.OnClickListener, ViewPager.OnPageChangeListener{

    public static String FINISH_ACTIVITY = "com.example.ustc_pc.myapplication.finish.activityPaperG";

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private LinearLayout linearLayoutQuestionNum, mLinearLayoutNoNet, mLinearLayoutErrorRetry;
    private RelativeLayout mRelativeLayoutPaperTitle;
    private TextView mTypeTV, mIndexTV, mSumTV;//题型，第几题，总题数
    private MenuView.ItemView timerMenu;

    private int COURSE_ID = 1;
    private int askType = 0;
    private String _strPaperID = "";
    private String _strPaperName = "";
    private Paper _paper;
    private String mLabel = "";
    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_paper);

//        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        askType = getIntent().getIntExtra("TYPE", Paper.PAPER_BASIC);
        _strPaperID = getIntent().getStringExtra("PAPER_ID");
        _strPaperName = getIntent().getStringExtra("PAPER_NAME");

        if(askType == Paper.PAPER_ERROR){
            COURSE_ID = getIntent().getIntExtra("COURSE_ID", 1);
            mLabel = getIntent().getStringExtra("LABEL");
        }
        initView();
        initData4Paper();
        initBroadcast();
    }

    FinishActivityBroadcastReceiver finishActivityBroadcastReceiver;
    private void initBroadcast(){
        finishActivityBroadcastReceiver = new FinishActivityBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ActivityPaperG.FINISH_ACTIVITY);
        registerReceiver(finishActivityBroadcastReceiver, filter);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(finishActivityBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_paper, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch(id){
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                finish();
                break;
            case R.id.action_answer_sheets:
                return showAnswerSheetDialog();
            case R.id.action_favorite_question:
                return addFavoriteQuestion();
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

    private boolean showAnswerSheetDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_answer_sheets);
        dialog.setTitle(getString(R.string.answer_sheets));
        GridView gridView = (GridView) dialog.findViewById(R.id.gridView_answer);
//        RelativeLayout titleRL = (RelativeLayout)dialog.findViewById(R.id.relativeLayout_answer_sheet_title);
//        titleRL.setVisibility(View.GONE);
        Button subAnswerBT = (Button) dialog.findViewById(R.id.button_submit);
        subAnswerBT.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivityPaperReport();
            }
        });
        AnswerListAdapter answerStAdapter  = new AnswerListAdapter();
        gridView.setAdapter(answerStAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewPager.setCurrentItem(position);
                dialog.cancel();
            }
        });
        dialog.show();
        return true;
    }

    private void startActivityPaperReport(){
        long lEndTime = System.currentTimeMillis();
        _paper._lEndTime = lEndTime;
        Intent intent = new Intent();
        intent.setClass(this, ActivityPaperReport.class);
        intent.putExtra("PAPER", _paper);
        startActivity(intent);
        finish();
    }

    private void initView() {
        mViewPager = (ViewPager)findViewById(R.id.viewPager_questions);
        mRelativeLayoutPaperTitle = (RelativeLayout)findViewById(R.id.relativeLayout_paper_title);
        mLinearLayoutErrorRetry = (LinearLayout)findViewById(R.id.linearLayout_error_retry);
        mLinearLayoutErrorRetry.setOnClickListener(this);
        linearLayoutQuestionNum = (LinearLayout)findViewById(R.id.linearLayout_question_num);
        mLinearLayoutNoNet = (LinearLayout)findViewById(R.id.linearLayout_no_network);
        mLinearLayoutNoNet.setOnClickListener(this);
        mTypeTV = (TextView)findViewById(R.id.textView_question_type);
        switch (askType) {
            case Paper.PAPER_ERROR:
                mTypeTV.setText(mLabel);
                break;
            default:
                mTypeTV.setText(_strPaperName);
        }
        mIndexTV = (TextView)findViewById(R.id.textView_question_index);
        mSumTV = (TextView)findViewById(R.id.textView_question_sum);
        timerMenu = (MenuView.ItemView)findViewById(R.id.action_timer);

        mViewPager.setOnPageChangeListener(this);
    }
    private void initData4Paper() {
        if(askType == Paper.PAPER_ERROR){
            GetErrorPaperAsync getErrorPaperAsync = new GetErrorPaperAsync();
            getErrorPaperAsync.execute();
        }
        else {
            String fileName = "paper_" + _strPaperID + ".json";
            String filePath = FileOperation.APP_PATH + fileName;
            if (!Util.isConnect(this)) {//No Network
                File file = new File(filePath);
                if (!file.exists()) {//paper file is not exist
                    //show no network
                    mLinearLayoutNoNet.setVisibility(View.VISIBLE);

                } else {//paper 文件存在 ，but no network
//                downloadFinished();
                    mLinearLayoutNoNet.setVisibility(View.GONE);
                }
            } else {//has Network
                GetPaperAsyncTask getPaperAsyncTask = new GetPaperAsyncTask();
                getPaperAsyncTask.execute(_strPaperID);
            }
        }

        }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.button_submit:
                submitAnswer();
                break;
            case R.id.linearLayout_no_network:// no network
                refresh();
                break;
            case R.id.linearLayout_error_retry://
                refresh();
                break;
        }
    }

    /**
     * 重新刷新页面
     */
    private void refresh(){
        GetPaperAsyncTask getPaperAsyncTask = new GetPaperAsyncTask();
        getPaperAsyncTask.execute(_strPaperID);
    }

    /**
     提交答案
     */
    private void submitAnswer(){
        saveUserData();
        if(isPaperFinish()){// finished
            startActivityPaperReport();
        }else{// unfinished
            showUnfinishedAlertDialog();
        }


        finish();
    }

    private void saveUserData() {
        _paper._lSpendTime = _lUserSpendTime;
    }

    /**
     * check paper is finished ?
     * @return
     */
    private boolean isPaperFinish(){

        return false;
    }

    private void showUnfinishedAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unfinish_paper);
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityPaperReport();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        if(position < _paper._questions.size()){
            linearLayoutQuestionNum.setVisibility(View.VISIBLE);
            int index = position + 1;
            mIndexTV.setText("" + index);
            mTypeTV.setText(_paper._strName);
            long curTime = System.currentTimeMillis();
            _paper._questions.get(position > 0 ? (position - 1) : position)._lEndTime = curTime;
            _paper._questions.get(position)._lStartTime = curTime;
        }else{
            mPagerAdapter.notifyDataSetChanged();
            linearLayoutQuestionNum.setVisibility(View.GONE);
            _paper._questions.get(position - 1)._lEndTime = System.currentTimeMillis();
            mTypeTV.setText(getResources().getString(R.string.answer_sheets));
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private void initPaperAdapterAndTimer(){
        mSumTV.setText(_paper._questions.size() + "");

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        initTimer();
    }

    public void initTimer(){
        startTime = System.currentTimeMillis();
        _paper._lStartTime = startTime;
        //初始化第一道题的时间
        _paper._questions.get(0)._lStartTime = startTime;

        handlerTimer.removeCallbacks(updateTimer);
        handlerTimer.postDelayed(updateTimer,1000);
    }

    private Long startTime;
    private Handler handlerTimer = new Handler(){
        public void handleMessage(Message msg){
            String time =msg.getData().getString("TIME");
            if(time != null && time.length() > 0){
                if(timerMenu == null) {
                    timerMenu = (MenuView.ItemView)findViewById(R.id.action_timer);
                }
                timerMenu.setTitle(time);
                _paper._strSpendTime = time;
//                mPagerAdapter.notifyDataSetChanged();
            }
        }
    };

    private long _lUserSpendTime;//用户做题时间

    //固定要执行的方法
    private Runnable updateTimer = new Runnable() {
        public void run() {
            //final TextView time = (TextView) findViewById(R.id.timer);
            Long spentTime = System.currentTimeMillis() - startTime;
            _lUserSpendTime = spentTime;
            _paper._lSpendTime = _lUserSpendTime;
            //计算目前已過分鐘數
            Long minius = (spentTime/1000)/60;
            //計算目前已過秒數
            Long seconds = (spentTime/1000) % 60;
            Message msg = new Message();
            Bundle bundle = new Bundle();
            String strMinius = minius + "";
            if(minius < 10) strMinius = "0"+strMinius;
            String strSeconds = seconds + "";
            if(seconds < 10) strSeconds = "0"+ strSeconds;
            String strTime = strMinius+":"+strSeconds;
            _paper._strSpendTime =  strTime;
            bundle.putString("TIME", strTime);
            msg.setData(bundle);
            handlerTimer.sendMessage(msg);
            //time.setText(minius+":"+seconds);
            handlerTimer.postDelayed(this, 1000);
        }
    };

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
            Bundle args = new Bundle();
            args.putSerializable("PAPER", _paper);
            if( position < _paper._questions.size() ) {
                args.putInt("TYPE", ScreenSlidePageFragment.TYPE_QUESTION);
                args.putSerializable("QUESTION", _paper._questions.get(position));
            }else{
                args.putInt("TYPE",ScreenSlidePageFragment.TYPE_ANSWER_SHEET);
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return _paper._questions.size() + 1;
        }

        @Override
        public int getItemPosition(Object item){
            ScreenSlidePageFragment screenSlidePageFragment = (ScreenSlidePageFragment)item;
            int type = screenSlidePageFragment.getiType();
            if(type == ScreenSlidePageFragment.TYPE_ANSWER_SHEET) {
                return PagerAdapter.POSITION_NONE;
            }else{
                return PagerAdapter.POSITION_UNCHANGED;
            }
        }
    }

    class AnswerListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return _paper._questions.size();
        }

        @Override
        public Object getItem(int position) {
            return _paper._questions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AnswerViewHolder viewHolder;
            if(convertView == null){
                convertView = View.inflate(parent.getContext(), R.layout.layout_textview, null);
                viewHolder = new AnswerViewHolder();
                viewHolder.mTextView = (TextView)convertView.findViewById(R.id.textView_layout);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (AnswerViewHolder)convertView.getTag();
            }
            Question question = _paper._questions.get(position);
            int index = position + 1;
            viewHolder.mTextView.setText(""+index);
            if(question._iUserHasDone == 0){
                viewHolder.mTextView.setBackgroundDrawable(parent.getResources().getDrawable(R.drawable.bg_unanswer_sheet_option));

            }else {
                viewHolder.mTextView.setBackgroundDrawable(parent.getResources().getDrawable(R.drawable.bg_answer_sheet_option));
                viewHolder.mTextView.setTextColor(-1);//white
            }

            return convertView;
        }
    }

    class AnswerViewHolder{
        TextView mTextView;
    }

    class GetPaperAsyncTask extends AsyncTask<String, Integer, Paper> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(ActivityPaperG.this, null, getResources().getString(R.string.loading));

        }
        @Override
        protected Paper doInBackground(String... params) {
            Paper paper = NetUtil.getPaper(params[0]);
            return paper;
        }

        @Override
        protected void onPostExecute(Paper result){
            if(result != null) {
                _paper = result;
                _paper._iType = askType;
                mLinearLayoutNoNet.setVisibility(View.GONE);
                mLinearLayoutErrorRetry.setVisibility(View.GONE);
                mRelativeLayoutPaperTitle.setVisibility(View.VISIBLE);

                initPaperAdapterAndTimer();
            }else{
                if(Util.isConnect(ActivityPaperG.this)){//network available, but paper == null
                    mLinearLayoutErrorRetry.setVisibility(View.VISIBLE);
                    mRelativeLayoutPaperTitle.setVisibility(View.GONE);
                    mLinearLayoutNoNet.setVisibility(View.GONE);
                }else{
                    mLinearLayoutNoNet.setVisibility(View.VISIBLE);
                    mRelativeLayoutPaperTitle.setVisibility(View.GONE);
                    mLinearLayoutErrorRetry.setVisibility(View.GONE);
                }
            }
            progressDialog.dismiss();
        }
    }

    class GetErrorPaperAsync extends AsyncTask<Integer, Integer, Paper>{

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(ActivityPaperG.this, null, getResources().getString(R.string.loading));

        }

        @Override
        protected Paper doInBackground(Integer... params) {
            DBHelper dbHelper = DBHelper.getInstance(ActivityPaperG.this);
            SQLiteDatabase mDB = dbHelper.getReadableDatabase();
            Cursor cursor = mDB.query(Strings.TABLE_NAME_LOG_QUESTIONS, new String[]{"questionID","firstKP","isCorrect"},
                    "firstKP = ? and isCorrect = 0", new String[]{mLabel}, null, null, null);
            ArrayList<String> questionIDs = new ArrayList<>();
            while(cursor.moveToNext()){
                String questionID = cursor.getString(0);
                if(questionID != null && questionID.length() > 0)questionIDs.add(questionID);
            }
            ArrayList<Question> questions = null;
            if(!questionIDs.isEmpty()) {
                questions = new ArrayList<>();
                int size = questionIDs.size();
                for(int i=0 ;i < size; i++){
                    String questionID = questionIDs.get(i);
                    Question question = FileOperation.getQuestionByID(questionID);
                    if(question != null) questions.add(question);
                }
            }

            if(questions != null) {
                Paper result = new Paper();
                result._questions = questions;
                result._strName = mLabel;
                result._iNumQ = questions.size();
                result._iCourseId = COURSE_ID;
                result._iType = Paper.PAPER_ERROR;
                return result;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Paper result){
            if(result == null){
                mLinearLayoutErrorRetry.setVisibility(View.VISIBLE);
                mLinearLayoutNoNet.setVisibility(View.GONE);
            }else{
                _paper = result;
                mLinearLayoutNoNet.setVisibility(View.GONE);
                mLinearLayoutErrorRetry.setVisibility(View.GONE);
                mRelativeLayoutPaperTitle.setVisibility(View.VISIBLE);

                initPaperAdapterAndTimer();
            }
            progressDialog.dismiss();
        }
    }

    class FinishActivityBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }
}
