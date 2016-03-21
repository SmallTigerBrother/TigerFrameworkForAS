package com.mn.tiger.download;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mn.tiger.datastorage.db.annotation.Column;
import com.mn.tiger.datastorage.db.annotation.ColumnObject;
import com.mn.tiger.datastorage.db.annotation.Id;
import com.mn.tiger.datastorage.db.annotation.Table;
import com.mn.tiger.datastorage.db.annotation.Transient;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 
 * 该类作用及功能说明: 下载任务记录
 * 
 * @date 2014年8月18日
 */
@Table(name = "Downloader", execAfterTableCreated = "CREATE INDEX index_name ON Downloader(url,params)")
public class TGDownloader implements Serializable
{
	/**
	 * 下载状态——等待
	 */
	public static final int DOWNLOAD_WAITING = -2;
	
	/**
	 * 下载状态——开始
	 */
	public static final int DOWNLOAD_STARTING = -1;

	/**
	/**
	 * 下载状态——下砸中
	 */
	public static final int DOWNLOAD_DOWNLOADING = 0;

	/**
	 * 下载状态——成功
	 */
	public static final int DOWNLOAD_SUCCEED = 1;

	/**
	 * 下载状态——失败
	 */
	public static final int DOWNLOAD_FAILED = 2;

	/**
	 * 下载状态——暂停
	 */
	public static final int DOWNLOAD_PAUSE = 3;
	
	/**
	 * 下载状态——取消
	 */
	public static final int DOWNLOAD_CANCEL = 4;
	
	@Transient
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	// 下载url地址
	@Column(column = "url")
	private String url;
	// 下载请求参数
	@ColumnObject
	@Column(column = "params")
	private HashMap<String, String> params;
	// 文件大小（字节）
	@Column(column = "fileSize")
	private long fileSize;
	// 文件下载完成的字节数
	@Column(column = "completeSize")
	private long completeSize;
	// 文件下载状态，DonwnloadConstant:INIT:0
	// DOWNLOADING:1,SUCCESS:2,FAILURE:3,PAUSE:4,SOURCE_ERROR:5(下载来源错误，文件长度或MD5校验出错)
	@Column(column = "downloadStatus")
	private int downloadStatus = DOWNLOAD_WAITING;
	// 文件下载保存的位置
	@Column(column = "savePath")
	private String savePath;
	// 请求类型
	@Column(column = "requestType")
	private int requestType;
	// 文件流校验加密字符串
	@Column(column = "checkKey")
	private String checkKey;
	// 是否使用断点下载
	@Column(column = "isBreakPoints")
	private boolean isBreakPoints = false;
	// 下载出错时，错误码
	@Column(column = "errorCode")
	private int errorCode;
	// 下载出错时，错误信息
	@Column(column = "errorMsg")
	private String errorMsg;

	// 自定义执行的任务类的名称
	@Column(column = "taskClsName")
	private String taskClsName = "";
	
	//参数类的类名，用户反射生成参数类
	@Column(column = "paramsClsName")
	private String paramsClsName= TGDownloadParams.class.getName();
	
	/**
	 * 用于区分不同类型下载任务，在同一客户端存在多个下载中心时使用
	 */
	// 下载类型
	@Column(column = "downloadType")
	private String downloadType;
	
	public static TGDownloader getInstance(Context context, TGDownloadParams downloadParams, int downloadTaskId)
	{
		TGDownloader downloader = null;
		downloader = TGDownloadDBHelper.getInstance(context).getDownloader(
				downloadParams.getUrl(), downloadParams.getParams(), downloadParams.getSavePath());
		
		if(downloader == null)
		{
			downloader = new TGDownloader();
			downloader.setId(downloadTaskId + "");
			downloader.setUrl(downloadParams.getUrl());
			downloader.setParams(downloadParams.getParams());
			downloader.setRequestType(downloadParams.getRequestType());
			downloader.setDownloadType(downloadParams.getDownloadType());
			downloader.setSavePath(downloadParams.getSavePath());
			downloader.setTaskClsName(downloadParams.getTaskClsName());
			downloader.setParamsClsName(downloadParams.getClass().getName());
		}
		
		return downloader;
	}
	
