package com.example.ustc_pc.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ustc_pc.myapplication.R;
import com.example.ustc_pc.myapplication.dao.Course;
import com.example.ustc_pc.myapplication.db.CourseDBHelper;
import com.example.ustc_pc.myapplication.fragment.CourseFragment;

import java.util.ArrayList;
import java.util.List;

public class CourseActivity extends AppCompatActivity implements CourseFragment.OnFragmentInteractionListener {

    private List<Course> mSelectedCourses;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private RelativeLayout mRelativeLayoutLeftMenu , mRelativeLayoutLeftHead;
    private ListView mLeftMenuLV;
    private Button mAddCourseBT;

    private CharSequence mTitle , mDrawerTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        mTitle = mDrawerTitle = getTitle();
        mSelectedCourses = new ArrayList<>();
        GetSelectedCoursesAsyncTask getSelectedCoursesAsyncTask = new GetSelectedCoursesAsyncTask(this);
        getSelectedCoursesAsyncTask.execute();
        initLeftMenu();
    }

    private void initLeftMenu() {
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name,R.string.navigation_drawer_close){
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
            }
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mRelativeLayoutLeftMenu = (RelativeLayout)findViewById(R.id.relativeLayout_left_menu);
        mRelativeLayoutLeftHead = (RelativeLayout)findViewById(R.id.relativeLayout_left_menu_head);
        mLeftMenuLV = (ListView)findViewById(R.id.listView_left_drawer);
        mAddCourseBT = (Button)findViewById(R.id.button_left_menu_add_course);

        LeftMenuLVAdapter leftMenuLVAdapter = new LeftMenuLVAdapter(this);
        mLeftMenuLV.setAdapter(leftMenuLVAdapter);
        mLeftMenuLV.setOnItemClickListener(new LeftMenuLVItemClickListener());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class LeftMenuLVItemClickListener implements ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            changeCourse(position);
        }
    }

    private void changeCourse(int position) {
        CourseFragment courseFragment = CourseFragment.newInstance(mSelectedCourses.get(position).getICourseID());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, courseFragment)
                .commit();
        mDrawerLayout.closeDrawer(mRelativeLayoutLeftMenu);

        mLeftMenuLV.setItemChecked(position, true);
        this.setTitle(mSelectedCourses.get(position).getStrCourseName());
    }

    @Override
    public void setTitle(CharSequence title){
        mTitle = title;
        try {
            getSupportActionBar().setTitle(mTitle);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class LeftMenuLVAdapter extends BaseAdapter{

        Context context;
        public LeftMenuLVAdapter(Context context){
            this.context = context;
        }
        @Override
        public int getCount() {
            return mSelectedCourses.size();
        }

        @Override
        public Course getItem(int i) {
            return mSelectedCourses.get(i);
        }

        @Override
        public long getItemId(int i) {
            return mSelectedCourses.get(i).getICourseID();
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            LeftMenuViewHolder leftMenuViewHolder;
            if(view == null){
                leftMenuViewHolder = new LeftMenuViewHolder();
                view = View.inflate(context, R.layout.layout_left_menu_courses_item,null);
                leftMenuViewHolder.iv =
                        (ImageView)view.findViewById(R.id.imageView_left_menu_course_ic);
                leftMenuViewHolder.tv =
                        (TextView)view.findViewById(R.id.textView_left_menu_course_name);
                view.setTag(leftMenuViewHolder);
            }else{
                leftMenuViewHolder = (LeftMenuViewHolder) view.getTag();
            }
            leftMenuViewHolder.tv.setText(getItem(position).getStrCourseName());
            return view;
        }

        class LeftMenuViewHolder{
            public ImageView iv;
            public TextView tv;

        }
    }
    private class GetSelectedCoursesAsyncTask extends AsyncTask<Integer, Integer, List<Course>>{

        ProgressDialog progressDialog;
        Context context;
        public GetSelectedCoursesAsyncTask(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(context, null,getString(R.string.loading));

        }
        @Override
        protected List<Course> doInBackground(Integer... integers) {
            CourseDBHelper courseDBHelper = CourseDBHelper.getInstance(context);
            List<Course> courses = courseDBHelper.getUserSelectedCourses();
            return courses;
        }

        @Override
        protected void onPostExecute(List<Course> result){
            if(result != null && !result.isEmpty())mSelectedCourses.addAll(result);
            progressDialog.dismiss();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_course, menu);
        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mRelativeLayoutLeftMenu);
        menu.findItem(R.id.action_more).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

}
