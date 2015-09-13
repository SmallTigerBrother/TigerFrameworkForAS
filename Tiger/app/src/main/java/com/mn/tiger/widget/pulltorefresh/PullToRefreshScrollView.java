/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.mn.tiger.widget.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

public class PullToRefreshScrollView extends BGARefreshLayout implements IPullToRefreshView
{
	public PullToRefreshScrollView(Context context)
	{
		super(context);
	}

	public PullToRefreshScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	public void setMode(Mode mode)
	{

	}

	@Override
	public void setOnRefreshListener(OnRefreshListener listener)
	{

	}

	@Override
	public void onRefreshComplete()
	{

	}
}
