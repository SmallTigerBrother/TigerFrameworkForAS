package com.mn.tiger.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mn.tiger.R;
import com.mn.tiger.utility.DisplayUtils;


/**
 * 该类作用及功能说明 搜索视图类
 * 
 * @version V2.0
 * @see JDK1.6,android-8
 */
public class TGSearchView extends RelativeLayout
{
	/**
	 * 日志标签
	 */
	protected final String LOG_TAG = this.getClass().getSimpleName();

	/**
	 * 查询内容事件监听器
	 */
	private OnQueryTextListener onQueryTextListener;

	/**
	 * 关闭图标
	 */
	private ImageView closeIcon;

	/**
	 * 搜索内容输入框
	 */
	private AutoCompleteTextView queryEditText;

	/**
	 * 搜索图标
	 */
	private TextView searchIcon;

	/**
	 * 关闭图标是否可用
	 */
	private boolean closeIconEnable = true;

	/**
	 * @date 2013-1-29 构造函数
	 * @param context
	 *            运行环境
	 */
	public TGSearchView(Context context)
	{
		super(context);
		setupViews();
	}

	/**
	 * @date 2013-1-29 构造函数
	 * @param context
	 *            运行环境
	 * @param attrs
	 *            属性
	 */
	public TGSearchView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setupViews();
	}

	/**
	 * 该方法的作用: 初始化所有视图
	 * 
	 * @date 2013-1-29
	 */
	private void setupViews()
	{
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.tiger_searchview, this);

		closeIcon = (ImageView) findViewById(R.id.tiger_searchview_closeicon);
		closeIcon.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				queryEditText.setText("");
				closeIcon.setVisibility(View.GONE);

				if (null != onQueryTextListener)
				{
					onQueryTextListener.onTextCleaned();
				}
			}
		});

		queryEditText = (AutoCompleteTextView) findViewById(R.id.tiger_searchview_query);
		queryEditText.setOnKeyListener(new OnKeyListener()
		{
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent keyEvent)
			{
				if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP
						&& null != onQueryTextListener)
				{
					InputMethodManager inputMethodManager = (InputMethodManager) getContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(queryEditText.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);

					onQueryTextListener.onQueryTextSubmit(queryEditText.getText());
					return true;
				}
				return false;
			}
		});

		queryEditText.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence text, int start, int before, int count)
			{
				if (null != text && !text.toString().equals("") && closeIconEnable)
				{
					closeIcon.setVisibility(View.VISIBLE);
				}
				else
				{
					closeIcon.setVisibility(View.GONE);
				}
				if (null != onQueryTextListener)
				{
					onQueryTextListener.onQueryTextChange(text);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence text, int start, int count, int after)
			{
			}

			@Override
			public void afterTextChanged(Editable editable)
			{
			}
		});
		
		queryEditText.setAdapter(new ArrayAdapter<CharSequence>(getContext(), 
				R.layout.tiger_search_pop_list_item));
		queryEditText.setThreshold(0);
		queryEditText.setDropDownHorizontalOffset(DisplayUtils.dip2px(getContext(), -8));
		queryEditText.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener()
		{
			@Override
			public boolean onPreDraw()
			{
				queryEditText.setDropDownWidth(queryEditText.getWidth());
				queryEditText.getViewTreeObserver().removeOnPreDrawListener(this);
				return false;
			}
		});

		searchIcon = ((TextView) findViewById(R.id.tiger_searchview_submitbutton));
		searchIcon.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				InputMethodManager inputMethodManager = (InputMethodManager) getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(queryEditText.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				
				if (onQueryTextListener != null)
				{
					onQueryTextListener.onQueryTextSubmit(queryEditText.getText());
				}
			}
		});
	}

	/**
	 * 该方法的作用: 设置查询内容
	 * 
	 * @date 2013-1-29
	 * @param query
	 *            查询内容
	 */
	public void setQuery(CharSequence query)
	{
		if (null != query && null != queryEditText)
		{
			queryEditText.setText(query);
		}
	}

	/**
	 * 该方法的作用: 获取输入框
	 * 
	 * @date 2013-3-25
	 * @return
	 */
	public AutoCompleteTextView getInputTextView()
	{
		return queryEditText;
	}

	/**
	 * 该方法的作用: 设置查询字体颜色
	 * 
	 * @date 2013-1-29
	 * @param color
	 *            字体颜色
	 */
	public void setQueryTextColor(int color)
	{
		if (null != queryEditText)
		{
			queryEditText.setTextColor(color);
		}
	}

	/**
	 * 该方法的作用: 设置查询字体大小
	 * 
	 * @date 2013-1-29
	 * @param size
	 *            字体大小
	 */
	public void setQueryTextSize(float size)
	{
		if (null != queryEditText)
		{
			queryEditText.setTextSize(size);
		}
	}

	/**
	 * 该方法的作用: 设置查询提示
	 * 
	 * @date 2013-1-29
	 * @param hint
	 *            提示
	 */
	public void setQueryHint(CharSequence hint)
	{
		if (null != hint && null != queryEditText)
		{
			queryEditText.setHint(hint);
		}
	}

	/**
	 * 该方法的作用: 设置查询提示文字颜色
	 * 
	 * @date 2013-1-29
	 * @param color
	 *            提示文字颜色
	 */
	public void setQueryHintColor(int color)
	{
		if (null != queryEditText)
		{
			queryEditText.setHintTextColor(color);
		}
	}

	/**
	 * 该方法的作用: 设置关闭按钮是否可用
	 * 
	 * @date 2013-1-29
	 * @param enable
	 *            是否可用
	 */
	public void setCloseEnabled(boolean enable)
	{
		if (null == closeIcon)
		{
			return;
		}
		this.closeIconEnable = enable;
		if (closeIconEnable)
		{
			closeIcon.setVisibility(View.VISIBLE);
		}
		else
		{
			closeIcon.setVisibility(View.GONE);
		}
	}

	/**
	 * 该方法的作用: 设置关闭图标
	 * 
	 * @date 2013-1-29
	 * @param drawable
	 *            关闭图标
	 */
	public void setCloseIcon(Drawable drawable)
	{
		if (null != drawable && null != closeIcon)
		{
			this.closeIcon.setImageDrawable(drawable);
		}
	}

	/**
	 * 该方法的作用: 获取关闭图标视图
	 * 
	 * @date 2013-1-31
	 * @return 关闭图标视图
	 */
	public ImageView getCloseIconView()
	{
		return closeIcon;
	}

	/**
	 * 该方法的作用: 设置查询内容事件监听器
	 * 
	 * @date 2013-1-29
	 * @param listener
	 *            内容事件监听器
	 */
	public void setOnQueryTextListener(OnQueryTextListener listener)
	{
		this.onQueryTextListener = listener;
	}

	/**
	 * 该方法的作用: 设置焦点变化事件监听器
	 * 
	 * @date 2013-1-29
	 * @param listener
	 *            焦点变化事件监听器
	 */
	public void setOnQueryTextFocusChangeListener(OnFocusChangeListener listener)
	{
		if (null != queryEditText && null != listener)
		{
			queryEditText.setOnFocusChangeListener(listener);
		}
	}

	/**
	 * 该方法的作用: dip转换为px
	 * 
	 * @date 2013-2-7
	 * @param dpValue
	 *            dip的数值
	 * @return
	 */
	protected int dip2px(float dpValue)
	{
		float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 该方法的作用: 获取搜索关键字
	 * @author l00220455
	 * @date 2013-6-17
	 * @return
	 */
	public String getQueryText()
	{
		return queryEditText.getText().toString();
	}

	/**
	 * 设置搜索框可见性
	 * @param visible
	 */
	public void setSearchIconVisible(int visible)
	{
		this.searchIcon.setVisibility(visible);
	}

	/**
	 * 该类作用及功能说明 查询内容事件监听器接口，当查询内容发生变化或提交时触发该事件
	 * 
	 * @version V2.0
	 * @see JDK1.6,android-8
	 */
	public static interface OnQueryTextListener
	{
		/**
		 * 该方法的作用: 查询内容变化时的回调方法
		 * 
		 * @date 2013-1-29
		 * @param newText
		 *            新的查询内容
		 */
		public void onQueryTextChange(CharSequence newText);

		/**
		 * 该方法的作用: 提交查询内容时的回调方法
		 * 
		 * @date 2013-1-29
		 * @param query
		 *            查询内容
		 */
		public void onQueryTextSubmit(CharSequence query);
		
		/**
		 * 当输入内容被清空时的回调方法
		 */
		public void onTextCleaned();
	}
	
	/**
	 * 布局参数LayoutParams
	 */
	public static class LayoutParams extends RelativeLayout.LayoutParams
	{
		public LayoutParams(int w, int h)
		{
			super(w, h);
		}
		
		public LayoutParams(MarginLayoutParams source)
		{
			super(source);
		}
		
		public LayoutParams(RelativeLayout.LayoutParams source)
		{
			super(source);
		}
	}
}
