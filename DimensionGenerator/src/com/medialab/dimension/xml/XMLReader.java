package com.medialab.dimension.xml;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.medialab.dimension.data.DimensionVO;

/**
 * xml读取类
 */
public abstract class XMLReader
{	
	public XMLReader()
	{
	}
	
	/**
	 * 读取dimen.xml文件
	 * @param filePath 文件路径
	 * @return
	 */
	public final ArrayList<DimensionVO> readDimensionXML(String filePath)
	{
		System.out.println("[Method:readDimensionXML]");
		ArrayList<DimensionVO> dimensions = new ArrayList<DimensionVO>();
		
		DocumentBuilder documentBuilder;
		try
		{
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document =documentBuilder.parse(new File(filePath));
			NodeList nodeList = document.getElementsByTagName("dimen");
			int nodeCount = nodeList.getLength();
			System.out.println("[Method:readDimensionXML] node count == " + nodeCount);
			for(int i = 0; i < nodeCount; i++)
			{
				DimensionVO dimensionVO = parseDimensionVO(nodeList.item(i));
				if(null != dimensionVO)
				{
					dimensions.add(dimensionVO);
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		return dimensions;
	}
	
	/**
	 * 解析dimension数据
	 * @param dimenNode
	 * @return
	 */
	public abstract DimensionVO parseDimensionVO(Node dimenNode);	
}
