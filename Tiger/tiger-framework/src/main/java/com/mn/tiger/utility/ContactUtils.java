package com.mn.tiger.utility;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;

/**
 * 该类作用及功能说明:通讯录联系人相关
 * 
 * @date 2014年3月19日
 */
public class ContactUtils
{
	/**
	 * 该方法的作用:新建联系人
	 * 
	 * @date 2014年3月19日
	 * @param context
	 * @param name
	 * @param email
	 * @param phone
	 */
	public static void insertContact(Activity context, String name, String email, String phone)
	{
		Intent intent = new Intent(Intent.ACTION_INSERT);
		intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
		intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
		intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
		intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email);
		context.startActivityForResult(intent, 1);
	}
}
