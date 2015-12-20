package com.medialab.dimension.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.medialab.dimension.data.DimensionVO;
import com.medialab.dimension.data.DimensionVO.Type;

/**
 * 默认xml读取类
 */
public class DefaultXMLReader extends XMLReader
{
	/**
	 * sp数值的前缀
	 */
	private static final String SP_PREFIX = "text_size_";
	
	/**
	 * sp数值的后缀
	 */
	private static final String SP_SUFFIX = "pt";
	
	/**
	 * dp数值的前缀
	 */
	private static final String DP_PREFIX = "margin_val_";
	
	/**
	 * dp数值的后缀
	 */
	private static final String DP_SUFFIX = "px";
	
	public DefaultXMLReader()
	{
	}
	
	/**
	 * 解析dimension数据
	 * @param dimenNode
	 * @return
	 */
	public DimensionVO parseDimensionVO(Node dimenNode)
	{
		Element element = (Element)dimenNode;
		
		String name = element.getAttribute("name");
		if(null != name)
		{
			DimensionVO dimensionVO = null;
			if(name.startsWith(SP_PREFIX))
			{
				dimensionVO = new DimensionVO(Type.SP);
			}
			else if(name.startsWith(DP_PREFIX))
			{
				dimensionVO = new DimensionVO(Type.DP);
			}
			
			if(null != dimensionVO)
			{
				dimensionVO.setName(name);
				dimensionVO.setPxValue(parsePxValue(name));
				return dimensionVO;
			}
		}
		
		return null;
	}
		
	/**
	 * 解析px数值
	 * @param name
	 * @return
	 */
	private int parsePxValue(String name)
	{
		if(name.startsWith(SP_PREFIX))
		{
			String value = name.replace(SP_PREFIX, "").replace(SP_SUFFIX, "").trim();
			return Integer.parseInt(value);
		}
		else if(name.startsWith(DP_PREFIX))
		{
			String value = name.replace(DP_PREFIX, "").replace(DP_SUFFIX, "").trim();
			return Integer.parseInt(value);
		}
		
		return 0;
	}
}
