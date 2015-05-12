package com.hpt.search;

import java.util.ResourceBundle;

import com.hpt.search.common.SearchGlobal;
import com.hpt.search.service.SearchService;
/**
 * 
 * @Title:HptSearcher
 * @description:获取搜索服务类
 * @author 赵俊夫
 * @date 2014-7-16 下午8:38:43
 * @version V1.0
 */
public class HptSearcher {
	private static ResourceBundle bundle = null;
	private static volatile SearchService searchService = null;
	private static String searcher = null;
	
	static{
		try{
			bundle = java.util.ResourceBundle.getBundle(SearchGlobal.configFile);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static SearchService getService(){
		if(searchService==null){
			synchronized (HptSearcher.class) {
				if(searchService !=null) return searchService;
				searcher = bundle.getString("searcher");
				if(searcher==null){
					throw new RuntimeException("not found propertity named 'searcher' in search.properties");
				}
				Class clazz = null;;
				try {
					clazz = Class.forName(searcher);
					searchService = (SearchService) clazz.newInstance();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return searchService;
	}
}
