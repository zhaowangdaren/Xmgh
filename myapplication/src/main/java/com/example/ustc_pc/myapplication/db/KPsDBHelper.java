package com.example.ustc_pc.myapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.ustc_pc.myapplication.dao.DaoMaster;
import com.example.ustc_pc.myapplication.dao.DaoSession;
import com.example.ustc_pc.myapplication.dao.KPs;
import com.example.ustc_pc.myapplication.dao.KPsDao;
import com.example.ustc_pc.myapplication.unit.Util;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by ustc-pc on 2015/7/30.
 */
public class KPsDBHelper {
    private static KPsDBHelper instance;

    private static Context mContext;
    private KPsDao kPsDao;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private SQLiteDatabase db;

    private KPsDBHelper(Context context){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, Util.DB_NAME, null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        kPsDao = daoSession.getKPsDao();
    }

    public static KPsDBHelper getInstance(Context context){
        if(instance == null){
            instance = new KPsDBHelper(context);
            if(mContext == null)mContext = context;
        }
        return instance;
    }
    public void insertKPses(List<KPs> kPses){
        if(kPses == null)return;
        for(KPs kPs : kPses){
            kPsDao.insert(kPs);
        }
    }

    public void updateKPses(List<KPs> kPses){
        if(kPses == null)return;
        for(KPs kPs : kPses){
            kPsDao.update(kPs);
        }
    }

    public List<KPs> getAllKPs(){
        return kPsDao.loadAll();
    }

    public List<KPs> getCourseKPs(int iCourseID){
        QueryBuilder<KPs> queryBuilder = kPsDao.queryBuilder();
        queryBuilder.where(KPsDao.Properties.ICourseID.eq(iCourseID));
        return queryBuilder.list();
    }

    public List<KPs> queryKPsByKPID(int iCourseID, List<String> strKPIDs) {
        if (strKPIDs == null)return null;
        List<KPs> kPses = kPsDao.queryBuilder()
                .where(KPsDao.Properties.StrKPID.in(strKPIDs), KPsDao.Properties.ICourseID.eq(iCourseID))
                .list();

        return kPses;
    }
}
