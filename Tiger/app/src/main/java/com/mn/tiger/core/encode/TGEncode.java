package com.mn.tiger.core.encode;

import it.sauronsoftware.base64.Base64;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.mn.tiger.core.cache.TGCache;
import com.mn.tiger.utility.FileUtils;
import com.mn.tiger.core.zip.TGZip;

public class TGEncode
{
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 
	 * 该方法的作用:把byte数组转换成十六进制的字符串
	 * 
	 * @date 2013-2-26
	 * @param b
	 * @return
	 */
	private static String toHexString(byte[] b)
	{
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++)
		{
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	/**
	 * 
	 * 该方法的作用:用AES解密
	 * 
	 * @date 2013-2-26
	 * @param hieroglyph
	 *            AES加密的字符串
	 * @param key
	 *            密钥
	 * @param isUnZip
	 *            是否需要压缩   
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] getAESDecrypt(String key, String hieroglyph, boolean isUnZip) throws Exception
	{
		try
		{
			SecretKey k = new SecretKeySpec(string2Bytes(key), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, k);

			byte[] bt = cipher.doFinal(string2Bytes(hieroglyph));
			if(isUnZip)
			{
				bt = TGZip.gzipDecompress(bt).getBytes("utf-8");
			} 
			return bt;
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	/**
	 * 
	 * 该方法的作用:用AES解密
	 * 
	 * @date 2013-2-26
	 * @param hieroglyph
	 *            AES加密的字符串
	 * @param key
	 *            密钥
	 * @return String
	 * @throws Exception
	 */
	public static String getAESDecryptLocal(String key, String hieroglyph) throws Exception
	{
		try
		{
			byte[] bt = getAESDecrypt(key, hieroglyph, false);
			return new String(bt, "UTF-8");
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	public static byte[] string2Bytes(String str) throws UnsupportedEncodingException
	{
		return Base64.decode(str.getBytes("UTF-8"));
	}

	public static String bytes2String(byte[] b)
	{
		try
		{
			return new String(Base64.encode(b), "UTF-8");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

	public static String getAESEncryptLocal(String key, String vivid)
	{
		try
		{
			byte[] encodedData = TGEncode.getAESEncrypt(key, vivid.getBytes("UTF-8"), false);
			return bytes2String(encodedData);
		}
		catch (Exception e)
		{
		}
		return "";
	}

	/**
	 * 
	 * 该方法的作用:用AES加密数据
	 * 
	 * @param vivid
	 *            需要加密的byte[]
	 * @param key
	 *            密钥
	 * @param isZip
	 *            是否需要压缩         
	 * @return byte[] 加密后byte数组
	 */
	@SuppressLint("TrulyRandom")
	public static byte[] getAESEncrypt(String key, byte[] vivid, boolean isZip) throws Exception
	{
		try
		{
			SecretKey k = new SecretKeySpec(string2Bytes(key), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, k);
			byte[] bt = cipher.doFinal(isZip ? TGZip.gzipCompress(new String(vivid, "UTF-8")) : vivid);
			return bt;
		}
		catch (IOException e)
		{
			throw e;
		}
	}

	/**
	 * 
	 * 该方法的作用:用RSA加密数据
	 * 
	 * @param key
	 * @param hieroglyph
	 * @return
	 */
	public static String getRSAEncrypt(String encryptkey, String data) throws Exception
	{
		PublicKey key = getPublicKeyByString(encryptkey);
		byte[] bytes = data.getBytes("UTF-8");
		byte[] en = getRSAEncrypt(key, bytes);
		byte[] b = Base64.encode(en);
		return (new String(b, "UTF-8"));

	}

	/**
	 * 
	 * 该方法的作用:RSA加密,公钥加密，私钥解密，反之亦然
	 * 
	 * @date 2013-2-26
	 * @param data
	 * @param publicKey
	 *            公钥
	 * @return
	 * @throws Exception
	 */
	public static byte[] getRSAEncrypt(PublicKey publicKey, byte[] data) throws Exception
	{
		if (publicKey != null)
		{
			try
			{
				Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
				SecureRandom random = new SecureRandom();
				cipher.init(Cipher.ENCRYPT_MODE, publicKey, random);
				return cipher.doFinal(data);
			}
			catch (Exception e)
			{
				throw e;
			}
		}
		return null;
	}

	/**
	 * 该方法的作用:获取RSA公钥
	 * 
	 * @date 2013-2-26
	 * @param key
	 * @return 返回公钥对象(PublicKey)
	 * @throws Exception
	 */
	private static PublicKey getPublicKeyByString(String key) throws Exception
	{
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		byte[] b1 = Base64.decode(key.getBytes("UTF-8"));
		X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(b1);
		PublicKey publicKey = keyFactory.generatePublic(bobPubKeySpec);
		return publicKey;
	}

	/**
	 * 
	 * 该方法的作用:MD5加密
	 * 
	 * @date 2013-2-26
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String getMD5Encrypt(byte[] data) throws Exception
	{
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(data);
		return toHexString(md5.digest());
	}

	/**
	 * 
	 * 该方法的作用:MD5加密
	 * 
	 * @date 2013-2-26
	 * @param inStr
	 *            需要加密的String
	 * @return String
	 * @throws Exception
	 */
	public static String getMD5Encrypt(String inStr) throws Exception
	{
		/** 定义数字签名方法, 可用：MD5, SHA-1 */
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] md5Bytes = md.digest(inStr.getBytes("UTF-8"));
		return toHexString(md5Bytes);
	}

	/**
	 * 
	 * 该方法的作用:对数据流读取的信息进行加密
	 * 
	 * @date 2013-2-26
	 * @param input
	 *            数据流读取的信息
	 * @return 加密后的String
	 */
	public static String getMD5Encrypt(InputStream input) throws Exception
	{
		byte[] buffer = new byte[1024];
		int numRead = 0;
		MessageDigest md5;
		try
		{
			md5 = MessageDigest.getInstance("MD5");
			while ((numRead = input.read(buffer)) > 0)
			{
				md5.update(buffer, 0, numRead);
			}
			return toHexString(md5.digest());
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			if (input != null)
			{
				try
				{
					input.close();
				}
				catch (IOException e)
				{
					throw e;
				}
			}
		}
	}

	/**
	 * 
	 * 该方法的作用:文件加密,适应于大文件的加密
	 * 
	 * @date 2013-3-25
	 * @param context
	 * @param file
	 * @throws Exception
	 */
	public static void simpleEncodeFile(Context context, File file) throws Exception
	{
		if (!file.exists())
		{
			throw new FileNotFoundException();
		}
		String fileContent = FileUtils.readFile(file.getAbsolutePath());
		int cutLen = 100;
		if (fileContent.length() < 100)
		{
			cutLen = fileContent.length() / 2;
		}
		String subContent = fileContent.substring(0, cutLen);
		fileContent = fileContent.substring(cutLen);
		String encodeContent = fileContent.concat(subContent);
		file.delete();
		FileUtils.createFileThroughContent(file.getAbsolutePath(), encodeContent);
	}

	/**
	 * 
	 * 该方法的作用:文件解密,适用于大文件的解密
	 * 
	 * @date 2013-3-25
	 * @param context
	 * @param file
	 * @throws Exception
	 */
	public static void simpleDecodeFile(Context context, File file) throws Exception
	{
		if (!file.exists())
		{
			throw new FileNotFoundException();
		}
		String fileContent = FileUtils.readFile(file.getAbsolutePath());
		int cutLen = 100;
		if (fileContent.length() < 100)
		{
			cutLen = fileContent.length() / 2;
		}
		String subContent = fileContent.substring(fileContent.length() - cutLen);
		fileContent = fileContent.substring(0, fileContent.length() - cutLen);
		String decodeContent = subContent.concat(fileContent);
		file.delete();
		FileUtils.createFileThroughContent(file.getAbsolutePath(), decodeContent);
	}
	
	/**
	 * 该方法的作用:
	 * 加密AESKey
	 * @date 2014年8月26日
	 * @param context
	 * @param aesKey
	 * @return
	 */
	public static String encryptAESKey(Context context, String aesKey)
	{
		if(!TextUtils.isEmpty(aesKey))
		{
			return encryptAESKey(context, aesKey, 24);
		}
		
		return aesKey;
	}
	
	/**
	 * 该方法的作用:
	 * 加密AESKey
	 * @date 2014年8月26日
	 * @param context
	 * @param aesKey
	 * @param maxConfuseCount
	 * @return
	 */
	private static String encryptAESKey(Context context, String aesKey, int maxConfuseCount)
	{
		int confuseCount = 1;
		StringBuilder strBuilder = new StringBuilder(aesKey);
		ArrayList<ConfuseChar> reaList = new ArrayList<TGEncode.ConfuseChar>();
		
		while(confuseCount <= maxConfuseCount)
		{
			confuseCount++;
			int randomIndex = (int) (Math.random() * (aesKey.length() - 2));
			reaList.add(new ConfuseChar(randomIndex, strBuilder.charAt(randomIndex)));
			strBuilder.replace(randomIndex, randomIndex + 1, getConfusedChar());
		}
		
		TGCache.saveCache(context, "SEATPYCNE", reaList);
		
		return strBuilder.toString();
	}
	
	/**
	 * 该方法的作用:
	 * 获取混淆字符
	 * @date 2014年8月26日
	 * @return
	 */
	private static String getConfusedChar()
	{
		int index = (int) (Math.random() * (ConfuseChar.CONFUSECHARS.length - 1));
		return ConfuseChar.CONFUSECHARS[index];
	}
	
	/**
	 * 该方法的作用:
	 * 解密AESKey
	 * @date 2014年8月26日
	 * @param context
	 * @param aesKey
	 * @return
	 */
	public static String decrypytAESKey(Context context, String aesKey)
	{
		@SuppressWarnings("unchecked")
		ArrayList<ConfuseChar> reaList = (ArrayList<ConfuseChar>) TGCache.getCache(context, "SEATPYCNE");
		
		if(!TextUtils.isEmpty(aesKey) && null != reaList)
		{
			Collections.reverse(reaList);
			StringBuilder strBuilder = new StringBuilder(aesKey);
			int count = aesKey.length();
			for(int i = 0; i < count; i++)
			{
				ConfuseChar confuseChar = reaList.get(i);
				strBuilder.replace(confuseChar.index, confuseChar.index + 1, confuseChar.value + "");
			}
			return strBuilder.toString();
		}
		
		return aesKey;
	}
	
	private static class ConfuseChar implements Serializable
	{
		public static final String[] CONFUSECHARS = 
			{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V",
			"W","X","Y","Z","1","2","3","4","5","6","7","8","9","10"};
		
		/**
		 * @date 2014年8月23日
		 */
		private static final long serialVersionUID = 1L;

		public ConfuseChar(int index, char value)
		{
			this.index = index;
			this.value = value;
		}
		
		public int index;
		
		public char value;
	}
	
}
