package com.mn.tiger.request.error;

import android.content.Context;
import android.content.res.Resources.NotFoundException;

import com.mn.tiger.log.Logger;
import com.mn.tiger.utility.CR;
import com.mn.tiger.utility.Commons;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

/**
 * Http请求异常
 */
public enum TGErrorMsgEnum
{
	/** 文件下载错误码 */
	FAILED_CHECK_FILE_MD5(10001, "下载文件与服务端文件MD5校验不一致", "download file MD5 is not the same with server."),
	FAILED_GET_FILE_SIZE(10002, "获取文件大小失败", "Failed to get file size"),
	DOWNLOAD_SAVE_PATH_IS_NULL(10003, "下载保存地址为空","The save path is null"),
	NOT_SUPPORT_BREAKPOINT(10004, "该服务不支持断点下载","This service not support breakpoint download."),
	SDCARD_NOT_AVALIABLE(10005, "内存卡不可用","The sdCard is not avaliable"),
	USABLE_SPACE_NOT_ENOUGH(10006, "可用空间不足，请清理空间","The usable space is not enough"),
	FAILED_DELETE_FILE(10007, "文件删除失败", "Failed to delete file"),

	FAILED_LOCAL_DATABASE(10010, "本地数据库异常", "local database error"),
	UNKNOWHOST_EXCEPTION(10014,"服务器连接异常", "Unknow host"),

	SERVER_EXCEPTION(10015, "服务异常，请联系管理员", "Service is error,Please contact the admin"),
	VERIFY_UNSUCCESSFULLY(10016,"校验失败", "Verify unsuccessfully."),
	SYSTEM_ERROR(10017, "系统错误", "System error"),
	FILE_NOT_EXITS(10018,"文件不存在", "file not exits"),
	FILE_IS_EMPTY(10019, "文件内容为空", "file is empty");

	public final int code;
	public final String cnMsg;
	public final String enMsg;

	private static final Logger LOG = Logger.getLogger(TGErrorMsgEnum.class);

	TGErrorMsgEnum(int code, String cnMsg, String enMsg)
	{
		this.code = code;
		this.cnMsg = cnMsg;
		this.enMsg = enMsg;
	}

	/**
	 * 该方法的作用:根据当前应用语言获取对应的错误信息
	 *
	 * @date 2014年1月10日
	 * @param context
	 * @param error
	 * @return <p>
	 *         先从资源文件里面查找是否有指定格式的错误信息，如果有，则返回当前语言下的错误信息(格式为：error_错误码,如：
	 *         error_10001)</br> 如果没有，则返回当前语言下的默认错误描述信息（默认错误信息只支持中英文）
	 *         </p>
	 */
	public static String getErrorMsg(Context context, TGErrorMsgEnum error)
	{
		String errorMsg = "";
		if (null != error)
		{
			HashMap<String, String> errorMap = new HashMap<String, String>();
			try
			{
				// 先获取资源文件的对应错误提示信息
				errorMsg = context.getString(CR.getStringId(context, error.name()));
			}
			catch (NotFoundException e)
			{
				LOG.e("[Method:getErrorMsg] string resource of " + error.name() + " is not found..");
				errorMsg = "";
			}

			// 如果没有则获取当前语言的默认提示
			if (errorMsg.equals(""))
			{
				String currLanguage = Commons.getSystemLanguage(context);
				if (currLanguage.equals(Locale.CHINESE.toString()))
				{
					errorMsg = error.cnMsg;
				}
				else
				{
					errorMsg = error.enMsg;
				}
			}
			errorMap.put("errorInfo", "(" + error.code + ")" + errorMsg);
			JSONObject jsonObj = new JSONObject(errorMap);
			return jsonObj.toString();
		}
		else
		{
			return errorMsg;
		}
	}
}
