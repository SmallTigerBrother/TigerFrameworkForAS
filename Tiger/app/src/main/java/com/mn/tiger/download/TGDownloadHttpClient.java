package com.mn.tiger.download;

import android.content.Context;
import android.text.TextUtils;

import com.mn.tiger.log.Logger;
import com.mn.tiger.request.error.TGErrorMsgEnum;
import com.mn.tiger.request.error.TGHttpError;
import com.mn.tiger.request.receiver.TGHttpResult;
import com.mn.tiger.utility.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * 执行下载的HttpClient
 */
public abstract class TGDownloadHttpClient
{
    private static final Logger LOG = Logger.getLogger(TGDownloadHttpClient.class);

    protected static final int MAX_RETRY = 3;

    protected Context context;

    // 下载参数
    protected TGDownloader downloader;

    /**
     * 下载文件校验器
     */
    protected IDownloadFileChecker downloadFileChecker;

    /**
     * 下载文件写入器
     */
    protected DownloadFileWriter downloadFileWriter;

    protected TGDownloadTask downloadTask;

    protected int retryCount = 0;

    public TGDownloadHttpClient(Context context, TGDownloader downloader,
                              TGDownloadTask downloadTask)
    {
        this.context = context;
        this.downloader = downloader;
        this.downloadTask = downloadTask;
        downloadFileWriter = new DownloadFileWriter(context, downloader, downloadTask, this);
        downloadFileChecker = new FileSizeDownloadFileChecker();
    }

    public TGHttpResult execute()
    {
        onDownloadStart(downloader);
        executeHttpConnect();
        return handleResponse();
    }

    protected abstract void executeHttpConnect();

    protected TGHttpResult handleResponse()
    {
        TGHttpResult httpResult = initHttpResult();
        httpResult.setResponseCode(getResponseCode());
        //从返回值里面读取参数到downloader中
        readParamsFromResponse();

        // 检测存储路径是否为空
        if (TextUtils.isEmpty(downloader.getSavePath()))
        {
            httpResult.setResponseCode(TGErrorMsgEnum.DOWNLOAD_SAVE_PATH_IS_NULL.code);
            httpResult.setResult(TGErrorMsgEnum.getErrorMsg(context,
                    TGErrorMsgEnum.DOWNLOAD_SAVE_PATH_IS_NULL));

            downloader.setErrorCode(httpResult.getResponseCode());
            downloader.setErrorMsg(httpResult.getResult().toString());
            onDownloadFailed(downloader);
            return httpResult;
        }

        if (!isResponseOk(getResponseCode()))
        {
            // 服务返回异常responseCode
            downloader.setErrorCode(httpResult.getResponseCode());
            downloader.setErrorMsg(httpResult.getResult().toString());
            onDownloadFailed(downloader);
            return httpResult;
        }

        //检测本地是否已下载，若本地已下载，直接下载完成
        if (hasAlreadyDownloaded())
        {
            downloader.setCompleteSize(downloader.getFileSize());
            onDownloadSuccess(downloader);
            return httpResult;
        }

        return dealDownloadResult(httpResult);
    }

    /**
     * 判断返回响应码不为200或204
     * @return
     */
    protected boolean isResponseOk(int responseCode)
    {
        return responseCode == HttpURLConnection.HTTP_OK
                || responseCode == HttpURLConnection.HTTP_PARTIAL;
    }

    protected abstract int getResponseCode();

    /**
     * 从response中读取下载参数
     */
    private void readParamsFromResponse()
    {
        long serverFileSize = getContentLength();
        // 第一次请求，设置文件大小
        if (downloader.getFileSize() <= 0)
        {
            downloader.setFileSize(serverFileSize);
        }

        String acceptRange = getAcceptRangesFromResponseHeaders();
        String savePath = getDownloadFileSavePath();
        // 设置文件存储路径
        downloader.setSavePath(savePath);
        if(!downloader.isBreakPoints())
        {
            downloader.setBreakPoints(serverFileSize > 0 && acceptRange.equalsIgnoreCase("bytes"));
        }

        TGDownloadDBHelper.getInstance(context).saveDownloader(downloader);
    }

