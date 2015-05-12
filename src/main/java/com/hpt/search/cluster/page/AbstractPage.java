package com.hpt.search.cluster.page;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @Title:AbstractPage
 * @description:页面生成-直接使用html拼接的方式，做到轻量级
 * @author 赵俊夫
 * @date 2015-5-11 下午3:20:53
 * @version V1.0
 */
public abstract class AbstractPage {
	private static Map<String,AbstractPage> pageMap = new HashMap<String,AbstractPage>(0);
	static{
		//注册页面处理类
		pageMap.put("index", new NodeManagerPage());
		pageMap.put("search", new SimpleSearchPage());
	}
	
	public abstract String createPage();
	public abstract String getEncode();
	public abstract String getContentType();
	
	public static AbstractPage  getPage(String page){
		return pageMap.get(page);
	}
}
