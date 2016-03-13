package com.mn.tiger.download.db;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mn.tiger.download.DownloadStatus;
import com.mn.tiger.download.TGDownloadParams;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Dalang on 2016/3/13.
 */
public class TGDownloader extends Downloader
{
    private static final Gson GSON = new Gson();

    private HashMap<String,String> paramsMap;

    private HashMap<String, String> extrasMap;

    public static TGDownloader fromTGDownloadParams(TGDownloadParams downloadParams, int downloadTaskId)
    {
        TGDownloader downloader = TGDownloadDBHelper.getInstance().findDownloader(downloadParams.getUrl(),
                downloadParams.getParams(), downloadParams.getSavePath());

        if(downloader == null)
        {
            downloader = new TGDownloader();
            downloader.setId(downloadTaskId);
            downloader.setUrl(downloadParams.getUrl());
            if(null != downloadParams.getParams() && !downloadParams.getParams().isEmpty())
            {
                downloader.paramsMap = downloadParams.getParams();
            }
            downloader.setRequestType(downloadParams.getRequestType());
            downloader.setDownloadType(downloadParams.getDownloadType());
            downloader.setSavePath(downloadParams.getSavePath());
            downloader.setTaskClsName(downloadParams.getTaskClsName());
            downloader.setParamsClsName(downloadParams.getClass().getName());
            downloader.setDownloadStatus(DownloadStatus.WAITING);
            downloader.setAccessRanges(false);
            downloader.setCreateTime(new Date());
            downloader.extrasMap = downloadParams.getExtras();
            downloader.setSoftDelete(false);
        }
        else
        {
            //重置数据
            if(downloader.getSoftDelete())
            {
                downloader.setCreateTime(new Date());
                downloader.setSoftDelete(false);
            }
        }

        return downloader;
    }

    static TGDownloader fromDownloader(Downloader downloader)
    {
        if(null != downloader)
        {
            TGDownloader tgDownloader = new TGDownloader();
            if(!TextUtils.isEmpty(downloader.getParams()))
            {
                tgDownloader.paramsMap = GSON.fromJson(downloader.getParams(), new TypeToken<HashMap<String,String>>(){}.getType());
            }
            if(!TextUtils.isEmpty(downloader.getParams()))
            {
                tgDownloader.extrasMap = GSON.fromJson(downloader.getExtras(), new TypeToken<HashMap<String,String>>(){}.getType());
            }
            tgDownloader.setId(downloader.getId());
            tgDownloader.setUrl(downloader.getUrl());
            tgDownloader.setRequestType(downloader.getRequestType());
            tgDownloader.setSavePath(downloader.getSavePath());
            tgDownloader.setParams(downloader.getParams());
            tgDownloader.setParamsClsName(downloader.getParamsClsName());
            tgDownloader.setTaskClsName(downloader.getTaskClsName());
            tgDownloader.setCheckKey(downloader.getCheckKey());
            tgDownloader.setFileSize(downloader.getFileSize());
            tgDownloader.setCompleteSize(downloader.getCompleteSize());
            tgDownloader.setDownloadStatus(downloader.getDownloadStatus());
            tgDownloader.setDownloadType(downloader.getDownloadType());
            tgDownloader.setErrorCode(downloader.getErrorCode());
            tgDownloader.setErrorMsg(downloader.getErrorMsg());
            tgDownloader.setAccessRanges(downloader.getAccessRanges());
            tgDownloader.setCreateTime(downloader.getCreateTime());
            return tgDownloader;
        }
        return null;
    }

    public static String getParamsString(HashMap<String, String> params)
    {
        if(null != params && !params.isEmpty())
        {
            return GSON.toJson(params);
        }
        return "";
    }

    public HashMap<String, String> getParamsMap()
    {
        return paramsMap;
    }

    public HashMap<String, String> getExtrasMap()
    {
        return extrasMap;
    }

    public Downloader toDownloader()
    {
        if(null != paramsMap && !paramsMap.isEmpty() && TextUtils.isEmpty(getParams()))
        {
            setParams(GSON.toJson(paramsMap));
        }
        if(null != extrasMap && !extrasMap.isEmpty() && TextUtils.isEmpty(getParams()))
        {
            setParams(GSON.toJson(extrasMap));
        }
        return this;
    }
}
