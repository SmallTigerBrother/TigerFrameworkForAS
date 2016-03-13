package com.mn.tiger.download.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.mn.tiger.download.db.Downloader;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DOWNLOADER".
*/
public class DownloaderDao extends AbstractDao<Downloader, Long> {

    public static final String TABLENAME = "DOWNLOADER";

    /**
     * Properties of entity Downloader.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "ID");
        public final static Property Url = new Property(1, String.class, "url", false, "URL");
        public final static Property Params = new Property(2, String.class, "params", false, "PARAMS");
        public final static Property FileSize = new Property(3, long.class, "fileSize", false, "FILE_SIZE");
        public final static Property CompleteSize = new Property(4, long.class, "completeSize", false, "COMPLETE_SIZE");
        public final static Property DownloadStatus = new Property(5, int.class, "downloadStatus", false, "DOWNLOAD_STATUS");
        public final static Property SavePath = new Property(6, String.class, "savePath", false, "SAVE_PATH");
        public final static Property RequestType = new Property(7, int.class, "requestType", false, "REQUEST_TYPE");
        public final static Property CheckKey = new Property(8, String.class, "checkKey", false, "CHECK_KEY");
        public final static Property AccessRanges = new Property(9, boolean.class, "accessRanges", false, "ACCESS_RANGES");
        public final static Property ErrorCode = new Property(10, int.class, "errorCode", false, "ERROR_CODE");
        public final static Property ErrorMsg = new Property(11, String.class, "errorMsg", false, "ERROR_MSG");
        public final static Property TaskClsName = new Property(12, String.class, "taskClsName", false, "TASK_CLS_NAME");
        public final static Property ParamsClsName = new Property(13, String.class, "paramsClsName", false, "PARAMS_CLS_NAME");
        public final static Property DownloadType = new Property(14, String.class, "downloadType", false, "DOWNLOAD_TYPE");
        public final static Property CreateTime = new Property(15, java.util.Date.class, "createTime", false, "CREATE_TIME");
        public final static Property SoftDelete = new Property(16, boolean.class, "softDelete", false, "SOFT_DELETE");
        public final static Property Extras = new Property(17, String.class, "extras", false, "EXTRAS");
    };


    public DownloaderDao(DaoConfig config) {
        super(config);
    }
    
    public DownloaderDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DOWNLOADER\" (" + //
                "\"ID\" INTEGER PRIMARY KEY NOT NULL ," + // 0: id
                "\"URL\" TEXT NOT NULL ," + // 1: url
                "\"PARAMS\" TEXT," + // 2: params
                "\"FILE_SIZE\" INTEGER NOT NULL ," + // 3: fileSize
                "\"COMPLETE_SIZE\" INTEGER NOT NULL ," + // 4: completeSize
                "\"DOWNLOAD_STATUS\" INTEGER NOT NULL ," + // 5: downloadStatus
                "\"SAVE_PATH\" TEXT NOT NULL ," + // 6: savePath
                "\"REQUEST_TYPE\" INTEGER NOT NULL ," + // 7: requestType
                "\"CHECK_KEY\" TEXT," + // 8: checkKey
                "\"ACCESS_RANGES\" INTEGER NOT NULL ," + // 9: accessRanges
                "\"ERROR_CODE\" INTEGER NOT NULL ," + // 10: errorCode
                "\"ERROR_MSG\" TEXT," + // 11: errorMsg
                "\"TASK_CLS_NAME\" TEXT NOT NULL ," + // 12: taskClsName
                "\"PARAMS_CLS_NAME\" TEXT NOT NULL ," + // 13: paramsClsName
                "\"DOWNLOAD_TYPE\" TEXT," + // 14: downloadType
                "\"CREATE_TIME\" INTEGER NOT NULL ," + // 15: createTime
                "\"SOFT_DELETE\" INTEGER NOT NULL ," + // 16: softDelete
                "\"EXTRAS\" TEXT);"); // 17: extras
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DOWNLOADER\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Downloader entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        stmt.bindString(2, entity.getUrl());
 
        String params = entity.getParams();
        if (params != null) {
            stmt.bindString(3, params);
        }
        stmt.bindLong(4, entity.getFileSize());
        stmt.bindLong(5, entity.getCompleteSize());
        stmt.bindLong(6, entity.getDownloadStatus());
        stmt.bindString(7, entity.getSavePath());
        stmt.bindLong(8, entity.getRequestType());
 
        String checkKey = entity.getCheckKey();
        if (checkKey != null) {
            stmt.bindString(9, checkKey);
        }
        stmt.bindLong(10, entity.getAccessRanges() ? 1L: 0L);
        stmt.bindLong(11, entity.getErrorCode());
 
        String errorMsg = entity.getErrorMsg();
        if (errorMsg != null) {
            stmt.bindString(12, errorMsg);
        }
        stmt.bindString(13, entity.getTaskClsName());
        stmt.bindString(14, entity.getParamsClsName());
 
        String downloadType = entity.getDownloadType();
        if (downloadType != null) {
            stmt.bindString(15, downloadType);
        }
        stmt.bindLong(16, entity.getCreateTime().getTime());
        stmt.bindLong(17, entity.getSoftDelete() ? 1L: 0L);
 
        String extras = entity.getExtras();
        if (extras != null) {
            stmt.bindString(18, extras);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Downloader readEntity(Cursor cursor, int offset) {
        Downloader entity = new Downloader( //
            cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // url
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // params
            cursor.getLong(offset + 3), // fileSize
            cursor.getLong(offset + 4), // completeSize
            cursor.getInt(offset + 5), // downloadStatus
            cursor.getString(offset + 6), // savePath
            cursor.getInt(offset + 7), // requestType
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // checkKey
            cursor.getShort(offset + 9) != 0, // accessRanges
            cursor.getInt(offset + 10), // errorCode
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // errorMsg
            cursor.getString(offset + 12), // taskClsName
            cursor.getString(offset + 13), // paramsClsName
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // downloadType
            new java.util.Date(cursor.getLong(offset + 15)), // createTime
            cursor.getShort(offset + 16) != 0, // softDelete
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17) // extras
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Downloader entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setUrl(cursor.getString(offset + 1));
        entity.setParams(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setFileSize(cursor.getLong(offset + 3));
        entity.setCompleteSize(cursor.getLong(offset + 4));
        entity.setDownloadStatus(cursor.getInt(offset + 5));
        entity.setSavePath(cursor.getString(offset + 6));
        entity.setRequestType(cursor.getInt(offset + 7));
        entity.setCheckKey(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setAccessRanges(cursor.getShort(offset + 9) != 0);
        entity.setErrorCode(cursor.getInt(offset + 10));
        entity.setErrorMsg(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setTaskClsName(cursor.getString(offset + 12));
        entity.setParamsClsName(cursor.getString(offset + 13));
        entity.setDownloadType(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setCreateTime(new java.util.Date(cursor.getLong(offset + 15)));
        entity.setSoftDelete(cursor.getShort(offset + 16) != 0);
        entity.setExtras(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Downloader entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Downloader entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
