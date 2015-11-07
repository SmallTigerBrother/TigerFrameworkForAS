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
    public TGUploadHttpClient(Context context, TGUploader uploader, TGUploadTask uploadTask)
    {
        // 初始化上传参数
        this.context = context;
        this.uploader = uploader;
        this.completeSize = uploader.getCompleteSize();
        this.uploadTask = uploadTask;
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
        LOG.i("[Method:writeFilePart] file == " + file.getAbsolutePath() + " startPosition == "  +
                startPosition + "  endPositions == " + endPosition);

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
                                   String value) throws IOException
    {
        LOG.i("[Method:writeStringPart] value == " + value);

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


    public void onUploadStart(TGUploader uploader)
    {
        uploader.setUploadStatus(TGUploadManager.UPLOAD_STARTING);
        uploadTask.onUploadStart(uploader);
    }

    /**
     *
     * 该方法的作用: 上传过程中
     * @date 2014年8月19日
     * @param uploader
     */
    private void onUploading(TGUploader uploader, int progress)
    {
        // 修改上传状态为正在上传
        uploader.setUploadStatus(TGUploadManager.UPLOAD_UPLOADING);
        uploadTask.onUploading(uploader, progress);
    }

    /**
     *
     * 该方法的作用: 上传文件完成，删除数据库记录
     * @date 2014年8月19日
     * @param uploader
     */
    private void onUploadFinish(TGUploader uploader)
    {
        // 删除本地记录
        uploader.setUploadStatus(TGUploadManager.UPLOAD_SUCCEED);
        TGUploadDBHelper.getInstance(context).deleteUploader(uploader);

        uploadTask.onUploadFinish(uploader);
    }

    /**
     *
     * 该方法的作用: 上传文件过程中出现异常，如果不是断点上传，删除本地文件
     * @date 2014年8月19日
     * @param uploader
     */
    protected void onUploadFailed(TGUploader uploader)
    {
        uploader.setUploadStatus(TGUploadManager.UPLOAD_FAILED);
        // 如果不是分块上传，删除数据库记录
        if(true)
        {
            TGUploadDBHelper.getInstance(context).deleteUploader(uploader);
        }

        uploadTask.onUploadFailed(uploader);
    }

    /**
     *
     * 该方法的作用: 停止上传，如果不是断点上传，删除本地文件
     * @date 2014年8月19日
     * @param uploader
     */
    private void onUploadStop(TGUploader uploader)
    {
        uploader.setUploadStatus(TGUploadManager.UPLOAD_PAUSE);
        // 如果不是分块上传，删除数据库记录
        if(true)
        {
            TGUploadDBHelper.getInstance(context).deleteUploader(uploader);
        }
        uploadTask.onUploadStop(uploader);
    }

    /**
     * 该方法的作用: 取消上传，直接删除本地文件和数据库记录
     * @date 2014年8月19日
     * @param uploader
     */
    private void onUploadCancel(TGUploader uploader)
    {
        uploader.setUploadStatus(TGUploadManager.UPLOAD_PAUSE);
        // 删除数据库记录
        TGUploadDBHelper.getInstance(context).deleteUploader(uploader);

        uploadTask.onUploadCancel(uploader);
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

    public void setUploader(TGUploader uploader)
    {
        this.uploader = uploader;
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