    /**
     * 该方法的作用:处理下载结果
     *
     * @date 2014年4月25日
     * @param httpResult
     */
    protected TGHttpResult dealDownloadResult(TGHttpResult httpResult)
    {
        InputStream inputStream = null;
        try
        {
            //1、 检测保存空间不足
            if (hasEnoughStorageSpace(downloader.getFileSize()))
            {
                LOG.e("[Method:dealDownloadResult] "
                        + TGErrorMsgEnum.getErrorMsg(context,
                        TGErrorMsgEnum.USABLE_SPACE_NOT_ENOUGH));

                downloader.setErrorCode(TGErrorMsgEnum.USABLE_SPACE_NOT_ENOUGH.code);
                downloader.setErrorMsg(TGErrorMsgEnum.getErrorMsg(context, TGErrorMsgEnum.USABLE_SPACE_NOT_ENOUGH));
                onDownloadFailed(downloader);
                return httpResult;
            }

            //2、 若支持断点下载，执行断点下载；若不支持断点下载，使用普通下载
            if (downloader.isBreakPoints())
            {
                LOG.d("[Method:dealDownloadResult]  Server support break point");

                // 写文件流到本地
                inputStream = getDownloadInputStream();
                downloadFileWriter.writeToRandomFile(inputStream, downloader.getSavePath(), downloader.getCompleteSize(),
                        downloader.getFileSize());
            }
            else
            {
                LOG.d("[Method:dealDownloadResult]  Server do not support break point");

                inputStream = getDownloadInputStream();
                downloadFileWriter.writeToLocalFile(inputStream, downloader.getSavePath());
            }
        }
        catch (IOException e)
        {
            LOG.e("[Method:dealDownloadResult] " + e.getMessage(), e);
            downloader.setErrorCode(TGHttpError.IOEXCEPTION);
            downloader.setErrorMsg(TGHttpError.getDefaultErrorMsg(context, TGHttpError.IOEXCEPTION));
            onDownloadFailed(downloader);
        }
        catch (NumberFormatException e)
        {
            LOG.e("[Method:dealDownloadResult] " + e.getMessage(), e);
            downloader.setErrorCode(TGErrorMsgEnum.FAILED_GET_FILE_SIZE.code);
            downloader.setErrorMsg(TGErrorMsgEnum.getErrorMsg(context, TGErrorMsgEnum.FAILED_GET_FILE_SIZE));
            onDownloadFailed(downloader);
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    LOG.e("[Method:dealDownloadResult] " + e.getMessage(), e);
                    downloader.setErrorCode(TGHttpError.IOEXCEPTION);
                    downloader.setErrorMsg(TGHttpError.getDefaultErrorMsg(context, TGHttpError.IOEXCEPTION));
                    onDownloadFailed(downloader);
                }
            }
        }
        return httpResult;
    }

    protected abstract long getContentLength();

    protected abstract InputStream getDownloadInputStream() throws IOException;

    /**
     * 判断是否有充足的存储空间
     * @param serverFileSize
     * @return
     */
    protected boolean hasEnoughStorageSpace(long serverFileSize)
    {
        return serverFileSize > 0
                && serverFileSize > FileUtils.getFreeBytes(downloader.getSavePath());
    }

    /**
     * 文件是否已下载完成
     * @return
     */
    private boolean hasAlreadyDownloaded()
    {
        if(null != downloadFileChecker)
        {
            return downloadFileChecker.isFileAlreadyDownloaded(downloader);
        }
        return false;
    }

    /**
     * 下载回调方法——成功
     * @param downloader
     */
    void onDownloadSuccess(TGDownloader downloader)
    {
        // 下载完成，校验本地文件校验值与服务端下发的校验值是否一致
        if (!isDownloadFileCorrect())
        {
            downloader.setErrorCode(TGErrorMsgEnum.FAILED_CHECK_FILE_MD5.code);
            downloader.setErrorMsg(TGErrorMsgEnum.getErrorMsg(context, TGErrorMsgEnum.FAILED_CHECK_FILE_MD5));
            onDownloadFailed(downloader);
            return;
        }

        // 如果是断点下载，删除本地记录
        downloader.setDownloadStatus(TGDownloader.DOWNLOAD_SUCCEED);
        TGDownloadDBHelper.getInstance(context).deleteDownloader(downloader);

        downloadTask.onDownloadSuccess(downloader);
    }

    /**
     * 下载回调方法——开始
     * @param downloader
     */
    void onDownloadStart(TGDownloader downloader)
    {
        // 设置下载状态为开始
        downloader.setDownloadStatus(TGDownloader.DOWNLOAD_STARTING);

        downloadTask.onDownloadStart(downloader);
    }

    /**
     * 下载回调方法——下载中
     * @param downloader
     */
    void onDownloading(TGDownloader downloader)
    {
        // 如果是断点下载，更新本地记录
        downloader.setDownloadStatus(TGDownloader.DOWNLOAD_DOWNLOADING);
        TGDownloadDBHelper.getInstance(context).updateDownloader(downloader);

        // 服务端没返回文件长度时，不返回进度
        if (downloader.getFileSize() <= 0)
        {
            return;
        }

        downloadTask.onDownloading(downloader);
    }

    /**
     * 下载回调方法——暂停
     * @param downloader
     */
    void onDownloadPause(TGDownloader downloader)
    {
        downloader.setDownloadStatus(TGDownloader.DOWNLOAD_PAUSE);
        // 如果不是断点下载，删除本地文件和数据库记录; 断点下载，更新本地数据库下载状态
        if(!downloader.isBreakPoints())
        {
            FileUtils.deleteFile(downloader.getSavePath());
            TGDownloadDBHelper.getInstance(context).deleteDownloader(downloader);
        }
        else
        {
            TGDownloadDBHelper.getInstance(context).updateDownloader(downloader);
        }

        downloadTask.onDownloadPause(downloader);
    }

    /**
     * 下载回调方法——失败
     * @param downloader
     */
    void onDownloadFailed(TGDownloader downloader)
    {
        // 如果是下载前校验文件流出错，删除出错文件与数据，重新开始下载
        if (downloader.getErrorCode() == TGErrorMsgEnum.FAILED_CHECK_FILE_MD5.code &&
                retryCount <= MAX_RETRY)
        {
            retryCount++;
            retry(downloader);
            return;
        }

        downloader.setDownloadStatus(TGDownloader.DOWNLOAD_FAILED);
        // 如果不是断点下载，删除本地文件和数据库记录; 断点下载，更新本地数据库下载状态
        if (!downloader.isBreakPoints())
        {
            FileUtils.deleteFile(downloader.getSavePath());
            TGDownloadDBHelper.getInstance(context).deleteDownloader(downloader);
        }
        else
        {
            TGDownloadDBHelper.getInstance(context).updateDownloader(downloader);
        }

        downloadTask.onDownloadFailed(downloader);
    }

    /**
     * 重新尝试下载
     * @param downloader
     */
    private void retry(TGDownloader downloader)
    {
        LOG.d("[Method:retry] retryCount == " + retryCount);
        // 删除本地文件
        FileUtils.deleteFile(downloader.getSavePath());
        // 如果是断点下载，删除数据库记录
        if (downloader.isBreakPoints())
        {
            TGDownloadDBHelper.getInstance(context).deleteDownloader(downloader);
        }

        // 重新下载
        downloader.setCompleteSize(0);
        downloader.setCheckKey("");
        downloader.setErrorCode(0);
        downloader.setErrorMsg("");
    }

    protected abstract void onRetry(TGDownloader downloader);

    public abstract void cancel();

    /**
     * 下载回调方法——取消
     * @param downloader
     */
    void onDownloadCancel(TGDownloader downloader)
    {
        FileUtils.deleteFile(downloader.getSavePath());
        TGDownloadDBHelper.getInstance(context).deleteDownloader(downloader);
        downloadTask.onDownloadCancel(downloader);
    }

    protected abstract String getAcceptRangesFromResponseHeaders();
    /**
     * 下载的文件是否正确
     * @return
     */
    private boolean isDownloadFileCorrect()
    {
        if(null != downloadFileChecker)
        {
            return downloadFileChecker.isFileCorrect(downloader);
        }

        return true;
    }

    /**
     *
     * 该方法的作用: 获取文件保存路径
     *
     * @date 2014年8月23日
     * @return
     */
    private String getDownloadFileSavePath()
    {
        String savePath = downloader.getSavePath();
        if (savePath.endsWith("/"))
        {
            String fileName = getFileNameFromResponseHeaders();
            savePath = savePath + fileName;
        }

        return savePath;
    }

    /**
     * 该方法的作用: 获取服务器返回文件名
     *
     * @date 2014年8月23日
     * @return
     */
    protected abstract String getFileNameFromResponseHeaders();

    /**
     * 设置下载文件校验器
     * @param downloadFileChecker
     */
    public void setDownloadFileChecker(IDownloadFileChecker downloadFileChecker)
    {
        this.downloadFileChecker = downloadFileChecker;
    }

    /**
     * 该方法的作用:
     * 初始化网络请求结果
     * @date 2013-12-1
     * @return
     */
    protected TGHttpResult initHttpResult()
    {
        TGHttpResult httpResult = new TGHttpResult();
        httpResult.setResponseCode(TGHttpError.UNKNOWN_EXCEPTION);
        httpResult.setResult(TGHttpError.getDefaultErrorMsg(context, TGHttpError.UNKNOWN_EXCEPTION));
        return httpResult;
    }
}
