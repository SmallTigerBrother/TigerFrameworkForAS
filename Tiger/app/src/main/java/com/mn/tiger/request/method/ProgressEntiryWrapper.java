package com.mn.tiger.request.method;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

public class ProgressEntiryWrapper extends HttpEntityWrapper
{
	private ProgressListener progressListener;
	
	public ProgressEntiryWrapper(HttpEntity wrapped, ProgressListener progressListener)
	{
		super(wrapped);
		this.progressListener = progressListener;
	}
	
	@Override
	public void writeTo(OutputStream outstream) throws IOException
	{
		super.writeTo(outstream instanceof CountingOutputStream ? outstream : new CountingOutputStream(outstream, this.progressListener));
	}
	
	public static class CountingOutputStream extends FilterOutputStream
	{
		private ProgressListener progressListener;
		
		private long transfered = 0;
		
		public CountingOutputStream(OutputStream out, ProgressListener progressListener)
		{
			super(out);
			this.progressListener = progressListener;
		}
		
		@Override
		public void write(byte[] buffer, int offset, int length) throws IOException
		{
			super.write(buffer, offset, length);
			this.transfered += length;
			progressListener.transferred(transfered);
		}
		
		@Override
		public void write(int oneByte) throws IOException
		{
			super.write(oneByte);
			this.transfered++;
			progressListener.transferred(transfered);
		}
	}
	
	/**
	 * 进度监听器接口
	 */
	public static interface ProgressListener
	{
		/**
		 * 已写入数据大小
		 * @param num
		 */
		void transferred(long num);
	}

}

