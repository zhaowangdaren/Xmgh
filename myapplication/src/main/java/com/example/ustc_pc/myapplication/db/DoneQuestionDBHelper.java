package com.example.ustc_pc.myapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.ustc_pc.myapplication.activity.ShowRecordedQueActivity;
import com.example.ustc_pc.myapplication.dao.DaoMaster;
import com.example.ustc_pc.myapplication.dao.DaoSession;
import com.example.ustc_pc.myapplication.dao.DoneQuestion;
import com.example.ustc_pc.myapplication.dao.DoneQuestionDao;
import com.example.ustc_pc.myapplication.dao.KPs;
import com.example.ustc_pc.myapplication.unit.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ustc_zy on 2015/8/6.
 */
public class DoneQuestionDBHelper {
    private static Context mContext;
    private static DoneQuestionDBHelper instance;

    private DoneQuestionDao questionDao;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private SQLiteDatabase db;

    private DoneQuestionDBHelper(Context context){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(
                context, Util.DB_NAME, null
        );
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        questionDao = daoSession.getDoneQuestionDao();
    }

    public static DoneQuestionDBHelper getInstance(Context context){
        if(instance == null){
            instance = new DoneQuestionDBHelper(context);
            if(mContext == null )mContext = context;
        }
        return instance;
    }

    public void insertDoneQue(List<DoneQuestion> doneQuestions){
        if(doneQuestions == null)return;
        for(DoneQuestion doneQuestion : doneQuestions){
            questionDao.insertOrReplace(doneQuestion);
        }
    }

    public void updateDoneQuestions(List<DoneQuestion> doneQuestions){
        if(doneQuestions == null)return;
        for(DoneQuestion doneQuestion : doneQuestions){
            List<DoneQuestion> questionsDB = questionDao.queryBuilder()
                    .where(DoneQuestionDao.Properties.LQuestionID.eq(doneQuestion.getLQuestionID()))
                    .list();
            if(questionsDB != null && !questionsDB.isEmpty()){
                questionsDB.get(0).setStrNote(doneQuestion.getStrNote());
                questionsDB.get(0).setIsFavorite(doneQuestion.getIsFavorite());
                questionDao.update(questionsDB.get(0));
            }else{
                questionDao.insertOrReplace(doneQuestion);
            }
        }
    }

    public List<String> queryErrorKPs(int iCourseID) {
        List<DoneQuestion> errorQues = questionDao.queryBuilder()
                .where(DoneQuestionDao.Properties.ICourseID.eq(iCourseID),DoneQuestionDao.Properties.IsCorrect.eq(false))
                .orderAsc(DoneQuestionDao.Properties.StrQuestionKpID)
                .list();
        List<String> result = new ArrayList<>();
        String lastKPID = null;
        for(DoneQuestion doneQuestion : errorQues){
            String kpID = doneQuestion.getStrQuestionKpID();
            if(lastKPID == null || !lastKPID.equals(kpID)){
                lastKPID = kpID;
                result.add(kpID);
            }
        }
        return result;
    }

    public List<String> queryFavoriteKPs(int iCourseID) {
        List<DoneQuestion> favoriteQues = questionDao.queryBuilder()
                .where(DoneQuestionDao.Properties.ICourseID.eq(iCourseID),DoneQuestionDao.Properties.IsFavorite.eq(true))
                .orderAsc(DoneQuestionDao.Properties.StrQuestionKpID)
                .list();
        List<String> result = new ArrayList<>();
        String lastKPID = null;
        for(DoneQuestion doneQuestion : favoriteQues){
            String kpID = doneQuestion.getStrQuestionKpID();
            if(lastKPID == null || !lastKPID.equals(kpID)){
                lastKPID = kpID;
                result.add(lastKPID);
            }
        }
        return result;
    }

