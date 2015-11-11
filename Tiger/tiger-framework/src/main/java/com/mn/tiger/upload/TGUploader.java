package com.mn.tiger.upload;

import java.io.Serializable;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mn.tiger.datastorage.db.annotation.Column;
import com.mn.tiger.datastorage.db.annotation.ColumnObject;
import com.mn.tiger.datastorage.db.annotation.Id;
import com.mn.tiger.request.TGHttpParams;

/**
 *
 * 该类作用及功能说明: 包含所有上传信息（上传url、上传文件路径等）的类
 *
 * @date 2014年6月18日
 */
public class TGUploader implements Cloneable, Serializable
{
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	// 文件上传的服务地址
	@Column(column = "serviceURL")
	private String serviceURL;

	// 文件上传请求参数
	@ColumnObject(column = "stringParams")
	private HashMap<String, String> stringParams;

	// 本地上传文件的路径
	@Column(column = "fileParams")
	private HashMap<String, String> fileParams;

	// 文件上传完成的字节数
	@Column(column = "completeSize")
	private long completeSize;

	// 上传的文件大小
	@Column(column = "fileSize")
	private long fileSize;

	// 上传状态(客户端记录上传状态)
	@Column(column = "uploadStatus")
	private int uploadStatus = TGUploadManager.UPLOAD_WAITING;

	// 上传出错时，错误码
	@Column(column = "errorCode")
	private int errorCode;

	// 上传出错时，错误信息
	@Column(column = "errorMsg")
	private String errorMsg;

	// 自定义执行的任务类的名称
	@Column(column = "taskClsName")
	private String taskClsName = "";

	// 上传参数的类名，用户反射生成上传参数
	@Column(column = "paramsClsName")
	private String paramsClsName = TGUploadParams.class.getName();

	// 上传类型
	@Column(column = "type")
	private String type;

	public TGUploader()
	{
	}

	public String getServiceURL()
	{
		return serviceURL;
	}

	public void setServiceURL(String serviceURL)
	{
		this.serviceURL = serviceURL;
	}

	public long getFileSize()
	{
		return fileSize;
	}

	public void setFileSize(long fileSize)
	{
		this.fileSize = fileSize;
	}

	public int getUploadStatus()
	{
		return uploadStatus;
	}

	public void setUploadStatus(int uploadStatus)
	{
		this.uploadStatus = uploadStatus;
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

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getTaskClsName()
	{
		return taskClsName;
	}

	public void setTaskClsName(String taskClsName)
	{
		this.taskClsName = taskClsName;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public HashMap<String, String> getStringParams()
	{
		return stringParams;
	}

	public void setStringParams(HashMap<String, String> params)
	{
		this.stringParams = params;
	}

	@SuppressWarnings("unused")
	private String getStringParamsStr()
	{
		return new Gson().toJson(stringParams, new TypeToken<HashMap<String,String>>(){}.getType());
	}

	@SuppressWarnings("unused")
	private void setStringParamsStr(String params)
	{
		this.stringParams = new Gson().fromJson(params, new TypeToken<HashMap<String,String>>(){}.getType());
	}

	@SuppressWarnings("unused")
	private String getFileParamsStr()
	{
		return new Gson().toJson(fileParams, new TypeToken<HashMap<String,String>>(){}.getType());
	}

	@SuppressWarnings("unused")
	private void setFileParamsStr(String params)
	{
		this.fileParams = new Gson().fromJson(params, new TypeToken<HashMap<String,String>>(){}.getType());
	}

	public HashMap<String, String> getFileParams()
	{
		return fileParams;
	}

	public void setFileParams(HashMap<String, String> fileParams)
	{
		this.fileParams = fileParams;
	}

	public void setParamsClsName(String paramsClsName)
	{
		this.paramsClsName = paramsClsName;
	}

	public String getParamsClsName()
	{
		return paramsClsName;
	}

	public long getCompleteSize()
	{
		return completeSize;
	}

	public void setCompleteSize(long completeSize)
	{
		this.completeSize = completeSize;
	}

	@Override
	public Object clone()
	{
		Object obj = null;
		try
		{
			obj = super.clone();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return obj;
	}

	public TGHttpParams convertToHttpParams()
	{
		TGHttpParams httpParams = new TGHttpParams();
		httpParams.setStringParams(this.stringParams);
		httpParams.setFileParams(this.fileParams);

		return httpParams;
	}

	public static TGUploader getInstanse(TGUploadParams uploadParams)
	{
		TGUploader uploader = new TGUploader();
		uploader.setFileParams(uploadParams.getFileParams());
		uploader.setStringParams(uploadParams.getStringParams());
		uploader.setParamsClsName(uploadParams.getClass().getName());
		uploader.setTaskClsName(uploadParams.getTaskClsName());
		uploader.setType(uploadParams.getUploadType());

		return uploader;
	}
}
