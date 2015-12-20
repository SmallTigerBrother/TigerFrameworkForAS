package com.medialab.dimension.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.medialab.dimension.data.DimensionVO;

/**
 * XML数据写入类
 */
public class XMLWriter
{
	public XMLWriter()
	{
		
	}
	
	/**
	 * 将内容写入到xml中
	 * @param dimensions dimen数据数组
	 * @param filePath
	 */
	public void writeDimensionXML(ArrayList<DimensionVO> dimensions, String filePath)
	{
		System.out.println("[Method:generateDimensionXML] dimension count == " + dimensions.size());
		
		StringBuilder builder = new StringBuilder();
		builder.append("<resources> \n");
			
		for (DimensionVO dimensionVO : dimensions)
		{
			builder.append(createXMLNode(dimensionVO));
			builder.append("\n");
		}
		
		builder.append("</resources>");
		
		writeToFile(builder.toString(), filePath);
	}
	
	/**
	 * 创建XML中的dimen节点
	 * @param dimensionVO
	 * @return
	 */
	private String createXMLNode(DimensionVO dimensionVO)
	{
		String unit = "";
		switch (dimensionVO.getType())
		{
		case SP:
			unit = "sp";
			break;
		case DP:
			unit = "dp";
			break;

		default:
			break;
		}
		
		return "<dimen name=\"" + dimensionVO.getName() + "\">" + dimensionVO.getAndroidValue() + unit + "</dimen>";
	}
	
	/**
	 * 写入文件
	 * @param content
	 * @param filePath
	 */
	private void writeToFile(String content, String filePath)
	{
		File myFile = new File(filePath);
		if (!myFile.exists())
		{
			try
			{
				myFile.createNewFile();
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
			}
		}

		if (null != content)
		{
			FileWriter fw = null;
			try
			{
				fw = new FileWriter(myFile);
				fw.write(content);
				fw.flush();
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
			}
			finally
			{
				if (fw != null)
				{
					try
					{
						fw.close();
					}
					catch (IOException e)
					{
						System.out.println(e.getMessage());
					}
				}
			}
		}

	}
}
