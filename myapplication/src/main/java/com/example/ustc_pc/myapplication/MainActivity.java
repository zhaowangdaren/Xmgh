package com.example.ustc_pc.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ustc_pc.myapplication.activity.ActivityHistoryTest;
import com.example.ustc_pc.myapplication.activity.LoginActivity;
import com.example.ustc_pc.myapplication.activity.ActivityPersonal;
import com.example.ustc_pc.myapplication.activity.ActivityUserBook;
import com.example.ustc_pc.myapplication.activity.ActivityZhenTi;
import com.example.ustc_pc.myapplication.dao.Course;
import com.example.ustc_pc.myapplication.db.CourseDBHelper;
import com.example.ustc_pc.myapplication.db.UserSharedPreference;
import com.example.ustc_pc.myapplication.fragment.CourseFragment;
import com.example.ustc_pc.myapplication.net.Util;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener, CourseFragment.OnFragmentInteractionListener{

    public static final String ARG_SECTION_NUMBER = "section_number";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
//    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private int mICurCourseID = 1;


    private Toolbar toolbar;
    private DrawerLayout mLeftMenuDrawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private boolean isDrawerOpen;

    // View
    private ImageView mHeadIV;
    private TextView mUserNameTV;

    private ListView mLeftCoursesLV;
    private List<Course> mSelectedCourses;

    private RelativeLayout mHeaderAreaRL, mLeftMenuRL;

    private PopupWindow mMenuPW;
    private TextView mMenuPosTV;

    private Button mErrorBookBT, mErrorTestBT, mTestHistoryBT, mZhenTiBT, mNoteBookBT, mFavotirtBookBT;
    /**
     *
     * @param savedInstanceState
     */

    FragmentManager mFragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = getTitle();
        mFragmentManager = getSupportFragmentManager();
        try {
            initToolbar();

            initLeftMenuView();
            initMainContent();
            initTopRightCornerMenu();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void initMainContent() {
        int lastCourseID = new UserSharedPreference(this).getLastCouresID();
        mICurCourseID = lastCourseID;
        if(lastCourseID != Util.NO_LAST_COURSE){
            mFragmentManager.beginTransaction()
                    .replace(R.id.container, CourseFragment.newInstance(lastCourseID))
                    .commit();
            mLeftMenuDrawerLayout.closeDrawers();
            UserSharedPreference userSharedPreference = new UserSharedPreference(this);
            userSharedPreference.setLastCourseID(lastCourseID);
        }else{
            mLeftMenuDrawerLayout.openDrawer(mLeftMenuRL);
        }
    }

    private void initLeftMenuView() {
        mLeftMenuRL = (RelativeLayout)findViewById(R.id.relativeLayout_left_menu);
        initLeftCourseMenu();
        initLeftPersonalInfo();
    }

    private void initLeftPersonalInfo() {
        mHeadIV = (ImageView)findViewById(R.id.imageView_menu_head);
        mUserNameTV = (TextView)findViewById(R.id.textView_drawer_nick_name);
        mHeaderAreaRL = (RelativeLayout)findViewById(R.id.relativeLayout_menu_head_area);
        mHeaderAreaRL.setOnClickListener(this);

        mUserNameTV.setText(new UserSharedPreference(this).getStrUserName());
    }

    CoursesAdapter mLeftCourseAdapter ;
    private void initLeftCourseMenu(){
        mLeftCoursesLV = (ListView)findViewById(R.id.listView_navigation);
        mSelectedCourses = new ArrayList<>();
        mLeftCourseAdapter = new CoursesAdapter();
        GetSelectedCourseImageViewAsyncTask getSelectedCourseAsyncTask = new GetSelectedCourseImageViewAsyncTask(this);
        getSelectedCourseAsyncTask.execute();

        mLeftCoursesLV.setAdapter(mLeftCourseAdapter);
        mLeftCoursesLV.setOnItemClickListener(this);
    }

    class GetSelectedCourseImageViewAsyncTask extends AsyncTask<Integer, Integer, List<Course>> {

        Context context;
        public GetSelectedCourseImageViewAsyncTask(Context context){
            this.context = context;
        }
        @Override
        protected List<Course> doInBackground(Integer... params) {
            CourseDBHelper courseDBHelper = CourseDBHelper.getInstance(context);

            return courseDBHelper.getUserSelectedCourses();
        }

        @Override
        protected void onPostExecute(List<Course> result){
            if(result != null && result.size() > 0) {
                mSelectedCourses.clear();
                mSelectedCourses.addAll(result);
            }
        }
    }

    /**
     * It is for the top right corner menu
     */
    private void initTopRightCornerMenu() {
        mMenuPosTV = (TextView)findViewById(R.id.textView_hider_toolbar);
        View view = View.inflate(this, R.layout.menu_more_my,null);
        mMenuPW = new PopupWindow(view, android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
//        mMenuPW.setWidth(300);
//        mMenuPW.setHeight(600);
        //加上下面三句就可以在popWin外点击让popWin消失，同时popWin内也可以获取焦点
        mMenuPW.setFocusable(true);
        mMenuPW.setOutsideTouchable(true);
        mMenuPW.setBackgroundDrawable(new PaintDrawable());


        mMenuPW.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.alpha = 1.0f;//设置为不透明，即恢复原来的界面
                getWindow().setAttributes(params);
            }
        });

        mErrorBookBT = (Button)view.findViewById(R.id.button_error_book);
        mErrorTestBT = (Button)view.findViewById(R.id.button_error_test);
        mTestHistoryBT = (Button)view.findViewById(R.id.button_test_history);
        mZhenTiBT = (Button)view.findViewById(R.id.button_zhen_ti);
        mNoteBookBT = (Button)view.findViewById(R.id.button_note_book);
        mFavotirtBookBT = (Button)view.findViewById(R.id.button_favorite_book);

        mErrorBookBT.setOnClickListener(this);
        mErrorTestBT.setOnClickListener(this);
        mTestHistoryBT.setOnClickListener(this);
        mZhenTiBT.setOnClickListener(this);
        mNoteBookBT.setOnClickListener(this);
        mFavotirtBookBT.setOnClickListener(this);
    }

    /**
     * The User Head ImageView on the left menu
     */
    private void initHeadIV() {
        UserSharedPreference userSharedPreference = new UserSharedPreference(this);
        Bitmap bitmap = userSharedPreference.getUserPhotoBitmap();
        if(bitmap != null) mHeadIV.setImageBitmap(bitmap);
    }

    private void initToolbar(){
        toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLeftMenuDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mLeftMenuDrawerLayout, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close){
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                isDrawerOpen = false;
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                isDrawerOpen = true;
            }
        };
        actionBarDrawerToggle.syncState();
        mLeftMenuDrawerLayout.setDrawerListener(actionBarDrawerToggle);

    }

    @Override
    public void onResume(){
        super.onResume();
        UserSharedPreference userSharedPreference = new UserSharedPreference(this);
        boolean isLogin = userSharedPreference.getIsLogin();
        if( !isLogin ){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        mUserNameTV.setText(userSharedPreference.getStrUserName());
        initHeadIV();

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int iCourseID = mSelectedCourses.get(position).getICourseID();
        mICurCourseID = iCourseID;
        mFragmentManager.beginTransaction()
                .replace(R.id.container, CourseFragment.newInstance(iCourseID))
                .commit();
        mLeftMenuDrawerLayout.closeDrawers();
        UserSharedPreference userSharedPreference = new UserSharedPreference(this);
        userSharedPreference.setLastCourseID(iCourseID);
    }

    public void onSectionAttached(int number) {
        if(mSelectedCourses == null){
            GetSelectedCourseImageViewAsyncTask getSelectedCourseAsyncTask = new GetSelectedCourseImageViewAsyncTask(this);
            getSelectedCourseAsyncTask.execute();
        }
        mTitle = mSelectedCourses.get(number).getStrCourseName();

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //if (!mNavigationDrawerFragment.isDrawerOpen()) {
        if(!isDrawerOpen){
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.action_settings:
                WindowManager.LayoutParams lp=getWindow().getAttributes();
                lp.alpha = 0.5f;
                getWindow().setAttributes(lp);
                mMenuPW.showAsDropDown(mMenuPosTV,0,4);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast toast = Toast.makeText(this, "Wheeee!", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.relativeLayout_menu_head_area:
                intent = new Intent();
                intent.setClass(this, ActivityPersonal.class);
                startActivity(intent);
                break;
            case R.id.button_error_book:
                intent = new Intent();
                intent.setClass(this, ActivityUserBook.class);
                intent.putExtra(ARG_SECTION_NUMBER, mICurCourseID);
                intent.putExtra("TYPE",ActivityUserBook.TYPE_ERROR_BOOK);
                startActivity(intent);
                break;
            case R.id.button_error_test:
                intent = new Intent();
                intent.setClass(this, ActivityUserBook.class);
                intent.putExtra(ARG_SECTION_NUMBER, mICurCourseID);
                intent.putExtra("TYPE", ActivityUserBook.TYPE_ERROR_TEST);
                startActivity(intent);
                break;
            case R.id.button_test_history:
                intent = new Intent();
                intent.setClass(this, ActivityHistoryTest.class);
                intent.putExtra(ARG_SECTION_NUMBER, mICurCourseID);
                startActivity(intent);
                break;
            case R.id.button_zhen_ti:
                intent = new Intent();
                intent.setClass(this, ActivityZhenTi.class);
                intent.putExtra(ARG_SECTION_NUMBER, mICurCourseID);
                startActivity(intent);
                break;
            case R.id.button_note_book:
                intent = new Intent();
                intent.setClass(this, ActivityUserBook.class);
                intent.putExtra(ARG_SECTION_NUMBER, mICurCourseID);
                intent.putExtra("TYPE",ActivityUserBook.TYPE_NOTE_BOOK);
                startActivity(intent);
                break;
            case R.id.button_favorite_book:
                intent = new Intent();
                intent.setClass(this, ActivityUserBook.class);
                intent.putExtra(ARG_SECTION_NUMBER, mICurCourseID);
                intent.putExtra("TYPE", ActivityUserBook.TYPE_FAVORITE_BOOK);
                startActivity(intent);
                break;
        }
    }

    class CoursesAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mSelectedCourses.size();
        }

        @Override
        public Object getItem(int position) {
            return mSelectedCourses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CourseViewHolder viewHolder ;
            if(convertView == null){
                convertView = View.inflate(parent.getContext(), R.layout.layout_left_menu_courses_item, null);
                viewHolder = new CourseViewHolder();
                viewHolder.iconIV = (ImageView)convertView.findViewById(R.id.imageView_left_menu_course_ic);
                viewHolder.nameTV = (TextView)convertView.findViewById(R.id.textView_left_menu_course_name);
            }else{
                viewHolder = (CourseViewHolder) convertView.getTag();
            }
            String name = mSelectedCourses.get(position).getStrCourseName();
            viewHolder.nameTV.setText(name);

            if(name.equals(getString(R.string.politics))){
                viewHolder.iconIV.setImageResource(R.mipmap.ic_politics);
            }else
            if(name.equals(getString(R.string.english_1))){
                viewHolder.iconIV.setImageResource(R.mipmap.ic_english_1);
            }else
            if(name.equals(getString(R.string.english_2))){
                viewHolder.iconIV.setImageResource(R.mipmap.ic_english_2);
            }

            convertView.setTag(viewHolder);
            return convertView;
        }

        class CourseViewHolder{
            TextView nameTV;
            ImageView iconIV;
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_unuse, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

}
