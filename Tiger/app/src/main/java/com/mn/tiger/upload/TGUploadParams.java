package com.mn.tiger.upload;

import java.io.Serializable;

/**
 * 
 * 该类作用及功能说明: 上传请求参数
 * 
 * @date 2014年8月25日
 */
public class TGUploadParams implements Cloneable, Serializable 
{
	private static final long serialVersionUID = 1L;

	// 本地上传的路径
	private String filePath;
	
	// 文件上传的服务地址url
	private String serviceURL;
	
	// 下载类型
	private String uploadType;
	
	// 执行下载的类名
	private String taskClsName = "";
	
	// 任务权重
	private int weight;

	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
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
