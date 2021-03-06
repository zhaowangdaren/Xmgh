package com.example.ustc_pc.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.dao.Course;
import com.example.ustc_pc.myapplication.db.CourseDBHelper;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.net.OkHttpUtil;
import com.example.ustc_pc.myapplication.unit.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectCourseActivity extends AppCompatActivity implements AbsListView.OnItemClickListener ,View.OnClickListener{

    GridView gridView;
    List<Course> mCourses;
    CourseItemAdapter courseItemAdapter;

    Button mFinishBT;

    // View failed
    RelativeLayout mFailedRL;

    private boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_course);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridView = (GridView)findViewById(R.id.gridView_courses);
        mFinishBT = (Button)findViewById(R.id.button_finished);

        mCourses = new ArrayList<>();
        GetAllCourseFromDB getAllCourseFromDB = new GetAllCourseFromDB(this);
        getAllCourseFromDB.execute();
        courseItemAdapter = new CourseItemAdapter(this);
        if(mCourses != null){
            gridView.setAdapter(courseItemAdapter);
            gridView.setOnItemClickListener(this);
            mFinishBT.setOnClickListener(this);
        }


        mFailedRL = (RelativeLayout) findViewById(R.id.relativeLayout_load_failed);
        mFailedRL.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if(id == android.R.id.home){
            if(isChanged){
                FinishSelectCourseAsyncTask finishSelectCourseAsyncTask = new FinishSelectCourseAsyncTask(this);
                finishSelectCourseAsyncTask.execute(mCourses);
            }else{
                startCourseActivity();
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mCourses.get(position).getIsSelected())mCourses.get(position).setIsSelected(false);
        else mCourses.get(position).setIsSelected(true);
        courseItemAdapter.notifyDataSetChanged();
        isChanged = true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_finished:
                if(isChanged) {
                    FinishSelectCourseAsyncTask finishSelectCourseAsyncTask = new FinishSelectCourseAsyncTask(this);
                    finishSelectCourseAsyncTask.execute(mCourses);
                }else {
                    startCourseActivity();
                }
                break;
            case R.id.relativeLayout_load_failed:
                if(Util.isConnect(this)) {
                    GetAllCoursesFromServerAsync getAllCoursesFromServerAsync = new GetAllCoursesFromServerAsync(this);
                    getAllCoursesFromServerAsync.execute();
                }else{
                    Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    class FinishSelectCourseAsyncTask extends AsyncTask<List<Course>, Integer, Boolean> {
        Context context;
        ProgressDialog progressDialog;
        public FinishSelectCourseAsyncTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(context, null, getString(R.string.loading));
        }

        @Override
        protected Boolean doInBackground(List<Course>... params) {
            CourseDBHelper courseDBHelper = CourseDBHelper.getInstance(context);
            courseDBHelper.updateCourses(params[0]);

            List<Course> deletedCourses = getUnselectCourse();
            List<Course> addCourses = getSelectedCourse();
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            try {
                okHttpUtil.addCourse(new UserSharedPreference(context).getiUserID(), addCourses);
                okHttpUtil.deleteCourse(new UserSharedPreference(context).getiUserID(),deletedCourses);
            } catch (IOException e) {
                Log.e("Error ", e.toString());
            }
            return true;
        }

        private List<Course> getSelectedCourse() {
            List<Course> result = new ArrayList<>();
            for(Course course : mCourses){
                if(course.getIsSelected())result.add(course);
            }
            return result;
        }

        private List<Course> getUnselectCourse() {
            List<Course> result = new ArrayList<>();
            for(Course course : mCourses){
                if(!course.getIsSelected())result.add(course);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result){
            progressDialog.dismiss();
            startCourseActivity();
        }
    }

    public void startCourseActivity(){
        Intent intent = new Intent(this, CourseActivity.class);
        startActivity(intent);
        finish();
    }

    class CourseItemAdapter extends BaseAdapter{
        private Context mContext;

        public CourseItemAdapter(Context context){
            mContext = context;
        }
        @Override
        public int getCount() {
            return mCourses.size();
        }

        @Override
        public Object getItem(int position) {
            return mCourses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView== null){
                convertView = View.inflate(mContext, R.layout.activity_select_course_item, null);
                viewHolder = new ViewHolder();
                viewHolder.relativeLayout = (RelativeLayout)convertView.findViewById(R.id.relativeLayout_course_item);
                viewHolder.textView = (TextView)convertView.findViewById(R.id.textView_course_name);
                viewHolder.checkIV = (ImageView)convertView.findViewById(R.id.imageView_select_course_check);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.textView.setText(mCourses.get(position).getStrCourseName());
            if(mCourses.get(position).getIsSelected()){
                viewHolder.checkIV.setVisibility(View.VISIBLE);
                viewHolder.textView.setTextColor(getResources().getColor(R.color.offical_blue));
                viewHolder.relativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
            }
            else {//unselected
                viewHolder.checkIV.setVisibility(View.INVISIBLE);
                viewHolder.textView.setTextColor(getResources().getColor(R.color.gray));
                viewHolder.relativeLayout.setBackgroundColor(getResources().getColor(R.color.transparency_gray));
            }
            return convertView;
        }

        class ViewHolder{
            RelativeLayout relativeLayout;
            TextView textView;
            ImageView checkIV;
        }
    }

    class GetAllCourseFromDB extends AsyncTask<Integer, Integer, List<Course>> {

        Context context;
        ProgressDialog progressDialog;
        public GetAllCourseFromDB(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(context, null, getString(R.string.loading));
        }
        @Override
        protected List<Course> doInBackground(Integer... params) {
            CourseDBHelper courseDBHelper = CourseDBHelper.getInstance(context);
            return courseDBHelper.getAllCourses();
        }

        @Override
        protected void onPostExecute(List<Course> result){
            if(result == null || result.isEmpty()){
                if(Util.isConnect(context)) {
                    GetAllCoursesFromServerAsync getAllCoursesFromServerAsync = new GetAllCoursesFromServerAsync(context);
                    getAllCoursesFromServerAsync.execute();
                }else{
                    mFailedRL.setVisibility(View.VISIBLE);
                    Toast.makeText(context, getString(R.string.network_error),Toast.LENGTH_SHORT).show();
                }
            }
            else{
                mCourses = result;
                courseItemAdapter.notifyDataSetChanged();
            }
            progressDialog.dismiss();
        }
    }

    class GetAllCoursesFromServerAsync extends AsyncTask<Integer, Integer, ArrayList<Course>>{

        ProgressDialog progressDialog;
        Context context;
        public GetAllCoursesFromServerAsync(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute(){
            mFailedRL.setVisibility(View.GONE);
            progressDialog = ProgressDialog.show(context, null,getString(R.string.getting_courses));
        }

        @Override
        protected ArrayList<Course> doInBackground(Integer... integers) {
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            ArrayList<Course> allCourses = null;
            try {
                allCourses = okHttpUtil.getAllCoursesInfo();
            } catch (IOException e) {
                Log.e("Error:", "Get Courses AsyncTask"+ e.toString());
                return null;
            }
            if(allCourses == null || allCourses.isEmpty() )return null;
            //insert All Courses into db
            CourseDBHelper courseDBHelper = CourseDBHelper.getInstance(context);
            courseDBHelper.insertCourses(allCourses);
            return allCourses;
        }

        @Override
        protected void onPostExecute(ArrayList<Course> result){
            if(result == null || result.isEmpty()){
                mFailedRL.setVisibility(View.VISIBLE);
            }else{
                mFailedRL.setVisibility(View.GONE);
                mCourses = result;
                courseItemAdapter.notifyDataSetChanged();
            }
            progressDialog.dismiss();
        }
    }
}
