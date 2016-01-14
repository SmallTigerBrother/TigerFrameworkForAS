package com.mn.tiger.utility;

import android.app.Activity;
import android.content.Intent;

/**
 * 该类作用及功能说明:邮件相关类
 * 
 * @date 2014年3月19日
 */
public class EMailUtils
{
	/**
	 * 该方法的作用:调用手机中email应用发送邮件
	 * 
	 * @date 2014年3月19日
	 * @param context
	 * @param receivers
	 * @param copys
	 * @param title
	 * @param content
	 */
	public static void sendMail(Activity context, String[] receivers, String[] copys, String title,
			String content)
	{
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("plain/text");
		//接收者
		intent.putExtra(Intent.EXTRA_EMAIL, receivers);
		//抄送者
		intent.putExtra(Intent.EXTRA_CC, copys);
		//邮件Title
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
		//邮件内容
		intent.putExtra(Intent.EXTRA_TEXT, content);
		context.startActivity(Intent.createChooser(intent, null));
	}

}