	public TGDownloader()
	{
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the fileSize
	 */
	public long getFileSize()
	{
		return fileSize;
	}

	/**
	 * @param fileSize
	 *            the fileSize to set
	 */
	public void setFileSize(long fileSize)
	{
		this.fileSize = fileSize;
	}

	/**
	 * @return the completeSize
	 */
	public long getCompleteSize()
	{
		return completeSize;
	}

	/**
	 * @param completeSize
	 *            the completeSize to set
	 */
	public void setCompleteSize(long completeSize)
	{
		this.completeSize = completeSize;
	}

	/**
	 * @return the urlstring
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * @param url
	 *            the url string to set
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}

	public HashMap<String, String> getParams()
	{
		return params;
	}

	public void setParams(HashMap<String, String> params)
	{
		this.params = params;
	}
	
	@SuppressWarnings("unused")
	private String getParamsStr()
	{
		return new Gson().toJson(params);
	}
	
	static String getParamsString(HashMap<String, String> params)
	{
		return new Gson().toJson(params);
	}

	@SuppressWarnings("unused")
	public void setParamsStr(String params)
	{
		this.params = new Gson().fromJson(params, new TypeToken<HashMap<String, String>>(){}.getType());
	}

	/**
	 * @return the downloadStatus
	 */
	public int getDownloadStatus()
	{
		return downloadStatus;
	}

	/**
	 * @param downloadStatus
	 *            the downloadStatus to set
	 */
	public void setDownloadStatus(int downloadStatus)
	{
		this.downloadStatus = downloadStatus;
	}

	/**
	 * @return the savePath
	 */
	public String getSavePath()
	{
		return savePath;
	}

	/**
	 * @param savePath
	 *            the savePath to set
	 */
	public void setSavePath(String savePath)
	{
		this.savePath = savePath;
	}

	public int getRequestType()
	{
		return requestType;
	}

	public void setRequestType(int requestType)
	{
		this.requestType = requestType;
	}

	public String getCheckKey()
	{
		return checkKey;
	}

	public void setCheckKey(String checkKey)
	{
		this.checkKey = checkKey;
	}

	public int getErrorCode()
	{
		return errorCode;
	}

	public void setErrorCode(int errorCode)
	{
		this.errorCode = errorCode;
	}

	public String getErrorMsg()
	{
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg)
	{
		this.errorMsg = errorMsg;
	}

	public boolean isBreakPoints()
	{
		return isBreakPoints;
	}

	public void setBreakPoints(boolean isBreakPoints)
	{
		this.isBreakPoints = isBreakPoints;
	}

	public void setTaskClsName(String taskClsName)
	{
		this.taskClsName = taskClsName;
	}

	public String getTaskClsName()
	{
		return taskClsName;
	}

	public String getParamsClsName()
	{
		return paramsClsName;
	}
	
	public void setParamsClsName(String paramsClsName)
	{
		this.paramsClsName = paramsClsName;
	}
	
	public String getDownloadType()
	{
		return downloadType;
	}
	
	/**
	 * 用于区别不同类型的下载任务，当同一个客户端存在多个下载中心时，可以用此属性区分
	 * @param type
	 */
	public void setDownloadType(String type)
	{
		this.downloadType = type;
	}
	
	@Override
	public String toString()
	{
		return "Downloader [fileSize=" + fileSize + ", complete=" + completeSize + ", urlString=" + url
				+ ", params=" + params + ", savePath=" + savePath + ", downloadStatus=" + downloadStatus
				+ ", requestType=" + requestType + ", checkKey=" + checkKey + ", isBreakPoints=" + isBreakPoints
				+ ", downloadType=" + downloadType + "]";
	}
}
