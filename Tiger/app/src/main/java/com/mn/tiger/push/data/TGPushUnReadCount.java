package com.mn.tiger.push.data;

import java.io.Serializable;

public abstract class TGPushUnReadCount implements Serializable
{
	private static final long serialVersionUID = 1L;

	public abstract int getAllUnReadCount();
}
