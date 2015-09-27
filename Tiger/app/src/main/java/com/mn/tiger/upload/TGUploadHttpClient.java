package com.mn.tiger.upload;

import android.content.Context;

import com.mn.tiger.log.Logger;
import com.mn.tiger.task.TGTask;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

/**
 * Created by peng on 15/9/27.
 */
public abstract class TGUploadHttpClient
{
    private static final Logger LOG = Logger.getLogger(TGUploadHttpClient.class);

    private Context context;

    /** 上传文件路径 */
    protected TGUploader uploader;

    /** 上传监听 */
    protected IUploadListener uploadListener;

    /** 上传任务 */
    protected TGUploadTask uploadTask;

    /** 缓冲字节数 */
    protected int bufferByteLength = 1024 * 4;

    /** 定义数据分隔线 */
    public static String BOUNDARY = "---------" + String.valueOf(System.currentTimeMillis()) + "--";

    /** 编码格式 */
    protected String UTF8 = "UTF-8";

    /** 已完成上传的长度 */
    protected long completeSize;

    /**
     * 构造函数
     * @date 2014年7月30日
     * @param context
     * @param uploader
     */
    public TGUploadHttpClient(Context context, TGUploader uploader,
                              TGUploadTask uploadTask, IUploadListener uploadListener)
    {
        // 初始化上传参数
        this.context = context;
        this.uploader = uploader;
        this.completeSize = uploader.getCompleteSize();
        this.uploadTask = uploadTask;
        this.uploadListener = uploadListener;
    }

    public void execute()
    {
    }

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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("--");
        stringBuilder.append(boundary);
        stringBuilder.append("\r\n");
        stringBuilder.append("Content-Disposition: form-data;name=\""
                + getFileContentDispositionName() + "\";filename=\""
                + file.getName() + "\"\r\n");
        stringBuilder.append("Content-Type:application/octet-stream\r\n\r\n");
        byte[] data = stringBuilder.toString().getBytes(UTF8);

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
                    uploadTask.getTaskState() == TGTask.TGTaskState.PAUSE)
            {
                onUploadStop(uploader);
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
            onUploading(uploader, 2);
        }
    }

    /**
     * 该方法的作用:输出字符串数据
     *
     * @date 2014年3月27日
     * @param boundary
     * @param value
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    protected void writeStringPart(OutputStream outputStream, String boundary,
                                   String value) throws UnsupportedEncodingException, IOException
    {
        byte[] end_data = ("\r\n--" + boundary + "--\r\n").getBytes(UTF8);// 定义最后数据分隔线

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("--");
        stringBuilder.append(boundary);
        stringBuilder.append("\r\n");
        stringBuilder.append("Content-Disposition: form-data;name=\"").append(getStringContentDispositionName()).append("\"\r\n");
        stringBuilder.append("Content-Type:text/plain; charset=UTF-8\r\n\r\n");
        byte[] partBoundary = stringBuilder.toString().getBytes(UTF8);

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

    protected void onUploadStart(TGUploader uploader)
    {
        if (uploadListener != null)
        {
        }
    }

    protected void onUploading(TGUploader uploader, int progress)
    {
        if (uploadListener != null)
        {
            uploadListener.onUploading(uploader, progress);
        }
    }

    protected void onUploadFailed(int errorCode, String errorMsg)
    {
        if (uploadListener != null)
        {
            uploader.setErrorCode(errorCode);
            uploader.setErrorMsg(errorMsg);
            uploadListener.onUploadFailed(uploader);
        }
        else
        {
            LOG.e("[Method:uploadFailed]  uploadListener is null,Please set sendListener on Construct..");
        }
    }

    protected void onUploadStop(TGUploader uploader)
    {
        if (uploadListener != null)
        {
            uploadListener.onUploadStop(uploader);
        }
        else
        {
            LOG.e("[Method:uploadStop]  uploadListener is null,Please set sendListener on Construct..");
        }
    }

    public void setPartBOUNDARY(String bOUNDARY)
    {
        BOUNDARY = bOUNDARY;
    }

    public String getFileContentDispositionName()
    {
        return "tiger_upload_file";
    }

    public String getStringContentDispositionName()
    {
        return "tiger_upload_code";
    }

    /**
     * 该类作用及功能说明 上传数据监听类
     *
     * @date 2014年1月8日
     */
    public interface IUploadListener
    {
        /**
         * 该方法的作用:正在上传文件流
         *
         * @date 2014年1月22日
         * @param uploader
         *            上传信息
         */
        void onUploading(TGUploader uploader, int progress);

        /**
         * 该方法的作用:上传文件流结束
         *
         * @date 2014年1月22日
         * @param uploader
         *            上传信息
         */
        void onUploadFinish(TGUploader uploader);

        /**
         * 该方法的作用:下载出错
         *
         * @date 2014年1月22日
         * @param uploader
         *            上传信息
         */
        void onUploadFailed(TGUploader uploader);

        /**
         * 该方法的作用:下载停止
         *
         * @date 2014年1月22日
         * @param uploader
         *            上传信息
         */
        void onUploadStop(TGUploader uploader);
    }

    public void setUploader(TGUploader uploader)
    {
        this.uploader = uploader;
    }

    public void setUploadListener(IUploadListener uploadListener)
    {
        this.uploadListener = uploadListener;
    }

    public void setUploadTask(TGUploadTask uploadTask)
    {
        this.uploadTask = uploadTask;
    }

    public void setCompleteSize(long completeSize)
    {
        this.completeSize = completeSize;
    }
}
