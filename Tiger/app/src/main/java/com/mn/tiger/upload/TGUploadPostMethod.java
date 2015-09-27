package com.mn.tiger.upload;

import android.content.Context;

import com.mn.tiger.log.LogTools;
import com.mn.tiger.request.method.ApachePostMethod;
import com.mn.tiger.task.TGTask.TGTaskState;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

/**
 * 该类作用及功能说明 文件上传post请求类
 * 
 * @date 2014年3月28日
 */
public class TGUploadPostMethod extends ApachePostMethod
{
	private final String LOG_TAG = this.getClass().getSimpleName();

	/** 上传文件路径 */
	private TGUploader uploader;
	
	/** 上传监听 */
	private IUploadSendListener sendListener;
	
	/** 上传任务 */
	private TGUploadTask uploadTask;
	
	/** 缓冲字节数 */
	private int bufferByteLength = 1024 * 4;
	
	/** 定义数据分隔线 */
	public static String BOUNDARY = "---------" + String.valueOf(System.currentTimeMillis()) + "--";

	/** 编码格式 */
	protected String UTF8 = "UTF-8";
	
	/** 已完成上传的长度 */
	private long completeSize;
	
	/**
	 * 构造函数
	 * @date 2014年7月30日
	 * @param context
	 * @param uploader
	 */
	public TGUploadPostMethod(Context context, TGUploader uploader,
			TGUploadTask uploadTask, IUploadSendListener sendListener)
	{	
		// 初始化上传参数
//		super(context, uploader.getServiceURL(), null);
		this.uploader = uploader;
		this.completeSize = uploader.getCompleteSize();
		this.uploadTask = uploadTask;
		this.sendListener = sendListener;
	}

//	protected void appendParams2OutputStream(Object params) throws IOException
//	{
//		// 链接失败或上传信息为空，直接返回
//		if (null == getHttpURLConnection() || TextUtils.isEmpty(uploader.getFilePath()))
//		{
//			uploadFailed(TGHttpError.IOEXCEPTION,
//					TGHttpError.getDefaultErrorMsg(getContext(), TGHttpError.IOEXCEPTION));
//			return;
//		}
//
//		// 获取请求参数
//		String strParams = null;
//		if (params != null)
//		{
//			strParams = (String) params;
//		}
//
//		// 上传内容
//		BufferedOutputStream outputStream = new BufferedOutputStream(getHttpURLConnection()
//				.getOutputStream());
//		try
//		{
//			// 写入参数信息
//			writeStringPart(outputStream, getPartBoundary(), strParams);
//
//			// 写文件
//			File uploadFile = new File(uploader.getFilePath());
//			if (uploadFile != null && uploadFile.exists())
//			{
//				writeFilePart(outputStream, getPartBoundary(), uploadFile,
//						uploader.getStartPosition(), uploader.getEndPosition());
//			}
//			outputStream.flush();
//		}
//		catch (IOException e)
//		{
//			LogTools.e(LOG_TAG, "[method:outputRequestParams] " + e.getMessage(), e);
//			uploadFailed(TGHttpError.IOEXCEPTION,
//					TGHttpError.getDefaultErrorMsg(getContext(), TGHttpError.IOEXCEPTION));
//		}
//		finally
//		{
//			if (null != outputStream)
//			{
//				try
//				{
//					outputStream.close();
//				}
//				catch (IOException e)
//				{
//					LogTools.e(LOG_TAG, "[method:outputRequestParams] " + e.getMessage(), e);
//					uploadFailed(TGHttpError.IOEXCEPTION,
//							TGHttpError.getDefaultErrorMsg(getContext(), TGHttpError.IOEXCEPTION));
//				}
//			}
//		}
//	}
	
