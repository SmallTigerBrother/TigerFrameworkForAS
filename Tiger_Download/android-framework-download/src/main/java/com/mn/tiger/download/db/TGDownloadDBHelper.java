package com.mn.tiger.download.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mn.tiger.app.TGApplicationProxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by peng on 16/3/13.
 */
public class TGDownloadDBHelper extends DaoMaster.OpenHelper
{
    /**
     * 数据库名称
     */
    private static final String DATABASE_NAME = "tiger_download_2.db";

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

    public TGDownloader findDownloader(String urlStr, HashMap<String, String> params, String savePath)
    {
        Downloader downloader = downloaderDao.queryBuilder().where(DownloaderDao.Properties.Url.eq(urlStr),
                DownloaderDao.Properties.Params.eq(TGDownloader.getParamsString(params)),
                DownloaderDao.Properties.SavePath.eq(savePath)).limit(1).unique();
        return TGDownloader.fromDownloader(downloader);
    }

    /**
     * 查询指定状态、类型、是否已软删的下载记录
     * @param downloadStatuses
     * @param downloadTypes
     * @param includeSoftDelete
     * @return
     */
    public List<TGDownloader> findDownloaders(int[] downloadStatuses, String[] downloadTypes, boolean includeSoftDelete)
    {
        QueryBuilder<Downloader> queryBuilder = downloaderDao.queryBuilder();
        //拼接下载状态类型参数
        if (null != downloadStatuses && downloadStatuses.length > 0)
        {
            queryBuilder.where(DownloaderDao.Properties.DownloadStatus.in(Arrays.asList(downloadStatuses)));
        }

        //拼接下载类型参数
        if(null != downloadTypes && downloadTypes.length > 0)
        {
            queryBuilder.where(DownloaderDao.Properties.DownloadType.in(Arrays.asList(downloadTypes)));
        }

        //是否需要已软删的数据
        if(!includeSoftDelete)
        {
            queryBuilder.where(DownloaderDao.Properties.SoftDelete.eq(false));
        }

        List<Downloader> downloaders = queryBuilder.list();
        return convert2TGDownloaderList(downloaders);
    }

    private ArrayList<TGDownloader> convert2TGDownloaderList(List<Downloader> downloaders)
    {
        if(null != downloaders && downloaders.size() > 0)
        {
            int count = downloaders.size();
            ArrayList<TGDownloader> result = new ArrayList<>(count);
            for (int i = 0; i < count; i++)
            {
                result.add(TGDownloader.fromDownloader(downloaders.get(i)));
            }
            return result;
        }
        return new ArrayList<>(0);
    }

    public void saveOrUpdateDownloader(TGDownloader downloader)
    {
        downloaderDao.insertOrReplace(downloader);
    }

    public void deleteDownloader(TGDownloader downloader)
    {
        downloaderDao.delete(downloader);
    }

    public void softDeleteDownloader(TGDownloader downloader)
    {
        downloader.setSoftDelete(true);
        downloaderDao.insertOrReplace(downloader);
    }

    public void deleteDownloader(int taskId)
    {
        downloaderDao.queryBuilder().where(DownloaderDao.Properties.Id.eq(taskId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public List<TGDownloader> findDownloaders(int downloadStatus)
    {
        List<Downloader> downloaders = downloaderDao.queryBuilder().where(DownloaderDao.Properties.DownloadStatus.eq(downloadStatus)).list();
        if(null != downloaders && downloaders.size() > 0)
        {
            int count = downloaders.size();
            ArrayList<TGDownloader> result = new ArrayList<>(count);
            for (int i = 0; i < count; i++)
            {
                result.add(TGDownloader.fromDownloader(downloaders.get(i)));
            }
            return result;
        }
        return new ArrayList<>(0);
    }

}
