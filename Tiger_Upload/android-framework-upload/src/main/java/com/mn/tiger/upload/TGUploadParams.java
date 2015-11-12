package com.mn.tiger.upload;

import com.mn.tiger.request.TGHttpParams;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * 该类作用及功能说明: 上传请求参数
 *
 * @date 2014年8月25日
 */
public class TGUploadParams extends TGHttpParams implements Cloneable, Serializable
{
	private static final long serialVersionUID = 1L;

	// 文件上传的服务地址url
	private String serviceURL;

	// 下载类型
	private String uploadType;

	// 执行下载的类名
	private String taskClsName = "";

	// 任务权重
	private int weight;

	/**
	 * 字符串参数
	 */
	private HashMap<String, String> stringParams;

	/**
	 * 文件参数
	 */
	private HashMap<String, String> fileParams;

	/**
	 * 添加字符串参数
	 * @param key
	 * @param value
	 */
	public void addStringParam(String key, String value)
	{
		if(null == stringParams)
		{
			stringParams = new HashMap<String, String>();
		}
		stringParams.put(key, value);
	}

	/**
	 * 添加文件参数
	 * @param key
	 * @param filePath
	 */
	public void addFileParam(String key, String filePath)
	{
		if(null == fileParams)
		{
			fileParams = new HashMap<String, String>();
		}
		fileParams.put(key, filePath);
	}

	public String getServiceURL()
	{
		return serviceURL;
	}

	public void setServiceURL(String serviceURL)
	{
		this.serviceURL = serviceURL;
	}

	public String getUploadType()
	{
		return uploadType;
	}

	public void setUploadType(String uploadType)
	{
		this.uploadType = uploadType;
	}

	public String getTaskClsName()
	{
		return taskClsName;
	}

	public void setTaskClsName(String taskClsName)
	{
		this.taskClsName = taskClsName;
	}

	public int getWeight()
	{
		return weight;
	}

	public void setWeight(int weight)
	{
		this.weight = weight;
	}

}