	/**
	 * 该方法的作用:输出文件数据
	 * @date 2014年3月28日
	 * @param outputStream
	 * @param boundary
	 * @param file
	 * @param startPosition
	 * @param endPosition
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	protected void writeFilePart(OutputStream outputStream, String boundary, File file,
			long startPosition, long endPosition) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		sb.append("--");
		sb.append(boundary);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data;name=\""
				+ getFileContentDispositionName() + "\";filename=\""
				+ file.getName() + "\"\r\n");
		sb.append("Content-Type:application/octet-stream\r\n\r\n");
		byte[] data = sb.toString().getBytes(UTF8);

		byte[] end_data = ("\r\n--" + boundary + "--\r\n").getBytes(UTF8);// 定义最后数据分隔线

		outputStream.write(data);

		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
		randomAccessFile.seek(startPosition);
		long uploadLength = endPosition - startPosition;

		int byteLength = bufferByteLength;
		byte[] bufferOut = new byte[byteLength];

		long bufferCount = uploadLength % byteLength == 0 ? uploadLength / byteLength : uploadLength
				/ byteLength + 1;

		for (int i = 0; i < bufferCount; i++)
		{
			if (uploadTask == null || 
							uploadTask.getTaskState() == TGTaskState.PAUSE)
			{
				uploadStop(uploader);
				return;
			}
			
			int readLen = 0;
			if (i == (bufferCount - 1))
			{// 最后一次读取
				readLen = Long.valueOf(uploadLength - i * byteLength).intValue();
				bufferOut = new byte[readLen];
			}
			else
			{
				readLen = byteLength;
			}

			randomAccessFile.read(bufferOut);
			outputStream.write(bufferOut, 0, readLen);
		}
		
		outputStream.write(end_data);
		
		if(this.completeSize <= 0)
		{
			uploading(uploader, 2);
		}
	}
	
	/**
	 * 该方法的作用:输出字符串数据
	 * 
	 * @date 2014年3月27日
	 * @param connection
	 * @param boundary
	 * @param value
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	protected void writeStringPart(OutputStream outputStream, String boundary,
			String value) throws UnsupportedEncodingException, IOException
	{
		byte[] end_data = ("\r\n--" + boundary + "--\r\n").getBytes(UTF8);// 定义最后数据分隔线

		StringBuilder sb = new StringBuilder();
		sb.append("--");
		sb.append(boundary);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data;name=\"").append(getStringContentDispositionName()).append("\"\r\n");
		sb.append("Content-Type:text/plain; charset=UTF-8\r\n\r\n");
		byte[] partBoundary = sb.toString().getBytes(UTF8);

		byte[] data = value.getBytes(UTF8);

		try
		{
			outputStream.write(partBoundary);
			outputStream.write(data);
			outputStream.write(end_data);
		}
		catch (IOException e)
		{
			throw e;
		}
	}

	private void uploading(TGUploader uploader, int progress)
	{
		if (sendListener != null)
		{
			sendListener.uploading(uploader, progress);
		}
	}
	
	private void uploadFailed(int errorCode, String errorMsg)
	{
		if (sendListener != null)
		{
			uploader.setErrorCode(errorCode);
			uploader.setErrorMsg(errorMsg);
			sendListener.onFailed(uploader);
		}
		else
		{
			LogTools.e(LOG_TAG,
					"[Method:uploadFailed]  sendListener is null,Please set sendListener on Construct..");
		}
	}
	
	private void uploadStop(TGUploader uploader)
	{
		if (sendListener != null)
		{
			sendListener.onStop(uploader);
		}
		else
		{
			LogTools.e(LOG_TAG,
					"[Method:uploadStop]  sendListener is null,Please set sendListener on Construct..");
		}
	}
	
	public void setPartBOUNDARY(String bOUNDARY)
	{
		BOUNDARY = bOUNDARY;
	}

	/**
	 * 该方法的作用:获取分隔符
	 * 
	 * @date 2014年3月27日
	 * @return
	 */
	public String getPartBoundary()
	{
		return BOUNDARY;
	}

	public String getFileContentDispositionName()
	{
		return "hw_upload_file";
	}
	
	public String getStringContentDispositionName()
	{
		return "hw_upload_code";
	}
	
	/**
	 * 该类作用及功能说明 上传数据监听类
	 * 
	 * @date 2014年1月8日
	 */
	public interface IUploadSendListener
	{
		/**
		 * 该方法的作用:正在上传文件流
		 * 
		 * @date 2014年1月22日
		 * @param uploader
		 *            上传信息
		 */
		void uploading(TGUploader uploader, int progress);

		/**
		 * 该方法的作用:上传文件流结束
		 * 
		 * @date 2014年1月22日
		 * @param uploader
		 *            上传信息
		 */
		void onFinish(TGUploader uploader);

		/**
		 * 该方法的作用:下载出错
		 * 
		 * @date 2014年1月22日
		 * @param uploader
		 *            上传信息
		 */
		void onFailed(TGUploader uploader);

		/**
		 * 该方法的作用:下载停止
		 * 
		 * @date 2014年1月22日
		 * @param uploader
		 *            上传信息
		 */
		void onStop(TGUploader uploader);
	}

	public TGUploader getUploader()
	{
		return uploader;
	}

	public void setUploader(TGUploader uploader)
	{
		this.uploader = uploader;
	}

	public IUploadSendListener getSendListener()
	{
		return sendListener;
	}

	public void setSendListener(IUploadSendListener sendListener)
	{
		this.sendListener = sendListener;
	}

	public TGUploadTask getUploadTask()
	{
		return uploadTask;
	}

	public void setUploadTask(TGUploadTask uploadTask)
	{
		this.uploadTask = uploadTask;
	}

	public long getCompleteSize()
	{
		return completeSize;
	}

	public void setCompleteSize(long completeSize)
	{
		this.completeSize = completeSize;
	}
}
