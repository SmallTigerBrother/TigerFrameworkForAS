package com.mn.tiger.download;

/**
 * Created by Dalang on 2016/3/13.
 */
public class DownloadStatus
{
    /**
     * 下载状态——等待
     */
    public static final int WAITING = -2;

    /**
     * 下载状态——开始
     */
    public static final int STARTING = -1;

    /**
     /**
     * 下载状态——下砸中
     */
    public static final int DOWNLOADING = 0;

    /**
     * 下载状态——成功
     */
    public static final int SUCCEED = 1;

    /**
     * 下载状态——失败
     */
    public static final int FAILED = 2;

    /**
     * 下载状态——暂停
     */
    public static final int PAUSE = 3;

    /**
     * 下载状态——取消
     */
    public static final int CANCEL = 4;
}
