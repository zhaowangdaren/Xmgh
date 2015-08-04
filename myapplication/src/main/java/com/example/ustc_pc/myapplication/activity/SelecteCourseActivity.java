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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.dao.Course;
import com.example.ustc_pc.myapplication.db.CourseDBHelper;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.net.OkHttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelecteCourseActivity extends AppCompatActivity implements AbsListView.OnItemClickListener ,View.OnClickListener{

    GridView gridView;
    List<Course> mCourses;
    CourseItemAdapter courseItemAdapter;

    Button mFinishBT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_course);

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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mCourses.get(position).getIsSelected())mCourses.get(position).setIsSelected(false);
        else mCourses.get(position).setIsSelected(true);
        courseItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_finished:
                FinishSelectCourseAsyncTask finishSelectCourseAsyncTask = new FinishSelectCourseAsyncTask(this);
                finishSelectCourseAsyncTask.execute(mCourses);
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

            OkHttpUtil okHttpUtil = new OkHttpUtil();
            try {
                okHttpUtil.addCourse(new UserSharedPreference(context).getiUserID(),params[0]);
            } catch (IOException e) {
                Log.e("Error ", e.toString());
            }
            return true;
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
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.textView.setText(mCourses.get(position).getStrCourseName());
            if(mCourses.get(position).getIsSelected())viewHolder.relativeLayout.setBackgroundColor(getResources().getColor(R.color.gray));
            else viewHolder.relativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
            return convertView;
        }

        class ViewHolder{
            RelativeLayout relativeLayout;
            TextView textView;
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
            if(result == null || result.isEmpty())return;
            else{
                mCourses = result;
            }
            progressDialog.dismiss();
        }
    }
}
