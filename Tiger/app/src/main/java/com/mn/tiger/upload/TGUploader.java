package com.mn.tiger.upload;

import com.mn.tiger.datastorage.db.annotation.Column;
import com.mn.tiger.datastorage.db.annotation.Id;
import com.mn.tiger.datastorage.db.annotation.Transient;

import java.io.Serializable;

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
	// 上传的用户名
	@Column(column = "userName")
	private String userName;
	// 文件上传的服务地址
	@Column(column = "serviceURL")
	private String serviceURL;
	// 文件上传请求参数
	@Column(column = "params")
	private String params;
	// 本地上传文件的路径
	@Column(column = "filePath")
	private String filePath;
	// 上传服务站点名称
	@Column(column = "serverName")
	private String serverName;
	// 上传完成后，返回的文档id
	@Column(column = "docId")
	private String docId;
	// 服务器保存的路径
	@Column(column = "uuid")
	private String uuid;
	// 文件上传完成的字节数
	@Column(column = "completeSize")
	private long completeSize;
	// 上传的文件大小
	@Column(column = "fileSize")
	private long fileSize;
	// 上传的文件名称
	@Column(column = "docName")
	private String docName;
	// 上传的文档类型
	@Column(column = "docType")
	private String docType;
	// 文档版本号
	@Column(column = "docVersion")
	private String docVersion;
	// 上传状态(客户端记录上传状态)
	@Column(column = "uploadStatus")
	private int uploadStatus = TGUploadManager.UPLOAD_WAITING;
	// 上传状态(服务端返回上传状态0-失败，1-续传成功，2-上传成功)
	@Column(column = "serverUploadStatus")
	private int serverUploadStatus = 0;
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
	// 限制区域站点
	@Column(column = "finalSite")
	private String finalSite;
	// 上传的起始位置(用于断点上传)
	@Transient
	private long startPosition;
	// 上传的结束位置(用于断点上传)
	@Transient
	private long endPosition;
	
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

	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
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

	public int getServerUploadStatus()
	{
		return serverUploadStatus;
	}

	public void setServerUploadStatus(int serverUploadStatus)
	{
		this.serverUploadStatus = serverUploadStatus;
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

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getServerName()
	{
		return serverName;
	}

	public void setServerName(String serverName)
	{
		this.serverName = serverName;
	}

	public String getDocId()
	{
		return docId;
	}

	public void setDocId(String docId)
	{
		this.docId = docId;
	}

	public String getUuid()
	{
		return uuid;
	}

	public void setUuid(String uuid)
	{
		this.uuid = uuid;
	}

	public String getDocName()
	{
		return docName;
	}

	public void setDocName(String docName)
	{
		this.docName = docName;
	}

	public String getDocType()
	{
		return docType;
	}

	public void setDocType(String docType)
	{
		this.docType = docType;
	}

	public String getDocVersion()
	{
		return docVersion;
	}

	public void setDocVersion(String docVersion)
	{
		this.docVersion = docVersion;
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

	public String getParams()
	{
		return params;
	}

	public void setParams(String params)
	{
		this.params = params;
	}

	public long getCompleteSize()
	{
		return completeSize;
	}

	public void setCompleteSize(long completeSize)
	{
		this.completeSize = completeSize;
	}

	public String getFinalSite()
	{
		return finalSite;
	}

	public long getStartPosition()
	{
		return startPosition;
	}

	public void setStartPosition(long startPosition)
	{
		this.startPosition = startPosition;
	}

	public long getEndPosition()
	{
		return endPosition;
	}

	public void setEndPosition(long endPosition)
	{
		this.endPosition = endPosition;
	}

	public void setFinalSite(String finalSite)
	{
		this.finalSite = finalSite;
	}

	public void setParamsClsName(String paramsClsName)
	{
		this.paramsClsName = paramsClsName;
	}
	
	public String getParamsClsName()
	{
		return paramsClsName;
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
}
