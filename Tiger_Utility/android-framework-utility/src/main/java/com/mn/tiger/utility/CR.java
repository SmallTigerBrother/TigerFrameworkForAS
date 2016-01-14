package com.mn.tiger.utility;

import android.content.Context;
import android.content.res.Resources;

import java.lang.reflect.Field;

public class CR
{
	public static int getMipmapId(Context context, String name)
	{
		Resources resources = context.getResources();
		return resources.getIdentifier(name, "mipmap", context.getPackageName());
	}

	public static int getDrawableId(Context context, String name)
	{
		Resources res = context.getResources();
		return res.getIdentifier(name, "drawable", context.getPackageName());
	}

	public static int getLayoutId(Context context, String name)
	{
		Resources res = context.getResources();
		return res.getIdentifier(name, "layout", context.getPackageName());
	}

	public static int getStringId(Context context, String name)
	{
		Resources res = context.getResources();
		return res.getIdentifier(name, "string", context.getPackageName());
	}

	public static int getColorId(Context context, String name)
	{
		Resources res = context.getResources();
		return res.getIdentifier(name, "color", context.getPackageName());
	}

	public static int getViewId(Context context, String name)
	{
		Resources res = context.getResources();
		return res.getIdentifier(name, "id", context.getPackageName());
	}

	public static int getRawId(Context context, String name)
	{
		Resources res = context.getResources();
		return res.getIdentifier(name, "raw", context.getPackageName());
	}

	public static int getAnimId(Context context, String name)
	{
		Resources res = context.getResources();
		return res.getIdentifier(name, "anim", context.getPackageName());
	}

	public static int getStyleId(Context context, String name)
	{
		Resources res = context.getResources();
		return res.getIdentifier(name, "style", context.getPackageName());
	}

	public static int getStyleableId(Context context, String name)
	{
		Resources res = context.getResources();
		return res.getIdentifier(name, "styleable", context.getPackageName());
	}

	public static int getAttrId(Context context, String name)
	{
		Resources res = context.getResources();
		return res.getIdentifier(name, "attr", context.getPackageName());
	}

	public static int getArrayId(Context context, String name)
	{
		Resources res = context.getResources();
		return res.getIdentifier(name, "array", context.getPackageName());
	}

	public static int getDimenId(Context context, String name)
	{
		Resources res = context.getResources();
		return res.getIdentifier(name, "dimen", context.getPackageName());
	}

	public static final int[] getResourceDeclareStyleableIntArray(Context context, String name)
	{
		try
		{
			// use reflection to access the resource class
			Field[] fields2 = Class.forName(context.getPackageName() + ".R$styleable").getFields();

			// browse all fields
			for (Field f : fields2)
			{
				// pick matching field
				if (f.getName().equals(name))
				{
					// return as int array
					int[] ret = (int[]) f.get(null);
					return ret;
				}
			}
		}
		catch (Throwable t)
		{
		}

		return null;
	}
}
