package com.mn.tiger.download;

import java.io.Serializable;
import java.util.HashMap;

import com.mn.tiger.request.HttpType;

/**
 * 
 * 该类作用及功能说明: 下载请求参数
 * 
 * @date 2014年8月18日
 */
public class TGDownloadParams implements Cloneable, Serializable 
{
	// 序列化ID
	private static final long serialVersionUID = 1L;
	// 下载url地址
    private String url; 
	// 下载请求参数
	private HashMap<String, String> params;
	// 文件下载保存的位置
	private String savePath;
	// 请求方式
	private int requestType = HttpType.REQUEST_GET;
	// 执行下载的类名
	private String taskClsName = "";
	
	/**
	 * 用于区分不同类型下载任务，在同一客户端存在多个下载中心时使用
	 */
	private String downloadType;
	
	public TGDownloadParams()
	{
		params = new HashMap<String, String>();
	}
	
	public String getUrl()
	{
		return url;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}
	
	public void addRequestParam(String key, String value)
	{
		this.params.put(key, value);
	}
	
	public HashMap<String, String> getParams()
	{
		return params;
	}
	
	void setParams(HashMap<String, String> params)
	{
		this.params = params;
	}
	
	public String getSavePath()
	{
		return savePath;
	}
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
	public String getTaskClsName()
	{
		return taskClsName;
	}
	public void setTaskClsName(String taskClsName)
	{
		this.taskClsName = taskClsName;
	}
	
	public String getDownloadType()
	{
		return downloadType;
	}
	
	/**
	 * 用于区分不同类型下载任务，在同一客户端存在多个下载中心时使用
	 */
	public void setDownloadType(String downloadType)
	{
		this.downloadType = downloadType;
	}
}
