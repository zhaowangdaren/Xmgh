package com.example.ustc_pc.myapplication.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DONE_QUESTION".
*/
public class DoneQuestionDao extends AbstractDao<DoneQuestion, Void> {

    public static final String TABLENAME = "DONE_QUESTION";

    /**
     * Properties of entity DoneQuestion.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property ICourseID = new Property(0, Integer.class, "iCourseID", false, "I_COURSE_ID");
        public final static Property IQuestionType = new Property(1, Integer.class, "iQuestionType", false, "I_QUESTION_TYPE");
        public final static Property IQuestionID = new Property(2, Integer.class, "iQuestionID", false, "I_QUESTION_ID");
        public final static Property IsFavorite = new Property(3, Boolean.class, "isFavorite", false, "IS_FAVORITE");
        public final static Property IsCorrect = new Property(4, Boolean.class, "isCorrect", false, "IS_CORRECT");
        public final static Property StrNote = new Property(5, String.class, "strNote", false, "STR_NOTE");
        public final static Property StrUserAnswer = new Property(6, String.class, "strUserAnswer", false, "STR_USER_ANSWER");
        public final static Property ISpendTime = new Property(7, Integer.class, "iSpendTime", false, "I_SPEND_TIME");
    };


    public DoneQuestionDao(DaoConfig config) {
        super(config);
    }
    
    public DoneQuestionDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DONE_QUESTION\" (" + //
                "\"I_COURSE_ID\" INTEGER," + // 0: iCourseID
                "\"I_QUESTION_TYPE\" INTEGER," + // 1: iQuestionType
                "\"I_QUESTION_ID\" INTEGER," + // 2: iQuestionID
                "\"IS_FAVORITE\" INTEGER," + // 3: isFavorite
                "\"IS_CORRECT\" INTEGER," + // 4: isCorrect
                "\"STR_NOTE\" TEXT," + // 5: strNote
                "\"STR_USER_ANSWER\" TEXT," + // 6: strUserAnswer
                "\"I_SPEND_TIME\" INTEGER);"); // 7: iSpendTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DONE_QUESTION\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, DoneQuestion entity) {
        stmt.clearBindings();
 
        Integer iCourseID = entity.getICourseID();
        if (iCourseID != null) {
            stmt.bindLong(1, iCourseID);
        }
 
        Integer iQuestionType = entity.getIQuestionType();
        if (iQuestionType != null) {
            stmt.bindLong(2, iQuestionType);
        }
 
        Integer iQuestionID = entity.getIQuestionID();
        if (iQuestionID != null) {
            stmt.bindLong(3, iQuestionID);
        }
 
        Boolean isFavorite = entity.getIsFavorite();
        if (isFavorite != null) {
            stmt.bindLong(4, isFavorite ? 1L: 0L);
        }
 
        Boolean isCorrect = entity.getIsCorrect();
        if (isCorrect != null) {
            stmt.bindLong(5, isCorrect ? 1L: 0L);
        }
 
        String strNote = entity.getStrNote();
        if (strNote != null) {
            stmt.bindString(6, strNote);
        }
 
        String strUserAnswer = entity.getStrUserAnswer();
        if (strUserAnswer != null) {
            stmt.bindString(7, strUserAnswer);
        }
 
        Integer iSpendTime = entity.getISpendTime();
        if (iSpendTime != null) {
            stmt.bindLong(8, iSpendTime);
        }
    }

    /** @inheritdoc */
    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    /** @inheritdoc */
    @Override
    public DoneQuestion readEntity(Cursor cursor, int offset) {
        DoneQuestion entity = new DoneQuestion( //
            cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0), // iCourseID
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // iQuestionType
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // iQuestionID
            cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0, // isFavorite
            cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0, // isCorrect
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // strNote
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // strUserAnswer
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7) // iSpendTime
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DoneQuestion entity, int offset) {
        entity.setICourseID(cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0));
        entity.setIQuestionType(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setIQuestionID(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setIsFavorite(cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0);
        entity.setIsCorrect(cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0);
        entity.setStrNote(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setStrUserAnswer(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setISpendTime(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Void updateKeyAfterInsert(DoneQuestion entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    /** @inheritdoc */
    @Override
    public Void getKey(DoneQuestion entity) {
        return null;
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
