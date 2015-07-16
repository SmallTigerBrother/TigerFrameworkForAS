package com.mn.tiger.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * 
 * 该类作用及功能说明: 应用业务异常
 * 
 * @date 2014年7月8日
 */
public class TGBusinessException extends Exception
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3928451114441444507L;

	/** The error_code. */
	private String error_code = "";

	/** The error_message. */
	private String error_message = "";

	/** The cause. */
	private Throwable cause;

	/**
	 * Instantiates a new Business exception.
	 * 
	 * @date 2014年7月8日
	 * @param code
	 */
	public TGBusinessException(String code)
	{
		super(code);
		this.error_code = code;
	}

	/**
	 * Instantiates a new Business exception.
	 * 
	 * @date 2014年7月8日
	 * @param ex
	 */
	public TGBusinessException(Throwable ex)
	{
		this.cause = ex;
	}

	/**
	 * Instantiates a new Business exception.
	 * 
	 * @date 2014年7月8日
	 * @param code
	 * @param error_message
	 * @param ex
	 */
	public TGBusinessException(String code, String error_message, Throwable ex)
	{
		super(code);
		this.error_code = code;
		this.cause = ex;
		this.error_message = error_message;
	}

	/**
	 * 
	 * 该方法的作用: Gets the error_code.
	 * 
	 * @date 2014年7月8日
	 * @return
	 */
	public String getError_code()
	{
		return error_code;
	}

	/**
	 * 
	 * 该方法的作用: Sets the error_code.
	 * 
	 * @author pWX197040
	 * @date 2014年7月8日
	 * @param error_code
	 */
	public void setError_code(String error_code)
	{
		this.error_code = error_code;
	}

	/**
	 * Gets the error_message.
	 * 
	 * @return the error_message
	 */
	public String getError_message()
	{
		return error_message;
	}

	/**
	 * Sets the error_message.
	 * 
	 * @param error_message
	 *            the error_message to set
	 */
	public void setError_message(String error_message)
	{
		this.error_message = error_message;
	}

	/**
	 * Gets the cause.
	 * 
	 * @return the cause
	 */
	@Override
	public Throwable getCause()
	{
		return cause;
	}

	/**
	 * Sets the cause.
	 * 
	 * @param cause
	 *            the cause to set
	 */
	public void setCause(Throwable cause)
	{
		this.cause = cause;
	}

	/**
	 * getMessage.
	 * 
	 * @return String
	 */
	@Override
	public String getMessage()
	{
		String message = super.getMessage();
		Throwable cause = getCause();
		if (cause != null)
		{
			message = message + "    " + error_message + ";    \n" + cause.getMessage();
		}
		return message + "    " + error_message + "";
	}

	/**
	 * printStackTrace.
	 * 
	 * @param ps
	 *            ps
	 * @version
	 */
	@Override
	public void printStackTrace(PrintStream ps)
	{
		if (getCause() == null)
		{
			super.printStackTrace(ps);

		}
		else
		{
			ps.println(this);
			getCause().printStackTrace(ps);
		}
	}

	/**
	 * printStackTrace.
	 * 
	 * @param pw
	 *            pw
	 * @version
	 */
	@Override
	public void printStackTrace(PrintWriter pw)
	{
		if (getCause() == null)
		{
			super.printStackTrace(pw);
		}
		else
		{
			pw.println(this);
			getCause().printStackTrace(pw);
		}
	}

	/**
	 * printStackTrace.
	 * 
	 * @version
	 */
	@Override
	public void printStackTrace()
	{
		printStackTrace(System.err);
	}

}
