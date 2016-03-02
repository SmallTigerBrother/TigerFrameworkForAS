package com.medialab.dimension;

import java.util.ArrayList;

import com.medialab.dimension.data.DimensionVO;
import com.medialab.dimension.policy.CaculatePolicy;
import com.medialab.dimension.policy.CaculatePolicyBaseOnIphone5;
import com.medialab.dimension.xml.DefaultXMLReader;
import com.medialab.dimension.xml.XMLWriter;

public class GenerateDimenXML
{
	/**
	 * 原始文件路径
	 */
	private static final String resFilePath = "/Users/peng/AndroidStudioProjects/SharingEconomy_Rent/app/src/main/res/values-xxhdpi/dimension.xml";
	
	/**
	 * 结果文件路径
	 */
	private static final String resultFilePath = "/Users/peng/Documents/dimension.xml";
	
	public static void main(String[] args)
	{
		//1、读取dimen.xml文件
		ArrayList<DimensionVO> dimensions = new DefaultXMLReader().readDimensionXML(resFilePath);
		
		//2、创建计算策略
		CaculatePolicy caculatePolicy = new CaculatePolicyBaseOnIphone5(720, 1280, 4.5);
		
		//3、计算所有dimension的大小
		for (DimensionVO dimensionVO : dimensions)
		{
			switch (dimensionVO.getType())
			{
			case SP:
				dimensionVO.setAndroidValue(caculatePolicy.px2sp(dimensionVO.getPxValue()));
				break;

			case DP:
				dimensionVO.setAndroidValue(caculatePolicy.px2dp(dimensionVO.getPxValue()));
				
				break;
				
			default:
				break;
			}
		}
		
		//4、将结果写入到指定目录下
		new XMLWriter().writeDimensionXML(dimensions, resultFilePath);
	}
}
