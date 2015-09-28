package com.mn.tiger.upload;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mn.tiger.datastorage.TGDBManager;
import com.mn.tiger.datastorage.db.exception.DbException;
import com.mn.tiger.datastorage.db.sqlite.WhereBuilder;
import com.mn.tiger.datastorage.db.upgrade.AbsDbUpgrade;
import com.mn.tiger.log.LogTools;
import com.mn.tiger.utility.Constant;

/**
 * 该类作用及功能说明:数据库操作类
 *
 * @date 2014年6月5日
 */
public class TGUploadDBHelper
{
	/**
	 * 日志标识
	 */
	protected final String LOG_TAG = this.getClass().getSimpleName();

	/**
	 * instance
	 */
	private static TGUploadDBHelper instance;

	/**
	 * 数据库操作管理类
	 */
	private TGDBManager dbManager;

	/**
	 * 数据库名称
	 */
	private static String DATABASE_NAME = "tiger_upload.db";

	/**
	 * 数据库版本
	 */
	private static int DATABASE_VERSION = 1;

	/** 本地文件path */
	private final String UPLOADER_COLUMN_FILE_PATH = "filePath";

	/** 文件上传类型 */
	private final String UPLOADER_COLUMN_TYPE = "type";

	/**
	 *
	 * 该方法的作用: 获取单例实例
	 *
	 * @date 2014年8月29日
	 * @param context
	 * @return
	 */
	public static synchronized TGUploadDBHelper getInstance(Context context)
	{
		if (instance == null)
		{
			instance = new TGUploadDBHelper(context);
		}
		return instance;
	}

	/**
	 * 构造函数
	 *
	 * @date 2014年8月29日
	 * @param context
	 */
	private TGUploadDBHelper(Context context)
	{
		dbManager = getDB(context);
	}

	/**
	 *
	 * 该方法的作用: 创建上传数据库
	 *
	 * @date 2014年8月29日
	 * @param context
	 * @return
	 */
	private TGDBManager getDB(Context context)
	{
		TGDBManager db = TGDBManager.create(context, context.getApplicationInfo().dataDir
						+ File.separator + Constant.STORE_DATABASE_PATH, DATABASE_NAME, DATABASE_VERSION,
				new AbsDbUpgrade()
				{
					@Override
					public void upgradeSuccess()
					{
						LogTools.p(TAG, "tiger_upload upgrade success");
					}

					@Override
					public void upgradeFail()
					{
						LogTools.e(TAG, "tiger_upload upgrade success");
					}

					@Override
					public void upgradeNeedless()
					{
						LogTools.p(TAG, "tiger_upload upgrade need less.");
					}

				});
		db.configAllowTransaction(true);
		db.configDebug(false);
		return db;
	}

	/**
	 * 根据本地文件路径查找上传具体信息
	 *
	 * @throws DbException
	 */
	public synchronized TGUploader getUploader(HashMap<String, String> fileParams)
	{
		TGUploader uploader = null;
		try
		{
			uploader = dbManager.findFirst(TGUploader.class,
					WhereBuilder.b(UPLOADER_COLUMN_FILE_PATH, "=",
							new Gson().toJson(fileParams, new TypeToken<HashMap<String, String>>()
							{
							}.getType())));
		}
		catch (DbException e)
		{
			LogTools.e(LOG_TAG, e.getMessage(), e);
		}

		return uploader;
	}

	/**
	 * 查找所有上传具体信息
	 *
	 * @throws DbException
	 */
	public synchronized List<TGUploader> getAllUploader()
	{
		List<TGUploader> uploaderList = null;
		try
		{
			uploaderList = dbManager.findAll(TGUploader.class);
		}
		catch (DbException e)
		{
			LogTools.e(LOG_TAG, e.getMessage(), e);
		}

		return uploaderList;
	}

	/**
	 * 根据类型查找上传具体信息
	 *
	 * @throws DbException
	 */
	public synchronized List<TGUploader> getUploaderByType(String type)
	{
		List<TGUploader> uploaderList = null;
		try
		{
			uploaderList = dbManager.findAll(TGUploader.class,
					WhereBuilder.b(UPLOADER_COLUMN_TYPE, "=", type));
		}
		catch (DbException e)
		{
			LogTools.e(LOG_TAG, e.getMessage(), e);
		}

		return uploaderList;
	}

	/**
	 * 该方法的作用:保存文件上传信息(有记录则更新记录)
	 *
	 * @date 2014年1月8日
	 * @param info
	 * @throws DbException
	 */
	public synchronized void saveUploader(TGUploader info)
	{
		try
		{
			dbManager.save(info);
		}
		catch (DbException e)
		{
			LogTools.e(LOG_TAG, e.getMessage(), e);
		}
	}

	/**
	 * 该方法的作用:更新文件上传状态
	 *
	 * @date 2014年1月6日
	 * @param info
	 * @throws DbException
	 */
	public void updateUploader(TGUploader info)
	{
		if (info == null)
		{
			return;
		}

		try
		{
			dbManager.update(info);
		}
		catch (DbException e)
		{
			LogTools.e(LOG_TAG, e.getMessage(), e);
		}
	}

	/**
	 * 上传完成后删除数据库中的数据
	 *
	 * @throws DbException
	 */
	public synchronized void deleteUploader(TGUploader info)
	{
		try
		{
			dbManager.delete(info);
		}
		catch (DbException e)
		{
			LogTools.e(LOG_TAG, e.getMessage(), e);
		}
	}

	/**
	 * 上传完成后删除数据库中的数据
	 *
	 * @throws DbException
	 */
	public synchronized void deleteUploader(String filePath)
	{
		try
		{
			dbManager.delete(TGUploader.class,
					WhereBuilder.b(UPLOADER_COLUMN_FILE_PATH, "=", filePath));
		}
		catch (DbException e)
		{
			LogTools.e(LOG_TAG, e.getMessage(), e);
		}
	}
}