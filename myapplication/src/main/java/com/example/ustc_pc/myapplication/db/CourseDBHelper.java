package com.example.ustc_pc.myapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.ustc_pc.myapplication.dao.Course;
import com.example.ustc_pc.myapplication.dao.CourseDao;
import com.example.ustc_pc.myapplication.dao.DaoMaster;
import com.example.ustc_pc.myapplication.dao.DaoSession;
import com.example.ustc_pc.myapplication.net.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ustc-pc on 2015/7/28.
 */
public class CourseDBHelper {
    private static Context mContext;

    private static CourseDBHelper instance;
    private CourseDao courseDao;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private SQLiteDatabase db;
    private CourseDBHelper(Context context){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, Util.DB_NAME, null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        courseDao = daoSession.getCourseDao();
    }

    public static CourseDBHelper getInstance(Context context){
        if(instance == null){
            instance = new CourseDBHelper(context);
            if(mContext == null)mContext = context;
        }
        return  instance;
    }

    public void insertCourses(List<Course> allCourses) {
        if(allCourses == null)return;
        for(Course course : allCourses){
            List<Course> courseDBs = courseDao.queryBuilder()
                    .where(CourseDao.Properties.ICourseID.eq(course.getICourseID()))
                    .list();
            if(courseDBs != null && !courseDBs.isEmpty()){
                courseDBs.get(0).setIsSelected(course.getIsSelected());
                courseDao.update(courseDBs.get(0));
            }else {
                courseDao.insertOrReplace(course);
            }
        }
    }

    public void updateCourses(List<Course> courses){
        if(courses == null)return;
        for(Course course : courses){
            List<Course> courseDBs = courseDao.queryBuilder()
                    .where(CourseDao.Properties.ICourseID.eq(course.getICourseID()))
                    .list();
            if(courseDBs != null && !courseDBs.isEmpty()
                    && (courseDBs.get(0).getIsSelected() != course.getIsSelected())){
                courseDBs.get(0).setIsSelected(course.getIsSelected());
                courseDao.update(courseDBs.get(0));
            }
        }
    }

    public List<Course> getAllCourses(){
        return courseDao.loadAll();
    }


    public List<Course> getUserSelectedCourses(){
        List<Course> result = courseDao.loadAll();
        ArrayList<Course> selectedCourses = new ArrayList<>();
        for(Course course : result){
            if(course.getIsSelected())selectedCourses.add(course);
        }
        return selectedCourses;
    }

    public void updateCourse(Course course) {
        if(course == null)return;
        courseDao.update(course);
    }
}
