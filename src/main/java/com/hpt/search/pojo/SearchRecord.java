package com.hpt.search.pojo;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @Title:SearchRecord
 * @description:搜索统计
 * @author 赵俊夫
 * @date 2015-5-12 上午10:50:47
 * @version V1.0
 */
public class SearchRecord {
	public static AtomicLong searchCount=new AtomicLong(1);  
	public static AtomicLong searchTime=new AtomicLong(0);
	
}
