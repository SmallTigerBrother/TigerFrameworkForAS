package com.medialab.dimension;

import com.medialab.dimension.policy.CaculatePolicy;
import com.medialab.dimension.policy.CaculatePolicyBaseOnIphone6;

public class CaculatePX
{
	public static void main(String[] args)
	{
		CaculatePolicy caculatePolicy = new CaculatePolicyBaseOnIphone6(480, 800, 3.7);
		System.out.println(caculatePolicy.px2px(20));
		System.out.println(caculatePolicy.px2px(80));
		System.out.println(caculatePolicy.px2px(100));
		System.out.println(caculatePolicy.px2px(110));
	}
}