    public List<String> queryNoteKPs(int iCourseID) {
        List<DoneQuestion> favoriteQues = questionDao.queryBuilder()
                .where(DoneQuestionDao.Properties.ICourseID.eq(iCourseID)
                        ,DoneQuestionDao.Properties.StrNote.isNotNull())
                .orderAsc(DoneQuestionDao.Properties.StrQuestionKpID)
                .list();
        List<String> result = new ArrayList<>();
        String lastKPID = null;
        for(DoneQuestion doneQuestion : favoriteQues){
            String kpID = doneQuestion.getStrQuestionKpID();
            if(lastKPID == null || !lastKPID.equals(kpID)){
                lastKPID = kpID;
                result.add(lastKPID);
            }
        }
        return result;
    }

    public void deleteAll(int iUserID) {
        questionDao.deleteAll();
    }

    public List<DoneQuestion> queryRecordedQue(int iCourseID, KPs kps, int type) {
        List<DoneQuestion> recordedQues = null;
        switch (type){
            case ShowRecordedQueActivity.TYPE_ERROR_QUE:
                recordedQues = questionDao.queryBuilder()
                        .where(DoneQuestionDao.Properties.ICourseID.eq(iCourseID)
                                , DoneQuestionDao.Properties.IsCorrect.eq(false)
                                , DoneQuestionDao.Properties.StrQuestionKpID.eq(kps.getStrKPID()))
                        .orderAsc(DoneQuestionDao.Properties.LQuestionID)
                        .list();
                break;
            case ShowRecordedQueActivity.TYPE_FAV_QUE:
                recordedQues = questionDao.queryBuilder()
                        .where(DoneQuestionDao.Properties.ICourseID.eq(iCourseID)
                                , DoneQuestionDao.Properties.IsFavorite.eq(true)
                                , DoneQuestionDao.Properties.StrQuestionKpID.eq(kps.getStrKPID()))
                        .orderAsc(DoneQuestionDao.Properties.LQuestionID)
                        .list();
                break;
            case ShowRecordedQueActivity.TYPE_NOTE_QUE:
                recordedQues = questionDao.queryBuilder()
                        .where(DoneQuestionDao.Properties.ICourseID.eq(iCourseID)
                                , DoneQuestionDao.Properties.StrNote.isNotNull()
                                , DoneQuestionDao.Properties.StrQuestionKpID.eq(kps.getStrKPID()))
                        .orderAsc(DoneQuestionDao.Properties.LQuestionID)
                        .list();
                break;
        }
        return recordedQues;
    }

    public List<DoneQuestion> queryRecordedQue(Integer iCourseID, Integer iTestType, String strKPID, Integer iShowType) {
        List<DoneQuestion> recordedQues = null;
        switch (iShowType){
            case ShowRecordedQueActivity.TYPE_ERROR_QUE:
                recordedQues = questionDao.queryBuilder()
                        .where(DoneQuestionDao.Properties.ICourseID.eq(iCourseID)
                                , DoneQuestionDao.Properties.IsCorrect.eq(false)
                                , DoneQuestionDao.Properties.IQuestionType.eq(iTestType)
                                , DoneQuestionDao.Properties.StrQuestionKpID.eq(strKPID))
                        .orderAsc(DoneQuestionDao.Properties.LQuestionID)
                        .list();
                break;
            case ShowRecordedQueActivity.TYPE_FAV_QUE:
                recordedQues = questionDao.queryBuilder()
                        .where(DoneQuestionDao.Properties.ICourseID.eq(iCourseID)
                                , DoneQuestionDao.Properties.IsFavorite.eq(true)
                                , DoneQuestionDao.Properties.IQuestionType.eq(iTestType)
                                , DoneQuestionDao.Properties.StrQuestionKpID.eq(strKPID))
                        .orderAsc(DoneQuestionDao.Properties.LQuestionID)
                        .list();
                break;
            case ShowRecordedQueActivity.TYPE_NOTE_QUE:
                recordedQues = questionDao.queryBuilder()
                        .where(DoneQuestionDao.Properties.ICourseID.eq(iCourseID)
                                , DoneQuestionDao.Properties.StrNote.isNotNull()
                                , DoneQuestionDao.Properties.IQuestionType.eq(iTestType)
                                , DoneQuestionDao.Properties.StrQuestionKpID.eq(strKPID))
                        .orderAsc(DoneQuestionDao.Properties.LQuestionID)
                        .list();
                break;
        }
        return recordedQues;
    }
}
