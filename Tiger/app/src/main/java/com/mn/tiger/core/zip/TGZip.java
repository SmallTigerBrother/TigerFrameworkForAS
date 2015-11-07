package com.mn.tiger.core.zip;

/**
 * @date 2013-2-22
 *
 */

import com.mn.tiger.log.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * 该类作用及功能说明---解压、压缩文本工具类
 *
 */
public class TGZip
{
	private static final Logger LOG = Logger.getLogger(TGZip.class);

	/**
	 * gzip 解压
	 *
	 * @param compressed
	 *            已压缩的文本
	 * @return 原文
	 */
	public static String gzipDecompress(byte[] compressed)
	{
		if (compressed == null)
		{
			return null;
		}

		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		GZIPInputStream gzin = null;
		String decompressed;
		try
		{
			out = new ByteArrayOutputStream();
			in = new ByteArrayInputStream(compressed);
			gzin = new GZIPInputStream(in);

			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = gzin.read(buffer)) != -1)
			{
				out.write(buffer, 0, offset);
			}

			decompressed = new String(out.toByteArray(), "UTF-8"); // out.toString();

		}
		catch (IOException e)
		{
			decompressed = null;
			LOG.e("[Method:gzipDecompress]", e);
		}
		finally
		{
			if (gzin != null)
			{
				try
				{
					gzin.close();
				}
				catch (IOException e)
				{
					LOG.e("[Method:gzipDecompress]", e);
				}
			}
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
					LOG.e("[Method:gzipDecompress]", e);
				}
			}
			if (out != null)
			{
				try
				{
					out.close();
				}
				catch (IOException e)
				{
					LOG.e("[Method:gzipDecompress]", e);
				}
			}
		}
		return decompressed;
	}

	/**
	 * gzip压缩
	 *
	 * @param str
	 *            原文
	 * @return gzip压缩后的文本
	 * @throws UnsupportedEncodingException
	 *
	 *
	 */
	public static byte[] gzipCompress(String str) throws UnsupportedEncodingException
	{
		if (str == null)
		{
			return null;
		}

		byte[] compressed;
		ByteArrayOutputStream out = null;
		GZIPOutputStream gzip = null;

		try
		{
			out = new ByteArrayOutputStream();
			gzip = new GZIPOutputStream(out);
			gzip.write(str.getBytes("UTF-8"));
			gzip.flush();
			gzip.close();

			compressed = out.toByteArray();
		}
		catch (IOException e)
		{
			LOG.e("[Method:gzipCompress]", e);
			compressed = null;
		}
		finally
		{
			if (gzip != null)
			{
				try
				{
					gzip.close();
				}
				catch (IOException e)
				{
					LOG.e("[Method:gzipCompress]", e);
				}
			}
			if (out != null)
			{
				try
				{
					out.close();
				}
				catch (IOException e)
				{
					LOG.e("[Method:gzipCompress]", e);
				}
			}
		}
		return compressed;
	}
}
