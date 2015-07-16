package com.mn.tiger.log;

public class LogConfig
{

	/**
	 * 可以使TAG, 也可以使包名， 支持通配符: com.jianq.* 格式 表示com.jianq下面的所有子包， 都是用该配置
	 */
	public String filter;

	public LogLevel logLevel;
}
