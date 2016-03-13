package com.mn.tiger.download.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mn.tiger.app.TGApplicationProxy;

/**
 * Created by peng on 16/3/13.
 */
public class TGDownloadDBHelper extends DaoMaster.OpenHelper
{
    /**
     * 数据库名称
     */
    private static final String DATABASE_NAME = "tiger_download";

    private DownloaderDao downloaderDao;

    private static TGDownloadDBHelper instance;

    public static TGDownloadDBHelper getInstance()
    {
        if(null == instance)
        {
            synchronized (TGDownloadDBHelper.class)
            {
                if(null == instance)
                {
                    instance = new TGDownloadDBHelper(TGApplicationProxy.getApplication());
                }
            }
        }

        return instance;
    }

    private TGDownloadDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null);

        SQLiteDatabase writeDB = getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(writeDB);
        DaoSession daoSession = daoMaster.newSession();
        downloaderDao = daoSession.getDownloaderDao();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
